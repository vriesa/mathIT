/*
 * MatrixAlgebra.java - Class to display unary and binary matrix operations
 *
 * Copyright (C) 2016 Andreas de Vries
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
package org.mathIT.gui;

import org.mathIT.algebra.Matrix;
import static org.mathIT.graphs.Graph.SEPARATOR;
import static org.mathIT.util.Formats.*;

/** This class offers a simple GUI frame to display unary and binary matrix operations.
 *  Having been created by one of its constructors, it immediately shows the
 *  appropriate message frame.
 *  @author Andreas de Vries
 *  @version 1.0
 */
public class MatrixAlgebra extends javax.swing.JFrame {
	private static final long serialVersionUID = 8598673622635355958L;
	/** First matrix to be operated upon.*/
   private Matrix A;
   /** Second matrix to be operated upon. It is ignored in case of unary operators.*/
   private Matrix B;
   /** Resulting matrix of the current operation. It is null if the result is not a matrix (for instance the determinant).*/
   private Matrix resultMatrix;
   /** Currently offered matrix exponent. It is updated by clicking the exponent button.*/
   private int exponent;

   /**
    * Creates new form MatrixAlgebra
    */
   public MatrixAlgebra() {
      exponent = 2;
      initComponents();
   }
   
   /** Creates a new form MatrixAlgebra for unary operations on the matrix A.
    * @param A matrix to be operated upon
    */
   public MatrixAlgebra(Matrix A) {
      this("Matrix Algebra", A);
   }
   
   /** Creates a new form MatrixAlgebra for unary operations on the matrix A.
    *  @param title the title of this frame
    *  @param A matrix to be operated upon
    */
   public MatrixAlgebra(String title, Matrix A) {
      this.A = A;
      exponent = 2;
      
      inputPanel = new javax.swing.JPanel();
      matrixAInput = new javax.swing.JPanel();
      jScrollPaneA = new javax.swing.JScrollPane();
      matrixATable = new javax.swing.JTable();
      operatorInput = new javax.swing.JPanel();
      operationsLabel = new javax.swing.JLabel();
      detPanel = new javax.swing.JPanel();
      detButton = new javax.swing.JButton();
      powerPanel = new javax.swing.JPanel();
      powerDecrementPanel = new javax.swing.JPanel();
      powerButton = new javax.swing.JButton();
      powerDecrementButton = new javax.swing.JButton();
      controlPanel = new javax.swing.JPanel();
      loadFormatCheckBox = new javax.swing.JCheckBox();
      loadAButton = new javax.swing.JButton();
      emptyLabel1 = new javax.swing.JLabel();
      saveResultButton = new javax.swing.JButton();
      emptyLabel3 = new javax.swing.JLabel();
      jSeparator1 = new javax.swing.JSeparator();
      emptyLabel4 = new javax.swing.JLabel();
      cancelButton = new javax.swing.JButton();
      resultPanel = new javax.swing.JPanel();
      jScrollPaneResult = new javax.swing.JScrollPane();
      outputArea = new javax.swing.JLabel();
      outputArea.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      setTitle(title);

      inputPanel.setLayout(new java.awt.BorderLayout());

      //matrixAInput.setMaximumSize(new java.awt.Dimension(400, 400));

      jScrollPaneA.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Matrix A"));
      jScrollPaneA.setPreferredSize(new java.awt.Dimension(300, 300));

      matrixAInput.add(jScrollPaneA);

      inputPanel.add(matrixAInput, java.awt.BorderLayout.WEST);

      operationsLabel.setText("Operation");
      operatorInput.add(operationsLabel);
     
      operatorInput.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
      operatorInput.setMaximumSize(new java.awt.Dimension(400, 400));
      operatorInput.setLayout(new java.awt.GridLayout(5, 1));

      detButton.setText("det A");
      detButton.addActionListener((java.awt.event.ActionEvent evt) -> {
         detButtonActionPerformed(evt);
      });
      detPanel.add(detButton);

      operatorInput.add(detPanel);

      powerButton.setText("<html>A<sup>"+exponent+"</sup>");
      powerButton.addActionListener((java.awt.event.ActionEvent evt) -> {
         powerButtonActionPerformed(evt);
      });
      powerPanel.add(powerButton);

      operatorInput.add(powerPanel);

      powerDecrementButton.setText("<html>A<sup>0</sup>");
      powerDecrementButton.addActionListener((java.awt.event.ActionEvent evt) -> {
         powerDecrementButtonActionPerformed(evt);
      });
      powerDecrementPanel.add(powerDecrementButton);

      operatorInput.add(powerDecrementPanel);
 
      inputPanel.add(operatorInput, java.awt.BorderLayout.CENTER);

      getContentPane().add(inputPanel, java.awt.BorderLayout.CENTER);

      loadFormatCheckBox.setSelected(true);
      loadFormatCheckBox.setText("Load adjacency matrix");
      loadFormatCheckBox.setToolTipText("Load a CSV file representing the adjacency matrix of a graph");
      controlPanel.add(loadFormatCheckBox);

      loadAButton.setText("Load A from CSV");
      loadAButton.addActionListener((java.awt.event.ActionEvent evt) -> {
         loadAButtonActionPerformed(evt);
      });
      controlPanel.add(loadAButton);

      emptyLabel1.setText("   ");
      emptyLabel1.setEnabled(false);
      emptyLabel1.setFocusable(false);
      controlPanel.add(emptyLabel1);

      saveResultButton.setText("Save result to CSV");
      saveResultButton.addActionListener((java.awt.event.ActionEvent evt) -> {
         saveResultButtonActionPerformed(evt);
      });
      controlPanel.add(saveResultButton);

      emptyLabel3.setText("   ");
      controlPanel.add(emptyLabel3);
      controlPanel.add(jSeparator1);

      emptyLabel4.setText("   ");
      controlPanel.add(emptyLabel4);

      cancelButton.setText("Cancel");
      cancelButton.addActionListener((java.awt.event.ActionEvent evt) -> {
         cancelButtonActionPerformed(evt);
      });
      controlPanel.add(cancelButton);

      getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

      jScrollPaneResult.setViewportView(outputArea);
      jScrollPaneResult.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Result"));
      jScrollPaneResult.setPreferredSize(new java.awt.Dimension(300, 300));
      jScrollPaneResult.setRequestFocusEnabled(false);

      resultPanel.add(jScrollPaneResult);

      getContentPane().add(resultPanel, java.awt.BorderLayout.EAST);
      
      matrixATable = createTable(A);
      jScrollPaneA.setViewportView(matrixATable);
      
      // /*
      addComponentListener(new java.awt.event.ComponentAdapter() {
         @Override
         public void componentResized(java.awt.event.ComponentEvent e) {
            //System.out.println("### width: " + getWidth());
            int width = getWidth() / 2 - 50;
            java.awt.Dimension dimension = new java.awt.Dimension(width,width);
            jScrollPaneA.setPreferredSize(dimension);
            matrixAInput.revalidate();
            jScrollPaneResult.setPreferredSize(dimension);
            resultPanel.revalidate();
         }
      });
      // */
      
      pack();
      setVisible(true);
   }

   /** Creates a new form MatrixAlgebra for binary operations on the matrices A and B.
    * @param A first operand
    * @param B second operand
    */
   public MatrixAlgebra(Matrix A, Matrix B) {
      this.A = A;
      this.B = B;
      exponent = 2;
      
      initComponents();
      matrixATable = createTable(A);
      jScrollPaneA.setViewportView(matrixATable);
      matrixBTable = createTable(B);
      jScrollPaneB.setViewportView(matrixBTable);
      //pack();
      
      addComponentListener(new java.awt.event.ComponentAdapter() {
         @Override
         public void componentResized(java.awt.event.ComponentEvent e) {
            //System.out.println("### width: " + getWidth());
            int width = getWidth() / 3 - 50;
            java.awt.Dimension dimension = new java.awt.Dimension(width,width);
            jScrollPaneA.setPreferredSize(dimension);
            matrixAInput.revalidate();
            jScrollPaneB.setPreferredSize(dimension);
            matrixBInput.revalidate();
            jScrollPaneResult.setPreferredSize(dimension);
            resultPanel.revalidate();
         }
      });
      setVisible(true);
   }
   
   /** This method returns a JTable from the specified matrix and attaches it to the specified JScrollpane.
    *  @param A a matrix.
    */
   private javax.swing.JTable createTable(Matrix A) {
      String[][] data = new String[A.getRows()][A.getColumns()];
      int pts = 20; // font size of displayed digits to compute the column widths
      int[] maxDigits = new int[A.getColumns()];
      java.util.Arrays.fill(maxDigits, pts);
      
      for (int i=0; i<data.length; i++) {
         for (int j=0; j<data[0].length; j++) {
            data[i][j] = O_DOT_A10.format(A.getValue(i+1, j+1));
            if (maxDigits[i] < data[i][j].length() * pts) {
               maxDigits[i] = data[i][j].length() * pts;
            }
         }
      }
      
      String[] columnNames = new String[A.getColumns()];
      for (int i=0; i<columnNames.length; i++) {
         columnNames[i] = ""; // + (i+1);
      }
      
      javax.swing.JTable table = new javax.swing.JTable(data, columnNames);

      // centering data cells:
      javax.swing.table.DefaultTableCellRenderer center = new javax.swing.table.DefaultTableCellRenderer();
      center.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      for (int i=0; i<A.getColumns(); i++) {
         table.getColumnModel().getColumn(i).setPreferredWidth(maxDigits[i]);
         table.getColumnModel().getColumn(i).setCellRenderer(center);
      }
      
      // Centering table headers:
      ((javax.swing.table.DefaultTableCellRenderer) table.getTableHeader()
        .getDefaultRenderer())
        .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

      table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
      
      return table;
   }
   
   /** Computes the matrix from the specified table.
    * @param table the table to be computed
    */
   private Matrix computeMatrixFromTable(javax.swing.JTable table) {
      double[][] a = new double[table.getRowCount()][table.getColumnCount()];
      
      for (int i=0; i<a.length; i++) {
         for (int j=0; j<a[0].length; j++) {            
            a[i][j] = Double.parseDouble((String) table.getValueAt(i, j));
         }
      }
      
      return new Matrix(a);
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      inputPanel = new javax.swing.JPanel();
      matrixAInput = new javax.swing.JPanel();
      jScrollPaneA = new javax.swing.JScrollPane();
      matrixATable = new javax.swing.JTable();
      operatorInput = new javax.swing.JPanel();
      operationsLabel = new javax.swing.JLabel();
      plusPanel = new javax.swing.JPanel();
      plusButton = new javax.swing.JButton();
      minusPanel = new javax.swing.JPanel();
      minusButton = new javax.swing.JButton();
      timesPanel = new javax.swing.JPanel();
      timesButton = new javax.swing.JButton();
      detPanel = new javax.swing.JPanel();
      detButton = new javax.swing.JButton();
      powerPanel = new javax.swing.JPanel();
      powerButton = new javax.swing.JButton();
      powerDecrementPanel = new javax.swing.JPanel();
      powerDecrementButton = new javax.swing.JButton();
      matrixBInput = new javax.swing.JPanel();
      jScrollPaneB = new javax.swing.JScrollPane();
      matrixBTable = new javax.swing.JTable();
      controlPanel = new javax.swing.JPanel();
      loadFormatCheckBox = new javax.swing.JCheckBox();
      loadAButton = new javax.swing.JButton();
      emptyLabel1 = new javax.swing.JLabel();
      loadBButton = new javax.swing.JButton();
      emptyLabel2 = new javax.swing.JLabel();
      saveResultButton = new javax.swing.JButton();
      emptyLabel3 = new javax.swing.JLabel();
      jSeparator1 = new javax.swing.JSeparator();
      emptyLabel4 = new javax.swing.JLabel();
      cancelButton = new javax.swing.JButton();
      resultPanel = new javax.swing.JPanel();
      jScrollPaneResult = new javax.swing.JScrollPane();
      outputArea = new javax.swing.JLabel();

      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      setTitle("Matrix Algebra");

      inputPanel.setLayout(new java.awt.BorderLayout());

      matrixAInput.setMaximumSize(new java.awt.Dimension(400, 400));

      jScrollPaneA.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Matrix A"));
      jScrollPaneA.setPreferredSize(new java.awt.Dimension(300, 300));

      matrixATable.setModel(new javax.swing.table.DefaultTableModel(
         new Object [][] {
            {"1", null, null},
            {null, "1", null},
            {null, null, "1"}
         },
         new String [] {
            "1", "2", "3"
         }
      ) {
         /**
			 * 
			 */
			private static final long serialVersionUID = 4879457937330699855L;
			@SuppressWarnings("rawtypes")
			Class[] types = new Class [] {
            java.lang.String.class, java.lang.String.class, java.lang.String.class
         };

         @SuppressWarnings("rawtypes")
			public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
         }
      });
      matrixATable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
      jScrollPaneA.setViewportView(matrixATable);

      matrixAInput.add(jScrollPaneA);

      inputPanel.add(matrixAInput, java.awt.BorderLayout.WEST);

      operatorInput.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
      operatorInput.setMaximumSize(new java.awt.Dimension(400, 400));
      operatorInput.setLayout(new java.awt.GridLayout(9, 1));

      operationsLabel.setText("Operation");
      operatorInput.add(operationsLabel);

      plusButton.setText(" + ");
      plusButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            plusButtonActionPerformed(evt);
         }
      });
      plusPanel.add(plusButton);

      operatorInput.add(plusPanel);

      minusButton.setText(" - ");
      minusButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            minusButtonActionPerformed(evt);
         }
      });
      minusPanel.add(minusButton);

      operatorInput.add(minusPanel);

      timesButton.setText(" * ");
      timesButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            timesButtonActionPerformed(evt);
         }
      });
      timesPanel.add(timesButton);

      operatorInput.add(timesPanel);

      detButton.setText("det A");
      detButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            detButtonActionPerformed(evt);
         }
      });
      detPanel.add(detButton);

      operatorInput.add(detPanel);

      powerButton.setText("<html>A<sup>2</sup>");
      powerButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            powerButtonActionPerformed(evt);
         }
      });
      powerPanel.add(powerButton);

      operatorInput.add(powerPanel);

      powerDecrementButton.setText("<html>A<sup>0</sup>");
      powerDecrementButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            powerDecrementButtonActionPerformed(evt);
         }
      });
      powerDecrementPanel.add(powerDecrementButton);

      operatorInput.add(powerDecrementPanel);

      inputPanel.add(operatorInput, java.awt.BorderLayout.CENTER);

      jScrollPaneB.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Matrix B"));
      jScrollPaneB.setPreferredSize(new java.awt.Dimension(300, 300));
      jScrollPaneB.setRequestFocusEnabled(false);

      matrixBTable.setModel(new javax.swing.table.DefaultTableModel(
         new Object [][] {
            {"1", null, null},
            {null, "1", null},
            {null, null, "1"}
         },
         new String [] {
            "1", "2", "3"
         }
      ) {
 			private static final long serialVersionUID = -4944851734739683090L;
			@SuppressWarnings("rawtypes")
			Class[] types = new Class [] {
            java.lang.String.class, java.lang.String.class, java.lang.String.class
         };

         @SuppressWarnings("rawtypes")
			public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
         }
      });
      matrixBTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
      jScrollPaneB.setViewportView(matrixBTable);

      matrixBInput.add(jScrollPaneB);

      inputPanel.add(matrixBInput, java.awt.BorderLayout.EAST);

      getContentPane().add(inputPanel, java.awt.BorderLayout.CENTER);

      loadFormatCheckBox.setSelected(true);
      loadFormatCheckBox.setText("Load adjacency matrix");
      loadFormatCheckBox.setToolTipText("Load a CSV file representing the adjacency matrix of a graph");
      controlPanel.add(loadFormatCheckBox);

      loadAButton.setText("Load A from CSV");
      loadAButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            loadAButtonActionPerformed(evt);
         }
      });
      controlPanel.add(loadAButton);

      emptyLabel1.setText("   ");
      emptyLabel1.setEnabled(false);
      emptyLabel1.setFocusable(false);
      controlPanel.add(emptyLabel1);

      loadBButton.setText("Load B from CSV");
      loadBButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            loadBButtonActionPerformed(evt);
         }
      });
      controlPanel.add(loadBButton);

      emptyLabel2.setText("   ");
      controlPanel.add(emptyLabel2);

      saveResultButton.setText("Save result to CSV");
      saveResultButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveResultButtonActionPerformed(evt);
         }
      });
      controlPanel.add(saveResultButton);

      emptyLabel3.setText("   ");
      controlPanel.add(emptyLabel3);
      controlPanel.add(jSeparator1);

      emptyLabel4.setText("   ");
      controlPanel.add(emptyLabel4);

      cancelButton.setText("Cancel");
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelButtonActionPerformed(evt);
         }
      });
      controlPanel.add(cancelButton);

      getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

      jScrollPaneResult.setBorder(javax.swing.BorderFactory.createTitledBorder("Result"));
      jScrollPaneResult.setPreferredSize(new java.awt.Dimension(300, 300));

      outputArea.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      jScrollPaneResult.setViewportView(outputArea);

      resultPanel.add(jScrollPaneResult);

      getContentPane().add(resultPanel, java.awt.BorderLayout.EAST);

      pack();
   }// </editor-fold>//GEN-END:initComponents

   private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
      dispose();
   }//GEN-LAST:event_cancelButtonActionPerformed

   private void loadAButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAButtonActionPerformed
      Matrix tmp = loadFromCSV();      
      if (tmp == null) return;
      else A = tmp;
      matrixATable = createTable(A);
      jScrollPaneA.setViewportView(matrixATable);
      exponent = 1;
      powerButton.setText("<html>A<sup>" + (++exponent) + "</sup>");
      powerDecrementButton.setText("<html>A<sup>" + (exponent - 2) + "</sup>");
      outputArea.setText("");
      jScrollPaneResult.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Result"));
      jScrollPaneResult.repaint();
   }//GEN-LAST:event_loadAButtonActionPerformed

   private void loadBButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBButtonActionPerformed
      Matrix tmp = loadFromCSV();      
      if (tmp == null) return;
      else B = tmp;
      matrixBTable = createTable(B);
      jScrollPaneB.setViewportView(matrixBTable);
      exponent = 1;
      powerButton.setText("<html>A<sup>" + (++exponent) + "</sup>");
      powerDecrementButton.setText("<html>A<sup>" + (exponent - 2) + "</sup>");
      outputArea.setText("");
      jScrollPaneResult.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("Result"));
      jScrollPaneResult.repaint();
   }//GEN-LAST:event_loadBButtonActionPerformed

   private void saveResultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveResultButtonActionPerformed
      if (resultMatrix != null) {
         saveAsCSV(resultMatrix);
      } else {
         saveAsCSV(A);
      }
   }//GEN-LAST:event_saveResultButtonActionPerformed

   private void timesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timesButtonActionPerformed
      A = computeMatrixFromTable(matrixATable);
      B = computeMatrixFromTable(matrixBTable);
      resultMatrix = A.times(B);
      outputArea.setText("<html>" + resultMatrix.toHTML());
      jScrollPaneResult.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("A*B:"));
      jScrollPaneResult.repaint();
   }//GEN-LAST:event_timesButtonActionPerformed

   private void plusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusButtonActionPerformed
      A = computeMatrixFromTable(matrixATable);
      B = computeMatrixFromTable(matrixBTable);
      resultMatrix = A.add(B);
      outputArea.setText("<html>" + resultMatrix.toHTML());
      jScrollPaneResult.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("A+B:"));
      jScrollPaneResult.repaint();
   }//GEN-LAST:event_plusButtonActionPerformed

   private void minusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minusButtonActionPerformed
      A = computeMatrixFromTable(matrixATable);
      B = computeMatrixFromTable(matrixBTable);
      resultMatrix = A.minus(B);
      outputArea.setText("<html>" + resultMatrix.toHTML());
      jScrollPaneResult.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("A-B:"));
      jScrollPaneResult.repaint();
   }//GEN-LAST:event_minusButtonActionPerformed

   private void detButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detButtonActionPerformed
      // TODO add your handling code here:
      javax.swing.JOptionPane.showMessageDialog(null, "det A = " + O_DOT_A10.format(A.det()));
   }//GEN-LAST:event_detButtonActionPerformed

   private void powerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_powerButtonActionPerformed
      A = computeMatrixFromTable(matrixATable);

      if (exponent < 0 && Math.abs(A.det()) < Matrix.EPSILON) {
         javax.swing.JOptionPane.showMessageDialog(null, "Negative power of a non-invertible matrix does not exist!");
         return;
      }
      resultMatrix = A.pow(exponent);

      outputArea.setText("<html>" + resultMatrix.toHTML());
      jScrollPaneResult.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("<html>A<sup>"+exponent+"</sup>:"));
      jScrollPaneResult.repaint();
      powerButton.setText("<html>A<sup>" + (++exponent) + "</sup>");
      powerDecrementButton.setText("<html>A<sup>" + (exponent - 2) + "</sup>");
   }//GEN-LAST:event_powerButtonActionPerformed

   private void powerDecrementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_powerDecrementButtonActionPerformed
      A = computeMatrixFromTable(matrixATable);
      if (exponent < 2 && Math.abs(A.det()) < Matrix.EPSILON) {
         javax.swing.JOptionPane.showMessageDialog(null, "Negative power of a non-invertible matrix does not exist!");
         return;
      }
      
      resultMatrix = A.pow(exponent - 2);

      outputArea.setText("<html>" + resultMatrix.toHTML());
      jScrollPaneResult.setViewportBorder(javax.swing.BorderFactory.createTitledBorder("<html>A<sup>"+(exponent-2)+"</sup>:"));
      jScrollPaneResult.repaint();
      powerButton.setText("<html>A<sup>" + (--exponent) + "</sup>");
      powerDecrementButton.setText("<html>A<sup>" + (exponent - 2) + "</sup>");
   }//GEN-LAST:event_powerDecrementButtonActionPerformed

   /** Loads a matrix file from the specified CSV file.*/
   private Matrix loadFromCSV() {
      final String separator = Character.toString(SEPARATOR);
      StringBuilder text = org.mathIT.util.Files.loadTextFile();
      if (text == null) return null;
      
      int pos = text.indexOf(separator), pre, i, j;
      if (pos < 0) { // || text.indexOf(separator) > text.indexOf("\n")) {
         throw new IllegalArgumentException("No valid CSV format!");
      }
      
      // Determine numbers of rows and columns:
      int rows, cols;
      String[] names;
      if (loadFormatCheckBox.isSelected()) { // adjacency matrix
         //boolean undirected = "undirected".equals(text.substring(0, pos).trim());
      
         // Determine the names and the number of columns:
         pre = pos + 1;
         pos = text.indexOf("\n", pre);
         names = text.substring(pre, pos).split(separator);            
         rows = names.length;
         cols = rows;
         
         // Ignore line beginning with "threshold" for networks of activatables:
         if ("threshold".equals(text.substring(pos+1, text.indexOf(separator, pos+1)))) {
            pre = text.indexOf(separator, pos+1) + 1;
            pos = text.indexOf("\n", pre);
         }
      
         // Ignore line beginning with "active" for networks of activatables:
         if ("active".equals(text.substring(pos+1, text.indexOf(separator, pos+1)))) {
            pre = text.indexOf(separator, pos+1) + 1;
            pos = text.indexOf("\n", pre);
         }
      } else { // for a matrix file, the number of rows is given in the first data cell
         rows = Integer.parseInt(text.substring(0, pos));
         pre = pos + 1;
         pos = text.indexOf("\n", pre);
         names = text.substring(pre, pos).split(separator);
         cols = names.length;         
      }
          
      // Determine matrix:
      double[][] matrix = new double[rows][cols];
      i = 0;
      pre = text.indexOf(separator, pos) + 1; // first row contains vertex name
      pos = text.indexOf("\n", pre);
      String[] number;
      while (pos > 0 && pre > 0) {
         number = text.substring(pre, pos).split(separator, -1);
         for (j = 0; j < matrix.length; j++) {
            if (number[j].equals("")) number[j] = "0";
            matrix[i][j] = Double.parseDouble(number[j]);
         }
         i++;
         pre = text.indexOf(separator, pos) + 1; // first row contains vertex name
         pos = text.indexOf("\n", pre);
      }
      return new Matrix(matrix);
   }
   
   /** Save the specified matrix as CSV file.*/
   private void saveAsCSV(Matrix matrix) {
      int i, j;
      StringBuilder csv = new StringBuilder();
      
      // Control row, format: number of rows, each column name as its number
      csv.append(matrix.getRows());
      for (i = 1; i <= matrix.getColumns(); i++) {
         csv.append(SEPARATOR);
         csv.append(i);
      }
      csv.append('\n');  // new line
      
      // The matrix:
      for (i = 1; i <= matrix.getRows(); i++) {
         for (j = 1; j <= matrix.getColumns(); j++) {
            csv.append(SEPARATOR);
            csv.append(O_DOT_A10.format(matrix.getValue(i,j)));
            /*
            if (matrix.getValue(j, j) == 0) {
               csv.append("");
            } else {
               csv.append(O_DOT_A10.format(matrix.getValue(i,j)));
            }
            */
         }
         csv.append('\n');  // new line
      }
      org.mathIT.util.Files.save("matrix.csv", csv);
   }
   
   /**
    * for test purposes ...
    */
   /*
   public static void main(String args[]) {
      try {
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException ex) {
         java.util.logging.Logger.getLogger(MatrixAlgebra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(MatrixAlgebra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(MatrixAlgebra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(MatrixAlgebra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }
        //</editor-fold>

      // Create and display the form
      java.awt.EventQueue.invokeLater(() -> {
         // Haus-vom-Nikolaus example: ---
         Matrix A = new Matrix(new double[][] {
            //1  2  3  4  5
            { 0, 1, 1, 0, 0}, //  1
            { 0, 0, 1, 1, 0}, //  2
            { 0, 0, 0, 1, 0}, //  3
            { 1, 0, 0, 0, 1}, //  4
            { 0, 1, 0, 0, 0}, //  5
         });
         new MatrixAlgebra(A);
      });
   }
   // */

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton cancelButton;
   private javax.swing.JPanel controlPanel;
   private javax.swing.JButton detButton;
   private javax.swing.JPanel detPanel;
   private javax.swing.JLabel emptyLabel1;
   private javax.swing.JLabel emptyLabel2;
   private javax.swing.JLabel emptyLabel3;
   private javax.swing.JLabel emptyLabel4;
   private javax.swing.JPanel inputPanel;
   private javax.swing.JScrollPane jScrollPaneA;
   private javax.swing.JScrollPane jScrollPaneB;
   private javax.swing.JScrollPane jScrollPaneResult;
   private javax.swing.JSeparator jSeparator1;
   private javax.swing.JButton loadAButton;
   private javax.swing.JButton loadBButton;
   private javax.swing.JCheckBox loadFormatCheckBox;
   private javax.swing.JPanel matrixAInput;
   private javax.swing.JTable matrixATable;
   private javax.swing.JPanel matrixBInput;
   private javax.swing.JTable matrixBTable;
   private javax.swing.JButton minusButton;
   private javax.swing.JPanel minusPanel;
   private javax.swing.JLabel operationsLabel;
   private javax.swing.JPanel operatorInput;
   private javax.swing.JLabel outputArea;
   private javax.swing.JButton plusButton;
   private javax.swing.JPanel plusPanel;
   private javax.swing.JButton powerButton;
   private javax.swing.JButton powerDecrementButton;
   private javax.swing.JPanel powerDecrementPanel;
   private javax.swing.JPanel powerPanel;
   private javax.swing.JPanel resultPanel;
   private javax.swing.JButton saveResultButton;
   private javax.swing.JButton timesButton;
   private javax.swing.JPanel timesPanel;
   // End of variables declaration//GEN-END:variables
}
