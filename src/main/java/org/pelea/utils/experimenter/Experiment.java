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

package org.pelea.utils.experimenter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.pelea.core.configuration.configuration;

public class Experiment {
    
    public static final int SOLVED = 1;
    public static final int DEADEND = 2;
    public static final int ERROR = 3;
    public static final int OUTOFTIME = 4;
    public static final int BLOCK = 5;
    
    public String SEPARATOR = ";";
    
    private final long time_start;
    private long time_end;
    private int replanning;
    private int cases;
    private int executedActions;
    private int result;
    
    private final List<Episode> planningEpisodes;
    private final List<Episode> learningEpisodes;
    private final List<Value> values;
    
    public Experiment() {
        
        this.SEPARATOR = configuration.getInstance().getParameter("GENERAL", "SEPARATOR");
        this.time_start = System.currentTimeMillis();
        this.time_end = 0;
        this.replanning = -1;
        this.executedActions = 0;
        this.cases = 0;
        this.planningEpisodes = new ArrayList<Episode>();
        this.learningEpisodes = new ArrayList<Episode>();
        this.values = new ArrayList<Value>();
    }
    
    private String getValue(String name) {
        for (int i = 0; i < this.values.size(); i++) {
            if (this.values.get(i).getName().toUpperCase().contains(name)) {
                return this.values.get(i).getValue();
            }
        }
        
        return null;
    }
    
    public void finish(int result) {
        this.result = result;
        this.time_end = System.currentTimeMillis();
    }
    
    public void addEpisode(HashMap variables) {
        this.planningEpisodes.add(new Episode(variables));
        this.replanning++;
    }
    
    public void addValueEpisode(String name, String value) {
        this.planningEpisodes.get(this.planningEpisodes.size()-1).addValue(name, value);
    }
    
    public void addLearningEpisode(HashMap variables) {
        this.learningEpisodes.add(new Episode(Episode.LEARNING, variables));
    }
    
    public void addCase() {
        this.cases++;
    }
    
    public void addValue(String name, String value) {
        this.values.add(new Value(name, value));
    }
    
    public void addValue(Value value) {
        this.values.add(value);
    }

    public void executeAction() {
        this.executedActions++;
    }
    
    public String getXML(String domain, String problem, String globalName) throws IOException {
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date();
        String buffer;
        String episodes = "";
        int i;
        
        buffer = "<EXPERIMENT>";
        
        String value = this.getValue("HORIZON");
        
        if (value != null) {
            buffer += "<NAME>" + globalName + "(" + value  + ")</NAME>";
        }
        else {
            buffer += "<NAME>" + globalName + "</NAME>";
        }
        
        buffer += "<DATE>" + sdf.format(date) + "</DATE>";
        buffer += "<DOMAIN>" + domain + "</DOMAIN>";
        buffer += "<PROBLEM>" + problem + "</PROBLEM>";
        buffer += "<EXECUTEDACTIONS>" + this.executedActions + "</EXECUTEDACTIONS>";
        buffer += "<REPLANING>" + (this.planningEpisodes.size()-1) + "</REPLANING>";
        buffer += "<CASESLEARNED>" + this.cases + "</CASESLEARNED>";
        buffer += "<ACTIONSBYCASEBASE>" + this.learningEpisodes.size() + "</ACTIONSBYCASEBASE>";
        buffer += "<RESULT>" + this.result + "</RESULT>";
        
        if (this.planningEpisodes.size() > 0)
            buffer += "<FIRSTPLANNINGTIME>" + this.planningEpisodes.get(0).getTime() + "</FIRSTPLANNINGTIME>";
        else
            buffer += "<FIRSTPLANNINGTIME>0.00</FIRSTPLANNINGTIME>";            
            
        buffer += "<TOTALTIME>" + this.getTime() + "</TOTALTIME>"; 
        
        for (i = 0; i < this.values.size(); i++) {
            buffer += this.values.get(i).getXML(); 
        }

        buffer += "<EPISODES>";
        
        for (i = 0; i < this.planningEpisodes.size(); i++) {
            buffer += this.planningEpisodes.get(i).getXML();
            episodes += (i == this.planningEpisodes.size()-1) ? this.planningEpisodes.get(i).getTime():this.planningEpisodes.get(i).getTime()+this.SEPARATOR;
        }
        
        buffer += "</EPISODES>";
        buffer += "<SEQUENCE>" + episodes + "</SEQUENCE>";
        buffer += "</EXPERIMENT>";
        
        return buffer;
    }
    
    public int getNumActions() {
        return this.executedActions;
    }
    
    public double getTime() {
        return (this.time_end - this.time_start) / 1000.0;
    }
    
    public double getTimeMiliseconds() {
        return (this.time_end - this.time_start);
    }
    
    public double getPlanningTime(int position) {
        if (this.planningEpisodes.size() > 0) {
            return this.planningEpisodes.get(position).getTime();
        }
        else {
            return 0.00;
        }
    }
    
    public int getReplanningEpisodes() {
        return this.replanning;
    }
}