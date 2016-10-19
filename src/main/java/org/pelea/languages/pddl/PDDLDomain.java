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

package org.pelea.languages.pddl;


import org.pelea.languages.Domain;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.pelea.languages.pddl.predicates.actions.Action;
import org.pelea.languages.pddl.predicates.actions.ActionList;
import org.pelea.languages.pddl.predicates.actions.ActionResult;
import org.pelea.languages.pddl.structures.NodeList;
import org.pelea.languages.pddl.structures.NodeTree;
import org.pelea.languages.pddl.types.Type;
import org.pelea.languages.pddl.structures.TypeList;
import org.pelea.languages.pddl.structures.nodes.Predicate;
import org.pelea.utils.Util;

/**
 *
 * @author moises
 */
public class PDDLDomain extends Domain 
{
    private List requirements;
    private TypeList types;
    private List<Predicate> predicates;
    private List<Predicate> functions;
    private ActionList actions;
    
    public PDDLDomain()
    {
        this.requirements  = new ArrayList<String>();
        this.types         = new TypeList();
        this.predicates    = new ArrayList<Predicate>();
        this.functions     = new ArrayList<Predicate>();
        this.actions       = new ActionList();
    }
    
    public PDDLDomain(String xml)
    {
        this.requirements  = new ArrayList<String>();
        this.types         = new TypeList();
        this.predicates    = new ArrayList<Predicate>();
        this.functions     = new ArrayList<Predicate>();
        this.actions       = new ActionList();
        
        try
        {
            SAXBuilder builder  = new SAXBuilder();
            Document document   = (Document) builder.build(new StringReader(xml));
            Element domain      = (Element) document.getRootElement().getContent().get(1);              
            List nodes          = domain.getChildren();
            
            this.setDomainName(domain.getAttributeValue("name"));
            
            for (int i = 0; i < nodes.size(); i++)
            {
                if (((Element) nodes.get(i)).getName().contains("requirements"))
                    this.generateRequirements(((Element) nodes.get(i)).getChildren());
                else if (((Element) nodes.get(i)).getName().contains("types"))
                    this.generateTypes(((Element) nodes.get(i)).getChildren());
                else if (((Element) nodes.get(i)).getName().contains("predicates"))
                    this.generatePredicates(((Element) nodes.get(i)).getChildren());
                else if (((Element) nodes.get(i)).getName().contains("functions"))
                    this.generateFunctions(((Element) nodes.get(i)).getChildren());            
                else
                    this.generateAction((Element) nodes.get(i));
            }           
        }
        catch (IOException io)
        {
            Util.printError(this.getName(), io.getMessage());
	} 
        catch (JDOMException jdomex) 
        {
            Util.printError(this.getName(), jdomex.getMessage());
        }

    }
    
    @Override
    public void loadDomain(String xml)
    {
        this.requirements  = new ArrayList<String>();
        this.types         = new TypeList();
        this.predicates    = new ArrayList<Predicate>();
        this.functions     = new ArrayList<Predicate>();
        this.actions       = new ActionList();
        
        try
        {
            SAXBuilder builder  = new SAXBuilder();
            Document document   = (Document) builder.build(new StringReader(xml));
            Element domain      = document.getRootElement();
            List nodes          = domain.getChildren();
            
            this.setDomainName(domain.getAttributeValue("name"));
            
            for (int i = 0; i < nodes.size(); i++)
            {
                if (((Element) nodes.get(i)).getName().contains("requirements"))
                    this.generateRequirements(((Element) nodes.get(i)).getChildren());
                else if (((Element) nodes.get(i)).getName().contains("types"))
                    this.generateTypes(((Element) nodes.get(i)).getChildren());
                else if (((Element) nodes.get(i)).getName().contains("predicates"))
                    this.generatePredicates(((Element) nodes.get(i)).getChildren());
                else if (((Element) nodes.get(i)).getName().contains("functions"))
                    this.generateFunctions(((Element) nodes.get(i)).getChildren());            
                else
                    this.generateAction((Element) nodes.get(i));
                    //this.generateActions(nodes.subList(i, nodes.size()));
            }           
        }
        catch (IOException io)
        {
            Util.printError(this.getName(), io.getMessage());
	} 
        catch (JDOMException jdomex) 
        {
            Util.printError(this.getName(), jdomex.getMessage());
        }
    }

    private void generateRequirements(List elements)
    {
        Element node = null;
        
        for (int i = 0; i < elements.size(); i++) 
        {
            node = (Element) elements.get(i);
               
            this.requirements.add(node.getAttributeValue("name"));             
        }
    }
    
    private void generateTypes(List elements)
    {
        Element node = null;
        
        for (int i = 0; i < elements.size(); i++) 
        {
            node = (Element) elements.get(i);
               
            this.types.addType(new Type(node.getAttributeValue("name"), node.getAttributeValue("type")));              
        }
    }
    
    private void generatePredicates(List elements)
    {
        for (int i = 0; i < elements.size(); i++) 
        {
            this.predicates.add(this.predicates.size(), new Predicate((Element) elements.get(i)));
        }
    }
    
    private void generateFunctions(List elements)
    {
        for (int i = 0; i < elements.size(); i++) 
        {
            this.functions.add(this.functions.size(), new Predicate((Element) elements.get(i)));
        }
    }
    
    private void generateParameters(Action auxiliar, List parameters)
    {
        List values = null;
        
        for (int i = 0; i < parameters.size(); i++) 
        {
             auxiliar.addParameter(((Element) parameters.get(i)).getAttributeValue("name"), ((Element) parameters.get(i)).getAttributeValue("type"));
        }
    }
    
    private List generateTree(List elements)
    {
        List values         = null;
        List nodes          = new ArrayList();
        NodeTree new_node   = null;
        
        for (int i = 0; i < elements.size(); i++) 
        {
            Element element = (Element) elements.get(i);
            
            switch (UtilPDDL.getType(element.getAttributeValue("type"), element.getName()))
            {
                case UtilPDDL.NODE_PREDICATE:
                    new_node = new NodeTree(element);
                    break;
                case UtilPDDL.NODE_PREDICATE_ATOM:
                    new_node = new NodeTree(element);
                    break;
                case UtilPDDL.NODE_FUNCTION:
                    new_node = new NodeTree(element);
                    break;
                default:
                    new_node = new NodeTree(element);
                    new_node.addDescendents(generateTree(element.getChildren()));
                    break;
            }

            nodes.add(nodes.size(), new_node);
        }
        
        return nodes;
    }
    
    private void generateAction(Element element)
    {
        Element node        = null;
        List nodes          = null;
        Action  auxiliar    = null;
        
        /*for (Object element : elements) {*/
            
        node = (Element) element;
        nodes   = node.getChildren();
        auxiliar = new Action(node.getAttributeValue("name"), node.getAttributeValue("type"));
            
        this.generateParameters(auxiliar, ((Element) nodes.get(0)).getChildren());
        auxiliar.addPreconditions((NodeTree) this.generateTree(((Element) nodes.get(1)).getChildren()).get(0));
        auxiliar.addEffects((NodeTree) this.generateTree(((Element) nodes.get(2)).getChildren()).get(0));
            
        this.actions.addAction(auxiliar);            
        /*}*/
    }
    
    public List<ActionResult> generatePreconditionsByAction(String name, String[][] values, NodeList state) {
        
        Action action = this.actions.find(name);
        action.generateParameters(values);
        
        List<ActionResult> preconditions = action.generatePreconditions(values, state);
        return preconditions;
    }
    
    public List<ActionResult> generateEffectsByAction(String name, String[][] values, NodeList state) {
        
        Action action = this.actions.find(name);
        action.generateParameters(values);
        
        List<ActionResult> effects = action.generateEffects(values, 2, state);
        
        return effects;
    }
    
    @Override
    public String generateDomainXML(boolean complete)
    {
        String code = "";
        
        if (complete)
        {
            code += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            code += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
            code += "<domain name=\"" + this.getDomainName() + "\"> "; 
            code += this.types.getXml();
            code += "</domain>";
        } 
        
        if (complete) {
            code += "</define>";
        }
            
        return code;
    }
    
    public String generateDomainPDDL()
    {
        return null;
    }
}