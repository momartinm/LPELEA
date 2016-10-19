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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.pelea.core.configuration.configuration;
import org.pelea.planner.Planner;
import org.pelea.planner.plan.Plan;
import org.pelea.planner.plan.PlanAction;
import org.pelea.utils.Util;

public class LPGAdapt extends Planner {

    private final int SPEED = 1;
    private final int QUALITY = 2;
    
    private final int executionMode;
    private int plans;
    private final boolean parallel;
    
    
    @SuppressWarnings("empty-statement")
    public LPGAdapt(String id) {
        
        super(id, "LPG-Adapt-Speed", true);
        
        this._result_files = new String[this.plans];
        this._result_file_base = "lpg_plan";
        this.executionMode =  (configuration.getInstance().getParameter(id, "REPAIR_MODE") != null) ?Integer.parseInt(configuration.getInstance().getParameter(id, "REPAIR_MODE")):SPEED;
        this.output = true;
        this.parallel = false;
        
        if (this.executionMode == QUALITY) {
            this.plans = 100; 
        }
        else {
            this.plans = (configuration.getInstance().getParameter(id, "NUMBER_OF_PLANS") != null) ?Integer.parseInt(configuration.getInstance().getParameter(id, "NUMBER_OF_PLANS")):1;
        }
    }

    @Override
    public String getRePlanH(String domainH, String problem, String plan) throws Exception {
        return this.getPlanH(domainH, problem, plan);
    }
    
    /*public String getPlanH(String domainH, String problemH, String planH) throws Exception {
        String command = "";
        String result = "";
        
        String domain_path  = this.temp + "domain.pddl";
        String problem_path = this.temp + "problem.pddl";
        String plan_path = this.temp + "plan.pddl";
        
        // Convertimos a ppdl el dominio
        
        try {
            if ((configuration.getInstance().getParameter("GENERAL", "TEMPORAL") != null) && (configuration.getInstance().getParameter("GENERAL", "TEMPORAL").equals("YES"))) {
                this.createFile(XML2PDDL.convertDomainTemporal(domainH), domain_path);
                this.createFile(XML2PDDL.convertProblemTemporal(problemH), problem_path);
            }
            else {
                this.createFile(XML2PDDL.convertDomain(domainH), domain_path);
                this.createFile(XML2PDDL.convertProblem(problemH), problem_path);
                this.createFile(PDDLPlan.convert(planH), plan_path);
            }
            
            Util.printDebug(this.getName(), "Generating temporal files by domain and problem in PDDL");
        } 
        catch (Exception e) {
            result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            result += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
            result += "<error>";
            result += "<type>3</type>";
            result += "<message>Domain or problem could not be converted into PDDL</message>";
            result += "</error>";
            result += "</define>";
            
            return result;
        }
        
        if (this.createFileExecution(domain_path, problem_path, plan_path, 1)) {
            try {
                if (this.execCommand(this.getComand(domain_path, problem_path, 1), this._result_files)) {
                    Util.printDebug(this.getName(), "Plan generated correctly");
                    
                    for (int i = 0; i < this._result_files.length; i++)
                    {
                        Plan plan = this.getPlanInfo(this._result_files[i]);
                        plan.setTime(this.time);
                        
                        result += "<plans name=\"xPddlPlan\" domain=\" " + XML2PDDL.getNameDomain(domainH) + "\">";
                        result += plan.generateXML(domainH, problemH);
                        result += "</plans>";
                    }
                }
                else
                {
                    result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                    result += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
                    result += "<error>";
                    result += "<type>1</type>";
                    result += "<message>No solution founded</message>";
                    result += "</error>";
                    result += "</define>";
                }

            }
            catch (IOException e)
            {
                result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                result += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
                result += "<error>";
                result += "<type>9</type>";
                result += "<message>No plan files founded</message>";
                result += "</error>";
                result += "</define>";
            }

            new File(domain_path).delete();
            new File(problem_path).delete();

            for (int i = 0; i < this._result_files.length; i++) 
            {
                new File(this.getResultFile(i)).delete();
            }
        }
        else {
            result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            result += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
            result += "<error>";
            result += "<type>12</type>";
            result += "<message>Error execution planning process</message>";
            result += "</error>";
            result += "</define>";
        }
        
        return result;
    }*/

    @Override
    public String getComand(String domain, String problem, int type) throws Exception {
        if (System.getProperty("os.name").contains("win")) {
            return "Windows does not supported";
        }
        else {
            this.createFileExecution(domain, problem, "", 1);
            return "sh " + this.temp + "lpg-adapt-execution.sh";      
        }
    }
    
    @Override
    public String getComand(String domain, String problem, String plan, int type) throws Exception {
        if (System.getProperty("os.name").contains("win")) {
            return "Windows does not supported";
        }
        else {
            this.createFileExecution(domain, problem, plan, 1);
            return "sh " + this.temp + "lpg-adapt-execution.sh";      
        }
    }
    
    private boolean createFileExecution(String domainH, String problem, String plan, int type) {
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter(new FileWriter(this.temp + "lpg-adapt-execution.sh"));
            
            writer.println("#! /bin/bash");
            writer.println("");
            
            if (this.executionMode == SPEED)        
                writer.println(this.path + "lpg-adapt speed -o " + domainH + " -f " + problem + " -input_plan " + plan + " -n " + this.plans + " -cputime " + this.maxTime + " -adapt_all_diff -out " + this._result_file_base);
            else
                writer.println(this.path + "lpg-adapt quality -o " + domainH + " -f " + problem + " -input_plan " + plan + " -n " + this.plans + " -cputime " + this.maxTime + " -adapt_all_diff -out " + this._result_file_base);

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
        String cadena;
        String[] elements = null;
        int order = 0;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(fileName));
            
            while ((cadena = bf.readLine())!=null) { 

                if (cadena.length() > 0) {
                    if (!cadena.substring(0, 1).contains(";")) {
                        
                        cadena = cadena.replace("(", "");
                        cadena = cadena.replace(")", "");
                        cadena = cadena.replace(":", "");
                        cadena = cadena.replace("[", "");
                        cadena = cadena.replace("]", "");
                        
                        elements = cadena.substring(0).split(" ");
                        //order = Integer.parseInt(elements[0]);
                        
                        PlanAction pa = new PlanAction(elements[3], order, Integer.parseInt(elements[elements.length-1]));
                        
                        for (int i = 4; i < elements.length-1; i++) {
                            pa.addValue(elements[i]);
                        }
                        
                        order++;
                        plan.addAction(pa);
                    }
                }   
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
    public Boolean saveOutput(String data) {
        
        String[] files = data.split("Plan file:");

        if (files.length > 1) {
            
            this._result_files = new String[1];
            
            if (files[files.length-1].indexOf(".SOL") > 0) {
                this._result_files[0] = this._result_file_base + "_" + (files.length-1) + ".SOL";
                return true;
            }
            //files[files.length-1].substring(0, files[files.length-1].indexOf(".SOL")+4).trim();        
            return false;
        }
        
        return false;
    }
}
