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

package org.pelea.languages.pddl.structures.nodes;

import org.jdom.Element;

/**
 *
 * @author moises
 */
public class Value
{
    private String _value;
    private String _type;
    
    public Value(String value)
    {
        this._value = value.trim();
        this._type  = null;
    }
    
    public Value(String value, String type)
    {
        this._value = value.trim();
        this._type  = type;
    }
    
    public Value(Element code) {
        this._value = code.getAttributeValue("value").trim();
        this._type = code.getAttributeValue("type").trim();
    }
    
    public String getValue()
    {
        return this._value;
    }
    
    public void setName(String value)
    {
        this._value = value.trim();
    }
    
    public String getType()
    {
        return this._type;
    }
    
    public void setType(String type)
    {
        this._type = type.trim();
    }
    
    public String getXml()
    {
        if (this._type == null)
            return "<term name=\"" + this._value + "\"/>";
        else
            return "<term name=\"" + this._value + "\" type=\"" + this._type + "\"/>";
    }
    
    public Value clone() {
        return new Value(this._value, this._type);
    }
    
    public boolean equal(Value value) {    
        return this.getValue().toUpperCase().matches(value.getValue().toUpperCase());
    }
}
