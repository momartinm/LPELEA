/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3m.es
 * 
 * Copyright 2012, Mois�s Mart�nez
 *
 * (Questions/bug reports now to be sent to Mois�s Mart�nez)
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.pelea.planner.plan.Plan;
import org.pelea.planner.Planner;
import org.pelea.planner.plan.PlanAction;
import org.pelea.utils.Util;

public class FD extends Planner {   
    
    private final String[] tempFiles;
    private String hff;
    private String expanded;
    private String generated;
    private String cost;
    private boolean heuristic;
    
    @SuppressWarnings("empty-statement")
    public FD(String id) {
        super(id, "FD", false);
        
        this.heuristic = false;
        this.hff = "";
        this.generated = "";
        this.expanded = "";
        this.cost = "";
        this._result_files = new String[1];
        this.tempFiles = new String[]{"output.sas", "output", "plan_numbers_and_cost"};
        this._result_file_base = "sas_plan";
        this._result_files[0] = this._result_file_base;
        
    }
    
    @Override
    public String getPlanH(String domainH, String problemH) throws Exception {
       
        this.heuristic = false;
        
        String result = super.getPlanH(domainH, problemH);
        
        for (String tempFile : this.tempFiles) {
            new File(tempFile).delete();
        }
        
        new File(this.temp + "execution.sh").delete();
        
        return result;
    }
    
    @Override
    public String getRePlanH(String domainH, String problem, String time) throws Exception {
        return this.getPlanH(domainH, problem);
    }

    @Override
    public String getComand(String domain, String problem, int type) throws Exception {
        if (System.getProperty("os.name").contains("win")) {
            return "Windows does not supported";
        }
        else {
            if (this.createFileExecution(domain, problem, type))
                return "sh " + this.temp + "execution.sh";
            else
                return "echo \"Execution file could not be generared.\"";
        }
    }
    
    @Override
    public String getComand(String domain, String problem, String plan, int type) throws Exception {
        if (System.getProperty("os.name").contains("win")) {
            return "Windows does not supported";
        }
        else {
            if (this.createFileExecution(domain, problem, type))
                return "sh " + this.temp + "execution.sh";
            else
                return "echo \"Execution file could not be generared.\"";
        }
    }
    
    private boolean createFileExecution(String domainH, String problem, int type) {
        PrintWriter writer = null;
        
        try {
            new File (this.temp + "execution.sh").setExecutable(true);
            writer = new PrintWriter(this.temp + "execution.sh");
            
            writer.println("#! /bin/bash");
            writer.println("");
            writer.println("ulimit -t " + this.maxTime);
            
            writer.println(this.path + "translate/translate.py " + domainH + " " + problem);
            writer.println(this.path + "preprocess/preprocess < output.sas");
            
            if (type == 1)
                writer.println(this.path + "search/downward --heuristic \"hlm=lmcount(lm_rhw(reasonable_orders=true,lm_cost_type=2,cost_type=2),pref=true)\"  --heuristic \"hff=ff()\" --search \"lazy_greedy([hlm, hff], preferred=[hff, hlm])\"  < output");
            else if (type == 2)
                writer.println(this.path + "search/downward --heuristic \"hlm=lmcount(lm_rhw(reasonable_orders=true,lm_cost_type=2,cost_type=2),pref=true)\"  --heuristic \"hff=ff()\" --search \"lazy_greedy([hlm, hff], preferred=[hff, hlm])\"  < output");
            else if (type == 3)
                writer.println(this.path + "search/downward --heuristic \"hlm=lmcount(lm_rhw(reasonable_orders=true,lm_cost_type=2,cost_type=2),pref=true)\"  --heuristic \"hff=ff()\" --search \"lazy_greedy([hlm, hff], preferred=[hff, hlm])\"  < output");
            
            writer.close();
            
            return true;
        
        } 
        catch (IOException ex) {
            writer.close();
            return false;
        }
    }

    @Override
    public Plan getPlanInfo(String fileName) {
        
        Plan plan = new Plan();
        BufferedReader bf = null;
        String cadena = null;
        String[] elements = null;
        int order = 0;

        plan.addVariable("hff", this.hff);
        plan.addVariable("generated", this.generated);
        plan.addVariable("expanded", this.expanded);
        plan.addVariable("cost", this.cost);
        
        try {
            bf = new BufferedReader(new FileReader(fileName));
            
            while ((cadena = bf.readLine())!=null) {   
                elements = cadena.substring(1, cadena.length()-1).split(" ");
                
                PlanAction pa = new PlanAction(elements[0], order, 1);
                
                for (int i = 1; i < elements.length; i++) {
                    pa.addValue(elements[i]);
                }
                
                plan.addAction(pa);
                
                order++;
            }
            
            bf.close();
            
            return plan;
        } 
        catch (FileNotFoundException ex) {
            Util.printError(this.getName(), "File " + fileName + " not found");
            return null;
        } 
        catch (IOException ex) {
            Util.printError(this.getName(), "Reading file " + fileName);
            return null;
        }
    }
    
    @Override    
    public void analyzeOutput(String line) {
        
        if (line.contains("Best heuristic value")) {
           if (!this.heuristic) {
               this.hff = line.substring(line.indexOf(":")+1, line.indexOf("/")).trim();
               this.heuristic = true;
           }
        }
        else if (line.contains("Expanded")) {
            this.expanded = line.replace("Expanded", "").replace("state(s).", "").trim();
        }
        else if (line.contains("Generated") && line.contains("state")) {
            this.generated = line.replace("Generated", "").replace("state(s).", "").trim();
        }
        else if (line.contains("Plan length")) {
            this.cost = line.replace("Plan length: ", "").replace("step(s).", "").trim();
        }
    }
}