package com.astedt.robin.concurrentobjecthandler;

import java.util.List;

/*
@author Robin Åstedt
USAGE:
 * README.md for info
*/


public class ConcurrentObject {
    
    private boolean removeFlag = false;
    
    public boolean isFlaggedForRemoval() {
        return removeFlag;
    }
    
    public void remove() {
        removeFlag = true;
    }
    
    public void read(List<ConcurrentObject> objectList) {
        
    }
    public void write() {
        
    }
    
}