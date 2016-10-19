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

package org.pelea.monitoring.info.PDDL;

import java.io.BufferedWriter;
import java.io.FileWriter;
import org.pelea.monitoring.info.Info;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.pelea.languages.pddl.PDDLDomain;
import org.pelea.languages.pddl.PDDLPlan;
import org.pelea.languages.pddl.PDDLProblem;
import org.pelea.languages.pddl.comparison.PDDLComparison;
import org.pelea.languages.pddl.plan.Action;
import org.pelea.languages.pddl.plan.ActionPlan;
import org.pelea.languages.pddl.predicates.actions.ActionResult;
import org.pelea.languages.pddl.structures.NodeList;
import org.pelea.utils.Util;
import xml2pddl.XML2PDDL;

public class InfoPDDL extends Info {
    
    //private final PDDLDomain domain;
    //private final PDDLProblem problem;
    //private PDDLPlan plan;
    private PDDLComparison comparison;
    
    public InfoPDDL(String comparisonClass, String experimentName, String experimentDir) throws Exception
    {
        super("Info Monitoring PDDL", experimentName, experimentDir);
        this.domain = new PDDLDomain();
        this.problem = new PDDLProblem();
        this.plan = null;
        this.comparison = (PDDLComparison) Class.forName(comparisonClass).getConstructor().newInstance();
    }
    
    public InfoPDDL(String domain, String comparisonClass, String experimentName, String experimentDir) throws Exception
    {
        super("Info Monitoring PDDL", experimentName, experimentDir);
        this.domain = new PDDLDomain(domain);
        this.problem = new PDDLProblem();
        this.plan = null;
        this.comparison = (PDDLComparison) Class.forName(comparisonClass).getConstructor().newInstance();
    }
    
    public InfoPDDL(String domain, String problem, String comparisonClass, String experimentName, String experimentDir) throws Exception
    {
        super("Info Monitoring PDDL", experimentName, experimentDir);
        this.domain = new PDDLDomain(domain);
        this.problem = new PDDLProblem(problem);
        this.plan = null;
        this.comparison = (PDDLComparison) Class.forName(comparisonClass).getConstructor().newInstance();
    }
    
    @Override
    public void loadPlan(String plan) throws JDOMException, IOException {
        
        if (this.plan == null) {
            this.plan = new PDDLPlan(plan);
            
        }
        else {
            this.plan.updatePlan(plan);
        }
        
        this.replanning++;
        this.pool.addEpisode(this.plan.getVariables());
        this.pool.addValueEpisode("goals", this.problem.getNumberGoalsReached() + "");
    }

    /**
     * This function compare a pddl state with the current state or with the next
     * state generated after the next action of the current plan is applicates
     * @param stateH
     * @param useNextState Define the state will be used in the comparation. If it is true, it will
     * be used next state and if it is false it will be used current state.
     * @param saveState
     * @param validateState
     * @return true if both state are similar and false if they are not
     */
    @Override
    public boolean isValidState(String stateH, boolean useNextState, boolean saveState, boolean validateState)
    {
        NodeList temporal = new NodeList();
        
        boolean equalStates;
        
        if (validateState) {
            try
            {
                SAXBuilder builder      = new SAXBuilder();
                Document document       = (Document) builder.build(new StringReader(stateH));
                Element state           = document.getRootElement();
                List predicates         = ((Element) state.getChildren().get(1)).getChildren();

               for (int i = 0; i < predicates.size(); i++) {
                   temporal.add((Element) predicates.get(i));
               }
               
               equalStates = (this.comparison.getType() == PDDLComparison.EFFECTS) ? this.comparison.compare(((PDDLProblem) this.problem).getNextPreconditions(), temporal):this.comparison.compare(((PDDLProblem) this.problem).getNextState(), temporal);
               
               if (equalStates) {
                    if (saveState) {
                       ((PDDLProblem) this.problem).modifyState(temporal);
                    }
                    this.cursor++;
                    return true;
               }
               else 
                   return false;
            }
            catch (Exception e) {
               Util.printError(this.getName(), "Analizing State");
               return false;
            }
        }
        else {
            this.cursor++;
            return true;
        }
    }
    
    /**
     * This funcion generate a new state using the next action and save the new state
     * in the value newState.
     */
    @Override
    public void generateNextState()
    {
        ActionPlan ap = this.plan.getAction(this.cursor);
        
        for (int i = 0; i < ap.size(); i++) {
            Action a = ap.get(i);
            ((PDDLProblem) this.problem).generateNextState(((PDDLDomain) this.domain).generateEffectsByAction(a.getName(), a.getValues(), ((PDDLProblem) this.problem).getState()));
        }
        
        int nextAction = this.cursor + 1;
        
        if (nextAction < this.plan.getNumberOfActions()) {
            ap = this.plan.getAction(nextAction);
            
            for (int i = 0; i < ap.size(); i++) {
                Action a = ap.get(i);
                ((PDDLProblem) this.problem).generateNextPreconditions(((PDDLDomain) this.domain).generatePreconditionsByAction(a.getName(), a.getValues(), ((PDDLProblem) this.problem).getState()));
            }
        }
        else {
            ((PDDLProblem) this.problem).generateNextPreconditions(new ArrayList<ActionResult>()); 
        }
    }

    @Override
    public void deletePlan() {
        ((PDDLPlan) this.plan).deleteActions(this.cursor);
    }
    
    public void saveProblem(String dir, String name) throws Exception {
        BufferedWriter file = new BufferedWriter(new FileWriter(dir+name));
        file.write(XML2PDDL.convertProblem(this.problem.generateProblemXML(true)));
        file.close();
        System.out.println("GENERATING FILE: " + dir+name);
    }
}
