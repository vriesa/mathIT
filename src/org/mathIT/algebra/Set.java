/*
 * Set.java
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
 * @author Andreas de Vries
 * @version 1.1
 * @see EnumSet
 * @see MathSet
 * @see OrderedSet
 * @see java.util.HashSet
 * @param <E> the type of the elements of this set
 */
public class Set<E> extends java.util.HashSet<E> {
   private static final long serialVersionUID = 1120767115L; // hash code of "org.mathIT.algebra.Set"
   /** Constructs a new, empty set; the backing HashMap instance has default 
    *  initial capacity (16) and load factor (0.75).
    */
   public Set() {
      //super();
   }
   
   /** Constructs a new set containing the elements in the specified collection.
    *  @param c a collection
    */
   public Set(java.util.Collection<? extends E> c) {
      super(c);
   }
   
   /** Constructs a new, empty set; the backing HashMap instance has the 
    *  specified initial capacity and default load factor (0.75).
    *  @param initialCapacity the initial capacity of the hash map
    *  @throws IllegalArgumentException - if the initial capacity is less than zero
    */
   public Set(int initialCapacity) {
      super(initialCapacity);
   }
   
   /** Constructs a new, empty set; the backing HashMap instance has the 
    *  specified initial capacity and the specified load factor.
    *  @param initialCapacity the initial capacity of the hash map
    *  @param loadFactor - the load factor of the backing hash map
    *  @throws IllegalArgumentException if the initial capacity is less than zero, 
    *    or if the load factor is nonpositive
    */
   public Set(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   /** Constructs a new set from the input array. Note that the set does not
    *  contain duplicate elements, any element equal to another one
    *  preceding it is ignored, according to the contract of a
    *  {@link java.util.Set}.
    *  The backing HashMap instance has the 
    *  initial capacity of the array size and the default load factor (0.75).
    *  @param elements array containing the elements
    */
   public Set(E[] elements) {
      super(elements.length);
      this.addAll(Arrays.asList(elements));
   }
   
   /** Creates and returns a clone copy of this set.
    *  I.e., the returned set object returned by this method is independent of this object.
    *  @return a cloned copy of this set
    */
   public Set<E> copy() {
      Set<E> set = new Set<>(this.size());
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
   public Set<E> minus(java.util.Set<E> minuend) {
      Set<E> s = new Set<>(this.size());
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
   public Set<E> minus(E element) {
      if (size() == 0 || (size() == 1 && contains(element) ) ) return emptySet();
      Set<E> s = new Set<>(this.size());
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
   public Set<E> intersect(java.util.Set<? extends E> set) {
      Set<E> s = new Set<>(this.size());
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
    *  of <code>Set</code> or being of <code>Set</code> itself, 
    *  and each set is expected to contain elements of
    *  class <code>E</code> or a subclass (or implementing class) of <code>E</code>.
    *  @param sets a list of sets
    *  @return the intersection of this set and all input sets
    */
   public Set<E> intersect(ArrayList<? extends java.util.Set<? extends E>> sets) {
      Set<E> s = new Set<>(this.size());
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
   public Set<? extends E> unify(java.util.Set<? extends E> set) {
      Set<E> s = new Set<>(this.size());
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
    *  of <code>Set</code> or being of <code>Set</code> itself, 
    *  and each set is expected to contain elements of
    *  class <code>E</code> or a subclass (or implementing class) of <code>E</code>.
    *  @param sets a list of sets
    *  @return the union of this set and all input sets
    */
   public Set<E> unify(ArrayList<? extends java.util.Set<? extends E>> sets) {
      Set<E> s = new Set<>(this.size());
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
   public ArrayList<Set<E>> subsets(int k) {
      return subsets(this,k);
   }
   
   // ---- static methods: -----------------------------------------------------
   /** Returns the empty set. The type-safe way to obtain an empty set using 
    *  this method is illustrated by the following example:
    *  <pre>
    *     Set&lt;String&gt; s = Set.emptySet();
    *  </pre>
    *  Implementation note: Implementations of this method need not create a 
    *  separate Set object for each call.
    *  If an explicit variable for the empty set is not desired, the method
    *  {@link #emptySet(Set)} may be used..
    *  @param <E> type of elements of this set
    *  @return the empty set
    *  @see #emptySet(Set)
    *  @see java.util.Collections#emptySet()
    */
   public static <E> Set<E> emptySet() {
      return new Set<>();
   }
   
   /** Returns the empty set. The type-safe way to obtain an empty set using 
    *  this method is illustrated by the following example call:
    *  <pre>
    *     Set.emptySet(new Set&lt;String&gt;());
    *  </pre>
    *  This method is appropriate if an explicit variable for the empty set is 
    *  not desired.
    *  @param <E> type of elements of this set
    *  @param set a set
    *  @return the empty set
    *  @see #emptySet()
    */
   public static <E> Set<E> emptySet(Set<E> set) {
      return new Set<>();
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
   public static <E> ArrayList<Set<E>> subsets(java.util.Set<E> set, int k) {
      ArrayList<Set<E>> subsets = new ArrayList<>();
      if (k == 0) {
         subsets.add(emptySet(new Set<E>()));
         return subsets;
      }

      Set<E> tmpS;
      ArrayList<E> s = new ArrayList<>(set); // represent set as an array list (indexing each element!)
      if (k == 1) {
         for (E i : s) {
            tmpS = new Set<>();
            tmpS.add(i);
            subsets.add(tmpS);
         }
      } else if ( k > 1 && k <= s.size() ) {
         ArrayList<Set<E>> list;
         for (int i = 1; i < s.size(); i++) {
            tmpS = new Set<>();
            for (int j = 0; j < i; j++) {
               tmpS.add(s.get(j));
            }
            list = subsets(tmpS, k-1); // recursion step
            for (int j = 0; j < list.size(); j++) {
               tmpS = list.get(j); // get subset j
               tmpS.add(s.get(i)); // add element i
               subsets.add(tmpS);
            }  // for j
         } // for i
      }
      return subsets;
   }
   
   /*
   public static void main(String... args) {
      final Set<Integer> finalSet = emptySet(); //new Set<Integer>());
      finalSet.add(1);
      System.out.println("finalSet = " + finalSet);
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
      Set<Character> s = new Set<Character>(
         //new Character[] {'A', 'n', 'd', 'r', 'e', 'a', 's', 'x', 'y'}
            //new Character[] {'l', 'i', 'e', 'b'}
            new Character[] {'a', 'b'}
      );
      Set<Character> s1 = new Set<Character>(java.util.Arrays.asList(
         new Character[] {'u', 'g', 'a', 'b'}
      ));
      Set<Character> s2 = new Set<Character>(java.util.Arrays.asList(
         new Character[] {'v', 'i', 'e', 'l'}
      ));
      System.out.println("s ^ s1 = " + s.intersect(s1));
      System.out.println("s ^ s2 = " + s.intersect(s2));
      ArrayList<Set<Character>> liste = new ArrayList<Set<Character>>();
      liste.add(s1); liste.add(s2);
      System.out.println("s ^ s1 ^ s2 = " + s.intersect(liste));
      System.out.println("s u s1 = " + s.unify(s1));
      System.out.println("s u s2 = " + s.unify(s2));
      System.out.println("s u s1 u s2 = " + s.unify(liste));
      String 
      ausgabe = "=====================\nTeilmengen:\n";
      // // ... oder von Zahlen:
      Set<Number> z = new Set<Number>(
         new Integer[] {2, 4, 4, 8}
      );
      Set<Number> z1 = new Set<Number>(//java.util.Arrays.asList(
      //Set<Integer> z1 = new Set<Integer>(java.util.Arrays.asList(
         new Integer[] {1, 2, 3, 4}
      );
      Set<Double> z2 = new Set<Double>(java.util.Arrays.asList(
         new Double[] {1.0, Math.PI, 4.0, 4.2}
      ));
      System.out.println("z ^ z1 = " + z.intersect(z1));
      System.out.println("z ^ z2 = " + z.intersect(z2));
      ArrayList<Set<? extends Number>> listeZ = new ArrayList<Set<? extends Number>>();
      listeZ.add(z1); listeZ.add(z2);
      System.out.println("z ^ z1 ^ z2 = " + z.intersect(listeZ));
      System.out.println("z u z1 = " + z.unify(z1));
      System.out.println("z u z2 = " + z.unify(z2));
      System.out.println("z u z1 u z2 = " + z.unify(listeZ));
      // ------
      //Set<String> menge = new TreeSet<String>();
      //menge.add("A"); menge.add("B"); menge.add("C"); menge.add("abc");
      for (int k = 0; k <= 6; k++) {
         ArrayList<Set<Character>> t = subsets(s,k);
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
      Set<Integer> menge = new Set<Integer>();
      for (int i = 1; i <= n2; i++) {
         menge.add(i);
      }
      //Set<String> menge = new TreeSet<String>();
      //menge.add("A"); menge.add("B"); menge.add("C"); menge.add("abc");
      for (int k = 0; k <= 6; k++) {
         ArrayList<Set<Integer>> t = subsets(menge,k);
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
