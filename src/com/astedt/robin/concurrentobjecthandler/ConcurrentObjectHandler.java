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

public class ConcurrentObjectHandler implements Runnable {
    
    // Fields
    List<ConcurrentObjectWorker> workers;
    List<Thread> workerThreads;
    List<ConcurrentObject> objects;
    int workerCount;
    Thread handlerThread;
    
    boolean initialized;
    boolean running = false;
    boolean internallyStartedCheck = false;
    
    
    @Override
    public void run() {
        if (internallyStartedCheck) {
            internallyStartedCheck = false;
            
            for (ConcurrentObjectWorker worker : workers) {
                worker.proceed();
            }
            
            while (running) {
                boolean allWorkersDone = true;
                for (ConcurrentObjectWorker worker : workers) {
                    if (worker.phase == ConcurrentObjectWorker.ExecutionPhases.READING ||
                            worker.phase == ConcurrentObjectWorker.ExecutionPhases.WRITING) {
                        allWorkersDone = false;
                    }
                }
                if (allWorkersDone) {
                    for (ConcurrentObjectWorker worker : workers) {
                        worker.proceed();
                    }
                }
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
    
    public boolean init(int workerCount, List<ConcurrentObject> objects) {
        if (initialized) return false;
        addWorkers(workerCount);
        addObjects(objects);
        initWorkerThreads();
        handlerThread = new Thread(this);
        initialized = true;
        return true;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public List<ConcurrentObject> getObjects() {
        return objects;
    }
    
    private void startWorkerThread() {
        
        for (Thread thread : workerThreads) {
            thread.start();
        }
        
    }
    
    private void initWorkerThreads() {
        
        workerThreads = new ArrayList<>();
        for (ConcurrentObjectWorker worker : workers) {
            Thread thread = new Thread(worker);
            workerThreads.add(thread);
        }
    }
    
    private void addWorkers(int count) {
            
        workerCount = count;
        workers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ConcurrentObjectWorker worker = new ConcurrentObjectWorker(i);
            workers.add(worker);
        }
    }
    
    private void addObjects(List<ConcurrentObject> objects) {
        this.objects = objects;
        
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

    
    
}