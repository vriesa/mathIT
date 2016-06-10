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

/** This class provides a factory method to create a file name extension filter for files in
 *  <a href="http://en.wikipedia.org/wiki/FASTA_format" target="_top">FASTA format</a>.
 *  It can be used simply by a file chooser, for example:
 *  <pre>
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.addChoosableFileFilter(FastaFileFilter.create());
 *  </pre>
 *  @author Andreas de Vries
 *  @version 1.1
 */
public class FastaFileFilter {
   /** There shall be no instances of this class. */
   private FastaFileFilter() {}
   /**
    * Returns a file name extension filter for files in
    * <a href="http://en.wikipedia.org/wiki/FASTA_format" target="_top">FASTA format</a>.
    * @return a file name extension filter for FASTA files 
    */
   public static javax.swing.filechooser.FileNameExtensionFilter create() {
      return new javax.swing.filechooser.FileNameExtensionFilter(
         "FASTA file (*.fasta, *.fna, *.fa)", 
         "fasta", "fa", "seq", "fsa", // generic fasta: Any generic fasta file
         "fna",  // FASTA nucleic acid:  generically specifying nucleic acids
         "ffn",  // FASTA nucleotide coding regions: contains coding regions for a genome
         "faa",  // FASTA amino acid: Contains amino acids
         "mpfa", // multiple protein FASTA file
         "frn"   // FASTA non-coding RNA: Contains non-coding RNA regions for a genome, e.g., tRNA, rRNA
      );
   }    
}
