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

import static org.mathIT.genes.AminoAcid.*;

/** This enum provides the 4<sup>3</sup> = 64 DNA codons made of the alphabet
 *  T, C, A, G; associated to each cocon is the amino acid to which it is
 *  transformed by the genetic code during the process of genetic expression.
 *  
 *  The genetic code is adapted from Mortimer, Müller: <i>Chemie.</i> 
 *  Thieme, Stuttgart 2007, S.627.
 *  <pre>
   TTT (Phe), TCT (Ser), TAT (Tyr), TGT (Cys),
   TTC (Phe), TCC (Ser), TAC (Tyr), TGC (Cys),
   TTA (Leu), TCA (Ser), TAA (Stop),TGA (Sec),
   TTG (Leu), TCG (Ser), TAG (Pyl), TGG (Trp),

   CTT (Leu), CCT (Pro), CAT (His), CGT (Arg),
   CTC (Leu), CCC (Pro), CAC (His), CGC (Arg),
   CTA (Leu), CCA (Pro), CAA (Gln), CGA (Arg),
   CTG (Leu), CCG (Pro), CAG (Gln), CGG (Arg),

   ATT (Ile), ACT (Thr), AAT (Asn), AGT (Ser),
   ATC (Ile), ACC (Thr), AAC (Asn), AGC (Ser),
   ATA (Ile), ACA (Thr), AAA (Lys), AGA (Arg),
   ATG (Met), ACG (Thr), AAG (Lys), AGG (Arg),

   GTT (Val), GCT (Ala), GAT (Asp), GGT (Gly),
   GTC (Val), GCC (Ala), GAC (Asp), GGC (Gly),
   GTA (Val), GCA (Ala), GAA (Glu), GGA (Gly),
   GTG (Val, 61), GCG (Ala, 62), GAG (Glu, 63), GGG (Gly, 64),
   NNN (null, 65);
 *  </pre>
 * @author Andreas de Vries
 * @version 1.1
 */
public enum Codon {
   /** Genetic code according to Mortimer, Müller: Chemie. Thieme, Stuttgart 2007, S.627.*/
   TTT (Phe,  1), TCT (Ser,  2), TAT (Tyr,  3), TGT (Cys,  4),
   TTC (Phe,  5), TCC (Ser,  6), TAC (Tyr,  7), TGC (Cys,  8),
   TTA (Leu,  9), TCA (Ser, 10), TAA (Stop,11), TGA (Sec, 12),
   TTG (Leu, 13), TCG (Ser, 14), TAG (Pyl, 15), TGG (Trp, 16),

   CTT (Leu, 17), CCT (Pro, 18), CAT (His, 19), CGT (Arg, 20),
   CTC (Leu, 21), CCC (Pro, 22), CAC (His, 23), CGC (Arg, 24),
   CTA (Leu, 25), CCA (Pro, 26), CAA (Gln, 27), CGA (Arg, 28),
   CTG (Leu, 29), CCG (Pro, 30), CAG (Gln, 31), CGG (Arg, 32),

   ATT (Ile, 33), ACT (Thr, 34), AAT (Asn, 35), AGT (Ser, 36),
   ATC (Ile, 37), ACC (Thr, 38), AAC (Asn, 39), AGC (Ser, 40),
   ATA (Ile, 41), ACA (Thr, 42), AAA (Lys, 43), AGA (Arg, 44),
   ATG (Met, 45), ACG (Thr, 46), AAG (Lys, 47), AGG (Arg, 48),

   GTT (Val, 49), GCT (Ala, 50), GAT (Asp, 51), GGT (Gly, 52),
   GTC (Val, 53), GCC (Ala, 54), GAC (Asp, 55), GGC (Gly, 56),
   GTA (Val, 57), GCA (Ala, 58), GAA (Glu, 59), GGA (Gly, 60),
   GTG (Val, 61), GCG (Ala, 62), GAG (Glu, 63), GGG (Gly, 64),
   NNN (null, 65);
   
   /** Code number for bitmask coding. */
   private byte bitmaskCode;
   
   /** Amino acid which is related to this codon by the genetic code. */
   private AminoAcid aminoAcid;
   
   /** Constructor of this codon. */
   private Codon(AminoAcid aminoAcid, int bitmaskCode) {
      this.bitmaskCode = (byte) bitmaskCode;
      this.aminoAcid   = aminoAcid;
   }
   
   /** Returns the bitmask code referring to this codon.
    *  It is (arbitrarily chosen to be) as follows.
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
      
      65 &rarr; NNN (unknown codon)
    *  </pre>
    *  @return the bitmask code of this codon
    */
   public byte getBitmaskCode() {
      return bitmaskCode;
   }
   
   /** Returns the amino acid which is related to this codon by the genetic code.
    *  Note that the stop codon TAA returns "Stop" instead of an amino acid.
    *  @return the amino acid coded by this codon
    */
   public AminoAcid getAminoAcid() {
      return aminoAcid;
   }
   
   /** Returns the amino acid which is related to this codon by the genetic code.
    *  Note that the stop codon TAA returns "Stop" instead of an amino acid.
    *  @return the amino acid coded by this codon
    */
   public AminoAcid encode() {
      return aminoAcid;
   }
   
   /** Returns the codon which is related to the specified amino acid by the genetic code.
    *  If the amino acid cannot be decoded the unknown Codon NNN is returned.
    *  @param aminoAcid an amino acid
    *  @return the amino acid coded by this codon
    */
   public static Codon decode(AminoAcid aminoAcid) {
      for (Codon codon : values()) {
         if (aminoAcid.equals(codon.aminoAcid)) {
            return codon;
         }
      }
      //throw new IllegalArgumentException("Unknown aminoAcid " + aminoAcid);
      return NNN;
   }
   
   /** Returns the DNA codon which is represented by the specified bitmask code,
    *  an integer <i>x</i> satisfying 0 &le; <i>x</i> &lt; 64.
    *  It is (arbitrarily) chosen to be as follows.
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
      
      65 &rarr; NNN (unknown codon)
    *  </pre>
    *  @param bitmaskCode the bitmask code representing a codon.
    *  @return the codon represented by the specified bitmask code
    *  @throws IllegalArgumentException if the string is not a codon triplet
    */
   public static Codon fromBitmaskCode(byte bitmaskCode) {
      for (Codon codon : values()) {
         if (bitmaskCode == codon.bitmaskCode) {
            return codon;
         }
      }
      throw new IllegalArgumentException(
         bitmaskCode + " is not a bitmask code representing a DNA codon triplet"
      );
   }
   
   /** Returns the DNA codon which is represented by the specified three 
    *  nucleobases, a triplet, consisting of the DNA letters T, C, A, G.
    *  @param x the first of the three-letter string representing a codon.
    *  @param y the second of the three-letter string representing a codon.
    *  @param z the third of the three-letter string representing a codon.
    *  @return the codon represented by the specified triplet
    *  @throws IllegalArgumentException if the string is not a codon triplet
    */
   public static Codon fromTriplet(char x, char y, char z) {
      String triplet = "" + x + y + z;
      for (Codon codon : values()) {
         if (triplet.equals(codon.toString())) {
            return codon;
         }
      }
      throw new IllegalArgumentException(triplet + " is not a DNA codon triplet");
   }
   
   /** Returns the DNA codon which is represented by the specified three-letter 
    *  string, a triplet, consisting of the DNA letters T, C, A, G.
    *  @param  triplet three-letter string representing a codon.
    *  @return the codon represented by the specified triplet
    *  @throws IllegalArgumentException if the string is not a codon triplet
    */
   public static Codon fromString(CharSequence triplet) {
      if (triplet.length() != 3) {
         throw new IllegalArgumentException(triplet + " is not a triplet");
      }
      for (Codon codon : values()) {
         if (triplet.toString().equals(codon.toString())) {
            return codon;
         }
      }
      //throw new IllegalArgumentException(triplet + " is not a codon triplet");
      return NNN;
   }
   
   public static void main(String... args) {
      //String out = "";
      
      //javax.swing.JOptionPane.showMessageDialog(null, out);
      
      StringBuilder string = new StringBuilder("AGA");
      System.out.println(string + " = " + Codon.fromString(string));
      AminoAcid acid = Trp;
      System.out.println(acid + " <- " + Codon.decode(acid));
      
      // /*
      int count = 0;
      for(Codon c: values()) {
         if (count %  4 == 0) {
            System.out.println();
         }
         if (count % 16 == 0) {
            System.out.println();
         }
         System.out.print("" + c.getBitmaskCode() +" &rarr; " + c + ", ");
         count++;
      }
      // */
   }
}
