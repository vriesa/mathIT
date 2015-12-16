/*
 * WeightedGraph.java - Class representing a weighted directed graph
 *
 * Copyright (C) 2009-2012 Andreas de Vries
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
 */
package org.mathIT.graphs;

import java.util.ArrayList;
import static java.util.Arrays.copyOf;
import javax.swing.JTable;

/** 
 * This class represents a weighted directed graph as an array list of vertices.
 * Each vertex of a graph of this class must be an instance of a class
 * implementing the interface {@link Vertible}.
 * To create a weighted graph, a set of vertices implementing <code>Vertible.java</code>
 * along with the distance matrix is necessary.
 * It is important to note that the indices of the vertices depend uniquely on the
 * distance matrix <code>weight</code> in the sense that the distance from 
 * vertex <i>i</i> to vertex <i>j</i> is given by the matrix entry
 * <code>weight[i][j]</code>.
 * <p>
 * Most easily, a vertex class is programmed by extending the vertex
 * class {@link Vertex}. 
 * For example, if a network of cities is to be established, a class 
 * <code>City</code> can be written such as
 * </p>
 * <pre>
 *   public class City extends Vertex&lt;City&gt; {
 *      ...
 *   }
 * </pre>
 * <p>
 * and the network of cities as
 * </p>
 * <pre>
 *   public class CityNetwork extends WeightedGraph&lt;City&gt; {
 *      ...
 *   }
 * </pre>
 * <p>
 * A more tedious, but also more flexible way is to implement instead the
 * interface {@link Vertible}, e.g.,
 * </p>
 * <pre>
 *   public class City implements Vertible&lt;City&gt; {
 *      ...
 *   }
 * </pre>
 * <p>
 * If one is interested only in a simple weighted graph consisting of vertices 
 * with the minimal set of attributes, the static method
 * {@link #createWeightedGraph(double[][])} can be invoked,
 * </p>
 * <pre>
 *   public class MyGraph {
 *      double[][] matrix;
 *      ...
 *      WeightedGraph&lt;SimpleVertex&gt; graph = WeightedGraph.create(matrix);
 *      ...
 *   }
 * </pre>
 * 
 * @author Andreas de Vries
 * @version 1.1
 * @param <V> the type of the vertices of this graph
 */
//public class WeightedGraph<V extends Vertex<V>> extends Graph<V> {
public class WeightedGraph<V extends Vertible<V>> extends Graph<V> {
   /** Constant representing the infinite weight.*/
   public final static double INFINITY = Double.POSITIVE_INFINITY; 
   /** Decimal Format "#,##0.#####".*/
   public final static java.text.DecimalFormat DF = new java.text.DecimalFormat("#,##0.#####");
   /** Weight matrix. weight[i][j] is the distance from vertex i to vertex j.*/
   protected double[][] weight;
   
   /** Creates a graph from the specified array of vertices and the weight matrix.
    *  In particular, the adjacency list of each vertex is derived from the matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code> 
    *  do not have an edge connecting them, the respective weight matrix entry 
    *  <code>weight[i][j]</code> is expected to have the value {@link Double#POSITIVE_INFINITY}.
    *  In each vertex, previously stored values for its index or its adjacency 
    *  list are always overwritten with the ones derived by the weight matrix!
    *  @param vertices array of the vertices forming this graph
    *  @param weight the weight matrix determing the weights of each edge of this graph
    */
   public WeightedGraph(V[] vertices, double[][] weight) {
      this(false,vertices,weight);
   }
   
   /** Creates a graph from the specified array of vertices and the weight matrix.
    *  In particular, the adjacency list of each vertex is derived from the matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code> 
    *  do not have an edge connecting them, the respective weight matrix entry 
    *  <code>weight[i][j]</code> is expected to have the value {@link Double#POSITIVE_INFINITY}.
    *  In each vertex, previously stored values for its index or its adjacency 
    *  list are always overwritten with the ones derived by the weight matrix!
    *  @param undirected indicator whether this graph is undirected
    *  @param vertices array of the vertices forming this graph
    *  @param weight the weight matrix determing the weights of each edge of this graph
    *  @throws IllegalArgumentException if the weight matrix is not symmetric or if
    *  the number of vertices is inconsistent with the matrix size
    */
   public WeightedGraph(boolean undirected, V[] vertices, double[][] weight) {
      super(undirected, vertices);
      
      if (weight.length != vertices.length || weight[0].length != vertices.length) {
         throw new IllegalArgumentException(
            "Vertex set and weight matrix of the graph are inconsistent"
         );
      }
      
      if (undirected && !isSymmetric(weight)) {
         throw new IllegalArgumentException(
            "Weight matrix of this undirected graph must be symmetric"
         );
      }

      this.adjacency = new int[weight.length][weight.length];
      
      ArrayList<V> adj;  // an array list since the length of the adjacency is unknown at this moment
      for (int i = 0; i < vertices.length; i++) {
         adj = new ArrayList<>(vertices.length);
         for(int j = 0; j < weight[0].length; j++) {            
            if ( weight[i][j] != 0 && weight[i][j] < INFINITY ) { // is there an edge from i to j?
               adj.add(vertices[j]);
               adjacency[i][j] = 1;
            }
         }
         vertices[i].setAdjacency(adj.toArray(java.util.Arrays.copyOf(vertices, 0)));
         //vertices[i].setAdjacency(adj.toArray(copyOf(vertices, 0)));
         vertices[i].setIndex(i);
      }
      this.numberOfEdges = this.computeNumberOfEdges();
      //this.vertices = vertices;
      this.weight = weight;
      this.weighted = true;
   }
   
   /** Creates a directed graph from the specified array list of vertices and the weight matrix;
    *  the adjacency list of each vertex is derived from the weight matrix.
    *  If two vertices <code>v<sub>i</sub></code> and <code>vertices<sub>j</sub></code> 
    *  do not have an edge connecting them, the respective weight matrix entry 
    *  <code>weight[i][j]</code> is expected to have the value {@link Double#POSITIVE_INFINITY}.
    *  In each vertex, previously stored values for its index or its adjacency 
    *  list are always overwritten with the ones derived by the weight matrix!
    *  @param vertices array of the vertices forming this graph
    *  @param weight the weight matrix determing the weights of each edge of this graph
    *  @param arrayTemplate an array of the type of vertices. This array may be empty.
    *  It is necessary technically because generic array creation is prohibited in Java.
    */
   //@SuppressWarnings("unchecked")
   public WeightedGraph(ArrayList<V> vertices, double[][] weight, V[] arrayTemplate) {
      super(vertices, arrayTemplate);
      
      if (weight.length != vertices.size() || weight[0].length != vertices.size()) {
         throw new IllegalArgumentException(
            "Vertex set and weight matrix of the graph are inconsistent"
         );
      }
      
      /*      
      this.vertices = (V[]) vertices.toArray(new Vertex[]{});
      //this.vertices = (V[]) vertices.toArray((V[]) new Vertex[]{});
      //this.vertices = (V[]) copyOf(vertices.toArray((V[]) new Vertible[]{}), vertices.size());
      //this.vertices = vertices.toArray((V[]) java.lang.reflect.Array.newInstance(Object.class, 0));
      */
      /*
      for (int i = 0; i < this.vertices.length; i++) {
         this.vertices[i] = (E) this.vertices[i];
      }
      */
      
      //System.out.println("### vertices:" + this.vertices);      
      
      this.adjacency = new int[weight.length][weight.length];
      this.weight = weight;
      this.weighted = true;
      
      ArrayList<V> adj;  // an array list since the length of the adjacency is unknown at this moment
      for (int i = 0; i < vertices.size(); i++) {
         adj = new ArrayList<>(vertices.size());
         for(int j = 0; j < weight[0].length; j++) {            
            if ( weight[i][j] != 0 && weight[i][j] < INFINITY ) { // is there an edge from i to j?
               adj.add(vertices.get(j));
               adjacency[i][j] = 1;
            }
         }
         this.vertices[i].setAdjacency(adj.toArray(copyOf(this.vertices, 0)));
         this.vertices[i].setIndex(i);
      }
      this.numberOfEdges = this.computeNumberOfEdges();
   }
   
   /** Checks whether the weight matrix is symmetric. */
   private boolean isSymmetric(double[][] weight) {
      boolean symmetric = true;
      for (int i = 1; symmetric && i < weight.length; i++) {
         for (int j = 0; symmetric && j < i; j++) {
            symmetric &= (weight[i][j] == weight[j][i]);
         }
      }
      return symmetric;
   }

   /**
    * The weight matrix of this graph.
    * @return the weight matrix of this graph
    */
   public double[][] getWeight() {
      return weight;
   }
   
   /** Distance matrix filled by Floyd-Warshall algorithm.*/
   public double[][] dist;
   /** Successor matrix filled by Floyd-Warshall algorithm.*/
   public int[][] next;
   
   /** Finds all-pairs shortest paths in this graph.
    *  The method stores for each vertex in the matrices dist and pred.
    */
   public void floydWarshall() {
      dist = new double[vertices.length][vertices.length];
      next = new int[vertices.length][vertices.length];
      
      for(int v = 0; v < vertices.length; v++) {
         for(int w = 0; w < vertices.length; w++) {
            dist[v][w] = weight[v][w];
            next[v][w] = -1;
         }
      }
      
      for(int u = 0; u < vertices.length; u++) {
         for(int v = 0; v < vertices.length; v++) {
            for(int w = 0; w < vertices.length; w++) {
               // relax:
               if (dist[v][w] > dist[v][u] + dist[u][w] ) { //weight[u][w]) {
                  dist[v][w] = dist[v][u] + dist[u][w]; //weight[u][w];
                  next[v][w] = u;
               }
            }
         }
      }
   }
   
   /** Finds shortest paths from source vertex v[s] to all other vertices of this graph.
    *  The method stores for each vertex individually its minimal distance to s and 
    *  its predecessor on a shortest path.
    *  @param s index of the source vertex
    *  @throws IllegalArgumentException if there are negative weights
    */
   public void dijkstra(int s) {
      // check whether dijkstra is applicable:
      for (int i = 0; i < weight.length; i++) {
         for (int j = 0; j < weight[0].length; j++) {
            if (weight[i][j] < 0) {
               //throw new IllegalArgumentException(
               //   "Dijkstra algorithm is not applicable to a graph with a negative weight"
               //);
            }
         }
      }
      
      // initialize single source s;
      for (int i = 0; i < vertices.length; i++) {
         vertices[i].setPredecessor(null);
         vertices[i].setDistance(INFINITY);
      }
      
      V source = vertices[s];
      source.setDistance(0);
      V[] adj = source.getAdjacency();
      
      for (int i = 0; i < adj.length; i++) {
         adj[i].setPredecessor(source);
         adj[i].setDistance(weight[source.getIndex()][adj[i].getIndex()]);
      }

      PriorityQueue<V> q = new PriorityQueue<>(this.vertices);
      //FibonacciHeap<V> q = new FibonacciHeap<V>(this.vertices);
      V u;
      double d;
    
      while( q.size() > 0 ) {
         u = q.extractMin();
         for(V v : u.getAdjacency()) {
            // relax:
            d = u.getDistance() + weight[u.getIndex()][v.getIndex()];
            if (v.getDistance() > d) {
               q.decreaseKey(v,d); // changes distance of v and adjust the heap
               v.setPredecessor(u);
            }
         }
      }
   }
   
   
   /** Distance matrix filled by Bellman-Ford algorithm.*/
   public double[] distance;
   /** Successor matrix filled by Bellman-Ford algorithm.*/
   public int[] pred;
   
   /** Finds shortest paths from source vertex v[s] to all other vertices of this graph.
    *  The method stores for each vertex individually its minimal distance to s and 
    *  its predecessor on a shortest path.
    *  <p>
    *  Running time <i>T</i>(<i>n</i>, <i>e</i>) = &Theta;(<i>n</i> <i>e</i>)
    *  where <i>n</i> denotes the number of vertices and <i>e</i> the number of edges.
    *  </p>
    *  @param source index of the source vertex
    *  @throws IllegalArgumentException if the grapph contains negative cycles
    */
   public void bellmanFord(int source) {  // noch zu testen!!!
      distance = new double[vertices.length]; pred = new int[vertices.length];
      // Step 1: Initialize graph
      for (int v = 0; v < vertices.length; v++) {
         if (v == source) {
            distance[v] = 0;
         } else {
            distance[v] = Double.POSITIVE_INFINITY;
         }
         pred[v] = -1;
      }
      
      // Step 2: relax edges repeatedly
      for (int i = 1; i < vertices.length; i++) {
/*
         for (int u = 0; u < vertices.length; u++) {
            for (int v = 0; v < vertices.length; v++) {
               if (distance[u] + weight[u][v] < distance[v]) {
                  distance[v] = distance[u] + weight[u][v];
                  pred[v] = u;
               }
            }
         }
 */
         double d;
         for(V u : vertices) {
            for (V v : u.getAdjacency()) {
               d = distance[u.getIndex()] + weight[u.getIndex()][v.getIndex()];
               if (d < distance[v.getIndex()]) {
                  distance[v.getIndex()] = d;
                  pred[v.getIndex()] = u.getIndex();
               }               
            }
         }
      }
   
      // Step 3: check for negative-weight cycles
      for (int u = 0; u < vertices.length; u++) {
         for (int v = 0; v < vertices.length; v++) {
            if (distance[u] + weight[u][v] < distance[v]) {
               //throw new IllegalArgumentException(
               //   "Negative cycle detected: [u,v]=["+u+","+v+"]"
               //);
            }
         }
      }
   }
   
   /**
    * Returns a representation of this graph as a text in CSV format.
    * @return a StringBuilder representation of this graph in CSV fomat
    */
   @Override
   public StringBuilder toCSV() {
      //final char separator = Graph.separator;
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

      //System.out.println("### class of vertex: " + vertices[0].getClass());
      if (vertices[0] instanceof Activatable) {
         csv.append("threshold");
         for (i = 0; i < vertices.length; i++) {
            csv.append(SEPARATOR);
            csv.append(DF.format(((Activatable)vertices[i]).getThreshold()).replace(',', '.'));
         }
         csv.append('\n');  // new line
      }

      //System.out.println("### active: " + ((SocialNetwork)this).isActive());
      if (this instanceof NetworkOfActivatables && ((NetworkOfActivatables)this).isActive()) {
         csv.append("active");
         for (i = 0; i < vertices.length; i++) {
            csv.append(SEPARATOR);
            if (((Activatable)vertices[i]).isActive()) {
               csv.append('1');
            } else {
               csv.append('0');
            }
         }
         csv.append('\n');  // new line
      }

      // The weight matrix:
      for (i = 0; i < weight.length; i++) {
         csv.append(vertices[i].getName());
         for (j = 0; j < weight[0].length; j++) {
            csv.append(SEPARATOR);
            //csv.append(DF.format(weight[i][j]));
            if (weight[i][j] == 0) {
               csv.append(""); //(weight[i][j]);
            } else {
               csv.append(DF.format(weight[i][j]));
            }
         }
         csv.append('\n');  // new line      
      }
      return csv;
   }
   
   /**
    * Returns a string representation of the object.
    * @return a string representation of the object
    */
   @Override
   public String toString() {
      String spaces = "";
      for (int i = 1; i <= (vertices.length / 10); i++) spaces += " "; 
      String output = spaces + "  ";
      for (int j = 0; j < vertices.length; j++) {
         output += vertices[j] + " ";
      }
      output += "\n";
      for (int i = 0; i < vertices.length; i++) {
         output += vertices[i] + " ";
         for (int j = 0; j < weight.length; j++) {
            output += spaces + DF.format(weight[i][j]) + " ";
         }
         output += "\n";
      }
      return output;
   }
      
   /** Creates a graph from the specified weight matrix.
    *  In particular, the adjacency list of each vertex is derived from the matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code> 
    *  do not have an edge connecting them, the respective weight matrix entry 
    *  <code>weight[i][j]</code> is expected to have the value 
    *  {@link Double#POSITIVE_INFINITY} or <code>0.0</code>.
    *  The vertices of the returned weighted graph are of the raw type
    *  {@link Vertex}.
    *  @param weight the weight matrix determing the weights of each edge of this graph
    *  @return the graph specified by the weight matrix
    */
   //@SuppressWarnings("unchecked")
   public static WeightedGraph<SimpleVertex> createWeightedGraph(double[][] weight) {
   //public static WeightedGraph createWeightedGraph(double[][] weight) {
      if (weight.length != weight[0].length) {
         throw new IllegalArgumentException(
            "Weight matrix of the graph is not square"
         );
      }
      
      //Vertible<Vertex>[] vertices = new Vertex[weight.length];
      SimpleVertex[] vertices = new SimpleVertex[weight.length];
      for (int i = 0; i < vertices.length; i++) {
         vertices[i] = new SimpleVertex(i,"" + i);
         //vertices[i] = new Vertex(i,"" + i);
      }
      
      //ArrayList<Vertible<Vertex>> adjacency;  // array list since the size of adjacency is unknown at this moment
      ArrayList<SimpleVertex> adjacency;  // array list since the size of adjacency is unknown at this moment
      for (int i = 0; i < vertices.length; i++) {
         //adjacency = new ArrayList<Vertible<Vertex>>(vertices.length);
         adjacency = new ArrayList<>(vertices.length);
         for(int j = 0; j < weight[0].length; j++) {            
            if ( weight[i][j] != 0 && weight[i][j] < INFINITY ) { // is there an edge from i to j?
               adjacency.add(vertices[j]);
            }
         }
         vertices[i].setAdjacency(adjacency.toArray(copyOf(vertices,0)));
         //vertices[i].setAdjacency(adjacency.toArray(new Vertex[0]));
         vertices[i].setIndex(i);
      }
      return new WeightedGraph<>(vertices, weight);
   }
   
   /** Creates a weighted graph from a CSV file selected by a file chooser dialog.
    *  In particular, the adjacency list of each vertex is derived from the matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code>
    *  do not have an edge connecting them, the respective adjacency matrix entry
    *  <code>adjacency[i][j]</code> is expected to have the value {@link Double#POSITIVE_INFINITY}.
    *  The vertices of the returned graph are of the raw type {@link SimpleVertex}.
    *  @return the graph specified by the CSV file
    */
   public static WeightedGraph<SimpleVertex> createWeightedGraphFromCSVFile() {
      final String separator = Character.toString(SEPARATOR);
      StringBuilder text = org.mathIT.util.Files.loadTextFile();

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
      
      // Determine weight matrix:
      double[][] weight = new double[vertices.length][vertices.length];
      i = 0;
      pre = text.indexOf(separator, pos) + 1; // first row contains vertex name
      pos = text.indexOf("\n", pre);
      String[] number;
      while (pos > 0 && pre > 0) {
         number = text.substring(pre, pos).split(separator, -1);
         for (j = 0; j < weight.length; j++) {
            if (number[j].equals("")) number[j] = "0";
            weight[i][j] = Double.parseDouble(number[j].replace(',', '.'));
         }
         i++;
         pre = text.indexOf(separator, pos) + 1; // first row contains vertex name
         pos = text.indexOf("\n", pre);
      }
      
      return new WeightedGraph<>(undirected,vertices,weight);
   }
   
   /** Creates a graph from the weight matrix specified by the input table.
    *  In particular, the adjacency list of each vertex is derived from the matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code> 
    *  do not have an edge connecting them, the respective weight matrix entry 
    *  <code>weight[i][j]</code> is expected to have the value {@link Double#POSITIVE_INFINITY}.
    *  The vertices of the returned weighted graph are of the raw type
    *  {@link Vertex}.
    *  @param jTable the weight matrix determing the weights of each edge of this graph
    *  @return the graph specified by the weight matrix
    */
   //@SuppressWarnings("unchecked")
   public static WeightedGraph<SimpleVertex> create(JTable jTable) throws NumberFormatException {
      WeightedGraph<SimpleVertex> graph = null;
      int n = jTable.getColumnCount() - 1;
      String entry;
      int i = -1, j = -1;
      try {
         // create vertices and weights:
         SimpleVertex[]  vertices = new SimpleVertex[n];
         double[][] weights = new double[n][n];
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
                  entry.equalsIgnoreCase("inf") || entry.equals("\\infty")
               ) {
                  weights[i][j] = INFINITY;
               } else {
                  weights[i][j] = Double.parseDouble(entry);
               }
            }
         }
         graph = new WeightedGraph<>(vertices,weights);
      } catch (NumberFormatException nfe) {
         throw new NumberFormatException("Not a number at (" + i + "," + j + ")");
      } catch (Exception e) {
         e.printStackTrace();
      }
      return graph;
   }
   
   // /*
   public static void show(JTable tabelle) {
      javax.swing.JScrollPane scrollpane = new javax.swing.JScrollPane(tabelle);
      //javax.swing.JOptionPane.showMessageDialog(null, scrollpane, "Ergebnis", -1);
      javax.swing.JOptionPane pane = new javax.swing.JOptionPane(scrollpane,-1);
      javax.swing.JDialog dialog = pane.createDialog(null,"Graph");
      dialog.setResizable(true);
      dialog.setVisible(true);
      dialog.dispose();
   }
   // */
   
   /*
   public static void main(String[] args) {
      WeightedGraph<SimpleVertex> network;
      //Entfernungsmatrix
      double inf = Double.POSITIVE_INFINITY;
      double[][] y ={{ 0 ,  100 ,  100 , 100 , 100 , inf, inf , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, 225, inf, inf, inf}, 
                     {100,   0  ,  inf , inf , inf , inf, inf , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf},
                     {100,  inf ,   0  , inf , inf , inf, inf , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf},
                     {100,  inf ,  inf ,  0  , inf , inf, inf , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf},
                     {100,  inf ,  inf , inf ,  0  , inf, inf , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf},
                     {225,  inf ,  inf , inf , inf ,  0 , 100 , 100, 100, 265, inf, inf, inf, inf, inf, inf, inf, 265, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , 100,  0  , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , 100, inf ,  0 , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , 100, inf , inf,  0 , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , 265, inf , inf, inf,  0 , 100, 100, 100, 225, inf, inf, inf, inf, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , inf, inf , inf, inf, 100,  0 , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , inf, inf , inf, inf, 100, inf,  0 , inf, inf, inf, inf, inf, inf, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , inf, inf , inf, inf, 100, inf, inf,  0 , inf, inf, inf, inf, inf, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , inf, inf , inf, inf, 225, inf, inf, inf,  0 , 100, 100, 100, 245, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , inf, inf , inf, inf, inf, inf, inf, inf, 100,  0 , inf, inf, inf, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , inf, inf , inf, inf, inf, inf, inf, inf, 100, inf,  0 , inf, inf, inf, inf, inf},
                     {inf,  inf ,  inf , inf , inf , inf, inf , inf, inf, inf, inf, inf, inf, 100, inf, inf,  0 , inf, inf, inf, inf},
                     {225,  inf ,  inf , inf , inf , 345, inf , inf, inf, inf, inf, inf, inf, 245, inf, inf, inf,  0 , 100, 100, 100},
                     {inf,  inf ,  inf , inf , inf , inf, inf , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, 100,  0 , inf, inf},
                     {inf,  inf ,  inf , inf , inf , inf, inf , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, 100, inf,  0 , inf},
                     {inf,  inf ,  inf , inf , inf , inf, inf , inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, 100, inf, inf,  0 }};
                     
      //y = new double[][]{{0,2,inf},{inf,0,3},{2,5,0}};
      network = WeightedGraph.createWeightedGraph(y);
      
      // --- Baustelle: ----
      //ArrayList<SimpleVertex> v = new ArrayList<SimpleVertex>();
      //v.add(new SimpleVertex(1, "A"));
      //v.add(new SimpleVertex(2, "B"));
      //v.add(new SimpleVertex(3, "C"));
      //network = new WeightedGraph<SimpleVertex>(v,y);
      // --------------------
      
      
      String ausgabe = "";
      int s, z;
      s=0; z=9; //1;
      //s = 12; z = 9;
      network.dijkstra(s);
      network.bellmanFord(s);
      System.out.print("dist = [");
      for (int i = 0; i < network.vertices.length; i++) {
         System.out.print(network.distance[i] + " ");
      }
      System.out.println("]");
      System.out.print("pred = [");
      for (int i = 0; i < network.vertices.length; i++) {
         System.out.print(network.pred[i] + " ");
      }
      System.out.println("]");
      
//      network.floydWarshall();
//      System.out.println("dist=");
//      for (int i = 0; i < network.vertices.length; i++) {
//         for (int j = 0; j < network.vertices.length; j++) {
//            System.out.print(network.dist[i][j] + " ");
//         }
//         System.out.println();
//      }
//      System.out.println("next=");
//      for (int i = 0; i < network.vertices.length; i++) {
//         for (int j = 0; j < network.vertices.length; j++) {
//            System.out.print(network.next[i][j] + " ");
//         }
//         System.out.println();
//      }
      
      //ArrayList<Vertible> route = new ArrayList<Vertible>();
      //Vertible city = network.getVertices()[z];
      ArrayList<SimpleVertex> route = new ArrayList<>();
      SimpleVertex city = network.getVertices()[z];
      while( city != null && !city.equals(network.vertices[s]) ) {
         route.add(city);
         city = city.getPredecessor();
      }
    
      ausgabe += network.vertices[s].getName();
      for(int i = route.size() - 1; i >= 0; i--) {
         ausgabe += " -> " + route.get(i).getName();
      }
      
      for(SimpleVertex c : network.getVertices()) {
         ausgabe += "\n Distance from " + network.getVertices()[s].getName() + " to " + c.getName() + ": " + c.getDistance();
      }
      System.out.println(ausgabe);
      //javax.swing.JOptionPane.showMessageDialog(null, network.toHTMLString(),"Graph", -1);
      //show(network.toJTable());
      //network.depthFirstSearch(0,null);
   }
   // */
}
