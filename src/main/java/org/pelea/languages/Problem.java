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

package org.pelea.languages;

import java.io.IOException;
import org.jdom.JDOMException;

/**
 *
 * @author moises
 */
public abstract class Problem implements Cloneable
{
    protected String problemName;
    protected String domainName;
    
    public abstract String generateProblemXML(boolean complete);
    public abstract String generateStateXML(boolean complete);
    public abstract boolean goalsReached();
    public abstract int getNumberGoalsReached();
    public abstract void loadState(String state) throws IOException, JDOMException;
    
    /**
     * Modify name of the problem
     * @param name
     */
    public void setProblemName(String name) {
        this.problemName = name;
    }
    
    /**
     * Return name of the problem
     * @return
     */
    public String getProblemName() {
        return this.problemName;
    }
    
    /**
     * Change name of the domain
     * @param name
     */
    public void setDomainName(String name) {
        this.domainName = name;
    }
    
    /**
     * Return name of the domain
     * @return
     */
    public String getDomainName() {
        return this.domainName;
    }
    
    public String getName() {
        return this.problemName;
    }
}