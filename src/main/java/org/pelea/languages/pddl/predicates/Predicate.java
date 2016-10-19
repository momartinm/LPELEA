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

package org.pelea.languages.pddl.predicates;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author moises
 */
public class Predicate
{
    private String _name;
    private List<String> _values;

    public Predicate()
    {
        this._values = new ArrayList<String>();
    }

    public Predicate(String name)
    {
        this._name   = name;
        this._values = new ArrayList<String>();
    }

    public void addValue(String values)
    {
        this._values.add(values);
    }

    public void addName(String name)
    {
        this._name = name;
    }

    public String getName()
    {
    return this._name;
    }

    public boolean haveValue(String value)
    {
        int contador = 0;

        while (contador < this._values.size())
        {
            if (this._values.get(contador).matches(value))
                return true;

            contador++;
        }

        return false;
    }
    
    public String getXML()
    {
        String code = "";
        
        code += "<atom predicate=\"" + this._name +"\">";
	
        for (int i = 0; i < this._values.size(); i++)
            code += "<term name=\"" + this._values.get(i) + "\"/>"; 
	
        code += "</atom>"; 
        
        return code;
    }
}