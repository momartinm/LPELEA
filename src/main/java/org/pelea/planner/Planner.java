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

package org.pelea.planner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.pelea.core.configuration.configuration;
import org.pelea.languages.pddl.PDDLPlan;
import org.pelea.planner.plan.Plan;
import org.pelea.planner.plan.PlanAction;
import org.pelea.utils.Util;
import xml2pddl.XML2PDDL;

public abstract class Planner {
    
    public static final int MODE_PLANING   = 1;
    public static final int MODE_REPLANING  = 2;
    
    protected String path;
    protected String _result_file_base;
    protected String _result_files[];
    protected String name;
    protected String id;
    protected String temp;
    protected int mode;
    protected boolean output;
    protected boolean deleteFiles;
    protected long time;
    protected String maxTime;
    protected List<Double> times;
    
    public Planner(String id, String name, boolean output) {
        this.id = id;
        this.name = name;
        this.temp = configuration.getInstance().getParameter("GENERAL", "TEMP_DIR");
        this.path = configuration.getInstance().getParameter(id, "PLANNER_DIR");
        this.mode = Integer.parseInt(configuration.getInstance().getParameter(id, "PLANNER_MODE"));
        this.deleteFiles = configuration.getInstance().getParameter(id, "DELETE_FILES") != null ? Boolean.parseBoolean(configuration.getInstance().getParameter(id, "DELETE_FILES")):true;
        this.maxTime = configuration.getInstance().getParameter(id, "MAX_PLANNING_TIME") != null ? configuration.getInstance().getParameter(id, "MAX_PLANNING_TIME"):"unlimited";
        this.output = output;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getResultFile(int position) {
        return this._result_files[position];
    }
    
    public void setResultFile(String path, int position) {
        this._result_files[position] = path;
    }
    
    public String getResultFileBase() {
        return this._result_file_base;
    }
    
    public void setResultFileBase(String name) {
        this._result_file_base = name;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public void createFile(String content, String path) {
        BufferedWriter file;

        try 
        {
            file = new BufferedWriter(new FileWriter(path));
            file.write(content);
            file.close();
        } 
        catch (IOException e) 
        {
            Util.printError(this.getName(), "Error saving file " + path);
        }
    }
    
    public Boolean execCommand(String command, String[] resultFiles) {
        long start_time, end_time;
        boolean result  = true;
        String buffer = "";
        this.times = new ArrayList<Double>();
        
        Util.printDebug(this.getName(), "Executing command [" + command + "]");
	
	try 
        {
            start_time = System.currentTimeMillis();
            
            Runtime rt   = Runtime.getRuntime();
            
            Process p    = rt.exec(command.split(" "));
            
            BufferedReader stdError  = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            BufferedReader stdInput  = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            
            while ((line = stdInput.readLine()) != null) {
                
                this.analyzeOutput(line);
                
                if (this.output) {
                    buffer += line + "\n";
                }
                else {
                    Util.printDebug(this.getName(), line);
                }
            }
            
            while ((line = stdError.readLine()) != null) {
                this.analyzeOutput(line);
                
                if (this.output) {
                    buffer += line + "\n";
                }
                else {
                    Util.printDebug(this.getName(), line);
                }
                
            }
            
            int exitVal = p.waitFor();
            
            end_time = System.currentTimeMillis();
            
            this.time = end_time - start_time;
                 
            if (this.output) {
                result = this.saveOutput(buffer);
            }
        }
        catch (IOException ex) {
            Util.printError(this.getName(), "Executing command [" + command + "]");
            result = false;
        } catch (InterruptedException ex) {
            Util.printError(this.getName(), "Executing command [" + command + "]");
            result = false;
        }

        return result;
    }
    
    public Boolean execMultipleCommand(String[] command, String[] resultFiles) {
        
        long start_time, end_time;
        boolean result  = true;
        
        for (int i = 0; i < command.length; i++) {
            Util.printDebug(this.getName(), "Executing command [" + command[i] + "]");
        }

        return result;
    }
    
    /**
     *
     * @param domainH
     * @param problemH
     * @return
     * @throws Exception
     */
    public String getPlanH(String domainH, String problemH, String planH) throws Exception {
        
        String command = "";
        String result = "";
        String xml = "";
        boolean planFound = false;
        
        String domain_path  = this.temp + "domain.pddl";
        String problem_path = this.temp + "problem.pddl";
        String plan_path = this.temp + "plan.pddl";
        
        // Convertimos a ppdl el dominio
        
        try {
            
            if ((configuration.getInstance().getParameter("GENERAL", "TEMPORAL") != null) && (configuration.getInstance().getParameter("GENERAL", "TEMPORAL").equals("YES"))) {
                this.createFile(XML2PDDL.convertDomainTemporal(domainH), domain_path);
                this.createFile(XML2PDDL.convertProblemTemporal(problemH), problem_path);
                this.createFile(PDDLPlan.convert(planH), plan_path);
            }
            else {
                this.createFile(XML2PDDL.convertDomain(domainH), domain_path);
                this.createFile(XML2PDDL.convertProblem(problemH), problem_path);
                this.createFile(PDDLPlan.convert(planH), plan_path);
            }
            
            Util.printDebug(this.getName(), "Generating temporal files by domain and problem in PDDL");
        } 
        catch (Exception e) {
            result = Util.generateMessageError("3", "Domain or problem could not be converted into PDDL");  
            return result;
        }

        try {
            command = this.getComand(domain_path, problem_path, plan_path, 0);

            if (this.execCommand(command, this._result_files)) {
                Util.printDebug(this.getName(), "Plan generated correctlly");

                for (String resultFile : this._result_files) {
                    Plan plan = this.getPlanInfo(resultFile);
                    
                    if (plan != null) {
                        plan.setTime(this.time);
                        System.out.println(this.time);
                        xml += "<plans name=\"xPddlPlan\" domain=\" " + XML2PDDL.getNameDomain(domainH) + "\">";
                        xml += plan.generateXML(domainH, problemH);
                        xml += "</plans>";
                        planFound = true;
                    }
                }
                
                if (planFound) {
                    result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                    result += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
                    result += xml;
                    result += "</define>";
                }
                else {
                    result = Util.generateMessageError("1", "No solution founded");
                }
                
            } 
            else {
                result = Util.generateMessageError("1", "No solution founded");
            }
        }
        catch (IOException e) {
            result = Util.generateMessageError("9", "No plan files founded");
        }
        
        if (this.deleteFiles) {
            new File(domain_path).delete();
            new File(problem_path).delete();
        
            for (int i = 0; i < this._result_files.length; i++)        
                new File(this.getResultFile(i)).delete();
        }
        
        return result;
    }
    
    /**
     *
     * @param domainH
     * @param problemH
     * @return
     * @throws Exception
     */
    public String getPlanH(String domainH, String problemH) throws Exception {
        String command = "";
        String result = "";
        String xml = "";
        boolean planFound = false;
        
        String domain_path  = this.temp + "domain.pddl";
        String problem_path = this.temp + "problem.pddl";
        
        // Convertimos a ppdl el dominio
        
        try 
        {
            if ((configuration.getInstance().getParameter("GENERAL", "TEMPORAL") != null) && (configuration.getInstance().getParameter("GENERAL", "TEMPORAL").equals("YES"))) 
            {
                this.createFile(XML2PDDL.convertDomainTemporal(domainH), domain_path);
                this.createFile(XML2PDDL.convertProblemTemporal(problemH), problem_path);
            }
            else 
            {
                this.createFile(XML2PDDL.convertDomain(domainH), domain_path);
                this.createFile(XML2PDDL.convertProblem(problemH), problem_path);
            }
            
            Util.printDebug(this.getName(), "Generating temporal files by domain and problem in PDDL");
        } 
        catch (Exception e) {
            result = Util.generateMessageError("3", "Domain or problem could not be converted into PDDL");  
            return result;
        }

        try
        {
            command = this.getComand(domain_path, problem_path, 1);

            if (this.execCommand(command, this._result_files)) 
            {
                Util.printDebug(this.getName(), "Plan generated correctlly");

                for (String resultFile : this._result_files) {
                    Plan plan = this.getPlanInfo(resultFile);
                    
                    if (plan != null) {
                        plan.setTime(this.time);
                        xml += "<plans name=\"xPddlPlan\" domain=\" " + XML2PDDL.getNameDomain(domainH) + "\">";
                        xml += plan.generateXML(domainH, problemH);
                        xml += "</plans>";
                        planFound = true;
                    }
                }
                
                if (planFound) {
                    result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                    result += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
                    result += xml;
                    result += "</define>";
                }
                else {
                    result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                    result += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
                    result += "<plans name=\"xPddlPlan\" domain=\" " + XML2PDDL.getNameDomain(domainH) + "\">";
                    result += "<plan time=\"" + this.time + "\"/>";
                    result += "</plans>";
                    result += "</define>";
                }
                
            } 
            else {
                result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                result += "<define xmlns=\"http://www.pelea.org/xPddl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.pelea.org/xPddl xPddl.xsd\">";
                result += "<plan time=\"" + this.time + "\"/>";
                result += "</define>";
            }
        }
        catch (IOException e) {
            result = Util.generateMessageError("9", "No plan files founded");
        }
        
        new File(domain_path).delete();
        new File(problem_path).delete();
        
        for (int i = 0; i < this._result_files.length; i++)        
            new File(this.getResultFile(i)).delete();
        
        return result;
    }
            
    
    /*
     * This method analyze plan generated by the planner. This analyzer use the 
     * output of the IPC. If the planner uses other output, this method must 
     * to be overwrite. 
     */
    public Plan getPlanInfo(String fileName) {
        Plan plan               = new Plan();
        String cadena           = "";
        String temp             = "";
        
        int state               = 1; //0:nothing 1:order 2:name 3:value 4:cost
        int position            = 0;
        int groups              = 0;
        
        PlanAction action;
        char character;
        
        try {
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
    
    public abstract String getRePlanH(String domain, String problem, String plan) throws Exception;
    public abstract String getComand(String domain, String problem, int type) throws Exception;
    public abstract String getComand(String domain, String problem, String plan, int type) throws Exception;
    
    public Boolean saveOutput(String data) {
        String[] lines = data.split("\n");
        
        try
        {
            PrintWriter file = new PrintWriter(new FileWriter(this._result_file_base));

            for (String line : lines) {
                file.println(line);
            }
            
            file.close();
        } 
        catch (IOException e) {
            Util.printError(this.getName(), "Saving command output");
            return false;
        }
        
        return true;
    }
    
    public void analyzeOutput(String line) {
    }
}
