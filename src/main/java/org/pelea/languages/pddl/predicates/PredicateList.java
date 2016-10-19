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
public class PredicateList 
{
    private List<Predicate> _predicates;

    public PredicateList()
    {
        this._predicates = new ArrayList<Predicate>();
    }

    public int getSize()
    {
        return this._predicates.size();
    }

    public void addPredicate(Predicate item)
    {
        this._predicates.add(item);
    }

    public List<Predicate> getpredicatesByName(String name)
    {
        if (!name.isEmpty())
        {
            List<Predicate> items = new ArrayList<Predicate>();

            for (int i = 0; i < this.getSize(); i++)
            {
                if (this._predicates.get(i).getName().matches(name))
                    items.add(this._predicates.get(i));
            }

            return items;
        }

        return null;
    }

    public List<Predicate> getpredicatesByValue(String value)
    {
        if (!value.isEmpty())
        {
            List<Predicate> items = new ArrayList<Predicate>();

            for (int i = 0; i < this.getSize(); i++)
            {
                if (this._predicates.get(i).getName().matches(value))
                    items.add(this._predicates.get(i));
            }

            return items;
        }

        return null;
    }
    
    public List<Predicate> get_predicates(String name, List<String> values)
    {
        return null;
    }
    
    public String geteXml()
    {
        String code = "";
        
        code += "<init>";
        
        for (int i = 0; i < this._predicates.size(); i++)
        {
            code += this._predicates.get(i).getXML();
        }
        
        code += "</init>";
        
        return code;
    }
}
