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
import org.pelea.languages.pddl.UtilPDDL;
import org.pelea.languages.pddl.structures.NodeList;

/**
 *
 * @author moises
 */
public class Connector extends Node
{
    private String _name;
    
    public Connector(String name)
    {
        this._type = UtilPDDL.getType(name, "GD"); //TODO SOLVE THIS
        this._name = name;
    }
    
    public Connector(Element code)
    {                
        this._type = UtilPDDL.getType(code.getAttributeValue("type"), code.getName());
        this._name = code.getAttributeValue("type").toUpperCase();
    }
    
    @Override
    public String getName()
    {
        return this._name;
    }
    
    public void setName(String name)
    {
        this._name = name;
    }
    
    @Override
    public String getXml()
    {
        return "";
    }
    
    public String getHeadXml()
    {
        return "<gd type=\"" + this.getName() + "\">";
    }
    
    public String getFootXml()
    {
        return "</gd>";
    }

    @Override
    public Node maching(String[][] params, int mode, NodeList state) 
    {
        return null;
    }
    
    @Override
    public Node clone() 
    {
        return null;
    }

    @Override
    public Boolean equal(Node node) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
