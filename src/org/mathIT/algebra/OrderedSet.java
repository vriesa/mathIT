/*
 * OrderedSet.java
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
import java.util.Comparator;
import java.util.SortedSet;

/**
 * This class enables to create finite mathematical totally ordered sets 
 * and supplies some of the
 * usual mathematical set operations such as immutable copy, set difference,
 * union, or intersection.
 * The class extends the {@link java.util.TreeSet} class and is based 
 * on a {@link java.util.TreeMap}. 
 * The elements are ordered using their natural ordering, or by a 
 * Comparator provided at set creation time, depending on which constructor is used.
 * <p>
 * This implementation provides guaranteed log(n) time cost for the basic operations 
 * (add, remove and contains). Note that the ordering maintained by a set 
 * (whether or not an explicit comparator is provided) must be <i>consistent with 
 * equals</i> if it is to correctly implement the Set interface. 
 * (See {@link java.lang.Comparable} or {@link java.util.Comparator} for a 
 * precise definition of "consistent with equals.") 
 * This is so because the <code>Set</code> interface is defined in terms of the 
 * <code>equals</code> operation, but an <code>OrderedSet</code> instance performs 
 * all element comparisons using its <code>compareTo</code> (or <code>compare</code>) 
 * method, so two elements that are deemed equal by this method are, from the 
 * standpoint of the set, equal. The behavior of a set <i>is</i> well-defined 
 * even if its ordering is inconsistent with equals; it just fails to obey the 
 * general contract of the {@link java.util.Set} interface.
 * </p>
 * For further technical details see {@link java.util.TreeSet TreeSet}.
 * @param <E> the type of the elements of the set
 * @see MathSet
 * @see EnumSet
 * @see Set
 * @see java.util.TreeSet
 * @author Andreas de Vries
 * @version 1.1
 */
public class OrderedSet<E extends Comparable<E>> extends java.util.TreeSet<E> {
   private static final long serialVersionUID = 9223372035314726100L; // = "OrderedSet".hashCode() + Long.MAX_VALUE
   /**
    * Constructs a new, empty ordered set, sorted according to the
    * natural ordering of its elements. All elements inserted into
    * the set must implement the {@link Comparable} interface.
    * Furthermore, all such elements must be <i>mutually
    * comparable</i>, i.e., {@code e1.compareTo(e2)} must not throw a
    * {@code ClassCastException} for any elements {@code e1} and
    * {@code e2} in the set.  If the user attempts to add an element
    * to the set that violates this constraint (for example, the user
    * attempts to add a string element to a set whose elements are
    * integers), the {@code add} call will throw a
    * {@code ClassCastException}.
    */
   public OrderedSet() {
      super();
   }
   
   /**
    * Constructs a new, empty ordered set, sorted according to the specified
    * comparator.  All elements inserted into the set must be <i>mutually
    * comparable</i> by the specified comparator: {@code comparator.compare(e1,
    * e2)} must not throw a {@code ClassCastException} for any elements
    * {@code e1} and {@code e2} in the set.  If the user attempts to add
    * an element to the set that violates this constraint, the
    * {@code add} call will throw a {@code ClassCastException}.
    * @param comparator the comparator that will be used to order this set.
    * If {@code null}, the {@linkplain Comparable natural ordering} of the 
    * elements will be used.
    */
   public OrderedSet(Comparator<? super E> comparator) {
      super(comparator);
   }
    
   /**
    * Constructs a new ordered set containing the elements in the specified
    * collection, sorted according to the <i>natural ordering</i> of its
    * elements.  All elements inserted into the set must implement the
    * {@link Comparable} interface.  Furthermore, all such elements must be
    * <i>mutually comparable</i>: {@code e1.compareTo(e2)} must not throw a
    * {@code ClassCastException} for any elements {@code e1} and
    * {@code e2} in the set.
    *
    * @param c collection whose elements will comprise the new set
    * @throws ClassCastException if the elements in {@code c} are
    *         not {@link Comparable}, or are not mutually comparable
    * @throws NullPointerException if the specified collection is null
    */
   public OrderedSet(java.util.Collection<? extends E> c) {
      super(c);
   }
   
   /** Constructs a new set from the input array. Note that the set does not
    *  contain duplicate elements, any element equal to another one
    *  preceding it is ignored, according to the contract of a
    *  {@link SortedSet}.
    *  The backing HashMap instance has the 
    *  initial capacity of the array size and the default load factor (0.75).
    *  @param elements array containing the elements
    *  @throws ClassCastException if the elements in {@code c} are
    *         not {@link Comparable}, or are not mutually comparable
    */
   public OrderedSet(E[] elements) {
      this.addAll(Arrays.asList(elements));
   }
   
   /** Constructs a new set from the input element. The resulting set accordingly
    *  is a one-element set or a singleton.
    *  Note that for elements of primitive type you must invoke the wrapper class
    *  objects, e.g., <code>OrderedSet&lt;Integer&gt; s = new OrderedSet&lt;&gt;(new Integer(2));</code>. 
    *  @param element an element
    */
   public OrderedSet(E element) {
      super();
      this.add(element);
   }
   
   /** Creates and returns a clone copy of this set.
    *  I.e., the returned set object returned by this method is independent of this object.
    *  @return a cloned copy of this set
    */
   public OrderedSet<E> copy() {
      OrderedSet<E> set = new OrderedSet<>();
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
   public OrderedSet<E> minus(SortedSet<E> minuend) {
      OrderedSet<E> s = new OrderedSet<>();
      for (E x : this) {
         s.add(x);
      }
      s.removeAll(minuend);
      return s;
   }

   /** Returns the set difference of this set minus the specified element.
    *  The method does not change this set.
    *  @param element the set to be subtracted from this set
    *  @return the set this - {element}
    */
   public OrderedSet<E> minus(E element) {
      if (size() == 0 || (size() == 1 && contains(element) ) ) return emptySet();
      OrderedSet<E> s = new OrderedSet<>();
      for (E x : this) {
         if (!x.equals(element)) {
               s.add(x);
         }
      }
      return s;
   }
   
   /** Returns the intersection of this set and the specified set.
    *  The specified set is expected to contain elements of
    *  class <code>E</code>.
    *  @param set a set
    *  @return the intersection of this set and the input set
    */
   public OrderedSet<E> intersect(SortedSet<E> set) {
      OrderedSet<E> s = new OrderedSet<>();
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
    *  of <code>OrderedSet</code> or being of <code>OrderedSet</code> itself, 
    *  and each set is expected to contain only elements of
    *  class <code>E</code>.
    *  @param sets a list of sets
    *  @return the intersection of this set and all input sets
    */
   public OrderedSet<E> intersect(ArrayList<? extends SortedSet<E>> sets) {
      OrderedSet<E> s = new OrderedSet<>();
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
    *  class <code>E</code>.
    *  @param set a set
    *  @return the union of this set and the input set
    */
   public OrderedSet<E> unify(SortedSet<E> set) {
      OrderedSet<E> s = new OrderedSet<>();
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
    *  of <code>OrderedSet</code> or being of <code>OrderedSet</code> itself, 
    *  and each set is expected to contain only elements of
    *  class <code>E</code>.
    *  @param sets a list of sets
    *  @return the union of this set and all input sets
    */
   public OrderedSet<E> unify(ArrayList<? extends SortedSet<E>> sets) {
      OrderedSet<E> s = new OrderedSet<>();
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
    *  @param k an integer
    *  @return a list of all <i>k</i>-element subsets of the set <i>s</i>
    */
   public ArrayList<OrderedSet<E>> subsets(int k) {
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
   public ArrayList<MathSet<OrderedSet<E>>> partitions() {
      return partitions(this);
   }
   
   // ---- static methods: -----------------------------------------------------
   /** Returns the empty set. The type-safe way to obtain an empty set using 
    *  this method is illustrated by the following example:
    *  <pre>
    *     OrderedSet&lt;String&gt; s = OrderedSet.emptySet();
    *  </pre>
    *  Implementation note: Implementations of this method need not create a 
    *  separate OrderedSet object for each call.
    *  If an explicit variable for the empty set is not desired, the method
    *  {@link #emptySet(OrderedSet)} may be used..
    *  @param <E> type of elements of this set
    *  @return the empty set
    *  @see #emptySet(OrderedSet)
    *  @see java.util.Collections#emptySet()
    */
   public static <E extends Comparable<E>> OrderedSet<E> emptySet() {
      return new OrderedSet<>();
   }
   
   /** Returns the empty set. The type-safe way to obtain an empty set using 
    *  this method is illustrated by the following example call:
    *  <pre>
    *     OrderedSet.emptySet(new OrderedSet&lt;String&gt;());
    *  </pre>
    *  This method is appropriate if an explicit variable for the empty set is 
    *  not desired.
    *  @param <E> type of elements of this set
    *  @param set a set
    *  @return the empty set
    *  @see #emptySet()
    */
   public static <E extends Comparable<E>> OrderedSet<E> emptySet(OrderedSet<E> set) {
      return new OrderedSet<>();
   }
   
   /** Returns the <i>k</i>-element subsets of a set <i>s</i>,
    *  i.e., each <i>k</i>-element combination of <i>s</i>,
    *  stored in an array list.
    *  Especially, the array contains only the empty set if <i>k</i>=0, and
    *  only the entire set <i>s</i> if <i>k</i> = <i>n</i> where <i>n</i> is the 
    *  size of <i>s</i>. If <i>k</i> &gt; <i>n</i>, the array list is empty.
    *  @param <E> type of elements of this set
    *  @param set a set
    *  @param k an integer
    *  @return a list of all <i>k</i>-element subsets of the set <i>s</i>
    */
   public static <E extends Comparable<E>> ArrayList<OrderedSet<E>> subsets(SortedSet<E> set, int k) {
      ArrayList<OrderedSet<E>> subsets = new ArrayList<>();
      if (k == 0) {
         subsets.add(emptySet(new OrderedSet<E>()));
         return subsets;
      }

      OrderedSet<E> tmpS;
      ArrayList<E> s = new ArrayList<>(set);
      if (k == 1) {
         for (E i : s) {
            tmpS = new OrderedSet<>();
            tmpS.add(i);
            subsets.add(tmpS);
         }
      } else if ( k > 1 && k <= s.size() ) {
         ArrayList<OrderedSet<E>> tmp;
         for (int i = 0; i < s.size(); i++) {
            tmpS = new OrderedSet<>(); // size: Größe: n über k
            for (int j = i+1; j < s.size(); j++) {
               tmpS.add(s.get(j));
            }
            tmp = subsets(tmpS, k-1);
            for (int j = 0; j < tmp.size(); j++) {
               tmpS = tmp.get(j);
               //if (!tmpS.equals(emptySet(new OrderedSet<E>())))
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
   public static <E extends Comparable<E>> ArrayList<MathSet<OrderedSet<E>>> partitions(OrderedSet<E> set) {
      ArrayList<MathSet<OrderedSet<E>>> partitions = new ArrayList<>();
      ArrayList<OrderedSet<E>> partition;
      Object[] s = set.toArray();
      ArrayList<int[]> pList = MathSet.partitions(s);
      int n, i;
      for (int[] p : pList) {
         partition = new ArrayList<>();
         n = max(p);
         for (i = 0; i <= n; i++) {
            partition.add(new OrderedSet<E>());
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
    * @see #partitions(OrderedSet)
    */
   public static <E extends Comparable<E>> ArrayList<int[]> partitions(E[] set) {
      return MathSet.partitions(set);
   }
   
   /** Returns the maximum entry of this array. */
   private static int max(int[] x) {
      int max = Integer.MIN_VALUE;
      for (int i = 0; i < x.length; i++) {
         if (max < x[i]) max = x[i];
      }
      return max;
   }
   
   /** For test purposes ... */
   /*
   public static void main(String... args) {
      OrderedSet<Integer> menge_;
      //ArrayList<MathSet<OrderedSet<Integer>>> partitions;
      ArrayList<int[]> partitions;
      long time;
      for (int n = 2; n <= 3; n += 1) { // n = 13 => running time > 1:30 h
         menge_ = new OrderedSet<>();
         for (int i = 1; i <= n; i++) {
            menge_.add(i);
         }
         time = System.currentTimeMillis();
         //partitions = partitions(menge_);
         partitions = partitions(menge_.toArray(new Integer[]{}));
         time = System.currentTimeMillis() - time;
         System.out.println(n+" -> "+ time/1000. + " sec; ("+partitions.size()+" partitions)");
         for (int[] p : partitions) System.out.println(java.util.Arrays.toString(p));
         //for (MathSet<OrderedSet<Integer>> p : partitions) System.out.println(p);
      }
      System.exit(0);
      final OrderedSet<Integer> finalOrderedSet = emptySet(); //new OrderedSet<Integer>());
      finalOrderedSet.add(1);
      System.out.println("finalOrderedSet = " + finalOrderedSet);
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
      // //OrderedSet<Integer> s = new HashOrderedSet<Integer>(n);
      // //for (int i = 1; i <= n; i++) {
      // //   s.add(i);
      // //}
      // // ... oder von Characters:
      OrderedSet<Character> s = new OrderedSet<>(
         //new Character[] {'A', 'n', 'd', 'r', 'e', 'a', 's', 'x', 'y'}
         new Character[] {'l', 'i', 'e', 'b'}
      );
      OrderedSet<Character> s1 = new OrderedSet<>(java.util.Arrays.asList(
         new Character[] {'u', 'g', 'a', 'b'}
      ));
      OrderedSet<Character> s2 = new OrderedSet<>(java.util.Arrays.asList(
         new Character[] {'v', 'i', 'e', 'l'}
      ));
      System.out.println("s ^ s1 = " + s.intersect(s1));
      System.out.println("s ^ s2 = " + s.intersect(s2));
      ArrayList<OrderedSet<Character>> liste = new ArrayList<>();
      liste.add(s1); liste.add(s2);
      System.out.println("s ^ s1 ^ s2 = " + s.intersect(liste));
      System.out.println("s u s1 = " + s.unify(s1));
      System.out.println("s u s2 = " + s.unify(s2));
      System.out.println("s u s1 u s2 = " + s.unify(liste));
      String 
      ausgabe = "=====================\nTeilmengen:\n";
      // // ... oder von Zahlen:
      OrderedSet<Double> z = new OrderedSet<>(
         new Double[] {2., 4., 4., 8.}
      );
      OrderedSet<Double> z1 = new OrderedSet<>(//java.util.Arrays.asList(
      //OrderedSet<Integer> z1 = new OrderedSet<Integer>(java.util.Arrays.asList(
         new Double[] {1., 2., 3., 4.}
      );
      OrderedSet<Double> z2 = new OrderedSet<>(java.util.Arrays.asList(
         new Double[] {1.0, Math.PI, 4.0, 4.2}
      ));
      System.out.println("z ^ z1 = " + z.intersect(z1));
      System.out.println("z ^ z2 = " + z.intersect(z2));
      ArrayList<OrderedSet<Double>> listeZ = new ArrayList<>();
      listeZ.add(z1); listeZ.add(z2);
      System.out.println("z ^ z1 ^ z2 = " + z.intersect(listeZ));
      System.out.println("z u z1 = " + z.unify(z1));
      System.out.println("z u z2 = " + z.unify(z2));
      System.out.println("z u z1 u z2 = " + z.unify(listeZ));
      // ------
      //OrderedSet<String> menge = new TreeOrderedSet<String>();
      //menge.add("A"); menge.add("B"); menge.add("C"); menge.add("abc");
      for (int k = 0; k <= 6; k++) {
         ArrayList<OrderedSet<Character>> t = subsets(s,k);
         //ArrayList<TreeOrderedSet<String>> t = subsets(menge,k);
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
      // //OrderedSet<String> s = new HashOrderedSet<String>(n);
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
      OrderedSet<Integer> menge = new OrderedSet<>();
      for (int i = 1; i <= n2; i++) {
         menge.add(i);
      }
      //OrderedSet<String> menge = new TreeOrderedSet<String>();
      //menge.add("A"); menge.add("B"); menge.add("C"); menge.add("abc");
      for (int k = 0; k <= 6; k++) {
         ArrayList<OrderedSet<Integer>> t = subsets(menge,k);
         //ArrayList<TreeOrderedSet<String>> t = subsets(menge,k);
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
