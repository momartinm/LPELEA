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
import org.pelea.languages.pddl.structures.nodes.Connector;
import org.pelea.languages.pddl.structures.nodes.Fluent;
import org.pelea.languages.pddl.structures.nodes.Node;
import org.pelea.languages.pddl.structures.nodes.Predicate;
import org.pelea.languages.pddl.UtilPDDL;
import org.pelea.languages.pddl.predicates.actions.ActionResult;

/**
 *
 * @author moises
 */
public class NodeTree implements Cloneable
{
    private Node node;
    private List<NodeTree> descendents;
    
    public NodeTree(Element code)
    {
        switch (UtilPDDL.getType(code.getAttributeValue("type"), code.getName()))
        {
            case UtilPDDL.NODE_PREDICATE:
                
                this.node = new Predicate(((Element) code.getChildren().get(0)));
                break;
                
            case UtilPDDL.NODE_PREDICATE_ATOM:
                
                this.node = new Predicate(code);
                break;
                
            case UtilPDDL.NODE_FUNCTION:
                
                this.node = new Fluent(((Element) code.getChildren().get(0)));
                break;
            
            default:
                
                this.node = new Connector(code);
                break;
        }

        this.descendents   = new ArrayList<NodeTree>();
    }
    
    public void addDescendent(NodeTree node)
    {
        this.descendents.add(this.descendents.size(), node);
    }
    
    public void addDescendents(List nodes)
    {
        for (int i = 0; i < nodes.size(); i++) 
        {
            this.descendents.add(this.descendents.size(), (NodeTree) nodes.get(i));
        }
    }
    
    public String getXml()
    {
        String code = "";
        
        switch (this.node.getType())
        {
            case UtilPDDL.NODE_PREDICATE:
                
                code += "<gd type=\"predicate\">";
                code += ((Predicate) this.node).getXml();
                code += "</gd>";
                break;
                
            case UtilPDDL.NODE_FUNCTION:
                
                code += "<gd type=\"number\">";
                code += ((Fluent) this.node).getXml();
                code += "</gd>";
                break;
            
            default:
                
                code += ((Connector) this.node).getHeadXml();
                
                for (int i = 0; i < this.descendents.size(); i++)
                {
                    code += this.descendents.get(i).getXml();
                }
                
                code += ((Connector) this.node).getFootXml();
                
                break;
        }
        
        return code;
    }
    
    public void maching(String[][] values, List<ActionResult> predicates, int mode, NodeList state)
    {        
        switch (this.node.getType())
        {
            case UtilPDDL.NODE_PREDICATE:
                predicates.add(new ActionResult((Predicate) this.node.maching(values, mode, state), mode));
                break;
            case UtilPDDL.NODE_FUNCTION:
                predicates.add(new ActionResult((Fluent) this.node.maching(values, mode, state), mode));
                break;
            case UtilPDDL.NODE_NOT:
                for (int i = 0; i < this.descendents.size(); i++)
                    this.descendents.get(i).maching(values, predicates, 1, state);
                break;
            default:
                for (int i = 0; i < this.descendents.size(); i++)
                    this.descendents.get(i).maching(values, predicates, mode, state);
                break;
        }
    }
    
    public void getPredicates(NodeList nodes)
    {
        switch (this.node.getType())
        {        
            case UtilPDDL.NODE_PREDICATE:
                
                nodes.add(this.node);
                break;
                
            case UtilPDDL.NODE_FUNCTION:
                
                nodes.add(this.node);
                break;

            default:
                
                for (int i = 0; i < this.descendents.size(); i++)
                    this.descendents.get(i).getPredicates(nodes);
                break;
        }
    }
    
    public boolean compare(NodeTree list) {
        NodeList me = new NodeList();
        NodeList other = new NodeList();
        
        this.getPredicates(me);
        this.getPredicates(other);
        
        return me.compare(other);
    }
}