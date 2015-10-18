/*
 * NoWireException.java - Class to indicate empty quantum circuits at the jQuantum computer simulator
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
 */
package org.mathIT.quantum;
/**
 * This exception class is thrown if an empty quantum circuit, i.e., 
 * a quantum circuit with no wires, is tried to be processed.
 * @author  Andreas de Vries
 * @version 1.4
 */
public class NoWireException extends java.lang.Exception {
	private static final long serialVersionUID = 83624169;
    
    /**
     * Creates a new instance of <code>NoWireException</code> without detail message.
     */
    public NoWireException() {
    }
        
    /**
     * Constructs an instance of <code>NoWireException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoWireException(String msg) {
        super(msg);
    }
}
