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

package org.pelea.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.pelea.core.communication.Message;
import org.pelea.core.communication.Messages;
import org.pelea.core.configuration.configuration;

public class Util 
{  
    public static String getTypeNode(byte node)
    {
        switch (node)
        {
            case 1: return "DECISIONSUPPORT Node"; 
            case 2: return "EXECUTION Node";
            case 3: return "GOALSMETRICS Node";
            case 5: return "LOWLEVELPLANNER Node"; 
            case 6: return "LOWTOHIGH Node"; 
            case 7: return "MONITORING Node"; 
            case 8: return "LEARNING Node";       
        }
        
        return "Unknown Node";
    }
    
    public static byte getCodeByName(String node)
    {
        String[] hierarchy = node.split(".");   
        
        if (hierarchy.length == 0)
        {
            hierarchy  = new String[1];
            hierarchy[0] = node;
        }
            
        if (hierarchy[hierarchy.length-1].toUpperCase().contains("MONITORING")) {
            return Messages.NODE_MONITORING;
        }  
        if (hierarchy[hierarchy.length-1].toUpperCase().contains("EXECUTION")) {
            return Messages.NODE_EXECUTION;
        } 
        if (hierarchy[hierarchy.length-1].toUpperCase().contains("LOWTOHIGH")) {
            return Messages.NODE_LOWTOHIGH;
        } 
        if (hierarchy[hierarchy.length-1].toUpperCase().contains("LOWLEVELPLANNER")) {
            return Messages.NODE_LOWLEVELPLANNER;
        } 
        if (hierarchy[hierarchy.length-1].toUpperCase().contains("GOALS")) {
            return Messages.NODE_GOALS;
        } 
        if (hierarchy[hierarchy.length-1].toUpperCase().contains("HIGHLEVELREPLANER")) {
            return Messages.NODE_HIGHLEVELREPLANER;
        } 
        if (hierarchy[hierarchy.length-1].toUpperCase().contains("DECISIONSUPPORT")) {
            return Messages.NODE_DECISSIONSUPPORT;
        } 
        
        return 0;
    }
    
    public static String getTypeMessage(byte code)
    {
        switch (code)
        {
            case 1: return "Register node";
            case 11: return "Execute plan of actions";
            case 12: return "Execute action";
            case 13: return "Execute action with time";
            case 14: return "Get sensors info";
            case 15: return "Get sensors info with time";
            case 16: return "Get last time";
            case 21: return "Plan of actions executed";
            case 22: return "Action executed";
            case 23: return "Action executed with time";
            case 24: return "Sensors info";
            case 25: return "Sensors info with time";
            case 26: return "Last time";
            case 31: return "Translate Low to High "; 
            case 32: return "Response Low to high translation";
            case 41: return "Translate High to Low";
            case 42: return "Response High to Low";
            case 51: return "Get goals";
            case 52: return "Response new info goals";
            case 61: return "Repair or replan";
            case 62: return "Get info by plan"; 
            case 63: return "Response repair or replan";
            case 64: return "Respose info plan";
            case 97: return "Finish round";
            case 98: return "Stop node";
            case 99: return "Start node";
        }
        
        return "Unknown message";
    }
    
    public static void printDebug(String name, String msg)
    {
        String value = configuration.getInstance().getParameter("GENERAL", "DEBUG");
        
        if ((value != null) && (value.matches("ON")))
        {
            System.out.println("DEBUG [" + name + "]: " + msg);
        }
    }
    
    private static String generateInfo(String name, Message msg, int mode) {
        
        String info = "";
        
        if (mode == 1) {
           info += "DEBUG [" + name + "]: Sending Message\n";
        }
        else {
            info += "DEBUG [" + name + "]: Reciving Message\n";
        }

        info += "DEBUG [" + name + "]: Init Message content\n";
        info += "MSG [SENDER]:" + msg.getSender() + "\n";
        info += "MSG [TYPEMSG]:" + getTypeMessage(msg.getTypeMsg()) + "\n";
        info += "MSG [TYPENODE]:" + getTypeNode(msg.getSenderType()) + "\n";

        if (msg.getContent() != null) {
            info += "MSG [CONTENT]: Message(Size = " + msg.getContent().length() + ")\n";
        }
        else {
            info += "MSG [CONTENT]: No Message\n";
        }

        info += "DEBUG [" + name + "]: End Message content\n";
        
        return info;
    }
    
    public static void printDebug(String name, Message msg, int mode) {
        Boolean debug = Boolean.parseBoolean(configuration.getInstance().getParameter("GENERAL", "DEBUG"));
        Boolean log = Boolean.parseBoolean(configuration.getInstance().getParameter("GENERAL", "LOG_FILE"));
        
        if ((debug != null) && debug) {
            if (mode == 1) {
                System.out.println("DEBUG [" + name + "]: Sending Message");
            }
            else {
                System.out.println("DEBUG [" + name + "]: Reciving Message");
            }
            
            System.out.println("DEBUG [" + name + "]: Init Message content");
            System.out.println("MSG [SENDER]:" + msg.getSender());
            System.out.println("MSG [TYPEMSG]:" + getTypeMessage(msg.getTypeMsg()));
            System.out.println("MSG [TYPENODE]:" + getTypeNode(msg.getSenderType()));

            if (msg.getContent() != null) {
                System.out.println("MSG [CONTENT]: Message(Size = " + msg.getContent().length() + ")");
            }
            else {
                System.out.println("MSG [CONTENT]: No Message");
            }
            
            System.out.println("DEBUG [" + name + "]: End Message content");
        }
        
        if ((log != null) && log) {
            
        }
        
    }
    
    public static void printError(String name, String msg)
    {
        System.out.println("ERROR [" + name + "]: " + msg);
    }
    
    public static String[] getNodesFromXML(String xml, String[] tags)
    {
        String content[] = new String[tags.length];
        
        try
        {
            XMLOutputter outputter  = new XMLOutputter(Format.getPrettyFormat());
            SAXBuilder builder      = new SAXBuilder();
            Document document       = (Document) builder.build(new StringReader(xml));
            List nodes              = document.getRootElement().getChildren();
            Element node            = null;

            //TODO: It should be recursive, but nodes always are in the first level.
            
            for (int i = 0; i < nodes.size(); i++)
            {
                node = (Element) nodes.get(i);
                
                for (String tag : tags) {
                    if (node.getName().toUpperCase().matches(tag.toUpperCase())) {
                        content[i] = outputter.outputString((Element) nodes.get(i));
                        break; //TODO i dont like.
                    }
                }
            }
            return content;
                            
         }
         catch (Exception ex)
         {
            Util.printError("Util Library", ex.toString());
            return null;
         }
    }
    
    public static String[] getDocumentsFromXML(String xml, String[] tags)
    {
        String content[] = new String[tags.length];
        
        try
        {
            XMLOutputter outputter  = new XMLOutputter(Format.getPrettyFormat());
            SAXBuilder builder      = new SAXBuilder();
            Document document       = null;
            List nodes              = null;
            Element node            = null;
            
            document    = (Document) builder.build(new StringReader(xml));
            nodes        = document.getRootElement().getChildren();
            
            //TODO: It should be recursive, but nodes always are in the first level.
            
            for (int i = 0; i < nodes.size(); i++)
            {
                node = (Element) nodes.get(i);
                
                for (int j = 0; j < tags.length; j++)
                {
                    if (node.getName().toUpperCase().matches(tags[j].toUpperCase()))
                    {
                        content[i]  = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
                        content[i] += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
                        content[i] += outputter.outputString((Element) nodes.get(i));
                        content[i] += "</define>";
                        break; //TODO i dont like.
                    }
                }
            }
            return content;
                            
         }
         catch (Exception ex)
         {
            Util.printError("Util Library", ex.toString());
            return null;
         }
    }
    
    public static int getPosition(String[] values, String value)
    {
        boolean found = false;
        int position  = 0;
        
        while ((!found) && (position < values.length))
        {
            if (values[position].toUpperCase().matches(value.toUpperCase()))
                found = true;
            else
                position++;
        }
        
        return (position > values.length) ? 0:position;
    }
    
    public static String getFileContentToString(String fileName)
    {
        String cadena   = "";
        String content  = "";
        
        BufferedReader bf;
        
        try {
            bf = new BufferedReader(new FileReader(fileName));
                         
            while ((cadena = bf.readLine())!=null) {
                content += cadena;
            }

            bf.close();
        }        
        catch (FileNotFoundException fnfex) {
            Util.printError("Util Library", fnfex.toString());
            content  = "";
        }
        catch (IOException ioex) {
            Util.printError("Util Library", ioex.toString());
            content  = "";
        }
        
        return content;
    }
    
    public static List getFileContentToList(String fileName)
    {
        String cadena = "";
        ArrayList<String> content = new ArrayList<String>();
        BufferedReader bf;
        
        try 
        {
            bf = new BufferedReader(new FileReader(fileName));
                         
            while ((cadena = bf.readLine())!=null) {
                content.add(cadena);
            }

            bf.close();
        } 
        catch (FileNotFoundException fnfex) 
        {
            Util.printError("Util Library", fnfex.toString());
        }
        catch (IOException ioex)
        {
            Util.printError("Util Library", ioex.toString());
        }
        
        return content;
    }
    
    public static String getClassNode(int node, String id)
    {
        System.out.println(getClassName(node));
        
        return configuration.getInstance().getParameter(id, "CLASS"); 
    }
    
    public static String getClassName(int value) {
        switch (value)
        {
            case 1: return "DECISIONSUPPORT"; 
            case 2: return "EXECUTION";
            case 3: return "GOALSMETRICS";
            case 5: return "LOWLEVELPLANNER"; 
            case 6: return "LOWTOHIGH"; 
            case 7: return "MONITORING"; 
            case 8: return "LEARNING"; 
        }
        
        return "";
    }
    
    public static String generateMessageError(String code, String message) {
        
        String result;
        
        result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        result += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
        result += "<error>";
        result += "<type>" + code +"</type>";
        result += "<message>" + message + "</message>";
        result += "</error>";
        result += "</define>";
    
        return result;
    }
    
    public static String readFile(String filename) throws FileNotFoundException, IOException {
        
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        String buffer = "";
        
        while ((line = reader.readLine()) != null) {
            buffer += line;
        }
        
        return buffer;
    }
}