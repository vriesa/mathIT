/*
 * Vertex.java - Abstract class representing a vertex of a directed graph
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

/** An instance of this class represents a vertex of a graph.
 *  @see Graph
 *  @see WeightedGraph
 *  @author Andreas de Vries
 *  @version 1.1
 *  @param <V> the type of the vertices
 */
public abstract class Vertex<V extends Vertible<V>> implements Vertible<V> {
//public class Vertex<V extends Vertex<V>> implements Vertible<V> {
   /** Name of this vertex.*/
   protected String name;
   /** The index position in the graph.*/
   protected int index;
   /** Adjacency list of this vertex. It contains all vertices connected to it by an edge.*/
   //protected Vertible<V>[] adjacency;
   protected V[] adjacency;
   /** The predecessor of this vertex in an optimum path in the graph.*/
   V predecessor;
   /** The distance to the predecessor of this vertex in an optimum path.*/
   private double distance;
   /** Flag which is utilized by several standard algorithms, e.g. breadth-first-search.*/
   private boolean marked;
   /** Flag which is utilized by several standard algorithms, e.g. breadth-first-search.*/
   private boolean inProcess;
   
   /** Creates a vertex with the specified index which also specifies its name.
    *  @param index the index of this vertex
    */
   public Vertex(int index) {
      this.name = "" + index;
      this.index = index;
      this.adjacency = null;
      this.predecessor = null;
      this.distance = WeightedGraph.INFINITY;
   }
   
   /** Creates a vertex with the specified name.
    *  @param name the name of this vertex
    */
   public Vertex(String name) {
      this.name = name;
      this.index = -1;
      this.adjacency = null;
      this.predecessor = null;
      this.distance = WeightedGraph.INFINITY;
   }
   
   /** Creates a vertex with the specified index and name.
    *  @param index the index of this vertex
    *  @param name the name of this vertex
    */
   @SuppressWarnings("unchecked")
   public Vertex(int index, String name) {
      this.name = name;
      this.index = index;
      this.adjacency = (V[]) java.lang.reflect.Array.newInstance(this.getClass(), 0);
      this.predecessor = null;
      this.distance = WeightedGraph.INFINITY;
   }
   
   /** Creates a vertex with the specified index and adjacency list,
    *  whose name equals the index.
    *  @param index the index of this vertex
    *  @param adjacency the adjacency list of this vertex
    */
   public Vertex(int index, V[] adjacency) {
      this.name = "" + index;
      this.index = index;
      this.adjacency = adjacency;
      this.predecessor = null;
      this.distance = WeightedGraph.INFINITY;
   }
   
   /** Creates a vertex with the specified index and adjacency list,
    *  whose name equals the index.
    *  @param index the index of this vertex
    *  @param name the name of this vertex
    *  @param adjacency the adjacency list of this vertex
    */
   public Vertex(int index, String name, V[] adjacency) {
      this.name = name;
      this.index = index;
      this.adjacency = adjacency;
      this.predecessor = null;
      this.distance = WeightedGraph.INFINITY;
   }
   
   /** Creates and returns a copy of this object. 
    *  For any object x, the expression <code>x.clone() != x</code> is true,.
    *  @return a clone of this object
    */
   @Override
   public abstract V copy(); 

   /** Sets the name of this vertex as specified by the weighted graph it belongs to.
    *  The index has to be consistent with the weight matrix of the
    *  weighted graph.
    *  @param name the name of this vertex in the weighted graph
    */
   @Override
   public void setName(String name) {
      this.name = name;
   }
   
   /** Returns the name of this vertex.
    *  @return the name of this vertex
    */
   @Override
   public String getName() {
      return name;
   }
   
   /** Returns the index of this vertex as specified by the weighted graph it belongs to.
    *  The index has to be consistent with the weight matrix of the
    *  weighted graph.
    *  @return the name of this vertex
    */
   @Override
   public int getIndex() {
      return index;
   }
   
   /** Sets the index of this vertex as specified by the weighted graph it belongs to.
    *  The index has to be consistent with the weight matrix of the
    *  weighted graph.
    *  @param index the index of this vertex in the weighted graph
    */
   @Override
   public void setIndex(int index) {
      this.index = index;
   }
   
   /** Returns the adjacency list of this vertex as specified by the weighted 
    *  graph it belongs to.
    *  The adjacency list has to be consistent with the weight matrix of the
    *  weighted graph.
    *  @return the adjacency list this vertex
    */
   @Override
   public V[] getAdjacency() {
      return adjacency;
   }
   
   /** Sets the adjacency list of this vertex as specified by the weighted 
    *  graph it belongs to.
    *  The adjacency list has to be consistent with the weight matrix of the
    *  weighted graph.
    *  @param adjacency the adjacency list this vertex
    */
   @Override
   public void setAdjacency(V[] adjacency) {
      this.adjacency = adjacency;
   }
      
   /** Returns the predecessor of this vertex in the shortest path from a
    *  specified source as determined by the Dijkstra algorithm.
    *  @return the predecessor of this vertex in the shortest path
    */
   @Override
   public V getPredecessor() {
      return predecessor;
   }
   
   /** Sets the predecessor of this vertex in the shortest path from a
    *  specified source as determined by the Dijkstra algorithm.
    *  @param predecessor the predecessor of this vertex in the shortest path
    */
   @Override
   public void setPredecessor(V predecessor) {
      this.predecessor = predecessor;
   }
   
   /** Returns the distance of the shortest path from a
    *  specified source to this vertex as determined by the Dijkstra algorithm.
    *  @return the distance of this vertex in the shortest path
    */
   @Override
   public double getDistance() {
      return distance;
      //return key;
   }
   
   /** Sets the distance of the shortest path from a
    *  specified source to this vertex as determined by the Dijkstra algorithm.
    *  @param distance the distance of this vertex in the shortest path
    */
   @Override
   public void setDistance(double distance) {
      this.distance = distance;
      //this.key = distance;
   }
   
   /** Marks this vertex as true. The mark is designed to be 
    *  utilized as a temporary and dynamic attribute for algorithms.
    */
   @Override
   public void mark() {
      marked = true;
   }
   
   /** Sets this vertex mark flag to the specified value. The mark is designed 
    *  to be utilized as a temporary and dynamic attribute for algorithms.
    *  @param marked flag which marks this vertex
    */
   @Override
   public void setMarked(boolean marked) {
      this.marked = marked;
   }
   
   /** Returns a flag if this vertex is marked. The flag is designed to be 
    *  utilized as a temporary and dynamic attribute for algorithms.
    *  @return <code>true</code> if and only this vertex is marked
    */
   @Override
   public boolean isMarked() {
      return marked;
   }
   
   /** Returns a flag if this vertex is in process. The flag is designed to be 
    *  utilized as a temporary and dynamic attribute for algorithms.
    *  @return <code>true</code> if and only this vertex is marked
    */   
   @Override
   public boolean isInProcess() {
      return inProcess;
   }
   
   /** Sets this vertex process flag to the specified value. The flag is designed 
    *  to be utilized as a temporary and dynamic attribute for algorithms.
    *  @param inProcess flag which marks this vertex
    */
   @Override
   public void setInProcess(boolean inProcess) {
      this.inProcess = inProcess;
   }
   
   @Override
   public String toString() {
      return name;
      /*
      String output = name + "( -> ";
      int i;
      for(i = 0; i < adjacency.length - 1; i++) {
         output += adjacency[i].getName() + ",";
      }
      if (adjacency.length > 0) {
         output += adjacency[i].getName();
      }
      output += ")";
      return output;
      // */
   }
}
