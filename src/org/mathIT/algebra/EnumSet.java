/*
 * EnumSet.java
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

/**
 * This class enables to create finite mathematical sets and supplies some of the
 * usual mathematical set operations such as immutable copy, set difference,
 * union, or intersection.
 * The class extends the {@link java.util.EnumSet} class and is thus 
 * a specialized {@link MathSet} implementation for use with enum types.  All of
 * the elements in an enum set must come from a single enum type that is
 * specified, explicitly or implicitly, when the set is created.  Enum sets
 * are represented internally as bit vectors.  This representation is
 * extremely compact and efficient. The space and time performance of this
 * class should be good enough to allow its use as a high-quality, typesafe
 * alternative to traditional <code>int</code>-based "bit flags."  Even bulk
 * operations (such as <code>containsAll</code> and <code>retainAll</code>) should
 * run very quickly if their argument is also an enum set.
 * <p>
 * The iterator returned by the <code>iterator</code> method traverses the
 * elements in their <i>natural order</i> (the order in which the enum
 * constants are declared).  The returned iterator is <i>weakly
 * consistent</i>: it will never throw {@link java.util.ConcurrentModificationException}
 * and it may or may not show the effects of any modifications to the set that
 * occur while the iteration is in progress.
 * </p>
 * <p>
 * Null elements are not permitted.  Attempts to insert a null element
 * will throw {@link NullPointerException}.  Attempts to test for the
 * presence of a null element or to remove one will, however, function
 * properly.
 * </p>
 * <p>
 * Implementation note: All basic operations execute in constant time.
 * They are likely (though not guaranteed) to be much faster than their
 * {@link MathSet} counterparts.  Even bulk operations execute in
 * constant time if their argument is also an enum set.
 * </p>
 * <p>
 * For further technical details see {@link java.util.EnumSet}.
 * </p>
 *
 * @author Andreas de Vries
 * @version 1.2
 * @param <E> the type of the elements of this enum set
 * @see MathSet
 * @see OrderedSet
 * @see Set
 */
public class EnumSet<E extends Enum<E>> {
   protected java.util.EnumSet<E> enumSet;
   /** Constructs a new set.
    *  Since the underlying enum set cannot be generated as an empty set without
    *  information about the enum type or a first element in it, the empty set
    *  is the <code>null</code> object and therefore typeless.
    */
   public EnumSet() {         
      enumSet = null;
   }
   
   /** Constructs a new, empty set. The syntax to invoke this constructor
    *  reads:
    *  <pre>
    *    EnumSet&lt;Type&gt; set = new EnumSet&lt;Type&gt;(Type.class);
    *  </pre>
    *  where <code>Type</code> is a given enum.
    *  @param elementType type of the elements of this set
    */
   public EnumSet(Class<E> elementType) {
      enumSet = java.util.EnumSet.noneOf(elementType);
   }
   
   /** Constructs a new, empty set from the given name of an enum.
    *  The syntax to invoke this constructor reads:
    *  <pre>
    *    EnumSet set = new EnumSet(Name.class);
    *  </pre>
    *  where <code>Name</code> is the fully qualified name of a given enum.
    *  @param enumName the fully qualified name of the enum
    *  @throws ClassNotFoundException if the enum cannot be located
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public EnumSet(String enumName) throws ClassNotFoundException {
      EnumSet tmp = new EnumSet(Class.forName(enumName));
      enumSet = tmp.enumSet;
   }
   
   /** Creates an enum set initialized from the specified collection.
    *  If the specified collection is an EnumSet instance, this constructor 
    *  behaves identically to {@link #copy()}. 
    *  Otherwise, the specified collection must contain at least one element 
    *  (in order to determine the underlying enum set's element type).
    *  @param c the collection from which this enum set is constructed
    */
   public EnumSet(java.util.Collection<E> c) {
      enumSet = java.util.EnumSet.copyOf(c);
   }
   
   /** Constructs a new set containing the specified element. 
    *  @param element the element
    */
   public EnumSet(E element) {
      enumSet = java.util.EnumSet.of(element);
   }
   
   /** Constructs a new set from the input array. Note that the set does not
    *  contain duplicate elements, any element equal to another one
    *  preceding it is ignored, according to the contract of a
    *  {@link java.util.EnumSet}.
    *  @param first the first element
    *  @param rest array (varargs) containing the elements up to the first one
    */
   public EnumSet(E first, E[] rest) {
      enumSet = java.util.EnumSet.of(first, rest);
   }
   
   /* // Does not work!!?
   public EnumSet(E[] elements) {
      E first = elements[0];
      ArrayList<E> rest = new ArrayList<>();
      for (int i = 1; i < elements.length; i++) {
         rest.add(elements[i]);
      }
      enumSet = java.util.EnumSet.of(first, rest.toArray(elements));
   }
   // */
   
   /** Constructs a new set from the input enum set. 
    *  @param enumSet the enum set underlying this EnumSet
    */
   public EnumSet(java.util.EnumSet<E> enumSet) {
      this.enumSet = java.util.EnumSet.copyOf(enumSet);
   }
   
   /** Creates and returns a clone copy of this set.
    *  I.e., the returned set object returned by this method is independent of this object.
    *  @return a cloned copy of this set
    */
   public EnumSet<E> copy() {
      EnumSet<E> set = new EnumSet<>();
      set.enumSet = java.util.EnumSet.copyOf(this.enumSet);
      return set;
   }
   
   /** Returns the set difference of this set minus the specified minuend.
    *  The method does not change this set.
    *  @param minuend the set to be subtracted from this set
    *  @return the set this - minuend
    */
   public EnumSet<E> minus(EnumSet<E> minuend) {
      EnumSet<E> s = new EnumSet<>();
      s.enumSet = java.util.EnumSet.copyOf(this.enumSet);
      s.enumSet.removeAll(minuend.enumSet);
      return s;
   }

   /** Returns the set difference of this set minus the specified element.
    *  The method does not change this set.
    *  @param element the set to be subtracted from this set
    *  @return the set this - {element}
    */
   public EnumSet<E> minus(E element) {
      //if (size() == 0 || (size() == 1 && contains(element) ) ) return emptyEnumSet();
      EnumSet<E> s = new EnumSet<>();
      s.enumSet = java.util.EnumSet.copyOf(this.enumSet);
      s.enumSet.remove(element);
      return s;
   }
   
   /** Returns the intersection of this set and the specified set.
    *  The specified set is expected to contain elements of
    *  class <code>E</code> or a subclass (or implementing class) of <code>E</code>.
    *  @param set a set
    *  @return the intersection of this set and the input set
    */
   public EnumSet<E> intersect(EnumSet<? extends E> set) {
      EnumSet<E> s = new EnumSet<>();
      for (E x : this.enumSet) {
         if (set.enumSet.contains(x)) {
            if ( s.enumSet == null) {
               s.enumSet = java.util.EnumSet.of(x);
            } else {
               s.enumSet.add(x);
            }
         }
      }
      //if (s.enumSet.size() == 0) s = emptyEnumSet();
      return s;
   }
       
   /** Returns the intersection of this set and the specified set list.
    *  The specified list is expected to contain set objects of a subclass
    *  of <code>EnumSet</code> or being of <code>EnumSet</code> itself, 
    *  and each set is expected to contain elements of
    *  class <code>E</code> or a subclass (or implementing class) of <code>E</code>.
    *  @param sets a list of sets
    *  @return the intersection of this set and all input sets
    */
   public EnumSet<E> intersect(ArrayList<? extends EnumSet<? extends E>> sets) {
      EnumSet<E> s = new EnumSet<>();
      boolean contained;
      for (E x : this.enumSet) {
         contained = true;
         for (int i = 0; i < sets.size() && contained; i++) {
            contained = sets.get(i).enumSet.contains(x);
         }
         if (contained) {
            if ( s.enumSet == null) {
               s.enumSet = java.util.EnumSet.of(x);
            } else {
               s.enumSet.add(x);
            }
         }
      }
      //if (s.enumSet.size() == 0) s.enumSet = emptyEnumSet();
      return s;
   }
       
   /** Returns the union of this set and the specified set.
    *  The specified set is expected to contain elements of
    *  class <code>E</code> or a subclass (or implementing class) of <code>E</code>.
    *  @param set a set
    *  @return the union of this set and the input set
    */
   public EnumSet<E> unify(EnumSet<? extends E> set) {
      EnumSet<E> s = new EnumSet<>();
      s.enumSet = java.util.EnumSet.copyOf(this.enumSet);
      if (this.enumSet == null || this.enumSet.size() == 0) {
         return s;
      }
      for (E x : set.enumSet) {
         s.enumSet.add(x);
      }
      // if (s.size() == 0) s = emptyEnumSet();
      return s;
   }
       
   /** Returns the union of this set and the specified set list.
    *  The specified list is expected to contain set objects of a subclass
    *  of <code>EnumSet</code> or being of <code>EnumSet</code> itself, 
    *  and each set is expected to contain elements of
    *  class <code>E</code> or a subclass (or implementing class) of <code>E</code>.
    *  @param sets a list of sets
    *  @return the union of this set and all input sets
    */
   public EnumSet<E> unify(ArrayList<? extends EnumSet<? extends E>> sets) {
      EnumSet<E> s = new EnumSet<>();
      s.enumSet = java.util.EnumSet.copyOf(this.enumSet);
      for (int i = 0; i < sets.size(); i++) {
         for (E x : sets.get(i).enumSet) {
            if ( s.enumSet == null) {
               s.enumSet = java.util.EnumSet.of(x);
            } else {
               s.enumSet.add(x);
            }
         }
      }
      // if (s.size() == 0) s = emptyEnumSet();
      return s;
   }
   
   /**
    * Returns the size of this EnumSet, that is, the number of its elements.
    * @return the size of this EnumSet
    */
   public int size() {
      return enumSet.size();
   }
       
   /** Returns the <i>k</i>-element subsets of this set,
    *  i.e., each of its <i>k</i>-element combination, stored in an array list.
    *  Especially, the array contains only the empty set if <i>k</i>=0, and
    *  only this set <i>s</i> if <i>k</i> = <i>n</i> where <i>n</i> is the 
    *  size of this set. If <i>k</i> &gt; <i>n</i>, the array list is empty.
    *  @param k an integer
    *  @return a list of all <i>k</i>-element subsets of the set <i>s</i>
    */
   public ArrayList<EnumSet<E>> subsets(int k) {
      return subsets(this,k);
   }
   
   /** Returns a string representation of this set.
    *  @return a string representation of this set
    */
   @Override
   public String toString() {
      if (enumSet == null) {
         //System.out.println("??? enum = null!");
         return "null";
      }
      String result = enumSet.toString();
      result = result.replace('[','{');
      result = result.replace(']','}');
      return result;
      //return enumSet.toString();
   }
   
   // ---- static methods: -----------------------------------------------------   
   /** Returns the <i>k</i>-element subsets of a set <i>s</i>,
    *  i.e., each <i>k</i>-element combination of <i>s</i>,
    *  stored in an array list.
    *  Especially, the array contains only the empty set if <i>k</i>=0, and
    *  only the entire set <i>s</i> if <i>k</i> = <i>n</i> where <i>n</i> is the 
    *  size of <i>s</i>. If <i>k</i> &gt; <i>n</i>, the array list is empty.
    *  @param <E> type of the elements of this subset
    *  @param set a set
    *  @param k an integer
    *  @return a list of all <i>k</i>-element subsets of the set <i>s</i>
    */
   public static <E extends Enum<E>> ArrayList<EnumSet<E>> subsets(EnumSet<E> set, int k) {
      ArrayList<EnumSet<E>> subsets = new ArrayList<>();
      if (set.enumSet == null) {
         return subsets;
      }
      if (k == 0) {
         EnumSet<E> tmp = new EnumSet<>();
         tmp.enumSet = java.util.EnumSet.copyOf(set.enumSet);
         tmp.enumSet.clear();
         subsets.add(tmp);
         return subsets;
      }

      EnumSet<E> tmpS;
      // ????
      if (k == 1) {
//System.out.println("### set="+set+", k="+k);
         for (E i : set.enumSet) {
            tmpS = new EnumSet<>(i);
            subsets.add(tmpS);
         }
      } else if ( k > 1 && k <= set.enumSet.size() ) {
         ArrayList<E> s = new ArrayList<>();
         for (E i : set.enumSet) {
            s.add(i);
         }
         ArrayList<EnumSet<E>> tmp;
         for (int i = 0; i < s.size(); i++) {
            //tmpS = new EnumSet<E>(); // size: n choose k
            //tmpS.enumSet = java.util.EnumSet.copyOf(set.enumSet);
            tmpS = new EnumSet<>(set.enumSet);
//System.out.println("### set = " + set + ", enumSet " + tmpS.enumSet);
            tmpS.enumSet.clear();
            for (int j = i+1; j < s.size(); j++) {
               //if ( tmpS.enumSet == null) tmpS.enumSet = java.util.EnumSet.of(s.get(j));
               //else tmpS.enumSet.add(s.get(j));
               tmpS.enumSet.add(s.get(j));
            }
//System.out.println("### s="+s+", tmpS="+tmpS+"k="+k);
            tmp = subsets(tmpS, k-1);
//System.out.println("### tmp="+tmp+", tmpS="+tmpS+", k="+k);
            for (int j = 0; j < tmp.size(); j++) {
               tmpS = tmp.get(j);
               //if (!tmpS.equals(emptyEnumSet(new EnumSet<E>())))
               {
                  tmpS.enumSet.add(s.get(i));
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
    * 2 sec on a 2 GHz dual core processor, but for <i>n</i> &le; 11 the running time
    * is about 10 seconds and explodes for greater <i>n</i>.
    * @param <E> the type of the elements of the set
    * @param set a set
    * @return a list of all partitions of the set.
    */
   public static <E extends Enum<E>> ArrayList<MathSet<EnumSet<E>>> partitions(EnumSet<E> set) {
      ArrayList<MathSet<EnumSet<E>>> partitions = new ArrayList<>();
      MathSet<EnumSet<E>> partition = new MathSet<>(), partition_i; // the partitions
      if (set.size() <= 1) {
         partition.add(set);
         partitions.add(partition);
      } else {
         ArrayList<MathSet<EnumSet<E>>> tmpPartitions;
         EnumSet<E> s;
         EnumSet<E> x = new EnumSet<>();
         for (E e : set.enumSet) {
            x = new EnumSet<>(e); break;
         }
         s = set.minus(x);
         tmpPartitions = partitions(s);
         for (int i = 0; i < tmpPartitions.size(); i++) { // walk through the parts
            partition_i = tmpPartitions.get(i);
            partition = new MathSet<>();
            partition.add(x);
            for (EnumSet<E> p : partition_i) {
               partition.add(p);
            }
            partitions.add(partition);
            for (EnumSet<E> p : partition_i) {
               partition = partition_i.copy();
               partition.remove(p);
               p = p.unify(x);
               partition.add(p);
               partitions.add(partition);
            }
         }
      }
      return partitions;
   }
   
   /*
   public static void main(String... args) {
//      EnumSet<Zahlen> menge_ = new EnumSet<>(Zahlen.$1, new Zahlen[]{
//         Zahlen.$2, Zahlen.$3, Zahlen.$4, Zahlen.$5, Zahlen.$6, Zahlen.$7, 
//         Zahlen.$8, Zahlen.$9, Zahlen.$10, Zahlen.$11, //Zahlen.$12, //Zahlen.$13, 
//      });
//      System.out.println("set = " + menge_);
//      ArrayList<MathSet<EnumSet<Zahlen>>> partitions;
//      long time;
//      time = System.currentTimeMillis();
//      partitions = partitions(menge_);
//      time = System.currentTimeMillis() - time;
//      System.out.println(" -> " + time / 1000. + " sec; (" + partitions.size() + " partitions)");
//      //System.out.println(partitions);
//      System.exit(0);
      EnumSet<Zahlen> s = new EnumSet<>(
         Zahlen.$1, new Zahlen[]{Zahlen.$2,Zahlen.$3, Zahlen.$4, Zahlen.$5}
      );
      EnumSet<Zahlen> s1 = new EnumSet<>(
         Zahlen.$1, new Zahlen[]{Zahlen.$3, Zahlen.$5}
      );
      EnumSet<Zahlen> s2 = new EnumSet<>(
         Zahlen.$2, new Zahlen[]{Zahlen.$4, Zahlen.$6}
      );
      System.out.println("s = " + s);
      System.out.println("s1 = " + s1);
      System.out.println("s2 = " + s2);
      System.out.println("s - s1 = " + s.minus(s1));
      System.out.println("s - s2 = " + s.minus(s2));
      
      System.out.println("s ^ s1 = " + s.intersect(s1));
      System.out.println("s ^ s2 = " + s.intersect(s2));
      ArrayList<EnumSet<Zahlen>> liste = new ArrayList<>();
      liste.add(s1); liste.add(s2);
      System.out.println("s ^ s1 ^ s2 = " + s.intersect(liste));
      System.out.println("s u s1 = " + s.unify(s1));
      System.out.println("s u s2 = " + s.unify(s2));
      System.out.println("s u s1 u s2 = " + s.unify(liste));
      String 
      ausgabe = "=====================\nTeilmengen:\n";
      for (int k = 0; k <= 8; k++) {
         ArrayList<EnumSet<Zahlen>> t = subsets(s,k);
         //ArrayList<TreeEnumSet<String>> t = subsets(menge,k);
         if (k < 9) {
            ausgabe += " ";
         }
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
      // //EnumSet<String> s = new EnumSet<String>(n);
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
      System.out.println(ausgabe);
      
      // Erzeugung einer leeren EnumSet:
      EnumSet<Zahlen> mySet = new EnumSet<>(Zahlen.class);
      System.out.println("mySet = " + mySet);
      try {
         //EnumSet<Zahlen> mySet2 = new EnumSet<Zahlen>("org.mathIT.algebra.Zahlen");
         EnumSet<Zahlen> mySet2 = new EnumSet<>("org.mathIT.algebra.Zahlen");
         System.out.println("mySet2 = " + mySet2);
         //System.out.println("Klasse: " + Class.forName("org.mathIT.algebra.Zahlen"));
      } catch (ClassNotFoundException cnfe) {
         System.err.println(cnfe);
      }
   }
   // */
}

enum Zahlen {
   $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13
}

enum GeradeZahlen {
   $2, $4, $6
}

enum UngeradeZahlen {
   $1, $3, $5
} 
