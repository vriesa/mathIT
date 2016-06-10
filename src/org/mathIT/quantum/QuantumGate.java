/*
 * QuantumGate.java - Class of the jQuantum computer simulator
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
package org.mathIT.quantum;
import org.mathIT.util.FunctionParser;
/**
 * This class encodes an elementary quantum gate. It is the central class for the design of
 * quantum circuits and is used to determine the sequence of quantum operations.
 * The initial state of a quantum circuit is considered as a special quantum gate,
 * the "initialState" gate. It is a classical state for which each qubit either
 * has the value |0&gt; or |1&gt;.
 *
 * @author  Andreas de Vries
 * @version 1.5
 */
public class QuantumGate implements java.io.Serializable {
    private static final long serialVersionUID = 1488467558;
    /** specifies the name of this quantum gate. It is uniquely related to a quantum operation.*/
    public String name;
    /** specifies the index of qubits on which the gate operates. 
     *  For a controlled gate, the last entry always is the target qubit.
     *  However, for a Grover operator gate, this field is abused to
     *  specify the "needle", i.e., the value which is known to the oracle
     *  but which can only be found after successive Grover iterations.
     */
    public int[] qubits;
    /** specifies if the gate operates on the y-register. If not, the x-register is acted on.*/
    public boolean yRegister;
    /** the parsed function for the function evaluation gate <i>U<sub>f</sub></i>. 
     *  It is null for all other gates.
     */
    public FunctionParser function;
    /** The axis of rotation, if this gate represents a qubit rotation. */
    public String axis;
    /** rotation angle, as the integral part <i>n</i> of <i>&#x03C0;</i>. 
     *  More accurately, <i>n</i> = <i>&#x03C0;</i>/<i>&#x03C6;</i> where <i>&#x03C6;</i>
     *  is the rotation angle in radians.
     */
    public int phiAsPartOfPi;
    
    /** Constructs a generic quantum gate.
     * @param name the name of the quantum gate; possible values are determined by
     * {@link Circuit#gatelist}
     * @param qubits array representing the state of the qubits
     * @param yRegister flag indicating whether the gate applies to the y-register
     * @throws IllegalArgumentException if the gate name is unknown
     */
    public QuantumGate(String name, int[] qubits, boolean yRegister) {
        if (!isValid(name)) {
          throw new IllegalArgumentException("Unknown quantum gate " + name);
       }
        
        this.name = name;
        this.qubits = qubits;
        this.yRegister = yRegister;
        this.function = null;
    }

    /** Constructor for a quantum gate evaluating a function.
     * @param name the name of the quantum gate; possible values are determined by
     * {@link Circuit#gatelist}
     * @param qubits array representing the state of the qubits
     * @param function the function to be evaluated
     * @param yRegister flag indicating whether the gate applies to the y-register
     * @throws IllegalArgumentException if the gate name is unknown or the function is null
     */
    public QuantumGate(String name, int[] qubits, FunctionParser function, boolean yRegister) {
        if (!isValid(name)) {
          throw new IllegalArgumentException("Unknown quantum gate " + name);
       }
        if (name.equals("Function") && function == null) {
          throw new IllegalArgumentException("Unspecified Function");
       }
        
        this.name = name;
        this.qubits = qubits;
        this.yRegister = yRegister;
        this.function = function;
    }
    
    /** Constructor for a rotation operation.
     * @param name the name of the quantum gate; possible values are determined by
     * {@link Circuit#gatelist}
     * @param qubits array representing the state of the qubits
     * @param axis the axis of rotation, i.e., "x", "y", or "z"
     * @param phiAsPartOfPi rotation angle, as the integral part <i>n</i> of <i>&#x03C0;</i>;
     *  more accurately, <i>n</i> = <i>&#x03C0;</i>/<i>&#x03C6;</i> where <i>&#x03C6;</i>
     *  is the rotation angle in radians.
     * @param yRegister indicates whether this gate belngs to the <i>y</i>-register
     * @throws IllegalArgumentException if the gate name is unknown
     */
    public QuantumGate(String name, int[] qubits, String axis, int phiAsPartOfPi, boolean yRegister) {
        if (!isValid(name)) {
          throw new IllegalArgumentException("Unknown quantum gate " + name);
        }
        
        this.name = name;
        this.qubits = qubits;
        this.axis = axis;
        this.phiAsPartOfPi = phiAsPartOfPi;
        this.yRegister = yRegister;
    }
    
    /**
     * Returns the name of this quantum gate.
     * @return the name of this quantum gate
     */
    public String getName() {
        return name;
    }
    
    /** Returns an array of the indices of the qubits on which this gate operates.
     *  For gates acting on a single qubit, such as a Hadamard or a Pauli gate,
     *  the array has length 1 and contains the index of its qubit.
     *  For a controlled gate, the last entry always is the target qubit.
     *  @return the indices of the qubits this quantum gate operates upon
     */
    public int[] getQubits() {
        return qubits;
    }
    
    /** Checks whether the specified string represents a valid quantum gate.
     *  @param name the name to be checked
     *  @return <code>true</code> if and only if the name represents a valid quantum gate
     */
    public static boolean isValid(String name) {
        boolean valid = false;
        for (int i = 0; !valid && i < Circuit.gatelist.length; i++) {
           valid = name.equals(Circuit.gatelist[i]);
        }
        return valid;
    }
}
