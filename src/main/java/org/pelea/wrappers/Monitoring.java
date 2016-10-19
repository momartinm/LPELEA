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

package org.pelea.wrappers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.pelea.core.communication.Message;
import org.pelea.core.communication.Messages;
import org.pelea.core.communication.connector.CommunicationModel;
import org.pelea.core.configuration.configuration;
import org.pelea.core.module.Module;
import org.pelea.monitoring.data.Data;
import org.pelea.monitoring.info.Info;
import org.pelea.monitoring.info.PDDL.InfoPDDL;
import org.pelea.utils.Util;
import org.pelea.utils.experimenter.Experiment;
import pddl2xml.Pddl2XML;

public abstract class Monitoring extends Module {

    public static int DAY = 86400000; // 86400 seconds = 24 hours.
    public static int MAX = 1000000; //1000 seconds
    
    protected final Data data;
    protected final Info info;
    
    protected final String code;
    
    protected int answers; 
    
    protected final boolean validateState;
    protected final boolean initExecution;
    protected final boolean pddlIden;
    
    protected final String outputFile;
    protected String outputDir;
    
    protected long time;
    protected long maxTime;
    
    protected int rounds;
    protected int round;

    public Monitoring(String name, byte communicationModel) throws Exception {
        
        super(name, "MONITORING", CommunicationModel.SERVER, communicationModel);
        
        SimpleDateFormat df = new SimpleDateFormat ("HH_mm_ss_dd_MM_yyyy");
        
        int numProblems = configuration.getInstance().getParameter("GENERAL", "PROBLEM").split(",").length;
        
        this.validateState = Boolean.parseBoolean(configuration.getInstance().getParameter(this.name, "VALIDATE_STATE")); 
        this.initExecution = Boolean.parseBoolean(configuration.getInstance().getParameter(this.name, "INITEXECUTION"));
        this.pddlIden = Boolean.parseBoolean(configuration.getInstance().getParameter(this.name, "PDDL_IDENTIFICATION")); 
        this.outputDir = configuration.getInstance().getParameter("GENERAL", "OUTPUT_DIR");
        this.outputFile = this.outputDir + "results_" + df.format(new Date()) + ".xml";
        
        if (!new File(this.outputDir).exists()) {
            new File(this.outputDir).mkdir();
        }
        
        this.code = configuration.getInstance().getParameter(this.name, "EXECUTION_CODE").toUpperCase();
        this.answers = 0;   
        
        this.data = new Data(configuration.getInstance().getParameter("GENERAL", "DOMAIN"));
        this.info = (this.initExecution) ? new InfoPDDL(Pddl2XML.convertDomain(configuration.getInstance().getParameter("GENERAL", "DOMAIN")), configuration.getInstance().getParameter(this.getName(), "COMPARISON_CLASS"), configuration.getInstance().getParameter("GENERAL", "NAME"), this.outputFile):new InfoPDDL(configuration.getInstance().getParameter(this.getName(), "COMPARISON_CLASS"), configuration.getInstance().getParameter("GENERAL", "NAME"), this.outputFile);
       
        this.rounds = Integer.parseInt(configuration.getInstance().getParameter("GENERAL", "ROUNDS")) * numProblems;
        this.round = 0;
        this.maxTime = MAX; 
        this.maxTimeExecution = DAY;
    }
    
    public Monitoring(String name, byte communicationModel, String code) throws Exception {
        
        super(name, "MONITORING", CommunicationModel.SERVER, communicationModel);
        
        SimpleDateFormat df = new SimpleDateFormat ("HH_mm_ss_dd_MM_yyyy");
        
        int numProblems = configuration.getInstance().getParameter("GENERAL", "PROBLEM").split(",").length;
        
        this.validateState = Boolean.parseBoolean(configuration.getInstance().getParameter(this.name, "VALIDATE_STATE")); 
        this.initExecution = Boolean.parseBoolean(configuration.getInstance().getParameter(this.name, "INITEXECUTION"));
        this.pddlIden = Boolean.parseBoolean(configuration.getInstance().getParameter(this.name, "PDDL_IDENTIFICATION")); 
        this.outputDir = configuration.getInstance().getParameter("GENERAL", "OUTPUT_DIR").trim();
        this.outputDir = this.outputDir.substring(0, this.outputDir.length()-1) + code + "/";
        this.outputFile = this.outputDir + "results_" + df.format(new Date()) + ".xml";

        if (!new File(this.outputDir).exists()) {
            new File(this.outputDir).mkdir();
        }
        
        this.code = configuration.getInstance().getParameter(this.name, "EXECUTION_CODE").toUpperCase();
        this.answers = 0;
        
        this.data = new Data(configuration.getInstance().getParameter("GENERAL", "DOMAIN"));
        this.info = (this.initExecution) ? new InfoPDDL(Pddl2XML.convertDomain(configuration.getInstance().getParameter("GENERAL", "DOMAIN")), configuration.getInstance().getParameter(this.getName(), "COMPARISON_CLASS"), configuration.getInstance().getParameter("GENERAL", "NAME"), this.outputFile):new InfoPDDL(configuration.getInstance().getParameter(this.getName(), "COMPARISON_CLASS"), configuration.getInstance().getParameter("GENERAL", "NAME"), this.outputFile);
       
        this.rounds = Integer.parseInt(configuration.getInstance().getParameter("GENERAL", "ROUNDS")) * numProblems;
        this.round = 0;
        this.maxTime = MAX; 
        this.maxTimeExecution = DAY;
    }
    
    protected abstract void solveProblem(Message msg) throws Exception;
    protected abstract void processState(Message msg) throws Exception;
    protected abstract boolean executeAction() throws Exception;
    protected abstract void processPlan(Message msg) throws Exception;
    protected abstract void translateState(Message msg) throws Exception;
    
    protected void initRound() {
        //Message send = new Message(this.commumicationModel.getType(), Messages.NODE_EXECUTION, Messages.MSG_GETSENSORS, null);
        //this.answers = ((RMIConnector) this.commumicationModel).sendMessage(send);
    }
    
    protected void finishRound() throws Exception {
        
        this.round++;
        
        //Save result and finish execution
        Util.printDebug(this.name, "ROUND: " + this.round);
        Util.printDebug(this.name, "ROUNDS: " + this.rounds);
        
        if (this.round >= this.rounds) {                        
            this.info.saveExperiments();
            this.state = Module.STOPPING;
        }
        else {
            this.answers = 0; //Reset answers from execution 
            Message send = new Message(this.commumicationModel.getType(), Messages.NODE_EXECUTION, Messages.MSG_FINISH, null);
            this.commumicationModel.sendMessage(send);
        }
    }
    
    protected boolean waitingAnswers() {
        return (this.answers != 0);
    }
    
    protected boolean matches(List<String> idems, String name) {
        for (String idem : idems) {
            if (idem.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Message messageHandler(Message recieve) throws Exception {
        
        this.time = System.currentTimeMillis();
        
        switch (recieve.getTypeMsg()) {
            case Messages.MSG_GETSENSORS_RES:
                
                this.answers--;
                if (!this.waitingAnswers()) {
                    this.processState(recieve);
                }
                break;
            case Messages.MSG_GETPLANINFO_RES:
                this.processPlan(recieve);
                break;
            case Messages.MSG_REPAIRORPEPLAN_RES:
                this.processPlan(recieve);
                break;
            case Messages.MSG_SOLVEPROBLEM:
                this.solveProblem(recieve);
                break;
            case Messages.MSG_TRANSLATEHTL_RES:
                this.translateState(recieve);
                break;
            case Messages.MSG_TRANSLATELTH_RES:
                this.translateState(recieve);
                break;
        }
        
        if ((System.currentTimeMillis() - this.time) > this.maxTime) {
            this.info.finishExperiment(Experiment.BLOCK);
            this.finishRound();
        }
        
        if ((System.currentTimeMillis() - this.startExecutionTime) > this.maxTimeExecution) {
            this.info.finishExperiment(Experiment.OUTOFTIME);
            this.finishRound();
        }
        
        return null;    
    }

    @Override
    public void errorHandler() throws Exception {
        
        this.info.finishExperiment(Experiment.ERROR);
        this.finishRound();
        
    }
    
}
