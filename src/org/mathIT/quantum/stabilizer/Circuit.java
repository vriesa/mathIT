/*
 * Circuit.java - Class to implement a quantum circuit
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
import java.util.ArrayList;
import java.util.HashSet;
import org.mathIT.quantum.NoWireException;
import org.mathIT.quantum.QuantumGate;
import org.mathIT.util.FunctionParser;
/**
 * This class enables the storage of quantum circuits and offers methods to
 * execute quantum operations on it. A quantum circuit consists
 * of a sequence of quantum gates.
 *
 * @author  Andreas de Vries
 * @version 1.2
 */
public class Circuit extends ArrayList<QuantumGate> implements java.io.Serializable {
   private static final long serialVersionUID = -1847355447L; //hash code of "Circuit" 
   /**
    * In this array the names of all possible quantum gates are stored. 
    * These possible values are:
      "initialState",
      "Hadamard", "cNOT", "Pauli-X", "Pauli-Y", "Pauli-Z", "S", "T", "sqrt-X", 
      "invS", "invT",
      "Toffoli", "QFT", "invQFT", "Function", "Rotation", "Grover", "Measurement"
    * .
    */
   public static String[] gatelist = {
      "initialState",
      "Hadamard", "cNOT", "Pauli-X", "Pauli-Y", "Pauli-Z", "S", "T", "sqrt-X", 
      "invS", "invT",
      "Toffoli", "QFT", "invQFT", "Function", "Rotation", "Grover", "Measurement"
   };
   /** The <i>x</i>-register.*/
   private Register xRegister;
   /** The <i>y</i>-register.*/
   private Register yRegister;
   /** The number of qubits of the <i>x</i>-register.*/
   private int xRegisterSize = 0;
   /** The number of qubits of the <i>y</i>-register.*/
   private int yRegisterSize = 0;
   /** The number of qubits of both the <i>x</i>-register and the <i>y</i>-register.*/
   private int numberOfWires = 0;
   /** Number of the gate to be executed next during the execution of this quantum circuit.
    *  If the circuit is in its initial state, the number is 0.
    */
   private int nextGateNumber = 0;
   
   /** Creates an empty quantum circuit.*/
   public Circuit() {
      super();
   }
   
   /** Yields the size of the <i>x</i>-register of this quantum circuit.
    *  @return the size of the <i>x</i>-register
    */
   public int getXRegisterSize() {
      return xRegisterSize;
   }
   
   /** Yields the size of the <i>y</i>-register of this quantum circuit.
    *  @return the size of the <i>y</i>-register
    */
   public int getYRegisterSize() {
      return yRegisterSize;
   }
   
   /** Returns the number of wires of this quantum circuit.
    *  @return the number of quantum wires 
    */
   public int getNumberOfWires() {
      return numberOfWires;
   }
   
   /** Sets the initial states of the qubits of this quantum circuit.
    *  @param initialQubits the initial states of the qubits of this quantum circuit
    */
   public void setInitialQubits(int[] initialQubits) {
      setInitialState(initialQubits);
   }
   
   /** Yields the current gate in this quantum circuit which is to be executed next.
    *  @return the next gate
    */
   public QuantumGate getNextGate() {
      return get(nextGateNumber);
   }
   
   /** Yields the current gate in this quantum circuit which is to be executed next.
    *  @return the next gate
    */
   public QuantumGate getPreviousGate() {
      return get(nextGateNumber - 1);
   }
   
   /** Returns the number of the gate to be executed next in this quantum circuit.
    *  The initial state is counted as gate number 0.
    *  @return the next gate number
    */
   public int getNextGateNumber() {
      return nextGateNumber;
   }
   
   /** Returns the <i>x</i>-register of this quantum circuit.
    *  @return the <i>x</i>-register of this quantum circuit
    */
   public Register getXRegister() {
      return xRegister;
   }
   
   /** Returns the <i>y</i>-register of this quantum circuit.
    *  @return the <i>y</i>-register of this quantum circuit
    */
   public Register getYRegister() {
      return yRegister;
   }
   
   /** Provides the entire list of quantum gates of this quantum circuit.
    *  @return the entire list of quantum gates of this circuit
    */
   public ArrayList<QuantumGate> getGates() {
     return new ArrayList<>(this);
   }
   
   /** Constructs and initializes this quantum cicuit according to the 
    *  specified sizes of the registers and the list of quantum gates.
    *  Any previously stored quantum gates of this circuit will be deleted.
    *  @param registerSizes the sizes of the registers, where 
    *  <code>registerSizes[0]</code> denotes the size of the <i>x</i>-register, and
    *  <code>registerSizes[1]</code> denotes the size of the <i>y</i>-register
    *  @param gates the list of quantum gates this quantum gate consists of
    */
   public void initialize(int[] registerSizes, ArrayList<QuantumGate> gates) {
      this.xRegisterSize = registerSizes[0];
      this.yRegisterSize = registerSizes[1];
      this.numberOfWires = xRegisterSize + yRegisterSize;
      
      clear();
      addAll(gates);
      if(size() > 0) {
         nextGateNumber = 1;
      }
   
      initializeRegisters();
   }
   
   /** Initializes this quantum circuit and deletes all quantum gates.
    * @param xRegisterSize size of the x-register
    * @param yRegisterSize size of the y-register
    * @param initialState initial state of the register
    * @return true if and only if initialization has been successful
    */
   public boolean initialize(int xRegisterSize, int yRegisterSize, int initialState) {
      this.xRegisterSize = xRegisterSize;
      this.yRegisterSize = yRegisterSize;
      this.numberOfWires = xRegisterSize + yRegisterSize;
      
      this.clear();
      nextGateNumber = 0;
      
      xRegister = new Register( xRegisterSize );
      yRegister = new Register( yRegisterSize );
      
      xRegister.getReal()[0] = 0; // standard initial state is |0&gt; ...
      xRegister.getReal()[initialState] = 1;
      
      int[] initialQubits = new int[ xRegisterSize + yRegisterSize ];
      int j = 1;
      while ( initialState > 0 ) {
         initialQubits[xRegisterSize - j] = initialState % 2;  
         initialState /= 2;
         j++;
      }
      setInitialState(initialQubits);
      return true;
   }
   
   /** Initializes this quantum register according to the initial qubit state
    *  given by quantum gate number 0.
    */
   public void initializeRegisters() {
      if (size() == 0) {
         return;
      }
      
      int[] initialQubits = get(0).getQubits();
      xRegister = new Register(xRegisterSize);
      for (int i = 1; i <= xRegisterSize; i++) {
         if (initialQubits[xRegisterSize - i] == 1) {
            xRegister.xPauli(i);
         }
      }

      nextGateNumber = 1; // important to mark the current gate to be executed
      
      yRegister = new Register(yRegisterSize);
      
      for (int i = 1; i <= yRegisterSize; i++) {
         if (initialQubits[xRegisterSize + yRegisterSize - i] == 1) {
            yRegister.xPauli(i);
         }
      }
   }
   
   /** Sets the quantum cicuit into its final state.
    *  In fact, by this method the entire quantum algorithm implemented by this
    *  circuit is executed.
    */
   public void setFinalStep() {
      nextGateNumber = this.size();
   }
   
   /** Executes the next quantum gate in this quantum circuit.
    *  @throws IllegalArgumentException if the quantum gate is unknown
    */
   public void setNextStep() {
      if ( nextGateNumber < this.size() ) {
         perform( get(nextGateNumber) );
         nextGateNumber++;
      }
   }
   
   /** Executes the inverse of the previously executed quantum gate in this quantum circuit.
    *  @throws IllegalArgumentException if the quantum gate is unknown or if it is a measurement gate
    */
   public void setPreviousStep() {
      if ( nextGateNumber > 1 ) {
         nextGateNumber--;
         unperform( get(nextGateNumber) );
      }
   }
   
   /**
    * Determines the initial state of the quantum circuit.
    * Here the array <code>initialQubits</code> specifies the indices of qubits 
    * either set to |0&gt; or |1&gt;. That is, an initial state corresponds to a 
    * classical bit state.
    * @param initialQubits the indices of qubits
    */ 
   public void setInitialState(int[] initialQubits) {
      if ( this.size() == 0 ) {
         this.add(0, new QuantumGate("initialState", initialQubits, false));
      } else {
         this.set(0, new QuantumGate("initialState", initialQubits, false));
      }
      nextGateNumber = 1;
   }
   
   /**
    * Adds a Hadamard gate on the <i>i</i>-th qubit of the quantum circuit;
    * if the flag <code>yRegister</code> is set, it is added to the <i>i</i>-th qubit
    * of the <i>y</i>-register, otherwise it is the <i>i</i>-th qubit of the 
    * <i>x</i>-register.
    * @param i the number of qubit
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addHadamard( int i, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("Hadamard", new int[]{i}, yRegister ) );
   }
   
   /**
    * Adds a CNOT gate on the two qubits specified by the two numbers 
    * in the qubit array;
    * if the flag <code>yRegister</code> is set, it is added to the
    * <i>y</i>-register, otherwise to the <i>x</i>-register.
    * @param qubits the numbers of qubit; array size <b>must</b> be two
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addCNOT( int[] qubits, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("cNOT", qubits, yRegister ) );
   }
   
   /**
    * Adds a Pauli-X gate on the <i>i</i>-th qubit of the quantum circuit;
    * if the flag <code>yRegister</code> is set, it is added to the <i>i</i>-th qubit
    * of the <i>y</i>-register, otherwise it is the <i>i</i>-th qubit of the 
    * <i>x</i>-register.
    * @param i the number of qubit
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addPauliX( int i, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("Pauli-X", new int[]{i}, yRegister ) );
   }
   
   /**
    * Adds a Pauli-Y gate on the <i>i</i>-th qubit of the quantum circuit;
    * if the flag <code>yRegister</code> is set, it is added to the <i>i</i>-th qubit
    * of the <i>y</i>-register, otherwise it is the <i>i</i>-th qubit of the 
    * <i>x</i>-register.
    * @param i the number of qubit
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addPauliY( int i, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("Pauli-Y",  new int[]{i}, yRegister ) );
   }
   
   /**
    * Adds a Pauli-Z gate on the <i>i</i>-th qubit of the quantum circuit;
    * if the flag <code>yRegister</code> is set, it is added to the <i>i</i>-th qubit
    * of the <i>y</i>-register, otherwise it is the <i>i</i>-th qubit of the 
    * <i>x</i>-register.
    * @param i the number of qubit
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addPauliZ( int i, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("Pauli-Z", new int[]{i}, yRegister ) );
   }
   
   /**
    * Adds an <i>S</i> gate on the <i>i</i>-th qubit of the quantum circuit;
    * if the flag <code>yRegister</code> is set, it is added to the <i>i</i>-th qubit
    * of the <i>y</i>-register, otherwise it is the <i>i</i>-th qubit of the 
    * <i>x</i>-register.
    * @param i the number of qubit
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addSGate( int i, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("S", new int[]{i}, yRegister ) );
   }
   
   /**
    * Adds an <i>S</i><sup>*</sup> gate, the inverse or adjoint of the <i>S</i> gate,
    * on the <i>i</i>-th qubit of the quantum circuit;
    * if the flag <code>yRegister</code> is set, it is added to the <i>i</i>-th qubit
    * of the <i>y</i>-register, otherwise it is the <i>i</i>-th qubit of the 
    * <i>x</i>-register.
    * @param i the number of qubit
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    * @see #addSGate(int, boolean)
    */
   public void addInvSGate( int i, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("invS", new int[]{i}, yRegister ) );
   }
   
   /**
    * Adds a <i>T</i> gate on the <i>i</i>-th qubit of the quantum circuit;
    * if the flag <code>yRegister</code> is set, it is added to the <i>i</i>-th qubit
    * of the <i>y</i>-register, otherwise it is the <i>i</i>-th qubit of the 
    * <i>x</i>-register.
    * @param i the number of qubit
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addTGate( int i, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("T", new int[]{i}, yRegister ) );
   }
   
   /**
    * Adds a &#x221A;X, or &#x221A;NOT gate on the <i>i</i>-th qubit of the quantum circuit;
    * if the flag <code>yRegister</code> is set, it is added to the <i>i</i>-th qubit
    * of the <i>y</i>-register, otherwise it is the <i>i</i>-th qubit of the 
    * <i>x</i>-register.
    * @param i the number of qubit
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addSqrtX( int i, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("sqrt-X", new int[]{i}, yRegister) );
   }
   
   /**
    * Adds a Toffoli gate on the three qubits specified by the three numbers 
    * in the qubit array;
    * if the flag <code>yRegister</code> is set, it is added to the
    * <i>y</i>-register, otherwise to the <i>x</i>-register.
    * @param qubits the numbers of qubit; array size <b>must</b> be three
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addToffoli( int[] qubits, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("Toffoli", qubits, yRegister ) );
   }
   
   /**
    * Adds the inverse Fourier transform gate to the circuit;
    * if the flag <code>yRegister</code> is set, it is added to the
    * <i>y</i>-register, otherwise to the <i>x</i>-register.
    * @param yRegister flag whether the gate is in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addInvQFT( boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      int[] qubits;
      if ( yRegister ) {
         qubits = new int[ yRegisterSize ];
         for ( int i = 0; i < yRegisterSize; i++ ) {
            qubits[i] = i+1;
         }
      } else {
         qubits = new int[ xRegisterSize ];
         for ( int i = 0; i < xRegisterSize; i++ ) {
            qubits[i] = i+1;
         }
      }
      this.add( new QuantumGate("invQFT", qubits, yRegister ) );
   }
   
   /**
    * Adds the Fourier transform gate to the circuit;
    * if the flag <code>yRegister</code> is set, it is added to the
    * <i>y</i>-register, otherwise to the <i>x</i>-register.
    * @param yRegister flag whether the gate is in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addQFT( boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      int[] qubits;
      if ( yRegister ) {
         qubits = new int[ yRegisterSize ];
         for ( int i = 0; i < yRegisterSize; i++ ) {
            qubits[i] = i+1;
         }
      } else {
         qubits = new int[ xRegisterSize ];
         for ( int i = 0; i < xRegisterSize; i++ ) {
            qubits[i] = i+1;
         }
      }
      this.add( new QuantumGate("QFT", qubits, yRegister ) );
   }
   
   /**
    * Adds a function evaluating gate to the circuit, specified by the input function.
    * The <i>x</i>-register qubits save as control qubits whereas the function
    * values are strored in the <i>y</i>-register.
    * @param function the parsed function
    * @throws org.mathIT.quantum.NoWireException if the numbers of wires is not positive
    */
   public void addFunction( FunctionParser function ) throws NoWireException {
      if ( numberOfWires <= 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      int[] qubits = new int[ yRegisterSize ];
      for ( int i = 0; i < yRegisterSize; i++ ) {
         qubits[i] = i+1;
      }
      this.add( new QuantumGate("Function", qubits, function, true ) );     
   }
   
   /**
    * Adds a rotation gate to the circuit. The axis may be <i>x</i>, <i>y</i>,
    * or <i>z</i>, specified by the strings <code>"x"</code>, <code>"y"</code>,
    * <code>"z"</code>, respectively. 
    * The rotation angle is specified as the integer part of <i>&#x03C0;</i>.
    * @param qubits the qubit numbers being involved
    * @param yRegisterChosen flag whether the gate is added to the <i>y</i>-register
    * @param axis the rotation axis
    * @param phiAsPartOfPi the integer part of <i>&#x03C0;</i> representing the rotation angle 
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addRotation( 
      int[] qubits, boolean yRegisterChosen, String axis, int phiAsPartOfPi
   ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("Rotation", qubits, axis, phiAsPartOfPi, yRegisterChosen ) );
   }
   
   /**
    * Adds the Grover gate searching for the specified needle to the circuit.
    * @param needle the searched for value of the Grover gate
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addGrover(int needle) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      int[] qubits = {needle};
      this.add( new QuantumGate("Grover", qubits, false) );
   }
   
   /**
    * Adds a measurement gate on the qubits specified by the qubit array;
    * if the flag <code>yRegister</code> is set, it is added to the
    * <i>y</i>-register, otherwise to the <i>x</i>-register.
    * @param qubits the numbers of qubit
    * @param yRegister flag whether the gate is added in the <i>y</i>-register
    * @throws org.mathIT.quantum.NoWireException if the register is not existing
    */
   public void addMeasurement( int[] qubits, boolean yRegister ) throws NoWireException {
      if ( numberOfWires == 0 ) {
         throw new NoWireException( "Register not existing" );
      }
      this.add( new QuantumGate("Measurement", qubits, yRegister ) );
   }
   
   /**
    * Executes the entire quantum circuit and returns <code>true</code> if
    * the algorithm is terminated.
    * @return <code>true</code> after termination of execution
    */
   public boolean executeAll() {
      initializeRegisters();
      for (int i = 1; i < size(); i++) {
         perform(get(i));
      }
      setFinalStep();
      return true;
   }
   
   /** Executes the specified quantum gate.
    *  @param gate the quantum gate to execute
    *  @throws IllegalArgumentException if the quantum gate is unknown
    */
   private void perform(QuantumGate gate) {
      if ( gate.getName().equalsIgnoreCase("Hadamard") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.hadamard( qubit );
         } else {
            xRegister.hadamard( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("cNOT") ) {
         if ( gate.yRegister ) {
            yRegister.cNOT( gate.qubits[0], gate.qubits[1] );
         } else {
            xRegister.cNOT( gate.qubits[0], gate.qubits[1] );
         }
      } else if ( gate.getName().equalsIgnoreCase("Pauli-X") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.xPauli( qubit );
         } else {
            xRegister.xPauli( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("Pauli-Y") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.yPauli( qubit );
         } else {
            xRegister.yPauli( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("Pauli-Z") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.zPauli( qubit );
         } else {
            xRegister.zPauli( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("S") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.sGate( qubit );
         } else {
            xRegister.sGate( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("invS") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.inverseSGate( qubit );
         } else {
            xRegister.inverseSGate( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("T") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.tGate( qubit );
         } else {
            xRegister.tGate( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("sqrt-X") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.sqrtX( qubit );
         } else {
            xRegister.sqrtX( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("Toffoli") ) {
         if ( gate.yRegister ) {
            yRegister.toffoli( gate.qubits[0], gate.qubits[1], gate.qubits[2] );
         } else {
            xRegister.toffoli( gate.qubits[0], gate.qubits[1], gate.qubits[2] );
         }
      } else if ( gate.getName().equalsIgnoreCase("QFT") ) {
         if ( gate.yRegister ) { // ( 1 << n ) = 2^n:
            yRegister.qft( (1 << yRegisterSize), (1 << yRegisterSize) );
         } else {
            xRegister.qft( (1 << xRegisterSize), (1 << xRegisterSize) );
         }
      } else if ( gate.getName().equalsIgnoreCase("invQFT") ) {
         if ( gate.yRegister ) { // ( 1 << n ) = 2^n:
            yRegister.inverseQft( (1 << yRegisterSize), (1 << yRegisterSize) );
         } else {
            xRegister.inverseQft( (1 << xRegisterSize), (1 << xRegisterSize) );
         }
      } else if ( gate.getName().equalsIgnoreCase("Function") ) {
         int zMin = 0;
         int zMax = ( 1 << (yRegisterSize - 1) );
         int z = (int) ( zMin + ( zMax - zMin)*Math.random() );
         // evaluate function with random number z; if the z-variable is set 
         // by the user, the function is independent from z:
         yRegister = xRegister.evaluateFunction( yRegister, gate.function, z );
         yRegisterSize = yRegister.size;
      } else if ( gate.getName().equalsIgnoreCase("Rotation") ) {
         double phi = Math.PI / gate.phiAsPartOfPi;
         if ( gate.yRegister ) {
            yRegister.rotate(gate.qubits, gate.axis, phi);
         } else {
            xRegister.rotate(gate.qubits, gate.axis, phi);
         }
      } else if ( gate.getName().equalsIgnoreCase("Grover") ) {
         xRegister.grover(gate.qubits[0]);
      } else if ( gate.getName().equalsIgnoreCase("Measurement") ) {
         if ( gate.yRegister ) {
            if ( gate.qubits.length == 1 ) { // single-qubit measurement
               yRegister.measure(gate.qubits[0]);
               ArrayList<Integer> values = new ArrayList<>();
               for (int i = 0; i < yRegister.getReal().length; i++) {
                  if (
                     Math.abs(yRegister.getReal()[i]) > Register.ACCURACY ||
                     Math.abs(yRegister.getImaginary()[i]) > Register.ACCURACY
                  ) {
                     values.add(i);
                  }
               }
               int[] value = new int[values.size()];
               for (int i = 0; i < value.length; i++) {
                  value[i] = values.get(i);
               }
               modifyXRegister(value);
            } else { // entire register measurement
               modifyXRegister(new int[]{yRegister.measure()});
            }
         } else { // x-Register measurement
            if ( gate.qubits.length == 1 ) { // single-qubit measurement
               xRegister.measure( gate.qubits[0] );
            } else { // entire register measurement
               xRegister.measure();
            }
            
            // modify y-register (works only if exclusively local gates are used for y-register):
            if (xRegister.getEntanglement() != null && xRegister.getEntanglement().size() > 0) {
               Integer[] x;
               for (int y : xRegister.getEntanglement().keySet()) {
                  x = xRegister.getEntanglement().get(y).toArray(new Integer[0]);
                  for (int i = 0; i < x.length; i++) {
                     if (xRegister.getReal()[x[i]] == 0) {
                        xRegister.getEntanglement().get(y).remove(x[i]);
                     }
                  }
               }
               HashSet<Integer> keys = new HashSet<>(xRegister.getEntanglement().keySet());
               for (int y : keys) {
                  if (xRegister.getEntanglement().get(y).isEmpty()) {
                     xRegister.getEntanglement().remove(y);
                     yRegister.getReal()[y] = 0;
                  }
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Unknown quantum gate " + gate.getName());
      }
   }
   
   /** Modifies the x-Register in case of a y-register measurement of the specified value.*/
   private void modifyXRegister(int[] value) {
      if (xRegister.getEntanglement() != null && xRegister.getEntanglement().size() > 0) {
         double[] realTmp = new double[ xRegister.getReal().length ];
         double[] imagTmp = new double[ xRegister.getReal().length ];
         double length = 0;
         int i;
         boolean preserve;
         HashSet<Integer> keys = new HashSet<>(xRegister.getEntanglement().keySet());
         for (int y : keys) {
            preserve = false;
            for (i = 0; !preserve && i < value.length; i++) {
               preserve |= (y == value[i]);
            }
            if (!preserve) {
               xRegister.getEntanglement().remove(y);
            }
         }
         
         for (int y : xRegister.getEntanglement().keySet()) {
            for (int index : xRegister.getEntanglement().get(y)) {
               realTmp[index] = xRegister.getReal()[index];
               imagTmp[index] = xRegister.getImaginary()[index];
            }
         }
         
         for (i = 0; i < realTmp.length; i++) {
            if (realTmp[i] != 0 || imagTmp[i] != 0 ) {
               length += realTmp[i]*realTmp[i] + imagTmp[i]*imagTmp[i];
            }
         }
         
         length = Math.sqrt(length);
         
         for (i = 0; i < realTmp.length; i++) {
            realTmp[i] = realTmp[i] / length;
            imagTmp[i] = imagTmp[i] / length;
         }
         
         xRegister.setReal(realTmp);
         xRegister.setImaginary(imagTmp);
      }
   }
   
   /** Executes the inverse of the specified quantum gate.
    *  @param gate the quantum gate whose inverse is to execute
    *  @throws IllegalArgumentException if the quantum gate is unknown or if it is a measurement gate
    */
   private void unperform(QuantumGate gate) {
      if ( gate.getName().equalsIgnoreCase("Hadamard") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.hadamard( qubit );
         } else {
            xRegister.hadamard( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("cNOT") ) {
         if ( gate.yRegister ) {
            yRegister.cNOT( gate.qubits[0], gate.qubits[1] );
         } else {
            xRegister.cNOT( gate.qubits[0], gate.qubits[1] );
         }
      } else if ( gate.getName().equalsIgnoreCase("Pauli-X") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.xPauli( qubit );
         } else {
            xRegister.xPauli( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("Pauli-Y") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.yPauli( qubit );
         } else {
            xRegister.yPauli( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("Pauli-Z") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.zPauli( qubit );
         } else {
            xRegister.zPauli( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("S") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.inverseSGate( qubit );
         } else {
            xRegister.inverseSGate( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("invS") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.sGate( qubit );
         } else {
            xRegister.sGate( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("T") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.inverseTGate( qubit );
         } else {
            xRegister.inverseTGate( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("sqrt-X") ) {
         int qubit = gate.qubits[0];
         if ( gate.yRegister ) {
            yRegister.inverseSqrtX( qubit );
         } else {
            xRegister.inverseSqrtX( qubit );
         }
      } else if ( gate.getName().equalsIgnoreCase("Toffoli") ) {
         if ( gate.yRegister ) {
            yRegister.toffoli( gate.qubits[0], gate.qubits[1], gate.qubits[2] );
         } else {
            xRegister.toffoli( gate.qubits[0], gate.qubits[1], gate.qubits[2] );
         }
      } else if ( gate.getName().equalsIgnoreCase("QFT") ) {
         if ( gate.yRegister ) { // ( 1 << n ) = 2^n:
            yRegister.inverseQft( (1 << yRegisterSize), (1 << yRegisterSize) );
         } else {
            xRegister.inverseQft( (1 << xRegisterSize), (1 << xRegisterSize) );
         }
      } else if ( gate.getName().equalsIgnoreCase("invQFT") ) {
         if ( gate.yRegister ) { // ( 1 << n ) = 2^n:
            yRegister.qft( (1 << yRegisterSize), (1 << yRegisterSize) );
         } else {
            xRegister.qft( (1 << xRegisterSize), (1 << xRegisterSize) );
         }
      } else if ( gate.getName().equalsIgnoreCase("Function") ) {
         initializeRegisters(); // it is hard to compute the reverse state ...
      } else if ( gate.getName().equalsIgnoreCase("Rotation") ) {
         double phi = - Math.PI / gate.phiAsPartOfPi;
         if ( gate.yRegister ) { // ( 1 << n ) = 2^n:
            yRegister.rotate(gate.qubits, gate.axis, phi);
         } else {
            xRegister.rotate(gate.qubits, gate.axis, phi);
         }
      } else if ( gate.getName().equalsIgnoreCase("Grover") ) {
         xRegister.inverseGrover(gate.qubits[0]);
      } else if ( gate.getName().equalsIgnoreCase("Measurement") ) {
         throw new IllegalArgumentException("Measurement gate is not reversible!");
      } else {
         throw new IllegalArgumentException("Unknown quantum gate " + gate.getName());
      }
   }
}
