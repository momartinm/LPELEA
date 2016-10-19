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
public class Fluent extends Node implements Cloneable
{    
    public static final int ASSIGN = 1;
    public static final int COMPARATION = 2;
    
    private int operator;
    
    private byte mode;
    private int typeOperation;
    private final List<Object> values;

    public Fluent()
    {
        this.mode = NUMERIC;
        this._type = UtilPDDL.NODE_FUNCTION;
        this.values = new ArrayList<Object>();
    }
    
    public Fluent(String operation, String operationType, String name, String value)
    {
        this.mode = NUMERIC;
        this._type = UtilPDDL.NODE_FUNCTION;
        this.operator = this.operation2Code(operation.trim());
        this.typeOperation = this.typeOperation2Code(operationType.trim());
        this.values = new ArrayList<Object>();
        this.values.add(new Predicate(name, false));
        this.values.add(new NumericValue(value));
    }
    
    public Fluent(String operation, String operationType)
    {
        this.mode = NUMERIC;
        this._type = UtilPDDL.NODE_FUNCTION;
        this.operator = this.operation2Code(operation.trim());
        this.typeOperation = this.typeOperation2Code(operationType.trim());
        this.values = new ArrayList<Object>();
    }
    
    public Fluent(int operation, int operationType, String name, String value)
    {
        this.mode = NUMERIC;
        this._type = UtilPDDL.NODE_FUNCTION;
        this.operator = operation;
        this.typeOperation = operationType;
        this.values = new ArrayList<Object>();
        this.values.add(new Predicate(name, false));
        this.values.add(new NumericValue(value));
    }    
    
    public Fluent(byte mode, int operation, int operationType, String name, String value)
    {
        this.mode = mode;
        this._type = UtilPDDL.NODE_FUNCTION;
        this.operator = operation;
        this.typeOperation = operationType;
        this.values = new ArrayList<Object>();
        this.values.add(new Predicate(name, false));
        this.values.add(new NumericValue(value));
    }
    
    public Fluent(int operation, int operationType)
    {
        this.mode = NUMERIC;
        this._type = UtilPDDL.NODE_FUNCTION;
        this.operator = operation;
        this.typeOperation = operationType;
        this.values = new ArrayList<Object>();
    }    
    
    public Fluent(Element code)
    {
        this.mode = NUMERIC;
        this._type = UtilPDDL.NODE_FUNCTION;
        this.typeOperation = this.typeOperation2Code(code.getAttributeValue("type"));
        this.operator = this.operation2Code(code.getAttributeValue("literal"));        
        this.values = new ArrayList<Object>();
        
        for (Object val : code.getChildren()) {
            if (((Element) val).getName().matches("atom"))
                this.values.add(new Predicate((Element) val));
            else if (((Element) val).getName().matches("term"))
                this.values.add(new NumericValue((Element) val));
        }
     }
    
    public byte getMode()
    {
        return this.mode;
    }
    
    private int typeOperation2Code(String value) {
        if (value.trim().matches("init-assign"))
            return Fluent.ASSIGN;
        if (value.trim().matches("comp"))
            return Fluent.COMPARATION;
        return 0;
    }
    
    private String code2TypeOperation() {
        if (this.typeOperation == Fluent.ASSIGN)
            return "init-assign";
        if (this.typeOperation == Fluent.COMPARATION)
            return "comp";
        return "";
    }
            
    private int operation2Code(String value)
    {
        if (value.trim().matches("<="))
            return 1;
        if (value.trim().matches("<"))
            return 2;
        if (value.trim().matches(">"))
            return 3;
        if (value.trim().matches(">="))
            return 4;
        if (value.trim().matches("increase"))
            return 5;
        if (value.trim().matches("decrease"))
            return 6;
        if (value.trim().matches("="))
            return 7;        
        
        return 0;
    }
    
    private String code2Operation()
    {
        if (this.operator == 1)
            return "<=";
        if (this.operator == 2)
            return "<";
        if (this.operator == 3)
            return ">";
        if (this.operator == 4)
            return ">=";
        if (this.operator == 5)
            return "increase";
        if (this.operator == 6)
            return "decrease";
        if (this.operator == 7)
            return "=";
        
        return "";
    }
    
    
    public void addValue(Object value) {
        this.values.add(value);
    }
    
    public Object getValue(int position) {
        return this.values.get(position);
    }
    
    public Object[] getValues() {
        Object[] auxiliar = new Object[this.values.size() + 1];
        
        for (int i = 0; i < this.values.size(); i++)
            auxiliar[i] = this.values.get(i);
        
        return auxiliar;
    }
    
    public void changeMode(byte mode)
    {
        this.mode = mode;
    }

    @Override
    public String getName()
    {
        return ((Predicate) this.values.get(0)).getName();
    }
    
    public void addOperation(int operation)
    {
        this.operator = operation;
    }

    public int getOperation()
    {
        return this.operator;
    }
    
    public int getTypeOperation() {
        return this.typeOperation;
    }

    @Override
    public String getXml() 
    {
        String code = "";
        
        code += "<gd type=\"binary\">";        
        code += "<binary type=\"" + this.code2TypeOperation() + "\" literal=\"" + this.code2Operation() + "\">";
        for (int i = 0; i < this.values.size(); i++) {
            
            Object val = this.values.get(i);
            
            if (val instanceof Predicate)
                code += ((Predicate) val).getXml();
            else if (val instanceof Value)
                code += ((Value) val).getXml();
            else if (val instanceof NumericValue)
                code += ((NumericValue) val).getXml();
        }
        code += "</binary>";
        code += "</gd>";
        
        return code;
    }
    
    private int calculateValue(NodeList state, String[][] params, Predicate predicate) {
        
        int value = 0;
        List<Node> node = null;
        
        if (predicate.values() == 0) {
            node = state.getNodesByName(predicate.getName());
        }
        else {
            
            String[] idems = predicate.getValues();
            String[] values = new String[idems.length];
            int j;
            
            for (int i = 0; i < idems.length; i++) {
                j = 0;
                while (j < params.length) {
                    if (idems[i].equals(params[j][0]))
                        break;
                    else
                        j++;
                }
                values[i] = params[j][1];
            }
            
            node = state.getNodesByNameAndValue(predicate.getName(), values);
        }
        
        return ((Fluent) node.get(0)).getValue();
    }

    @Override
    public Node maching(String[][] params, int mode, NodeList state) 
    {
        Fluent auxiliar = null;
        int values[] = new int[this.values.size()];
        int value = 0;
        
        for (int i = 0; i < this.values.size(); i++) {
            
            if (this.values.get(i) instanceof Predicate) 
                values[i] = this.calculateValue(state, params, (Predicate) this.values.get(i));
            else
                values[i] = ((NumericValue) this.values.get(i)).getValue();
        }
        
        value = (this.operator == 5) ? values[0] + values[1]:values[0] - values[1];

        auxiliar = new Fluent(7, Fluent.ASSIGN, ((Predicate) this.values.get(0)).getName(), value + "");
        return auxiliar;
    }

    @Override
    public Node clone() 
    {
        Fluent auxiliar = new Fluent(this.operator, this.typeOperation);
    
        for (int i = 0; i < this.values.size(); i++)
        {
            Object val = this.values.get(i);
            
            if (val instanceof Predicate)
                auxiliar.addValue(((Predicate) val).clone());
            else
                auxiliar.addValue(((NumericValue) val).clone());
        }
        
        return auxiliar;
    }

    @Override
    public Boolean equal(Node node) 
    {
        if (this._type == node._type)
        {
            if (this.typeOperation == ((Fluent) node).getTypeOperation()) {
                if (this.values.size() == ((Fluent) node).values.size())
                {
                    for (int i = 0; i < this.values.size(); i++)
                    {
                        Object value1 = this.values.get(i);
                        Object value2 = ((Fluent) node).getValue(i);
                        
                        if ((value1 instanceof Predicate) && (value2 instanceof Predicate))
                            if (!((Predicate) value1).equal((Predicate) value2))
                                return false;
                        else if ((value1 instanceof NumericValue) && (value2 instanceof NumericValue))
                            if (!((NumericValue) value1).equal((NumericValue) value2))
                                return false;
                        else
                            return false;
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }
    
    public Boolean equal(Fluent node) 
    {
        if (this._type == node._type)
        {
            if (this.typeOperation == node.getTypeOperation()) {
                if (this.values.size() == node.values.size())
                {
                    for (int i = 0; i < this.values.size()-1; i++)
                    {
                        Object value1 = this.values.get(i);
                        Object value2 = ((Fluent) node).getValue(i);
                        
                        if ((value1 instanceof Predicate) && (value2 instanceof Predicate)) {
                            if (!((Predicate) value1).equal((Predicate) value2))
                                return false;
                        }
                        else
                            return false;
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }
    
    public void updateValue(int value) {
        ((NumericValue) this.values.get(this.values.size()-1)).update(value);
    }
    
    public int getValue() {
        return ((NumericValue) this.values.get(this.values.size()-1)).getValue();
    }
}