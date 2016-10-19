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
        
public class MetricFF extends Planner
{
    public MetricFF(String id)
    {
        super(id, "METRIC-FF", true);
        
        this._result_files      = new String[1];
        this._result_file_base  = this.temp + "plan_final.tmp";
        this._result_files[0]   = this._result_file_base;
    }

    @Override
    public String getComand(String domain, String problem, int type) throws Exception {
        if (System.getProperty("os.name").contains("win")) {
            return "echo \"No windows command\"";
        }
        else {
            return this.path + "ff -o " + domain + " -f " + problem;      
        }
    }
    
    @Override
    public String getComand(String domain, String problem, String plan, int type) throws Exception {
        if (System.getProperty("os.name").contains("win")) {
            return "echo \"No windows command\"";
        }
        else {
            return this.path + "ff -o " + domain + " -f " + problem;      
        }    }

    @Override
    public String getRePlanH(String domainH, String problem, String time) throws Exception 
    {
        return this.getPlanH(domainH, problem);
    }
    
    @Override
    public Plan getPlanInfo(String fileName) 
    {
        Plan plan               = new Plan();
        String cadena           = "";
        String temp             = "";
        BufferedReader bf       = null;
        int position            = 0;
        int auxiliar            = 0;
        int order               = 0;

        char character;
        
        Boolean proccesingPlan  = false;
        
        try
        {
            bf = new BufferedReader(new FileReader(fileName));
            
            while ((cadena = bf.readLine())!=null) 
            {
                proccesingPlan = (proccesingPlan || cadena.contains("0:")) ? true:false;

                if (proccesingPlan) {
                    position = cadena.indexOf(":");
                    
                    System.out.println(cadena);
                    
                    if (position > 0) {                  
                        PlanAction action = null;
                     
                        position++;
                        
                        while (position < cadena.length())
                        {
                            character = cadena.charAt(position);
                            
                            if ((character != '(') && (character != ')'))
                            {
                                if (character == ' ')
                                {
                                    if (!"".equals(temp))
                                    {
                                        if (action != null) {action.addValue(temp);}
                                        else {action = new PlanAction(temp, order); order++;}
                                    
                                        temp = ""; 
                                    }
                                }        
                                else
                                    temp += character;
                            }        
                            
                            position++;
                        }
                        
                        if (!temp.isEmpty()) {
                            action.addValue(temp);
                            temp = "";
                        }
                        
                        plan.addAction(action);
                    }
                    else 
                    {
                        proccesingPlan = false;
                    } 
                }
                else
                {
                    if (cadena.contains("total time")) {
                        auxiliar = cadena.indexOf("seconds");
                        plan.setTime(Double.parseDouble(cadena.substring(0, auxiliar).trim()));
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
}

