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

/** This enum provides the 4<sup>3</sup> = 64 mRNA codons made of the alphabet
 *  U, C, A, G; associated to each cocon is the amino acid to which it is
 *  transformed by the genetic code during the process of genetic expression.
 *  
 *  The genetic code is adapted from Mortimer, Müller: <i>Chemie.</i> 
 *  Thieme, Stuttgart 2007, S.627.
 *  <pre>
   UUU (Phe), UCU (Ser), UAU (Tyr), UGU (Cys),
   UUC (Phe), UCC (Ser), UAC (Tyr), UGC (Cys),
   UUA (Leu), UCA (Ser), UAA (Stop),UGA (Sec),
   UUG (Leu), UCG (Ser), UAG (Pyl), UGG (Trp),

   CUU (Leu), CCU (Pro), CAU (His), CGU (Arg),
   CUC (Leu), CCC (Pro), CAC (His), CGC (Arg),
   CUA (Leu), CCA (Pro), CAA (Gln), CGA (Arg),
   CUG (Leu), CCG (Pro), CAG (Gln), CGG (Arg),

   AUU (Ile), ACU (Thr), AAU (Asn), AGU (Ser),
   AUC (Ile), ACC (Thr), AAC (Asn), AGC (Ser),
   AUA (Ile), ACA (Thr), AAA (Lys), AGA (Arg),
   AUG (Met), ACG (Thr), AAG (Lys), AGG (Arg),

   GUU (Val), GCU (Ala), GAU (Asp), GGU (Gly),
   GUC (Val), GCC (Ala), GAC (Asp), GGC (Gly),
   GUA (Val), GCA (Ala), GAA (Glu), GGA (Gly),
   GUG (Val), GCG (Ala), GAG (Glu), GGG (Gly);
 *  </pre>
 *  @author Andreas de Vries
 *  @version 1.0
 */
public enum RNACodon {
   /** Genetic code according to Mortimer, Müller: Chemie. Thieme, Stuttgart 2007, S.627.*/
   UUU (Phe), UCU (Ser), UAU (Tyr), UGU (Cys),
   UUC (Phe), UCC (Ser), UAC (Tyr), UGC (Cys),
   UUA (Leu), UCA (Ser), UAA (Stop),UGA (Sec),
   UUG (Leu), UCG (Ser), UAG (Pyl), UGG (Trp),

   CUU (Leu), CCU (Pro), CAU (His), CGU (Arg),
   CUC (Leu), CCC (Pro), CAC (His), CGC (Arg),
   CUA (Leu), CCA (Pro), CAA (Gln), CGA (Arg),
   CUG (Leu), CCG (Pro), CAG (Gln), CGG (Arg),

   AUU (Ile), ACU (Thr), AAU (Asn), AGU (Ser),
   AUC (Ile), ACC (Thr), AAC (Asn), AGC (Ser),
   AUA (Ile), ACA (Thr), AAA (Lys), AGA (Arg),
   AUG (Met), ACG (Thr), AAG (Lys), AGG (Arg),

   GUU (Val), GCU (Ala), GAU (Asp), GGU (Gly),
   GUC (Val), GCC (Ala), GAC (Asp), GGC (Gly),
   GUA (Val), GCA (Ala), GAA (Glu), GGA (Gly),
   GUG (Val), GCG (Ala), GAG (Glu), GGG (Gly);
   
   /** Amino acid which is related to this codon by the genetic code. */
   private AminoAcid aminoAcid;
   
   /** Constructor of this codon. */
   private RNACodon(AminoAcid aminoAcid) {
      this.aminoAcid = aminoAcid;
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
   
   /** Returns the amino acid which is related to this codon by the genetic code.
    *  Note that the stop codon TAA returns "Stop" instead of an amino acid.
    *  @param aminoAcid amino acid
    *  @return the amino acid coded by this codon
    */
   public static RNACodon decode(AminoAcid aminoAcid) {
      for (RNACodon codon : values()) {
         if (aminoAcid.equals(codon.aminoAcid)) {
            return codon;
         }
      }
      throw new IllegalArgumentException("Unknown aminoAcid " + aminoAcid);
   }
   
   /** Returns the DNA codon which is represented by the specified three-letter 
    *  string, a triplet, consisting of the DNA letters T, C, A, G.
    *  @param  triplet three-letter string representing a codon.
    *  @return the codon represented by the specified triplet
    *  @throws IllegalArgumentException if the string is not a codon triplet
    */
   public static RNACodon fromString(String triplet) {
      if (triplet.length() != 3) {
         throw new IllegalArgumentException(triplet + " is not a triplet");
      }
      for (RNACodon codon : values()) {
         if (triplet.equals(codon.toString())) {
            return codon;
         }
      }
      throw new IllegalArgumentException(triplet + " is not a codon triplet");
   }
   
   /*
   public static void main(String... args) {      
      String string = "UGA";
      System.out.println(string + " = " + RNACodon.fromString(string));
      AminoAcid acid = Trp;
      System.out.println(acid + " = " + RNACodon.decode(acid));
   }
   // */
}
