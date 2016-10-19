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

public class EpisodeNode {
    private final double time;
    private final int actions;
    private final int goalsReached;
    private final int hff;
    
    public EpisodeNode(double time, int actions) {
        this.time = time;
        this.actions = actions;
        this.goalsReached = 0;
        this.hff = 0;
    }
    
    public EpisodeNode(double time, int actions, int hff, int goals) {
        this.time = time;
        this.actions = actions;
        this.goalsReached = goals;
        this.hff = hff;
    }   
    
    public double getTime() {
        return this.time;
    }
    
    public int getActions() {
        return this.actions;
    }
    
    public int getGoalsReached() {
        return this.goalsReached;
    }
    
    public int getHff() {
        return this.hff;
    }
    
    public int getGoals() {
        return this.goalsReached;
    }
}
