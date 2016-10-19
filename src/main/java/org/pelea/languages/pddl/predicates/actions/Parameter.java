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

import org.pelea.languages.pddl.types.Type;

/**
 *
 * @author moises
 */
public class Parameter 
{
    private String _name;
    private String _type;
    
    public Parameter(String name, String type)
    {
        this._name = name.trim();
        this._type = type.trim();
    }
    
    public String getName()
    {
        return this._name;
    }
    
    public void setName(String name)
    {
        this._name = name.trim();
    }
    
    public String getType()
    {
        return this._type;
    }
    
    public void setType(String type)
    {
        this._type = type.trim();
    }
}
