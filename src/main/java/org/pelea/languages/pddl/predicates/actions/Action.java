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

package org.pelea.languages.pddl.predicates.actions;

import java.util.ArrayList;
import java.util.List;
import org.pelea.languages.pddl.structures.NodeList;
import org.pelea.languages.pddl.structures.NodeTree;
import org.pelea.languages.pddl.structures.nodes.Node;
import org.pelea.languages.pddl.structures.nodes.Predicate;

/**
 *
 * @author moises
 */
public class Action 
{
    public static int NODURATIVE = 0;
    public static int DURATIVE = 1;
    
    private final String _name;
    private final int _type;
    private final List<Parameter> _parameters;
    private NodeTree _preconditions;
    private NodeTree _effects;
    
    public Action(String name, String type) {
        this._name = name.trim();        
        this._type = (type.toUpperCase().contains("NO-DURATIVE")) ? Action.NODURATIVE:Action.DURATIVE;
        this._parameters = new ArrayList<Parameter>();
    }
    
    public void addParameter(String name, String type) {
        this._parameters.add(this._parameters.size(), new Parameter(name, type));
    }
    
    public void addPreconditions(NodeTree node)
    {
        this._preconditions = node;
    }
    
    public void addEffects(NodeTree node)
    {
        this._effects = node;
    }
    
    public String getName()
    {
        return this._name;
    }
    
    public int getType()
    {
        return this._type;
    }
    
    public void generateParameters(String[][] values)
    {
        for (int i = 0; i < this._parameters.size(); i++) {
            values[i][0] = this._parameters.get(i).getName();
        }
    }
    
    public List<ActionResult> generateEffects(String[][] parameters, int mode, NodeList state)
    {
        List<ActionResult> predicates = new ArrayList<ActionResult>();
        this._effects.maching(parameters, predicates, mode, state);
        
        return predicates;
    }
    
    public List<ActionResult> generatePreconditions(String[][] parameters, NodeList state)
    {
        List<ActionResult> predicates = new ArrayList<ActionResult>();
        
        if (state.size() > 0)
            this._preconditions.maching(parameters, predicates, 3, state);
        
        return predicates;
    }
}
