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

package org.pelea.core.communication.RMI;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.pelea.core.communication.Message;
import org.pelea.utils.Util;

/**
 * @author Moises Martinez
 * @group PLG Universidad Carlos III
 * @version 1.0
 */

public class implementationMaster extends UnicastRemoteObject implements interfaceMaster 
{
    public final int CODESIZE = 8;
    
    private int[] activationCode;
    private int[] realCode;
    private ArrayList<client> nodes;
    private MailBox buffer;

    public implementationMaster (int[] code) throws RemoteException 
    {
        this.nodes          = new ArrayList <client> ();
        this.buffer         = new MailBox();
        this.activationCode = new int[CODESIZE];
        this.realCode       = new int[]{0,0,0,0,0,0,1,0};
    
        this.setActivationCode(code);
    }
    
    public void setActivationCode(String code)
    {  
        String[] data = code.split(",");
        
        for (int i = 0; i < CODESIZE; i++) {
            this.activationCode[i] = Integer.parseInt(data[i]);
        }
    }
    
    public void setActivationCode(int[] code) {
        for (int i = 0; i < CODESIZE; i++) {
            this.activationCode[i] = code[i];
        }
    }
    
    public interfaceSlave getReferenceByName (String name)
    {
        for (int i = 0; i < this.nodes.size(); i++)
        {
            if (this.nodes.get(i).getName().matches(name)) {
                return this.nodes.get(i).getInterface();
            }
        }
        return null;
    }
    
    public interfaceSlave getReferenceByPosition (int position)
    {
        return this.nodes.get(position).getInterface();
    }
    
    public interfaceSlave getReferenceByType (byte type)
    {
        for (int i = 0; i < this.nodes.size(); i++)
        {
            if (this.nodes.get(i).getType() == type) {
                return this.nodes.get(i).getInterface();
            }
        }
        return null;
    }
    
    public List getReferenceListByType (byte type)
    {
        ArrayList<client> references = new ArrayList();
        
        for (int i = 0; i < this.nodes.size(); i++)
        {
            if (this.nodes.get(i).getType() == type) 
            {
                references.add(this.nodes.get(i));
            }
        }
        
        return references;
    }

    @Override
    public synchronized void registerNode(Message message, interfaceSlave node) throws RemoteException
    {
        try
        {
            Util.printDebug("RMI REGISTER", "CLIENT " + Util.getTypeNode(message.getSenderType()) + "[ " + message.getSender() + " ]");
            
            this.nodes.add(new client(message.getSenderType(), message.getSender(), node));

            this.realCode[message.getSenderType()-1]++;
        } 
        catch (Exception e)
        {
            throw new RemoteException ();
        }
    }
    
    @Override
    public synchronized void unRegisterNode(Message message) throws RemoteException
    {
        try
        {
            Util.printDebug("RMI UNREGISTER", "CLIENT " + Util.getTypeNode(message.getSenderType()) + "[ " + message.getSender() + " ]");
            
            for (int i = 0; i < this.nodes.size(); i++) {
                if (this.nodes.get(i).getName().matches(message.getSender())) {
                    this.nodes.remove(i);
                    break;
                }
            }
            this.realCode[message.getSenderType()-1]--;
        } 
        catch (Exception e)
        {
            throw new RemoteException ();
        }
    }
    
    @Override
    public void sendMessage(Message msg) throws RemoteException 
    {
        //System.out.println(msg.getContent());
        this.buffer.insertMessage(msg);
    }

    @Override
    public synchronized Message getMessage() throws RemoteException
    {
        Message msg = this.buffer.getMessage();
        return msg;
    }
    
    public int getNumNodes()
    {
        return this.nodes.size();
    }
    
    public boolean messages()
    {
        return (!this.buffer.empty());
    }

    public boolean isActive()
    {
        return Arrays.equals(this.activationCode, this.realCode);
    }
    
    public List<String> getNodesByCode(byte type)
    {
        ArrayList<String> names = new ArrayList();
        
        for (int i = 0; i < this.nodes.size(); i++)
        {
            if (this.nodes.get(i).getType() == type) 
            {
                names.add(this.nodes.get(i).getName());
            }
        }
        
        return names;
    }
}
