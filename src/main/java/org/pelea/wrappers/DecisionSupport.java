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
import java.util.ArrayList;
import java.util.List;
import org.pelea.core.communication.Message;
import org.pelea.core.communication.Messages;
import org.pelea.core.communication.connector.CommunicationModel;
import org.pelea.core.configuration.configuration;
import org.pelea.core.module.Module;
import org.pelea.planner.Planner;
import org.pelea.response.Response;
import org.pelea.utils.Util;

public abstract class DecisionSupport extends Module
{
    protected final List<Planner> planners;
    
    public DecisionSupport(String name, byte communicationModel) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotBoundException, MalformedURLException, RemoteException {   
        
        super(name, "DECISIONSUPPORT", CommunicationModel.CLIENT, communicationModel);
        this.planners = new ArrayList();
        
        String codes[] = configuration.getInstance().getParameter(this.name, "PLANNERS").split(",");
            
        for (int i = 0; i < codes.length; i++)
        {
            codes[i] = codes[i].trim();
                
            Util.printDebug(this.getName(), "INIT PLANNER " + configuration.getInstance().getParameter(codes[i], "PLANNER_NAME") + " IN MODE " + configuration.getInstance().getParameter(codes[i], "PLANNER_MODE"));
            Planner p = (Planner) (Class.forName(configuration.getInstance().getParameter(codes[i], "PLANNER_CLASS")).getConstructor(String.class)).newInstance(codes[i]);
                
            this.planners.add(p);
        }
    }
    
    protected int getPlanner(int mode)
    {
        int position = 0;
        
        while (position < this.planners.size()) {
            if (this.planners.get(position).getMode() == mode) {
               return position; 
            }
            
            position++;
        }
        
        return 0;
    }
    
    public abstract String RepairOrReplan(String stateH, String domainH, String planH);
    public abstract String getPlanHInfoMonitor(String stateH, String domainH);

    @Override
    public Message messageHandler(Message recieve) throws Exception {
        
        Response res;
        
        switch (recieve.getTypeMsg()) {
            case Messages.MSG_START:
                this.state = Module.RUNNING;
                return null;
            case Messages.MSG_GETPLANINFO:
                if (this.isRunning()) {
                    res = new Response(recieve.getContent());
                    return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_GETPLANINFO_RES, this.getPlanHInfoMonitor(res.getNode("problem", true), res.getNode("domain", true)));
                }
                break;
            case Messages.MSG_REPAIRORPEPLAN:
                if (this.isRunning()) { 
                    res = new Response(recieve.getContent());
                    return new Message(this.commumicationModel.getType(), Messages.NODE_MONITORING, Messages.MSG_REPAIRORPEPLAN_RES, this.RepairOrReplan(res.getNode("problem", true), res.getNode("domain", true), res.getNode("plans", true)));
                }
                break;
            case Messages.MSG_STOP:
                if (this.isRunning()) this.state = Module.STOPPING;
                return null;
        }
        
        return null;
    }

    @Override
    public void errorHandler() throws Exception {
    }
}
