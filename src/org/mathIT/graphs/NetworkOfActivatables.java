/*
 * NetworkOfActivatables.java
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
 * This interface guarantees networks of activatable vertices, for instance
 * a {@link SocialNetwork social network} of activatable {@link Actor actors}.
 * Such networks are usable for simulations of activation processes such as
 * the spread of infections or of marketing trends in societies.
 * This interface must be implemented by networks that are to be
 * visualized by the {@link GraphViewer GraphViewer} class.
 * @author Andreas de Vries
 * @version 1.0
 * @param <V> the type of nodes of this network
 */
public interface NetworkOfActivatables<V extends Activatable> {
   /**
    * Returns at least one of the nodes has been active during the history
    * of this network.
    * @return true if and only if a node of this network has been active
    */
   public boolean isActive();
   
   /**
    * Sets this network as active, i.e., it has had at least one of its nodes 
    * been active during the history of this network.
    * @param active true if and only if a node of this network has been active
    */
   public void setActive(boolean active);
   
   /**
    * Returns the active nodes finally activated by the specified active initiators
    * in this network.
    * @param initiators an array of activated nodes of this network.
    * This methods marks each of them as active.
    * @return the set of all nodes activiated by the initiators
    */
   public HashSet<V> runActivation(HashSet<V> initiators);
   
   /**
    * Returns the nodes activated by the specified active generation of nodes
    * in this network, after a single activation step.
    * @param activeGeneration a list of activated nodes of this network.
    * This methods marks each of them as active.
    * @return all active nodes after this activation step, usually 
    * all nodes activated by the input active generation plus the input generation.
    */
   public HashSet<V> nextActivationStep(HashSet<V> activeGeneration);
}
