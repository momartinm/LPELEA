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

package org.pelea.response;

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

public class Response 
{
    private List<Node> nodes; 
    
    public Response()
    {
        this.nodes = new ArrayList<Node>();
    }
    
    public Response(String content)
    {
        String xml = "";
        this.nodes = new ArrayList<Node>();

        try
        {
            XMLOutputter outputter  = new XMLOutputter(Format.getPrettyFormat());
            SAXBuilder builder      = new SAXBuilder();
            Document document       = (Document) builder.build(new StringReader(content));
            List elements           = document.getRootElement().getChildren();
            
            //TODO: It should be recursive, but nodes always are in the first level.
            
            for (int i = 0; i < elements.size(); i++)
            {
                this.nodes.add(new Node(((Element) elements.get(i)).getName(), outputter.outputString((Element) elements.get(i))));
            }           
         }
         catch (Exception ex)
         {
            Util.printError("Error processing response", ex.toString());
         }
    }
    
    public String getNode(String tag, boolean head)
    {
        boolean find = false;
        int position = 0;
        
        while (position < this.nodes.size())
        {
            if (this.nodes.get(position).getName().matches(tag))
                return this.nodes.get(position).getNode(head);
            
            position++;
        }
        
        return null;
    }
    
    public void addNode(String name, String content) throws JDOMException, IOException
    {
        this.nodes.add(new Node(name, content));
    }
    
    public String generateResponse()
    {
        String xml;
        
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        xml += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
        
        for (int i = 0; i < this.nodes.size(); i++) 
        {
            xml += this.nodes.get(i).getNode(false);
        } 
        
        xml += "</define>";
        
        return xml;
    }
}
