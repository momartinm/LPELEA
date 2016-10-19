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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.pelea.core.communication.Message;
import org.pelea.core.communication.Messages;
import org.pelea.core.configuration.configuration;
import org.pelea.core.module.Module;
import org.pelea.core.module.basic.error.ErrorHandler;
import org.pelea.languages.pddl.PDDLProblem;
import org.pelea.languages.pddl.structures.NodeList;
import org.pelea.languages.pddl.structures.nodes.Fluent;
import org.pelea.languages.pddl.structures.nodes.Node;
import org.pelea.languages.pddl.structures.nodes.Predicate;
import org.pelea.response.Response;
import org.pelea.utils.Util;
import org.pelea.wrappers.Execution;
import pddl2xml.Pddl2XML;

/**
 *
 * @author MoisÃ©s MartÃ­nez
 */
public class MDPExecution extends Execution
{
    private final int BUFFER = 131072;
    
    private Socket connection;
    private PrintStream out;
    private BufferedReader in;
    
    private final long connectingTime;
    private final long waitingTime = 1000;
    
    private final String mdpsim; 
    private final int mdpsimPort;
    
    private final ErrorHandler errors;
    private final long timeLimit; 
    
    private PDDLProblem actualState;
    
    private final int problemRounds;
    private int rounds;
    private int round;
    
    private int allowedTime;
    private int time;
    private int allowedTurns;
    private int turn;
    
    private int timeSpend;
    private int turnsUsed;
    private int problem;
    private final String problems[];
    
    private boolean goalsReached;
    private final String log;
    
    private boolean error;
            
    public MDPExecution(String name) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotBoundException, MalformedURLException, RemoteException, Exception
    {
        super(name,Module.RMI);
   
        int horizons = (configuration.getInstance().getParameter("GENERAL", "HORIZON") != null) ? configuration.getInstance().getParameter("GENERAL", "HORIZON").split(",").length:1;
        
        this.problem = 0;
        this.problems = configuration.getInstance().getParameter("GENERAL", "PROBLEM").split(",");
        
        this.timeLimit = 1000000000;
        this.rounds = Integer.parseInt(configuration.getInstance().getParameter("GENERAL", "ROUNDS")) * this.problems.length * horizons;
        this.problemRounds = Integer.parseInt(configuration.getInstance().getParameter("GENERAL", "ROUNDS")) * horizons;
        this.round = 0;
        
        this.mdpsimPort = Integer.parseInt(configuration.getInstance().getParameter(this.name, "PORT"));
        this.mdpsim = configuration.getInstance().getParameter(this.name, "MDPSIM");
        this.connectingTime = Integer.parseInt(configuration.getInstance().getParameter(this.name, "MDPSIMTIME"));
        
        this.error = false;
        this.errors = (ErrorHandler) (Class.forName(configuration.getInstance().getParameter(this.name, "ERRORCLASS")).getConstructor()).newInstance();
         
        this.turn = 0;
        this.timeSpend = 0;
        this.turnsUsed = 0;
        this.goalsReached = false;
    
        this.actualState = new PDDLProblem(Pddl2XML.convertDomain(problems[0]));
        this.log = configuration.getInstance().getParameter("GENERAL", "TEMP_DIR");
        this.restartMDPSim(1000);
    }
    
    public MDPExecution(String name, String code) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotBoundException, MalformedURLException, RemoteException, Exception
    {
        super(name,Module.RMI);
   
        int horizons = (configuration.getInstance().getParameter("GENERAL", "HORIZON") != null) ? configuration.getInstance().getParameter("GENERAL", "HORIZON").split(",").length:1;
        
        this.problem = 0;
        this.problems = configuration.getInstance().getParameter("GENERAL", "PROBLEM").split(",");
        
        this.timeLimit = 1000000000;
        this.rounds = Integer.parseInt(configuration.getInstance().getParameter("GENERAL", "ROUNDS")) * this.problems.length * horizons;
        this.problemRounds = Integer.parseInt(configuration.getInstance().getParameter("GENERAL", "ROUNDS")) * horizons;
        this.round = 0;
        
        this.mdpsimPort = Integer.parseInt(configuration.getInstance().getParameter(this.name, "PORT"));
        this.mdpsim = configuration.getInstance().getParameter(this.name, "MDPSIM");
        this.connectingTime = Integer.parseInt(configuration.getInstance().getParameter(this.name, "MDPSIMTIME"));
        
        this.error = false;
        this.errors = (ErrorHandler) (Class.forName(configuration.getInstance().getParameter(this.name, "ERRORCLASS")).getConstructor()).newInstance();
         
        this.turn = 0;
        this.timeSpend = 0;
        this.turnsUsed = 0;
        this.goalsReached = false;
   
        this.actualState = new PDDLProblem(Pddl2XML.convertDomain(problems[0]));
        this.log = "./experiment" + code + "/"; 
        this.restartMDPSim(1000);
    }
    
    private void startMDPSim(int numRounds) throws IOException, Exception {
        
        String command = this.mdpsim + " --port=" + this.mdpsimPort + " --round-limit=" + numRounds + " --log-dir=" + this.log + " --time-limit=" + this.timeLimit + " " + configuration.getInstance().getParameter("GENERAL", "PPDDL_DOMAIN");

        for (String problemTemp : this.problems) {
            command += " " + problemTemp; 
        }
        
        Runtime.getRuntime().exec(command);
    
        Util.printDebug(this.name, "Starting MDPSim");
    }
    
    private void stopMDPSim() throws IOException, InterruptedException {

        String filename = configuration.getInstance().getParameter("GENERAL", "TEMP_DIR") + "killMPDSim.sh";
        
        BufferedWriter file = new BufferedWriter(new FileWriter(filename));
        file.write("#!/bin/bash" + System.getProperty("line.separator"));
        file.write("variable=`ps -ef | grep mdpsim | awk 'NR==1{print $2}'`" + System.getProperty("line.separator"));
        file.write("kill -9 $variable" + System.getProperty("line.separator"));
        file.close();
        
        Process p = Runtime.getRuntime().exec("sh " + filename);        
        p.waitFor();
        
        new File(filename).delete();
    }
    
    private void restartMDPSim(int rounds) throws IOException, InterruptedException, Exception {
        this.stopMDPSim();
        this.startMDPSim(rounds);
    }
    
    private boolean analyzeResponse(String response) throws InterruptedException {
        boolean find;
        int position;
        
        try 
        {
            SAXBuilder builder      = new SAXBuilder();
            Document document       = (Document) builder.build(new StringReader(response));
            List<Element> nodes     = document.getRootElement().getChildren();
            
            if (nodes.get(0).getName().matches("error")) {
                return true;
            }
            
            if (nodes.get(0).getName().matches("session-init")) {
                Element setting = nodes.get(0).getChild("setting");
                
                if (setting != null)
                {
                    //this.rounds         = Integer.parseInt(setting.getChild("rounds").getText());
                    this.allowedTurns   = Integer.parseInt(setting.getChild("allowed-turns").getText());
                    this.allowedTime    = Integer.parseInt(setting.getChild("allowed-time").getText());
                    return false;
                }
                else
                    return true;
            }
            
            if (nodes.get(0).getName().matches("round-init")) {
                this.round  = Integer.parseInt(nodes.get(0).getChild("round").getText());
                this.time   = Integer.parseInt(nodes.get(0).getChild("time-left").getText());
                
                if (nodes.size() > 1) {
                
                    Element state = nodes.get(1);
                    
                    if (state.getName().matches("state"))
                    {
                        List<String> names  = new ArrayList<String>();
                        String code         = "";

                        for (Element atom : (List<Element>) state.getChildren()) 
                        {
                            find     = false;
                            position = 0;

                            if (atom.getName().matches("atom"))
                                code = "predicate";
                            else if (atom.getName().matches("fluent"))
                                code = "function";

                            while ((!find) && (position < names.size())) {
                                if (names.get(position).matches(atom.getChildText(code)))
                                    find = true;
                                position++;
                            }

                            if (!find) names.add(atom.getChildText(code));
                        }

                        this.actualState.defineDynamicPredicates(names);
                    }
                    else
                        return true;

                    this.state = Module.RUNNING;
                    
                    return false;
                }
                else
                    return false;
            }
            
            if (nodes.get(0).getName().matches("state")) {
                NodeList predicates = new NodeList();
                
                for (int i = 0; i < nodes.get(0).getChildren().size(); i++) {
                    Element atom = ((List<Element>) nodes.get(0).getChildren()).get(i);
                    
                    if (atom.getName().matches("atom")) {
                        
                        Predicate p = new Predicate(atom.getChildText("predicate"), Predicate.DYNAMIC);

                        List<Element> values = atom.getChildren("term");

                        for (Element term : values) {
                            p.addValue(term.getText());
                        }

                        predicates.add(p);
                    }
                    else if (atom.getName().matches("fluent")) {
                        predicates.add(new Fluent(Node.DYNAMIC, 7, Fluent.ASSIGN, atom.getChildText("function"), atom.getChildText("value")));
                    }
                }
                
                this.actualState.updateState(predicates);
                return false;
            }
            
            if (nodes.get(0).getName().matches("end-round")) {
                this.timeSpend      = Integer.parseInt(nodes.get(0).getChild("time-spent").getText());
                this.turnsUsed      = Integer.parseInt(nodes.get(0).getChild("turns-used").getText());
                
                this.goalsReached   = (nodes.get(0).getChild("goal-reached") != null);
                
                //System.out.println(response);
                
                if (this.goalsReached) {
                    
                    Element state = nodes.get(0).getChild("state");
                    
                    NodeList predicates = new NodeList();
                    
                    List<Element> atoms = state.getChildren();
                    
                    for (Element atom : atoms) {
                        
                        if (atom.getName().matches("atom")) {

                            Predicate p = new Predicate(atom.getChildText("predicate"), Predicate.DYNAMIC);
                            List<Element> values = atom.getChildren("term");

                            for (Element term : values) {
                                p.addValue(term.getText());
                            }

                            predicates.add(p);
                        }
                        else if (atom.getName().matches("fluent")) {
                            predicates.add(new Fluent(7, Fluent.ASSIGN, atom.getChildText("function"), atom.getChildText("value")));
                        }
                    }
                    this.actualState.updateState(predicates);
                    Util.printDebug(this.name, "GOALS REACHED BY TURN " + this.round);
                }
                else {
                    return true;
                }
                
                return false;
            }
        } 
        catch (JDOMException ex) 
        {
            Logger.getLogger(MDPExecution.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MDPExecution.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return false;
    }
    
    private boolean initSession(String experimentName, String problemName) {
        
        long time = System.currentTimeMillis();
        
        while ((System.currentTimeMillis() - this.time) > this.connectingTime) {
            try {
                this.connection = new Socket(configuration.getInstance().getParameter(this.name, "IP") , Integer.parseInt(configuration.getInstance().getParameter(this.name, "PORT")) );
                this.out = new PrintStream(this.connection.getOutputStream());
                this.in = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));

                String xml      = "";
                char[] buffer   = new char[BUFFER];

                this.out.print(this.generateSession(experimentName, problemName));
                this.out.flush();

                int bytes = this.in.read(buffer);

                if (bytes > 0) {
                    xml = generateDocument(new String (buffer, 0, bytes));
                }
                else
                {            
                    xml += "<error>";
                    xml += "Error getting response from MDPSim";
                    xml += "</error>";
                }

                return this.analyzeResponse(xml);
            } 
            catch (Exception ex) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MDPExecution.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        
        return false;
    }
    
    private void finishSession() {
        try {
            this.connection.close();
        } catch (IOException ex) {
            Logger.getLogger(MDPExecution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean startRound() throws InterruptedException {
        String xml = "<round-request/>\0";
        
        try {
           
            char[] buffer   = new char[BUFFER];
                
            this.out.print(xml);
            this.out.flush();
            
            Thread.sleep(this.waitingTime);
            
            int bytes = this.in.read(buffer);

            if (bytes > 0) {
                xml = generateDocument(new String (buffer, 0, bytes));
            }
            else {     
                xml += "<error>";
                xml += "Error starting round";
                xml += "</error>";
            }
            
            return this.analyzeResponse(xml);
        } 
        catch (IOException ex) {
            return false;
        }
    }
    
    private boolean finishRound() {
        String xml = "<done/>\0";
        
        this.out.print(xml);
        this.out.flush();
            
        return true;
    }
    
    
    private String generateDocument(String response) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
        
        xml += "<response>";
        xml += response.replaceAll("[\n\r]","");
        xml += "</response>";
        
        return xml;
    }
    
    private String generateSession(String experimentName, String problemName) {
        String xml;
        
        xml  = "<session-request>";
        xml += "<name>" + experimentName  + "</name>";
        xml += "<problem>" + problemName + "</problem>";
        xml += "</session-request>";
            
        return xml;
    }
    
    
    private String generateAction(String action) throws JDOMException, IOException {
        SAXBuilder builder  = new SAXBuilder();
        String xml          = "";
        
        Document document   = (Document) builder.build(new StringReader(action));
        Element node        = document.getRootElement();
            
        ArrayList errorData = this.errors.generateDataError(this.actualState.getPredicates(), action);
        
        if (node != null) {
            xml += "<act>";
            xml += "<action>";
            xml += "<name>" + node.getAttributeValue("name").toLowerCase() + "</name>";

            List<Element> childrens = node.getChildren();
                
            for (Element children : childrens) 
            {
                xml += "<term>" + children.getAttributeValue("name").toLowerCase() + "</term>";
            }
            
            for (int i = 0; i < errorData.size(); i++) {
                xml += "<term>" + errorData.get(i) + "</term>";
            }

            xml += "</action>";
            xml += "</act>";
         }
        
        //System.out.println(xml);
            
        return xml;
    }
    
    public String generateState(String response) {
        String xml = "";
        
        return xml;
    }

    @Override
    public void executePlan(String planL) {
	try 
        {
            XMLOutputter outputter  = new XMLOutputter(Format.getPrettyFormat());
            SAXBuilder builder      = new SAXBuilder();
            Document document       = (Document) builder.build(new StringReader(planL));
            Element plan            = ((Element) document.getRootElement());        
            List actions            = plan.getChildren();
        
            for (int i = 0; i < actions.size(); i++) {
                this.executeAction(outputter.outputString((Element) actions.get(i)));
            }
        
        } 
        catch (IOException e) {
           Util.printError(this.getName(), "Executing plan of actions");
	} catch (JDOMException e) {
            Util.printError(this.getName(), "Executing plan of actions");
        }
    }
    
    private String execute(String actionL) throws JDOMException, InterruptedException, IOException {
        
        String xml      = "";
        char[] buffer   = new char[BUFFER];

        Util.printDebug(this.getName(), actionL);
        
        this.out.print(this.generateAction(actionL));
        this.out.flush();
            
        Thread.sleep(this.waitingTime);

        int bytes = this.in.read(buffer);

        if (bytes > 0) {
            xml = generateDocument(new String (buffer, 0, bytes));
        }
        else {
            xml += "<error>";
            xml += "Error getting answer from MDPSim";
            xml += "</error>";
        }
        
        return xml;
    }

    @Override
    public void executeAction(String actionL) {
        try {       
            this.error = this.analyzeResponse(this.execute(actionL));
        } catch (JDOMException ex) { 
            Logger.getLogger(MDPExecution.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MDPExecution.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MDPExecution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void executeAction(double d, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSensors() {
        String xml    = ""; 

        if (!this.error) {
            xml += this.actualState.generateStateXML(true);
            //Util.printError(this.getName(), xml);
        }
        else {
            xml = Util.generateMessageError("51", "Error getting actual state");
            this.error = false;
        }
        
        return xml;
    }

    @Override
    public String getSensorsWithTime(double d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getLastTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String solveProblem() {
        String xml;
        Response res;
        
        try {
            
            this.actualState = new PDDLProblem(Pddl2XML.convertDomain(this.problems[this.problem]));
            
            if (this.round == 0){
                
                if (this.problem > 0)
                    this.finishSession();
                    
                this.initSession(this.actualState.getName() + this.round+1, this.actualState.getName());
            }
            res = new Response(Pddl2XML.convertDomain(configuration.getInstance().getParameter("GENERAL", "DOMAIN")));
            res.addNode("problem", this.actualState.generateProblemXML(false));
            
            xml = res.generateResponse();
            
            Util.printDebug(this.name, "STARTING SOLVING PROBLEM " + this.actualState.getName());
            this.startRound();
        } 
        catch (Exception ex) {
            
            //System.out.println(ex.toString());
            
            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
            xml += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
            xml += "<error>";
            xml += "Error getting actual state";
            xml += "</error>";
            xml += "</define>";
        }
        
        return xml;
    }

    @Override
    public Message messageHandler(Message recieve) throws Exception {
        switch (recieve.getTypeMsg())
        {
            case Messages.MSG_START: 
                this.state = Module.RUNNING;
                if (this.initExecution) {
                    return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_SOLVEPROBLEM, this.solveProblem());
                }
                break;
            case Messages.MSG_STOP:
                if (this.isRunning()) this.state = Module.STOPPING;
                    this.stopMDPSim();
                break;
            case Messages.MSG_EXECUTEPLAN: 
                if (this.isRunning()) {
                    this.executePlan(recieve.getContent());
                    if (this.error)
                        return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_GETSENSORS_RES, Util.generateMessageError("55", "Action cannot be executed."));
                    else
                        return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_GETSENSORS_RES, this.getSensors());
                }
                break;
            case Messages.MSG_EXECUTEACTION: 
                if (this.isRunning()) {
                    this.executeAction(recieve.getContent());
                    return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_GETSENSORS_RES, this.getSensors());
                }  
                break;
            case Messages.MSG_GETSENSORS: 
                if (!this.isWaiting()) 
                    this.startRound();
                return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_GETSENSORS_RES, this.getSensors());
            case Messages.MSG_FINISH:
                
                if (this.round % this.problemRounds == 0) {                   
                    this.problem++;
                }
                
                if (this.problem < this.problems.length) 
                    return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_SOLVEPROBLEM, this.solveProblem());
                
                break;
        }
        return null;
    }

    @Override
    public void errorHandler() {
    }
}
