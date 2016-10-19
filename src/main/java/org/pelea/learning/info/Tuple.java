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

package org.pelea.learning.info;

import org.pelea.languages.pddl.structures.NodeList;
import org.pelea.languages.pddl.structures.NodeTree;

/**
 *
 * @author moises
 */
public class Tuple
{
    private NodeList state;
    private NodeTree goals;
    private String action;
    
    public Tuple(NodeList state, NodeTree goals, String action)
    {
        this.state = state;
        this.goals = goals;
        this.action = action;
    }
    
    public NodeList getState()
    {
        return this.state;
    }
    
    public void setState(NodeList state)
    {
        this.state = state;
    }
    
    public NodeTree getGoals()
    {
        return this.goals;
    }
    
    public void setGoals(NodeTree goals)
    {
        this.goals = goals;
    }
    
    public String getAction(){
        return this.action;
    }
    
    public void setAction(String action){
        this.action = action;
    }
}