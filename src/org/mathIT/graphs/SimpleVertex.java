/*
 * SimpleVertex.java - Class representing a vertex of a directed graph
 *
 * Copyright (C) 2009-2012 Andreas de Vries
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
package org.mathIT.graphs;

/** An instance of this class represents a vertex of a directed graph.
 *  @author Andreas de Vries
 *  @version 1.1
 */
public class SimpleVertex extends Vertex<SimpleVertex> {
   /** Creates a vertex with the specified index which also specifies its name.
    *  @param index the index of this vertex
    */
   public SimpleVertex(int index) {
      super(index);
   }
   
   /** Creates a vertex with the specified name.
    *  @param name the name of this vertex
    */
   public SimpleVertex(String name) {
      super(name);
   }
   
   /** Creates a vertex with the specified index and name.
    *  @param name the name of this vertex
    *  @param index the index of this vertex
    */
   public SimpleVertex(int index, String name) {
      super(index,name);
   }
   
   /** Creates a vertex with the specified name, index, and adjacency list.
    *  @param name the name of this vertex
    *  @param index the index of this vertex
    *  @param adjacency the adjacency of this vertex
    */
   public SimpleVertex(int index, String name, SimpleVertex[] adjacency) {
      super(index, name, adjacency);
   }
   
   /** Creates and returns a copy of this object. 
    *  For any object x, the expression <code>x.clone() != x</code> is true,.
    *  @return a clone of this object
    */
   @Override
   public SimpleVertex copy() {
      return new SimpleVertex(index, name, adjacency);
   }

   @Override
   public String toString() {
      return name;
      /*
      String output = name; + "( -> ";
      int i = 0;
      for(i = 0; i < adjacency.length - 1; i++) {
         output += adjacency[i].getName() + ",";
      }
      if (adjacency.length > 0) {
         output += adjacency[i].getName();
      }
      output += ")";
      return output;
      // */
   }
}

