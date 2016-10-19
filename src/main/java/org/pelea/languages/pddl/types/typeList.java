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

package org.pelea.languages.pddl.types;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author moises
 */
public class typeList
{
    private  List<Type> _types;

    public typeList()
    {
        this._types = new ArrayList<Type>();
    }

    public int getSize()
    {
        return this._types.size();
    }

    public void addType(Type item)
    {
        this._types.add(item);
    }

    public int getNumberType(String type)
    {
        int count = 0;

        for (int i = 0; i < this._types.size(); i++)
        {
            if (this._types.get(i).getType().matches(type))
                count++;
        }

        return count;
    }

    public Type getTypeByName(String id)
    {
        int position = 0;

        while (position < this._types.size())
        {
            if (this._types.get(position).getName().matches(id))
                return this._types.get(position);
            position++;
        }

        return null;
    }

    public Type getTypeByType(String type)
    {
        int position = 0;

        while (position < this._types.size())
        {
            if (this._types.get(position).getType().matches(type))
                return this._types.get(position);
            position++;
        }

        return null;

    }
    
    public String generateXml()
    {
        String code = "";
        
        code += "<objects>";
        
        for (int i = 0; i < this._types.size(); i++)
        {
            code += this._types.get(i).generateXml();
        }
        
        code += "</objects>";
        
        return code;
    }
}
