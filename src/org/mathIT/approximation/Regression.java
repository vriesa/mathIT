/*
 * Regression.java - Class coomputing regression curves
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
import static org.mathIT.numbers.Numbers.*;
/**
 * This class enables to generate objects from data points (<i>t</i>, <i>y</i>) 
 * such as time series and to compute regression polynomials from them.
 * There are multiple data series possible, i.e., <i>y</i> is an array.
 * @author Andreas de Vries
 * @version 1.1
 */
public class Regression {
   private double[] t;
   private double[][] y;
   /** measure errors.*/
   private double[][] deltaY;
   /** r-vector of parameters.*/
   private double[][] x;
   /** (r x r)-matrix of coefficients.*/
   private double[][][] b;
   /** (n x r)-matrix a_{ij} = f_i(x_j).*/
   private double[][][] a;
   /** r-vector of minimum error function for the fitting of a polynomial of degree 0, 1, ..., r-1.*/
   private double[][] chi2;

   /** constructor for time series with unknown measurement errors.
    *  @param t the <i>t</i>-values of the data points (<i>t</i>, <i>y</i>)
    *  @param y an array of the <i>y</i>-values of the data points (<i>t</i>, <i>y</i>)
    */
   public Regression(double[] t, double[][] y) {
      this.t = t;
      this.y = y;
      this.deltaY = new double[ y.length ][ y[0].length ];
      for ( int iy = 0; iy < y.length; iy++ ) {
         for ( int i = 0; i < deltaY[0].length; i++ ) {
           // assuming simply measurement error sigma = sqrt(y) [Brandt 1999, p. 261]:
           //this.deltaY[iy][i] = Math.sqrt(Math.abs(y[iy][i]));
           //if ( deltaY[iy][i] == 0 ) deltaY[iy][i] = Float.MIN_VALUE; // 0 is forbidden!
           this.deltaY[iy][i] = 1; // see Brandt 1999, p. 418
         }
      }
   }
   /** constructor for time series with known measurement errors.
    *  @param t the <i>t</i>-values of the data points (<i>t</i>, <i>y</i>)
    *  @param y an array of the <i>y</i>-values of the data points (<i>t</i>, <i>y</i>)
    *  @param deltaY the measurement errors
    */
   public Regression(double[] t, double[][] y, double[][] deltaY) {
      this.t = t;
      this.y = y;
      this.deltaY = deltaY;
   }
    
   /** Returns the <i>t</i>-values of the data points (<i>t</i>, <i>y</i>).
    *  @return the <i>t</i>-values of the data points (<i>t</i>, <i>y</i>).
    */
   public double[] getT() {
      return t;
   }
   
   /** Returns the array of <i>y</i>-values of the data points (<i>t</i>, <i>y</i>).
    *  @return the array of <i>y</i>-values of the data points (<i>t</i>, <i>y</i>).
    */
   public double[][] getY() {
      return y;
   }
   
   /**
    *  polynomial regression for given data points. 
    *  <!-- 
    *  It computes the coefficients of a polynomial
    *  
    *       y(t) = x_0 f_0 (t) + x_1 f_1 (t) + ... + x_r f_r (t),
    *  
    *  where each f_j is a polynomial of degree j-1 in t:
    *  
    *       f_j (t) = b_{j0} t^0 + b_{j1} t^1 + ... + b_{j,j-1} t^{j-1}.
    *  
    *  With respect to the control variable values t_i and the measurement
    *  weights g_i = 1 / sigma_i^2, they satisfy the orthonormality conditions
    *  
    *       sum_{i=1}^n g_i f_j (t_i) f_k(t_i)  =  delta_{jk}.
    * 
    *  For the computation of f_j the matrix a_{ij} is used, defined by 
    *  
    *       a_{ij} = f_j (t_i).
    *  
    *  For details, see S. Brandt: Datenanalyse. Spektrum Akademischer Verlag,
    *  Heidelberg Berlin 1999, &sect;12.
    *  -->
    *  <p style="text-align:center">
    *     <i>y</i>(<i>t</i>) = <i>x</i><sub>0</sub> <i>f</i><sub>0</sub>(<i>t</i>) 
    *        + <i>x</i><sub>1</sub> <i>f</i><sub>1</sub>(<i>t</i>) + ...
    *        + <i>x</i><sub><i>r</i></sub> <i>f</i><sub><i>r</i></sub>(<i>t</i>),
    *  </p>
    *  @param rMax maximal number of parameters
    */
   public void computeCoefficients( int rMax ) {
      int n = t.length;
      x = new double[y.length][rMax];
      b = new double[y.length][rMax][rMax];
      a = new double[y.length][n][rMax];
      chi2 = new double[y.length][rMax];
      for ( int iy = 0; iy < y.length; iy++ ) {
         double sg = 0, tBar = 0;
         double[] g = new double[n];
         // compute weights g and weighted mean tBar:
         for ( int i = 0; i < n; i++ ) {
            g[i]  = 1 / ( deltaY[iy][i] * deltaY[iy][i] );
            sg   += g[i];
            tBar += g[i] * t[i];
         }
         tBar /= sg;
         // compute b and a for rMax=1:
         //b[iy] = new double[rMax][rMax];
         //a[iy] = new double[n][rMax];
         b[iy][0][0] = 1 / Math.sqrt(sg);
         for ( int i = 0; i < n; i++ ) {
            a[iy][i][0] = b[iy][0][0];
         }
         // compute b and a for rMax = 2:
         if ( rMax >= 2 ) {
            double s = 0;
            for ( int i = 0; i < n; i++ ) {
               s += g[i] * pow( (t[i] - tBar), 2 );
            }
            b[iy][1][1] = 1 / Math.sqrt(s);
            b[iy][1][0] = - b[iy][1][1] * tBar;
            for ( int i = 0; i < n; i++ ) {
               a[iy][i][1] = b[iy][1][0] + b[iy][1][1] * t[i];
            }
         }
         // compute b and a for rMax > 2:
         for ( int j = 2; j < rMax; j++ ) {
            double alpha = 0, beta = 0, gamma2 = 0;
            for ( int i = 0; i < n; i++ ) {
               alpha += g[i] * t[i] * pow( a[iy][i][j-1], 2 );
               beta  += g[i] * t[i] * a[iy][i][j-1] * a[iy][i][j-2];
            }
            for ( int i = 0; i < n; i++ ) {
               gamma2 += g[i] * pow( (t[i] - alpha) * a[iy][i][j-1] - beta * a[iy][i][j-2], 2 );
            }
            double gamma1 = 1 / Math.sqrt(gamma2);
            b[iy][j][0] = gamma1 * (- alpha * b[iy][j-1][0] - beta * b[iy][j-2][0]);
            for ( int k = 1; k <= j-2; k++ ) {
               b[iy][j][k] = gamma1 * ( b[iy][j-1][k-1] - alpha * b[iy][j-1][k] - beta * b[iy][j-2][k] );
            }
            b[iy][j][j-1] = gamma1 * ( b[iy][j-1][j-2] - alpha * b[iy][j-1][j-1] );
            b[iy][j][j]   = gamma1 * b[iy][j-1][j-1];
            for ( int i = 0; i < n; i++ ) {
               a[iy][i][j] = b[iy][j][0];
               for ( int k = 1; k <= j; k++ ) {
                  a[iy][i][j] += b[iy][j][k] * pow( t[i], k );
               }
            }
         }
         // compute x and chi2:
         for ( int j = 0; j < rMax; j++ ) {
            for ( int i = 0; i < n; i++ ) {
               x[iy][j] += g[i] * a[iy][i][j] * y[iy][i];            
            }
            for ( int i = 0; i < n; i++ ) {
               double s = 0;
               for ( int k = 0; k <= j; k++ ) {
                  s += a[iy][i][k] * x[iy][k];
               }
               chi2[iy][j] += g[i] * pow( (y[iy][i] - s), 2 );
            }
         }
      }
   }
   
   /**
    *  computes the value on the regression curve at t.
    *  It does not return its distance from the confidence limit, so it does not matter
    *  whether the measurement errors are known or not.
    *  @param t value of the control variable
    *  @param nr the degree of the regression polynomial
    *  @return regression value, for each y-data row
    */
   public double[] polynomial( double t, int nr ) {
      return regcon( t, nr, 0, 0 )[0];
   }

   /**
    *  computes the value on the regression curve at t and its distance from the confidence limits,
    *  if the measurement errors are known.
    *  @param t value of the control variable
    *  @param p probability of the confidence limit
    *  @param nr number of parameters <i>r</i> in the polynomial
    *  @return for each y-data row, an array consisting of the regression value and its distance from the confidence limits
    */
   public double[][] polynomial( double t, double p, int nr ) {
      double pprime = (p + 1) * .5;
      double s = 1.;
      double factor = org.mathIT.statistics.Probability.standardNormalQuantile(pprime);
      return regcon( t, nr, factor, s );
   }
   
   /**
    *  computes the value eta(t) on the regression curve at t and its distance from the confidence limits,
    *  if the measurement errors are unknown.
    *  The distance of eta(t) from the confidence limits depends on chi2, 
    *  the value of the least square fit for adjusting a polynomial of degree r-1
    *  under the assumption that all measurement errors sigma_i = 1. It must be positive, chi2 %gt; 0.
    *  @param t value of the control variable
    *  @param chi2 value of the least square fit for adjusting a polynomial of degree r-1
    *  @param p probability of the confidence limit
    *  @param nr number of parameters <i>r</i> in the polynomial
    *  @param nf number <i>f = N - r</i> of degrees of freedom
    *  @return for each y-data row, an array consisting of the regression value and its distance from the confidence limits
    */
   public double[][] polynomial(double t, double chi2, double p, int nr, int nf ) {
      double pprime = (p + 1) * .5;
//      if (chi2 > 0.) {
      double s = Math.sqrt(chi2 / nf);
      double factor = org.mathIT.statistics.Probability.studentQuantile(pprime, nf);
//      }
      return regcon( t, nr, factor, s );
   }
   
   private double[][] regcon(double t, int nr, double factor, double s ) {
      double[] eta = new double[ y.length ];
      double[] coneta = new double[ y.length ]; 
      double d;

      for ( int iy = 0; iy < y.length; iy++ ) {
         eta[iy] = 0;
         coneta[iy] = 0;
         for ( int j = 0; j < nr; ++j ) {
            d = b[iy][j][0];
            if (j > 0) {
               for ( int k = 1; k <= j; ++k ) {
//                  d += b[iy][j, k] * pow(t, k-1);
                  d += b[iy][j][k] * pow(t, k);
               }
            }
            eta[iy] += x[iy][j] * d;
            coneta[iy] += d * d;
         }
         coneta[iy] = factor * s * Math.sqrt(coneta[iy]);
      }
      double[][] result = {eta, coneta};
      return result;
   }
   
   /** For test purposes...*/
   /*
   public static void main( String[] args ) {
      java.text.DecimalFormat twoDigits = new java.text.DecimalFormat("#,##0.00");
      double[] t = {-.9, -.7, -.5, -.3, -.1, .1, .3, .5, .7, .9};
      double[][] y = {{81, 50, 35, 27, 26, 60, 106, 189, 318, 520}};
      double[][] deltaY = new double[y.length][y[0].length];
      for ( int iy = 0; iy < y.length; iy++ ) {
         for ( int i = 0; i < y[0].length; i++ ) {
            deltaY[iy][i] = Math.sqrt( y[iy][i] );
         }
      }
      int rMax = 10;
      
      //Regression regression = new Regression(t,y,deltaY);
      Regression regression = new Regression(t,y);
      
      String output = "<html><table><tr><td valign=\"top\">";
      for ( int iy = 0; iy < y.length; iy++ ) {
         output += "<table border=\"1\" align=\"center\">";
         output += "<tr><th><i>j</i></th>";
         output += "<th><i>t<sub>j</sub></i></th>";
         output += "<th><i>y<sub>j</sub></i></th></tr>";
         for ( int j = 0; j < t.length; j++ ) {
            output += "<tr><td style=\"text-align:right\">";
            output += (j+1) + "</td><td style=\"text-align:right\">" + t[j];
            output += "</td><td style=\"text-align:right\">" + y[iy][j] + "</td></tr>";
         }
         output += "</table></td><td valign=\"top\"><table border=\"1\" align=\"center\">";
         output += "<tr><th><i>r</i></th><th><i>x<sub>r</sub></th><th>chi<sup>2</sup></th>";
         output += "</tr>";
      }
      
      regression.computeCoefficients( rMax );
      for ( int iy = 0; iy < y.length; iy++ ) {
         for ( int r = 0; r < rMax; r++ ) {
            output += "<tr><td style=\"text-align:right\">" + (r+1) + "</td>";
            output += "<td style=\"text-align:right\">" + twoDigits.format(regression.x[iy][r]) + "</td>";
            output += "<td style=\"text-align:right\">" + twoDigits.format(regression.chi2[iy][r]) + "</td>";
            output += "</tr>";
         }
      }
      output += "</table></td></tr></table></html>";
      javax.swing.JOptionPane.showMessageDialog( null, output, "Regression", javax.swing.JOptionPane.PLAIN_MESSAGE );
      System.exit(0);
   }
   // */
}
