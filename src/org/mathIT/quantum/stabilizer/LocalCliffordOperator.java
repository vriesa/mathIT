/*
 * LocalCliffordOperator.java - Operator in the local Clifford group
 *
 * Copyright (C) 2008-2012 Andreas de Vries
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
/** 
 * An operator in the local Clifford group <i>C</i><sub>1</sub>.
 * The 24 local Clifford operators form a multiplicative group 
 * <i>C</i><sub>1</sub>,
 * the multiplication in the Clifford group is not commutative.
 * A local Clifford operator is a stabilizer if and only if it is
 * in the Pauli group <i>P</i><sub>1</sub>
 * = {
 *  &plusmn;<i>I</i>, &plusmn;i<i>I</i>, &plusmn;<i>X</i>, &plusmn;i<i>X</i>,
 *  &plusmn;<i>Y</i>, &plusmn;i<i>Y</i>, &plusmn;<i>Z</i>, &plusmn;i<i>Z</i>
 * }.
 * @author  Andreas de Vries
 * @version 1.0
 */
public class LocalCliffordOperator {
   static final char[] paulinames = {'I', 'X', 'Y', 'Z'};
   
   // special local Clifford operators:
   /** The identity operator acting on a single qubit.*/
   private static final int I_CODE    = 0;         //!< Identity
   /** The Pauli-X operator acting on a single qubit.*/
   private static final int X_CODE    = 1;        //!< Pauli X
   /** The Pauli-Y operator acting on a single qubit.*/
   private static final int Y_CODE    = 2;        //!< Pauli Y 
   /** The Pauli-Z operator acting on a single qubit.*/
   private static final int Z_CODE    = 3;        //!< Pauli Z
   /** The Hadamard operator acting on a single qubit.*/
   private static final int H_CODE    = 10;       //!< Hadamard
   /** The Sqrt(+iZ) operator acting on a single qubit.*/
   private static final int spiZ_CODE = 5;        //!< Sqrt (+iZ)
   /** The Sqrt(-iZ) operator acting on a single qubit.*/
   private static final int smiZ_CODE = 6;        //!< Sqrt (-iZ)
   /** The Sqrt(+iY) operator acting on a single qubit.*/
   private static final int spiY_CODE = 11;       //!< Sqrt (+iY)
   /** The Sqrt(-iY) operator acting on a single qubit.*/
   private static final int smiY_CODE = 9;        //!< Sqrt (-iY)
   /** The Sqrt(+iX) operator acting on a single qubit.*/
   private static final int spiX_CODE = 14;       //!< Sqrt (+iX)
   /** The Sqrt(-iX) operator acting on a single qubit.*/
   private static final int smiX_CODE = 15;       //!< Sqrt (-iX)
   /** The pi/4 phase rotation operator acting on a single qubit.*/
   //private static final int S_CODE    = smiZ_CODE; //!< Pi/4 phase rot
   /** The Hermitian conjugate of the pi/4 phase rotation acting on a single qubit.*/
   //private static final int Sh_CODE   = spiZ_CODE; //!< herm. conj. of Pi/4 phase rot
   
   // special local Clifford operators:
   /** The identity operator acting on a single qubit.*/
   public static final LocalCliffordOperator I = new LocalCliffordOperator(I_CODE);
   /** The Pauli-X operator acting on a single qubit.*/
   public static final LocalCliffordOperator X = new LocalCliffordOperator(X_CODE);
   /** The Pauli-Y operator acting on a single qubit.*/
   public static final LocalCliffordOperator Y = new LocalCliffordOperator(Y_CODE);
   /** The Pauli-Z operator acting on a single qubit.*/
   public static final LocalCliffordOperator Z = new LocalCliffordOperator(Z_CODE);
   /** The Hadamard operator acting on a single qubit.*/
   public static final LocalCliffordOperator H = new LocalCliffordOperator(H_CODE);
   /** The Sqrt(+iZ) operator acting on a single qubit.*/
   public static final LocalCliffordOperator spiZ = new LocalCliffordOperator(spiZ_CODE);
   /** The Sqrt(-iZ) operator acting on a single qubit.*/
   public static final LocalCliffordOperator smiZ = new LocalCliffordOperator(smiZ_CODE);
   /** The Sqrt(+iY) operator acting on a single qubit.*/
   public static final LocalCliffordOperator spiY = new LocalCliffordOperator(spiY_CODE);
   /** The Sqrt(-iY) operator acting on a single qubit.*/
   public static final LocalCliffordOperator smiY = new LocalCliffordOperator(smiY_CODE);
   /** The Sqrt(+iX) operator acting on a single qubit.*/
   public static final LocalCliffordOperator spiX = new LocalCliffordOperator(spiX_CODE);
   /** The Sqrt(-iX) operator acting on a single qubit.*/
   public static final LocalCliffordOperator smiX = new LocalCliffordOperator(smiX_CODE);
   /** The pi/4 phase rotation operator acting on a single qubit.*/
   public static final LocalCliffordOperator S = new LocalCliffordOperator(smiZ_CODE);
   /** The Hermitian conjugate of the pi/4 phase rotation acting on a single qubit.*/
   public static final LocalCliffordOperator S_H = new LocalCliffordOperator(spiZ_CODE);
   
   /** Identifies the operator. 0 is identity I, 1 is Pauli X,
    *  2 is Pauli Y, 3 is Pauli Z, 4 is I^B, 5 is I^C etc.
    */
   int code;
   
   /** The 24 operators of the local Clifford group <i>C</i><sub>1</sub> in matrix form.
    *  The indicies are organized as follows:
    *  <pre>
    *    matrices[code][row][column][real/imaginary part]
    *  </pre>
    *  with the index ranges <code>matrices[0..23][0..1][0..1][0..1]</code>.
    */
   private static double[][][][] matrices = new double[][][][] {
      { //  0: IA = I
         {new double[] { 1., 0.}, new double[] { 0., 0.}},
         {new double[] { 0., 0.}, new double[] { 1., 0.}}
      },
      { //  1: XA = X
         {new double[] { 0., 0.}, new double[] { 1., 0.}},
         {new double[] { 1., 0.}, new double[] { 0., 0.}}
      },
      { //  2: YA = Y
         {new double[] { 0., 0.}, new double[] { 0.,-1.}},
         {new double[] { 0., 1.}, new double[] { 0., 0.}}
      },
      { //  3: ZA = Z
         {new double[] { 1., 0.}, new double[] { 0., 0.}},
         {new double[] { 0., 0.}, new double[] {-1., 0.}}
      },
      { //  4: IB
         {new double[] { 0., 0.}, new double[] { 0., 1.}},
         {new double[] { 1., 0.}, new double[] { 0., 0.}}
      },
      { //  5: XB
         {new double[] { 1., 0.}, new double[] { 0., 0.}},
         {new double[] { 0., 0.}, new double[] { 0.,-1.}}
      },
      { //  6: YB = S
         {new double[] { 1., 0.}, new double[] { 0., 0.}},
         {new double[] { 0., 0.}, new double[] { 0., 1.}}
      },
      { //  7: ZB
         {new double[] { 0., 0.}, new double[] { 0.,-1.}},
         {new double[] { 1., 0.}, new double[] { 0., 0.}}
      },
      { //  8: IC
         {new double[] { 1./sqrt(2), 0.}, new double[] {-1./sqrt(2), 0.}},
         {new double[] {-1./sqrt(2), 0.}, new double[] {-1./sqrt(2), 0.}}
      },
      { //  9: XC
         {new double[] { 1./sqrt(2), 0.}, new double[] {-1./sqrt(2), 0.}},
         {new double[] { 1./sqrt(2), 0.}, new double[] { 1./sqrt(2), 0.}}
      },
      { // 10: YC = H
         {new double[] { 1./sqrt(2), 0.}, new double[] { 1./sqrt(2), 0.}},
         {new double[] { 1./sqrt(2), 0.}, new double[] {-1./sqrt(2), 0.}}
      },
      { // 11: ZC
         {new double[] { 1./sqrt(2), 0.}, new double[] { 1./sqrt(2), 0.}},
         {new double[] {-1./sqrt(2), 0.}, new double[] { 1./sqrt(2), 0.}}
      },
      { // 12: ID
         {new double[] { 1./sqrt(2), 0.}, new double[] { 0., 1./sqrt(2)}},
         {new double[] { 0.,-1./sqrt(2)}, new double[] {-1./sqrt(2), 0.}}
      },
      { // 13: XD
         {new double[] { 1./sqrt(2), 0.}, new double[] { 0.,-1./sqrt(2)}},
         {new double[] { 0., 1./sqrt(2)}, new double[] {-1./sqrt(2), 0.}}
      },
      { // 14: YD
         {new double[] { 1./sqrt(2), 0.}, new double[] { 0., 1./sqrt(2)}},
         {new double[] { 0., 1./sqrt(2)}, new double[] { 1./sqrt(2), 0.}}
      },
      { // 15: ZD
         {new double[] { 1./sqrt(2), 0.}, new double[] { 0.,-1./sqrt(2)}},
         {new double[] { 0.,-1./sqrt(2)}, new double[] { 1./sqrt(2), 0.}}
      },
      { // 16: IE
         {new double[] { 1./sqrt(2), 0.}, new double[] { 0.,-1./sqrt(2)}},
         {new double[] { 1./sqrt(2), 0.}, new double[] { 0., 1./sqrt(2)}}
      },
      { // 17: XE
         {new double[] { 1./sqrt(2), 0.}, new double[] { 0., 1./sqrt(2)}},
         {new double[] {-1./sqrt(2), 0.}, new double[] { 0., 1./sqrt(2)}}
      },
      { // 18: YE
         {new double[] { 1./sqrt(2), 0.}, new double[] { 0.,-1./sqrt(2)}},
         {new double[] {-1./sqrt(2), 0.}, new double[] { 0.,-1./sqrt(2)}}
      },
      { // 19: ZE = HS
         {new double[] { 1./sqrt(2), 0.}, new double[] { 0., 1./sqrt(2)}},
         {new double[] { 1./sqrt(2), 0.}, new double[] { 0.,-1./sqrt(2)}}
      },
      { // 20: IF
         {new double[] { 1./sqrt(2), 0.}, new double[] { 1./sqrt(2), 0.}},
         {new double[] { 0., 1./sqrt(2)}, new double[] { 0.,-1./sqrt(2)}}
      },
      { // 21: XF
         {new double[] { 1./sqrt(2), 0.}, new double[] { 1./sqrt(2), 0.}},
         {new double[] { 0.,-1./sqrt(2)}, new double[] { 0., 1./sqrt(2)}}
      },
      { // 22: YF
         {new double[] { 1./sqrt(2), 0.}, new double[] {-1./sqrt(2), 0.}},
         {new double[] { 0.,-1./sqrt(2)}, new double[] { 0.,-1./sqrt(2)}}
      },
      { // 23: ZF
         {new double[] { 1./sqrt(2), 0.}, new double[] {-1./sqrt(2), 0.}},
         {new double[] { 0., 1./sqrt(2)}, new double[] { 0., 1./sqrt(2)}}
      }
   };
   
   // Inline functions and tables needed by them. Consider them as private.
   private static int[][] meas_conj_tbl = new int[][] {
      {1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3}, 
      {2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1}, 
      {3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2}
   };
      
   private static int[][] mult_tbl = new int[][] {
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23},
      {1, 0, 3, 2, 6, 7, 4, 5, 11, 10, 9, 8, 13, 12, 15, 14, 19, 18, 17, 16, 22, 23, 20, 21},
      {2, 3, 0, 1, 5, 4, 7, 6, 10, 11, 8, 9, 15, 14, 13, 12, 17, 16, 19, 18, 23, 22, 21, 20},
      {3, 2, 1, 0, 7, 6, 5, 4, 9, 8, 11, 10, 14, 15, 12, 13, 18, 19, 16, 17, 21, 20, 23, 22},
      {4, 5, 6, 7, 0, 1, 2, 3, 20, 21, 22, 23, 16, 17, 18, 19, 12, 13, 14, 15, 8, 9, 10, 11},
      {5, 4, 7, 6, 2, 3, 0, 1, 23, 22, 21, 20, 17, 16, 19, 18, 15, 14, 13, 12, 10, 11, 8, 9},
      {6, 7, 4, 5, 1, 0, 3, 2, 22, 23, 20, 21, 19, 18, 17, 16, 13, 12, 15, 14, 11, 10, 9, 8},
      {7, 6, 5, 4, 3, 2, 1, 0, 21, 20, 23, 22, 18, 19, 16, 17, 14, 15, 12, 13, 9, 8, 11, 10},
      {8, 9, 10, 11, 16, 17, 18, 19, 0, 1, 2, 3, 20, 21, 22, 23, 4, 5, 6, 7, 12, 13, 14, 15},
      {9, 8, 11, 10, 18, 19, 16, 17, 3, 2, 1, 0, 21, 20, 23, 22, 7, 6, 5, 4, 14, 15, 12, 13},
      {10, 11, 8, 9, 17, 16, 19, 18, 2, 3, 0, 1, 23, 22, 21, 20, 5, 4, 7, 6, 15, 14, 13, 12},
      {11, 10, 9, 8, 19, 18, 17, 16, 1, 0, 3, 2, 22, 23, 20, 21, 6, 7, 4, 5, 13, 12, 15, 14},
      {12, 13, 14, 15, 20, 21, 22, 23, 16, 17, 18, 19, 0, 1, 2, 3, 8, 9, 10, 11, 4, 5, 6, 7},
      {13, 12, 15, 14, 22, 23, 20, 21, 19, 18, 17, 16, 1, 0, 3, 2, 11, 10, 9, 8, 6, 7, 4, 5},
      {14, 15, 12, 13, 21, 20, 23, 22, 18, 19, 16, 17, 3, 2, 1, 0, 9, 8, 11, 10, 7, 6, 5, 4},
      {15, 14, 13, 12, 23, 22, 21, 20, 17, 16, 19, 18, 2, 3, 0, 1, 10, 11, 8, 9, 5, 4, 7, 6},
      {16, 17, 18, 19, 8, 9, 10, 11, 12, 13, 14, 15, 4, 5, 6, 7, 20, 21, 22, 23, 0, 1, 2, 3},
      {17, 16, 19, 18, 10, 11, 8, 9, 15, 14, 13, 12, 5, 4, 7, 6, 23, 22, 21, 20, 2, 3, 0, 1}, 
      {18, 19, 16, 17, 9, 8, 11, 10, 14, 15, 12, 13, 7, 6, 5, 4, 21, 20, 23, 22, 3, 2, 1, 0},
      {19, 18, 17, 16, 11, 10, 9, 8, 13, 12, 15, 14, 6, 7, 4, 5, 22, 23, 20, 21, 1, 0, 3, 2},
      {20, 21, 22, 23, 12, 13, 14, 15, 4, 5, 6, 7, 8, 9, 10, 11, 0, 1, 2, 3, 16, 17, 18, 19}, 
      {21, 20, 23, 22, 14, 15, 12, 13, 7, 6, 5, 4, 9, 8, 11, 10, 3, 2, 1, 0, 18, 19, 16, 17},
      {22, 23, 20, 21, 13, 12, 15, 14, 6, 7, 4, 5, 11, 10, 9, 8, 1, 0, 3, 2, 19, 18, 17, 16},
      {23, 22, 21, 20, 15, 14, 13, 12, 5, 4, 7, 6, 10, 11, 8, 9, 2, 3, 0, 1, 17, 16, 19, 18}
   };
   
   private static int[] adj_tbl = new int[] {
      0,  1,  2,  3,
      4,  6,  5,  7,
      8, 11, 10,  9,
     12, 13, 15, 14,
     20, 22, 23, 21,
     16, 19, 17, 18
   };    
   
   private static int[][] phase_tbl = new int[][] {
      {0, 0, 0, 0},
      {0, 0, 1, 3},
      {0, 3, 0, 1},
      {0, 1, 3, 0}
   };
   
   /** Constructor, takes an integer in 0..23.
    *  @param code the code of this operator
    */
   public LocalCliffordOperator (int code) {
     this.code = code;
   }

   /** constructor, takes a sign symbol in 0..3 (for I, X, Y, Z) and a
    *  permutation symbol 0..5 (for A, B, ..., F).
    *  @param signsymb the sign symbol of this operator
    *  @param permsymb the permutation symbol of this operator
    */
   public LocalCliffordOperator (int signsymb, int permsymb) {
      //assert (signsymb < 4 && permsymb < 6);
      code = permsymb * 4 + signsymb;
   }
  
   /** Returns the name of this local Clifford operator.
    *  For instance, "YC" for Hadamard=Y^C.
    *  <pre>
    *  code  |  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23
    *  -------------------------------------------------------------------------------
    *  name  | IA XA YA ZA IB XB YB ZB IC XC YC ZC ID XD YD ZD IE XE YE ZE IF XF YF ZF
    *  usual |  I  X  Y  Z        S           H                         HS
    *  </pre>
    * @return the name of this local Clifford operator
    */
   public String getName() {
      //return "" + (paulinames[code & 0x03]) + (char) ('A' + code / 4);
      // /*
      if (code == 0) {
         return "I";
      } else if (code == 1) {
         return "X";
      } else if (code == 2) {
         return "Y";
      } else if (code == 3) {
         return "Z";
      } else if (code == 6) {
         return "S";
      } else if (code == 10) {
         return "H";
      } else if (code == 19) {
         return "HS";
      } else if (code == 20) {
         return "SH";
      } else {
         return "" + (paulinames[code & 0x03]) + (char) ('A' + code / 4);
      }
      // */
   }
   
   /** Replaces this operator by the conjugation
    *  trans &middot; <code>this</code> &middot; trans<sup>&dagger;</sup> 
    *  and returns a phase, either +1 if trans is a stabilizer, or -1.
    *  A local Clifford operator is a stabilizer if and only if it is
    *  in the Pauli group <i>P</i><sub>1</sub>.
    *  @param trans the operator which conjugates this operator
    *  @return +1 if trans is a stabilizer -1 otherwise
    *  @throws IllegalArgumentException if this gate is not a Pauli gate
    */
   public int conjugate(LocalCliffordOperator trans) {
      //If this is the identity, we don't have to do nothing
      if (this.code == I_CODE) {
         return 1; //0;
      }
      //This is meant to be used only if *this is a Pauli:
      // -> assert (code >= X.code && code <= Z.code);
      if (code < X.code && code > Z.code) {
         throw new IllegalArgumentException("Conjugation operation is not applied to a Pauli gate");
      }
      // First the sign:
      int zeta;
      if ((trans.code & 0x03) == 0 || (trans.code & 0x03) == code) {
         // zeta = + sgn pi
         // sgn pi = -1 iff trans.code >= 4 && trans.code <= 15
         if (trans.code >= 4 && trans.code <= 15) {
            zeta = 2;
         } else {
            zeta = 0;
         }
      } else {
         // zeta = - sgn pi
         // sgn pi = -1 iff trans.code >= 4 && trans.code <= 15
         if (trans.code >= 4 && trans.code <= 15) {
            zeta = 0;
         } else {
            zeta = 2;
         }
      }
      // Now the operator:
      // First check the table (to be removed!):
      //assert (loccliff_tables::meas_conj_tbl [code-X.code] [trans.code] 
      //   == trans * code * trans.adjoint()); 
      code = meas_conj_tbl [code - X_CODE] [trans.code];
      return (zeta == 2) ? -1 : 1;
   }
   
   /** Returns the Hermitian adjoint of this operator.
    *  @return the Hermitian adjoint of this operator.
    */
   public LocalCliffordOperator adjoint() {
      return new LocalCliffordOperator(adj_tbl[code]);
   }
   
   /** Returns the phase of the multiplication of op1 * op2.*/
   static int mult_phase (LocalCliffordOperator op1, LocalCliffordOperator op2) {
      //if (op1.code > Z.code || op2.code > Z.code) 
      //   throw new IllegalArgumentException("Illegal operator: " + op1 + ", " + op2);
      return phase_tbl[op1.code][op2.code];
   }
   
   /** Returns whether this operator is the Pauli-<i>X</i> gate or the 
    *  Pauli-<i>Y</i>.
    *  @return <code>true</code> if this operator is <i>X</i> or <i>Y</i>
    */
   public boolean isXY() {
      return (code == X_CODE || code == Y_CODE);
   }
   
   /** Returns whether this operator is represented by a diagonal matrix.
    *  This means that it is an operator diagonal in the computational basis.
    *  @return <code>true</code> if this operator is represented by a diagonal matrix
    */
   public boolean isDiagonal() {
      return (code == I_CODE || code == Z_CODE || code == smiZ_CODE || code == spiZ_CODE);
   }
   
   /** Returns this local Clifford operator as an array representing a 2x2 matrix 
    *  of complex numbers.
    *  The indices are organized as follows:
    *  <pre>
    *    matrix[row][column][real/imaginary part]
    *  </pre>
    *  with the index ranges <code>matrix[0..1][0..1][0..1]</code>.
    *  @return this local Clifford operator in a 2x2 complex matrix representation
    */
   public double[][][] getMatrix() {
      return matrices[code];
   }

   /** Muliplies this operator with the specified local Clifford operator.
    *  The local Clifford operators form a multiplicative group 
    *  <i>C</i><sub>1</sub>, the Clifford group.
    *  The multiplication in the Clifford group is not commutative.
    *  @param operator the operator to be multiplied from the right
    *  @return the product <code>this &middot; operator</code>
    */
   public LocalCliffordOperator multiply(LocalCliffordOperator operator) {
      return new LocalCliffordOperator(mult_tbl[code][operator.code]);
   }
   
   /** Checks whether this LC operator is equal to the specified operator.
    *  @param operator the local Clifford operator to be compared with this operator
    *  @return <code>true</code> if and only if both operators are equal
    */
   public boolean equals(LocalCliffordOperator operator) {
      return code == operator.code;
   }
   
   /** Returns this operator as a string representation of the 2x2 matrix
    *  representing it.
    *  @return the 2x2 matrix representing this operator
    */
   @Override
   public String toString() {
      //return "" + getName();
      double[][][] matrix = getMatrix();
      boolean firstCol;
      String output = getName() + " =\n";
      
      for (int i = 0; i < matrix.length; i++) {
         output += " (";
         firstCol = true;
         for (int j = 0; j < matrix[0].length; j++) {
            output += org.mathIT.numbers.Complex.toString(matrix[i][j]);
            if (firstCol) {
               output += ", ";
               firstCol = false;
            }
         }
         output += ")\n";
      }
      return output;
   }
   
   /** Check two phases for equality.*/
   //boolean equals(int ph1, int ph2) {
   //   return ((ph1 ^ ph2) & 0x03) == 0;
   //}
   
   /** Compares the specified object with this local Clifford operator.
    *  @return <code>true</code> if and only if the specified object
    *  represents the same local Clifford operator than this operator
    */
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }
      if (o == null || o.getClass() != this.getClass()) {
         return false;
      }
      return ((LocalCliffordOperator) o).code == code;
   }

   /** Returns the hash code for this local Clifford operator.
    *  @return the hash code for this local Clifford operator
    */
   @Override
   public int hashCode() {
      return (Integer.valueOf(code)).hashCode();
   }
   
   static String showHTML(double[][][] matrix) {
      java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.#");
      String output = "<table border=\"0\">";
      for(int i = 0; i < matrix.length; i++) {
         output += "<tr><td>(</td>";
         for(int j = 0; j < matrix[0].length; j++) {
            output += "<td align=\"center\">";
            output += org.mathIT.numbers.Complex.toString(new double[]{matrix[i][j][0], matrix[i][j][1]}, df);
            output += "</td>";
         }
         output += "<td>)</td></tr>";
      }
      output += "</table>";
      return output;
   }
   
   static String show(double[][][] matrix) {
      java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.0");
      String output = "";
      for(int i = 0; i < matrix.length; i++) {
         output += " ( ";
         for(int j = 0; j < matrix[0].length; j++) {
            if( matrix[i][j][0] >= 0) {
               output += " ";
            }
            //output += df.format(matrix[i][j][0]) + " + ";
            //output += df.format(matrix[i][j][1]) + " i ";
            output += org.mathIT.numbers.Complex.toString(new double[]{matrix[i][j][0], matrix[i][j][1]}, df);
            output += " ";
         }
         output += ")\n   ";
      }
      return output;
   }
   
   /* .. for test purposes ...
   public static void main(String[] args) {
      String output = "";

      LocalCliffordOperator zc = new LocalCliffordOperator(11);
      LocalCliffordOperator xd = new LocalCliffordOperator(13);
      System.out.println("hash code of "+H+"="+H.hashCode());
      System.out.println("hash code of "+zc+"="+zc.hashCode());
      System.out.println("hash code of "+xd+"="+xd.hashCode());
      System.exit(0);
      //output += zc + "x\n" + H + "= \n" + zc.multiply(H);
      //output += "\n\n" + H + "x\n" + zc + "= \n" + H.multiply(zc);

      char[] letter = {'A', 'B', 'C', 'D', 'E', 'F'};
      //for (int code = 0; code < 24; code++) {
      output += "<html><table border=1><tr><td></td><th>I</th><th>X</th><th>Y</th><th>Z</th></tr>";
      for (int i = 0; i < 6; i++) {
         output += "<tr><th>" + letter[i] + "</th>";
         for (int j = 0; j < 4; j++) {
            int code = 4*i + j;
            LocalCliffordOperator op = new LocalCliffordOperator(code);
            //output += "[" + code + "]";
            output += "<td align=\"center\">" + showHTML(op.getMatrix()) + "</td>";
            //output += "\n" + op; //.getMatrix();
            //if (code < 23) output += ",";
         }
      }
      
      output += "</table></html>";
      
 
      //javax.swing.JOptionPane.showMessageDialog(null, output, "Clifford Operators", -1);
      
      //System.exit(0);
      
      output = "Multiplication Table\n";
      String matrixOutput = output;
      
      for (int code1 = 1; code1 < 4; code1++) {
         for (int code2 = 0; code2 < 8; code2++) {
            LocalCliffordOperator op1 = new LocalCliffordOperator(code1);
            LocalCliffordOperator op2 = new LocalCliffordOperator(code2);
            output += op1.getName() + " x " + op2.getName();
            output += " = " + op1.multiply(op2).getName() + "\n";
            matrixOutput += op1.getName() + "=" + show(op1.getMatrix());
            matrixOutput += " x \n" + op2.getName() + "=" + show(op2.getMatrix());
            matrixOutput += " = \n" + op1.multiply(op2).getName() + "=" + 
               show(op1.multiply(op2).getMatrix()) + " * i^" + mult_phase(op1, op2) + "\n";
            // //if (code < 23) output += ",";
         }
      }
      
      System.out.println(output);
      System.out.println(matrixOutput);
   }
   // */
}
