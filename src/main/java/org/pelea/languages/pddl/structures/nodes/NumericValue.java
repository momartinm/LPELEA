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

package org.pelea.languages.pddl.structures.nodes;

import org.jdom.Element;

/**
 *
 * @author moises
 */
public class NumericValue {
    
    private int value;
    
    public NumericValue(int value) {
        this.value = value;
    }
    
    public NumericValue(String value) {
        this.value = Integer.parseInt(value);
    }
    
    public NumericValue(Element code) {
        this.value = Integer.parseInt(code.getAttributeValue("value").trim());
    }
    
    public int getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = Integer.parseInt(value);
    }
    
    public String getXml() {
        return "<term type=\"NUMBER\" value=\"" + this.value + "\"/>";
    }
    
    public NumericValue clone() {
        return new NumericValue(this.value);
    }
    
    public boolean equal(NumericValue value) {    
        return (this.getValue() == value.getValue());
    }
    
    public void update(int value) {
        this.value += value;
    }
}
