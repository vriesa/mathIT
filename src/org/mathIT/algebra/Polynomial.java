/*
 * Polynomial.java - Class providing number theoretic functions and mathematical constants
 *
 * Copyright (C) 2006 Andreas de Vries
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
import java.util.Set;
import java.util.TreeMap;

/**
 *  This class enables to generate objects representing polynomials with real coefficients.
 *  A polynomial with real coefficients has the form
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
 *  &#x2208; <span style="font-size:large;">&#x211D;</span>,
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
 *  {@link ExponentComparator}, with a <i>descending</i> order.
 *  The simplest way to create a polynomial
 *  is given by the following code snippet:
 *  <pre>
 *     Polynomial p = new Polynomial();
 *     p.put(1023, 1.0);
 *     p.put(2, 3.5);
 *     p.put(1, 3.);
 *     p.put(0, -1.);
 *  </pre>
 *  Here the order of put instructions is arbitrary.
 *  This object then represents the polynomial
 *  <p style="text-align:center">
 *    <i>p</i>(<i>x</i>) 
 *    &nbsp; = &nbsp; 
 *    <i>x</i><sup>1023</sup> - 3.5<i>x</i><sup>2</sup> 
 *    + 3<i>x</i> - 1
 *  </p>
 *  Additionally, this class provides some easy to use and comparably fast 
 *  static methods for polynomial operations where a polynomial is represented 
 *  by an array of double values.
 *  The principle of this representation is as follows: The element at index <i>i</i> 
 *  of the array denotes the coefficient <i>a<sub>i</sub></i> of the polynomial,
 *  and the length of the array is &gt;= the degree of the polynomial.
 *  If the degree of the involved polynomials is not too large, these
 *  method are fast.
 *  @see PolynomialZ
 *  @author Andreas de Vries
 *  @version 1.1
 */
public class Polynomial extends TreeMap<Integer,Double> {
   private static final long serialVersionUID = 913625844L;
   private static final double ACCURACY = 1e-10;
   private static java.text.DecimalFormat outputFormat = new java.text.DecimalFormat( "#,##0.##########" );
   private static final Integer ZERO = Integer.valueOf(0);
   private ExponentComparator ec;
   
   /** Creates an empty polynomial with a new {@link ExponentComparator}.
    */
   public Polynomial(){
      super(new ExponentComparator());
      ec = (ExponentComparator) this.comparator();
   }
   
   /** Creates an empty polynomial with the given {@link ExponentComparator}.
    *  @param ec the exponent comparator
    */
   public Polynomial(ExponentComparator ec){
      super(ec);
      this.ec = ec;
   }
   
   /** Comparably fast method which returns an array representing the polynomial given
    *  by the polynomial product of <i>p</i> and <i>q</i>.
    *  The principle representation is as follows: The element at index <i>i</i> 
    *  of the array denotes the coefficient <i>a<sub>i</sub></i> of the polynomial,
    *  and the length of the array is &gt;= the degree of the polynomial.
    *  If the degree of the involved polynomials is not too large, this
    *  method is fast and easy to use.
    *  Compare this static method to the object method
    *  {@link #multiply(Polynomial)}.
    *  @param p array representing the first polynomial
    *  @param q array representing the second polyniomial
    *  @return the product <i>pq</i> of the polynomials <i>p</i> and <i>q</i> 
    *  @see #divide(double[],double[])
    */
   public static double[] multiply( double[] p, double[] q ) {
      double[] r = new double[p.length + q.length - 1];
      for (int i = 0; i < p.length; i++) {
         for (int j = 0; j < q.length; j++) {
            r[i+j] += p[i]*q[j];
         }
      }
      double[] rr = new double[deg(r)+1];
      for ( int i = 0; i < rr.length; i++ ) {
         rr[i] = r[i];
      }
      return rr;
   }

   /** Comparably fast method which returns an array {q,r} of two arrays
    *  representing the quotient <i>q</i> of the two given polynomials <i>u/v</i>
    *  and the remainder <i>r</i>.
    *  The principle of the array representation is as follows: The element at index <i>i</i> 
    *  of the array denotes the coefficient <i>a<sub>i</sub></i> of the polynomial,
    *  and the length of the array is &gt;= the degree of the polynomial.
    *  If the degree of the involved polynomials is not too large, this
    *  method is fast and easy to use.
    *  Compare this static method to the object method
    *  {@link #divide(Polynomial)}.
    *  @param u array representing the first polynomial
    *  @param v array representing the second polyniomial
    *  @return the array {<i>q,r</i>} where <i>q</i> is the quotient polynomial <i>u/v</i>,
    *  and <i>r</i> is the remainder polynomial such that <i>u</i> = <i>qv</i> + <i>r</i>
    *  @see #multiply(double[],double[])
    */
   public static double[][] divide( double[] u, double[] v ) {
      double[] q;
      if ( u.length < v.length ) {
         q = new double[1];
         double[][] result = {q,u};
         return result;
      }
      
      double[] r = new double[u.length];
      
      for( int i = 0; i < u.length; i++ ) {
         r[i] = u[i];
      }
      
      int k, j; 
      int n  = u.length - 1;
      int nv = v.length - 1;
      //double tmp;
      //int nvk, kk, jj;
      q = new double[n-nv+1];
      
      for ( k = n - nv; k >= 0; k-- ) {
         // q_k = r_{nv+k} / v_{nv}:
         q[k] = r[nv+k] / v[nv];
//System.out.println("### 1.1 k=" + k + ", nv=" + nv + ", r="+r +", q="+q + ", u="+u +", v="+v);
         for ( j = nv+k-1; j >= k; j-- ) {
            // r_j -= q_k * v_{j-k}:
            r[j] -= q[k] * v[j-k];
//System.out.println("### 1.2.1 k=" + k + ", j="+j +", nv=" + nv + ", r="+r +", q="+q );
         }
//System.out.println("### before r="+r +", q="+q);
      }
      
      for ( j = nv; j <= n; j++ ) {
         r[j] = 0;
      }
      
      double[] rr = new double[deg(r)+1];
      for ( int i = 0; i < rr.length; i++ ) {
         rr[i] = r[i];
      }

      double[] qq = new double[deg(q)+1];
      for ( int i = 0; i < qq.length; i++ ) {
         qq[i] = q[i];
      }
      
//System.out.println("### r="+r +", q="+q);
      double[][] result = {qq,rr};
      return result;
   }   

   /** Determines the degree of the polynomial represented by the given array.
    *  The degree is defined as the maximum exponent of the polynomial.
    *  For the principle how an array represents a polynomial, see 
    *  {@link #multiply(double[],double[])}
    *  By definition, an array consisting only of zeros represents a polynomial of degree zero.
    *  @param p array representing the polynomial
    *  @return the degree of <i>p</i>
    *  @see #deg()
    */
   public static int deg(double[] p) {
      int max = 0;
      for ( int i = p.length-1; i >= 0; i-- ) {
         if ( Math.abs(p[i]) > ACCURACY ) {
            max = i;
            break;
         }
      }
      return max;
   }

   /** Returns a string representation of the given polynomial.
    *  For the principle how an array represents a polynomial, see 
    *  {@link #multiply(double[],double[])}
    *  @param p array representing a polynomial <i>p</i>
    *  @return a string representation of the polynomial <i>p</i>
    *  @see #toString()
    */
   public static String toString(double[] p) {
      if (p.length == 0) return "0";
      if (p.length == 1 && p[0] == 0) return "0";
      String output = "";
      //int exponent = 0;
      double coefficient;
      boolean start = true;
      for( int exponent = p.length - 1; exponent >= 0; exponent-- ) {
         coefficient = p[exponent];
         if (coefficient == 0) continue;
         if ( coefficient > 0 && !start ) { 
            output += " + ";
         } else if ( coefficient < 0 ) { 
            output += " - ";
         }
         if ( Math.abs(Math.abs(coefficient)-1) > ACCURACY || exponent == 0 ) { 
            output += outputFormat.format(Math.abs(coefficient)) + " ";
         }
         if ( exponent > 1 ) {
            output += "x^" + exponent;
         } else if ( exponent == 1 ) {
            output += "x";
         }
         start = false;
      }
      return output;
   }
   
   //--- object methods: -----------------------------------------
   /** Adds the term +<i>a<sub>e</sub> x<sup>e</sup></i> to this
    *  polynomial. Here <i>a<sub>e</sub></i> may be negative, whereas <i>e</i> must
    *  be a non-negative integer.
    *  @param exponent the exponent <i>e</i> in the term <i>a<sub>e</sub> x<sup>e</sup></i>
    *  @param coefficient the coefficient <i>a<sub>e</sub></i> in the term <i>a<sub>e</sub> x<sup>e</sup></i>
    *  @return the previous coefficient associated with the exponent, or null if there did not exist a
    *  term with the exponent <i>e</i> in the polynomial.
    */
   @Override
   public Double put(Integer exponent, Double coefficient) {
      //if ( degree < exponent.intValue() ) {
      //   degree = exponent.intValue();
      //}
      return super.put(exponent, coefficient);
   }
   
   /** Multiplies this polynomial with the given polynomial <i>q</i>.
    *  @param q the polynomial to be multiplied with this polynomial
    *  @return the product of this polynomial times <i>q</i>
    *  @see #multiply(double[],double[])
    */
   public Polynomial multiply( Polynomial q ) {
      Polynomial p=this, r = new Polynomial(ec);
      
      int i, j; 
      Integer k; 
      double tmp;
      int degP = p.deg();
      int degQ = q.deg();
      
      for (i = 0; i <= degP; i++) {
         for (j = 0; j <= degQ; j++) {
            // r_{i+j} += p_i q_j:
//System.out.println("### 1.1 (i,j)=("+i+","+j+ "), r="+r +", p="+p + ", q="+q);
            if ( p.get(i) != null && q.get(j) != null ) {
               k = Integer.valueOf(i+j);
               if ( r.get(k) != null ) {
                  tmp = r.get(k);
               } else {
                  tmp = 0;
               }
               tmp += p.get(i) * q.get(j);
               r.put( k, tmp);
            }
         }
      }
      
      // clear all terms whose coefficients are near to zero:
      /*
      for( Iterator e = r.keySet().iterator(); e.hasNext(); ) {
         //k = (Integer) e.nextElement();
         k = e.next();
         if ( Math.abs(r.get(k)) < ACCURACY ) r.remove(k);
      }
      */
      Set<Integer> keys = r.keySet();
      for( Integer e : keys ) {
         if ( Math.abs(r.get(e)) < ACCURACY ) r.remove(e);
      }
      return r;
   }
   
   /** Divides this polynomial by the given polynomial <i>v</i>
    *  and returns an array {q,r} holding the quotient <i>q</i> as the first entry
    *  and the remainder <i>r</i> as the second entry.
    *  @param v the polynomial to divide this polynomial
    *  @return the array {<i>q,r</i>} where <i>q</i> is the quotient of this polynomial, say <i>u</i>, over <i>v</i>,
    *  and <i>r</i> is the remainder polynomial such that <i>u</i> = <i>qv</i> + <i>r</i>
    *  @see #divide(double[],double[])
    */
   public Polynomial[] divide( Polynomial v ) {
      Polynomial u = this;
      
      Polynomial r = new Polynomial(ec), q = new Polynomial(ec);
      
      //Integer exponent;
      //for( Iterator e = u.keySet().iterator(); e.hasNext(); ) {
      //   exponent = (Integer) e.next();
      //   r.put( exponent, u.get( exponent ) );
      //}
      
      Set<Integer> keys = u.keySet();
      for( Integer exponent : keys ) {
         r.put( exponent, u.get( exponent ) );
      }
      
      int k, j; 
      int n  = u.deg();
      int nv = v.deg();
      double tmp;
      Integer nvk; //, kk, jj;
      
      for ( k = n - nv; k >= 0; k-- ) {
         // q_k = r_{nv+k} / v_{nv}:
//System.out.println("### 1.1 k=" + k + ", nv=" + nv + ", r="+r +", q="+q + ", u="+u +", v="+v);
         nvk = Integer.valueOf(nv+k);
         if ( r.get( nvk ) != null ) {
            //tmp = ((Double) r.get( nvk ) ).doubleValue() / ((Double)v.get(Integer.valueOf(nv))).doubleValue();
            q.put(k, r.get( nvk ) / (v.get(nv)) );
         //} else {
         //   tmp = 0;
         }
         //if ( Math.abs(tmp) > ACCURACY ) q.put( Integer.valueOf(k), new Double(tmp) );
//System.out.println("### 1.2 k=" + k + ", nv=" + nv + ", r="+r +", q="+q + ", u="+u +", v="+v);
         //q.put( k, ( new BigDecimal((BigInteger)r.get( nv.add(k)))).divide( new BigDecimal((BigInteger)v.get(nv)), scale, BigDecimal.ROUND_HALF_UP ) );
         for ( j = nv+k-1; j >= k; j-- ) {
            // r_j -= q_k * v_{j-k}:
//System.out.println("### 1.2.1 k=" + k + ", j="+j +", nv=" + nv + ", r="+r +", q="+q );
            //kk = Integer.valueOf(k);
            if ( q.get(k) != null && v.get(j-k) != null ) {
               tmp = q.get(k) * v.get(j-k);
               //if ( Math.abs(tmp) > ACCURACY ) {  // tmp != 0
                  //jj = Integer.valueOf(j);
                  //if ( r.get(jj) == null ) {
                  //   r.put(jj, new Double(-tmp) );
                  //} else {
                  //   r.put( jj, new Double( ((Double) r.get(jj)).doubleValue() - tmp ) );
                  //}
                  if ( r.get(j) == null ) {
                     r.put(j, -tmp);
                  } else {
                     r.put(j, r.get(j) - tmp );
                  }
               //}
            }
         }
//System.out.println("### before r="+r +", q="+q);
      }
      
      for ( j = nv; j <= n; j++ ) {
         r.remove(j);
      }
      
      if ( r.get(ZERO) != null && Math.abs(r.get(ZERO)) < ACCURACY ) {
         r.remove(ZERO);
      }
      
//System.out.println("### r="+r +", q="+q);
      Polynomial[] result = {q,r};
      return result;
   }

   /** Returns the degree of this polynomial.
    *  The degree is defined as the maximum exponent of the polynomial.
    *  Since this polynomial is sorted with respect to the exponents in
    *  descending order, the first key of this map is the degree.
    *  By definition, an empty polynomial has degree zero.
    *  @return the degree of this polynomial
    *  @see #deg(double[])
    */
   public int deg() {
      //return degree;
      // /* more efficient, since there are usually more puts than polynomial generatings:
      java.util.Iterator<Integer> e = this.keySet().iterator(); 
      return e.hasNext() ? e.next() : 0;
   }
   
   /** Returns a string representation of this polynomial.
    *  @return a string representation of this polynomial
    *  @see #toString(double[])
    */
   @Override
   public String toString() {
      if (isEmpty()) return "0";
      String output = "";
      double coefficient;
      boolean start = true;
      Set<Integer> keys = this.keySet();
      for( int exponent : keys ) {
         coefficient = this.get(exponent);
         if ( coefficient > 0 && !start ) { 
            output += " + ";
         } else if ( coefficient < 0 ) { 
            output += " - ";
         }
         if ( Math.abs(Math.abs(coefficient)-1) > ACCURACY || exponent == 0 ) { 
            output += outputFormat.format(Math.abs(coefficient)) + " ";
         }
         if ( exponent > 1 ) {
            output += "x^" + exponent;
         } else if ( exponent == 1 ) {
            output += "x";
         }
         start = false;
      }
      return output;
   }
   
   /* --------------------------------------------------------------------------
   public static void main( String[] args ) {
      long start;
      
      start = System.currentTimeMillis();
      //Polynomial p = new Polynomial();
      Polynomial p = new Polynomial();
      System.out.println( "leer: P(x) = " + p );
      //--- P(x) = 5x^3 + 2x + 7: -------------------------
      //p.put( Integer.valueOf("0"), new Double("7") );
      //p.put( Integer.valueOf("1"), new Double("2") );
      //p.put( Integer.valueOf("3"), new Double("24") );
      //--- P(x) = x^2 - 1: -------------------------------
      //p.put( Integer.valueOf("0"), new Double("-1") );
      //p.put( Integer.valueOf("2"), new Double("1") );
      //--- P(x) = x^1023 - 3.5 x^2 + 3x - 1: ------------------
      p.put( 2, 3.5 );
      p.put( 1, 3. );
      p.put( 0, -1. );
      p.put( 10023, 1.0 );
      //---------------------------------------------------      
      System.out.println( "P(x) = " + p + ", deg P=" + p.deg() );
      
      //Polynomial q = new Polynomial();
      Polynomial q = new Polynomial(new ExponentComparator());
      //--- Q(x) = 8x^2 + 3x: -----------------------------
      //q.put( Integer.valueOf("1"), new Double("3") );
      //q.put( Integer.valueOf("2"), new Double("8") );
      //--- Q(x) = x + 1: ---------------------------------
      //q.put( Integer.valueOf("0"), new Double("1") );
      //q.put( Integer.valueOf("1"), new Double("1") );
      //--- Q(x) = - x^523 + x: ---------------------------------
      q.put( Integer.valueOf("523"), new Double("-1") );
      q.put( Integer.valueOf("1"), new Double("1") );
      //---------------------------------------------------
      System.out.println( "Q(x) = " + q + ", deg Q=" + q.deg() );
      
      Polynomial[] division = p.divide(q);
      String output = "P(x) = (" + division[0] + ") Q(x)";
      String remainder = division[1].toString();
      if ( remainder != "" ) output += " + " + division[1];
      System.out.println( output );
      
      System.out.println("P(x) Q(x) = " + p.multiply(q) );
      
      System.out.println("\nGebraucht fuer TreeMap: " + (System.currentTimeMillis()-start) + "ms");

      start = System.currentTimeMillis();

      double[] p1 = new double[0];
      System.out.println( "leer: P(x) = " + toString(p1) );
      //--- P(x) = 5x^3 + 2x + 7: -------------------------
      //p.put( Integer.valueOf("0"), new Double("7") );
      //p.put( Integer.valueOf("1"), new Double("2") );
      //p.put( Integer.valueOf("3"), new Double("24") );
      //--- P(x) = x^2 - 1: -------------------------------
      //p.put( Integer.valueOf("0"), new Double("-1") );
      //p.put( Integer.valueOf("2"), new Double("1") );
      //--- P(x) = x^1023 - 3.5 x^2 + 3x - 1: ------------------
      int size = 10024;
      p1 = new double[size];
      p1[size - 1] = 1;
      p1[2] = -3.5;
      p1[1] = 3;
      p1[0] = -1;
      //---------------------------------------------------      
      System.out.println( "P(x) = " + toString(p1) + ", deg P=" + deg(p1) );
      
      double[] q1;
      //--- Q(x) = 8x^2 + 3x: -----------------------------
      //q.put( Integer.valueOf("1"), new Double("3") );
      //q.put( Integer.valueOf("2"), new Double("8") );
      //--- Q(x) = x + 1: ---------------------------------
      //q.put( Integer.valueOf("0"), new Double("1") );
      //q.put( Integer.valueOf("1"), new Double("1") );
      //--- Q(x) = - 523 x + x: ---------------------------------
      q1 = new double[524];
      q1[523] = -1;
      q1[1] = 1;
      //---------------------------------------------------
      System.out.println( "Q(x) = " + toString(q1) + ", deg Q=" + deg(q1) );
      
      System.out.println("PQ = " + toString(multiply(p1,q1)));
      double[][] division1 = divide(p1,q1);
      System.out.println("P/Q = " + toString(division1[0]) + " Rest " + toString(division1[1]));

      System.out.println("\nGebraucht fuer double-Array: " + (System.currentTimeMillis()-start) + "ms" );
   }
   // ----------------------------------------------------------------------- */
}

