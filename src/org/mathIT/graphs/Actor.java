/*
 * Actor.java - Class representing an actor of a social network
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

/**
 * This class represents actors as individual nodes of a {@link SocialNetwork social network}.
 * In usual diffusion models for the spread of an idea or innovation through a
 * social network,
 * each individual actor is either active (an adopter of the innovation) or inactive.
 * An inactive actor is activated if the influence of its incident neighbors,
 * each given by corresponding weighted edges, in sum outvalue its threshold.
 * For details see
 * <a href="http://www.cs.cornell.edu/home/kleinber/kdd03-inf.pdf">
 * D. Kempe, J. Kleinberg &amp; É. Tardos:
 * ‘Maximizing the Spread of Influence through a Social Network’,
 * <i>Proc. 9th ACM SIGKDD Intl. Conf. on Knowledge Discovery and Data Mining.</i>
 * 2003.
 * </a>
 * @author Andreas de Vries
 * @version 0.2beta
 */
public class Actor extends Vertex<Actor> implements Activatable {
   /** Version ID for serialization. */
   //private static final long serialVersionUID = 9223372036791682602L; // = Long.MAX_VALUE - "Actor".hashCode()
   /** Flag whether this actor is active. */
   protected boolean active;

   /** The threshold of this actor. */
   protected double threshold;

   /** Creates an actor with the specified index which also specifies its name.
    *  The activation flag and the threshold of this actor are set to their default
    *  values <code>false</code> and 0.0, respectively.
    *  @param index the index of this actor in a social network. The index must be
    *  unique in a given network, and all indices should in total occupy the
    *  range {0, 1, ..., <i>n</i>–1} where <i>n</i> denotes the number of actors
    *  in the network.
    */
   public Actor(int index) {
      super(index);
      this.active = false;
      this.threshold = .0;
   }

   /**
    *  Creates an actor with the specified index and threshold, where the index
    *  also specifies its name.
    *  In the linear threshold model the sum of incident neighbors' weights
    *  have to outvalue this threshold so that this actor becomes active.
    *  The activation flag of this actor is set to its default value <code>false</code>.
    *  @param index the index of this actor in a social network. The index must be
    *  unique in a given network, and all indices should in total occupy the
    *  range {0, 1, ..., <i>n</i>–1} where <i>n</i> denotes the number of actors
    *  in the network.
    *  @param threshold the threshold of this actor
    */
   public Actor(int index, double threshold) {
      super(index);
      this.active = false;
      this.threshold = threshold;
   }

   /** Creates an actor with the specified name.*/
   /*
   public Actor(String name) {
      super(name);
      this.active = false;
      this.threshold = .0;
   }
   */

   /** Creates an actor with the specified index and name.
    *  The activation flag and the threshold of this actor are set to their default
    *  values <code>false</code> and 0.0, respectively.
    *  @param index the index of this actor in a social network. The index must be
    *  unique in a given network, and all indices should in total occupy the
    *  range {0, 1, ..., <i>n</i>–1} where <i>n</i> denotes the number of actors
    *  in the network.
    *  @param name the name of this actor
    */
   public Actor(int index, String name) {
      super(index,name);
      this.active = false;
      this.threshold = .0;
   }

   /** Creates an actor with the specified name, index, adjacency list, and threshold.
    *  The activation flag of this actor is set to its default value <code>false</code>.
    *  @param index the index of this actor
    *  @param name the name of this actor
    *  @param threshold the threshold of this actor
    */
   public Actor(int index, String name, double threshold) {
      super(index, name);
      this.threshold = threshold;
      this.active = false;
   }

   /** Creates an actor with the specified name, index, and adjacency list.
    *  The activation flag and the threshold of this actor are set to their default
    *  values <code>false</code> and 0.0, respectively.
    *  @param index the index of this actor
    *  @param name the name of this actor
    *  @param adjacency the adjacency of this actor
    */
   public Actor(int index, String name, Actor[] adjacency) {
      super(index, name, adjacency);
      this.threshold = .0;
      this.active = false;
   }

   /** Creates an actor with the specified name, index, adjacency list, and threshold.
    *  The activation flag of this actor is set to its default value <code>false</code>.
    *  @param index the index of this actor
    *  @param name the name of this actor
    *  @param adjacency the adjacency of this actor
    *  @param threshold the threshold of this actor
    */
   public Actor(int index, String name, Actor[] adjacency, double threshold) {
      super(index, name, adjacency);
      this.threshold = threshold;
      this.active = false;
   }

   /** Creates and returns a copy of this actor.
    *  For any actor x, the expression <code>x.copy() != x</code> is true,.
    *  @return a copy of this actor
    */
   @Override
   public Actor copy() {
      return new Actor(index, name, adjacency, threshold);
   }

   /** Returns whether this actor is active.
    *  @return whether this actor is active
    */
   @Override
   public boolean isActive() {
      return active;
   }

   /** Sets the flag whether this actor is active.
    *  @param active flag whether this actor is active
    */
   @Override
   public void setActive(boolean active) {
      this.active = active;
   }

   /** Returns the threshold of this actor.
    *  In the linear threshold model the sum of incident neighbors' weights
    *  have to outvalue this threshold so that this actor becomes active.
    *  @return the threshold of this actor
    */
   @Override
   public double getThreshold() {
      return threshold;
   }

   /** Sets the threshold of this actor.
    *  In the linear threshold model the sum of incident neighbors' weights
    *  have to outvalue this threshold so that this actor becomes active.
    *  @param threshold the threshold of this actor
    */
   @Override
   public void setThreshold(double threshold) {
      this.threshold = threshold;
   }

   @Override
   public String toString() {
      String output = name; // + "( -> ";
      /*
      int i = 0;
      for(i = 0; i < adjacency.length - 1; i++) {
         output += adjacency[i].getName() + ",";
      }
      if (adjacency.length > 0) {
         output += adjacency[i].getName();
      }
      output += ")";
      // */
      return output;
   }
}
