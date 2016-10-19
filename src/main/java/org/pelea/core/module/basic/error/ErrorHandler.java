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

package org.pelea.core.module.basic.error;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.pelea.languages.pddl.structures.NodeList;

/**
 *
 * @author moises
 */
public abstract class ErrorHandler {
    
    public int getErrorCode(String error) {
        
        return Integer.parseInt(error.substring(5));
    }
    
    public String[] getActionValues(String action) throws JDOMException, IOException {
        
        SAXBuilder builder  = new SAXBuilder();
        Document document   = (Document) builder.build(new StringReader(action));
        List<Element> childrens = document.getRootElement().getChildren();
                
        String[] values = new String[childrens.size()+1];
        
        values[0] = document.getRootElement().getAttributeValue("name");
        
        for (int i = 0; i < childrens.size(); i++) {
            values[i+1] = childrens.get(i).getAttributeValue("name");
        }
        
        return values;
    }
    
    public String generateError(String error) {
        
        String xml = "";
        
        xml += "<act>";
        xml += "<action>";
        xml += "<name>" + "remove_"+error.toLowerCase() + "</name>";
        xml += "</action>";
        xml += "</act>";
        
        return xml;
    }

    public abstract ArrayList generateDataError(NodeList predicates, String action) throws JDOMException, IOException;
}