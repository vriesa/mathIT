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
import static org.mathIT.genes.Codon.*;

/** This class represents an ORF (open reading frame), i.e., a possible gene 
 *  (or part of a gene) of a living organism coding a particular protein.
 *  An ORF usually is opened with a start codon ATG and ended with one of the 
 *  stop codons TAA, TAG and TGA.
 *  @author Andreas de Vries
 *  @version 1.0
 */
public class ORF implements java.io.Serializable {
   private static final long serialVersionUID = 9223372036854697276L; 
   // = Long.MAX_VALUE - "ORF".hashCode()
   /** The name of the property file. This file stores the directory which has 
    *  been opened recently for loading or saving files.
    */
   private static final String propertyFile = 
     System.getProperty("user.home") + System.getProperty("file.separator") + "ORF.xml";
   
   
   /** Bitmask. It refers to the bitmask code of DNA codons.
    *  The code is (arbitrarily chosen to be) as follows.
    *  <pre>
       1 &rarr; TTT,  2 &rarr; TCT,  3 &rarr; TAT,  4 &rarr; TGT,
       5 &rarr; TTC,  6 &rarr; TCC,  7 &rarr; TAC,  8 &rarr; TGC,
       9 &rarr; TTA, 10 &rarr; TCA, 11 &rarr; TAA, 12 &rarr; TGA,
      13 &rarr; TTG, 14 &rarr; TCG, 15 &rarr; TAG, 16 &rarr; TGG,
   
      17 &rarr; CTT, 18 &rarr; CCT, 19 &rarr; CAT, 20 &rarr; CGT,
      21 &rarr; CTC, 22 &rarr; CCC, 23 &rarr; CAC, 24 &rarr; CGC,
      25 &rarr; CTA, 26 &rarr; CCA, 27 &rarr; CAA, 28 &rarr; CGA,
      29 &rarr; CTG, 30 &rarr; CCG, 31 &rarr; CAG, 32 &rarr; CGG,
   
      33 &rarr; ATT, 34 &rarr; ACT, 35 &rarr; AAT, 36 &rarr; AGT,
      37 &rarr; ATC, 38 &rarr; ACC, 39 &rarr; AAC, 40 &rarr; AGC,
      41 &rarr; ATA, 42 &rarr; ACA, 43 &rarr; AAA, 44 &rarr; AGA,
      45 &rarr; ATG, 46 &rarr; ACG, 47 &rarr; AAG, 48 &rarr; AGG,
   
      49 &rarr; GTT, 50 &rarr; GCT, 51 &rarr; GAT, 52 &rarr; GGT,
      53 &rarr; GTC, 54 &rarr; GCC, 55 &rarr; GAC, 56 &rarr; GGC,
      57 &rarr; GTA, 58 &rarr; GCA, 59 &rarr; GAA, 60 &rarr; GGA,
      61 &rarr; GTG, 62 &rarr; GCG, 63 &rarr; GAG, 64 &rarr; GGG
    *  </pre>
    *  Due to the fact that the three-letter alphabet of the four letters
    *  T, C, A, G requires 7 bits of memory space, the respective bitmask is as follows:
    *  <pre>
    *  7-bit group number:    9       8       7       6       5       4       3       2       1
    *  long ( = 64 bits):  |x|2109876|5432109|8765432|1098765|4321098|7654321|0987654|3210987|6543210|
    *  (tens:)                  6          5           4          3           2          1          0
    *  </pre>
    */
   private static long[] BITMASK = {
      0x000000000000007FL,
      0x0000000000003F80L,
      0x00000000001FC000L,
      0x000000000FE00000L,
      0x00000007F0000000L,
      0x000003F800000000L,
      0x0001FC0000000000L,
      0x00FE000000000000L,
      0x7F00000000000000L
   };
   
   /** Information about this ORF.*/
   private String info;
   
   /** The codon sequence of this gene.*/
   private long[] sequence;
   
   /** Constructor of this gene where sequence is a DNA sequence without start and stop codons. 
    *  @param info information about this ORF
    *  @param string a DNA sequence
    */
   public ORF(String info, CharSequence string) {
      this.info = info;
      //ArrayList<ORF> genes = new ArrayList<>();
      int offset = 0, max = string.length() - 3;
      String triplet;
      ArrayList<Codon> sequence = new ArrayList<>();
      
      do {
         // Read the triplets:
         while (offset <= max) {
            // !! What if triplet == "ATG" ??
            triplet = string.subSequence(offset, offset + 3).toString();
            sequence.add(Codon.fromString(triplet));
            offset += 3;
         }
      } while (offset <= max);
      this.sequence  = new long[1 + sequence.size()/9];
      for (int i = 0; i < sequence.size(); i++) {
         this.sequence[i/9] = (
           this.sequence[i/9] | 
           ( BITMASK[i%9] & ((long) sequence.get(i).getBitmaskCode() << 7*(i%9)) )
         );
      }
   }
   
   /** Constructor of this gene.
    *  @param info information about this ORF
    *  @param sequence a sequence of codons
    */
   public ORF(String info, ArrayList<Codon> sequence) {
      this(sequence);
      this.info = info;
   }
   
   /** Constructor of this gene.
    *  @param sequence a sequence of codons
    */
   public ORF(ArrayList<Codon> sequence) {
      this.sequence  = new long[1 + sequence.size()/9];
      for (int i = 0; i < sequence.size(); i++) {
         this.sequence[i/9] = (
           this.sequence[i/9] | 
           ( BITMASK[i%9] & ((long) sequence.get(i).getBitmaskCode() << 7*(i%9)) )
         );
      }
   }
   
   /** Constructor of this gene.
    *  @param sequence a sequence of codons
    */
   public ORF(Codon[] sequence) {
      this.sequence  = new long[1 + sequence.length/9];
      for (int i = 0; i < sequence.length; i++) {
         this.sequence[i/9] = (
           this.sequence[i/9] | 
           ( BITMASK[i%9] & ((long) sequence[i].getBitmaskCode() << 7*(i%9)) )
         );
      }
   }
   
   /** Constructor of this gene.
    *  @param frequency the frequency distribution of the 64 codons
    */
   public ORF(TreeMap<Codon, Double> frequency) {
      this.sequence  = new long[0];
   }
   
   /** Computes the frequency distribution from the codon sequence of this gene.*/
   private static TreeMap<Codon, Double> computeFrequency(ArrayList<ORF> genes) {
      TreeMap<Codon, Double> p = new TreeMap<>();
      
      int[] f = new int[Codon.values().length + 2]; // Codons + space + NNN
      
      // Count occurences of each codon:
      int count = 0;
      int code;
      
      for (ORF gene : genes) {
         for (int i = 0; i < gene.sequence.length; i++) {
            for (int j = 0; j < 9; j++) {
               code = (int) ((gene.sequence[i] & BITMASK[j]) >> (7*j));
               //System.out.println("### j=" + j + ": " + BITMASK[j] + " &rarr; " + code);
               if (code == 0) break; // the current array entry does not contain codons anymore
               f[code] += 1;
               count++;
            }
         }
      }
      
      for (Codon c : values()) {
         p.put(c, 100. * f[c.getBitmaskCode()] / count);
      }
      return p;
   }
   
   /** Returns the info of this ORF.
    *  @return the info of this ORF
    */
   public String getInfo() {
      return info;
   }

   /** Returns the codon sequence of this gene.
    *  @return the codon sequence of this gene
    */
   public ArrayList<Codon> getSequence() {
      ArrayList<Codon> list = new ArrayList<>(9*sequence.length);
      byte code;
      for (int i = 0; i < sequence.length; i++) {
         for (int j = 0; j < 9; j++) {
            code = (byte) ((sequence[i] & BITMASK[j]) >> 7*j);
            if (code == 0) break; // there is no codon anymore ...
            else list.add(Codon.fromBitmaskCode(code));
         }
      }
      return list;
   }
  
   /** Returns the frequency distribution of the codons in the specified list of genes as HTML table.
    *  Each frequency is given in %.
    *  @param genes a list of genes
    *  @return an HTML string representing the frequency distribution of the codons in the specified list
    */
   public static String getFrequencyAsHTMLTable(ArrayList<ORF> genes) {
      java.text.DecimalFormat threeDigits = new java.text.DecimalFormat("0.000");
      TreeMap<Codon, Double> frequency = computeFrequency(genes);
      
      String out = "<table border=\"1\">";
      int counter = 0;
      for (Codon c : frequency.keySet()) {
         if (c.equals(NNN)) continue;
         if (counter % 8 == 0) out += "<tr>";
         if (!c.equals(NNN)) {
            out += "<td>" + c + ": " + threeDigits.format(frequency.get(c)) + "</td>";
         }
         if (counter % 8 == 7 && counter != 63) out += "</tr>";
         counter++;
      }
      out += "</table>";
      if (frequency.get(NNN) > 0.0) {
         out += "<table border=\"1\"><tr><td>NNN: " + threeDigits.format(frequency.get(NNN)) + 
           "</td></tr></table>"; 
      }
      return out;
   }
  
   /** Returns the frequency distribution of the codons in the specified list of ORF's as HTML graph.
    *  Each frequency is given in %.
    *  @param orfs a list of ORF's
    *  @return a graphical HTML string representing the frequency distribution of the 64 codons
    */
   public static String getFrequencyAsHTMLGraph(ArrayList<ORF> orfs) {
      // ... noch nicht ganz perfekt ... z.B.: vertikale Säulen mit gedrehter Beschriftung
      java.text.DecimalFormat threeDigits = new java.text.DecimalFormat("0.000");
      TreeMap<Codon, Double> frequency = computeFrequency(orfs);
      int tableWidth = 600;
      String out  = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
      double max  = 0.;
      for (Codon c : Codon.values()) {
         if (max < frequency.get(c)) max = frequency.get(c);
      }
      max = Math.ceil(max);
      
      for (Codon c : Codon.values()) {
         if (c.equals(NNN) && frequency.get(NNN) == 0.0) continue;
         int width = (int) (frequency.get(c) * tableWidth / max);
         out += "<tr><td style=\"font-size:smaller\">" + c + "&nbsp;</td><td>" +
            "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
            "<tr><td style=\"font-size:smaller\"><div style=\"width:" + width + "px; height:5px; " +
            "border-style:solid; border-width:1px; background-color:#FFD700\"></div></td>" +
            "<td style=\"font-size:smaller\">&nbsp;" + threeDigits.format(frequency.get(c)) + "%</td></tr></table>" +
            "</td></tr>";
      }
      out += "</table>";
      return out;
   }
  
   /** Returns the entropy of the frequency distribution of this gene.
    *  The entropy is computed to the basis 2, i.e.,
    *  <i>H</i> = - &sum; <i>p<sub>i</sub></i> log<sub>2</sub><i>p<sub>i</sub></i>.
    *  @param orfs a list of ORF's
    *  @return the entropy of the frequency distribution of this list of ORF's
    */
   public static double entropy(ArrayList<ORF> orfs) {
      TreeMap<Codon, Double> frequency = computeFrequency(orfs);
      double h = 0, p;
      for (Codon c : Codon.values()) {
         p  = frequency.get(c) / 100.;
         if (p != 0) {
            h -= p * Math.log(p);
         }
      }
      return h / Math.log(2);
   }
   
   /** Returns a string representing this gene.
    *  @return a string representing this gene
    */
   @Override
   public String toString() {
      StringBuilder string = new StringBuilder();
      byte code;
      for (int i = 0; i < sequence.length; i++) {
         for (int j = 0; j < 9; j++) {
            code = (byte) ((sequence[i] & BITMASK[j]) >> 7*j);
            if (code == 0) break; // there is no codon anymore ...
            else string.append(Codon.fromBitmaskCode(code).toString());
         }
      }
      return string.toString();
   }
  
   /** Factory method producing a list of genes from a specified DNA base sequence 
    *  of the letters A, C, G, T.
    *  @param string DNA base sequence
    *  @return a list of genes decoded by the specified string
    */
   public static ArrayList<ORF> decode(CharSequence string) {
      ArrayList<ORF> genes = new ArrayList<>();
      int offset = 0, max = string.length() - 3;
      boolean geneSwitchedOn = false;
      String triplet;
      ArrayList<Codon> sequence;
      
      do {
         // Find start codon ATG:
         while (offset <= max && !string.subSequence(offset, offset + 3).toString().equals("ATG")) {
            offset++;
         }
         if (offset <= max) geneSwitchedOn = true; // gene sequence found
         
         sequence = new ArrayList<Codon>();
         // Read the triplets:
         while (offset <= max && geneSwitchedOn) {
            // !! What if triplet == "ATG" ??
            triplet = string.subSequence(offset, offset + 3).toString();
            sequence.add(Codon.fromString(triplet));
            if (triplet.equals("TAA") || triplet.equals("TAG") || triplet.equals("TGA")) {
               genes.add(new ORF(sequence));
               geneSwitchedOn = false;
            } else {
               offset += 3;
            }
         }
      } while (offset <= max);
      
      if(sequence != null && sequence.size() > 0) genes.add(new ORF(sequence)); // no stop codon!
      
      if (offset != string.length()) {
         System.out.println(" ORF incomplete, offset=" + offset + " != " + string.length()); // so what?
         //if(sequence != null && sequence.size() > 0) genes.add(new ORF(sequence)); // ???
      }
      return genes;
   }
   
   /** Returns a genome after opening a file in FASTA format, determined by a file chooser dialog.
    *  The method uses the flag completeGenome which specifies whether the file represents a complete 
    *  genome or a single gene. In a complete genmome, the start codons and stop codons have
    *  to be recognized to identify the different genes. (???)
    *  @param completeGenome flag specifying whether the file represents a complete genome
    *  @return a genome determined by the FASTA file, or null if no file is selected
    */
   //@SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static ArrayList<ORF> loadFASTA(boolean completeGenome) {
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
      ArrayList<ORF> genes = new ArrayList<>();
      
      boolean comment;       // necessary since a comment line may be split up by buffer size!
      
      StringBuilder nucleobases;
      
      int returnVal = fileChooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         file = fileChooser.getSelectedFile();
         System.out.println("Opened file " + file);
         nucleobases = new StringBuilder();
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
                  if (buffer[i] == '>' || comment) { // comment line in FASTA
                     if (!completeGenome && i > 0) { // there has just been read a gene
                        genes.add(new ORF(name, nucleobases));
                        nucleobases = new StringBuilder();
                     }
                     comment = true;
                     name  = "";
                     i++;
                     while(i < buffer.length && buffer[i] != '\n') {
                        name += buffer[i];
                        i++;
                     }
                     if (i < buffer.length) {
                        comment = false;
                     }
                  } else if (buffer[i] == '\n') {
                     continue;
                  } else if (buffer[i] == '\u0000') { // eof reached
                     break;  // end for-loop
                  } else {
                     //System.out.println("i="+i+", char = " + ((int) buffer[i]) + ", " + buffer[i] + " = \u0010?");
                     nucleobases.append(buffer[i]);
                  }
               }
            }
            //System.out.println("### erzeuge Genom ...");
            if (completeGenome) genes = decode(nucleobases);
            else genes.add(new ORF(name, nucleobases));
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
      return genes;
   }
   
   /** Saves this genome in GENE format.
    *  @param orfs a list of ORF's
    */
   public static void save(ArrayList<ORF> orfs) {
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }
      FileNameExtensionFilter filter = new FileNameExtensionFilter("ORF file (*.orf)", "orf");
      fileChooser.addChoosableFileFilter(filter);
      
      int returnVal = fileChooser.showSaveDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         File file = fileChooser.getSelectedFile();
         // open the file:
         ObjectOutputStream output = null; 
         try {
            output = new ObjectOutputStream(new FileOutputStream(file)); 
            try {
               output.writeObject(orfs);
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
   @SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static ArrayList<ORF> loadORFs() {
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }
      FileNameExtensionFilter filter = new FileNameExtensionFilter("ORF file (*.orf)", "orf");
      fileChooser.addChoosableFileFilter(filter);
      
      ArrayList<ORF> orfs = null;
      
      int returnVal = fileChooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         File file = fileChooser.getSelectedFile();
         // open the file:
         ObjectInputStream input = null;
         try {
            input = new ObjectInputStream(new FileInputStream(file));
            orfs = (ArrayList<ORF>) input.readObject();
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
      return orfs;
   }
   
   /*
   public static void main(String... args) {
      // Frequency [in %] of codons in the gene of Escherichia Coli K-12. 
      // Data according to Merkl, Waack: Bioinformatik Interaktiv. Wiley, Weinheim 2009, S.8.
      java.util.TreeMap<Codon, Double> p = new java.util.TreeMap<>();
      p.put(TTT,2.08);p.put(TCT,0.89);p.put(TAT,1.53);p.put(TGT,0.49);
      p.put(TTC,1.78);p.put(TCC,0.90);p.put(TAC,1.30);p.put(TGC,0.65);
      p.put(TTA,1.22);p.put(TCA,0.64);p.put(TAA,0.19);p.put(TGA,0.09);
      p.put(TTG,1.28);p.put(TCG,0.86);p.put(TAG,0.02);p.put(TGG,1.48);
      
      p.put(CTT,1.00);p.put(CCT,0.65);p.put(CAT,1.23);p.put(CGT,2.29);
      p.put(CTC,1.06);p.put(CCC,0.47);p.put(CAC,1.04);p.put(CGC,2.30);
      p.put(CTA,0.35);p.put(CCA,0.81);p.put(CAA,1.43);p.put(CGA,0.32);
      p.put(CTG,5.56);p.put(CCG,2.47);p.put(CAG,2.93);p.put(CGG,0.49);
      
      p.put(ATT,2.91);p.put(ACT,0.91);p.put(AAT,1.58);p.put(AGT,0.76);
      p.put(ATC,2.64);p.put(ACC,2.42);p.put(AAC,2.28);p.put(AGC,1.59);
      p.put(ATA,0.36);p.put(ACA,0.59);p.put(AAA,3.47);p.put(AGA,0.16);
      p.put(ATG,2.80);p.put(ACG,1.37);p.put(AAG,1.07);p.put(AGG,0.11);
      
      p.put(GTT,1.88);p.put(GCT,1.57);p.put(GAT,3.18);p.put(GGT,2.60);
      p.put(GTC,1.49);p.put(GCC,2.51);p.put(GAC,2.05);p.put(GGC,3.07);
      p.put(GTA,1.11);p.put(GCA,1.98);p.put(GAA,4.12);p.put(GGA,0.67);
      p.put(GTG,2.66);p.put(GCG,3.49);p.put(GAG,1.80);p.put(GGG,1.02);
      
      String out;
      
      ArrayList<ORF> genes = new ArrayList<>();
      
      genes = loadFASTA(true);
      if (genes.size() > 0) {
         out = "<html>";
         out += getFrequencyAsHTMLTable(genes);
         out += "<br>Entropy:" + entropy(genes);
         out += " (" + genes.size() + " genes)";         
         javax.swing.JOptionPane.showMessageDialog(null, out, "Codon Frequencies", -1);
         
         out = "<html>";
         out += getFrequencyAsHTMLGraph(genes);
         out += " (" + genes.size() + " genes)";
         
         javax.swing.JOptionPane.showMessageDialog(null, out, "Codon Frequencies", -1);
         
         if (genes.size() < 10) {
            out = "";
            for(ORF g: genes) {
               out += g + "\n";
            }
            System.out.println(out);
         }
         //ORF.save(genes);
      }
      
      /*
//      genes = loadORFs();
//      if (genes != null && genes.size() > 0) {
//         out = "<html>";
//         out += getFrequencyAsHTMLTable(genes);
//         out += "<br>Entropy:" + entropy(genes);
//         out += " (" + genes.size() + " genes)";         
//         javax.swing.JOptionPane.showMessageDialog(null, out, "Codon Frequencies", -1);
//         
//         out = "<html>";
//         out += getFrequencyAsHTMLGraph(genes);
//         javax.swing.JOptionPane.showMessageDialog(null, out, "Codon Frequencies", -1);
//      }
   }
   // */
}

