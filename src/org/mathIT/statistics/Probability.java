/*
 * Probability.java
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
package org.mathIT.statistics;
import java.lang.reflect.*;
import static org.mathIT.numbers.Numbers.*;
/** 
 * This class provides probabilistic functions such as probability distributions
 * or quantiles. They are implemented as static methods.
 * @author  Andreas de Vries
 * @version 1.2
 */
public class Probability {
    // Suppresses default constructor, ensuring non-instantiability.
    private Probability() {
    }

   //--- Probability distributions: --------------------------------------------
   /** Returns the normal density with the specified parameters.
    *  @param x the current value
    *  @param x0 the average value
    *  @param sigma the standard deviation
    *  @return the standard normal density of (x - x0) / sigma
    */
   public static double normalDensity(double x, double x0, double sigma) {
      return standardNormalDensity( (x - x0) / sigma );
   }
   
   /** Returns normal distribution.
    *  @param x the current value
    *  @param x0 the average value
    *  @param sigma the standard deviation
    *  @return the standard normal density of (x - x0) / sigma
    */
   public static double normalDistribution(double x, double x0, double sigma) {
      return standardNormalDistribution( ( x - x0) / sigma );
   }

   /** Computes the quantile of the normal distribution.
    *  @param p probability
    *  @param x0 width
    *  @param sigma standard deviation
    *  @return quantile <i>x<sub>p</sub></i>
    */
   public static double normalQuantile (double p, double x0, double sigma) {
      return standardNormalQuantile(p) * sigma + x0;
   }
   
   /** Standard normal density function.
    *  @param x value of random variable
    *  @return standard normal density value
    */
   public static double standardNormalDensity(double x) {
      double arg = x * x;
      if ( Math.abs(arg) >= 500. ) {
         return 0.;
      } else {
         return Math.exp(arg) / Math.sqrt(2*Math.PI); //* .39894228;
      }
   }

   /** standard normal distribution.
    * @param x value of random variable
    * @return standard normal distribution value
    */
   public static double standardNormalDistribution(double x) {
      final double c_dp5 = .5;
      double f, s, arg;

      /* Computing 2nd power */
      arg = x * x * .5;
      s = 1.;
      if (x < 0.) {
         s = -1.;
      }      
      f = incGamma(c_dp5, arg);
      return (s * f + 1.) / 2;
   }
   
   /** Standard normal quantile. See 
    * <a href="http://mathworld.wolfram.com/QuantileFunction.html">http://mathworld.wolfram.com/QuantileFunction.html</a>
    * @param p the probability of the quantile
    * @return the quantile of p
    */
   @SuppressWarnings("rawtypes")
	public static double standardNormalQuantile(double p) {
      int c__0 = 0;
      double epsilon = 1e-8; //double c_d1em8 = 1e-8;

      double xzero; 
      double[] x = new double[2]; //x0, x1;

      /* boundary of range */
      if ( p >= 1. ) {
         return Double.POSITIVE_INFINITY;
      } else if ( p <= 0.) {
         return Double.NEGATIVE_INFINITY;
      } else {
         x[0] = 0.;
         x[1] = .1;
         xzero = 0;
         // These are the argument types of the function sztnr:
         Class[] argTypes = { Double.TYPE, Double.TYPE };
         try {
            /* auxzbr determines two x-values enclosing a zero:*/
            x = auxzbr(x[0], x[1], "szstnr", argTypes, p, c__0, c__0);
            //auxzbr(&x0, &x1, szstnr, p, &c__0, &c__0);
            /* auxzfn determines a zero enclosed by x[0] and x[1]:*/
            xzero = auxzfn(x[0], x[1], "szstnr", argTypes, p, c__0, c__0, epsilon);
            //auxzfn(&x0, &x1, &xzero, szstnr, p, &c__0, &c__0, &c_d1em8);
         } catch ( Exception e ) {
            e.printStackTrace();
         }
         return xzero;
      }
   }
   
   //--- Student t-distribution:
   /** Student t-density function for <i>n</i> degrees of freedom. It is defined by
    *  <pre>
    * 
    *                 ( 1 + t^2/n )^{-(n+1)/2}
    *         f(x) = --------------------------.
    *                   B(.5, .5*n) sqrt{n}
    *
    *  </pre>
    *  where <i>B</i> denotes the {@link org.mathIT.numbers.Numbers#beta(double,double) beta function}.
    *  @param x independent variable
    *  @param n degrees of freedom
    *  @return value of the Student density
    *  @see org.mathIT.numbers.Numbers#beta(double,double)
    */
   public static double studentDensity(double x, int n) {
      //final double c_dp5 = .5;
      //double ret_val, d__1;
      double beta, a; //, sqan, an2, arg;

      //an2 = n * .5;
      //sqan = Math.sqrt(n);
      //arg = -an2 - .5;
      //d__1 = x*x / n + 1.;
      a = Math.pow(x*x / n + 1., -(n+1)/2. );
      //a = Math.pow(x*x / n + 1., arg);
      beta = beta(.5, n/2.);
      return a / ( beta * Math.sqrt(n) );
   }
   
   /** Student t-distribution function of <i>n</i> degrees of freedom.
    *  @param x a real number
    *  @param n degrees of freedom of the Student t-distribution
    *  @return the value of the Student t-distribution function with <i>n</i> degrees of freedom at <i>x</i>
    *  @see #studentDensity(double,int)
    */
   public static double studentDistribution(double x, int n) {
      double a, an2, arg;

      an2 = n * .5;
      arg = n / (n + x * x);
      a = incBeta(an2, .5, arg);
      if (x >= 0.) {
         return 1. - a * .5;
      } else {
         return a * .5;
      }
   }
   
   /** Computes the quantile of the Student t-distribution.
    *  @param p the probability
    *  @param n degrees of freedom of the Student t-distribution
    *  @return the quantile referring to <i>p</i>
    *  @see #studentDensity(double,int)
    */
   public static double studentQuantile(double p, int n) {
      int c__0 = 0;
      double epsilon = 1e-6;
      
      double xzero = 0;
      double[] x = new double[2]; //x0, x1;

      /* boundary of range */
      if (p >= 1.) {
         return 1e10;
      }
      if (p <= 0.) {
         return -1e10;
      }
      /* normal range */
      if (p < 1. && p > 0.) {
         x[0] = 0.;
         x[1] = p;
         xzero = 0;
         // These are the argument types of the function sztud:
         @SuppressWarnings("rawtypes")
			Class[] argTypes = { Double.TYPE, Double.TYPE, Integer.TYPE };
         try {
            /* auxzbr determines two x-values enclosing a zero:*/
            x = auxzbr(x[0], x[1], "szstud", argTypes, p, n, c__0);
            //auxzbr(x0, &x1, szstud, p, n, &c__0);
            /* auxzfn determines a zero enclosed by x[0] and x[1]:*/
            xzero = auxzfn(x[0], x[1], "szstud", argTypes, p, n, c__0, epsilon);
            //auxzfn(&x0, &x1, &xzero, szstud, p, n, &c__0, &c_d1em6);
         } catch ( Exception e ) {
            e.printStackTrace();
         }
      }
      return xzero;
   }

   /** Returns <i>p</i> minus the cumulative standardized normal of <i>x</i>. 
    *  This is an auxiliary function which is invoked via callback ("reflection")
    *  and must therefore be declared "public".
    *  @param x a random variable value
    *  @param p a probability
    *  @return <i>p</i> minus the cumulative standardized normal of <i>x</i>
    */
   public static double szstnr(double x, double p) {
      return p - standardNormalDistribution(x);
   }

   /** returns <i>p</i> minus the cumulative Student distribution of (<i>x, n</i>).
    *  This is an auxiliary function which is invoked via callback ("reflection")
    *  and must therefore be declared "public".
    *  @param x a random variable value
    *  @param p a probability
    *  @param n degrees of freedom
    *  @return <i>p</i> minus the cumulative student distriibution of (<i>x, n</i>)
    */
   public static double szstud(double x, double p, int n) {
      return p - studentDistribution(x, n);
   }

   /** yields two numbers which enclose a zero of the inputted function.
    *  The function has to be either implemented in the actual class, 
    *  or referred to with its full path name (package + class).
    *  It must be declared public!
    *  @param x0 first x-value in whose neighborhoood the zero is supposed
    *  @param x1 second x-value in whose neighborhoood the zero is supposed
    *  @param function name of function to be considered (must be declared public!)
    *  @param argTypes types of the arguments
    *  @param par parameter of the function
    *  @param npar1 parameter of the function
    *  @param npar2 parameter of the function
    *  @return an array x[] consisting of two enclosing x-values {x0, x1}
    *  @throws ClassNotFoundException if the arguments are not correct
    *  @throws NoSuchMethodException if the arguments are not correct
    *  @throws IllegalAccessException if the arguments are not correct
    *  @throws InvocationTargetException if the arguments are not correct
    */
   @SuppressWarnings("rawtypes")
	public static double[] auxzbr(double x0, double x1,
             String function, Class[] argTypes,
             double par, int npar1, int npar2
   ) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      int i = 0, iMax = 10;
      double f0, f1, xs;

      if (x0 == x1) {
         x1 = x0 + 1.;
      }
      
      //Object[] args = { Double.valueOf(x0), Double.valueOf(par), Integer.valueOf(npar1), Integer.valueOf(npar2) };
      Object[] argsMax = { Double.valueOf(x0), Double.valueOf(par), Integer.valueOf(npar1), Integer.valueOf(npar2) };
      Object[] args = new Object[ argTypes.length ];
      for (int j = 0; j < args.length; j++) {
         args[j] = argsMax[j];
      }
      Method func = Class.forName("org.mathIT.statistics.Probability").getMethod( function, argTypes );
      f0 = ( (Double) func.invoke( null, args ) ).doubleValue();
      //f0=(*funct)(x0, par, npar1, npar2);
      args[0] = Double.valueOf(x1); 
      f1 = ( (Double) func.invoke( null, args ) ).doubleValue();
      //f1=(*funct)(x1, par, npar1, npar2);
      //while( (f0 * f1 > 0.) && i < iMax ) {
      while( signProd(f0, f1) > 0 && i < iMax ) {
         if ( Math.abs(f0) <= Math.abs(f1)) {
            xs = x0;
            x0 += (x0 - x1) * 2.;
            x1 = xs;
            f1 = f0;
            args[0] = Double.valueOf(x0);
            f0 = ( (Double) func.invoke( null, args ) ).doubleValue();
            //f0 = (*funct)(x0, par, npar1, npar2);
         } else {
            xs = x1;
            x1 += (x1 - x0) * 2.;
            x0 = xs;
            f0 = f1;
            args[0] = Double.valueOf(x1); 
            f1 = ( (Double) func.invoke( null, args ) ).doubleValue();
            //f1 = (*funct)(x1, par, npar1, npar2);
         }
        i++;
      }
      double[] result = {x0,x1};
      return result;
   }

   /** Determines numerically a zero of the inputted function, enclosed by x0 and x1.
    *  @param x0 first x-value in whose neighborhoood the zero is supposed
    *  @param x1 second x-value in whose neighborhoood the zero is supposed
    *  @param function name of function to be considered; it has to be either 
    *  implemented in the actual class, or referred to with its full path name
    *  @param argTypes types of the arguments
    *  @param par parameter of the function
    *  @param npar1 parameter of the function
    *  @param npar2 parameter of the function
    *  @param epsiln accuracy of approximation
    *  @return an array x[] consisting of two enclosing x-values {x0, x1}
    *  @throws ClassNotFoundException if the arguments are not correct
    *  @throws NoSuchMethodException if the arguments are not correct
    *  @throws IllegalAccessException if the arguments are not correct
    *  @throws InvocationTargetException if the arguments are not correct
    */
   @SuppressWarnings("rawtypes")
	public static double auxzfn(double x0, double x1,
             String function, Class[] argTypes,
             double par, int npar1, int npar2, double epsiln)
   throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      double d__1;

      int i, iMax = 2000;
      double f0, f1, fm, xm;
      //Object[] args = { Double.valueOf(x0), Double.valueOf(par), Integer.valueOf(npar1), Integer.valueOf(npar2) };
      Object[] argsMax = { Double.valueOf(x0), Double.valueOf(par), Integer.valueOf(npar1), Integer.valueOf(npar2) };
      Object[] args = new Object[ argTypes.length ];
      for ( int j = 0; j < args.length; j++ ) {
         args[j] = argsMax[j];
      }
      Method func = Class.forName("org.mathIT.statistics.Probability").getMethod( function, argTypes );

      for (i = 1; i <= iMax; ++i) {
         f0 = ( (Double) func.invoke( null, args ) ).doubleValue();
         //f0 = (*funct)(x0, par, npar1, npar2);
         args[0] = Double.valueOf(x1); 
         f1 = ( (Double) func.invoke( null, args ) ).doubleValue();
         //f1 = (*funct)(x1, par, npar1, npar2);
         if (f0 == 0.) {
            return x0;
         } else if (f1 == 0.) {
            return x1;
         }
         xm = (x0 + x1) * .5;
         if ((d__1 = x0 - x1) > epsiln && Math.abs(d__1) >= epsiln) {
            args[0] = Double.valueOf(xm); 
            fm = ( (Double) func.invoke( null, args ) ).doubleValue();
            //fm = (*funct)(&xm, par, npar1, npar2);
            if (f0 * fm < 0.) {
               x1 = xm;
            } else {
               x0 = xm;
            }
         } else {
            return xm;
         }
      }
      return x0;
   }
   
   /** returns sign(<i>xy</i>). I.e., 
    *  0 if <i>x</i> = <i>y</i> = 0,
    *  1 if sign(<i>x</i>) == sign(<i>y</i>), and 
    *  -1 if sign(<i>x</i>) != sign(<i>y</i>).
    *  @param x a real number
    *  @param y a real number
    *  @return sign(<i>xy</i>)
    */ 
   public static byte signProd( double x, double y ) {
      byte result = 1;
      if ( x == 0 || y == 0) result = 0;
      else if ( (x < 0 && y > 0) || (x > 0 && y < 0))  result = -1;
      return result;
   }
   
   /** For test purposes...*/
   /*
   public static void main( String[] args ) {
      try {
         String function = "szstud";
         Class[] argTypes = { Double.TYPE, Double.TYPE, Integer.TYPE };
         double x = .05;
         double[] y = auxzbr( 0, x, function, argTypes, x, 2, 0 );
         double xzero = auxzfn( y[0], y[1], function, argTypes, x, 0, 0, 1e-6 );
         System.out.println( "x0="+ y[0] + ", " + y[1] + ", xzero="+xzero );
      } catch ( NoSuchMethodException nsme ) {
         nsme.printStackTrace();
      } catch ( Exception e ) {
         e.printStackTrace();
      }
   }
   // */
}

