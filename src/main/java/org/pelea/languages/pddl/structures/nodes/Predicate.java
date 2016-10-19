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

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.pelea.languages.pddl.UtilPDDL;
import org.pelea.languages.pddl.structures.NodeList;

/**
 *
 * @author moises
 */
public class Predicate extends Node implements Cloneable
{
    private boolean predicate;
    private byte _mode;
    private String _name;
    private List<Value> _values;

    public Predicate()
    {
        this.predicate = true;
        this._mode   = STATIC;
        this._type   = UtilPDDL.NODE_PREDICATE;
        this._values = new ArrayList<Value>();
    }

    public Predicate(String name)
    {
        this.predicate = true;
        this._mode   = STATIC;
        this._type   = UtilPDDL.NODE_PREDICATE;
        this._name   = name.trim();
        this._values = new ArrayList<Value>();
    }

    public Predicate(String name, boolean predicate)
    {
        this.predicate = predicate;
        this._mode   = STATIC;
        this._type   = UtilPDDL.NODE_PREDICATE;
        this._name   = name.trim();
        this._values = new ArrayList<Value>();
    }    
    
    public Predicate(String name, byte mode)
    {
        this.predicate = true;
        this._mode   = mode;
        this._type   = UtilPDDL.NODE_PREDICATE;
        this._name   = name.trim();
        this._values = new ArrayList<Value>();
    }
    
    public Predicate(Element code)            
    {
        this.predicate = true;
        this._mode      = STATIC;
        this._type      = UtilPDDL.NODE_PREDICATE;
        
        if (code.getAttributeValue("predicate") != null)
            this._name = code.getAttributeValue("predicate").trim();
        else {
            this.predicate = false;
            this._name = code.getAttributeValue("name").trim();
        }
        
        this._values    = new ArrayList<Value>();
        
        List values = code.getChildren();
      
        for (int j = 0; j < values.size(); j++)
        {
            this._values.add(new Value(((Element) values.get(j)).getAttributeValue("name"), ((Element) values.get(j)).getAttributeValue("type")));
        }
    }
    
    public int values() {
        return this._values.size();
    }

    public void addValue(String value)
    {
        this._values.add(new Value(value));
    }
    
    public void addValue(String value, String type)
    {
        this._values.add(new Value(value, type));
    }
    
    public String getValue(int position) {
        return this._values.get(position).getValue();
    }
    
    public String[] getValues()
    {
        String[] auxiliar = new String[this._values.size()];
        
        for (int i = 0; i < this._values.size(); i++)
            auxiliar[i] = this._values.get(i).getValue();
        
        return auxiliar;
    }
    
    public void setValue(String name, int position) {
        this._values.get(position).setName(name);
    }

    public void addName(String name)
    {
        this._name = name;
    }
    
    public void changeMode(byte mode)
    {
        this._mode = mode;
    }

    @Override
    public String getName()
    {
        return this._name;
    }
    
    public byte getMode()
    {
        return this._mode;
    }

    public boolean haveValueByName(String value)
    {
        int contador = 0;

        while (contador < this._values.size())
        {
            if (this._values.get(contador).getValue().matches(value))
                return true;

            contador++;
        }

        return false;
    }


    @Override
    public String getXml() 
    {
        String code = "";
        
        code += (this.predicate) ? "<atom predicate=\"" + this._name +"\">":"<atom name=\"" + this._name +"\">";

        for (int i = 0; i < this._values.size(); i++)
            code += this._values.get(i).getXml(); 
	
        code += "</atom>"; 
        
        return code;
    }

    @Override
    public Node maching(String[][] params, int mode, NodeList state) 
    {
        Predicate auxiliar  = new Predicate(this.getName());
        int position        = 0;
        
        while (position < this._values.size())
        {
            for (int i = 0; i < params.length; i++)
            {
                if ((this._values.get(position).getValue() == null) ? false:this._values.get(position).getValue().equals(params[i][0])) {
                    auxiliar._values.add(new Value(params[i][1], params[i][0]));
                    position++;
                    break;
                }
            }
        }    
            
        return auxiliar;
    }
    
    @Override
    public Node clone() 
    {
        Predicate auxiliar = new Predicate(this._name);
    
        for (int i = 0; i < this._values.size(); i++)
        {
            auxiliar.addValue(this._values.get(i).getValue(), this._values.get(i).getType());
        }
        
        return auxiliar;
    }

    @Override
    public Boolean equal(Node node) 
    {
        if (this._type == node._type)
        {
            if (this._name == null ? ((Predicate) node).getName() == null : this._name.toUpperCase().equals(((Predicate) node).getName().toUpperCase()))
            {
                if (this._values.size() == ((Predicate) node)._values.size())
                {
                    for (int i = 0; i < this._values.size(); i++)
                    {
                        if (this._values.get(i).getValue() == null ? ((Predicate) node)._values.get(i).getValue() != null : !this._values.get(i).getValue().toUpperCase().equals(((Predicate) node)._values.get(i).getValue().toUpperCase()))
                            return false;
                    }

                    return true;
                }

                return false;
            }
        }
        
        return false;
    }
    
    public String getValuesDebug()
    {
        String result = "";
        
        for (int i = 0; i < this._values.size(); i++)
        {
            result += this._values.get(i).getValue();
        }
        
        return result;
    }
}
