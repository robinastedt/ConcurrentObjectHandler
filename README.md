# ConcurrentObjectHandler
Concurrent Object Handler for Java

AUTHOR: Robin Åstedt

BUILD VERSION: 3 

INTRODUCTION:

The reason for this project is to create a framework for dealing with concurrency in n-body systems where you'd want to assign several threads to update a large amount of objects.
Mainly created for simulation environments but could be applied elsewhere.

It will handle objects for you that needs to be updated and interact with each other. The handler allocates an arbitrary number of worker threads to deal with these updates; and handles read and write instructions.

First it will let all objects update their internal state and lets the objects read the state of other objects. However, during this read phase, the publically available information is read-only and all internal changes will stay internal and not yet become available to the other objects.

When all objects have updated their internal state it will issue a write command to all objects and copy their internal state to their external fields accesable by their public methods. This ensures that information that is fetched from another object will remain the same during the whole read cycle and should, thus, be thread-safe.

Each object can safely be flagged for removal and new objects can be queued up to be added to the worker pool. When new objects are queued for addition the handler will allocate the objects to the worker threads with the currently lowest load.



DISCLAIMER:

This is intended as a proof of concept and has not been properly tested. Please review the source code before use.

USAGE:

 * Let your objects extend ```ConcurrentObject```

 * Create a new instance of ```ConcurrentObjectHandler```

 * Call ```init(int workerCount, List<ConcurrentObject> objects)```

 * In this method you specify the amount of workers needed
   as well as passing a list of your objects to the handler.

 * When ready, call ```start()```

 * When the work is done, call ```stop()```
 
 * If needed, you can ask the handler if it ```isRunning()```
 
 * Call ```remove()``` on any object to flag it for removal.
 
 * Call ```addNewObject(ConcurrentObject newObject)``` or ```newNewObjects(List<ConcurrentObject> newObjects)``` on the handler to queue up new objects to be allocated to worker threads.
 
 * Check out ```Example.java``` and ```ExampleObject.java``` to get an idea of how to use it.


EXAMPLE IMPLEMENTATION:

```java
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
```



