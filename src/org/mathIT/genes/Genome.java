/*
 * (c) 2011-12 Andreas de Vries
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
package org.mathIT.genes;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import static org.mathIT.genes.Utils.propertyFile;

/** This class represents a genome of a living organism. The genome is the entire 
 *  genetic information of a cell. It specifies an algorithm for creating and 
 *  maintaining the entire organism containing the cell.
 *  For usual DNA sequences, the heap memory heap size has to be augmented.
 *  A DNA sequence consisting of some 30 million letters requires a heap memory
 *  of some 1 GB, which can be allocated by invoking the application via
 *  <p style="text-align:center;">
 *  <code>java -ms1024m -mx1024m org.mathIT.genes.Genome</code>
 *  </p>
 *  @author Andreas de Vries
 *  @version 1.0
 */
public class Genome implements java.io.Serializable {
   private static final long serialVersionUID = 2129332503L; // = "Genome".hashCode()
   
   /** Field to store the name of the current file (without file name extension).*/
   private static String fileName = "Test";
   
   /** Constant determining the "corridor of equality" of two double values.*/
   //private static final double EPSILON = 1.e-12;
   
   /** Nucleobases. These are U (RNA), T (DNA), C, A, G (RNA and DNA). N means unknown.*/
   static final char[] VALUES = {'U', 'T', 'C', 'A', 'G', 'N', '?'};
   
   /** Bitmask
    *    0 = 000 -> ' '
    *    1 = 001 -> 'U'
    *    2 = 010 -> 'T'
    *    3 = 011 -> 'C'
    *    4 = 100 -> 'A'
    *    5 = 101 -> 'G'
    *    6 = 110 -> 'N'  // possibly 'A', 'C', 'G', 'T' ("aNy", 1 of 4)
    *    7 = 111 -> '?'  // one of the restrictive classes R, Y, K, M, S, W (1 of 2) or B, D, H, V (1 of 3)
    *  3-Bit group number:    21  20  19  18  17  16  15  14  13  12  11  10  9   8   7   6   5   4   3   2   1
    *  long ( = 64 bits):  |x|210|987|654|321|098|765|432|109|876|543|210|987|654|321|098|765|432|109|876|543|210|
    *  (tens:)                  6             5            4            3             2            1            0
    */
   private static long[] BITMASK = {
      0x0000000000000007L,
      0x0000000000000038L,
      0x00000000000001C0L,
      0x0000000000000E00L,
      0x0000000000007000L,
      0x0000000000038000L,
      0x00000000001C0000L,
      0x0000000000E00000L,
      0x0000000007000000L,
      0x0000000038000000L,
      0x00000001C0000000L,
      0x0000000E00000000L,
      0x0000007000000000L,
      0x0000038000000000L,
      0x00001C0000000000L,
      0x0000E00000000000L,
      0x0007000000000000L,
      0x0038000000000000L,
      0x01C0000000000000L,
      0x0E00000000000000L,
      0x7000000000000000L
   };
      
   /** The name of this genome.*/
   private String name;
   
   /** The nucleobase sequence of this genome.*/
   private long[] sequence;
   
   /** The number of nucleobases of this genome.*/
   private int size;
   
   /** The absolute frequency distribution of the nucleobases.*/
   private TreeMap<Character, Integer> frequencies;
   
   /** The absolute frequencies of nucleobase pairs of the sequence of this genome.*/
    TreeMap<String, Integer> pairFrequencies;
    
   /** The absolute frequencies of nucleobase triplets of the sequence of this genome.*/
    TreeMap<String, Integer> tripletFrequencies;
   
   /** Constructor of a genome with the specified name and the specified sequence 
    *  of nucleobases, i.e., the letters A, C, G, T (or U for an RNA genome).
    *  Also accepted are letters specified by the 
    *  <a href="http://en.wikipedia.org/wiki/FASTA_format">FASTA format</a>,
    *  but all letters &ne; 'N' are stored as '?'
    *  @param name the name of the genome
    *  @param sequence the sequence of nucleobases
    */
   public Genome(String name, ArrayList<Character> sequence) {
      this.name     = name;
      this.size     = sequence.size();
      this.sequence = new long[1 + sequence.size()/21];
      
      char letter;
      for (int i = 0; i < sequence.size(); i++) {
         letter = sequence.get(i);
         if (letter == 'U') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (1L << 3*(i%21)) ));
         } else if (letter == 'T') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (2L << 3*(i%21)) ));
         } else if (letter == 'C') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (3L << 3*(i%21)) ));
         } else if (letter == 'A') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (4L << 3*(i%21)) ));
         } else if (letter == 'G') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (5L << 3*(i%21)) ));
         } else if (letter == 'N') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (6L << 3*(i%21)) ));
         } else switch (letter) {
            case 'R': case 'Y': case 'K': case 'M': case 'S': case 'W': // one of two possible nucleobases ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            case 'B': case 'D': case 'H': case 'V': // one of three possible nucleobases ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            case 'X': // masked ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            case '-': // gap of indeterminate length ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            default: throw new IllegalArgumentException(letter + " is not a nucleobase");
         }
      }
      //System.out.println("### ... und berechne Haeufigkeit ...");
      this.frequencies        = computeFrequencies();
      //System.out.println("### sequence = " + sequence);
      //System.out.println("### -> " + java.util.Arrays.toString(this.sequence));
      //System.out.println("### size = " + sequence.size() + " -> length="+this.sequence.length);
      this.pairFrequencies    = computePairFrequencies();
      this.tripletFrequencies = computeTripletFrequencies();
   }
   
   /** Constructor of a genome with the specified name and the specified sequence 
    *  of nucleobases, i.e., the letters A, C, G, T (or U for an RNA genome).
    *  @param name the name of the genome
    *  @param sequence the sequence of nucleobases
    */
   public Genome(String name, char[] sequence) {
      this.name     = name;
      this.size     = sequence.length;
      this.sequence = new long[1 + sequence.length/21];
      for (int i = 0; i < sequence.length; i++) {
         if (sequence[i] == 'U') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (1L << 3*(i%21)) ));
         } else if (sequence[i] == 'T') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (2L << 3*(i%21)) ));
         } else if (sequence[i] == 'C') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (3L << 3*(i%21)) ));
         } else if (sequence[i] == 'A') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (4L << 3*(i%21)) ));
         } else if (sequence[i] == 'G') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (5L << 3*(i%21)) ));
         } else if (sequence[i] == 'N') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (6L << 3*(i%21)) ));
         } else switch (sequence[i]) {
            case 'R': case 'Y': case 'K': case 'M': case 'S': case 'W': // one of two possible nucleobases ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            case 'B': case 'D': case 'H': case 'V': // one of three possible nucleobases ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            case 'X': // masked ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            case '-': // gap of indeterminate length ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            default: throw new IllegalArgumentException(sequence[i] + " is not a nucleobase");
         }
      }
      
      this.frequencies        = computeFrequencies();
      this.pairFrequencies    = computePairFrequencies();
      this.tripletFrequencies = computeTripletFrequencies();
   }
   
   /** Constructor of a genome with the specified name and the specified sequence 
    *  of nucleobases, i.e., the letters A, C, G, T (or U for an RNA genome).
    *  @param name the name of the genome
    *  @param sequence sequence of nucleobases
    */
   public Genome(String name, CharSequence sequence) {
      this.sequence = new long[1 + sequence.length()/21];
      for (int i = 0; i < sequence.length(); i++) {
         if (sequence.charAt(i) == 'U') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (1L << 3*(i%21)) ));
         } else if (sequence.charAt(i) == 'T') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (2L << 3*(i%21)) ));
         } else if (sequence.charAt(i) == 'C') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (3L << 3*(i%21)) ));
         } else if (sequence.charAt(i) == 'A') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (4L << 3*(i%21)) ));
         } else if (sequence.charAt(i) == 'G') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (5L << 3*(i%21)) ));
         } else if (sequence.charAt(i) == 'N') {
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (6L << 3*(i%21)) ));
         } else switch (sequence.charAt(i)) {
            case 'R': case 'Y': case 'K': case 'M': case 'S': case 'W': // one of two possible nucleobases ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            case 'B': case 'D': case 'H': case 'V': // one of three possible nucleobases ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            case 'X': // masked ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            case '-': // gap of indeterminate length ...
            this.sequence[i/21] = (this.sequence[i/21] | (BITMASK[i%21] & (7L << 3*(i%21)) )); break;
            default: throw new IllegalArgumentException(sequence.charAt(i) + " is not a nucleobase");
         }
      }
      
      this.frequencies        = computeFrequencies();
      this.pairFrequencies    = computePairFrequencies();
      this.tripletFrequencies = computeTripletFrequencies();
   }
   
   /** Returns whether the specified character should be ignored for statistical 
    *  evaluations. For example, for DNA nucleobases (dna=true, recognized=true)
    *  the letters 'U' and 'N' are marked to be ignored.
    *  @param c a nucleobase letter (possibly 'N' if it is unrecognized)
    *  @param dna specifies whether the letter represents a DNA nucleobase ('T', 'C', 'A', 'G')
    *  @param onlyRecognized specifies whether unrecognized nucleobase letters ('N' or '?') should be ignored
    *  @return true if the letter is to be ignored
    */
   private static boolean ignore(char c, boolean dna, boolean onlyRecognized) {
      if (dna) { // DNA
         return (c == 'U' || ((c == 'N' || c == '?') && onlyRecognized));
      } else {   // RNA
         return (c == 'T' || ((c == 'N' || c == '?') && onlyRecognized));
      }
   }
  
   /** Returns whether the specified character should be ignored for statistical 
    *  evaluations. For example, for DNA nucleobases the letters 'U' and 'N' are 
    *  marked to be ignored.
    *  @param c a nucleobase letter (possibly 'N' if it is unrecognized)
    *  @return true if the letter is to be ignored
    */
   private static boolean ignore(char c) {
      //return (c == 'U' || c == 'N');
      return ignore(c, true, true);
   }
   
   /** Returns whether the specified character should be ignored for statistical 
    *  evaluations.
    *  @param sequence an amino acid sequence
    *  @param dna specifies whether the letter represents a DNA nucleobase ('T', 'C', 'A', 'G')
    *  @param onlyRecognized specifies whether unrecognized amino acid letters should be ignored
    *  @return true if the sequence is to be ignored
    */
   private static boolean ignore(String sequence, boolean dna, boolean onlyRecognized) {
      boolean ignore = false;
      for (int i = 0; i < sequence.length(); i++) {
         ignore |= ignore(sequence.charAt(i), dna, onlyRecognized);
      }
      return ignore;
   }
  
   /** Computes the absolute frequency distribution of the nucleobases of the 
    *  sequence of this genome.
    *  @return the absolute frequency distribution of the nucleobases of the 
    *  sequence of this genome
    */
   private TreeMap<Character, Integer> computeFrequencies() {
      TreeMap<Character, Integer> p = new TreeMap<>();
      int[] f = new int[VALUES.length + 1];
      
      int code;
      int i, j;
      
      for (i = 0; i < sequence.length; i++) {
         for (j = 0; j < 21; j++) {
            code = (int) ((sequence[i] & BITMASK[j]) >> (3*j));
            //System.out.println("### j=" + j + ": " + BITMASK[j] + " -> " + code);
            if (code == 0) break;  // the current array entry does not contain nucleobases anymore
            f[code] += 1;
         }
      }
      
      for (i = 0; i < VALUES.length; i++) {
         //p.put(VALUES[i], 100. * f[i + 1] / size);  // <- relative frequencies in %
         p.put(VALUES[i], f[i + 1]);
      }
      return p;
   }
   
   /** Computes the absolute frequencies of nucleobase pairs of the sequence of 
    *  this genome.
    *  @return the absolute frequencies of nucleobase pairs of the sequence of 
    *  this genome
    */
   private TreeMap<String, Integer> computePairFrequencies() {
      TreeMap<String, Integer> p = new TreeMap<>();
      int[] f = new int[64]; // 2*3 Bit = 64 mögliche Werte
      int c1, c2;  // codes for first and second letter
      
      long code = 0;
      int i, j;
      
      for (i = 0; i < sequence.length; i++) {
         for (j = 1; j < 21; j++) {
            code = (sequence[i] & BITMASK[j]) >> (3*j - 3);
            if (code == 0) break;  // the current array entry does not contain nucleobases anymore
            //System.out.print("### " + VALUES[(int) ((sequence[i] & BITMASK[j-1]) >> 3*(j-1)) - 1]);
            //System.out.print(VALUES[(int) ((sequence[i] & BITMASK[j]) >> 3*j) - 1]);
            code |= (sequence[i] & BITMASK[j-1]) >> (3*j - 3);
            //System.out.println(" => i=" + i + ", code=" + code + " = " + Long.toBinaryString(code));
            f[(int) code] += 1;
         }
         // possibly there is a letter in the next "row"
         //System.out.println("### code=" + code + "=" + Long.toBinaryString(code) + ", j=" + j + ", i=" + i + ", length=" + sequence.length);
         if (code != 0 && i < sequence.length - 1) {
            // preceding nucleobase letter: (sequence[i] & BITMASK[j-1]) >> 3*(j-1) ...
            code = (sequence[i] & BITMASK[20]) >> 60 | (sequence[i+1] & BITMASK[0] << 3);
            f[(int) code] += 1;
            /*
            System.out.println("### code=" + code + "=" + Long.toBinaryString(code) //);
               + ", " + Long.toBinaryString((sequence[i]   & BITMASK[20]) >> 60)
               + ", " + Long.toBinaryString((sequence[i+1] & BITMASK[0])  <<  3)
            );
            // */
            //System.out.println("### code=" + code + "=" + Long.toBinaryString(code));
         }
      }
      
      for (i = 1; i <= f.length; i++) {
         //System.out.print("### i&56=" + Long.toBinaryString(i&56) + " (i&7)=" + Long.toBinaryString(i&7));
         c1 = (i & 7);        //  7 = 000111 _2
         c2 = (i & 56) >> 3;  // 56 = 111000 _2
         if (c1*c2 == 0 || c1 >= VALUES.length + 1 || c2 >= VALUES.length + 1) continue;
         //System.out.println(" ### i=" + i + ", pair=" + VALUES[c1 - 1] + VALUES[c2 - 1] + ", f=" + f[i]);
         //p.put("" + VALUES[c1 - 1] + VALUES[c2 - 1], 100. * f[i] / size);  // <- relative frequencies
         p.put("" + VALUES[c1 - 1] + VALUES[c2 - 1], f[i]);
      }
      return p;
   }
   
   /** Computes the absolute frequencies of nucleobase triplets from the sequence of this genome.*/
   private TreeMap<String, Integer> computeTripletFrequencies() {
      TreeMap<String, Integer> p = new TreeMap<>();
      int[] f = new int[512]; // 3*3 Bit = 512 mögliche Werte
      int c1, c2, c3;  // codes for first and second letter
      
      long code = 0;
      int i, j;
      
      for (i = 0; i < sequence.length; i++) {
         for (j = 2; j < 21; j++) {
            code = (sequence[i] & BITMASK[j]) >> (3*j - 6);
            if (code == 0) break;  // the current array entry does not contain nucleobases anymore
            //System.out.print("### " + VALUES[(int) ((sequence[i] & BITMASK[j-1]) >> 3*(j-1)) - 1]);
            //System.out.print(VALUES[(int) ((sequence[i] & BITMASK[j]) >> 3*j) - 1]);
            code |= (sequence[i] & BITMASK[j-1]) >> (3*j - 6) | (sequence[i] & BITMASK[j-2]) >> (3*j - 6);
            //System.out.println(" => i=" + i + ", code=" + code + " = " + Long.toBinaryString(code));
            f[(int) code] += 1;
         }
         // possibly there is a letter in the next "row"
         //System.out.println("### code=" + code + "=" + Long.toBinaryString(code) + ", j=" + j + ", i=" + i + ", length=" + sequence.length);
         if (code != 0 && i < sequence.length - 1) {
            // preceding nucleobase letter: (sequence[i] & BITMASK[j-1]) >> 3*(j-3) ...
            code = (sequence[i]   & BITMASK[19]) >> 57 | 
                   (sequence[i]   & BITMASK[20]) >> 57 | 
                   (sequence[i+1] & BITMASK[0])  <<  6;
            f[(int) code] += 1;
            /*
            System.out.println("### code=" + code + "=" + Long.toBinaryString(code) //);
               + "\n "+ Long.toBinaryString((sequence[i]   & BITMASK[19]) >> 57)
               + ", " + Long.toBinaryString((sequence[i]   & BITMASK[20]) >> 57)
               + ", " + Long.toBinaryString((sequence[i+1] & BITMASK[0])  <<  6)
            );
            // */
            //System.out.println("### code=" + code + "=" + Long.toBinaryString(code));
            // ... and possibly there is another letter in the next "row"
            if ((sequence[i+1] & BITMASK[1]) != 0) {
               // preceding nucleobase letter: (sequence[i] & BITMASK[j-1]) >> 3*(j-3) ...
               code = (sequence[i]   & BITMASK[20]) >> 60 |
                      (sequence[i+1] & BITMASK[0])  <<  3 |
                      (sequence[i+1] & BITMASK[1])  <<  3;
               f[(int) code] += 1;
               /*
               System.out.println("+++ code=" + code + "=" + Long.toBinaryString(code) //);
                  + "\n "+ Long.toBinaryString((sequence[i]   & BITMASK[20]) >> 60)
                  + ", " + Long.toBinaryString((sequence[i+1] & BITMASK[0])  <<  3)
                  + ", " + Long.toBinaryString((sequence[i+1] & BITMASK[1])  <<  3)
               );
               // */
            }
         }
      }
      
      /*=======
      int sum = 0; for (i = 0; i < f.length; i++) {sum += f[i];}
      System.out.println("+++ sum="+sum);
      //=======*/
      
      for (i = 1; i < f.length; i++) {
         //System.out.print("### i&448=" + Long.toBinaryString(i&448) + ", i&56=" + Long.toBinaryString(i&56) + " (i&7)=" + Long.toBinaryString(i&7));
         c1 = (i & 7);        //   7 = 000000111 _2
         c2 = (i & 56)  >> 3; //  56 = 000111000 _2
         c3 = (i & 448) >> 6; // 448 = 111000000 _2
         if (c1*c2*c3 == 0 || 
             c1 >= VALUES.length + 1 || c2 >= VALUES.length + 1 || c3 >= VALUES.length + 1
         ) continue;
         //System.out.println("(c1,c2,c3) = (" + c1 + "," + c2 + "," + c3 + ")"); 
         //System.out.println(" ### i=" + i + ", triplet=" + VALUES[c1 - 1] + VALUES[c2 - 1] + VALUES[c3 - 1] + ", f=" + f[i]);
         p.put("" + VALUES[c1 - 1] + VALUES[c2 - 1] + VALUES[c3 - 1], f[i]);
      }
      return p;
   }
   
   /** Decodes the nucleobase sequence of this genome.*/
   private ArrayList<Character> decodeSequence() {
      ArrayList<Character> out = new ArrayList<>(21*sequence.length);
      long code;
      //System.out.println("### sequence = " + java.util.Arrays.toString(sequence));
      for (int i = 0; i < sequence.length; i++) {
         for (int j = 0; j < 21; j++) {
            code = (sequence[i] & BITMASK[j]) >> (3*j);
            //System.out.println("### j=" + j + ": " + BITMASK[j] + " -> " + code);
            if (code == 0) break;
            out.add(VALUES[(int) code - 1]);
         }
      }
      return out;
   }
   
   /** Returns the name of this genome.
    *  @return the name of this genome
    */
   public String getName() {
      return name;
   }
  
   /** Returns the nucleobase sequence of this genome.
    *  @return the nucleobase sequence of this genome
    */
   public StringBuilder getSequence() {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < sequence.length; i++) {
         for (int j = 0; j < 21; j++) {
            long code = (sequence[i] & BITMASK[j]) >> (3*j);
            if (code == 0) break;
            out.append(VALUES[(int) code - 1]);
         }
      }
      return out;
   }
  
   /** Returns the nucleobase sequence of this genome.
    *  @return the nucleobase sequence of this genome
    */
   public ArrayList<Character> getSequenceAsArrayList() {
      return decodeSequence();
   }
  
   /** Returns the absolute frequency distribution of the nucleobases.
    *  @return the frequency distribution of the nucleobases
    */
   public TreeMap<Character, Integer> getFrequencies() {
      return frequencies;
   }
  
   /** Returns the absolute frequency distribution of the nucleobase pairs.
    *  @return the frequency distribution of the nucleobase pairs
    */
   public TreeMap<String, Integer> getPairFrequencies() {
      return pairFrequencies;
   }
  
   /** Returns the absolute frequency distribution of the nucleobase triplets.
    *  @return the frequency distribution of the nucleobase triplets
    */
   public TreeMap<String, Integer> getTripletFrequencies() {
      return tripletFrequencies;
   }
  
   /** Returns the frequency distribution of the nucleobases as HTML table.
    *  @return an HTML string representing the frequency distribution of the nucleobases
    */
   public String getFrequenciesAsHTMLTable() {
      String out = "<table border=\"1\">";
      int counter = 0;
      for (char b : VALUES) {
         if (ignore(b)) continue;
         //if (frequencies.get(b) == 0.0) continue;
         if (counter % 8 == 0) out += "<tr>";
         out += "<td>" + b + ": " + frequencies.get(b) + "</td>";
         if (counter % 8 == 7) out += "</tr>";
         counter++;
      }
      out += "</table>";
      return out;
   }
  
   /** Returns the frequency distribution of this genome as a CSV data table string.
    *  The data separator is the tabulator sign '\t'.
    *  @return CSV data table string
    */
   protected String getFrequenciesAsCSVTable() {
      String out1 = "\n", out2 = "\n";
      for (char c : frequencies.keySet()) {
         if (ignore(c, true, true)) continue;
         out1 += c + "\t";
         out2 += frequencies.get(c) + "\t";
      }
      return out1 + out2;
   }
  
   /** Returns the specified map as a CSV data table string.
    *  The data separator is the tabulator sign '\t'.
    *  @param map a map
    *  @return CSV data table string
    */
   protected static String getAsCSVTable(java.util.Map<String, Integer> map) {
      String out1 = "\n", out2 = "\n";
      for (String c : map.keySet()) {
         if (ignore(c, true, true)) continue;
         out1 += c + "\t";
         out2 += map.get(c) + "\t";
      }
      return out1 + out2 + "\n";
   }
  
   /** Returns the frequency distribution of the nucleobases as HTML graph.
    *  @return a graphical HTML string representing the frequency distribution of the nucleobases
    */
   public String getFrequenciesAsHTMLGraph() {
      int tableWidth = 100; // unit: px
      String out  = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
      double max  = 0;
      for (char b : VALUES) {
         if (max < frequencies.get(b)) max = frequencies.get(b);
      }
      max++;
      
      for (char b : VALUES) {
         if (ignore(b, true, true)) continue;
         //if (frequencies.get(b) == 0.0) continue;
         int width = (int) (frequencies.get(b) * tableWidth / max);
         out += "<tr><td style=\"font-size:small\">" + b + "&nbsp;</td><td>" +
            "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
            "<tr><td style=\"font-size:smaller\"><div style=\"width:" + width + "px; height:5px; " +
            "border-style:solid; border-width:1px; background-color:#FFD700\"></div></td>" +
            "<td style=\"font-size:smaller\">&nbsp;" + frequencies.get(b) + 
            "</td></tr></table>" +
            "</td></tr>";
      }
      out += "</table>";
      return out;
   }
  
   /** Returns the frequency distribution of the nucleobases as HTML graph in 
    *  landscape format.
    *  @return a graphical HTML string in landscape format representing the 
    *  frequency distribution of the nucleobases
    */
   public String getFrequenciesAsHTMLGraph2() {
      // ... vertikale Säulen mit gedrehter Beschriftung(??) ... noch nicht fertig!
      int tableHeight = 100;
      //String out  = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
      String out  = "<table border=\"0\" cellspacing=\"0\" cellpadding=\".5px\">";
      int max  = 0;
      for (Character c : frequencies.keySet()) {
         if (max < frequencies.get(c)) max = frequencies.get(c);
      }
      max++;
     
      out += "<tr valign=\"bottom\">";
      for (Character c : frequencies.keySet()) {
         if (ignore(c, true, true)) continue;
         int height = (frequencies.get(c) * tableHeight / max);
         out += "<td style=\"font-size:smaller\"><div style=\"height:" + height + "px; width:7px; " +
            "border-width:1px; border-style:solid; background-color:#FFD700\"></div></td>";
      }
      out += "</tr><tr>";
      for (Character c : frequencies.keySet()) {
         if (ignore(c)) continue;
         out += "<td style=\"font-size:small\">" + c + "&nbsp;</td>";
      }
      out += "</tr>";
      out += "</table>";
      return out;
   }
   
   /** Returns the frequency distribution of nucleobase pairs as HTML graph.
    *  @return a graphical HTML string representing the frequency distribution of nucleobase pairs
    */
   public String getPairFrequenciesAsHTMLGraph() {
      int tableWidth = 100; // unit: px
      String out  = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
      int max  = 0;
      
      for (String pair : pairFrequencies.keySet()) {
         if (max < pairFrequencies.get(pair)) max = pairFrequencies.get(pair);
      }
      max++;
      
      for (String pair : pairFrequencies.keySet()) {
         if (ignore(pair, true, true)) continue;
         int width = pairFrequencies.get(pair) * tableWidth / max;
         out += "<tr><td style=\"font-size:small\">" + pair + "&nbsp;</td><td>" +
         "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
         "<tr><td style=\"font-size:smaller\"><div style=\"width:" + width + "px; height:5px; " +
         "border-style:solid; border-width:1px; background-color:#FFD700\"></div></td>" +
         "<td style=\"font-size:smaller\">&nbsp;" + pairFrequencies.get(pair) + 
         "</td></tr></table>" +
         "</td></tr>";
      }
      out += "</table>";
      return out;
   }
  
   /** Returns the frequency distribution of nucleobase triplets as HTML graph.
    *  @return a graphical HTML string representing the frequency distribution of the nucleobase triplets
    */
   public String getTripletFrequenciesAsHTMLGraph() {
      int tableWidth = 100; // unit: px
      String out  = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
      int max  = 0;

      for (String triplet : tripletFrequencies.keySet()) {
         if (max < tripletFrequencies.get(triplet)) max = tripletFrequencies.get(triplet);
      }
      max++;
      
      for (String triplet : tripletFrequencies.keySet()) {
         if (ignore(triplet, true, true)) continue;
         int width = tripletFrequencies.get(triplet) * tableWidth / max;
         out += "<tr><td style=\"font-size:smaller\">" + triplet + "&nbsp;</td><td>" +
         "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
         "<tr><td style=\"font-size:smaller\"><div style=\"width:" + width + "px; height:5px; " +
         "border-style:solid; border-width:1px; background-color:#FFD700\"></div></td>" +
         "<td style=\"font-size:smaller\">&nbsp;" + tripletFrequencies.get(triplet) + 
         "</td></tr></table>" +
         "</td></tr>";
      }
      out += "</table>";
      return out;
   }
  
   /** Returns the probability to obtain the specified nucleobase of this genome sequence.
    *  @param b the specified nucleobase
    *  @return the probability of the nucleobase of this genome
    */
   public double p(char b) {
      return (double) frequencies.get(b) / size;
   }
   
   /** Returns the entropy of the frequency distribution of the nucleobases of this genome.
    *  The entropy is computed to the basis 2, i.e.,
    *  <i>H</i> = - &sum; <i>p<sub>i</sub></i> log<sub>2</sub><i>p<sub>i</sub></i>.
    *  @return the entropy of the frequency distribution of this genome
    */
   public double entropy() {
      double h = 0, p;
      for (char b : VALUES) {
         p  = (double) frequencies.get(b) / size;
         if (p != 0) h -= p * Math.log(p);
      }
      return h / Math.log(2);
   }
   
   /** Returns a string representing this genome, with line breaks encoded by
    *  "&lt;br&gt;" instead of '\n'.
    *  @return an HTML string representing this genome
    */
   public String asHTMLString() {
      int i_max = 25;
      StringBuilder out = new StringBuilder(name + "<br><br>");
      int max = sequence.length > i_max ? i_max : sequence.length;
      long code;
      for (int i = 0; i < max; i++) {
         for (int j = 0; j < 21; j++) {
            code = (sequence[i] & BITMASK[j]) >> (3*j);
            if (code == 0) break;
            if ((21*i + j) % 100 == 0 && i > 0) out.append("<br>");
            out.append(VALUES[(int) code - 1]);
         }
      }
      if (sequence.length > i_max) out.append("...");
      return out.toString();
   }
  
   /** Returns a string representing this genome.
    *  @return a string representing this genome
    */
   @Override
   public String toString() {
      int i_max = 4;
      StringBuilder out;
      out = (new StringBuilder()).append(name).append("\n");
      int max = sequence.length > i_max ? i_max : sequence.length;
      long code;
      for (int i = 0; i < max; i++) {
         for (int j = 0; j < 21; j++) {
            code = (sequence[i] & BITMASK[j]) >> (3*j);
            if (code == 0) break;
            out.append(VALUES[(int) code - 1]);
         }
      }
      if (sequence.length > i_max) out.append("...");
      return out.toString();
   }
   
   /** Returns a genome after opening a file in 
    *  <a href="http://en.wikipedia.org/wiki/FASTA_format" target="_top">FASTA format</a>, 
    *  determined by a file chooser dialog.
    *  @return a genome determined by the FASTA file, or null if no file is selected
    */
   //@SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static Genome loadFASTA() {
      final int BUFFER_SIZE = 200; // in bytes
      
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }      
      fileChooser.addChoosableFileFilter(FastaFileFilter.create());

      File file;
      char[] buffer;
      String name = "";
      Genome genome = null;
      
      boolean comment, named = false; // comment is necessary since a comment line may be split up by buffer size!
      
      ArrayList<Character> nucleobases;
      
      int returnVal = fileChooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         file = fileChooser.getSelectedFile();
         System.out.println("Opened file " + file);
         nucleobases = new ArrayList<>((int) file.length());
         // open the file:
         FileReader input = null;
         try {
            comment = false;
            input = new FileReader(file);
            while (input.ready()) {
               buffer = new char[BUFFER_SIZE];
               input.read(buffer);
               //System.out.println("Read: " + java.util.Arrays.toString(buffer));
               for (int i = 0; i < buffer.length; i++) {
                  if(buffer[i] == '>' || buffer[i] == ';' || comment) { // comment line in FASTA
                     comment = true;
                     i++;
                     while(i < buffer.length && buffer[i] != '\n') {
                        if (!named) name += buffer[i];
                        i++;
                     }
                     if (i < buffer.length) {
                        comment = false;
                        named = true;
                     }
                  } else if (buffer[i] == '\n') {
                     continue;
                  } else if (buffer[i] == '\u0000') { // eof reached
                     break;  // end for-loop
                  } else {
                     //System.out.println("i="+i+", char = " + ((int) buffer[i]) + ", " + buffer[i] + " = \u0010?");
                     nucleobases.add(buffer[i]);
                  }
               }
            }
            //System.out.println("### erzeuge Genom ...");
            genome = new Genome(name, nucleobases);
         } catch (IOException ioe) {
            ioe.printStackTrace();
         } finally {
            try {
               if (input != null)  input.close();
            } catch (IOException ioe) {
               ioe.printStackTrace();
            }
            fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
            try {
               Properties props = new Properties();
               props.setProperty("currentDirectory", file.getParent());
               props.storeToXML(new FileOutputStream(propertyFile), null);
            } catch (Exception e) {
               System.err.println(e.getMessage());
            }
         }
      }
      return genome;
   }
   
   /** Saves this genome in GNOM format. */
   public void save() {
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }
      
      FileNameExtensionFilter filter = new FileNameExtensionFilter("Genome file (*.gnom)", "gnom");
      fileChooser.addChoosableFileFilter(filter);
      fileChooser.setSelectedFile(new File(fileName + ".gnom"));
      
      int returnVal = fileChooser.showSaveDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         File file = fileChooser.getSelectedFile();
         // open the file:
         ObjectOutputStream output = null; 
         try {
            output = new ObjectOutputStream(new FileOutputStream(file)); 
            try {
               output.writeObject(this);
               output.flush();
            } catch (EOFException eof) {
               // do nothing, the stream will be closed in the finally clause
            } catch ( IOException ioe ) {
               ioe.printStackTrace();
            }
         } catch (IOException ioe) {
            ioe.printStackTrace();
         } finally {
            try {
               if (output != null)  output.close();
            } catch (IOException ioe) {
               ioe.printStackTrace();
            }
            try {
               Properties props = new Properties();
               props.setProperty("currentDirectory", file.getParent());
               props.storeToXML(new FileOutputStream(propertyFile), null);
            } catch (Exception e) {
               System.err.println(e.getMessage());
            }
         }
      }
   }
   
   /** Saves this genome as CSV file. */
   public void saveAsCSV() {
      if (fileName == null || fileName.equals("") || fileName.equals("Test")) {
         fileName = (name.length() < 50) ? name : name.substring(0, 50);
         fileName = fileName.replace(' ','_');
         fileName = fileName.replace(',','_');
         //fileName = fileName.replace('.','_');
         fileName = fileName.replace('|','-');
         if (fileName.equals("")) fileName = "Test";
      }
      
      File file;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         file = new File(
            props.getProperty("currentDirectory") + 
            System.getProperty("file.separator") + fileName + ".csv"
         );
      } catch(Exception e) {
         file = new File(fileName + ".csv");
      }

      FileWriter output = null; 
      try {
         output = new FileWriter(file); 
         try {
            output.write(name + "\n\nNucleobase Frequencies"+getFrequenciesAsCSVTable());
            output.write("\n\nNucleobase Pair Frequencies" + getAsCSVTable(pairFrequencies));
            output.write("\nNucleobase Triplet Frequencies" + getAsCSVTable(tripletFrequencies));
            output.flush();
            System.out.println(file + " saved!");
         } catch (EOFException eof) {
            // do nothing, the stream will be closed in the finally clause
         } catch ( IOException ioe ) {
            ioe.printStackTrace();
         }
      } catch (IOException ioe) {
         ioe.printStackTrace();
      } finally {
         try {
            if (output != null)  output.close();
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
         try {
            Properties props = new Properties();
            props.setProperty("currentDirectory", file.getParent());
            props.storeToXML(new FileOutputStream(propertyFile), null);
         } catch (Exception e) {
            System.err.println(e.getMessage());
         }
      }
   }
   
   /** Returns a genome after opening a file in GNOM format, found by a file chooser dialog.
    *  @return a genome determined by the GNOM file, or null if no file is selected
    */
   //@SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static Genome loadGenome() {
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }

      FileNameExtensionFilter filter = new FileNameExtensionFilter("Genome file (*.gnom)", "gnom");
      fileChooser.addChoosableFileFilter(filter);
      
      Genome genome = null;
      
      int returnVal = fileChooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         File file = fileChooser.getSelectedFile();
         // open the file:
         ObjectInputStream input = null;
         try {
            input = new ObjectInputStream(new FileInputStream(file));
            genome = (Genome) input.readObject();
         } catch (IOException ioe) {
            ioe.printStackTrace();
         } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
         } finally {
            try {
               if (input != null)  input.close();
            } catch (IOException ioe) {
               ioe.printStackTrace();
            }            
            fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
            try {
               Properties props = new Properties();
               props.setProperty("currentDirectory", file.getParent());
               props.storeToXML(new FileOutputStream(propertyFile), null);
            } catch (Exception e) {
               System.err.println(e.getMessage());
            }
         }
      }
      return genome;
   }
   
   /*
   public static void main(String... args) {
      String out;
      //out = "<html>";
      //String sequence = "GTATGGTCGGCCTGAGTTAA";
      //Genome test = new Genome("E. Coli", sequence);
      //System.out.println(test);
      
      Genome genome;
      
//      char[] sequence = {'A', 'A', 'G', 'T', 'T', 'C', 'G', 'A', 'A', 'G'};
//      genome = new Genome("Test", sequence);
//      
//      out += "<br>" + genome.asHTMLString();
//      out += "<br>" + genome.getFrequenciesAsHTMLTable() 
//      + "<br>" + genome.getFrequenciesAsHTMLGraph2()
//      ;
//      out += "<br><br>Entropy:" + genome.entropy();
//      //out += "<br>" + genome.getPairFrequenciesAsHTMLGraph();
//      //out += "<br>" + genome.getTripletFrequenciesAsHTMLGraph();
//      
//      javax.swing.JOptionPane.showMessageDialog(null, out, "Genome", -1);
      
      out = "<html>"; // + genome.getName() + ":<br>" + genome.getFrequenciesAsHTMLGraph();
      //javax.swing.JOptionPane.showMessageDialog(null, out);
      
      genome = loadFASTA();
      if (genome != null) {
        out += "<br>" + genome.getName();
//        out += ":<br><br>" + genome.getFrequenciesAsHTMLGraph();
//        out += "<br>" + genome.getFrequenciesAsHTMLTable();
//        out += "<br><br>Entropy:" + genome.entropy() + "<br><br>" + genome.asHTMLString();
//        out += "<br>" + genome.getPairFrequenciesAsHTMLGraph();
        out += "<br>" + genome.getTripletFrequenciesAsHTMLGraph();
        javax.swing.JOptionPane.showMessageDialog(null, out, "Genome", -1);
        //genome.saveAsCSV();
        //genome.save();
        
        boolean dna = true;
        StringBuilder out2 = new StringBuilder("Vanishing nucleobase frequencies " + fileName + ":\n");
        int count;
        count = 1;
        for (char letter : genome.getFrequencies().keySet()) {
           if (ignore(letter, dna, true)) continue;
           if (genome.getFrequencies().get(letter) == 0) {
              out2.append(letter + ", ");
              if (count % 27 == 0) out2.append("\n");
              count++;
           }
        }
        Utils.save("OmittedNucleobases_" + fileName+ ".txt", out2);

        count = 1;
        out2 = new StringBuilder("Vanishing pair frequencies " + fileName + ":\n");        
        for (String pair : genome.getPairFrequencies().keySet()) {
           if (ignore(pair, dna, true)) continue;
           if (genome.getPairFrequencies().get(pair) == 0) {
              out2.append(pair + ", ");
              if (count % 20 == 0) out2.append("\n");
              count++;
           }
        }
        Utils.save("OmittedPairs_" + fileName+ ".txt", out2);

        out2 = new StringBuilder("Vanishing triplet frequencies " + fileName + ":\n");        
        count = 1;
        for (String triplet : genome.getTripletFrequencies().keySet()) {
           if (ignore(triplet, dna, true)) continue;
           if (genome.getTripletFrequencies().get(triplet) == 0) {
              out2.append(triplet + ", ");
              if (count % 16 == 0) out2.append("\n");
              count++;
           }
        }
        Utils.save("OmittedTriplets_" + fileName+ ".txt", out2);
        javax.swing.JOptionPane.showMessageDialog(null, out, "Protein", -1);
        //protein.saveAsCSV();
        //protein.save();
      }

//      out = "<html>";
//      genome = loadGenome();
//      if (genome != null) {
//         //--- Determine runtime of decoding: (30 mio letters => 1 sec)
//         //long time = System.currentTimeMillis();
//         //int laenge = genome.decodeSequence().size();
//         //time = System.currentTimeMillis() - time;
//         //System.out.println("### Sequence length: " + laenge);
//         //System.out.println("### Sequence decoding needed " + time/1000. + " sec");
//         
//         out += "<br>" + genome.getName() + ":<br>" + genome.getFrequenciesAsHTMLGraph() + 
//          genome.getFrequenciesAsHTMLTable();
//         out += "<br><br>Entropy:" + genome.entropy() + " (" + genome.getSequence().length() + " nucleobases)";
//         out += "<br><br>" + genome.asHTMLString();
//         javax.swing.JOptionPane.showMessageDialog(null, out);
//      }
   }
   // */
}

