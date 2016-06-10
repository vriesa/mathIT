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
