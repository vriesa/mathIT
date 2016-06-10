/*
 * PolynomialZ.java
 *
 * Copyright (C) 2006 Andreas de Vries
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
import static java.math.BigInteger.*;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *  This class enables to generate objects representing polynomials with integer
 *  coefficients. A polynomial with integer coefficients has the form
 *  <p style="text-align:center">
 *    <i>p</i>(<i>x</i>) 
 *    &nbsp; = &nbsp;
 *      <i>a</i><sub>0</sub>
 *    + <i>a</i><sub>1</sub> <i>x</i>
 *    + <i>a</i><sub>2</sub> <i>x</i><sup>2</sup>
 *    + &nbsp; ... &nbsp;
 *    + <i>a<sub>n</sub></i> <i>x<sup>n</sup></i>
 *  </p>
 *  where <i>a</i><sub>0</sub>, <i>a</i><sub>1</sub>, ..., <i>a<sub>n</sub></i>
 *  &#x2208; <span style="font-size:large;">&#x2124;</span>,
 *  and <i>a<sub>n</sub></i> &#x01C2; 0. 
 *  Then <i>n</i> is called the <i>degree</i> of the polynomial.
 *  Internally, a polynomial is represented by a sorted map, where the
 *  key represents the unique exponent and the value the respective coefficient,
 *  i.e., is given by the map
 *  <p style="text-align:center">
 *     [&lt;<i>n</i>,<i>a<sub>n</sub></i>&gt;,
 *     ...,
 *      &lt;0,<i>a</i><sub>0</sub>&gt;]
 *  </p>
 *  The default comparator for the exponents, i.e., the keys of this TreeMap, is
 *  {@link BigExponentComparator}, with a <i>descending</i> order.
 *  The simplest way to create a polynomial
 *  is given by the following code snippet:
 *  <pre>
 *     PolynomialZ p = new PolynomialZ();
 *     p.put( new BigInteger("1023"), new BigInteger("1") );
 *     p.put( new BigInteger("2"), new BigInteger("-3") );
 *     p.put( new BigInteger("1"), new BigInteger("5") );
 *     p.put( new BigInteger("0"), new BigInteger("-1") );
 *  </pre>
 *  Here the order of put instructions is arbitrary.
 *  This object then represents the polynomial
 *  <p style="text-align:center">
 *    <i>p</i>(<i>x</i>) 
 *    &nbsp; = &nbsp; 
 *    <i>x</i><sup>1023</sup> - 3<i>x</i><sup>2</sup> 
 *    + 5<i>x</i> - 1
 *  </p>
 *  @see Polynomial
 *  @author Andreas de Vries
 *  @version 1.1
 */
public class PolynomialZ extends TreeMap<BigInteger, BigInteger> {
   private static final long serialVersionUID = -1742369818L;
   private BigExponentComparator ec;
   
   /** Creates an empty polynomial with a new {@link BigExponentComparator}.
    */
   public PolynomialZ() {
      super(new BigExponentComparator());
      this.ec = (BigExponentComparator) this.comparator();
   }
   
   /** Creates an empty polynomial with the given {@link BigExponentComparator}.
    *  @param ec an exponent comparator
    */
   public PolynomialZ(BigExponentComparator ec) {
      super(ec);
      this.ec = ec;
   }

   /** Creates a polynomial with a the single term <i>a<sub>e</sub> x<sup>e</sup></i>
    *  and a new {@link BigExponentComparator}.
    *  @param exponent the exponent <i>e</i>
    *  @param coefficient the coefficient <i>a<sub>e</sub></i>
    */
   public PolynomialZ(BigInteger exponent, BigInteger coefficient) {
      super(new BigExponentComparator());
      this.ec = (BigExponentComparator) this.comparator();
      if (coefficient.compareTo(ZERO) != 0) {
         this.put(exponent, coefficient);
      }
   }
   
   /** Creates a polynomial with a the single term <i>a<sub>e</sub> x<sup>e</sup></i>
    *  and the given {@link BigExponentComparator}.
    *  @param exponent the exponent <i>e</i>
    *  @param coefficient the coefficient <i>a<sub>e</sub></i>
    *  @param ec the comparator to compare exponents
    */
   public PolynomialZ(BigInteger exponent, BigInteger coefficient, BigExponentComparator ec) {
      super(ec);
      this.ec = (BigExponentComparator) this.comparator();
      if (coefficient.compareTo(ZERO) != 0) {
         this.put(exponent, coefficient);
      }
   }
   
   /** Adds the term <i>a<sub>e</sub> x<sup>e</sup></i> to this
    *  polynomial. Here <i>a<sub>e</sub></i> may be negative, whereas <i>e</i> must
    *  be a non-negative integer.
    *  @param exponent the exponent <i>e</i> in the term <i>a<sub>e</sub> x<sup>e</sup></i>
    *  @param coefficient the coefficient <i>a<sub>e</sub></i> in the term 
    *  <i>a<sub>e</sub> x<sup>e</sup></i>
    *  @return the previous coefficient associated with the exponent, or null 
    *  if there does not exist a term with the exponent <i>e</i> in the polynomial.
    */
   @Override
   public BigInteger put(BigInteger exponent, BigInteger coefficient) {
      return super.put(exponent, coefficient);
   }
   
   /** Returns the sum this + <i>q</i> of this polynomial and the specified 
    *  polynomial <i>q</i>.
    *  @param q the polynomial to be added to this polynomial
    *  @return the sum this + <i>q</i>
    */
   public PolynomialZ plus(PolynomialZ q) {
      PolynomialZ p = this, r = new PolynomialZ(ec);
      Set<BigInteger> exponents = keySet();
      for (BigInteger i : exponents) {
         r.put(i,p.get(i));
      }
      
      exponents = q.keySet();
      for (BigInteger i : exponents) {
         if (p.get(i) != null) {
            if (p.get(i).equals(q.get(i).negate())) {
               r.remove(i);
            } else {
               r.put(i,p.get(i).add(q.get(i)));
            }
         } else {
            r.put(i,q.get(i));
         }
      }
      return r;
   }

   /** Returns the sum this + <i>q</i> of this polynomial and the specified 
    *  polynomial <i>q</i>, with coefficients modulo <i>m</i>.
    *  @param q the polynomial to be added to this polynomial
    *  @param m the summand
    *  @return the sum this + <i>q</i>
    */
   public PolynomialZ plus(PolynomialZ q, BigInteger m) {
      PolynomialZ p = this, r = new PolynomialZ(ec);
      BigInteger tmp;
      Set<BigInteger> exponents = keySet();
      for (BigInteger i : exponents) {
         r.put(i,p.get(i));
      }
      
      exponents = q.keySet();
      for (BigInteger i : exponents) {
         if (p.get(i) != null) {
            if ( (tmp = p.get(i).add(q.get(i)).mod(m)).signum() == 0 ) {
               r.remove(i);
            } else {
               r.put(i,tmp);
            }
         } else {
            r.put(i,q.get(i).mod(m));
         }
      }
      return r;
   }

   /** Returns the difference this - <i>q</i> of this polynomial and the specified 
    *  polynomial <i>q</i>.
    *  @param q the polynomial to be subtracted from this polynomial
    *  @return the difference this - <i>q</i>
    */
   public PolynomialZ minus(PolynomialZ q) {
      PolynomialZ p = this, r = new PolynomialZ(ec);
      
      Set<BigInteger> exponents = keySet();
      for (BigInteger i : exponents) {
         r.put(i,p.get(i));
      }
      
      exponents = q.keySet();
      for (BigInteger i : exponents) {
         if (p.get(i) != null) {
            if (p.get(i).equals(q.get(i))) {
               r.remove(i);
            } else {
               r.put(i,p.get(i).subtract(q.get(i)));
            }
         } else {
            r.put(i,q.get(i).negate());
         }
      }      
      return r;
   }

   /** Returns the difference this - <i>q</i> of this polynomial and the specified 
    *  polynomial <i>q</i>, with coefficients modula <i>m</i>.
    *  @param q the polynomial to be subtracted from this polynomial
    *  @param m the modulus
    *  @return the difference this - <i>q</i>
    */
   public PolynomialZ minus(PolynomialZ q, BigInteger m) {
      PolynomialZ p = this, r = new PolynomialZ(ec);
      BigInteger tmp;
      Set<BigInteger> exponents = keySet();
      for (BigInteger i : exponents) {
         r.put(i,p.get(i).mod(m));
      }
      
      exponents = q.keySet();
      for (BigInteger i : exponents) {
         if (p.get(i) != null) {
            if ( (tmp = p.get(i).subtract(q.get(i)).mod(m)).signum() == 0 ) {
               r.remove(i);
            } else {
               r.put(i,tmp);
            }
         } else {
            r.put(i,q.get(i).negate().mod(m));
         }
      }      
      return r;
   }
   
   /** Multiplies this polynomial with the given polynomial <i>q</i>.
    *  @param q the polynomial to be multiplied with this polynomial
    *  @return the product of this polynomial times <i>q</i>
    */
   public PolynomialZ multiply(PolynomialZ q) {
      PolynomialZ p = this, r = new PolynomialZ(ec);
      Set<BigInteger> pKeys = keySet();
      Set<BigInteger> qKeys = q.keySet();
      
      if (pKeys.isEmpty() || qKeys.isEmpty()) return r;
      
      BigInteger k, tmp;
      
      for (BigInteger i : pKeys) {
         for (BigInteger j : qKeys) {
            // r_{i+j} += p_i q_j:
            k = i.add(j);
            if (r.get(k) != null) {
               tmp = r.get(k);
            } else {
               tmp = ZERO;
            }
            tmp = tmp.add(p.get(i).multiply(q.get(j)));
            if (tmp.equals(ZERO)) {
               r.remove(k);
            } else {
               r.put(k, tmp);
            }
         }
      }
      
      return r;
   }
      
   /** Multiplies this polynomial with the given polynomial <i>q</i> modulo <i>n</i>.
    *  This means that all coefficients of the involved polynomials are
    *  computed modulo <i>n</i>.
    *  @param q the polynomial to be multiplied with this polynomial
    *  @param n the modulus
    *  @return the product of this polynomial times <i>q</i>, with coefficients 
    *  mod <i>n</i>
    */
   public PolynomialZ multiplyMod(PolynomialZ q, BigInteger n) {
      PolynomialZ p = this, r = new PolynomialZ(ec);
      Set<BigInteger> pKeys = keySet();
      Set<BigInteger> qKeys = q.keySet();
      
      if (pKeys.isEmpty() || qKeys.isEmpty()) return r;
      
      BigInteger k, tmp;
      
      for (BigInteger i : pKeys) {
         for (BigInteger j : qKeys) {
            // r_{i+j} += p_i q_j:
            k = i.add(j);
            if (r.get(k) != null) {
               tmp = r.get(k).mod(n);
            } else {
               tmp = ZERO;
            }
            tmp = tmp.add(p.get(i).multiply(q.get(j))).mod(n);
            if (tmp.equals(ZERO)) {
               r.remove(k);
            } else {
               r.put(k, tmp);
            }
         }
      }
      
      return r;
   }
   
   /** Multiplies this polynomial with the given polynomial <i>q</i> modulo <i>n</i>.
    *  This means that all coefficients of the involved polynomials are
    *  computed modulo <i>n</i>.
    *  This methods is implemented to walk through all possible powers of the two
    *  involved polynomials; in consequence it should be faster than 
    *  {@link #multiplyMod(PolynomialZ, BigInteger)} if all, or nearly all,
    *  powers have non-vanishing coefficients.
    *  @param q the polynomial to be multiplied with this polynomial
    *  @param n the modulus
    *  @return the product of this polynomial times <i>q</i>, with coefficients 
    *  mod <i>n</i>
    *  @see #multiplyMod(PolynomialZ, BigInteger)
    */
   public PolynomialZ multiplyMod2( PolynomialZ q, BigInteger n ) {
      PolynomialZ p=this, r = new PolynomialZ(ec);
      
      BigInteger i, j, k, tmp;
      BigInteger degP = p.getDegree();
      BigInteger degQ = q.getDegree();
      
      for (i = ZERO; i.compareTo(degP) <= 0; i = i.add(ONE) ) {
         for (j = ZERO; j.compareTo(degQ) <= 0; j = j.add(ONE) ) {
            // r_{i+j} += p_i q_j:
            if ( p.get(i) != null && q.get(j) != null ) {
               k = i.add(j);
               if ( r.get(k) != null ) {
                  tmp = ( r.get(k)).mod(n);
               } else {
                  tmp = ZERO;
               }
               tmp = (tmp.add(p.get(i).multiply(q.get(j)))).mod(n);
               if (tmp.compareTo(ZERO) == 0) {
                  r.remove(k);
               } else {
                  r.put( k, tmp);
               }
            }
         }
      }
      /* Notwendig???: ---
      Set<BigInteger> keys = r.keySet();
      for ( BigInteger e : keys ) {
         if ( (r.get(e)).compareTo(ZERO) == 0 ) {
            r.remove(e);
            System.err.println("### (2) coefficient = 0!! "+r+" k="+e);
         }
      }
      // -------------------- */
      return r;
   }
   
   /** Divides this polynomial by the given polynomial <i>v</i>
    *  and returns an array {q,r} holding the quotient <i>q</i> as the first 
    *  entry and the remainder <i>r</i> as the second entry.
    *  @param v the polynomial to divide this polynomial
    *  @return the array {<i>q,r</i>} where <i>q</i> is the quotient of this 
    *  polynomial, say <i>u</i>, over <i>v</i>, and <i>r</i> is the remainder 
    *  polynomial such that <i>u</i> = <i>qv</i> + <i>r</i>
    */
   public PolynomialZ[] divide( PolynomialZ v) {
      PolynomialZ u = this;
      
      PolynomialZ r = new PolynomialZ(ec), q = new PolynomialZ(ec);
      
      Set<BigInteger> keys = u.keySet();
      for ( BigInteger exponent : keys ) {
         r.put( exponent, u.get( exponent ) );
      }
      
      BigInteger k, j, tmp;
      BigInteger n  = u.getDegree();
      BigInteger nv = v.getDegree();
      
      for ( k = n.subtract( nv ); k.compareTo(ZERO) >= 0; k = k.subtract(ONE) ) {
         // q_k = r_{nv+k} / v_{nv}:
         if ( r.get( nv.add(k) ) != null ) {
            q.put( k, ( r.get( nv.add(k) ) ).divide( v.get(nv) ) );
         }
         for ( j = nv.add(k).subtract(ONE); j.compareTo(k) >= 0; j = j.subtract(ONE) ) {
            // r_j -= q_k * v_{j-k}:
            if ( q.get(k) != null && v.get(j.subtract(k)) != null ) {
               tmp = ( q.get(k)).multiply( v.get(j.subtract(k)) );
               if ( r.get(j) == null ) {
                  r.put(j, tmp.negate());
                  } else {
                     r.put(j, ( r.get(j)).subtract(tmp) );
               }
            }
         }
      }
      
      for ( j = nv; j.compareTo(n) <= 0; j = j.add(ONE) ) {
         r.remove(j);
      }
      
      /* Notwendig??? -------------------- 
      if ( r.get(ZERO) != null && (  r.get(ZERO) ).compareTo(ZERO) == 0 ) {
         r.remove(ZERO);
            System.err.println("### (4) coefficient = 0!! "+r+" k="+k);
      }
      // -------------------- */
      
      PolynomialZ[] result = {q,r};
      return result;
   }
   
   /** Returns the remainder of the division of this polynomial by the given 
    *  polynomial <i>y</i>.
    *  The algorithm is implemented after R. Crandall &amp; C. Pomerance:
    *  <i>Prime Numbers. A Computational Perspective.</i> 2<sup>nd</sup> edition.
    *  Springer, New York 2005, &sec;9.6.2
    *  @param y the polynomial to divide this polynomial
    *  @return the remainder <i>r</i>, which is the unique polynomial with coefficients
    *  mod <i>m</i> such that <i>u</i> = <i>qv</i> + <i>r</i> mod <i>m</i>,
    *  where <i>u</i> is this polynomial and <i>q</i> is the quotient <i>u/v</i>
    */
   public PolynomialZ mod(PolynomialZ y) {
      BigInteger degY = y.getDegree();
      if (degY.signum() == 0) return new PolynomialZ();
      
      BigInteger degX = getDegree();
      BigInteger d = degX.subtract(degY);
      
      if (d.signum() < 0) return new PolynomialZ();
      
      PolynomialZ X = reversal(degX);
      PolynomialZ Y = y.reversal(degY);
      PolynomialZ q = Y.reciprocal(d);
      PolynomialZ r;
      BigInteger i;
      
      d = d.add(ONE);
      q = q.multiply(X).truncate(d);
      r = X.minus(q.multiply(Y));
      i = r.index(d);
      // r(x) <- r(x) / t^i:
      if (i.signum() > 0) {
         java.util.SortedMap<BigInteger,BigInteger> tmp = r.headMap(i.subtract(ONE));
         r = new PolynomialZ();
         Set<BigInteger> keys = tmp.keySet();
         for (BigInteger j : keys) {
            r.put(j.subtract(i), tmp.get(j));
         }
      }
      
      return r.reversal(degX.subtract(i));
   }
   
   /** Returns the remainder of the division of this polynomial by the given 
    *  polynomial <i>y</i> modulo <i>m</i>.
    *  Modulo means that all coefficients of the involved polynomials are
    *  computed modulo <i>m</i>.
    *  The algorithm is implemented after R. Crandall &amp; C. Pomerance:
    *  <i>Prime Numbers. A Computational Perspective.</i> 2<sup>nd</sup> edition.
    *  Springer, New York 2005, &sec;9.6.2
    *  @param y the polynomial to divide this polynomial
    *  @param m the modulus
    *  @return the remainder <i>r</i>, which is the unique polynomial with coefficients
    *  mod <i>m</i> such that <i>u</i> = <i>qv</i> + <i>r</i> mod <i>m</i>,
    *  where <i>u</i> is this polynomial and <i>q</i> is the quotient <i>u/v</i>
    */
   public PolynomialZ mod(PolynomialZ y, BigInteger m) {
      BigInteger degY = y.getDegree();
      if (degY.signum() == 0) return new PolynomialZ();
      
      BigInteger degX = getDegree();
      BigInteger d = degX.subtract(degY);
      PolynomialZ r; // resulting polynomial
      
      if (d.signum() < 0) { // return copy of this polynomial
         r = new PolynomialZ(ec);
         Set<BigInteger> keys = keySet();
         for (BigInteger e : keys) {
            r.put(e, get(e).mod(m));
         }
         return r;
      }
      
      PolynomialZ X = reversal(degX);
      PolynomialZ Y = y.reversal(degY);
      PolynomialZ q = Y.reciprocal(d,m);
      BigInteger i;
      
      d = d.add(ONE);
      q = q.multiply(X).truncate(d);
      r = X.minus(q.multiplyMod(Y,m),m);
      i = r.index(d);
      // r(x) <- r(x) / t^i:
      if (i.signum() > 0) {
         java.util.SortedMap<BigInteger,BigInteger> tmp = r.headMap(i.subtract(ONE));
         r = new PolynomialZ();
         Set<BigInteger> keys = tmp.keySet();
         for (BigInteger j : keys) {
            r.put(j.subtract(i), tmp.get(j));
         }
      }
      
      return r.reversal(degX.subtract(i));
   }
   
   /** Divides this polynomial by the given polynomial <i>v</i> modulo <i>m</i>
    *  and returns an array {q,r} holding the quotient <i>q</i> as the first entry
    *  and the remainder <i>r</i> as the second entry.
    *  Modulo means that all coefficients of the involved polynomials are
    *  computed modulo <i>m</i>.
    *  @param v the polynomial to divide this polynomial
    *  @param m the modulus
    *  @return the array {<i>q,r</i>} where <i>q</i> is the quotient of this 
    *  polynomial, say <i>u</i>, over <i>v</i>, and <i>r</i> is the remainder 
    *  polynomial such that <i>u</i> = <i>qv</i> + <i>r</i> mod <i>m</i>
    */
   public PolynomialZ[] divideMod( PolynomialZ v, BigInteger m) {
      PolynomialZ u = this;
      
      PolynomialZ r = new PolynomialZ(ec), q = new PolynomialZ(ec);
      
      Set<BigInteger> keys = u.keySet();
      for ( BigInteger exponent : keys ) {
         r.put( exponent, u.get( exponent ) );
      }
      
      BigInteger k, j;
      BigInteger tmp;
      BigInteger n  = u.getDegree();
      BigInteger nv = v.getDegree();
      
      for ( k = n.subtract( nv ); k.compareTo(ZERO) >= 0; k = k.subtract(ONE) ) {
         // q_k = r_{nv+k} / v_{nv}:
         if ( r.get( nv.add(k) ) != null ) {
            //tmp = (( r.get( nv.add(k) ) ).divide( v.get(nv) )).mod(m);
            tmp = r.get(nv.add(k)).divide( v.get(nv) ).mod(m);
         } else {
            tmp = ZERO;
         }
         if ( tmp.compareTo(ZERO) != 0 ) q.put( k, tmp );
         //q.put( k, ( new BigDecimal((BigInteger)r.get( nv.add(k)))).divide( new BigDecimal((BigInteger)v.get(nv)), scale, BigDecimal.ROUND_HALF_UP ) );
         for ( j = nv.add(k).subtract(ONE); j.compareTo(k) >= 0; j = j.subtract(ONE) ) {
            // r_j -= q_k * v_{j-k}:
            if ( q.get(k) != null && v.get(j.subtract(k)) != null ) {
               tmp = q.get(k).multiply(v.get(j.subtract(k))).mod(m);
               if ( tmp.compareTo(ZERO) != 0 ) {
                  if ( r.get(j) == null ) {
                     //r.put(j, (ZERO.subtract(tmp)).mod(m) );
                     r.put(j, tmp.negate().mod(m) );
                  } else {
                     tmp = r.get(j).subtract(tmp).mod(m);
                     if ( tmp.compareTo(ZERO) == 0 ) {
                        r.remove(j);   
                     } else {
                        r.put(j, tmp);
                     }
                  }
               }
            }
         }
      }
      
      for ( j = nv; j.compareTo(n) <= 0; j = j.add(ONE) ) {
         r.remove(j);
      }
      
      /* Notwendig??? ------------------------------ 
      if ( r.get(ZERO) != null && r.get(ZERO).compareTo(ZERO) == 0 ) {
         r.remove(ZERO);
            System.err.println("### (6) coefficient = 0!! "+r+" k="+k);
      }
      // ----------------------------------------------*/
      
      PolynomialZ[] result = {q,r};
      return result;
   }
   
   /** Returns the polynomial <i>q<sup>e</sup></i> mod (<i>p, n</i>) 
    *  where <i>q</i> is this polynomial.
    *  @param e the exponent
    *  @param n the modulus
    *  @return <code>this</code><i><sup>e</sup></i> mod <i>n</i>
    */
   public PolynomialZ modPow(int e, BigInteger n) {
      return modPow(BigInteger.valueOf(e), getDegree().intValue() + e + 1, n);
   }
   
   /** Returns the polynomial <i>q<sup>e</sup></i> mod (<i>p, n</i>) 
    *  where <i>q</i> is this polynomial. Naive algorithm.
    *  @param p the modulus polynomial
    *  @param e the exponent
    *  @param n the modulus
    *  @return <code>this</code><i><sup>e</sup></i> mod (<i>p</i>, <i>n</i>)
    */
   public PolynomialZ modPow(PolynomialZ p, BigInteger e, BigInteger n) {
      PolynomialZ result = new PolynomialZ(ZERO, ONE, ec);
      PolynomialZ square = this;
      
      for ( int i=0; i <= e.bitLength(); i++ ) {
         if (e.testBit(i)) {
            //result = (result.multiplyMod(square, n)).divideMod(p,n)[1];
            result = (result.multiplyMod(square, n)).mod(p,n);
         }
         //square = (square.multiplyMod(square,n)).divideMod(p, n)[1];
         square = (square.multiplyMod(square,n)).mod(p, n);
      }
      return result;
   }

   /** 
    * Returns <i>s<sup>e</sup></i> mod (<i>x<sup>r</sup></i> - 1, <i>n</i>) 
    * where <i>s</i> is this polynomial.
    * @param e the exponent
    * @param r the degree of the monic polynomial <i>x<sup>r</sup></i> - 1
    * @param n the modulus
    * @return <i>s<sup>e</sup></i> mod (<i>x<sup>r</sup></i> - 1, <i>n</i>) 
    * where <i>s</i> is this polynomial
    * @throws IllegalArgumentException if <i>r</i> $lt; 0
    */
   public PolynomialZ modPow(BigInteger e, int r, BigInteger n) {
      if (r < 0) {
         throw new IllegalArgumentException("Negative polynomial degree: " + r);
      }
      /* computes s^e mod (x^r - 1, n) where s is this polynomial. */
      PolynomialZ result = new PolynomialZ(ZERO, ONE, ec);
      PolynomialZ square = this;
      BigInteger rr = BigInteger.valueOf(r);
      
//System.out.println("<br/> <h2><i>r</i> = "+r+"</h2>");
      for ( int i=0; i <= e.bitLength(); i++ ) {
         if (e.testBit(i)) {
            result = result.multiplyMod(square, rr, n);
         }
         square = square.squareMod(rr,n);
//System.out.println("<br/> "+i+") <i>p</i>(<i>x</i>) = "+result.toHTMLString()); 
      }
      return result;
   }
   
   /** 
    * Returns the polynomial <i>as</i> mod (<i>x<sup>r</sup></i> - 1, <i>n</i>) 
    * where <i>s</i> is this polynomial.
    * @param a a polynomial
    * @param r the degree of the monic polynomial <i>x<sup>r</sup></i> - 1
    * @param n the modulus
    * @return <i>as</i> mod (<i>x<sup>r</sup></i> - 1, <i>n</i>) 
    * where <i>s</i> is this polynomial.
    */
   private PolynomialZ multiplyMod(PolynomialZ a, BigInteger r, BigInteger n) {
      /* computes a * s mod (x^r - 1, n) where s is this polynomial. */
      PolynomialZ t = new PolynomialZ(ec);
      SortedMap<BigInteger,BigInteger> S = this.tailMap(r.subtract(ONE));
      SortedMap<BigInteger,BigInteger> A = a.tailMap(r.subtract(ONE));
      BigInteger k;
      BigInteger tmp;
      
      Set<BigInteger> keysS = S.keySet();
      Set<BigInteger> keysA = A.keySet();
      
      for (BigInteger i : keysS) {
         for (BigInteger j : keysA) {
            // k = (i+j) % r:
            k = i.add(j).mod(r);
            // t[k] = (t[k] + s[i] * a[j]) % n:
            tmp = S.get(i).multiply(A.get(j)).mod(n);
            if ( !tmp.equals(ZERO) ) {
               if ( t.get(k) != null ) {
                  tmp = t.get(k).add(tmp).mod(n);
               }
               if ( tmp.equals(ZERO) ) {
                  t.remove(k);
               } else {
                  t.put(k, tmp);
               }
            }
         }
      }
      return t;
   }
   
   /** 
    * Returns <i>s</i><sup>2</sup> mod (<i>x<sup>r</sup></i> - 1, <i>n</i>) 
    * where <i>s</i> is this polynomial.
    * @param r the degree of the monic polynomial <i>x<sup>r</sup></i> - 1
    * @param n the modulus
    * @return <i>s<sup>e</sup></i> mod (<i>x<sup>r</sup></i> - 1, <i>n</i>) 
    * where <i>s</i> is this polynomial.
    */
   private PolynomialZ squareMod(BigInteger r, BigInteger n) {
      /* computes s^2 mod (x^r - 1, n) where s is this polynomial.*/
      PolynomialZ t = new PolynomialZ(ec);
      BigInteger k;
      BigInteger tmp;      
      TreeSet<BigInteger> keys = new TreeSet<>(tailMap(r.subtract(ONE)).keySet());
      
      for (BigInteger i : keys) {
         for (BigInteger j : keys) {
            // k = (i+j) % r:
            k = i.add(j).mod(r);
            // t[k] = (t[k] + s[i] * a[j]) % n:
            tmp = get(i).multiply(get(j)).mod(n);
            if ( !tmp.equals(ZERO) ) {
               if ( t.get(k) != null ) {
                  tmp = t.get(k).add(tmp).mod(n);
               }
               if ( tmp.equals(ZERO) ) {
                  t.remove(k);
               } else {
                  t.put(k, tmp);
               }
            }
         }
      }
      return t;
   }

   /** 
    * Evaluates this polynomial at the point <i>x</i>. 
    * The algorithm is naive and does not use the Horner scheme.
    * @param x the argument value to be evaluated
    * @return the value of <i>s</i>(<i>x</i>) mod <i>n</i>, 
    * where <i>s</i> is this polynomial
    */
   public BigInteger evaluate(BigInteger x) {
      BigInteger y = ZERO;
      Set<BigInteger> keys = this.keySet();
      for( BigInteger exponent : keys ) {
         y = y.add( x.pow(exponent.intValue()).multiply(get(exponent)) );
      }
      return y;
   }
   
   /** 
    * Evaluates this polynomial at the point <i>x</i> modulo <i>n</i>. 
    * The algorithm is naive and does not use the Horner scheme.
    * @param x the argument value to be evaluated
    * @param n the modulus
    * @return the value of <i>s</i>(<i>x</i>) mod <i>n</i>, where <i>s</i> is this polynomial
    */
   public BigInteger evaluateMod(BigInteger x, BigInteger n) {
      BigInteger y = ZERO;
      Set<BigInteger> keys = this.keySet();
      for( BigInteger exponent : keys ) {
         y = y.add( x.modPow(exponent, n).multiply(this.get(exponent)) ).mod(n);
      }
      return y;
   }
   
   /** Returns the polynomial index of this polynomial by the specified degree <i>d</i>.
    *  The algorithm is implemented from R. Crandall &amp; C. Pomerance:
    *  <i>Prime Numbers. A Computational Perspective.</i> 2<sup>nd</sup> edition.
    *  Springer, New York 2005.
    *  @param d the degree of the reversal.
    *  @return the reversal of this polynomial by degree <i>d</i>
    */
   private BigInteger index(BigInteger d) {
      if (get(d) != null) return d;
      
      SortedMap<BigInteger,BigInteger> p = headMap(d);

      if (p.isEmpty()) return ZERO;
      return p.lastKey();
   }
   
   /** Returns the reversal of this polynomial by the specified degree <i>d</i>.
    *  The algorithm is implemented from R. Crandall &amp; C. Pomerance:
    *  <i>Prime Numbers. A Computational Perspective.</i> 2<sup>nd</sup> edition.
    *  Springer, New York 2005.
    *  @param d the degree of the reversal.
    *  @return the reversal of this polynomial by degree <i>d</i>
    */
   private PolynomialZ reversal(BigInteger d) {
      PolynomialZ r = new PolynomialZ(ec);
      
      TreeSet<BigInteger> exponents = new TreeSet<>(keySet());
      for (BigInteger j : exponents) {
         if (d.compareTo(j) < 0 ) continue;
         // a_{d-j}  <-> a_j
         r.put(d.subtract(j),get(j));
      }
      return r;
   }
   
   /** Returns this polynomial mod x^n,*/
   private PolynomialZ truncate(BigInteger n) {
      PolynomialZ p = new PolynomialZ();
      Set<BigInteger> keys = (new TreeSet<>(keySet())).headSet(n);
      for (BigInteger j : keys) {
         if (j.compareTo(n) >= 0) break;
         p.put(j,get(j));
      }
      return p;
   }
   
   /** Returns the truncated reciprocal of this polynomial through the specified 
    *  degree. The method requires that the first coefficient <i>a</i><sub>0</sub>
    *  of this polynomial satisfies <i>a</i><sub>0</sub> = 1.
    *  The algorithm is implemented from R. Crandall &amp; C. Pomerance:
    *  <i>Prime Numbers. A Computational Perspective.</i> 2<sup>nd</sup> edition.
    *  Springer, New York 2005.
    *  @param degree the degree to which the reciprocal is truncated
    *  @return the truncated reciprocal of this polynomial through the specified degree
    */
    private PolynomialZ reciprocal(BigInteger degree) {
       // This method requires that a_0 = 1! This constraint will not be checked ...
       
       PolynomialZ h;  // auxiliary polynomial
       PolynomialZ two = new PolynomialZ(ZERO,org.mathIT.numbers.BigNumbers.TWO);
       
       // 1. Initialize:
       PolynomialZ g = new PolynomialZ(ZERO, ONE);
       BigInteger n = ONE;  // working degree precision
       BigInteger d = degree.add(ONE);
       
       // 2. Newton loop:
       while (n.compareTo(d) < 0) {
          n = n.add(n);  // double the working degree precision
          if (n.compareTo(d) > 0)  n = d;
          
          // h(x) = this(x) mod x^n, i.e. simple truncation:
          h = truncate(n).multiply(g).truncate(n);
          // g(x) = g(x) (2-h(x)) mod x^n
          g = g.multiply(two.minus(h)).truncate(n);
       }
       
       return g;
    }

   /** Returns the truncated reciprocal of this polynomial through the specified 
    *  degree, with coefficients modulo the number <i>m</i>.
    *  The method requires that the first coefficient <i>a</i><sub>0</sub> of 
    *  this polynomial satisfies <i>a</i><sub>0</sub> = 1.
    *  The algorithm is implemented from R. Crandall &amp; C. Pomerance:
    *  <i>Prime Numbers. A Computational Perspective.</i> 2<sup>nd</sup> edition.
    *  Springer, New York 2005.
    *  @param degree the degree to which the reciprocal is truncated
    *  @param m the modulus
    *  @return the truncated reciprocal of this polynomial through the specified degree
    */
    private PolynomialZ reciprocal(BigInteger degree, BigInteger m) {
       // This method requires that a_0 = 1! This constraint will not be checked ...
       
       PolynomialZ h;  // auxiliary polynomial
       PolynomialZ two = new PolynomialZ(ZERO,org.mathIT.numbers.BigNumbers.TWO);
       
       // 1. Initialize:
       PolynomialZ g = new PolynomialZ(ZERO, ONE);
       BigInteger n = ONE;  // working degree precision
       BigInteger d = degree.add(ONE);
       
       // 2. Newton loop:
       while (n.compareTo(d) < 0) {
          n = n.add(n);  // double the working degree precision
          if (n.compareTo(d) > 0)  n = d;
          
          // h(x) = this(x) mod x^n, i.e. simple truncation:
          h = truncate(n).multiplyMod(g,m).truncate(n);
          // g(x) = g(x) (2-h(x)) mod x^n
          g = g.multiply(two.minus(h,m)).truncate(n);
       }
       
       return g;
    }

   /** Returns the degree of this polynomial.
    *  The degree is defined as the maximum exponent of the polynomial.
    *  Since this polynomial is sorted with respect to the exponents in
    *  descending order, the first key of this map is the degree.
    *  By definition, an empty polynomial has degree zero.
    *  @return the degree of this polynomial
    */
   public BigInteger getDegree() {
      //java.util.Iterator<BigInteger> e = this.keySet().iterator(); 
      //return e.hasNext() ?  e.next() : ZERO;
      Set<BigInteger> keys = keySet();
      for (BigInteger e : keys) {
         return e;
      }
      return ZERO;
   }
   
   /** Returns a binary string representation of this polynomial, where the 
    *  <i>j</i>-the bit (counted from the right) is zero iff the coefficient
    *  of <i>x<sup>j</sup></i> is zero.
    *  This repesentation is equivalent to this polynomial if the
    * coefficients are all 0 or 1.
    *  @return a binary string representation of this polynomial
    */
   public String toBinaryString() {
      String out = "";
      for (BigInteger i = getDegree(); i.compareTo(ZERO) >= 0; i = i.subtract(ONE)) {
         out += get(i) == null ? "0" : "1";
      }
      return out;
   }

   
   /** Returns a HTML string representation of this polynomial.
    *  @return a HTML string representation of this polynomial
    */
   public String toHTMLString() {
      if (isEmpty()) return "0";
      String output = "";
      BigInteger coefficient;
      boolean start = true;
      Set<BigInteger> keys = this.keySet();
      for ( BigInteger exponent : keys ) {
         coefficient =  this.get( exponent );
         if ( coefficient.signum() > 0 && !start ) { 
            output += " + ";
         } else if ( coefficient.signum() < 0 ) { 
            output += " - ";
         }
         if ( (coefficient.abs()).compareTo(ONE) != 0 || exponent.compareTo(ZERO) == 0 ) { 
            output += coefficient.abs().toString() + " ";
         }
         if ( exponent.compareTo(ONE) > 0 ) {
            output += "<i>x</i><sup>" + exponent.toString() + "</sup>";
         } else if ( exponent.compareTo(ONE) == 0 ) {
            output += "<i>x</i>";
         }
         start = false;
      }
      return output;
   }
   
   /** Returns a string representation of this polynomial.
    *  @return a string representation of this polynomial
    */
   @Override
   public String toString() {
      if (isEmpty()) return "0";
      String output = "";
      BigInteger coefficient;
      boolean start = true;
      Set<BigInteger> keys = this.keySet();
      for ( BigInteger exponent : keys ) {
         coefficient =  this.get( exponent );
         if ( coefficient.signum() > 0 && !start ) { 
            output += " + ";
         } else if ( coefficient.signum() < 0 ) { 
            output += " - ";
         }
         if ( (coefficient.abs()).compareTo(ONE) != 0 || exponent.compareTo(ZERO) == 0 ) { 
            output += coefficient.abs().toString();
            if( !exponent.equals(ZERO) ) {
               output += " ";
            }
         }
         if ( exponent.compareTo(ONE) > 0 ) {
            output += "x^" + exponent.toString();
         } else if ( exponent.compareTo(ONE) == 0 ) {
            output += "x";
         }
         start = false;
      }
      return output;
   }
   
   /* --------------------------------------------------------------------------
   public static void main( String[] args ) {
      PolynomialZ p = new PolynomialZ();
      //System.out.println( "leer: P(x) = " + p );
      //--- P(x) = 5x^3 + 2x + 7: -------------------------
      //p.put( new BigInteger("0"), new BigInteger("1") );
      //p.put( new BigInteger("1"), new BigInteger("2") );
      //p.put( new BigInteger("2"), new BigInteger("3") );
      //p.put( new BigInteger("3"), new BigInteger("6") );
      //p.put( new BigInteger("5"), new BigInteger("9") );
      //p.put( new BigInteger("6"), new BigInteger("1") );
      //BigInteger d = new BigInteger("3");
      //System.out.println("reciprocal("+p+","+d+") = "+p.reciprocal(d));
      //p = new PolynomialZ(); 
      //d = new BigInteger("2");
      //p.put( new BigInteger("0"), new BigInteger("1") );
      //p.put( new BigInteger("2"), new BigInteger("3") );
      //p.put( new BigInteger("3"), new BigInteger("6") );
      //System.out.println("ind("+p+","+d+") = "+p.index(d));
      //--- P(x) = x^2 - 1: -------------------------------
      //p.put( new BigInteger("0"), new BigInteger("-1") );
      //p.put( new BigInteger("2"), new BigInteger("1") );
      //--- P(x) = x^3 - 3 x^2 + 3x - 1: ------------------
      //p.put( new BigInteger("3"), new BigInteger("1") );
      //p.put( new BigInteger("2"), new BigInteger("-3") );
      //p.put( new BigInteger("1"), new BigInteger("3") );
      //p.put( new BigInteger("0"), new BigInteger("-1") );
      //--- P(x) = x^6 + x^4 + x^2 + x + 1: ------------------
      //p.put( new BigInteger("6"), new BigInteger("1") );
      //p.put( new BigInteger("4"), new BigInteger("1") );
      //p.put( new BigInteger("2"), new BigInteger("1") );
      //p.put( new BigInteger("1"), new BigInteger("1") );
      //p.put( new BigInteger("0"), new BigInteger("1") );
      //--- P(x) = x + 1: ------------------
      p.put( new BigInteger("1"), new BigInteger("1") );
      p.put( new BigInteger("0"), new BigInteger("1") );
      //---------------------------------------------------
      System.out.println( "P(x) = " + p +", degree="+p.getDegree());
      //javax.swing.JOptionPane.showMessageDialog(null, "<html>P(x) = " + p.toHTMLString() +", deg(P)="+p.getDegree());
      int r = 3; BigInteger n = new BigInteger("13");
      long zeit = System.nanoTime();
      p.multiply(p);
      zeit = System.nanoTime() - zeit;
      System.out.println("neu: "+zeit+" ns");
      //zeit = System.nanoTime();
      //p.multiply2(p);
      //zeit = System.nanoTime() - zeit;
      //System.out.println("alt: "+zeit+" ns");
      //System.out.println("P^2(x)\n = " + p.multiply(p) + "\n = " + p.multiply2(p));
      //System.out.println("P^2(x)\n = " + p.multiplyMod(p,r,n) + "\n = " + p.multiplyMod2(p,r,n));
      
      r = 3;
      n = new BigInteger("561");
      //n = new BigInteger("338");
      System.out.println("P^" + n + "(x) mod (x^" + r + " - 1, " + n +")\n = " + p.modPow(n,r,n));
      
      System.exit(0);
      PolynomialZ q = new PolynomialZ();
      //--- Q(x) = 8x^2 + 3x: -----------------------------
      //q.put( new BigInteger("1"), new BigInteger("3") );
      //q.put( new BigInteger("2"), new BigInteger("8") );
      //--- Q(x) = x + 1: ---------------------------------
      //q.put( new BigInteger("0"), new BigInteger("1") );
      //q.put( new BigInteger("1"), new BigInteger("1") );
      //--- Q(x) = x - 1: ---------------------------------
      //q.put( new BigInteger("0"), new BigInteger("-1") );
      //q.put( new BigInteger("1"), new BigInteger("1") );
      //--- Q(x) = x^n + 1: ---------------------------------
      q.put( new BigInteger("3"), new BigInteger("1") );
      q.put( new BigInteger("0"), new BigInteger("-1") );
      //---------------------------------------------------
      System.out.println( "Q(x) = " + q + ", degree="+q.getDegree());
      System.out.println("P^" + n + "(x) mod " + n +"\n = " + p.modPow(n.intValue(),n));
      //System.out.println("P^" + n + "(x) mod (" + q + ", " + n +")\n = " + p.modPow(q,n,n));
      //System.out.println("<i>P</i><sup>" + n + "</sup>(<i>x</i>) mod " + n +" = <br>" + p.modPow(n.intValue(),n).toHTMLString());
      //System.out.println("<i>P</i><sup>" + n + "</sup>(<i>x</i>) mod (" + n +", " + r + ") = " + p.modPow(n,r,n).toHTMLString());
      //javax.swing.JOptionPane.showMessageDialog(null, "<html><i>P</i><sup>" + n + "</sup>(<i>x</i>) mod " + n +" = <br>" + p.modPow(n.intValue(),n).toHTMLString());
      
      System.exit(0);
      
      n = new BigInteger("341");
      BigInteger max = new BigInteger("341");
      //System.out.println("(341 over 11) = " + org.mathIT.numbers.Numbers.exactBinomial(341,11));
      //System.out.println("(341 over 31) = " + org.mathIT.numbers.Numbers.exactBinomial(341,31));
      //System.out.println("(341 over 11) mod 341 = " + org.mathIT.numbers.Numbers.exactBinomial(341,310).mod(n));
      //System.out.println("(341 over 31) mod 341 = " + org.mathIT.numbers.Numbers.exactBinomial(341,31).mod(n));
      for (n = new BigInteger("338"); n.compareTo(max) <= 0; n = n.add(ONE)) {
         System.out.println("P^" + n + "(x) mod (x^" + r + " - 1, " + n +") = " + p.modPow(n,r,n));
      }
      System.exit(0);
      
      System.out.println( "P(x)Q(x) = " + p.multiply(q));
      PolynomialZ v = new PolynomialZ();
      //--- M(x) = x^8 + x^4 + x^3 + x + 1: ---------------
      v.put( new BigInteger("21"), new BigInteger("1") );
      v.put( new BigInteger("28"), new BigInteger("1") );
      v.put( new BigInteger("12"), new BigInteger("1") );
      v.put( new BigInteger("3"), new BigInteger("1") );
      v.put( new BigInteger("1"), new BigInteger("1") );
      v.put( new BigInteger("14"), new BigInteger("1") );
      v.put( new BigInteger("2"), new BigInteger("1") );
      //java.util.Collections.max(v);
      System.out.println("V(x) = "+v+", degree="+v.getDegree());

      PolynomialZ[] division = p.divideMod(q, org.mathIT.numbers.BigNumbers.TWO);
      String output = "P(x) = (" + division[0] + ") Q(x)";
      String remainder = division[1].toString();
      if ( remainder != "" ) output += " + " + division[1];
      System.out.println( output );
      
      //System.out.println("P(x) P(x) = " + p.multiplyMod(p,TWO));
      //System.out.println("P(x) P(x) = " + (p.multiplyMod(p,TWO)).mod(q, TWO) + " mod ("+q+", 2)");

      PolynomialZ exponential = p.modPow(q, org.mathIT.numbers.BigNumbers.TWO, org.mathIT.numbers.BigNumbers.TWO);
      //PolynomialZ exponential = p.modPow( q, new BigInteger("5"), new BigInteger("7") );
      System.out.println("P(x)^e = " + exponential + " mod (Q(x), n)");
   }
   // ----------------------------------------------------------------------- */
}
