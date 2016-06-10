/*
 * PlotPane.java - Canvas panel for plotting function graphs
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
package org.mathIT.plot;
import java.awt.*;
import java.awt.print.*;
import javax.swing.*;
import org.mathIT.util.FunctionParser;
/**
 * An object of this class is a canvas for plotting graphs of a function depending 
 * on one variable.
 * The variables used in this class are related to a given function <i>f</i> by:
 * <p style="text-align:center">
 *   <i>y</i> = <i>f</i> (<i>x</i>)
 * </p>
 * @author  Andreas de Vries
 * @version 1.1
 */
public class PlotPane extends JPanel implements Printable {
   private static final long serialVersionUID = -1955500023; // = "PlotPane".hashCode()
   private int numberOfFunctions;
   private int width;
   private int height;
   /** x-value of the origin.*/
   int ox;
   /** y-value of the origin.*/
   int oy;
   /** lower limit of x.*/
   double xmin;
   /** upper limit of x.*/
   double xmax;
   /** lower limit of y.*/
   double ymin;
   /** upper limit of y.*/
   double ymax;
   /** indicates whether the y-limits are calculated automatically.*/
   boolean yAuto;
   /** indicates whether the x-y ratio are the same.*/
   boolean yRatio;
   /** Indicates whether coordinate ticks are drawn.*/
   private boolean drawTicks;
   /** Indicates whether a coordinate grid is drawn.*/
   private boolean drawGrid;
   /** thickness of the plot pane border (in pixel).*/ 
   int border = 50;
   private boolean initialPlot = true;
   private boolean isPrinting = false;
   //private String[] functions;
   private FunctionParser parser;
   /** step width in pixels the graph is plotted with.*/
   private final int steps  = 3;
   private Color background = Color.white;
   private Color axesColor  = Color.black;
   private Color ticksColor = Color.black;
   private Color gridColor  = Color.lightGray;
   /** array of arrays containing the function pixel values. */
   private int[][] fPixels;
   /** array containing the line colors. */
   private Color[] lineColor;
   /** array containing the line widths. */
   private byte[] linewidth;
   /** scale factor for x-values.*/
   double xScale;
   /** scale factor for y-values.*/
   double yScale;
   private double[] xTicks;
   private int[] xTicksPixel;
   private double[] yTicks;
   private int[] yTicksPixel;
   private java.text.DecimalFormat oneDigit = new java.text.DecimalFormat( "#,##0.#######" );
   /** Holds the coordinates of the user's last mousePressed event.*/
   private int lastX, lastY;
   private boolean mouseDragged = false;
   
   /** Creates an empty plot pane. 
    *  @param width the width of this plot pane
    *  @param height the width of this plot pane
    */
   public PlotPane(int width, int height) {
      setPreferredSize( new Dimension(width, height) );
      this.numberOfFunctions = 0;
      this.width  = width;
      this.height = height;
      this.xmin = 0;
      this.xmax = 0;
      this.ymin = 0;
      this.ymax = 0;
      this.lineColor = null;
      this.linewidth = null;
      this.yAuto  = false;
      this.yRatio = false;
      this.drawTicks = false;
      this.drawGrid  = false;
      parser = null;
      this.initialPlot = false;

      addMouseListener(new java.awt.event.MouseAdapter() {
         @Override
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            myMouseClicked(evt);
         }
         @Override
         public void mousePressed(java.awt.event.MouseEvent evt) {
            myMousePressed(evt);
         }
         @Override
         public void mouseReleased(java.awt.event.MouseEvent evt) {
            myMouseReleased(evt);
         }
      });

      addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
         //public void mouseMoved(java.awt.event.MouseEvent evt) {
         //   myMouseMoved(evt);
         //}
         @Override
         public void mouseDragged(java.awt.event.MouseEvent evt) {
            myMouseDragged(evt);
         }
      });
              
//      addKeyListener(new java.awt.event.KeyAdapter() {
//         public void keyReleased(java.awt.event.KeyEvent evt) {
//            myKeyReleased(evt);
//         }
//      });
   }
   
   /** Creates a new plot pane. 
    *  @param width the width of this plot pane
    *  @param height the width of this plot pane
    *  @param functions an array of all functions to be plotted, 
    *     given by strings parseable by {@link org.mathIT.util.FunctionParser FunctionParser}.
    *  @param lineColor an array of colors for each function 
    *     (i.e., <code>surfaceColor.length = functions.length</code>)
    *  @param linewidth an array of line strengths in pixels for each function 
    *     (i.e., <code>lineStrength.length = functions.length</code>);
    *     usually, the strength is a small positive integer
    *  @param xmin the minimum <i>x</i>-value to be plotted
    *  @param xmax the maximum <i>x</i>-value to be plotted
    *  @param ymin the minimum <i>y</i>-value to be plotted
    *  @param ymax the maximum <i>y</i>-value to be plotted
    *  @param yAuto indicates whether the limits of <i>y</i>-values, given by
    *     <i>y</i> = <i>f</i> (<i>x</i>), are calculated automatically
    *  @param yRatio indicates whether the ratio <i>z/x</i> is one
    *  @param drawTicks indicates whether the axes are marked with ticks
    *  @param drawGrid indicates whether the plot is marked with a grid
    */
   public PlotPane(
     int width, int height,
     String[] functions,
     Color[] lineColor, byte[] linewidth,
     double xmin, double xmax, double ymin, double ymax,
     boolean yAuto, boolean yRatio,
     boolean drawTicks, boolean drawGrid
   ) {
      setPreferredSize( new Dimension(width, height) );
      this.numberOfFunctions = functions.length;
      this.width  = width;
      this.height = height;
      //this.functions = functions;
      this.xmin = xmin;
      this.xmax = xmax;
      this.ymin = ymin;
      this.ymax = ymax;
      this.lineColor = lineColor;
      this.linewidth = linewidth;
      this.yAuto  = yAuto;
      this.yRatio = yRatio;
      this.drawTicks = drawTicks;
      this.drawGrid  = drawGrid;
      parser = new FunctionParser( functions );
      computePixels();
      this.initialPlot = false;

      addMouseListener(new java.awt.event.MouseAdapter() {
         @Override
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            myMouseClicked(evt);
         }
         @Override
         public void mousePressed(java.awt.event.MouseEvent evt) {
            myMousePressed(evt);
         }
         @Override
         public void mouseReleased(java.awt.event.MouseEvent evt) {
            myMouseReleased(evt);
         }
      });

      addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
         //public void mouseMoved(java.awt.event.MouseEvent evt) {
         //   myMouseMoved(evt);
         //}
         @Override
         public void mouseDragged(java.awt.event.MouseEvent evt) {
            myMouseDragged(evt);
         }
      });
              
//      addKeyListener(new java.awt.event.KeyAdapter() {
//         public void keyReleased(java.awt.event.KeyEvent evt) {
//            myKeyReleased(evt);
//         }
//      });
   }
   
   private void myMouseDragged(java.awt.event.MouseEvent evt) {
      int min = 1;  //step width in pixels
      if ( mouseDragged ) {
         ox  += min * (evt.getX() - lastX);
         xmin -= min / xScale;
         xmax -= min / xScale;
         oy  +=  min* (evt.getY() - lastY);
         ymin -= min / yScale;
         ymax -= min / yScale;
         yAuto = false;
         //computePixels();
         //repaint();
         //System.out.println( "###Maus gezogen: (x,y) = (" + (evt.getX() - lastX) + ", " + (evt.getY() - lastY) + ")");
      }
      lastX = evt.getX();
      lastY = evt.getY();
      mouseDragged = true;
   }
   
   private void myMousePressed(java.awt.event.MouseEvent evt) {
      setCursor( new Cursor( Cursor.MOVE_CURSOR ) );
      //setCursor( new Cursor( Cursor.HAND_CURSOR ) );
      //setCursor( new Cursor( Cursor.CROSSHAIR_CURSOR ) );
   }
   
   private void myMouseReleased(java.awt.event.MouseEvent evt) {
      mouseDragged = false;
      computePixels();
      repaint();
   }
   
   /*
   private void myKeyReleased(java.awt.event.KeyEvent evt) {
      int min = 10;  // step width in pixel
      if (evt.getKeyCode() == KeyEvent.VK_UP) {
         oy  += min;
         ymin += min / yScale;
         ymax += min / yScale;
         yAuto = false;
         computePixels();
         repaint();
      } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
         oy  -= min;
         ymin -= min / yScale;
         ymax -= min / yScale;
         yAuto = false;
         computePixels();
         repaint();
      } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
         ox  += min;
         xmin -= min / xScale;
         xmax -= min / xScale;
         computePixels();
         repaint();
      } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
         ox  -= min;
         xmin += min / xScale;
         xmax += min / xScale;
         computePixels();
         repaint();
      } else if (evt.getKeyCode() == KeyEvent.VK_P) {
         startPrinterJob();        
      }
   }
    */

   private void myMouseClicked(java.awt.event.MouseEvent evt) {
      double factor  = .8;
      double xMiddle = ( xmax + xmin ) / 2;
      double xDelta  = ( xmax - xmin ) / 2;
      double yMiddle = ( ymax + ymin ) / 2;
      double yDelta  = ( ymax - ymin ) / 2;
      
      if ( 
        ( evt.getButton() == java.awt.event.MouseEvent.BUTTON1 && 
          evt.isControlDown() && evt.isAltDown() )
        || evt.getButton() == java.awt.event.MouseEvent.BUTTON2
      ) {
         startPrinterJob();
      } else if ( 
        ( evt.getButton() == java.awt.event.MouseEvent.BUTTON1 && evt.isControlDown() )
        || evt.getButton() == java.awt.event.MouseEvent.BUTTON3
      ) {
         xScale *= factor;
         yScale *= factor;
         xmin = xMiddle - xDelta / factor;
         xmax = xMiddle + xDelta / factor;
         ymin = yMiddle - yDelta / factor;
         ymax = yMiddle + yDelta / factor;
         yAuto = false;
         computePixels();
         repaint();
      } else if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
         xScale /= factor;
         yScale /= factor;
         xmin = xMiddle - xDelta * factor;
         xmax = xMiddle + xDelta * factor;
         ymin = yMiddle - yDelta * factor;
         ymax = yMiddle + yDelta * factor;
         yAuto = false;
         computePixels();
         repaint();
      }
   }

   /** Decides whether a coordinate grid is plotted.
    * @param drawGrid flag deciding whether to plot a coordinate grid
    */
   public void setDrawGrid( boolean drawGrid ) {
      this.drawGrid = drawGrid;
   }
   
   /** Decides whether coordinate ticks are plotted.
    * @param drawTicks flag deciding whether to plot coordinate ticks
    */
   public void setDrawTicks( boolean drawTicks ) {
      this.drawTicks = drawTicks;
   }
   
   /** Sets the function pixel values of this plot.
    * @param fPixels function pixel values
    */
   public void setFPixels( int[][] fPixels ) {
      this.fPixels = fPixels;
   }
   
   /**
    * Computes all pixel values of the current function values.
    */
   public void computePixels() {
      //double x;
      double y;
      double[][] fValues;

      setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
      fValues = computeValues();
      if ( initialPlot ) computeYScale( fValues );
      fPixels = new int[ fValues.length ][ width ];
      for ( int i = 0; i < fPixels.length; i++ ) {
         for ( int j = 0; j < fPixels[i].length; j++ ) {
            //x = ( j - ox ) / xScale;
            //y = f_i(x_j):
            y = fValues[i][j];
            fPixels[i][j] = (int) ( - y * yScale ) + oy;
         }
      }
      computeXTicks(); 
      computeYTicks();
   } 

   /** Computes the scale of the x-values, the x-value of the origin, and the function values.*/
   private double[][] computeValues() {
      double x;
      double y;
      double[][] fValues;

      if ( initialPlot ) {
         xScale = width / ( xmax - xmin );
         ox = (int) (- xmin * xScale);
      }
      fValues = new double[ numberOfFunctions ][ width ];
      for ( int i = 0; i < fValues.length; i++ ) {
         for ( int j = 0; j < fValues[i].length; j++ ) {
            x = ( j - ox ) / xScale;
            //y = f(x);
            y = parser.evaluate( i, x );
            fValues[i][j] = y;
         }
      }
      return fValues;
   }
    
   /** Computes the scale of the y-values, and the y-value of the origin.*/
   private void computeYScale( double[][] fValues ) {
      double y;

      // find range of f(x):
      if ( fValues == null || fValues.length == 0 ) {  // no function was defined
         ymin = 0;
         ymax = 0;
         yScale = 0;
         return;
      }
      
      if ( yAuto || yRatio ) {
         ymin = fValues[0][0];
         ymax = ymin;
         for ( int i = 0; i < fValues.length; i++ ) {
            for ( int j = 0; j < fValues[i].length; j++ ) {
               //y = f_i(x_j):
               y = fValues[i][j];
               if ( ymax < y ) {
                  ymax = y;
               } else if ( ymin > y ) {
                  ymin = y;
               }
            }
         }
      }
      
      if ( yRatio ) {
         yScale = xScale;
         oy = height/2; //(int) ( ( ymax + ymin ) * yScale / 2 ) + border;
      } else {
         yScale = ( height - 2 * border ) / ( ymax - ymin );
         oy = (int) ( ymax * yScale) + border;
      }
   }

   /** Computes the pixel values of the ticks on the x-axis.*/
   private void computeXTicks() {
      int magnitude = (int) Math.floor( Math.log(xmax - xmin) / Math.log(10) );
      double tmpMagnitude = Math.log(xmax - xmin) / Math.log(10);
      if ( tmpMagnitude - magnitude < .3 )
          magnitude--;
      double step = Math.pow( 10, magnitude );
      int numberOfTicks = (int) ( ( xmax - xmin ) / step ) + 3;
      double accuracy = .01;

      if ( Math.abs( (int) ( xmin / step ) - xmin / step ) < accuracy ) {
         numberOfTicks++;
      }
      if ( Math.abs( (int) ( xmax / step ) - xmax / step ) > accuracy ) {
         numberOfTicks--;
      }
      
      xTicks      = new double[ numberOfTicks ];
      xTicksPixel = new int[ numberOfTicks ];
      double x = step * Math.floor( xmin/step );
      
      for ( int i = 0; i < numberOfTicks; i++ ) {
         xTicks[i]      = step * Math.rint( x / step );
         xTicksPixel[i] = (int) (x * xScale) + ox;
         x += step;
      }
   }

   /** Computes the pixel values of the ticks on the y-axis.*/
   private void computeYTicks() {
      double step;
      if ( yRatio ) {
         step = xTicks[1] - xTicks[0];
      } else {
         int magnitude = (int) Math.floor( Math.log(ymax - ymin) / Math.log(10) );
         double tmpMagnitude = Math.log(ymax - ymin) / Math.log(10);
         if ( tmpMagnitude - magnitude < .3 )
            magnitude--;
         step = Math.pow( 10, magnitude );
      }
      int numberOfTicks = (int) ( ( ymax - ymin ) / step ) + 5;  // border factor!
      //double accuracy = .01;

      yTicks      = new double[ numberOfTicks ];
      yTicksPixel = new int[ numberOfTicks ];
      double y = step * Math.floor( ymin/step );
            
      for ( int i = 0; i < numberOfTicks; i++ ) {
         yTicks[i]      = step * Math.round( y / step );
         yTicksPixel[i] = (int) ( - y * yScale ) + oy;
         y += step;
      }
   }
  
   /** Sets the origin of this plot pane.
    * @param ox the x value of the origin
    * @param oy the y value of the origin
    */
   public void setOrigin( int ox, int oy ) {
      this.ox = ox;
      this.oy = oy;
   }

   /** Sets ticks for this plot.
    * @param xTicks array of ticks of the x values
    * @param xTicksPixel array of ticks of the x pixel values
    * @param yTicks array of ticks of the y values
    * @param yTicksPixel array of ticks of the y pixel values
    */
   public void setTicks(
     double[] xTicks, int[] xTicksPixel, 
     double[] yTicks, int[] yTicksPixel
   ) {
      this.xTicks = xTicks;
      this.xTicksPixel = xTicksPixel;
      this.yTicks = yTicks;
      this.yTicksPixel = yTicksPixel;
   }
   
   @Override
   public void paint( Graphics g ) {
      int acx1, acy1, acx2, acy2, k;
      
      setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
      
      if ( !isPrinting ) {
         // Change plot area if window is resized:
         Dimension size = getSize();
         if ( width != (int) size.getWidth() || height != size.getHeight() ) {
            width  = (int) size.getWidth();
            height = (int) size.getHeight();
            computePixels();
         }
      }

      g.setColor( background );
      g.fillRect( 0, 0, width, height );
      
      if ( drawGrid ) {//draw ticks grid:
         g.setColor( gridColor );
         for ( int i = 0; i < xTicks.length; i++ ) {
            g.drawLine( xTicksPixel[i], 0, xTicksPixel[i], height );
         }
         for ( int i = 0; i < yTicks.length; i++ ) {
            g.drawLine( 0, yTicksPixel[i], width, yTicksPixel[i] );
         }
      }
      if ( drawTicks ) {//draw ticks
         g.setColor( ticksColor );
         int fontHeight = g.getFontMetrics().getHeight();
//System.out.println( "### height of font: " + g.getFontMetrics().getHeight() );
         for ( int i = 0; i < xTicks.length; i++ ) {
            String tick = oneDigit.format( xTicks[i] );
            int tickWidth = g.getFontMetrics().charsWidth( tick.toCharArray(), 0, tick.length() );
//System.out.println( " # width of x=\"" + tick + "\": " + tickWidth );
            if ( oy < 0 || oy > height - fontHeight ) {  // the x-axis is out of the screen
               g.drawString( tick, xTicksPixel[i] - tickWidth / 2, height/2 + fontHeight );
               g.drawLine( xTicksPixel[i], height/2 - 2, xTicksPixel[i], height/2 + 2);
            } else {
               g.drawString( tick, xTicksPixel[i] - tickWidth / 2, oy + fontHeight );
               g.drawLine( xTicksPixel[i], oy - 2, xTicksPixel[i], oy + 2);
            }
         }
         for ( int i = 0; i < yTicks.length; i++ ) {
            String tick = oneDigit.format( yTicks[i] );
            int tickWidth = g.getFontMetrics().charsWidth( tick.toCharArray(), 0, tick.length() );
            if ( ox < 16 || ox > width ) {  // the y-axis is out of the screen
               g.drawString( tick, width / 2 - tickWidth - 2, yTicksPixel[i] + fontHeight / 3 );
               g.drawLine( width/2 - 2, yTicksPixel[i],  width/2 + 2, yTicksPixel[i] );
            } else {
//System.out.println( " # width of y=\"" + tick + "\": " + tickWidth );
               g.drawString( tick, ox - tickWidth - 2, yTicksPixel[i] + fontHeight / 3 );
               g.drawLine( ox - 1, yTicksPixel[i],  ox + 1, yTicksPixel[i] );
            }
         }
      }

      // coordinate axes
      g.setColor( axesColor );
      //g.drawRect( 0, 0, width - 1, height - 1 );
      g.drawLine( 0, oy, width, oy );
      g.drawLine( ox, 0, ox, height );

      
//      if ( fPixels == null ) {
//         this.fPixels = new int[width];
////System.out.println("+++ width = " + width );
//      }

//      g.drawString( " Hallo!", 50, 50 );

      //draw the curves
      k = 0;
//      for ( int i = 0; i <= curve.nop - steps; i += steps ) {
      for ( int i = 0; i < numberOfFunctions; i++ ) {
         g.setColor( lineColor[i] );
         // --- Swing: ----------------------------------------------------------------------
         Graphics2D g2D = (Graphics2D) g;
         BasicStroke pen = new BasicStroke( linewidth[i], BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL );
/*
         if ( i==1 ) {
            pen = new BasicStroke( linewidth[i], BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
         }
         if ( i==2 ) {
            pen = new BasicStroke( linewidth[i], BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL );
         }
*/
         if (pen != null) {
            g2D.setStroke(pen);
         }
         // --- Swing -  --------------------------------------------------------------------
         for ( int j = 0; j < width - steps; j += steps ) {
            acx1 = j;
            acy1 = fPixels[i][j];
            acx2 = j+steps;
            acy2 = fPixels[i][j+steps];

            // the next "if" checks to see at least one of the endpoints is on screen
            if ( ( 0 <= acx2 && acx2 < width && 0 <= acy2 && acy2 < height ) || 
                 ( 0 <= acx1 && acx1 < width && 0 <= acy1 && acy1 < height )
            ) {
               // the next "if" checks to see the points aren't too far away
               if (Math.abs(acx1 - acx2) < width/3 && Math.abs(acy1 - acy2) < height/3) {
                   //g.drawLine(acx1, acy1, acx2, acy2);
                   g2D.drawLine(acx1, acy1, acx2, acy2);
               }
            }
            k = j;
         } // end for-j
         if ( k != width - 1 ) {
            k++;
            acx1 = k;
            acy1 = fPixels[i][k];
            acx2 = fPixels.length - 1;
            acy2 = fPixels[i][fPixels.length - 1 ];
            // the next "if" checks to see at least one of the endpoints is on screen
//            if ((0 < acx2 && acx2 < Curve.xsize && 0 < acy2 && acy2 < Curve.ysize) || (0 < acx1 && acx1 < Curve.xsize && 0 < acy1 && acy1 < Curve.ysize)) {
            if ( ( 0 < acx2 && acx2 < width && 0 < acy2 && acy2 < height ) || 
                 ( 0 < acx1 && acx1 < width && 0 < acy1 && acy1 < height )
            ) {
               // the next "if" checks to see the points aren't too far away
               if (Math.abs(acx1 - acx2) < width/3 && Math.abs(acy1 - acy2) < height/3) {
                  //g.drawLine(acx1, acy1, acx2, acy2);
                  g2D.drawLine(acx1, acy1, acx2, acy2);
               }
            }
         }
      } // for-i
      setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
   }

   @Override
   public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
      if (pi >= 1) {
         return Printable.NO_SUCH_PAGE;
      }
      paint(g);
      return Printable.PAGE_EXISTS;
   }
   
   /**
    *  Prints this plot.
    */
   public void startPrinterJob() {
      int origWidth = width, origHeight = height;
      
      isPrinting = true;
      
      PrinterJob printJob = PrinterJob.getPrinterJob();
      PageFormat pageFormat = printJob.defaultPage();
      pageFormat.setOrientation(PageFormat.LANDSCAPE);
      Paper paper = pageFormat.getPaper();
      /*
      // Format A4 = 580 x 800 Pixel^2:
      paper.setImageableArea( 0.0, 0.0, 580.0, 800.0 );
      paper.setSize( 580.0, 800.0 );
      pageFormat.setPaper( paper );
      //pageFormat = printJob.validatePage( pageFormat );
      */
/*
System.out.println("### Page height: " + paper.getHeight() +
", width: " + paper.getWidth() +
"\nimageable height: " + paper.getImageableHeight() +
", width: " + paper.getImageableWidth() +
"\nimageable x: " + paper.getImageableX() +
", y: " + paper.getImageableY()
);
*/
      paper.setImageableArea( 0, 0, paper.getWidth(), paper.getHeight() );
      //paper.setImageableArea( 0, 0, height, width );
      pageFormat.setPaper( paper );
      pageFormat.setOrientation(PageFormat.LANDSCAPE);

System.out.println("Printing curve (" 
+ paper.getImageableHeight() + "px x " + paper.getImageableWidth() + "px)");

      printJob.setPrintable( this, pageFormat );
      if (printJob.printDialog()) {
         try {
            width  = (int) paper.getImageableHeight();
            height = (int) paper.getImageableWidth();
            initialPlot = true;
            computePixels();
            printJob.print();
         } catch (Exception ex) {
            ex.printStackTrace();
         } finally {
            width  = origWidth;
            height = origHeight;
            initialPlot = true;
            computePixels();
         }
      }
      isPrinting = false;
   }
}
