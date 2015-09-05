package com.astedt.robin.concurrentobjecthandler;

/*
@author Robin Åstedt
USAGE:
 * README.md for info
*/

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConcurrentObjectWorker implements Runnable {
    
    int id;
    List<ConcurrentObject> objects;
    List<ConcurrentObject> removalList;
    ExecutionPhases phase;
    ConcurrentObjectHandler handler;
    
    boolean running;
    
    enum ExecutionPhases {
        INITIALIZED,
        READING,
        READING_DONE,
        WRITING,
        WRITING_DONE
    }
    
    public ConcurrentObjectWorker(ConcurrentObjectHandler handler, int id) {
        this.handler = handler;
        this.id = id;
        objects = new ArrayList<>();
        removalList = new ArrayList<>();
        phase = ExecutionPhases.INITIALIZED;
    }
    
    public List<ConcurrentObject> getObjects() {
        return objects;
    }
    
    public void addConcurrentObject(ConcurrentObject object) {
        objects.add(object);
    }
    
    public int getWorkload() {
        return objects.size();
    }
    
    public void stop() {
        running = false;
    }
    
    public void proceed() {
        switch (phase) {
            case INITIALIZED:
            case WRITING_DONE:
                phase = ExecutionPhases.READING;
                break;
            case READING_DONE:
                phase = ExecutionPhases.WRITING;
                break;
            default:
                break;
        }
    }
    
    @Override
    public void run() {
        running = true;
        while (running) {
            switch (phase) {
                case READING:
                {
                    for (ConcurrentObject object : objects) {
                        object.read(objects);
                    }
                    phase = ExecutionPhases.READING_DONE;
                }
                break;
                case WRITING:
                {
                    for (ConcurrentObject object : objects) {
                        if (object.isFlaggedForRemoval()) {
                            removalList.add(object);
                        }
                        else {
                            object.write();
                        }
                    }
                    if (!removalList.isEmpty()) {
                        for (ConcurrentObject object : removalList) {
                            objects.remove(object);
                        }
                        removalList.clear();
                    }
                    phase = ExecutionPhases.WRITING_DONE;
                }
                break;
                case READING_DONE:
                case WRITING_DONE:
                case INITIALIZED:
                default:
                {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ConcurrentObjectWorker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            }
        }
    }
    
    public List<ConcurrentObject> getRemovalList() {
        return removalList;
    }
    
}