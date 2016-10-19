/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pelea.core;

/**
 *
 * @author moises
 */
public class state 
{
    private String _stateLow;
    private String _stateHigh;
    
    public state()
    {
        this._stateHigh = "";
        this._stateLow  = "";
    }
    
    public void setStateHigh(String stateHigh)
    {
        this._stateHigh = stateHigh;
    }
    
    public void setStateLow(String stateLow)
    {
        this._stateLow = stateLow;
    }
    
    public String getStateHigh()
    {
        return this._stateHigh;
    }
    
    public String getStateLow()
    {
        return this._stateLow;
    }
}
