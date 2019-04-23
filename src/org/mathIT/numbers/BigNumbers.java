/*
 * BigNumbers.java
 *
 * Copyright (C) 2007-2019 Andreas de Vries
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
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import static java.math.BigInteger.*;
import org.mathIT.algebra.PolynomialZ;
import static org.mathIT.numbers.Numbers.factorial;
/**
 * This class provides basic analytical and number theoretic functions for big numbers.
 * @author  Andreas de Vries
 * @version 1.2
 */
public class BigNumbers {
   // Suppresses default constructor, ensuring non-instantiability.
   private BigNumbers() {
   }

   /** The BigInteger constant two.
    *  @see java.math.BigInteger
    */
   public static final BigInteger TWO = BigInteger.valueOf(2);
   /** The BigInteger constant three.
    *  @see java.math.BigInteger
    */
   public static final BigInteger THREE = BigInteger.valueOf(3);

   /** Maximum precision of BigDecimals, having the value {@value}.*/
   public final static int PRECISION = 50; //50;
   /** This math context is used throughout this class. It determines the
    *  maximum precision ("scale") of the BigDecimal numbers and the rounding mode
    *  and is initialized with the values
    *  PRECISION = {@value #PRECISION} and
    *  rounding mode = {@link java.math.RoundingMode RoundingMode.HALF_EVEN}.
    */
   public final static MathContext MATH_CONTEXT = new MathContext(PRECISION, RoundingMode.HALF_EVEN);
   /** The number 0 as a BigDecimal. It equals {@link java.math.BigInteger#ZERO}.*/
   public static final BigDecimal ZERO_DOT = BigDecimal.ZERO;
   /** The number 1 as a BigDecimal. It equals {@link java.math.BigInteger#ONE}.*/
   public static final BigDecimal ONE_DOT = BigDecimal.ONE;
   /** The number 2 as a BigDecimal.*/
   public static final BigDecimal TWO_DOT = BigDecimal.valueOf(2.);
   /** The number 10 as a BigDecimal. It equals {@link java.math.BigInteger#TEN}.*/
   public static final BigDecimal TEN_DOT = BigDecimal.TEN;
   /** The number 1/6 as a BigDecimal. It is computed with the initial value of {@link #MATH_CONTEXT}.*/
   public static final BigDecimal ONE_SIXTH = ONE_DOT.divide(BigDecimal.valueOf(6),MATH_CONTEXT);
   /** The number 1/3 as a BigDecimal. It is computed with the initial value of {@link #MATH_CONTEXT}.*/
   public static final BigDecimal ONE_THIRD = ONE_DOT.divide(BigDecimal.valueOf(3),MATH_CONTEXT);
   /** The number 1/2 as a BigDecimal.*/
   public static final BigDecimal ONE_HALF = BigDecimal.valueOf(.5);
   /** Square root of 2.
    *  @see Numbers#SQRT2
    */
   public static final BigDecimal SQRT_TWO = new BigDecimal("1.4142135623730950488016887242096980785696718753769480731766797379907324784621070388503875343276415727");
   /** Square root of 1/2.
    *  @see Numbers#SQRT_1_2
    */
   public static final BigDecimal SQRT_ONE_HALF = new BigDecimal("0.70710678118654752440084436210484903928483593768847403658833986899536623923105351942519376716382078635298505586");
   /** 10th root of 2.*/
   public static final BigDecimal ROOT_10_TWO = new BigDecimal("1.07177346253629316421300632502334202290638460497755678");
   /** The constant <i>e</i>, the base of the natural logarithms.
    *  The constant is implemented to a precision of 10<sup>-100</sup>.
    *  @see java.lang.Math#E
    */
   //                                                       0        1         2         3         4         5         6         7         8         9         100
   //                                                       1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
   public static final BigDecimal E     = new BigDecimal("2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274");
   /** The constant &#x03C0;, the ratio of the circumference of a circle to its diameter.
    *  The constant is implemented to a precision of 10<sup>-100</sup>.
    *  @see java.lang.Math#PI
    */
   //                                                       0        1         2         3         4         5         6         7         8         9         100
   //                                                       1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
   public static final BigDecimal PI    = new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679");
   /** Euler-Mascheroni constant &#947;.
    *  The constant is implemented to a precision of 10<sup>-101</sup>.
    *  @see Numbers#GAMMA
    */
   //                                                       0        1         2         3         4         5         6         7         8         9         100
   //                                                       12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901
   public static final BigDecimal GAMMA = new BigDecimal("0.57721566490153286060651209008240243104215933519399235988057672348848677267776646709369470632917467495");
   /** The constant 2&#x03C0;/360, the ratio of 1 radians per degree.
    *  The constant is implemented to a precision of 10<sup>-102</sup>.
    *  @see #PI
    *  @see Numbers#RADIANS
    */
   //                                                         0        1         2         3         4         5         6         7         8         9         100
   //                                                         1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
   public static final BigDecimal RADIANS = new BigDecimal("0.0174532925199432957692369076848861271344287188854172545609719144017100911460344944368224156963450948");
   /** The constant &#x03C0;/4. 
    *  See <a href="http://oeis.org/A003881" target="_top">http://oeis.org/A003881</a>,
    *  @see #PI
    */
   public static final BigDecimal PI_4  = new BigDecimal("0.7853981633974483096156608458198757210492923498437764552437361480769541015715522496570087063355292670");
   /** The constant &#x03C0;/2.
    *  See <a href="http://oeis.org/A019669" target="_top">http://oeis.org/A019669</a>,
    *  @see #PI
    */
   public static final BigDecimal PI_2  = new BigDecimal("1.57079632679489661923132169163975144209858469968755291048747229615390820314310449931401741267105853");
   /** The constant 3&#x03C0;/4.
    *  @see #PI
    */
   public static final BigDecimal PI3_4 = PI.multiply(new BigDecimal("0.75"));
   /** The constant 5&#x03C0;/4.
    *  @see #PI
    */
   public static final BigDecimal PI5_4 = PI.multiply(new BigDecimal("1.25"));
   /** The constant 3&#x03C0;/2.
    *  @see #PI
    */
   public static final BigDecimal PI3_2 = PI.multiply(new BigDecimal("1.5"));
   /** The constant 7&#x03C0;/4.
    *  @see #PI
    */
   public static final BigDecimal PI7_4 = PI.multiply(new BigDecimal("1.75"));
   /** Ramanujan's constant e<sup>&#x03C0; &#x221A;163</sup>, up to an accuracy of 10<sup>-102</sup>.
    *  It is "almost" an integer number.
    *  It is listed up to 5000 digits at Simon Plouffe's web site at
    *  <a href="http://pi.lacim.uqam.ca/piDATA/ramanujan.txt" target="_top">pi.lacim.uqam.ca/piDATA/ramanujan.txt</a>.
    */
   //                                                                             0        1         2         3         4         5         6         7         8         9         100
   //                                                                             123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012
   public static final BigDecimal RAMANUJAN  = new BigDecimal("262537412640768743.999999999999250072597198185688879353856337336990862707537410378210647910118607312951181346186064504193");
   /** The value of the Riemann Zeta function &#x03B6;(2) = <i>&pi;</i><sup>2</sup>/6.
    *  See <a href="http://oeis.org/A013661" target="_top">http://oeis.org/A013661</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    */
   public static final BigDecimal ZETA_2  = new BigDecimal("1.64493406684822643647241516664602518921894990120679843773555822937000747040320087383362890061975870");
   /** The value of the Riemann Zeta function &#x03B6;(3), also called Ap&eacute;ry's constant.
    *  See <a href="http://oeis.org/A002117" target="_top">http://oeis.org/A002117</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_3
    */
   public static final BigDecimal ZETA_3  = new BigDecimal("1.20205690315959428539973816151144999076498629234049888179227155534183820578631309018645587360933525814619915");
   /** The value of the Riemann Zeta function &#x03B6;(4) = <i>&pi;</i><sup>6</sup>/945.
    *  See <a href="http://oeis.org/A013664" target="_top">http://oeis.org/A013664</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    */
   public static final BigDecimal ZETA_4  = new BigDecimal("1.08232323371113819151600369654116790277475095191872690768297621544412061618696884655690963594169991");
   /** The value of the Riemann Zeta function &#x03B6;(5).
    *  See <a href="http://oeis.org/A013663" target="_top">http://oeis.org/A013663</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_5
    */
   public static final BigDecimal ZETA_5  = new BigDecimal("1.03692775514336992633136548645703416805708091950191281197419267790380358978628148456004310655713333");
   /** The value of the Riemann Zeta function &#x03B6;(6) = <i>&pi;</i><sup>6</sup>/945.
    *  See <a href="http://oeis.org/A013664" target="_top">http://oeis.org/A013664</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    */
   public static final BigDecimal ZETA_6  = new BigDecimal("1.01734306198444913971451792979092052790181749003285356184240866400433218290195789788277397793853517");
   /** The value of the Riemann Zeta function &#x03B6;(7).
    *  See <a href="http://oeis.org/A013665" target="_top">http://oeis.org/A013665</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_7
    */
   public static final BigDecimal ZETA_7  = new BigDecimal("1.00834927738192282683979754984979675959986356056523870641728313657160147831735573534609696891385132");
   /** The value of the Riemann Zeta function &#x03B6;(8) = <i>&pi;</i><sup>8</sup>/9450.
    *  See <a href="http://oeis.org/A013666" target="_top">http://oeis.org/A013666</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    */
   public static final BigDecimal ZETA_8  = new BigDecimal("1.00407735619794433937868523850865246525896079064985002032911020265258295257474881439528723037237197");
   /** The value of the Riemann Zeta function &#x03B6;(9).
    *  See <a href="http://oeis.org/A013667" target="_top">http://oeis.org/A013667</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_9
    */
   public static final BigDecimal ZETA_9  = new BigDecimal("1.00200839282608221441785276923241206048560585139488875654859661590978505339025839895039306912716958");
   /** The value of the Riemann Zeta function &#x03B6;(10).
    *  See <a href="http://oeis.org/A013668" target="_top">http://oeis.org/A013668</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    */
   public static final BigDecimal ZETA_10  = new BigDecimal("1.00099457512781808533714595890031901700601953156447751725778899463629146515191295439704196861038565");
   /** The value of the Riemann Zeta function &#x03B6;(11).
    *  See <a href="http://oeis.org/A013669" target="_top">http://oeis.org/A013669</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_11
    */
   public static final BigDecimal ZETA_11 = new BigDecimal("1.00049418860411946455870228252646993646860643575820861711914143610005405979821981470259184302356063");
   /** The value of the Riemann Zeta function &#x03B6;(12).
    *  See <a href="http://oeis.org/A013670" target="_top">http://oeis.org/A013670</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    */
   public static final BigDecimal ZETA_12  = new BigDecimal("1.00024608655330804829863799804773967096041608845800340453304095213325201968194091304904280855190069");
   /** The value of the Riemann Zeta function &#x03B6;(13).
    *  See <a href="http://oeis.org/A013671" target="_top">http://oeis.org/A013671</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_13
    */
   public static final BigDecimal ZETA_13 = new BigDecimal("1.00012271334757848914675183652635739571427510589550984513670267162089672682984420981289271395326813");
   /** The value of the Riemann Zeta function &#x03B6;(14).
    *  See <a href="http://oeis.org/A013672" target="_top">http://oeis.org/A013672</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    */
   public static final BigDecimal ZETA_14  = new BigDecimal("1.00006124813505870482925854510513533374748169616915454948275520225286294102317742087665978297199846");
   /** The value of the Riemann Zeta function &#x03B6;(15).
    *  See <a href="http://oeis.org/A013673" target="_top">http://oeis.org/A013673</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_15
    */
   public static final BigDecimal ZETA_15 = new BigDecimal("1.00003058823630702049355172851064506258762794870685817750656993289333226715634227957307233434701754");
   /** The value of the Riemann Zeta function &#x03B6;(16).
    *  See <a href="http://oeis.org/A013674" target="_top">http://oeis.org/A013674</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    */
   public static final BigDecimal ZETA_16  = new BigDecimal("1.00001528225940865187173257148763672202323738899047153115310520358878708702795315178628560484632246");
   /** The value of the Riemann Zeta function &#x03B6;(17).
    *  See <a href="http://oeis.org/A013675" target="_top">http://oeis.org/A013675</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_17
    */
   public static final BigDecimal ZETA_17 = new BigDecimal("1.00000763719763789976227360029356302921308824909026267909537984397293564329028245934208173863691667");
   /** The value of the Riemann Zeta function &#x03B6;(18).
    *  See <a href="http://oeis.org/A013676" target="_top">http://oeis.org/A013676</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    */
   public static final BigDecimal ZETA_18  = new BigDecimal("1.00000381729326499983985646164462193973045469721895333114317442998763003954265004563800196866898964");
   /** The value of the Riemann Zeta function &#x03B6;(19).
    *  See <a href="http://oeis.org/A013677" target="_top">http://oeis.org/A013677</a>,
    *  or M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_19
    */
   public static final BigDecimal ZETA_19 = new BigDecimal("1.00000190821271655393892565695779510135325857114483863023593304676182394970534130931266422711807630");
   /** The value of the Riemann Zeta function &#x03B6;(20).
    *  See <a href="http://oeis.org/A013678" target="_top">http://oeis.org/A013678</a>.
    */
   public static final BigDecimal ZETA_20 = new BigDecimal("1.00000095396203387279611315203868344934594379418741059575005648985113751373114390025783609797638747");
   /** The value of the Riemann Zeta function &#x03B6;(21).
    *  See M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_21
    */
   public static final BigDecimal ZETA_21 = new BigDecimal("1.00000047693298678781");
   /** The value of the Riemann Zeta function &#x03B6;(23).
    *  See M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_23
    */
   public static final BigDecimal ZETA_23 = new BigDecimal("1.00000011921992596531");
   /** The value of the Riemann Zeta function &#x03B6;(25).
    *  See M. Abramowitz and I. A. Stegun: <i>Handbook of Mathematical Functions</i>, p. 811.
    *  @see Numbers#ZETA_25
    */
   public static final BigDecimal ZETA_25 = new BigDecimal("1.00000002980350351465");

   /** Returns the best rational approximation of a real number <i>x</i>,
     * that is, the integers <i>p, q</i> such that <i>x</i> &#x2248; <i>p/q</i>.
     * The algorithm computes the continued fraction coefficients corresponding to
     * <i>x</i>, where the number of coefficients is bounded by
     * <code>limit</code>. Usually, the value of <code>limit</code> should be about 40.
     * @param x the number to be approximated
     * @param limit the maximum number of continued fraction coefficients being considered
     * @return a two-element array <code>y</code> where <code>y[0]</code> = <i>p</i> and
     * <code>y[1]</code> = <i>q</i> such that <i>x</i> &#x2248; <i>p/q</i>
     * @see #continuedFraction(BigDecimal,int)
     */
   public static BigInteger[] bestRationalApproximation(BigDecimal x, int limit) {
      BigInteger[] cf = continuedFraction(x,limit);
      // q_{k-2} = q1, q_{k-1} = q2, q_{k} = q3:
      BigInteger q1 = ONE, q2 = ZERO, q3 = ONE; // <- k=0
      // p_{k-2} = p1, p_{k-1} = p2, p_{k} = p3:
      BigInteger p1 = ZERO, p2 = ONE, p3 = ZERO; // <- k=0
      for (BigInteger a : cf) {
         // p_{k} = a_{k} p_{k-1} + p_{k-2}:
         p3 = a.multiply(p2).add(p1);
         p1 = p2;
         p2 = p3;
         // q_{k} = a_{k} q_{k-1} + q_{k-2}:
         q3 = a.multiply(q2).add(q1);
         q1 = q2;
         q2 = q3;
      }
      if (x.signum() < 0) p3 = p3.negate();
      return new BigInteger[]{p3,q3};
   }

   /** Returns a BigDecimal whose value is <i>x<sup>n</sup></i>,
    *  using the core algorithm defined in ANSI standard X3.274-1996 with rounding
    *  according to the context {@link #MATH_CONTEXT}.
    *  The absolute value of the parameter <i>n</i> must be in the range 
    *  0 through 999999999, inclusive.
    *  The allowable exponent range of this method depends on the version of
    *  {@link java.math.BigDecimal#pow(int,MathContext)}.
    *  Especially, pow({@link #ZERO_DOT}, 0) returns {@link #ONE_DOT}.
    *  @param x number to be raised to the power
    *  @param n power to raise <i>x</i> to
    *  @return <i>x<sup>n</sup></i>
    */
   public static BigDecimal pow(BigDecimal x, int n) {
      //if (x.equals(ZERO_DOT) && n == 0) return ONE_DOT;
      return x.pow(n, MATH_CONTEXT);
   }

   /** Returns the <i>n</i>th root of <i>z</i>, with an accuracy of
    *  10<sup>-{@value #PRECISION}/2</sup> <i>z</i>.
    *  The root is understood as the principal root <i>r</i> with the unique real
    *  number with the same sign as <i>z</i> such that <i>r<sup>n</sup> = z</i>.
    *  If <i>n</i> = 0, the value of <i>z</i> is returned, if <i>n</i> &lt; 0,
    *  the value {@link #ZERO_DOT 0.0} is returned.
    *  @param n the radical
    *  @param z the radicand
    *  @return the principal <i>n</i>th root <i>r</i> of <i>z</i> such that
    *  <i>r<sup>n</sup> = z</i>
    */
   public static BigDecimal root(int n, BigInteger z) {
      return root(n, new BigDecimal(z));
   }

   /** Returns the <i>n</i>th root of <i>z</i>, with an accuracy of
    *  10<sup>-{@value #PRECISION}/2</sup> <i>z</i>.
    *  The root is understood as the principal root <i>r</i> with the unique real
    *  number with the same sign as <i>z</i> such that <i>r<sup>n</sup> = z</i>.
    *  @param n the radical
    *  @param z the radicand
    *  @return the principal <i>n</i>th root <i>r</i> of <i>z</i> such that
    *    <i>r<sup>n</sup> = z</i>
    *  @throws IllegalArgumentException if <i>n</i> = 0,
    *    or if <i>n</i> is even and <i>z</i> &lt; 0
    */
   public static BigDecimal root(int n, BigDecimal z) {
      return root(n,z,PRECISION);
   }

   /** Returns the <i>n</i>th root of <i>z</i>, with an accuracy
    *  given by 10<sup>-precision/2</sup> <i>z</i>.
    *  The root is understood as the principal root <i>r</i> with the unique real
    *  number with the same sign as <i>z</i> such that <i>r<sup>n</sup> = z</i>.
    *  @param n the radical
    *  @param z the radicand
    *  @param precision the accuracy
    *  @return the principal <i>n</i>th root <i>r</i> of <i>z</i> such that
    *    <i>r<sup>n</sup> = z</i>
    *  @throws IllegalArgumentException if <i>n</i> = 0,
    *    or if <i>n</i> is even and <i>z</i> &lt; 0
    */
   public static BigDecimal root(int n, BigDecimal z, int precision) {
      if (n==0) {
         throw new IllegalArgumentException("Zeroth root does not exist!");
      }

      if (z.equals(ZERO_DOT)) {
         return ZERO_DOT;
      }
      
      byte sign = 1;
      if (z.signum() < 0) {
         if (n % 2 == 0) {
            throw new IllegalArgumentException("" + n + "-th root of a negative number");
         }
         sign = -1;
         z = z.negate();
      }

      MathContext mc = new MathContext(n*precision, RoundingMode.HALF_EVEN);
      if (n < 0) return ONE_DOT.divide(root(-n,z), mc);

      //int scale = precision;
      BigDecimal w, n1, n2, h;
      BigDecimal accuracy = new BigDecimal("1e-"+precision);

      // initial value of w:
      w = ROOT_10_TWO.pow(10 * z.toBigInteger().bitLength() / n, mc);
      n1 = BigDecimal.valueOf(n-1).divide(BigDecimal.valueOf(n), mc);
      n2 = BigDecimal.valueOf(n);
      //while (pow(w,n).subtract(z).abs().compareTo(accuracy) > 0) {
      while (w.pow(n,mc).subtract(z).abs().compareTo(accuracy) > 0) {
         // w = (n-1) w/n + z/(n w^(n-1)):
         w = w.multiply(n1,mc).add(z.divide(w.pow(n-1,mc).multiply(n2,mc), mc));
      }
      // round up to scale digits:
      if (sign < 0) w = w.negate();

      // compute h such that z = w^n (1+h):
      h = z.divide(w.pow(n,mc), mc).subtract(ONE_DOT);

      if (h.compareTo(ONE_DOT) < 0) { // accuracy < 1 always???
         // Taylor expansion of (1+h)^{1/n} to compute w (1+h)^{1/n} = z^{1/n}:
         n2 = ONE_DOT.divide(n2, mc);
         // factor f = 1 + h/n - 1/2n * (n-1)/n * h^2 + 1/6n * (n-1)/n * h^3:
         BigDecimal f = ONE_DOT.add(h.multiply(n2,mc));
         f = f.subtract(ONE_HALF.multiply(n2,mc).multiply(n1,mc).multiply(h.pow(2,mc),mc));
         f = f.add(ONE_SIXTH.multiply(n2,mc).multiply(n1,mc).multiply(TWO_DOT.subtract(n1,mc)).multiply(pow(h,3),mc));
         w = w.multiply(f,mc);
      }
      return w;
   }

   /** Returns the square root of <i>z</i>, with an accuracy of 10<sup>-{@value #PRECISION}/2</sup> <i>z</i>.
    *  The root is understood as the principal root <i>r</i> with the unique real
    *  number with the same sign as <i>z</i> such that <i>r</i><sup>2</sup> = <i>z</i>.
    *  @param z the radicand
    *  @return the principal square root <i>r</i> of <i>z</i> such that
    *  <i>r</i><sup>2</sup> = <i>z</i>
    */
   public static BigDecimal sqrt(BigInteger z) {
      return sqrt(new BigDecimal(z));
   }

   /** Returns the square root of <i>z</i>, with an accuracy of 10<sup>-{@value #PRECISION}/2</sup> <i>z</i>.
    *  The root is understood as the principal root <i>r</i> with the unique real
    *  number with the same sign as <i>z</i> such that <i>r</i><sup>2</sup> = <i>z</i>.
    *  @param z the radicand
    *  @param precision the desired precision
    *  @return the principal square root <i>r</i> of <i>z</i> such that
    *  <i>r</i><sup>2</sup> = <i>z</i>
    */
   public static BigDecimal sqrt(BigDecimal z, int precision) {
      return root(2,z,precision);
   }

   /** Returns the square root of <i>z</i>, with an accuracy of 10<sup>-{@value #PRECISION}/2</sup> <i>z</i>.
    *  The root is understood as the principal root <i>r</i> with the unique real
    *  number with the same sign as <i>z</i> such that <i>r</i><sup>2</sup> = <i>z</i>.
    *  @param z the radicand
    *  @return the principal square root <i>r</i> of <i>z</i> such that
    *  <i>r</i><sup>2</sup> = <i>z</i>
    */
   public static BigDecimal sqrt(BigDecimal z) {
      return root(2,z);
   }

   /** Returns the value of <i>n</i> mod <i>m</i>, even for negative values.
    *  Here the usual periodic definition is used, i.e.,
    *  <p style="text-align:center;">
    *    <i>n</i> mod <i>m</i> = <i>n</i> - &#x23A3;<i>n</i>/<i>m</i>&#x23A6;
    *    &nbsp; for <i>m</i> &#x2260; 0,
    *    &nbsp; and <i>n</i> mod 0 = <i>n</i>.
    *  </p>
    *  For instance,
    *  5 mod 3 = 2, but -5 mod 3 = 1, 5 mod (-3) = -1, and -5 mod (-3) = -2.
    *  See R.L. Graham, D.E. Knuth, O. Patashnik: <i>Concrete Mathematics.</i>
    *  2nd Edition. Addison-Wesley, Upper Saddle River, NJ 1994, &sect;3.4 (p.82)
    *  @param n the value to be computed
    *  @param m the modulus
    *  @return the value <i>n</i> mod <i>m</i>
    */
   public static BigInteger mod(BigInteger n, BigInteger m) {
      //if (m == 0) return n;
      //byte mPositive = 1;
      //if (m < 0) {mPositive = -1; m = -m; n = -n;}
      //return (n >= 0) ? n%m * mPositive : (m + n%m)%m * mPositive;
      if (m.equals(ZERO)) return n;
      if (m.signum() > 0) {
         return n.mod(m);
      } else {
         n = n.negate();
         m = m.negate();
         return n.mod(m).negate();
      }
   }

   /** Returns the value of <i>n</i> mod <i>m</i>, even for negative values.
    *  Here the usual periodic definition is used, i.e.,
    *  5 mod 3 = 2, but -5 mod 3 = 1, 5 mod (-3) = -1, and -5 mod (-3) = -2.
    *  See R.L. Graham, D.E. Knuth, O. Patashnik: <i>Concrete Mathematics.</i>
    *  2nd Edition. Addison-Wesley, Upper Saddle River, NJ 1994, &sect;3.4 (p.82)
    *  @param n the value to be computed
    *  @param m the modulus
    *  @return the value <i>n</i> mod <i>m</i>
    */
   public static BigDecimal mod(BigDecimal n, BigDecimal m) {
      if (m.compareTo(ZERO_DOT) == 0) return n;
      if (n.compareTo(ZERO_DOT) == 0) return n;

      BigDecimal mPositive = ONE_DOT;
      if (m.signum() < 0) {mPositive = mPositive.negate(); m = m.negate(); n = n.negate();}
      BigDecimal r = n.remainder(m);

      if (r.compareTo(ZERO_DOT) == 0) {
         return ZERO_DOT;
      } else if (n.signum() > 0) {
         return r.multiply(mPositive);
      } else {
         return m.add(r).multiply(mPositive);
      }
   }

   /** Returns the value of <i>m<sup>e</sup></i> mod <i>n</i> for a nonnegative
    *  integer exponent <i>e</i> and a positive modulus <i>n</i>.
    *  @param m the value to be raised to the power
    *  @param e the exponent
    *  @param n the modulus
    *  @return the value <i>m<sup>e</sup></i> mod <i>n</i>
    *  @throws ArithmeticException if <i>e</i> &lt; 0 and gcd(<i>x</i>, <i>n</i>) &gt; 1
    */
   public static long modPow(BigInteger m, long e, long n) {
      long x = m.mod(BigInteger.valueOf(n)).longValue();
      if (e < 0) {
         // find the multiplicative inverse of x mod n by the extended Euclid algorithm:
         long[] euclid = Numbers.euclid(x,n);
         if(euclid[0] == 1) { // gcd(x,n) == 1
            x = euclid[1];
            e = -e;
         } else {
            throw new ArithmeticException(
                  "Negative exponent "+e+" is not possible ("+m+ " and "+n+" are not relatively prime)"
            );
         }
      }

      long y = 1;
      if (n > 0) {
         while (e > 0) {
            if ((e & 1L) == 1) { //  <=>  if (e % 2 == 1) {
               y = (y * x) % n;
            }
            x = (x * x) % n;
            e = e >> 1; // <=> e /= 2;
         }
         return y;
      } else {
         while (e > 0) {
            if ((e & 1L) == 1) { //  <=>  if (e % 2 == 1) {
               y = Numbers.mod(y * x, n);
            }
            x = Numbers.mod(x * x, n);
            e = e >> 1; // <=> e /= 2;
         }
         return y;
      }
   }

   /** Returns the value of <i>m<sup>e</sup></i> mod <i>n</i> for a nonnegative
    *  integer exponent <i>e</i> and a positive modulus <i>n</i>.
    *  @param x the value to be raised to the power
    *  @param e the exponent
    *  @param n the modulus
    *  @return the value <i>m<sup>e</sup></i> mod <i>n</i>
    *  @throws ArithmeticException if <i>e</i> &lt; 0 and gcd(<i>x</i>, <i>n</i>) &gt; 1
    */
   public static BigInteger modPow(BigInteger x, BigInteger e, BigInteger n) {
      if (e.signum() < 0) {
         // find the multiplicative inverse of x mod n by the extended Euclid algorithm:
         BigInteger[] euclid = euclid(x,n);
         if (euclid[0].equals(ONE)) { // gcd(x,n) == 1
            x = euclid[1];
            e = e.negate();
         } else {
            throw new ArithmeticException(
                  "Negative exponent "+e+" is not possible ("+x+ " and "+n+" are not relatively prime)"
            );
         }
      }

      BigInteger y = ONE;
      while (e.signum() > 0) {
         if (e.mod(TWO).equals(ONE)) {
            y = mod(y.multiply(x), n);
         }
         x = mod(x.multiply(x), n);
         e = e.divide(TWO);
      }
      return y;
   }

   /** Returns the value of (<i>x<sup>e</sup></i>) mod <i>n</i>.
    *  @param x the value to be raised to the power
    *  @param e the exponent
    *  @param n the modulus
    *  @return the value of <i>x<sup>e</sup></i> mod <i>n</i>
    */
   public static BigDecimal modPow(BigDecimal x, int e, BigDecimal n) {
      return mod(x.pow(e, MATH_CONTEXT), n);
   }

   /** Returns the value of <i>x</i><sup>2</sup>.
    *  @param x the value to be squared
    *  @return the square of the input <i>x</i>, i.e., <i>x</i><sup>2</sup>
    */
   public static BigInteger sqr(BigInteger x) {
      return x.multiply(x);
   }

   /** Returns an array of three integers <i>x</i>[0], <i>x</i>[1], <i>x</i>[2]
    *  as given by the extended Euclidian algorithm for integers <i>m</i> and <i>n</i>.
    *  The three integers satisfy the equations
    *  <p style="text-align:center">
    *    <i>x</i>[0] = gcd(<i>m</i>, <i>n</i>)
    *                = <i>x</i>[1] <i>m</i> + <i>x</i>[2] <i>n</i>.
    *  </p>
    *  This methods implements an iterative version of the extended Euclidian algorithm.
    *  @param m the first integer
    *  @param n the second integer
    *  @return an array of three integers <i>x</i>[0], <i>x</i>[1], <i>x</i>[2]
    *    such that <i>x</i>[0] = gcd(<i>m</i>, <i>n</i>)
    *    = <i>x</i>[1] <i>m</i> + <i>x</i>[2] <i>n</i>
    *  @see Numbers#euclid(long,long)
    */
   public static BigInteger[] euclid(BigInteger m, BigInteger n) {
       BigInteger x[] = { ZERO, ONE, ZERO };
       BigInteger u = ZERO, v = ONE,
                  q, r, tmp;
       boolean mNegative = false, nNegative = false;

       if (m.signum() < 0) { m = m.negate(); mNegative = true; }
       if (n.signum() < 0) { n = n.negate(); nNegative = true; }

       while (n.signum() > 0) {
           // determine q and r such that m = qn + r:
           q = m.divide(n); r = m.mod(n);
           // replace m <- n and n <- r:
           m = n; n = r;
           // replace:
           tmp = u; u = x[1].subtract(q.multiply(u)); x[1] = tmp;
           tmp = v; v = x[2].subtract(q.multiply(v)); x[2] = tmp;
       }
       x[0] = m;
       if  (mNegative) x[1] = x[1].negate();
       if  (nNegative) x[2] = x[2].negate();
       return x;
   }

   /** Returns the least common multiple of <i>m</i> and <i>n</i>.
    *  @param m the first integer
    *  @param n the second integer
    *  @return the least common multiple of <i>m</i> and <i>n</i>
    */
   public static BigInteger lcm(BigInteger m, BigInteger n) {
      return m.multiply(n).divide(m.gcd(n));
   }

   /** Tests deterministically whether the given integer <i>n</i> is prime.
    *  This algorithm first uses the method
    *  {@link java.math.BigInteger#isProbablePrime(int) isProbablePrime}
    *  of the class {@link java.math.BigInteger} which yields false if
    *  the given number is not prime with certainty.
    *  @param n the integer to test
    *  @return true if and only if <i>n</i> is prime.
    */
   public static boolean isPrime(BigInteger n) {
      if (n.equals(TWO))   return true;
      if (n.equals(THREE)) return true;
      if (n.signum() < 0 || n.mod(TWO).equals(ZERO) || n.mod(THREE).equals(ZERO)) {
         return false;
      }

      if (!n.isProbablePrime(1000)) return false; // definitely not prime!
      //System.out.println(n + " is probably prime!");

      BigInteger FOUR = BigInteger.valueOf(4);
      boolean bigstep = false;  // flag for wheel 2-4-2-4-2-...
      BigInteger i = BigInteger.valueOf(5);
      BigInteger iMax = sqrt(n).toBigInteger();

      while (i.compareTo(iMax) <= 0) {
         if (n.mod(i).equals(ZERO)) {
            return false;
         }
         //<=>  flag = !(n.mod(i).equals(ZERO));
         i = bigstep ? i.add(FOUR) : i.add(TWO);
         bigstep = !bigstep;
      }
      return true;
   }

   /** Returns true if <i>n</i> is a strong probable prime to base <i>a</i>,
    *  and false if <i>n</i> is not prime.
    *  This algorithm is the core of the Miller-Rabin primality test,
    *  cf. R. Crandall &amp; C. Pomerance:
    *  <i>Prime Numbers. A Computational Perspective.</i> 2<sup>nd</sup> edition.
    *  Springer, New York 2005, &sec;3.5.
    *  The number <i>n</i> must be an odd number &gt; 3, and 1 &lt; <i>a</i> &lt; n-1.
    *  @param n the number to be tested on strong probable primality
    *  @param a the base of the strong probable primality test
    *  @return true if <i>n</i> is a strong probable prime to base <i>a</i>,
    *  and false if <i>n</i> is not prime
    *  @throws IllegalArgumentException if <i>n</i> &le; 3, or <i>a</i> &le; 1,
    *  or <i>a</i> &ge; <i>n</i> - 1
    *  @see Numbers#isStrongProbablePrime(int,int)
    */
   public static boolean isStrongProbablePrime(BigInteger n, BigInteger a) {
      if (n.compareTo(THREE) <= 0 || a.compareTo(ONE) <= 0 || a.compareTo(n.subtract(ONE)) >= 0) {
         throw new IllegalArgumentException("n="+n+", a="+a);
      }

      // Determine s and t such that n-1 = t*2^s:
      int s = 0;
      BigInteger t = n.subtract(ONE);
      while (t.mod(TWO).signum() == 0) {
         t = t.divide(TWO);
         s++;
      }

      // Test the odd part t of n-1:
      BigInteger b = a.modPow(t, n);
      if (b.equals(ONE) || b.equals(n.subtract(ONE))) {
         return true;
      }

      // Test the power of 2 in n-1:
      for (int j = 1; j < s; j++) {
         b = b.multiply(b).mod(n);
         if (b.equals(n.subtract(ONE))) {
            return true;
         }
      }
      return false;
   }

   /**
    *  The AKS primality test, returns true if the integer <i>n</i> &gt; 1 is prime.
    *  This test has a polynomial time complexity with respect to log <i>n</i>,
    *  proving that the decision problem whether a given integer is prime, is
    *  in the complexity class <b>P</b>.
    *  @param n an integer &gt; 1
    *  @return true if and only if <i>n</i> is prime
    */
   public static boolean primalityTestAKS(BigInteger n) {
      if (n.mod(TWO).equals(ZERO)) return false;

      // Step 1:
      if (isPower(n)) return false;
      
      boolean rDivides = true; // flag needed to find r
      int lgn = n.bitLength();
      int minOrder = (30 + lgn)*(30 + lgn)/12; // <- Ralf Meyer
      //int minOrder = lgn*lgn;
      int r = 1;
      int a, k;
      PolynomialZ poly1, poly2;
      BigInteger r_;

      // Step 2: Determine the smallest r such that r does not divide n^k for all k <= minOrder
      while (rDivides) {
         r++; // ??? r += 2;
         r_ = BigInteger.valueOf(r);
         // return if gcd(n,r) > 1:
         if (n.gcd(r_).compareTo(ONE) > 0) {
            return n.equals(r_);
         }

         k = 0;
         rDivides = false;
         while (k <= minOrder && !rDivides) {
            k++;
            // r divides n^k - 1?
            rDivides = n.pow(k).subtract(ONE).mod(r_).equals(ZERO);
         }
      }

      /*
      for (a = 1; a <= end; a++) {
         // poly1 = (x + a)^n  mod  (x^r - 1, n)
         poly1 = new PolynomialZ();
         poly1.put(ONE, ONE);
         poly1.put(ZERO, BigInteger.valueOf(a));
         poly1 = poly1.modPow(n, r, n);
         // poly2 = x^n + a   mod  (x^r - 1, n)
         poly2 = new PolynomialZ();
         poly2.put(n.mod(BigInteger.valueOf(r)), ONE);
         poly2.put(ZERO, (BigInteger.valueOf(a)).mod(n));
         if (!poly1.equals(poly2)) return false;
      }
      return true;
      // */
      // /*
      // geht auch??
      a = 1;
      poly1 = new PolynomialZ();
      poly1.put(ONE, ONE);
      poly1.put(ZERO, BigInteger.valueOf(a));
      poly1 = poly1.modPow(n, r, n);
      poly2 = new PolynomialZ();
      poly2.put(n.mod(BigInteger.valueOf(r)), ONE);
      poly2.put(ZERO, (BigInteger.valueOf(a)).mod(n));
      return poly1.equals(poly2);
      // */
   }

   /**
    * Test for reduced AKS algorithm (probably wrong!)
    * @param n the integer to be analyzed
    * @param r the polynomial degree
    * @return true iff <i>n</i> is prime (?)
    */
   public static boolean nakedAKS(BigInteger n, int r) {
      //if (n.mod(TWO).equals(ZERO)) return false;
      int a;
      PolynomialZ poly1, poly2;

      a = 1;
      // poly1 = (x - a)^n mod (x^r - 1, n)
      poly1 = new PolynomialZ();
      poly1.put(ONE, ONE);
      poly1.put(ZERO, BigInteger.valueOf(a));
      poly1 = poly1.modPow(n, r, n);
      poly2 = new PolynomialZ();
      poly2.put(n.mod(BigInteger.valueOf(r)), ONE);
      poly2.put(ZERO, (BigInteger.valueOf(a)).mod(n));
//System.out.println("n="+n+", r="+r+", poly1="+poly1+", poly2="+poly2);
      return poly1.equals(poly2);
      // */
   }
   
   /** Returns the order ord(<i>m,n</i>) of <i>m</i> modulo <i>n</i>.
    *  More precisely, we have
    *  <!--
    *
    *     ord(m, n) = min_i { i > 0 : m^i = 1 mod n },
    *
    *  -->
    *  <p style="text-align:center">
    *
    *     ord(<i>m, n</i>) = min<sub><i>i</i></sub> { <i>i</i> &gt; 0 : <i>m<sup>i</sup></i> = 1 mod <i>n</i> },
    *
    *  </p>
    *  The order is computed by Floyd's cycle finding algorithm.
    * @param m the number of which the order is computed
    * @param n the modulus
    * @return the order of <i>m</i> mod <i>n</i>
    */
   public static BigInteger ord(BigInteger m, BigInteger n) {
      BigInteger i, x, y;
      m = m.mod(n);
      if (!m.gcd(n).equals(ONE)) {
         i = ZERO;
      } else {
         i = ONE;
         x = m;
         y = m.multiply(x).mod(n);
         while (!x.equals(y)) {
            // i++; x = (m*x) mod n; y = (m*y)^2 mod n:
            i = i.add(ONE);
            x = m.multiply(x).mod(n);
            y = m.multiply(m).multiply(y).mod(n);
         }
      }
      return i;
   }

   /** Tests whether there exist integers <i>m</i> and <i>k</i> such that
    *  <i>n = m<sup>k</sup></i>.
    *  @param n the number to be checked
    *  @return true if and only if there exist integers <i>m</i> and <i>k</i> such that
    *  <i>n = m<sup>k</sup></i>
    */
   public static boolean isPower(BigInteger n) {
      BigDecimal temp;
      BigDecimal nBD = new BigDecimal(n);
      int greatexp = n.bitLength();  // = [log_2(n)]
      int k = 2;

      while (k <= greatexp) {
         temp = pow(root(k, n).setScale(0, RoundingMode.HALF_UP), k);
         if (nBD.compareTo(temp.setScale(0, RoundingMode.HALF_UP)) == 0) {
            return true;
         }
         k++;
      }
      return false;
   }


   /** Returns an array containing coefficients of the continued fraction of
    *  the number <i>x</i>, with at most <code>limit</code> coefficients.
    *  Note that by the finite precision of <i>x</i> the higher continuous fraction
    *  coefficients get more and more imprecise. For instance, for the
    *  Euler number the coefficients are correct up to the limit 87,
    *  for Ap&eacute;ry's constant up to the limit 100,
    *  <a href="http://oeis.org/A013631" target="_top">http://oeis.org/A013631</a>,
    *  for &#x03C0; up to the limit 43, cf.
    *  <a href="http://oeis.org/A001203" target="_top">http://oeis.org/A001203</a>.
    *  @param x the number to be expanded as a continuous fraction
    *  @param limit the maximum number of continuous fraction coefficients to be computed
    *  @return an array of length <code>limit</code> containing the continuous fraction coefficients
    */
   public static BigInteger[] continuedFraction(BigDecimal x, int limit) {
      MathContext mc = MATH_CONTEXT;
      if (x.scale() > PRECISION) {
         //System.out.println("MATH_CONTEXT set to scale="+x.scale());
         mc = new MathContext(x.scale());
      }
      double accuracy = 1e-50;
      BigDecimal precision = BigDecimal.valueOf(accuracy);

      if (x.signum() == 0) return new BigInteger[]{ZERO};
      if (x.signum() <  0) x = x.negate();
      BigInteger[] a = new BigInteger[limit];
      if (limit <= 0) return a;
      int i = 0;
      BigDecimal xi;
      if (x.compareTo(ONE_DOT) >= 0) {
         //xi = 1/x;
         xi = ONE_DOT.divide(x, mc);
      } else {
         xi = x;
         a[0] = ZERO;
         i++;
      }
      while (xi.compareTo(precision) > 0 && i < limit) {
         //a[i] = (long) (1/xi);
         a[i] = ONE_DOT.divide(xi, mc).toBigInteger();
         //xi = 1/xi - a[i] = 1/xi - (long) (1/xi);
         xi = ONE_DOT.divide(xi, mc).subtract(new BigDecimal(a[i]));
         i++;
      }
      BigInteger[] result = new BigInteger[i];
      System.arraycopy(a, 0, result, 0, result.length);
      return result;
   }

   /** Returns Euler's number e raised to the power of <i>x</i>.
    *  The value is computed up to an accuracy of 10<sup>-100</sup>.
    *  @param x the exponent
    *  @return the number <i>e<sup>x</sup></i>
    *  @see #E
    */
   public static BigDecimal exp(BigDecimal x) {
      int n = 70; // yields an accuracy of 10^{-100}
      // Treating of very large numbers remains to be done!!!
      return exp(x,n);
   }

   /** Returns the exponential value e<sup><i>x</i></sup> of a number <i>x</i>,
    *  up to an approximation order of <i>n</i>.
    *  For <i>n</i> = 70, the value is computed up to an accuracy of 10<sup>-100</sup>.
    *  The range of the integral part of <i>x</i> is limited according to
    *  the method {@link java.math.BigDecimal#pow(int,MathContext)}.
    *  @param x the number
    *  @param n the order of approximation
    *  @return the number e<i><sup>x</sup></i>, up to the approximation order <i>n</i>
    */
   public static BigDecimal exp(BigDecimal x, int n) {
      MathContext mc = new MathContext(100, RoundingMode.HALF_EVEN);
      BigDecimal[] xMod1 = x.divideAndRemainder(ONE_DOT, mc);
      xMod1[0] = E.pow(xMod1[0].intValue(), mc);
      x = xMod1[1];
      // Forster I, p.47 (a_k = 1/(n-k)!):
      BigDecimal y = ONE_DOT.divide(new BigDecimal(factorial(n)), mc);
      for (int k = n-1; k >= 0; k--) {
         y = y.multiply(x).add(ONE_DOT.divide(new BigDecimal(factorial(k)), mc));
      }
      return y.multiply(xMod1[0]);
   }

   /** Returns the natural logarithm of a number <i>x</i>.
    *  The value is computed up to an accuracy of 10<sup>-{@link #PRECISION}</sup>.
    *  @param x the number
    *  @return the natural logarithm of the argument
    */
   public static BigDecimal ln(BigInteger x) {
      return ln(new BigDecimal(x),PRECISION);
   }

   /** Returns the natural logarithm of a number <i>x</i>.
    *  The value is computed up to an accuracy of 10<sup>-{@link #PRECISION}</sup>.
    *  @param x the number
    *  @return the natural logarithm of the argument
    */
   public static BigDecimal ln(BigDecimal x) {
      return ln(x,PRECISION);
   }

   /** Returns the natural logarithm of a number <i>x</i>, up to an approximation
    *  order of <i>n</i>.
    *  With <i>n</i>, the value is computed up to an accuracy of 10<sup>-n</sup>.
    *  @param x the number
    *  @param n the order up to which the approximation is computed
    *  @return the natural logarithm of the the argument
    */
   public static BigDecimal ln(BigDecimal x, int n) {
      if (x.signum() <= 0) {
         throw new IllegalArgumentException("ln(" + x.setScale(5,RoundingMode.HALF_EVEN)+") not defined");
      }
      int precision = 100;
      // Determine maximum m such that e^m <= x (where m = 0 if x < 1):
      int m = 0;
      while (E.pow(m+1).compareTo(x) < 0) { m++; }
      // x = x / e^m:
      //x = x.divide(E.pow(m), MATH_CONTEXT); // <- does not modify the scale of x!!
      x = x.divide(E.pow(m), precision, RoundingMode.HALF_EVEN);
      // y = (x-1) / (x+1):
      BigDecimal y = x.subtract(ONE_DOT).divide(x.add(ONE_DOT), RoundingMode.HALF_EVEN);
      BigDecimal ln = y;

      for (int k = 1; k <= n; k++) {
         // ln = ln + y^(2k+1) / (2k+1):
         ln = ln.add(y.pow(2*k+1).divide(BigDecimal.valueOf(2*k+1), RoundingMode.HALF_EVEN));
      }
      return ln.multiply(TWO_DOT).add(BigDecimal.valueOf(m));
   }

   // Horner scheme:
   /** Returns the dual logarithm of a number <i>x</i>, up to an approximation
    *  order of <i>n</i>.
    *  With <i>n</i>, the value is computed up to an accuracy of 10<sup>-n</sup>.
    *  @param x the number
    *  @param n the order up to which the approximation is computed
    *  @return the dual logarithm of the the argument
    */
   public static BigDecimal ln2(BigDecimal x, int n) {
      if (x.signum() <= 0) {
         throw new IllegalArgumentException("ln(" + x.setScale(5,RoundingMode.HALF_EVEN)+") not defined");
      }
      int precision = 100;
      // Determine maximum m such that e^m <= x (where m = 0 if x < 1):
      int m = 0;
      while (E.pow(m+1).compareTo(x) < 0) { m++; }
      // x = x / e^m:
      x = x.divide(E.pow(m), precision, RoundingMode.HALF_EVEN);
      // y = (x-1) / (x+1):
      BigDecimal y = x.subtract(ONE_DOT).divide(x.add(ONE_DOT), RoundingMode.HALF_EVEN);
      // Horner scheme: ln y = 2 y (sum (y^2)^k / (2k+1)):
      BigDecimal y2 = y.multiply(y, new MathContext(precision, RoundingMode.HALF_EVEN));
      BigDecimal ln = TWO_DOT.divide(BigDecimal.valueOf(2*n+1), precision, RoundingMode.HALF_EVEN);

      for (int k = n - 1; k >= 0; k--) {
         // ln = ln * y^2 + a_{n-k}:
         ln = ln.multiply(y2).add(TWO_DOT.divide(BigDecimal.valueOf(2*k+1), precision, RoundingMode.HALF_EVEN));
      }
      return ln.multiply(y).add(BigDecimal.valueOf(m));
   }

   /** Returns the exact value of <i>&pi;</i> up th the
    *  specified precision.
    *  @param precision the length of the representation of <i>&pi;</i>
    *  @return the value of <i>&pi;</i> in hexadecimal expansion up to the specified precision
    */
   public static BigDecimal pi(int precision) {
      // use Bailey-Borwein-Plouffe algorithm to compute the hexadecimal ciphers:
      String hex = "3.";
      for (int i = 1; i <= precision; i++) {
         hex += Numbers.bbp(i);
      }
      return hexToBigDecimal(hex, precision);
   }

   /** Returns the exact value of <i>&pi;</i> up th the
    *  specified precision and the number system.
    *  @param precision the length of the representation of <i>&pi;</i>
    *  @param radix the radix of the number system
    *  @return the natural logarithm of the the argument
    *  @throws IllegalArgumentException if the radix is not known
    */
   public static String pi(int precision, int radix) {
      // use Bailey-Borwein-Plouffe algorithm to compute the hexadecimal ciphers:
      String hex = "3.";
      for (int i = 1; i <= precision; i++) {
         hex += Numbers.bbp(i);
      }
      if (radix == 2) {
         return Numbers.hexToBin(hex);
      } else if (radix == 3) {
         return decToTern(hexToBigDecimal(hex, precision),precision);
      } else if (radix == -3) {
         return decToTernB(hexToBigDecimal(hex, precision),precision);
      } else if (radix == 10) {
         return hexToBigDecimal(hex, precision).toPlainString();
      } else if (radix == 16) {
         return hex;
      } else {
         throw new IllegalArgumentException("Radix "+radix+" not implemented");
      }
   }

   /** Returns the arc tangent of a value; the returned angle is in the range
    *  -&#x03C0;/2 through &#x03C0;/2.
    *  The precision of the returned value is at least 10<sup>-100</sup>.
    *  @param x a number
    *  @return the arc tangent of the argument
    *  @see #arctan(BigDecimal,int)
    */
   public static BigDecimal arctan(BigDecimal x) {
      return arctan(x,126);
   }

   /** Returns the trigonometric cosine of an angle <i>x</i>.
    *  The value is computed up to an accuracy of 10<sup>-100</sup>.
    *  @param x the angle in radians
    *  @return the cosine of the the argument
    *  @see #cos(BigDecimal,int)
    */
   public static BigDecimal cos(BigDecimal x) {
      int order = 32;  // 32 => precision of 100 decimal digits

      if (x.compareTo(ZERO_DOT) == 0) {
         return ONE_DOT;
      } else {
         // x = x % (2 pi):
         x = mod(x,PI.multiply(TWO_DOT));
         if (x.signum() < 0) {
            x = x.negate();
         }
         if (x.compareTo(PI_4) < 0) {          // x < pi/4
            // cos(x,n):
            return cos(x,order);
         } else if (x.compareTo(PI3_4) < 0) {  // x < 3pi/4
            // sin(pi/2 - x,n):
            return sin(PI_2.subtract(x),order);
         } else if (x.compareTo(PI5_4) <= 0) { // x <= 5pi/4
            // - cos(pi - x):
            return cos(PI.subtract(x),order).negate();
         } else if (x.compareTo(PI7_4) < 0) {  // x < 7pi/4
            // - sin(x - 3pi/2):
            return cos(x.subtract(PI3_2),order).negate();
         } else {                              // x >= 7pi/4
            // cos(x - 2pi):
            return cos(x.subtract(PI.multiply(TWO_DOT)),order);
         }
      }
   }

   /** Returns the trigonometric sine of an angle <i>x</i>.
    *  The value is computed up to an accuracy of 10<sup>-100</sup>.
    *  @param x the angle in radians
    *  @return the sine of the the argument
    *  @see #sin(BigDecimal,int)
    */
   public static BigDecimal sin(BigDecimal x) {
      int order = 32;  // 32 => precision of 100 decimal digits

      if (x.compareTo(ZERO_DOT) == 0) {
         return x;
      } else {
         // x = x % (2 pi):
         x = mod(x,PI.multiply(TWO_DOT));
         BigDecimal sign = ONE_DOT;
         if (x.signum() < 0) {
            sign = sign.negate();
            x = x.negate();
         }
         if (x.compareTo(PI_4) < 0) {          // x < pi/4
            // sign * sin(x,n):
            return sin(x,order).multiply(sign);
         } else if (x.compareTo(PI3_4) < 0) {  // x < 3pi/4
            // sign * cos(pi/2 - x,n):
            return cos(PI_2.subtract(x),order).multiply(sign);
         } else if (x.compareTo(PI5_4) <= 0) { // x <= 5pi/4
            // sign * sin(pi - x):
            return sin(PI.subtract(x),order).multiply(sign);
         } else if (x.compareTo(PI7_4) < 0) {  // x < 7pi/4
            // - sign * cos(x - 3pi/2):
            return cos(x.subtract(PI3_2),order).negate().multiply(sign);
         } else {                              // x >= 7pi/4
            // sign * sin(x - 2pi):
            return sin(x.subtract(PI.multiply(TWO_DOT)),order).multiply(sign);
         }
      }
   }

   /** Returns the arc tangent of a value up to an approximation order <i>n</i>;
    *  the returned angle is in the range -&#x03C0;/2 through &#x03C0;/2.
    *  For even <i>n</i> &#x2264; 130, the precision of the returned value is about
    *  10<sup>-0.8<i>n</i></sup>
    *  in the worst case. The most imprecise values to be computed are
    *  <i>x</i><sub>1</sub> = &#x221A;2 - 1 and <i>x</i><sub>2</sub> = &#x221A;2 + 1
    *  (= 1/<i>x</i><sub>1</sub>).
    *  For <i>n</i> = 126, the precision of the returned value is about
    *  10<sup>-100</sup> for these argument values.
    *  @param x a number
    *  @param n the number of iterations
    *  @return the arc tangent of the argument
    */
   public static BigDecimal arctan(BigDecimal x, int n) {
      int precision = 100;
      //if (x.compareTo(ZERO_DOT) == 0) return ZERO_DOT;
      if (x.abs().compareTo(new BigDecimal("1E-"+(precision+1))) < 0) return ZERO_DOT;
      if (x.signum() < 0) return arctan(x.negate(),n).negate();
      if (x.compareTo(ONE_DOT) == 0) return PI_4;

      /* The following 2 conditions cause 2 recursion calls if
         x > sqrt(2) - 1   (<=> (x-1)/(x+1) > 1/(sqrt(2) - 1) = sqrt(2) + 1) */
      if (x.compareTo(ONE_DOT) > 0) {
         // arctan x = pi/4 + arctan((x-1) / (x+1)) for x > 0:
         // x  <-  (x-1) / (x+1):
         x = x.subtract(ONE_DOT).divide(x.add(ONE_DOT), precision, RoundingMode.HALF_EVEN);
         return PI_4.add(arctan(x,n));
      }

      if (x.compareTo(SQRT_TWO.subtract(ONE_DOT)) > 0) {
         // arctan x = pi/4 - arctan((1-x) / (x+1)) for x > 0:
         // x  <-  (1-x) / (x+1):
         x = ONE_DOT.subtract(x).divide(x.add(ONE_DOT), precision, RoundingMode.HALF_EVEN);
         return PI_4.subtract(arctan(x,n));
      }

      BigDecimal atan;
      boolean plusSwitch = false;
      // Horner scheme: arctan x = x (sum (x^2)^k / (2k+1)):
      BigDecimal x2 = x.multiply(x, new MathContext(precision, RoundingMode.HALF_EVEN));
      atan = ONE_DOT.divide(BigDecimal.valueOf(2*n+1), precision, RoundingMode.HALF_EVEN);
      if (n % 2 != 0) {
         atan.negate();
         plusSwitch = !plusSwitch;
      }

      for (int k = n - 1; k >= 0; k--) {
         // atan = atan * x^2 + (-1)^(2[k/2]) a_{n-k}:
         if (plusSwitch) {
            atan = atan.multiply(x2).add(ONE_DOT.divide(BigDecimal.valueOf(2*k+1), precision, RoundingMode.HALF_EVEN));
         } else {
            atan = atan.multiply(x2).subtract(ONE_DOT.divide(BigDecimal.valueOf(2*k+1), precision, RoundingMode.HALF_EVEN));
         }
         plusSwitch = !plusSwitch;
      }
      return atan.multiply(x);
   }

   /** Returns the trigonometric sine of an angle <i>x</i>
    *  with |<i>x</i>| &#x2264; &#x03C0;/4, up to the approximation order <i>n</i>.
    *  For <i>n</i> = 32, the precision of the returned value is about 10<sup>-100</sup>.
    *  Note, however, that the precision depends on the scale of the argument <i>x</i>,
    *  i.e., a low scale may result in a low precision.
    *  @param x the angle in radians
    *  @param n the number of iterations
    *  @return the sine of the argument
    */
   public static BigDecimal cos(BigDecimal x, int n) {
      BigDecimal cos = ONE_DOT;

      for (int k = 1; k <= n; k++) {
         if (k % 2 == 0) {
            cos = cos.add(x.pow(2*k).divide(new BigDecimal(factorial(2*k)),RoundingMode.HALF_EVEN));
         } else {
            cos = cos.subtract(x.pow(2*k).divide(new BigDecimal(factorial(2*k)),RoundingMode.HALF_EVEN));
         }
      }
      return cos;
   }

   /** Returns the trigonometric sine of an angle <i>x</i>
    *  with |<i>x</i>| &#x2264; &#x03C0;/4, up to the approximation order <i>n</i>.
    *  For <i>n</i> = 32, the precision of the returned value is about 10<sup>-100</sup>.
    *  @param x the angle in radians
    *  @param n the number of iterations
    *  @return the sine of the argument
    */
   public static BigDecimal sin(BigDecimal x, int n) {
      BigDecimal sin = x;

      for (int k = 1; k <= n; k++) {
         if (k % 2 == 0) {
            sin = sin.add(x.pow(2*k+1).divide(new BigDecimal(factorial(2*k+1)),RoundingMode.HALF_EVEN));
         } else {
            sin = sin.subtract(x.pow(2*k+1).divide(new BigDecimal(factorial(2*k+1)),RoundingMode.HALF_EVEN));
         }
      }
      return sin;
   }

   /** Returns the <i>n</i>-th Brownian number with respect to the specified radix.
    * The <i>n</i>-th Brownian number <i>b<sub>n</sub></i> is defined as
    * <p style="text-align:center">
    *   <i>b<sub>n</sub></i> = sqrt(<i>f</i>(<i>n</i>))
    * </p>
    * where <i>f</i> is defined by the recursion
    * <i>f</i>(<i>n</i>) = radix * <i>f</i>(<i>n</i> - 1).
    * The Brownian numbers were introduced by Kevin Brown.
    * For details, see J.-P. Delahaye:
    * <i>Mathématiques pour le plaisir. Un inventaire de curiosités</i>.
    * Belin, Paris 2010, p. 140.
    * @param n an integer
    * @param precision the number of digits which are calculated
    * @return the <i>n</i>-th Brownian number <i>b<sub>n</sub></i>
    */
   public static BigDecimal brown(int n, int precision) {
      return sqrt(new BigDecimal(brownSub(n)), precision);
   }

   private static BigInteger brownSub(int n) {
      if (n <= 0) {
         return ZERO;
      } else {
         return TEN.multiply(brownSub(n-1)).add(BigInteger.valueOf(n));
      }
   }

   /** Returns a binary string as an integer.
    *  @param bin the binary string to be represented in decimal form
    *  @return the BigInteger representing the binary string
    *  @throws NumberFormatException if the string is not binary
    */
   public static BigInteger binToDec(String bin) {
     BigInteger base = TWO;
     boolean negative = false;
     BigInteger a_i;
     BigInteger n = ZERO;

     if (bin.substring(0,1).equals("-")) {
        negative = true;
        bin = bin.substring(1);
     }
     if (bin.substring(bin.length()-1, bin.length()).equals("-")) {
        negative = true;
        bin = bin.substring(0, bin.length() - 1);
     }

     for (int i = 0; i < bin.length(); i++) {
        a_i = new BigInteger(bin.substring(i, i+1));
        if (a_i.compareTo(ONE) > 0) {
           throw new NumberFormatException ("No binary number");
        }
        n = n.add(a_i.multiply(base.pow(bin.length() - i - 1)));
     }

     if (negative)  n = n.negate();
     return n;
   }

   /** Returns <i>n</i> as a binary string.
    * @param n the decimal value to be represented in binary form.
    * @return the binary representation of <i>n</i>
    * @see #decToBin(BigDecimal, int)
    */
    public static String decToBin(BigInteger n) {
      final BigInteger base = TWO;
      boolean negative = false;
      String symbols = "";
      BigInteger q = n;
      int r;

      if (n.compareTo(ZERO) == 0) {
        symbols = "0";
      } else {
         if (n.compareTo(ZERO) < 0) {
            q = q.negate();
            negative = true;
         }

         while (q.compareTo(ZERO) > 0) {
            r = q.mod(base).intValue();
            symbols = r + symbols;
            q = q.divide(base);
         }
      }
      if (negative)  symbols = "-" + symbols;
      return symbols;
   }

   /** Returns <i>n</i> as a binary string of the specified minimum length.
    * @param n the decimal value to be represented in binary form
    * @param minimumLength the minimum length of the returned binary string
    * @return string representing the binary representation of n
    */
   public static String decToBin(BigInteger n, int minimumLength) {
      String out = decToBin(n);
      // pad with zeros:
      for (int i = out.length(); i < minimumLength; i++) {
         out = "0" + out;
      }
      return out;
   }

   /** Returns <i>z</i> as a hexadecimal string with at most <code>limit</code>
    *  positions right of the binary point.
    * @param z the decimal value to be represented in hexadecimal form.
    * @param limit the maximum position after the binary point.
    * @return the binary representation of <i>z</i>
    */
   public static String decToBin(BigDecimal z, int limit) {
      final BigDecimal base = TWO_DOT;
      boolean negative = false;
      String symbols = "";

      if (z.compareTo(ZERO_DOT) == 0) {
         symbols = "0";
      } else {
         if (z.compareTo(ZERO_DOT) < 0) {
            z = z.negate();
            negative = true;
         }
         if (z.compareTo(ONE_DOT) >= 0) {
            symbols = decToBin(z.toBigInteger()) + ".";
         }

         z = z.subtract(new BigDecimal(z.toBigInteger())); //z -= (int) z;
         z = z.multiply(base); //z *= base;
         int r;
         int counter = 0;
         while (z.compareTo(ZERO_DOT) > 0 && counter <= limit) {
            r = z.intValue(); //(int) z;
            symbols += r;
            z = z.subtract(new BigDecimal(z.toBigInteger())); //z -= (int) z;
            z = z.multiply(base); //z *= base;
            counter++;
         }
      }

      if (negative)  symbols = "-" + symbols;
      return symbols;
   }

   /** Returns a binary string as a decimal floating-point number.
    *  @param bin the binary string to be represented in decimal form.
    *  @return the double number representing the binary string
    *  @throws NumberFormatException if the string is not binary
    */
   public static BigDecimal binToBigDecimal(String bin) {
      return binToBigDecimal(bin, MATH_CONTEXT);
   }
   
   /** Returns a binary string as a decimal floating-point number.
    *  @param bin the binary string to be represented in decimal form.
    *  @param length precision (number of digits)
    *  @return the double number representing the binary string
    *  @throws NumberFormatException if the string is not binary
    */
   public static BigDecimal binToBigDecimal(String bin, int length) {
      return binToBigDecimal(bin, new MathContext(length, RoundingMode.HALF_EVEN));
   }

   /** Returns a binary string as a decimal floating-point number.
    *  @param bin the binary string to be represented in decimal form.
    *  @param mc MATH_CONTEXT (precision and rounding mode)
    *  @return the double number representing the binary string
    *  @throws NumberFormatException if the string is not binary
    */
   public static BigDecimal binToBigDecimal(String bin, MathContext mc) {
      BigDecimal base = TWO_DOT;
      boolean negative = false;
      String symbol;
      double a_i;
      BigDecimal x;
      int point; // position of hexadecimal point

      if (bin.substring(0,1).equals("-")) {
        negative = true;
        bin = bin.substring(1);
      }
      if (bin.substring(bin.length()-1, bin.length()).equals("-")) {
        negative = true;
        bin = bin.substring(0,bin.length() - 1);
      }

      point = bin.indexOf('.');

      if (point == -1) {  // the string represents an integer!
         return new BigDecimal(negative? binToDec(bin).negate() : binToDec(bin));
      }

      if (point == 0) {
         bin = "0" + bin;
         point = 1;
      }

      x = new BigDecimal(binToDec(bin.substring(0,point)));

      bin = bin.substring(point+1, bin.length());

      for (int i = 0; i < bin.length(); i++) {
        symbol = bin.substring(i, i+1);
        a_i = Integer.parseInt(symbol);
        if (a_i > 1) {
           throw new NumberFormatException ("No binary number \""+bin+"\"");
        }
        x = x.add(BigDecimal.valueOf(a_i).divide(base.pow(i+1, mc), mc));
      }
      return negative? x.negate() : x;
   }

  /** Returns <i>n</i> as a ternary string.
   * @param n the decimal value to be represented in ternary form
   * @return string representing the ternary representation of n
   */
   public static String decToTern(BigInteger n) {
      BigInteger base = THREE;
      boolean negative = false;
      String symbols = "";
      BigInteger q = n, r;
      
      if (n.equals(ZERO)) {
         symbols = "0";
      } else {
         if (n.signum() < 0) {
            q = q.negate();
            negative = true;
         }
         
         while ( q.signum() > 0) {
            r = q.mod(base);
            symbols = r + symbols;
            q = q.divide(base); // q /= base;
         }
      }
      if (negative) symbols = "-" + symbols;
      return symbols;
   }
   
  /** Returns <i>n</i> as a ternary string of the specified minimum length.
   *  @param n the decimal value to be represented in ternary form
   *  @param minimumLength the minimum length of the returned ternary string
   *  @return string representing the ternary representation of n
   */
   public static String decToTern(BigInteger n, int minimumLength) {
      boolean negative = false;
      if (n.signum() < 0) {
         n = n.negate();
         negative = true;
      }
      String out = decToTern(n);
      // pad with zeros:
      for (int i = out.length(); i < minimumLength; i++) {
         out = "0" + out;
      }
      if (negative) out = "-" + out;
      return out;
   }

   /** Returns <i>z</i> as a ternary string with at most 
    *  {@value #PRECISION} digits right of
    *  the ternary point.
    *  @param z the decimal value to be represented in ternary form.
    *  @return the ternary representation of <i>z</i>
    */
   public static String decToTern(BigDecimal z) {
      return decToTern(z, PRECISION);
   }

   /** Returns <i>z</i> as a ternary string with at most <code>limit</code>
    *  positions right of the ternary point.
    *  @param z the decimal value to be represented in ternary form.
    *  @param limit the maximum position after the ternary point.
    *  @return the ternary representation of <i>z</i>
    */
   public static String decToTern(BigDecimal z, int limit) {
      BigDecimal base = BigDecimal.valueOf(3);
      boolean negative = false;
      String symbols;
      
      if (z.signum() == 0) {
         symbols = "0";
      } else {
         if (z.signum() < 0) {
            z = z.negate();
            negative = true;
         }
         
         symbols = decToTern(z.toBigInteger()) + ".";
         
         z = z.subtract(new BigDecimal(z.toBigInteger())); // z -= (long) z;
         z = z.multiply(base); // z *= base;
         int r;
         int counter = 0;
         while (z.signum() > 0 && counter <= limit) {
            r = z.intValue();
            symbols += r;
            z = z.subtract(BigDecimal.valueOf(r)); // z -= r;
            z = z.multiply(base); // z *= base;
            counter++;
         }
      }
      return negative? "-" + symbols : symbols;
   }
   
   /** Returns a ternary string as an integer.
    *  @param tern the ternary string to be represented in decimal form
    *  @return the long integer representing the ternary string
    *  @throws NumberFormatException if the string is not ternary
    */
   public static BigInteger ternToDec(String tern) {
      BigInteger base = THREE;
      boolean negative = false;
      String symbol;
      int a_i;
      BigInteger n = ZERO;

      if (tern.substring(0,1).equals("-")) {
         negative = true;
         tern = tern.substring(1);
      }
      if (tern.substring(tern.length()-1, tern.length()).equals("-")) {
         negative = true;
         tern = tern.substring(0, tern.length() - 1);
      }

      for (int i = 0; i < tern.length(); i++) {
         symbol = tern.substring(i, i+1);
         a_i = Integer.parseInt(symbol);
         if (a_i > 2) {
            throw new NumberFormatException ("No ternary number \""+tern+"\"");
         }
         n = n.add(BigInteger.valueOf(a_i).multiply(base.pow(tern.length() - i - 1)));
      }

      if (negative)  n = n.negate();
      return n;
   }

   /** Returns a ternary string as a decimal floating-point number.
    *  @param tern the ternary string to be represented in decimal form.
    *  @return the BigDecimal number representing the ternary string
    *  @throws NumberFormatException if the string is not ternary
    */
   public static BigDecimal ternToBigDecimal(String tern) {
      return ternToBigDecimal(tern, MATH_CONTEXT);
   }
   
   /** Returns a ternary string as a decimal floating-point number.
    *  @param tern the ternary string to be represented in decimal form.
    *  @param length the precision (number of digits)
    *  @return the BigDecimal number representing the ternary string
    *  @throws NumberFormatException if the string is not ternary
    */
   public static BigDecimal ternToBigDecimal(String tern, int length) {
      return ternToBigDecimal(tern, new MathContext(length,RoundingMode.HALF_EVEN));
   }

   /** Returns a ternary string as a decimal floating-point number.
    *  @param tern the ternary string to be represented in decimal form.
    *  @param mc the MATH_CONTEXT (precision and rounding mode)
    *  @return the BigDecimal number representing the ternary string
    *  @throws NumberFormatException if the string is not ternary
    */
   public static BigDecimal ternToBigDecimal(String tern, MathContext mc) {
      BigDecimal base = BigDecimal.valueOf(3);
      boolean negative = false;
      String symbol;
      int a_i;
      BigDecimal x;
      int point; // position of hexadecimal point

      if (tern.substring(0,1).equals("-")) {
        negative = true;
        tern = tern.substring(1);
      }
      if (tern.substring(tern.length()-1, tern.length()).equals("-")) {
        negative = true;
        tern = tern.substring(0,tern.length() - 1);
      }

      point = tern.indexOf('.');

      if (point == -1) {  // the string represents an integer!
         return negative?
            new BigDecimal(ternToDec(tern).negate()) :
            new BigDecimal(ternToDec(tern));
      }

      if (point == 0) {
         tern  = "0" + tern;
         point = 1;
      }

      x = new BigDecimal(ternToDec(tern.substring(0,point)));

      //System.out.println("x="+x);

      tern = tern.substring(point+1, tern.length());

      for (int i = 0; i < tern.length(); i++) {
        symbol = tern.substring(i, i+1);
        a_i = Integer.parseInt(symbol);
        if (a_i > 2) {
           throw new NumberFormatException ("No ternary number \""+tern+"\"");
        }
        x = x.add(BigDecimal.valueOf(a_i).divide(base.pow(i+1, mc), mc));
      }
      return negative? x.negate() : x;
   }

   // --- balanced ternary system: ---v-----------------------------------------
   /** Returns the integer division <i>n</i>/3 in the balanced ternary system.
    *  It is defined by [<i>n</i>/3] except for the case <i>n</i> % 2 where
    *  it gives [<i>n</i>/3] + 1.
    *  @param n the decimal value to be divised
    *  @return the integer division <i>n</i>/3 in the balanced ternary system
    */
   public static BigInteger div3b(BigInteger n) {
      boolean negative = false;
      BigInteger q;
      
      if (n.signum() < 0) {
         negative = true;
         n = n.negate();
      }
      q = (n.mod(THREE).equals(TWO)) ? n.divide(THREE).add(ONE) : n.divide(THREE);
      return negative? q.negate() : q;
   }
   
  /** Returns the symbol in the balanced ternary system representing the value 
   *  <i>n</i> mod 3.
   *  @param n the decimal value to be divised
   *  @return the symbol in the balanced ternary system representing the value 
   *  <i>n</i> mod 3. 
   */
   public static char mod3b(BigInteger n) {
      if (n.mod(THREE).equals(ONE)) {
         return Numbers.TBP;
      } else if (n.mod(THREE).equals(ZERO)) {
         return Numbers.TB0;
      } else {
         return Numbers.TBM;
      }
   }
   
  /** Returns <i>n</i> as a balanced ternary string.
   * @param n the decimal value to be represented in ternary form
   * @return string representing the balanced ternary representation of n
   */
   public static String decToTernB(BigInteger n) {
      String symbols = "";
      BigInteger q = n;
      char r;
      
      if (n.signum() == 0) {
         symbols = "" + Numbers.TB0;
      } else {
         while (q.signum() != 0) {
            r = mod3b(q);
            symbols = r + symbols;
            q = div3b(q);
         }
      }
      return symbols;
   }
   
  /** Returns <i>n</i> as a balanced ternary string of the specified minimum length.
   *  @param n the decimal value to be represented in balanced ternary form
   *  @param minimumLength the minimum length of the returned ternary string
   *  @return string representing the balanced ternary representation of n
   */
   public static String decToTernB(BigInteger n, int minimumLength) {
      String out = decToTernB(n);
      // pad with zeros:
      for (int i = out.length(); i < minimumLength; i++) {
         out = "0" + out;
      }
      return out;
   }

   /** Returns a balanced ternary string as an integer.
    *  @param tern the ternary string to be represented in decimal form
    *  @return the long integer representing the ternary string
    *  @throws NumberFormatException if the string is not ternary
    */
   public static BigInteger ternBToDec(String tern) {
      char symbol;
      int a_i = 0;
      BigInteger n = ZERO;
      
      for (int i = 0; i < tern.length(); i++) {
         symbol = tern.charAt(i);
         if (symbol == Numbers.TBM) {
            a_i = -1;
         } else if (symbol == Numbers.TB0) {
            a_i = 0;
         } else if (symbol == Numbers.TBP) {
            a_i = 1;
         } else {
            throw new NumberFormatException ("No ternary number \""+tern+"\"");
         }
         n = n.add(BigInteger.valueOf(a_i).multiply(THREE.pow(tern.length() - i - 1)));
     }
     return n;
   }
   
   /** Returns <i>z</i> as a balanced ternary string with at most 
    *  {@value #PRECISION} digits right of
    *  the ternary point.
    *  @param z the decimal value to be represented in balanced ternary form.
    *  @return the balanced ternary representation of <i>z</i>
    */
   public static String decToTernB(BigDecimal z) {
      return decToTernB(z, PRECISION);
   }

   /** Returns <i>z</i> as a balanced ternary string with at most <code>limit</code>
    *  positions right of the ternary point.
    *  @param z the decimal value to be represented in balanced ternary form.
    *  @param limit the maximum position after the ternary point.
    *  @return the balanced ternary representation of <i>z</i>
    */
   public static String decToTernB(BigDecimal z, int limit) {
      return Numbers.ternToTernB(decToTern(z,limit));
   }
   
   /** Returns a balanced ternary string as a decimal floating-point number.
    *  @param tern the ternary string to be represented in decimal form.
    *  @return the BigDecimal number representing the balanced ternary string
    *  @throws NumberFormatException if the string is not balanced ternary
    */
   public static BigDecimal ternBToBigDecimal(String tern) {
      return ternBToBigDecimal(tern,MATH_CONTEXT);
   }

   /** Returns a balanced ternary string as a decimal floating-point number.
    *  @param tern the balanced ternary string to be represented in decimal form.
    *  @param length the precision of the decimal number
    *  @return the BigDecimal number representing the balanced ternary string
    *  @throws NumberFormatException if the string is not balanced ternary
    */
   public static BigDecimal ternBToBigDecimal(String tern, int length) {
      return ternBToBigDecimal(tern,new MathContext(length,RoundingMode.HALF_EVEN));
   }

   /** Returns a balanced ternary string as a decimal floating-point number.
    *  @param tern the balanced ternary string to be represented in decimal form.
    *  @param mc the MATH_CONTEXT (digit number and rounding mode)
    *  @return the BigDecimal number representing the balanced ternary string
    *  @throws NumberFormatException if the string is not balanced ternary
    */
   public static BigDecimal ternBToBigDecimal(String tern, MathContext mc) {
      BigDecimal base = BigDecimal.valueOf(3);
      boolean negative = false;
      char symbol;
      int a_i = 0;
      BigDecimal x;
      int point; // position of hexadecimal point

      if (tern.substring(0,1).equals("-")) {
        negative = true;
        tern = tern.substring(1);
      }
      if (tern.substring(tern.length()-1, tern.length()).equals("-")) {
        negative = true;
        tern = tern.substring(0,tern.length() - 1);
      }

      point = tern.indexOf('.');

      if (point == -1) {  // the string represents an integer!
         return new BigDecimal(negative? ternBToDec(tern).negate() : ternBToDec(tern));
      }

      if (point == 0) {
         tern  = "0" + tern;
         point = 1;
      }

      x = new BigDecimal(ternBToDec(tern.substring(0,point)));

      //System.out.println("x="+x);

      tern = tern.substring(point+1, tern.length());

      for (int i = 0; i < tern.length(); i++) {
         symbol = tern.charAt(i);
         if (symbol == Numbers.TBM) {
            a_i = -1;
         } else if (symbol == Numbers.TB0) {
            a_i = 0;
         } else if (symbol == Numbers.TBP) {
            a_i = 1;
         } else {
            throw new NumberFormatException ("No ternary number \""+tern+"\"");
         }
         x = x.add(
            BigDecimal.valueOf(a_i).
            divide(base.pow(tern.length() - i - 1, MATH_CONTEXT), MATH_CONTEXT)
         );
     }

     return negative? x.negate() : x;
   }
   // --- balanced ternary system  ---^-----------------------------------------
   
   /** Returns <i>n</i> as a hexadecimal string.
    * @param n the decimal value to be represented in hexadecimal form.
    * @return the hexadecimal representation of <i>n</i>
    */
   public static String decToHex(BigInteger n) {
     final BigInteger base = new BigInteger("16");
     boolean negative = false;
     String symbols = "";
     BigInteger q = n;
     int r;

     if (n.compareTo(ZERO) == 0) {
       symbols = "0";
     } else {
        if (n.compareTo(ZERO) < 0) {
           q = q.negate();
           negative = true;
        }

        while (q.compareTo(ZERO) > 0) {
           r = q.mod(base).intValue();
           if (r <= 9) {
              symbols = r + symbols;
           } else if (r == 10) {
              symbols = "A" + symbols;
           } else if (r == 11) {
              symbols = "B" + symbols;
           } else if (r == 12) {
              symbols = "C" + symbols;
           } else if (r == 13) {
              symbols = "D" + symbols;
           } else if (r == 14) {
              symbols = "E" + symbols;
           } else if (r == 15) {
              symbols = "F" + symbols;
           }
           q = q.divide(base); //q /= base;
        }
     }
     if (negative)  symbols = "-" + symbols;
     return symbols;
   }

  /** Returns <i>z</i> as a hexadecimal string with at most 100 positions right of
   *  the hexadecimal point.
   * @param z the decimal value to be represented in hexadecimal form.
   * @return the hexadecimal representation of <i>z</i>
   */
   public static String decToHex(BigDecimal z) {
      return decToHex(z, 100);
   }

  /** Returns <i>z</i> as a hexadecimal string with at most <code>limit</code>
   *  positions right of the hexadecimal point.
   * @param z the decimal value to be represented in hexadecimal form.
   * @param limit the maximum position after the hexadecimal point.
   * @return the hexadecimal representation of <i>z</i>
   */
   public static String decToHex(BigDecimal z, int limit) {
      final BigDecimal base = new BigDecimal(16.);
      boolean negative = false;
      String symbols = "";

      if (z.compareTo(ZERO_DOT) == 0) {
         symbols = "0";
      } else {
         if (z.compareTo(ZERO_DOT) < 0) {
            z = z.negate();
            negative = true;
         }
         if (z.compareTo(ONE_DOT) >= 0) {
            symbols = decToHex(z.toBigInteger()) + ".";
         }

         z = z.subtract(new BigDecimal(z.toBigInteger())); //z -= (int) z;
         z = z.multiply(base); //z *= base;
         int r;
         int counter = 0;
         while (z.compareTo(ZERO_DOT) > 0 && counter <= limit) {
            r = z.intValue(); //(int) z;
            if (r <= 9) {
               symbols += r;
            } else if (r == 10) {
               symbols += "A";
            } else if (r == 11) {
               symbols += "B";
            } else if (r == 12) {
               symbols += "C";
            } else if (r == 13) {
               symbols += "D";
            } else if (r == 14) {
               symbols += "E";
            } else if (r == 15) {
               symbols += "F";
            }
            z = z.subtract(new BigDecimal(z.toBigInteger())); //z -= (int) z;
            z = z.multiply(base); //z *= base;
            counter++;
         }
     }

     if (negative)  symbols = "-" + symbols;
     return symbols;
   }

   /** Returns a hexadecimal string as an integer.
    *  @param hex the hexadecimal string to be represented in decimal form
    *  @return the integer representing the hexadecimal string
    *  @throws NumberFormatException if the string is not hexadecimal
    */
   public static BigInteger hexToDec(String hex) {
      BigInteger base = BigInteger.valueOf(16);
      boolean negative = false;
      String symbol;
      int a_i;
      BigInteger n = ZERO;

      if (hex.substring(0,1).equals("-")) {
         negative = true;
         hex = hex.substring(1);
      }
      if (hex.substring(hex.length()-1, hex.length()).equals("-")) {
         negative = true;
         hex = hex.substring(0, hex.length() - 1);
      }

      for (int i = 0; i < hex.length(); i++) {
         symbol = hex.substring(i, i+1);
         if (symbol.equalsIgnoreCase("A")) {
            a_i = 10;
         } else if (symbol.equalsIgnoreCase("B")) {
            a_i = 11;
         } else if (symbol.equalsIgnoreCase("C")) {
            a_i = 12;
         } else if (symbol.equalsIgnoreCase("D")) {
            a_i = 13;
         } else if (symbol.equalsIgnoreCase("E")) {
            a_i = 14;
         } else if (symbol.equalsIgnoreCase("F")) {
            a_i = 15;
         } else {
            a_i = Integer.parseInt(symbol);
         }
//         if (a_i > 2) {
//            throw new NumberFormatException ("No hexadecimal number \""+hex+"\"");
//         }
         n = n.add(BigInteger.valueOf(a_i).multiply(base.pow(hex.length() - i - 1)));
      }

      if (negative)  n = n.negate();
      return n;
   }

   /** Returns a hexadecimal string as a decimal fraction number.
    *  @param hex the hexadecimal string to be represented in decimal form.
    *  @return the BigDecimal number representing the hexadecimal string
    *  @throws NumberFormatException if the string is not hexadecimal
    */
   public static BigDecimal hexToBigDecimal(String hex) {
      return ternToBigDecimal(hex, MATH_CONTEXT);
   }

   /** Returns a hexadecimal string as a decimal fraction number.
    *  @param hex the hexadecimal string to be represented in decimal form.
    *  @param length the precision (number of digits)
    *  @return the BigDecimal number representing the hexadecimal string
    *  @throws NumberFormatException if the string is not hexadecimal
    */
   public static BigDecimal hexToBigDecimal(String hex, int length) {
      return hexToBigDecimal(hex, new MathContext(length,RoundingMode.HALF_EVEN));
   }

   /** Returns a hexadecimal string as a decimal fraction number.
    *  @param hex the hexadecimal string to be represented in decimal form.
    *  @param mc the MATH_CONTEXT (precision and rounding mode)
    *  @return the BigDecimal number representing the hexadecimal string
    *  @throws NumberFormatException if the string is not hexadecimal
    */
   public static BigDecimal hexToBigDecimal(String hex, MathContext mc) {
      BigDecimal base = BigDecimal.valueOf(16);
      boolean negative = false;
      String symbol;
      int a_i;
      BigDecimal x;
      int point; // position of hexadecimal point

      if (hex.substring(0,1).equals("-")) {
        negative = true;
        hex = hex.substring(1);
      }
      if (hex.substring(hex.length()-1, hex.length()).equals("-")) {
        negative = true;
        hex = hex.substring(0,hex.length() - 1);
      }

      point = hex.indexOf('.');

      if (point == -1) {  // the string represents an integer!
         return negative?
            new BigDecimal(hexToDec(hex).negate()) :
            new BigDecimal(hexToDec(hex));
      }

      if (point == 0) {
         hex   = "0" + hex;
         point = 1;
      }

      x = new BigDecimal(hexToDec(hex.substring(0,point)));

      //System.out.println("x="+x);

      hex = hex.substring(point+1, hex.length());

      for (int i = 0; i < hex.length(); i++) {
         symbol = hex.substring(i, i+1);
         if (symbol.equalsIgnoreCase("A")) {
            a_i = 10;
         } else if (symbol.equalsIgnoreCase("B")) {
            a_i = 11;
         } else if (symbol.equalsIgnoreCase("C")) {
            a_i = 12;
         } else if (symbol.equalsIgnoreCase("D")) {
            a_i = 13;
         } else if (symbol.equalsIgnoreCase("E")) {
            a_i = 14;
         } else if (symbol.equalsIgnoreCase("F")) {
            a_i = 15;
         } else {
            a_i = Integer.parseInt(symbol);
         }
         x = x.add(BigDecimal.valueOf(a_i).divide(base.pow(i+1, mc), mc));
      }
      return negative? x.negate() : x;
   }

   /** Returns the Gray code of an integer.
    *  @param x an integer
    *  @return the Gray code of <i>x</i> as a string
    *  @see #grayCode(BigInteger, int)
    */
   public static String grayCode(BigInteger x) {
      x = x.xor(x.shiftRight(1));
      String code = "";
      for (int i = x.bitLength(); i >= 0; i--) {
         code += x.testBit(i) ? "1" : "0";
      }
      return code;
   }

   /** Returns the Gray code of an integer <i>x</i>, with a given minimum length.
    *  If the minimum length is greater than the bit length of the integer <i>x</i>,
    *  the Gray code string is padded with leading zeros.
    *  @param x an integer
    *  @param minimumLength the minimum length of the returned Gray code string
    *  @return the Gray code of <i>x</i> as a string
    *  @see #grayCode(BigInteger)
    */
   public static String grayCode(BigInteger x, int minimumLength) {
      x = x.xor(x.shiftRight(1));
      String out = "";
      int length = (x.bitLength() > minimumLength) ? x.bitLength() : minimumLength;
      for (int i = length - 1; i >= 0; i--) {
         out += x.testBit(i) ? "1" : "0";
      }
      return out;
   }

   /** Returns binary representation of the integer represented by a
    *  Gray code string; the string is padded with zeros if it is shorter than
    *  the specified minimum length.
    *  @param grayCode a Gray code string
    *  @param minimumLength the minimum length of the binary string
    *  @return the binary representation of the integer represented by the Gray code string
    *  @throws NumberFormatException if the string does not represent a Gray code
    *  @see #grayCodeToBinary(String)
    */
   public static String grayCodeToBinary(String grayCode, int minimumLength) {
      String out = decToBin(grayCodeToDecimal(grayCode));
      // pad with zeros:
      for (int i = out.length(); i < minimumLength; i++) {
         out = "0" + out;
      }
      return out;
   }

   /** Returns binary representation of the integer in which is represented by a Gray code string.
    *  @param grayCode a Gray code string
    *  @return the binary representation of the integer represented by the Gray code string
    *  @throws NumberFormatException if the string does not represent a Gray code
    *  @see #grayCodeToBinary(String, int)
    */
   public static String grayCodeToBinary(String grayCode) {
      return decToBin(grayCodeToDecimal(grayCode));
   }

   /** Returns the integer represented by a Gray code string.
    *  @param grayCode a Gray code string
    *  @return the integer represented by the Gray code string
    *  @throws NumberFormatException if the string does not represent a Gray code
    */
   public static BigInteger grayCodeToDecimal(String grayCode) {
      BigInteger x = binToDec(grayCode), y = ZERO;
      for(int i = grayCode.length() - 1; i >= 0; i--) {
         // y = ((y & (1 << i+1)) >> 1) ^ (x & (1 << i)) + y:
         y = y.and(ONE.shiftLeft(i+1)).shiftRight(1).xor(x.and(ONE.shiftLeft(i))).add(y);
      }
      return y;
   }

   /** For test purposes...*/
   /*
   public static void main (String[] args) {
      //BigDecimal l2  = BigDecimal.valueOf(0.69314718055994530941723212145818);
      //BigDecimal l10 = BigDecimal.valueOf(2.3025850929940456840179914546844);
      //BigDecimal b = l10.divide(l2, MATH_CONTEXT);
      //System.out.println("log10/log2 = " + b);
      //System.exit(0);
      //System.out.println(ONE_DOT.divide(SQRT_TWO, 100, BigDecimal.ROUND_HALF_EVEN));

      //System.out.println(RADIANS.doubleValue());

      //--- root: -----------
//      BigDecimal x, y;
//      x = new BigDecimal("199774421497046087277749");
//      int n = 2;
//      for (int i=0; i <= 100; i += 10) {
//         y = x.multiply(TEN_DOT.pow(i),MATH_CONTEXT);
//         System.out.println(y + " =\n" + root(n,y,MATH_CONTEXT.getPrecision()).pow(n,MATH_CONTEXT));
//      }
//      System.exit(0);
      //for (int i=0; i < 50; i += 10) {
      //   y = x.divide(TEN_DOT.pow(i), MATH_CONTEXT);
      //   System.out.println(y.setScale(PRECISION,BigDecimal.ROUND_HALF_EVEN) + " =\n" + root(n,y).pow(n).setScale(PRECISION,BigDecimal.ROUND_HALF_EVEN));
      //}
      //System.out.println(x.setScale(PRECISION,BigDecimal.ROUND_HALF_EVEN) + " =\n" + root(n,x).pow(n).setScale(PRECISION,BigDecimal.ROUND_HALF_EVEN));
      //System.out.println("sqrt("+x.setScale(PRECISION,BigDecimal.ROUND_HALF_EVEN) + ") =\n" + root(n,x).setScale(PRECISION,BigDecimal.ROUND_HALF_EVEN));
      //--- continued fraction: -----------
      //BigDecimal x = SQRT_TWO.subtract(ONE_DOT);
      ////BigDecimal x = ZETA_3.multiply(BigDecimal.valueOf(26)).divide(PI.pow(3), 100, BigDecimal.ROUND_HALF_EVEN);
      ////BigDecimal x = ZETA_3.divide(PI.pow(3), 100, BigDecimal.ROUND_HALF_EVEN);
      //System.out.println("scale=" + x.scale());
      //int limit = 100;
      //System.out.print("x=[");
      //BigInteger[] cf = continuedFraction(x,limit);
      //for (int i=0; i < cf.length - 1; i++) {
      //   System.out.print(cf[i] + ",");
      //}
      //System.out.println(cf[cf.length-1] + "]");
      //for (int i=1; i < 20; i++) {
      //   BigInteger[] bra = bestRationalApproximation(x,i);
      //   System.out.println("x = " + bra[0] +"/"+bra[1]);
      //}

      //--- AKS: -----------
      // boolean prime, primeAKS;
      // BigInteger m;
      // BigInteger TWO = BigInteger.valueOf(2);
// 
      // int r = 2;
      // //BigInteger n = new BigInteger("90698401"); // AKS korrekt für r=5, 7, 13, 19, nicht für r= 2, 3, 4, 6, 8, 17
      // //BigInteger n = new BigInteger("214852609"); // AKS korrekt für r=5, 7, 11, 13, 17, nicht für r= 2, 3, 4, 6, 12, 19
      // BigInteger n = new BigInteger("79624621"); // AKS korrekt für r=7, 11, 13, 17, 19, nicht für r= 2, 3, 4, 6, 12, 23
      // //BigInteger n = new BigInteger("341550071728321"); // AKS korrekt für r=11, 19, nicht für r= 2, 3, 4, 13, 17
      // //BigInteger n = new BigInteger("4498414682539051"); // AKS korrekt für r=11, 19, nicht für r= 2, 3, 4, 13, 17
      // for (r = 2; r <= 31; r++) {
         // prime = isPrime(n);
         // //primeAKS = isStrongProbablePrime(n,BigInteger.valueOf(r));
         // primeAKS = nakedAKS(n,r);
         // if (prime != primeAKS) {
            // System.out.println("r="+r+", " + n + " is prime? " + prime + ", AKS: " + primeAKS);
         // }
      // }
      // //System.exit(0);

      //m = new BigInteger("561"); // = 3 x 11 x 17, Carmichael number
      //m = new BigInteger("2508013"); // = 53 * 79 * 599
      //m = new BigInteger("90698401"); // = 103 * 647 * 1361
      //m = new BigInteger("214852609"); // = 229 * 457 * 2053
      //m = new BigInteger("79624621"); // = 139 * 691 * 829
      //m = new BigInteger("168003672409"); // = 3037 x 6073 x 9109, Carmichael number, 38.5 sec
      //m = new BigInteger("4507445537641"); // = 9091 * 18181 * 272721, Carmichael number, 1 min
      //m = new BigInteger("2152302898747"); // = 6763 * 10627 * 29947, strong pseudoprime, 1 min
      //m = new BigInteger("341550071728321"); // = 10670053 * 32010157, strong pseudoprime, 97 sec
      //m = new BigInteger("199774421497046087277749"); // prime! etwa 2 Tage ...
      //m = new BigInteger("199774421497046087279453"); // prime! etwa 2 Tage ...
      //m = new BigInteger("1524157877488187891"); // = 9091 * 167655689966801, about 347 sec = 5:47 min
      //m = new BigInteger("101"); // prime! needs about 0.06 sec
      //m = new BigInteger("1234567891"); // prime! needs about 18 sec
      //m = new BigInteger("2").pow(31).subtract(ONE); // needs about 27 sec sec
      //m = new BigInteger("199774421497046087277751"); // = 11 x 43 x 67 x 4283 x 2014147 x 730743061
      //m = new BigInteger("2310868403"); // = 47287 * 48896, 21 sec
      //m = new BigInteger("219781"); // = 271 * 811

      // Factors factors = new Factors(m);
      // System.out.println(m + " = " + factors);
      // //javax.swing.JOptionPane.showMessageDialog(null, "<html>" + m + " = " + factors.toHTMLString());
// 
      // System.exit(0);
      // long time;
      // time = System.currentTimeMillis();
      // System.out.print(m + " prime? " + isPrime(m));
      // time = System.currentTimeMillis() - time;
      // System.out.print(" (" + time + " ms)");
      // time = System.currentTimeMillis();
      // primeAKS = nakedAKS(m,r);
      // //primeAKS = primalityTestAKS(m);
      // time = System.currentTimeMillis() - time;
      // System.out.println(", AKS: " + primeAKS + " (AKS: " + time + " ms)");
      // //System.exit(0);

      // long count = 0;
// 
      // BigInteger start = new BigInteger("50000001");
      // r = 17;
      // while (r < 1000) {
         // if (Numbers.isPrime(r)) {
            // for (m = start; true; m = m.add(TWO)) {
            // //for (m = BigInteger.valueOf(r%2==0 ? r+1 : r+2); true; m = m.add(TWO)) {
               // time = System.currentTimeMillis();
               // prime = isPrime(m);
               // time = System.currentTimeMillis() - time;
               // if (prime) {
                  // count++;
               // }
               // if (prime && count % 10000 == 0) {
                  // System.out.print("r="+r+", " + m + " prime? " + prime + " (" + time + " ms)");
                  // time = System.currentTimeMillis();
                  // primeAKS = nakedAKS(m,r);
                  // //primeAKS = primalityTestAKS(m);
                  // time = System.currentTimeMillis() - time;
                  // System.out.println(", AKS: " + primeAKS + " (AKS: " + time + " ms)");
               // }
               // if (prime) {
                  // continue; // if m is prime, nakedAKS gives the correct result!
               // }
               // time = System.currentTimeMillis();
               // //primeAKS = isStrongProbablePrime(m,BigInteger.valueOf(r));
               // primeAKS = nakedAKS(m,r);
               // //primeAKS = primalityTestAKS(m);
               // time = System.currentTimeMillis() - time;
               // //if (prime && count % 10000 == 0) System.out.println(", AKS: " + primeAKS + " (AKS: " + time + " ms)");
               // if (prime != primeAKS) {
                  // System.out.println("### Error!!!! r="+r+", m="+m+", AKS="+primeAKS);
                  // break;  // inner for-loop
               // }
               // //m = m.multiply(TWO).add(BigInteger.valueOf(3 + 2 * (int) (Math.random()*Integer.MAX_VALUE)));
               // //m = m.add(TWO);
            // }
         // }
         // r += 1;
      // }
      // System.exit(0);

      // for (int i = 1; i <= 10; i++) {
         // time = System.currentTimeMillis();
         // prime = isPrime(m);
         // time = System.currentTimeMillis() - time;
         // if (prime) System.out.print(i + ") " + m + " prime? " + prime + " (" + time + " ms)");
         // time = System.currentTimeMillis();
         // primeAKS = primalityTestAKS(m);
         // time = System.currentTimeMillis() - time;
         // if (prime) System.out.println(", AKS: " + primeAKS + " (AKS: " + time + " ms)");
         // if (prime != primeAKS) {
            // System.out.println("### Error!!!! r="+r+", m="+m+", AKS="+primeAKS);
            // //System.exit(0);
         // }
         // //m = m.multiply(TWO).add(BigInteger.valueOf(3 + 2 * (int) (Math.random()*Integer.MAX_VALUE)));
         // m = m.add(TWO);
      // }
      // System.exit(0);
      
      //
      // modulo:------------
      //BigDecimal n = BigDecimal.valueOf(5.);
      //BigDecimal m = BigDecimal.valueOf(3.);;
      //System.out.println("5 mod 3="+mod(n,m)+", -5 mod 3="+mod(n.negate(),m)+
      //   ", 5 mod -3="+mod(n,m.negate())+", -5 mod -3 ="+mod(n.negate(),m.negate()));
      //
      // Gray Code: -------------------------
//      for (BigInteger i = BigInteger.valueOf(1024); i.compareTo(BigInteger.valueOf(1100)) < 0; i = i.add(ONE)) {
//         System.out.println(grayCode(i,5) + " = " + Numbers.grayCode(i.longValue(),5));
//      }
//      for (BigInteger i = new BigInteger("0"); i.compareTo(new BigInteger("129")) < 0; i = i.add(ONE)) {
//         System.out.println(i + " -> " + grayCode(i,5) + " -> " + grayCodeToBinary(grayCode(i,5),5));
//      }
      // Binary: -------------------------
      String 
      s = "12021";
      s = "p0n0p0np0n";
      System.out.println(ternBToBigDecimal(s+".np0pp0nn") + " = " + Numbers.ternBToDec(s));
      //System.out.println(ternBToBigDecimal(s) + " = " + Numbers.ternBToDouble(s));
      //BigInteger n = new BigInteger("-23"); long nL = -23L;
      BigDecimal nn = new BigDecimal("-1.234"); double nL = -1.234;
      System.out.println(decToTernB(nn) + " = " + Numbers.decToTernB(nL));
      for (BigDecimal i = BigDecimal.valueOf(1.); i.compareTo(BigDecimal.valueOf(2.1)) < 0; i = i.add(BigDecimal.valueOf(.1))) {
      //   System.out.println(decToBin(i,10) + " = " + Numbers.decToBin(i.doubleValue(),10));
      }
      //System.out.println(PI);
      //System.out.println(decToBin(PI,400));
      //System.out.println(decToTern(PI,400));
      //System.out.println(Numbers.ternToTernB(decToTern(PI,400)));
      // ------------------------------------
//      BigInteger x = BigInteger.valueOf(5);
//      BigInteger e = BigInteger.valueOf(3);
//      BigInteger n = BigInteger.valueOf(7);
//      System.out.println(x + "^" + e + " mod " + n + " = "+modPow(x,e,n));
//      x = BigInteger.valueOf(-5);
//      e = BigInteger.valueOf(3);
//      n = BigInteger.valueOf(7);
//      System.out.println(x + "^" + e + " mod " + n + " = "+modPow(x,e,n));
//      x = BigInteger.valueOf(5);
//      e = BigInteger.valueOf(-3);
//      n = BigInteger.valueOf(7);
//      System.out.println(x + "^" + e + " mod " + n + " = "+modPow(x,e,n));
//      x = BigInteger.valueOf(5);
//      e = BigInteger.valueOf(3);
//      n = BigInteger.valueOf(-7);
//      System.out.println(x + "^" + e + " mod " + n + " = "+modPow(x,e,n));
//       BigDecimal x = PI; //BigDecimal.valueOf(5);
//       int e = 3;
//       BigDecimal n = BigDecimal.valueOf(7);
//       System.out.println(x + "^" + e + " mod " + n + " = "+modPow(x,e,n));
//       x = BigDecimal.valueOf(-15);
//       e = 3;
//       n = BigDecimal.valueOf(7);
//       System.out.println(x + "^" + e + " mod " + n + " = "+modPow(x,e,n));
//       x = BigDecimal.valueOf(5);
//       e = -3;
//       n = BigDecimal.valueOf(7);
//       System.out.println(x + "^" + e + " mod " + n + " = "+modPow(x,e,n));
//       x = BigDecimal.valueOf(5);
//       e = 3;
//       n = BigDecimal.valueOf(-7);
//       System.out.println(x + "^" + e + " mod " + n + " = "+modPow(x,e,n));
      //--- sin, cos, ln, exp: ---
      //BigDecimal x = PI_4;
      //System.out.println("ln 2 = " + ln(TWO_DOT,6).setScale(25,6));
      // x = ONE_DOT;
      // System.out.println("Abweichung: " + arctan(x,100).subtract(PI_4).setScale(105,6));
      // for(int order = 124; order <= 130; order += 2) {
         // x = SQRT_TWO.subtract(ONE_DOT);
         // System.out.println("x="+x.setScale(3,6) + ": " +
            // arctan(x,150).subtract(arctan(x,order)).setScale(105,6) + ", n="+order*.8);
         // //x = SQRT_TWO.add(ONE_DOT);
         // //System.out.println("x="+x.setScale(3,6) + ": " +
         // //   arctan(x,100).subtract(arctan(x,order)).setScale(25,6) + ", n="+order);
      // }
      //
      // long zeit;
      // zeit = System.nanoTime();
      // //System.out.println("x="+x + ": " + cos(x));
      // for (x = new BigDecimal("0.51"); x.compareTo(new BigDecimal("0.43")) <= 0; x = x.add(new BigDecimal(".0025"))) {
         // //System.out.println("x="+x.doubleValue() + ": " + (sin(x).doubleValue() - Math.sin(x.doubleValue())));
         // //System.out.println("x="+x.doubleValue() + ": " + (cos(x).doubleValue() - Math.cos(x.doubleValue())));
         // System.out.println("x="+x + ": " + arctan(x,8).setScale(25,6));
         // System.out.println("x="+x + ": " + Math.atan(x.doubleValue()));
         // //System.out.println("ln "+x + " = " + ln(x,100).setScale(50,6));
         // //System.out.println("ln "+x + " = " + Math.log(x.doubleValue()));
         // //System.out.println("e^"+x + " = " + exp(x).setScale(50,6));
         // //System.out.println("e^"+x + " = " + Math.exp(x.doubleValue()));
      // }
      // zeit = System.nanoTime() - zeit;
      // System.out.println("Laufzeit: " + zeit/1000000 + " ms");
      //zeit = System.nanoTime();
      //for (x = TEN_DOT; x.compareTo(new BigDecimal("11")) <= 0; x = x.add(new BigDecimal("0.1"))) {
      //   System.out.println("ln "+x + " = " + ln2(x,100).setScale(50,6));
      //}
      //zeit = System.nanoTime() - zeit;
      //System.out.println("Laufzeit: " + zeit/1000000 + " ms");

      //long zeit;
      //zeit = System.nanoTime();
      //algorithmics.TP1.BigNumbers.sin(x,100);
      //zeit = System.nanoTime() - zeit;
      //System.out.println("rekursiv: " + zeit + " ns");

      //zeit = System.nanoTime();
      //sin(x,100);
      //zeit = System.nanoTime() - zeit;
      //System.out.println("iterativ: " + zeit + " ns");

      //for(int n=45; n <= 35; n++) {
         //System.out.println("Abweichung(n="+n+"): " + sin(x,n).add(SQRT_ONE_HALF).setScale(105,6));
         //System.out.println("Abweichung(n="+n+"): " + cos(x,n).subtract(SQRT_ONE_HALF).setScale(105,6));
         //System.out.println("Abweichung(n="+n+"): " + arctan(x,n).subtract(PI.divide(BigDecimal.valueOf(6),6)).setScale(105,6));
         //System.out.println("  sin.scale() = " + sin(X,n).scale());
      //}


      //System.out.println(test + " = " + decToHex(test,50) + "_{16}");
      //

      //boolean isPower;
      //BigInteger m = new BigInteger("1524157877488187881"); // needs about 1,4 sec
      //BigInteger m = new BigInteger("1234567891");
      //BigInteger m = new BigInteger("48"); // needs about 130 ms
      //long time = System.currentTimeMillis();
      //isPower = isPower(m);
      //time = System.currentTimeMillis() - time;
      //System.out.println(m + " isPower? " + isPower + " (running time: " + time + "ms)");

      //BigInteger[] x = euclid(new BigInteger("21"), new BigInteger("35"));
      //or(BigInteger n : x) {
      //   System.out.print(n + ", ");
      //}
      //System.out.println();
      //System.out.println("ord(5,7)=" + ord(new BigInteger("5"), new BigInteger("7")));
      //System.out.print("5 mod 3=" + mod(new BigInteger("5"), new BigInteger("3")));
      //System.out.print(", -5 mod 3=" + mod(new BigInteger("-5"), new BigInteger("3")));
      //System.out.print(", 5 mod -3=" + mod(new BigInteger("5"), new BigInteger("-3")));
      //System.out.println(", -5 mod -3=" + mod(new BigInteger("-5"), new BigInteger("-3")));
      //System.out.println("5 mod 0=" + mod(new BigInteger("5"), new BigInteger("0")));
   }
   // */
}