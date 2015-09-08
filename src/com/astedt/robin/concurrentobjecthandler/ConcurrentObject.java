package com.astedt.robin.concurrentobjecthandler;

import java.util.List;

/*
@author Robin Åstedt
USAGE:
 * README.md for info      
*/


public class ConcurrentObject {
    
    private boolean removeFlag = false;
    
    // Return whether or not the object has been flagged for removal
    // Warning, this flag might change during the read step
    // and might NOT be thread-safe, instead its recommended
    // to treat an object as "alive" if it's included in the
    // objectList passed through the read function.
    public boolean isFlaggedForRemoval() {
        return removeFlag;
    }
    
    // Flag object for removal after this step
    // Safe to be called from anywhere
    public void remove() {
        removeFlag = true;
    }
    
    // Called once every step. Treat this as the objects main-loop.
    // When changing the object's state, make sure you put the data
    // inside a strict "internal field", see ExampleObject.java.
    public void read(List<ConcurrentObject> objectList) {
        
    }
    
    // Called once every step, avoid putting any logic here.
    // Here you should only copy data from internal to external fields.
    public void write() {
        
    }
    
}
