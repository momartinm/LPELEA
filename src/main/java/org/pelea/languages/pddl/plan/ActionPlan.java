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

package org.pelea.languages.pddl.plan;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.pelea.languages.pddl.structures.NodeList;

public class ActionPlan {
    
    private final NodeList actions;
    
    public ActionPlan()
    {
        this.actions = new NodeList();
    }
    
    public void addAction(Element predicate)
    {
        this.actions.add(new Action(predicate));
    }
    
    public int size()
    {
        return this.actions.size();
    }
    
    public Action get(int position)
    {
        return (Action) this.actions.get(position);
    }
    
    public String getXml()
    {
        String code = "";
        
        //code += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        code += "<action-plan num_actions=\"" + this.actions.size() + "\">";
	
        for (int i = 0; i < this.actions.size(); i++)
            code += this.actions.getXml(false);
	
        code += "</action-plan>";       
        
        return code;
    }
    
    public String getXmlByPosition(int position)
    {
        String code = "";
        
        //code += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        code += "<action-plan num_actions=\"1\">";
	code += this.actions.get(position).getXml();
        code += "</action-plan>"; 
        
        return code;
    }
    
    public List getPDDLActions() {
        
        ArrayList<String> PDDLactions = new ArrayList<String>();
        
        for (int i = 0; i < this.actions.size(); i++) {
            PDDLactions.add(((Action) this.actions.get(i)).printPDDL());
        }
        
        return PDDLactions;
    }
}
