/*
 * QubitVertex.java - Qubit representation of a quantum GraphRegister
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
import java.util.HashSet;
import static org.mathIT.quantum.stabilizer.LocalCliffordOperator.*;
/** 
 * A GraphRegister object maintains a list of its vertices (qubits), each 
 * described by an object of this class QubitVertex.
 * @author  Andreas de Vries
 * @version 1.0
 */
 public class QubitVertex {
   /** byprod is the vertex operator (VOp) associated with the qubit (the name 
    *  stems from the term 'byproduct operator' used for the similar concept in 
    *  the one-way quantum computer.
    */
   LocalCliffordOperator byprod;
   /** neigbors is the adjacency list for this vertex. */
   HashSet<Integer> neighbors;
   /** Upon construction, a qubit vertex is initialised with the Hadamard
    *  operation as VOp, and with empty neighbor list. This makes it represent
    *  a state |0&gt;. 
    */
   public QubitVertex() {
      byprod = H;
      neighbors = new HashSet<>();
   }
   
   /** Returns the complex 2x2 matrix representing the vertex operator 
    *  associated to this qubit vertex, according to the graph state formalism.
    *  @return the complex 2x2 matrix representing the vertex operator 
    *  associated to this qubit vertex
    */
   public double[][][] getMatrix() {
      return byprod.getMatrix();
   }
   
   /** Compares the specified object with this qubit vertex.
    *  Two qubit vertices are the same if and only if they
    *  are represented by the same local Clifford operator.
    *  @return <code>true</code> if and only if the specified object
    *  represents a physically equivalent qubit vertex than this vertex
    */
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }
      if (o == null || o.getClass() != this.getClass()) {
         return false;
      }
      return ((QubitVertex) o).byprod.equals(byprod);
   }

   /** Returns the hash code for this qubit vertex.
    *  @return the hash code for this qubit vertex
    */
   @Override
   public int hashCode() {
      return byprod.hashCode();
   }
   
   @Override
   public String toString() {
      return byprod.getName() + " -> " + neighbors;
   }
}
