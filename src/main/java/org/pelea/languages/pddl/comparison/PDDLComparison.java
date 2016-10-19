/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3m.es
 * 
 * Copyright 2012, Moisés Martínez
 *
 * (Questions/bug reports now to be sent to Moisés Martínez)
 *
 * This file is part of Pelea.
 * 
 * Pelea is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Pelea is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Pelea.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ************************************************************************/

package org.pelea.languages.pddl.comparison;

import org.pelea.languages.pddl.structures.NodeList;
import org.pelea.languages.pddl.structures.NodeTree;

/**
 *
 * @author moises
 */
public abstract class PDDLComparison 
{
    public static int TOTAL = 1;
    public static int PARTIAL = 2;
    public static int EFFECTS = 3;
    
    private int type;
    
    public void setType(int type) {
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    public abstract boolean compare(NodeList genPredicates, NodeList obsPredicates);
    public abstract boolean compare(NodeList genPredicates, NodeTree genGoals, NodeList obsPredicates, NodeTree obsGoals);
}
