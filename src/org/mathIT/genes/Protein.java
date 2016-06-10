/*
 * (c) 2011 Andreas de Vries
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
import static org.mathIT.genes.AminoAcid.*;
import static org.mathIT.genes.Utils.propertyFile;

/** This class represents a protein as a sequence of amino acids.
 *  An amino acid sequence consisting of some 20 million letters requires a heap memory
 *  of about 256 MB, which can be allocated by invoking the application via
 *  <p style="text-align:center;">
 *  <code>java -ms256m -mx256m org.mathIT.genes.Protein</code>
 *  </p>
 *  @author Andreas de Vries
 *  @version 1.0
 */
public class Protein implements java.io.Serializable {
   private static final long serialVersionUID = 9223372035499135132L; // = Long.MAX_VALUE - "Protein".hashCode()

   /** Field to store the name of the current file (without file name extension).*/
   private static String fileName = "Test";
   
   /** Constant determining the "corridor of equality" of two double values.*/
   //private static final double EPSILON = 1.e-12;
   
   /** Nucleobases. These are U (RNA), T (DNA), C, A, G (RNA and DNA). N means unknown.*/
   static final char[] VALUES = new char[values().length + 5];
   static {
      int i;
      VALUES[0] = ' ';
      for (i=1; i <= AminoAcid.values().length; i++) {
         VALUES[i] = AminoAcid.values()[i-1].getSymbol();
      }
      VALUES[i++] = 'Z';
      VALUES[i++] = 'X';
      VALUES[i++] = '*';
      VALUES[i++] = '-';
      //System.out.println(java.util.Arrays.toString(VALUES));
   };
   
   /** Bitmask
    *  <pre>
    *    0 = 00000 &rarr; ' ' // &larr; unoccupied place
    *    1 = 00001 &rarr; 'G'
    *    2 = 00010 &rarr; 'A'
    *    3 = 00011 &rarr; 'V'
    *    4 = 00100 &rarr; 'L'
    *    5 = 00101 &rarr; 'I'
    *    6 = 00110 &rarr; 'F'
    *    7 = 00111 &rarr; 'S'
    *    8 = 01000 &rarr; 'T'
    *    9 = 01001 &rarr; 'Y'
    *   10 = 01010 &rarr; 'C'
    *   11 = 01011 &rarr; 'U' // &larr; 21st amino acid
    *   12 = 01100 &rarr; 'M'
    *   13 = 01101 &rarr; 'P'
    *   14 = 01110 &rarr; 'K'
    *   15 = 01111 &rarr; 'H'
    *   16 = 10000 &rarr; 'W'
    *   17 = 10001 &rarr; 'R'
    *   18 = 10010 &rarr; 'N'
    *   19 = 10011 &rarr; 'Q'
    *   20 = 10100 &rarr; 'O' // &larr; 22nd amino acid
    *   21 = 10101 &rarr; 'D'
    *   22 = 10110 &rarr; 'E'
    *   23 = 10111 &rarr; 'Z' // &larr; glutamic acid or glutamine (E or Q)
    *   24 = 11000 &rarr; 'X' // &larr; any amino acid
    *   25 = 11001 &rarr; '*' // &larr; translation stop
    *   26 = 11010 &rarr; '-' // &larr; gap of unknown length
    *  5-Bit group number:       12    11    10    9     8     7     6     5     4     3     2     1
    *  long ( = 64 bits):  |xxxx|98765|43210|98765|43210|98765|43210|98765|43210|98765|43210|98765|43210|
    *  (tens:)                 6           5           4           3           2           1           0
    *  </pre>
    */
   private static long[] BITMASK = {
      0x000000000000001FL,
      0x00000000000003E0L,
      0x0000000000007C00L,
      0x00000000000F8000L,
      0x0000000001F00000L,
      0x000000003E000000L,
      0x00000007C0000000L,
      0x000000F800000000L,
      0x00001F0000000000L,
      0x0003E00000000000L,
      0x007C000000000000L,
      0x0F80000000000000L
   };
      
   /** The name of this protein.*/
   private String name;
   
   /** The amino acid sequence of this protein.*/
   private long[] sequence;
   
   /** The number of amino acids of this protein.*/
   private int size;
   
   /** The absolute frequency distribution of the amino acids.*/
   private TreeMap<Character, Integer> frequencies;
   
   /** The absolute frequency of amino acid pairs of the sequence of this protein.*/
    TreeMap<String, Integer> pairFrequencies;
    
   /** The absolute frequencies of amino acid triplets of the sequence of this protein.*/
    TreeMap<String, Integer> tripletFrequencies;
   
   /** Constructor of a protein with the specified name and the specified sequence 
    *  of amino acids, i.e., the letters A, C, G, T (or U for an RNA protein).
    *  Also accepted are letters specified by the 
    *  <a href="http://en.wikipedia.org/wiki/FASTA_format">FASTA format</a>,
    *  but all letters &ne; 'N' are stored as '?'
    *  @param name the name of the protein
    *  @param sequence the sequence of amino acids
    */
   public Protein(String name, ArrayList<Character> sequence) {
      this.name     = name;
      this.size     = sequence.size();
      this.sequence = new long[1 + sequence.size()/12];
      long c;
            
      for (int i = 0; i < sequence.size(); i++) {
         for (c = 1; c < VALUES.length; c++) {
            if (sequence.get(i) == VALUES[(int) c]) {
               this.sequence[i/12] = (this.sequence[i/12] | (BITMASK[i%12] & (c << 5*(i%12)) ));
               //System.out.println("# i="+i+" " + Long.toBinaryString(this.sequence[i/12]));
               break;
            }
         }
         if (c == VALUES.length - 1) {
            throw new IllegalArgumentException(sequence.get(i) + " is not an amino acid");
         }
      }
              
      this.frequencies        = computeFrequencies();
      //System.out.println("### sequence = " + sequence);
      //System.out.println("### &rarr; " + java.util.Arrays.toString(this.sequence));
      //System.out.println("### size = " + sequence.size() + " &rarr; length="+this.sequence.length);
      this.pairFrequencies    = computePairFrequencies();
      this.tripletFrequencies = computeTripletFrequencies();
   }
   
   /** Constructor of a protein with the specified name and the specified sequence 
    *  of amino acids, i.e., the letters A, C, G, T (or U for an RNA protein).
    *  @param name the name of the protein
    *  @param sequence the sequence of amino acids
    */
   public Protein(String name, char[] sequence) {
      this.name     = name;
      this.size     = sequence.length;
      this.sequence = new long[1 + sequence.length/12];
      long c;
            
      for (int i = 0; i < sequence.length; i++) {
         for (c = 1; c < VALUES.length; c++) {
            if (sequence[i] == VALUES[(int) c]) {
               this.sequence[i/12] = (this.sequence[i/12] | (BITMASK[i%12] & (c << 5*(i%12)) ));
               break;
            }
         }
         if (c == VALUES.length - 1) {
            throw new IllegalArgumentException(sequence[i] + " is not an amino acid");
         }
      }
              
      this.frequencies        = computeFrequencies();
      this.pairFrequencies    = computePairFrequencies();
      this.tripletFrequencies = computeTripletFrequencies();
   }
   
   /** Constructor of a protein with the specified name and the specified sequence 
    *  of amino acids, i.e., the letters A, C, G, T (or U for an RNA protein).
    *  @param name the name of the protein
    *  @param sequence sequence of amino acids
    */
   public Protein(String name, CharSequence sequence) {
      this.sequence = new long[1 + sequence.length()/12];
      long c;
            
      for (int i = 0; i < sequence.length(); i++) {
         for (c = 1; c < VALUES.length; c++) {
            if (sequence.charAt(i) == VALUES[(int) c]) {
               this.sequence[i/12] = (this.sequence[i/12] | (BITMASK[i%12] & (c << 5*(i%12)) ));
               break;
            }
         }
         if (c == VALUES.length - 1) {
            throw new IllegalArgumentException(sequence.charAt(i) + " is not an amino acid");
         }
      }
      
      this.frequencies        = computeFrequencies();
      this.pairFrequencies    = computePairFrequencies();
      this.tripletFrequencies = computeTripletFrequencies();
   }
   
   /** Returns whether the specified character should be ignored for statistical 
    *  evaluations.
    *  @param c an amino acid letter
    *  @param onlyRecognized specifies whether unrecognized amino acid letters are to be considered
    *  @param onlyHuman specifies whether only amino acids in human proteins are to be considered
    *  @return true if the letter is to be ignored
    */
   private static boolean ignore(char c, boolean onlyRecognized, boolean onlyHuman) {
      return 
          c == ' ' || 
        ((c == 'Z' || c == 'X' || c == '*' || c == '-') && onlyRecognized) ||
        ( c == 'O' && onlyHuman);
   }
  
   /** Returns whether the specified character should be ignored for statistical 
    *  evaluations.
    *  @param c an amino acid letter
    *  @param onlyRecognized specifies whether unrecognized amino acid letters
    *  @return true if the letter is to be ignored
    */
   private static boolean ignore(char c, boolean onlyRecognized) {
      return ignore(c,onlyRecognized,true);
   }
  
   /** Returns whether the specified character should be ignored for statistical 
    *  evaluations.
    *  @param c an amino acid letter
    *  @return true if the letter is to be ignored
    */
   private static boolean ignore(char c) {
      return ignore(c, true);
   }
   
   /** Returns whether the specified character should be ignored for statistical 
    *  evaluations.
    *  @param sequence an amino acid sequence
    *  @param onlyRecognized specifies whether unrecognized amino acid letters should be ignored
    *  @return true if the letter is to be ignored
    */
   private static boolean ignore(String sequence, boolean onlyRecognized) {
      boolean ignore = false;
      for (int i = 0; i < sequence.length(); i++) {
         ignore |= ignore(sequence.charAt(i), onlyRecognized);
      }
      return ignore;
   }
  
   /** Computes the absolute frequency distribution of the amino acids of the 
    *  sequence of this protein.
    *  @return the absolute frequency distribution of the amino acids of the 
    *  sequence of this protein
    */
   private TreeMap<Character, Integer> computeFrequencies() {
      TreeMap<Character, Integer> p = new TreeMap<>();
      int[] f = new int[VALUES.length];
      
      long code;
      int i, j;
      
      for (i = 0; i < sequence.length; i++) {
         for (j = 0; j < 12; j++) {
            code = ((sequence[i] & BITMASK[j]) >> (5*j));
            //System.out.println("### j=" + j + ": " + Long.toBinaryString(BITMASK[j]) + " &rarr; " + code);
            if (code == 0) break;  // the current array entry does not contain amino acids anymore
            f[(int) code]++;
         }
      }
      
      for (i = 0; i < VALUES.length; i++) {
         p.put(VALUES[i], f[i]);
      }
      return p;
   }
   
   /** Computes the absolute frequency of amino acid pairs of the sequence of 
    *  this protein.
    *  @return the absolute frequency of amino acid pairs of the sequence of 
    *  this protein
    */
   private TreeMap<String, Integer> computePairFrequencies() {
      TreeMap<String, Integer> p = new TreeMap<>();
      int[] f = new int[1024]; // 2*5 Bit = 1024 mögliche Werte
      int c1, c2;  // codes for first and second letter
      
      long code = 0;
      int i, j;
      
      for (i = 0; i < sequence.length; i++) {
         for (j = 1; j < 12; j++) {
            code = (sequence[i] & BITMASK[j]) >> (5*j - 5);
            if (code == 0) break;  // the current array entry does not contain amino acids anymore
            //System.out.print("### " + VALUES[(int) ((sequence[i] & BITMASK[j-1]) >> 5*(j-1))]);
            //System.out.print(VALUES[(int) ((sequence[i] & BITMASK[j]) >> 5*j)]);
            code |= (sequence[i] & BITMASK[j-1]) >> (5*j - 5);
            //System.out.println(" => i=" + i + ", code=" + code + " = " + Long.toBinaryString(code));
            f[(int) code] += 1;
         }
         // possibly there is a letter in the next "row"
         //System.out.println("### code=" + code + "=" + Long.toBinaryString(code) + ", j=" + j + ", i=" + i + ", length=" + sequence.length);
         if (code != 0 && i < sequence.length - 1) {
            // preceding amino acid letter: (sequence[i] & BITMASK[j-1]) >> 5*(j-1) ...
            code = (sequence[i] & BITMASK[11]) >> 55 | (sequence[i+1] & BITMASK[0] << 5);
            f[(int) code] += 1;
            /*
            System.out.println("### code=" + code + "=" + Long.toBinaryString(code) //);
               + ", " + Long.toBinaryString((sequence[i]   & BITMASK[11]) >> 55)
               + ", " + Long.toBinaryString((sequence[i+1] & BITMASK[0])  <<  5)
            );
            // */
            //System.out.println("### code=" + code + "=" + Long.toBinaryString(code));
         }
      }
      
      for (i = 1; i < f.length; i++) {
         //System.out.print("### i&992=" + Long.toBinaryString(i&992) + " (i&31)=" + Long.toBinaryString(i&31));
         c1 = (i &  31);       //  31 = 0000011111 _2
         c2 = (i & 992) >> 5;  // 992 = 1111100000 _2
         if (c1*c2 == 0 || c1 >= VALUES.length || c2 >= VALUES.length) continue;
         //if (f[i] > 0) System.out.println(" ### i=" + i + ", pair=" + VALUES[c1] + VALUES[c2] + ", f=" + f[i]);
         //System.out.print("("+c1+","+c2+") ");
         p.put("" + VALUES[c1] + VALUES[c2], f[i]);
      }
      return p;
   }
   
   /** Computes the absolute frequencies of amino acid triplets from the sequence of this protein.*/
   private TreeMap<String, Integer> computeTripletFrequencies() {
      TreeMap<String, Integer> p = new TreeMap<>();            
      int[] f = new int[32768]; // 3*5 bits = 32768 possible values !!!
      int c1, c2, c3;  // codes for first and second letter
      
      long code = 0;
      int i, j;
      
      for (i = 0; i < sequence.length; i++) {
         for (j = 2; j < 12; j++) {
            code = (sequence[i] & BITMASK[j]) >> (5*j - 10);
            if (code == 0) break;  // the current array entry does not contain amino acids anymore
            //System.out.print("### " + VALUES[(int) ((sequence[i] & BITMASK[j-1]) >> 5*(j-1)) - 1]);
            //System.out.print(VALUES[(int) ((sequence[i] & BITMASK[j]) >> 5*j) - 1]);
            code |= (sequence[i] & BITMASK[j-1]) >> (5*j - 10) | (sequence[i] & BITMASK[j-2]) >> (5*j - 10);
            //System.out.println(" => i=" + i + ", code=" + code + " = " + Long.toBinaryString(code));
            f[(int) code]++;
         }
         // possibly there is a letter in the next "row"
         //System.out.println("### code=" + code + "=" + Long.toBinaryString(code) + ", j=" + j + ", i=" + i + ", length=" + sequence.length);
         if (code != 0 && i < sequence.length - 1) {
            // preceding amino acid letter: (sequence[i] & BITMASK[j-1]) >> 5*(j-3) ...
            code = (sequence[i]   & BITMASK[10]) >> 50 | 
                   (sequence[i]   & BITMASK[11]) >> 50 | 
                   (sequence[i+1] & BITMASK[0])  << 10;
            f[(int) code]++;
            /*
            System.out.println("### code=" + code + "=" + Long.toBinaryString(code) //);
               + "\n "+ Long.toBinaryString((sequence[i]   & BITMASK[10]) >> 50)
               + ", " + Long.toBinaryString((sequence[i]   & BITMASK[11]) >> 50)
               + ", " + Long.toBinaryString((sequence[i+1] & BITMASK[0])  << 10)
            );
            // */
            //System.out.println("### code=" + code + "=" + Long.toBinaryString(code));
            // ... and possibly there is another letter in the next "row"
            if ((sequence[i+1] & BITMASK[1]) != 0) {
               // preceding amino acid letter: (sequence[i] & BITMASK[j-1]) >> 5*(j-5) ...
               code = (sequence[i]   & BITMASK[11]) >> 55 |
                      (sequence[i+1] & BITMASK[0])  <<  5 |
                      (sequence[i+1] & BITMASK[1])  <<  5;
               f[(int) code]++;
               /*
               System.out.println("+++ code=" + code + "=" + Long.toBinaryString(code) //);
                  + "\n "+ Long.toBinaryString((sequence[i]   & BITMASK[11]) >> 55)
                  + ", " + Long.toBinaryString((sequence[i+1] & BITMASK[0])  <<  5)
                  + ", " + Long.toBinaryString((sequence[i+1] & BITMASK[1])  <<  5)
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
         c1 = (i &    31);       //    31 = 000000000011111 _2
         c2 = (i &   992) >>  5; //   992 = 000001111100000 _2
         c3 = (i & 31744) >> 10; // 31744 = 111110000000000 _2
         if (c1*c2*c3 == 0 || 
             c1 >= VALUES.length || c2 >= VALUES.length || c3 >= VALUES.length
         ) continue;
         //System.out.println("(c1,c2,c3) = (" + c1 + "," + c2 + "," + c3 + ")"); 
         //if (f[i] > 0) System.out.println(" ### i=" + i + ", triplet=" + VALUES[c1] + VALUES[c2] + VALUES[c3] + ", f=" + f[i]);
         p.put("" + VALUES[c1] + VALUES[c2] + VALUES[c3], f[i]);
      }
      return p;
   }
   
   /** Decodes the amino acid sequence of this protein.*/
   private ArrayList<Character> decodeSequence() {
      ArrayList<Character> out = new ArrayList<>(21*sequence.length);
      long code;
      //System.out.println("### sequence = " + java.util.Arrays.toString(sequence));
      for (int i = 0; i < sequence.length; i++) {
         for (int j = 0; j < 12; j++) {
            code = (sequence[i] & BITMASK[j]) >> (5*j);
            //System.out.println("### j=" + j + ": " + BITMASK[j] + " &rarr; " + code);
            if (code == 0) break;
            out.add(VALUES[(int) code]);
         }
      }
      return out;
   }
   
   /** Returns the name of this protein.
    *  @return the name of this protein
    */
   public String getName() {
      return name;
   }
  
   /** Returns the amino acid sequence of this protein.
    *  @return the amino acid sequence of this protein
    */
   public StringBuilder getSequence() {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < sequence.length; i++) {
         for (int j = 0; j < 12; j++) {
            long code = (sequence[i] & BITMASK[j]) >> (5*j);
            if (code == 0) break;
            out.append(VALUES[(int) code]);
         }
      }
      return out;
   }
  
   /** Returns the amino acid sequence of this protein.
    *  @return the amino acid sequence of this protein
    */
   public ArrayList<Character> getSequenceAsArrayList() {
      return decodeSequence();
   }
  
   /** Returns the absolute frequency distribution of the amino acids.
    *  @return the frequency distribution of the amino acids
    */
   public TreeMap<Character, Integer> getFrequencies() {
      return frequencies;
   }
  
   /** Returns the absolute frequency distribution of the amino acid pairs.
    *  @return the frequency distribution of the amino acid pairs
    */
   public TreeMap<String, Integer> getPairFrequencies() {
      return pairFrequencies;
   }
  
   /** Returns the absolute frequency distribution of the amino acids.
    *  @return the frequency distribution of the amino acid triplets
    */
   public TreeMap<String, Integer> getTripletFrequencies() {
      return tripletFrequencies;
   }
  
   /** Returns the absolute frequency distribution of the amino acids triplets
    *  with the highest <i>n</i><sub>high</sub> frequencies and the
    *  lowest <i>n</i><sub>low</sub> frequencies, specified by the to parameters
    *  <code>highest</code> and <code>lowest</code>.
    *  @param highest the limit of most frequent triplets to be returned
    *  @param lowest the limit of least frequent triplets to be returned
    *  @return the frequency distribution of the extremal amino acid triplets
    */
   public TreeMap<String, Integer> getExtremalTripletFrequencies(int highest, int lowest) {
      TreeMap<String, Integer> extrema = new TreeMap<>();
      java.util.TreeSet<Integer> f = new java.util.TreeSet<>(tripletFrequencies.values());
      int i = 1, lowerLimit = 0, upperLimit = size;
      for (int x : f) {
         if (i == lowest) {
            lowerLimit = x;
            break;
         }
         i++;
      }
      i = 1;
      f = new java.util.TreeSet<>(f.descendingSet());
      for (int x : f) {
         if (i == highest) {
            upperLimit = x;
            break;
         }
         i++;
      }
      System.out.println("### low="+lowerLimit+", high=" + upperLimit+" (highest="+highest+", lowest="+lowest+")");
      for (String k: tripletFrequencies.keySet()) {
         if(tripletFrequencies.get(k) <= lowerLimit || tripletFrequencies.get(k) >= upperLimit) {
            extrema.put(k, tripletFrequencies.get(k));
         }
      }
      return extrema;
   }
  
   /** Returns the frequency distribution of the amino acids as HTML table.
    *  @return an HTML string representing the frequency distribution of the amino acids
    */
   public String getFrequenciesAsHTMLTable() {
      String out = "<table border=\"1\">";
      int counter = 0;
      for (char b : VALUES) {
         if (ignore(b)) continue;
         //if (frequencies.get(b) == 0.0) continue;
         if (counter % 11 == 0) out += "<tr>";
         out += "<td>" + b + ": " + frequencies.get(b) + "</td>";
         if (counter % 11 == 10) out += "</tr>";
         counter++;
      }
      out += "</table>";
      return out;
   }
  
   /** Returns the frequency distribution of this protein as a CSV data table string.
    *  The data separator is the tabulator sign '\t'.
    *  @return CSV data table string
    */
   protected String getFrequenciesAsCSVTable() {
      String out1 = "\n", out2 = "\n";
      for (char c : frequencies.keySet()) {
         if (ignore(c, true)) continue;
         out1 += c + "\t";
         out2 += frequencies.get(c) + "\t";
      }
      return out1 + out2;
   }
  
   /** Returns the specified map as a CSV data table string.
    *  The data separator is the tabulator sign '\t'.
    *  @param map data map
    *  @return CSV data table string
    */
   protected static String getAsCSVTable(java.util.Map<String, Integer> map) {
      String out1 = "\n", out2 = "\n";
      boolean ignore;
      for (String c : map.keySet()) {
         ignore = false;
         for (int i = 0; i < c.length(); i++) {
            ignore |= ignore(c.charAt(i), true);
         }
         if (ignore) continue;
         out1 += c + "\t";
         out2 += map.get(c) + "\t";
      }
      return out1 + out2 + "\n";
   }
  
   /** Returns the frequency distribution of the amino acids as HTML graph.
    *  @return a graphical HTML string representing the frequency distribution of the amino acids
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
         if (ignore(b, true)) continue;
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
  
   /** Returns the frequency distribution of the amino acids as HTML graph in 
    *  landscape format.
    *  @return a graphical HTML string in landscape format representing the 
    *  frequency distribution of the amino acids
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
         if (ignore(c, true)) continue;
         int height = (frequencies.get(c) * tableHeight / max);
         out += "<td style=\"font-size:smaller\"><div style=\"height:" + height + "px; width:7px; " +
            "border-width:1px; border-style:solid; background-color:#FFD700\"></div></td>";
      }
      out += "</tr><tr>";
      for (Character c : frequencies.keySet()) {
         if (ignore(c, true)) continue;
         out += "<td style=\"font-size:small\">" + c + "&nbsp;</td>";
      }
      out += "</tr>";
      out += "</table>";
      return out;
   }
   
   /** Returns the frequency distribution of amino acid pairs as HTML graph.
    *  @return a graphical HTML string representing the frequency distribution of amino acid pairs
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
         if (ignore(pair, false)) continue;
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
  
   /** Returns the frequency distribution of amino acid triplets as HTML graph.
    *  @param frequencies frequency distribution
    *  @return a graphical HTML string representing the frequency distribution 
    *  of the amino acid triplets
    */
   public static String getFrequenciesAsHTMLGraph(TreeMap<String, Integer> frequencies) {
      int tableWidth = 100; // unit: px
      String out  = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
      int max  = 0;

      for (String string : frequencies.keySet()) {
         if (max < frequencies.get(string)) max = frequencies.get(string);
      }
      max++;
      
      for (String string : frequencies.keySet()) {
         if (frequencies.get(string) == 0) continue;
         if (ignore(string, true)) continue;
         int width = frequencies.get(string) * tableWidth / max;
         out += "<tr><td style=\"font-size:smaller\">" + string + "&nbsp;</td><td>" +
         "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
         "<tr><td style=\"font-size:smaller\"><div style=\"width:" + width + "px; height:5px; " +
         "border-style:solid; border-width:1px; background-color:#FFD700\"></div></td>" +
         "<td style=\"font-size:smaller\">&nbsp;" + frequencies.get(string) + 
         "</td></tr></table>" +
         "</td></tr>";
      }
      out += "</table>";
      return out;
   }
   
  
   /** Returns the probability to obtain the specified amino acid of this protein sequence.
    *  @param b the specified amino acid
    *  @return the probability of the amino acid of this protein
    */
   public double p(char b) {
      return (double) frequencies.get(b) / size;
   }
   
   /** Returns the entropy of the frequency distribution of the amino acids of this protein.
    *  The entropy is computed to the basis 2, i.e.,
    *  <i>H</i> = - &sum; <i>p<sub>i</sub></i> log<sub>2</sub><i>p<sub>i</sub></i>.
    *  @return the entropy of the frequency distribution of this protein
    */
   public double entropy() {
      double h = 0, p;
      for (char b : VALUES) {
         p  = (double) frequencies.get(b) / size;
         if (p != 0) {
            h -= p * Math.log(p);
         }
      }
      return h / Math.log(2);
   }
   
   /** Returns a string representing this protein, with line breaks encoded by
    *  "&lt;br&gt;" instead of '\n'.
    *  @return an HTML string representing this protein
    */
   public String asHTMLString() {
      int i_max = 50;
      StringBuilder out = new StringBuilder(name + "<br><br>");
      int max = sequence.length > i_max ? i_max : sequence.length;
      long code;
      for (int i = 0; i < max; i++) {
         for (int j = 0; j < 12; j++) {
            code = (sequence[i] & BITMASK[j]) >> (5*j);
            if (code == 0) break;
            if ((12*i + j) % 100 == 0 && i > 0) out.append("<br>");
            out.append(VALUES[(int) code]);
         }
      }
      if (sequence.length > i_max) out.append("...");
      return out.toString();
   }
  
   /** Returns a string representing this protein.
    *  @return a string representing this protein
    */
   @Override
   public String toString() {
      int i_max = 10;
      StringBuilder out = new StringBuilder();
      out.append(name + "\n");
      int max = sequence.length > i_max ? i_max : sequence.length;
      long code;
      for (int i = 0; i < max; i++) {
         for (int j = 0; j < 12; j++) {
            code = (sequence[i] & BITMASK[j]) >> (5*j);
            if (code == 0) break;
            out.append(VALUES[(int) code]);
         }
      }
      if (sequence.length > i_max) out.append("...");
      return out.toString();
   }
   
   /** Returns a protein after opening a file in 
    *  <a href="http://en.wikipedia.org/wiki/FASTA_format" target="_top">FASTA format</a>, 
    *  determined by a file chooser dialog.
    *  @return a protein determined by the FASTA file, or null if no file is selected
    */
   //@SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static Protein loadFASTA() {
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
      Protein protein = null;
      
      boolean comment, named = false; // comment is necessary since a comment line may be split up by buffer size!
      
      ArrayList<Character> aminoAcids;
      
      int returnVal = fileChooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         file = fileChooser.getSelectedFile();
         System.out.println("Opened file " + file);
         aminoAcids = new ArrayList<>((int) file.length());
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
                     aminoAcids.add(buffer[i]);
                  }
               }
            }
            //System.out.println("### erzeuge Genom ...");
            protein = new Protein(name, aminoAcids);
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
      return protein;
   }
   
   /** Saves this protein in PROT format. */
   public void save() {
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }
      
      FileNameExtensionFilter filter = new FileNameExtensionFilter("Protein file (*.prot)", "prot");
      fileChooser.addChoosableFileFilter(filter);
      fileChooser.setSelectedFile(new File(fileName + ".prot"));
      
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
      
   /** Saves this protein as CSV file. */
   public void saveAsCSV() {
      if (fileName == null || fileName.equals("") || fileName.equals("Test")) {
         fileName = (name.length() < 50) ? name : name.substring(0, 50);
         fileName = fileName.replace(' ','_');
         fileName = fileName.replace(',','_');
         //fileName = fileName.replace('.','_');
         fileName = fileName.replace('|','-');
         if (fileName.equals("")) fileName = "Test";
      }
      
      //JFileChooser fileChooser;
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
   
   /** Returns a protein after opening a file in GNOM format, found by a file chooser dialog.
    *  @return a protein determined by the GNOM file, or null if no file is selected
    */
   //@SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static Protein loadProtein() {
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }

      FileNameExtensionFilter filter = new FileNameExtensionFilter("Protein file (*.prot)", "prot");
      fileChooser.addChoosableFileFilter(filter);
      
      Protein protein = null;
      
      int returnVal = fileChooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         File file = fileChooser.getSelectedFile();
         // open the file:
         ObjectInputStream input = null;
         try {
            input = new ObjectInputStream(new FileInputStream(file));
            protein = (Protein) input.readObject();
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
            try {
               Properties props = new Properties();
               props.setProperty("currentDirectory", file.getParent());
               props.storeToXML(new FileOutputStream(propertyFile), null);
            } catch (Exception e) {
               System.err.println(e.getMessage());
            }
         }
      }
      return protein;
   }
   
   /*
   public static void main(String... args) {
      String out = "<html>";
      //String sequence = "GTATGGTCGGCCTGAGTTAA";
      //Protein test = new Protein("E. Coli", sequence);
      //System.out.println(test);
      
      Protein protein;
      
      char[] sequence = {'A', 'A', 'G', 'T', 'T', 'C', 'G', 'A', 'A', 'G'};
      protein = new Protein("Test", sequence);
      
      out += "<br>" + protein.asHTMLString();
      out += "<br>" + protein.getFrequenciesAsHTMLTable() 
      + "<br>" + protein.getFrequenciesAsHTMLGraph2()
      ;
      out += "<br><br>Entropy:" + protein.entropy();
      //out += "<br>f(AA)=" + protein.getPairFrequencies().get("AA");
      //out += "<br>f(AG)=" + protein.getPairFrequencies().get("AG");
      //out += "<br>f(GG)=" + protein.getPairFrequencies().get("GG");
      //out += "<br>f(AT)=" + protein.getPairFrequencies().get("AT");
      //out += "<br>f(TT)=" + protein.getPairFrequencies().get("TT");
      out += "<br>f(AAA)=" + protein.getTripletFrequencies().get("AAA");
      out += "<br>f(AAG)=" + protein.getTripletFrequencies().get("AAG");
      out += "<br>f(AGG)=" + protein.getTripletFrequencies().get("AGG");
      out += "<br>f(GGG)=" + protein.getTripletFrequencies().get("GGG");
      out += "<br>" + getFrequenciesAsHTMLGraph(protein.getExtremalTripletFrequencies(0,1));
      out += "<br><b>Vanishing triplet frequencies:</b>";
      for (String triplet : protein.getTripletFrequencies().keySet()) {
         if (ignore(triplet, true)) continue;
         if (protein.getTripletFrequencies().get(triplet) == 0) {
            //out += triplet + ", ";
         }
      }
      
      javax.swing.JOptionPane.showMessageDialog(null, out, "Protein", -1);
      
      out = "<html>"; // + protein.getName() + ":<br>" + protein.getFrequenciesAsHTMLGraph();
      //javax.swing.JOptionPane.showMessageDialog(null, out);
      
      protein = loadFASTA();
      if (protein != null) {
        out += "<br>" + protein.getName();
        out += protein.asHTMLString();
//        out += ":<br><br>" + protein.getFrequenciesAsHTMLGraph();
//        out += "<br>" + protein.getFrequenciesAsHTMLTable();
//        out += "<br><br>Entropy:" + protein.entropy() + "<br><br>" + protein.asHTMLString();
//        out += "<br>" + protein.getPairFrequenciesAsHTMLGraph();
        out += "<br>" + getFrequenciesAsHTMLGraph(protein.getExtremalTripletFrequencies(0,1));
        /*
        out += "<br>f(AA)=" + protein.getPairFrequencies().get("AA");
        out += "<br>f(AG)=" + protein.getPairFrequencies().get("AG");
        out += "<br>f(GG)=" + protein.getPairFrequencies().get("GG");
        out += "<br>f(AT)=" + protein.getPairFrequencies().get("AT");
        out += "<br>f(AAA)=" + protein.getTripletFrequencies().get("AAA");
        out += "<br>f(AAG)=" + protein.getTripletFrequencies().get("AAG");
        out += "<br>f(AGG)=" + protein.getTripletFrequencies().get("AGG");
        out += "<br>f(AGA)=" + protein.getTripletFrequencies().get("AGA");
        out += "<br>f(GGG)=" + protein.getTripletFrequencies().get("GGG");

        out += "<br>" + getFrequenciesAsHTMLGraph(protein.getExtremalTripletFrequencies(0,1));
        
        StringBuilder out2 = new StringBuilder("Vanishing amino acid frequencies " + fileName + ":\n");
        int count;
        count = 1;
        for (char letter : protein.getFrequencies().keySet()) {
           if (ignore(letter, true)) continue;
           if (protein.getFrequencies().get(letter) == 0) {
              out2.append(letter + ", ");
              if (count % 27 == 0) out2.append("\n");
              count++;
           }
        }
        Utils.save("OmittedAminoAcids_" + fileName+ ".txt", out2);

        out2 = new StringBuilder("Vanishing pair frequencies " + fileName + ":\n");        
        count = 1;
        for (String pair : protein.getPairFrequencies().keySet()) {
           if (ignore(pair, true)) continue;
           if (protein.getPairFrequencies().get(pair) == 0) {
              out2.append(pair + ", ");
              if (count % 20 == 0) out2.append("\n");
              count++;
           }
        }
        Utils.save("OmittedPairs_" + fileName+ ".txt", out2);

        out2 = new StringBuilder("Vanishing triplet frequencies " + fileName + ":\n");        
        count = 1;
        for (String triplet : protein.getTripletFrequencies().keySet()) {
           if (ignore(triplet, true)) continue;
           if (protein.getTripletFrequencies().get(triplet) == 0) {
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
      
      /*
//      out = "<html>";
//      protein = loadProtein();
//      if (protein != null) {
//         //--- Determine runtime of decoding: (30 mio letters => 1 sec)
//         //long time = System.currentTimeMillis();
//         //int laenge = protein.decodeSequence().size();
//         //time = System.currentTimeMillis() - time;
//         //System.out.println("### Sequence length: " + laenge);
//         //System.out.println("### Sequence decoding needed " + time/1000. + " sec");
//         
//         out += "<br>" + protein.getName() + ":<br>" + protein.getFrequenciesAsHTMLGraph() + 
//          protein.getFrequenciesAsHTMLTable();
//         out += "<br><br>Entropy:" + protein.entropy() + " (" + protein.getSequence().length() + " amino acids)";
//         out += "<br><br>" + protein.asHTMLString();
//         javax.swing.JOptionPane.showMessageDialog(null, out, "Protein", -1);
//      }
   }
   // */
}

