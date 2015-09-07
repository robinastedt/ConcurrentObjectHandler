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
    
    //Internal fields
    private float x, y;
    
    //External fields
    private float x_, y_;
    
    //Public methods to access external fields
    public float getX() {
        return x_;
    }
    public float getY() {
        return y_;
    }
    
    @Override
    public void read(List<ConcurrentObject> concurrentObjectList) {
        for (ConcurrentObject concurrentObject : concurrentObjectList) {
            ExampleObject object = (ExampleObject)concurrentObject;
            
            x = Example.singleton;
            
            Example.singleton = x;
            //TODO: Read values from objects
        }
        
    }

    @Override
    public void write() {
        x_ = x;
        y_ = y;
    }
    
}
