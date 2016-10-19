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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.pelea.planner.plan.Plan;
import org.pelea.planner.plan.PlanAction;
import org.pelea.planner.Planner;
import org.pelea.utils.Util;

public class TSB extends Planner 
{
    public TSB(String id)
    {
        super(id, "TSB", false);
        this._result_files      = new String[1];
        this._result_file_base  = this.temp + "plan_final.tmp";
        this._result_files[0]   = this._result_file_base;
    }
    
    @Override
    public Plan getPlanInfo(String fileName) 
    {
        Plan plan               = new Plan();
        String cadena           = "";
        String temp             = "";
        
        int state               = 1; //0:nothing 1:order 2:name 3:value 4:cost
        int position            = 0;
        int groups              = 0;
        
        PlanAction action       = null;
        char character;
        
        try
        {
            BufferedReader bf = new BufferedReader(new FileReader(fileName));
            
            while ((cadena = bf.readLine())!=null) 
            {
                if (cadena.contains(":"))
                {
                    action      = new PlanAction();
                    position    = 0;

                    while (position < cadena.length())
                    {
                        character = cadena.charAt(position);
                        
                        switch (character)
                        {
                            case '(': state = 2;
                                break;
                            case ')': action.addValue(temp); temp = ""; state = 0;
                                break;
                            case '[': state = 4;
                                break;
                            case ']': if (state == 4){action.setCost(Double.parseDouble(temp)); temp = ""; state = 1; plan.addAction(action);
                            }
                                break;
                            case '.': if (state == 1){action.setExecutionOrder(Integer.parseInt(temp)); temp = ""; state = 0;}
                                break;
                            case ':': 
                                if (state == 1){action.setExecutionOrder(Integer.parseInt(temp)); temp = ""; state = 0;}
                                else{temp = "";}
                                break;
                            case ' ': 
                                if (state == 2){action.setName(temp); temp = ""; state = 3;}
                                else if (state == 3){action.addValue(temp); temp = "";}      
                                break;
                            default:
                                temp += character;
                        }

                        position++;
                    }
                }
            }
            
            bf.close();
            
            return plan;
        } 
        catch (FileNotFoundException ex) 
        {
            Util.printError(this.getName(), "File " + fileName + " not found");
            return null;
        } 
        catch (IOException ex) 
        {
            Util.printError(this.getName(), "Reading file " + fileName);
            return null;
        }
    }

    @Override
    public String getRePlanH(String domainH, String problem, String time) throws Exception 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getComand(String domainH, String problem, int type) throws Exception 
    {
        return this.path + "plan " + this.path + " " + domainH + " " + problem + " " + this._result_file_base; 
    }
    
    @Override
    public String getComand(String domainH, String problem, String plan, int type) throws Exception 
    {
        return this.path + "plan " + this.path + " " + domainH + " " + problem + " " + this._result_file_base; 
    }
}
