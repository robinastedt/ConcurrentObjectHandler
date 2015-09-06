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
    
    
    // Fields
    int id;
    List<ConcurrentObject> objects;
    List<ConcurrentObject> removalList;
    ExecutionPhases phase;
    ConcurrentObjectHandler handler;
    
    // Keeps the thread running while true
    boolean running;
    
    // Different phases a worker goes through
    enum ExecutionPhases {
        INITIALIZED,
        READING,
        READING_DONE,
        WRITING,
        WRITING_DONE
    }
    
    // Constructor, initializes the fields
    // Accepts an id, only used for bug-tracking.
    public ConcurrentObjectWorker(int id) {
        this.id = id;
        objects = new ArrayList<>();
        removalList = new ArrayList<>();
        phase = ExecutionPhases.INITIALIZED;
    }
    
    // Returns objects currently being allocated to this worker
    public List<ConcurrentObject> getObjects() {
        return objects;
    }
    
    // Allocate a new object to the worker
    public void addConcurrentObject(ConcurrentObject object) {
        objects.add(object);
    }
    
    // Returns the current workload of the worker.
    // Mainly used by the Handler to determine where to
    // allocate new objects.
    public int getWorkload() {
        return objects.size();
    }
    
    // Flag this worker to terminate at the end of this cycle.
    public void stop() {
        running = false;
    }
    
    // Function called from the handler to tell the worker to
    // proceed to its next phase.
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
    
    // Main thread entry-point and loop.
    @Override
    public void run() {
        running = true;
        while (running) {
            switch (phase) {
                // If READING, call each its objects read function
                // and set phase to READING_DONE
                case READING:
                {
                    for (ConcurrentObject object : objects) {
                        object.read(objects);
                    }
                    phase = ExecutionPhases.READING_DONE;
                }
                break;
                // If WRITING, for each object, if it's not flagged for removal,
                // call its write function. Otherwise, add it to a list of
                // objects to be removed at the end of the phase.
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
                //Default case, wait until the handler tells the worker to proceed.
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
}