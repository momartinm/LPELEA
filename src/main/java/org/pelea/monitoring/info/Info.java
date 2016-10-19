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

package org.pelea.monitoring.info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.JDOMException;
import org.pelea.languages.Domain;
import org.pelea.languages.Plan;
import org.pelea.languages.Problem;
import org.pelea.languages.pddl.plan.ActionPlan;
import org.pelea.utils.experimenter.Experiment;
import org.pelea.utils.experimenter.ExperimentPool;

public abstract class Info {
    
    private final String name;
    private final String fileName;
    
    protected Domain domain;
    protected Problem problem;
    protected Plan plan;
    //private Comparison comparison;
    
    protected ExperimentPool pool;
    protected int cursor;
    protected List<String> values;
    
    protected int replanning;
    
    public Info(String name, String experimentName, String dir) {
        this.name = name;
        this.fileName = dir;
        this.cursor = 0;
        this.values = new ArrayList<String>();
        this.pool = new ExperimentPool(experimentName);
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final String getFileName() {
        return this.fileName;
    }
    
    public abstract void loadPlan(String plan) throws JDOMException, IOException;
    public abstract boolean isValidState(String stateH, boolean useNextState, boolean saveState, boolean validateState);
    public abstract void generateNextState();
    public abstract void deletePlan();
    
    /**
     * This function return the next action that will be executed. 
     * @return The next action of the plan
     */
    public ActionPlan getNextAction() {
        return this.plan.getAction(this.cursor);
    }
    
    public boolean goalsReached() {
        return this.problem.goalsReached();
    }
    
    public void resetPlan() {
        this.cursor = 0;
        this.plan = null;
    }
    
    public boolean isPlan() {
        return ((this.plan != null) && (this.plan.getNumberOfActions() > 0));
    }
    
    public String getDomainName() {
        return this.domain.getName();
    }
    
    public String getDomainXML() {
        return this.domain.generateDomainXML(false);
    }
    
    public String getProblemName() {
        return this.problem.getName();
    }
    
    public String getProblemXML()
    {
        return this.problem.generateProblemXML(false);
    }
    
    public String getPlanXML() {
        return this.plan.generateXML();
    }
    
    public String getPlanXML(int position) {
        return this.plan.generateXML(position);
    }
    
    public boolean moreActions() {
        return (this.plan != null) ?  (this.cursor < this.plan.getNumberOfActions()):false;
    }
    
    /**
     * This function return the number of actions that has been executed and monitoring
     * in a simulated environment.
     * @return Return the number of actions that has been executed correctly. 
     */
    public int actionsExecuted() {
        return this.cursor;
    }
    
        public void addExperiment(Experiment experiment) {
        this.pool.addExperiment(experiment);
    }
    
    public void executeAction() {
        this.pool.executeAction();
    }
    
    public void finishExperiment(int result) {
        this.pool.finishExperiment(result);
    }
    
    public void generateExperimentFile(String fileName) throws IOException {
        this.pool.saveXML(fileName, this.domain.getName(), this.problem.getName());
    }
    
    public void loadState(String state) throws IOException, JDOMException {
        this.problem.loadState(state);
    }      
    
    public void loadDomain(String domain) {
        this.domain.loadDomain(domain);
    }
    
    public void saveExperiments() throws IOException {
        this.pool.saveXML(this.fileName, this.domain.getName(), this.problem.getName());
    }
    
    public int getReplanningSteps() {
        return this.replanning;
    }
}
