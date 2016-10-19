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

package org.pelea.languages;

import java.io.IOException;
import java.util.HashMap;
import org.jdom.JDOMException;
import org.pelea.languages.pddl.plan.ActionPlan;

/**
 *
 * @author moises
 */
public abstract class Plan implements Cloneable
{
    protected String name;
    protected double time;
    protected int version;
    
    public abstract String getActionXML(int position);
    public abstract ActionPlan getAction(int position);
    public abstract void deleteAction(int position);
    public abstract void deletePlan();
    public abstract String generateXML();    
    public abstract String generateXML(int position); 
    public abstract int getNumberOfActions();
    public abstract int updatePlan(String XMLPlan) throws JDOMException, IOException;
    public abstract void addVariable(String variable, String value);
    public abstract String getValue(String key);
    public abstract HashMap getVariables();
    
    public String getName() {
        return this.name;
    }
    
    public double getPlanningTime() {
        return this.time;
    }
    
    public int getVersion() {
        return this.version;
    }

}
