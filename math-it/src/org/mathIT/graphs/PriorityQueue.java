/*
 * PriorityQueue.java - Class representing a priority queue
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

/**
 * This class represents a priority queue for an array list of vertices.
 * The priority queue is ordered as a minimum heap such that its root
 * is guaranteed to be the {@link Vertible vertex} with the minimum key.
 * Usually, the key is the distance to a given start vertex in a
 * {@link WeightedGraph weighted graph}.
 * The priority queue class is used in the
 * {@link WeightedGraph#dijkstra(int) Dijkstra algorithm}.
 * @see Vertible
 * @see WeightedGraph#dijkstra(int)
 * @author Andreas de Vries
 * @version 1.1
 * @param <V> the type of the vertices of the graph
 */
public class PriorityQueue<V extends Vertible<V>> {
   /** The priority queue of vertices as a heap array.
    *  Its entry heap[0] is the vertex nearest to the source.
    */
   V[] heap;
   int[] queueIndex;
   /** The current size of the heap.*/
   int size;

   /** Creates a priority queue containing the input vertices.
    *  @param vertices an array of vertices
    */
   public PriorityQueue(V[] vertices) {
      size = 0;
      // /*
      // Determine maximum index:
      int length = vertices.length;
      for (int i = 0; i < vertices.length; i++) {
         if (length < vertices[i].getIndex()) {
            length = vertices[i].getIndex();
         }
      }
      // */

      queueIndex = new int[length];
      //heap = java.util.Arrays.<V>copyOf(vertices,vertices.length); // ab Java 6
      heap = vertices.clone();
      for (V v : vertices) {
         insert(v);
      }
   }

   /** Returns the number of elements in this priority queue.
    *  @return the number of elements in this priority queue.
    */
   public int size() {
      return size;
   }

   /** Returns the root of the heap representing this priority queue.
    *  In fact, the root is the element with the minimum key.
    *  @return the root number of elements in this priority queue.
    */
   public V getRoot() {
      return heap[0];
   }

   /** Inserts vertex into this priority queue.
    *  @param vertex the vertex to be inserted
    */
   private void insert(V vertex) {
      int i = size; // start i from the bottom
      V tmpV;
      heap[size] = vertex; // insert object
      //heap[size].setQueueIndex(size);
      queueIndex[vertex.getIndex()] = size;
      size++; // extend the heap with one object
      while (i > 0 && heap[(i-1)/2].getDistance() > heap[i].getDistance() ) { // heap property violated?
         tmpV = heap[i]; heap[i] = heap[(i-1)/2]; heap[(i-1)/2] = tmpV;
         //heap[i].setQueueIndex(i); heap[(i-1)/2].setQueueIndex((i-1)/2);
         queueIndex[heap[i].getIndex()] = i; queueIndex[heap[(i-1)/2].getIndex()] = (i-1)/2;
         i = (i - 1)/2; // go up one generation
      }
//if(!isHeap()) System.out.println("### no heap (inserted): " + this);
  }

   /** Returns the minimum of this priority queue and deletes it from the queue.
    *  @return the minimum vertex of this priority queue
    */
   public V extractMin() { // deletes the minimum object of the heap
      V root = heap[0]; // store minimum to return in the end
      V tmpV;
      int i, min, l, r;
      size--; // decrease heap
      heap[0] = heap[size]; // minimum now deleted from heap and exchanged with the latest element
      //heap[0].setQueueIndex(0);
      queueIndex[heap[0].getIndex()] = 0;
      i = 0; // start i from the root
      // reheap:
      while (2*i + 1 < size) { // while there is at least a left child
         l = 2*i + 1; r = 2*(i + 1); // index of left and right child
         if (r < size) { // does right child exist at all?
            if (heap[l].getDistance() < heap[r].getDistance()) { // which child is less?
               min = l;
            } else {
               min = r;
            }
         } else {
            min = l;
         }
         if (heap[i].getDistance() > heap[min].getDistance() ) { // heap property violated?
            tmpV = heap[i]; heap[i] = heap[min]; heap[min] = tmpV;
            //heap[i].setQueueIndex(i); heap[min].setQueueIndex(min);
            queueIndex[heap[i].getIndex()] = i; queueIndex[heap[min].getIndex()] = min;
            i = min;
         } else {
            i = size + 1; // exit loop
         }
      }
//if(!isHeap()) {System.out.println("### extracted: " + this + ", size="+size); System.exit(0);}
     return root;
   }

   /** Decreases the key of the specified element <i>v</i> to the value <i>d</i>.
    *  The element remains unmodified if the new key is not less than the current
    *  key value.
    *  In the Dijkstra algorithm, the key is the current minimum distance of the
    *  vertex to a specified start vertex.
    *  @param v the vertex whose key is to be decreased
    *  @param d the new value of the key
    *  @return <code>true</code> if and only if the new value <i>d</i> is less
    *  than the current key value
    */
   public boolean decreaseKey(V v, double d) {
      if ( v.getDistance() > d ) {
         V tmp;
         int i = queueIndex[v.getIndex()]; // start i
         v.setDistance(d);
         while (i > 0 && heap[(i-1)/2].getDistance() > heap[i].getDistance()) { // heap property violated?
            tmp = heap[i]; heap[i] = heap[(i-1)/2]; heap[(i-1)/2] = tmp;
            //heap[(i-1)/2].setQueueIndex((i-1)/2); heap[i].setQueueIndex(i);
            queueIndex[heap[i].getIndex()] = i; queueIndex[heap[(i-1)/2].getIndex()] = (i-1)/2;
            i = (i - 1)/2; // go up one generation
         }
//if(!isHeap()) System.out.println("+++ no heap (decreaseKey): " + this + "\ni="+queueIndex[v.getIndex()]);
        return true;
      } else {
         return false;
      }
   }

   /** Returns a string representationj of this priority queue.
    *  @return a string representationj of this priority queue
    */
   @Override
   public String toString() {
      String output = "\n[";
      for ( int i = 0; i < size; i++ ) { //heap.length; i++ ) {
         output += "(" + heap[i].getIndex() + "," + heap[i].getDistance() +",[" + queueIndex[heap[i].getIndex()] + "]), ";
      }
      output += "(" + heap[size - 1] + "," + heap[size - 1] + ")]";
      return output;
   }

   /** Checks if this priority queue is actually a heap.
    *  @return <code>true</code> if and only if this priority queue is a minimum heap
    */
   boolean isHeap() {
      boolean istHeap = true;
      for(int i = 1; i < size && istHeap; i++) {
         istHeap = heap[(i-1)/2].getDistance() <= heap[i].getDistance();
      }
      return istHeap;
   }
}
