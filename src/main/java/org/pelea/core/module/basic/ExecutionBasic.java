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

package org.pelea.core.module.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.pelea.core.configuration.configuration;
import org.pelea.core.module.Module;
import org.pelea.response.Response;
import org.pelea.wrappers.Execution;
import org.pelea.utils.Util;
import pddl2xml.Pddl2XML;

/**
 *
 * @author moises
 */

public class ExecutionBasic extends Execution
{ 
    private final Socket connection;
    private final PrintStream out;
    private final BufferedReader in;

    public ExecutionBasic(String name) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotBoundException, MalformedURLException, RemoteException, IOException {
        
        super(name,Module.RMI);
        
        this.connection     = new Socket(configuration.getInstance().getParameter(this.name, "IP") , Integer.parseInt(configuration.getInstance().getParameter(this.name, "PORT")) );
        this.out            = new PrintStream(this.connection.getOutputStream());
        this.in             = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
    }

    @Override
    public void executePlan(String planL) 
    {
	try 
        {
            XMLOutputter outputter  = new XMLOutputter(Format.getPrettyFormat());
            SAXBuilder builder      = new SAXBuilder();
            Document document       = (Document) builder.build(new StringReader(planL));
            Element plan            = ((Element) document.getRootElement());        
            List actions            = plan.getChildren();
        
            for (int i = 0; i < actions.size(); i++) {
                this.executeAction(outputter.outputString((Element) actions.get(i)));
            }
        } 
        catch (Exception e) 
        {
           Util.printError(this.getName(), "Executing plan of actions");
	}
    }

    @Override
    public void executeAction(String actionL) 
    {
        char[] buffer = new char[4096];
        int bytes     = 0;
        String xml    = ""; 
        
        if (this.executionMode)
        {
            try 
            {
                this.out.print("92");
                this.out.flush();

                Util.printDebug(this.getName(), "Sending command 92");

                this.out.print(actionL);
                this.out.flush();
                
                Util.printDebug(this.getName(), actionL);

                bytes = this.in.read(buffer);

                if (bytes > 0) {
                    xml += new String (buffer, 0, bytes);
                }
                else
                {
                    xml += "<error>";
                    xml += "Error getting answer from robot";
                    xml += "</error>";
                }
            }
            catch (IOException ex) 
            {
                xml += "<error>";
                xml += "Error getting answer from robot";
                xml += "</error>";
            }
            catch (Exception ex)
            {
                Util.printError(this.getName(), ex.toString());
            }
        }
        else
        {
            Util.printDebug(this.getName(), "Executing action: " + actionL);
        }   
        
        
    }

    @Override
    public void executeAction(double time, String action) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSensors() 
    {
        char[] buffer = new char[4096];
        int bytes     = 0;
        String xml    = ""; 

        xml += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        xml += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
        
        try 
        {
            this.out.print("94");
            this.out.flush();
            
            Util.printDebug(this.getName(), "Sending command 94");
            
            bytes = this.in.read(buffer);
            
            if (bytes > 0)
            {
                xml += "<state level=\"l\" problem=\"" + "Problem Name" + "\" domain=\"" + "Domain name" + "\">";
                xml += new String (buffer, 0, bytes);
                xml += "</state>";
            }
            else
            {
                xml += "<error>";
                xml += "Error getting answer from robot";
                xml += "</error>";
            }
        }
        catch (IOException ex) 
        {
            xml += "<error>";
            xml += "Error getting answer from robot";
            xml += "</error>";
        }
        
        xml += "</define>";
        
        return xml;
    }

    @Override
    public String getSensorsWithTime(double instant_time) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getLastTime() 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String solveProblem() 
    {
        String xml = "";
        Response res = new Response();
        
        try 
        {
            res.addNode("domain", Pddl2XML.convertDomain(configuration.getInstance().getParameter("GENERAL", "DOMAIN")));
            res.addNode("problem", Pddl2XML.convertProblem(configuration.getInstance().getParameter("GENERAL", "PROBLEM")));
            
            xml = res.generateResponse();
        } 
        catch (Exception ex) 
        {
            xml += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
            xml += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
            xml += "<error>";
            xml += "Error getting actual state";
            xml += "</error>";
            xml += "</define>";
        }
        
        return xml;
    }
}
