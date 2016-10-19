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

import java.util.HashMap;
import java.util.Map;

public class Episode {
    
    public static final int PLANNING = 1;
    public static final int LEARNING = 2;
    
    private final HashMap<String, String> variables;
    
    private final int type;
    
    public Episode(HashMap<String, String> variables) {
        this.type = PLANNING;
        this.variables = new HashMap<String, String>();
        
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            this.variables.put(entry.getKey(), entry.getValue());
        }
    }
    
    public Episode(int type, HashMap<String, String> variables) {
        this.type = type;
        this.variables = new HashMap<String, String>();
        
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            this.variables.put(entry.getKey(), entry.getValue());
        }
    }
    
    public Episode(int type) {
        this.type = type;
        this.variables = new HashMap<String, String>();
    }
    
    /*public Episode(int type, String time, String actions) {
        this.type = type;
        this.values = new HashMap<String, String>();
        this.values.put("time", time);
        this.values.put("actions", actions);
    }*/
    
    public void addValue(String key, String value) {
        this.variables.put(key, value);
    }
    
    public String getValue(String name) {
        return this.variables.get(name);
    }
    
    public double getTime() {
        return Double.parseDouble(this.variables.get("time")) / 1000.0;
    }
    
    public int getType() {
        return this.type;
    }
    
    public double getTimeMiliseconds() {
        return Double.parseDouble(this.variables.get("time"));
    }
    
    public String getActions() {
        return this.variables.get("actions");
    }
    
    public String getXML() {
        String result;
        
        result  = "<EPISODE TYPE=\"" + this.type  + "\">";
        for (Map.Entry<String, String> entry : this.variables.entrySet()) {
            result += "<" + entry.getKey().toUpperCase() + ">";
            
            if (entry.getKey().toUpperCase().matches("TIME")) 
                result += Double.parseDouble(entry.getValue()) / 1000.0;
            else
                result += entry.getValue();
            
            result += "</" + entry.getKey().toUpperCase() + ">";
        }
        result += "</EPISODE>";
        
        return result;
    }
}
