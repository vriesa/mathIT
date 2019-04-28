/*
 * GraphRegister.java - Quantum register for stabilizer quantum circuits
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import static org.mathIT.quantum.stabilizer.LocalCliffordOperator.*;
/**
 *  This class represents the states of a quantum register consisting of 
 *  stabilizer states.
 *  Internally, the quantum computations are performed by the graph state
 *  formalism in which each qubit is represented by a vertex and entanglement
 *  of two qubits by an edge connecting their corresponding vertices.
 *  <p>
 *  For a detailed description of the graphic state formalism and its
 *  algorithms see the article
 *  </p>
 *  <p>
 *  S. Anders, H. J. Briegel: 
 *  'Fast simulation of Stabilizer Circuits using a Graph States Formalism',
 *  <i>Phys. Rev. A</i> <b>73</b>, 022334 (2006)
 *  DOI: 
 *  <a href="http://dx.doi.org/10%2E1103/PhysRevA%2E73%2E022334" target="_top">
 *  10.1103/PhysRevA.73.022334</a>
 *  (Preprint:
 *  <a href="http://arxiv.org/abs/quant-ph/0504117" target="_top">quant-ph/0504117</a>)
 *  </p>
 *  This class is based essentially on the C++ program graphsim.cpp, version 0.10 
 *  from 2005/01/27 written by Simon Anders, downloadable under
 *  <a href="http://homepage.uibk.ac.at/~c705213/work/graphsim.html">
 *           http://homepage.uibk.ac.at/~c705213/work/graphsim.html</a>
 *  @author  Andreas de Vries
 *  @version 1.0
 */
public class GraphRegister {
   /** A lookup table on how any LC operator can be composed from them
    *  generators spiZ (denoted 'V') and smiX (denoted 'U').*/
   private static String[] comp_tbl = {
      "UUUU", "UU", "VVUU", "VV", "VUU", "V", "VVV", "UUV", "UVU", "UVUUU", "UVVVU", "UUUVU",
      "UVV", "VVU", "UUU", "U", "VVVU", "UUVU", "VU", "VUUU", "UUUV", "UVVV", "UV", "UVUU"
   };
   
   /** 
    * Auxiliary lookup table to compute the c-phase gate.
    * Structure of the table:
    * first index: whether there was an edge between the operands before
    *   (0 = no, 1 = yes)
    * second and third index: byprod op of v1 and v2
    * third index: information to obtain:
    *    0 = whether after the cphase there is an edges
    *    1,2 = new values of the byprod ops of v1 and v2Id
    */
   private static int[][][][] cphase_tbl = { // index ranges: [2][24][24][3]
      {{{1, 0, 0}, {1, 0, 0}, {1, 0, 3}, {1, 0, 3}, {1, 0, 5}, {1, 0, 5}, 
         {1, 0, 6}, {1, 0, 6}, {0, 3, 8}, {0, 3, 8}, {0, 0, 10}, {0, 0, 10}, 
         {1, 0, 3}, {1, 0, 3}, {1, 0, 0}, {1, 0, 0}, {1, 0, 6}, {1, 0, 6}, 
         {1, 0, 5}, {1, 0, 5}, {0, 0, 10}, {0, 0, 10}, {0, 3, 8}, {0, 3, 8}}, 
        {{1, 0, 0}, {1, 0, 0}, {1, 0, 3}, {1, 0, 3}, {1, 0, 5}, {1, 0, 5}, 
         {1, 0, 6}, {1, 0, 6}, {0, 2, 8}, {0, 2, 8}, {0, 0, 10}, {0, 0, 10}, 
         {1, 0, 3}, {1, 0, 3}, {1, 0, 0}, {1, 0, 0}, {1, 0, 6}, {1, 0, 6}, 
         {1, 0, 5}, {1, 0, 5}, {0, 0, 10}, {0, 0, 10}, {0, 2, 8}, {0, 2, 8}}, 
        {{1, 2, 3}, {1, 0, 1}, {1, 0, 2}, {1, 2, 0}, {1, 0, 4}, {1, 2, 6}, 
         {1, 2, 5}, {1, 0, 7}, {0, 0, 8}, {0, 0, 8}, {0, 2, 10}, {0, 2, 10}, 
         {1, 0, 2}, {1, 0, 2}, {1, 0, 1}, {1, 0, 1}, {1, 0, 7}, {1, 0, 7}, 
         {1, 0, 4}, {1, 0, 4}, {0, 2, 10}, {0, 2, 10}, {0, 0, 8}, {0, 0, 8}}, 
        {{1, 3, 0}, {1, 0, 1}, {1, 0, 2}, {1, 3, 3}, {1, 0, 4}, {1, 3, 5}, 
         {1, 3, 6}, {1, 0, 7}, {0, 0, 8}, {0, 0, 8}, {0, 3, 10}, {0, 3, 10}, 
         {1, 0, 2}, {1, 0, 2}, {1, 0, 1}, {1, 0, 1}, {1, 0, 7}, {1, 0, 7}, 
         {1, 0, 4}, {1, 0, 4}, {0, 3, 10}, {0, 3, 10}, {0, 0, 8}, {0, 0, 8}}, 
        {{1, 4, 3}, {1, 4, 3}, {1, 4, 0}, {1, 4, 0}, {1, 4, 6}, {1, 4, 6}, 
         {1, 4, 5}, {1, 4, 5}, {0, 6, 8}, {0, 6, 8}, {0, 4, 10}, {0, 4, 10}, 
         {1, 4, 0}, {1, 4, 0}, {1, 4, 3}, {1, 4, 3}, {1, 4, 5}, {1, 4, 5}, 
         {1, 4, 6}, {1, 4, 6}, {0, 4, 10}, {0, 4, 10}, {0, 6, 8}, {0, 6, 8}}, 
        {{1, 5, 0}, {1, 5, 0}, {1, 5, 3}, {1, 5, 3}, {1, 5, 5}, {1, 5, 5}, 
         {1, 5, 6}, {1, 5, 6}, {0, 6, 8}, {0, 6, 8}, {0, 5, 10}, {0, 5, 10}, 
         {1, 5, 3}, {1, 5, 3}, {1, 5, 0}, {1, 5, 0}, {1, 5, 6}, {1, 5, 6}, 
         {1, 5, 5}, {1, 5, 5}, {0, 5, 10}, {0, 5, 10}, {0, 6, 8}, {0, 6, 8}}, 
        {{1, 6, 0}, {1, 5, 1}, {1, 5, 2}, {1, 6, 3}, {1, 5, 4}, {1, 6, 5}, 
         {1, 6, 6}, {1, 5, 7}, {0, 5, 8}, {0, 5, 8}, {0, 6, 10}, {0, 6, 10}, 
         {1, 5, 2}, {1, 5, 2}, {1, 5, 1}, {1, 5, 1}, {1, 5, 7}, {1, 5, 7}, 
         {1, 5, 4}, {1, 5, 4}, {0, 6, 10}, {0, 6, 10}, {0, 5, 8}, {0, 5, 8}}, 
        {{1, 6, 0}, {1, 4, 2}, {1, 4, 1}, {1, 6, 3}, {1, 4, 7}, {1, 6, 5}, 
         {1, 6, 6}, {1, 4, 4}, {0, 4, 8}, {0, 4, 8}, {0, 6, 10}, {0, 6, 10}, 
         {1, 4, 1}, {1, 4, 1}, {1, 4, 2}, {1, 4, 2}, {1, 4, 4}, {1, 4, 4}, 
         {1, 4, 7}, {1, 4, 7}, {0, 6, 10}, {0, 6, 10}, {0, 4, 8}, {0, 4, 8}}, 
        {{0, 8, 3}, {0, 8, 2}, {0, 8, 0}, {0, 8, 0}, {0, 8, 6}, {0, 8, 6}, 
         {0, 8, 5}, {0, 8, 4}, {0, 8, 8}, {0, 8, 8}, {0, 8, 10}, {0, 8, 10}, 
         {0, 8, 0}, {0, 8, 0}, {0, 8, 2}, {0, 8, 2}, {0, 8, 4}, {0, 8, 4}, 
         {0, 8, 6}, {0, 8, 6}, {0, 8, 10}, {0, 8, 10}, {0, 8, 8}, {0, 8, 8}}, 
        {{0, 8, 3}, {0, 8, 2}, {0, 8, 0}, {0, 8, 0}, {0, 8, 6}, {0, 8, 6}, 
         {0, 8, 5}, {0, 8, 4}, {0, 8, 8}, {0, 8, 8}, {0, 8, 10}, {0, 8, 10}, 
         {0, 8, 0}, {0, 8, 0}, {0, 8, 2}, {0, 8, 2}, {0, 8, 4}, {0, 8, 4}, 
         {0, 8, 6}, {0, 8, 6}, {0, 8, 10}, {0, 8, 10}, {0, 8, 8}, {0, 8, 8}}, 
        {{0, 10, 0}, {0, 10, 0}, {0, 10, 2}, {0, 10, 3}, {0, 10, 4}, {0, 10, 5}, 
         {0, 10, 6}, {0, 10, 6}, {0, 10, 8}, {0, 10, 8}, {0, 10, 10}, {0, 10, 10}, 
         {0, 10, 2}, {0, 10, 2}, {0, 10, 0}, {0, 10, 0}, {0, 10, 6}, {0, 10, 6}, 
         {0, 10, 4}, {0, 10, 4}, {0, 10, 10}, {0, 10, 10}, {0, 10, 8}, {0, 10, 8}}, 
        {{0, 10, 0}, {0, 10, 0}, {0, 10, 2}, {0, 10, 3}, {0, 10, 4}, {0, 10, 5}, 
         {0, 10, 6}, {0, 10, 6}, {0, 10, 8}, {0, 10, 8}, {0, 10, 10}, {0, 10, 10}, 
         {0, 10, 2}, {0, 10, 2}, {0, 10, 0}, {0, 10, 0}, {0, 10, 6}, {0, 10, 6}, 
         {0, 10, 4}, {0, 10, 4}, {0, 10, 10}, {0, 10, 10}, {0, 10, 8}, {0, 10, 8}}, 
        {{1, 2, 3}, {1, 0, 1}, {1, 0, 2}, {1, 2, 0}, {1, 0, 4}, {1, 2, 6}, 
         {1, 2, 5}, {1, 0, 7}, {0, 0, 8}, {0, 0, 8}, {0, 2, 10}, {0, 2, 10}, 
         {1, 0, 2}, {1, 0, 2}, {1, 0, 1}, {1, 0, 1}, {1, 0, 7}, {1, 0, 7}, 
         {1, 0, 4}, {1, 0, 4}, {0, 2, 10}, {0, 2, 10}, {0, 0, 8}, {0, 0, 8}}, 
        {{1, 2, 3}, {1, 0, 1}, {1, 0, 2}, {1, 2, 0}, {1, 0, 4}, {1, 2, 6}, 
         {1, 2, 5}, {1, 0, 7}, {0, 0, 8}, {0, 0, 8}, {0, 2, 10}, {0, 2, 10}, 
         {1, 0, 2}, {1, 0, 2}, {1, 0, 1}, {1, 0, 1}, {1, 0, 7}, {1, 0, 7}, 
         {1, 0, 4}, {1, 0, 4}, {0, 2, 10}, {0, 2, 10}, {0, 0, 8}, {0, 0, 8}}, 
        {{1, 0, 0}, {1, 0, 0}, {1, 0, 3}, {1, 0, 3}, {1, 0, 5}, {1, 0, 5}, 
         {1, 0, 6}, {1, 0, 6}, {0, 2, 8}, {0, 2, 8}, {0, 0, 10}, {0, 0, 10}, 
         {1, 0, 3}, {1, 0, 3}, {1, 0, 0}, {1, 0, 0}, {1, 0, 6}, {1, 0, 6}, 
         {1, 0, 5}, {1, 0, 5}, {0, 0, 10}, {0, 0, 10}, {0, 2, 8}, {0, 2, 8}}, 
        {{1, 0, 0}, {1, 0, 0}, {1, 0, 3}, {1, 0, 3}, {1, 0, 5}, {1, 0, 5}, 
         {1, 0, 6}, {1, 0, 6}, {0, 2, 8}, {0, 2, 8}, {0, 0, 10}, {0, 0, 10}, 
         {1, 0, 3}, {1, 0, 3}, {1, 0, 0}, {1, 0, 0}, {1, 0, 6}, {1, 0, 6}, 
         {1, 0, 5}, {1, 0, 5}, {0, 0, 10}, {0, 0, 10}, {0, 2, 8}, {0, 2, 8}}, 
        {{1, 6, 0}, {1, 4, 2}, {1, 4, 1}, {1, 6, 3}, {1, 4, 7}, {1, 6, 5}, 
         {1, 6, 6}, {1, 4, 4}, {0, 4, 8}, {0, 4, 8}, {0, 6, 10}, {0, 6, 10}, 
         {1, 4, 1}, {1, 4, 1}, {1, 4, 2}, {1, 4, 2}, {1, 4, 4}, {1, 4, 4}, 
         {1, 4, 7}, {1, 4, 7}, {0, 6, 10}, {0, 6, 10}, {0, 4, 8}, {0, 4, 8}}, 
        {{1, 6, 0}, {1, 4, 2}, {1, 4, 1}, {1, 6, 3}, {1, 4, 7}, {1, 6, 5}, 
         {1, 6, 6}, {1, 4, 4}, {0, 4, 8}, {0, 4, 8}, {0, 6, 10}, {0, 6, 10}, 
         {1, 4, 1}, {1, 4, 1}, {1, 4, 2}, {1, 4, 2}, {1, 4, 4}, {1, 4, 4}, 
         {1, 4, 7}, {1, 4, 7}, {0, 6, 10}, {0, 6, 10}, {0, 4, 8}, {0, 4, 8}}, 
        {{1, 4, 3}, {1, 4, 3}, {1, 4, 0}, {1, 4, 0}, {1, 4, 6}, {1, 4, 6}, 
         {1, 4, 5}, {1, 4, 5}, {0, 6, 8}, {0, 6, 8}, {0, 4, 10}, {0, 4, 10}, 
         {1, 4, 0}, {1, 4, 0}, {1, 4, 3}, {1, 4, 3}, {1, 4, 5}, {1, 4, 5}, 
         {1, 4, 6}, {1, 4, 6}, {0, 4, 10}, {0, 4, 10}, {0, 6, 8}, {0, 6, 8}}, 
        {{1, 4, 3}, {1, 4, 3}, {1, 4, 0}, {1, 4, 0}, {1, 4, 6}, {1, 4, 6}, 
         {1, 4, 5}, {1, 4, 5}, {0, 6, 8}, {0, 6, 8}, {0, 4, 10}, {0, 4, 10}, 
         {1, 4, 0}, {1, 4, 0}, {1, 4, 3}, {1, 4, 3}, {1, 4, 5}, {1, 4, 5}, 
         {1, 4, 6}, {1, 4, 6}, {0, 4, 10}, {0, 4, 10}, {0, 6, 8}, {0, 6, 8}}, 
        {{0, 10, 0}, {0, 10, 0}, {0, 10, 2}, {0, 10, 3}, {0, 10, 4}, {0, 10, 5}, 
         {0, 10, 6}, {0, 10, 6}, {0, 10, 8}, {0, 10, 8}, {0, 10, 10}, {0, 10, 10}, 
         {0, 10, 2}, {0, 10, 2}, {0, 10, 0}, {0, 10, 0}, {0, 10, 6}, {0, 10, 6}, 
         {0, 10, 4}, {0, 10, 4}, {0, 10, 10}, {0, 10, 10}, {0, 10, 8}, {0, 10, 8}}, 
        {{0, 10, 0}, {0, 10, 0}, {0, 10, 2}, {0, 10, 3}, {0, 10, 4}, {0, 10, 5}, 
         {0, 10, 6}, {0, 10, 6}, {0, 10, 8}, {0, 10, 8}, {0, 10, 10}, {0, 10, 10}, 
         {0, 10, 2}, {0, 10, 2}, {0, 10, 0}, {0, 10, 0}, {0, 10, 6}, {0, 10, 6}, 
         {0, 10, 4}, {0, 10, 4}, {0, 10, 10}, {0, 10, 10}, {0, 10, 8}, {0, 10, 8}}, 
        {{0, 8, 3}, {0, 8, 2}, {0, 8, 0}, {0, 8, 0}, {0, 8, 6}, {0, 8, 6}, 
         {0, 8, 5}, {0, 8, 4}, {0, 8, 8}, {0, 8, 8}, {0, 8, 10}, {0, 8, 10}, 
         {0, 8, 0}, {0, 8, 0}, {0, 8, 2}, {0, 8, 2}, {0, 8, 4}, {0, 8, 4}, 
         {0, 8, 6}, {0, 8, 6}, {0, 8, 10}, {0, 8, 10}, {0, 8, 8}, {0, 8, 8}}, 
        {{0, 8, 3}, {0, 8, 2}, {0, 8, 0}, {0, 8, 0}, {0, 8, 6}, {0, 8, 6}, 
         {0, 8, 5}, {0, 8, 4}, {0, 8, 8}, {0, 8, 8}, {0, 8, 10}, {0, 8, 10}, 
         {0, 8, 0}, {0, 8, 0}, {0, 8, 2}, {0, 8, 2}, {0, 8, 4}, {0, 8, 4}, 
         {0, 8, 6}, {0, 8, 6}, {0, 8, 10}, {0, 8, 10}, {0, 8, 8}, {0, 8, 8}}}, 
       {{{0, 0, 0}, {0, 3, 0}, {0, 3, 2}, {0, 0, 3}, {0, 3, 4}, {0, 0, 5}, 
         {0, 0, 6}, {0, 3, 6}, {1, 5, 23}, {1, 5, 22}, {1, 5, 21}, {1, 5, 20}, 
         {0, 5, 2}, {0, 6, 2}, {0, 5, 0}, {0, 6, 0}, {0, 6, 6}, {0, 5, 6}, 
         {0, 6, 4}, {0, 5, 4}, {1, 5, 10}, {1, 5, 11}, {1, 5, 8}, {1, 5, 9}}, 
        {{0, 0, 3}, {0, 2, 2}, {0, 2, 0}, {0, 0, 0}, {0, 2, 6}, {0, 0, 6}, 
         {0, 0, 5}, {0, 2, 4}, {1, 4, 23}, {1, 4, 22}, {1, 4, 21}, {1, 4, 20}, 
         {0, 6, 0}, {0, 4, 0}, {0, 6, 2}, {0, 4, 2}, {0, 4, 4}, {0, 6, 4}, 
         {0, 4, 6}, {0, 6, 6}, {1, 4, 10}, {1, 4, 11}, {1, 4, 8}, {1, 4, 9}}, 
        {{0, 2, 3}, {0, 0, 2}, {0, 0, 0}, {0, 2, 0}, {0, 0, 6}, {0, 2, 6}, 
         {0, 2, 5}, {0, 0, 4}, {1, 4, 22}, {1, 4, 23}, {1, 4, 20}, {1, 4, 21}, 
         {0, 4, 0}, {0, 6, 0}, {0, 4, 2}, {0, 6, 2}, {0, 6, 4}, {0, 4, 4}, 
         {0, 6, 6}, {0, 4, 6}, {1, 4, 11}, {1, 4, 10}, {1, 4, 9}, {1, 4, 8}}, 
        {{0, 3, 0}, {0, 0, 0}, {0, 0, 2}, {0, 3, 3}, {0, 0, 4}, {0, 3, 5}, 
         {0, 3, 6}, {0, 0, 6}, {1, 5, 22}, {1, 5, 23}, {1, 5, 20}, {1, 5, 21}, 
         {0, 6, 2}, {0, 5, 2}, {0, 6, 0}, {0, 5, 0}, {0, 5, 6}, {0, 6, 6}, 
         {0, 5, 4}, {0, 6, 4}, {1, 5, 11}, {1, 5, 10}, {1, 5, 9}, {1, 5, 8}}, 
        {{0, 4, 3}, {0, 6, 2}, {0, 6, 0}, {0, 4, 0}, {0, 6, 6}, {0, 4, 6}, 
         {0, 4, 5}, {0, 6, 4}, {1, 0, 21}, {1, 0, 20}, {1, 0, 23}, {1, 0, 22}, 
         {0, 0, 0}, {0, 2, 0}, {0, 0, 2}, {0, 2, 2}, {0, 2, 4}, {0, 0, 4}, 
         {0, 2, 6}, {0, 0, 6}, {1, 0, 8}, {1, 0, 9}, {1, 0, 10}, {1, 0, 11}}, 
        {{0, 5, 0}, {0, 6, 0}, {0, 6, 2}, {0, 5, 3}, {0, 6, 4}, {0, 5, 5}, 
         {0, 5, 6}, {0, 6, 6}, {1, 0, 22}, {1, 0, 23}, {1, 0, 20}, {1, 0, 21}, 
         {0, 3, 2}, {0, 0, 2}, {0, 3, 0}, {0, 0, 0}, {0, 0, 6}, {0, 3, 6}, 
         {0, 0, 4}, {0, 3, 4}, {1, 0, 11}, {1, 0, 10}, {1, 0, 9}, {1, 0, 8}}, 
        {{0, 6, 0}, {0, 5, 0}, {0, 5, 2}, {0, 6, 3}, {0, 5, 4}, {0, 6, 5}, 
         {0, 6, 6}, {0, 5, 6}, {1, 0, 23}, {1, 0, 22}, {1, 0, 21}, {1, 0, 20}, 
         {0, 0, 2}, {0, 3, 2}, {0, 0, 0}, {0, 3, 0}, {0, 3, 6}, {0, 0, 6}, 
         {0, 3, 4}, {0, 0, 4}, {1, 0, 10}, {1, 0, 11}, {1, 0, 8}, {1, 0, 9}}, 
        {{0, 6, 3}, {0, 4, 2}, {0, 4, 0}, {0, 6, 0}, {0, 4, 6}, {0, 6, 6}, 
         {0, 6, 5}, {0, 4, 4}, {1, 0, 20}, {1, 0, 21}, {1, 0, 22}, {1, 0, 23}, 
         {0, 2, 0}, {0, 0, 0}, {0, 2, 2}, {0, 0, 2}, {0, 0, 4}, {0, 2, 4}, 
         {0, 0, 6}, {0, 2, 6}, {1, 0, 9}, {1, 0, 8}, {1, 0, 11}, {1, 0, 10}}, 
        {{1, 22, 6}, {1, 20, 5}, {1, 20, 6}, {1, 22, 5}, {1, 20, 3}, {1, 22, 0}, 
         {1, 22, 3}, {1, 20, 0}, {0, 0, 0}, {0, 0, 2}, {0, 2, 2}, {0, 2, 0}, 
         {0, 6, 6}, {0, 4, 4}, {0, 6, 4}, {0, 4, 6}, {0, 4, 2}, {0, 6, 0}, 
         {0, 4, 0}, {0, 6, 2}, {0, 2, 4}, {0, 2, 6}, {0, 0, 6}, {0, 0, 4}}, 
        {{1, 22, 5}, {1, 20, 6}, {1, 20, 5}, {1, 22, 6}, {1, 20, 0}, {1, 22, 3}, 
         {1, 22, 0}, {1, 20, 3}, {0, 2, 0}, {0, 2, 2}, {0, 0, 2}, {0, 0, 0}, 
         {0, 4, 6}, {0, 6, 4}, {0, 4, 4}, {0, 6, 6}, {0, 6, 2}, {0, 4, 0}, 
         {0, 6, 0}, {0, 4, 2}, {0, 0, 4}, {0, 0, 6}, {0, 2, 6}, {0, 2, 4}}, 
        {{1, 20, 6}, {1, 20, 7}, {1, 20, 4}, {1, 20, 5}, {1, 20, 1}, {1, 20, 0}, 
         {1, 20, 3}, {1, 20, 2}, {0, 2, 2}, {0, 2, 0}, {0, 0, 0}, {0, 0, 2}, 
         {0, 6, 4}, {0, 4, 6}, {0, 6, 6}, {0, 4, 4}, {0, 4, 0}, {0, 6, 2}, 
         {0, 4, 2}, {0, 6, 0}, {0, 0, 6}, {0, 0, 4}, {0, 2, 4}, {0, 2, 6}}, 
        {{1, 20, 5}, {1, 20, 4}, {1, 20, 7}, {1, 20, 6}, {1, 20, 2}, {1, 20, 3}, 
         {1, 20, 0}, {1, 20, 1}, {0, 0, 2}, {0, 0, 0}, {0, 2, 0}, {0, 2, 2}, 
         {0, 4, 4}, {0, 6, 6}, {0, 4, 6}, {0, 6, 4}, {0, 6, 0}, {0, 4, 2}, 
         {0, 6, 2}, {0, 4, 0}, {0, 2, 6}, {0, 2, 4}, {0, 0, 4}, {0, 0, 6}}, 
        {{0, 2, 5}, {0, 0, 6}, {0, 0, 4}, {0, 2, 6}, {0, 0, 0}, {0, 2, 3}, 
         {0, 2, 0}, {0, 0, 2}, {0, 6, 6}, {0, 6, 4}, {0, 4, 6}, {0, 4, 4}, 
         {1, 16, 18}, {1, 16, 19}, {1, 16, 16}, {1, 16, 17}, {1, 16, 12}, 
         {1, 16, 13}, {1, 16, 14}, {1, 16, 15}, {0, 4, 2}, {0, 4, 0}, {0, 6, 2}, 
         {0, 6, 0}}, {{0, 2, 6}, {0, 0, 4}, {0, 0, 6}, {0, 2, 5}, {0, 0, 2}, 
         {0, 2, 0}, {0, 2, 3}, {0, 0, 0}, {0, 4, 4}, {0, 4, 6}, {0, 6, 4}, 
         {0, 6, 6}, {1, 16, 17}, {1, 16, 16}, {1, 16, 19}, {1, 16, 18}, 
         {1, 16, 15}, {1, 16, 14}, {1, 16, 13}, {1, 16, 12}, {0, 6, 0}, {0, 6, 2}, 
         {0, 4, 0}, {0, 4, 2}}, {{0, 0, 5}, {0, 2, 6}, {0, 2, 4}, {0, 0, 6}, 
         {0, 2, 0}, {0, 0, 3}, {0, 0, 0}, {0, 2, 2}, {0, 4, 6}, {0, 4, 4}, 
         {0, 6, 6}, {0, 6, 4}, {1, 16, 16}, {1, 16, 17}, {1, 16, 18}, {1, 16, 19}, 
         {1, 16, 14}, {1, 16, 15}, {1, 16, 12}, {1, 16, 13}, {0, 6, 2}, {0, 6, 0}, 
         {0, 4, 2}, {0, 4, 0}}, {{0, 0, 6}, {0, 2, 4}, {0, 2, 6}, {0, 0, 5}, 
         {0, 2, 2}, {0, 0, 0}, {0, 0, 3}, {0, 2, 0}, {0, 6, 4}, {0, 6, 6}, 
         {0, 4, 4}, {0, 4, 6}, {1, 16, 19}, {1, 16, 18}, {1, 16, 17}, {1, 16, 16}, 
         {1, 16, 13}, {1, 16, 12}, {1, 16, 15}, {1, 16, 14}, {0, 4, 0}, {0, 4, 2}, 
         {0, 6, 0}, {0, 6, 2}}, {{0, 6, 6}, {0, 4, 4}, {0, 4, 6}, {0, 6, 5}, 
         {0, 4, 2}, {0, 6, 0}, {0, 6, 3}, {0, 4, 0}, {0, 2, 4}, {0, 2, 6}, 
         {0, 0, 4}, {0, 0, 6}, {1, 12, 16}, {1, 12, 17}, {1, 12, 18}, {1, 12, 19}, 
         {1, 12, 14}, {1, 12, 15}, {1, 12, 12}, {1, 12, 13}, {0, 0, 0}, {0, 0, 2}, 
         {0, 2, 0}, {0, 2, 2}}, {{0, 6, 5}, {0, 4, 6}, {0, 4, 4}, {0, 6, 6}, 
         {0, 4, 0}, {0, 6, 3}, {0, 6, 0}, {0, 4, 2}, {0, 0, 6}, {0, 0, 4}, 
         {0, 2, 6}, {0, 2, 4}, {1, 12, 19}, {1, 12, 18}, {1, 12, 17}, {1, 12, 16}, 
         {1, 12, 13}, {1, 12, 12}, {1, 12, 15}, {1, 12, 14}, {0, 2, 2}, {0, 2, 0}, 
         {0, 0, 2}, {0, 0, 0}}, {{0, 4, 6}, {0, 6, 4}, {0, 6, 6}, {0, 4, 5}, 
         {0, 6, 2}, {0, 4, 0}, {0, 4, 3}, {0, 6, 0}, {0, 0, 4}, {0, 0, 6}, 
         {0, 2, 4}, {0, 2, 6}, {1, 12, 18}, {1, 12, 19}, {1, 12, 16}, {1, 12, 17}, 
         {1, 12, 12}, {1, 12, 13}, {1, 12, 14}, {1, 12, 15}, {0, 2, 0}, {0, 2, 2}, 
         {0, 0, 0}, {0, 0, 2}}, {{0, 4, 5}, {0, 6, 6}, {0, 6, 4}, {0, 4, 6}, 
         {0, 6, 0}, {0, 4, 3}, {0, 4, 0}, {0, 6, 2}, {0, 2, 6}, {0, 2, 4}, 
         {0, 0, 6}, {0, 0, 4}, {1, 12, 17}, {1, 12, 16}, {1, 12, 19}, {1, 12, 18}, 
         {1, 12, 15}, {1, 12, 14}, {1, 12, 13}, {1, 12, 12}, {0, 0, 2}, {0, 0, 0}, 
         {0, 2, 2}, {0, 2, 0}}, {{1, 10, 5}, {1, 8, 6}, {1, 8, 5}, {1, 10, 6}, 
         {1, 8, 0}, {1, 10, 3}, {1, 10, 0}, {1, 8, 3}, {0, 4, 2}, {0, 4, 0}, 
         {0, 6, 0}, {0, 6, 2}, {0, 2, 4}, {0, 0, 6}, {0, 2, 6}, {0, 0, 4}, 
         {0, 0, 0}, {0, 2, 2}, {0, 0, 2}, {0, 2, 0}, {0, 6, 6}, {0, 6, 4}, 
         {0, 4, 4}, {0, 4, 6}}, {{1, 10, 6}, {1, 8, 5}, {1, 8, 6}, {1, 10, 5}, 
         {1, 8, 3}, {1, 10, 0}, {1, 10, 3}, {1, 8, 0}, {0, 6, 2}, {0, 6, 0}, 
         {0, 4, 0}, {0, 4, 2}, {0, 0, 4}, {0, 2, 6}, {0, 0, 6}, {0, 2, 4}, 
         {0, 2, 0}, {0, 0, 2}, {0, 2, 2}, {0, 0, 0}, {0, 4, 6}, {0, 4, 4}, 
         {0, 6, 4}, {0, 6, 6}}, {{1, 8, 5}, {1, 8, 4}, {1, 8, 7}, {1, 8, 6}, 
         {1, 8, 2}, {1, 8, 3}, {1, 8, 0}, {1, 8, 1}, {0, 6, 0}, {0, 6, 2}, 
         {0, 4, 2}, {0, 4, 0}, {0, 2, 6}, {0, 0, 4}, {0, 2, 4}, {0, 0, 6}, 
         {0, 0, 2}, {0, 2, 0}, {0, 0, 0}, {0, 2, 2}, {0, 4, 4}, {0, 4, 6}, 
         {0, 6, 6}, {0, 6, 4}}, {{1, 8, 6}, {1, 8, 7}, {1, 8, 4}, {1, 8, 5}, 
         {1, 8, 1}, {1, 8, 0}, {1, 8, 3}, {1, 8, 2}, {0, 4, 0}, {0, 4, 2}, 
         {0, 6, 2}, {0, 6, 0}, {0, 0, 6}, {0, 2, 4}, {0, 0, 4}, {0, 2, 6}, 
         {0, 2, 2}, {0, 0, 0}, {0, 2, 0}, {0, 0, 2}, {0, 6, 4}, {0, 6, 6}, 
         {0, 4, 6}, {0, 4, 4}}}      
   };
   
   /** Array list storing all the qubits, represented as QubitVertex objects. */
   ArrayList<QubitVertex> vertices;
   
   /**
    *  Creates a register of <i>n</i> qubits, initialized to the state |0&gt;.
    *  A register of size <i>n</i> enables a total of <i>q</i> = 2<sup><i>n</i></sup> 
    *  quantum states as the computational basis.
    *  @param size the number of qubits this register consists of
    */
   public GraphRegister(int size) {
      vertices = new ArrayList<>(size);
      for(int i = 0; i < size; i++) {
         vertices.add(new QubitVertex());
      }
   }
   
   /** Add an edge to the graph underlying the state.*/
   private void add_edge(int v1, int v2) {
      //assert(v1 != v2);
      if (v1 == v2) {
         throw new IllegalArgumentException("Edge with identical vertices "+v1);
      }
      vertices.get(v1).neighbors.add(v2);
      vertices.get(v2).neighbors.add(v1);
   }
   
   private boolean del_edge(int v1, int v2) {
      vertices.get(v1).neighbors.remove(v2);
      vertices.get(v2).neighbors.remove(v1);
      return true;
   }
   
   /** Toggle an edge to the graph underlying the state,
    *  i.e., add it if not present, and delete it if present.
    */
   private void toggle_edge(int v1, int v2) {
      if (vertices.get(v1).neighbors.remove(v2)) {
         vertices.get(v2).neighbors.remove(v1);
      } else {
         add_edge(v1, v2);
      }
   }
   
   /** Toggles the edges between the vertex sets vs1 and vs2.
    *  The function takes extra care not to invert an edge twice. If vs1 and
    *  vs2 are disjunct, this cannot happen and we do not need the function.
    *  If vs1 == v2s, we can do without, too. 
    */
   private void toggle_edges(HashSet<Integer> vs1, HashSet<Integer> vs2) {
      HashSet<Edge> procd_edges = new HashSet<>();
      Edge edge;
      for (int i : vs1) {
         for (int j : vs2) {
            if ((i != j) && !procd_edges.contains(edge = new Edge(i, j))) {
               procd_edges.add(edge);
               toggle_edge(i, j);
            }
         }
      }
   }
   
   /** Measure the bare graph state in the Z basis.*/
   private int graph_Z_measure (int v, int force) {
      int res;
      if (force == -1) {
         res = (Math.random() < .5) ? 0 : 1;
      } else {
         res = force;
      }

      // Copy neighborhoods: (necessary since otherwise toggling edges results in a ConcurrentModificationException)
      HashSet<Integer> nbg  = new HashSet<>(vertices.get(v).neighbors);
      for (int i : nbg) {
         if (del_edge(v, i) && res == 1) {
            vertices.get(i).byprod = vertices.get(i).byprod.multiply(Z);
         }
      }
      if (res == 0) {
         vertices.get(v).byprod = vertices.get(v).byprod.multiply(H);
      } else {
         vertices.get(v).byprod = vertices.get(v).byprod.multiply(X.multiply(H));
      }
      return res;
   }
   
   /** Measure the bare graph state in the Y basis.*/
   private int graph_Y_measure (int v, int force) {
      int res;
      if (force != 0 && force != 1) {
         res = (Math.random() < .5) ? 0 : 1;
      } else {
         res = force;
      }
      ArrayList<Integer> vnbg = new ArrayList<>(vertices.get(v).neighbors);
      for (int i : vnbg) {
         if (res != 0) {
            vertices.get(i).byprod = vertices.get(i).byprod.multiply(spiZ);
            //vertices.get(i).byprod = spiZ.multiply(vertices.get(i).byprod);
         } else {
            vertices.get(i).byprod = vertices.get(i).byprod.multiply(smiZ);
            //vertices.get(i).byprod = smiZ.multiply(vertices.get(i).byprod);
         }
      }
      vnbg.add(v); // Now, vnbg is the set of v and its neighbours.
      for (int i = 0; i < vnbg.size(); i++) {
         for (int j = i; j < vnbg.size(); j++) {
            if (vnbg.get(i) != vnbg.get(j)) {  // <-- ???
               toggle_edge(vnbg.get(i), vnbg.get(j));
            }
         }
      }

      if (res == 0) {
         vertices.get(v).byprod = S.multiply(vertices.get(v).byprod);
      } else {
         // Measurement result: -|0y>
         vertices.get(v).byprod = S.adjoint().multiply(vertices.get(v).byprod);
      }
      return res;
   }
   
   /** Measure the bare graph state in the X basis.*/
   private int graph_X_measure (int v, int force) {
      if (vertices.get(v).neighbors.isEmpty()) {
         //not entangled qubit => result always 0:
         return 0;
      }
      
      // entangled qubit => let's get on with the complicated procedure
      // throw a die:
      int res;
      if (force != 0 && force != 1) {
         res = (Math.random() < .5) ? 0 : 1;
      } else {
         res = force;
      }
      int vb = vertices.get(v).neighbors.iterator().next(); // the choosen vertex
      // preparation step: store the neighborhood of v and vb
      HashSet<Integer> vn  = new HashSet<>(vertices.get(v).neighbors);
      HashSet<Integer> vbn = new HashSet<>(vertices.get(vb).neighbors);
      // First, put the byproduct ops: 
      if (res == 0) {
         // measured a |+>:
         // spiY on vb
         vertices.get(vb).byprod = vertices.get(vb).byprod.multiply(spiY);
         // Z on all in nbg(v) \ nbg(vb) \ {vb}
         for (int i : vertices.get(v).neighbors) {
            if (i != vb && vertices.get(vb).neighbors.contains(i)) {
               vertices.get(i).byprod = vertices.get(i).byprod.multiply(Z);
            }
         }
      } else {
         // measured a |->:
         // smiY on vb, and Z on v:
         vertices.get(vb).byprod = vertices.get(vb).byprod.multiply(smiY);
         vertices.get(v).byprod = Z.multiply(vertices.get(v).byprod);
         // Z on all in nbg(vb) \ nbg(v) \ {v}
         for (int i : vertices.get(vb).neighbors) {
            if (i != v && vertices.get(v).neighbors.contains(i)) {
               vertices.get(i).byprod = vertices.get(i).byprod.multiply(Z);
            }
         }
      }     
      // Toggling the edges in three steps
      // STEP 1: complement with Edges (nbg(v), nbg(vb)):
      toggle_edges(vn, vbn);
      // STEP 2: complement with the complete subgraph induced by the 
      // intersection of nbg(v) and nbg(vb):
      // First, make the intersection
      ArrayList<Integer> isc = new ArrayList<>();
      for (int i : vn) {
         if (vbn.contains(i)) {
            isc.add(i);
         }
      }
      // Now, toggle the edges
      for (int i = 0; i < isc.size(); i++) {
         for (int j = i; j < isc.size(); j++) {
            if (! isc.get(i).equals(isc.get(j))) {
               toggle_edge(isc.get(i), isc.get(j));
            }
         }
      }
      // STEP 3: Toggle all edges from vb to nbg(v) \ {vb}
      for (int i : vn) {
         if (i != vb) {
            toggle_edge(vb, i);
         }
      }
      return res;
   }
   
   /** 
    * Do neighborhood inversions to reduce the VOp of vertex v to the identity.
    * 'avoid' is avoided as swapping partner, i.e. the swapping partner will not 
    * be 'avoid' unless this is the only neighbor of v. 
    * @throws IllegalArgumentException if no neighbor is available
    */
   private boolean remove_byprod_op (int v, int avoid) {
      // Of course, we need a neighborhood
      if (vertices.get(v).neighbors.isEmpty()) {
         throw new IllegalArgumentException("Isolated vertex.");
      }
      // This will be the swapping partner:
      Iterator<Integer> it = vertices.get(v).neighbors.iterator();
      int vb = it.next();
      if (vb == avoid) {
         // Is there an alternative to 'avoid'? If so, use it.
         if (it.hasNext()) {
            vb = it.next();
         }
      }

      String comp = comp_tbl[vertices.get(v).byprod.code];
      for (int pos = comp.length() - 1; pos >= 0; pos--) {
         if (comp.charAt(pos) == 'U') {
            // A U will vanish if we do an inversion on v
            invertNeighborhood(v);
         } else {
            //assert (comp[pos] == 'V');
            // For this we need to invert on a neighbor of v
            invertNeighborhood(vb);
         }
      }
      // Now, we should have Id left
      //assert (vertices.get(v).byprod == Id); 
      return true;
   }
   
   /** Use the cphase look-up table. This is called by cphase after VOps that do not
    *  commute with the cphase gate have been removed as far as possible. 
    */
   private void cphase_with_table (int v1, int v2) {
      ConnectionInfo ci = getConnectionInfo(v1, v2);
      int op1 = vertices.get(v1).byprod.code;
      int op2 = vertices.get(v2).byprod.code;
      // The table must only be used if a vertex has either no
      // non-operand neighbors, or a diagonal byprod op
      //assert ((!ci.non1) || vertices.get(v1).byprod.is_diagonal());
      //assert ((!ci.non2) || vertices.get(v2).byprod.is_diagonal());
      if ( (ci.non1) && !vertices.get(v1).byprod.isDiagonal()) {
         throw new RuntimeException("Wrong operator, cphase table cannot be used");
      }
      if ( (ci.non2) && !vertices.get(v2).byprod.isDiagonal()) {
         throw new RuntimeException("Wrong operator, cphase table cannot be used");
      }
      
      if (cphase_tbl[ci.wasEdge ? 1 : 0][op1][op2][0] != 0) {
         add_edge(v1, v2);
      } else {
         del_edge(v1, v2);
      }
      vertices.get(v1).byprod.code = cphase_tbl[ci.wasEdge ? 1 : 0][op1][op2][1];
      vertices.get(v2).byprod.code = cphase_tbl[ci.wasEdge ? 1 : 0][op1][op2][2];
      // The condition above must also hold afterwards:	
      ci = getConnectionInfo (v1, v2);
      //assert ((!ci.non1) || vertices.get(v1).byprod.is_diagonal());
      //assert ((!ci.non2) || vertices.get(v2).byprod.is_diagonal());
   }
   
   /** Check whether the qubits are connected to each other and to non-operand vertices.*/
   private ConnectionInfo getConnectionInfo (int v1, int v2) {
      ConnectionInfo ci = new ConnectionInfo();
      ci.wasEdge = vertices.get(v1).neighbors.contains(v2);
      if (! ci.wasEdge) {
         ci.non1 = vertices.get(v1).neighbors.size() >= 1; 
         ci.non2 = vertices.get(v2).neighbors.size() >= 1;
      } else {
         ci.non1 = vertices.get(v1).neighbors.size() >= 2; 
         ci.non2 = vertices.get(v2).neighbors.size() >= 2;
      }
      return ci;
   }
   
   /** Returns the quantum register (in state vecor representation) 
    * represented by this graph register.
    * @return the register state represented by this graph register state
    */
   public Register getRegister() {
      int q = (1 << vertices.size()); // note: (1 << n) == 2^n
      final double BY_SQRT_Q = 1/sqrt(q);
      double[] real = new double[q];
      double[] imag = new double[q];
      double[][][][] matrices = new double[vertices.size()][2][2][2];
      double tmp;
      int[] ia = new int[matrices.length];  // auxiliary indices
      int[] ja = new int[matrices.length];  // auxiliary indices
      int rows, cols; // auxiliary rows and columns of the resulting matrix
      double[] c = new double[2]; // stores each complex entry of the matrix
      
      /* Vector representing the diagonal of the matrix given by the c-Z entangled qubits.*/
      boolean[] entangleZ = new boolean[q]; // default entries: false
      
      for (int i = 0; i < vertices.size(); i++ ) {
         matrices[i] = vertices.get(i).getMatrix();
         
         for (int j : vertices.get(i).neighbors) {
            if (j > i) {
               continue;
            } // take each edge only once

            for (int k = 0; k < q; k++) {
               // if the bit (j-1) is not met by k, then go on...
               if ( (k & ((1 << i) | (1 << j))) < ((1 << i) | (1 << j)) ) {
                  continue;
               }
               entangleZ[k] = !entangleZ[k];
            }
         }
      }
      
      // compute the tensor product of all operator matrices:
      for(int i = 0; i < q; i++) {
         for(int j = 0; j < q; j++) {
            rows = q;
            cols = q;
            // determine the indices of the entry in each matrix corresponding to (i,j):
            for (int k = matrices.length - 1; k >= 0; k--) { // operators backward ...
               rows /= matrices[k].length;
               cols /= matrices[k][0].length;
               ia[k] = (i / rows) % matrices[k].length;
               ja[k] = (j / cols) % matrices[k][0].length;
            }
            
            // compute the current entry of the tensor product:
            c[0] = matrices[0][ia[0]][ja[0]][0];
            c[1] = matrices[0][ia[0]][ja[0]][1];            
            for (int k = 1; k < matrices.length; k++) {
               tmp  = c[0];
               c[0] = matrices[k][ia[k]][ja[k]][0] * c[0] 
                    - matrices[k][ia[k]][ja[k]][1] * c[1];
               c[1] = matrices[k][ia[k]][ja[k]][0] * c[1] 
                    + matrices[k][ia[k]][ja[k]][1] * tmp; // <- take the old c[0]!
            }
            
            // sum over all columns j to obtain the i-th qubit value:
            tmp = entangleZ[j] ? -BY_SQRT_Q : BY_SQRT_Q;
            real[i] += c[0] * tmp;
            imag[i] += c[1] * tmp;
         }
      }
      
      for(int i = 0; i < real.length; i++) {
         if (abs(real[i]) < Register.ACCURACY) {
            real[i] = 0;
         }
         if (abs(imag[i]) < Register.ACCURACY) {
            imag[i] = 0;
         }
      }
      
      //Register register = new Register(vertices.size());
      Register register = new Register(vertices.size(), false);
      register.setReal(real);
      register.setImaginary(imag);
      return register;
   }
   
   /* --- for test purposes ... --------------------
   static String show(double[][][] matrix) {
      java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.#");
      String output = "";
      for(int i = 0; i < matrix.length; i++) {
         output += " ( ";
         for(int j = 0; j < matrix[0].length; j++) {
            if( matrix[i][j][0] >= 0) output += " ";
            //output += df.format(matrix[i][j][0]) + " + ";
            //output += df.format(matrix[i][j][1]) + " i ";
            output += org.mathIT.numbers.Complex.toString(new double[]{matrix[i][j][0], matrix[i][j][1]}, df);
            output += " ";
         }
         output += ")\n";
      }
      return output;
   }
   // --- test -------------------------------------- */   
      
   /** Apply the specified local (i.e., single-qubit) operator on vertex v. 
    *  @param v the qubit on which the gate is to be applied
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    *  @param operator the local Clifford operator which is applied to the 
    *  vertex operator of qubit vertex v
    */
   void apply(int v, LocalCliffordOperator operator) {
      vertices.get(v).byprod = operator.multiply(vertices.get(v).byprod);
   }
   
   /** Apply a Hadamard gate on vertex v.
    *  @param v the qubit on which the gate is to be applied
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    */
   public void hadamard(int v) {
      apply(v, H);
   }
   
   /** Applies a Pauli-<i>X</i>, or "bit flip", on vertex v. 
    *  @param v the qubit on which the gate is to be applied
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    *  @see #bitFlip(int)
    */
   public void xPauli(int v) {
      apply(v, X);
   }
   
   /** Applies a Pauli-<i>Y</i> on vertex v. 
    *  @param v the qubit on which the gate is to be applied
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    */
   public void yPauli(int v) {
      apply(v, Y);
   }
   
   /** Applies a Pauli-<i>Z</i>, or "phase flip", on vertex v. 
    *  @param v the qubit on which the gate is to be applied
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    *  @see #phaseFlip(int)
    */
   public void zPauli(int v) {
      apply(v, Z);
   }
   
   /** Applies an  <i>S</i> gate, or "phase gate", on vertex v. 
    *  @param v the qubit on which the gate is to be applied
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    */
   public void sGate(int v) {
      apply(v, S);
   }
   
   /** Applies an inverse <i>S</i> gate on vertex v. 
    *  @param v the qubit on which the gate is to be applied
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    *  @see #sGate(int)
    */
   public void inverseSGate(int v) {
      apply(v, spiZ);
   }
   
   /** Applies a bitflip gate, i.e., a Pauli X, on vertex v. 
    *  @param v the qubit on which the gate is to be applied
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    *  @see #xPauli(int)
    */
   public void bitFlip(int v) {
      apply(v, X);
   }
   
   /** Apply a phase flip gate (i.e. a Pauli-<i>Z</i>) on vertex v.
    *  @param v the qubit on which the gate is to be applied
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    */
   public void phaseFlip(int v) {
      apply(v, Z);
   }
   
   /** Apply a phase gate <i>S</i> on qubit v. Phase gate, or phase rotation, 
    *  means the gate <i>S</i> = |0&gt;&lt;0| + i |1&gt;&lt;1|.
    *  @param v the qubit on which the gate is to be applied
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    */
   public void phaseRot(int v) {
      apply(v, S);
   }
   
   /** 
    * Does a conditional phase gate c-<i>S</i> between the two qubits.
    * @param v1 the control qubit 
    * (in an <i>n</i> qubit register, v1 = 0, 1, ..., <i>n</i> - 1
    * @param v2 the target qubit
    * (in an <i>n</i> qubit register, v2 = 0, 1, ..., <i>n</i> - 1
    */
   public void cPhase(int v1, int v2) {
      // If there are non-operand neighbors, we can use neighborhood inversion
      // to remove the byprod operators. 
      // These will store whether the operand vertices have nonoperand neighbors.
      ConnectionInfo ci = getConnectionInfo (v1, v2);
      
      if (ci.non1) {
         remove_byprod_op(v1, v2);
      }
      ci = getConnectionInfo(v1, v2);
      if (ci.non2) {
         remove_byprod_op(v2, v1);
      }
      ci = getConnectionInfo(v1, v2);
      if (ci.non1 && !vertices.get(v1).byprod.isDiagonal()) {
         // this can happen if v1 was first skipped
         remove_byprod_op(v1, v2);
      }
      cphase_with_table(v1, v2);
   }
   
   /** 
    * Performs a controlled NOT gate between the vertices vc (control) and vt (target).
    * @param vc the control qubit 
    * (in an <i>n</i> qubit register, vc = 0, 1, ..., <i>n</i> - 1)
    * @param vt the target qubit 
    * (in an <i>n</i> qubit register, vc = 0, 1, ..., <i>n</i> - 1)
    */
   public void cNOT(int vc, int vt) {
      hadamard(vt);
      cPhase(vc, vt);
      hadamard(vt);
   }
   
   /** 
    * Measures qubit v with the Pauli Z gate as basis operator and returns the measured
    * value.
    * @param v the measured qubit 
    * (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    * @return the measured value
    */
   public int measure(int v) {
      return measure(v, Z);
   }
   
   /** 
    * Measures qubit v in the specified basis operator and returns the measured
    * value.
    * The measurement basis must be a Pauli operator, i.e., has to be equal to 
    * either <i>X</i>, <i>Y</i>, or <i>Z</i>.
    * @param v the measured qubit 
    * (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    * @param basis the Pauli gate which serves as measurement basis
    * @return the qubit value as measured in the specified basis
    */
   public int measure(int v, LocalCliffordOperator basis) {
      return measure(v, basis, -1);
   }
   
   /** 
    * Measures qubit v in the specified basis operator and returns the measured
    * value.
    * The measurement basis must be a Pauli operator, i.e., has to be equal to 
    * either <i>X</i>, <i>Y</i>, or <i>Z</i>.
    * If you want to know whether the result was 
    * choosen at random or determined by the state, pass a <code>Boolean</code> 
    * object in which this information will be written. If you want to force the result
    * to be a certain value, pass 0 or 1 to 'force'. This only works, if the 
    * result is not determined. If it is, 'force' is ignored.
    * @param v the measured qubit 
    * (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    * @param basis the Pauli gate which serves as measurement basis 
    */
   int measure(int v, LocalCliffordOperator basis, int force) {
      //assert (basis.code >= X.code && basis.code <= Z.code);
      if (basis.code < 1 || basis.code > 3) {
         throw new IllegalArgumentException("Measurement basis is not a Pauli gate");
      }
      //assert (force >= -1 && force <= 1);
      if (force < -1 || force > 1) {
         throw new IllegalArgumentException("Parameter force is not -1, 0, or 1: "+force);
      }
      
      //LocalCliffordOperator basis_orig = new LocalCliffordOperator(basis.code);  // <- needed to check correctness ...
      int rp = basis.conjugate(vertices.get(v).byprod.adjoint()); // phase
      //assert (rp == rp_p1 || rp == rp_m1); // <=> rp == +1 or -1
      if (force != -1 && rp == -1) {
         force = force ^ 0x01;
      }
      int res;
      switch (basis.code) {
         case 1 /* X */: res = graph_X_measure(v, force); break;
         case 2 /* Y */: res = graph_Y_measure(v, force); break;
         case 3 /* Z */: res = graph_Z_measure(v, force); break;
         default: throw new IllegalArgumentException("Measurement basis is not a Pauli gate"); //exit(1);
      }
      if (rp == -1) {
         //res = (res == 0) ? 1 : 0;
         res ^= 1;
      } else {
         //assert (rp == rp_p1);  // i.e., rp == 1
         if (rp != 1) {
            throw new RuntimeException("Illegal phase unequal to 1: "+rp);
         }
      }
      // check: the measured vertex should be singled out:
      //assert (vertices.get(v).neighbors.size() == 0);
      if (vertices.get(v).neighbors.size() > 0) {
         throw new RuntimeException("Vertex "+v+" is still connected to other qubits after its measurement!");
      }
      // Check that the vertex is now in the correct eigenstate:
      //LocalCliffordOperator assert_op = X;
      //assert (assert_op.conjugate (vertices.get(v).byprod) == (res ? rp_m1 : rp_p1));
      
      //assert (assert_op == basis_orig);
      return res;
   }
   
   /** Does a neighborhood inversion (i.e., local complementation) about vertex v.
    *  This changes the state's graph representation but not the state itself, as the
    *  necessary correction to the VOps are applied.
    *  @param v the control qubit 
    *  (in an <i>n</i> qubit register, v = 0, 1, ..., <i>n</i> - 1)
    */
   void invertNeighborhood(int v) {
      // Invert the neighborhood:
      Integer[] vn = vertices.get(v).neighbors.toArray(new Integer[0]);
      for (int i = 0; i < vn.length; i++) {
         for (int j = i; j < vn.length; j++) {
            if (vn[i] != vn[j]) {
               //cerr << "toggling " << *i << "," << *j << endl;
               toggle_edge(vn[i], vn[j]);
            }
         }
         // and adjust the local Cliffords:
         vertices.get(vn[i]).byprod = vertices.get(vn[i]).byprod.multiply(spiZ.adjoint());
      }
      // finally, adjust the local Clifford of v:
      vertices.get(v).byprod = vertices.get(v).byprod.multiply(smiX.adjoint());
   }
   
   String print_adj_list() {
      return vertices.toString();
   }
   
   /** Compares the specified object with this local Clifford operator.
    *  The method returns true if and only if the specified object represents a 
    *  graph register state which is the same than this register.
    *  @param o the specified reference with which to compare
    *  @return <code>true</code> if and only if the specified object
    *  is the same graph register state than this operator
    */
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }
      if (o == null || o.getClass() != this.getClass()) {
         return false;
      }
      boolean equal = true;
      GraphRegister r = (GraphRegister) o;
      for (int k = 0; k < vertices.size() && equal; k++) {
         equal &= vertices.get(k).equals(r.vertices.get(k));
      }
      return equal;
   }
   
   /** Returns the hash code for this graph register state.
    *  @return the hash code for this graph register state
    */
   @Override
   public int hashCode() {
      int hash = 7;
      
      for (int k = 0; k < vertices.size(); k++) {
         double[][][] matrix = vertices.get(k).getMatrix();
         for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
               // Several operators represent the same physical register state, e.g., H=ZC
               hash = 31*hash + (Double.valueOf(matrix[i][j][0])).hashCode();
               hash = 31*hash + (Double.valueOf(matrix[i][j][1])).hashCode();
            }
         }
      }
      
      return hash;
   }
   
   /** Returns a string representation of this register.
    *  It prints its vertices, each one representing a qubit
    *  and being shown with its associated vertex operator and its
    *  vacancy list, i.e., the list of its neighbor vertices
    *  represnting those qubits which are entangled.
    *  @return a string representation of this register
    */
   @Override
   public String toString() {
      String output = "";
      for (int i=0; i < vertices.size(); i++) {
         output += "Vertex " + i + ": " + vertices.get(i) + "\n";
      }
      return output;
   }
   
   /** For test purposes...*/
   /*
   public static void main (String[] args) {
      int n = 3;
      
      if ( args.length > 0 && args[0] != null )  n = Integer.parseInt( args[0] );

      //double norm = 1; ///SQRT2;// / 2f; // SQRT2
      GraphRegister register = new GraphRegister(n);
      Register reg = new Register(n, false);
      
      System.out.println("|00>:\n" + register.getRegister()+", hash code="+register.hashCode());
      //System.out.println("|00>:\n" + reg);
      //System.out.println(" hashcode: rg=" + reg.hashCode() + ", gr=" + register.getRegister().hashCode());
      register.apply(0,H);
      register.cNOT(0,1);
      reg.hadamard(1);
      reg.cNOT(1,2);
      System.out.println("H_0 cNOT^0_1 |00>:\n" + register + register.getRegister()+", hash code="+register.hashCode()+"\n");
      System.out.println("H_0 cNOT^0_1 |00>:\n" + reg+", hash code="+register.hashCode()+"\n");
      //System.out.println(" hashcode: rg=" + reg.hashCode() + ", gr=" + register.getRegister().hashCode());
      register.apply(0,X);
      reg.xPauli(1);
      //System.out.println("X_0 H_0 |00>:\n" + register + register.getRegister()+"\n");
      register.apply(0,H);
      reg.hadamard(1);
      System.out.println("H_0 X_0 H_0 |00>:\n" + register + register.getRegister()+", hash code="+register.hashCode());
      System.out.println("|00>:\n" + reg);
      //System.out.println(" hashcode: rg=" + reg.hashCode() + ", gr=" + register.getRegister().hashCode());
      //
      
      //System.out.println("Measurement of qubit 3 in Z: " + register.measure(3, Z) + "\n" + register);
      //System.out.println(register.getRegister());
      
      //System.exit(0);
            
      // gstest.cpp:------------------

      int nbr_of_qubits = 6;
      GraphRegister gr = new GraphRegister(nbr_of_qubits);
      Register rg = new Register(nbr_of_qubits, false);
      //if ( !rg.toString().equals(gr.getRegister().toString()) ) {
      if ( !rg.equals(gr.getRegister()) ) {
         System.out.println("Registers schon am Start falsch!\n rg: " + rg + "\n gr:\n" + gr.getRegister());
         System.out.println("--- HashCodes: " + rg.hashCode() + ", " + gr.getRegister().hashCode());
         System.exit(1); //break;
      }
      
      for (int iter = 0; iter < 500; iter++) {
         int qubit = (int) (random() * nbr_of_qubits);
         int what  = (int) (random() * 15);
         String output = "\ni="+iter+", Register before:\nrg:\n" + rg + "\ngr:\n" + gr.getRegister() + "\n"+gr; 
         output += "--- Hashcodes: rg="+rg.hashCode() + ", gr=" + gr.getRegister().hashCode()+"\nnachher:";
//what=1; qubit=1;
         
         switch (what) {
            case 0: 
               gr.hadamard(qubit); 
               rg.hadamard(qubit+1); 
               //if ( rg.hashCode() != gr.getRegister().hashCode() ) {
               if ( !rg.equals(gr.getRegister()) ) {
                  System.out.println("Registers are not equal after what="+what+" und q="+qubit+"!" + output+"\n rg:\n" + rg + "\n gr:\n" + gr.getRegister());
                  System.out.println("--- Hashcodes: rg="+rg.hashCode() + ", gr=" + gr.getRegister().hashCode());
                  System.exit(1); //break;
               }
               break;
            case 1: 
               //gr.apply(qubit, S);
               //rg.rotate(new int[]{qubit+1}, "z", PI/4); 
               //if ( rg.hashCode() != gr.getRegister().hashCode() ) {
               if ( !rg.equals(gr.getRegister()) ) {
                  System.out.println("Registers are not equal after what="+what+" und q="+qubit+"!" + output+"\n rg:\n" + rg + "\n gr:\n" + gr.getRegister());
                  System.out.println("--- Hashcodes: rg="+rg.hashCode() + ", gr=" + gr.getRegister().hashCode());
                  System.exit(1); //break;
               }
               break;
            case 2: 
               gr.apply(qubit, Z); 
               rg.zPauli(qubit+1);
               //if ( rg.hashCode() != gr.getRegister().hashCode() ) {
               if ( !rg.equals(gr.getRegister()) ) {
                  System.out.println("Registers are not equal after what="+what+" und q="+qubit+"!" + output+"\n rg:\n" + rg + "\n gr:\n" + gr.getRegister());
                  System.out.println("--- Hashcodes: rg="+rg.hashCode() + ", gr=" + gr.getRegister().hashCode());
                  System.exit(1); //break;
               }
               break;
            case 3: 
               gr.apply(qubit, X); 
               rg.xPauli(qubit+1);
               //if ( rg.hashCode() != gr.getRegister().hashCode() ) {
               if ( !rg.equals(gr.getRegister()) ) {
                  System.out.println("Registers are not equal after what="+what+" und q="+qubit+"!" + output+"\n rg:\n" + rg + "\n gr:\n" + gr.getRegister());
                  System.out.println("--- Hashcodes: rg="+rg.hashCode() + ", gr=" + gr.getRegister().hashCode());
                  System.exit(1); //break;
               }
               break;
            default:
            int qubit2 = (int) (Math.random() * nbr_of_qubits);
            if (qubit2 == qubit) {
               continue;
            }
            
            what = (random() <= .5) ? -1 : -2;
            if (what == -1) {
               gr.cPhase(qubit,qubit2); //gr.apply(qubit,H); gr.cPhase(qubit, qubit2); gr.apply(qubit,H);
               rg.hadamard(qubit2+1); rg.cNOT(qubit+1, qubit2+1); rg.hadamard(qubit2+1);
            } else {
               gr.cNOT(qubit, qubit2);
               rg.cNOT(qubit+1, qubit2+1);
            }
            //if ( rg.hashCode() != gr.getRegister().hashCode() ) {
            if ( !rg.equals(gr.getRegister()) ) {
               System.out.println("Registers are not equal after what="+what+" und c="+qubit+", t="+qubit2+"!" + output+"\n rg:\n" + rg + "\n gr:\n" + gr + gr.getRegister());
               System.out.println("--- Hashcodes: rg="+rg.hashCode() + ", gr=" + gr.getRegister().hashCode());
               System.exit(1); //break;
               //break;
            }
         }
         //if (iter % 100 == 0) {
         //   System.out.println(iter);
         //}
      }
      System.out.println();
      
      //System.out.println(gr);
      
      //System.out.println("Measurement of Z: " + gr.measure(1, Z));

      //System.out.println(gr);
      // gstest.cpp ------------------

      System.exit(0);
   }
   // */
}


/** This structure is needed only by toggle_edges. */
class Edge { //: public pair<int, int> {
   int first;
   int second;
   
   public Edge (int a, int b) {
      if (a < b) {
         first = a; second = b;
      } else {
         first = b; second = a;
      }
   }
   
   @Override
   public int hashCode() {
      return first << 16 ^ second;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Edge other = (Edge) obj;
      if (this.first != other.first) {
         return false;
      }
      if (this.second != other.second) {
         return false;
      }
      return true;
   }
}

/** This class is only for internal use for the cphase functions. */
class ConnectionInfo {
   boolean wasEdge;
   boolean non1;
   boolean non2;
};

