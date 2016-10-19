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

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import org.pelea.core.communication.Message;
import org.pelea.core.communication.Messages;
import org.pelea.core.communication.connector.CommunicationModel;
import org.pelea.core.configuration.configuration;
import org.pelea.core.module.Module;

public abstract class Execution extends Module { 
    protected final boolean initExecution;
    protected final boolean executionMode;

    public Execution(String name, byte communicationModel) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotBoundException, MalformedURLException, RemoteException {
        
        super(name, "EXECUTION", CommunicationModel.CLIENT, communicationModel);
        
        this.initExecution = Boolean.parseBoolean(configuration.getInstance().getParameter(this.name, "INITEXECUTION"));     
        this.executionMode = Boolean.parseBoolean(configuration.getInstance().getParameter(this.name, "EXECUTIONMODE"));      
    }

    public abstract void executePlan(String planL);
    public abstract void executeAction(String actionL);
    public abstract void executeAction(double time, String action);
    public abstract String getSensors();
    public abstract String getSensorsWithTime(double instant_time);
    public abstract double getLastTime();
    public abstract String solveProblem();
    
    @Override
    public Message messageHandler(Message recieve) throws Exception {
        
        switch (recieve.getTypeMsg()) {
            case Messages.MSG_START: 
                if (this.initExecution) {
                    return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_SOLVEPROBLEM, this.solveProblem());
                }
                return null;
            case Messages.MSG_STOP:
                this.state = Module.STOPPING;
                return null;
            case Messages.MSG_EXECUTEPLAN: 
                this.executePlan(recieve.getContent());
                return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_GETSENSORS_RES, this.getSensors());
            case Messages.MSG_EXECUTEACTION: 
                this.executeAction(recieve.getContent());
                return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_GETSENSORS_RES, this.getSensors());
            case Messages.MSG_GETSENSORS: 
                return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_GETSENSORS_RES, this.getSensors());
        }
        return null;
    }

    @Override
    public void errorHandler() throws Exception {
    }
}
