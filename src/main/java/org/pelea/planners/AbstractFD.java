/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3m.es
 * 
 * Copyright 2012, Mois?s Mart?nez
 *
 * (Questions/bug reports now to be sent to Mois?s Mart?nez)
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
import java.io.IOException;
import java.io.PrintWriter;
import org.pelea.core.configuration.configuration;
import org.pelea.planner.plan.Plan;
import org.pelea.planner.Planner;
import org.pelea.planner.plan.PlanAction;
import org.pelea.utils.Util;

public class AbstractFD extends Planner 
{   
    private String[] tempFiles;
    private String horizon;
    private final boolean useFile;
    private final String abstractionFile;
    
    @SuppressWarnings("empty-statement")
    public AbstractFD(String id) {
        super(id, "AFD", false);
        
        this._result_files = new String[1];
        this.tempFiles = new String[4];
        this._result_file_base = "sas_plan";
        this._result_files[0] = this._result_file_base;
        this.tempFiles = new String[]{"output.sas", "output", "plan_numbers_and_cost"};
        this.useFile = Boolean.parseBoolean(configuration.getInstance().getParameter(this.id, "USE_FILE"));
        this.abstractionFile = (this.useFile) ? configuration.getInstance().getParameter(this.id, "ABSTRACTION_FILE"):"abstractions.sas";
    }
    
    @Override
    public String getPlanH(String domainH, String problemH) throws Exception {
        String result = super.getPlanH(domainH, problemH);
        
        for (String tempFile : this.tempFiles) {
            new File(tempFile).delete();
        }
        
        new File(this.temp + "execution.sh").delete();
        
        return result;
    }
    
    @Override
    public String getRePlanH(String domain, String problem, String plan) throws Exception {
        String result = super.getPlanH(domain, problem);
        
        for (String tempFile : this.tempFiles) {
            new File(tempFile).delete();
        }
        
        //new File(this.temp + "execution.sh").delete();
        
        return result;
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
    public String getComand(String domain, String problem, String Plan, int type) throws Exception {
        
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
    
    private boolean createFileExecution(String domainH, String problem, int type)
    {
        PrintWriter writer = null;
        String options = "";
        
        try 
        {
            new File (this.temp + "execution.sh").setExecutable(true);
            writer = new PrintWriter(this.temp + "execution.sh");
            writer.println("#! /bin/bash");
            writer.println("");
            writer.println("ulimit -t " + this.maxTime);
            
            if (this.useFile) {
                writer.println(options + this.path + "translate/translate.py " + domainH + " " + problem);
                writer.println("cat " + this.abstractionFile + " >> output.sas");
                writer.println(options + this.path + "preprocess/preprocess --use-abstractions < output.sas");
                writer.println(options + this.path + "search/downward --heuristic \"hff=ffabs()\" --search \"lazy_greedy([hff], preferred=[hff])\" --abstractions " + this.horizon + " < output");
            }
            else {
                if (this.mode == type) {
                    writer.println("rm " + this.path + "abstractions.sas");
                    writer.println(options + this.path + "translate/translate.py " + domainH + " " + problem);
                    writer.println(options + this.path + "preprocess/preprocess < output.sas");
                    writer.println(options + this.path + "search/downward --heuristic \"hlm=lmcount(lm_exhaust(cost_type=NORMAL, reasonable_orders=false, only_causal_landmarks=false, disjunctive_landmarks=true, conjunctive_landmarks=true, no_orders=false, lm_cost_type=NORMAL),pref=true)\" --generate-lm-abstractions < output");
                    writer.println("cat " + this.abstractionFile + " >> output.sas");
                    writer.println(options + this.path + "preprocess/preprocess --use-abstractions < output.sas");
                    writer.println(options + this.path + "search/downward --heuristic \"hff=ffabs()\" --search \"lazy_greedy([hff], preferred=[hff])\" --abstractions " + this.horizon + " < output");
                }
                else {
                    writer.println(options + this.path + "translate/translate.py " + domainH + " " + problem);
                    writer.println("cat " + this.abstractionFile + " >> output.sas");
                    writer.println(options + this.path + "preprocess/preprocess --use-abstractions < output.sas");
                    writer.println(options + this.path + "search/downward --heuristic \"hff=ffabs()\" --search \"lazy_greedy([hff], preferred=[hff])\" --abstractions " + this.horizon + " < output");
                }
            }
                
            writer.close();
            return true;
        
        } 
        catch (IOException ex) 
        {
            writer.close();
            return false;
        }
    }
    
    public void setHorizon(String horizon) {
        this.horizon = horizon;
    }
    
    public String getHorizon() {
        return this.horizon;
    }

    @Override
    public Plan getPlanInfo(String fileName) {
        
        Plan plan               = new Plan();
        BufferedReader bf       = null;
        String cadena           = null;
        String[] elements       = null;
        int order               = 0;
        
        try
        {
            bf = new BufferedReader(new FileReader("hff"));
            plan.addVariable("hff", bf.readLine());
            bf.close(); 
            new File("hff").delete();
        } 
        catch (Exception ex) 
        {
            //Nothing to do
        }
        
        try
        {
            bf = new BufferedReader(new FileReader(fileName));
            
            while ((cadena = bf.readLine())!=null) 
            {   
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