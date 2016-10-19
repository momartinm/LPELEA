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

package org.pelea.languages.pddl.plan;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.pelea.languages.pddl.structures.nodes.Node;
import org.pelea.languages.pddl.structures.nodes.Predicate;
import org.pelea.languages.pddl.structures.nodes.Value;
import org.pelea.languages.pddl.UtilPDDL;
import org.pelea.languages.pddl.structures.NodeList;

/**
 *
 * @author moises
 */
public class Action extends Node implements Cloneable
{
    private String _name;
    private String _start;
    private String _execution;
    private List<Value> _values;

    public Action()
    {
        this._type   = UtilPDDL.NODE_ACTION;
        this._values = new ArrayList<Value>();
    }

    public Action(String name, String start, String execution)
    {
        this._type      = UtilPDDL.NODE_ACTION;
        this._name      = name.trim();
        this._start     = start;
        this._execution = execution;
        this._values    = new ArrayList<Value>();
    }
    
    public Action(Element code)            
    {
        this._type      = UtilPDDL.NODE_ACTION;
        this._name      = code.getAttributeValue("name").trim();
        this._start     = code.getAttributeValue("start_time").trim();
        this._execution = code.getAttributeValue("execution_time").trim();
        this._values    = new ArrayList<Value>();
        
        List values = code.getChildren();
      
        for (int j = 0; j < values.size(); j++)
        {
            this._values.add(new Value(((Element) values.get(j)).getAttributeValue("name"), ((Element) values.get(j)).getAttributeValue("class")));
        }
    }

    public void addValue(String value)
    {
        this._values.add(new Value(value));
    }
    
    public void addValue(String value, String type)
    {
        this._values.add(new Value(value, type));
    }
    
    public String[][] getValues()
    {
        String[][] auxiliar = new String[this._values.size()][2];
        
        for (int i = 0; i < this._values.size(); i++) {
            auxiliar[i][0] = ""; //Empty to put name of the value
            auxiliar[i][1] = this._values.get(i).getValue();
        }
        
        return auxiliar;
    }
    
    public List getValuesList()
    {
        ArrayList auxiliar = new ArrayList();
        
        for (int i = 0; i < this._values.size(); i++) {
            auxiliar.add(this._values.get(i).getValue());
        }
        
        return auxiliar;
    }

    public void addName(String name)
    {
        this._name = name;
    }

    @Override
    public String getName()
    {
        return this._name;
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
    
    public ArrayList<String> getValuesByType(String type)
    {
        int contador = 0;
        ArrayList<String> values = new ArrayList();
        
        while (contador < this._values.size())
        {
            if (this._values.get(contador).getType().matches(type))
                values.add(this._values.get(contador).getValue());

            contador++;
        }

        return values;
    }

    @Override
    public String getXml() 
    {
        String code = "";
        
        code += "<action name=\"" + this._name +"\" start_time=\"" + this._start +"\" execution_time=\"" + this._execution +"\">";
	
        for (int i = 0; i < this._values.size(); i++)
            code += this._values.get(i).getXml(); 
	
        code += "</action>"; 
        
        return code;
    }

    @Override
    public Node maching(String[][] params, int mode, NodeList state) 
    {
        Predicate auxiliar  = new Predicate(this.getName());
        int position        = 0;
        
        for (int i = 0; i < params.length; i++) {            
            
            if (position < this._values.size()) {      
                if (this._values.get(position).getValue() == null ? params[i][0] == null : this._values.get(position).getValue().equals(params[i][0])) {
                    auxiliar.addValue(params[i][1], params[i][0]);
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
        Action auxiliar = new Action(this._name, this._start, this._execution);
    
        for (int i = 0; i < this._values.size(); i++)
        {
            auxiliar.addValue(this._values.get(i).getValue(), this._values.get(i).getType());
        }
        
        return auxiliar;
    }

    @Override
    public Boolean equal(Node node) 
    {
        if (this._type == node.getType())
        {
            if (this._name == null ? ((Action) node).getName() == null : this._name.toUpperCase().equals(((Predicate) node).getName().toUpperCase()))
            {
                if (this._values.size() == ((Action) node)._values.size())
                {
                    for (int i = 0; i < this._values.size(); i++)
                    {
                        if (this._values.get(i).getValue() == null ? ((Action) node)._values.get(i).getValue() != null : !this._values.get(i).getValue().toUpperCase().equals(((Action) node)._values.get(i).getValue().toUpperCase())) {
                            return false;
                        }
                    }

                    return true;
                }

                return false;
            }
        }
        
        return false;
    }
    
    public String printPDDL() {
        
        String action = "(" + this._name;
        
        for (int i = 0; i < this._values.size(); i++) {
            action += " " + this._values.get(i).getValue();
        }
        action += ")";
       
        return action;
    }
}
