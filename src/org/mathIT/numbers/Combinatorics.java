/*
 * Combinatorics.java
 *
 * Copyright (C) 2008-2012 Andreas de Vries
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
package org.mathIT.numbers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import static org.mathIT.numbers.Numbers.*;

/** This class provides methods related to combinatorics.
 *  Examples are routines to generate all permutations or
 *  all combinations of a set.
 *  @author Andreas de Vries
 *  @version 1.0
 */
public class Combinatorics {
   /** For every number <i>k</i> with <i>k</i> &ge; 0 a list of all words 
    *  of length <i>k</i> over the specified alphabet is returned.
    *  There are <i>n<sup>k</sup></i> words of this kind.
    *  The input set <i>s</i> itself is <i>not</i> modified by this method.
    *  @param k a nonnegative integer &le; <i>n</i>
    *  @param alphabet the alphabet
    *  @return a set of all words of length <i>k</i>
    *  @throws IllegalArgumentException if <i>k</i> &le; 0
    */
   public static HashSet<StringBuilder> words(int k, HashSet<StringBuilder> alphabet) {
      if ( k <= 0 ) {
         throw new IllegalArgumentException("k must satisfy 0 < k <= alphabet.size(), but k="+k);
      }
      
      HashSet<StringBuilder> words, list;
      
      if (k == 1) {  // base case: words of length 1
         return alphabet;
      } else {
         list = words(k - 1, alphabet);  // recursion
         words = new HashSet<>();
         for (StringBuilder letter : alphabet) {
            for (StringBuilder w : list) {
               words.add((new StringBuilder(letter)).append(w));
            }
         }
         return words;
      }
   }
   
   /** For every number <i>k</i> with <i>k</i> &ge; 0 a list of all words 
    *  of length <i>k</i> over the specified alphabet is returned.
    *  There are <i>n<sup>k</sup></i> words of this kind.
    *  The input set <i>s</i> itself is <i>not</i> modified by this method.
    *  @param k a nonnegative integer &le; <i>n</i>
    *  @param alphabet the alphabet
    *  @return a set of all words of length <i>k</i>
    *  @throws IllegalArgumentException if <i>k</i> &le; 0
    */
   public static TreeSet<String> words(int k, Set<Character> alphabet) {
      if ( k <= 0 ) {
         throw new IllegalArgumentException("k must satisfy 0 < k <= alphabet.size(), but k="+k);
      }
      
      TreeSet<String> words, list;
      
      if (k == 1) {  // base case: words of length 1
         words = new TreeSet<>();
         for (Character letter : alphabet) {
            words.add(String.valueOf(letter));
         }
         return words;
      } else {
         list = words(k - 1, alphabet);  // recursion
         words = new TreeSet<>();
         for (char letter : alphabet) {
            for (String w : list) {
               words.add(letter + w);
            }
         }
         return words;
      }
   }
   
   /** For every number <i>k</i> with <i>k</i> &ge; 0 a list of all words 
    *  of length <i>k</i> over the specified alphabet is returned.
    *  There are <i>n<sup>k</sup></i> words of this kind.
    *  The input list <i>s</i> itself is <i>not</i> modified by this method.
    *  @param k a nonnegative integer
    *  @param alphabet the alphabet
    *  @return a list of all words of length <i>k</i>
    *  @throws IllegalArgumentException if <i>k</i> &lt; 0 or <i>k</i> &gt;= 0
    */
   public static StringBuilder[] words(int k, char[] alphabet) {
      if (k <= 0) {
         throw new IllegalArgumentException("k must satisfy k > 0, but k="+k);
      }
      
      StringBuilder[] words, list;
      int i;
      
      if (k == 1) {  // base case: words of length 1
         words = new StringBuilder[alphabet.length];
         for (i = 0; i < words.length; i++) {
            words[i] = (new StringBuilder()).append(alphabet[i]);
         }
         return words;
      } else {
         int j, w=0;
         list = words(k - 1, alphabet);  // recursion
         words = new StringBuilder[alphabet.length * list.length];
         for (i = 0; i < alphabet.length; i++) {
            for (j = 0; j < list.length; j++) {
               words[w++] = (new StringBuilder(list[j])).append(alphabet[i]);
            }
         }
         return words;
      }
   }
   
   /** For every number <i>k</i> with <i>k</i> &ge; 0 a list of all words 
    *  of length <i>k</i> over the specified alphabet is returned.
    *  There are <i>n<sup>k</sup></i> words of this kind.
    *  The input list <i>s</i> itself is <i>not</i> modified by this method.
    *  @param k a nonnegative integer
    *  @param alphabet the alphabet
    *  @return a list of all words of length <i>k</i>
    *  @throws IllegalArgumentException if <i>k</i> &lt; 0 or <i>k</i> &gt;= 0
    */
   public static ArrayList<StringBuilder> words(int k, ArrayList<Character> alphabet) {
      if (k <= 0) {
         throw new IllegalArgumentException("k must satisfy k > 0, but k="+k);
      }
      
      ArrayList<StringBuilder> words, list;
      
      if (k == 1) {  // base case: words of length 1
         words = new ArrayList<>(alphabet.size());
         for (Character letter : alphabet) {
            words.add((new StringBuilder()).append(letter));
         }
         return words;
      } else {
         list = words(k - 1, alphabet);  // recursion
         words = new ArrayList<>(alphabet.size() * list.size());
         for (char letter : alphabet) {
            for (StringBuilder w : list) {
               words.add((new StringBuilder()).append(letter).append(w));  // do not change object w!
            }
         }
         return words;
      }
   }
   
   /** For every number <i>k</i> with <i>k</i> &ge; 0 a list of all words 
    *  of length <i>k</i> over the specified alphabet is returned.
    *  There are <i>n<sup>k</sup></i> words of this kind.
    *  The input list <i>s</i> itself is <i>not</i> modified by this method.
    *  @param k a nonnegative integer
    *  @param alphabet the alphabet
    *  @return a list of all words of length <i>k</i>
    *  @throws IllegalArgumentException if <i>k</i> &lt; 0 or <i>k</i> &gt;= 0
    */
   public static byte[][] words(int k, byte[] alphabet) {
      if (k <= 0) {
         throw new IllegalArgumentException("k must satisfy k > 0, but k="+k);
      }
      
      byte[][] words, list;
      //byte[] word;
      int i;
      
      if (k == 1) {  // base case: words of length 1
         words = new byte[alphabet.length][k];
         for (i = 0; i < words.length; i++) {
            words[i][0] = alphabet[i];
         }
         return words;
      } else {
         int j, l, w = 0;
         list = words(k - 1, alphabet);  // recursion
         words = new byte[alphabet.length * list.length][k];
         for (i = 0; i < alphabet.length; i++) {
            for (j = 0; j < list.length; j++) {
               words[w][0] = alphabet[i];
               for (l = 1; l < k; l++) {
                  //System.out.println("### i="+i+", j="+j+", l="+l);
                  words[w][l] = list[j][l-1];
               }
               w++;
            }
         }
         return words;
      }
   }
   
   /** For every number <i>k</i> with 0 &le; <i>k</i> &lt; <i>n</i>! the 
    *  <i>k</i>-th permutation of the sequence <i>s</i> is returned.
    *  The input list <i>s</i> itself is <i>not</i> modified by this method.
    *  @param <T> subclass of {@link ArrayList}
    *  @param k a nonnegative integer &lt; <i>n</i>!
    *  @param s a sequence of objects
    *  @return the sequence resulting from the <i>k</i>-th permutation
    *  @throws IllegalArgumentException if <i>k</i> &lt; 0 or <i>k</i> &gt;= <i>n</i>!
    */
   public static <T> ArrayList<T> permutation(int k, ArrayList<T> s) {
      if ( k < 0 || java.math.BigInteger.valueOf(k).compareTo(factorial(s.size())) >= 0 ) {
         throw new IllegalArgumentException("k must satisfy 0 <= k < s.size()!, but k="+k);
      }
      ArrayList<T> newS = new ArrayList<>(s);
      T tmp;
      int factorial = 1;
      for (int j = 2; j < s.size(); j++) {
         factorial *= (j-1);
         //swap s[j - ((k / factorial) mod j)] with s[j]:
         tmp = newS.get(j - (k/factorial) % j);
         newS.set(j - (k/factorial) % j, newS.get(j));
         newS.set(j, tmp);
      }
      return newS;
   }
   
   /** Returns all permutations of a sequence as a 2-dimensional array.
    *  If the size of the sequence is 0, an array of length zero is returned.
    *  @param <T> a general class
    *  @param s a sequence containing elements of a specified type &lt;T&gt;
    *  @return a table where each row is a permutation of the eentries of <i>s</i>
    */
   @SuppressWarnings("unchecked")
   public static <T> T[][] permutations(T[] s) {
      if (s.length == 0) {
         return (T[][]) java.lang.reflect.Array.newInstance(
            java.lang.reflect.Array.newInstance(
               s.getClass().getComponentType(), 0).getClass(), 0
         );
      }
      
      T[] perm = (T[]) java.lang.reflect.Array.newInstance(s.getClass().getComponentType(), s.length);
      T[][] p = (T[][]) java.lang.reflect.Array.newInstance(
         java.lang.reflect.Array.newInstance(
            s.getClass().getComponentType(), s.length).getClass(), factorial(s.length).intValue()
         );
      if (s.length == 1) {
         p[0] = perm; // <- no copy necessary (there is only one entry...)
         p[0][0] = s[0];
         return p;
      } else {
         T[][] tmp;
         T[] newS;
         int i = 0, j, k, l;
         for (int x = 0; x < s.length; x++) {
            newS = (T[]) java.lang.reflect.Array.newInstance(
               s.getClass().getComponentType(), s.length - 1
            );
            l = 0;
            for (j = 0; j < s.length; j++) {
               if (j != x) newS[l++] = s[j];
            }
            tmp = permutations(newS);  // <- recursion
            for (j = 0; j < tmp.length; j++) {
               p[i+j] = java.util.Arrays.copyOf(perm, perm.length); 
               p[i+j][0] = s[x];
               for (k = 0; k < tmp[0].length; k++) {
                  p[i+j][k+1] = tmp[j][k];
               } // for k
            }  // for j
            i += tmp.length;
         } // for x
         return p;
      }
   }
   
   /** Returns all permutations of a set as a 2-dimensional array.
    *  If the size of the set is 0, an array of length zero is returned.
    *  This method detects whether not all elements are of the same type
    *  and returns a 2-dimensional array of the "least" superclass of all
    *  elements.
    *  For instance, a set of different numbers may be input as follows:
    *  <pre>
      HashSet&lt;Number&gt; s = new HashSet&lt;Number&gt;(java.util.Arrays.asList(
         new Number[] {1, 3, 3.1415, java.math.BigInteger.valueOf(5)}
      ));
      Number[][] perms = permutations(s);
      for (int i=0; i &lt; perms.length; i++) {
         System.out.print((1+i) + ") ");
         for (Number x : perms[i]) {
            System.out.print(x + ", ");
         }
         System.out.println();
      }
    *  </pre>
    *  @param <T> a general class
    *  @param set a set containing elements of a specified type &lt;T&gt;
    *  @return a table where each row is a permutation of the elements of <i>set</i>
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static <T> T[][] permutations(Set<T> set) {
      Class clazz = findTypeOfElements(set, null);
      //System.out.println("### Determined class " + clazz); 
      T[] s = (T[]) java.lang.reflect.Array.newInstance(clazz, set.size());
      int k = 0;
      for (T x : set) {
         s[k++] = x;
      }
      return permutations(s);
   }
   
   /** Determines recursively the "least" superclass of all elements of set.
    *  The method should be invoked with the start class null.
    *  @param set a set
    *  @param clazz the currently found least superclass so far; should be null at start
    *  @return the least superclass of all elements of set.
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   private static <T> Class findTypeOfElements(Set<T> set, Class clazz) {
      for (T x : set) {
         if (clazz == null) {
            clazz = x.getClass();
         } else if (!clazz.isAssignableFrom(x.getClass())) {
            return findTypeOfElements(set, clazz.getSuperclass());
         }
      }
      return clazz;
   }

   /** Returns the <i>k</i>-element subsets of a set <i>s</i>,
    *  i.e., each <i>k</i>-element combination of <i>s</i>,
    *  stored in an array list.
    *  Especially, the array contains only the empty set if <i>k</i>=0, and
    *  only the entire set <i>s</i> if <i>k</i> = <i>n</i> where <i>n</i> is the 
    *  size of <i>s</i>. If <i>k</i> &gt; <i>n</i>, the array list is empty.
    *  Also see {@link org.mathIT.algebra.MathSet#subsets(int)} for unsorted sets.
    *  @param <T> a class implementing {@link Comparable}
    *  @param s a set
    *  @param k an integer
    *  @return a list of all <i>k</i>-element subsets of the set <i>s</i>
    *  @see org.mathIT.algebra.MathSet#subsets(int)
    */
   public static <T extends Comparable<T>> ArrayList<TreeSet<T>> subsets(SortedSet<T> s, int k) {
      final TreeSet<T> EMPTY_SET = new TreeSet<>();
      ArrayList<TreeSet<T>> subsets = new ArrayList<>();
      TreeSet<T> tmpS;
      if (k == 0) {
         subsets.add(EMPTY_SET);
      } else if (k == 1) {
         for (T i : s) {
            //tmpS = new HashSet<Integer>(1);
            tmpS = new java.util.TreeSet<>();
            tmpS.add(i);
            subsets.add(tmpS);
         }
      } else if ( k > 1 && k <= s.size() ) {
         ArrayList<TreeSet<T>> tmp;
         for (T x : s) {
            //tmpS = new HashSet<Integer>(); // Größe: n über k
            tmpS = new java.util.TreeSet<>(); // size: Größe: n über k
            for (T l : s) {
               //if (l > x) tmpS.add(l);
               if (l.compareTo(x) > 0) tmpS.add(l);
            }
            tmp = subsets(tmpS, k-1); // <- recursion
            for (int j = 0; j < tmp.size(); j++) {
               tmpS = tmp.get(j);
               //if (!tmpS.equals(EMPTY_SET)) 
               {
                  tmpS.add(x);
                  subsets.add(tmpS);
               }
            }  // for j
         } // for x
      }
      return subsets;
   }
   
   /**
    * Returns an array resulting from adding 1 to the specified array considered
    * as digits modulo <i>k</i> of a number.
    * This is very similar to adding 1 to a number in a <i>k</i>-adic number system,
    * only that also for bases <i>k</i> &gt; 9 there are used integers instead of
    * one-digit symbols.
    * If an overflow occurs, i.e., <i>x</i> is the array [<i>k</i>-1, ..., <i>k</i>-1], 
    * the null array is returned.
    * This algorithm requires a time complexity of <i>O</i>(<i>n</i>) where <i>n</i>
    * is the length of the array <i>x</i>.
    * @param x an array
    * @param k the base
    * @return addition of 1 according to the <i>k</i>-adic number system
    */
   private static int[] add1To(int[] x, int k) {
      int[] y = java.util.Arrays.copyOf(x, x.length);
      int carry = 0;
      int pos = x.length - 1;
      do {
         if (x[pos] < k-1) {
            y[pos] = x[pos] + 1;
            carry = 0;
         } else {
            y[pos] = 0;
            carry = 1;
            pos--;
         }
      } while(carry > 0 && pos >= 0);
      if (carry > 0) return null; // overflow!
      return y;
   }
   
   /**
    * Returns a list of all surjections <i>f</i>: {0, 1, ..., <i>n</i>-1} &rarr; 
    * {0, 1 ..., <i>k</i>-1}, each one of which encoded as an array such that 
    * <i>f</i>(<i>x</i>) = <code>n[x]</code> &isin; {0, 1 ..., <i>k</i>-1}.
    * I.e., the number <i>x</i> is mapped to <code>n[x]</code>.
    * A surjection <i>f</i>: <i>A</i> &rarr; <i>B</i> between two arbitrary sets
    * <i>A</i> and <i>B</i> is a mapping whose image is exactly <i>B</i>, i.e,,
    * <i>f</i>(<i>A</i>) = <i>B</i>; in other words, for every element <i>b &isin; B</i>
    * there exists a value <i>a &isin; A</i> such that <i>f</i>(<i>a</i>) = <i>b</i>.
    * For finite sets, in particular, a necessary condition for 
    * <i>f</i>: {0, 1, ..., <i>n</i>-1} &rarr; {0, 1 ..., <i>k</i>-1} to be a surjection 
    * is that <i>n</i> &ge; <i>k</i>.
    * A surjection is also often called an onto mapping.
    * This algorithm requires a time complexity of <i>O</i>(<i>kn</i><sup><i>k</i>+1</sup>).
    * @param n a positive integer specifying the domain {0, 1, ..., <i>n</i>-1}
    * @param k a positive integer specifying the range {0, 1, ..., <i>k</i>-1}
    * @return a list of all surjections <i>f</i>: {0, 1, ..., <i>n</i>-1} &rarr; 
    * {0, 1 ..., <i>k</i>-1}, each one encoded as an array
    */
   public static ArrayList<int[]> surjections(int n, int k) {
      ArrayList<int[]> surjections = new ArrayList<>();
      if (n < k) return surjections; // better InvalidArgumentException??
      int[] p = new int[n];
      for (int i = 1; i < k; i++) {
         p[n - i] = k - i;
      }
      //System.out.println("### p="+java.util.Arrays.toString(p));
      while (p != null) {
         if (isSurjective(p,k)) { // isSurjective check requires running time O(kn)
            surjections.add(p);
         }
         p = add1To(p, k);
      }
      return surjections;
   }
   
   /**
    * Returns whether the mapping <i>f</i>: {0, 1, ..., <i>n</i>-1} &rarr; {0, 1, ..., <i>k</i>-1}
    * is a surjection.
    * Here <i>f</i> is encoded as an array such that 
    * <i>f</i>(<i>x</i>) = <code>f[x]</code> &isin; {0, 1 ..., <i>k</i>-1}.
    * In general, a mapping <i>f</i>: <i>A</i> &rarr; <i>B</i> between two arbitrary sets
    * <i>A</i> and <i>B</i> is called <i>surjective</i>, or <i>onto</i>, 
    * if its image is exactly <i>B</i>, i.e,,
    * <i>f</i>(<i>A</i>) = <i>B</i>; in other words, for every element <i>b &isin; B</i>
    * there exists a value <i>a &isin; A</i> such that <i>f</i>(<i>a</i>) = <i>b</i>.
    * For finite sets, in particular, a necessary condition for 
    * <i>f</i>: {0, 1, ..., <i>n</i>-1} &rarr; {0, 1 ..., <i>k</i>-1} to be a surjection 
    * is that <i>n</i> &ge; <i>k</i>.
    * This algorithm requires a time complexity of <i>O</i>(<i>nk</i>).
    * @param f an array, encoding a mapping {0, 1, ..., <i>n</i>-1} &rarr; {0, 1, ..., <i>k</i>-1}
    * @param k a positive integer specifying the range {0, 1, ..., <i>k</i>-1}
    * @return true if and only if <i>f</i> is surjective on {0, 1, ..., <i>k</i>-1}
    */
   public static boolean isSurjective(int[] f, int k) {
      boolean isSurjective;
      for (int i = 0; i < k; i++) {
         isSurjective = false;
         for (int j = 0; j < f.length; j++) {
            if (f[j] == i) {
               isSurjective = true;
               break;
            }
         }
         if (!isSurjective) return false;
      }
      return true;
   }
   
   /** For test purposes ...*/
   /*
   public static void main(String... args) {
      long time;
      ArrayList<int[]> surjections;
      for (int n = 2; n <= 9; n += 1) {
         int k = n; //for (int k = 1; k <= n; k++) {
            time = System.currentTimeMillis();
            surjections = surjections(n,k);
            time = System.currentTimeMillis() - time;
            System.out.println(n+" -> "+ time/1000. + " sec; ("+surjections.size()+" surjections)");
         //}
      }
      System.exit(0);
      int i;
      ArrayList<Character> sequenz = new ArrayList<>(java.util.Arrays.asList(
         //new Character[] {'A', 'n', 'd', 'r', 'e', 'a', 's', 'x', 'y'}
         new Character[] {'a', 'b', 'c'}
      ));
      long zeit = System.nanoTime();
            
      ArrayList<ArrayList<Character>> perms = new ArrayList<>();
      //ArrayList<ArrayList<StringBuilder>> words = new ArrayList<>();
      for(int k=1; k < factorial(sequenz.size()).intValue(); k++) {
         perms.add(permutation(k,sequenz));
         //words.add(words(k,sequenz));
         //System.out.println(permutation(k,sequenz));
         System.out.println("k="+k+": " + words(k,sequenz));
      }
      zeit = System.nanoTime() - zeit;
      System.out.println("+++ Laufzeit "+zeit+" ns");
      //perms = null;
      // Erstellen einer Menge S = {1, 2, ..., n}:
      //int n = 3;
      //Set<Integer> s = new HashSet<Integer>(n);
      //for (int i = 1; i <= n; i++) {
      //   s.add(i);
      //}
      // ... oder von Characters:
//      HashSet<Character> s = new HashSet<Character>(java.util.Arrays.asList(
//         //new Character[] {'A', 'n', 'd', 'r', 'e', 'a', 's', 'x', 'y'}
//         new Character[] {'l', 'i', 'e', 'b'}
//      ));
      // ... oder von Strings:
      //Set<String> s = new HashSet<String>(n);
      //s.add("A"); s.add("B"); s.add("C"); s.add("abc");
      
      HashSet<Number> s = new HashSet<>(java.util.Arrays.asList(
         //new Character[] {'A', 'n', 'd', 'r', 'e', 'a', 's', 'x', 'y'}
         new Number[] {1, 3, 3.1415, java.math.BigInteger.valueOf(5)}
      ));
      // Aufruf zur Erstellung aller Permutationen von S:
      zeit = System.nanoTime();
      Number[][] p = permutations(s);
      zeit = System.nanoTime() - zeit;
      System.out.println("+++ Laufzeit "+zeit+" ns");
      System.out.println("S = " + s);
      for (i=0; i < p.length; i++) {
         System.out.print((1+i) + ") ");
         for (Number x : p[i]) {
            System.out.print(x + ", ");
         }
         System.out.println();
      }
      // Wiederholung: -----
      p = null;
      zeit = System.nanoTime();
      perms = new ArrayList<>();
      for(int k=0; k < factorial(sequenz.size()).intValue(); k++) {
         perms.add(permutation(k,sequenz));
         //System.out.println(permutation(k,sequenz));
      }
      perms = null;
      zeit = System.nanoTime() - zeit;
      System.out.println("+++ Laufzeit "+zeit+" ns");
      zeit = System.nanoTime();
      p = permutations(s);
      zeit = System.nanoTime() - zeit;
      System.out.println("+++ Laufzeit "+zeit+" ns");
      // --------------------
      
      // Ausgabe:
      String ausgabe;
      ausgabe = "=====================\nPermutationen:\n";
      for (i = 0; false && i < p.length; i++) {
         if (i < 9) ausgabe += " ";
         ausgabe += (i+1) + ": ";
         for (int j = 0; j < p[0].length - 1; j++) {
            ausgabe += p[i][j] + " - ";
         }
         if (p.length > 0 && p[0].length > 0) { 
            ausgabe += p[i][p[0].length - 1];
         }
         ausgabe += "\n";
      }
      ausgabe += "\n" + p.length + " Permutationen";
      System.out.println(ausgabe);
      
      // Aufruf zur Erstellung aller Teilmengen von S:      
      // Ausgabe:
      //String 
      ausgabe = "=====================\nTeilmengen:\n";
      int n2 = 4;
      SortedSet<Integer> menge = new TreeSet<>();
      for (i = 1; i <= n2; i++) {
         menge.add(i);
      }
      //Set<String> menge = new TreeSet<String>();
      //menge.add("A"); menge.add("B"); menge.add("C"); menge.add("abc");
      for (int k = 0; k <= 6; k++) {
         ArrayList<TreeSet<Integer>> t = subsets(menge,k);
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
