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

package org.pelea.monitoring.data;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.pelea.utils.Util;
import pddl2xml.Pddl2XML;

/**
 *
 * @author moises
 */
public class Data
{
    public static int LOWLEVEL = 1;
    public static int HIGHLEVEL = 2;
 
    private String _name;
    
    private String _stateH;
    private String _stateL;
    private String _domainH;
    private String _domainL;
    private String _planH;
    private String _planL;
    private String _action;
    
    private List<String[]> _actions;
    
    private int _executedActions;
    
    private List<DataVariable> variables;
    
    public Data() {
        this.setName("Buffer Monitoring");
    }
    
    public Data(String domain) {
        try
        {
            this.setName("Buffer Monitoring");
            
            XMLOutputter outputter  = new XMLOutputter(Format.getPrettyFormat());
            SAXBuilder builder      = new SAXBuilder();
            Document document       = null;

            document                = (Document) builder.build(new StringReader(Pddl2XML.convertDomain(domain)));
            this._domainH           = outputter.outputString((Element) document.getRootElement().getContent().get(1));
                    
            this._actions           = new ArrayList();
            this._executedActions   = -1;
            this.variables = new ArrayList<DataVariable>();
        
        }
        catch (IOException io) 
        {
            Util.printError(this.getName(), io.getMessage());
        } 
        catch (JDOMException jdomex) 
        {
            Util.printError(this.getName(), jdomex.getMessage());
        }
        catch (Exception ex)
        {
            Util.printError(this.getName(), ex.toString());
        }
    }
    
    public Data(String domain, String problem) {
                try
        {
            this.setName("Buffer Monitoring");
            
            XMLOutputter outputter  = new XMLOutputter(Format.getPrettyFormat());
            SAXBuilder builder      = new SAXBuilder();
            Document document       = null;
            
            document                = (Document) builder.build(new StringReader(Pddl2XML.convertDomain(problem)));
            this._stateH            = outputter.outputString((Element) document.getRootElement().getContent().get(1));
            
            document                = (Document) builder.build(new StringReader(Pddl2XML.convertDomain(domain)));
            this._domainH           = outputter.outputString((Element) document.getRootElement().getContent().get(1));
                    
            this._actions           = new ArrayList();
            this._executedActions   = -1;
            this.variables = new ArrayList<DataVariable>();
        
        }
        catch (IOException io) 
        {
            Util.printError(this.getName(), io.getMessage());
        } 
        catch (JDOMException jdomex) 
        {
            Util.printError(this.getName(), jdomex.getMessage());
        }
        catch (Exception ex)
        {
            Util.printError(this.getName(), ex.toString());
        }
    }
    
    public void setName(String name)
    {
        this._name = name;
    }

    public String getName()
    {
        return this._name;
    }
    
    public void setStateH(String state)
    {
        this._stateH = state;
    }
    
    public String getStateH()
    {
        return this._stateH;
    }
    
    public void setStateL(String state)
    {
        this._stateL = state;
    }
    
    public String getStateL()
    {
        return this._stateL;
    }
    
    public void setDomainH(String domain)
    {
        this._domainH = domain;
    }
    
    public String getDomainH()
    {
        return this._domainH;
    }
    
    public void setDomainL(String domain)
    {
        
        this._domainL = domain;
    }
    
    public String getDomainL()
    {
        return this._domainL;
    }
    
    public void setPlanH(String plan)
    {
        this._planH = plan;
    }
    
    public String getPlanH()
    {
        return this._planH;
    }
    
    public void setPlanL(String plan)
    {
        this._planL = plan;
    }
    
    public String getPlanL()
    {
        return this._planL;
    }
    
    public void setAction(String action)
    {
        this._action = action;
    }
    
    public String getAction()
    {
        return this._action;
    }
    
    public void addVariable(String name, String value) {
        this.variables.add(new DataVariable(name, value));
    }
    
    public void updateVariable(String name, String value) {
        for (int i = 0; i < this.variables.size(); i++) {
            if (this.variables.get(i).getName().matches(name)) {
                this.variables.get(i).setValue(value);
            }
        }
    }
    
    public String buildXML(boolean stateH, boolean stateL, boolean domainH, boolean domainL, boolean planH, boolean planL, boolean action)
    {
        String xml = "";
        
        xml += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        xml += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
        
        if (stateH)     xml += this._stateH;
        if (stateL)     xml += this._stateL;
        if (domainH)    xml += this._domainH;
        if (domainL)    xml += this._domainL;
        if (planH)      xml += this._planH;
        if (planL)      xml += this._planL;
        if (action)     xml += this._action;

        xml += "</define>";
	
        return xml;
    }
    
    public String buildXML(boolean stateH, boolean stateL, boolean domainH, boolean domainL, boolean planH, boolean planL, boolean action, String[] variables)
    {
        String xml = "";
        
        xml += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        xml += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
        
        if (stateH)     xml += this._stateH;
        if (stateL)     xml += this._stateL;
        if (domainH)    xml += this._domainH;
        if (domainL)    xml += this._domainL;
        if (planH)      xml += this._planH;
        if (planL)      xml += this._planL;
        if (action)     xml += this._action;

        for (int i = 0; i < variables.length; i++) {
            for (int j = 0; j < this.variables.size(); j++) {
                if (variables[i].compareTo(this.variables.get(j).getName()) == 0)
                    xml += "<" + this.variables.get(j).getName() + ">" + this.variables.get(j).getValue() + "</" + this.variables.get(j).getName() + ">";
            }
        }
        
        xml += "</define>";
	
        return xml;
    }
    
    public void addActionHighLevel(String planActions)
    {
        try 
        {
            XMLOutputter outputter  = new XMLOutputter(Format.getPrettyFormat());
            SAXBuilder builder      = new SAXBuilder();
            Document document       = (Document) builder.build(new StringReader(planActions));
            Element nodes           = (Element) document.getRootElement().getChildren().get(0);
            List plan               = nodes.getChildren();
            
            for (int i = 0; i < plan.size(); i++) {
                List actions = ((Element) plan.get(i)).getChildren();
                
                for (int j = 0; j < actions.size(); j++) {
                    this._actions.add(this._actions.size(), new String[]{outputter.outputString((Element) actions.get(j)), null});
                }
            }
        }
        catch (Exception e) {
            Util.printError(this.getName(), "Converting plan high level to low level (" + e.toString() + ")");
	}
    }
    
    public void addActionsLowLevel(String planActions)
    {
        try {
            
            XMLOutputter outputter  = new XMLOutputter(Format.getPrettyFormat());
            SAXBuilder builder      = new SAXBuilder();
            Document document       = (Document) builder.build(new StringReader(planActions));
            Element nodes           = (Element) document.getRootElement().getChildren().get(0);
            List plan               = nodes.getChildren();
            
            for (int i = 0; i < plan.size(); i++) {
                this._actions.get(i)[1] = outputter.outputString((Element) plan.get(i));
            }
        }
        catch (Exception e) {
            Util.printError(this.getName(), "Converting plan high level to low level (" + e.toString() + ")");
	}
    }
    
    public String getNextAction(int level)
    {
        this._executedActions++;
        
        if (level == Data.HIGHLEVEL)
            return this._actions.get(this._executedActions)[0];
        if (level == Data.LOWLEVEL)
            return this._actions.get(this._executedActions)[1];
        
        return null;
    }
    
    public String getLastAction(int level)
    {
        if (level == Data.HIGHLEVEL)
            return this._actions.get(this._executedActions)[0];
        if (level == Data.LOWLEVEL)
            return this._actions.get(this._executedActions)[1];
        
        return null;
    }
    
    public boolean actions()
    {
        return ((this._executedActions + 1) < this._actions.size());
    }   
}