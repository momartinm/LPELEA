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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Node {
    private final String node;
    private final String name;
    
    public Node(String name, String node) throws JDOMException, IOException
    {
        this.name = name;
        
        XMLOutputter outputter  = new XMLOutputter(Format.getPrettyFormat());
        SAXBuilder builder      = new SAXBuilder();
        Document document = (Document) builder.build(new StringReader(node));
        
        if (document.getRootElement().getName().toUpperCase().contains("DEFINE"))
            this.node = outputter.outputString((Element) document.getRootElement().getChildren().get(0));
        else
            this.node = outputter.outputString(document.getRootElement());
    }
    
    public String getNode(boolean head)
    {
        String xml;
        
        if (head)
        {
            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
            xml += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
            xml += this.node;
            xml += "</define>";
        }
        else 
        {
            xml = this.node;
        }
    
        return xml;
    }
    
    public String getName()
    {
        return this.name;
    }
}
