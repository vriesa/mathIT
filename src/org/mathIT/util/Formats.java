/*
 * Formats.java
 *
 * Copyright (C) 2006-2012 Andreas de Vries
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
package org.mathIT.util;
import java.text.*;
/**
 * This class provides often used formats.
 * @author  Andreas de Vries
 * @version 1.1
 */
public class Formats {
   // Suppresses default constructor, ensuring non-instantiability.
   private Formats() {
   }

   /** Number format with the format pattern <code>"#,###.0"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat A_DOT_O1 = new DecimalFormat("#,###.0");
   /** Number format with the format pattern <code>"#,###.00"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat A_DOT_O2 = new DecimalFormat("#,###.00");
   /** Number format with the format pattern <code>"#,###.000"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat A_DOT_O3 = new DecimalFormat("#,###.000");
   /** Number format with the format pattern <code>"#,###.0000"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat A_DOT_O4 = new DecimalFormat("#,###.0000");
   /** Number format with the format pattern <code>"#,###.00000"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat A_DOT_O5 = new DecimalFormat("#,###.00000");
   /** Number format with the format pattern <code>"#,###.000000"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat A_DOT_O6 = new DecimalFormat("#,###.000000");
   /** Number format with the format pattern <code>"#,###.0000000000"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat A_DOT_O10 = new DecimalFormat("#,###.0000000000");

   /** Number format with the format pattern <code>"#,##0.##"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_A0 = new DecimalFormat("#,##0.");
   /** Number format with the format pattern <code>"#,##0.#"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_A1 = new DecimalFormat("#,##0.#");
   /** Number format with the format pattern <code>"#,##0.##"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_A2 = new DecimalFormat("#,##0.##");
   /** Number format with the format pattern <code>"#,##0.###"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_A3 = new DecimalFormat("#,##0.###");
   /** Number format with the format pattern <code>"#,##0.####"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_A4 = new DecimalFormat("#,##0.####");
   /** Number format with the format pattern <code>"#,##0.#####"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_A5 = new DecimalFormat("#,##0.#####");
   /** Number format with the format pattern <code>"#,##0.######"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_A6 = new DecimalFormat("#,##0.######");
   /** Number format with the format pattern <code>"#,##0.##"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_A10 = new DecimalFormat("#,##0.##########");
   
   /** Number format with the format pattern <code>"#,##0.0"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_O1 = new DecimalFormat("#,##0.0");
   /** Number format with the format pattern <code>"#,##0.00"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_O2 = new DecimalFormat("#,##0.00");
   /** Number format with the format pattern <code>"#,##0.000"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_O3 = new DecimalFormat("#,##0.000");
   /** Number format with the format pattern <code>"#,##0.0000"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_O4 = new DecimalFormat("#,##0.0000");
   /** Number format with the format pattern <code>"#,##0.00000"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_O5 = new DecimalFormat("#,##0.00000");
   /** Number format with the format pattern <code>"#,##0.000000"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_O6 = new DecimalFormat("#,##0.000000");
   /** Number format with the format pattern <code>"#,##0.0000000000"</code>. 
    *  @see java.text.DecimalFormat
    */
   public static final DecimalFormat O_DOT_O10 = new DecimalFormat("#,##0.0000000000");

   /** For test purposes...*/
   /*
   public static void main ( String[] args ) {
   }
   // */
}