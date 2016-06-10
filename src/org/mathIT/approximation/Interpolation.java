/*
 * Interpolation.java - Class com puting interpolation curves
 *
 * Copyright (C) 2004-2012 Andreas de Vries
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
/**
 * This class enables to generate objects from data points (<i>x</i>, <i>y</i>) 
 * such as time series and to compute interpolation polynomials from them.
 * There are multiple data series possible, i.e., <i>y</i> is an array.
 * @author Andreas de Vries
 * @version 1.1
 */
public class Interpolation {

   private double[] xValues;
   private double[][] yValues;
   /** error estimate (esp. for polynomial interpolation).*/
   //private double[] dy;
    
   /** Creates a new instance of Interpolation.
    *  @param xValues the <i>x</i>-values of the data points (<i>x</i>, <i>y</i>)
    *  @param yValues an array of the <i>y</i>-values of the data points (<i>x</i>, <i>y</i>)
    */
   public Interpolation(double[] xValues, double[][] yValues) {
      this.xValues = xValues;
      this.yValues = yValues;
   }
    
   /** Returns the <i>x</i>-values of the data points (<i>x</i>, <i>y</i>).
    * @return the <i>x</i>-values of the data points
    */
   public double[] getXValues() {
      return xValues;
   }
   
   /** Returns the array of <i>y</i>-values of the data points (<i>x</i>, <i>y</i>).
    * @return the <i>y</i>-values of the data points
    */
   public double[][] getYValues() {
      return yValues;
   }
   
   /** 
    * piecewise linear continuous function interpolating between the <i>x-y</i> values.
    * @param x a data point
    * @return the <i>y</i>-values for each curve at <i>x</i>
    */
   public double[] piecewiseLinear( double x ) {
      int i = 1;
      // Vorsicht: geht nur bei Zeitreihen bzw. sortierten x-Werten!!
      while (i < xValues.length - 1 && x > xValues[i] ) {
          i++;
      }
      
      double[] y = new double[yValues.length];
      for ( int j = 0; j < yValues.length; j++ ) {
         // y = a x + b, with a = (y_i - y_{i-1} / (x_i - x_{i-1}), b = y_{i-1] - a x_{i-1}:
         double a = ( yValues[j][i] - yValues[j][i-1] ) / ( xValues[i] - xValues[i-1] ); 
         double b = yValues[j][i-1] - a * xValues[i-1];
         y[j] = a*x + b;
      }
      return y;
   }
   
   /** Returns the value at <i>x</i> of the polynomial interpolating through 
    *  the given data points, as given by Lagrange's classical formula. 
    *  If there are <i>n</i> data points, the polynomial has degree <i>n</i>.
    *  @param x a data point
    *  @return the y-values for each curve
    *  @throws IllegalArgumentException if there exist two identical x-values
    */
   public double[] polynomial(double x) {
      double[] y = new double[ yValues.length ];
      int ns = 0;
      double den, dif, dift, ho, hp, w;
       
      int n = xValues.length;
       
      for ( int k = 0; k < yValues.length; k++ ) {
         double[] c = new double[ xValues.length];
         double[] d = new double[ xValues.length];
         dif = Math.abs( x - xValues[0] );
         //following we find the index ns of the closest table entry ...
         for ( int i = 0; i < n; i++ ) {
            dift = Math.abs( x - xValues[i] );
            if ( dift < dif ) {
               ns = i;
               dif = dift;
            }
            // ... and initialize the tableau of c's and d's:
            c[i] = yValues[k][i];
            d[i] = yValues[k][i];
         }
         // This is the initial approximation of y:
         y[k] = yValues[k][ns--];
         /* For each column of the tableau, we loop over the current c's and 
          * d's and update them.*/
         for ( int m = 1; m < n; m++ ) {
            for ( int i = 0; i < n - m; i++ ) {
               ho = xValues[i] - x;
               hp = xValues[i+m] - x;
               w  = c[i+1] - d[i];
               // The following error can occur if two input x-values are identical
               if ( ( den = ho - hp ) == 0 ) throw new IllegalArgumentException("Two identical x data values");
               den = w / den;
               // The c's and d's are updated:
               d[i] = hp + den;
               c[i] = ho + den;
            }
            /* After each column in the tableau is complemented, we decide which
             * correction, c or d, we add to our accumulating value of y, i.e.,
             * which path to take through the tableau -- forking up or down. We
             * do this in such a way as to take the most "straight line" route
             * through the tableau to its apex, updating ns accordingly to keep
             * track of where we are. This route keeps the partial information
             * centered on the target x. The last dy added is thus the error indication.
             */
            y[k] += ( 2*(ns + 1) < (n - m) ? c[ns + 1] : d[ns--] );
            //y[k] += ( dy[k] = ( 2*(ns + 1) < (n - m) ? c[ns + 1] : d[ns--] ) );
         }
      }
      return y;
   }
}
