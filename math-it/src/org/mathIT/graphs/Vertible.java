/*
 * Vertible.java - Interface specifying a vertex of a directed graph
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

/** This interface specifies classes representing a vertex of a graph.
 *  @see Graph
 *  @author Andreas de Vries
 *  @version 1.1
 *  @param <V> the type of the vertices of the graph
 */
public interface Vertible<V extends Vertible<V>> {
   /** Creates and returns a new copy of this object. 
    *  For any object x, the expression <code>x.copy() != x</code> is true.
    *  The typical implementation in a class <code>VertexClazz</code> 
    *  implementing this interface could simply look like 
    *  <code>return new VertexClazz(...)</code>.
    *  @return a copy of this object
    */
   public V copy();

   /** Sets the index of this vertex as specified by the graph it belongs to.
    *  The index has to be consistent with the weight matrix of the
    *  graph.
    *  @param name the name of this vertex in the graph
    */
   public void setName(String name);
   
   /** Returns the name of this vertex.
    *  @return the name of this vertex
    */
   public String getName();
   
   /** Returns the index of this vertex as specified by the graph it belongs to.
    *  The index has to be consistent with the weight matrix of the
    *  graph.
    *  @return the name of this vertex
    */
   public int getIndex();
   
   /** Sets the index of this vertex as specified by the graph it belongs to.
    *  The index has to be consistent with the weight matrix of the
    *  graph.
    *  @param index the index of this vertex in the graph
    */
   public void setIndex(int index);
   
   /** Returns the adjacency list of this vertex as specified by the 
    *  graph it belongs to.
    *  The adjacency list has to be consistent with the weight matrix of the
    *  graph.
    *  @return the adjacency list this vertex
    */
   public V[] getAdjacency();
   //public Vertible<V>[] getAdjacency();
   
   /** Sets the adjacency list of this vertex as specified by the 
    *  graph it belongs to.
    *  The adjacency list has to be consistent with the weight matrix of the
    *  graph.
    *  @param adjacency the adjacency list this vertex
    */
   public void setAdjacency(V[] adjacency);
      
   /** Returns the predecessor of this vertex in the shortest path from a
    *  specified source as determined by the Dijkstra algorithm.
    *  @return the predecessor of this vertex in the shortest path
    */
   public V getPredecessor();
   
   /** Sets the predecessor of this vertex in the shortest path from a
    *  specified source as determined by the Dijkstra algorithm.
    *  @param predecessor the predecessor of this vertex in the shortest path
    *  @see WeightedGraph
    */
   public void setPredecessor(V predecessor);
   
   /** Returns the distance of the shortest path from a
    *  specified source to this vertex as determined by the Dijkstra algorithm.
    *  @return the distance of this vertex in the shortest path
    *  @see WeightedGraph
    */
   public double getDistance();
   
   /** Sets the distance of the shortest path from a
    *  specified source to this vertex as determined by the Dijkstra algorithm.
    *  @param distance the distance of this vertex in the shortest path
    */
   public void setDistance(double distance);
   
   /** Marks this vertex as true. The mark is designed to be 
    *  utilized as a temporary and dynamic attribute for algorithms.
    */   
   public void mark();
   
   /** Sets this vertex mark flag to the specified value. The mark is designed 
    *  to be utilized as a temporary and dynamic attribute for algorithms.
    *  @param marked flag which marks this vertex
    */
   public void setMarked(boolean marked);
   
   /** Returns a flag if this vertex is marked. The flag is designed to be 
    *  utilized as a temporary and dynamic attribute for algorithms.
    *  @return <code>true</code> if and only this vertex is marked
    */
   public boolean isMarked();
   
   /** Returns a flag if this vertex is in process. The flag is designed to be 
    *  utilized as a temporary and dynamic attribute for algorithms.
    *  @return <code>true</code> if and only this vertex is marked
    */   
   public boolean isInProcess();
   
   /** Sets this vertex process flag to the specified value. The flag is designed 
    *  to be utilized as a temporary and dynamic attribute for algorithms.
    *  @param inProcess flag which marks this vertex
    */
   public void setInProcess(boolean inProcess);   
}

