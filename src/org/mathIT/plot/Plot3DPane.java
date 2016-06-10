/*
 * PlotPane3D.java - Canvas panel for plotting graphs of 3D functions
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
import static org.mathIT.numbers.Numbers.*;
import org.mathIT.util.FunctionParser;
/**
 * An object of this class is a canvas for plotting graphs of three-dimensional function.
 * The variables used in this class are related to a given function <i>f</i> by:
 * <p style="text-align:center">
 *   <i>z</i> = <i>f</i> (<i>x</i>, <i>y</i>)
 * </p>
 * @author  Andreas de Vries
 * @version 1.1
 */
public class Plot3DPane extends JPanel implements Printable {
   private static final long serialVersionUID = 1102231994; // = "Plot3DPane".hashCode()
   /** number of mesh nodes in <i>x</i>-direction.*/
   private int meshNodesX = 50;
   /** number of mesh nodes in <i>y</i>-direction.*/
   private int meshNodesY = 50;
   /** width of plotted mesh (in pixels). Must be a multiple of resolution.*/
   //private int meshWidth = 2*resolution;
   private int numberOfFunctions;
   private int width;
   private int height;
   /** lower limit of x.*/
   double xmin;
   /** upper limit of x.*/
   double xmax;
   /** lower limit of y.*/
   double ymin;
   /** upper limit of y.*/
   double ymax;
   /** lower limit of z.*/
   double zmin;
   /** upper limit of z.*/
   double zmax;
   /** indicates whether the z-limits are calculated automatically.*/
   boolean zAuto;
   /** indicates whether the x/y-z ratio are the same.*/
   boolean zRatio;
   /** minimum projected x-value.*/
   int vxmin;
   /** maximum projected x-value.*/
   int vxmax;
   /** minimum projected y-value.*/
   int vymin;
   /** maximum projected y-value.*/
   int vymax;
   /** Indicates whether coordinate ticks are drawn.*/
   private boolean drawTicks;
   /** Indicates whether a coordinate grid is drawn.*/
   private boolean drawGrid;
   /** thickness of the plot pane border (in pixel).*/ 
   int border = 50;
//   private boolean initialPlot = true;
   private boolean isPrinting = false;
   //private String[] functions;
   private FunctionParser parser;
   /** step width in pixels the graph is plotted with.*/
   private final int steps  = 3;
   private Color background = Color.white;
   private Color axesColor  = Color.black;
   private Color ticksColor = Color.black;
   private Color gridColor  = Color.lightGray;
   private Color projectedLineColor = Color.red;
   /** array of arrays containing the function values. */
   private double[][][] surface;
   /** represents the x-coordinate projected on each surface. */
   private double[][] xLine;
   /** represents the x-coordinate projected on each surface. */
   private double[][] yLine;
   /** array containing the area colors. */
   private Color[] surfaceColor;
   /** array containing the area colors. */
   private byte[] lineStrength;
   /** rotation angle of (projected) coordinate frame around the screen <i>x</i>-axis.*/
   double angleX = 30.0; 
   /** rotation angle of (projected) coordinate frame around the screen <i>y</i>-axis.*/
   double angleY = -45.0;

   /** scale factor for x-values.*/
   double xScale;
   /** scale factor for y-values.*/
   double yScale;
   /** scale factor for y-values.*/
   double zScale;
   private double[] xTicks;
   private int[] xTicksPixel;
   private double[] yTicks;
   private int[] yTicksPixel;
   private double[] zTicks;
   //private int[] zTicksPixel;
   private java.text.DecimalFormat oneDigit = new java.text.DecimalFormat( "#,##0.#######" );
   /** Holds the coordinates of the user's last mousePressed event.*/
   private int lastX, lastY;
   //private boolean mouseDragged = false;
   
   /** Creates a new 3D plot pane.
    *  @param width the width of this plot pane
    *  @param height the width of this plot pane
    *  @param functions an array of all functions to be plotted, 
    *     given by strings parseable by {@link org.mathIT.util.FunctionParser FunctionParser}.
    *  @param surfaceColor an array of colors for each function 
    *     (i.e., <code>surfaceColor.length = functions.length</code>)
    *  @param lineStrength an array of line strengths in pixels for each function 
    *     (i.e., <code>lineStrength.length = functions.length</code>);
    *     usually, the strength is a small positive integer
    *  @param xmin the minimum <i>x</i>-value to be plotted
    *  @param xmax the maximum <i>x</i>-value to be plotted
    *  @param ymin the minimum <i>y</i>-value to be plotted
    *  @param ymax the maximum <i>y</i>-value to be plotted
    *  @param zmin the minimum <i>z</i>-value to be plotted
    *  @param zmax the maximum <i>z</i>-value to be plotted
    *  @param zAuto indicates whether the limits of <i>z</i>-values, given by
    *     <i>z</i> = <i>f</i> (<i>x</i>, <i>y</i>), are calculated automatically
    *  @param zRatio indicates whether the ratio <i>z/x</i> is one
    *  @param drawTicks indicates whether the axes are marked with ticks
    *  @param drawGrid indicates whether the plot is marked with a grid
    */
   public Plot3DPane(
     int width, int height, String[] functions, 
     Color[] surfaceColor, byte[] lineStrength, 
     double xmin, double xmax, double ymin, double ymax, double zmin, double zmax, 
     boolean zAuto, boolean zRatio, boolean drawTicks, boolean drawGrid
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
      this.zmin = zmin;
      this.zmax = zmax;
      this.vxmin = 0;
      this.vxmax = width;
      this.vymin = 0;
      this.vymax = height;
      this.surfaceColor = surfaceColor;
      this.lineStrength = lineStrength;
      this.zAuto  = zAuto;
      this.zRatio = zRatio;
      this.drawTicks = drawTicks;
      this.drawGrid  = drawGrid;
      parser = new FunctionParser( functions );
      computeScreen();
      surface = computeSurface();
      xLine = computeXLine();
      yLine = computeYLine();
      computeXTicks();
      computeYTicks();
      computeZTicks();

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
         @Override
         public void mouseDragged(java.awt.event.MouseEvent evt) {
            myMouseDragged(evt);
         }
      });
   }
   
   private void myMouseDragged(java.awt.event.MouseEvent evt) {
      if ( evt.isAltDown() ) {  // translate:
         int deltaX = evt.getX() - lastX;
         int deltaY = evt.getY() - lastY;
         vxmin += deltaX;
         vxmax += deltaX;
         vymin += deltaY;
         vymax += deltaY;
      } else { // rotate:
         double factor = .75;
         angleX = (angleX + (int) ( factor * (evt.getY() - lastY) ) ) % 360;
         angleY = angleY + (int) ( factor * (evt.getX() - lastX) );
         if ( angleY >  180 ) angleY -= 360;
         if ( angleY < -180 ) angleY += 360;
      }
      lastX = evt.getX();
      lastY = evt.getY();
   }
   
   private void myMousePressed(java.awt.event.MouseEvent evt) {
      setCursor( new Cursor( Cursor.MOVE_CURSOR ) );
      //setCursor( new Cursor( Cursor.HAND_CURSOR ) );
      //setCursor( new Cursor( Cursor.CROSSHAIR_CURSOR ) );
      lastX = evt.getX();
      lastY = evt.getY();
      //mouseDragged = true;
   }
   
   private void myMouseReleased(java.awt.event.MouseEvent evt) {
      //mouseDragged = false;
      //repaint();
   }
   
   private void myMouseClicked(java.awt.event.MouseEvent evt) {
      double delta = 20;
      
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
         if ( vxmax > vxmin + 3*delta && vymax > vymin + 3*delta ) {
            vxmin += delta;
            vxmax -= delta;
            vymin += delta;
            vymax -= delta;
            repaint();
         }
      } else if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
         if ( vxmax > vxmin + delta && vymax > vymin + delta ) {
            vxmin -= delta;
            vxmax += delta;
            vymin -= delta;
            vymax += delta;
            repaint();
         }
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
   
   /** Computes the scale of the x-values, the x-value of the origin, and the function values.*/
   private double[][][] computeSurface() {
      double x;
      double y;
      double z;
      double[][][] surface;

      surface = new double[ numberOfFunctions ][ meshNodesX ][ meshNodesY ];
      for ( int i = 0; i < surface.length; i++ ) {
         for ( int j = 0; j < meshNodesX; j++ ) {
            x = xmin + j * (xmax - xmin) / meshNodesX;
            for ( int k = 0; k < meshNodesY; k++ ) {
               y = ymin + k * (ymax - ymin) / meshNodesY;
               //z = f(x,y);
               z = parser.evaluate( i, x, y );
               surface[i][j][k] = z;
            }
         }
      }
      return surface;
   }

   private double[][] computeXLine() {
      double y;
      double z;
      double[][] line;

      line = new double[ numberOfFunctions ][ meshNodesY ];
      for ( int i = 0; i < line.length; i++ ) {
         for ( int j = 0; j < meshNodesY; j++ ) {
            y = ymin + j * (ymax - ymin) / meshNodesY;
            //z = f(x,y);
            z = parser.evaluate( i, 0, y );
            line[i][j] = z;
         }
      }
      return line;
   }
    
   private double[][] computeYLine() {
      double x;
      double z;
      double[][] line;

      line = new double[ numberOfFunctions ][ meshNodesX ];
      for ( int i = 0; i < line.length; i++ ) {
         for ( int j = 0; j < meshNodesX; j++ ) {
            x = xmin + j * (xmax - xmin) / meshNodesX;
            //z = f(x,y);
            z = parser.evaluate( i, x, 0 );
            line[i][j] = z;
         }
      }
      return line;
   }
    
   /** Computes the screen area, given by the upper-left and the lower-right corners
    *  (<i>v</i><sub><i>x</i>, min</sub>, <i>v</i><sub><i>y</i>, min</sub>) 
    *  and 
    *  (<i>v</i><sub><i>x</i>, max</sub>, <i>v</i><sub><i>y</i>, max</sub>).
    *  They are given by the projection of the cube vertices
    *  (<i>x</i><sub>min</sub>, <i>y</i><sub>min</sub>, <i>z</i><sub>min</sub>) 
    *  and
    *  (<i>x</i><sub>max</sub>, <i>y</i><sub>max</sub>, <i>z</i><sub>max</sub>). 
    */
   private void computeScreen() {
      int border = width/4;
      double tmp, tmpMin, tmpMax;
      
      tmpMin = vx(xmin,0,0);
      if ( (tmp = vx(0,ymin,0)) < tmpMin ) {
         tmpMin = tmp;
      }
      if ( (tmp = vx(0,0,zmin)) < tmpMin ) {
         tmpMin = tmp;
      }

      tmpMax = vx(xmax,0,0);
      if ( (tmp = vx(0,ymax,0)) > tmpMax ) {
         tmpMax = tmp;
      }
      if ( (tmp = vx(0,0,zmax)) > tmpMax ) {
         tmpMax = tmp;
      }
      vxmin = (int) Math.round( width * tmpMin / (tmpMax - tmpMin) );
      vxmax = (int) Math.round( width * tmpMax / (tmpMax - tmpMin) );
      vxmin += width/2; //??
      vxmax += width/2; //??

      tmpMin = vy(xmin,0,0);
      if ( (tmp = vy(0,ymin,0)) < tmpMin ) {
         tmpMin = tmp;
      }
      if ( (tmp = vy(0,0,zmin)) < tmpMin ) {
         tmpMin = tmp;
      }

      tmpMax = vy(xmax,0,0);
      if ( (tmp = vy(0,ymax,0)) > tmpMax ) {
         tmpMax = tmp;
      }
      if ( (tmp = vy(0,0,zmax)) > tmpMax ) {
         tmpMax = tmp;
      }
      
      vymin = (int) Math.round( height * tmpMin / (tmpMax - tmpMin) );
      vymax = (int) Math.round( height * tmpMax / (tmpMax - tmpMin) );
      vymin += height/2; //??
      vymax += height/2; //??

      if ( vxmax > vxmin + border && vymax > vymin + border ) {
         vxmin += border;
         vxmax -= border;
         vymin += border;
         vymax -= border;
         vymin -= (width - height)/2;
         vymax -= (width - height)/2;
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
         x += step;
      }
   }

   /** Computes the pixel values of the ticks on the y-axis.*/
   private void computeYTicks() {
      int magnitude = (int) Math.floor( Math.log(ymax - ymin) / Math.log(10) );
      double tmpMagnitude = Math.log(ymax - ymin) / Math.log(10);
      if ( tmpMagnitude - magnitude < .3 )
          magnitude--;
      double step = Math.pow( 10, magnitude );
      int numberOfTicks = (int) ( ( ymax - ymin ) / step ) + 3;
      double accuracy = .01;

      if ( Math.abs( (int) ( ymin / step ) - ymin / step ) < accuracy ) {
         numberOfTicks++;
      }
      if ( Math.abs( (int) ( ymax / step ) - ymax / step ) > accuracy ) {
         numberOfTicks--;
      }
      
      yTicks      = new double[ numberOfTicks ];
      double y = step * Math.floor( ymin/step );
      
      for ( int i = 0; i < numberOfTicks; i++ ) {
         yTicks[i]      = step * Math.rint( y / step );
         y += step;
      }
   }
  
   /** Computes the pixel values of the ticks on the z-axis.*/
   private void computeZTicks() {
      double step;
      if ( zRatio ) {
         step = xTicks[1] - xTicks[0];
      } else {
         int magnitude = (int) Math.floor( Math.log(zmax - zmin) / Math.log(10) );
         double tmpMagnitude = Math.log(zmax - zmin) / Math.log(10);
         if ( tmpMagnitude - magnitude < .3 )
            magnitude--;
         step = Math.pow( 10, magnitude );
      }
      int numberOfTicks = (int) ( ( zmax - zmin ) / step ) + 5;  // border factor!
      //double accuracy = .01;

      zTicks      = new double[ numberOfTicks ];
      double z = step * Math.floor( zmin/step );
            
      for ( int i = 0; i < numberOfTicks; i++ ) {
         zTicks[i]      = step * Math.round( z / step );
         z += step;
      }
   }

   /** Sets ticks for this plot.
    * @param xTicks array of ticks of the x values
    * @param xTicksPixel array of ticks of the x pixel values
    * @param yTicks array of ticks of the y values
    * @param yTicksPixel array of ticks of the y pixel values
    * @param zTicks array of ticks of the z values
    * @param zTicksPixel array of ticks of the z pixel values
    */
   public void setTicks(
     double[] xTicks, int[] xTicksPixel, 
     double[] yTicks, int[] yTicksPixel,
     double[] zTicks, int[] zTicksPixel
   ) {
      this.xTicks = xTicks;
      this.xTicksPixel = xTicksPixel;
      this.yTicks = yTicks;
      this.yTicksPixel = yTicksPixel;
      this.zTicks = zTicks;
      //this.zTicksPixel = zTicksPixel;
   }
   
   @Override
   public void paint( Graphics g1 ) {
      setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
      
      Graphics2D g = (Graphics2D) g1;
      if ( !isPrinting ) {
         // Change plot area if window is resized:
         Dimension size = getSize();
         if ( width != (int) size.getWidth() || height != size.getHeight() ) {
            vxmin = (int) ( (double) vxmin * size.getWidth() / width );
            vxmax = (int) ( (double) vxmax * size.getWidth() / width );
            vymin = (int) ( (double) vymin * size.getHeight() / height );
            vymax = (int) ( (double) vymax * size.getHeight() / height );
            width  = (int) size.getWidth();
            height = (int) size.getHeight();
            //computeScreen();
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
         drawTicks(g);
      } // 

      // coordinate axes
      axes(g);
      graph(g);
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
      int origVxmin = vxmin, origVxmax = vxmax, origVymin = vymin, origVymax = vymax; 
      
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
            
            vxmin = (int) ( (double) vxmin * width / origWidth );
            vxmax = (int) ( (double) vxmax * width / origWidth );
            vymin = (int) ( (double) vymin * height / origHeight );
            vymax = (int) ( (double) vymax * height / origHeight );
            printJob.print();
         } catch (Exception ex) {
            ex.printStackTrace();
         } finally {
            width  = origWidth;
            height = origHeight;
            vxmin = origVxmin;
            vxmax = origVxmax;
            vymin = origVymin;
            vymax = origVymax;
            isPrinting = false;
         }
      }
   }
   
   // plot methods: -------------------------------
   /** Plots the axes of this graph.
    * @param g the graphics object of this plot
    */
   public void axes(Graphics g) {
      int px, py;

      g.setColor( axesColor );
      
      double xs = vx(xmin, 0.0, 0.0);
      double ys = vy(xmin, 0.0, 0.0);
      double xe = vx(xmax, 0.0, 0.0);
      double ye = vy(xmax, 0.0, 0.0);
      line(g, xs, ys, xe, ye);
      px = mapX(xe) - 10;
      py = mapY(ye) - 10;
//System.out.println("###e=("+px+", "+ py + ")");
      if ( px < 0 ) px = 0;
      if ( px > width ) px = width-10;
      if ( py < 0 ) py = 10;
      if ( py > height ) py = height;
//System.out.println("###e=("+px+", "+ py + ")");
      g.drawString("x", px, py);
      
      xs = vx(0.0, ymin, 0.0);
      ys = vy(0.0, ymin, 0.0);
      xe = vx(0.0, ymax, 0.0);
      ye = vy(0.0, ymax, 0.0);
      line(g, xs, ys, xe, ye);
      px = mapX(xe) - 10;
      py = mapY(ye) - 10;
      if ( px < 0 ) px = 0;
      if ( px > width ) px = width-10;
      if ( py < 0 ) py = 10;
      if ( py > height ) py = height;
      g.drawString("y", px, py);
      
      xs = vx(0.0, 0.0, zmin);
      ys = vy(0.0, 0.0, zmin);
      xe = vx(0.0, 0.0, zmax);
      ye = vy(0.0, 0.0, zmax);
      line(g, xs, ys, xe, ye);
      px = mapX(xe) + 10;
      py = mapY(ye) + 10;
      if ( px < 0 ) px = 0;
      if ( px > width ) px = width-10;
      if ( py < 0 ) py = 10;
      if ( py > height ) py = height;
      g.drawString("z", px, py);
   }
   
   void graph(Graphics2D g) {
      //int acx1, acy1, acx2, acy2, l;
      double x1, x2, y1, y2, z1, z2, vx1, vx2, vy1, vy2;
      
      for ( int i = 0; i < numberOfFunctions; i++ ) {
         g.setColor( surfaceColor[i] );
         BasicStroke pen = new BasicStroke( lineStrength[i], BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL );
         g.setStroke(pen);

         // fix x:
         for ( int j = 0; j < meshNodesX; j += steps ) {
            //l = 0;
            x1 = xmin + j * (xmax - xmin) / meshNodesX;
            y1 = ymin; 
            z1 = surface[i][j][0];
            vx1 = vx(x1,y1,z1);
            vy1 = vy(x1,y1,z1);
            for ( int k = 0; k < meshNodesY - steps; k += steps ) {
               y2 = ymin + (k+steps) * (ymax - ymin) / meshNodesY;
               z2 = surface[i][j][k+steps];

               vx2 = vx(x1,y2,z2);
               vy2 = vy(x1,y2,z2);
               line( g, vx1, vy1, vx2, vy2);
               
               //y1 = y2;
               //z1 = z2;
               vx1 = vx2;
               vy1 = vy2;
            }
         } // end for-j
         
         // fix y:
         for ( int k = 0; k < meshNodesY; k += steps ) {
            //l = 0;
            y1 = ymin + k * (ymax - ymin) / meshNodesY;
            x1 = xmin; 
            z1 = surface[i][0][k];
            vx1 = vx(x1,y1,z1);
            vy1 = vy(x1,y1,z1);
            for ( int j = 0; j < meshNodesX - steps; j += steps ) {
               x2 = xmin + (j+steps) * (xmax - xmin) / meshNodesX;
               z2 = surface[i][j+steps][k];

               vx2 = vx(x2,y1,z2);
               vy2 = vy(x2,y1,z2);
               line( g, vx1, vy1, vx2, vy2);
               
               //x1 = x2;
               //z1 = z2;
               vx1 = vx2;
               vy1 = vy2;
            }
         } // end for-j
         
         g.setColor( projectedLineColor );
         // projected x-coordinate:
         y1 = ymin;
         z1 = xLine[i][0];
         vx1 = vx(0,y1,z1);
         vy1 = vy(0,y1,z1);
         for ( int j = 0; j < meshNodesY - steps; j += steps ) {
            y2 = ymin + (j+steps) * (ymax - ymin) / meshNodesY;
            z2 = xLine[i][j+steps];

            vx2 = vx(0,y2,z2);
            vy2 = vy(0,y2,z2);
            line( g, vx1, vy1, vx2, vy2);
               
            //y1 = y2;
            //z1 = z2;
            vx1 = vx2;
            vy1 = vy2;
         } // end for-j
         
         // projected y-coordinate:
         x1 = xmin;
         z1 = yLine[i][0];
         vx1 = vx(x1,0,z1);
         vy1 = vy(x1,0,z1);
         for ( int j = 0; j < meshNodesX - steps; j += steps ) {
            x2 = xmin + (j+steps) * (xmax - xmin) / meshNodesX;
            z2 = yLine[i][j+steps];

            vx2 = vx(x2,0,z2);
            vy2 = vy(x2,0,z2);
            line( g, vx1, vy1, vx2, vy2);
               
            //x1 = x2;
            //z1 = z2;
            vx1 = vx2;
            vy1 = vy2;
         } // end for-j
      } // for-i
   }
    
   /** Returns the projected value on the x axis given by the current rotation angles.
    * @param x the x value
    * @param y the y value
    * @param z the z value
    * @return the projected value on the x axis given by the current rotation angles
    */
   public double vx(double x, double y, double z) {
      double b = angleY * RADIANS;
      return x * Math.cos(b) - y * Math.sin(b);
   }

   /** Returns the projected value on the y axis given by the current rotation angles.
    * @param x the x value
    * @param y the y value
    * @param z the z value
    * @return the projected value on the y axis given by the current rotation angles
    */
   public double vy(double x, double y, double z) {
      double a = angleX * RADIANS;
      double b = angleY * RADIANS;
      return z * Math.cos(a) + Math.sin(a) * ( x*Math.sin(b) + y*Math.cos(b) );
   }

   /** Returns the projected value of the specified x value mapped onto this plot.
    * @param x the x value
    * @return the projected value of x
    */
   public int mapX(double x) {
      int px = vxmin + (int)((x - xmin) / (xmax - xmin) * (vxmax - vxmin));
      return px;
   }

   /** Returns the projected value of the specified x value mapped onto this plot.
    * @param y the y value
    * @return the projected value of y
    */
   public int mapY(double y) {
      int py = vymin + (int)((ymax - y) / (ymax - ymin) * (vymax - vymin));
      return py;
   }
   
   /** returns the x-value corresponding to the pixels (<i>p<sub>x</sub>,p<sub>y</sub></i>).
    *  It uses the transformation
    *  <p style="text-align:center">
    *    (<i>x,y</i>)<sup>T</sup> 
    *    = A<sup>-1</sup> 
    *      [(<i>p<sub>x</sub>,p<sub>y</sub></i>)<sup>T</sup> - <i>a</i>]
    *  </p>
    *  where <i>A</i> is the rotation composed of the rotation with 
    *  <code>angleX</code> around the screen <i>x</i>-axis and
    *  <code>angleY</code> around the screen <i>y</i>-axis,
    *  and <i>a</i> the (pixel) translation.
    *  @param px pixels <i>p<sub>x</sub></i> in <i>x</i> coordinate
    *  @param py pixels <i>p<sub>y</sub></i> in <i>y</i> coordinate
    *  @return <i>x</i>-value corresponding to (<i>p<sub>x</sub>,p<sub>y</sub></i>)
    */
   public double pixelToX(int px, int py) {
      double sinA = Math.sin(angleX * RADIANS);
      double b = angleY * RADIANS;
      double[] v = pixelToV(px,py);
      double x = v[0] * Math.cos(b);
      if ( sinA != 0. ) {
         x += v[1] * Math.sin(b)/sinA;
      }
      return x;
   } 
   
   /** returns the x-value corresponding to the pixels (<i>p<sub>x</sub>,p<sub>y</sub></i>).
    *  @param px pixels <i>p<sub>x</sub></i> in <i>x</i> coordinate
    *  @param py pixels <i>p<sub>y</sub></i> in <i>y</i> coordinate
    *  @return <i>y</i>-value corresponding to (<i>p<sub>x</sub>,p<sub>y</sub></i>)
    *  @see #pixelToX(int,int)
    */
   public double pixelToY(int px, int py) {
      double sinA = Math.sin(angleX * RADIANS);
      double b = angleY * RADIANS;
      double[] v = pixelToV(px,py);
      double y = v[0] * Math.sin(b); 
      if ( sinA != 0. ) {
         y -= v[1] * Math.cos(b)/sinA;
      }
      return y;
   }

   /** Returns the projected coordinate values from the pixel values.
    * @param px the pixel value of x
    * @param py the pixel value of y
    * @return an array where the first entry is the projected x coordinate and the second entry the projected y coordinate
    */
   public double[] pixelToV(int px, int py) {
      double[] v = new double[2];
      // v_x:
      v[0] = (px - vxmin) * (xmax - xmin) / (vxmax - vxmin) - xmin;
      //v_y:
      v[1] = ymax - (py - vymin) * (ymax - ymin) / (vymax - vymin);
      return v;
   }

   /** Draws the line through the specified start and end coordinates projected onto the plot plane. 
    * @param g the graphics object of this plot
    * @param x1 the x coordinate of the starting point
    * @param y1 the y coordinate of the starting point
    * @param x2 the x coordinate of the end point
    * @param y2 the y coordinate of the end point
    */
   public void line(Graphics g, double x1, double y1, double x2, double y2) {
      g.drawLine(mapX(x1), mapY(y1), mapX(x2), mapY(y2));
   }
      
   /** Draws ticks on the axes of this plot.
    * @param g the graphics object of this plot
    */
   public void drawTicks(Graphics2D g) {
      int x, y;
      String tick;
      int tickWidth;
      
      g.setColor( ticksColor );
      int fontHeight = g.getFontMetrics().getHeight();

      for ( int i = 0; i < xTicks.length; i++ ) {
         if ( xTicks[i] >= xmin && xTicks[i] <= xmax ) {  // x-axis is in its range?
            x = mapX( vx( xTicks[i], 0, 0 ) );
            y = mapY( vy( xTicks[i], 0, 0 ) );
            tick = oneDigit.format( xTicks[i] );
            tickWidth = g.getFontMetrics().charsWidth( tick.toCharArray(), 0, tick.length() );
            g.drawString( tick, x - tickWidth / 2, y + fontHeight );
            g.drawLine( x, y-2, x, y + 2);
         }
      }

      for ( int i = 0; i < yTicks.length; i++ ) {
         if ( yTicks[i] >= ymin && yTicks[i] <= ymax ) {  // y-axis is in its range?
            x = mapX( vx( 0, yTicks[i], 0 ) );
            y = mapY( vy( 0, yTicks[i], 0 ) );
            tick = oneDigit.format( yTicks[i] );
            tickWidth = g.getFontMetrics().charsWidth( tick.toCharArray(), 0, tick.length() );
            g.drawString( tick, x - tickWidth - 2, y + fontHeight / 3 );
            g.drawLine( x - 1, y,  x + 1, y );
         }
      }
      
      for ( int i = 0; i < zTicks.length; i++ ) {
         if ( zTicks[i] >= zmin && zTicks[i] <= zmax ) {  // z-axis is in its range?
            x = mapX( vx( 0, 0, zTicks[i] ) );
            y = mapY( vy( 0, 0, zTicks[i] ) );
            tick = oneDigit.format( zTicks[i] );
            tickWidth = g.getFontMetrics().charsWidth( tick.toCharArray(), 0, tick.length() );
            g.drawString( tick, x - tickWidth - 2, y + fontHeight / 3 );
            g.drawLine( x - 1, y,  x + 1, y );
         }
      }
   }
}
