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

package org.pelea.planners;

import org.pelea.planner.plan.Plan;
import org.pelea.planner.Planner;

public class Sayphi extends Planner 
{
    
    public Sayphi(String id) {
        super(id, "SAYPHI", true);
    }

    @Override
    public String getRePlanH(String domainH, String problem, String time) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getComand(String domainH, String problem, int type) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String getComand(String domain, String problem, String plan, int type) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Plan getPlanInfo(String File) {
        throw new UnsupportedOperationException("Not supported yet.");
    }  
}
