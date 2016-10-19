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

package org.pelea.utils.experimenter.result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;

public class ProblemNode implements Comparable {
    
    private final String name;
    private final int code;
    private final List<PlannerNode> planners;
    
    public ProblemNode(String name) {
        this.name = name;
        
        name += "0";
        
        this.code = Integer.parseInt(name.replaceAll("[^\\d.]", ""));
        this.planners = new ArrayList<PlannerNode>();
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getCode() {
        return this.code;
    }
    
    private PlannerNode getPlanner(int horizon) {
        for (PlannerNode planner : this.planners) {
            if (planner.getHorizon() == horizon) {
                return planner;
            }
        }
        return null;
    }
    
    public void addExperiment(Element node, int horizon) {
       
        PlannerNode planner = this.getPlanner(horizon);
        
        if (planner == null) {
            planner = new PlannerNode(horizon);
            planner.addExperiment(node);
            this.planners.add(planner);
        }
        else
            planner.addExperiment(node);
    }
    
    public String printData(int option, int horizon) {

        PlannerNode p = this.getPlanner(horizon);
        
        if (p != null) {
            switch (option) {
                case 1: return p.getFirstPlanningTime();
                case 2: return p.getPlanningTime();
                case 3: return p.getReplanningEpisodes();
                case 4: return p.getNumActions();
                case 5: return p.getExperiments();
            }
        }
        
        return "-";
    }
    
    private int getMax() {
        int max = 0;
        
        for (int i = 0; i < this.planners.size(); i++) {
            if (max < this.planners.get(i).getSize(0))
                max = this.planners.get(i).getSize(0);
        }
        
        return max;
    }
    
    private int getMin() {
        int min = 0;
        
        for (int i = 0; i < this.planners.size(); i++) {
            if (min > this.planners.get(i).getSize(0))
                min = this.planners.get(i).getSize(0);
        }
        
        return min;
    }
    
    public String generateName(int position) {
        if (this.planners.get(position).getHorizon() == 1000000)
            return "FD";
        else
            return "AKFD(" + this.planners.get(position).getHorizon() + ")";
    }
    
    public void generateGraph(int horizons, String folder) throws IOException {
        
        DecimalFormat structure = new DecimalFormat("0.00"); 
        
        List<Integer> vHorizon = new ArrayList<Integer>();
        int i = 0;
        
        if (horizons == this.planners.size()) {
            
            for (i = 0; i < this.planners.size(); i++) {
                vHorizon.add(this.planners.get(i).getHorizon());
            }
                
            BufferedWriter buffer = new BufferedWriter(new FileWriter(folder + "/" + "data_" + this.getName() + ".dat"));
            
            int max = this.getMin();
            double maxTime = 0.0;
            
            for (i = 0; i < max; i++) {
                
                buffer.write(i + " ");
                
                for (int j = 0; j < this.planners.size(); j++) {
                    
                    if (maxTime < this.planners.get(j).getEpisode(0, i))
                        maxTime = this.planners.get(j).getEpisode(0, i);
                    
                    buffer.write(structure.format(this.planners.get(j).getEpisode(0, i)).replace(",", ".") + " ");
                    buffer.write(this.planners.get(j).getHFF(0, i) + " ");
                    buffer.write(this.planners.get(j).getGoals(0, i) + " ");
                }
                
                buffer.newLine();
            }
            
            buffer.write(i + " ");
            
            for (i = 0; i < this.planners.size(); i++) {
                buffer.write("0.00 ");
            }
            
            buffer.close();
            
            Collections.sort(vHorizon);
            
            BufferedWriter gnu = new BufferedWriter(new FileWriter(folder + "/" + this.getName() + ".gp"));
            
            gnu.write("set terminal pdf");
            gnu.newLine();
            gnu.write("set output '" + this.getName() + ".pdf'");
            gnu.newLine();
            gnu.write("set grid");
            gnu.newLine();
            gnu.write("set xlabel \"Planning iteration\"");
            gnu.newLine();
            gnu.write("set xrange [0:" + (max+5) + ".0]");
            gnu.newLine();
            gnu.write("set ylabel \"Planning Time (Seconds)\"");
            gnu.newLine();
            gnu.write("set yrange [0:" + (maxTime+5) + "]");
            gnu.newLine();
            gnu.write("set title \"Average planning time for the whole cycle of planning and execution\"");
            gnu.newLine();
            gnu.write("plot ");
            
            for (i = 0; i < vHorizon.size(); i++) {
                for (int j = 0; j < this.planners.size(); j++) {
                    if (vHorizon.get(i) == this.planners.get(j).getHorizon()) {
                        if ((i+1) == vHorizon.size())
                            gnu.write("\"data_" + this.getName() + ".dat\" using 1:" + (j+2) + " with lines title \"" + this.generateName(j) + "\" ");
                        else    
                            gnu.write("\"data_" + this.getName() + ".dat\" using 1:" + (j+2) + " with lines title \"" + this.generateName(j) + "\", \\");
                        gnu.newLine();
                        break;
                    }
                }
            }
            
            gnu.close();
            
            BufferedWriter execution = new BufferedWriter(new FileWriter(folder + "/" + this.getName() + ".sh"));

            execution.write("#! /bin/bash");
            execution.newLine();
            execution.write("gnuplot " + folder + "/" + this.getName() + ".gp");
            execution.newLine();
            
            execution.close();

            Runtime rt   = Runtime.getRuntime();
            Process p    = rt.exec("sh " + folder + "/" + this.getName() + ".sh");
            try {
                int exitVal = p.waitFor();

            } catch (InterruptedException ex) {
                Logger.getLogger(ProblemNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //new File("data_" + this.getName() + ".dat").delete(); 
            //new File(this.getName() + ".gp").delete();     
            new File(folder + "/" + this.getName() + ".sh").delete();
        }
    }

    @Override
    public int compareTo(Object t) {
        if (this.getCode() > ((ProblemNode) t).getCode())
            return 1;
        return 0;
    }
}
