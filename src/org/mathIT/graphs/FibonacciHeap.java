/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (barak_naveh@users.sourceforge.net)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* --------------------------
 * FibonnaciHeap.java
 * --------------------------
 * (C) Copyright 1999-2003, by Nathan Fiedler and Contributors.
 *
 * Original Author:  Nathan Fiedler
 * Contributor(s):   John V. Sichi, Andreas de Vries
 *
 */
package org.mathIT.graphs;

import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Stack;


/**
 * This class implements a Fibonacci heap data structure. Much of the code in
 * this class is based on the algorithms in the "Introduction to Algorithms"by
 * Cormen, Leiserson, and Rivest in Chapter 21. The amortized running time of
 * most of these methods is O(1), making it a very fast data structure. Several
 * have an actual running time of O(1). <code>removeMin()</code> and 
 * <code>delete()</code> have O(log <i>n</i>)
 * amortized running times because they do the heap consolidation. If you
 * attempt to store nodes in this heap with key values of -Infinity
 * ({@link Double#NEGATIVE_INFINITY}) 
 * the <code>delete()</code> operation may fail to remove the correct element.
 *
 * <p><b>Note that this implementation is not synchronized.</b> If multiple
 * threads access a set concurrently, and at least one of the threads modifies
 * the set, it <i>must</i> be synchronized externally. This is typically
 * accomplished by synchronizing on some object that naturally encapsulates the
 * set.</p>
 *
 * <p>This class was originally developed by Nathan Fiedler for the GraphMaker
 * project.</p>
 *
 * @author Nathan Fiedler
 * @version 1.1
 * @param <V> the type of the vertices
 */
public class FibonacciHeap<V extends Vertible<V>> {
   //- Static fields/initializers ---------------------------------------------
   private static final double oneOverLogPhi = 1. / log((1. + sqrt(5)) / 2.);
   //- Instance fields --------------------------------------------------------

   /**
    * Points to the minimum node in the heap.
    */
   private FibonacciHeapNode<V> minNode;

   /**
    * Number of nodes in the heap.
    */
   private int nNodes;

   /**
    * Table mapping the data to its heap node
    */
   private java.util.HashMap<V,FibonacciHeapNode<V>> nodeTable;

   //- Constructors -----------------------------------------------------------

   /**
    * Constructs a FibonacciHeap object that contains no elements.
    */
   public FibonacciHeap() {
      nodeTable = new java.util.HashMap<>();
   } // FibonacciHeap

   /**
    * Constructs a FibonacciHeap object containing the elements of the specified array.
    * @param vertices an array of vertices this heap shall consist of 
    */
   public FibonacciHeap(V[] vertices) {
      nodeTable = new java.util.HashMap<>(vertices.length);
      for (V v : vertices) {
         insert(v);
      }
   } // FibonacciHeap

   //- Methods ----------------------------------------------------------------

   /**
    * Tests if the Fibonacci heap is empty or not. Returns true if the heap is
    * empty, false otherwise.
    *
    * <p>Running time: O(1) actual</p>
    *
    * @return true if the heap is empty, false otherwise
    */
   public boolean isEmpty() {
      return minNode == null;
   }

   // isEmpty

   /**
    * Removes all elements from this heap.
    */
   public void clear() {
      minNode = null;
      nNodes = 0;
   }

   // clear

   /**
    * Decreases the key value for a heap node, given the new value to take on.
    * The structure of the heap may be changed and will not be consolidated.
    *
    * <p>Running time: O(1) amortized</p>
    *
    * @param x node to decrease the key of
    * @param k new key value for node x
    *
    * @exception IllegalArgumentException Thrown if k is larger than x.key
    * value.
    */
   public void decreaseKey(V x, double k) {
      FibonacciHeapNode<V> node = nodeTable.get(x);
      decreaseKey(node, k);
      x.setDistance(k);
   }

   /**
    * Decreases the key value for a heap node, given the new value to take on.
    * The structure of the heap may be changed and will not be consolidated.
    *
    * <p>Running time: O(1) amortized</p>
    *
    * @param x node to decrease the key of
    * @param key new key value for node x
    *
    * @exception IllegalArgumentException Thrown if k is larger than x.key
    * value.
    */
   private void decreaseKey(FibonacciHeapNode<V> x, double key) {
      if (key > x.key) {
         throw new IllegalArgumentException(
            "decreaseKey() got larger key value");
      }

      x.key = key;

      FibonacciHeapNode<V> y = x.parent;

      if ((y != null) && (x.key < y.key)) {
         cut(x, y);
         cascadingCut(y);
      }

      if (x.key < minNode.key) {
         minNode = x;
      }
   }

   // decreaseKey

   /**
    * Deletes a node from the heap given the reference to the node. The trees
    * in the heap will be consolidated, if necessary. This operation may fail
    * to remove the correct element if there are nodes with key value
    * -Infinity.
    *
    * <p>Running time: O(log n) amortized</p>
    *
    * @param x node to remove from heap
    */
   //public void delete(FibonacciHeapNode<V> x) {
   public void delete(V x) {
      // make x as small as possible
      decreaseKey(x, Double.NEGATIVE_INFINITY);

      // remove the smallest, which decreases n also
      extractMin();
   }

   // delete

   /**
    * Inserts a new data element into the heap. No heap consolidation is
    * performed at this time, the new node is simply inserted into the root
    * list of this heap.
    *
    * <p>Running time: O(1) actual</p>
    *
    * @param x data element to be inserted into this heap
    */
   public void insert(V x) {
      FibonacciHeapNode<V> node = new FibonacciHeapNode<>(x,x.getDistance());
      nodeTable.put(x, node);
      insert(node);
   }
   /**
    * Inserts a new data element into the heap. No heap consolidation is
    * performed at this time, the new node is simply inserted into the root
    * list of this heap.
    *
    * <p>Running time: O(1) actual</p>
    *
    * @param node new node to insert into heap
    */
   private void insert(FibonacciHeapNode<V> node) {
      // concatenate node into min list
      if (minNode != null) {
         node.left = minNode;
         node.right = minNode.right;
         minNode.right = node;
         node.right.left = node;

         if (node.key < minNode.key) {
            minNode = node;
         }
      } else {
         minNode = node;
      }
      nNodes++;
   }

   // insert

   /**
    * Returns the smallest element in the heap. This smallest element is the
    * one with the minimum key value.
    *
    * <p>Running time: O(1) actual</p>
    *
    * @return heap node with the smallest key
    */
   //private FibonacciHeapNode<V> min() {
   //   return minNode;
   //}

   // min

   /**
    * Removes the smallest element from the heap. This will cause the trees in
    * the heap to be consolidated, if necessary.
    *
    * <p>Running time: O(log n) amortized</p>
    *
    * @return data of the node with the smallest key
    */
   public V extractMin() {
      FibonacciHeapNode<V> z = minNode;

      if (z != null) {
         int numKids = z.degree;
         FibonacciHeapNode<V> x = z.child;
         FibonacciHeapNode<V> tempRight;

         // for each child of z do...
         while (numKids > 0) {
            tempRight = x.right;
            // remove x from child list
            x.left.right = x.right;
            x.right.left = x.left;
            // add x to root list of heap
            x.left = minNode;
            x.right = minNode.right;
            minNode.right = x;
            x.right.left = x;

            // set parent[x] to null
            x.parent = null;
            x = tempRight;
            numKids--;
         }
         // remove z from root list of heap
         z.left.right = z.right;
         z.right.left = z.left;

         if (z == z.right) {
            minNode = null;
         } else {
            minNode = z.right;
            consolidate();
         }
         // decrement size of heap
         nNodes--;
      }
      //return z;
      return z.getData();
   }

   // removeMin

   /**
    * Returns the size of the heap which is measured in the number of elements
    * contained in the heap.
    *
    * <p>Running time: O(1) actual</p>
    *
    * @return number of elements in the heap
    */
   public int size() {
      return nNodes;
   }

   // size

   /**
    * Creates a String representation of this Fibonacci heap.
    *
    * @return String of this.
    */
   @Override
   public String toString() {
      if (minNode == null) {
         return "FibonacciHeap=[]";
      }

      // create a new stack and put root on it
      Stack<FibonacciHeapNode<V>> stack = new Stack<>();
      stack.push(minNode);

      StringBuilder buf = new StringBuilder(512);
      buf.append("FibonacciHeap=[");

      // do a simple breadth-first traversal on the tree
      while (!stack.empty()) {
         FibonacciHeapNode<V> curr = stack.pop();
         buf.append(curr);
         buf.append(", ");

         if (curr.child != null) {
            stack.push(curr.child);
         }

         FibonacciHeapNode<V> start = curr;
         curr = curr.right;

         while (curr != start) {
            buf.append(curr);
            buf.append(", ");

            if (curr.child != null) {
               stack.push(curr.child);
            }

            curr = curr.right;
         }
      }

      buf.append(']');

      return buf.toString();
   }

   // toString

   /**
    * Performs a cascading cut operation. This cuts y from its parent and then
    * does the same for its parent, and so on up the tree.
    *
    * <p>Running time: O(log n); O(1) excluding the recursion</p>
    *
    * @param y node to perform cascading cut on
    */
   void cascadingCut(FibonacciHeapNode<V> y) {
      FibonacciHeapNode<V> z = y.parent;

      // if there's a parent...
      if (z != null) {
         // if y is unmarked, set it marked
         if (!y.mark) {
            y.mark = true;
         } else {
            // it's marked, cut it from parent
            cut(y, z);

            // cut its parent as well
            cascadingCut(z);
         }
      }
   }

   /**
    * Consolidates this Fibonacci heap by cascading cut.
    */
   protected void consolidate() {
      int size = ((int) floor(log(nNodes) * oneOverLogPhi)) + 1;

      ArrayList<FibonacciHeapNode<V>> array = new ArrayList<>(size);

      // Initialize degree array
      for (int i = 0; i < size; i++) {
         array.add(null);
      }

      // Find the number of root nodes.
      int numRoots = 0;
      FibonacciHeapNode<V> x = minNode;

      if (x != null) {
         numRoots++;
         x = x.right;

         while (x != minNode) {
            numRoots++;
            x = x.right;
         }
      }

      // For each node in root list do...
      while (numRoots > 0) {
         // Access this node's degree..
         int d = x.degree;
         FibonacciHeapNode<V> next = x.right;

         // ..and see if there's another of the same degree.
         for (;;) {
            FibonacciHeapNode<V> y = array.get(d);
            if (y == null) {
               // Nope.
               break;
            }

            // There is, make one of the nodes a child of the other.
            // Do this based on the key value.
            if (x.key > y.key) {
               FibonacciHeapNode<V> temp = y;
               y = x;
               x = temp;
            }

            // FibonacciHeapNode<V> y disappears from root list.
            link(y, x);

            // We've handled this degree, go to next one.
            array.set(d, null);
            d++;
         }

         // Save this node for later when we might encounter another
         // of the same degree.
         array.set(d, x);

         // Move forward through list.
         x = next;
         numRoots--;
      }

      // Set min to null (effectively losing the root list) and
      // reconstruct the root list from the array entries in array[].
      minNode = null;

      for (int i = 0; i < size; i++) {
         FibonacciHeapNode<V> y = array.get(i);
         if (y == null) {
            continue;
         }

         // We've got a live one, add it to root list.
         if (minNode != null) {
            // First remove node from root list.
            y.left.right = y.right;
            y.right.left = y.left;

            // Now add to root list, again.
            y.left = minNode;
            y.right = minNode.right;
            minNode.right = y;
            y.right.left = y;

            // Check if this is a new min.
            if (y.key < minNode.key) {
               minNode = y;
            }
         } else {
            minNode = y;
         }
      }
   }

   // consolidate

   /**
    * The reverse of the link operation: removes x from the child list of y.
    * This method assumes that min is non-null.
    *
    * <p>Running time: O(1)</p>
    *
    * @param x child of y to be removed from y's child list
    * @param y parent of x about to lose a child
    */
   void cut(FibonacciHeapNode<V> x, FibonacciHeapNode<V> y) {
      // remove x from childlist of y and decrement degree[y]
      x.left.right = x.right;
      x.right.left = x.left;
      y.degree--;

      // reset y.child if necessary
      if (y.child == x) {
         y.child = x.right;
      }

      if (y.degree == 0) {
         y.child = null;
      }

      // add x to root list of heap
      x.left = minNode;
      x.right = minNode.right;
      minNode.right = x;
      x.right.left = x;

      // set parent[x] to nil
      x.parent = null;

      // set mark[x] to false
      x.mark = false;
   }

   // cut

   /**
    * Make node y a child of node x.
    *
    * <p>Running time: O(1) actual</p>
    *
    * @param y node to become child
    * @param x node to become parent
    */
   void link(FibonacciHeapNode<V> y, FibonacciHeapNode<V> x) {
      // remove y from root list of heap
      y.left.right = y.right;
      y.right.left = y.left;

      // make y a child of x
      y.parent = x;

      if (x.child == null) {
         x.child = y;
         y.right = y;
         y.left = y;
      } else {
         y.left = x.child;
         y.right = x.child.right;
         x.child.right = y;
         y.right.left = y;
      }

      // increase degree[x]
      x.degree++;

      // set mark[y] false
      y.mark = false;
   }
   
   // link
   /**
    * Joins two Fibonacci heaps into a new one. No heap consolidation is
    * performed at this time. The two root lists are simply joined together.
    *
    * <p>Running time: O(1) actual</p>
    *
    * @param <V> class implementing {@link Vertible} and representing the vertices
    * @param h1 first heap
    * @param h2 second heap
    *
    * @return new heap containing h1 and h2
    */
   public static <V extends Vertible<V>> FibonacciHeap<V> union(
           FibonacciHeap<V> h1, FibonacciHeap<V> h2
   ) {
      FibonacciHeap<V> h = new FibonacciHeap<>();

      if ((h1 != null) && (h2 != null)) {
         h.minNode = h1.minNode;

         if (h.minNode != null) {
            if (h2.minNode != null) {
               h.minNode.right.left = h2.minNode.left;
               h2.minNode.left.right = h.minNode.right;
               h.minNode.right = h2.minNode;
               h2.minNode.left = h.minNode;

               if (h2.minNode.key < h1.minNode.key) {
                  h.minNode = h2.minNode;
               }
            }
         } else {
            h.minNode = h2.minNode;
         }
         h.nNodes = h1.nNodes + h2.nNodes;
      }
      return h;
   }
   // union
}
// FibonacciHeap


/* --------------------------
 * FibonnaciHeapNode.java
 * --------------------------
 * (C) Copyright 1999-2008, by Nathan Fiedler and Contributors.
 *
 * Original Author:  Nathan Fiedler
 * Contributor(s):   John V. Sichi
 */
/**
 * Implements a node of the Fibonacci heap. It holds the information necessary
 * for maintaining the structure of the heap. It also holds the reference to the
 * key value (which is used to determine the heap structure).
 *
 * @author Nathan Fiedler
 */
class FibonacciHeapNode<V> {
   //- Instance fields --------------------------------------------------------

   /**
    * Node data.
    */
   V data;

   /**
    * first child node
    */
   FibonacciHeapNode<V> child;

   /**
    * left sibling node
    */
   FibonacciHeapNode<V> left;

   /**
    * parent node
    */
   FibonacciHeapNode<V> parent;

   /**
    * right sibling node
    */
   FibonacciHeapNode<V> right;

   /**
    * true if this node has had a child removed since this node was added to
    * its parent
    */
   boolean mark;

   /**
    * key value for this node
    */
   double key;

   /**
    * number of children of this node (does not count grandchildren)
    */
   int degree;

   //- Constructors -----------------------------------------------------------

   /**
    * Default constructor. Initializes the right and left pointers, making this
    * a circular doubly-linked list.
    *
    * @param data data for this node
    * @param key initial key for node
    */
   public FibonacciHeapNode(V data, double key) {
      this.data = data;
      this.key = key;
      //completeConstructor();
      right = this;
      left = this;      
   }

   //- Methods ----------------------------------------------------------------

   //private void completeConstructor() {
   //   right = this;
   //   left = this;      
   //}
   
   /**
    * Obtain the key for this node.
    *
    * @return the key
    */
   public final double getKey() {
      return key;
   }

   /**
    * Returns the data for this node.
    * @return the data for this node.
    */
   public final V getData() {
      return data;
   }

   /**
    * Return the string representation of this object.
    * @return string representing this object
    */
   @Override
   public String toString() {
      //if (true) {
      //   return Double.toString(key);
      //} else {
         StringBuilder buf = new StringBuilder();
         buf.append("Node=[parent = ");

         if (parent != null) {
            buf.append(Double.toString(parent.key));
         } else {
            buf.append("---");
         }

         buf.append(", key = ");
         buf.append(Double.toString(key));
         buf.append(", degree = ");
         buf.append(Integer.toString(degree));
         buf.append(", right = ");

         if (right != null) {
            buf.append(Double.toString(right.key));
         } else {
            buf.append("---");
         }

         buf.append(", left = ");

         if (left != null) {
            buf.append(Double.toString(left.key));
         } else {
            buf.append("---");
         }

         buf.append(", child = ");

         if (child != null) {
            buf.append(Double.toString(child.key));
         } else {
            buf.append("---");
         }

         buf.append(']');

         return buf.toString();
      //} // if (true) ...
   } // toString
}
