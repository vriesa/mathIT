/*
 * QuantumToolBox.java
 *
 * Copyright (C) 2006-2012 Andreas de Vries
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
package org.mathIT.quantum;
import static org.mathIT.numbers.Numbers.*;
/**
 * This class provides utilities for quantum computation and quantum algorithms.
 * @author  Andreas de Vries
 * @version 1.1
 */
public class QuantumToolBox {
   // Suppresses default constructor, ensuring non-instantiability.
   private QuantumToolBox() {
   }
   
   /** Given the number <i>z</i> &#x2248; <i>s</i>/<i>r</i> as information,
    *  where <i>r</i> is the order modulo <i>n</i> of the number <i>a</i>,
    *  and 0 &lt;= <i>s</i> &lt; <i>r</i>
    *  (but not knowing neither <i>s</i> nor <i>r</i>), 
    *  this method returns a multiple of <i>r</i> if such a number is found,
    *  or -1 otherwise.
    *  The method is used by Shor's quantum algorithm for integer factorization
    *  where <i>n</i> denotes the number to be factorized,
    *  <i>z</i> is the measurement result of the quantum register, and
    *  <i>a</i> is a random number satisfying 1 &lt; <i>a</i> &lt; <i>n</i>.
    *  The method utilizes continued fractions to approximate <i>z</i>.
    *  By the quantum mechanical laws and by the construction of Shor's algorithm,
    *  it is guaranteed in principle that <i>z</i> is a rational number and hence 
    *  the method terminates. However, by the finite precision of the data type 
    *  of <i>z</i>, a limit of maximum continued fraction coefficients is required
    *  to let the method terminate certainly.
    *  Some small number triples for which these requirements are satisfied, are the following:
    *  <ul>
    *    <li> <i>z</i> = 2867./4096, <i>a</i> = 5, <i>n</i> = 33 (<i>r</i> = 10);</li>
    *    <li> <i>z</i> = 1536./2048, <i>a</i> = 4, <i>n</i> = 15 (<i>r</i> = 4);</li>
    *    <li> <i>z</i> = 13453./16384, <i>a</i> = 3, <i>n</i> = 91 (<i>r</i> = 6);</li>
    *  </ul>
    *  For details see M.A. Nielsen, I.L. Chuang:
    *  <i>Quantum Computation and Quantum Information.</i>
    *  Cambridge University Press, Cambridge 2000, &sect;5.3.1
    *  @param z a number approximating the unknown rational number <i>s/r</i>
    *  @param a a previously randomly chosen test number
    *  @param n the modulus
    *  @param limit the maximum number of continued fraction coefficients to be computed
    *  @return a multiple of the order <i>r</i> of <i>a</i> mod <i>n</i>
    *  @see org.mathIT.numbers.Numbers#continuedFraction(double,int)
    *  @see org.mathIT.numbers.Numbers#ord(long,long)
    */
   public static long order( double z, long a, long n, int limit ) {
      long[] cf = continuedFraction(z,limit);
      // q_{k-2} = q1, q_{k-1} = q2, q_{k} = q3:
      long q1 = 1, q2 = 0, q3; // <- k=0
      for ( long x : cf ) {
         // q_{k} = a_{k} q_{k-1} + q_{k-2}
         q3 = x * q2 + q1;
         q1 = q2;
         q2 = q3;
         if ( modPow( a, q3, n ) == 1 ) {
            return q3;
         }
      }
      return -1;
   }

   /**
    *  For test puroses...
    *  @param args the command line arguments
    */
   /*
   public static void main(String args[]) {
      java.math.BigDecimal xBD = org.mathIT.numbers.BigNumbers.ZETA_3;
      double x = xBD.doubleValue();
      //double ZETA_2 = 1.64493406684822643647; double x = ZETA_2/(PI*PI);
      //double ZETA_4 = 1.08232323371113819152; double x = ZETA_4/pow(PI, 4);
      //double ZETA_6 = 1.01734306198444913971; double x = ZETA_6/pow(PI, 6);
      //double ZETA_8 = 1.00407735619794433938; double x = ZETA_8/pow(PI, 8);
      //double ZETA_10 = 1.00099457512781808534; double x = ZETA_10/pow(PI, 10);
      //double ZETA_12 = 1.00024608655330804830; double x = ZETA_12/pow(PI, 12);
      //double ZETA_26 = 1.00000001490155482837; double x = ZETA_26/pow(PI,26);
      //double x = ZETA_3 / pow(PI, 3); int a = 3, n = 91;
      //double x = E; int a = 3, n = 91;
      if ( args != null && args.length > 1 ) {
         x = Double.parseDouble( args[0] ) / Double.parseDouble( args[1] );
      } else if ( args != null && args.length > 0 ) {
         x = Double.parseDouble( args[0] );
      }
      long[] cf = continuedFraction(x,24);
      String ausgabe = " a = [";
      for( int i = 0; i < cf.length - 1; i++ ){
         ausgabe += cf[i] + ",";
      }
      ausgabe += cf[cf.length-1] + "]";
      java.math.BigInteger[] cfBig = org.mathIT.numbers.BigNumbers.continuedFraction(xBD,27);
      ausgabe += "\n a = [";
      for( int i = 0; i < cfBig.length - 1; i++ ){
         ausgabe += cfBig[i] + ",";
      }
      ausgabe += cfBig[cfBig.length-1] + "]";
      //System.out.println(ausgabe);

      //x = 2867./4096; int a = 5, n = 33;
      //x = 1536./2048; int a = 4, n = 15;
      x = 13453./16384; int a = 3, n = 91;
      //System.out.println(" a = " + continuedFraction(x));
      System.out.println("order("+x+","+a+","+n+") = " + order(x, a, n, 24));

      System.out.println("Best rational approximations of x = " + x + ":");
      cf = continuedFraction(x,24);
      for(int i=1; i <= cf.length && i <= 25; i++) {
         long[] bra = bestRationalApproximation(x,i);
         System.out.println(" i="+i+": "+ bra[0] + "/" + bra[1]);
      }
   }   
   // */
}
