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

/**
 *
 * @author moises
 */
public class ActionList 
{
    private List<Action> _actions;

    public ActionList()
    {
        this._actions = new ArrayList<Action>();
    }

    public int getSize()
    {
        return this._actions.size();
    }
    
    public void addAction(Action node)
    {
        this._actions.add(this._actions.size(), node);
    }
    
    public Action find(String name)
    {
        for (int i = 0; i < this._actions.size(); i++) {
            if (this._actions.get(i).getName().toUpperCase().equals(name.toUpperCase()))
                return this._actions.get(i);
        }
        
        return null;
    }
}