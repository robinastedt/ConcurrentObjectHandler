/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.astedt.robin.concurrentobjecthandler.testbench;

import com.astedt.robin.concurrentobjecthandler.ConcurrentObject;
import java.util.List;

/**
 *
 * @author robin
 */
public class TestObject2 extends ConcurrentObject {
    
    long iterations = 0;
    long operations = 0;
    
    double x;
    double x_;
    
    public double getX() {
        return x_;
    }
    
    public TestObject2(double x) {
        this.x = x;
    }
    
    @Override
    public void read(List<ConcurrentObject> concurrentObjects) {
        double sum = 0.0;
        for (ConcurrentObject concurrentObject : concurrentObjects) {
            TestObject2 object = (TestObject2)concurrentObject;
            sum += object.getX();
            operations++;
        }
        x = (x + (sum / concurrentObjects.size())) / 2;
    }
    
    @Override
    public void write() {
        x_ = x;
        iterations++;
    }
    
    public long getIterations() {
        return iterations;
    }
    
    public long getOperations() {
        return operations;
    }
}
