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

import java.util.ArrayList;
import org.pelea.core.communication.Message;

/**
 * @author Moises Martinez
 * @group PLG Universidad Carlos III
 * @version 1.0
 */
public class MailBox 
{
    private final ArrayList <Message> tail;

    public MailBox () 
    {
        tail = new ArrayList();
    }

    public synchronized void insertMessage (Message m) 
    {
        synchronized (MailBox.class) 
        {
            this.tail.add(m);
        }
    }

    public synchronized Message readMessage () 
    {
        synchronized (MailBox.class) 
        {
            return this.tail.get(0);
        }
    }

    public synchronized Message getMessage () 
    {
        synchronized (MailBox.class) 
        {
            return this.tail.remove(0);
        }
    }

    public boolean empty () 
    {
        return this.tail.isEmpty();
    }

    public int getNumberMessages()
    {
        return this.tail.size();
    }

}
