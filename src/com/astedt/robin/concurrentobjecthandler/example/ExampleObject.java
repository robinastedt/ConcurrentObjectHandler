/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.astedt.robin.concurrentobjecthandler.example;

import com.astedt.robin.concurrentobjecthandler.ConcurrentObject;
import java.util.List;

/**
 *
 * @author robin
 */
public class ExampleObject extends ConcurrentObject {
    
    private float x_, y_;
    public float x, y;
    
    @Override
    public void read(List<ConcurrentObject> concurrentObjectList) {
        for (ConcurrentObject concurrentObject : concurrentObjectList) {
            ExampleObject object = (ExampleObject)concurrentObject;
            
            //TODO: Read values from objects
        }
        
    }

    @Override
    public void write() {
        x = x_;
        y = y_;
    }
    
}
