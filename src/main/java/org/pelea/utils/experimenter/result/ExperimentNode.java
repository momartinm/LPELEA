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

import java.util.ArrayList;
import java.util.List;

public class ExperimentNode {
    
    private final int replanning;
    private final int actions;
    private final int result;
    private final int horizon;
    private final double time;
    private final double firstTime;
    private final List<EpisodeNode> episodes;
    
    
    public ExperimentNode(double time, int replanning, int actions, double firstTime, int horizon, int result) {
        this.replanning = replanning;
        this.actions = actions;
        this.horizon = horizon;
        this.result = result;
        this.time = time;
        this.firstTime = firstTime;
        this.episodes = new ArrayList<EpisodeNode>();
    }
    
    public ExperimentNode(double time, int replanning, int actions, double firstTime, int result) {
        this.replanning = replanning;
        this.actions = actions;
        this.result = result;
        this.time = time;
        this.firstTime = firstTime;
        this.horizon = 10000000;
        this.episodes = new ArrayList<EpisodeNode>();  
    }
    
    public void addEpisode(double time, int actions) {
        this.episodes.add(new EpisodeNode(time, actions));
    }
    
    public void addEpisode(double time, int actions, int hff, int goals) {
        this.episodes.add(new EpisodeNode(time, actions, hff, goals));
    }
    
    public double getPlanningExecutionTime() {
        return this.time;
    }
    
    public double getFirstPlanningTime() {
        return this.firstTime;
    }
    
    public int getReplanningEpisodes() {
        return this.replanning;
    }
    
    public int getNumActions() {
        return this.actions;
    }
    
    public int getHorizon() {
        return this.horizon;
    }
    
    public int getNumEpisodes() {
        return this.episodes.size();
    }
    
    public int getResult() {
        return this.result;
    }
    
    public double getEpisodeTime(int episode) {
        
        if (episode >= this.episodes.size())
            return 0.00;
        else
            return this.episodes.get(episode).getTime();
    }
    
    public int getEpisodeHff(int episode) {
        return this.episodes.get(episode).getHff();
    }
    
    public int getEpisodeGoals(int episode) {
        return this.episodes.get(episode).getGoals();
    }
    
    public double getPlanningTime() {
        double sum = 0.0;
        
        for (EpisodeNode episode : this.episodes) {
            sum += episode.getTime();
        }
        return sum;
    }
}
