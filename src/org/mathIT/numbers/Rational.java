/*
 * Rational.java
 *
 * Copyright (C) 2007-2012 Andreas de Vries
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
import java.math.BigInteger;
import static java.math.BigInteger.*;
import static org.mathIT.numbers.BigNumbers.*;
/**
 * This class enables the creation of a rational number
 * <i>n</i> &#8712; <span style="font-size:large;">&#x211A;</span>
 * and the implementation of mathematical functions operating
 * on them. A rational number 
 * <i>n</i> &#8712; <span style="font-size:large;">&#x211A;</span>
 * is uniquely determined by <i>n</i> = <i>p</i>/<i>q</i> where
 * the numerator <i>p</i> and the denominator <i>q</i> 
 * are relatively prime integers.
 * Internally, a rational number <i>n</i> is internally represented by two
 * {@link BigInteger BigIntegers}.
 * @author  Andreas de Vries
 * @version 1.1
 */
public class Rational {
   //private static final long serialVersionUID = 491437390;
   /** The numerator of this rational number.*/
   private BigInteger p;
   /** The denominator of this rational number.*/
   private BigInteger q;
   
   /** Creates a rational number <i>p/q</i> with the numerator <i>p</i> and the 
    *  denominator <i>q</i>. <i>p</i> and <i>q</i> need not be relatively prime,
    *  the contructor cancels common divisors automatically.
    *  Moreover, it is guaranteed <i>q</i> &gt; 0.
    *  If <i>q</i> = 0, then it is set <i>p</i> = {@link BigInteger#ZERO 0} 
    *  and <i>q</i> = {@link BigInteger#ONE 1}.
    *  @param p the numerator
    *  @param q the denominator
    */
   public Rational(long p, long q) {
      if ( p == 0 || q == 0 ) {
         this.p = BigInteger.ZERO;
         this.q = BigInteger.ONE;
      } else {
         BigInteger[] cancelled = cancel(BigInteger.valueOf(p), BigInteger.valueOf(q));
         this.p = cancelled[0];
         this.q = cancelled[1];
      }
   }

   /** Creates a rational number <i>p/q</i> with the numerator <i>p</i> and the 
    *  denominator <i>q</i>. <i>p</i> and <i>q</i> need not be relatively prime,
    *  the contructor cancels common divisors automatically.
    *  Moreover, it is guaranteed <i>q</i> &gt; 0.
    *  If <i>q</i> = 0, then it is set <i>p</i> = {@link BigInteger#ZERO 0} 
    *  and <i>q</i> = {@link BigInteger#ONE 1}.
    *  @param p the numerator
    *  @param q the denominator
    */
   public Rational(BigInteger p, BigInteger q) {
      if ( p.equals(ZERO) || q.equals(ZERO) ) {
         this.p = ZERO;
         this.q = ONE;
      } else {
         BigInteger[] cancelled = cancel(p,q);
         this.p = cancelled[0];
         this.q = cancelled[1];
      }
   }
      
   /** Returns the rational number <i>k</i> = <i>m + n</i> where
    *  <i>m</i> is this rational number.
    *  @param n the summand
    *  @return the rational number representing <code>this</code> + <i>n</i>
    *  @see #plus(Rational)
    */
   public Rational add( Rational n ) {
      return plus(n);
   }

   /** Returns a two-element array representing the fraction <i>p</i>/<i>q</i>
    *  where all common factors of numerator and denominator are cancelled.
    *  Here a fraction is represented by a two-element array whose first entry is the numerator
    *  and whose second entry is the denominator.
    *  @param p the numerator
    *  @param q the denominator
    *  @return the array {<i>p', q'</i>} where <i>p'</i> and <i>q'</i> emerge from
    *  <i>p</i> and <i>q</i> by cancelling all common factors
    */
   public static BigInteger[] cancel( BigInteger p, BigInteger q ) {
      if ( q.signum() < 0 ) {
         p = p.negate();
         q = q.negate();
      }      

      BigInteger gcd = p.gcd(q);
      p = p.divide(gcd);
      q = q.divide(gcd);
      return new BigInteger[]{p,q};
   }
   
   /** Returns the rational number <i>k</i> = <i>m</i>/<i>n</i> where
    *  <i>m</i> is this rational number.
    *  @param n the divisor
    *  @return the rational number representing <code>this</code> / <i>n</i>
    *  @see #divide(BigInteger)
    *  @see #divide(long)
    */
   public Rational divide( Rational n ) {
      BigInteger[][] factor = {cancel(p,n.p), cancel(n.q,q)};
      return new Rational(factor[0][0].multiply(factor[1][0]), factor[0][1].multiply(factor[1][1]));
   }

   /** Returns the rational number <i>k</i> = <i>m</i>/<i>n</i> where
    *  <i>m</i> is this rational number.
    *  @param n the divisor
    *  @return the rational number representing <code>this</code> / <i>n</i>
    *  @see #divide(Rational)
    *  @see #divide(long)
    */
   public Rational divide( BigInteger n ) {
      BigInteger[] factor = cancel(p,n);
      return new Rational(factor[0], q.multiply(factor[1]));
   }

   /** Returns the rational number <i>k</i> = <i>m</i>/<i>n</i> where
    *  <i>m</i> is this rational number.
    *  @param n the divisor
    *  @return the rational number representing <code>this</code> / <i>n</i>
    *  @see #divide(Rational)
    *  @see #divide(BigInteger)
    */
   public Rational divide( long n ) {
      return divide(BigInteger.valueOf(n));
   }

   /** Returns the rational number <i>k</i> = <i>m - n</i> where
    *  <i>m</i> is this rational number.
    *  @param n the subtrahend
    *  @return the rational number representing <code>this</code> - <i>n</i>
    */
   public Rational minus( Rational n ) {
      BigInteger denominator = lcm(this.q, n.q);
      BigInteger numerator = p.multiply(denominator).divide(q).subtract(n.p.multiply(denominator).divide(n.q));
      return new Rational(numerator, denominator);
   }

   /** Returns the rational number <i>k</i> = <i>m</i><i>n</i> where
    *  <i>m</i> is this rational number.
    *  @param n the factor
    *  @return the rational number representing <code>this</code> * <i>n</i>
    */
   public Rational multiply( Rational n ) {
      BigInteger[][] factor = {cancel(p,n.q), cancel(n.p,q)};
      return new Rational(factor[0][0].multiply(factor[1][0]), factor[0][1].multiply(factor[1][1]));
   }

   /** Returns the rational number <i>k</i> = <i>m</i><i>n</i> where
    *  <i>m</i> is this rational number.
    *  @param n the factor
    *  @return the rational number representing <code>this</code> * <i>n</i>
    */
   public Rational multiply( BigInteger n ) {
      BigInteger[] factor = cancel(n,q);
      return new Rational(p.multiply(factor[0]), factor[1]);
   }

   /** Returns the negative of this rational number.
    *  @return -<code>this</code>
    */
   public Rational negate() {
      return new Rational(p.negate(),q);
   }

   /** Returns the rational number <i>k</i> = <i>m</i> + <i>n</i> where
    *  <i>m</i> is this rational number.
    *  @param n the summand
    *  @return the rational number representing <code>this</code> + <i>n</i>
    */
   public Rational plus( Rational n ) {
      BigInteger denominator = lcm(this.q, n.q);
      BigInteger numerator = p.multiply(denominator).divide(q).add(n.p.multiply(denominator).divide(n.q));
      return new Rational(numerator, denominator);
   }
   
   /** Returns the reciprocal 1/<i>n</i> of this rational <i>n</i>.
    *  @return the reciprocal of this rational
    */
   public Rational reciprocal() {
      return new Rational(q,p);
   }

   /** Returns the rational number <i>k</i> = <i>m</i> - <i>n</i> where
    *  <i>m</i> is this rational number.
    *  @param n the subtrahend
    *  @return the rational number representing <code>this</code> - <i>n</i>
    *  @see #minus(Rational)
    */
   public Rational subtract( Rational n ) {
      return minus(n);
   }

   /** Returns a string representation of this rational number.
    *  @return a string representation of this rational number
    */
   @Override
   public String toString() {
      String output = "";
      if ( p.signum() < 0 ) {
         output += "(";
      }
      output += p + "/" + q;
      if ( p.signum() < 0 ) {
         output += ")";
      }
      return output;
   }
   
   /** For test purposes...*/
   /*
   public static void main( String[] args ) {
      //Rational n = new Rational(99, 363);
      Rational m = new Rational( new BigInteger("990"), new BigInteger("363"));
      Rational n = new Rational( new BigInteger("24"), new BigInteger("-60"));
      System.out.println(m + " + " + n + " = " + m.plus(n));
      System.out.println(m + " - " + n + " = " + m.minus(n));
      System.out.println(m + " * " + n + " = " + m.multiply(n));
      System.out.println(m + " / " + n + " = " + m.divide(n));
      m = new Rational( new BigInteger("2"), new BigInteger("-13"));
      n = new Rational( new BigInteger("15"), new BigInteger("13"));
      System.out.println(m + " + " + n + " = " + m.plus(n));
      System.out.println("1/"+m+" = " + m.reciprocal());
   }
   // */
}
