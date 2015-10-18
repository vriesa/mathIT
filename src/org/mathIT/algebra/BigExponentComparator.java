/*
 * BigExponentComparator.java
 *
 * Copyright (C) 2005 Andreas de Vries
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
package org.mathIT.algebra;
import java.math.BigInteger;
/**
 *  This class implements a comparator to enable descending order with respect to 
 *  the exponents of a polynomial created by the class
 *  {@link PolynomialZ}.
 *  @author  Andreas de Vries
 *  @version 1.3
 */
public class BigExponentComparator implements java.util.Comparator<BigInteger> {
   /** Compares its two arguments for order, which is reverse than the usual order. 
    *  Returns a negative integer, zero, 
    *  or a positive integer as the first argument is greater than, equal to, or 
    *  less than the second.
    *  @param x the first number to be compared
    *  @param y the second number to be compared
    *  @return -1 if <code>x&gt;y</code>, 0 if <code>x==y</code>, or 1 if <code>x&lt;y</code>
    */
   @Override
   public int compare(BigInteger x, BigInteger y) {
      //reverse order:
      return - x.compareTo(y);
   }
}
