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

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author moises
 */
public class RMIRegistry 
{
    private String host;
    private int port;
    private Registry registry;
    
    public RMIRegistry(int port) throws RemoteException
    {
        this.port = port;
        this.registry = LocateRegistry.getRegistry(port);
    }
    
    public RMIRegistry(String host, int port) throws RemoteException
    {
        this.port = port;
        this.host = host;
        this.registry = LocateRegistry.getRegistry(host, port);
    }
    
    public void run() throws RemoteException
    {
        this.registry = LocateRegistry.createRegistry(this.port);
        //this.registry.list();
    }
    
    public void registerNode(String url, implementationMaster node) throws RemoteException {
        this.registry.rebind(url, node);
    }
    
    public Remote registerNode(String url) throws RemoteException, NotBoundException {
        return this.registry.lookup(url);
    }
}
