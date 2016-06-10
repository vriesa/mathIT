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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/** This class represents the DNA genome of a living organism. The genome is the entire 
 *  genetic information of a cell. It specifies an algorithm for creating and 
 *  maintaining the entire organism containing the cell. Gene regulatory
 *  proteins supply some of the switches  turning specific instructions of the 
 *  algorithm on and off. These are the start codon ATG and the stop codons
 *  TAA, TAG and TGA.
 *  For usual DNA sequences, the heap memory heap size has to be augmented.
 *  A DNA sequence consisting of some 30 million letters requires a heap memory
 *  of some 1 GB, which can be allocated by invoking the application via
 *  <p style="text-align:center;">
 *  <code>java -ms1024m -mx1024m org.mathIT.genes.DNA</code>
 *  </p>
 * @author Andreas de Vries
 * @version 1.1
 */
public class DNA implements java.io.Serializable {
   private static final long serialVersionUID = 9223372036854707976L;
   // = Long.MAX_VALUE - "DNA".hashCode()
   /** The name of the property file. This file stores the directory which has 
    *  been opened recently for loading or saving files.
    */
   private static final String propertyFile = 
     System.getProperty("user.home") + System.getProperty("file.separator") + "DNA.xml";
     
   /** Constant determining the "corridor of equality" of two double values.*/
   //private static final double EPSILON = 1.e-12;
   
   private static final char[] VALUES = {'T', 'C', 'A', 'G'}; // DNA
   
   /** Bitmask
    *    0 = 000 -> 'T'
    *    1 = 001 -> 'C'
    *    2 = 010 -> 'A'
    *    3 = 011 -> 'G'
    *  2-Bit group number:  32 31 30 29 28 27 26 25 24 23 22 21 20 19 18 17 16 15 14 13 12 11 10 9  8  7  6  5  4  3  2  1
    *  long ( = 64 bits):  |32|10|98|76|54|32|10|98|76|54|32|10|98|76|54|32|10|98|76|54|32|10|98|76|54|32|10|98|76|54|32|10|
    *  (tens:)                  6              5              4              3                2            1            0
    */
   private static long[] BITMASK = {
      0x0000000000000003L,
      0x000000000000000CL,
      0x0000000000000030L,
      0x00000000000000C0L,
      0x0000000000000300L,
      0x0000000000000C00L,
      0x0000000000003000L,
      0x000000000000C000L,
      0x0000000000030000L,
      0x00000000000C0000L,
      0x0000000000300000L,
      0x0000000000C00000L,
      0x0000000003000000L,
      0x000000000C000000L,
      0x0000000030000000L,
      0x00000000C0000000L,
      0x0000000300000000L,
      0x0000000C00000000L,
      0x0000003000000000L,
      0x000000C000000000L,
      0x0000030000000000L,
      0x00000C0000000000L,
      0x0000300000000000L,
      0x0000C00000000000L,
      0x0003000000000000L,
      0x000C000000000000L,
      0x0030000000000000L,
      0x00C0000000000000L,
      0x0300000000000000L,
      0x0C00000000000000L,
      0x3000000000000000L,
      0xC000000000000000L
   };
      
   /** The name of this genome.*/
   private String name;
   
   /** The nucleobase sequence of this genome.*/
   private long[] sequence;
   
   /** The length of this nucleobase sequence, i.e., the number of letters T, C, A, G.*/
   private int length;
   
   /** The relatuve frequency distribution of the 5 nucleobases. Each frequency is given in %.*/
   private TreeMap<Character, Integer> frequency;
   
   /** Constructor of this genome.
    *  @param name the name of this genome
    *  @param sequence the sequence of nucleobases
    */
   public DNA(String name, ArrayList<Character> sequence) {
      //System.out.println("### erzeuge Genom (size = " + sequence.size() + ")...");
      this.name      = name;
      this.length    = sequence.size();
      this.sequence  = new long[1 + length/32];
      
      char letter;
      for (int i = 0; i < length; i++) {
         letter = sequence.get(i);
         if (letter == 'T') {
            this.sequence[i/32] = (this.sequence[i/32] | (BITMASK[i%32] & 0L));
         } else if (letter == 'C') {
            this.sequence[i/32] = (this.sequence[i/32] | (BITMASK[i%32] & (1L << 2*(i%32))));
         } else if (letter == 'A') {
            this.sequence[i/32] = (this.sequence[i/32] | (BITMASK[i%32] & (2L << 2*(i%32))));
         } else if (letter == 'G') {
            this.sequence[i/32] = (this.sequence[i/32] | (BITMASK[i%32] & (3L << 2*(i%32))));
         } else {
            throw new IllegalArgumentException(letter + " is not a nucleobase");
         }
      }
      //System.out.println("### ... und berechne Haeufigkeit ...");
      this.frequency = computeFrequency();
      //System.out.println("### sequence = " + sequence);
      //System.out.println("### -> " + java.util.Arrays.toString(this.sequence));
      //System.out.println("### size = " + sequence.size() + " -> length="+this.sequence.length);
   }
   
   /** Constructor of this genome.
    *  @param name the name of this genome
    *  @param sequence the sequence of nucleobases
    */
   public DNA(String name, char[] sequence) {
      this.name     = name;
      this.length   = sequence.length;
      this.sequence = new long[1 + length/21];
      for (int i = 0; i < sequence.length; i++) {
         if (sequence[i] == 'T') {
            this.sequence[i/32] = (this.sequence[i/32] | (BITMASK[i%32] & 0L));
         } else if (sequence[i] == 'C') {
            this.sequence[i/32] = (this.sequence[i/32] | (BITMASK[i%32] & (1L << 2*(i%32)) ));
         } else if (sequence[i] == 'A') {
            this.sequence[i/32] = (this.sequence[i/32] | (BITMASK[i%32] & (2L << 2*(i%32)) ));
         } else if (sequence[i] == 'G') {
            this.sequence[i/32] = (this.sequence[i/32] | (BITMASK[i%32] & (3L << 2*(i%32)) ));
         } else {
            throw new IllegalArgumentException(sequence[i] + " is not a nucleobase");
         }
      }
      
      this.frequency = computeFrequency();
   }
   
   /** Constructor of this genome. */
/*    public DNA(ArrayList<Codon> sequence) {
      this.sequence  = new char[3*sequence.size()];
      int[] triplet;
      for (int i=0; i < this.sequence.length; i++) {
         triplet = char.fromCodon(sequence.get(i));
         this.sequence[i]   = triplet[0];
         this.sequence[i+1] = triplet[1];
         this.sequence[i+2] = triplet[2];
      }
      this.frequency = computeFrequency();
   }
 */   
   
   /** Computes the frequency distribution of the nucleobases from the sequence of this genome.*/
   private TreeMap<Character, Integer> computeFrequency() {
      TreeMap<Character, Integer> p = new TreeMap<>();
      int[] f = new int[VALUES.length];  // absolute frequency of each nucleobase (code = 0, ..., 3)
      
      int code, i, j;
      for (i = 0; i < sequence.length; i++) {
         for (j = 0; j < 31; j++) {
            if (32*i + j >= this.length) break;  // the current array entry does not contain nucleobases anymore
            code = (int) ((sequence[i] & BITMASK[j]) >> (2*j));
            //System.out.println("### j=" + j + ": " + BITMASK[j] + " -> " + code);
            f[code]++;
         }
         // j=31 and the highest bit of a long number is the sign bit
         if (32*i + j >= this.length) break;  // the current array entry does not contain nucleobases anymore
         code = (int) ((sequence[i] & 0x4000000000000000L) >> (2*j));
         if (sequence[i] < 0) code += 2;
         System.out.println("### j=" + j + ": " + Long.toBinaryString((sequence[i] & BITMASK[j])) + " -> " + Long.toBinaryString(code));
         f[code]++;
      }
      
      for (code = 0; code < VALUES.length; code++) {
         p.put(VALUES[code], f[code]);
      }
      return p;
   }
   
   /** Decodes the nucleobase sequence of this genome.*/
   private ArrayList<Character> decodeSequence() {
      ArrayList<Character> out = new ArrayList<>(32*sequence.length);
      long code;
      //System.out.println("### sequence = " + java.util.Arrays.toString(sequence));
      for (int i = 0; i < sequence.length; i++) {
         for (int j = 0; j < 32; j++) {
            if (32*i + j >= this.length) break;  // the current array entry does not contain nucleobases anymore
            code = (sequence[i] & BITMASK[j]) >> (2*j);
            //System.out.println("### j=" + j + ": " + BITMASK[j] + " -> " + code);
            out.add(VALUES[(int) code]);
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
      long code;
      for (int i = 0; i < sequence.length; i++) {
         for (int j = 0; j < 32; j++) {
            if (32*i + j >= this.length) break;  // the current array entry does not contain nucleobases anymore
            code = (sequence[i] & BITMASK[j]) >> (2*j);
            out.append(VALUES[(int) code]);
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
  
   /** Returns the frequency distribution of the 4 nucleobases.
    *  Each frequency is given in %.
    *  @return the frequency distribution [in %] of the 4 nucleobases
    */
   public TreeMap<Character, Integer> getFrequency() {
      return frequency;
   }
  
   /** Returns the frequency distribution of the 64 codons as HTML table.
    *  Each frequency is given in %.
    *  @return an HTML string representing the frequency distribution of the 64 codons
    */
   public String getFrequencyAsHTMLTable() {
      java.text.DecimalFormat threeDigits = new java.text.DecimalFormat("0.000");
      String out = "<table border=\"1\">";
      int counter = 0;
      for (char b : VALUES) {
         //if (b == 'U') continue; // we are dealing with DNA genomes
         if (counter % 8 == 0) out += "<tr>";
         //out += "<td>" + c + "</td><td>" + threeDigits.format(frequency.get(c)) + "</td>";
         out += "<td>" + b + ": " + threeDigits.format(frequency.get(b)) + "</td>";
         if (counter % 8 == 7) out += "</tr>";
         counter++;
      }
      out += "</table>";
      return out;
   }
  
   /** Returns the frequency distribution of the 64 codons as HTML graph.
    *  Each frequency is given in %.
    *  @return a graphical HTML string representing the frequency distribution of the 64 codons
    */
   public String getFrequencyAsHTMLGraph() {
      java.text.DecimalFormat threeDigits = new java.text.DecimalFormat("0.000");
      int tableWidth = 600; // unit: px
      String out  = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
      double max  = 0.;
      for (char b : VALUES) {
         if (max < frequency.get(b)) max = frequency.get(b);
      }
      max = Math.ceil(max);
      
      for (char b : VALUES) {
         //if (b == 'U') continue; // we are dealing with DNA genomes
         int width = (int) (frequency.get(b) * tableWidth / max);
         out += "<tr><td style=\"font-size:small\">" + b + "&nbsp;</td><td>" +
            "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
            "<tr><td style=\"font-size:smaller\"><div style=\"width:" + width + "px; height:5px; " +
            "border-style:solid; border-width:1px; background-color:#FFD700\"></div></td>" +
            "<td style=\"font-size:smaller\">&nbsp;" + threeDigits.format(frequency.get(b)) + 
            "%</td></tr></table>" +
            "</td></tr>";
      }
      out += "</table>";
      return out;
   }
  
   /*
   public String getFrequencyAsHTMLGraph2() {
      // ... vertikale Säulen mit gedrehter Beschriftung ... noch nicht fertig!
      java.text.DecimalFormat threeDigits = new java.text.DecimalFormat("0.000");
      int tableHeight = 100;
      String out  = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
      double max  = 0.;
      for (char b : VALUES) {
         if (max < frequency.get(b)) max = frequency.get(b);
      }
      max = Math.ceil(max);
      
      for (char b : VALUES) {
         //if (b == 'U') continue; // we are dealing with DNA genomes
         int height = (int) (frequency.get(b) * tableHeight / max);
         out += "<tr><td style=\"font-size:small\">" + b + "&nbsp;</td><td>" +
            "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
            "<tr><td style=\"font-size:smaller\"><div style=\"height:" + height + "px; width:5px; " +
            "border-style:solid; border-height:1px; background-color:#FFD700\"></div></td>" +
            "<td style=\"font-size:smaller\">&nbsp;" + threeDigits.format(frequency.get(b)) + 
            "%</td></tr></table>" +
            "</td></tr>";
      }
      out += "</table>";
      return out;
   }
   // */
   
   /** Returns the probability to obtain the specified nucleobase of this genome sequence.
    *  @param b the specified nucleobase
    *  @return the probability of the nucleobase of this genome
    */
   public double p(char b) {
      return frequency.get(b) / 100.;
   }
   
   /** Returns the entropy of the frequency distribution of this genome.
    *  The entropy is computed to the basis 2, i.e.,
    *  <i>H</i> = - &sum; <i>p<sub>i</sub></i> log<sub>2</sub><i>p<sub>i</sub></i>.
    *  @return the entropy of the frequency distribution of this genome
    */
   public double entropy() {
      double h = 0, p;
      for (char b : VALUES) {
         p  = frequency.get(b) / 100.;
         if (p != 0) {
            h -= p * Math.log(p);
         }
      }
      return h / Math.log(2);
   }
   
   /** Returns a string representing this genome, with line breaks encoded by
    *  "<br>" instead of '\n'.
    *  @return an HTML string representing this genome
    */
   public String asHTMLString() {
      String out = "";
      ArrayList<Character> seq = decodeSequence();
      for (int i = 0; i < seq.size(); i++) {
         if (i % 100 == 0 && i > 0) out += "<br>";
         out += seq.get(i);
      }
      return out;
   }
  
   /** Returns a string representing this genome.
    *  @return a string representing this genome
    */
   @Override
   public String toString() {
      //StringBuilder out = new StringBuilder();
      //out.append(name + "\n");
      StringBuilder out;
      out = (new StringBuilder()).append(name).append("\n");
      int max = sequence.length > 4? 4 : sequence.length;
      long code;
      for (int i = 0; i < max; i++) {
         for (int j = 0; j < 32; j++) {
            if (32*i + j >= this.length) break;  // the current array entry does not contain nucleobases anymore
            code = (sequence[i] & BITMASK[j]) >> (2*j);
            /*
            if (code == 0L) out.append("T");
            else if (code == 1L) out.append("C");
            else if (code == 2L) out.append("A");
            else if (code == 3L) out.append("G");
            */
            out.append(VALUES[(int) code]);
         }
      }
      if (sequence.length > 4) out.append("...");
      return out.toString();
   }
  
   /** Factory method producing a genome from a specified DNA base sequence of 
    *  the letters A, C, G, T.
    *  @param name the name of the generated genome
    *  @param string DNA base sequence
    * @return the genome specified by the DNA sequence
    */
   public static DNA encode(String name, CharSequence string) {
      char[] sequence = new char[string.length()];
      for (int i = 0; i < sequence.length; i++) {
         sequence[i] = string.charAt(i);
      }
      return new DNA(name, sequence);
   }
        
   /** Returns a genome after opening a file in FASTA format, determined by a file chooser dialog.
    *  @return a genome determined by the FASTA file, or null if no file is selected
    */
   //@SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static DNA loadFASTA() {
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
      DNA genome = null;
      
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
                  if(buffer[i] == '>' || comment) { // comment line in FASTA
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
            genome = new DNA(name, nucleobases);
         } catch (IOException ioe) {
            ioe.printStackTrace();
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
      return genome;
   }
   
   /** Saves this genome in gnom format. */
   public void save() {
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }
      FileNameExtensionFilter filter = new FileNameExtensionFilter("DNA file (*.dna)", "dna");
      fileChooser.addChoosableFileFilter(filter);
      
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
   
   /** Returns a genome after opening a file in GNOM format, found by a file chooser dialog.
    *  @return a genome determined by the GNOM file, or null if no file is selected
    */
   //@SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static DNA loadDNA() {
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }
      FileNameExtensionFilter filter = new FileNameExtensionFilter("DNA file (*.dna)", "dna");
      fileChooser.addChoosableFileFilter(filter);
      
      DNA genome = null;
      
      int returnVal = fileChooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         File file = fileChooser.getSelectedFile();
         // open the file:
         ObjectInputStream input = null;
         try {
            input = new ObjectInputStream(new FileInputStream(file));
            genome = (DNA) input.readObject();
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
      return genome;
   }
   
   /*
   public static void main(String... args) {
      String out = "<html>";
      //String sequence = "GTATGGTCGGCCTGAGTTAA";
      //DNA test = DNA.decode("E. Coli", sequence);
      
      char[] sequence = {'A', 'A', 'G', 'T', 'T', 'C', 'A', 'A'};
      DNA genome = new DNA("Test", sequence);
      
      out += "<br>" + genome.getName() + ":<br>" + genome.asHTMLString() + genome.getFrequencyAsHTMLTable();
      out += "<br>Entropy:" + genome.entropy();
      
      
      javax.swing.JOptionPane.showMessageDialog(null, out);

      out = "<html>"; // + genome.getName() + ":<br>" + genome.getFrequencyAsHTMLGraph();
      //System.out.println(out);
      //javax.swing.JOptionPane.showMessageDialog(null, out);
      
      genome = loadFASTA();
      if (genome != null) {
        out += "<br>" + genome.getName() + ":<br>" + genome.getFrequencyAsHTMLGraph() + 
          genome.getFrequencyAsHTMLTable();
        out += "<br>Entropy:" + genome.entropy();
        javax.swing.JOptionPane.showMessageDialog(null, out);
        genome.save();
      }

      out = "<html>";
      genome = loadDNA();
      if (genome != null) {
         //--- Determine runtime of decoding: (30 mio letters => 1 sec)
         //long time = System.currentTimeMillis();
         //int laenge = genome.decodeSequence().size();
         //time = System.currentTimeMillis() - time;
         //System.out.println("### Sequence length: " + laenge);
         //System.out.println("### Sequence decoding needed " + time/1000. + " sec");
         
         out += "<br>" + genome.getName() + ":<br>" + genome.getFrequencyAsHTMLGraph() + 
          genome.getFrequencyAsHTMLTable();
         out += "<br>Entropy:" + genome.entropy() + " (" + genome.getSequence().length() + " nucleobases)";
         out += "\n" + genome;
         javax.swing.JOptionPane.showMessageDialog(null, out);
      }
   }
   // */
}

