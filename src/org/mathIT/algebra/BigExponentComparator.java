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
