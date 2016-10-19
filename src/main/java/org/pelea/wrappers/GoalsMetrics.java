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
import org.pelea.core.communication.connector.CommunicationModel;
import org.pelea.core.module.Module;

public abstract class GoalsMetrics extends Module {
    
    public GoalsMetrics(String name, byte communicationModel) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotBoundException, MalformedURLException, RemoteException {   
        super(name, "GOALSANDMETRICS", CommunicationModel.CLIENT, communicationModel);
    }
    
    public abstract String getGoals(String domain, String problem);
    
    @Override
    public Message messageHandler(Message recieve) throws Exception {
        return null;
    }

    @Override
    public void errorHandler() throws Exception {
    }
}
