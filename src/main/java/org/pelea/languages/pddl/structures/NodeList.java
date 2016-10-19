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

package org.pelea.languages.pddl.structures;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.pelea.languages.pddl.structures.nodes.Fluent;
import org.pelea.languages.pddl.structures.nodes.Node;
import org.pelea.languages.pddl.structures.nodes.Predicate;
import org.pelea.languages.pddl.UtilPDDL;

/**
 *
 * @author moises
 */
public class NodeList implements Cloneable
{
    public static int NOTNEGATED = 1;
    public static int NEGATED = 2;
    
    private List<Node> nodes;
    private List<Integer> states;

    public NodeList()
    {
        this.nodes = new ArrayList<Node>();
        this.states = new ArrayList<Integer>();
    }

    public int size()
    {
        return this.nodes.size();
    }
    
    public Node get(int i)
    {
        return this.nodes.get(i);
    }
    
    public int getStateByNode(int i) {
        return this.states.get(i);
    }

    public void add(Node node) {
        this.nodes.add(node);
        this.states.add(NOTNEGATED);
    }
    
    public void add(Node node, int state) {
        this.nodes.add(node);
        this.states.add(state);
    }
    
    public void add(Element code)
    {
        if (UtilPDDL.getType(code.getAttributeValue("type"), code.getName()) == UtilPDDL.NODE_FUNCTION)
            this.nodes.add(this.nodes.size(), new Fluent(((Element) code.getChildren().get(0))));
        else
            this.nodes.add(this.nodes.size(), new Predicate(code));
    }
    
    public void delete(Node node)
    {
        for (int i = 0; i < this.size(); i++)
        {
            if (this.nodes.get(i).getType() == node.getType())
            {
                if (this.nodes.get(i).equal(node))
                {
                    this.nodes.remove(i);
                    break;
                }
            }
        }
    }
    
    public void update(Node node)
    {
        for (int i = 0; i < this.size(); i++) {
            if (this.nodes.get(i).getType() == node.getType()) {
                if (((Fluent) this.nodes.get(i)).equal((Fluent) node)) {
                    ((Fluent) this.nodes.get(i)).updateValue(((Fluent) node).getValue());
                    break;
                }
            }
        }
    }
    
    public void delete(int position)
    {
        this.nodes.remove(position);
    }
    
    public void updateValueNode(Node node)
    {
        for (int i = 0; i < this.size(); i++)
        {
            if (this.nodes.get(i).getType() == node.getType()) 
            {
                //if (this.nodes.get(i).equal(node))
                    //((Fluent) this.nodes.get(i)).update(node);
            }
        }
    }

    public List<Node> getNodesByName(String name)
    {
        if (!name.isEmpty())
        {
            List<Node> items = new ArrayList<Node>();

            for (int i = 0; i < this.size(); i++)
            {
                if (this.nodes.get(i).getName().matches(name))
                    items.add(this.nodes.get(i));
            }

            return items;
        }

        return null;
    }

    public List<Node> getNodesByValue(String value)
    {
        if (!value.isEmpty())
        {
            List<Node> items = new ArrayList<Node>();

            for (int i = 0; i < this.size(); i++)
            {
                if (this.nodes.get(i).getName().matches(value))
                    items.add(this.nodes.get(i));
            }

            return items;
        }

        return null;
    }
    
    public List<Node> getNodesByNameAndValue(String name, String[] values)
    {
        int i, j = 0; 
        
        if (!name.isEmpty())
        {
            List<Node> items = new ArrayList();

            for (i = 0; i < this.size(); i++)
            {
                if (this.nodes.get(i).getName().matches(name)) {
                    
                    if (this.nodes.get(i) instanceof Predicate) { 
                    
                        String[] pValues = ((Predicate) this.nodes.get(i)).getValues();

                        for (j = 0; j < values.length; j++) {
                            if (!pValues[j].toUpperCase().matches(values[j].toUpperCase()))
                                break;
                        }

                        if (j == values.length) {
                            items.add(this.nodes.get(i));
                        }
                    }
                    else if (this.nodes.get(i) instanceof Fluent) {
                    
                         Object[] nodes = ((Fluent) this.nodes.get(i)).getValues();
                         
                         String[] pValues = ((Predicate) nodes[0]).getValues();

                         for (j = 0; j < values.length; j++) {
                            if (!pValues[j].toUpperCase().matches(values[j].toUpperCase()))
                                break;
                        }

                        if (j == values.length) {
                            items.add(this.nodes.get(i));
                        }
                         
                    }
                }
            }

            return items;
        }

        return null;
    }
    
    public void deleteNodeByMode(byte mode)
    {
        int position = 0;
        
        while (position < this.size())
        {
            if (this.nodes.get(position).getType() == UtilPDDL.NODE_PREDICATE)
            {
                if (((Predicate) this.nodes.get(position)).getMode() == mode)
                    this.nodes.remove(position);
                else
                    position++;
            }
            else if (this.nodes.get(position).getType() == UtilPDDL.NODE_FUNCTION)
            {
                if (((Fluent) this.nodes.get(position)).getMode() == mode)
                    this.nodes.remove(position);
                else
                    position++;
            }
            else
                position++;
        }
    }
    
    public String getXml(boolean problem)
    {
        String code = "";
        
        if (problem)
            code += "<init>";
        
        for (int i = 0; i < this.nodes.size(); i++)
        {
            code += this.nodes.get(i).getXml();
        }
        
        if (problem)
            code += "</init>";
        
        return code;
    }
    
    
    public List generateCursor(int size)
    {
        ArrayList ids = new ArrayList();
        
        for (int i = 0; i < size; i++)
        {
            ids.add(new Integer(i));
        }
        
        return ids;
    }
    
    public boolean existPredicate(Node predicate) {
        int position = 0;
        
        while (position < this.size()) {
            if(predicate.getType() == this.get(position).getType()) {
                if (predicate.equal(this.get(position))) {
                    return true;
                }
            }
            
            position++;
        }
        
        return false;
    }
    
    private boolean existPredicate(Node predicate, ArrayList predicates) {
        int position = 0;
        
        while (position < predicates.size()) {
            if(predicate.getType() == this.get((Integer) predicates.get(position)).getType()) {
                if (predicate.equal(this.get((Integer) predicates.get(position)))) {
                    predicates.remove(position);
                    return true;
                }
            }
            position++;
        }
        
        return false;
    }
    
    public boolean notExistPredicate(Node predicate) {
        int position = 0;
        
        while (position < this.size()) {
            if(predicate.getType() == this.get(position).getType()) {
                if (predicate.equal(this.get(position))) {
                    return false;
                }
            }
            
            position++;
        }
        
        return true;
    }
    
    private boolean notExistPredicate(Node predicate, ArrayList predicates) {
        int position = 0;
        
        while (position < predicates.size()) {
            if(predicate.getType() == this.get((Integer) predicates.get(position)).getType()) {
                if (predicate.equal(this.get((Integer) predicates.get(position)))) {
                    return false;
                }
            }
            position++;
        }
        
        return true;
    }
    
    public boolean compare(NodeList list)
    {
        ArrayList cursor    = (ArrayList) this.generateCursor(this.size());
        int position        = 0;
        boolean find;
        
        while (position < list.size()) {
                       
            if (list.getStateByNode(position) == NodeList.NOTNEGATED) { //EXISTE
                if (!this.existPredicate(list.get(position), cursor)) {
                    return false;
                }
            }
            else { //NO EXISTE
                if (!this.notExistPredicate(list.get(position), cursor)) {
                    return false;
                }
            }
            
            position++;
            
        }
        
        /*while (position < list.size()) {
            cursorPosition = 0;
            find = false;
            
            while ((!find) && (cursorPosition < cursor.size())) {
                if (list.get(position).getType() == this.get((Integer) cursor.get(cursorPosition)).getType()) {
                    if (list.get(position).equal(this.get((Integer) cursor.get(cursorPosition)))) {
                        find = true;
                        cursor.remove(cursorPosition);
                    }
                }                
                cursorPosition++;
            }
            
            if (find)   position++;
            else {
                //System.out.println(list.get(position).getXml());
                return false;
            }
        }*/

        return true;
    }
    
    public int countPredicate(NodeList predicates) {
        
        int count = 0;
        
        for (int i = 0; i < this.size(); i++) {
            for (int j = 0; j < predicates.size(); j++) {
                if (this.nodes.get(i).equal(predicates.get(j))) {
                    count++;
                    break;
                }
            }
        }
        
        return count;
    }
    
    public void printDebug() {
        for (int i = 0; i < this.size(); i++) {
            System.out.println("(" + this.nodes.get(i).getName() + ((Predicate) this.nodes.get(i)).getValuesDebug() + ")");
        }
    }
}
