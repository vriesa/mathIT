/*
 * MathSet.java
 *
 * Copyright (C) 2013 Andreas de Vries
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
package org.mathIT.algebra;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class enables to create finite mathematical sets and supplies some of the
 * usual mathematical set operations such as immutable copy, set difference,
 * union, or intersection.
 * The class extends the {@link java.util.HashSet} class and is backed 
 * by a hash table (actually a {@link java.util.HashMap} instance). 
 * It makes no guarantees as to the iteration order of the set; in particular, 
 * it does not guarantee that the order will remain constant over time. 
 * This class permits the null element.
 * This class offers constant time performance for the basic operations 
 * (add, remove, contains and size), assuming the hash function disperses the 
 * elements properly among the buckets. Iterating over this set requires time 
 * proportional to the sum of the HashSet instance's size (the number of elements) 
 * plus the "capacity" of the backing HashMap instance (the number of buckets). 
 * Thus, it is very important not to set the initial capacity too high 
 * (or the load factor too low) if iteration performance is important.
 * For further technical details see {@link java.util.HashSet}.
 * @see EnumSet
 * @see OrderedSet
 * @see Set
 * @see java.util.HashSet
 * @author Andreas de Vries
 * @version 1.2
 * @param <E> the type of the elements of this set
 */
public class MathSet<E> extends java.util.HashSet<E> {
   private static final long serialVersionUID = 9223372035065452569L; // = "MathSet".hashCode() + Long.MAX_VALUE
   /** Constructs a new, empty set; the backing HashMap instance has default 
    *  initial capacity (16) and load factor (0.75).
    */
   public MathSet() {
      super();
   }
   
   /** Constructs a new set containing the elements in the specified collection.
    *  @param c a collection
    */
   public MathSet(java.util.Collection<? extends E> c) {
      super(c);
   }
   
   /** Constructs a new, empty set; the backing HashMap instance has the 
    *  specified initial capacity and default load factor (0.75).
    *  @param initialCapacity the initial capacity of the hash map
    *  @throws IllegalArgumentException - if the initial capacity is less than zero
    */
   public MathSet(int initialCapacity) {
      super(initialCapacity);
   }
   
   /** Constructs a new, empty set; the backing HashMap instance has the 
    *  specified initial capacity and the specified load factor.
    *  @param initialCapacity the initial capacity of the hash map
    *  @param loadFactor - the load factor of the backing hash map
    *  @throws IllegalArgumentException if the initial capacity is less than zero, 
    *    or if the load factor is nonpositive
    */
   public MathSet(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   /** Constructs a new set from the input array. Note that the set does not
    *  contain duplicate elements, any element equal to another one
    *  preceding it is ignored, according to the contract of a
    *  {@link java.util.Set}.
    *  The backing HashMap instance has the 
    *  initial capacity of the array size and the default load factor (0.75).
    *  Note that for elements of primitive type you must invoke the wrapper class
    *  objects, e.g., <code>MathSet&lt;Integer&gt; s = new MathSet&lt;&gt;(new Integer[]{1,2,3});</code>. 
    *  @param elements array containing the elements
    */
   public MathSet(E[] elements) {
      super(elements.length);
      this.addAll(Arrays.asList(elements));
   }
   
   /** Constructs a new set from the input element. The resulting set accordingly
    *  is a one-element set or a singleton.
    *  The backing HashMap instance has the 
    *  initial capacity of the array size and the default load factor (0.75).
    *  Note that for elements of primitive type you must invoke the wrapper class
    *  objects, e.g., <code>MathSet&lt;Integer&gt; s = new MathSet&lt;&gt;(new Integer(2));</code>. 
    *  @param element an element
    */
   public MathSet(E element) {
      super();
      this.add(element);
   }
   
   /** Creates and returns a clone copy of this set.
    *  I.e., the returned set object returned by this method is independent of this object.
    *  @return a cloned copy of this set
    */
   public MathSet<E> copy() {
      MathSet<E> set = new MathSet<>(this.size());
      for (E x : this) {
         set.add(x);
      }
      return set;
   }
   
   /** Returns the set difference of this set minus the specified minuend.
    *  The method does not change this set.
    *  @param minuend the set to be subtracted from this set
    *  @return the set this - minuend
    */
   public MathSet<E> minus(java.util.Set<E> minuend) {
      MathSet<E> s = new MathSet<>(this.size());
      for (E x : this) {
         s.add(x);
      }
      s.removeAll(minuend);
      return s;
   }

   /** Returns the set difference of this set minus the singleton generated by the 
    *  specified element.
    *  The method does not change this set.
    *  @param element specifying the singleton set to be subtracted from this set
    *  @return the set this - {element}
    */
   public MathSet<E> minus(E element) {
      if (size() == 0 || (size() == 1 && contains(element) ) ) return emptySet();
      MathSet<E> s = new MathSet<>(this.size());
      for (E x : this) {
         if (!x.equals(element)) {
               s.add(x);
         }
      }
      return s;
   }
   
   /** Returns the intersection of this set and the specified set.
    *  The specified set is expected to contain elements of
    *  class <code>E</code> or a subclass (or implementing class) of <code>E</code>.
    *  @param set a set
    *  @return the intersection of this set and the input set
    */
   public MathSet<E> intersect(java.util.Set<? extends E> set) {
      MathSet<E> s = new MathSet<>(this.size());
      for (E x : this) {
         if (set.contains(x)) {
            s.add(x);
         }
      }
      if (s.size() == 0) s = emptySet();
      return s;
   }
       
   /** Returns the intersection of this set and the specified set list.
    *  The specified list is expected to contain set objects of a subclass
    *  of <code>MathSet</code> or being of <code>MathSet</code> itself, 
    *  and each set is expected to contain elements of
    *  class <code>E</code> or a subclass (or implementing class) of <code>E</code>.
    *  @param sets a list of sets
    *  @return the intersection of this set and all input sets
    */
   public MathSet<E> intersect(ArrayList<? extends java.util.Set<? extends E>> sets) {
      MathSet<E> s = new MathSet<>(this.size());
      boolean contained;
      for (E x : this) {
         contained = true;
         for (int i = 0; i < sets.size() && contained; i++) {
            contained = sets.get(i).contains(x);
         }
         if (contained) {
            s.add(x);
         }
      }
      if (s.size() == 0) s = emptySet();
      return s;
   }
       
   /** Returns the union of this set and the specified set.
    *  The specified set is expected to contain elements of
    *  class <code>E</code> or a subclass (or implementing class) of <code>E</code>.
    *  @param set a set
    *  @return the union of this set and the input set
    */
   public MathSet<E> unify(java.util.Set<? extends E> set) {
      MathSet<E> s = new MathSet<>(this.size());
      for (E x : this) {
         s.add(x);
      }
      for (E x : set) {
         s.add(x);
      }
      if (s.size() == 0) s = emptySet();
      return s;
   }
       
   /** Returns the union of this set and the specified set list.
    *  The specified list is expected to contain set objects of a subclass
    *  of <code>MathSet</code> or being of <code>MathSet</code> itself, 
    *  and each set is expected to contain elements of
    *  class <code>E</code> or a subclass (or implementing class) of <code>E</code>.
    *  @param sets a list of sets
    *  @return the union of this set and all input sets
    */
   public MathSet<E> unify(ArrayList<? extends java.util.Set<? extends E>> sets) {
      MathSet<E> s = new MathSet<>(this.size());
      for (E x : this) {
         s.add(x);
      }
      for (int i = 0; i < sets.size(); i++) {
         for (E x : sets.get(i)) {
            s.add(x);
         }
      }
      if (s.size() == 0) s = emptySet();
      return s;
   }
       
   /** Returns the <i>k</i>-element subsets of this set,
    *  i.e., each of its <i>k</i>-element combination, stored in an array list.
    *  Especially, the array contains only the empty set if <i>k</i>=0, and
    *  only this set <i>s</i> if <i>k</i> = <i>n</i> where <i>n</i> is the 
    *  size of this set. If <i>k</i> &gt; <i>n</i>, the array list is empty.
    *  Also see {@link org.mathIT.numbers.Combinatorics#subsets(java.util.SortedSet,int)}
    *  for sorted sets.
    *  @param k an integer
    *  @return a list of all <i>k</i>-element subsets of the set <i>s</i>
    *  @see org.mathIT.numbers.Combinatorics#subsets(java.util.SortedSet, int)
    */
   public ArrayList<MathSet<E>> subsets(int k) {
      return subsets(this,k);
   }
   
   /**
    * Returns a list of partitions of this set.
    * A partition of a set is a collection of disjoint subsets whose union equals
    * the set. For instance, the set <i>S</i> = {<i>a</i>, <i>b</i>, <i>c</i>}
    * has the five partitions
    * <p style="text-align:center">
    * {{<i>a</i>}, {<i>b</i>}, {<i>c</i>}},
    * </p>
    * <p style="text-align:center">
    * {{<i>a</i>, <i>b</i>}, {<i>c</i>}}, &nbsp;
    * {{<i>a</i>, <i>c</i>}, {<i>b</i>}}, &nbsp;
    * {{<i>a</i>}, {<i>c</i>, <i>b</i>}},
    * </p>
    * <p style="text-align:center">
    * {{<i>a</i>, <i>b</i>, <i>c</i>}}.
    * </p>
    * The running time of this algorithm with respect to the size <i>n</i> of 
    * the set is very bad, its time complexity is estimated as 
    * <i>O</i>(<i>n<sup>n</sup></i>). For <i>n</i> &le; 10 it requires less than
    * 2 sec on a 2 GHz dual core processor, but for <i>n</i> &le; 11 the running time
    * is about 10 seconds and explodes for greater <i>n</i>.
    * The number of partitions of a set of <i>n</i> is given by the <i>n</i>-th
    * Bell number <i>B<sub>n</sub></i>, see
    * <a href="http://oeis.org/A000110" target="_blank">http://oeis.org/A000110</a>.
    * @return a list of all partitions of this set.
    */
   public ArrayList<MathSet<MathSet<E>>> partitions() {
      return partitions(this);
   }
   
   // ---- static methods: -----------------------------------------------------
   /** Returns the empty set. The type-safe way to obtain an empty set using 
    *  this method is illustrated by the following example:
    *  <pre>
    *     MathSet&lt;String&gt; s = MathSet.emptySet();
    *  </pre>
    *  Implementation note: Implementations of this method need not create a 
    *  separate MathSet object for each call.
    *  If an explicit variable for the empty set is not desired, the method
    *  {@link #emptySet(MathSet) emptySet(MathSet&lt;E&gt;)} may be used.
    *  @param <E> type of the elements of this set
    *  @return the empty set
    *  @see #emptySet(MathSet)
    *  @see java.util.Collections#emptySet()
    */
   public static <E> MathSet<E> emptySet() {
      return new MathSet<>();
   }
   
   /** Returns the empty set. The type-safe way to obtain an empty set using 
    *  this method is illustrated by the following example call:
    *  <pre>
    *     MathSet.emptySet(new MathSet&lt;String&gt;());
    *  </pre>
    *  This method is appropriate if an explicit variable for the empty set is 
    *  not desired.
    *  @param <E> type of the elements of this set
    *  @param set specifies the element types of this empty set
    *  @return the empty set
    *  @see #emptySet()
    */
   public static <E> MathSet<E> emptySet(MathSet<E> set) {
      return new MathSet<>();
   }
   
   /** Returns the <i>k</i>-element subsets of a set <i>s</i>,
    *  i.e., each <i>k</i>-element combination of <i>s</i>,
    *  stored in an array list.
    *  Especially, the array contains only the empty set if <i>k</i>=0, and
    *  only the entire set <i>s</i> if <i>k</i> = <i>n</i> where <i>n</i> is the 
    *  size of <i>s</i>. If <i>k</i> &gt; <i>n</i>, the array list is empty.
    *  @param <E> type of the elements of this set
    *  @param set a set
    *  @param k an integer
    *  @return a list of all <i>k</i>-element subsets of the set <i>s</i>
    */
   public static <E> ArrayList<MathSet<E>> subsets(java.util.Set<E> set, int k) {
      ArrayList<MathSet<E>> subsets = new ArrayList<>();
      if (k == 0) {
         subsets.add(emptySet(new MathSet<E>()));
         return subsets;
      }

      MathSet<E> tmpS;
      ArrayList<E> s = new ArrayList<>(set);
      if (k == 1) {
         for (E i : s) {
            tmpS = new MathSet<>();
            tmpS.add(i);
            subsets.add(tmpS);
         }
      } else if ( k > 1 && k <= s.size() ) {
         ArrayList<MathSet<E>> tmp;
         for (int i = 0; i < s.size(); i++) {
            tmpS = new MathSet<>(); // size: Größe: n über k
            for (int j = i+1; j < s.size(); j++) {
               tmpS.add(s.get(j));
            }
            tmp = subsets(tmpS, k-1);
            for (int j = 0; j < tmp.size(); j++) {
               tmpS = tmp.get(j);
               //if (!tmpS.equals(emptySet(new MathSet<E>())))
               {
                  tmpS.add(s.get(i));
                  subsets.add(tmpS);
               }
            }  // for j
         } // for x
      }
      return subsets;
   }
   
   /**
    * Returns a list of partitions of the specified set.
    * A partition of a set is a collection of disjoint subsets whose union equals
    * the set. For instance, the set <i>S</i> = {<i>a</i>, <i>b</i>, <i>c</i>}
    * has the five partitions
    * <p style="text-align:center">
    * {{<i>a</i>}, {<i>b</i>}, {<i>c</i>}},
    * </p>
    * <p style="text-align:center">
    * {{<i>a</i>, <i>b</i>}, {<i>c</i>}}, &nbsp;
    * {{<i>a</i>, <i>c</i>}, {<i>b</i>}}, &nbsp;
    * {{<i>a</i>}, {<i>c</i>, <i>b</i>}},
    * </p>
    * <p style="text-align:center">
    * {{<i>a</i>, <i>b</i>, <i>c</i>}}.
    * </p>
    * The running time of this algorithm with respect to the size <i>n</i> of 
    * the set is very bad, its time complexity is estimated as 
    * <i>O</i>(<i>n<sup>n</sup></i>). For <i>n</i> &le; 10 it requires less than
    * 2 sec on a 2 GHz dual core processor, but for <i>n</i> &le; 12 the running time
    * is about 10 seconds and explodes for greater <i>n</i>.
    * The number of partitions of a set of <i>n</i> is given by the <i>n</i>-th
    * Bell number <i>B<sub>n</sub></i>, see
    * <a href="http://oeis.org/A000110" target="_blank">http://oeis.org/A000110</a>.
    * @param <E> the type of the elements of the set
    * @param set a set
    * @return a list of all partitions of the set.
    */
   @SuppressWarnings("unchecked")
   public static <E> ArrayList<MathSet<MathSet<E>>> partitions(MathSet<E> set) {
      ArrayList<MathSet<MathSet<E>>> partitions = new ArrayList<>();
      /* // --- Requires too much space to compute the partitions for |set| = 13 ---
      MathSet<MathSet<E>> partition; // the partitions
      if (set.size() <= 1) {
         partition.add(set);
         partitions.add(partition);
      } else {
         ArrayList<MathSet<MathSet<E>>> tmpPartitions;
         MathSet<E> s;
         MathSet<E> x = new MathSet<>(1);
         for (E e : set) {
            x.add(e); break;
         }
         s = set.minus(x);
         tmpPartitions = partitions(s);
         for (int i = 0; i < tmpPartitions.size(); i++) { // walk through the parts
            partition_i = tmpPartitions.get(i);
            partition = new MathSet<>();
            partition.add(x);
            for (MathSet<E> p : partition_i) {
               partition.add(p);
            }
            partitions.add(partition);
            for (MathSet<E> p : partition_i) {
               partition = partition_i.copy();
               partition.remove(p);
               p = p.unify(x);
               partition.add(p);
               partitions.add(partition);
            }
         }
      }
      // */
      // --- More space-efficient solution: ---
      ArrayList<MathSet<E>> partition;
      Object[] s = set.toArray();
      ArrayList<int[]> pList = partitions(set.toArray());
      int n, i;
      for (int[] p : pList) {
         partition = new ArrayList<>();
         n = max(p);
         for (i = 0; i <= n; i++) {
            partition.add(new MathSet<E>());
         }
         for (i = 0; i < set.size(); i++) {
            partition.get(p[i]).add((E) s[i]);
         }
         partitions.add(new MathSet<>(partition));
      }
      return partitions;
   }
   
   /**
    * Returns a list of partitions of the specified set,
    * each partition encoded as an array <code>partition[]</code> meaning that 
    * set element <code>i</code> is in subset <code>partition[i]</code>.
    * In general, a partition of a set is a collection of disjoint subsets whose union equals
    * the set. For instance, the set <i>S</i> = {<i>a</i>, <i>b</i>, <i>c</i>}
    * has the five partitions
    * <p style="text-align:center">
    * {{<i>a</i>}, {<i>b</i>}, {<i>c</i>}},
    * </p>
    * <p style="text-align:center">
    * {{<i>a</i>, <i>b</i>}, {<i>c</i>}}, &nbsp;
    * {{<i>a</i>, <i>c</i>}, {<i>b</i>}}, &nbsp;
    * {{<i>a</i>}, {<i>c</i>, <i>b</i>}},
    * </p>
    * <p style="text-align:center">
    * {{<i>a</i>, <i>b</i>, <i>c</i>}}.
    * </p>
    * The running time of this algorithm with respect to the size <i>n</i> of 
    * the set is very bad, its time complexity is estimated as 
    * <i>O</i>(<i>n<sup>n</sup></i>). For <i>n</i> &le; 10 it requires less than
    * 2 sec on a 2 GHz dual core processor, but for <i>n</i> &le; 12 the running time
    * is about 10 seconds and explodes for greater <i>n</i>.
    * The number of partitions of a set of <i>n</i> is given by the <i>n</i>-th
    * Bell number <i>B<sub>n</sub></i>, see
    * <a href="http://oeis.org/A000110" target="_blank">http://oeis.org/A000110</a>.
    * @param <E> the type of the elements of the set
    * @param set a set
    * @return a list of all partitions of the set, each partition encoded by an array
    * @see #partitions(MathSet)
    */
   public static <E> ArrayList<int[]> partitions(E[] set) {
      ArrayList<int[]> partitions = new ArrayList<>();
      int[] partition = new int[set.length];
      if (set.length <= 1) {
         partitions.add(partition);
      } else {
         int[] partition_i;
         ArrayList<int[]> tmpPartitions;
         E[] s = java.util.Arrays.copyOf(set, set.length - 1);
         tmpPartitions = partitions(s); // recursion
         for (int i = 0; i < tmpPartitions.size(); i++) { // walk through the parts
            partition_i = tmpPartitions.get(i);
            int stageMax = max(partition_i) + 1;
            for (int stage = 0; stage <= stageMax; stage++) {
               partition = java.util.Arrays.copyOf(partition_i, set.length);
               partition[set.length - 1] = stage;
               partitions.add(partition);
            }
         }
      }
      /* Test output: ---
      for (int[] p : partitions) {
         System.out.println(java.util.Arrays.toString(p));      
      }
      // */
      return partitions;
   }
   
   /** Returns the maximum entry of this array. */
   private static int max(int[] x) {
      int max = Integer.MIN_VALUE;
      for (int i = 0; i < x.length; i++) {
         if (max < x[i]) max = x[i];
      }
      return max;
   }
   
   @Override
   public String toString() {
      if (this.isEmpty()) return "{}";
      StringBuilder out = new StringBuilder("{");
      for (E x : this) {
         out.append(x.toString());
         out.append(", ");
      }
      out.replace(out.length() - 2, out.length(), "");
      out.append("}");
      return out.toString();
   }
   
   /** For test purposes...*/
   /*
   public static void main(String... args) {
      MathSet<Integer> menge_;
      //ArrayList<MathSet<MathSet<Integer>>> partitions;
      ArrayList<int[]> partitions;
      long time;
      for (int n = 2; n <= 13; n += 1) { // n = 13 => running time > 1:30 h
         menge_ = new MathSet<>();
         for (int i = 1; i <= n; i++) {
            menge_.add(i);
         }
         time = System.currentTimeMillis();
         //partitions = partitions(menge_);
         partitions = partitions(menge_.toArray(new Integer[]{}));
         time = System.currentTimeMillis() - time;
         System.out.println(n+" -> "+ time/1000. + " sec; ("+partitions.size()+" partitions)");
         //for (int[] p : partitions) System.out.println(java.util.Arrays.toString(p));
         //for (MathSet<MathSet<Integer>> p : partitions) System.out.println(p);
      }
      System.exit(0);
      // ArrayList<Character> sequenz = new ArrayList<Character>(java.util.Arrays.asList(
         // new Character[] {'A', 'n', 'd', 'r', 'e', 'a', 's', 'x', 'y'}
      // ));
      // long zeit = System.nanoTime();
      // ArrayList<ArrayList<Character>> perms = new ArrayList<ArrayList<Character>>();
      // for(int k=0; k < factorial(sequenz.size()).intValue(); k++) {
         // perms.add(permutation(k,sequenz));
         // //System.out.println(permutation(k,sequenz));
      // }
      // zeit = System.nanoTime() - zeit;
      // System.out.println("+++ Laufzeit "+zeit+" ns");
      // perms = null;
      // // Erstellen einer Menge S = {1, 2, ..., n}:
      // //int n = 3;
      // //Set<Integer> s = new HashSet<Integer>(n);
      // //for (int i = 1; i <= n; i++) {
      // //   s.add(i);
      // //}
      // // ... oder von Characters:
      MathSet<Character> s = new MathSet<Character>(
         //new Character[] {'A', 'n', 'd', 'r', 'e', 'a', 's', 'x', 'y'}
         //new Character[] {'l', 'i', 'e', 'b'}
         new Character[] {'a', 'b', 'c', 'd'}
      );
      //int k_ = 2;
      //ArrayList<int[]> surjections = org.mathIT.numbers.Combinatorics.surjections(s.size(), k_);
      //for(int[] p : surjections) {
      //   System.out.println(java.util.Arrays.toString(p) + ", ");
      //}
      MathSet<Character> s1 = new MathSet<Character>(java.util.Arrays.asList(
         new Character[] {'u', 'g', 'a', 'b'}
      ));
      MathSet<Character> s2 = new MathSet<Character>(java.util.Arrays.asList(
         new Character[] {'v', 'i', 'e', 'l'}
      ));
      System.out.println("s ^ s1 = " + s.intersect(s1));
      System.out.println("s ^ s2 = " + s.intersect(s2));
      ArrayList<MathSet<Character>> liste = new ArrayList<MathSet<Character>>();
      liste.add(s1); liste.add(s2);
      System.out.println("s ^ s1 ^ s2 = " + s.intersect(liste));
      System.out.println("s u s1 = " + s.unify(s1));
      System.out.println("s u s2 = " + s.unify(s2));
      System.out.println("s u s1 u s2 = " + s.unify(liste));
      String 
      ausgabe = "=====================\nTeilmengen:\n";
      // // ... oder von Zahlen:
      MathSet<Number> z = new MathSet<Number>(
         new Integer[] {2, 4, 4, 8}
      );
      MathSet<Number> z1 = new MathSet<Number>(//java.util.Arrays.asList(
      //Set<Integer> z1 = new MathSet<Integer>(java.util.Arrays.asList(
         new Integer[] {1, 2, 3, 4}
      );
      MathSet<Double> z2 = new MathSet<Double>(java.util.Arrays.asList(
         new Double[] {1.0, Math.PI, 4.0, 4.2}
      ));
      System.out.println("z ^ z1 = " + z.intersect(z1));
      System.out.println("z ^ z2 = " + z.intersect(z2));
      ArrayList<MathSet<? extends Number>> listeZ = new ArrayList<MathSet<? extends Number>>();
      listeZ.add(z1); listeZ.add(z2);
      System.out.println("z ^ z1 ^ z2 = " + z.intersect(listeZ));
      System.out.println("z u z1 = " + z.unify(z1));
      System.out.println("z u z2 = " + z.unify(z2));
      System.out.println("z u z1 u z2 = " + z.unify(listeZ));
      // ------
      //Set<String> menge = new TreeSet<String>();
      //menge.add("A"); menge.add("B"); menge.add("C"); menge.add("abc");
      for (int k = 0; k <= 6; k++) {
         ArrayList<MathSet<Character>> t = subsets(s,k);
         //ArrayList<TreeSet<String>> t = subsets(menge,k);
         if (k < 9) ausgabe += " ";
         ausgabe += k + ": ";
         for (int j = 0; j < t.size() - 1; j++) {
            ausgabe += t.get(j) + ", ";
         }
         if (t.size() > 0) 
         {
            ausgabe += t.get(t.size() - 1);
         }
         //ausgabe += "  (" + t.size() + " Teilmengen)\n";
         ausgabe += "\n";
      }
      System.out.println(ausgabe);
      // // ... oder von Strings:
      // //Set<String> s = new HashSet<String>(n);
      // //s.add("A"); s.add("B"); s.add("C"); s.add("abc");
      // 
      // // Aufruf zur Erstellung aller Permutationen von S:
      // zeit = System.nanoTime();
      // Object[][] p = permutations(s);
      // zeit = System.nanoTime() - zeit;
      // System.out.println("+++ Laufzeit "+zeit+" ns");
      // 
      // // Wiederholung: -----
      // p = null;
      // zeit = System.nanoTime();
      // perms = new ArrayList<ArrayList<Character>>();
      // for(int k=0; k < factorial(sequenz.size()).intValue(); k++) {
         // perms.add(permutation(k,sequenz));
         // //System.out.println(permutation(k,sequenz));
      // }
      // perms = null;
      // zeit = System.nanoTime() - zeit;
      // System.out.println("+++ Laufzeit "+zeit+" ns");
      // zeit = System.nanoTime();
      // p = permutations(s);
      // zeit = System.nanoTime() - zeit;
      // System.out.println("+++ Laufzeit "+zeit+" ns");
      // // --------------------
      // 
      // // Ausgabe:
      // String ausgabe;
      // ausgabe = "=====================\nPermutationen:\n";
      // for (int i = 0; false && i < p.length; i++) {
         // if (i < 9) ausgabe += " ";
         // ausgabe += (i+1) + ": ";
         // for (int j = 0; j < p[0].length - 1; j++) {
            // ausgabe += p[i][j] + " - ";
         // }
         // if (p.length > 0 && p[0].length > 0) { 
            // ausgabe += p[i][p[0].length - 1];
         // }
         // ausgabe += "\n";
      // }
      // ausgabe += "\n" + p.length + " Permutationen";
      // System.out.println(ausgabe);
      
      // Aufruf zur Erstellung aller Teilmengen von S:      
      // Ausgabe:
      //String 
      ausgabe = "=====================\nTeilmengen:\n";
      int n2 = 4;
      MathSet<Integer> menge = new MathSet<Integer>();
      for (int i = 1; i <= n2; i++) {
         menge.add(i);
      }
      //Set<String> menge = new TreeSet<String>();
      //menge.add("A"); menge.add("B"); menge.add("C"); menge.add("abc");
      for (int k = 0; k <= 6; k++) {
         ArrayList<MathSet<Integer>> t = subsets(menge,k);
         //ArrayList<TreeSet<String>> t = subsets(menge,k);
         if (k < 9) ausgabe += " ";
         ausgabe += k + ": ";
         for (int j = 0; j < t.size() - 1; j++) {
            ausgabe += t.get(j) + ", ";
         }
         if (t.size() > 0) 
         {
            ausgabe += t.get(t.size() - 1);
         }
         //ausgabe += "  (" + t.size() + " Teilmengen)\n";
         ausgabe += "\n";
      }
      System.out.println(ausgabe);
   }
   // */
}
