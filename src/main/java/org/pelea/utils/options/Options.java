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

package org.pelea.utils.options;

import java.util.ArrayList;
import java.util.List;

public class Options 
{
    private final List<Option> _params;
    private final String[] _codes = new String[]{"c", "n", "p", "parse", "d"};
    private final String[] _alias = new String[]{"file", "name", "pid", "parse", "domain"};
    
    private String getStringCode(String option) {
        for (int i = 0; i < this._codes.length; i++) {
            if (this._codes[i].matches(option.substring(1)))
                return this._alias[i];
        }
        
        return null;
    }
    
    private byte getNumericalCode(String option) {
        for (int i = 0; i < this._codes.length; i++) {
            if (this._codes[i].matches(option.substring(1)))
                return (byte) i;
        }
        
        return (byte) -1;
    }

    private boolean isOption(String value) {
        return value.trim().startsWith("-");
    }
    
    public Options(String[] params) {
        this._params = new ArrayList<Option>();
        
        int position = 0;
        
        while ((params.length > (position+1)) && (position < params.length)) {
            if ((this.isOption(params[position])) && (!this.isOption(params[position+1]))) {              
                this._params.add(new Option(this.getNumericalCode(params[position]), params[position+1], this.getStringCode(params[position])));
            }
            
            position += 2;
        }
    }
    
    public int getNumOptions() {
        return this._params.size();
    }
    
    public String getOption(String code) {
        for (int i = 0; i < this._params.size(); i++) {
            if (this._params.get(i).getCode().matches(code))
                return this._params.get(i).getValue();
        }
        
        return null;
    }
    
    public int getOptionInt(String code) {
        for (int i = 0; i < this._codes.length; i++) {
            if (this._params.get(i).getCode().matches(code))
                return Integer.parseInt(this._params.get(i).getValue());
        }
        
        return -1;
    }
    
    public void showOptionsLine() {
        
        System.out.println("PROGRAM");
        System.out.println();
        
        System.out.print("      java -jar program ");
        
        for (int i = 0; i < this._codes.length; i++)
        {
            System.out.print("-" + this._codes[i] + " " + this._alias[i] + " ");
        }
        
        System.out.println();
        System.out.println();
        
        System.out.print("DESCRIPTION");
        System.out.println();
        
        System.out.print("Mandatory arguments to use PELEA");
        System.out.println();
        
        System.out.println("      -c");
        System.out.println("Config file in XML format for each node");
        System.out.println();
        
        System.out.println("      -n");
        System.out.println("unique name of the node");
        System.out.println();
        
        System.out.println("      -p");
        System.out.println("PID number using to generate experiment dir");
        System.out.println();
        
        System.out.print("Mandatory arguments to use PELEA parse xml");
        System.out.println();
        
        System.out.println("      -parse");
        System.out.println("Folder which contais xml files");
        System.out.println();
        
        System.out.println("      -d");
        System.out.println("Domain name");
        System.out.println();
        
        System.out.println("      -n");
        System.out.println("Problem names separates with comma");
        System.out.println();
    } 
}
