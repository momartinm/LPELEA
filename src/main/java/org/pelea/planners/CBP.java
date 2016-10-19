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
import org.pelea.planner.Planner;
import org.pelea.planner.plan.Plan;
import org.pelea.planner.plan.PlanAction;
import org.pelea.utils.Util;

public class CBP extends Planner
{
    private int numFiles = 1;
    
    public CBP(String id)
    {
        super(id, "CBP-ROLLER", false);
        
        this._result_file_base  = this.temp + "result";
        this._result_files      = new String[numFiles];
        
        for (int i = 0; i < numFiles; i++) {
            this._result_files[i]   = this._result_file_base + "." + (i+1);
        }
    }

    @Override
    public String getComand(String domain, String problem, int type) throws Exception {
        if (System.getProperty("os.name").indexOf( "win" ) >= 0) {
            return "echo \"No windows command\"";
        }
        else //Linux command
        {
            if (this.output) {
                return this.path + "cbp-roller -o " + domain + " -f " + problem;
            }
            else {
                return this.path + "cbp-roller -o " + domain + " -f " + problem + " -F " + this._result_file_base;
            } 
        }
    }
    
    @Override
    public String getComand(String domain, String problem, String plan, int type) throws Exception {
        if (System.getProperty("os.name").indexOf( "win" ) >= 0) {
            return "echo \"No windows command\"";
        }
        else //Linux command
        {
            if (this.output) {
                return this.path + "cbp-roller -o " + domain + " -f " + problem;
            }
            else {
                return this.path + "cbp-roller -o " + domain + " -f " + problem + " -F " + this._result_file_base;
            } 
        }
    }

    @Override
    public String getRePlanH(String domainH, String problem, String time) throws Exception 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Plan getPlanInfo(String fileName) 
    {
        Plan plan               = new Plan();
        String cadena           = "";
        String temp             = "";
        BufferedReader bf       = null;
        int position            = 0;
        int order               = 0;

        char character;
        
        Boolean proccesingPlan  = false;
        
        try
        {
            bf = new BufferedReader(new FileReader(fileName));
            
            while ((cadena = bf.readLine())!=null) 
            {
                proccesingPlan = (proccesingPlan || cadena.contains("0:")) ? true:false;

                if (proccesingPlan)
                {
                    position = cadena.indexOf(":");
                    
                    if (position > 0)
                    {
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
                        
                        plan.addAction(action);
                    }
                    else 
                    {
                        proccesingPlan = false;
                    } 
                }
                else {
                    if (cadena.contains(";Time:")) {
                        plan.setTime(Long.parseLong(cadena.trim().substring(6)));
                    }
                    else if (cadena.contains(";Cost:")) {
                        plan.setCost(Integer.parseInt(cadena.trim().substring(6)));
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