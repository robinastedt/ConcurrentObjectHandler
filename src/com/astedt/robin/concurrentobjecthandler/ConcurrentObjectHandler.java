package com.astedt.robin.concurrentobjecthandler;

/*
@author Robin Åstedt
USAGE:
 * README.md for info
*/


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConcurrentObjectHandler implements Runnable {
    
    // Fields
    List<ConcurrentObjectWorker> workers;
    List<Thread> workerThreads;
    List<ConcurrentObject> objects;
    LinkedList<ConcurrentObject> newObjects;
    int workerCount;
    Thread handlerThread;
    
    // Keeps the thread running while true
    boolean running = false;
    
    // Internal safety checks
    boolean initialized;
    boolean internallyStartedCheck = false;
    
    // Main thread entry-point and loop.
    @Override
    public void run() {
        // Thread should only be started internally
        if (internallyStartedCheck) {
            internallyStartedCheck = false;
            
            // First, all workers are in the INITIALIZED phase
            // Let them proceed.
            for (ConcurrentObjectWorker worker : workers) {
                worker.proceed();
            }
            
            // Main loop
            while (running) {
                // Check if any worker is still in READING or WRITING phase.
                boolean allWorkersDone = true;
                for (ConcurrentObjectWorker worker : workers) {
                    if (worker.phase == ConcurrentObjectWorker.ExecutionPhases.READING ||
                            worker.phase == ConcurrentObjectWorker.ExecutionPhases.WRITING) {
                        allWorkersDone = false;
                    }
                }
                // If every worker are done; continue
                if (allWorkersDone) {
                    
                    // If there are objects queued up for allocation
                    // Handle them before proceeding.
                    while (!newObjects.isEmpty()) {
                        // LinkedList and pollFirst() makes sure we can
                        // add objects to newObjects at any time.
                        ConcurrentObject object = newObjects.pollFirst();
                        objects.add(object);
                        
                        // Determine which worker currently has the lightest load
                        int lightestLoad = -1;
                        ConcurrentObjectWorker lightestWorker = null;
                        for (ConcurrentObjectWorker worker : workers) {
                            int workload = worker.getWorkload();
                            if (lightestLoad == -1 || workload < lightestLoad) {
                                lightestLoad = workload;
                                lightestWorker = worker;
                            }
                        }
                        
                        // Add the object to the worker with the lightest load
                        lightestWorker.addConcurrentObject(object);
                    }
                    
                    // When new objects are dealt with, let workers proceed.
                    for (ConcurrentObjectWorker worker : workers) {
                        worker.proceed();
                    }
                }
                // If any worker is still working, wait for it.
                else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ConcurrentObjectHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        else {
            try {
                throw new Exception("This thread should not be started externally!");
            } catch (Exception ex) {
                Logger.getLogger(ConcurrentObjectHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    // Tell the handler to terminate all its threads at the end of this step.
    // Also forwards this instruction to its workers.
    public boolean stop() {
        if (running) {
            
            for (ConcurrentObjectWorker worker : workers) {
                worker.stop();
            }
            
            running = false;
            return true;
        }
        else {
            return false;
        }
    }
    
    // Starts all worker threads
    public boolean start() {
        if (!initialized) return false;
        for (Thread thread : workerThreads) {
            thread.start();
        }
        internallyStartedCheck = true;
        running = true;
        handlerThread.start();
        return true;
    }
    
    // Initialization
    // Required to be called before start() since it takes vital information
    // such as the amount of requested workers as well as the initial
    // list of workers to be added.
    public boolean init(int workerCount, List<ConcurrentObject> objects) {
        if (initialized) return false;
        this.objects = objects;
        newObjects = new LinkedList<>();
        addWorkers(workerCount);
        addObjectsToWorkers(objects);
        initWorkerThreads();
        handlerThread = new Thread(this, "ConcurrentObjectHandler");
        initialized = true;
        return true;
    }
    
    // If needed, ask the handler if it's still running
    public boolean isRunning() {
        return running;
    }
    
    //Returns list of all objects
    public List<ConcurrentObject> getObjects() {
        return objects;
    }
    
    
    // Internal helper function
    // Initializes all the worker threads
    private void initWorkerThreads() {
        
        workerThreads = new ArrayList<>();
        for (ConcurrentObjectWorker worker : workers) {
            Thread thread = new Thread(worker, "ConcurrentObjectWorker id=" + worker.id);
            workerThreads.add(thread);
        }
    }
    
    // Internal helper function
    // Only called once during initialization to add initial workers
    private void addWorkers(int count) {
            
        workerCount = count;
        workers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ConcurrentObjectWorker worker = new ConcurrentObjectWorker(objects, i);
            workers.add(worker);
        }
    }
    
    // Internal helper function
    // Only called once during initialization to add initial objects
    // Not safe to run afterwards!
    // Instead use public function addNewObjects
    private void addObjectsToWorkers(List<ConcurrentObject> objects) {
        
        int objectCount = objects.size();
        int objectsPerWorker = objectCount / workerCount;
        int objectsRest = objectCount % workerCount;
        int distributionCycles = objectsPerWorker * workerCount;

        int objectPointer = 0;
        for (int i = 0; i < distributionCycles; i++) {
            ConcurrentObjectWorker worker = workers.get(i % workerCount);
            worker.addConcurrentObject(objects.get(objectPointer));
            objectPointer++;
            if (objectsRest > 0) {
                worker.addConcurrentObject(objects.get(objectPointer));
                objectPointer++;
                objectsRest--;
            }
        }
    }
    
    // Public functions to queue up new objects to be allocated to workers
    // Should be safe to be called at any time.
    public void addNewObjects(List<ConcurrentObject> newObjects) {
        this.newObjects.addAll(newObjects);
    }
    
    public void addNewObject(ConcurrentObject newObject) {
        this.newObjects.add(newObject);
    }

    
}