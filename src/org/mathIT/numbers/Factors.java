/*
 * Factors.java - Class to yield the prime factorization of a big integer
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
package org.mathIT.numbers;
import java.math.BigInteger;
import static java.math.BigInteger.ONE;
import java.util.Set;
import java.util.TreeMap;
import static org.mathIT.numbers.BigNumbers.*;
/**
 *  An object of this class contains a list of all prime factors and their exponents.
 *  By the Fundamental Theorem of Arithmetic, any positive integer <i>n</i> has a unique
 *  decomposition into prime factors, i.e.,
 *  <p style="text-align:center">
 *     <i>n</i> 
 *     = <i>p</i><sub>1</sub><sup><i>e</i><sub>1</sub></sup>
 *     + ...
 *     + <i>p<sub>k</sub><sup>e<sub>k</sub></sup></i>
 *  </p>
 *  where 
 *  <i>p</i><sub>1</sub> &lt; ... &lt; <i>p<sub>k</sub></i>
 *  are prime numbers and the exponents
 *  <i>e</i><sub>1</sub>, ..., <i>e<sub>k</sub></i>
 *  are positive integers.
 *  Technically, the prime factors are given as a 
 *  {@link TreeMap}&lt;{@link BigInteger}, {@link Integer}&gt;
 *  where the keys are the different unique primes and the values the
 *  respective exponents, i.e.,
 *  <p style="text-align:center">
 *     [&lt;<i>p</i><sub>1</sub>, <i>e</i><sub>1</sub>&gt;, ...,
 *      &lt;<i>p<sub>k</sub></i>, <i>e<sub>k</sub></i>&gt;]
 *  </p>
 *  @author  Andreas de Vries
 *  @version 1.1
 */

public class Factors extends TreeMap<BigInteger,Integer>{
   private static final long serialVersionUID = 572770532L;
   
   /** Computes prime factors of <i>n</i> and stores them into this map.
    *  @param n the number which is to be factorized
    */
   public Factors(BigInteger n) {
      super();
      factorize(n);
   }

   /** Computes prime factors of <i>n</i> and stores them into this map.
    *  @param n the number which is to be factorized
    */
   public Factors(long n) {
      super();
      factorize( BigInteger.valueOf(n) );
   }

   /** determines a list of the prime factors of n.*/
   private void factorize(BigInteger n) {
      BigInteger prime;
      int exponent;
      BigInteger d = TWO;
      BigInteger sqrtN = BigNumbers.sqrt(n).toBigInteger().add(ONE);
      final BigInteger THREE = BigInteger.valueOf(3);
      final BigInteger FOUR = BigInteger.valueOf(4);
      boolean plus2Step = true; // flag to control the wheel
      
      while (d.compareTo(sqrtN) <= 0) {
         if (n.mod(d).signum() == 0) {
            prime = d;
            exponent = 1;
            n = n.divide( d );
            while ( n.mod(d).signum() == 0 && n.compareTo(ONE) > 0 ) {
               exponent++;
               n = n.divide(d);
            }
            this.put(prime,exponent);
         }
         // for d >= 5 increment according to the wheel 2-4-2-4-2-...
         if (d.equals(TWO)) {
            d = d.add(ONE);
         } else if (d.equals(THREE)) {
            d = d.add(TWO);
         } else {
            d = plus2Step ? d.add(TWO) : d.add(FOUR);
            plus2Step = !plus2Step;
         }
      }
      if ( n.compareTo(ONE) > 0 ) {
         this.put(n, 1);
      }
   }
   
   /** The Carmichael function <i>&#x03BB;</i>(<i>n</i>).
    *  For a definition and an introduction to this function see
    *  <a href="http://www.math-it.org/Mathematik/Zahlentheorie/Carmichael.html" target="_top">
    *  http://www.math-it.org/Mathematik/Zahlentheorie/Carmichael.html</a>
    *  @return <i>&#x03BB;</i>(<i>n</i>) where <i>n</i> is the number represented by this factors
    */
   public BigInteger lambda() {
      BigInteger l = ONE;
      Set<BigInteger> bases = this.keySet();
      for ( BigInteger base : bases ) {
         if ( base.equals(TWO) && this.get(base) >= 3 ) {
            l = lcm ( l, base.pow( this.get(base) - 2 ) );
         } else {
            l = lcm ( l, (base.pow( this.get(base) - 1 )).multiply(base.subtract(ONE)));
         }
      }
      return l;
   }
  
   /** The Euler function <i>&#x03C6;</i>(<i>n</i>), sometimes also called <i>totient function</i>.
    *  For a definition and an introduction to this function see
    *  <a href="http://www.math-it.org/Mathematik/Zahlentheorie/Euler.html" target="_top">
    *  http://www.math-it.org/Mathematik/Zahlentheorie/Euler.html</a>
    *  @return <i>&#x03C6;</i>(<i>n</i>) where <i>n</i> is the number represented by this factors
    */
   public BigInteger phi() {
      BigInteger f = ONE;
      Set<BigInteger> factors = this.keySet();
      for ( BigInteger prime : factors ) {
         f = f.multiply( prime.pow( this.get(prime) - 1 ) ); 
         f = f.multiply( prime.subtract(ONE) );
      }
      return f;
   }

   /** Returns the integer value determined by these prime factors.
    *  @return the integer value determined by these factors
    */
   public BigInteger value() {
      Set<BigInteger> primeList = keySet();
      BigInteger n = ONE;
      for ( BigInteger p : primeList ) {
         n = n.multiply(p.pow(this.get(p)));
      }
      return n;
   }
   
   /** Returns a HTML string representation of this factor list.
    *  @return a HTML string representation of this factor list
    */
   public String toHTMLString() {
      String out = "";
      Set<BigInteger> primeList = keySet();
      BigInteger p;
      for (java.util.Iterator<BigInteger> i = primeList.iterator(); i.hasNext();) {
         p = i.next(); 
         out += p;
         if (get(p) > 1) out += "<sup>" + get(p) + "</sup>";
         if (i.hasNext()) out += " &middot; ";
      }
      return out;
   }
   
   /** Returns a string representation of this factor list.
    *  @return a string representation of this factor list
    */
   @Override
   public String toString() {
      String out = "";
      Set<BigInteger> primeList = keySet();
      BigInteger p;
      for (java.util.Iterator<BigInteger> i = primeList.iterator(); i.hasNext();) {
         p = i.next(); 
         out += p;
         if (get(p) > 1) out += "^" + get(p);
         if (i.hasNext()) out += " * ";
      }
      return out;
   }
   
   /*
   public static void main(String[] args) {
      BigInteger n;
      //n = new BigInteger("101");
      //n = new BigInteger("123456789"); // = 3 x 3607 x 3803
      //n = new BigInteger("1234567891");  // prim
      //n = new BigInteger("10510100501"); // = 101^5
      //n = new BigInteger("199691909519"); // = 19 * 101^5
      n = new BigInteger("20568266680457"); // = 19 * 101^5 * 103
      //n = new BigInteger("987654321987"); // = 3 x 329281 x 999809
      //n = new BigInteger("2310868403"); // = 47287 * 48896, 16 ms
      //n = new BigInteger("1524157877488187891"); // 9091 * 167655689966801, etwa 4 sec
      //n = new BigInteger("199774421497046087277749"); // etwa 2 Tage ...
      //n = new BigInteger("199774421497046087279453"); // etwa 2 Tage ...
      //n = new BigInteger("199774421497046087277751"); // = 11 x 43 x 67 x 4283 x 2014147 x 730743061
      //n = new BigInteger("199774421497046087277757"); // = 79 x 233 x 843443 x 12867706045657
      //n = new BigInteger("199774421497046087277761"); // = 7 x 28539203071006583896823
      //n = new BigInteger("168003672409"); // = 3037 x 6073 x 9109, Carmichael number
      //n = new BigInteger("2152302898747"); // = 6763 * 10627 * 29947, strong pseudoprime, 9 ms
      //n = new BigInteger("341550071728321"); // = 10670053 * 32010157, strong pseudoprime, 3,1 s
      Factors factors = new Factors(n);
      System.out.println("### Probe: " + n + " = " + factors.value());
      System.out.println(factors);
      //javax.swing.JOptionPane.showMessageDialog(null, "<html>" + n + " = " + factors.toHTMLString());
      System.out.println("phi = "+factors.phi());
      System.out.println("lambda = "+factors.lambda());
      //n = new BigInteger("199774421497046087277749"); // etwa 2 Tage ...
      //n = new BigInteger("2310868403"); // = 47287 * 48896, 16 ms
      BigInteger[] liste2 = {
          new BigInteger("341"),   new BigInteger("561"),   new BigInteger("645"),
          new BigInteger("1105"),  new BigInteger("1387"),  new BigInteger("1729"),
          new BigInteger("1905"),  new BigInteger("2047"),  new BigInteger("2465"),
          new BigInteger("2701"),  new BigInteger("2821"),  new BigInteger("3277"),
          new BigInteger("4033"),  new BigInteger("4369"),  new BigInteger("4371"),
          new BigInteger("4681"),  new BigInteger("5461"),  new BigInteger("6601"),
          new BigInteger("7957"),  new BigInteger("8321"),  new BigInteger("8481"),
          new BigInteger("8911"), new BigInteger("10261"), new BigInteger("10585"),
         new BigInteger("11305"), new BigInteger("12801"), new BigInteger("13741"),
         new BigInteger("13747"), new BigInteger("13981"), new BigInteger("14491"),
         new BigInteger("15709"), new BigInteger("15841")
      };
      
      BigInteger[] liste5 = {
         new BigInteger("219781"), new BigInteger("252601"),
         new BigInteger("399001"), new BigInteger("512461"),
         new BigInteger("722261"), new BigInteger("741751"),
         new BigInteger("852841"), new BigInteger("1024651")
      };
      
      BigInteger[] liste7 = {
         new BigInteger("3057601"), new BigInteger("3581761"),
         new BigInteger("5444489"), new BigInteger("5968873"),
         new BigInteger("6868261")
      };
      
      for (int i = 0; i < liste7.length; i++) {
         factors = new Factors(liste7[i]);
         System.out.println(liste7[i] +" = " + factors);
      }
      
      long time = System.currentTimeMillis();
      factors.factorize(n);
      time = System.currentTimeMillis() - time;
      System.out.println("### Running time: "+ time + " ms");
   }
   // */
}
