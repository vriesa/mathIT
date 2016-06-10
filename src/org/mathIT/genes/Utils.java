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
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.swing.JFileChooser;

/** This class provides static utility methods for the gene library package.
 *  @author Andreas de Vries
 *  @version 1.0
 */
public class Utils {
   /** The name of the property file. This file stores configuration data
    *  for the gene package programs, for example the directory which has 
    *  been opened recently for loading or saving files.
    */
   static final String propertyFile = 
     System.getProperty("user.home") + System.getProperty("file.separator") + "genes.xml";
   
   /** The current file name.*/
   static String currentFileName;
   
   /** There should be no instance of this class. */
   private Utils() {}
   
   /** Returns the specified map as a CSV data table string, with data aligned in columns.
    *  The data separator is the tabulator sign '\t'.
    *  @param map the map to be saved
    *  @return CSV data table string
    */
   public static String getAsCSVTable(java.util.Map<String, Integer> map) {
      int limit = 500;  // maximum number of rows
      if (map.size() < limit) {
         limit = map.size();
      }
      int i;
      StringBuilder out = new StringBuilder();
      StringBuilder[] x = new StringBuilder[limit];
      for (i = 0; i < x.length; i++) {
         x[i] = new StringBuilder();
      }
      i = 0;
      for (String c : map.keySet()) {
         //x[i % limit].append(c + "\t" + map.get(c) + "\t");
         x[i % limit].append(c).append("\t").append(map.get(c)).append("\t");
         //x[i % limit].append(c + "\t");
         i++;
      }
      for (i = 0; i < x.length; i++) {
         out.append(x[i] + "\n");
      }
      return "\n" + out.toString();
   }
   
   /** Returns the specified map as a CSV data table string, with data aligned in columns.
    *  The data separator is the tabulator sign '\t'.
    *  @param map the map to be saved
    *  @param minimum the minimum frequency number to be saved
    *  @return CSV data table string
    */
   public static String getAsCSVTable(java.util.Map<String, Integer> map, int minimum) {
      TreeMap<String, Integer> m = new TreeMap<>();
      for (String w : map.keySet()) {
         if (map.get(w) >= minimum) {
            m.put(w, map.get(w));
         }
      }
      return getAsCSVTable(m);
   }
  
   /** Returns the specified map as a CSV data table string, with data aligned in rows.
    *  The data separator is the tabulator sign '\t'.
    *  @param map a map of data
    *  @return CSV data table string
    */
   protected static String getAsCSVTableRows(java.util.Map<String, Integer> map) {
      String out1 = "\n", out2 = "\n";
      for (String c : map.keySet()) {
         out1 += c + "\t";
         out2 += map.get(c) + "\t";
      }
      return out1 + out2 + "\n";
   }
  
   /** Returns the frequency distribution of amino acid triplets as HTML graph.
    *  @param frequencies frequency distribution
    *  @return a graphical HTML string representing the frequency distribution 
    *  of the amino acid triplets
    */
   public static String getFrequenciesAsHTMLGraph(Map<String, Integer> frequencies) {
      int tableWidth = 100; // unit: px
      String out  = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
      int max  = 0;

      for (String string : frequencies.keySet()) {
         if (max < frequencies.get(string)) max = frequencies.get(string);
      }
      max++;
      
      for (String string : frequencies.keySet()) {
         if (frequencies.get(string) == 0) continue;
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
   
   /** Loads a FASTA file and returns the n-tuple frequencies of its letters.
    *  @param n the tuple size
    *  @return the frequency distribution of n-tuples over the base alphabet
    */
   //@SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static TreeMap<String, Integer> computeTupleFrequencies(int n) {
      return computeTupleFrequencies(n,null);
   }
   
   /** Asks the user to select a FASTA file, loads it and returns the n-tuple 
    *  frequencies of its letters.
    *  @param n the tuple size
    *  @param base the base alphabet (important for finding zero occurrences)
    *  @return the frequency distribution of n-tuples over the base alphabet
    */
   public static TreeMap<String, Integer> computeTupleFrequencies(int n, char[] base) {
      if (n <= 0) {
         throw new IllegalArgumentException("Parameter n must be positive: " + n);
      }
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
      
      int returnVal = fileChooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         file = fileChooser.getSelectedFile();
         return computeTupleFrequencies(file, n, base);
      }
      return null;
   }
   
   /** Asks the user to select a directory and returns a list of FASTA files.
    *  @return a list of FASTA files
    */
   public static File[] selectFASTAFiles() {
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }      
      fileChooser.addChoosableFileFilter(FastaFileFilter.create());
      fileChooser.setMultiSelectionEnabled(true);
      
      File[] files = null;
      
      int returnVal = fileChooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         files = fileChooser.getSelectedFiles();
      }
      return files;
   }
   
   /** Loads a FASTA file and returns the n-tuple frequencies of its letters.
    *  @param files an array of FASTA file
    *  @param n the tuple size
    *  @return the frequency distribution of n-tuples over the base alphabet
    */
   //@SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static TreeMap<String, Integer> computeTupleFrequencies(File[] files, int n) {
      if (n <= 0) {
         throw new IllegalArgumentException("Parameter n must be positive: " + n);
      }
      final int BUFFER_SIZE = 200*n; // in bytes
      
      TreeMap<String, Integer> frequencies = new TreeMap<>();
      
      for (File file : files) {
         System.out.println("Opened file " + file + " ...");
         
         char[] buffer;
         //String name = "";
         //boolean named = false;
         
         boolean comment; // comment is necessary since a comment line may be split up by buffer size!
         
         String tuple;
         int i, j;
         
         char[][] sequence = new char[n][n];
         int[] length = new int[n];
         for (j = 0; j < n; j++) length[j] = -j;
         
         FileReader input = null;
         try {
            comment = false;
            input = new FileReader(file);
            while (input.ready()) {
               buffer = new char[BUFFER_SIZE];
               input.read(buffer);
               //System.out.println("Read: " + java.util.Arrays.toString(buffer));
               for (i = 0; i < buffer.length; i++) {
                  if(buffer[i] == '>' || buffer[i] == ';' || comment) { // comment line in FASTA
                     comment = true;
                     i++;
                     while(i < buffer.length && buffer[i] != '\n') {
                        //if (!named) name += buffer[i];
                        i++;
                     }
                     if (i < buffer.length) {
                        comment = false;
                        //named = true;
                     }
                  } else if (buffer[i] == '\n') {
                     continue;
                  } else if (buffer[i] == '\u0000') { // eof reached
                     break;  // end for-loop
                  } else {
                     // Store nucleobase:
                     for (j = 0; j < n; j++) {
                        if (length[j] >= 0) sequence[j][length[j]] = buffer[i]; 
                        length[j]++;
                        if (length[j] == n) {
                           tuple = new String(sequence[j]);
                           if (frequencies.get(tuple) == null) {
                              frequencies.put(tuple, 1);
                           } else {
                              frequencies.put(tuple, frequencies.get(tuple) + 1);
                           }
                           length[j] = 0;
                        }
                     }
                  }
               }
            }
         } catch (IOException ioe) {
            ioe.printStackTrace();
         } finally {
            try {
               if (input != null)  input.close();
            } catch (IOException ioe) {
               ioe.printStackTrace();
            }
            currentFileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
            try {
               Properties props = new Properties();
               props.setProperty("currentDirectory", file.getParent());
               props.storeToXML(new FileOutputStream(propertyFile), null);
            } catch (Exception e) {
               System.err.println(e.getMessage());
            }
         }
      }
      return frequencies;
   }
   
   /** Loads a FASTA file and returns the n-tuple frequencies of its letters.
    *  @param file a FASTA file
    *  @param n the tuple size
    *  @param base the base alphabet (important for finding zero occurrences)
    *  @return the frequency distribution of n-tuples over the base alphabet
    */
   //@SuppressWarnings("unchecked")  // reading files cannot be guaranteed by no compiler at all!
   public static TreeMap<String, Integer> computeTupleFrequencies(File file, int n, char[] base) {
      if (n <= 0) {
         throw new IllegalArgumentException("Parameter n must be positive: " + n);
      }
      final int BUFFER_SIZE = 200*n; // in bytes
      
      System.out.println("Opened file " + file + " ...");
      
      char[] buffer;
      //String name = "";      
      //boolean named = false;
      boolean comment = false; // comment is necessary since a comment line may be split up by buffer size!
      TreeMap<String, Integer> frequencies = null;
      
      String tuple;
      int i, j;
      
      frequencies = new TreeMap<>();
      if (base != null) { // initialize frequency distribution .
         StringBuilder[] words = org.mathIT.numbers.Combinatorics.words(n, base);
         for (i = 0; i < org.mathIT.numbers.Numbers.pow(base.length, n); i++) {
            //frequencies.put("" + words[i], 0);
            frequencies.put(words[i].toString(), 0);
         }
      }
      
      char[][] sequence = new char[n][n];
      int[] length = new int[n];
      for (j = 0; j < n; j++) length[j] = -j;
      
      FileReader input = null;
      try {
         comment = false;
         input = new FileReader(file);
         while (input.ready()) {
            buffer = new char[BUFFER_SIZE];
            input.read(buffer);
            //System.out.println("Read: " + java.util.Arrays.toString(buffer));
            for (i = 0; i < buffer.length; i++) {
               if(buffer[i] == '>' || buffer[i] == ';' || comment) { // comment line in FASTA
                  comment = true;
                  i++;
                  while(i < buffer.length && buffer[i] != '\n') {
                     //if (!named) name += buffer[i];
                     i++;
                  }
                  if (i < buffer.length) {
                     comment = false;
                     //named = true;
                  }
               } else if (buffer[i] == '\n') {
                  continue;
               } else if (buffer[i] == '\u0000') { // eof reached
                  break;  // end for-loop
               } else {
                  // Store nucleobase:
                  for (j = 0; j < n; j++) {
                     if (length[j] >= 0) sequence[j][length[j]] = buffer[i]; 
                     length[j]++;
                     if (length[j] == n) {
                        tuple = new String(sequence[j]);
                        if (frequencies.get(tuple) == null) {
                           frequencies.put(tuple, 1);
                        } else {
                           frequencies.put(tuple, frequencies.get(tuple) + 1);
                        }
                        length[j] = 0;
                     }
                  }
               }
            }
         }
      } catch (IOException ioe) {
         ioe.printStackTrace();
      } finally {
         try {
            if (input != null)  input.close();
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
         currentFileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
         try {
            Properties props = new Properties();
            props.setProperty("currentDirectory", file.getParent());
            props.storeToXML(new FileOutputStream(propertyFile), null);
         } catch (Exception e) {
            System.err.println(e.getMessage());
         }
      }
      return frequencies;
   }
   
   /** Saves those occurrences of a frequency distribution which have the specified 
    *  frequency into a text file.
    *  @param fileName the file name (without extension) in which the text is stored
    *  @param distribution the frequency distribution
    *  @param frequency the frequency which filters the occurrences
    */
   public static void saveOccurrencesAsText(
     String fileName, TreeMap<String, Integer> distribution, int frequency
   ) {
      StringBuilder out = new StringBuilder(
        "Occurrences with frequency " + frequency + " (" + fileName + "):\n"
      );
      int count = 1;
      int linebreak = 2;
      for (String tuple : distribution.keySet()) {
         linebreak = 80/(tuple.length() + 2); break;
      }
      
      for (String tuple : distribution.keySet()) {
         if (distribution.get(tuple) == frequency) {
            out.append(tuple + ", ");
            if (count % linebreak == 0) out.append("\n");
            count++;
         }
      }
      save(fileName + ".txt", out);
   }
   
   /** Saves those occurrences of a frequency distribution which have the specified 
    *  frequency into a text file.
    *  @param fileName the file name (without extension) in which the text is stored
    *  @param distribution the frequency distribution
    *  @param frequency the frequency which filters the occurrences
    *  @param ignore list of characters to be ignored
    */
   public static void saveOccurrencesAsText(
      String fileName, TreeMap<String, Integer> distribution, int frequency, char[] ignore
   ) {
      StringBuilder out = new StringBuilder(
        "Occurrences with frequency " + frequency + " (" + fileName + "):\n"
      );        
      int i;
      int count = 1;
      boolean ignoreTuple;
      int linebreak = 2;
      for (String tuple : distribution.keySet()) {
         linebreak = 80/(tuple.length() + 2); break;
      }
      
      for (String tuple : distribution.keySet()) {
         if (distribution.get(tuple) == frequency) {
            ignoreTuple = false;
            for (i = 0; i < ignore.length; i++) {
               if (tuple.contains(""+ignore[i])) {
                  ignoreTuple = true; break;
               }
            }
            if (!ignoreTuple) {
               out.append(tuple + ", ");
               if (count % linebreak == 0) out.append("\n");
               count++;
            }
         }
      }
      save(fileName + ".txt", out);
   }
   
   /** This method asks the user to select a list of files, 
    *  computes the word frequencies and saves them in a CSV file.
    *  Note that the first line of each input file is ignored.
    *  Comma and space are condidered as separator symbols.
    */
   public static void saveWordFrequenciesAsCSV() {
      // --- 1) Let select a list of input files: ----
      JFileChooser fileChooser;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         fileChooser = new JFileChooser(new File(props.getProperty("currentDirectory")));
      } catch(Exception e) {
         fileChooser = new JFileChooser();
      }      
      fileChooser.setMultiSelectionEnabled(true);
      
      File[] files = null;
      
      int returnVal = fileChooser.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
         files = fileChooser.getSelectedFiles();
      }
      
      // --- 2) Read the file and count the words: -----------------------
      int BUFFER_SIZE = 160;  // number of buffer size
      char[] buffer;
      TreeMap<String, Integer> frequencies = new TreeMap<>();    
      FileReader input = null;
      StringBuilder word = new StringBuilder();
      String string, fileName = "", name = "Word Frequencies\nAnalyzed Files:\n";
      int i;
      //boolean named = false, firstLine;
      boolean firstLine;
      
      for (File file : files) {
         try {
            name += file.getName() + "\n";
            firstLine = true;
            input = new FileReader(file);
            while (input.ready()) {
               buffer = new char[BUFFER_SIZE];
               input.read(buffer);
               //System.out.println("Read: " + java.util.Arrays.toString(buffer));
               for (i = 0; i < buffer.length; i++) {
                  if(firstLine) { // ignore first line and use it a name
                     while(i < buffer.length && buffer[i] != '\n') {
                        //if (!named) name += buffer[i];
                        i++;
                     }
                     if (i < buffer.length) {
                        firstLine = false;
                        //named = true;
                     }
                  } else if (buffer[i] == '\n' || buffer[i] == ',') {
                     continue;
                  } else if (buffer[i] == '\u0000') { // eof reached
                     break;  // end for-loop
                  } else {
                     if (buffer[i] != ' ') {  // end of word yet reached?
                        word.append(buffer[i]);
                     } else {
                        string = word.toString();
                        word = new StringBuilder();
                        if (frequencies.get(string) == null) {
                           frequencies.put(string, 1);
                        } else {
                           frequencies.put(string, frequencies.get(string) + 1);
                        }
                     }
                  }
               }
            }
         } catch (IOException ioe) {
            ioe.printStackTrace();
         } finally {
            try {
               if (input != null)  input.close();
            } catch (IOException ioe) {
               ioe.printStackTrace();
            }
            //currentFileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
            try {
               Properties props = new Properties();
               props.setProperty("currentDirectory", file.getParent());
               props.storeToXML(new FileOutputStream(propertyFile), null);
            } catch (Exception e) {
               System.err.println(e.getMessage());
            }
         }
      }
      
      // --- 3) Save file: ----
      fileName = "WordFrequencies";
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
            //output.write(name + "\n"+getAsCSVTable(frequencies));  // all words
            output.write(name + "\n"+getAsCSVTable(frequencies, files.length));  // only words which are omitted throughout
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
   
   /** Saves the specified character sequence.
    *  @param fileName name of the file
    *  @param string a sequence of characters
    */
   public static void save(String fileName, CharSequence string) {
      File file;
      try {
         Properties props = new Properties();
         props.loadFromXML(new FileInputStream(propertyFile));
         file = new File(
            props.getProperty("currentDirectory") + 
            System.getProperty("file.separator") + fileName
         );
      } catch(Exception e) {
         file = new File(fileName);
      }
      
      // open the file:
      FileWriter output = null; 
      try {
         output = new FileWriter(file); 
         try {
            output.write(string.toString());
            output.flush();
            System.out.println(file + " saved!");
         } catch (EOFException eof) {
            // do nothing, the stream will be closed in the finally clause
         } catch (IOException ioe) {
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
   
   /*
   public static void main(String... args) {
      int f = 0; // frequency
      //int n_min = 2, n_max = 3, step = 1; // limits of tuple sizes, step width
      int n_min = 3, n_max = 9, step = 3; // limits of tuple sizes, and step width
      
      //char[] alphabet = Protein.VALUES, ignore = {' ', 'O', 'Z','X','*','-'};
      char[] alphabet = Genome.VALUES, ignore = {'U', 'N', '?'};      
      
      TreeMap<String, Integer> distribution;
      ArrayList<Character> b = new ArrayList<>();
      char[] base;
      
      int i;
      boolean use;
      for (int j = 0; j < alphabet.length; j++) {
         use = true;
         for (int k = 0; k < ignore.length && use; k++) {
            use &= (alphabet[j] != ignore[k]);
         }
         if (use) b.add(alphabet[j]);
      }
      
      base = new char[b.size()];
      
      for (i = 0; i < base.length; i++) {
         base[i] = b.get(i);
      }
      
      // --- used to aggergate word frequencies over many files: ---------
      // saveWordFrequenciesAsCSV();  System.exit(0);
      // -----------------------------------------------------------------

      File[] files = selectFASTAFiles();
      //File[] files = {
      //   new File("/home/devries/Downloads/Genomes/HomoSapiens/FASTA/hs_ref_GRCh37.p2_chrUn.fa"),
      //   new File("/home/devries/Downloads/Genomes/HomoSapiens/FASTA/hs_ref_GRCh37.p2_chrMT.fa")
      //};

      // compute and save word frequencies:
      String results = "";
      int top = 10;
      int limit = -1;
      String fileName = "";
      for (int n = n_min; n <= n_max; n += step) {
         if (fileName.equals("")) {
            fileName = files[0].getParentFile().getName();
            if (fileName.lastIndexOf('.') > 0) {
               fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            }
         }
         distribution = computeTupleFrequencies(files, n);
         // sort by frequencies:
         java.util.TreeSet<Integer> freqSet = new java.util.TreeSet<>(distribution.values());
         //ArrayList<Integer> freq = new ArrayList<Integer>();
         for (i=1; i <= top; i++) {  // select the top 10 ...
            if (!freqSet.isEmpty()) {
               limit = freqSet.pollLast();
               //freq.add(limit);
            }
         }
         
         if (limit == 0) limit = 3;
         
         TreeMap<String, Integer> distributionExtremal = new TreeMap<>();
         for (String word : distribution.keySet()) {
            if (distribution.get(word) >= limit) {
               distributionExtremal.put(word, distribution.get(word));
            }
         }
         
         results += getAsCSVTable(distributionExtremal) + "\n";
      }
      save(fileName + "_Top" + top + ".csv", results);
      System.exit(0);
      
      //System.out.println("File " + file + ": frequencies=" + distributionExtremal);
      
      for (File file : files) {
         results = "";
         limit = -1;
         for (int n = n_min; n <= n_max; n += step) {
            distribution = computeTupleFrequencies(file, n, base);
            // sort by frequencies:
            java.util.TreeSet<Integer> freqSet = new java.util.TreeSet<>(distribution.values());
            //ArrayList<Integer> freq = new ArrayList<Integer>();
            for (i=1; i <= top; i++) {  // select the top 10 ...
               if (!freqSet.isEmpty()) {
                  limit = freqSet.pollLast();
                  //freq.add(limit);
               }
            }
            
            if (limit == 0) limit = 3;
            
            TreeMap<String, Integer> distributionExtremal = new TreeMap<>();
            for (String word : distribution.keySet()) {
               if (distribution.get(word) >= limit) {
                  distributionExtremal.put(word, distribution.get(word));
               }
            }
            
            results += getAsCSVTable(distributionExtremal) + "\n";
         }
         fileName = file.getName();
         if (fileName.lastIndexOf('.') > 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
         }
         save(fileName + "_Top" + top + ".csv", results);
         
         //System.out.println("File " + file + ": frequencies=" + distributionExtremal);
      }
      
      System.exit(0);
      
      // compute and save word frequencies:
      for (File file : files) {
         //System.out.println(file + " selected");
         for (int n = n_min; n <= n_max; n += step) {
            distribution = computeTupleFrequencies(file, n, base);
            saveOccurrencesAsText(
               currentFileName + "_" + n, 
               distribution,
               f
            );
         }
      }      
   }
   // */
}
