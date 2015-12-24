/*
 * SocialNetwork.java - Class representing a social network
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
 */
package org.mathIT.graphs;

import java.util.HashSet;

/**
 * <p style="text-align:justify;">
 * This class represents a social network as a {@link Graph directed graph}
 * of individual nodes represented by {@link Actor actors}.
 * In usual diffusion models such as the “Linear Threshold model”
 * for the spread of an idea, an innovation, or a disease through a social network,
 * each individual actor is either <i>active</i> (e.g., an adopter of the innovation)
 * or <i>inactive</i>.
 * An inactive actor is activated if the influence of its incident neighbors,
 * each given by corresponding weighted edges, in sum outvalue its threshold.
 * Usually, the thresholds are chosen at random according to some specified distribution
 * function.
 * Given an initial set of active actors (with all other actors inactive),
 * the diffusion process unfolds deterministically in discrete steps:
 * in step <i>t</i>, all actors that were active in step <i>t</i>–1 remain active,
 * and any actor <i>i</i> is activated for which the total sum of the normalized weights
 * <i>w<sub>ji</sub></i> of its active neighbors
 * is at least its threshold &theta;<sub><i>i</i></sub>:
 * </p>
 * <table summary="" style="margin:auto; width:100pt">
 *   <tr>
 *     <td>
 *     <table summary="" border="0" style="width:40pt">
 *       <tr><td style="font-size:smaller; text-align:center;">&nbsp;</td></tr>
 *       <tr><td style="font-size:x-large; text-align:center;">&sum;</td></tr>
 *       <tr><td style="font-size:smaller; text-align:center;"><i>j</i> active</td></tr>
 *     </table>
 *     </td>
 *     <td>
 *       <i>w<sub>ji</sub></i>
 *     </td>
 *     <td>
 *       &nbsp; &ge; &nbsp;
 *     </td>
 *     <td>
 *       &theta;<sub><i>i</i></sub>
 *     </td>
 *   </tr>
 * </table>
 * <p style="text-align:justify;">
 * The threshold &theta;<sub><i>i</i></sub> therefore intuitively represents the
 * different latent tendencies of actors to get become active (e.g., to adopt the
 * innovation or to be infected by a disease) when their contacts do.
 * The process stops if no more activations are possible.
 * </p>
 * <p style="text-align:justify;">
 * Based on physical theories on interacting particle systems, the “Independent Cascade Model“
 * also starts with an initial set of active actors, but the process now unfolds
 * in discrete steps according to the randomized rule that an actor <i>i</i> first becomes
 * active in step <i>t</i> it has only a single chance to activate each currently
 * inactive neighbor <i>j</i> and succeeds with probability <i>w<sub>ij</sub></i>,
 * independently of the history so far. In other words, actor <i>j</i> is activated
 * is activated by <i>i</i> in step <i>t</i>+1 with probability <i>w<sub>ij</sub></i>.
 * Again, the process runs until no more activations are possible.
 * </p>
 * <p style="text-align:justify;">
 * The <i>influence maximization problem</i> asks, for a parameter <i>k</i>,
 * to find a set <i>A</i><sub>0</sub> of <i>k</i> initially active actors
 * which has maximum influence. For both the linear threshold model and the
 * independant cascade model, the influence maximization problem is NP-hard.
 * </p>
 * <p style="text-align:justify;">
 * For details and further references, see
 * <a href="http://www.cs.cornell.edu/home/kleinber/kdd03-inf.pdf">
 * D. Kempe, J. Kleinberg &amp; E. Tardos:
 * ’Maximizing the Spread of Influence through a Social Network‘,
 * <i>Proc. 9th ACM SIGKDD Intl. Conf. on Knowledge Discovery and Data Mining.</i>
 * 2003.
 * </a>
 * </p>
 * @version 0.2beta
 * @author Andreas de Vries
 */
public class SocialNetwork extends WeightedGraph<Actor> implements NetworkOfActivatables<Actor> {
   /** Version ID for serialization. */
   //private static final long serialVersionUID = 9223372035729046046L; // = Long.MAX_VALUE - "SocialNetwork".hashCode()

   /** Flag which shows if there has been an active actor during the history of this network.
    *  In particular, if it is false, no actor is active.
    */
   protected boolean active;
   
   /** Creates a social network of the specified actors and their directed and weighted 
    *  relations determined by the specified weight matrix.
    *  The indices of the actors must be
    *  unique and must in total occupy the entire
    *  range {0, 1, ..., <i>n</i>–1}, where <i>n</i> denotes the number of actors
    *  in the network.
    *  @param actors an array of the actors forming this social network
    *  @param weights the weight matrix determining the relations between the actors
    */
   public SocialNetwork(Actor[] actors, double[][] weights) {
      super(actors, weights);
      this.active = false;
   }

   /** Creates a social network of the specified actors and their
    *  relations determined by the specified flag whether they are symmetric
    *  and by the specified weight matrix.
    *  The indices of the actors must be
    *  unique and must in total occupy the entire
    *  range {0, 1, ..., <i>n</i>–1}, where <i>n</i> denotes the number of actors
    *  in the network.
    *  @param undirected flag whether this social network is undirected
    *  @param actors an array of the actors forming this social network
    *  @param weights the weight matrix determining the relations between the actors
    */
   public SocialNetwork(boolean undirected, Actor[] actors, double[][] weights) {
      super(undirected, actors, weights);
      this.active = false;
   }

   /** Creates a social network of the specified actors and their
    *  relations determined by the specified flag whether they are symmetric
    *  and by the specified weight matrix.
    *  The indices of the actors must be
    *  unique and must in total occupy the entire
    *  range {0, 1, ..., <i>n</i>–1}, where <i>n</i> denotes the number of actors
    *  in the network.
    *  @param undirected flag whether this social network is undirected
    *  @param actors an array of the actors forming this social network
    *  @param weights the weight matrix determining the relations between the actors
    *  @param active flag whether this social network has had an active actor during its history
    */
   private SocialNetwork(boolean undirected, Actor[] actors, double[][] weights, boolean active) {
      super(undirected, actors, weights);
      this.active = active;
   }

   /**
    * Returns at least one of the nodes has been active during the history
    * of this network.
    * @return true if and only if a node of this network has been active
    */
   @Override
   public boolean isActive() {
      return active;
   }
   
   /**
    * Sets this network as active, i.e., it has had at least one of its nodes 
    * been active during the history of this network.
    * @param active true if and only if a node of this network has been active
    */
   @Override
   public void setActive(boolean active) {
      this.active = active;
   }
   
   /**
    * Activates all actives specified by the input actors, i.e.,
    * marks each of them as active.
    * This method is a convenience routine to simplify activation, for instance 
    * of innovators.
    * Note that this method changes the state of the input actors (call by reference),
    * no matter whether the returned set is stored by the invoking process or not.
    * @param actors a set actors of this network to be activated
    * @return the set actors each of whom is activated
    */
   public HashSet<Actor> activate(Actor... actors) {
      for (Actor a : actors) {
         a.setActive(true);
      }
      
      if (actors.length > 0) this.active = true;
      
      HashSet<Actor> activated = new HashSet<>();
      java.util.Collections.addAll(activated, actors);
      return activated;
   }
   
   /**
    * Activates all actives specified by the input set of actors, i.e.,
    * marks each of them as active.
    * This method is a convenience routine to simplify activation, for instance 
    * of innovators.
    * Note that this method changes the state of the input actors (call by reference),
    * no matter whether the returned set is stored by the invoking process or not.
    * @param actors a set actors of this network to be activated
    * @return the set actors each of whom is activated
    */
   public HashSet<Actor> activate(java.util.Collection<Actor> actors) {
      for (Actor a : actors) {
         a.setActive(true);
      }
      if (actors.size() > 0) this.active = true;
      return new HashSet<>(actors);
   }
   
   /**
    * Deactivates all actors specified by the input actors, i.e.,
    * marks each of them as inactive.
    * This method is a convenience routine to simplify deactivation.
    * Note that this method changes the state of the input actors (call by reference),
    * no matter whether the returned set is stored by the invoking process or not.
    * @param actors a set actors of this network to be activated
    * @return the set actors each of whom is activated
    */
   public HashSet<Actor> deactivate(Actor... actors) {
      for (Actor a : actors) {
         a.setActive(false);
      }
      HashSet<Actor> deactivated = new HashSet<>();
      java.util.Collections.addAll(deactivated, actors);
      return deactivated;
   }
   
   /**
    * Deactivates all actors specified by the input set of actors, i.e.,
    * marks each of them as inactive.
    * This method is a convenience routine to simplify deactivation.
    * Note that this method changes the state of the input actors (call by reference),
    * no matter whether the returned set is stored by the invoking process or not.
    * @param actors a set actors of this network to be activated
    * @return the set actors each of whom is activated
    */
   public HashSet<Actor> deactivate(java.util.Collection<Actor> actors) {
      for (Actor a : actors) {
         a.setActive(false);
      }
      return new HashSet<>(actors);
   }
   
   /**
    * Returns the actors finally activated by the specified active initiators
    * in this social network, according to the linear threshold model.
    * @param initiators an array of activated actors of this network.
    * This methods marks each of them as active.
    */
   @Override
   public HashSet<Actor> runActivation(HashSet<Actor> initiators) {
      HashSet<Actor> activated = new HashSet<>(initiators);
      
      // local variables:
      int delta;
      
      do {
         delta = activated.size();
         activated = nextActivationStep(activated);
         delta = activated.size() - delta;
      } while (delta > 0);
      
      return activated;
   }
   
   /**
    * Returns the actors activated by the specified active generation of actors
    * in this social network, after a single activation step according to the 
    * linear threshold model.
    * @param activeGeneration a list of activated actors of this network.
    * This methods marks each of them as active.
    * @return all actors newly activated by the input active generation, plus the
    * input generation.
    */
   @Override
   public HashSet<Actor> nextActivationStep(HashSet<Actor> activeGeneration) {
      for (Actor x : activeGeneration) {
         x.setActive(true);
      }
      
      // local variables:
      HashSet<Actor> activated = new HashSet<>(activeGeneration);
      int i, j; double input, total;
      Actor[] parents = new Actor[this.vertices.length];
      
      i = 0;
      for (Actor x : vertices) {
         parents[i] = new Actor(x.getIndex(), x.getName(), x.getAdjacency(), x.getThreshold());
         parents[i].setActive(x.isActive());
         i++;
      }
      
      for (i = 0; i < vertices.length; i++) {
         if (vertices[i].isActive()) continue;
         total = 0;
         input = 0;
         for (j = 0; j < weight.length; j++) {
            if (weight[j][i] == INFINITY || weight[j][i] == 0) {
               continue;
            }
            total += weight[j][i];
            if (parents[j].isActive()) input += weight[j][i];
         }
         if (input/total >= vertices[i].getThreshold()) {
            vertices[i].setActive(true);
            activated.add(vertices[i]);
         }
      }
      return activated;
   }
   
   /** Creates a social network from a CSV file selected by a file chooser dialog.
    *  In particular, the adjacency list of each vertex is derived from the weight matrix.
    *  If two vertices <code>vertices[i]</code> and <code>vertices[j]</code>
    *  do not have an edge connecting them, the respective weight matrix entry
    *  <code>weight[i][j]</code> is expected to have the value 
    *  {@link Double#POSITIVE_INFINITY}, <code>0</code>, or an empty string.
    *  The vertices of the returned graph are of the raw type {@link Actor}.
    *  @return the social network specified by the CSV file
    */
   public static SocialNetwork createNetworkFromCSVFile() {
      final String separator = Character.toString(SEPARATOR);
      StringBuilder text = org.mathIT.util.Files.loadTextFile();
      
      if (text == null) return null;

      // Determine whether the graph is undirected:
      int pos = text.indexOf(separator), pre, i, j;
      if (pos < 0) {
         throw new IllegalArgumentException("No valid CSV format!");
      }
      boolean undirected = "undirected".equals(text.substring(0, pos).trim());
      
      // Determine the names and the number of vertices:
      pre = pos + 1;
      pos = text.indexOf("\n", pre);
      String[] names = text.substring(pre, pos).split(separator);
      Actor[] vertices = new Actor[names.length];
      for (i = 0; i < names.length; i++) {
         vertices[i] = new Actor(i, names[i]);
      }
      
      // Determine the thresholds of the vertices:
      if ("threshold".equals(text.substring(pos+1, text.indexOf(separator, pos+1)))) {
         pre = text.indexOf(separator, pos+1) + 1;
         pos = text.indexOf("\n", pre);
         String[] threshold = text.substring(pre, pos).split(separator);
         for (i = 0; i < names.length; i++) {
            if (threshold[i].equals("")) threshold[i] = "0";
            vertices[i].setThreshold(Double.parseDouble(threshold[i].replace(',', '.')));
         }
      }
      
      // Determine active stati of the vertices:
      boolean active = false;
      if ("active".equals(text.substring(pos+1, text.indexOf(separator, pos+1)))) {
         pre = text.indexOf(separator, pos+1) + 1;
         pos = text.indexOf("\n", pre);
         String[] activated = text.substring(pre, pos).split(separator);
         for (i = 0; i < activated.length; i++) {
            if (activated[i].equals("1")) {
               vertices[i].setActive(true);
            }
         }
         active = true;
      }
      
      // Determine weight matrix:
      double[][] weight = new double[vertices.length][vertices.length];
      i = 0;
      pre = text.indexOf(separator, pos) + 1; // first row contains vertex name
      pos = text.indexOf("\n", pre);
      String[] number;
      while (pos > 0 && pre > 0) {
         number = text.substring(pre, pos).split(separator, -1);
         for (j = 0; j < weight.length; j++) {
            if (number[j].equals("")) number[j] = "0";
            weight[i][j] = Double.parseDouble(number[j].replace(',', '.'));
         }
         i++;
         pre = text.indexOf(separator, pos) + 1; // first row contains vertex name
         pos = text.indexOf("\n", pre);
      }      
      return new SocialNetwork(undirected,vertices,weight, active);
   }
   
   public static void main(String[] args) {
      //double inf = WeightedGraph.INFINITY;
      boolean binary = false;  // whether vertex number should be shown in binary format
      boolean undirected = true;
      //int s;
      
      /* Haus-vom-Nikolaus example: ---
      undirected = false;
      double[][] w = {
         //1  2  3  4  5
         { 0, 1, 1, 0, 0}, //  1
         { 0, 0, 1, 1, 0}, //  2
         { 0, 0, 0, 1, 0}, //  3
         { 1, 0, 0, 0, 1}, //  4
         { 0, 1, 0, 0, 0}, //  5
      };
      // */

      // /* Easley-Kleinberg example: ---
      undirected = true;
      double[][] w = {
         //0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16
         { 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //  0
         { 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //  1
         { 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //  2
         { 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, //  3
         { 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0}, //  4
         { 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0}, //  5
         { 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0}, //  6
         { 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0}, //  7
         { 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //  8
         { 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0}, //  9
         { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0}, // 10
         { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0}, // 11
         { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1}, // 12
         { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1}, // 13
         { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0}, // 14
         { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1}, // 15
         { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0}, // 16
      };
      // */

      /* Permutation matrix: ---
      undirected = false;
      double[][] w = {
         //0  1  2  3  4 
         { 0, 0, 1, 0, 0}, //  0
         { 0, 1, 0, 0, 0}, //  1
         { 0, 0, 0, 0, 1}, //  2
         { 1, 0, 0, 0, 0}, //  3
         { 0, 0, 0, 1, 0}, //  4
      };
      // */
      
      /* Permutation matrix 2: ---
      undirected = false;
      double[][] w = {
         //0  1  2  3  4 
         { 0, 1, 0, 0, 0}, //  0
         { 0, 0, 1, 0, 0}, //  1
         { 0, 0, 0, 1, 0}, //  2
         { 0, 0, 0, 0, 1}, //  3
         { 1, 0, 0, 0, 0}, //  4
      };
      // */
      
      /* Permutation matrix 2: ---
      undirected = false;
      double[][] w = {
         //0  1  2  3  4  5
         { 0, 1, 1, 1, 1, 1}, //  0
         { 0, 0, 1, 1, 1, 1}, //  1
         { 0, 0, 0, 1, 1, 1}, //  2
         { 0, 0, 0, 0, 1, 1}, //  3
         { 0, 0, 0, 0, 0, 1}, //  4
         { 0, 0, 0, 0, 0, 0}, //  5
      };
      // */
      
      /* 3 cycles: ---
      undirected = false;
      double[][] w = {
         //1  2  3  4  5  6  7  8  9 10 11 12
         { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //  1
         { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //  2
         { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, //  3
         { 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0}, //  4
         { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //  5
         { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, //  6
         { 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0}, //  7
         { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0}, //  8
         { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}, //  9
         { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, // 10
         { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 11
         { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, // 12
      };
      // */
      
      /* shear mapping: ---
      undirected = false;
      double[][] w = {
         //1  2
         { 1, 1}, //  1
         { 0, 1}, //  2
      };
      // */
      
      /* Adder gate: ---
      undirected = false;
      double[][] w = {
         //1  2  3  4  5  6  7  8
         { 1, 0, 0, 0, 0, 0, 0, 0}, //  1
         { 0, 1, 0, 0, 0, 0, 0, 0}, //  2
         { 0, 0, 1, 0, 0, 0, 0, 0}, //  3
         { 0, 0, 0, 1, 0, 0, 0, 0}, //  4
         { 0, 0, 0, 0, 0, 0, 1, 0}, //  5
         { 0, 0, 0, 0, 0, 0, 0, 1}, //  6
         { 0, 0, 0, 0, 0, 1, 0, 0}, //  7
         { 0, 0, 0, 0, 1, 0, 0, 0}, //  8
      };
      binary = true;
      // */
      
      /* 2-bit adder gate: ---
      undirected = false;
      double[][] w = {
         //1  2  3  4
         { 1, 0, 0, 0}, //  1
         { 0, 1, 0, 0}, //  2
         { 0, 1, 0, 0}, //  3
         { 0, 0, 1, 0}, //  4
      };
      binary = true;
      // */
      
      /* Easley-Kleinberg toy web: ---
      undirected = false;
      double[][] w = {
         //1  2  3  4
         { 0, 1, 0, 1}, //  1
         { 0, 0, 1, 1}, //  2
         { 1, 0, 0, 0}, //  3
         { 0, 0, 1, 0}, //  4
      };
      // */
      
      /* Easley-Kleinberg toy web variation: ---
      undirected = false;
      double[][] w = {
         //1  2  3  4
         { 0, 1, 0, 1}, //  1
         { 0, 0, 1, 1}, //  2
         { 1, 0, 0, 0}, //  3
         { 0, 0, 0, 0}, //  4
      };
      // */
      
      /* Krumke-Noltemeier 3.4: ---
      undirected = false;
      double[][] w = {
         //1  2  3  4  5  6  7
         { 0, 1, 0, 0, 0, 0, 0}, //  1
         { 0, 0, 1, 1, 0, 0, 0}, //  2
         { 0, 0, 0, 0, 0, 0, 0}, //  3
         { 0, 0, 0, 0, 1, 0, 0}, //  4
         { 0, 1, 0, 1, 0, 0, 0}, //  5
         { 0, 0, 0, 0, 0, 0, 0}, //  6
         { 0, 0, 0, 0, 0, 1, 0}, //  7
      };
      // */
      
      /* OR gate: ---
      double[][] w = {
         //1  2  3  4  5  6  7  8
         { 0, 1, 0, 0, 0, 0, 0, 0}, //  1
         { 1, 0, 0, 0, 0, 0, 0, 0}, //  2
         { 0, 0, 1, 0, 0, 0, 0, 0}, //  3
         { 0, 0, 0, 1, 0, 0, 0, 0}, //  4
         { 0, 0, 0, 0, 1, 0, 0, 0}, //  5
         { 0, 0, 0, 0, 0, 1, 0, 0}, //  6
         { 0, 0, 0, 0, 0, 0, 1, 0}, //  7
         { 0, 0, 0, 0, 0, 0, 0, 1}, //  8
      };
      binary = true;
      // */
      
      /* cOR gate: ---
      undirected = false;
      double[][] w = {
         //1  2  3  4  5  6  7  8
         { 0, 1, 0, 0, 0, 0, 0, 0}, //  1
         { 0, 0, 0, 0, 0, 0, 1, 0}, //  2
         { 0, 0, 0, 0, 1, 0, 0, 0}, //  3
         { 0, 0, 0, 1, 0, 0, 0, 0}, //  4
         { 0, 0, 1, 0, 0, 0, 0, 0}, //  5
         { 0, 0, 0, 0, 0, 1, 0, 0}, //  6
         { 1, 0, 0, 0, 0, 0, 0, 0}, //  7
         { 0, 0, 0, 0, 0, 0, 0, 1}, //  8
      };
      binary = true;
      // */
      
      /* Brandes et al 2008, Fig. 1a: ---
      double[][] w = {
         //1  2  3  4  5  6
         { 0, 1, 1, 0, 0, 0}, //  1
         { 1, 0, 1, 0, 1, 0}, //  2
         { 1, 1, 0, 1, 0, 0}, //  3
         { 0, 0, 1, 0, 0, 0}, //  4
         { 0, 1, 0, 0, 0, 1}, //  5
         { 0, 0, 0, 0, 1, 0}, //  6
      };
      // maximum modularity: C_0=[{0, 1, 2}, {3}, {4}, {5}], Q=0.013888888888888895
      // */
      Actor[] x = new Actor[w.length];
      for (int i = 0; i < x.length; i++) {
         String name = "";
         if (binary) {
            int jMax = Integer.numberOfLeadingZeros(i) - Integer.numberOfLeadingZeros(x.length) - 1;
            //System.out.println("i=" + i + ", jMax=" + jMax);
            for (int j = 0; j < jMax; j++) {
               name += "0";
            }
            if (i != 0) name += Integer.toBinaryString(i);
         } else {
            //name += i;
            name += (i+1);
         }
         //if (!binary && i < 10) name = " " + name;
         x[i] = new Actor(i, name, .5);
      }
      
      SocialNetwork graph = new SocialNetwork(undirected,x,w);
      
      //graph.activate(x[6], x[9]);
      
      graph.visualize();
      
      /*
      s = 1;
      int ai = 1;
      System.out.println("x["+s+"]="+x[s]);
      System.out.println("x["+s+"].getAdjacency()["+ai+"]="+x[s].getAdjacency()[ai]);
      */
      //System.out.println(ausgabe);
      //graph.saveAsCSV();
      
      //graph = SocialNetwork.createNetworkFromCSVFile();
      //graph.visualize();      
   }
}
