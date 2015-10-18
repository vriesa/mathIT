/*
 * Edge.java - Class representing a graph
 *
 * Copyleft (C) 2013 Andreas de Vries
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

/**
 * Objects of this class represent edges of general graphs.
 * Although an edge of this class does not play the fundamental role internally 
 * in the math IT graph framework (edges are represented primarily on the 
 * adjacency matrix matrix of the {@link Graph Graph class}), it is needed as
 * basis for some additional tasks, e.g., graph visualization.
 * @author Andreas de Vries
 * @version 1.0
 * @param <V> Vertex type
 */
public class Edge<V extends Vertible<V>> {
   /** The label of thius graph.*/
   protected String label;
   /** The start vertex of this edge.*/
   protected V start;
   /** The end vertex of this edge.*/
   protected V end;
   /** Flag indicating whether this edge is directed.*/
   protected boolean directed;
   /** Flag indicating whether this edge is directed.*/
   protected boolean weighted;
   /** The weight of this edge. It is zero by default.*/
   protected double weight;

   /**
    * Creates a directed edge with the specified vertices.
    * The label is specified as the string 
    * "(<i>v</i><sub>s</sub>,<i>v</i><sub>e</sub>)" where
    * <i>v</i><sub>s</sub> denotes the start vertex and <i>v</i><sub>e</sub>
    * the end vertex.
    * @param startVertex the start vertex of this edge
    * @param endVertex the end vertex of this edge
    */
   public Edge(V startVertex, V endVertex) {
      this.label = "(" + startVertex.toString() + "," + endVertex.toString() + ")";
      this.start = startVertex;
      this.end   = endVertex;
      this.directed = true;
      this.weighted = false;
      this.weight = 0;
   }

   /**
    * Creates an edge with the specified properties.
    * The label is specified as the string 
    * "(<i>v</i>, <i>w</i>)" if the edge is directed,
    * and as the string 
    * "{<i>v</i>, <i>w</i>}" if the edge is undirected,
    * 
    * @param v the start vertex of this edge
    * @param w the end vertex of this edge 
    * @param directed flag indicating whether this edge is directed
    */
   public Edge(V v, V w, boolean directed) {
      if (directed) {
         this.label = "(" + v.toString() + "," + w.toString() + ")";         
      } else {
         this.label = "{" + v.toString() + "," + w.toString() + "}";         
      }
      this.start = v;
      this.end   = w;
      this.directed = directed;
      this.weighted = false;
      this.weight = 0;
   }

   /**
    * 
    * @param label the label of this edge
    * @param startVertex the start vertex of this edge
    * @param endVertex the end vertex of this edge
    */
   public Edge(String label, V startVertex, V endVertex) {
      this.label = label;
      this.start = startVertex;
      this.end   = endVertex;
      this.directed = true;
      this.weighted = false;
      this.weight = 0;
   }

   /**
    * 
    * @param label the label of this edge
    * @param startVertex the start vertex of this edge
    * @param endVertex the end vertex of this edge
    * @param directed flag indicating whether this edge is directed
    */
   public Edge(String label, V startVertex, V endVertex, boolean directed) {
      this.label = label;
      this.start = startVertex;
      this.end   = endVertex;
      this.directed = directed;
      this.weighted = false;
      this.weight = 0;
   }

   /**
    * Creates a weighted edge with the specified properties.
    * The label is set to the weight.
    * 
    * @param startVertex the start vertex of this edge
    * @param endVertex the end vertex of this edge
    * @param directed flag indicating whether this edge is directed
    * @param weight the weight of this edge
    */
   public Edge(V startVertex, V endVertex, boolean directed, double weight) {
      this.label = WeightedGraph.DF.format(weight);
      this.start = startVertex;
      this.end = endVertex;
      this.directed = directed;
      this.weighted = true;
      this.weight = weight;
   }
   
   /**
    * 
    * @param label the label of this edge
    * @param startVertex the start vertex of this edge
    * @param endVertex the end vertex of this edge
    * @param directed flag indicating whether this edge is directed
    * @param weight the weight of this edge
    */
   public Edge(String label, V startVertex, V endVertex, boolean directed, double weight) {
      this.label = label;
      this.start = startVertex;
      this.end = endVertex;
      this.directed = directed;
      this.weighted = true;
      this.weight = weight;
   }   

   /**
    * Returns the label of this edge.
    * @return the label of this edge
    */
   /*
   public String getLabel() {
      return label;
   }
   // */

   /**
    * Returns the start vertex of this edge.
    * @return the start vertex of this edge
    */
   public V getStartVertex() {
      return start;
   }

   /**
    * Returns the end vertex of this edge.
    * @return the end vertex of this edge
    */
   public V getEndVertex() {
      return end;
   }

   /**
    * Returns true if and only if this edge is directed.
    * @return true if and only if this edge is directed
    */
   public boolean isDirected() {
      return directed;
   }

   /**
    * Returns true if and only if this edge is weighted.
    * @return true if and only if this edge is weighted
    */
   public boolean isWeighted() {
      return weighted;
   }

   /**
    * Returns the weight of this edge.
    * @return the weight of this edge
    * @throws IllegalArgumentException if this method is called in case of an unweighted edge
    */
   public double getWeight() throws IllegalArgumentException {
      if (!weighted) throw new IllegalArgumentException("This edge is unweighted and therefore has no weight");
      return weight;
   }

   /**
    * Returns the label of this edge, or the weight if the edge is weighted.
    * @return the label of this edge or the weight if the edge is weighted
    */
   @Override
   public String toString() {
      return label;
   }
}
