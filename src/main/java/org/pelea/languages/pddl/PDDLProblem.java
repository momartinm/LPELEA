/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3m.es
 * 
 * Copyright 2012, Mois�s Mart�nez
 *
 * (Questions/bug reports now to be sent to Mois�s Mart�nez)
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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.pelea.languages.pddl.structures.TypeList;
import org.pelea.languages.pddl.structures.NodeTree;
import org.pelea.languages.pddl.structures.NodeList;
import org.pelea.languages.pddl.structures.nodes.Fluent;
import org.pelea.languages.pddl.structures.nodes.Node;
import org.pelea.languages.pddl.structures.nodes.Predicate;
import org.pelea.languages.pddl.types.Type;
import org.pelea.languages.Problem;
import org.pelea.languages.pddl.comparison.PDDLComparison;
import org.pelea.languages.pddl.metric.Metrics;
import org.pelea.languages.pddl.predicates.actions.ActionResult;

/**
 *
 * @author moises
 */
public class PDDLProblem extends Problem {
    
    protected ArrayList requirements;
    /**
     * List of the types of the problem
     */
    protected TypeList types;
    /**
     * List of the predicates that define the current state
     */
    protected NodeList currentState;
    /**
     * Next state generated when and action is applicated over the problem
     */
    protected NodeList nextState;
    protected NodeList nextPreconditions;
    
    /**
     * List of the predicates that define the initial problem
     */
    protected NodeList initialState;
  
    /**
     * Tree of the goals that must be reached
     */
    protected NodeTree goals;
    
    protected Metrics metrics;
    
    protected PDDLComparison comparator;

    
    public PDDLProblem()
    {
        this.requirements = new ArrayList<>();
        this.types = new TypeList();
        this.currentState = new NodeList();
        this.initialState = null;
        this.nextState = null;
        this.goals = null;
        this.metrics = null;
    }
    
    /**
     * 
     * @param data
     * @throws java.io.IOException
     * @throws org.jdom.JDOMException */
    public PDDLProblem(String data) throws IOException, JDOMException {
        this.types = new TypeList();
        this.currentState = new NodeList();
        this.goals = null;
        this.metrics = null;
        this.requirements = new ArrayList<String>();
        
        SAXBuilder builder  = new SAXBuilder();
        Document document   = (Document) builder.build(new StringReader(data));
        Element problem     = (document.getRootElement().getContent().size() > 1) ? (Element) document.getRootElement().getContent().get(1):(Element) document.getRootElement().getContent().get(0);       
        List nodes          = problem.getChildren();
        //Types
        
        this.problemName = problem.getAttributeValue("name").trim();
        this.domainName = problem.getAttributeValue("domain").trim();
        
        if (((Element) nodes.get(0)).getName().matches("requirements")) {
            this.generateRequirements(((Element) nodes.get(0)).getChildren());
            this.generateTypes(((Element) nodes.get(1)).getChildren());
            this.generatePredicates(((Element) nodes.get(2)).getChildren());
            this.goals = (NodeTree) this.generateTree(((Element) nodes.get(3)).getChildren()).get(0);
            if ((nodes.size() > 4) && (((Element) nodes.get(4)).getName().matches("metrics"))) {
                this.metrics = new Metrics(((Element) nodes.get(4)));
            }
        }
        else {
            this.generateTypes(((Element) nodes.get(0)).getChildren());
            this.generatePredicates(((Element) nodes.get(1)).getChildren());
            this.goals = (NodeTree) this.generateTree(((Element) nodes.get(2)).getChildren()).get(0);
            if ((nodes.size() > 3) && (((Element) nodes.get(3)).getName().matches("metrics"))) {
                this.metrics = new Metrics(((Element) nodes.get(3)));
            }
        }
    }
    
    /*
     * 
     */
    @Override
    public void loadState(String stateH) throws IOException, JDOMException {
        
        this.types = new TypeList();
        this.currentState = new NodeList();
        this.goals = null;
        this.initialState = null;
        this.nextState = null;
        
        SAXBuilder builder  = new SAXBuilder();
        Document document   = (Document) builder.build(new StringReader(stateH));
        Element state       = document.getRootElement();
        List nodes          = state.getChildren();
            
        this.setProblemName(state.getAttributeValue("name").trim());
        this.setDomainName(state.getAttributeValue("domain").trim());
       
        if (((Element) nodes.get(0)).getName().matches("requirements")) {
            this.generateRequirements(((Element) nodes.get(0)).getChildren());
            this.generateTypes(((Element) nodes.get(1)).getChildren());
            this.generatePredicates(((Element) nodes.get(2)).getChildren());
            this.goals = (NodeTree) this.generateTree(((Element) nodes.get(3)).getChildren()).get(0);
            if ((nodes.size() > 4) && (((Element) nodes.get(4)).getName().matches("metrics"))) {
                this.metrics = new Metrics(((Element) nodes.get(4)));
            }
        }
        else {
            this.generateTypes(((Element) nodes.get(0)).getChildren());
            this.generatePredicates(((Element) nodes.get(1)).getChildren());
            this.goals = (NodeTree) this.generateTree(((Element) nodes.get(2)).getChildren()).get(0);
            if ((nodes.size() > 3) && (((Element) nodes.get(3)).getName().matches("metrics"))) {
                this.metrics = new Metrics(((Element) nodes.get(3)));
            }
        }
    }
    
    private void generateRequirements(List elements) {
       for (int i = 0; i < elements.size(); i++) {
           this.requirements.add(((Element) elements.get(i)).getAttributeValue("name").trim());
       }    
    }
    
    private void generateTypes(List elements) {
        Element node = null;
        
        for (int i = 0; i < elements.size(); i++) {
            node = (Element) elements.get(i);
            
            this.types.addType(new Type(node.getAttributeValue("type"), node.getAttributeValue("name")));              
        }
    }
    
    private void generatePredicates(List elements) {
        
        for (int i = 0; i < elements.size(); i++) {
            this.currentState.add((Element) elements.get(i));
        }
    }
    
    private void generateInitialPredicates(List elements) {
        for (int i = 0; i < elements.size(); i++) 
        {
            this.currentState.add((Element) elements.get(i));
            this.initialState.add((Element) elements.get(i));
        }
    }
    
    private List generateTree(List elements) {
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
    
    /**
     *
     * @param complete
     * @return
     */
    @Override
    public String generateProblemXML(boolean complete) {
        String code = "";
        
        if (complete) {
            code += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            code += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
        }    
        
        code += "<problem name=\"" + this.getProblemName() + "\" domain=\"" + this.getDomainName() + "\"> "; 
        
        code += this.types.getXml();
        code += this.currentState.getXml(true);
        
        code += "<goals>"; 
        code += this.goals.getXml();
        code += "</goals>"; 
        
        if (this.metrics != null) {
            code += this.metrics.getXml();
        }
        
        code += "</problem>"; 
        
        if (complete) {
            code += "</define>";
        }
            
        return code;
    }    
    
    @Override
    public String generateStateXML(boolean complete) {
        String code = "";
        
        if (complete)
        {
            code += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            code += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
        }    
        
        code += "<state name=\"" + this.getProblemName() + "\" domain=\"" + this.getDomainName() + "\"> "; 
        
        code += this.types.getXml();
        code += this.currentState.getXml(true);
        
        code += "<goals>"; 
        code += this.goals.getXml();
        code += "</goals>"; 
        
        if (this.metrics != null) {
            code += this.metrics.getXml();
        }

        code += "</state>"; 
        
        if (complete) {
            code += "</define>";
        }
            
        return code;
    }   
    
    /**
     *
     * @return
     */
    public String generateProblemPDDL() {
        
        return "";
    }
    
    /**
     *
     * @param predicates
     */
    public void generateNextState(List<ActionResult> predicates) {
        
        this.nextState = new NodeList();
        
        for (int i = 0; i < this.currentState.size(); i++) {
            this.nextState.add(this.currentState.get(i).clone());
        }
        
        for (int i = 0; i < predicates.size(); i++) {
            if (predicates.get(i).getType() == ActionResult.ADDED) {
                if (predicates.get(i).getElement().getType() == UtilPDDL.NODE_PREDICATE)
                    this.nextState.add(predicates.get(i).getElement());
                else
                    this.nextState.update(predicates.get(i).getElement());
            }
            else if (predicates.get(i).getType() == ActionResult.DELETED) {
                this.nextState.delete(predicates.get(i).getElement());
            }
        }
    }
    
    public void generateNextPreconditions(List<ActionResult> predicates) {
        
        this.nextPreconditions = new NodeList();
        
        for (int i = 0; i < predicates.size(); i++) {
            this.nextPreconditions.add(predicates.get(i).getElement(), (predicates.get(i).getType() == ActionResult.DELETED) ? NodeList.NEGATED:NodeList.NOTNEGATED);
        }
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean goalsReached() {
        
        NodeList nodes = new NodeList();
        this.goals.getPredicates(nodes);
        
        return this.currentState.compare(nodes);
    }
    
    public boolean goalsReached(NodeList predicates) {
        
        NodeList nodes = new NodeList();
        this.goals.getPredicates(nodes);
        return predicates.compare(nodes);
    }
    
    /**
     *
     * @param predicates
     */
    public void modifyState(NodeList predicates) {
        this.currentState = predicates;
    }

    public void defineDynamicPredicates(List<String> predicates) {
        List<Node> nodes;
        
        for (int i = 0; i < predicates.size(); i++) {
            nodes = this.currentState.getNodesByName(predicates.get(i));
            for (int j = 0; j < nodes.size(); j++) {
                if (nodes.get(j).getType() == UtilPDDL.NODE_PREDICATE)
                    ((Predicate) nodes.get(j)).changeMode(Node.DYNAMIC);
                else if (nodes.get(j).getType() == UtilPDDL.NODE_FUNCTION)
                    ((Fluent) nodes.get(j)).changeMode(Node.DYNAMIC);
            }
        }
    }
    
    /**
     *
     * @return
     */
    public NodeList getPredicates() {
        return this.currentState;
    }
    
    public NodeList getNextState() {
        return this.nextState;
    }
    
    public NodeList getNextPreconditions() {
        return this.nextPreconditions;
    }
    
    public NodeList getState() {
        return this.currentState;
    }
    
    
    /**
     *
     * @param predicates
     */
    public void setPredicates(NodeList predicates) {
        this.currentState = predicates;
    }
    
    public void updateState(NodeList predicates) {
        //Delete dynamic predicates
        this.currentState.deleteNodeByMode(Predicate.DYNAMIC);
        
        //Add new dynamic predicates
        for (int i = 0; i < predicates.size(); i++) {
            this.currentState.add(predicates.get(i));
        }
    }

    @Override
    public int getNumberGoalsReached() {
        NodeList nodes = new NodeList();
        this.goals.getPredicates(nodes);
        return nodes.countPredicate(this.currentState);
    }
}