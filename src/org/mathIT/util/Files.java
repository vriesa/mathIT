/*
 * Files.java
 *
 * Copyright (C) 2013 Andreas de Vries
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
package org.mathIT.util;

//import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
//import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;

/**
 * This class offers static methods for saving and loading files, for instance 
 * of texts or CSV-formatted tables.
 * @author Andreas de Vries
 * @version 1.0
 */
public class Files {
   /** The name of this class, needed to find the current directory via the user preferences.*/
   private static final String clazz = "org.mathIT.util.Files";
   /** Attribute to access the current directory in the user's preferences.*/
   private static final String pref_currentDirectory = "pref_currentDirectory";
   
   private static String getCurrentDirectory() {
      String currentDirectory = null;
      try {
         currentDirectory = Preferences.userNodeForPackage(Class.forName(clazz))
                  .get(pref_currentDirectory, null);
      } catch (ClassNotFoundException cnf) {
         cnf.printStackTrace();
      }
      if (currentDirectory == null) currentDirectory = System.getProperty("user.home") + System.getProperty("file.separator");
      return currentDirectory;
   }
   
   /** Returns an object of class File to save a file
    *  with the specified path as default, optionally showing a file chooser dialog.
    *  @return null if the file name is null or empty, or if the file chooser dialog is canceled
    */
   private static File getFileToSave(String fileName, String currentDirectory, boolean showDialog) {
      if (fileName == null || fileName == "") return null;
      File file = null;
      
      if (showDialog) {
         JFileChooser fileChooser = new JFileChooser(currentDirectory);
         fileChooser.setSelectedFile(new File(fileName));
         int returnVal = fileChooser.showSaveDialog(null);
         if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fileChooser.getSelectedFile();
         // else: file  remains null
      } else {
         try {
            file = new File(
                    currentDirectory + System.getProperty("file.separator") + fileName);
         } catch (Exception e) {
            file = new File(fileName);
            e.printStackTrace();
         }
      }
      return file;
   }
   
   /** Returns an object of class File to load a file
    *  with the specified path as default, optionally showing a file chooser dialog.
    *  @return null if the file name is null or empty, or if the file chooser dialog is canceled
    */
   /*
	private static File getFileToLoad(String fileName, String currentDirectory, boolean showDialog) {
      if (fileName == null || fileName == "") return null;
      File file = null;
      
      if (showDialog) {
         JFileChooser fileChooser = new JFileChooser(currentDirectory);
         fileChooser.setSelectedFile(new File(fileName));
         int returnVal = fileChooser.showSaveDialog(null);
         if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fileChooser.getSelectedFile();
         // else: file  remains null
      } else {
         try {
            file = new File(
                    currentDirectory + System.getProperty("file.separator") + fileName);
         } catch (Exception e) {
            file = new File(fileName);
            e.printStackTrace();
         }
      }
      return file;
   }   
   // */
   
   /** Saves the specified text to a file. Here the text is a general
    * {@link java.lang.CharSequence character sequence}.
    * The file name specifies the name of the file, notably without the
    * directory. 
    * The directory is given by the directory last visited by 
    * the user by a method of this class, or its home directory by default.
    * This methods asks the user via a file chooser dialog where the file has to
    * be saved.
    * 
    * @param fileName the file name the file is to be saved to.
    * @param text the text to be saved
    * @see #save(java.lang.String, java.lang.CharSequence, boolean)
    */
   public static void save(String fileName, CharSequence text) {
      save(fileName, text, true);
   }
   
   /** Saves the specified text to a file. Here the text is a general
    * {@link java.lang.CharSequence character sequence}.
    * The file name specifies the name of the file, notably without the
    * directory. 
    * The directory is given by the directory last visited by 
    * the user by a method of this class, or its home directory by default.
    * If a file chooser dialog is to be shown to input the file from the user,
    * the flag <code>showDialog</code> must be set to <code>true</code>.
    * 
    * @param fileName the file name the file is to be saved to.
    * @param text the text to be saved
    * @param showDialog flag whether a FileChooser dialog is to be shown
    */
   public static void save(String fileName, CharSequence text, boolean showDialog) {
      File file = getFileToSave(fileName, getCurrentDirectory(), showDialog);
      
      if (file == null) return;
      
      // open the file:
      FileWriter output = null; 
      try {
         output = new FileWriter(file);
         try {
            output.write(text.toString());
            output.flush();
            Preferences.userNodeForPackage(Class.forName(clazz))
                    .put(pref_currentDirectory, file.getParent());
            System.out.println(file + " saved!");
         } catch (EOFException eof) {
            // do nothing, the stream will be closed in the finally clause
         } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
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
      }
   }

   /**
    * Returns the text from a file as an object of 
    * {@link StringBuilder StringBuilder}, the file being selected by a 
    * file chooser dialog.
    * @return the text read from the file
    */
   public static StringBuilder loadTextFile() {
      JFileChooser fileChooser = new JFileChooser(getCurrentDirectory());
      File file = null;

      int returnVal = fileChooser.showOpenDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         file = fileChooser.getSelectedFile();
      }

      if (file == null) return null;
      
      try {
         return loadText(file);         
      } catch (FileNotFoundException fnf) {
         System.err.println(fnf.getMessage());
         return null;
      }
   }

   /**
    * Returns the text from the specified file as an object of 
    * {@link StringBuilder StringBuilder}.
    * @param file File to be loaded
    * @return the text read from the file
    * @throws FileNotFoundException if the specified file is not found
    */
   public static StringBuilder loadText(File file) throws FileNotFoundException {
      final int BUFFER_SIZE = 1024; // in bytes
      char[] buffer;
      int i;
      StringBuilder text = new StringBuilder();
      try {
         FileReader input = new FileReader(file);
         Preferences.userNodeForPackage(Class.forName(clazz))
                 .put(pref_currentDirectory, file.getParent());
         //System.out.println("Opened file " + file + " ...");
         
         while (input.ready()) {
            buffer = new char[BUFFER_SIZE];
            input.read(buffer);
            //System.out.println("Read: " + java.util.Arrays.toString(buffer));
            for (i = 0; i < buffer.length; i++) {
               if (buffer[i] == '\u0000') { // eof reached
                  break;  // end for-loop
               } else {
                  text.append(buffer[i]);
               }
            }
         }
         input.close();
      } catch (ClassNotFoundException cnf) {
         System.err.println(cnf.getMessage());
      } catch (IOException ioe) {
         System.err.println(ioe.getMessage());
      }
      return text;
   }

   /* For test purposes ... */
   /*
   public static void main(String... args) {
      //save("test.txt", "ABC", false);
      StringBuilder text = loadTextFile();
      System.out.println("Read:\n" + text);
   }
   // */
}
