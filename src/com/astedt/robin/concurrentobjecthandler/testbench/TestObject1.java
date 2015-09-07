/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.astedt.robin.concurrentobjecthandler.testbench;

import java.util.List;

/**
 *
 * @author robin
 */
public class TestObject1 {
    
    long iterations = 0;
    long operations = 0;
    
    double x;
    double x_;
    
    public double getX() {
        return x_;
    }
    
    public TestObject1(double x) {
        this.x = x;
    }
    
    public void read(List<TestObject1> objects) {
        double sum = 0.0;
        for (TestObject1 object : objects) {
            sum += object.getX();
            operations++;
        }
        x = sum / objects.size();
    }
    
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
