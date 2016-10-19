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

package org.pelea.core.module.basic;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.util.ArrayList;
import org.jdom.JDOMException;
import org.pelea.core.communication.*;
import org.pelea.core.module.Module;
import org.pelea.languages.pddl.plan.ActionPlan;
import org.pelea.monitoring.info.PDDL.InfoPDDL;
import org.pelea.response.Response;
import org.pelea.utils.Util;
import org.pelea.utils.experimenter.Experiment;
import org.pelea.wrappers.Monitoring;

/**
 *
 * @author moises
 */
public class MonitoringBasic extends Monitoring
{
    //private final boolean useHorizon;
    
    public MonitoringBasic(String name) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotBoundException, MalformedURLException, RemoteException, Exception {
        super(name, Module.RMI);
    }
    
    public MonitoringBasic(String name, String masterPID) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotBoundException, MalformedURLException, RemoteException, Exception {
        super(name, Module.RMI, masterPID);
    }
    
    @Override
    public void solveProblem(Message msg) throws RemoteException, IOException, JDOMException
    {
        Experiment experiment = new Experiment();
        
        this.info.addExperiment(experiment);

        Response res = new Response(msg.getContent());
        
        this.info.resetPlan(); //Remove last plan
        this.info.loadState(res.getNode("problem", false));
        this.data.setStateH(this.info.getProblemXML());
        this.info.loadDomain(res.getNode("domain", false));
        //this.data.setDomainH(this.info.getDomainXML());
        
        Message send = new Message(this.commumicationModel.getType(), Messages.NODE_DECISSIONSUPPORT, Messages.MSG_GETPLANINFO, this.data.buildXML(true, false, true, false, false, false, false));
        this.commumicationModel.sendMessage(send);
    }

    @Override
    public void processState(Message msg) throws Exception
    {
        Response res = new Response(msg.getContent());

        if (this.info.isPlan())
        {
            this.info.generateNextState();
            
            //Compare new state for environment with the newState generated previosly.
            if ((this.info.isValidState(res.getNode("state", false), true, true, this.validateState))) {
                this.info.executeAction();

                //Save new state as current state
                this.data.setStateH(this.info.getProblemXML());

                if (this.info.moreActions()) {
                    
                    if (this.info.goalsReached()){
                        Util.printDebug(this.getName(), "Problem Solved");
                        this.info.finishExperiment(Experiment.SOLVED);
                        this.finishRound();
                    }
                    else
                        this.executeAction();
                }
                else {
                    if (this.info.goalsReached()){
                        Util.printDebug(this.getName(), "Problem Solved");
                        this.info.finishExperiment(Experiment.SOLVED);
                        this.finishRound();
                    }
                    else {
                        Util.printDebug(this.getName(), "Replanning 2");
                        
                        this.data.setStateH(this.info.getProblemXML());
                        this.data.setPlanH(this.info.getPlanXML(this.info.actionsExecuted()-1));
                        ((InfoPDDL) this.info).saveProblem(this.outputDir, "problem" + this.info.getReplanningSteps() + ".pddl");

                        
                        //Reset expected answers
                        this.answers = 0;
                        
                        //Delete wrong actions
                        this.info.deletePlan();
                        
                        //Generate a new plan of actions
                        Message send = new Message(this.commumicationModel.getType(), Messages.NODE_DECISSIONSUPPORT, Messages.MSG_REPAIRORPEPLAN, this.data.buildXML(true, false, true, false, true, false, false));
                        this.commumicationModel.sendMessage(send);
                    }
                }
            }
            else {
                //Load real state
                this.info.loadState(res.getNode("state", false));
                this.data.setStateH(this.info.getProblemXML());
                this.data.setPlanH(this.info.getPlanXML(this.info.actionsExecuted()));
                ((InfoPDDL) this.info).saveProblem(this.outputDir, "problem" + this.info.getReplanningSteps() + ".pddl");
                
                Util.printDebug(this.getName(), "Replanning 1");

                //Reset expected answers
                this.answers = 0;

                //Delete wrong actions
                this.info.deletePlan();

                //Generate a new plan of actions
                Message send = new Message(this.commumicationModel.getType(), Messages.NODE_DECISSIONSUPPORT, Messages.MSG_REPAIRORPEPLAN, this.data.buildXML(true, false, true, false, true, false, false));
                this.commumicationModel.sendMessage(send);
            }
        }
        else
        { 
            //Initial state sended by execution nodes, it is loaded as initial state.
            this.info.loadState(res.getNode("state", false));  
            Message send = new Message(this.commumicationModel.getType(), Messages.NODE_DECISSIONSUPPORT, Messages.MSG_GETPLANINFO, this.data.buildXML(true, false, true, false, false, false, false));
            /*this.answers = */this.commumicationModel.sendMessage(send);
        }
    }
    
    @Override
    public boolean executeAction() throws RemoteException
    {                              
        //ArrayList references    = (ArrayList) this.me.getReferenceListByType(messages.NODE_EXECUTION);
        ActionPlan ap = this.info.getNextAction();
        Message send = null;
        
        for (int i = 0; i < ap.size(); i++) {
            if (this.pddlIden) {
                ArrayList<String> idems = ap.get(i).getValuesByType(this.code);
                send = new Message(this.commumicationModel.getType(), Messages.NODE_EXECUTION, Messages.MSG_EXECUTEPLAN, ap.getXmlByPosition(i));
                
                this.answers += this.answers = this.commumicationModel.sendMessage(send, idems);
            }
            else {
                send = new Message(this.commumicationModel.getType(), Messages.NODE_EXECUTION, Messages.MSG_EXECUTEPLAN, ap.getXmlByPosition(i));
                this.answers += this.answers = this.commumicationModel.sendMessage(send);
            }
        }
            
        return true;                                       
    } 
    
    @Override
    public void processPlan(Message msg) throws Exception
    {
        Response res = new Response(msg.getContent());
        
        //Loading plan of actions in info structure to analyze the execution of the accions
        
        this.info.loadPlan(res.getNode("plans", false));
        this.data.setPlanH(res.getNode("plans", false));

        if (this.info.moreActions()) {
            this.executeAction();
        }
        else {
            Util.printDebug(this.getName(), "No more actions");
            this.info.finishExperiment(Experiment.DEADEND);
            this.finishRound();
        }
    }

    @Override
    protected void translateState(Message msg) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}