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

package org.pelea.core.module;

import java.io.StringReader;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.pelea.core.communication.Message;
import org.pelea.core.communication.Messages;
import org.pelea.core.communication.connector.CommunicationModel;
import org.pelea.core.communication.connector.ICECommunicationModel;
import org.pelea.core.communication.connector.RMICommunicationModel;
import org.pelea.core.configuration.configuration;
import org.pelea.utils.Util;

/**
 *
 * @author moises
 */
public abstract class Module {
    
    public static final long TIME = 2000;
    
    public static byte RUNNING = 1;
    public static byte WAITING = 2;
    public static byte STOPPING = 3;
    public static byte RESTARTING = 4;
    
    public static byte RMI = 1;
    public static byte ICE = 2;
    
    protected long synchronizationTime;
    protected String name;
    protected String prefix;
    protected byte mode;
    protected volatile byte state;
    
    protected CommunicationModel commumicationModel;
    
    protected volatile byte model;
    protected String PID;
    protected String masterPID;
    protected long maxTimeExecution;
    protected long startExecutionTime;
    
    protected int port;
    protected String host;
    
    public class ShuttingDownHandler extends Thread {
        public void run(CommunicationModel communicationModel) { 
            System.out.println("Control-C caught. Shutting down..."); 
            state = Module.STOPPING;
        }
    }
    
    public Module(String name, String prefix, byte mode, byte model) throws RemoteException, NotBoundException, MalformedURLException{
        //Runtime.getRuntime().addShutdownHook(new ShuttingDownHandler());
        
        this.port = Integer.parseInt(configuration.getInstance().getParameter("GENERAL", "PORT"));
        this.host = configuration.getInstance().getParameter("GENERAL", "IP");
        
        this.PID = ManagementFactory.getRuntimeMXBean().getName();
        this.mode = mode;
        this.name = name;
        this.prefix = prefix;
        this.model = model;
    
        if (this.model == Module.RMI)
            this.commumicationModel = new RMICommunicationModel(host, port, mode, (byte) Byte.parseByte(configuration.getInstance().getParameter(name, "type")), name);
        else
            this.commumicationModel = new ICECommunicationModel();
    }
    
    public Module(String name, String prefix, byte mode, byte model, String masterPID) throws NotBoundException, MalformedURLException, RemoteException {
        //Runtime.getRuntime().addShutdownHook(new ShuttingDownHandler());
        
        this.PID = ManagementFactory.getRuntimeMXBean().getName();
        this.masterPID = masterPID;
        this.mode = mode;
        this.name = name;
        this.prefix = prefix;
        this.model = model;
        
        if (this.model == Module.RMI)
            this.commumicationModel = new RMICommunicationModel(host, port, mode, (byte) Byte.parseByte(configuration.getInstance().getParameter(name, "type")), name);
        else
            this.commumicationModel = new ICECommunicationModel();    }
    
    public String getPID() {
        return this.PID;
    }
    
    public String getMasterPID() {
        return this.masterPID;
    }
    
    public void setMasterPID(String value) {
        this.masterPID = value;
    }
    
    public String getName() {   
        return this.name;
    }
    
    public void setName(String name) {   
        this.name = name;
    }
    
    public byte getMode() {
        return this.mode;
    }
    
    public long getSynchronizationTime() {
        return this.synchronizationTime;
    }
    
    public boolean error(String content) throws Exception {
        
        if (content != null) {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            SAXBuilder builder = new SAXBuilder();
            Document document = (Document) builder.build(new StringReader(content));
            Element error = (Element) document.getRootElement().getChildren().get(0);
            
            if (error.getName().toUpperCase().matches("ERROR")) {
                Util.printError(this.getName(), outputter.outputString(error));
                return true;
            }  
        }
        return false;
    }
    
    public long getMaxTimeExecution() {
        return this.maxTimeExecution;
    }
    
    public boolean isRunning() {return (this.state == RUNNING);}
    public boolean isWaiting() {return (this.state == WAITING);}
    public boolean isStopping() {return (this.state == STOPPING);}
    public boolean isRestarting() {return (this.state == RESTARTING);}
    
    public void syncronize() {
        
        this.commumicationModel.syncronize(this.name);
        
        if (this.mode == CommunicationModel.SERVER) 
            this.commumicationModel.sendGlobalMessage(new Message(this.commumicationModel.getType(), this.name, Messages.MSG_START, null));
    }
    
    public void run(long cycle) {
    
        Message recieve = null;
        Message send;
        
        this.state = Module.WAITING;
        this.startExecutionTime = System.currentTimeMillis();
        
        try {
            while (!this.isStopping()) {
            
                if (this.commumicationModel.messages()) {
                    recieve = this.commumicationModel.getMessage();
                    
                    Util.printDebug(this.getName(), recieve, 2);
                    
                    if (!this.error(recieve.getContent())) {
                        send = this.messageHandler(recieve);
                        if (send != null) this.commumicationModel.sendMessage(send);
                    }
                    else {
                        this.errorHandler();
                        this.state = Module.STOPPING;
                    }
                }
                
                try {
                    Thread.sleep(cycle);
                }
                catch (InterruptedException ie) {
                    Util.printError(this.getName(), "Waiting loop time (" + ie.toString() + ") .");
                } 
            }
            
            if (this.commumicationModel.getMode() == CommunicationModel.CLIENT)
                this.commumicationModel.sendUnRegisterMessage(this.name);        
        } 
        catch (RemoteException ex) {
            Util.printError(this.getName(), "Recieving message.");
        }
        catch (Exception ex) {
            
            Util.printError(this.getName(), ex.toString());
            
            if (this.commumicationModel.getMode() == CommunicationModel.CLIENT)
                this.commumicationModel.sendMessage(new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_ERROR, Util.generateMessageError("100", ex.toString())));
            else
                this.state = Module.STOPPING;
        }
        
        if (this.commumicationModel.getMode() == CommunicationModel.SERVER)
            this.commumicationModel.sendGlobalMessage(new Message(this.commumicationModel.getType(), this.name, Messages.MSG_STOP, null));

        
    }
    
    public abstract Message messageHandler(Message recieve) throws Exception;
    public abstract void errorHandler() throws Exception;
}
