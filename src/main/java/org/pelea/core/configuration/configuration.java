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

package org.pelea.core.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author moises
 */
public class configuration 
{
    static private configuration instance = new configuration();
    private HashMap<String, String> parameters;
    private String[] network;
    
    private configuration()
    {
        this.parameters = new HashMap();
        this.network    = new String[8];
    }
    
    static public configuration getInstance() 
    {
        return instance;
    }
    
    private String generateName(Element node)
    { 
        String id   = node.getAttributeValue("id");
        
        //if (((!"".equals(type)) && (type != null)) && ((!"".equals(id)) && (id != null)))
        if ((!"".equals(id)) && (id != null)) {
            return id;
        }
        
        return null;
    }
    
    private void readNode(String key, Element node)
    {
        List childrens = node.getChildren();
        
        if (childrens.size() > 0)
        {
            for (int i = 0; i < childrens.size(); i++) {
                this.readNode(this.generateName(node), (Element) childrens.get(i));
            }
        }
        else {
            this.setParameter(key + "." + node.getAttributeValue("name"), node.getAttributeValue("value"));
        }
    }
    
    public boolean readConfigFile(String path)
    {
        SAXBuilder builder = new SAXBuilder();  
        File xmlFile = new File(path);
        
        try
        {
            Document document = (Document) builder.build(xmlFile);
            Element rootNode = document.getRootElement();
            List list = rootNode.getChildren();
            
            for (int i = 0; i < list.size(); i++) 
            {
                this.readNode("General", (Element) list.get(i));
            }
            
            return true;
        }
        catch (IOException io) 
        {
            System.out.println(io.getMessage());
            return false;
	} 
        catch (JDOMException jdomex) 
        {
            System.out.println(jdomex.getMessage());
            return false;
        }	   
    }
    
    public String getParameter(String name, String property)
    {
        String key = name + "." + property;
        return this.parameters.get(key.toUpperCase());
    }
            
    public void setParameter(String name, String value)
    {
        this.parameters.put(name.toUpperCase(), value);
    }
    
    public int[] getArrayParameter(String name, String property)
    {
        //TODO: I have to find another way to do that.
        String[] temporal = this.getParameter(name, property).split(",");
        int code[] = new int[temporal.length];
        
        for (int i = 0; i< temporal.length; i++) {
            code[i] = ("1".equals(temporal[i])) ? 1:0;
        }

        return code;
    }

    public String getParameter(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
