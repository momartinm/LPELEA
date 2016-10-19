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

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
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

public class ExecutionFiles extends Execution
{ 
    public ExecutionFiles(String name) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotBoundException, MalformedURLException, RemoteException
    {
        super(name, Module.RMI);
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
        
            for (int i = 0; i < actions.size(); i++) 
            {
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
        System.out.println("Executing action: " + actionL);
    }

    @Override
    public void executeAction(double time, String action) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSensors() 
    {
        XMLOutputter outputter  = new XMLOutputter(Format.getCompactFormat());
        SAXBuilder builder      = new SAXBuilder();
        Document document;
        
        String xml              = "";
        
        xml += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        xml += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
            
        try 
        {
            document = (Document) builder.build(new StringReader(Pddl2XML.convertDomain(configuration.getInstance().getParameter("GENERAL", "PROBLEM"))));
         
            xml += outputter.outputString((Element) document.getRootElement().getContent().get(1));

        } 
        catch (Exception ex) 
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
        String xml      = "";
        Response res    = new Response();
        
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
