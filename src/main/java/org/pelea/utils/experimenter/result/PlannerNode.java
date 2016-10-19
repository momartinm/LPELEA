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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;

public class PlannerNode {
    
    private final int horizon;
    private final List<ExperimentNode> experiments;
    private int realSize;
    //private final List<Problem> problems;
    
    public PlannerNode(int horizon) {
        this.horizon = horizon;
        this.experiments = new ArrayList<ExperimentNode>();
        this.realSize = 0;
    }
    
    public int getHorizon() {
        return this.horizon;
    }
    
    public void addExperiment(Element node) {
        
        ExperimentNode exp;
        
        if (node.getChild("Horizon") != null) {
            exp = new ExperimentNode(Double.parseDouble(node.getChild("TOTALTIME").getValue()), 
                                Integer.parseInt(node.getChild("REPLANING").getValue()), 
                                Integer.parseInt(node.getChild("EXECUTEDACTIONS").getValue()),
                                Double.parseDouble(node.getChild("FIRSTPLANNINGTIME").getValue()),
                                Integer.parseInt(node.getChild("Horizon").getValue()),
                                Integer.parseInt(node.getChild("RESULT").getValue()));
        }
        else {
            exp = new ExperimentNode(Double.parseDouble(node.getChild("TOTALTIME").getValue()), 
                                Integer.parseInt(node.getChild("REPLANING").getValue()), 
                                Integer.parseInt(node.getChild("EXECUTEDACTIONS").getValue()),
                                Double.parseDouble(node.getChild("FIRSTPLANNINGTIME").getValue()),
                                Integer.parseInt(node.getChild("RESULT").getValue()));
        }
        
        if (exp.getResult() == 1) {
            this.realSize++;
        }
        
        List<Element> episodes = node.getChild("EPISODES").getChildren();
    
        for (int i = 0; i < episodes.size(); i++) {
            exp.addEpisode(Double.parseDouble(episodes.get(i).getChild("TIME").getValue()), (episodes.get(i).getChild("COST") != null) ? Integer.parseInt(episodes.get(i).getChild("COST").getValue()):0, (episodes.get(i).getChild("HFF") != null) ?  Integer.parseInt(episodes.get(i).getChild("HFF").getValue()):0, Integer.parseInt(episodes.get(i).getChild("GOALS").getValue()));
        }
        
        this.experiments.add(exp);
    }
    
    public String getPlanningTime() {
        
        DecimalFormat structure = new DecimalFormat("0.00"); 
        
        double mean = 0.0;
        double average = 0.0;
        
        for (int i = 0; i < this.experiments.size(); i++) {
            if (this.experiments.get(i).getResult() == 1)
                mean += this.experiments.get(i).getPlanningTime();
            //System.out.print(this.experiments.get(i).getPlanningTime() + " + ");
        }
        
        mean = mean / this.realSize;
        
        for (int i = 0; i < this.experiments.size(); i++) {
            if (this.experiments.get(i).getResult() == 1)
                average += Math.pow((this.experiments.get(i).getPlanningTime() - mean), 2);
        }
        
        average = Math.sqrt(average / this.realSize);
        
        if (mean == 0)
            return "$0$ ";
        else
            return "$" + structure.format(mean) + " \\pm " + structure.format(average) + "$ ";
    }
    
    public String getFirstPlanningTime() {
        
        DecimalFormat structure = new DecimalFormat("0.00"); 
        
        double mean = 0;
        double average = 0;
                
        for (int i = 0; i < this.experiments.size(); i++) {
            if (this.experiments.get(i).getResult() == 1)
                mean += this.experiments.get(i).getFirstPlanningTime();
        }
        
        mean = mean / this.realSize;
        
        for (int i = 0; i < this.experiments.size(); i++) {
            if (this.experiments.get(i).getResult() == 1)
                average += Math.pow(this.experiments.get(i).getFirstPlanningTime() - mean, 2);
        }
        
        average = Math.sqrt(average / this.realSize);
        
        if (mean == 0)
            return "$0$ ";
        else
            return "$" + structure.format(mean) + " \\pm " + structure.format(average) + "$ ";
    }
    
    public String getNumActions() {
        
        DecimalFormat structure = new DecimalFormat("0.00"); 
        
        double mean = 0.0;
        double average = 0.0;
        
        for (int i = 0; i < this.experiments.size(); i++) {
            if (this.experiments.get(i).getResult() == 1)
                mean += this.experiments.get(i).getNumActions();
        }
        
        mean = mean / this.realSize;
        
        for (int i = 0; i < this.experiments.size(); i++) {
            if (this.experiments.get(i).getResult() == 1)
                average += Math.pow(this.experiments.get(i).getNumActions() - mean, 2);
        }
        
        average = Math.sqrt(average / this.realSize);
        
        if (mean == 0)
            return "$0$ ";
        else
            return "$" + structure.format(mean) + " \\pm " + structure.format(average) + "$ ";
    }
    
    public String getReplanningEpisodes() {
        
        DecimalFormat structure = new DecimalFormat("0.00"); 
        
        double mean = 0.0;
        double average = 0.0;
        
        for (int i = 0; i < this.experiments.size(); i++) {
            if (this.experiments.get(i).getResult() == 1)
                mean += this.experiments.get(i).getReplanningEpisodes();
        }
        
        mean = mean / this.realSize;
        
        for (int i = 0; i < this.experiments.size(); i++) {
            if (this.experiments.get(i).getResult() == 1)
                average += Math.pow(this.experiments.get(i).getReplanningEpisodes() - mean, 2);
        }
        
        average = Math.sqrt(average / this.realSize);
        
        if (mean == 0)
            return "$0$ ";
        else
            return "$" + structure.format(mean) + " \\pm " + structure.format(average) + "$ ";
    }
    
    public String getExperiments() {
        
        int values[] = new int[]{0,0,0,0};
        
        for (int i = 0; i < this.experiments.size(); i++) {
            if (this.experiments.get(i).getResult() > 0)
                values[this.experiments.get(i).getResult()-1]++;
        }
        
        String lines = "$ ";
        
        lines += values[0] + ",";
        lines += values[1] + ",";
        lines += values[2] + ",";
        lines += values[3] + "/ " + this.experiments.size() + "$ ";
        
        return lines;
    }
    
    public int getSize(int position) {
        return this.experiments.get(position).getNumEpisodes();
    }
    
    public double getEpisode(int position, int episode) {
        return this.experiments.get(position).getEpisodeTime(episode);
    }
    
    public double getHFF(int position, int episode) {
        return this.experiments.get(position).getEpisodeHff(episode);
    }
    
    public double getGoals(int position, int episode) {
        return this.experiments.get(position).getEpisodeGoals(episode);
    }
    
    public double getEpisodeMaxTime(int episode) {
        double time = 0.0;
        
        for (int i = 0; i < this.experiments.size(); i++) {
            if (this.experiments.get(i).getResult() == 1) {
                if (this.experiments.get(i).getEpisodeTime(episode) > time)
                    time = this.experiments.get(i).getEpisodeTime(episode);
            }
        }
        return time;
    }
}
