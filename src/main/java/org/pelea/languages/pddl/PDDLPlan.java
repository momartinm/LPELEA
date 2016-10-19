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

import org.pelea.languages.Plan;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.pelea.languages.pddl.plan.ActionPlan;

/**
 *
 * @author moises
 */
public class PDDLPlan extends Plan {
    private static final int NUMPLANS = 1;
    private final ArrayList<ActionPlan> actions;
    private HashMap<String, String> variables;
    
    private int actionAdded;
    
    public PDDLPlan(String xml) throws JDOMException, IOException {
        this.actions = new ArrayList();
        this.variables = new HashMap();
        this.loadPlanFromXML(xml);
    }
    
    private void loadPlanFromXML(String xml) throws JDOMException, IOException {
        
        SAXBuilder builder = new SAXBuilder();
        Document document = (Document) builder.build(new StringReader(xml));
        Element plan = (Element) document.getRootElement();
        List plans = plan.getChildren();
     
        this.actionAdded = 0;
        this.name = plan.getAttributeValue("domain").trim();
        
        for (int k = 0; k < 1; k++)
        {
            Element p = ((Element) plans.get(k));
            List<Attribute> attrs  = p.getAttributes();
            
            for (Attribute attr : attrs) {
                this.variables.put(attr.getName(), attr.getValue());
            }
            
            List planActions = p.getChildren();
            
            for (Object planAction : planActions) {
                
                ActionPlan a    = new ActionPlan();
                List nodes = ((Element) planAction).getChildren();
                
                for (Object node : nodes) {
                    a.addAction((Element) node);
                }
                
                this.actions.add(a);
                this.actionAdded++;
            }
        }
    }
    
    @Override
    public void addVariable(String variable, String value) {
        this.variables.put(variable, value);
    }
    
    @Override
    public String getActionXML(int position)
    {
        return this.actions.get(position).getXml();
    }
    
    @Override
    public ActionPlan getAction(int position)
    {
        return this.actions.get(position);
    }
    
    public void deleteActions(int position)
    {   
        while (position < this.actions.size()) {
            this.actions.remove(position);
        }
    }
    
    @Override
    public void deleteAction(int position) {
        if (position < this.actions.size()) {
            this.actions.remove(position);
        }
    }
    
    @Override
    public void deletePlan() {
        this.deleteActions(0);
    }
    
    @Override
    public String generateXML() {
        return this.generateXML(0);
    }
    
    @Override
    public String generateXML(int position) {
        String code = "";
        
        code += "<plans name=\"xPddlPlan\" domain=\"" + this.name + "\">";
	code += "<plan time=\"" + this.time + "\">";
        
        for (Map.Entry entry : this.variables.entrySet()) {
            code += " " + entry.getKey() + "=\"" + this.time + "\"";
        }
        
        for (int i = position; i < this.actions.size(); i++) {
            code += this.actions.get(i).getXml();
        }
	
        code += "</plan>";
        code += "</plans>";
        
        return code;
    }
    
    @Override
    public int getNumberOfActions() {
        int numActions = 0;

        for (ActionPlan action : this.actions) {
            numActions += action.size();
        }
        return numActions;
    }
    
    public int getNumberOfActionAdded() {
        return this.actionAdded;
    }
    
    @Override
    public int updatePlan(String xml) throws JDOMException, IOException {
        this.loadPlanFromXML(xml);
        return 0;
        //return new String[]{Double.toString(this.time), Integer.toString(this.actions.size())};
    }
    
    public String generatePDDLPlan() throws IOException {
        
        String plan = "";
        
        for (int i = 0; i < this.actions.size(); i++) {
            List<String> pddlActions = this.actions.get(i).getPDDLActions();
                
            for (java.lang.String pddlAction : pddlActions) {
                plan += i + ": " + pddlAction + "\n";
            }
        }
        
        return plan;
    }
    
    public static String convert(String xml) throws JDOMException, IOException {
        try {
            PDDLPlan p = new PDDLPlan(xml);
            String plan = p.generatePDDLPlan();
            p.finalize();
            return plan;
        } catch (Throwable ex) {
            return "";
        }
    }
    
    @Override
    public String getValue(String key) {
        return this.variables.get(key);
    }

    @Override
    public HashMap getVariables() {
        return this.variables;
    }
}
