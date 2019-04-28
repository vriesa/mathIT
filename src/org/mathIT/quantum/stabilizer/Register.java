/*
 * Register.java - Class representing a quantum register
 *
 * Copyright (C) 2004-2012 Andreas de Vries
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
package org.mathIT.quantum.stabilizer;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import static org.mathIT.numbers.Numbers.*;
import org.mathIT.util.FunctionParser;
/**
 *  This class represents the states of a quantum register.
 *  In general, a register state is given uniquely by a vector
 *  <b><i>&#x03B1;</i></b> &#x2208; <span style="font-size:large;">&#x2102;</span><sup><i>q</i>-1</sup>
 *  with its components 
 *  <i>&#x03B1;</i><sub>0</sub>,
 *  <i>&#x03B1;</i><sub>1</sub>,
 *  ...,
 *  <i>&#x03B1;</i><sub><i>q</i>-1</sub>
 *  with respect to the  
 *  <i>q</i> = 2<sup><i>n</i></sup> basis states
 *  |0&gt;, |1&gt;, ..., |<i>q</i>-1&gt;,
 *  the <i>computational basis</i>.
 *  In other words,
 *  <p style="text-align:center">
 *    <b><i>&#x03B1;</i></b> 
 *     = (<i>&#x03B1;</i><sub>0</sub>,
 *        <i>&#x03B1;</i><sub>1</sub>,
 *        ...,
 *        <i>&#x03B1;</i><sub><i>q</i>-1</sub>)
 *     = <i>&#x03B1;</i><sub>0</sub> |0&gt;
 *     + <i>&#x03B1;</i><sub>1</sub> |1&gt;
 *     + ...
 *     + <i>&#x03B1;</i><sub><i>q</i>-1</sub> |<i>q</i>-1&gt;.
 *  </p>
 *  A qubit register as an object of this class then is given by the two
 *  arrays <code>real[]</code> and <code>imaginary</code> where 
 *  <code>real</code>[<i>j</i>] = Re <i>&#x03B1;<sub>j</sub></i>
 *  and 
 *  <code>imaginary</code>[<i>j</i>] = Im <i>&#x03B1;<sub>j</sub></i>
 *  for <i>j</i> = 0, 1, ..., <i>q</i>-1.
 *  @author  Andreas de Vries
 *  @version 1.0
 */
public class Register {
   /** The accuracy up to which calculations are done. Its actual value is {@value}.*/
   public final static double ACCURACY = 1e-12; //Double.MIN_VALUE;
   /** The number of qubits of this register.*/
   public int size;
   /** Indicates whether the current state is a stabilizer state.*/
   private boolean isStabilizerState;
   /** The graph register state representing this quantum register as long as
    *  it is a stabilizer state according to the Gottesman-Knill theorem.
    */
   private GraphRegister graphState;
   /** Array containing the real parts of the qubit state components of this register.
    *  It is null as long as this register is a stabilizer state.
    */
   private double[] real;
   /** Array containing the imaginary parts of the qubit state components of this register.
    *  It is null as long as this register is a stabilizer state.
    */
   private double[] imaginary;
   /** Map of a list containing the numbers of states being entangled with each other.*/
   private HashMap<Integer, ArrayList<Integer>> entanglement;
   
   /**
    *  Creates a register of <i>n</i> qubits, initialized to the state |0&gt;.
    *  A register of size <i>n</i> enables a total of <i>q</i> = 2<sup><i>n</i></sup> 
    *  quantum states as the computational basis.
    *  @param size the number of qubits this register consists of
    */
   public Register(int size) {
      this(size,true);
      //this.size = size;
      //isStabilizerState = true;
      
      //graphState = new GraphRegister(size);
   }
   
   /**
    *  Creates a register of <i>n</i> qubits, initialized to the state |0&gt;.
    *  A register of size <i>n</i> enables a total of <i>q</i> = 2<sup><i>n</i></sup> 
    *  quantum states as the computational basis.
    *  If the specified argument <code>asGraphRegister</code> is set <code>true</code>,
    *  this register is represented efficiently as a graph state consisting of
    *  <i>n</i> vertices representing the <i>n</i> qubits.
    *  If <code>asGraphRegister</code> is set <code>false</code>, this register
    *  is represented by two <code>double</code> arrays of size 2<sup><i>n</i></sup>
    *  storing the complex amplitudes.
    *  @param size the number of qubits this register consists of
    *  @param asGraphRegister indicates whether this register shall be represented
    *  as a graph state
    */
   public Register(int size, boolean asGraphRegister) {

      this.size = size;
      isStabilizerState = asGraphRegister;
      
      if (isStabilizerState && size > 0) {
         graphState = new GraphRegister(size);
      } else {
         int q;
         if (size > 0) {
            q = power2(size);
         } else {
            q = 0;
         }
         
         real = new double[q];
         imaginary = new double[q];
         
         if (size > 0) {
            real[0] = 1;
         }
      }
   }
   
   /**
    * Returns the size of this quantum register. I.e., the number of its qubits.
    * @return the size of this quantum register
    */
   public int getSize() {
      return size;
   }
   
   /** 
    * Returns the array containing the real parts of the qubit state components 
    * of this register.
    * @param real array of the real parts of this quantum register
    * @throws IllegalArgumentException if <code>real.length</code> is not equal 
    * to 2<sup><i>n</i></sup> where <i>n</i> is the size of this register
    */
   public void setReal(double[] real) {
      if (real.length != this.real.length) {
         throw new IllegalArgumentException(
            "Wrong register size " + real.length + " (" + this.real.length + " required)"
         );    
      }
      this.real = real;
   }
   
   /** 
    * Returns the array containing the real parts of the qubit state components 
    * of this register.
    * @return array of the real parts of this quantum register
    */
   public double[] getReal() {
      if (isStabilizerState) {
         return graphState.getRegister().getReal();
      }
      return real;
   }
   
   /** 
    * Returns the array containing the imaginary parts of the qubit state components 
    * of this register.
    * @param imaginary array of the imaginary parts of this quantum register
    * @throws IllegalArgumentException if <code>imaginary.length</code> is not equal 
    * to 2<sup><i>n</i></sup> where <i>n</i> is the size of this register
    */
   public void setImaginary(double[] imaginary) {
      if (imaginary.length != this.real.length) {
         throw new IllegalArgumentException(
            "Wrong register size " + imaginary.length + " (" + this.real.length + " required)"
         );
      }
      this.imaginary = imaginary;
   }

   /** 
    * Returns the array containing the imaginary parts of the qubit state components 
    * of this register.
    * @return array of the imaginary parts of this quantum register
    */
   public double[] getImaginary() {
      if (isStabilizerState) {
         return graphState.getRegister().getImaginary();
      }
      return imaginary;
   }

   /** 
    * Indicates whether the current state of this quantum register is a stabilizer
    * state. Stabilizer states are a certain class of quantum register states
    * which can be efficiently simulated by a classical computer according to
    * the Gottesman-Knill theorem.
    * @return <code>true</code> if and only if this quantum register is in a stabilizer state
    */
   public boolean isStabilizerState() {
      return isStabilizerState;
   }
   
   /** Returns the graph register state representing this quantum register as long as
    *  it is a stabilizer state according to the Gottesman-Knill theorem.
    *  If this register is not a stabilizer state, the graph register state
    *  is <code>null</code>.
    *  @return the graph register state representing this quantum register
    */
    public GraphRegister getGraphState() {
       return graphState;
    }
   
   /** Returns a map of a list containing the numbers of states being entangled with each other.
    * @return map of a list containing the numbers of states being entangled with each other
    */
   public HashMap<Integer, ArrayList<Integer>> getEntanglement() {
      return entanglement;
   }
   
   /** 
    *  Hadamard transformation of the <i>j</i>-th qubit. 
    *  Note that 1 &#x2264; <i>j</i> &#x2264; <i>n</i>, where <i>n</i> is the 
    *  quantum register size. The register is transformed directly.
    *  @param j the number of the qubit (1 &#x2264; <i>j</i> &#x2264; qubit size). 
    */
   public void hadamard( int j ) {
      if (isStabilizerState) {
         graphState.hadamard(j-1);
         return;
      }
      double realTmp, imaginaryTmp;
      
      for ( int k = 0; k < power2( size ); k += power2(j) ) {
         for ( int l = 0; l < power2(j-1); l++ ) {
            int i0 = k + l;
            int i1 = k + l + power2(j-1);
            
            realTmp      = real[ i0 ];
            imaginaryTmp = imaginary[ i0 ];
            
            real[ i0 ] = ( realTmp + real[ i1 ] ) / SQRT2;
            imaginary[ i0 ] = ( imaginaryTmp + imaginary[ i1 ] ) / SQRT2;
            real[ i1 ] = ( realTmp - real[ i1 ] ) / SQRT2;
            imaginary[ i1 ] = ( imaginaryTmp - imaginary[ i1 ] ) / SQRT2;
         }
      }
   }
   
   /**
    *  Applies the c-NOT gate to this register, where <i>j</i> is the control 
    *  qubit and <i>k</i> the target qubit.
    *  @param j the control qubit
    *  @param k the target qubit
    */
   public void cNOT(int j, int k) {
      if (isStabilizerState) {
         graphState.cNOT(j-1, k-1);
         return;
      }
      double tmp;
      
      /* In the following loop, all target qubit indices it which 
         have to be exchanged are determined and stored to a binary digit.*/
      HashSet<Integer> exchange = new HashSet<>();
      for (int t = 0; t < power2(size); t++) {
         // if the bit (j-1) is not met by t, then go on...
         if ( (t & power2(j-1)) <= 0 ) {
            continue;
         }
         
         if (!exchange.contains(t - power2(k-1))) {
            exchange.add(t);
         }
      }
      
      // exchange the stored indices:
      int t2;
      for ( int t : exchange) {
         t2 = (t + power2(k-1) < power2(size)) ? t + power2(k-1) : t - power2(k-1);
         tmp = real[t];
         real[t]  = real[t2];
         real[t2] = tmp;

         tmp = imaginary[t];
         imaginary[t]  =  imaginary[t2];
         imaginary[t2] =  tmp;
      }
   }
   
   /** 
    *  Applies the Pauli-X gate to the <i>j</i>-th qubit; on a classical bit, the
    *  X gate acts equally to a a classical NOT.
    *  Note that 1 &#x2264; <i>j</i> &#x2264; <i>n</i>, where <i>n</i> is the 
    *  quantum register size. The register is transformed directly.
    *  @param j the number of the qubit (1 &#x2264; <i>j</i> &#x2264; qubit size). 
    */
   public void xPauli( int j ) {
      if (isStabilizerState) {
         graphState.xPauli(j-1);
         return;
      }
      double realTmp, imaginaryTmp;
      
      for ( int k = 0; k < power2( size ); k += power2(j) ) {
         for ( int l = 0; l < power2(j-1); l++ ) {
            int i0 = k + l;
            int i1 = k + l + power2(j-1);            
            
            realTmp      = real[ i0 ];
            imaginaryTmp = imaginary[ i0 ];
            
            real[ i0 ]      = real[ i1 ];
            imaginary[ i0 ] = imaginary[ i1 ];
            real[ i1 ]      = realTmp;
            imaginary[ i1 ] = imaginaryTmp;
         }
      }
   }
   
   /** 
    *  Applies the Pauli-Y gate to the <i>j</i>-th qubit.
    *  Note that 1 &#x2264; <i>j</i> &#x2264; <i>n</i>, where <i>n</i> is the 
    *  quantum register size. The register is transformed directly.
    *  @param j the number of the qubit (1 &#x2264; <i>j</i> &#x2264; qubit size). 
    */
   public void yPauli( int j ) {
      if (isStabilizerState) {
         graphState.yPauli(j-1);
         return;
      }
      double realTmp, imaginaryTmp;
      
      for ( int k = 0; k < power2( size ); k += power2(j) ) {
         for ( int l = 0; l < power2(j-1); l++ ) {
            int i0 = k + l;
            int i1 = k + l + power2(j-1);            
            
            realTmp      = real[ i0 ];
            imaginaryTmp = imaginary[ i0 ];
         
            real[ i0 ]      = imaginary[ i1 ];
            imaginary[ i0 ] = - real[ i1 ];
            real[ i1 ]      = - imaginaryTmp;
            imaginary[ i1 ] = realTmp;
         }
      }
   }
   
   /** 
    *  Applies the Pauli-Z gate to the <i>j</i>-th qubit.
    *  Note that 1 &#x2264; <i>j</i> &#x2264; <i>n</i>, where <i>n</i> is the 
    *  quantum register size. The register is transformed directly.
    *  @param j the number of the qubit (1 &#x2264; <i>j</i> &#x2264; qubit size). 
    */
   public void zPauli( int j ) {
      if (isStabilizerState) {
         graphState.zPauli(j-1);
         return;
      }
      for ( int k = 0; k < power2( size ); k += power2(j) ) {
         for ( int l = 0; l < power2(j-1); l++ ) {
            int i1 = k + l + power2(j-1);            
            
            real[ i1 ]      *= -1;
            imaginary[ i1 ] *= -1;
         }
      }
   }
   
   /** 
    *  Applies the <i>S</i> gate to the <i>j</i>-th qubit.
    *  Note that 1 &#x2264; <i>j</i> &#x2264; <i>n</i>, where <i>n</i> is the 
    *  quantum register size. The register is transformed directly.
    *  @param j the number of the qubit (1 &#x2264; <i>j</i> &#x2264; qubit size). 
    */
   public void sGate( int j ) {
      if (isStabilizerState) {
         graphState.sGate(j-1);
         return;
      }
      
      for ( int k = 0; k < power2( size ); k += power2(j) ) {
         for ( int l = 0; l < power2(j-1); l++ ) {
            int i1 = k + l + power2(j-1);            
            
            double tmp = real[ i1 ];
            real[ i1 ]      = - imaginary[ i1 ];
            imaginary[ i1 ] = tmp;
         }
      }
   }
   
   /** 
    *  Applies the inverse <i>S</i> gate to the <i>j</i>-th qubit.
    *  Note that 1 &#x2264; <i>j</i> &#x2264; <i>n</i>, where <i>n</i> is the 
    *  quantum register size. The register is transformed directly.
    *  @param j the number of the qubit (1 &#x2264; <i>j</i> &#x2264; qubit size). 
    */
   public void inverseSGate( int j ) {
      if (isStabilizerState) {
         graphState.inverseSGate(j-1);
         return;
      }
      
      for ( int k = 0; k < power2( size ); k += power2(j) ) {
         for ( int l = 0; l < power2(j-1); l++ ) {
            int i1 = k + l + power2(j-1);            
            
            double tmp = real[ i1 ];
            real[ i1 ]      = imaginary[ i1 ];
            imaginary[ i1 ] = - tmp;
         }
      }
   }
   
   /** 
    *  Applies the <i>T</i> gate to the <i>j</i>-th qubit.
    *  Note that 1 &#x2264; <i>j</i> &#x2264; <i>n</i>, where <i>n</i> is the 
    *  quantum register size. The register is transformed directly.
    *  @param j the number of the qubit (1 &#x2264; <i>j</i> &#x2264; qubit size). 
    */
   public void tGate( int j ) {
      if (isStabilizerState) {
         isStabilizerState = false;
         Register newRegister = graphState.getRegister();
         real      = newRegister.real;
         imaginary = newRegister.imaginary;
         graphState = null;
      }
      
      for ( int k = 0; k < power2( size ); k += power2(j) ) {
         for ( int l = 0; l < power2(j-1); l++ ) {
            int i1 = k + l + power2(j-1);            
            
            double tmp = real[ i1 ];
            real[ i1 ]      = (tmp - imaginary[ i1 ]) / sqrt(2);
            imaginary[ i1 ] = (imaginary[ i1 ] + tmp) / sqrt(2);
         }
      }
   }
   
   /** 
    *  Applies the inverse <i>T</i> gate to the <i>j</i>-th qubit.
    *  Note that 1 &#x2264; <i>j</i> &#x2264; <i>n</i>, where <i>n</i> is the 
    *  quantum register size. The register is transformed directly.
    *  @param j the number of the qubit (1 &#x2264; <i>j</i> &#x2264; qubit size). 
    */
   public void inverseTGate( int j ) {
      if (isStabilizerState) {
         isStabilizerState = false;
         Register newRegister = graphState.getRegister();
         real      = newRegister.real;
         imaginary = newRegister.imaginary;
         graphState = null;
      }
      
      for ( int k = 0; k < power2( size ); k += power2(j) ) {
         for ( int l = 0; l < power2(j-1); l++ ) {
            int i1 = k + l + power2(j-1);            
            
            double tmp = real[ i1 ];
            real[ i1 ]      = (tmp + imaginary[ i1 ]) / sqrt(2);
            imaginary[ i1 ] = (imaginary[ i1 ] - tmp) / sqrt(2);
         }
      }
   }
   
   /** 
    *  Applies the &#x221A;X, or &#x221A;NOT gate to the <i>j</i>-th qubit.
    *  Note that 1 &#x2264; <i>j</i> &#x2264; <i>n</i>, where <i>n</i> is the 
    *  quantum register size. The register is transformed directly.
    *  @param j the number of the qubit (1 &#x2264; <i>j</i> &#x2264; qubit size). 
    */
   public void sqrtX( int j ) {
      if (isStabilizerState) {
         isStabilizerState = false;
         Register newRegister = graphState.getRegister();
         real      = newRegister.real;
         imaginary = newRegister.imaginary;
         graphState = null;
      }
      
      double x0, y0, x1, y1;
      
      for ( int k = 0; k < power2( size ); k += power2(j) ) {
         for ( int l = 0; l < power2(j-1); l++ ) {
            int i0 = k + l;
            int i1 = k + l + power2(j-1);            
            
            x0 = real[ i0 ];
            y0 = imaginary[ i0 ];
            x1 = real[ i1 ];
            y1 = imaginary[ i1 ];
            
            real[ i0 ]      = (  x0 + x1 - y0 + y1) / 2.;
            real[ i1 ]      = (  x0 + x1 + y0 - y1) / 2.;
            imaginary[ i0 ] = (  x0 - x1 + y0 + y1) / 2.;
            imaginary[ i1 ] = (- x0 + x1 + y0 + y1) / 2.;
         }
      }
   }
   
   /** 
    *  Applies the inverse &#x221A;X, or &#x221A;NOT gate to the <i>j</i>-th qubit.
    *  We have &#x221A;X<sup>-1</sup> = &#x221A;X<sup>3</sup>.
    *  Note that 1 &#x2264; <i>j</i> &#x2264; <i>n</i>, where <i>n</i> is the 
    *  quantum register size. The register is transformed directly.
    *  @param j the number of the qubit (1 &#x2264; <i>j</i> &#x2264; qubit size). 
    */
   public void inverseSqrtX( int j ) {
      if (isStabilizerState) {
         isStabilizerState = false;
         Register newRegister = graphState.getRegister();
         real      = newRegister.real;
         imaginary = newRegister.imaginary;
         graphState = null;
      }
      
      double x0, y0, x1, y1;
      
      for ( int k = 0; k < power2( size ); k += power2(j) ) {
         for ( int l = 0; l < power2(j-1); l++ ) {
            int i0 = k + l;
            int i1 = k + l + power2(j-1);            
            
            x0 = real[ i0 ];
            y0 = imaginary[ i0 ];
            x1 = real[ i1 ];
            y1 = imaginary[ i1 ];
         
            real[ i0 ]      = (  x0 + x1 + y0 - y1) / 2.;
            real[ i1 ]      = (  x0 + x1 - y0 + y1) / 2.;
            imaginary[ i0 ] = (- x0 + x1 + y0 + y1) / 2.;
            imaginary[ i1 ] = (  x0 - x1 + y0 + y1) / 2.;
         }
      }
   }
   
   /**
    *  Applies the Toffoli gate to this register. 
    *  Here <i>j</i><sub>1</sub> and <i>j</i><sub>2</sub> are the control qubits 
    *  and <i>k</i> is the target qubit.
    *  @param j1 the first control qubit
    *  @param j2 the second control qubit
    *  @param k  the target qubit
    */
   public void toffoli(int j1, int j2, int k) {
      if (isStabilizerState) {
         isStabilizerState = false;
         Register newRegister = graphState.getRegister();
         real      = newRegister.real;
         imaginary = newRegister.imaginary;
         graphState = null;
      }
      
      double tmp;
      
      /* In the following loop, all target qubit indices it which 
         have to be exchanged are determined and stored to a binary digit.*/
      HashSet<Integer> exchange = new HashSet<>();
      for (int t = 0; t < power2(size); t++) {
         // if the bits (j1-1) and (j2-1) are not met by t, then go on...
         if ( (t & power2(j1-1)) + (t & power2(j2-1)) < power2(j1 - 1) + power2(j2 - 1) ) {
            continue;
         }
         
         if (!exchange.contains(t - power2(k-1))) {
            exchange.add(t);
         }
      }
      
      // exchange the stored indices:
      int t2;
      for ( int t : exchange) {
         t2 = (t + power2(k-1) < power2(size)) ? t + power2(k-1) : t - power2(k-1);
         tmp = real[t];
         real[t]  = real[t2];
         real[t2] = tmp;

         tmp = imaginary[t];
         imaginary[t]  =  imaginary[t2];
         imaginary[t2] =  tmp;
      }
   }
   
   /**
    * Applies the quantum Fourier transform (QFT) to this register.
    * For each computational basis state |<i>j</i>&gt; it is defined as:
   <table style="margin:auto;" summary="">
     <tr>
       <td> 
         QFT( |<i>j</i>&gt; )    
       </td><td align="center"> 
         &nbsp; = &nbsp;     
       </td><td> 
         <table summary="" border="0"> 
           <tr><td align="center">1</td></tr> 
           <tr><td style="height:1px;"><hr></td></tr> 
           <tr><td>&#8730;<i>q</i></td></tr> 
         </table>
       </td><td> 
         <table summary="" border="0"> 
           <tr><td align="center" class="small"><i>q</i> - 1</td></tr> 
           <tr><td align="center" style="font-size:xx-large;">&#931;</td></tr> 
           <tr><td align="center" class="small"><i>k</i> = 0</td></tr> 
         </table> 
       </td><td>
         e<sup>2&#960;i<i>jk</i>/<i>q</i></sup> |<i>k</i>&gt;.
       </td>
     </tr>
   </table>
    * <p>
    * Here <i>q</i> denotes the total number of different basis states of the
    * register. If <i>q</i> = 2<sup><i>n</i></sup> where <i>n</i>
    * is the number of qubits of this register, then the fast Fourier transform
    * is applied. Otherwise, a new quantum register with <code>size</code> 
    * basis states is created from this register where only the first <i>q</i>
    * register states are transformed.
    * Note that both <i>q</i> and <code>size</code> must be less than or equal
    * 2<sup><i>n</i></sup> where <i>n</i> is the qubit size of this register.
    * </p>
    * @param q the total number of register basis states 
    * |0&gt;, |1&gt;, ..., |<i>q</i> - 1&gt; to be transformed
    * @param size the number of register basis states of the transformed register
    * @see #inverseQft(int,int)
    */
   public void qft( int q, int size ) {
      if (isStabilizerState) {
         isStabilizerState = false;
         Register newRegister = graphState.getRegister();
         real      = newRegister.real;
         imaginary = newRegister.imaginary;
         graphState = null;
      }
      
      if ( q == power2( this.size ) ) {
         fft(+1); // fast Fourier transform of the entire register
      } else {
         double[] realTmp = new double[size];
         double[] imaginaryTmp = new double[size];
         double phase;
      
         for ( int x=0; x < size; x++ ) {
            if ( real[x] != 0 || imaginary[x] != 0 ) {
               for ( int k=0; k < q; k++ ) {
                  phase = 2 * PI * k * x / q;
                  realTmp[k]      += real[x] * cos( phase ) - imaginary[x] * sin( phase );
                  imaginaryTmp[k] += real[x] * sin( phase ) + imaginary[x] * cos( phase );
               }
            }
         }
      
         double length=0;
         for ( int k=0; k<q; k++ ) {
            length += realTmp[k]*realTmp[k] + imaginaryTmp[k]*imaginaryTmp[k];
         }
         length = sqrt(length);
         
         for ( int k=0; k<q; k++ ) {
            realTmp[k] = realTmp[k] / length;
            imaginaryTmp[k] = imaginaryTmp[k] / length;
            if ( abs( realTmp[k] ) < ACCURACY ) {
               realTmp[k] = 0;
            }
            if ( abs( imaginaryTmp[k] ) < ACCURACY ) {
               imaginaryTmp[k] = 0;
            }
         }

         real = realTmp;
         imaginary = imaginaryTmp;
      }
   }

   /**
    * Applies the inverse quantum Fourier transform (QFT<sup>-1</sup>) to this register.
    * For each computational basis state |<i>j</i>&gt; it is defined as:
   <table style="margin:auto;" summary="">
     <tr>
       <td> 
         QFT<sup>-1</sup>( |<i>j</i>&gt; )    
       </td><td align="center"> 
         &nbsp; = &nbsp;     
       </td><td> 
         <table summary="" border="0"> 
           <tr><td align="center">1</td></tr> 
           <tr><td style="height:1px;"><hr></td></tr> 
           <tr><td>&#8730;<i>q</i></td></tr> 
         </table>
       </td><td> 
         <table summary="" border="0"> 
           <tr><td align="center" class="small"><i>q</i> - 1</td></tr> 
           <tr><td align="center" style="font-size:xx-large;">&#931;</td></tr> 
           <tr><td align="center" class="small"><i>k</i> = 0</td></tr> 
         </table> 
       </td><td>
         e<sup>2&#960;i<i>jk</i>/<i>q</i></sup> |<i>k</i>&gt;.
       </td>
     </tr>
   </table>
    * <p>
    * Here <i>q</i> denotes the total number of different basis states of the
    * register. If <i>q</i> = 2<sup><i>n</i></sup> where <i>n</i>
    * is the number of qubits of this register, then the inverse fast Fourier transform
    * is applied. Otherwise, a new quantum register with <code>size</code> 
    * basis states is created from this register where only the first <i>q</i>
    * register states are transformed.
    * Note that both <i>q</i> and <code>size</code> must be less than or equal
    * 2<sup><i>n</i></sup> where <i>n</i> is the qubit size of this register.
    * </p>
    * @param q the total number of register basis states 
    * |0&gt;, |1&gt;, ..., |<i>q</i> - 1&gt; to be transformed
    * @param size the number of register basis states of the transformed register
    * @see #qft(int,int)
    */
   public void inverseQft( int q, int size ) {
      if (isStabilizerState) {
         isStabilizerState = false;
         Register newRegister = graphState.getRegister();
         real      = newRegister.real;
         imaginary = newRegister.imaginary;
         graphState = null;
      }
      
      if ( q == power2( this.size ) ) {
         fft(-1); // inverse fast Fourier transform of the entire register
      } else {
         double[] realTmp = new double[size];
         double[] imaginaryTmp = new double[size];
         double phase;
      
         for ( int x=0; x < size; x++ ) {
            if ( real[x] != 0 || imaginary[x] != 0 ) {
               for ( int k=0; k < q; k++ ) {
                  phase = 2 * PI * k * x / q;
                  realTmp[k]      += real[x] * cos( phase )  - imaginary[x] * sin( -phase );
                  imaginaryTmp[k] += real[x] * sin( -phase ) + imaginary[x] * cos( phase );
               }
            }
         }
      
         double length=0;
         for ( int k=0; k<q; k++ ) {
            length += realTmp[k]*realTmp[k] + imaginaryTmp[k]*imaginaryTmp[k];
         }
         length = sqrt(length);
      
         for ( int k=0; k<q; k++ ) {
            realTmp[k] = realTmp[k] / length;
            imaginaryTmp[k] = imaginaryTmp[k] / length;
            if ( abs( realTmp[k] ) < ACCURACY ) {
               realTmp[k] = 0;
            }
            if ( abs( imaginaryTmp[k] ) < ACCURACY ) {
               imaginaryTmp[k] = 0;
            }
         }

         real = realTmp;
         imaginary = imaginaryTmp;
      }
   }

   /** Fast Fourier transform of the actual register, applying the discrete
    *  Fourier transform. It consists of two sections, the bit-reversal and
    *  the Danielson-Lanczos routine.
    */
   private void fft( int isign ) {
      int n = power2(size);
      double scale = Math.sqrt(1./n);

      int j = 0;
      for ( int i = 0; i < n; i++ ) {
         if (j >= i) {
            double tempr = real[j]*scale;
            double tempi = imaginary[j]*scale;
            real[j] = real[i]*scale;
            imaginary[j] = imaginary[i]*scale;
            real[i] = tempr;
            imaginary[i] = tempi;
         }
         int m = n/2;
         while ( m>=1 && j>=m ) {
            j -= m;
            m /= 2;
         }
         j += m;
      }
      
      // Danielson-Lanczos routine:
      int istep = 2;
      for (int mmax = 1; mmax < n; istep = 2*mmax ) {
         double delta = isign * PI/mmax;
         for (int m = 0; m < mmax; m++ ) {
            // trigonometric recurrence:
            double theta = m * delta;
            double wr = cos( theta );
            double wi = sin( theta );
            for ( int i = m; i < n; i += istep) {
               j = i + mmax;
               double tmpr = wr * real[j] - wi * imaginary[j];
               double tmpi = wr * imaginary[j] + wi * real[j];
               // this is the Danielson-Lanczos formula:
               real[j]       = real[i] - tmpr;
               imaginary[j]  = imaginary[i] - tmpi;
               real[i]      += tmpr;
               imaginary[i] += tmpi;
            }
         }
         mmax = istep;
      }
   }

   /**
    *  Applies one of the three basic rotation operators 
    *  <i>R<sub>x</sub></i>(<i>&#x03C6;</i>), <i>R<sub>y</sub></i>(<i>&#x03C6;</i>), 
    *  or <i>R<sub>z</sub></i>(<i>&#x03C6;</i>) to this register.
    *  Here a basic rotation operator represents a rotation with an angle
    *  <i>&#x03C6;</i> about the axis 
    *  <i>x</i>, <i>y</i>, or <i>z</i>, respectively.
    *  @param cQubits an array containing the control qubits and, as its last entry, the target qubit
    *  @param axis a string representing the axis, i.e., either "x", "y", or "z"
    *  @param phi the rotation angle in radians
    */
   public void rotate( int[] cQubits, String axis, double phi ) {
      if (isStabilizerState) {
         isStabilizerState = false;
         Register newRegister = graphState.getRegister();
         real      = newRegister.real;
         imaginary = newRegister.imaginary;
         graphState = null;
      }
      
      int k = cQubits[ cQubits.length - 1 ]; // the target qubit to be rotated
      double[] realTmp = new double[real.length];
      System.arraycopy(real,0,realTmp,0,real.length);
      double[] imaginaryTmp = new double[imaginary.length];
      System.arraycopy(imaginary,0,imaginaryTmp,0,imaginary.length);

      HashSet<Integer> controls = new HashSet<>();
      
       /* In the following loops, all target qubit indices ic which 
          have to be rotated are determined and stored to a set.*/
      if (cQubits.length == 1) { // there is no control qubit         
         for ( int ic = 0; ic < real.length; ic++ ) {
            if ( real[ic] != 0 || imaginary[ic] != 0 ) {
               if ( ( ic & (1 << k - 1) ) == 0 ) { // Is ic the smaller of the state pair to be rotated?
                  ic += power2(k-1); // 
               }
               controls.add(ic);
            }
         }
      } else { // there are control qubits
         int j=0;
         for ( int i = 0; i < cQubits.length - 1; i++ ) {
            j += power2(cQubits[i] - 1);
         }
         for ( int kc = j; kc < real.length; kc += 2*j ) {
            for ( int lc = 0; kc + lc < real.length; lc++ ) {
               int ic = kc + lc;
               boolean match = real[ic] != 0 || imaginary[ic] != 0;
               // Is each control bit set?
               for ( int i = 0; i < cQubits.length - 1 && match; i++ ) {
                  match &= (ic & (1 << cQubits[i] - 1)) >= 1;
               }
               if ( match ) {
                  // Is ic the smaller of the state pair to be rotated?
                  if ( ( ic & (1 << k - 1) ) == 0 ) { 
                     ic += power2(k-1);
                  }
                  controls.add(ic);
               }
            }
         }
      }
      
      int it0, it1;
      for ( int ic : controls ) {         
         it0 = ic - power2(k-1);
         it1 = ic;
         switch (axis) {
            case "x":
               realTmp[it0]      = real[it0] * cos(phi/2) + imaginary[it1] * sin(phi/2);
               imaginaryTmp[it0] = imaginary[it0] * cos(phi/2) - real[it1] * sin(phi/2);
               realTmp[it1]      = real[it1] * cos(phi/2) + imaginary[it0] * sin(phi/2);
               imaginaryTmp[it1] = imaginary[it1] * cos(phi/2) - real[it0] * sin(phi/2);
               break;
            case "y":
               realTmp[it0]      = real[it0] * cos(phi/2) - real[it1] * sin(phi/2);
               imaginaryTmp[it0] = imaginary[it0] * cos(phi/2) - imaginary[it1] * sin(phi/2);
               realTmp[it1]      = real[it1] * cos(phi/2) + real[it0] * sin(phi/2);
               imaginaryTmp[it1] = imaginary[it1] * cos(phi/2) + imaginary[it0] * sin(phi/2);
               break;
            case "z":
               realTmp[it0]      = real[it0] * cos(phi/2) + imaginary[it0] * sin(phi/2);
               imaginaryTmp[it0] = imaginary[it0] * cos(phi/2) - real[it0] * sin(phi/2);
               realTmp[it1]      = real[it1] * cos(phi/2) - imaginary[it1] * sin(phi/2);
               imaginaryTmp[it1] = imaginary[it1] * cos(phi/2) + real[it1] * sin(phi/2);
               break;
         }
         
         if ( abs(realTmp[it0]) < ACCURACY ) {
            realTmp[it0] = 0.;
         }
         if ( abs(imaginaryTmp[it0]) < ACCURACY ) {
            imaginaryTmp[it0] = 0.;
         }
         if ( abs(realTmp[it1]) < ACCURACY ) {
            realTmp[it1] = 0.;
         }
         if ( abs(imaginaryTmp[it1]) < ACCURACY ) {
            imaginaryTmp[it1] = 0.;
         }
      }
      real = realTmp;
      imaginary = imaginaryTmp;
   }
   
   /**
    *  Applies the function evaluation of a parsed function <i>f</i>(<i>z</i>) to the
    *  <i>y</i>-register.
    *  This method modifies the input <i>y</i>-register.
    *  @param yRegister the <i>y</i>-register where the function values are stored
    *  @param function the parsed function <i>f</i>(<i>z</i>)
    *  @param z the value at which <i>f</i>(<i>z</i>) is to be evaluated
    *  @return the <i>y</i>-register after the evaluation of <i>f</i>(<i>z</i>), 
    *  regarding entanglement
    *  @throws java.nio.BufferOverflowException if y-register is too small to 
    *  store all function values
    */
   public Register evaluateFunction(Register yRegister, FunctionParser function, int z) {
      if (isStabilizerState) {
         isStabilizerState = false;
         Register newRegister = graphState.getRegister();
         real      = newRegister.real;
         imaginary = newRegister.imaginary;
         graphState = null;
      }
      
      if (yRegister.isStabilizerState) {
         yRegister.isStabilizerState = false;
         Register newRegister = yRegister.graphState.getRegister();
         yRegister.real       = newRegister.real;
         yRegister.imaginary  = newRegister.imaginary;
         yRegister.graphState = null;
      }
      
      int x = 0;
      while ( x < real.length ) {
         if ( abs(real[x]) < ACCURACY && abs(imaginary[x]) < ACCURACY ) {
            return yRegister;
         }
         x++;
      }
      
      entanglement = new HashMap<>();
      double[] realTmp = new double[power2(yRegister.size)];
      double[] imagTmp = new double[power2(yRegister.size)];
      
      int index, f, y;
      double phase, tmp;
      
      for (x = 0; x < real.length; x++) {
         f = (int) function.evaluateInt(x , z);
         if (f < 0 || f >= yRegister.real.length) {
            throw new java.nio.BufferOverflowException();
         }
         phase = atan2(yRegister.imaginary[f], yRegister.real[f]);
         
         for (y = 0; y < realTmp.length; y++) {
            if (abs(yRegister.real[y]) < ACCURACY && abs(yRegister.imaginary[y]) < ACCURACY) {
               continue;
            }
            index = y ^ f;
            if (index < 0 || index >= yRegister.real.length) {
               throw new java.nio.BufferOverflowException();
            }
            
            if (abs(yRegister.real[index]) < ACCURACY && abs(yRegister.real[index]) < ACCURACY) {
               realTmp[index] = 1;
            } else {
               realTmp[index] = yRegister.real[index] * cos(phase) 
                              - yRegister.imaginary[index] * sin(phase);
               imagTmp[index] = yRegister.imaginary[index] * cos(phase) 
                              + yRegister.real[index] * sin(phase);
            }
            
            if (!entanglement.containsKey(index)) {
               entanglement.put(index, new ArrayList<Integer>());
            }
            entanglement.get(index).add(x);
         }
         //System.out.println("### x="+x+", phase="+phase);
         tmp = this.real[x];
         this.real[x]      = tmp * cos(phase) - this.imaginary[x] * sin(phase);
         this.imaginary[x] = this.imaginary[x] * cos(phase) + tmp * sin(phase);
      }
      
      double length = 0;

      yRegister.real = new double[power2(yRegister.size)];
      yRegister.imaginary = new double[power2(yRegister.size)];
      for (int k : entanglement.keySet()) {
         yRegister.real[k]      = realTmp[k];
         yRegister.imaginary[k] = imagTmp[k];
         length += yRegister.real[k] * yRegister.real[k];
      }
      length = sqrt(length);
      
      for (int k : entanglement.keySet()) {
         yRegister.real[k] = yRegister.real[k] / length;
      }

      return yRegister;
   }
   
   /**
    * Performs a Grover operator, or Grover iteration step, 
    * on this quantum register.
    * A Grover iteration consists of the following steps:
    * <ol>
    * <li>
    * Apply an oracle query which assigns +1 to each
    * value which is different from the searched for value (the "needle"), and
    * -1 to the value which is searched for;
    * </li>
    * <li>
    * Apply an <i>n</i>-fold Hadamard on the entire register
    * </li>
    * <li>
    * Apply the conditional phase shift of -1, -<i>I</i><sub>0</sub>, defined by
    * -<i>I</i><sub>0</sub>(|<i>0</i>&gt;) = |0&gt; and
    * <p style="text-align:center">
    *    -<i>I</i><sub>0</sub>(|<i>x</i>&gt;) = -|x&gt;
    *    &nbsp; &nbsp; if <i>x</i> &ne; 0.
    * </p>
    * </li>
    * <li>
    * Apply an <i>n</i>-fold Hadamard on the entire register
    * </li>
    * </ol>
    * The iteration step is described in
    * M.A. Nielsen &amp; I.L. Chuang: 
    * <i>Quantum Computation and Quantum Information.</i> 
    * Cambridge University Press, Cambridge, 2000, &sect;6.1.2.
    * <p>
    * In practice, an oracle could be a cryptologic algorithm in a known-plaintext
    * attack which applies a secret key to a given ciphertext and yields -1
    * if the deciphered text equals the known plaintex, and +1 otherwise.
    * </p>
    * @param needle the value to be searched for
    * @throws IllegalArgumentException if the needle value is out of register range
    */
   public void grover(int needle) {
      if (needle < 0 || needle >= real.length) {
         throw new IllegalArgumentException(
           "Searched value is out of register range: "+needle+" >= "+real.length
         );
      }
      // Step 1: Apply an oracle query for each set register value:
      for (int x = 0; x < real.length; x++) {
         if (x == needle) {
            if (abs(real[x]) > ACCURACY) {
               real[x] *= -1;
            }
            if (abs(imaginary[x]) > ACCURACY) {
               imaginary[x] *= -1;
            }
            break; // terminate loop, we only have one needle ...
         }
      }
      
      // Step 2: Apply an n-fold Hadamard on the entire register:
      for (int i = 1; i <= size; i++) {
         hadamard(i);
      }
      
      // Step 3: Apply the conditional phase shift -I_{|0>}:
      for (int x = 0; x < real.length; x++) {
         if (x != 0) { // |0> remains unchanged, all other values |x> get reversed sign
            if (abs(real[x]) > ACCURACY) {
               real[x] *= -1;
            }
            if (abs(imaginary[x]) > ACCURACY) {
               imaginary[x] *= -1;
            }
         }
      }
      
      // Step 4: Apply an n-fold Hadamard on the entire register:
      for (int i = 1; i <= size; i++) {
         hadamard(i);
      }
   }
   
   /**
    * Applies the inverse of the Grover operator.
    * @param needle the value to be searched for
    * @see #grover(int)
    */
   public void inverseGrover(int needle) {
      if (needle < 0 || needle >= real.length) {
         throw new IllegalArgumentException(
           "Searched value is out of register range: "+needle+" >= "+real.length
         );
      }
      
      // Step 1: Apply an n-fold Hadamard on the entire register:
      for (int i = 1; i <= size; i++) {
         hadamard(i);
      }
      
      // Step 2: Apply the conditional phase shift -I_{|0>}:
      for (int x = 0; x < real.length; x++) {
         if (x != 0) { // |0> remains unchanged, all other values |x> get reversed sign
            if (abs(real[x]) > ACCURACY) {
               real[x] *= -1;
            }
            if (abs(imaginary[x]) > ACCURACY) {
               imaginary[x] *= -1;
            }
         }
      }
      
      // Step 3: Apply an n-fold Hadamard on the entire register:
      for (int i = 1; i <= size; i++) {
         hadamard(i);
      }
      
      // Step 4: Apply an oracle query for each set register value:
      for (int x = 0; x < real.length; x++) {
         if (x == needle) {
            if (abs(real[x]) > ACCURACY) {
               real[x] *= -1;
            }
            if (abs(imaginary[x]) > ACCURACY) {
               imaginary[x] *= -1;
            }
            break; // terminate loop, we only have one needle ...
         }
      }
   }
   
   /**
    * The optimal number <i>r</i> of Grover iterations to successfully apply 
    * Grover's search algorithm. It is given by
    *   <table style="margin:auto;" summary="">
    *     <tr>
    *       <td><i>r</i> &nbsp; &asymp; &nbsp; </td>
    *       <td>
    *         <table summary="" border="0">
    *          <tr>
    *            <td align="center">&pi;</td>
    *          </tr>
    *          <tr>
    *            <td><hr></td>
    *          </tr>
    *          <tr>
    *            <td>4 arcsin(2<sup>-<i>n</i>/2</sup>)</td>
    *          </tr>
    *         </table>
    *       </td>
    *       <td> &nbsp; &asymp; &nbsp; </td>
    *       <td>
    *         <table summary="" border="0">
    *          <tr>
    *            <td align="center">2<sup><i>n</i>/2</sup> &pi;</td>
    *          </tr>
    *          <tr>
    *            <td><hr></td>
    *          </tr>
    *          <tr>
    *            <td style="text-align:center">4</td>
    *          </tr>
    *         </table>
    *       </td>
    *       <td> &nbsp; = &nbsp; 2<sup>(<i>n</i> - 4)/2</sup> &pi;</td>
    *     </tr>
    *   </table>
    * <p>
    * where <i>n</i> &ge; 2 is the size of the register, i.e., the number of its qubits.
    * </p>
    * @param size the size of the register
    * @return the optimal number of Grover iterations
    */
   public static int groverSteps(int size) {
      return (int) (sqrt(power2(size)) * PI/4);
   }
   
   /** 
    *  Returns the random value of a quantum measurement of this register. 
    *  A quantum measurement makes the register collapse randomly to one of 
    *  the qubits |<i>j</i>&gt;. Here the probability for a collapse
    *  to qubit |<i>j</i>&gt; is determined by the squared absolute value of the probability
    *  amplitudes &#x03B1;<sub><i>j</i></sub>, i.e. of the array values 
    *  real[<i>j</i>]<sup>2</sup> + imaginary[<i>j</i>]<sup>2</sup>.
    *  
    *  <p>
    *  To implement the random collapse, a random number <i>f</i> with 
    *  0 &#x2264; <i>f</i> &lt; 1 is chosen.
    *  Then successively the probabilities of the single qubits are subtracted,
    *  starting with the first qubit state |0&gt;, until <i>f</i> gets negative. The last
    *  qubit |<i>j</i>&gt; that caused the sign change is the measured qubit state.
    *  </p>
    *  @return the measured (random) value
    *  @see #measure(int)
    */
   public int measure() {
      if (isStabilizerState) {
         isStabilizerState = false;
         Register newRegister = graphState.getRegister();
         real      = newRegister.real;
         imaginary = newRegister.imaginary;
         graphState = null;
      }
      
      double f = Math.random();
      double p;
      
      int j=-1;
      while ( f >= 0 ) {
         j++;
         p = real[j] * real[j] + imaginary[j] * imaginary[j];
         f -= p;
      }
      
      // collapse:
      real = new double[power2(size)];
      imaginary = new double[power2(size)];
      
      real[j] = 1;
      
      return j;
   }

   /** 
    *  Returns the random value of a quantum measurement of the <i>j</i>-th 
    *  qubit of this register, <i>j</i> = 0, 1, ..., <i>n</i> - 1 where
    *  <i>n</i> is the size of this register.
    *  Such a quantum measurement makes the measured qubit collapse randomly 
    *  to 0 or 1, determined by the probability amplitudes of all states 
    *  |<i>m</i>&gt; where <i>m</i> contains either a 0 or a 1 at position <i>j</i>. 
    *  Here the probability for a collapse to the value "0" is determined
    *  by the squared absolute values of the probability amplitudes 
    *  &#x03B1;<sub><i>m</i></sub>, i.e., of the array values 
    *  real[<i>m</i>]<sup>2</sup> + imaginary[<i>m</i>]<sup>2</sup>, 
    *  where |<i>m</i>&gt; has a 0 at position <i>j</i>.
    *
    *  <p>
    *  All qubits in the register vanish if they are entangled with the qubit state
    *  corresponding to the value <i>not</i> measured: if "0" is measured, these are the
    *  states |<i>m</i>&gt; with <i>m</i> &amp; 2<sup><i>j</i>-1</sup> = 0, 
    *  if "1" is measured, these are |<i>m</i>&gt; with <i>m</i> &amp; 2<sup><i>j</i>-1</sup> &gt; 0. 
    *  The probability amplitudes of the remaining states must be normalized such that
    *  the whole register is in a unit state.
    *  </p>
    *  
    *  To implement the random collapse, a random number <i>f</i> with 
    *  0 &#x2264; <i>f</i> &lt; 1 is chosen.
    *  If <i>f</i> &lt; &#x03A3; |&#x03B1;<sub><i>m</i></sub>|<sup>2</sup>, 
    *  where <i>m</i> &amp; 2<sup><i>j</i>-1</sup> = 0, the measured value is "0", 
    *  otherwise it is "1".
    *  @param j the qubit to be measured
    *  @return the measured (random) value
    *  @see #measure()
    */
   public int measure(int j) {
      if (isStabilizerState) {
         return graphState.measure(j-1);
      }
      
      double f = Math.random();
      double p=0;
      int k, l, m;
      
      for (k = 0; k < power2( size ); k += power2(j)) {
         for (l = 0; l < power2(j-1); l++) {
            m = k + l;
            // --- Should never happen...:
            //if ( (m & power2(j-1)) > 0 )  
            //   System.err.println(" ### !!!??? " + m +"=|" + Long.toBinaryString(m) + ">: "+ m + " & " + power2(j-1) + " = " + ( m&power2(j-1) ) );
            p += real[m] * real[m] + imaginary[m] * imaginary[m];
         }
      }
      
      int value;
      if ( f < p ) {
         value = 0;         
      } else {
         value = 1;
      }
            
      for (k = 0; k < power2( size ); k += power2(j)) {
         for (l = 0; l < power2(j-1); l++) {
            m = k + l + (1 ^ value) * power2(j-1);
            real[m] = 0;
            imaginary[m] = 0;
            
            m = k + l + (1 & value) * power2(j-1);
            if (abs(real[m]) > ACCURACY || abs(imaginary[m]) > ACCURACY) {
               real[m] = 1;
               imaginary[m] = 0;
            }
         }
      }
      
      double length = 0;
      for (k = 0; k < power2( size ); k++) {
         length += real[k] * real[k] + imaginary[k] * imaginary[k];
      }
      length = sqrt(length);

      for (k = 0; k < power2( size ); k++) {
         real[k] /= length;
         imaginary[k] /= length;
      }
      
      return value;
   }
   
   /** Returns true if and only if the specified object represents a quantum 
    *  register which is physically equivalent to this register.
    *  Two quantum registers are physically equivalent if their qubit amplitudes
    *  only differ by an overall phase. Amplitudes are compared up to the accuracy
    *  specified by {@link #ACCURACY}.
    *  @param o the specified reference with which to compare
    *  @return <code>true</code> if the object is a quantum register physically
    *  equivalent to this register
    */
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }
      
      if (o == null) {
         return false;
      }
      
      if (o instanceof GraphRegister) {
         return equals(((GraphRegister) o).getRegister());
      }
      
      if (o.getClass() != this.getClass()) {
         return false;
      }
      
      Register r = (Register) o;
      if (r.isStabilizerState) {
         r = r.graphState.getRegister();
      }
      Register my = isStabilizerState ? graphState.getRegister() : this;
      
      // The register sizes must be equal:
      if (my.real.length != r.real.length) {
         return false;
      }
      if (my.real.length != r.imaginary.length) {
         return false;
      }
      
      // Compare up to an overall phase, determined by the first nonvanishing amplitude:
      double[] phase = new double[2];
      for (int i = 0; (phase[0] == 0 && phase[1] == 0) && i < my.real.length; i++) {
         if (abs(r.real[i]) >= ACCURACY || abs(r.imaginary[i]) >= ACCURACY) {
            if (abs(my.real[i]) < ACCURACY && abs(my.imaginary[i]) < ACCURACY) {
               return false;
            }
            phase = org.mathIT.numbers.Complex.divide(
               new double[]{r.real[i], r.imaginary[i]}, new double[]{my.real[i], my.imaginary[i]}
            );
         }
      }
      
      // compare qubits:
      double[] diff;
      for (int i = 0; (phase[0] != 0 || phase[1] != 0) && i < my.real.length; i++) {
         if (abs(my.real[i]) < ACCURACY && abs(my.imaginary[i]) < ACCURACY) {
            if (abs(r.real[i]) >= ACCURACY || abs(r.imaginary[i]) >= ACCURACY) {
               return false;
            } else {
               continue; // both amplitudes vanish ...
            }
         }
         
         diff = org.mathIT.numbers.Complex.divide(
            new double[]{r.real[i], r.imaginary[i]}, new double[]{my.real[i], my.imaginary[i]}
         );
         
         if (abs(phase[0] - diff[0]) > ACCURACY || abs(phase[1] - diff[1]) > ACCURACY) {
            return false; // phases are different
         }
      }
      return true; // if the above loop is finished, the registers must be equal
   }
      
   /**
    * Returns the hash code of the current state of this quantum register.
    * @return the hash code of the current state of this quantum register
    */
   @Override
   public int hashCode() {
      // Compare up to an overall phase, determined by the first nonvanishing amplitude:
      int i;
      int hash = 7;
      
      /*
      boolean[] entangleZ = new boolean[size]; // default entries: false
      
      if (isStabilizerState) {
         
         for (int i = 0; i < vertices.size(); i++ ) {
            matrices[i] = vertices.get(i).getMatrix();
            
            for (int j : vertices.get(i).neighbors) {
               if (j > i) continue; // take each edge only once
               
               for (int k = 0; k < q; k++) {
                  // if the bit (j-1) is not met by k, then go on...
                  if ( (k & ((1 << i) | (1 << j))) < ((1 << i) | (1 << j)) ) {
                     continue;
                  }
                  entangleZ[k] = !entangleZ[k];
               }
            }
         }
      }
      */
      
      for (int b = 0; b < size; b++) {
         i = (1 << b) - 1;
         if (isStabilizerState) {
            Register tmpReg = graphState.getRegister();
            double[] tmpReal = tmpReg.real;
            double[] tmpImag = tmpReg.imaginary;
            hash = 31*hash + (Double.valueOf(tmpReal[i]*tmpReal[i] + tmpImag[i]*tmpImag[i])).hashCode();
            /*
            for (int j = 0; j < c[0].length; j++) {
               rows = c.length;
               cols = c[0].length;
               //for (int k = 0; k < a.length; k++) { // vorwärts multiplizieren
               for (int k = a.length - 1; k >= 0; k--) { // rückwärts multiplizieren
                  rows /= a[k].length;
                  cols /= a[k][0].length;
                  ia[k] = (i / rows) % a[k].length;
                  ja[k] = (j / cols) % a[k][0].length;
               }
               
               c[i][j][0] = 1;
               c[i][j][1] = 1;
               
               for (int k = 0; k < a.length; k++) { // vorwärts multiplizieren
                  //for (int k = a.length - 1; k >= 0; k--) { // rückwärts multiplizieren
                  c[i][j][0] = a[k][ia[k]][ja[k]][0] * c[i][j][0] 
                  - a[k][ia[k]][ja[k]][1] * c[i][j][1];
                  c[i][j][1] = a[k][ia[k]][ja[k]][0] * c[i][j][1] 
                  + a[k][ia[k]][ja[k]][1] * c[i][j][0];
                  //System.out.print(ia[k]+""+ja[k]+" ");
               }
               //System.out.print(",");
               
               //if(abs(c[i][j][0]) < Register.ACCURACY) c[i][j][0] = 0;
               //if(abs(c[i][j][1]) < Register.ACCURACY) c[i][j][1] = 0;
            }
            */
//System.out.println();
         } else {
            //i = (1 << b) - 1;
            hash = 31*hash + (Double.valueOf(real[i]*real[i] + imaginary[i]*imaginary[i])).hashCode();
         }
      }
      return hash;
   }
   
   /**
    *  Returns a string representation of the first <code>nMax</code> qubits of
    *  this register.
    *  @param nMax specifies the maximum number of considered qubits
    *  @return a string representation of the first <code>nMax</code> qubits 
    *  of this register
    */
   public String show( int nMax ) {
      return toString(nMax);
   }
   
   /**
    *  Returns a string representation of the first <code>nMax</code> qubits of
    *  this register.
    *  @param nMax specifies the maximum number of considered qubits
    *  @return a string representation of the first <code>nMax</code> qubits 
    *  of this register
    */
   public String toString( int nMax ) {
      int jMax = power2( nMax );

      String output = "\n Register state:\n";

      for ( int j = 0; j < jMax; j++ ) {
         String binary = "";
         int k = j;
         while ( k > 0 ) {
            binary = k % 2 + binary;
            k /= 2;
         }
         int length = binary.length();
         for ( int i = nMax; i > length; i-- ) {
            binary = "0" + binary;
         }
         if ( j<10 ) {
            output += "    |" + j + ">:  ";
         }
         else {
            output += "   |" + j + ">:  ";
         }
         output += "( " + real[j] + " + " + imaginary[j] + " i )  |" + binary + ">\n";
      }
      return output;
   }

   /**
    *  Returns a string representation of this register.
    *  @return a string representation of this register
    */
   @Override
   public String toString() {
      if (isStabilizerState) {
         Register newRegister = graphState.getRegister();
         real = newRegister.real;
         imaginary = newRegister.imaginary;
      }
      
      int jMax = power2(size);

      String output = "|psi> = ";
      boolean first = true;

      for ( int j = 0; j < jMax; j++ ) {
         String binary = "";
         int k = j;
         while ( k > 0 ) {
            binary = k % 2 + binary;
            k /= 2;
         }
         int length = binary.length();
         for ( int i = size; i > length; i-- ) {
            binary = "0" + binary;
         }
         if (abs(real[j]) > ACCURACY || abs(imaginary[j]) > ACCURACY) {
            if (!first) {
               output += "\n      + ";
            }
            output += "(" + 
               org.mathIT.numbers.Complex.toString(new double[]{real[j], imaginary[j]})
               + ") |" + binary + ">";
            first = false;
         }
      }
      if (isStabilizerState) {
         real = null;
         imaginary = null;
      }
      return output;
   }

   /** Returns the value of 2<sup><i>n</i></sup>.
    *  The return value is given as an <code>int</code> value, since
    *  in the context of quantum registers, the values of <i>n</i> are comparably small.
    *  @param n the exponent
    *  @return the value of 2<sup><i>n</i></sup>
    */
   private static int power2( int n ) {
      return 1 << n;
   }
   
   /** For test porposes...*/
   /*
   private boolean initializeRandomly() {
      for (int k = 0; k < real.length; k++) {
         real[k]      = random();
         imaginary[k] = random();
      }
      
      double length = 0;
      for ( int k = 0; k < power2(size); k++ ) {
         length += real[k] * real[k] + imaginary[k] * imaginary[k];
      }
      length = sqrt(length);

      for ( int k = 0; k < power2(size); k++ ) {
         real[k]      /= length;
         imaginary[k] /= length;
      }
      return true;
   }
   // */

   /** For test purposes...*/
   /*
   public static void main (String[] args) {
      int n = 5;  // <= 22 !!
      
      if ( args.length > 0 && args[0] != null )  n = Integer.parseInt( args[0] );

      System.out.println(" Simulation of a quantum register of size " + n );
      System.out.println(" Number of qubits states: 2^" + n + "=" + power2(n) );
      System.out.println(" ACCURACY=" + ACCURACY );
      
      //long time;
      long start = System.currentTimeMillis();

      Register register = new Register(n);
      System.out.println(" Time for register generation: " + 
         ( (System.currentTimeMillis() - start)/1000.0 + " sec") );
      
      double norm = 1; // /SQRT2;// / 2f; // SQRT2
      //register.real[0] = norm;
      //register.real[3] = norm;
      //register.real[4] = norm;
      //register.real[7] = norm;
      //register.real[6] = norm;
      //register.real[7] = norm;

      System.out.println( register+", hash code = "+ register.hashCode());
      register.hadamard(2);
      //register.cNOT(1,2);
      //register.hadamard(2);
      System.out.println( register+", hash code = "+ register.hashCode());
      
      System.exit(0);
      
      start = System.currentTimeMillis();
      register.toffoli(1,2,3);
      System.out.println( register+", hash code = "+ register.hashCode());
      //System.out.println(" Time for toffoli transformation: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");

      //System.exit(0);
      
      //start = System.currentTimeMillis();
      //  System.out.println( " Measured:  |" + register.measure() + ">");
      //System.out.println(" Time for measurement: " + ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");

      //norm = 1/sqrt(1);
      //register.real[ 0] = 0;
      //register.real[ 1] = norm;
      //register.real[10] = norm;
      //register.real[20] = norm;
      //register.real[30] = norm;
      
      //start = System.currentTimeMillis();
      //register.qft( power2(n), power2(n) );
      //System.out.println(" Time for QFT: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");
      
      //System.out.println( register.show( n ) );
      
      //start = System.currentTimeMillis();
      //register.inverseQft( power2(n), power2(n) );
      //System.out.println(" Time for inverse QFT: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");

      //System.out.println( register.show( n ) );
      
      //start = System.currentTimeMillis();
      //System.out.println( " Measured:  \"" + register.measure(2) + "\"");
      //System.out.println(" Time for measurement: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");
      

      ////register.real[0] = 0;
      //start = System.currentTimeMillis();
      //register.fft( 1 );
      //System.out.println(" Time for FFT: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");

      //System.out.println( register.show( n ) );
      
      //start = System.currentTimeMillis();
      //register.fft( -1 );
      //System.out.println(" Time for inverse FFT: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");

      //System.out.println( register.show( n ) );
      
      //start = System.currentTimeMillis();
      //System.out.println( " Measured:  \"" + register.measure(2) + "\"");
      //System.out.println(" Time for measurement: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");
      
      //start = System.currentTimeMillis();
      //register.toffoli(1,2,3);
      //System.out.println(" Time for Toffoli transformation: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");

      //System.out.println( register.show( n ) );
      
      //start = System.currentTimeMillis();
      //System.out.println( " Measured:  |" + register.measure() + ">");
      //System.out.println(" Time for measurement: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");
      
      n = 3;
      register = new Register(n);
      norm = 1/sqrt(power2(n));
      //register.real[0] = norm;
      for ( int k = 0; k < register.real.length; k++ ) {
         register.real[k] = norm;
         register.imaginary[k] = 0.;
      }
      System.out.println( register.show( n ) );
      //start = System.currentTimeMillis();
      //int[] cQubits = {1,3,4,2};
      //register.rotate( cQubits, "x", PI/4 );
      //System.out.println( register.show( n ) );
      
      for( int i=1; i <= n; i++) {
         for( int j=1; j <=n; j++) {
            if ( i == j ) continue;
            for ( int k = 0; k < register.real.length; k++ ) {
               register.real[k] = norm;
               register.imaginary[k] = 0.;
            }
            System.out.println("============ (c,t) = ("+i+","+j+"): ===========");
            //System.out.println( register.show( n ) );
            int[] cQubits = {i,j};
            register.rotate( cQubits, "x", PI/2 );
            System.out.println( register.show( n ) );
         }
      }
      //System.out.println(" Time for rotation: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");

      //register.rotate( cQubits, "x", -PI/2 );
      //System.out.println( register.show( n ) );
      
      //start = System.currentTimeMillis();
      //System.out.println( " Measured:  |" + register.measure() + ">");
      //System.out.println(" Time for measurement: " + 
      //   ( (System.currentTimeMillis() - start)/1000.0 ) + " sec");
      
      //System.out.println( register.show( n ) );
      
      System.exit(0);
   }
   // */
}
