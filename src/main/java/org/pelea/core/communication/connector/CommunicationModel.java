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

import java.util.List;
import org.pelea.core.communication.Message;

/**
 *
 * @author moises
 */
public abstract class CommunicationModel
{
    public static byte SERVER = 1;
    public static byte CLIENT = 2;
    public static byte BOTH = 3;
    
    protected int port;
    protected String host;
    protected byte type;
    protected byte mode;
    
    public int getPort() {
        return this.port;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public byte getMode() {
        return this.mode;
    }
    
    public abstract int sendMessage(Message msg);
    public abstract int sendMessage(Message msg, List<String> names);
    public abstract void sendGlobalMessage(Message msg);
    public abstract void syncronize(String name);
    public abstract boolean messages();
    public abstract Message getMessage() throws Exception;
    public abstract void sendUnRegisterMessage(String name) throws Exception; 
}
