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

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import org.pelea.core.module.Module;
import org.pelea.wrappers.DecisionSupport;
import org.pelea.planner.Planner;
import org.pelea.utils.Util;

/**
 *
 * @author moises
 */
public class DecissionSupportBasic extends DecisionSupport
{

    public DecissionSupportBasic(String name) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotBoundException, MalformedURLException, RemoteException {
        super(name,Module.RMI);
    }

    @Override
    public String RepairOrReplan(String stateH, String domainH, String planH) {
        String plan     = "";
        int position    = 0;
        
        try 
        {    
            position    = this.getPlanner(Planner.MODE_REPLANING);
            plan        = this.planners.get(position).getRePlanH(domainH, stateH, planH);
        
            return plan;
        
        } 
        catch (Exception ex) 
        {
            Util.printError(this.getName(), "Generating plan of actions (" + ex.toString() + ")");
            
            plan += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
            plan += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
            plan += "<error>";
            plan += "Replanning using planner " + this.planners.get(position).getName();
            plan += "</error>";
            plan += "</define>";
            
            return plan;
        }
    }

    @Override
    public String getPlanHInfoMonitor(String stateH, String domainH) {
        String plan     = "";
        int position    = 0;
        
        try {    
            position    = this.getPlanner(Planner.MODE_PLANING);
            plan = this.planners.get(position).getPlanH(domainH, stateH);
            
            return plan;
        } 
        catch (Exception ex) {
            Util.printError(this.getName(), "Generating plan of actions (" + ex.toString() + ")");
            
            plan += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
            plan += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
            plan += "<error>";
            plan += "Generating plan of actions using planner " + this.planners.get(position).getName();
            plan += "</error>";
            plan += "</define>";
            
            return plan;
        }
    }
}
