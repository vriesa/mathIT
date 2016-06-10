/*
 * Canvas.java
 *
 * Copyright (C) 2013 Andreas de Vries
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
package org.mathIT.gui;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import org.mathIT.graphs.Vertible;

/**
 * This class enables to let plot and print a graph.
 * It uses visualization classes from the open source project JUNG
 * It bases on the JUNG Java Universal Network/Graph Framework
 * (<a href="http://jung.sourceforge.net">http://jung.sourceforge.net</a>),
 * version 2.0.1 from 2010.
 * @param <V> class implementing {@link Vertible} and representing vertices
 * @param <E> class representing edges
 * @author Andreas de Vries
 * @version 1.0
 */
public class GraphCanvas<V extends Vertible<V>, E> extends VisualizationViewer<V, E>  implements Printable {
   private static final long serialVersionUID = 2011128184L;  // = "Canvas".hashCode()
   /** The graphviewer object to be plotted. */
   final private GraphViewer<V,E> gv;

   /**
    * Create an instance with the specified parameters.
    *
    * @param gv the graphviewer object to be plotted
    * @param layout	The Layout to apply, with its associated Graph
    */
   public GraphCanvas(GraphViewer<V,E> gv, Layout<V, E> layout) {
      super(layout);
      this.gv = gv;
      initComponents();
   }

   /**
    * Create an instance with passed parameters.
    *
    * @param gv the graphviewer object to be plotted
    * @param layout	The Layout to apply, with its associated Graph
    * @param preferredSize the preferred size of this View
    */
   public GraphCanvas(GraphViewer<V,E> gv, Layout<V, E> layout, Dimension preferredSize) {
      super(layout, preferredSize);
      this.gv = gv;
      initComponents();
   }

   /**
    * Create an instance with the specified parameters.
    *
    * @param gv the graphviewer object to be plotted
    * @param model visualization model
    */
   public GraphCanvas(GraphViewer<V,E> gv, VisualizationModel<V, E> model) {
      super(model);
      this.gv = gv;
      initComponents();
   }

   /**
    * Create an instance with passed parameters.
    *
    * @param gv the graphviewer object to be plotted
    * @param model visualization model
    * @param preferredSize initial preferred size of the view
    */
   public GraphCanvas(GraphViewer<V,E> gv, VisualizationModel<V, E> model, Dimension preferredSize) {
      super(model, preferredSize);
      this.gv = gv;
      initComponents();
   }
 
   private void initComponents() {
      this.addMouseListener(new java.awt.event.MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent evt) {
            if (
              (evt.getButton() == MouseEvent.BUTTON1 && evt.isControlDown()) // && evt.isAltDown())
              || evt.getButton() == MouseEvent.BUTTON2
            ) {
               startPrinterJob();
            }
         }
      });
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
    * This method starts a printer job to print out this graph canvas.
    */
   public void startPrinterJob() {
      int width, height;
      int origWidth = this.getWidth(), origHeight = this.getHeight();
      
      PrinterJob printJob = PrinterJob.getPrinterJob();
      
      // /* --- Either default setting of printer: ---
      PageFormat pageFormat = printJob.defaultPage();
      pageFormat.setOrientation(PageFormat.LANDSCAPE);
      Paper paper = pageFormat.getPaper();
      paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight());
      
      pageFormat.setPaper( paper );

      width = (int) paper.getImageableHeight();
      height = (int) paper.getImageableWidth();
      this.setSize(new Dimension(width, height));
      this.gv.scaler.scale(this, 1f, getCenter());
      
      printJob.setPrintable(this, pageFormat);
      if (printJob.printDialog()) {
         try {
            printJob.print();
         } catch (Exception ex) {
            ex.printStackTrace();
         } finally {
            width  = origWidth;
            height = origHeight;
            this.setSize(new Dimension(width, height));
            this.gv.scaler.scale(this, 1f, getCenter());
         }
      }
   }
}
