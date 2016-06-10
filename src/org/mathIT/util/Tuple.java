/*
 * Tuple.java
 *
 * Copyleft (C) 2012 Andreas de Vries
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
package org.mathIT.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * An implementation of <code>Collection</code> that stores
 * <i>n</i> non-null objects and is not mutable.
 * Tuples respect <code>equals</code> in the sense that two tuples are
 * equal if they contain equal objects.
 * Thus in partucular they may be used as indices or keys in maps.<p>
 * Note that they do not protect from malevolent behavior: if one or another
 * object in the tuple is mutable, then it can be changed.
 * @author Andreas de Vries
 * @version 1.0
 * @param <T> type of stored elements
 */
//@SuppressWarnings("serial")
public final class Tuple<T> implements Collection<T>, Serializable {
   /** Version ID for serialization. */
   private static final long serialVersionUID = 9223372036773603415L; // = Long.MAX_VALUE - Tuple.hashCode()
   /** List of elements of this tuple.*/
   private ArrayList<T> elements;

   /**
    * Creates a
    * <code>Tuple</code> from the specified elements.
    *
    * @param elements the elements in the <code>Tuple</code>
    * @throws IllegalArgumentException if either argument is null or there are less than two elements
    */
   public Tuple(T[] elements) {
      if (elements.length < 2) throw new IllegalArgumentException("Tuple must contain at least 2 elements");
         
      boolean ok = true;
      for (int i=0; ok && i<elements.length; i++) {
         ok &= elements[i] != null;
      }
      if (!ok) {
         throw new IllegalArgumentException("Tuple must not contain null values");
      }
      java.util.Collections.addAll(this.elements, elements);
   }

   /**
    * Creates a Tuple from the passed Collection.
    * @param elements the elements of the new <code>Tuple</code>
    * @throws IllegalArgumentException if the input collection is null, contains
    * null values, or has &lt; 2 elements.
    */
   public Tuple(Collection<? extends T> elements) {
      if (elements == null) {
         throw new IllegalArgumentException("Input collection cannot be null");
      }
      
      if (elements.size() < 2) throw new IllegalArgumentException("Tuple must contain at least 2 elements");
      
      if (elements.contains(null)) {
         throw new IllegalArgumentException("Tuple must not contain null values");
      }
      this.elements = new ArrayList<>(elements);
   }

   /**
    * Creates a
    * <code>Tuple</code> from the specified elements.
    *
    * @param first the first element in the <code>Tuple</code>
    * @param second the second element in the <code>Tuple</code>
    * @throws IllegalArgumentException if either argument is null
    */
   public Tuple(T first, T second) {
      if (first == null || second == null) {
         throw new IllegalArgumentException("Tuple must not contain null values");
      }
      elements = new ArrayList<>(2);
      elements.add(first);
      elements.add(second);
   }

   /**
    * Returns the first element of this tuple.
    * @return the first element of this tuple.
    */
   public T getFirst() {
      return elements.get(0);
   }

   /**
    * Returns the second element of this tuple.
    * @return the second element of this tuple.
    */
   public T getSecond() {
      return elements.get(1);
   }

   @SuppressWarnings("rawtypes")
	@Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (o instanceof Tuple) {
         Tuple otherTuple = (Tuple) o;
         if (elements.size() != otherTuple.elements.size()) return false;
         boolean result = true;
         for (int i = 0; result && i < elements.size(); i++) {
            result &= this.elements.get(i).equals(otherTuple.elements.get(i));
         }
         return result;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int hashCode = 1;
      for (T element : elements) {
         hashCode = 31 * hashCode + element.hashCode();
      }
      return hashCode;
   }

   @Override
   public String toString() {
      return elements.toString();
   }

   /**
    * Since a tuple is not mutable, this method should not be invoked.
    * @param o an object
    * @return true if and only if the operation was successful
    * @throws UnsupportedOperationException if this method is invoked
    */
   @Override
   public boolean add(T o) {
      throw new UnsupportedOperationException("Tuples cannot be mutated");
   }

   /**
    * Since a tuple is not mutable, this method should not be invoked.
    * @param c an object
    * @return true if and only if the operation was successful
    * @throws UnsupportedOperationException if this method is invoked
    */
   @Override
   public boolean addAll(Collection<? extends T> c) {
      throw new UnsupportedOperationException("Tuples cannot be mutated");
   }

   /**
    * Since a tuple is not mutable, this method should not be invoked.
    * @throws UnsupportedOperationException if this method is invoked
    */
   @Override
   public void clear() {
      throw new UnsupportedOperationException("Tuples cannot be mutated");
   }

   @Override
   public boolean contains(Object o) {
      return elements.contains(o);
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return elements.containsAll(c);
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   public Iterator<T> iterator() {
      return elements.iterator();
   }

   @Override
   public boolean remove(Object o) {
      throw new UnsupportedOperationException("Tuples cannot be mutated");
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException("Tuples cannot be mutated");
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException("Tuples cannot be mutated");
   }

   @Override
   public int size() {
      return elements.size();
   }

   @Override
   public Object[] toArray() {
      return elements.toArray();
   }

   @Override
   public <S> S[] toArray(S[] a) {
      return elements.toArray(a);
   }
}
