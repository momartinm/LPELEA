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

package org.pelea.core.communication.connector;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import org.pelea.core.communication.Message;
import org.pelea.core.communication.Messages;
import org.pelea.core.communication.RMI.RMIRegistry;
import org.pelea.core.communication.RMI.client;
import org.pelea.core.communication.RMI.implementationMaster;
import org.pelea.core.communication.RMI.implementationSlave;
import org.pelea.core.communication.RMI.interfaceMaster;
import org.pelea.core.configuration.configuration;
import org.pelea.utils.Util;

/**
 *
 * @author moises
 */
public class RMICommunicationModel extends CommunicationModel
{
    private RMIRegistry registry;
    private interfaceMaster intMaster = null; 
    private implementationSlave  impSlave = null;
    private implementationMaster impMaster = null;
    private String url;
    
    public RMICommunicationModel(String host, int port, byte mode, byte type, String name) throws NotBoundException, MalformedURLException, RemoteException
    {
        boolean connected = false; 
        
        this.registry = new RMIRegistry(host, port);
        this.host = host;
        this.port = port;
        this.mode = mode;
        this.type = type;
        this.url  = "";
        
        if (this.mode == CommunicationModel.CLIENT)
        {
            System.out.print("Connecting to monitoring node (" + this.host + ":" + this.port + ") ");
            
            while (!connected) {
                try {
                    this.intMaster  = (interfaceMaster) this.registry.registerNode("rmi://" + this.host + ":" + this.port + "/Monitoring");
                    this.impSlave   = new implementationSlave();
                    this.impMaster  = null;
                    this.sendRegisterMessage(name);
                    connected = true;
                } 
                catch (RemoteException e) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Util.printError(name, "Waiting loop (" + ex.toString() + ")");
                    }
                }
            }
            
            System.out.println("");
        }
        else
        {
            this.registry.run();
            this.url = "rmi://" + this.host + ":" + this.port + "/Monitoring"; //TODO: It should be changed by dynamic value.
            this.intMaster  = null;
            this.impSlave   = null;
            this.impMaster  = new implementationMaster(configuration.getInstance().getArrayParameter(name, "NETWORK"));
            this.registry.registerNode(this.url, this.impMaster);
        }
    }
    
    private boolean matches(ArrayList<String> idems, String name) {
        for (int i = 0; i < idems.size(); i++) {
            if (idems.get(i).matches(name)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Message getMessage() throws RemoteException {
        return (this.mode == CommunicationModel.CLIENT) ? this.impSlave.getMessage():this.impMaster.getMessage();
    }
    
    public void sendRegisterMessage(String name) throws RemoteException {
        Message msg = new Message();

        msg.setSender(name);
        msg.setTypeMsg((byte) Messages.MSG_REGISTER);
        msg.setSenderType(this.type);
        msg.setRecipientType(Messages.NODE_MONITORING);
        msg.setContent(null);
        
        this.intMaster.registerNode(msg, this.impSlave);
    }
    
    @Override
    public void sendUnRegisterMessage(String name) throws RemoteException {
        Message msg = new Message();

        msg.setSender(name);
        msg.setTypeMsg((byte) Messages.MSG_UNREGISTER);
        msg.setSenderType(this.type);
        msg.setRecipientType(Messages.NODE_MONITORING);
        msg.setContent(null);
        
        this.intMaster.unRegisterNode(msg);
    }
    
    @Override
    public int sendMessage(Message msg) {
        int sended = 0;
        
        try {
            
            if (this.getMode() == CommunicationModel.SERVER) {
                ArrayList references = (ArrayList) this.impMaster.getReferenceListByType(msg.getRecipientType());

                for (int j = 0; j < references.size(); j++) {
                    client reference = (client) references.get(j);
                    reference.getInterface().sendMessage(msg);
                    //System.out.println("EXEC: SENDING MESSAGE FROM " + msg.getSender() + " TO " + reference.getName());
                    sended++;
                }
            }
            else {
                this.intMaster.sendMessage(msg);
            }
        }
        catch (RemoteException ex) {
            Util.printError(msg.getSender(), "Sending global message");
        }
        
        return sended;
    }
    
    @Override
    public int sendMessage(Message msg, List<String> names) {
        int sended = 0;
        
        try {
            
            ArrayList references = (ArrayList) this.impMaster.getReferenceListByType(msg.getRecipientType());
            
            for (int j = 0; j < references.size(); j++) {
                
                client reference = (client) references.get(j);
                
                if (this.matches((ArrayList<String>) names, reference.getName())) {
                    reference.getInterface().sendMessage(msg);
                    //System.out.println("EXEC: SENDING MESSAGE FROM " + msg.getSender() + " TO " + reference.getName()); 
                    sended++;
                } else {
                }
            }
        }
        catch (RemoteException ex)
        {
           Util.printError(msg.getSender(), "Sending global message");
        }
        
        return sended;
    }
    
    @Override
    public boolean messages() {
        return (this.mode == CommunicationModel.CLIENT) ? this.impSlave.messages():this.impMaster.messages();
    }
    
    public void setRegistryUrl(String url) {
        this.url = url;
    }
        
    public String getRegistryUrl() {   
        return this.url;
    }
    
    public boolean isSyncronized() {
        return this.impMaster.isActive();
    }

    @Override
    public void sendGlobalMessage(Message msg) {
        try {
            for (int i = 0; i < this.impMaster.getNumNodes(); i++) {
                this.impMaster.getReferenceByPosition(i).sendMessage(msg);
                Util.printDebug(msg.getSender(), msg, 1);
            }
        }
        catch (RemoteException ex) {
           Util.printError(msg.getSender(), "Sending global message");
        }
    }
    
    public int getNumNodes(byte type) {
        return this.impMaster.getReferenceListByType(type).size();
    }
    
    @Override
    public void syncronize(String name) {
        
        if (this.getMode() == CommunicationModel.SERVER) {
            Util.printDebug(name, "Waiting client nodes");

            while (!this.isSyncronized()) {

                try{
                    Thread.sleep((long) 100);
                }
                catch (InterruptedException ie)
                {
                    Util.printError(name, "Waiting client nodes (" + ie.toString() + ")");
                }
            }   
        }
    }
}
