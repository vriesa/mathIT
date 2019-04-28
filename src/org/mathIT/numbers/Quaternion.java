/*
 * Quaternion.java - Class providing static methods for quaternions.
 *
 * Copyright (C) 2008-2012 Andreas de Vries
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
import static java.lang.Math.*;
/**
 * This class enables the creation of objects representing quaternions, a
 * system of numbers introduced by Hamilton in 1843,
 * as well the implementation of mathematical functions of quaternions 
 * by static methods.
 * A <i>quaternion</i> <i>z</i> &#8712; <span style="font-size:large;">&#x210D;</span>
 * is uniquely determined by 
 * <p style="text-align:center">
 *   <i>x</i> 
 *   = <i>x</i><sub>0</sub> 
 *   + <i>x</i><sub>1</sub> i + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k,
 * </p>
 * where
 * <i>x</i><sub>0</sub>, <i>x</i><sub>1</sub>, <i>x</i><sub>2</sub>, <i>x</i><sub>3</sub>
 * are real numbers and the numbers i, j, k are defined to satisfy the
 * Hamilton relations
 * <p style="text-align:center">
 *   i<sup>2</sup> = j<sup>2</sup> = k<sup>2</sup> = ijk = -1,
 *   &nbsp; &nbsp;
 *   ij = -ji = k,
 * </p>
 * cf. Ebbinghaus et al.: <i>Numbers.</i> Springer-Verlag, New York Berlin Heidelberg 1991,
 * &sect;7.1.1.
 * This shows that the multiplication of quaternions is in general not commutative.
 * In particular, among some other consequences, 
 * the binomial formula is no longer valid for quaternions.
 * @author  Andreas de Vries
 * @version 1.1
 */
public class Quaternion extends Number {
   /** Version ID for serialization. */
   private static final long serialVersionUID = 9223372036485945537L; // = Long.MAX_VALUE - "Quaternion".hashCode()
   
   /** Accuracy up to which equality of double values are computed in
    *  methods of this class. Its current value is {@value}.
    *  It is used, for instance, in the method {@link #toString()}.
    */
   public static final double ACCURACY = 1e-10;
   /** Constant 0 &#8712; <span style="font-size:large;">&#x210D;</span>.*/
   public static final Quaternion ZERO = new Quaternion(0., 0., 0., 0.);
   /** Constant 1 &#8712; <span style="font-size:large;">&#x210D;</span>.*/
   public static final Quaternion ONE  = new Quaternion(1., 0., 0., 0.);
   /** Constant i &#8712; <span style="font-size:large;">&#x210D;</span>.*/
   public static final Quaternion   I  = new Quaternion(0., 1., 0., 0.);
   /** Constant j &#8712; <span style="font-size:large;">&#x210D;</span>.*/
   public static final Quaternion   J  = new Quaternion(0., 0., 1., 0.);
   /** Constant k &#8712; <span style="font-size:large;">&#x210D;</span>.*/
   public static final Quaternion   K  = new Quaternion(0., 0., 0., 1.);

   /** Object attribute representing a quaternion.*/
   private double[] z;
   
   /** Creates a quaternion
    *   <i>x</i> 
    *   = <i>x</i><sub>0</sub> 
    *   + <i>x</i><sub>1</sub> i + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k
    *  @param x0 the real part of the quaternion
    *  @param x1 the i-part of the quaternion
    *  @param x2 the j-part of the quaternion
    *  @param x3 the k-part of the quaternion
    */
   public Quaternion(double x0, double x1, double x2, double x3) {
      z = new double[]{x0, x1, x2, x3};
   }
   
   /** Creates a quaternion
    *   <i>x</i> 
    *   = Re <i>z</i> + (Im <i>z</i>)&#x2219;i + 0&#x2219;j + 0&#x2219;k
    *  from a complex number <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i.
    *  @param z the complex number from which the quaternion is created
    */
   public Quaternion(Complex z) {
      this.z = new double[]{z.getRe(), z.getIm(), 0, 0};
   }
   
   /** Creates a quaternion 
    *   <i>x</i> 
    *   = <i>x</i><sub>0</sub> + 0&#x2219;i + 0&#x2219;j + 0&#x2219;k,
    *  from a real number <i>x</i><sub>0</sub>.
    *  @param x0 the real number from which the quaternion is created
    */
   public Quaternion(double x0) {
      z = new double[]{x0, 0, 0, 0};
   }
   
   /** Returns the absolute value
    *  |<i>x</i>| of <i>x</i> &#8712; <span style="font-size:large;">&#x210D;</span>
    *  of this quaternion <i>x</i>.
    *  For 
    *  <i>x</i> 
    *  = <i>x</i><sub>0</sub> 
    *  + <i>x</i><sub>1</sub> i + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k,
    *  it is defined as 
    *  |<i>x</i>| = &#8730;(
    *     <i>x</i><sub>0</sub><sup>2</sup> + <i>x</i><sub>1</sub><sup>2</sup>
    *     <i>x</i><sub>2</sub><sup>2</sup> + <i>x</i><sub>3</sub><sup>2</sup>
    *  ).
    *  @return |<code>this</code>|
    */
   public double abs() {
      return sqrt(z[0]*z[0] + z[1]*z[1] + z[2]*z[2] + z[3]*z[3]);
   }
   
   /**
    *  Returns the sum of this number and the quaternion <i>z</i>. 
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k
    *  and <i>y</i> = <i>y</i><sub>0</sub> + <i>y</i><sub>1</sub> i
    *  + <i>y</i><sub>2</sub> j + <i>y</i><sub>3</sub> k
    *  we have
    *  <p style="text-align:center">
    *    <i>x + y</i> =
    *  (<i>x</i><sub>0</sub> + <i>y</i><sub>0</sub>)
    *  + (<i>x</i><sub>1</sub> + <i>y</i><sub>1</sub>) i
    *  + (<i>x</i><sub>2</sub> + <i>y</i><sub>2</sub>) j
    *  + (<i>x</i><sub>3</sub> + <i>y</i><sub>3</sub>) k
    *  </p>
    *  @param y the addend
    *  @return the sum <code>this</code> + <i>y</i>
    *  @see #plus(Quaternion)
    */
   public Quaternion add(Quaternion y) {
      return new Quaternion(
         this.z[0] + y.z[0], this.z[1] + y.z[1], this.z[2] + y.z[2], this.z[3] + y.z[3]
      );
   }
   
   /**
    *  Returns the conjugate of this number.
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k, the conjugate <i>x</i>*
    *  is defined as
    *  <p style="text-align:center">
    *    <i>x</i><sup>*</sup> =
    *    <i>x</i><sub>0</sub>   - <i>x</i><sub>1</sub> i
    *  - <i>x</i><sub>2</sub> j - <i>x</i><sub>3</sub> k
    *  </p>
    *  @return the conjugate <code>this</code>*
    */
   public Quaternion conjugate() {
      return new Quaternion(z[0], -z[1], -z[2], -z[3]);
   }
   
   /** Divides this quaternion by a real number <i>y</i>.
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k,
    *  we have 
    *  <p style="text-align:center">
    *    <i>x/y</i> = <i>x</i><sub>0</sub>/<i>y</i> + (<i>x</i><sub>1</sub>/<i>y</i>) i
    *    + (<i>x</i><sub>2</sub>/<i>y</i>) j + (<i>x</i><sub>3</sub>/<i>y</i>) k.
    *  </p>
    *  If |<i>y</i>| = 0, <i>y</i><sup>-1</sup> = 
    *  {@link java.lang.Double#NaN Double.NaN}.
    *  @param y divisor
    *  @return <code>this</code>/<i>y</i>
    */
   public Quaternion divide(double y) {
      //if (y == 0) throw new ArithmeticException("Divison by zero");
      return new Quaternion(
         this.z[0]/y, this.z[1]/y, this.z[2]/y, this.z[3]/y
      );
   }

   /** Divides this quaternion by a quaternion <i>y</i> from the left.
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k
    *  and <i>y</i> = <i>y</i><sub>0</sub> + <i>y</i><sub>1</sub> i
    *  + <i>y</i><sub>2</sub> j + <i>y</i><sub>3</sub> k
    *  we have
    *  <p style="text-align:center">
    *    <i>y</i><sup>-1</sup><i>x</i> = <i>y</i><sup>*</sup><i>x</i> / |<i>y</i>|<sup>2</sup>,
    *  </p>
    *  where <i>y</i><sup>*</sup> denotes the conjugate of <i>y</i>, and |<i>y</i>| its absolute value.
    *  If |<i>y</i>| = 0, <i>y</i><sup>-1</sup> = 
    *  {@link java.lang.Double#NaN Double.NaN}.
    *  @param y divisor
    *  @return <code>this</code>/<i>y</i>
    *  @see #divide(double)
    *  @see #divideFromRight(Quaternion)
    *  @see #abs()
    *  @see #inverse()
    */
   public Quaternion divideFromLeft(Quaternion y) {
      return y.inverse().multiply(this);
      //return this.multiply(y.inverse());
   }

   /** Divides this quaternion by a quaternion <i>y</i> from the right.
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k
    *  and <i>y</i> = <i>y</i><sub>0</sub> + <i>y</i><sub>1</sub> i
    *  + <i>y</i><sub>2</sub> j + <i>y</i><sub>3</sub> k
    *  we have
    *  <p style="text-align:center">
    *    <i>xy</i><sup>-1</sup> = <i>xy</i><sup>*</sup> / |<i>y</i>|<sup>2</sup>,
    *  </p>
    *  where <i>y</i><sup>*</sup> denotes the conjugate of <i>y</i>, and |<i>y</i>| its absolute value.
    *  If |<i>y</i>| = 0, <i>y</i><sup>-1</sup> = 
    *  {@link java.lang.Double#NaN Double.NaN}.
    *  @param y divisor
    *  @return <code>this</code>/<i>y</i>
    *  @see #divide(double)
    *  @see #divideFromLeft(Quaternion)
    *  @see #abs()
    *  @see #inverse()
    */
   public Quaternion divideFromRight(Quaternion y) {
      return this.multiply(y.inverse());
   }

   /** Returns the multiplicative inverse, or reciprocal, of this number.
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k
    *  we have
    *  <p style="text-align:center">
    *    <i>x</i><sup>-1</sup> = <i>x</i><sup>*</sup> / |<i>x</i>|<sup>2</sup>,
    *  </p>
    *  where <i>x</i><sup>*</sup> denotes the conjugate of <i>x</i>, and |<i>x</i>| its absolute value.
    *  If |<code>this</code>| = 0, <code>this</code><sup>-1</sup> = 
    *  {@link java.lang.Double#NaN Double.NaN}.
    *  @return <code>this</code><sup>-1</sup>
    *  @see #reciprocal()
    *  @see #abs()
    *  @see #conjugate()
    */
   public Quaternion inverse() {
      return conjugate().divide(z[0]*z[0] + z[1]*z[1] + z[2]*z[2] + z[3]*z[3]);
   }
   
   /**
    *  Subtracts <i>y</i> from this quaternion. 
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k
    *  and <i>y</i> = <i>y</i><sub>0</sub> + <i>y</i><sub>1</sub> i
    *  + <i>y</i><sub>2</sub> j + <i>y</i><sub>3</sub> k
    *  we have
    *  <p style="text-align:center">
    *    <i>x - y</i> =
    *  (<i>x</i><sub>0</sub> - <i>y</i><sub>0</sub>)
    *  + (<i>x</i><sub>1</sub> - <i>y</i><sub>1</sub>) i
    *  + (<i>x</i><sub>2</sub> - <i>y</i><sub>2</sub>) j
    *  + (<i>x</i><sub>3</sub> - <i>y</i><sub>3</sub>) k
    *  </p>
    *  @param y a quaternion
    *  @return the difference <code>this</code> - <i>y</i>
    *  @see #subtract(Quaternion)
    */
   public Quaternion minus(Quaternion y) {
      return new Quaternion(
         this.z[0] - y.z[0], this.z[1] - y.z[1], this.z[2] - y.z[2], this.z[3] - y.z[3]
      );
   }
   
   /** The product of a real number <i>y</i> with this quaternion. 
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k
    *  and a real number <i>y</i> we have
    *  <p style="text-align:center">
    *    <i>xy</i> = <i>yx</i>
    *   = <i>yx</i><sub>0</sub>   + <i>yx</i><sub>1</sub> i
    *   + <i>yx</i><sub>2</sub> j + <i>yx</i><sub>3</sub> k
    *  </p>
    *  For a real factor,
    *  the quaternion multiplication therefore is commutative.
    *  @param y a real number
    *  @return the product <code>this</code>&#x2219;<i>y</i> = <i>y</i>&#x2219;<code>this</code>
    *  @see #multiply(Quaternion)
    */
   public Quaternion multiply(double y) {
      return new Quaternion(y * z[0], y * z[1], y * z[2], y * z[3]);
   }
   
   /** Returns the product of this quaternion and the quaternion <i>y</i>.
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k
    *  and <i>y</i> = <i>y</i><sub>0</sub> + <i>y</i><sub>1</sub> i
    *  + <i>y</i><sub>2</sub> j + <i>y</i><sub>3</sub> k
    *  we have
    *  <p style="text-align:center">
    *    <i>x&#x2219;y</i> =
    *    (<i>x</i><sub>0</sub><i>y</i><sub>0</sub>
    *   - <i>x</i><sub>1</sub><i>y</i><sub>1</sub>
    *   - <i>x</i><sub>2</sub><i>y</i><sub>2</sub>
    *   - <i>x</i><sub>3</sub><i>y</i><sub>3</sub>)
    *  + (<i>x</i><sub>0</sub><i>y</i><sub>1</sub>
    *   + <i>x</i><sub>1</sub><i>y</i><sub>0</sub>
    *   + <i>x</i><sub>2</sub><i>y</i><sub>3</sub>
    *   - <i>x</i><sub>3</sub><i>y</i><sub>2</sub>) i
    *  + (<i>x</i><sub>0</sub><i>y</i><sub>2</sub>
    *   - <i>x</i><sub>1</sub><i>y</i><sub>3</sub>
    *   + <i>x</i><sub>2</sub><i>y</i><sub>0</sub>
    *   + <i>x</i><sub>3</sub><i>y</i><sub>1</sub>) j
    *  + (<i>x</i><sub>0</sub><i>y</i><sub>3</sub>
    *   + <i>x</i><sub>1</sub><i>y</i><sub>2</sub>
    *   - <i>x</i><sub>2</sub><i>y</i><sub>1</sub>
    *   + <i>x</i><sub>3</sub><i>y</i><sub>0</sub>) k
    *  </p>
    *  Note that multiplication of quaternions is not commutative, i.e.
    *  that in general <i>xy</i> &#x2260; <i>yx</i>.
    *  @param y a quaternion
    *  @return the product <code>this</code>&#x2219;<i>y</i>
    *  @see #multiply(double)
    */
   public Quaternion multiply(Quaternion y) {
      return new Quaternion(
         z[0] * y.z[0] - z[1] * y.z[1] - z[2] * y.z[2] - z[3] * y.z[3],
         z[0] * y.z[1] + z[1] * y.z[0] + z[2] * y.z[3] - z[3] * y.z[2],
         z[0] * y.z[2] - z[1] * y.z[3] + z[2] * y.z[0] + z[3] * y.z[1],
         z[0] * y.z[3] + z[1] * y.z[2] - z[2] * y.z[1] + z[3] * y.z[0]
      );
   }
   
   /**
    *  Returns the sum of this number and the quaternion <i>y</i>. 
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k
    *  and <i>y</i> = <i>y</i><sub>0</sub> + <i>y</i><sub>1</sub> i
    *  + <i>y</i><sub>2</sub> j + <i>y</i><sub>3</sub> k
    *  we have
    *  <p style="text-align:center">
    *    <i>x + y</i> =
    *  (<i>x</i><sub>0</sub> + <i>y</i><sub>0</sub>)
    *  + (<i>x</i><sub>1</sub> + <i>y</i><sub>1</sub>) i
    *  + (<i>x</i><sub>2</sub> + <i>y</i><sub>2</sub>) j
    *  + (<i>x</i><sub>3</sub> + <i>y</i><sub>3</sub>) k
    *  </p>
    *  @param y the addend
    *  @return the sum <code>this</code> + <i>y</i>
    *  @see #add(Quaternion)
    */
   public Quaternion plus(Quaternion y) {
      return new Quaternion(z[0] + y.z[0], z[1] + y.z[1], z[2] + y.z[2], z[3] + y.z[3]);
   }
   
   /** Returns the reciprocal of this number.
    *  @return <code>this</code><sup>-1</sup>
    *  @see #inverse()
    */
   public Quaternion reciprocal() {
      return inverse();
   }

   /**
    *  Subtracts <i>y</i> from this quaternion. 
    *  For <i>x</i> = <i>x</i><sub>0</sub> + <i>x</i><sub>1</sub> i
    *  + <i>x</i><sub>2</sub> j + <i>x</i><sub>3</sub> k
    *  and <i>y</i> = <i>y</i><sub>0</sub> + <i>y</i><sub>1</sub> i
    *  + <i>y</i><sub>2</sub> j + <i>y</i><sub>3</sub> k
    *  we have
    *  <p style="text-align:center">
    *    <i>x - y</i> =
    *  (<i>x</i><sub>0</sub> - <i>y</i><sub>0</sub>)
    *  + (<i>x</i><sub>1</sub> - <i>y</i><sub>1</sub>) i
    *  + (<i>x</i><sub>2</sub> - <i>y</i><sub>2</sub>) j
    *  + (<i>x</i><sub>3</sub> - <i>y</i><sub>3</sub>) k
    *  </p>
    *  @param y a quaternion
    *  @return the difference <code>this</code> - <i>z</i>
    *  @see #minus(Quaternion)
    */
   public Quaternion subtract(Quaternion y) {
      return new Quaternion(
         this.z[0] - y.z[0], this.z[1] - y.z[1], this.z[2] - y.z[2], this.z[3] - y.z[3]
      );
   }
   
   /** Returns a string representation of this quaternion in a "readable" standard format. 
    *  @see #toString(java.text.DecimalFormat)
    */
   @Override
   public String toString() {
      return toString(new java.text.DecimalFormat("#,###.########"));
   }
   
   /** Returns a string representation of this quaternion in the specified
    *  "readable" decimal format.
    *  If the real or the imaginary part are too large or too small,
    *  scientific notation is used.
    *  @param digit the decimal format in which <i>z</i> is to be displayed
    *  @return a string representation in the specified format
    *  @see #toString()
    */
   public String toString(java.text.DecimalFormat digit) {
      java.text.DecimalFormat scientific = new java.text.DecimalFormat("0.########E0");
      double upLimit = 1e9, lowLimit = 1e-9;
      boolean digital;
      String output = "";
      
      if ( Double.isNaN(z[0]) || Double.isNaN(z[1]) || Double.isNaN(z[2]) || Double.isNaN(z[3]) ) {
         output += "NaN";
      } else if ( 
         Math.abs(z[0]) < ACCURACY && Math.abs(z[1]) < ACCURACY &&
         Math.abs(z[2]) < ACCURACY && Math.abs(z[3]) < ACCURACY 
      ) {
         output += "0";
      } else {
         output += "(";
         if (Math.abs(z[0]) > ACCURACY) {
            digital = ( Math.abs(z[0]) < upLimit && Math.abs(z[0]) > lowLimit );
            output += digital ? digit.format(z[0]) : scientific.format(z[0]);
         }
         
         char[] units = {'i', 'j', 'k'};
         
         boolean first;
         
         for (int i = 1; i < z.length; i++) {
            first = true;
            for (int j = 0; j < i; j++) {
               first &= Math.abs(z[j]) < ACCURACY;
            }
            if (Math.abs(z[i]) < ACCURACY) continue;
            if (Math.abs(z[i] + 1) < ACCURACY) {
               output += " -";
            } else if ( Math.abs(z[i] - 1) < ACCURACY) {
               if (!first) output += " +";
            } else {
               if (!first && z[i] > 0) output += " + ";
               if (z[i] < 0) output += " - ";
               digital = (Math.abs(z[i]) < upLimit && Math.abs(z[i]) > lowLimit);
               output += digital? digit.format(Math.abs(z[i])) : scientific.format(Math.abs(z[i]));
            }
            output += " " + units[i-1];
         }
         output += ")";
      }
      
      return output;
   }
   
   /** Returns the integer value of the real part of this quaternion (by casting to type int).
    *  @return the double of the real part of this quaternion converted to int
    */
   @Override
   public int intValue() {
      return (int) z[0];
   }
   
   /** Returns the long value of the real part of this quaternion (by casting to type long).
    *  @return the double of the real part of this quaternion converted to long
    */
   @Override
   public long longValue() {
      return (long) z[0];
   }
   
   /** Returns the float value of the real part of this quaternion (by casting to type float).
    *  @return the double of the real part of this quaternion converted to float
    */
   @Override
   public float floatValue() {
      return (float) z[0];
   }

   /** Returns the value of the real part of this quaternion.
    *  @return the the real part of this quaternion
    */
   @Override
   public double doubleValue() {
      return z[0];
   }

   /** Returns the byte value of the real part of this quaternion (by casting to type byte).
    *  @return the double of the real part of this quaternion converted to byte
    */
   @Override
   public byte byteValue() {
      return (byte) z[0];
   }
   
   /** Returns the short value of the real part of this quaternion (by casting to type short).
    *  @return the double of the real part of this quaternion converted to short
    */
   @Override
   public short shortValue() {
      return (short) z[0];
   }
   
   /** Compares this object against the specified object. 
    *  The result is <code>true</code> if and only if the argument is not 
    *  <code>null</code> and is a 
    *  <code>Quaternion</code> object whose real and imaginary parts are
    *  <code>double</code>s which have the same values as the 
    *  real and imaginary parts of this object, according to 
    *  {@link Double#equals(Object)}.
    *  @param obj the object to compare with
    *  @return <code>true</code> if the objects are the same; <code>false</code> otherwise
    *  @see Double#equals(Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Quaternion) { 
         Quaternion tmp = (Quaternion) obj;
         return (
               (Double.valueOf(z[0])).equals(tmp.z[0]) 
            && (Double.valueOf(z[1])).equals(tmp.z[1])
            && (Double.valueOf(z[2])).equals(tmp.z[2])
            && (Double.valueOf(z[3])).equals(tmp.z[3])
         );
      }
      return false;        
   }

   /** Compares the two quaternions up to the specified accuracy. 
    *  The result is <code>true</code> if and only the absolute values of the
    *  differences between the two real parts and the two imaginary parts, respectively, 
    *  are less than then the specified accuracy. 
    *  @param a the first quaternion to compare with
    *  @param b the second quaternion to compare with
    *  @param accuracy the accuracy of the comparison
    *  @return <code>true</code> if the real and imaginary parts, respectively, 
    *  of the two numbers differ from each other by
    *  at most <code>accuracy</code>; <code>false</code> otherwise
    *  @see #equals(Object)
    */
   public static boolean equals(Quaternion a, Quaternion b, double accuracy) {
      if( (Math.abs(a.z[0] - b.z[0]) < accuracy) &&
          (Math.abs(a.z[1] - b.z[1]) < accuracy) &&
          (Math.abs(a.z[2] - b.z[2]) < accuracy) &&
          (Math.abs(a.z[3] - b.z[3]) < accuracy) ) {
         return true;
      } else {
         return false;
      }
   }
   
   /** Returns the hash code for this <code>Quaternion</code> number.
    *  @return hash code for this number
    */
   @Override
   public int hashCode() {
      long bits = Double.doubleToLongBits(this.z[0]);
      bits ^= Double.doubleToLongBits(this.z[1]) * 31;
      bits ^= Double.doubleToLongBits(this.z[2]) * 37;
      bits ^= Double.doubleToLongBits(this.z[3]) * 41;
      return (((int) bits) ^ ((int) (bits >> 32)));
   }
    
   /** for test purposes ...*/
   /*
   public static void main ( String args[] ) {
      // Eingabefeld:
      javax.swing.JTextField[] feld = new javax.swing.JTextField[8];
      
      feld[0] = new javax.swing.JTextField("1");
      feld[1] = new javax.swing.JTextField("1");
      feld[2] = new javax.swing.JTextField("23");
      feld[3] = new javax.swing.JTextField("-21");
      
      for (int i = 0; i < feld.length; i++) {
      //for (int i = 4; i < feld.length; i++) {
         feld[i] = new javax.swing.JTextField("0");
      }
    
      Object[] msg = {"x:", feld[0], feld[1], feld[2], feld[3], "y:", feld[4], feld[5], feld[6], feld[7]};
      javax.swing.JOptionPane optionPane = new javax.swing.JOptionPane( msg );
    
      optionPane.createDialog(null,"Eingabe").setVisible(true);

      //double[] z = { Double.parseDouble( feld1.getText() ), Double.parseDouble( feld2.getText() ) }; 
      //double[] w = { Double.parseDouble( feld3.getText() ), Double.parseDouble( feld4.getText() ) };       
      //double[] result = {0.,.0};
      
      Quaternion z = new Quaternion(
         Double.parseDouble( feld[0].getText() ), Double.parseDouble( feld[1].getText() ),
         Double.parseDouble( feld[2].getText() ), Double.parseDouble( feld[3].getText() )
      );
      Quaternion w = new Quaternion(
         Double.parseDouble( feld[4].getText() ), Double.parseDouble( feld[5].getText() ),
         Double.parseDouble( feld[6].getText() ), Double.parseDouble( feld[7].getText() )
      );
      System.out.println(z +" = "+ w +"? " + z.equals(w));
      System.out.println(z +" = "+ z +"? " + z.equals(z));
      System.out.println(z +" = "+ w +"? " + Quaternion.equals(z,w, ACCURACY));
      System.out.println(z +" = "+ z +"? " + Quaternion.equals(z,z, ACCURACY));
      Quaternion result;
      java.text.DecimalFormat digit = new java.text.DecimalFormat( "#,###.##########" );
      
      String ausgabe = "";           // Ausgabestring
        
      long start = System.currentTimeMillis();
      
      
      long zeit = System.currentTimeMillis();
      result = z.multiply(w);

      ausgabe += z + ".hashCode()=" + z.hashCode() + ", " + w + ".hashCode() = " + w.hashCode();
      ausgabe += "\n" + z.toString(digit) + ".multiply(";
      ausgabe += w.toString(digit);
      ausgabe += ") = ";
      ausgabe += result.toString(digit);
      
      result = w.multiply(z);
      ausgabe += "\n" + w.toString(digit) + ".multiply(";
      ausgabe += z.toString(digit);
      ausgabe += ") = ";
      ausgabe += result.toString(digit);
      ausgabe += "  (" + digit.format(zeit - start) + " ms)";
      
      ausgabe += "\n" + z + "* = " + z.conjugate() + ", " + w + "* = " + w.conjugate();
      ausgabe += "\n" + z + " " + z +"* = " + z.multiply(z.conjugate()) + ", " +
         z + "* " + z +" = " + z.conjugate().multiply(z);
      ausgabe += "\n" + w + " " + w +"* = " + w.multiply(w.conjugate()) + ", " +
         w + "* " + w +" = " + w.conjugate().multiply(w);
      ausgabe += "\n" + z + " " + w +"^(-1) = " + z.divideFromRight(w) + ", " +
         w + "^(-1) " + z +" = " + z.divideFromLeft(w);
      ausgabe += "\n" + z + "^(-1) = " + z.inverse();
      
      // Ausgabe auf dem Bildschirm:
      javax.swing.JOptionPane.showMessageDialog( null, ausgabe, "Ergebnis", javax.swing.JOptionPane.PLAIN_MESSAGE );
        
      System.exit( 0 );
   }
   // */
}
