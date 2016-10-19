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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExperimentPool {
    
    public String name;
    public List<Experiment> experiments;
    
    public ExperimentPool(String name) {
        this.name = name;
        this.experiments = new ArrayList<Experiment>();
    }
    
    public void addExperiment(Experiment e) {
        this.experiments.add(e);
    }
    
    public void addValue(int position, String variable, String value) {
        this.experiments.get(position).addValue(variable, value);
    }
    
    public void addValue(String variable, String value) {
        this.experiments.get(this.experiments.size()-1).addValue(variable, value);
    }
    
    public void addValueEpisode(String variable, String value) {
        this.experiments.get(this.experiments.size()-1).addValueEpisode(variable, value);
    }
    
    public void executeAction() {
        this.experiments.get(this.experiments.size()-1).executeAction();
    }
    
    public void finishExperiment(int state) {
        this.experiments.get(this.experiments.size()-1).finish(state);
    }
    
    public void addEpisode(HashMap values) {
        int position = this.experiments.size()-1;
        this.experiments.get(position).addEpisode(values);
    }
    
    public void addCase() {
        this.experiments.get(this.experiments.size()-1).addCase();
    }
    
    public void saveXML(String fileName, String domain, String problem) throws IOException {
        
        BufferedWriter buffer;
                        
        boolean exists = new File(fileName).exists();
        
        if (!exists) {
            buffer = new BufferedWriter(new FileWriter (fileName, false));
            buffer.write("<?xml version=\"1.0\"?>");
        }
        else {
            buffer = new BufferedWriter(new FileWriter (fileName, true));
        }
        
        buffer.write("<EXPERIMENTS>");

        int episodes = this.experiments.size();

        for (int i = 0; i < episodes; i++) {
            Experiment e = this.experiments.get(i);
            buffer.write(e.getXML(domain, problem, this.name));
        }

        buffer.write("</EXPERIMENTS>");
        buffer.close(); 
    }
}
