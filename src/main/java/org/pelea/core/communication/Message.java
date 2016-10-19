/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3n.es
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

package org.pelea.core.communication;

import java.io.Serializable;

/**
 * @author Moises Martinez
 * @group PLG Universidad Carlos III
 * @version 1.0
 */

public class Message implements Serializable 
{
    private String content;
    private String sender;
    private String recipient;
    private byte typeRecipient;
    private byte typeSender;
    private byte typeMsg;

    public Message() {
        this.content = null;
    }
    
    public Message(byte typeRecipient, String recipient, byte typeMsg, String content) {
        this.typeRecipient = typeRecipient;
        this.typeMsg = typeMsg;
        this.recipient = recipient;
        
        if (content != null)
            this.content = content;
        else            
            this.content = null;
    }
    
    public Message(byte typeSender, byte typeRecipient, String sender, byte typeMsg, String content) {
        this.typeSender = typeSender;
        this.typeRecipient = typeRecipient;
        this.sender = sender;
        this.typeMsg = typeMsg;
        this.recipient = null;
        
        if (content != null)
            this.content = content;
        else            
            this.content = null;
    }
    
    public Message (byte typeSender, byte typeRecipient, byte typeMsg, String content) {
        this.typeSender = typeSender;
        this.typeRecipient = typeRecipient;
        this.typeMsg = typeMsg;
        this.recipient = null;
        
        if (content != null)
            this.content = content;
        else            
            this.content = null;
    }
    
    public Message (byte typeSender, byte typeRecipient, byte typeMsg, String content, String name) {
        this.typeSender = typeSender;
        this.typeRecipient = typeRecipient;
        this.typeMsg = typeMsg;
        this.recipient = name;
        
        if (content != null)
            this.content = content;
        else            
            this.content = null;
    }
    
    public Message (byte typeRecipient, byte typeMsg, String content) {
        this.typeRecipient = typeRecipient;
        this.typeMsg = typeMsg;
        this.recipient = null;
        
        if (content != null)
            this.content = content;
        else            
            this.content = null;
    }
    
    public Message (byte typeRecipient, byte typeMsg, String content, String name) {
        this.typeRecipient = typeRecipient;
        this.typeMsg = typeMsg;
        this.recipient = name;
        
        if (content != null)
            this.content = content;
        else            
            this.content = null;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setSender(String sender){
        this.sender = sender;
    }

    public void setSenderType(byte type){
        this.typeSender = type;
    }
    
    public void setRecipientType(byte type){
        this.typeRecipient = type;
    }
    
    public void setTypeMsg(byte type){
        this.typeMsg = type;
    }
    
    public boolean isNamed() {
        return (this.recipient != null);
    }

    public void setMessage(Message origin) 
    {
        this.content   = origin.getContent();
        this.typeMsg   = origin.getTypeMsg();
        this.recipient = origin.getRecipient();
        this.sender = origin.getSender();
        this.typeRecipient = origin.getRecipientType();
        this.typeSender = origin.getSenderType();
    }

    public byte getTypeMsg(){
        return this.typeMsg;
    }
    
    public byte getSenderType(){
        return this.typeSender;
    }

    public String getSender(){
        return this.sender;
    }

    public String getRecipient(){
        return this.recipient;
    }
    
    public byte getRecipientType(){
        return this.typeRecipient;
    }
    
    public String getContent(){
        return this.content;
    }

    public char [] getContentBySize(int size) 
    {
        char chars [] = new char [size];
        int i;

        for (i = 0; i < size; i++)
            chars[i] = this.content.charAt(i);

        return chars;
    }
}
