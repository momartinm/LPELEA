/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3n.es
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

package org.pelea.core.communication;

/**
 *
 * @author moises
 */
public class Messages 
{
    public final static byte NODE_DECISSIONSUPPORT = 1;
    public final static byte NODE_EXECUTION = 2;
    public final static byte NODE_GOALS = 3;
    public final static byte NODE_HIGHLEVELREPLANER = 4;
    public final static byte NODE_LOWLEVELPLANNER = 5;
    public final static byte NODE_LOWTOHIGH = 6;
    public final static byte NODE_MONITORING = 7;
    public final static byte NODE_LEARNING = 8;
    
    //PREFIX BY GENERAL MESSAGES = 0
    public final static byte MSG_REGISTER = 01;
    public final static byte MSG_UNREGISTER = 02;
    
    //PREFIX BY EXECUTION = 1/2
    public final static byte MSG_EXECUTEPLAN = 11;
    public final static byte MSG_EXECUTEACTION = 12;
    public final static byte MSG_EXECUTEACTIONWITHTIME = 13;
    public final static byte MSG_GETSENSORS = 14;
    public final static byte MSG_GETSENSORSBYTIME = 15;
    public final static byte MSG_GETLASTTIME = 16;
    public final static byte MSG_EXECUTEPLAN_RES = 21;
    public final static byte MSG_EXECUTEACTION_RES = 22;
    public final static byte MSG_EXECUTEACTIONWITHTIME_RES = 23;
    public final static byte MSG_GETSENSORS_RES = 24;
    public final static byte MSG_GETSENSORSBYTIME_RES = 25;
    public final static byte MSG_GETLASTTIME_RES = 26;
    
    //PREFIX BY LOW TO HIGH = 3
    public final static byte MSG_TRANSLATELTH = 31;
    public final static byte MSG_TRANSLATELTH_RES = 32;
    
    //PREFIX BY LOW LEVEL PLANNER = 4
    public final static byte MSG_TRANSLATEHTL = 41;
    public final static byte MSG_TRANSLATEHTL_RES = 42;
    
    //PREFIX BY GOALS METRICS = 5
    public final static byte MSG_GETGOALS = 51;
    public final static byte MSG_GETGOALS_RES = 52;
    
    //PREFIX BY DECISSION SUPPORTS = 6
    public final static byte MSG_REPAIRORPEPLAN = 61;
    public final static byte MSG_GETPLANINFO = 62;
    public final static byte MSG_REPAIRORPEPLAN_RES = 63;
    public final static byte MSG_GETPLANINFO_RES = 64;
    
    public final static byte MSG_SOLVEPROBLEM = 71;
    public final static byte MSG_SOLVEPROBLEM_RES = 72;
    
    public final static byte MSG_REQACTION = 81;
    public final static byte MSG_REQACTION_RES = 82;
    public final static byte MSG_LEARNINGTUPLE = 83;
    public final static byte MSG_LEARNINGTUPLE_RES = 84;

    public final static byte MSG_FINISH = 97;
    public final static byte MSG_STOP = 98;
    public final static byte MSG_START = 99;
    
    public final static byte MSG_ERROR = 100;

}
