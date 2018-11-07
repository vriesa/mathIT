/*
 * Graph.java - Class representing a graph
 *
 * Copyright (C) 2009-2016 Andreas de Vries
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA
 * 
 * As a special exception, the copyright holders of this program give you permission 
 * to link this program with independent modules to produce an executable, 
 * regardless of the license terms of these independent modules, and to copy and 
 * distribute the resulting executable under terms of your choice, provided that 
 * you also meet, for each linked independent module, the terms and conditions of 
 * the license of that module. An independent module is a module which is not derived 
 * from or based on this program. If you modify this program, you may extend 
 * this exception to your version of the program, but you are not obligated to do so. 
 * If you do not wish to do so, delete this exception statement from your version.
 */
package org.mathIT.graphs;

import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.copyOf;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Function;
import javax.swing.JTable;
import org.mathIT.algebra.OrderedSet;
import org.mathIT.algebra.Matrix;
import org.mathIT.numbers.Numbers;

/**
 * This class represents a general graph without multiple edges
 * as an array list of vertices and the corresponding adjacency matrix.
 * Each vertex of a graph of this class must be an instance of a class
 * implementing the interface {@link Vertible}.
 * To create a graph, a set of vertices implementing <code>Vertible.java</code>
 * is necessary, along with the adjacency matrix.
 * It is important to note that the indices of the vertices depend uniquely on the
 * adjacency matrix <code>adjacency</code> in the sense that an edge from
 * vertex <i>i</i> to vertex <i>j</i> is given if the matrix entry
 * <code>adjacency[i][j]</code> is 1, and 0 otherwise.
 * <p>
 * Most easily, a vertex class is programmed by extending the vertex
 * class {@link Vertex}.
 * For example, if a network of persons is to be established, a class
 * <code>Person</code> can be written such as
 * </p>
 * <pre>
 *   public class Person extends Vertex&lt;Person&gt; {
 *      ...
 *   }
 * </pre>
 * <p>
 * and the network of persons as
 * </p>
 * <pre>
 *   public class SocialNetwork extends Graph&lt;Person&gt; {
 *      ...
 *   }
 * </pre>
 * <p>
 * A more tedious, but also more flexible way is to implement instead the
 * interface {@link Vertible}, e.g.,
 * </p>
 * <pre>
 *   public class Person implements Vertible&lt;Person&gt; {
 *      ...
 *   }
 * </pre>
 * <p>
 * If one is interested only in a simple graph consisting of vertices
 * with the minimal set of attributes, the static method
 * {@link #createGraph(int[][])} can be invoked,
 * </p>
 * <pre>
 *   public class MyGraph {
 *      int[][] adjacencyMatrix;
 *      ...
 *      Graph&lt;SimpleVertex&gt; graph = Graph.create(adjacencyMatrix);
 *      ...
 *   }
 * </pre>
 * @author Andreas de Vries
 * @version 2.0
 * @param <V> the type of the vertices
 * @see WeightedGraph
 */
public class Graph<V extends Vertible<V>> {
   /** Version ID for serialization. */
   //private static final long serialVersionUID = 69062958L;  // = "Graph".hashCode()
   /** Column separator for CSV files. */
   public static final char SEPARATOR = '\t';
   /** Flag whether this graph is directed or undirected. */
   protected boolean undirected;
   /** Flag whether this graph is weighted or unweighted. 
    *  Instances of this class are always unweighted, for weighted graphs 
    *  consider the subclass {@link WeightedGraph Weighted Graph}.
    */
   protected boolean weighted;
   /** Array of vertices forming this graph.*/
   protected V[] vertices;
   /** Adjacency matrix. adjacency[i][j] indicates whether there is an edge from vertex i to vertex j.*/
   protected int[][] adjacency;
   /** Number of edges.*/
   protected int numberOfEdges;
   /** The Hashimoto matrix, also called non-backtracking matrix or edge adjacency matrix.
    *  It encodes those pairs of adjacent edges for which the start vertex is 
    *  different from the end vertex (i.e., backtracks are excluded).
    */
   //protected int[][] hashimoto;
   /** The modiable Hashimoto matrix <i>M</i>(<i>i</i>)
    *  consisting of function entries <i>n</i>(<i>i</i>).
    *  The Hashimoto matrix, also called non-backtracking matrix or edge adjacency matrix,
    *  encodes those pairs of adjacent edges for which the start vertex is 
    *  different from the end vertex (i.e., backtracks are excluded).
    *  The modifiable Hashimoto matrix <i>M</i>(<i>i</i>) allows to simulate the removal of node <i>i</i>
    *  by its entries <i>n</i>(<i>i</i>). 
    *  Here the function <i>n</i>(<i>i</i>) in entry (<i>k,l</i>) 
    *  <p style="text-align:center;">
    *   (<i>m</i>)<sub><i>kl</i></sub>(<i>i</i>) = <i>n</i>(<i>i</i>)
    *  </p>
    *  is defined as the function yielding 1 if and only if edges <i>k</i> and <i>l</i>
    *  are adjacent, but are not linked via node <i>i</i>; in all other cases <i>n</i>(<i>i</i>) = 0.
    *  By consequence, <i>M</i>(-1) exactly yields the Hashimoto matrix.
    */
   protected ArrayList<ArrayList<Function<Integer,Integer>>> modifiableHashimoto = null;
   /**
    * This array stores the network relevance of all vertices with respect to the entire
    * graph. The network relevance, or influence, of a node is defined by
    * the impact of its removal to the
    * {@link org.mathIT.graphs.Graph#computeHashimoto() Hashimoto matrix} of the
    * remaining network. Network relevance is an important notion to study
    * system relevance, network stability, or network reliability.
    * @see #getRelevance(int) 
    */
   protected double[] relevance;

   /**
    * Creates an empty graph.
    */
   public Graph() {}

   /** Creates a graph with the specified vertices.
    *  @param vertices array of the vertices forming this graph
    *  @param arrayTemplate an array of the type of vertices. This array may be empty.
    *  It is necessary technically because generic array creation is prohibited in Java.
    */
   //@SuppressWarnings("unchecked")
   public Graph(ArrayList<V> vertices, V[] arrayTemplate) {
      this.undirected = false;
      this.weighted = false;
      this.vertices = vertices.toArray(arrayTemplate);
      //this.numberOfEdges = computeNumberOfEdges(adjacency);
      //this.hashimoto = computeHashimoto();
   }

   /** Creates a directed graph with the specified vertices. The adjacency matrix is created as the
    *  <i>n</i>&times;<i>n</i> null matrix, i.e., the graph consists of isolated vertices.
    *  @param vertices an array of the vertices of this graph
    */
   public Graph(V[] vertices) {
      this(false, vertices);
   }

   /** Creates a graph with the specified vertices. The adjacency matrix is created as the
    *  <i>n</i>&times;<i>n</i> null matrix, i.e., the graph consists of isolated vertices.
    *  @param undirected indicator whether this graph is undirected
    *  @param vertices an array of the vertices of this graph
    */
   public Graph(boolean undirected, V[] vertices) {
      this.undirected = undirected;
      this.weighted = false;
      this.vertices = vertices;
      this.adjacency = new int[vertices.length][vertices.length];
      //this.numberOfEdges = computeNumberOfEdges(undirected, adjacency);
      //this.hashimoto = computeHashimoto();
   }

   /** Creates a directed graph from the specified adjacency matrix.
    *  In particular, the adjacency list of each vertex is constructed from the adjacency matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code>
    *  do not have an edge connecting them, the respective adjacency matrix entry
    *  <code>adjacency[i][j]</code> is expected to have the value 0.
    *  In each vertex, previously stored values for its index or its adjacency
    *  list are always overwritten with the ones derived by the adjacency matrix!
    *  @param adjacency the adjacency matrix determining the adjacencies of each vertex of this graph
    *  @param arrayTemplate an array of the type of vertices, containing at least one template vertex.
    *  It is necessary technically because generic array creation is prohibited in Java.
    *  @throws IllegalArgumentException if adjacency is not a square matrix
    */
   public Graph(int[][] adjacency, V[] arrayTemplate) {
      this(false, adjacency, arrayTemplate);
   }

   /** Creates a graph from the specified adjacency matrix.
    *  In particular, the adjacency list of each vertex is constructed from the adjacency matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code>
    *  do not have an edge connecting them, the respective adjacency matrix entry
    *  <code>adjacency[i][j]</code> is expected to have the value 0.
    *  In each vertex, previously stored values for its index or its adjacency
    *  list are always overwritten with the ones derived by the adjacency matrix!
    *  @param undirected flag indicating whether this graph is undirected
    *  @param adjacency the adjacency matrix determining the adjacencies of each vertex of this graph
    *  @param arrayTemplate an array of the type of vertices, containing at least one template vertex.
    *  It is necessary technically because generic array creation is prohibited in Java.
    *  @throws IllegalArgumentException if adjacency is not a square matrix, 
    *  or if the graph is undirected and the adjacency matrix is not symmetric
    */
   public Graph(boolean undirected, int[][] adjacency, V[] arrayTemplate) {
      this.undirected = undirected;
      this.weighted = false;
      if (adjacency.length != adjacency[0].length) {
         throw new IllegalArgumentException(
            "Adjacency matrix is not a square matrix"
         );
      }

      if (undirected && !isSymmetric(adjacency)) {
         throw new IllegalArgumentException(
            "Adjacency matrix of this undirected graph must be symmetric"
         );
      }

      //this.vertices = vertices.toArray(Arrays.<V>copyOf(arrayType,0));
      this.vertices = java.util.Arrays.copyOf(arrayTemplate, adjacency.length);
      ArrayList<V> adj;  // an array list since the length of the adjacency is unknown at this moment
      for (int i = 0; i < vertices.length; i++) {
         vertices[i] = arrayTemplate[0].copy(); vertices[i].setName(""+i); vertices[i].setIndex(i);
      }

      for (int i = 0; i < vertices.length; i++) {
         adj = new ArrayList<>(vertices.length);
         for(int j = 0; j < adjacency[0].length; j++) {
            if (adjacency[i][j] == 1) { // is there an edge from i to j?
               adj.add(vertices[j]);
            }
         }
         vertices[i].setAdjacency(adj.toArray(copyOf(vertices,0)));
         vertices[i].setIndex(i);
      }
      this.adjacency = adjacency;
      this.numberOfEdges = computeNumberOfEdges();
      //this.hashimoto = computeHashimoto();
   }

   /** Creates a directed graph from the specified array of vertices and the adjacency matrix.
    *  In particular, the adjacency list of each vertex is constructed from the adjacency matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code>
    *  do not have an edge connecting them, the respective adjacency matrix entry
    *  <code>adjacency[i][j]</code> is expected to have the value 0.
    *  In each vertex, previously stored values for its index or its adjacency
    *  list are always overwritten with the ones derived by the adjacency matrix!
    *  @param vertices array of the vertices forming this graph
    *  @param adjacency the adjacency matrix determining the adjacencies of each vertex of this graph
    *  @throws IllegalArgumentException if adjacency is not a square matrix or if
    *  the number of vertices does not equal the length of the adjacency matrix
    */
   public Graph(V[] vertices, int[][] adjacency) {
      this(false,vertices,adjacency);
   }

   /** Creates a graph from the specified array of vertices and the adjacency matrix.
    *  In particular, the adjacency list of each vertex is constructed from the adjacency matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code>
    *  do not have an edge connecting them, the respective adjacency matrix entry
    *  <code>adjacency[i][j]</code> is expected to have the value 0.
    *  In each vertex, previously stored values for its index or its adjacency
    *  list are always overwritten with the ones derived by the adjacency matrix!
    *  @param undirected flag indicating whether this graph is undirected
    *  @param vertices array of the vertices forming this graph
    *  @param adjacency the adjacency matrix determining the adjacencies of each vertex of this graph
    *  @throws IllegalArgumentException if adjacency is not a square matrix, or if
    *  the number of vertices does not equal the length of the adjacency matrix,
    *  or if the graph is undirected and the adjacency matrix is not symmetric
    */
   public Graph(boolean undirected, V[] vertices, int[][] adjacency) {
      this.undirected = undirected;
      this.weighted = false;
      if (adjacency.length != adjacency[0].length) {
         throw new IllegalArgumentException(
            "Adjacency matrix is not a square matrix"
         );
      }
      if (adjacency.length != vertices.length) {
         throw new IllegalArgumentException(
            "Vertex set and adjacency matrix of the graph are inconsistent"
         );
      }
      
      if (undirected && !isSymmetric(adjacency)) {
         throw new IllegalArgumentException(
            "Adjacency matrix of this undirected graph must be symmetric"
         );
      }

      ArrayList<V> adj;  // an array list since the length of the adjacency is unknown at this moment
      for (int i = 0; i < vertices.length; i++) {
         adj = new ArrayList<>(vertices.length);
         for(int j = 0; j < adjacency[0].length; j++) {
            if (adjacency[i][j] == 1) { // is there an edge from i to j?
               adj.add(vertices[j]);
            }
         }
         vertices[i].setAdjacency(adj.toArray(copyOf(vertices,0)));
         vertices[i].setIndex(i);
      }
      this.vertices  = vertices;
      this.adjacency = adjacency;
      this.numberOfEdges = computeNumberOfEdges();
     //this.hashimoto = computeHashimoto();
   }
   
   /** Creates a directed graph from the specified array list of vertices and the adjacency matrix.
    *  In particular, the adjacency list of each vertex is constructed from the adjacency matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code>
    *  do not have an edge connecting them, the respective adjacency matrix entry
    *  <code>adjacency[i][j]</code> is expected to have the value 0.
    *  In each vertex, previously stored values for its index or its adjacency
    *  list are always overwritten with the ones derived by the adjacency matrix!
    *  @param vertices array of the vertices forming this graph
    *  @param adjacency the adjacency matrix determining the adjacency of each edge of this graph
    *  @param arrayTemplate an array of the type of vertices. This array may be empty.
    *  It is necessary technically because generic array creation is prohibited in Java.
    */
   //@SuppressWarnings("unchecked")
   public Graph(ArrayList<V> vertices, int[][] adjacency, V[] arrayTemplate) {
      if (adjacency.length != vertices.size() || adjacency[0].length != vertices.size()) {
         throw new IllegalArgumentException(
            "Vertex set and adjacency matrix of the graph are inconsistent"
         );
      }

      this.undirected = false;      
      this.weighted = false;
      this.vertices = vertices.toArray(arrayTemplate);

      ArrayList<V> adj;  // an array list since the length of the adjacency is unknown at this moment
      for (int i = 0; i < this.vertices.length; i++) {
         adj = new ArrayList<>(this.vertices.length);
         for(int j = 0; j < adjacency[0].length; j++) {
            if ( adjacency[i][j] == 1) { // is there an edge from i to j?
               adj.add(this.vertices[j]);
            }
         }
         this.vertices[i].setAdjacency(adj.toArray(copyOf(this.vertices,0)));
         this.vertices[i].setIndex(i);
      }
      this.adjacency = adjacency;
      this.numberOfEdges = computeNumberOfEdges();
      //this.hashimoto = computeHashimoto();
   }

   /** Returns the number of edges of this graph.
    *  @return the number of edges
    */
   protected final int computeNumberOfEdges() {
      int i, j, m = 0;
      if (undirected) { // symmetric adjacency matrix
         for (i = 0; i < adjacency.length; i++) {
            for (j = 0; j <= i; j++) {
               m += adjacency[i][j];
            }
         }
      } else {
         for (i = 0; i < adjacency.length; i++) {
            for (j = 0; j < adjacency.length; j++) {
               m += adjacency[i][j];
            }
         }
      }
      return m;
   }

   /** The Hashimoto matrix of this graph, also called non-backtracking matrix 
    *  or edge adjacency matrix.
    *  It encodes those pairs of adjacent edges for which the start vertex is 
    *  different from the end vertex (i.e., backtracks are excluded).
    *  For symmetric adjacency matrices, the labeling of edges follows the
    *  scheme in 
    *  A. Terras: <i>Zeta Functions of Graphs.</i> Cambridge University Press, 
    *  Cambridge New York 2011
    *  @return the Hashimoto matrix
    */
   public int[][] computeHashimoto() {
      /* This method could be equivalently executed by invoking computeModifiableHashimoto(-1)
       * with its functional approach..
       * However, this method seems to be about a factor 200 faster than the 
       * functional version if it has to initialize the matrix modifiableHashimoto,
       * and still a factor 5 faster if this matrix has been computed before.
       * For this reason, this method is implemented redundantly to
       * the functional version.
       */
      int i, j, k, l, m = 0;
      
      // Determine the number of oriented edges, ignoring self-loops:
      for (i = 0; i < adjacency.length; i++) {
         for (j = 0; j < i; j++) {
            m += adjacency[i][j] + adjacency[j][i];
         }
      }

      int[][] b = new int[m][m];
      
      // Indexing the edges, where e[k] has two components, 
      // where e[k][0] the start vertex and e[k][1] the end vertex:
      int[][] e = new int[m][2];
      
      k = 0;
      
      if (isSymmetric(adjacency)) {
         // Label the edges as Eq. (2.1) in A. Terras: Zeta Functions of Graphs. Cambridge 2011:
         m /= 2;
         for (i = 0; i < adjacency.length; i++) {
            for (j = 0; j < i; j++) {
               if (adjacency[i][j] == 1) {
                  e[k][0] = j;
                  e[k][1] = i;
                  e[k+m][0] = i;
                  e[k+m][1] = j;
                  k++;
               }
            }
         }
      } else {
         for (i = 0; i < adjacency.length; i++) {
            for (j = 0; j < i; j++) {
               if (adjacency[j][i] == 1) {
                  e[k][0] = j;
                  e[k][1] = i;
                  k++;
               }
               if (adjacency[i][j] == 1) {
                  e[k][0] = i;
                  e[k][1] = j;
                  k++;
               }
            }
         }
      }
      
      for (k = 0; k < e.length; k++) {
         for (l = 0; l < e.length; l++) {
            if (e[k][1] == e[l][0] && e[k][0] != e[l][1]) {
               b[k][l] = 1;
            }
         }
      }
      return b;
   }
   
   /** Returns the modifiable Hashimoto matrix <i>M</i>(<i>i</i>)
    *  consisting of function entries <i>n</i>(<i>i</i>).
    *  The Hashimoto matrix is also called non-backtracking matrix or edge adjacency matrix,
    *  encodes those pairs of adjacent edges for which the start vertex is 
    *  different from the end vertex (i.e., backtracks are excluded).
    *  For symmetric adjacency matrices, the labeling of edges follows the
    *  scheme in 
    *  A. Terras: <i>Zeta Functions of Graphs.</i> Cambridge University Press, 
    *  Cambridge New York 2011.
    *  The modifiable Hashimoto matrix <i>M</i>(<i>i</i>) allows to simulate the removal of node <i>i</i>
    *  by its entries <i>n</i>(<i>i</i>). 
    *  Here the function <i>n</i>(<i>i</i>) in entry (<i>k,l</i>) 
    *  <p style="text-align:center;">
    *   (<i>m</i>)<sub><i>kl</i></sub>(<i>i</i>) = <i>n</i>(<i>i</i>)
    *  </p>
    *  is defined as the function yielding 1 if and only if edges <i>k</i> and <i>l</i>
    *  are adjacent, but are not linked via node <i>i</i>; in all other cases <i>n</i>(<i>i</i>) = 0.
    *  In other words, the modified Hashimoto matrix <i>M</i> is related to the Hashimoto matrix <i>B</i>
    *  by the equation
    *  <p style="text-alig:center;">
    *    <i>M<sub>kl</sub></i> = <i>n</i>(i</i>) B<sub>kl</sub></i>
    *  </p>
    *  where <i>n<sub>i</sub> = 0 if node <i>i</i> is removed from the graph,
    *  and = 1 otherwise.
    *  By consequence, <<i>M</i>(-1) exactly yields the Hashimoto matrix. 
    *  @return the modifiable Hashimoto matrix
    */
   private ArrayList<ArrayList<Function<Integer,Integer>>> computeModifiableHashimoto() {      
      int i, j, k, l, m = 0;
      
      // Determine the number of oriented edges, ignoring self-loops:
      for (i = 0; i < adjacency.length; i++) {
         for (j = 0; j < i; j++) {
            m += adjacency[i][j] + adjacency[j][i];
         }
      }

      //ArrayList<ArrayList<Function<Integer,Integer>>> 
      modifiableHashimoto = new ArrayList<>(m);
      ArrayList<Function<Integer,Integer>> row;
      
      // Indexing the edges, where e[k] has two components, 
      // where e[k][0] the start vertex and e[k][1] the end vertex:
      int[][] e = new int[m][2];
      
      k = 0;
      
      if (isSymmetric(adjacency)) {
         // Label the edges as Eq. (2.1) in A. Terras: Zeta Functions of Graphs. Cambridge 2011:
         m /= 2;
         for (i = 0; i < adjacency.length; i++) {
            for (j = 0; j < i; j++) {
               if (adjacency[i][j] == 1) {
                  e[k][0] = j;
                  e[k][1] = i;
                  e[k+m][0] = i;
                  e[k+m][1] = j;
                  k++;
               }
            }
         }
      } else {
         for (i = 0; i < adjacency.length; i++) {
            for (j = 0; j < i; j++) {
               if (adjacency[j][i] == 1) {
                  e[k][0] = j;
                  e[k][1] = i;
                  k++;
               }
               if (adjacency[i][j] == 1) {
                  e[k][0] = i;
                  e[k][1] = j;
                  k++;
               }
            }
         }
      }
      
      for (k = 0; k < e.length; k++) {
         row = new ArrayList<>(e.length);
         for (l = 0; l < e.length; l++) {
            if (e[k][1] == e[l][0] && e[k][0] != e[l][1]) {
               // B[k][l] = 1;
               row.add(n_(e[k][1]));
            } else {
               row.add(x -> 0);
            }
         }
         modifiableHashimoto.add(row);
      }
      return modifiableHashimoto;
   }
   
   private Function<Integer,Integer> n_(int i) {
      return x -> x == i ? 0 : 1;
   }
   
   /** The modified Hashimoto matrix <i>M</i>(<i>i</i>) of this graph with node 
    *  <i>i</i> removed;
    *  <i>M</i>(<i>i</i>) is also called modified non-backtracking matrix 
    *  or modified edge adjacency matrix.
    *  It encodes those pairs of adjacent edges for which the start vertex is 
    *  different from the end vertex (i.e., backtracks are excluded).
    *  For symmetric adjacency matrices, the labeling of edges follows the
    *  scheme in 
    *  A. Terras: <i>Zeta Functions of Graphs.</i> Cambridge University Press, 
    *  Cambridge New York 2011.
    *  For <i>i</i> = -1 the Hashimoto matrix of the (full) graph is returned.
    *  The modified Hashimoto matrix <i>M</i> is related to the Hashimoto matrix <i>B</i>
    *  by the equation
    *  <p style="text-align:center;">
    *    <i>M<sub>kl</sub></i> = <i>n<sub>i</sub> B<sub>kl</sub></i>
    *  </p>
    *  <p>
    *  where <i>n<sub>i</sub></i> = 0 if node <i>i</i> is removed from the graph,
    *  and = 1 otherwise.
    *  For more details see 
    *  F. Morone, H.A. Makse (2015): 
    *  ‘Influence maximization in complex networks through optimal percolation’,
    *  <i>Nature</i> <b>524</b> (7563), pp. 65–68,
    *  <a href="http://dx.doi.org/10.1038/nature14604" target="_new">doi 10.1038/nature14604</a>
    *  (or preprint 
    *  <a href="http://arxiv.org/abs/1506.08326" target="_new">arxiv 1506.08326</a>).
    *  </p>
    *  <p>
    *  The implementation of this method relies on the field {@link #modifiableHashimoto}
    *  of this graph which is computed once and consists of functional lambda expressions,
    *  which are simply applied by the argument i.
    *  </p>
    *  
    *  @param i the node of the graph to be removed virtually
    *  @return the modified Hashimoto matrix
    *  @see #modifiableHashimoto
    */
   public int[][] getModifiedHashimoto(int i) {
      if (modifiableHashimoto == null) modifiableHashimoto = computeModifiableHashimoto();
      int k,l,m = modifiableHashimoto.size();
      
      int[][] M = new int[m][m];
      
      for (k = 0; k < m; k++) {
         for (l = 0; l < m; l++) {
            M[k][l] = modifiableHashimoto.get(k).get(l).apply(i);
         }
      }
      return M;
   }
   
   /** Sets the flag whether this graph is undirected.
    *  @param undirected flag which is true if this graph is undirected
    */
   public void setUndirected(boolean undirected) {
      if (undirected && !isSymmetric(adjacency)) {
         throw new IllegalArgumentException(
             "Adjacency matrix of this undirected graph must be symmetric");
      }
      this.undirected = undirected;
   }
   
   /** Checks whether the adjacency matrix is symmetric. */
   private static boolean isSymmetric(int[][] adjacency) {
      int i, j;
      for (i = 1; i < adjacency.length; i++) {
         for (j = 0; j < i; j++) {
            if (adjacency[i][j] != adjacency[j][i]) return false;
         }
      }
      return true;
   }

   /** Gets the flag whether this graph is undirected.
    *  @return true if and only if this graph is undirected
    */
   public boolean isUndirected() {
      return undirected;
   }

   /** Sets the vertices of this graph.
    *  @param vertices an array of all vertices of this graph
    */
   public void setVertices(V[] vertices) {
      this.vertices = vertices;
   }

   /** Returns an array containing all vertices of this graph.
    *  @return the vertices of this graph
    */
   public V[] getVertices() {
      return this.vertices;
   }

   /** Returns the vertex of index <code>i</code> of this graph.
    *  If a vertex with this index does not exist, it returns <code>null</code>.
    *  @param i the index
    *  @return the vertices of this graph
    */
   public V getVertex(int i) {
      if (i < 0 || i >= vertices.length) return null;
      return this.vertices[i];
   }

   /** Returns the adjacency matrix of this graph.
    *  @return the adjacency matrix of this graph
    */
   public int[][] getAdjacency() {
      return this.adjacency;
   }

   /** Adds a new vertex to this graph. This method does not modify the adjacency
    *  matrix.
    */
   //protected void add(V vertex) {
   //   V[] tmp = Arrays.copyOf(vertices, vertices.length + 1);
   //   tmp[vertices.length] = vertex;
   //   vertices = tmp;
   //}

   /** Returns the vertex of index <code>i</code> of this graph.
    *  If a vertex with this index does not exist, it returns <code>null</code>.
    *  @return the vertices of this graph
    */
   public int getNumberOfEdges() {
      return this.numberOfEdges;
   }

   /**
    * Creates a set of the edges of this graph.
    * @return a set of the edges of this graph
    */
   public HashSet<Edge<V>> collectEdges() {
      HashSet<Edge<V>> set = new HashSet<>();
      int jMin;
      double[][] weight = null;
      if (weighted) {
         weight = ((WeightedGraph<V>) this).getWeight();
      }
      for (int i = 0; i < adjacency.length; i++) {
         if (undirected) {
            jMin = i;  // only need the upper triangle entries of the adjacency matrix
         } else {
            jMin = 0;
         }
         for (int j = jMin; j < adjacency[0].length; j++) {
            if (adjacency[i][j] != 0) {
               if (weighted) {
                  set.add(new Edge<>(vertices[i], vertices[j], !undirected, weight[i][j]));
               } else {
                  set.add(new Edge<>(vertices[i], vertices[j], !undirected));
               }
            }
         }
      }
      return set;
   }
   
   /** Returns the degree of the vertex with index i, if this graph is undirected. 
    *  The degree of a vertex in an undirected graph is
    *  defined as the number of edges incident on it.
    *  For a <i>directed</i> graph the notion of degree is ambigous, since
    *  an edge is either incident on or outgoing from a vertex, i.e.,
    *  we have to distinguish the indegree and the outdegree.
    *  Note that for an undirected graph the degree of a vertex is equal to its
    *  indegree as well as to its outdegree, but its computation requires less computational time.
    *  @param vertex a vertex of the graph
    *  @return the degree of the vertex
    *  @throws IllegalArgumentException if this graph is directed
    *  @see #getDegree(int)
    *  @see #getIndegree(org.mathIT.graphs.Vertible)
    *  @see #getOutdegree(org.mathIT.graphs.Vertible)
    */
   public int getDegree(V vertex) {
      return getDegree(vertex.getIndex());
   }

   /** Returns the degree of the vertex with index i, if this graph is undirected. 
    *  The degree of a vertex in an undirected graph is
    *  defined as the number of edges incident on it.
    *  For a <i>directed</i> graph the notion of degree is ambigous, since
    *  an edge is either incident on or outgoing from a vertex, i.e.,
    *  we have to distinguish the indegree and the outdegree.
    *  Note that for an undirected graph the degree of a vertex is equal to its
    *  indegree as well as to its outdegree, but its computation requires less computational time.
    *  @param i the index of a vertex of the graph
    *  @return the degree of the vertex
    *  @throws IllegalArgumentException if this graph is directed
    *  @see #getDegree(org.mathIT.graphs.Vertible)
    *  @see #getIndegree(int)
    *  @see #getOutdegree(int)
    */
   public int getDegree(int i) {
      if (!undirected) { throw new IllegalArgumentException("This graph is directed!"); }
      int degree = 0;
      for (int j = 0; j < adjacency.length; j++) {
         degree += adjacency[i][j];
      }
      return degree;
   }

   /** Returns an array of the degrees of all vertices, if this graph is undirected, 
    *  with the entry <i>i</i> being the degree of the vertex with index <i>i</i>.
    *  The degree of a vertex in an undirected graph is
    *  defined as the number of edges incident on it.
    *  For a <i>directed</i> graph the notion of degree is ambigous, since
    *  an edge is either incident on or outgoing from a vertex, i.e.,
    *  we have to distinguish the indegree and the outdegree.
    *  Note that for an undirected graph the degree of a vertex is equal to its
    *  indegree as well as to its outdegree, but its computation requires less computational time.
    *  @return an array of the indegrees of all vertices of this graph
    *  @throws IllegalArgumentException if this graph is directed
    *  @see #getDegree(org.mathIT.graphs.Vertible)
    *  @see #getDegree(int) 
    *  @see #getIndegrees() 
    *  @see #getOutdegrees() 
    */
   public int[] getDegrees() {
      int[] deg = new int[vertices.length];
      int i;
      for (i = 0; i < adjacency.length; i++) {
         deg[i] = getDegree(i);
      }
      return deg;
   }

   /** Returns the indegree of a vertex of this graph. The indegree of a vertex is
    *  defined as the number of edges incident on it, or in other words
    *  as the number of its predecessors.
    *  @param vertex a vertex of the graph
    *  @return the indegree of the vertex
    *  @see #getIndegree(int)
    */
   public int getIndegree(V vertex) {
      return getIndegree(vertex.getIndex());
   }

   /** Returns the indegree of the vertex with index i. The indegree of a vertex is
    *  defined as the number of edges incident on it, or in other words
    *  as the number of its predecessors.
    *  @param i the index of a vertex of the graph
    *  @return the indegree of the vertex
    *  @see #getIndegree(org.mathIT.graphs.Vertible)
    */
   public int getIndegree(int i) {
      int indegree = 0;
      for (int j = 0; j < adjacency.length; j++) {
         indegree += adjacency[j][i];
      }
      return indegree;
   }

   /** Returns the outdegree of a vertex of this graph. The outdegree of a vertex is
    *  defined as the number of edges outgoing from it, or in other words
    *  as the number of its successors.
    *  @param vertex a vertex of the graph
    *  @return the outdegree of the vertex
    *  @see #getIndegree(int)
    */
   public int getOutdegree(V vertex) {
      return getOutdegree(vertex.getIndex());
   }

   /** Returns the outdegree of a vertex of this graph. The outdegree of a vertex is
    *  defined as the number of edges outgoing from it, or in other words
    *  as the number of its successors.
    *  @param i the index of a vertex of the graph
    *  @return the outdegree of the vertex
    *  @see #getOutdegree(org.mathIT.graphs.Vertible)
    */
   public int getOutdegree(int i) {
      int outdegree = 0;
      for (int j = 0; j < adjacency.length; j++) {
         outdegree += adjacency[i][j];
      }
      return outdegree;
   }

   /** Returns an array of the indegrees of all vertex of this graph, with the entry
    *  <i>i</i> being the indegree of the vertex with index <i>i</i>.
    *  The indegree of a vertex is
    *  defined as the number of edges incident on it, or in other words
    *  as the number of its predecessors.
    *  @return an array of the indegrees of all vertices of this graph
    *  @see #getIndegree(org.mathIT.graphs.Vertible)
    *  @see #getIndegree(int) 
    */
   public int[] getIndegrees() {
      int[] indeg = new int[vertices.length];
      int i;
      for (i = 0; i < adjacency.length; i++) {
         indeg[i] = getIndegree(i);
      }
      return indeg;
   }

   /** Returns an array of the outdegrees of all vertex of this graph, with the entry
    *  <i>i</i> being the outdegree of the vertex with index <i>i</i>.
    *  The outdegree of a vertex is
    *  defined as the number of edges outgoing from it, or in other words
    *  as the number of its successors.
    *  @return an array of the outdegrees of all vertices of this graph
    *  @see #getOutdegree(org.mathIT.graphs.Vertible)
    *  @see #getOutdegree(int) 
    */
   public int[] getOutdegrees() {
      int[] outdeg = new int[vertices.length];
      int i;
      for (i = 0; i < adjacency.length; i++) {
         outdeg[i] = getOutdegree(i);
      }
      return outdeg;
   }

   /**
    * Returns the network relevance of vertex <i>i</i> with respect to the entire
    * graph. The network relevance, or influence, of a node is defined by
    * the impact of its removal to the
    * {@link org.mathIT.graphs.Graph#computeHashimoto() Hashimoto matrix} of the
    * remaining network. Network relevance is an important notion to study
    * system relevance, network stability, or network reliability.
    * <p>
    * Cf.
    * A. Terras: <i>Zeta Functions of Graphs.</i> Cambridge University Press, 
    * Cambridge New York 2011,
    * or
    * F. Morone, H.A. Makse (2015): 
    * ‘Influence maximization in complex networks through optimal percolation’,
    * <i>Nature</i> <b>524</b> (7563), pp. 65–68,
    * <a href="http://dx.doi.org/10.1038/nature14604" target="_new">doi 10.1038/nature14604</a>
    * (or preprint 
    * <a href="http://arxiv.org/abs/1506.08326" target="_new">arxiv 1506.08326</a>).
    * </p>
    * @param i index of a node
    * @return network relevance of vertex i
    */
   public double getRelevance(int i) {
      return relevance[i];
   }

   /** Returns the Laplacian of this graph.
    *  @return the Laplacian of this graph
    */
   public Matrix laplacian() {
      double[][] l = new double[adjacency.length][adjacency.length];
      int i, j;
      if (undirected) {
         for (i = 0; i < l.length; i++) {
            for (j = 0; j < l.length; j++) {
               if (i == j) {
                  l[i][j] = getDegree(i) - adjacency[i][i];
               } else {
                  l[i][j] = -adjacency[i][j];
               }
            }
         }
      } else {
         for (i = 0; i < l.length; i++) {
            for (j = 0; j < l.length; j++) {
               if (i == j) {
                  l[i][j] = (getIndegree(i) + getOutdegree(i)) / 2 - adjacency[i][i];
               } else {
                  l[i][j] = (-adjacency[i][j] - adjacency[j][i]) / 2;
               }
            }
         }
      }
      return new Matrix(l);
   }

   /** Returns the subgraph given by the specified set of vertices of this graph.
    *  @param vertices a set of vertices of this graph
    *  @return the subgraph given by the specified set of vertices
    *  @throws IllegalArgumentException if a vertex of the set is not found in this graph
    */
   public Graph<V> subgraph(java.util.Set<V> vertices) {
      return subgraph(vertices.toArray(copyOf(this.vertices,0)));
   }

   /** Returns the subgraph given by the specified array of vertices of this graph.
    *  @param vertices an array of vertices of this graph
    *  @return the subgraph given by the specified array of vertices
    *  @throws IllegalArgumentException if a vertex of the vertex array is not found in this graph
    */
   public Graph<V> subgraph(V[] vertices) {
      for (V vertex : vertices) {
         boolean found = true;
         for (int j = 0; found && j<this.vertices.length; j++) {
            found = (vertex.equals(this.vertices[j]));
         }
         if (!found) {
            throw new IllegalArgumentException("Vertex not found in graph: " + vertex.getName());
         }
      }

      V[] v = Arrays.copyOf(vertices, vertices.length);
      for (int i=0; i<v.length; i++) {
         v[i] = vertices[i].copy();
         v[i].setIndex(i);
      }
      int[][] a = new int[v.length][v.length];
      for (int i=0; i<v.length; i++) {
         for (int j=0; j<v.length; j++) {
            a[i][j] = adjacency[v[i].getIndex()][v[j].getIndex()];
         }
      }
      return new Graph<>(v,a);
   }

   /** Returns the index of the searched target, or -1 if the target is not
    *  contained in this graph.
    *  In this case the algorithm has visited each vertex of the graph exactly once.
    *  The algorithm is taken from M. Dom et al.: "Tiefensuche (Ariadne und Co.)",
    *  in B. Vöcking et al.; <i>Taschenbuch der Algorithmen.</i> Springer-Verlag,
    *  Berlin Heidelberg 2008, p. 65
    *  [DOI <a href="http://dx.doi.org/10.1007/978-3-540-76394-9">10.1007/978-3-540-76394-9</a>]
    *  @param start index of the current vertex
    *  @param goal the searched vertex
    *  @return index of the searched vertex, or -1 if it does not exist
    */
   public int depthFirstSearch(int start, V goal) {
      // Stack of vertex indices to be visited next:
      Stack<Integer> next = new Stack<>();
      int i, k;

      // initialize mark flags:
      for(i=0; i<vertices.length; i++) {vertices[i].setMarked(false);}

      //Enqueue root
      next.add(start);
      while (!next.isEmpty()) {
         //Dequeue next node for comparison and add it 2 list of traversed nodes
         i = next.pop();
         vertices[i].mark();

         if (vertices[i].equals(goal)) {
            return i;
         } else {
            //Enqueue new neighbors
            for (V v : vertices[i].getAdjacency()) {
               k = v.getIndex();
               if (!vertices[k].isMarked() && !next.contains(k)) {
                  next.push(k);
               }
            }
         }
      }
      return -1;
   }

   /** Returns the index of the searched vertex if it is reachable from the start vertex.
    *  If the searched vertex is not reachable, or <code>null</code>, the
    *  algorithm visits each vertex of the graph reachable from the start vertex
    *  exactly once.
    *  @param start the start vertex
    *  @param goal the searched vertex
    *  @return index of the searched vertex, or -1 if it does not exist
    */
   public int depthFirstSearch(V start, V goal) {
      // initialize mark flags:
      /*
      for(int i=0; i<vertices.length; i++) {
         vertices[i].setMarked(false);
         vertices[i].setInProcess(false);
      }
      */
      for (V vertex : vertices) {
         vertex.setMarked(false);
         vertex.setInProcess(false);
      }

      traverseDepthFirst(start, goal);

      if (goal == null) {
         return -1;
      }
      if (goal.isMarked()) {
         return goal.getIndex();
      } else {
         return -1;
      }
   }

   /** Traverses the graph by depth-first search and marks every visited vertex.
    *  @param x the start vertex
    *  @param goal the searched vertex; the <code>null</code> is permitted to
    *  perform a complete walk through all reachable vertices
    */
   private void traverseDepthFirst(V x, V goal) {
      if (!x.isMarked() && (goal == null || !goal.isMarked())) {
         x.mark();
         for (V y : x.getAdjacency()) {
            traverseDepthFirst(y, goal);
         }
      }
   }

   /** Returns the index of the searched target, or -1 if the target is not
    *  contained in this graph.
    *  In this case the algorithm has visited each vertex of the graph exactly once.
    *  The algorithm is taken from M. Dom et al.: "Tiefensuche (Ariadne und Co.)",
    *  in B. Vöcking et al.; <i>Taschenbuch der Algorithmen.</i> Springer-Verlag,
    *  Berlin Heidelberg 2008, p. 71
    *  [DOI <a href="http://dx.doi.org/10.1007/978-3-540-76394-9">10.1007/978-3-540-76394-9</a>]
    *  @param start start vertex
    *  @param goal the searched vertex
    *  @return index of the searched vertex, or -1 if it does not exist
    */
   protected int breadthFirstSearch(V start, V goal) {
      Queue<V> queue = new LinkedList<>();
      V node;
      int index = -1;
      boolean found = false;

      // initialize mark flags:
      for (V vertex : vertices) {
         vertex.setMarked(false);
      }

      //Enqueue root
      start.mark();
      queue.offer(start);
      while (!found & !queue.isEmpty()) {
         //Dequeue next node for comparison
         node = queue.remove();

         if (node.equals(goal)) {
            found = true;
            index = node.getIndex();
         } else {
            //Enqueue new neighbors
            for (V neighbor : node.getAdjacency()) {
               if (!neighbor.isMarked()) {
                  neighbor.mark();
                  queue.offer(neighbor);
               }
            }
         }
      }
      return index;
   }

   /** Global variable needed in recursive algorithm cycleFinder.
    *  It store a list of all currently identified cycles starting in a
    *  specified vertex.
    */
   private LinkedList<LinkedList<V>> cycles;

   /** Returns a list of the cycles in this graph starting in <i>x</i>.
    *  A cycle is a closed path, i.e.,
    *  a path where start vertex and end vertex are identical. Note that in a
    *  cycle a vertex may be visited several times. For instance, in an
    *  undirected graph any path is a cycle, since it can be simply returned
    *  in the opposite direction.
    *  @param x the start vertex
    *  @return a list of all cycles starting in <i>x</i>
    *  @see #hasCycles() 
    */
   public LinkedList<LinkedList<V>> getCycles(V x) {
      /*
      for (int i = 0; i < vertices.length; i++) {
         vertices[i].setMarked(false);
         vertices[i].setInProcess(false);
      }
      */
      for (V v : vertices) {
         v.setMarked(false);
         v.setInProcess(false);
      }
      LinkedList<V> path = new LinkedList<>();
      cycles = new LinkedList<>();
      cycleFinder(x, path);
      return cycles;
   }
   
   /** Checks whether this graph contains a cycle.
    *  A cycle is a closed path, i.e.,
    *  a path where start vertex and end vertex are identical. Note that in a
    *  cycle a vertex may be visited several times. For instance, in an
    *  undirected graph any path is a cycle, since it can be simply returned
    *  in the opposite direction.
    *  @return true if this graph has a cycle
    *  @see #getCycles(org.mathIT.graphs.Vertible) 
    */
   public boolean hasCycles() {
      for (V v : vertices) {
         v.setMarked(false);
         v.setInProcess(false);
      }
      
      LinkedList<V> path = new LinkedList<>();
      cycles = new LinkedList<>();
      
      for (V v : vertices) {
         if (!v.isMarked()) {
            cycleFinder(v, path);
            if (!cycles.isEmpty()) return true;
         }
      }
      return false;
   }

   /** Recursive method to find the cycles starting in x.
    *  The algorithm expects that each vertex of this graph is unmarked
    *  {@link Vertible#setMarked(boolean)} and is set in status
    *  "not in process" {@link Vertible#setInProcess(boolean)}.
    *  It runs through the graph starting from vertex x in depth-first search,
    *  sets a currently found vertex immediately into status "in process",
    *  recursively runs through its neighbors, and finally both marks x and
    *  sets its status "not in process". Thus in fact there are three possible
    *  states a vertex can have during the algorithm, namely
    *  "unmarked and not in process" = "initial",
    *  "unmarked and in process" = "active", and
    *  "marked and not in process" = "ready".
    *  (Note that the state "marked and in process" is not possible in this algorithm.)
    *  The algorithm is taken from Holger Schlingloff: "Zyklensuche in Graphen",
    *  in B. Vöcking et al.; <i>Taschenbuch der Algorithmen.</i> Springer-Verlag,
    *  Berlin Heidelberg 2008, p. 88
    *  [DOI <a href="http://dx.doi.org/10.1007/978-3-540-76394-9">10.1007/978-3-540-76394-9</a>]
    *  @param x the start vertex
    *  @param path the current path which is checked to possibly yield a cycle;
    *    it must be called by reference
    */
   private void cycleFinder(V x, LinkedList<V> path) {
      if (x.isInProcess()) {
         path.add(x);
         cycles.add(path);
      } else if (!x.isMarked()) {
         x.setInProcess(true);
         path.add(x);
         for (V v : x.getAdjacency()) {
            cycleFinder(v,new LinkedList<>(path));
         }
         x.mark();
         x.setInProcess(false);
         path.remove(x);
      }
   }

   /** global variable needed for the recursive algorithm componentFinder.
    *  It specifies the currently achieved depth level during depth-first search.
    */
   private int level;

   /** Returns a list of the strongly connected components of this graph which are
    *  reachable from vertex <i>x</i>.
    *  Two vertices <i>x</i> and <i>y</i> are <i>connected</i> if there exists a
    *  cycle <i>x</i> &rarr; ... &rarr; <i>y</i> &rarr; ... &rarr; <i>x</i>,
    *  and all vertices being connected to each other form a <i>strongly connected
    *  component</i> (SCC).
    *  @param x the start vertex
    *  @return a list of all strongly connected components reachable from <i>x</i>
    */
   public ArrayList<Graph<V>> getComponents(V x) {
      for (V v : vertices) {
         v.setMarked(false);
         v.setInProcess(false);
      }
      level = 0;
      HashMap<V,Integer> componentNumber = new HashMap<>();
      HashMap<V,Integer> dfsLevel = new HashMap<>();
      componentFinder(x, componentNumber, dfsLevel); //, componentSets);

      // Determine the sets of the strongly connected components:
      ArrayList<Graph<V>> list = new ArrayList<>();
      //ArrayList<Graph<V>> list = new ArrayList<Graph<V>>();

      // 1) Determine the set of component numbers:
      HashSet<Integer> numbers = new HashSet<>();
      for (V v : componentNumber.keySet()) {
         numbers.add(componentNumber.get(v));
      }
      /*
      componentNumber.keySet().stream().forEach((v) -> {
         numbers.add(componentNumber.get(v));
      });
      */
      
      // 2) Determine the list of all components:
      for (Integer n : numbers) {
         HashSet<V> s = new HashSet<>();
         for (V v : componentNumber.keySet()) {
            if (componentNumber.get(v) == n) {
               s.add(v);
            }
         }
         list.add(subgraph(s));
      }
      return list;
   }

   /** Recursive method to find the strongly connected components reachable from x.
    *  It implements Tarjan's algorithm.
    *  The algorithm expects that each vertex of this graph is unmarked
    *  {@link Vertible#setMarked(boolean)} and is set in status
    *  "not in process" {@link Vertible#setInProcess(boolean)}.
    *  It runs through the graph starting from vertex x in depth-first search,
    *  sets a currently found vertex immediately into status "in process",
    *  recursively runs through its neighbors, and finally both marks x and
    *  sets its status "not in process". Thus in fact there are three possible
    *  states a vertex can have during the algorithm, namely
    *  "unmarked and not in process" = "initial",
    *  "unmarked and in process" = "active", and
    *  "marked and not in process" = "ready".
    *  (Note that the state "marked and in process" is not possible in this algorithm.)
    *  The algorithm is taken from Holger Schlingloff: "Zyklensuche in Graphen",
    *  in B. Vöcking et al.; <i>Taschenbuch der Algorithmen.</i> Springer-Verlag,
    *  Berlin Heidelberg 2008, p. 90
    *  [DOI <a href="http://dx.doi.org/10.1007/978-3-540-76394-9">10.1007/978-3-540-76394-9</a>]
    *  @param x the start vertex
    *  @param components a mapping table associating to each vertex its
    *  strongly connected component
    *  @param dfsLevel a mapping table associating to each vertex its
    *  current depth level of the depth-first search
    */
   private void componentFinder(
     V x, HashMap<V,Integer> component, HashMap<V,Integer> dfsLevel
   ) {
      if (!x.isMarked() && !x.isInProcess()) {
         x.setInProcess(true);
         component.put(x,level);
         dfsLevel.put(x,level);
         level++;
         for (V y : x.getAdjacency()) {
            if (!y.isMarked()) {
               componentFinder(y, component, dfsLevel);
               if (component.get(y) < component.get(x)) {  // cycle!!
                  component.put(x, component.get(y));
               }
            }
         }
         x.mark();
         x.setInProcess(false);
      }
   }

   /**
    * Returns a topological sorting of this graph as an integer array &sigma;,
    * where &sigma;(<i>i</i>) is the rank of vertex with index <i>i</i>.
    * array string representation of the object.
    * A <a href="http://en.wikipedia.org/wiki/Topological_sorting"><i>topological sorting</i></a>
    * of a directed graph is a linear ordering of all its vertices, i.e., a bijective
    * mapping &sigma;: <i>V</i> &rarr; {1, 2, ..., |<i>V</i>|}.
    * A topological sorting, however, is not possible if the graph is acyclic, i.e.,
    * if it contains a cycle.
    * Cf. S.O. Krumke &amp; H. Noltemeier: <i>Graphentheoretische Konzepte und Algorithmen.</i>
    * Teubner, Wiesbaden 2005, §3.2.
    * @return a topological sorting of this graph, or the zero array if the graph is not acyclic
    */
   public int[] topologicalSort() {
      int[] sigma = new int[vertices.length];
      LinkedList<V> L = new LinkedList<>();
      int[] indegree = getIndegrees();
      // L = {v in V: indegree(v) == 0}:
      for (V v: vertices) {
         if (indegree[v.getIndex()] == 0) {
            L.add(v);
         }
      }

      V v;
      int i = 1;
      while (!L.isEmpty()) {
         v = L.remove();
         sigma[v.getIndex()] = i;
         i++;
         for (V w: v.getAdjacency()) {
            indegree[w.getIndex()]--;
            if (indegree[w.getIndex()] == 0) {
               L.add(w);
            }
         }
      }
      if (i < vertices.length) {  // graph is not acyclic
         return new int[0];
      }
      return sigma;
   }

   /**
    * Returns a clustering of this graph which maximizes its modularity.
    * For graphs with not more than 11 vertices, the modularity is computed
    * by exhaustion, walking through all possible clusterings as
    * {@link org.mathIT.algebra.MathSet#partitions(org.mathIT.algebra.MathSet) subset partitions}
    * of the vertices, and therefore a true otimum clustering is accomplished.
    * For larger graphs, however, the running time of the exhaustive algorithm
    * explodes with estimated time complexity
    * <i>O</i>(<i>n<sup>n</sup></i>).
    * For this reason graphs with more than 11 vertices the greedy algorithm
    * as described in U. Brandes et al. (2008): ‘On Modularity Clustering’. 
    * <i>Knowledge and Data Engineering, IEEE Transactions on,</i> <b>20</b>(2): 172–188. 
    * <a href="http://dx.doi.org/10.1109/TKDE.2007.190689">doi: 10.1109/TKDE.2007.190689.</a>.
    * This implemantion requires a running time complexity of
    * <i>O</i>(<i>n<sup>5</sup></i>).
    * @return an optimum clustering with respect to modularity
    * @see Clustering#modularity(int[][], int, int[]) 
    * @see Clustering#modularity(int[][], int, int[], int[]) 
    */
   public Clustering detectClusters() {
      //if (vertices.length <= 11) return detectClustersExactly();
      
      Clustering[] clustering = new Clustering[vertices.length];
      double[] modularity = new double[clustering.length];
      int m = getNumberOfEdges();
      int[] deg = new int[0]; // initialize the array
      int[] indeg  = new int[0]; // initialize the array
      int[] outdeg = new int[0]; // initialize the array
      if (undirected) {
         deg    = getDegrees();
      } else {
         indeg  = getIndegrees();
         outdeg = getOutdegrees();
      }
      int i, j;
      double maxModularity;
      Clustering cl;
      
      // start with singletons:
      OrderedSet<Integer> set; // cluster candidate
      java.util.ArrayList<OrderedSet<Integer>> c = new java.util.ArrayList<>();      
      for (i = 0; i < vertices.length; i++) {
         set = new OrderedSet<>(i);
         c.add(set);
      }
      //System.out.println(c);
      clustering[0] = new Clustering(c);
      if (undirected) {
         modularity[0] = clustering[0].modularity(adjacency, m, deg);
      } else {
         modularity[0] = clustering[0].modularity(adjacency, m, indeg, outdeg);
      }
      /* --- Print iteration result: -----
      long[] Q__ = Numbers.bestRationalApproximation(modularity[0], 20);
      System.out.print("C_0 = " + clustering[0].toString(vertices));
      System.out.println(", Q_0 = "+modularity[0]+" = "+Q__[0]+"/"+Q__[1]);
      // ------ Print iteration result ------ */
      
      // Loop k computes clusterings with (n-k) clusters:
      for(int k = 1; k < clustering.length; k++) {
         double mod;
         maxModularity = Double.NEGATIVE_INFINITY;
         for (i = 1; i < clustering[k-1].getNumberOfClusters(); i++) {
            for (j = 0; j < i; j++) {
               cl = clustering[k-1].merge(j,i);
               if (undirected) {
                  mod = cl.modularity(adjacency, m, deg);
               } else {
                  mod = cl.modularity(adjacency, m, indeg, outdeg);
               }
               if (mod > maxModularity) {
                  maxModularity = mod;
                  clustering[k] = cl;
               }
               //System.out.println("### C_{"+k+";"+i+","+j+"}=" +cl.toString(vertices) +", Q=" + mod);
            }
         }
         modularity[k] = maxModularity;
         /* --- Print iteration result: -----
         int p = 2; // number of parts
         if (k != vertices.length - p) continue;
         long[] Q_ = Numbers.bestRationalApproximation(modularity[k], 20);
         System.out.print("C_"+k+" = " + clustering[k].toString(vertices));
         System.out.println(", Q_"+k+" = "+modularity[k]+" = "+Q_[0]+"/"+Q_[1]);
         // ------ Print iteration result ------ */
      }
      
      maxModularity = Double.NEGATIVE_INFINITY;
      int kMax = -1;
      for (int k = 0; k < modularity.length; k++) {
         if (modularity[k] > maxModularity) {
            maxModularity = modularity[k];
            kMax = k;
         }
      }
      
      long[] Q = Numbers.bestRationalApproximation(modularity[kMax], 20);
      System.out.println("Q = "+modularity[kMax]+" = "+Q[0]+"/"+Q[1]);
      /* --- Print result: -----
      int width = 200;
      int pts = 7; // font size
      String out = "<html>";
      if (vertices.length <= 100) {
          out += "C<sub>"+kMax+"</sub> = "+clustering[kMax].toString(vertices) + "<br>";
          System.out.println("C_"+kMax+" = "+clustering[kMax].toString(vertices));
          if (pts * clustering[kMax].toString(vertices).length() > width) {
             width = pts * clustering[kMax].toString(vertices).length();
          }
      }
      out += "Q = "+modularity[kMax]+" = "+Q[0]+"/"+Q[1];
      // --- Show message frame: ---
      if (pts * ("Q = "+modularity[kMax]+" = "+Q[0]+"/"+Q[1]).length() > width) {
          width = pts * ("Q = "+modularity[kMax]+" = "+Q[0]+"/"+Q[1]).length();
      }
      if (width > 600) width = 600;
      new org.mathIT.gui.MessageFrame(out, "Result", width, 60);
      // ------ Print result ------ */
      return clustering[kMax];
   }   

   /** Finds an optimum clustering by exhaustion. Advisable only for vertices.length &lt; 13.
    * @return an optimum clustering with respect to modularity
    */
   public Clustering detectClustersExactly() {
      // the following running times may change with advanced technology:
      if (vertices.length > 12) {
         String time;
         switch (vertices.length) {
            case 13: time = " about a minute"; break;
            case 14: time = " about 10 minutes"; break;
            case 15: time = " some hours"; break;
            case 16: time = " about a day"; break;
            case 17: time = " about a week"; break;
            default: time = " more than several weeks"; break;
         }
         int click = javax.swing.JOptionPane.showConfirmDialog(
            null, 
            ""+vertices.length+" nodes will last" + time + "!\nContinue anyway?",
            "Continuation Dialog", 
            2
         );
         //System.out.println("### click="+click);
         if (click == 2) return null;
         if (vertices.length > 17) {
            javax.swing.JOptionPane.showMessageDialog(null, "OK... But I stop nonetheless");
            return null;
         }
      }
      
      ArrayList<Clustering> clustering;
      int m = getNumberOfEdges();
      int[] deg, indeg, outdeg;
      if (undirected) {
         deg = getDegrees();
         indeg = deg;
         outdeg = deg;
      } else {
         indeg = getIndegrees();
         outdeg = getOutdegrees();
      }

      // Get a list of the best clusterings:
      clustering = clusterings(adjacency, m, indeg, outdeg);

      // /* --- Print result: -----
      if (vertices.length <= 100) {
         if (clustering.size() == 1) {
            System.out.println("C_* = "+clustering.get(0).toString(vertices));
         } else {
            for (int i = 0; i < clustering.size(); i++) {
               System.out.println("C_"+i+" = "+clustering.get(i).toString(vertices));
            }
         }
      }
      double mod = clustering.get(0).modularity(adjacency, m, indeg, outdeg);
      long[] Q = Numbers.bestRationalApproximation(mod, 20);
      System.out.println("Q = "+mod+" = "+Q[0]+"/"+Q[1]);
      // ------ Print result ------ */
      return clustering.get(0);
   }   

   /** Finds a clustering according to the relevance of each nde of this graph.
    *  Here the network relevance, or influence, of a node is defined by 
    *  the impact of its removal 
    *  to the {@link #computeHashimoto() Hashimoto matrix}
    *  of the remaining network.
    *  Network relevance is an important notion to study system relevance,
    *  network stability, or network reliability.
    *  The nodes are clustered into categories of network relevance.
    *  @return a clustering of nodes with respect to network relevance
    */
   public Clustering getRelevanceClusters() {
      final int N = 5; // number of categories: 0 - least relevant, ..., N - most relevant
      int[] vertexDistribution = new int[vertices.length];
      
      computeRelevances();
      
      // Divide interval [min,max] into N categories:
      double max = Arrays.stream(relevance).max().getAsDouble();
      double min = Arrays.stream(relevance).min().getAsDouble();
      double step = (max - min)/N;
      
      for (int i = 0; i < relevance.length; i++) {
         min = max;
         for(int n = N-1; n > 0; n--) {
            min -= step;
            if (relevance[i] >= min) {
               vertexDistribution[i] = n;
               break;
            }
         }
      }      
      return new Clustering(vertexDistribution);
   }

   /** Returns a list of the clusterings of the specified set with maximum modularity. */
   private ArrayList<Clustering> clusterings(int[][] adjacency, int m, int[] indeg, int[] outdeg) {
      int counter = 1;
      ArrayList<Clustering> clusterings = new ArrayList<>();
      int[] distribution = new int[vertices.length];
      Clustering c = new Clustering(distribution);
      clusterings.add(c);
      double Q = c.modularity(adjacency, m, indeg, outdeg);
      while (max(distribution) < vertices.length - 1) {
         counter++;
         nextPartition(distribution); // modifies the distribution!
         c = new Clustering(distribution);
         if (c.modularity(adjacency, m, indeg, outdeg) == Q) {
            clusterings.add(c);
         } else if (c.modularity(adjacency, m, indeg, outdeg) > Q) {
            clusterings.clear();
            clusterings.add(c);
            Q = c.modularity(adjacency, m, indeg, outdeg);
         }
      }
      System.out.println("Number of partitions: "+counter);
      return clusterings;
   }
   
   private void computeRelevances() {
      relevance = new double[vertices.length];

      // min and max absolute value of dominant eigenvalues:
      double min=Double.MAX_VALUE, max=-Double.MAX_VALUE;

      for (int i = 0; i < relevance.length; i++) {
         relevance[i] = (new Matrix(getModifiedHashimoto(i))).getDominantEigenvalue();
         if (min > relevance[i]) min = relevance[i];
         if (max < relevance[i]) max = relevance[i];
      }
      
      for (int i=0; i<relevance.length; i++) {
         relevance[i] = max - relevance[i];
      }
   }
   
   /** Returns the maximum entry of the specified array. */
   private static int max(int[] x) {
      return Arrays.stream(x).max().getAsInt();
   }
   
   /** Computes the next vertex distribution representing a cluster partition, 
    *  storing the result "in place" in the array itself.
    *  Here distribution[i] == k means that vertex i is in cluster k.
    *  It restarts a new cycle beginning with the distribution [0,...,0]
    *  if all array entries are different, i.e., 
    *  <code>
    *  distribution[0] &lt; distribution[1] &lt; ... 
    *  &lt; distribution[distribution.length - 1]
    *  </code>.
    *  This is equivalent to the condition max(distribution) < vertices.length - 1.
    */
   private static void nextPartition(int[] distribution) {
      for (int pointer = distribution.length - 1; pointer > 0; pointer--) {
         if (distribution[pointer] < max(distribution)) {
            distribution[pointer]++;
            return;
         }
         if (distribution[pointer] == max(Arrays.copyOf(distribution, pointer))) {
            distribution[pointer]++;
            return;
         }
         distribution[pointer] = 0;
      }
   }
   
   /**
    * Returns a string representation of this graph.
    * @return a string representation of this graph
    */
   @Override
   public String toString() {
      String output = "  ";
      int i, j;
      String spaces = "";
      for (i = 1; i <= (vertices.length / 10); i++) {
         spaces += " ";
      } 
      for (j = 0; j < vertices.length; j++) {
         output += vertices[j] + " ";
      }
      output += "\n";
      for (i = 0; i < vertices.length; i++) {
         output += vertices[i] + " ";
         for (j = 0; j < adjacency.length; j++) {
            output += spaces + adjacency[i][j] + " ";
         }
         output += "\n";
      }
      return output;
   }

   /**
    * Returns a string representation of the object as a table in HTML.
    * @return a HTML table representation of the object
    */
   public String toHTMLString() {
      String output = "<html><table border=\"1\">";
      output += "<tr><th></th>";
      for (V v : vertices) {
         output += "<th>" + v.getName() + "</th>";
      }
      output += "</tr>";
      for (int i = 0; i < vertices.length; i++) {
         output += "<tr>";
         output += "<th>" + vertices[i].getName() + "</th>";
         for (int j = 0; j < adjacency.length; j++) {
            output += "<td style=\"text-align:right;\">&nbsp;";
            output += adjacency[i][j];
            output += "&nbsp;</td>";
         }
         output += "</tr>";
      }
      output += "</table>";
      return output;
   }

   /**
    * Returns a string representation of the object as a {@link javax.swing.JTable JTable}.
    * @return a JTable representation of the object
    * @see javax.swing.JTable
    */
   public JTable toJTable() {
      int j;
      V entry;
      String[] columnNames = new String[vertices.length + 1];
      columnNames[0] = "Vertices";

      String[][] data = new String[vertices.length][vertices.length + 1];
      for (int i = 0; i < vertices.length; i++) {
         entry = vertices[i];
         columnNames[i+1] = entry.getName();
         data[i][0] = entry.getName();
         for (j = 1; j < data[0].length; j++) {
            data[i][j] = "" + adjacency[i][j-1];
         }
      }
      return new JTable(data,columnNames);
   }
   
   /**
    * Returns a representation of this graph as a text in CSV format.
    * @return a StringBuilder representation of this graph in CSV fomat
    */
   public StringBuilder toCSV() {
      int i, j;
      StringBuilder csv = new StringBuilder();
      
      //undirected ? csv = csv.append("undirected") : csv = csv.append("directed");
      if (undirected) {
         csv.append("undirected");
      } else {
         csv.append("directed");
      }
      
      // vertex names as columns:
      for (i = 0; i < vertices.length; i++) {
         csv.append(SEPARATOR);
         csv.append(vertices[i].getName());
      }
      
      csv.append('\n');  // new line
      
      // The adjacency matrix:
      for (i = 0; i < adjacency.length; i++) {
         csv.append(vertices[i].getName());
         for (j = 0; j < adjacency[0].length; j++) {
            csv.append(SEPARATOR);
            //csv.append(adjacency[i][j]);
            if (adjacency[i][j] == 0) {
               csv.append(""); //(adjacency[i][j]);
            } else {
               csv.append(adjacency[i][j]);
            }
         }
         csv.append('\n');  // new line      
      }
      return csv;
   }
   
   /** This method asks the user to select a file name
    *  and saves a representation of this graph as a CSV file.
    */
   public void saveAsCSV() {
      org.mathIT.util.Files.save("graph.csv", this.toCSV());
   }
   
   /** Creates a graph from a CSV file selected by a file chooser dialog.
    *  In particular, the adjacency list of each vertex is derived from the matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code>
    *  do not have an edge connecting them, the respective adjacency matrix entry
    *  <code>adjacency[i][j]</code> is expected to have the value {@link Double#POSITIVE_INFINITY}.
    *  The vertices of the returned graph are of the raw type {@link SimpleVertex}.
    *  @return the created graph
    */
   //@SuppressWarnings("unchecked")
   public static Graph<SimpleVertex> createGraphFromCSVFile() {
      final String separator = Character.toString(SEPARATOR);
      StringBuilder text = org.mathIT.util.Files.loadTextFile();

      if (text == null) return null;
      
      // Determine whether the graph is undirected:
      int pos = text.indexOf(separator), pre, i, j;
      if (pos < 0) { // || text.indexOf(separator) > text.indexOf("\n")) {
         throw new IllegalArgumentException("No valid CSV format!");
      }
      boolean undirected = "undirected".equals(text.substring(0, pos).trim());
      
      // Determine the names and the number of vertices:
      pre = pos + 1;
      pos = text.indexOf("\n", pre);
      String[] names = text.substring(pre, pos).split(separator);
      SimpleVertex[] vertices = new SimpleVertex[names.length];
      for (i = 0; i < names.length; i++) {
         vertices[i] = new SimpleVertex(i, names[i]);
      }
      
      // Ignore line beginning with "threshold" for networks of activatables:
      if ("threshold".equals(text.substring(pos+1, text.indexOf(separator, pos+1)))) {
         pre = text.indexOf(separator, pos+1) + 1;
         pos = text.indexOf("\n", pre);
      }
      
      // Ignore line beginning with "active" for networks of activatables:
      if ("active".equals(text.substring(pos+1, text.indexOf(separator, pos+1)))) {
         pre = text.indexOf(separator, pos+1) + 1;
         pos = text.indexOf("\n", pre);
      }
      
      // Determine adjacency matrix:
      int[][] adjacency = new int[vertices.length][vertices.length];
      i = 0;
      pre = text.indexOf(separator, pos) + 1; // first row contains vertex name
      pos = text.indexOf("\n", pre);
      String[] number;
      while (pos > 0 && pre > 0) {
         number = text.substring(pre, pos).split(separator, -1);
         for (j = 0; j < adjacency.length; j++) {
            if (number[j].equals("")) number[j] = "0";
            adjacency[i][j] = Integer.parseInt(number[j]);
         }
         i++;
         pre = text.indexOf(separator, pos) + 1; // first row contains vertex name
         pos = text.indexOf("\n", pre);
      }
      
      return new Graph<>(undirected,vertices,adjacency);
   }
   
   /** Creates a graph from the specified adjacency matrix.
    *  In particular, the adjacency list of each vertex is derived from the matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code>
    *  do not have an edge connecting them, the respective adjacency matrix entry
    *  <code>adjacency[i][j]</code> is expected to have the value {@link Double#POSITIVE_INFINITY}.
    *  The vertices of the returned graph are of the raw type {@link SimpleVertex}.
    *  @param adjacency the adjacency matrix determing the adjacencys of each edge of this graph
    *  @return the created graph
    */
   //@SuppressWarnings("unchecked")
   public static Graph<SimpleVertex> createGraph(int[][] adjacency) {
      if (adjacency.length != adjacency[0].length) {
         throw new IllegalArgumentException(
            "Weight matrix of the graph is not square"
         );
      }

      //Vertible<Vertex>[] vertices = new Vertex[adjacency.length];
      SimpleVertex[] vertices = new SimpleVertex[adjacency.length];
      for (int i = 0; i < vertices.length; i++) {
         vertices[i] = new SimpleVertex(i);
         //vertices[i] = new Vertex(i,"" + i);
      }

      //ArrayList<Vertible<Vertex>> adjacency;  // array list since the size of adjacency is unknown at this moment
      ArrayList<SimpleVertex> adj;  // array list since the size of adjacency is unknown at this moment
      for (int i = 0; i < vertices.length; i++) {
         adj = new ArrayList<>(vertices.length);
         for(int j = 0; j < adjacency[0].length; j++) {
            if ( adjacency[i][j] == 1 ) { // is there an edge from i to j?
               adj.add(vertices[j]);
            }
         }
         vertices[i].setAdjacency(adj.toArray(copyOf(vertices, 0)));
         vertices[i].setIndex(i);
      }
      return new Graph<>(vertices, adjacency);
   }
   // */

   /** Creates a graph from the adjacency matrix specified by the input table.
    *  In particular, the adjacency list of each vertex is derived from the matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code>
    *  do not have an edge connecting them, the respective adjacency matrix entry
    *  <code>adjacency[i][j]</code> is expected to have the value {@link Double#POSITIVE_INFINITY}.
    *  The vertices of the returned adjacencyed graph are of the raw type
    *  {@link Vertex}.
    *  @param jTable the adjacency matrix determing the adjacencys of each edge of this graph
    *  @return the created graph
    *  @throws NumberFormatException if the input table does not represent an adjacency matrix
    */
   //@SuppressWarnings("unchecked")
   public static Graph<SimpleVertex> create(JTable jTable) throws NumberFormatException {
      Graph<SimpleVertex> graph = null;
      int n = jTable.getColumnCount() - 1;
      String entry;
      int i = -1, j = -1;
      try {
         // create vertices and adjacencies:
         SimpleVertex[]  vertices = new SimpleVertex[n];
         int[][] adjacencys = new int[n][n];
         for (i = 0; i < n; i++) {
            vertices[i] = new SimpleVertex(i, jTable.getColumnName(i+1));
            for (j = 0; j < n; j++) {
               entry = (String) jTable.getValueAt(i,j+1);
               if (entry != null ) {
                  entry = entry.replace(',','.');
               }
               if (
                  entry == null || entry.equals("") || entry.equals(" ") ||
                  entry.equals("\u221E") || entry.equalsIgnoreCase("INFINITY") ||
                  entry.equalsIgnoreCase("inf") || entry.equalsIgnoreCase("\\infty")
               ) {
                  adjacencys[i][j] = 0;
               } else {
                  adjacencys[i][j] = Integer.parseInt(entry);
               }
            }
         }
         graph = new Graph<>(vertices,adjacencys);
      } catch (NumberFormatException nfe) {
         throw new NumberFormatException("Not a number at (" + i + "," + j + ")");
      } catch (Exception e) {
         //e.printStackTrace();
      }
      return graph;
   }

   /** This method displays the specified table in a simple option pane.
    *  @param table a table
    */
   // /*
   public static void show(JTable table) {
      javax.swing.JScrollPane scrollpane = new javax.swing.JScrollPane(table);
      javax.swing.JOptionPane pane = new javax.swing.JOptionPane(scrollpane,-1);
      javax.swing.JDialog dialog = pane.createDialog(null,"Graph");
      dialog.setResizable(true);
      dialog.setVisible(true);
      dialog.dispose();
   }
   // */

   /** For test purposes...
    * @param args arguments
    */
   public static void main(String[] args) {
//      int[] dist; long zaehler; long time; int n_;
//      for (n_ = 16; n_ <= 16; n_++) {
//         dist = new int[n_];
//         time = System.currentTimeMillis();
//         for (zaehler = 1; max(dist) < dist.length - 1; zaehler++) {
//            nextPartition(dist);
//            //System.out.println(zaehler + ": " +Arrays.toString(dist));
//         }
//         time = System.currentTimeMillis() - time;
//         System.out.println(n_ + " -> " + time/1000. + " sec; (" + zaehler + " partitions)");
//      }
//      System.exit(0);
      Graph<SimpleVertex> network;
      //Entfernungsmatrix
      //int inf = 0;
      int[][] y;
      /*
      y = new int[][] {
          {0,1,0},
          {1,0,1},
          {0,0,0},
      };
      network = new Graph<>(false, y, new SimpleVertex[]{new SimpleVertex(0)});
      network.computeHashimoto();
      //System.exit(0);;
      // */
      
      // /*
      y = new int[][] {
                  { 0, 1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0},
                  {1, 0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {1,0, 0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {1,0,0, 0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {1,0,0,0, 0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {1,0,0,0,0, 0, 0,1,1,1,0,0,0,0,0,0,0,1,0,0,0},
                  {0,0,0,0,0,0, 0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,1,0, 0, 0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,1,0,0, 0, 0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,1,0,0,0, 0, 1,1,1,1,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,1, 0, 0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,1,0, 0, 0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,1,0,0, 0, 0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,1,0,0,0, 0, 1,1,1,1,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,1, 0, 0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0, 0, 0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0, 0, 0,0,0,0},
                  {1,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0, 0, 1,1,1},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1, 0, 0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0, 0, 0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0, 0}
      };
      network = new Graph<>(false, y, new SimpleVertex[]{new SimpleVertex(0)});
      // */

      //y = new int[][]{{0,1,1},{0,0,1},{1,1,0}};

      //network = Graph.createGraph(y);

      /* ---- Beispiel Schlingloff (2008): ---
      y = new int[][]
      //  A B C D E F G
      {  {0,1,0,0,1,0,0}, // A
         {0,0,0,0,0,1,0}, // B
         {0,0,0,1,0,0,0}, // C
         {0,0,1,0,0,0,0}, // D
         {0,0,0,0,0,1,0}, // E
         {0,0,0,0,0,0,1}, // F
         {0,0,0,0,0,0,0}  // G
      };
      network = new Graph<>(y, new SimpleVertex[]{new SimpleVertex(0)});
      // ---- Beispiel Schlingloff (2008): --- */

      //System.out.print("Baustelle: ");
      //SimpleVertex city = network.getVertices()[1];
      //System.out.println(city);
      // --------------------------------------------------------- */

      //System.out.println(network.getCycles(network.vertices[2]));
      //network.getCycles(network.vertices[0]);

      //System.out.println(network.getComponents(network.vertices[0]));
      //System.out.println("--- Vertex " + network.vertices[1].getName() + ", index: " + network.vertices[1].getIndex());
      //System.out.println("Indegree:  " + network.getIndegree(1));
      //System.out.println("Outdegree: " + network.getOutdegree(1));
      //System.out.println("Topological Sorting: " + java.util.Arrays.toString(network.topologicalSort()));
      //network.getComponents(network.vertices[0]);
      
      //System.out.println("found vertex " + network.depthFirstSearch(network.vertices[0],null)); //network.vertices[20]));
      //System.out.println("found vertex " + network.depthFirstSearch(0,network.vertices[2]));
      //System.out.println("found vertex " + network.breadthFirstSearch(network.vertices[0],network.vertices[15]));

      //show(network.toJTable());
      //javax.swing.JOptionPane.showMessageDialog(null, network.toHTMLString(),"Graph", -1);
      //network.depthFirstSearch(0,null);
      
      //network = Graph.createGraphFromCSVFile();
      
      org.mathIT.gui.GraphViewer.visualize(network);
      Matrix L = network.laplacian();
      Matrix eigenvectors = L.getEigenvectors();
      Matrix eigen = new Matrix(new double[][] {
         L.getRealEigenvalues(), 
         //L.getImagEigenvalues()
      });
      System.out.println("Eigenvalues of L:\n "+eigen);
      //javax.swing.JOptionPane.showMessageDialog(null, "<html>" + L.toHTML("right", false));
      //javax.swing.JOptionPane.showMessageDialog(null, "<html>" + eigenvectors.toHTML("right", false));
   }
   // */
}
