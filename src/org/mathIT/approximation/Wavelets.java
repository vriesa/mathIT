/*
 * Wavelets.java - Class providing constants and methods for wavelets.
 *
 * Copyright (C) 2006-2012 Andreas de Vries
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
package org.mathIT.approximation;
import static org.mathIT.numbers.Numbers.*;
/**
 * This class provides static constants and methods to implement wavelets.
 * @author Andreas de Vries
 * @version 1.1
 */
public class Wavelets {
   // Suppresses default constructor, ensuring non-instantiability.
   private Wavelets() {
   }
   static String outputR = "";
   static long recursionCalls = 0;
   /** Number of the stored highervalued (leftmost) bits of the mantissa of a double value.
     * It is used to compute the wavelet function values of non-dyadic numbers, i.e.,
     * numbers with an infinite binary expansion, such as 1/3, 
     * or irrational numbers such &#x221A;2 or <i>&#x03C0;</i>.
     * The value 10 results in a pretty good approximation, 16 is fine, and 22 is excellent (but needs 
     * some 10 millions recursive calls for 1/3!).
     * For plotting, however, the scaling functions and wavelets D2--D10 are OK with
     * the value 7.
     * @see #accuracyBitMask
     */
   public static final int accuracyBits = 40; // float mantissa has 22 bits
   /* Some experimental data for the calculation of Daubechies scaling function phi_2 (D4):
      Accuracy bits = 10:
      ### 0.3333333333333333 => 0.333251953125
      phi_2(0.3333333333333333) = 0.7045746717190597 (8193 recursive calls)
      ### 1.4142135623730951 => 1.4140625
      phi_2(1.4142135623730951) = 0.14904470533682693 (761 recursive calls)
      
      Accuracy bits = 16:
      ### 0.3333333333333333 => 0.3333320617675781
      phi_2(0.3333333333333333) = 0.7046754366679916 (524289 recursive calls)
      ### 1.4142135623730951 => 1.4141998291015625
      phi_2(1.4142135623730951) = 0.1487104583614672 (393209 recursive calls)

      Accuracy bits = 22:
      ### 0.3333333333333333 => 0.3333333134651184
      phi_2(0.3333333333333333) = 0.7046769916360064 (33554433 recursive calls)
      ### 1.4142135623730951 => 1.4142134189605713
      phi_2(1.4142135623730951) = 0.14868260487041396 (25165817 recursive calls)
     */
   /** The bitmask which corresponds to the accuracy bits.
     * It is computed by <code>0xfffffffffffffL &lt;&lt; (52 - accuracyBits)</code>,
     * since the mantissa of a double value consists of 52 bits.
     * @see #accuracyBits
     */
   public static final long accuracyBitMask = 0xfffffffffffffL << (52 - accuracyBits);
   /** Standard accuracy of computations of wavelet values.*/
   public static final double ACCURACY = 1e-8;
  
   /** Daubechies filter coefficients <i><sub>N</sub>h<sub>k</sub></i> = <i>h</i>[<i>N</i>-1][<i>k</i>] 
    *  for the D-2<i>N</i> wavelet, <i>N</i> = 1, 2, ..., 10, i.e., D2, D4, ..., D20.
    *  We have 0 &le; k &lt; 2N.
    *  In the i-th row (0 &le; i &lt; 10) there are the 2(i+1)=2N coefficients of D2<i>N</i>.
    */
   public static final double[][] h = {
      //normalized Daubechies filter coefficients:
      //N=2 (D2):
      {1/SQRT2,1/SQRT2},
      //N=4 (D4)
      {.4829629131445341, .8365163037378077, .2241438680420134, -.1294095225512603},
      //N=6 (D6)
      {.3326705529500825, .8068915093110924, .4598775021184914, -.1350110200102546, -.0854412738820267,  .0352262918857095},
      //N=8 (D8)
      //{.325803428051297, 1.01094571509182,   .892200138246757,  -.0395750262356414, -.264507167369036,  .0436163004741781, .0465036010709817, -.0149869893303614},
      {.2303778133088964, .7148465705529154, .6308807679298587, -.0279837694168599, -.1870348117190931, .0308413818355607, .0328830116668852, -.0105974017850690},
      // original: {.2303778133088964, .7148465705529154, .6308807679398587, -.0279837694168599, -.1870348117190931, .0308413818355607, .0328830116668852, -.0105974017850690},
      //N=10 (D10)
      {.1601023979741929, .6038292697971895, .7243085284377726,  .1384281459013203, -.2422948870663823, -.0322448695846381,  .0775714938400459, -.0062414902127983, -.0125807519990820,  .0033357252854738},
      //N=12 (D12)
      {.1115407433501095, .4946238903984533, .7511339080210959,  .3152503517091982, -.2262646939654400, -.1297668675672625,  .0975016055873225,  .0275228655303053, -.0315820393174862,  .0005538422011614,  .0047772575109455, -.0010773010853085},
      //N=14 (D14)
      //? Version Resnikoff: {1.10099430745612e-1, 5.60791283625468e-1, 1.03114849163611, 6.64372482211065e-1, -2.03513822462596e-1, -3.16835011280582e-1,  1.00846465009390e-1,  1.14003445159730e-1,  -5.37824525896854e-2,  -2.34399415642046e-2,  1.77497923793590e-2,  6.07514995401994e-4,  -2.54790471818706e-3,  5.00226853122428e-4},
      {.0778520540850037, .3965393194818912, .7291320908461957,  .4697822874051889, -.1439060039285212, -.2240361849938412,  .0713092192668272,  .0806126091510774, -.0380299369350104, -.0165745416306655,  .0125509985560986,  .0004295779729214, -.0018016407040473,  .0003537137999745},
      //N=16 (D16)
      //? Version Resnikoff: {7.69556221082311e-2, 4.42467247152680e-1,  9.55486150428578e-1,  8.27816532422834e-1,  -2.23857353343735e-2,  -4.01658632781869e-1,  6.68194092220773e-4,  1.82076356847413e-1,  -2.45639010457868e-2,  -6.23502066503701e-2,  1.97721592967229e-2,  1.23688448196446e-2,  -6.88771925689277e-3,  -5.54004548959349e-4,  9.55229711300440e-4,  -1.66137261373438e-4},
      {.0544158422431072, .3128715909143166, .6756307362973195,  .5853546836542159, -.0158291052563823, -.2840155429615824,  .0004724845739124,  .1287474266204893, -.0173693010018090, -.0440882539307971,  .0139810279174001,  .0087460940474065, -.0048703529934520, -.0003917403733770,  .0006754494064506, -.0001174767841248},
      //N=18 (D18)
      //? Version Resnikoff:{5.38503495892927e-2, 3.44834303813753e-1, 8.55349064358953e-1, 9.29545714365917e-1,  1.88369549506573e-1,  -4.14751761801313e-1,  -1.36953549024509e-1,  2.10068342278954e-1,  4.34526754612515e-2,  -9.56472641201011e-2,  3.54892813240017e-4,  3.16241658524904e-2,  -6.67962022627260e-3,  -6.05496057508627e-3,  2.61296728049251e-3,  3.25814671351959e-4,  -3.56329759021301e-4,  5.56455140342699e-5},
      {.0380779473638778, .2438346746125858, .6048231236900955,  .6572880780512736,  .1331973858249883, -.2932737832791663, -.0968407832229492,  .1485407493381256,  .0307256814793365, -.0676328290613279,  .0002509471148340,  .0223616621236798, -.0047232047577518, -.0042815036824635,  .0018476468830563,  .0002303857635232, -.0002519631889427,  .0000393473203163},
      //N=20 (D20)
      //? Version Resnikoff:{3.77171575919661e-2,  2.66122182791924e-1,  7.45575071481257e-1,  9.73628110727500e-1,  3.97637741768455e-1,  -3.53336201787914e-1,  -2.77109878715249e-1,  1.80127448534566e-1,  1.31602987101180e-1,  -1.00966571195772e-1,  -4.16592480872940e-2,  4.69698140969989e-2,  5.10043696773336e-3,  -1.51790023357585e-2,  1.97332536494843e-3,  2.81768659017377e-3,  -9.69947839849689e-4,  -1.64709006089525e-4,  1.32354366850167e-4,  -1.87584156273697e-5}
      {.0266700579005473, .1881768000776347, .5272011889315757,  .6884590394534363,  .2811723436605715, -.2498464243271598, -.1959462743772862,  .1273693403357541,  .0930573646035547, -.0713941471663501, -.0294575368218399,  .0332126740593612,  .0036065535669870, -.0107331754833007,  .0013953517470688,  .0019924052951925, -.0006858566949564, -.0001164668551285, .0000935886703202, -.0000132642028945}
   };
      
   /** Initial values phi(j) of the Daubechies scaling functions phi=phi<sub>N</sub> 
    *  for the D2 - D20 wavelets in dimension n=1.
    *  We have 0 &le; j &lt; 2N.
    *  In the i-th row (0 &le; i &lt; 10) there are the 2(i+1) - 1 = 2N - 1 initial values of D2<i>N</i>.
    */
   public static final double[][] initialValues = {
      //D2:
      {1},
      //D4:
      {0, 1.3660254037844386, -0.3660254037844386}, //(1+SQRT3)/2, (1-SQRT3)/2}
      //D6:
      {0, 1.2863350694256968, -0.38583696104587584, 
       0.0952675460037808, 0.004234345616398079},
      //D8:
      {0, 1.0071699777273806, -0.03383695405488832, 
       0.0396104627162777, -0.01176435820581565, 
       -0.001197957596188022, 1.8829413233729477E-5},
      //D10:
      {0, 0.6961360550943198, 0.4490576048094856, 
       -0.18225400531803593, 0.03723182290388987, 
       0.0015175751497141362, -0.001726796243007974, 
       3.75694705183879E-5, 1.7413311609737836E-7},
      //D12:
      {0, 0.0035996161087599064, 0.05256026491696806, 
       0.10288796212599259, 0.18016575240795044, 
       0.0040308305986092455, 0.5474582545897935, 
       0.017639398474356235, 0.09170481903120684, 
       -4.692211423966246E-5, 2.3860602770260988E-8},
      //D14:
      { 0, 0.31041831211800375, 0.4630087339757032, 
        0.0841996644020489, 0.056733408492558195, 
       -0.011438134766878338, 0.05126955378811376, 
        0.03446606265598383, 0.004688705335206683, 
        0.005464238400621743, 0.0010767900019440144, 
        1.1157661610784888E-4, 1.0889805864759895E-6},
      //D16: 
      {0, 0.17040950551660405, 0.4054017019156317, 
       0.3661696710796683, 0.0604435506539972, 
       -0.03417520232921274, -0.05063755358899017, 
       -0.053706354199476905, 0.016939316680752922, 
       -4.0602338670891433E-4, 0.16702834508201225, 
       -0.04152733674980347, -0.007240181491384487, 
       0.001324067261206182, -2.350644429586776E-5},
      //D18: 
      {0, 1.0891076219609888, 0.17919863858978144, 
       0.061595688916198425, -0.26606002357202824, 
       0.005152631498202376, 0.009329398758173613, 
       -0.004258932562707229, -0.08916832183995663, 
       0.005092901626677226, 0.0029523764074319684, 
       0.00597670066946684, 0.0016673168036591525, 
       -5.60721297592991E-4, -2.7944653635525525E-5, 
       2.6787390005221754E-6, -1.0043659869376354E-8},
      //D20:
      {0, 0.15137830179259662, 0.31457706577845934, 
       0.3831923397952537, 0.15642870506535958, 
       0.39140241330115594, -0.3226323740894305, 
       0.13495433106736143, -0.4873883783789785, 
       0.10346335970075286, 0.1808533111725595, 
       0.0790475107522262, -0.1404264239706484, 
       0.05381736012258318, -0.0011381307595805482, 
       0.0013137982444027742, 0.0010412852511930792, 
       1.1423890858598409E-4, 1.2862461480070748E-6
      }
   };
   /** Data structure to store values of phi to be used in later recursion calls.*/
   private static java.util.TreeMap<Double,Double> phiValues = new java.util.TreeMap<>();
  
   /** 
    * returns the value of the scaling function <sub><i>N</i></sub>&#x3C6;(<i>x</i>) of the 
    * Daubechies wavelet D<i>N</i>, where <i>N</i> = 2, 4, ..., 20.
    * The function value is computed recursively for any dyadic number <i>x</i>,
    * i.e., a number with a finite binary expansion. More exactly, the mantissa
    * of <i>x</i> containing at most the first 52 bits of its binary expansion
    * according to the IEEE754 format is truncated by the accuracyBitMask.
    * This way the recursions always terminate at an integer which yields either
    * zero or one of the predefined initial values.
    *
    * @param N the index of the Daubechies wavelet class D2 ... D20, 
    * @param x the value for which the scaling function value is computed
    * @return the value of the scaling function <sub><i>N</i></sub>&#x3C6;(<i>x</i>) of D<i>N</i>
    * @see #accuracyBits
    * @see #accuracyBitMask
    * @see #initialValues
    * @see #psi(int,double)
    */
   public static double phi(int N, double x) {
      double dn=0;
      int _N = N/2 - 1; // index to access the initialValues array, i = N-1
      
      if ( _N < 0 || initialValues.length <= _N ) return Double.NaN;
      if ( x < 0 || x >= N - 1 ) return 0.0;

      for ( int k = 0; k < initialValues[_N].length; k++ ) {
         if ( Math.abs(x - k) < ACCURACY ) {
            return initialValues[_N][k];
         }
      }
      
      //Double X = new Double(x);
      if ( phiValues.containsKey(x) ) {
         return phiValues.get(x);
      }
      
      /* The following instruction truncates x to the next lower dyadic number 
       * determined by the accuracy. This step is necessary for a number with
       * a long binary expansion (such as 1/3 or irrational numbers PI or SQRT2)
       * to be computed in reasonable time.
       */
      x = Double.longBitsToDouble(Double.doubleToLongBits(x) & accuracyBitMask);
      
      for ( int k = 0; k < h[_N].length; k++ ) {
         dn += SQRT2 * h[_N][k] * phi(N, 2*x - k);
         recursionCalls++;
      }
      phiValues.put(x, dn);
      return dn;
   }
   
   /** 
    * returns the function value of the Daubechies wavelet <sub><i>N</i></sub>&#x3C8;(<i>x</i>) 
    * of the Daubechies class D<i>N</i>, where <i>N</i> = 2, 4, ..., 20.
    * The computation uses the scaling function <sub><i>N</i></sub>&#x3C6;(<i>x</i>).
    *
    * @param N the index of the Daubechies wavelet class D2 ... D20, 
    * @param x the value for which the wavelet value is computed
    * @return the function value of the Daubechies wavelet <sub><i>N</i></sub>&#x3C8;(<i>x</i>)
    * @see #phi(int,double)
    */
   public static double psi(int N, double x) {
      if ( x < 0 || x >= N - 1 ) return 0.0;
      
      int _N = N/2 - 1; // index to access the h filter array
      double result = 0;
      for( int k = 0; k < h[_N].length; k++ ) {
         if ( k % 2 == 0 ) {
            result += h[_N][N - 1 - k] * phi(N, 2*x - k);
         } else {
            result -= h[_N][N - 1 - k] * phi(N, 2*x - k);
         }
      }
      return SQRT2 * result;
   }
   
   /**
    * Computes the fast Daubechies wavelet transform of a data vector,
    * applying the Daubechies D-<i>N</i> wavelet; the returned two-dimensional array
    * contains the wavelet coefficients in its [0]-component, and the scaling function
    * coefficient in its [1]-component; <i>N</i> has to be an even number with 
    * 2 &#x2264; <i>N</i> &#x2264; 20. 
    * If the number of data points is not a power of 2, the data are augmented to the
    * next power <i>n</i> of two and filled up with zeros.
    * <p>
    * The principle of the Daubechies wavelet transform is as follows. 
    * Initially, the data points <i>a</i>[0], ..., <i>a</i>[2<sup><i>n</i></sup> - 1] 
    * are identified as the coefficients of the
    * Daubechies scaling function (or "father wavelet"). They determine recursively
    * the wavelet coefficients <i>c</i>[<i>k</i>] according to the scheme
    * </p>
    * <!--
    *             c0[k] = null
    *             a0[k| = a[0] a[1] ... a[2^n - 1]
    *
    *      =>     c1[0] c1[1] ... c1[2^{n-1} - 1]
    *             a1[0] a1[1] ... a1[2^{n-1} - 1]
    *
    *      =>     c2[0] ... c2[2^{n-2} - 1] | c1[0] ... c1[2^{n-1} - 1]
    *             a2[0] ... a2[2^{n-2} - 1]
    *
    *      =>     ...
    *
    *      =>     cn[0] | c(n-1)[0] c(n-1)[1] | ... | c1[0] ... c1[2^{n-1} - 1]
    *             an[0]
    * -->
    * <table summary="" border="0">
    *   <tr>
    *     <td style="text-align:right">
    *       <i>c</i><sub>0</sub>[<i>k</i>] = 
    *     </td>
    *     <td>
    *        <table summary="" border="0">
    *          <tr>
    *            <td>
    *              <table summary="" border="1"><tr><td>null</td></tr></table>
    *            </td>
    *          </tr>
    *        </table>
    *     </td>
    *   </tr>
    *   <tr>
    *     <td style="text-align:right">
    *       <i>a</i><sub>0</sub>[<i>k</i>] = 
    *     </td>
    *     <td>
    *        <table summary="" border="0">
    *          <tr>
    *            <td>
    *              <table summary="" border="1"><tr><td>a[0]</td><td>a[1]</td><td> ... </td><td>a[2<sup><i>n</i></sup> - 1]</td></tr></table>
    *            </td>
    *          </tr>
    *        </table>
    *     </td>
    *   </tr>
    *   <tr><td>&nbsp;</td></tr>
    *   <!-- next recursion step: -->
    *   <tr>
    *     <td style="text-align:right">
    *       &#x21D2;
    *       <i>c</i><sub>1</sub>[<i>k</i>] = 
    *     </td>
    *     <td>
    *        <table summary="" border="0">
    *          <tr>
    *            <td>
    *              <table summary="" border="1">
    *                <tr><td> c<sub>1</sub>[0] </td><td> c<sub>1</sub>[1] </td><td> ... </td><td> c<sub>1</sub>[2<sup><i>n</i>-1</sup> - 1]</td></tr>
    *              </table>
    *            </td>
    *          </tr>
    *        </table>
    *     </td>
    *   </tr>
    *   <tr>
    *     <td style="text-align:right">
    *       <i>a</i><sub>1</sub>[<i>k</i>] = 
    *     </td>
    *     <td>
    *        <table summary="" border="0">
    *          <tr>
    *            <td>
    *              <table summary="" border="1">
    *                <tr><td> a<sub>1</sub>[0] </td><td> a<sub>1</sub>[1] </td><td> ... </td><td> a<sub>1</sub>[2<sup><i>n</i>-1</sup> - 1]</td></tr>
    *              </table>
    *            </td>
    *          </tr>
    *        </table>
    *     </td>
    *   </tr>
    *   <tr><td>&nbsp;</td></tr>
    *   <!-- next recursion step: -->
    *   <tr>
    *     <td style="text-align:right">
    *       &#x21D2;
    *       <i>c</i><sub>2</sub>[<i>k</i>] = 
    *     </td>
    *     <td>
    *        <table summary="" border="0">
    *          <tr>
    *            <td>
    *              <table summary="" border="1">
    *                <tr><td>c<sub>2</sub>[0] </td><td> ... </td><td> c<sub>2</sub>[2<sup><i>n</i>-2</sup> - 1]</td></tr>
    *              </table>
    *            </td>
    *            <td>
    *              <table summary="" border="1">
    *                <tr><td>c<sub>1</sub>[0] </td><td> c<sub>1</sub>[1] </td><td> ... </td><td> c<sub>1</sub>[2<sup><i>n</i>-1</sup> - 1]</td></tr>
    *              </table>
    *            </td>
    *          </tr>
    *        </table>
    *     </td>
    *   </tr>
    *   <tr>
    *     <td style="text-align:right">
    *       <i>a</i><sub>2</sub>[<i>k</i>] = 
    *     </td>
    *     <td>
    *        <table summary="" border="0">
    *          <tr>
    *            <td>
    *              <table summary="" border="1">
    *                <tr><td> a<sub>2</sub>[0] </td><td> ... </td><td> a<sub>2</sub>[2<sup><i>n</i>-2</sup> - 1]</td></tr>
    *              </table>
    *            </td>
    *          </tr>
    *        </table>
    *     </td>
    *   </tr>
    *   <tr><td>&nbsp;</td></tr>
    *   <tr><td>&#x21D2;</td><td> &nbsp; .&nbsp;.&nbsp;.</td></tr>
    *   <tr><td>&nbsp;</td></tr>
    *   <!-- next recursion step: -->
    *   <tr>
    *     <td style="text-align:right">
    *       &#x21D2;
    *       <i>c</i><sub><i>n</i></sub>[<i>k</i>] = 
    *     </td>
    *     <td>
    *        <table summary="" border="0">
    *          <tr>
    *            <td>
    *              <table summary="" border="1">
    *                <tr><td>c<sub><i>n</i></sub>[0]</td></tr>
    *              </table>
    *            </td>
    *            <td> 
    *              <table summary="" border="1">
    *                <tr><td>c<sub><i>n</i>-1</sub>[0] </td><td> c<sub><i>n</i>-1</sub>[1] </td></tr>
    *              </table>
    *            </td>
    *            <td> 
    *              &nbsp; .&nbsp;.&nbsp;. &nbsp; 
    *            </td>
    *            <td>
    *              <table summary="" border="1">
    *                <tr><td>c<sub>2</sub>[0] </td><td> ... </td><td> c<sub>2</sub>[2<sup><i>n</i>-2</sup> - 1]</td></tr>
    *              </table>
    *            </td>
    *            <td>
    *              <table summary="" border="1">
    *                <tr><td>c<sub>1</sub>[0] </td><td> c<sub>1</sub>[1] </td><td> ... </td><td> c<sub>1</sub>[2<sup><i>n</i>-1</sup> - 1]</td></tr>
    *              </table>
    *            </td>
    *          </tr>
    *        </table>
    *     </td>
    *   </tr>
    *   <tr>
    *     <td style="text-align:right">
    *       <i>a</i><sub><i>n</i></sub>[<i>k</i>] = 
    *     </td>
    *     <td>
    *        <table summary="" border="0">
    *          <tr>
    *            <td>
    *              <table summary="" border="1">
    *                <tr><td> a<sub><i>n</i></sub>[0]</td></tr>
    *              </table>
    *            </td>
    *          </tr>
    *        </table>
    *     </td>
    *   </tr>
    * </table>
    *
    * Hence there are 2<sup>0</sup> + 2<sup>1</sup> + ... + 2<sup><i>n</i>-2</sup> + 2<sup><i>n</i>-1</sup> = 2<sup><i>n</i></sup>-1 
    * wavelet coefficients <i>c</i>[<i>k</i>], and a single scaling function coefficient 
    * <i>a</i>[0] returned. The coefficient a[0] is necessary to reconstruct the data completely.
    * See <a href="http://haegar.fh-swf.de/Publikationen/Wavelets.pdf" target="_top">http://haegar.fh-swf.de/Publikationen/Wavelets.pdf</a> for details.
    *
    * @param N the index of the applied Daubechie wavelet (<i>N</i> = 2, 4, ..., 20)
    * @param a the data to be transformed
    * @return a two-dimensional array containing the wavelet coefficients in its [0]-component, 
    *   and the scaling function coefficient in its [1]-component
    * @see #inverseTransform(int,double[][])
    */
   public static double[][] transform( int N, double[] a) {
      // preprocessing: if a.length is not a power of 2 it is filled up with zeros:
      double[] aNew = new double[ 1 << (int) Math.ceil(Math.log(a.length) / Math.log(2)) ];
      for ( int i = 0; i < a.length; i++) {
         aNew[i] = a[i];
      }
      double[] c = new double[0];
      double[][] w = {c,aNew};
      if (N % 2 != 0 || N < 2 || N > 20) return w;      
      return daub(N, w, true);
   }
   
   /** 
    * Computes the inverse fast Daubechies wavelet transform of a two-dimensional array <code>w</code> containing
    * a vector <code>w[0]</code> of the Daubechies D-<i>N</i> wavelet coefficients, and an array
    * <code>w[1]</code> containing the scaling function coefficient.
    * There are the following three mandatory restrictions for the parameters,
    * due to the wavelet transform algorithm as performed by 
    * {@link #transform(int,double[]) transform} method:
    * <ul>
    * <li><i>N</i> must an even number with 2 &#x2264; <i>N</i> &#x2264; 20.</li>
    * <li>The number <code>w[0].length</code> of wavelet coefficients <i>must</i> satisfy
    * <code>w[0].length</code> = 2<sup><i>n</i></sup>-1 for an integer <i>n</i>.
    * <li>The number <code>w[1].length</code> of scaling function coefficients <i>must</i> be 1.
    * </ul>
    *
    * @param N the index of the applied Daubechie wavelet (<i>N</i> = 2, 4, ..., 20)
    * @param w the wavelet coefficients <code>w[0]</code> and scaling function coefficients 
    * <code>w[1]</code> to be transformed
    *
    * @return a one-dimensional array of the transformed data (more exactly, the scaling function coefficients)
    * @see #transform(int,double[])
    */
   public static double[] inverseTransform( int N, double[][] w) {
      if ( N % 2 != 0 || N < 2 || N > 20 || w.length != 2) return w[0];
      int n = (int) Math.ceil(Math.log(w[0].length) / Math.log(2));
      if ( (1 << n) - 1 != w[0].length || w[1].length != 1 ) return w[0]; 
      return daub(N, w, false)[1];
   }

   /**
    * Applies the Daubechies N-coefficient wavelet filter to the two-dimensional
    * array w consisting of
    * if <code>forward</code> is true, and its inverse if <code>forward</code>
    * is false; N has to be an even number with 2 <= N <= 20, and the
    * number of data points must be a power of 2, i.e., w[1].length = 2^n.
    * Here the first array w[0] contains the wavelet coefficients c[j],
    * the second one w[1] the scaling function factors a[j].
    */
   private static double[][] daub( int N, double[][] w, boolean forward ) {      
      final int _N = N/2 - 1; // index of h: h[_N] contains the Daubechies-N coefficients
      int j, jMod1, jMod2, k;
      double[] a;    // the new scaling function coefficients
      double[] cNew; // the complete wavelet coefficients to be returned          
      
      if (forward) {  // apply wavelet transformation
         if ( w[1].length == 1) {  // end of recursion
            return w;
         }
      
         a = new double[w[1].length/2]; // the new scaling function coefficients
         double[] c = new double[w[1].length/2]; // the new (additional) wavelet coefficients
         
         for (k = 0; k < a.length; k++) {
            // a_{k} = sum_j  h[_N]_j w[1]_{2*k+j+k mod 2^{n-1}},
            // c_{k} = sum_j (-1)^(j+1) h[_N|_j a_{2*k+N-1-j mod 2^{n-1}}:
            for (j = 0; j < N; j++) {
               a[k] += h[_N][j] * w[1][(2*k + j) & (w[1].length - 1)];
//if( a.length == 4 ) System.out.println("### a_"+k+"+= h_"+j+" a_"+((2*k + j) & (w[1].length - 1)));
               jMod1 = (2*k + N - 1 - j) & (w[1].length - 1); // = (2k+N-1-j) mod w[1].length
               if (j % 2 == 0) {
                  c[k] -= h[_N][j] * w[1][jMod1];
               } else {
                  c[k] += h[_N][j] * w[1][jMod1];
               }
//if( a.length == 4 ) System.out.println("### c_"+k+((j%2==0)?"-":"+")+"= h_"+j+" a_"+jMod1);
            }
//if( a.length == 4 ) System.out.println("### c_"+k+"="+c[k]/SQRT2+" (c_0="+-3.0/8+")");
//if( a.length == 2 && k==0 ) System.out.println("### c_"+k+"="+c[k]/2+" (c_0="+(35-11*SQRT3)/32+")");
//if( a.length == 2 && k==1 ) System.out.println("### c_"+k+"="+c[k]/2+" (c_1="+(-27+3*SQRT3)/32+")");
         }
         cNew = new double[ c.length + w[0].length ];
         for(int i=0; i < c.length; i++) {
            cNew[i] = c[i];
         }
         for(int i=0; i < w[0].length; i++) {
            cNew[ i + c.length ] = w[0][i];
         }
      } else {  // apply inverse wavelet transformation
         if ( w[0].length == 0 ) {  // end of recursion
            return w;
         }

         a = new double[2*w[1].length]; // the new scaling function coefficients
         for ( k = 0; k < w[1].length; k++ ) {
            // a_{2k} = sum_j h[_N]_{2j} a_{k-j mod w[1].length} 
            //              + h[_N]_{2j+1} c_{k+j-N/2+1 mod w[1].length]},
            // a_{2k+1} = sum_j h[_N]_{2j+1} a_{k-j mod w[1].length} 
            //              + h[_N]_{2j} c_{k+j-N/2+1 mod w[1].length]}:
            for ( j = 0; j < N/2; j++ ) {
               jMod1 = (k - j) & (w[1].length - 1); // = (k-j) mod w[1].length
               jMod2 = (k + j - N/2 + 1) & (w[1].length - 1); // = (k+j-N/2+1) mod w[1].length
               a[2*k] += h[_N][2*j] * w[1][jMod1] + h[_N][2*j+1] * w[0][jMod2];
               a[2*k+1] += h[_N][2*j+1] * w[1][jMod1] - h[_N][2*j] * w[0][jMod2];
            }
         }
         cNew = new double[ w[0].length - w[1].length ];
         for(int i=0; i < cNew.length; i++) {
            cNew[i] = w[0][i + w[1].length];
         }
      }
      double[][] result = {cNew, a};
      return daub(N, result, forward);  // next recursion step
   }
   
   /**
    * Daubechies cascade algorithm to compute values of phi_N [Ch. 6.5 in Daubechies (1992)].
    */
   /*
   private static double[] cascade(int N, double[] w, int level) {      
      final int _N = N/2 - 1; // index of h: h[_N] contains the Daubechies-N coefficients
      int k, j, jMod1; //, jMod2;
      double[] a;    // the new scaling function coefficients
      //double[] cNew; // the complete wavelet coefficients to be returned                
      if ( level == 0 ) {  // end of recursion
         return w;
      }

      a = new double[2*w.length]; // the new scaling function coefficients
      for ( k = 0; k < w.length; k++ ) {
         // a_{2k} = sum_j h[_N]_{2j} a_{k-j mod w[1].length} 
         //              + h[_N]_{2j+1} c_{k+j-N/2+1 mod w[1].length]},
         // a_{2k+1} = sum_j h[_N]_{2j+1} a_{k-j mod w[1].length} 
         //              + h[_N]_{2j} c_{k+j-N/2+1 mod w[1].length]}:
         for ( j = 0; j < N/2; j++ ) {
            jMod1 = (k - j) & (w.length - 1); // = (k-j) mod w[1].length
            //jMod2 = (k + j - N/2 + 1) & (w[1].length - 1); // = (k+j-N/2+1) mod w[1].length
            a[2*k] += SQRT2 * h[_N][2*j] * w[jMod1];
            a[2*k+1] += SQRT2 * h[_N][2*j+1] * w[jMod1];
         }
      }
      return cascade(N, a, level - 1);  // next recursion step
   }
   // */
      
   /** This routine checks the conditions the Daubechies coefficients have to satisfy.
    *  These are, for each wavelet D2<i>N</i> (<i>N</i> = 1, ..., 10):
    *  <pre>
    *    - sumEven = sum_k^{N-1} h_{2k} = 1/sqrt(2);
    *    - sumOdd  = sum_k^{N-1} h_{2k+1} = 1/sqrt(2);
    *    - For each integer m = 0, 1, ..., N-1:
    *         sum_{k=2m}^{2N-1+2m} h_{k} h_{k-2m}
    *         = 1 if m=0,
    *         = 0 otherwise.
    *  </pre>
    * See <a href="http://math-it.org/Publikationen/Wavelets.pdf" target="_top">http://math-it.org/Publikationen/Wavelets.pdf</a> 
    * for details.
    * @return a message about the relevant data
    */
   public static String checkH() {
      //Output format of double numbers:
      //final java.text.DecimalFormat DF = new java.text.DecimalFormat("#0.################");
      final java.text.DecimalFormat DF = new java.text.DecimalFormat("#0.##############");
      String output = "";
      double sumEven, sumOdd, sumOrthogonal, sumWeighted;
      int N; //, min, max;
      
      for (int i = 0; i < h.length; i++) {
         N = i+1;
         output += "\nD" + 2*N + ": ";
         sumEven = 0;
         sumOdd = 0;
         for (int k=0; k < N; k++) {
            sumEven += h[i][2*k];
            sumOdd  += h[i][2*k + 1];
         }
         
         output += "sumEven = " + DF.format(sumEven*SQRT2) + ", sumOdd = " + DF.format(sumOdd*SQRT2);
         for (int m = 0; m < N; m++) {
            sumOrthogonal = 0;
            for ( int k = 2*m; k <= 2*N-1; k++ ) {
               sumOrthogonal += h[i][k] * h[i][k - 2*m];
            }
            output += "\n m=" + m + ": sumOrthogonal = " + DF.format(sumOrthogonal);
         }
         
         // check weighted sum:
         sumWeighted = 0;
         for (int k=0; k < 2*N; k++) {
            if (k % 2 == 0) {
               sumWeighted += k * h[i][k];
            } else {
               sumWeighted -= k * h[i][k];
            }
         }
         output += "\n sumWeighted = " + DF.format(sumWeighted);
      }
      return output;
   }
   
   /** Returns a structured string representation of the wavelet coefficients
    *  given by the array <i>w</i>.
    *  @param w array of wavelet coefficients
    *  @return a structured string representation of <i>w</i>
    */
   /*
   private static String showCoefficients( double[] w ) {
      String output = "";
      int m,i;
      for( m = 1; m <= w.length; m *= 2) {
         for( i = m-1; i < 2*m-2; i++) {
            output += w[i] + "|";
         }
         output += w[2*m-2] + " ||\n";
      }
      return output;
   }
   */

   /** Returns a structured string representation of the wavelet coefficients
    *  given by the array <i>w</i>.
    *  @param w array of wavelet coefficients
    *  @return a structured string representation of <i>w</i>
    */
   public static String showWaveletCoefficients( double[][] w ) {
      String output = "";
      int m,i;
      for( m = 1; m <= w[0].length; m *= 2) {
         for( i = m-1; i < 2*m-2; i++) {
            output += w[0][i] + "|";
         }
         output += w[0][2*m-2] + " ||\n";
      }
      return output;
   }
  
   /** Returns a structured string representation of the normalized Daubechies 
    *  filter coefficients {@link #h h}, in the specified format.
    *  The format can be one of the following string values:
    *  <ul>
    *    <li><code>"LaTeX"</code> for LaTeX format</li>
    *    <li><code>"Mathematica"</code> for Mathematica input format</li>
    *    <li>
    *      <code>"Java (rescaled)"</code> for a Java formatted output of
    *      the filter coefficients times {@link org.mathIT.numbers.Numbers#SQRT2 &#x221A;2}
    *    </li>
    *  </ul>
    *  @param format format of the string to be returned. E.g., "LaTeX", "Mathematica", or "Java (rescaled)"
    *  @return a structured string representation of <i>w</i>
    */
   public static String showDaubCoefficients(String format) {
      String output = "";
      if ( format.equalsIgnoreCase("LaTeX") ) {
         output += "\\begin{tabular}{*{" + h.length + "}{|r}|}\n";
         output += "%\n";
         output += "\\hline\n";
         for (int i=0; i < h.length - 1; i++) {
            output += "\\multicolumn{1}{|c}{\\textbf{D" + 2*(i+1) + "}} &\n";
         }
         output += "\\multicolumn{1}{|c|}{\\textbf{D" + 2*h.length + "}}\n";
         output += "\\\\ \\hline";
         for (int i=0; i < h[ h.length - 1].length ; i++) {
            for (int j=0; j < h.length - 1; j++) {
               output += (h[j].length > i) ? " " + h[j][i] : " ";
               output += " &";
            }
            output += " " + h[ h.length - 1 ][i] + "\n\\\\ \\hline\n";
         }
         output += "\\end{tabular}";
      } else if ( format.equalsIgnoreCase("Mathematica") ) {
         int[] nList = {4, 6, 8, 10, 12, 14, 16, 18, 20};
         output += "Remove[\"Global`*\"];";
         for (int index=0; index < nList.length; index++) {
            int N = nList[index];
            output += "\n\n(* Daubechies coefficients (N="+N+") *)\n\n";
            for (int i=0; i < h[N/2-1].length ; i++) {
               output += "h"+i+" = " + h[N/2-1][i] + ";\n";
            }
            output += "\nA = SetPrecision[\n{\n";
               for (int i = 1; i <= N - 2; i++) {
                  output += "  {";
                  for (int j = 1; j <= N - 2; j++) {
                     if (0 <= 2*i-j && 2*i-j < N) {
                        output += "h" + (2*i-j); //df.format(SQRT2 * h[_N][2*i-j]);
                     } else {
                        output += 0;
                     }
                     if ( j < N-2 ) output += ", ";
                  }
                  output += "}";
                  output += (i < N-2) ? ",\n" : "\n}, 25];\n";
               }
               output += "Eigenvectors[A] >> init"+N+".asc;\n";
         }
      } else if ( format.equalsIgnoreCase("Java (rescaled)") ) {
         output += "\n\n//Daubechies coefficients:\n   double[][] h = {";
         int[] nList = {4, 6, 8, 10, 12, 14, 16, 18, 20};
         for (int index=0; index < nList.length; index++) {
            int N = nList[index];
            output += "\n      // N="+N/2+" (D"+N+"):\n      {\n         ";
            for (int i=0; i < h[N/2-1].length - 1; i++) {
               output += h[N/2-1][i] * SQRT2 + ", ";
            }
            output += SQRT2 * h[N/2-1][h[N/2-1].length - 1] + "\n      } ";
            output += index == nList.length - 1 ? "\n   };" : ",";
         }
      }
      return output;
   }
   
   /** Serves only for test purposes...*/
   /*
   public static void main(String[] args) {
      long time;
      double x, y;
      int n;
      java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.###");
      double[] data;
      double[] data1;
      double[][] tmp;

      int N=4;
      if ( args.length >= 1 ) N = Integer.parseInt(args[0]); 
      System.out.println(checkH());
      //System.out.println(showDaubCoefficients("LaTeX"));
      //System.out.println(showDaubCoefficients("Mathematica"));
      System.out.println(showDaubCoefficients("Java (rescaled)"));
      
      // --- Berechnung Initialwerte: -------
      //double summe = 0;
      //int _N = N/2 - 1;
      //for (int i = 0; i < initialValues[_N].length; i++) {
      //   summe += initialValues[_N][i];
      //}
      
//      String ausgabe = "(Summe = "+summe+")\n\n";
//      ausgabe += "      {0, ";
//      for (int i = 0; i < initialValues[_N].length - 1; i++) {
//      //for (int i = initialValues[_N].length - 1; i > 0; i--) {
//         //normiert:
//         ausgabe += initialValues[_N][i] / summe + ", ";
//         //nicht normiert:
//         //ausgabe += initialValues[_N][i] + ", ";
//         if ( i % 2 != 0 ) ausgabe += "\n       ";
//         //if ( i % 2 == 0 ) ausgabe += "\n       "; //wenn falsch rum
//      }
//      //normiert:
//      //ausgabe += initialValues[_N][0] / summe + "\n      }";
//      ausgabe += initialValues[_N][initialValues[_N].length - 1]/summe + "\n      }";
//      //nicht normiert:
//      //ausgabe += initialValues[_N][0] + "\n      }";
//      System.out.println(ausgabe);
      // --- Berechnung Initialwerte  ----------------------

//      String accuracyBitMaskString = "";
//      for ( int bit = 51; bit >= 0; bit-- ) { // Long.toBinaryString cuts the leading zeros!!
//         accuracyBitMaskString += (accuracyBitMask & (1L << bit)) >> bit;
//      }
//      System.out.println("### accuracyBitMask="+accuracyBitMaskString);

      //------------------------------------------------
//      x = .01;
//      long longBits = Double.doubleToLongBits(x);
//      long sign     = (longBits & 0x8000000000000000L) >> 63;
//      long exponent = (longBits & 0x7ff0000000000000L) >> 52;
//      long mantisse = (longBits & 0x000fffffffffffffL);
//      int lowestOneBit = 0;
//      String mantisseString = "";

//      for ( int bit = 51; bit >= 0; bit-- ) { // Long.toBinaryString cuts the leading zeros!!
//         mantisseString += (mantisse & (1L << bit)) >> bit;
//      }
//      System.out.println("### "+x+": sign="+sign+", exponent="+(exponent-1023)+", mantisse="+mantisseString);
//      
//      System.out.println("### "+x+" => "+(x=Double.longBitsToDouble(longBits)));
//      
//      // position of rightmost one-bit in mantisse:
//      for ( int bit = 0; bit <= 51 && lowestOneBit == 0; bit++ ) {
//         if ( (mantisse & (1L << bit)) >> bit == 1 ) lowestOneBit = bit;
//      }
//      System.out.println("### lowest one-bit in mantisse: "+lowestOneBit+" => "+(52-lowestOneBit-exponent+1023)+" levels");

//      recursionCalls = 1;
//      time = System.currentTimeMillis();
//      y = phi(N, x);
//      time = System.currentTimeMillis() - time;
//      System.out.println("phi_"+N/2+"("+x+") = " + y + "\n ("+recursionCalls+" recursive calls, running time "+time/1000.+" sec)");
//      //------------------------------------------------
//      //------------------------------------------------
//      x = .25; //1./3.;
//      recursionCalls = 1;
//      time = System.currentTimeMillis();
//      y = phi(N, x);
//      time = System.currentTimeMillis() - time;
//      System.out.println("phi_"+N/2+"("+x+") = " + y + "\n ("+recursionCalls+" recursive calls, running time "+time/1000.+" sec)");
//      //------------------------------------------------
//      //------------------------------------------------
//      x = 1./3.;
//      recursionCalls = 1;
//      time = System.currentTimeMillis();
//      y = phi(N, x);
//      time = System.currentTimeMillis() - time;
//      System.out.println("phi_"+N/2+"("+x+") = " + y + "\n ("+recursionCalls+" recursive calls, running time "+time/1000.+" sec)");
//      //------------------------------------------------
//      //------------------------------------------------
//      x = N-2;
//      recursionCalls = 1;
//      time = System.currentTimeMillis();
//      y = phi(N, x);
//      time = System.currentTimeMillis() - time;
//      System.out.println("phi_"+N/2+"("+x+") = " + y + "\n ("+recursionCalls+" recursive calls, running time "+time/1000.+" sec)");
//      //------------------------------------------------
//      //------------------------------------------------
//      x = N-1;
//      recursionCalls = 1;
//      time = System.currentTimeMillis();
//      y = phi(N, x);
//      time = System.currentTimeMillis() - time;
//      System.out.println("phi_"+N/2+"("+x+") = " + y + "\n ("+recursionCalls+" recursive calls, running time "+time/1000.+" sec)");
//      //------------------------------------------------

//      // --- Bitmask Test: ---
//      for( int m = 3; m < 20; m++ ) {
//         x = 2./m;
//      //for( x = .01; x < .03; x += .01) {
//         longBits = Double.doubleToLongBits(x);
//         sign     = (longBits & 0x8000000000000000L) >> 63;
//         exponent = (longBits & 0x7ff0000000000000L) >> 52;
//         //exponent = (longBits >> 52 ) & 0x7ffL;
//         mantisse = (longBits & 0x000fffffffffffffL);
//         mantisseString = "";
//         for ( int bit = 51; bit >= 0; bit-- ) { // Long.toBinaryString cuts the leading zeros!!
//            mantisseString += (mantisse & (1L << bit)) >> bit;
//         }
//         System.out.println("### "+x+": sign="+sign+", exponent="+(exponent-1023)+", mantisse="+mantisseString);
//         // position of rightmost one-bit in mantisse:
//         lowestOneBit = 0;
//         for ( int bit = 0; bit <= 51 && lowestOneBit == 0; bit++ ) {
//            if ( (mantisse & (1L << bit)) >> bit == 1 ) lowestOneBit = bit;
//         }
////         System.out.println("### lowest one-bit in mantisse: "+lowestOneBit+" => "+(52-lowestOneBit-exponent+1023)+" levels");
//         
//         mantisse &= accuracyBitMask;
//         long mantisse2 = mantisse + (1L << 51 - accuracyBits);
//         mantisseString = "";
//         for ( int bit = 51; bit >= 0; bit-- ) { // Long.toBinaryString cuts the leading zeros!!
//            mantisseString += (mantisse & (1L << bit)) >> bit;
//         }
////         System.out.println("### "+x+": sign="+sign+", exponent="+(exponent-1023)+", mantisse="+mantisseString);
//         mantisseString = "";
//         for ( int bit = 51; bit >= 0; bit-- ) { // Long.toBinaryString cuts the leading zeros!!
//            mantisseString += (mantisse2 & (1L << bit)) >> bit;
//         }
////         System.out.println("### "+x+" (2): mantisse="+mantisseString);
//         lowestOneBit = 0;
//         for ( int bit = 0; bit <= 51 && lowestOneBit == 0; bit++ ) {
//            if ( (mantisse & (1L << bit)) >> bit == 1 ) lowestOneBit = bit;
//         }
//         //System.out.println("### lowest one-bit in mantisse: "+lowestOneBit+" => "+(52-lowestOneBit-exponent+1023)+" levels");
//         
//         long longBits2 = (longBits & accuracyBitMask) + (1L << 51 - accuracyBits);
//         longBits &= accuracyBitMask;
//         System.out.println("### "+x+"  => "+Double.longBitsToDouble(longBits));
//         System.out.println("### "+x+" (2) "+Double.longBitsToDouble(longBits2));
//         System.out.println("### Diff   =  "+(x - Double.longBitsToDouble(longBits)));
//         System.out.println("### Diff2  =  "+(Double.longBitsToDouble(longBits2) - x));
//      }
//      
//      long start;
//      n = 2;
//      data = new double[1<<n];
//           
//      // test dwt:
//      //data = new double[257];
//      //transform(20,data);
//      //------------------
//
//      data = new double[1<<n];
//      //data[0] = 1;
//      //data[0] = 1;
//      System.out.println("Data: ");
//      for (int j = 0; j < data.length; j++) {
//         if (j % 4 == 0) data[j] = 5;
//         else if ( (j+2) % 4 == 0) data[j] = -5;
//         System.out.print(""+df.format(data[j])+";");
//      }
//      System.out.println();
//         
//      start = System.currentTimeMillis();
//      tmp = transform(N,data);
//      for (int i = 0; i < tmp[0].length; i++) {
//         if ( Math.abs(tmp[0][i]) < ACCURACY ) tmp[0][i] = 0; // compress!
//      }
//      System.out.println(" Time for Daub("+N+") wavelet transform: " + 
//         ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");
//      System.out.println("Wavelet coefficients:\n" + showWaveletCoefficients(tmp));

//      // Cascade algorithm to compute initial values ...
//      for (int i = 0; i < 20; i++) {
//         data1 = cascade(N,data, i);
//         //System.out.println("data of level " + i +":");
//         for (int j = 0; j < data1.length; j++) {
//            if ( Math.abs(data1[j] - phi(N,1)) < 1e-5 || Math.abs(data1[j] - phi(N,2)) < 1e-5 )
//               //System.out.print(""+df.format(data1[j])+";");
//               System.out.println("level "+i+", data1["+j+"]="+data1[j]+" (length="+data1.length+")");
//            if ( j == data1.length/2 - 1)
//               System.out.println("level "+i+": data1["+j+"]="+data1[j]);
//         }
//         //System.out.println("\n-----");      
//      }
      
//
//      for (int i = 0; i < data.length; i++) {
//         data = new double[1<<n];
//         data[i] = 1;
//         System.out.println("Data: ");
//         for (int j = 0; j < data.length; j++) {
//            System.out.print(""+df.format(data[j])+";");
//         }
//         System.out.println();
//         
//         double[] a = new double[1]; a[0] = data[0];
//         double[] c = new double[data.length - 1];
//         for (int j = 0; j < c.length; j++) {
//            c[j] = data[j+1];
//         }
//         double[][] result = {c,a};
//         System.out.print("Wavelet coefficients: " + showWaveletCoefficients(result));
//         System.out.println("a=" + a[0] + "\n");
//         
//         data = inverseTransform(N,result);
//         
//         System.out.print("data=");
//         for (int j = 0; j < data.length; j++) {
//            System.out.print(""+df.format(data[j])+";");
//         }
//         System.out.println("\n-----");
//      
//      }
      
//      data = new double[data.length];
//      System.out.println("Data: ");
//      for (int j = 0; j < data.length; j++) {
//         if (j < data.length/3) data[j] = 100 - .1*j;
//         System.out.print(""+df.format(data[j])+";");
//      }
//      System.out.println();
//
//      start = System.currentTimeMillis();
//      tmp = transform(N,data);
//      System.out.println(" Time for Daub("+N+") wavelet transform: " + 
//         ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");
//      //System.out.println("Data transformed: ");
//      //System.out.print("c=");
//      for (int i = 0; i < tmp[0].length; i++) {
//         if ( Math.abs(tmp[0][i]) < ACCURACY ) tmp[0][i] = 0; // compress!
//         //System.out.print(""+df.format(tmp[0][i])+";");
//      }


//      System.out.print("\na=");
//      for (int i = 0; i < tmp[1].length; i++) {
//         System.out.print(""+df.format(tmp[1][i])+";");
//      }
//      System.out.println();
//      
//      System.out.println("Wavelet coefficients:\n" + showWaveletCoefficients(tmp));
//      
////      // --- inverse ---:
////      start = System.currentTimeMillis();
////      data = inverseTransform(N,tmp);
////      System.out.println(" Time for inverse Daub("+N+") wavelet transform: " + 
////         ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");
////      System.out.println("Data transformed: ");
////      for (int i = 0; i < data.length; i++) {
////         if ( Math.abs(data[i]) < ACCURACY ) data[i] = 0; // compress!
////         System.out.print(""+df.format(data[i])+";");
////      }
////      System.out.println("\n");
////      // --- inverse ----- 
//      
//      data = new double[1 << 7];
//      data[3] = 111;
//      System.out.println("Data: ");
//      double max = -Double.MAX_VALUE;
//      for (int i = 0; i < data.length; i++) {
//         System.out.print(""+df.format(data[i])+";");
//         if (max < data[i]) max = data[i];
//      }
//      System.out.println();
//      
//      start = System.currentTimeMillis();
//      tmp = transform(N, data);
//      System.out.println(" Time for Daub("+N+") wavelet transform: " + 
//         ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");
//      System.out.println("Data transformed:");
//
////      System.out.print("c=");
////      for (int i = 0; i < tmp[0].length; i++) {
////         if ( Math.abs(tmp[0][i]) < 0.01 * max ) tmp[0][i] = 0; // compress!
////         System.out.print(""+df.format(tmp[0][i])+";");
////      }
////      System.out.print("\na=");
////      for (int i = 0; i < tmp[1].length; i++) {
////         //if ( Math.abs(tmp[1][i]) < 0.07 * max ) tmp[1][i] = 0; // compress!
////         System.out.print(""+df.format(tmp[1][i])+";");
////      }
////      System.out.println();
//
//      System.out.println("Wavelet coefficients:\n" + showWaveletCoefficients(tmp));
//      
//      start = System.currentTimeMillis();
//      data = inverseTransform(N,tmp);
//      System.out.println(" Time for inverse Daub("+N+") wavelet transform: " + 
//         ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");
//      System.out.println("Data transformed: ");
//      System.out.print("data=");
//      for (int i = 0; i < data.length; i++) {
//         if ( Math.abs(data[i]) < 0.01 * max ) data[i] = 0; // compress!
//         System.out.print(""+df.format(data[i])+";");
//      }
//      System.out.println();
      
//      // --- zeige Hilfsmatrix zur Bestimmung der Initialwerte von phi: ---
//      df = new java.text.DecimalFormat("#,##0.######");
//      int _N = N/2 - 1;
//      String ausgabe = "<html><table summary="" border=\"1\">";
//      for (int i = 1; i <= N - 2; i++) {
//         ausgabe += "<tr>";
//         for (int j = 1; j <= N - 2; j++) {
//            ausgabe += "<td>";
//            if (0 <= 2*i-j && 2*i-j < N) {
//               ausgabe += "h_" + (2*i-j); //df.format(SQRT2 * h[_N][2*i-j]);
//            } else {
//               ausgabe += 0;
//            }
//            ausgabe += "</td>";
//         }
//         ausgabe += "</tr>";
//      }
//      ausgabe += "</table></html>";
//      javax.swing.JOptionPane.showMessageDialog(null, ausgabe);

      System.exit(0);
   }
   // */
}

