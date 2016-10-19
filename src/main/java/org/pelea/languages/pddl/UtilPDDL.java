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

package org.pelea.languages.pddl;

public class UtilPDDL 
{
    public static final int NODE_NOT_DEFINED = 0;
    public static final int NODE_PREDICATE = 1;
    public static final int NODE_AND = 2;
    public static final int NODE_OR = 3;
    public static final int NODE_FOR = 4;
    public static final int NODE_NOT = 5;
    public static final int NODE_FUNCTION = 6;
    public static final int NODE_PREDICATE_ATOM = 7;
    public static final int NODE_ACTION = 8;
    public static final int NODE_EXIST = 9;
    
    public static int getType(String type, String nodeName)
    {
        if (nodeName.toUpperCase().trim().matches("GD"))
        {
            if (type.toUpperCase().trim().matches("PREDICATE"))
                return NODE_PREDICATE;
            if (type.toUpperCase().trim().matches("AND"))
                return NODE_AND;
            if (type.toUpperCase().trim().matches("OR"))
                return NODE_OR;
            if (type.toUpperCase().trim().matches("FOR"))
                return NODE_FOR;
            if (type.toUpperCase().trim().matches("NOT"))
                return NODE_NOT;
            if (type.toUpperCase().trim().matches("BINARY"))
                return NODE_FUNCTION;  
        }
        
        if (nodeName.toUpperCase().trim().matches("ATOM"))
            return NODE_PREDICATE_ATOM;
        
        if (nodeName.toUpperCase().trim().matches("EFFECT"))
        {
            if (type.toUpperCase().trim().matches("PREDICATE"))
                return NODE_PREDICATE;
            if (type.toUpperCase().trim().matches("AND"))
                return NODE_AND;
            if (type.toUpperCase().trim().matches("OR"))
                return NODE_OR;
            if (type.toUpperCase().trim().matches("FOR"))
                return NODE_FOR;
            if (type.toUpperCase().trim().matches("NOT"))
                return NODE_NOT;
            if (type.toUpperCase().trim().matches("BINARY"))
                return NODE_FUNCTION;  
        }
        
        return NODE_NOT_DEFINED;
    }

}
