/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * Modified and adapted to the math IT framework by Andreas de Vries (2013)
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 */
package org.mathIT.graphs;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
//import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.Animator;
import edu.uci.ics.jung.visualization.util.PredicatedParallelEdgeIndexFunction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.mathIT.algebra.OrderedSet;

/**
 * This class provides a visualization frame to show a specified graph.
 * It bases on the JUNG Java Universal Network/Graph Framework
 * (<a href="http://jung.sourceforge.net" target="_new">http://jung.sourceforge.net</a>),
 * version 2.0.1 from 2010.
 * <p>
 * In particular, this class is an adaption of the class
 * <code>VisualizationViewer.java</code> in the package
 * <code>edu.uci.ics.jung.visualization</code>, written by Tom Nelson.
 * </p>
 *
 * @author Tom Nelson, Andreas de Vries
 * @version 1.1
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
@SuppressWarnings("serial")
public class GraphViewer<V extends Vertible<V>,E> extends JFrame {
   private String instructions =
           "<html>Use the mouse to select multiple vertices"
           + "<p>either by dragging a region, or by shift-clicking"
           + "<p>on multiple vertices."
           + "<p>If you select 2 (and only 2) vertices, then press"
           + "<p>the Compress Edges button, parallel edges between"
           + "<p>those two vertices will no longer be expanded."
           + "<p>If you select 2 (and only 2) vertices, then press"
           + "<p>the Expand Edges button, parallel edges between"
           + "<p>those two vertices will be expanded."
           + "<p>You can drag the vertices with the mouse."
           + "<p>Use the 'Picking'/'Transforming' combo-box to switch"
           + "<p>between picking and transforming mode.</html>";
   /**
    * The graph of the mathIT class {@link Graph} that is invoked
    * to be copied to the format compatible with this viewer.
    */
   protected Graph<V> invokerGraph;
   /**
    * The graph of the mathIT class {@link Graph} that is invoked
    * to be copied to the format compatible with this viewer.
    */
   protected edu.uci.ics.jung.graph.Graph<V,E> graph;
   /**
    * The visual component and renderers for the graph
    */
   protected Canvas<V,E> canvas;
   /**
    * The layout in which the graph is drawn.
    */
   protected Layout<V,E> layout;
   protected ScalingControl scaler;

   // --- GUI components: ---
   Container content;
   private JPanel controls;
   private JPanel zoomControls;
   private JPanel layoutChoice = new JPanel();
   private JComboBox jcb;
   //private JComboBox<Layout<V,E>> jcb;
   private javax.swing.JCheckBox edgeLabels;
   private JPanel edgePanel;
   private GraphZoomScrollPane gzsp;
   private JPanel modePanel;
   private JComboBox modeBox;
   private JPanel clusterControls;
   private JPanel buttonPanel;
   private JButton print;
   private JButton help;
   private JButton save;
   private JButton load;

   /**
    * Constructs a graph viewer from the specified graph
    * @param graph a graph of the class {@link Graph}
    */
   @SuppressWarnings("unchecked")
   public GraphViewer(Graph<V> graph) {
      this.invokerGraph = graph;
      if (graph.undirected) {
         //this.graph = new edu.uci.ics.jung.graph.UndirectedSparseGraph<V,E>();
         this.graph = new edu.uci.ics.jung.graph.UndirectedSparseGraph<>();
      } else {
         //this.graph = new edu.uci.ics.jung.graph.DirectedSparseGraph<V,E>();
         this.graph = new edu.uci.ics.jung.graph.DirectedSparseGraph<>();
      }
      for (int i = 0; i < graph.vertices.length; i++) {
          this.graph.addVertex(graph.vertices[i]);
      }

      // Collect edges of this graph and add them to the JUNG graph:
      HashSet<Edge<V>> edges = graph.collectEdges();
      for (Edge<V> e : edges) {
         this.graph.addEdge((E) e, e.getStartVertex(), e.getEndVertex());
      }
      initComponents();
   }
      
   /**
    * Constructs a graph viewer from the specified graph
    * @param graph a graph of the class 
    * <a href="http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/graph/Graph.html">edu.uci.ics.jung.graph.Graph</a>
    */
   public GraphViewer(edu.uci.ics.jung.graph.Graph<V,E> graph) {
      this.invokerGraph = null;
      this.graph = graph;
      initComponents();
   }
   
   /**
    * This method is called from within the constructor to initialize the frame.
    */
   @SuppressWarnings("unchecked")
   private void initComponents() {
      layout = new KKLayout<>(this.graph);
      //layout = new FRLayout<>(this.graph);
      //layout = new CircleLayout<>(this.graph);
      
      // Dimension preferredSize = new Dimension(840, 585); // old!
      Dimension preferredSize = new Dimension(900, 585);
      final VisualizationModel<V,E> visualizationModel =
              new DefaultVisualizationModel<>(layout, preferredSize);
      canvas = new Canvas<>(this, visualizationModel, preferredSize);

      final PredicatedParallelEdgeIndexFunction<V,E> eif = PredicatedParallelEdgeIndexFunction.getInstance();
      final Set<E> exclusions = new HashSet<>();
      eif.setPredicate(new Predicate<E>() {
         @Override
         public boolean evaluate(E e) {
            return exclusions.contains(e);
         }
      });

      /* Set the Nimbus look and feel */
      //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
      /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
       * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
       */
      try {
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException ex) {
         java.util.logging.Logger.getLogger(GraphViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(GraphViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(GraphViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(GraphViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }

      canvas.setBackground(Color.white);

      // --- Vertex configuration: ---
      canvas.getRenderContext().setVertexShapeTransformer(new GraphViewer.ClusterVertexShapeFunction());
      // ---- Vertex color: ----
      canvas.getRenderer().setVertexRenderer(
         new edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer<>(
            Color.yellow, Color.yellow,  // colors in normal state
        	   Color.white, Color.red,   // colors in picked state
        		canvas.getPickedVertexState(),
        		false
        ));
      //canvas.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<V>(canvas.getPickedVertexState(), Color.red, Color.yellow));

      pickActivated();

      // --- Vertex labels: -----
      canvas.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
      canvas.getRenderer().getVertexLabelRenderer().setPositioner(new BasicVertexLabelRenderer.InsidePositioner());
      canvas.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

      // add a listener for ToolTips
      canvas.setVertexToolTipTransformer(new ToStringLabeller<V>() {
         /* (non-Javadoc)
          * @see edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#getToolTipText(java.lang.Object)
          */
         @Override
         public String transform(V v) {
            if (v instanceof Graph) {
               return ((Graph<V>) v).getVertices().toString();
            }
            //return super.transform((V) v);
            return v.toString();
         }
      });

      // add a listener for ToolTips
      canvas.setEdgeToolTipTransformer(new ToStringLabeller<E>() {
         /* (non-Javadoc)
          * @see edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#getToolTipText(java.lang.Object)
          */
         @Override
         public String transform(E e) {
            return e.toString();
         }
      });

      // ---  Edge Labels: ---
      canvas.getRenderContext().setParallelEdgeIndexFunction(eif);
      canvas.getRenderContext().getEdgeLabelRenderer();

      /**
       * the regular graph mouse for the normal view
       */
      final DefaultModalGraphMouse<V,E> graphMouse = new DefaultModalGraphMouse<>();

      canvas.setGraphMouse(graphMouse);

      // --- Control Panel: ---------------
      content = getContentPane();
      gzsp = new GraphZoomScrollPane(canvas);
      content.add(gzsp);

      modeBox = graphMouse.getModeComboBox();
      modeBox.addItemListener(graphMouse.getModeListener());
      graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
      //graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
      modePanel = new JPanel();
      modePanel.setBorder(BorderFactory.createTitledBorder("Selection Mode"));
      modePanel.setLayout(new java.awt.GridLayout(3, 1));
      modePanel.add(modeBox);

      if (invokerGraph instanceof NetworkOfActivatables) {
         JButton next = new JButton("Next Activation Step");
         next.addActionListener((ActionEvent e) -> {
            nextActivationStep();
         });
         modePanel.add(next);
         JButton runAll = new JButton("Run Entire Activation");
         runAll.addActionListener((ActionEvent e) -> {
            runActivation();
         });
         modePanel.add(runAll);
      }

      scaler = new CrossoverScalingControl();

      JButton plus = new JButton("+");
      plus.addActionListener((ActionEvent e) -> {
         scaler.scale(canvas, 1.1f, canvas.getCenter());
      });
      JButton minus = new JButton("-");
      minus.addActionListener((ActionEvent e) -> {
         scaler.scale(canvas, 1 / 1.1f, canvas.getCenter());
      });
      
      buttonPanel = new JPanel();
      buttonPanel.setLayout(new java.awt.GridLayout(4, 1));
      print = new JButton("Print");
      print.addActionListener((ActionEvent e) -> {
         canvas.startPrinterJob();
      });
      buttonPanel.add(print);
      
      help = new JButton("Help");
      help.addActionListener((ActionEvent e) -> {
         JOptionPane.showMessageDialog((JComponent) e.getSource(), instructions, "Help", JOptionPane.PLAIN_MESSAGE);
      });
      buttonPanel.add(help);
      
      load = new JButton("Load CSV File");
      load.addActionListener((ActionEvent e) -> {
         //content.setCursor(new Cursor(Cursor.WAIT_CURSOR)); // funktioniert nicht...
         if (invokerGraph instanceof SocialNetwork) {
            SocialNetwork g = SocialNetwork.createNetworkFromCSVFile();
            if (g != null) {
               invokerGraph.shutDisplay();
               g.visualize();
            }
         } else if (invokerGraph instanceof WeightedGraph) {
            WeightedGraph g = WeightedGraph.createWeightedGraphFromCSVFile();
            if (g != null) {
               invokerGraph.shutDisplay();
               g.visualize();
            }
         } else {
            org.mathIT.graphs.Graph g = org.mathIT.graphs.Graph.createGraphFromCSVFile();
            if (g != null) {
               invokerGraph.shutDisplay();
               g.visualize();
            }
         }
         // content.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // funktioniert nicht...
      });
      buttonPanel.add(load);
     
      save = new JButton("Save as CSV");
      save.addActionListener((ActionEvent e) -> {
         if (invokerGraph instanceof SocialNetwork) {
            for (V v : graph.getVertices()) {
               Activatable a = (Activatable) v;
               if (canvas.getPickedVertexState().isPicked(v)) {
                  ((NetworkOfActivatables) invokerGraph).setActive(true);
                  a.setActive(true);
                  //} else {
                  //   a.setActive(false);
               }
            }
            ((SocialNetwork) invokerGraph).saveAsCSV();
         } else if (invokerGraph instanceof WeightedGraph) {
            ((WeightedGraph) invokerGraph).saveAsCSV();
         } else {
            invokerGraph.saveAsCSV();
         }
      });
      buttonPanel.add(save);
      
      Class<? extends Layout>[] combos = getCombos();
      jcb = new JComboBox(combos);
      // use a renderer to shorten the layout name presentation
      jcb.setRenderer(new DefaultListCellRenderer() {
         @Override
         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String valueString = value.toString();
            valueString = valueString.substring(valueString.lastIndexOf('.') + 1);
            return super.getListCellRendererComponent(list, valueString, index, isSelected,
                    cellHasFocus);
         }
      });
      jcb.addActionListener(new GraphViewer.LayoutChooser(jcb, canvas));
      //jcb.setSelectedItem(KKLayout.class);
      //jcb.setSelectedItem(FRLayout.class);
      //jcb.setSelectedItem(CircleLayout.class);
      jcb.setSelectedItem(layout.getClass());

      
      layoutChoice = new JPanel();
      layoutChoice.setBorder(BorderFactory.createTitledBorder("Graph Layout"));
      layoutChoice.setLayout(new java.awt.GridLayout(2, 1));
      layoutChoice.add(jcb);

      // Edge labels:
      edgePanel = new JPanel();
      edgeLabels = new javax.swing.JCheckBox("Edge Labels");
      edgePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));
      edgeLabels.addActionListener((java.awt.event.ActionEvent evt) -> {
         writeEdgeLabels();
      });
      edgePanel.add(edgeLabels);
      edgePanel.add(edgeLabels);
      layoutChoice.add(edgePanel);

      clusterControls = new JPanel(new GridLayout(3, 1));
      if (invokerGraph instanceof org.mathIT.graphs.Graph) {
         clusterControls.setBorder(BorderFactory.createTitledBorder("Clusters"));
         JButton detect = new JButton("Detect (Greedy)");
         detect.addActionListener((ActionEvent e) -> {
            detectClusters();
         });
         clusterControls.add(detect);
         
         JButton detectExactly = new JButton("Detect (Brute force)");
         detectExactly.addActionListener((ActionEvent e) -> {
            detectClustersExactly();
         });
         clusterControls.add(detectExactly);
         
         JButton computeHashimoto = new JButton("Hashimoto matrix");
         computeHashimoto.addActionListener((ActionEvent e) -> {
            org.mathIT.algebra.Matrix B = new org.mathIT.algebra.Matrix(invokerGraph.computeHashimoto());
            new org.mathIT.gui.MatrixAlgebra("Hashimoto matrix", B);
         });
         clusterControls.add(computeHashimoto);
      }
      
      
      // --- Build up Control Panel: ----
      controls = new JPanel();
      //controls.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
      zoomControls = new JPanel(new GridLayout(2, 1));
      zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
      zoomControls.add(plus);
      zoomControls.add(minus);
      controls.add(zoomControls);
      controls.add(layoutChoice);
      controls.add(modePanel);
      if (invokerGraph instanceof org.mathIT.graphs.Graph) {
         controls.add(clusterControls);
      }
      controls.add(buttonPanel);
      content.add(controls, BorderLayout.SOUTH);

      // Frame Properties:
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      pack();
      setLocationRelativeTo(null);
      //layout.setSize(canvas.getSize());
      //scaler.scale(canvas, 1.f, canvas.getCenter());
      setVisible(true);
   }

   /** Picks activated vertices in this graph.
    *  That means, if the vertices are instances of the interface
    *  <cod>{@link Activated Activated}</code>, the active one among them are
    *  marked as picked.
    */
   private void pickActivated() {
      if (!(invokerGraph.getVertex(0) instanceof Activatable)) return;
      // Pick activated actors in a social network:
      for (V v : graph.getVertices()) {
         if (((Activatable) v).isActive()) {
            canvas.getPickedVertexState().pick(v, true);               
         }
      }
   }

   /**
    * Returns the nodes activated by the specified active generation of nodes
    * in this network, after a single activation step. It in essence calls the
    * implemented method 
    * {@link NetworkOfActivatables#nextActivationStep(java.util.HashSet) nextActivationStep}
    * of the interface {@link NetworkOfActivatables NetworkOfActivatables}.
    * The current active generation for this step is determined from the picked 
    * vertices in this visualized graph.
    */
   @SuppressWarnings("unchecked")
   private void nextActivationStep() {
      if (!(invokerGraph.getVertex(0) instanceof Activatable)) return;
      HashSet<Activatable> activeGeneration = new HashSet<>();
      for (V v : graph.getVertices()) {
         Activatable a = (Activatable) v;
         if (canvas.getPickedVertexState().isPicked(v)) {
            a.setActive(true);
            activeGeneration.add(a);
         } else {
            a.setActive(false);
         }
      }
      //System.out.println("+++ activated before: " + activeGeneration);
      HashSet<Activatable> actives = 
         ((NetworkOfActivatables) invokerGraph).nextActivationStep(activeGeneration);
      //System.out.println("+++ activated after: " + actives);
      for (Activatable v : actives) {
         canvas.getPickedVertexState().pick((V) v, true);
      }
   }

   /**
    * Computes the nodes activated by the specified active generation of nodes
    * in this network, after a single activation step. It in essence calls the
    * implemented method 
    * {@link NetworkOfActivatables#nextActivationStep(java.util.HashSet) nextActivationStep}
    * of the interface {@link NetworkOfActivatables NetworkOfActivatables}.
    * The current active generation for this step is determined from the picked 
    * vertices in this visualized graph.
    */
   private void runActivation() {
      if (!(invokerGraph.getVertex(0) instanceof Activatable)) return;
      for (V v : graph.getVertices()) {
         Activatable a = (Activatable) v;
         if (canvas.getPickedVertexState().isPicked(v)) {
            //((NetworkOfActivatables) invokerGraph).setActive(true);
            a.setActive(true);
         } else {
            a.setActive(false);
         }
      }
      //System.out.println("+++ activated before: " + activeGeneration);
      //HashSet<Activatable> actives =
      //   ((NetworkOfActivatables<Activatable>) invokerGraph).runActivation(activeGeneration);
      //System.out.println("+++ activated after: " + actives);
      for (V v : graph.getVertices()) {
         if (((Activatable) v).isActive()) {
            canvas.getPickedVertexState().pick(v, true);
         }
      }
   }
   
   /**
    * Computes clusters for this graph. 
    */
   private void detectClusters() {
      ArrayList<OrderedSet<Integer>> list = invokerGraph.detectClusters().getClusters();
      // ---- Vertex color: ----
      canvas.getRenderer().setVertexRenderer(new VertexFillColor(list));
      canvas.repaint();
   }
   
   /**
    * Computes clusters for this graph. 
    */
   private void detectClustersExactly() {
      if (invokerGraph.getVertices().length > 12) {
         String time;
         switch (invokerGraph.getVertices().length) {
            case 13: time = " about a minute"; break;
            case 14: time = " about 10 minutes"; break;
            case 15: time = " some hours"; break;
            case 16: time = " about a day"; break;
            case 17: time = " about a week"; break;
            default: time = " more than several weeks"; break;
         }
         int click = JOptionPane.showConfirmDialog(
            this, 
            ""+invokerGraph.getVertices().length+" nodes will last" + time + "!\nContinue anyway?",
            "Continuation Dialog", 
            2
         );
         //System.out.println("### click="+click);
         if (click == 2) return;
         if (invokerGraph.getVertices().length > 17) {
            JOptionPane.showMessageDialog(this, "OK... But I stop nonetheless");
            return;
         }
      }
      long time = System.currentTimeMillis();
      ArrayList<OrderedSet<Integer>> list = invokerGraph.detectClustersExactly().getClusters();
      time = System.currentTimeMillis() - time;
      System.out.println("- Running time for brute force clustering: "+time/1000.+" sec");
      // ---- Vertex color: ----
      canvas.getRenderer().setVertexRenderer(new VertexFillColor(list));
      canvas.repaint();
   }
   
   /** Writes the edge lables.*/
   private void writeEdgeLabels() {
      Transformer<E, String> stringer;
      if (edgeLabels.isSelected()) {
         stringer = (E e) -> {
            if (e instanceof Edge) {
               return e.toString();
            } else {
               return graph.getEndpoints(e).toString();
            }
            //return e.toString();
         };
      } else {
         stringer = (E e) -> "";
      }
      canvas.getRenderContext().setEdgeLabelTransformer(stringer);
      canvas.repaint();
   }
   
   /*
   private void shutCanvas() {
      if (canvas != null) {
         canvas.setVisible(false);
         canvas = null;
      }
   }
   */

   /**
    * a demo class that will create a vertex shape that is either a polygon or
    * star. The number of sides corresponds to the number of vertices that were
    * collapsed into the vertex represented by this shape.
    *
    * @author Tom Nelson
    *
    * @param <V> the type of the vertices
    */
   class ClusterVertexShapeFunction<V> extends EllipseVertexShapeTransformer<V> {

      @SuppressWarnings("unchecked")
      ClusterVertexShapeFunction() {
         setSizeTransformer(new GraphViewer.ClusterVertexSizeFunction(20));
      }

      @Override
      public Shape transform(V v) {
         if (v instanceof Graph) {
            int size = ((edu.uci.ics.jung.graph.Graph) v).getVertexCount();
            if (size < 8) {
               int sides = Math.max(size, 3);
               return factory.getRegularPolygon(v, sides);
            } else {
               return factory.getRegularStar(v, size);
            }
         }
         return super.transform(v);
      }
   }

   /**
    * A demo class that will make vertices larger if they represent a collapsed
    * collection of original vertices
    *
    * @author Tom Nelson
    *
    * @param <V>
    */
   class ClusterVertexSizeFunction<V> implements Transformer<V, Integer> {

      int size;

      public ClusterVertexSizeFunction(Integer size) {
         this.size = size;
      }

      @Override
      public Integer transform(V v) {
         if (v instanceof Graph) {
            return 30;
         }
         return size;
      }
   }

   private class LayoutChooser implements ActionListener {

      private final JComboBox jcb;
      private final Canvas<V,E> canvas;

      private LayoutChooser(JComboBox jcb, Canvas<V,E> canvas) {
         super();
         this.jcb = jcb;
         this.canvas = canvas;
      }

      @Override
      @SuppressWarnings("unchecked")
      public void actionPerformed(ActionEvent arg0) {
         Object[] constructorArgs = {graph};

         Class<? extends Layout<V,E>> layoutC = (Class<? extends Layout<V,E>>) jcb.getSelectedItem();

         try {
            Constructor<? extends Layout<V,E>> constructor;
            Object o;
            Layout<V,E> l = null;
            
            if (
               layoutC.getName().equals("edu.uci.ics.jung.algorithms.layout.TreeLayout") ||
               layoutC.getName().equals("edu.uci.ics.jung.algorithms.layout.RadialTreeLayout") ||
               layoutC.getName().equals("edu.uci.ics.jung.algorithms.layout.BalloonLayout")
            ) {
               if (!(graph instanceof edu.uci.ics.jung.graph.DirectedSparseGraph)) {
                  javax.swing.JOptionPane.showMessageDialog(null, "Graph must be a directed tree-like graph!");
                  return;
               }
               constructor = layoutC.getConstructor(new Class[]{edu.uci.ics.jung.graph.Forest.class});
               o = constructor.newInstance(new Object[] {
                  new edu.uci.ics.jung.graph.DelegateForest((edu.uci.ics.jung.graph.DirectedGraph) graph)
               });
               l = (Layout<V,E>) o;
               l.setInitializer(canvas.getGraphLayout());
            } else if (layoutC.getName().equals("edu.uci.ics.jung.algorithms.layout.DAGLayout")){
               if (!(graph instanceof edu.uci.ics.jung.graph.DirectedSparseGraph)) {
                  javax.swing.JOptionPane.showMessageDialog(null, "Graph must be a tree or a forest!");
                  return;
               }
               constructor = layoutC.getConstructor(new Class[]{edu.uci.ics.jung.graph.Graph.class});
               o = constructor.newInstance(constructorArgs);
               l = (Layout<V,E>) o;
               l.setInitializer(canvas.getGraphLayout());
               l.setSize(canvas.getSize());
            } else {
               constructor = layoutC.getConstructor(new Class[]{edu.uci.ics.jung.graph.Graph.class});
               o = constructor.newInstance(constructorArgs);
               l = (Layout<V,E>) o;
               l.setInitializer(canvas.getGraphLayout());
               l.setSize(canvas.getSize());
            }
            
            layout = l;
            LayoutTransition<V,E> lt =
                    new LayoutTransition<>(canvas, canvas.getGraphLayout(), l);
            Animator animator = new Animator(lt);
            animator.start();
            canvas.getRenderContext().getMultiLayerTransformer().setToIdentity();
            canvas.repaint();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * This method yields a list of possible graph layouts, provided by the
    * JUNG project 
    * (<a href="http://jung.sourceforge.net" target="_new">http://jung.sourceforge.net</a>
    * in version 2.0.1 from 2010).
    * The graph layouts implement the interface
    * {@link edu.uci.ics.jung.algorithms.layout.Layout}.
    * @return an array of graph layouts
    */
   @SuppressWarnings("unchecked")
   protected Class<? extends Layout>[] getCombos() {
      List<Class<? extends Layout>> layouts = new ArrayList<>();
      layouts.add(KKLayout.class);
      layouts.add(FRLayout.class);
      layouts.add(FRLayout2.class);
      layouts.add(CircleLayout.class);
      layouts.add(SpringLayout.class);
      layouts.add(ISOMLayout.class);
      layouts.add(BalloonLayout.class);
      layouts.add(DAGLayout.class);
      layouts.add(TreeLayout.class);
      layouts.add(RadialTreeLayout.class);

      return layouts.toArray(new Class[0]);
   }

   private static final Color[] palette = {
      Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.MAGENTA,
      Color.ORANGE, Color.LIGHT_GRAY, Color.PINK, Color.BLACK
   };
   
   // inner class:
   private final class VertexFillColor extends edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer<V, E> {

      private final ArrayList<OrderedSet<Integer>> vertexSetList;

      VertexFillColor(ArrayList<OrderedSet<Integer>> vertexSetList) {
         super(palette[0], palette[1], true);
         this.vertexSetList = vertexSetList;
      }

      @Override
      protected void paintShapeForVertex(edu.uci.ics.jung.visualization.RenderContext<V, E> rc, V v, java.awt.Shape shape) {
         //System.out.println("+++ VertexFillColor: " + vertexSetList);
         edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator g = rc.getGraphicsContext();
         java.awt.Paint oldPaint = g.getPaint();
         java.awt.Rectangle r = shape.getBounds();
         float y2 = (float) r.getMaxY();
         int i = 0;
         for (int j = 0; j < vertexSetList.size(); j++) {
            if (vertexSetList.get(j).contains(v.getIndex())) {
               i = j;
               break;
            }
         }
         
         java.awt.Paint fillPaint = new java.awt.GradientPaint((float) r.getMinX(), (float) r.getMinY(),
                 Color.WHITE, //palette[i],
                 (float) r.getMinX(), y2,
                 palette[i],
                 false
         );
         g.setPaint(fillPaint);
         g.fill(shape);
         g.setPaint(oldPaint);
         
         java.awt.Paint drawPaint = rc.getVertexDrawPaintTransformer().transform(v);
         if (drawPaint != null) {
            g.setPaint(drawPaint);
         }
         java.awt.Stroke oldStroke = g.getStroke();
         java.awt.Stroke stroke = rc.getVertexStrokeTransformer().transform(v);
         if (stroke != null) {
            g.setStroke(stroke);
         }
         g.draw(shape);
         g.setPaint(oldPaint);
         g.setStroke(oldStroke);
      }
   }
}
