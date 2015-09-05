# ConcurrentObjectHandler
Concurrent Object Handler for Java

AUTHOR: Robin Åstedt

INTRODUCTION:

The reason for this project is to create a framework for dealing with concurrency in n-body systems where you'd want to assign several threads to update a large amount of objects.
Mainly created for simulation environments but could be applied elsewhere.

It will handle objects for you that needs to be updated and interact with each other. The handler allocates an arbitrary number of worker threads to deal with these updates; and handles read and write instructions.

First it will let all objects update their internal state and lets the objects read the state of other objects. However, during this read phase, the publically available information is read-only and all internal changes will stay internal and not yet become available to the other objects.

When all objects have updated their internal state it will issue a write command to all objects and copy their internal state to their external fields accesable by their public methods. This ensures that information that is fetched from another object will remain the same during the whole read cycle and should, thus, be thread-safe.



DISCLAIMER:

This is intended as a proof of concept and has not been properly tested. Please review the source code before use.

USAGE:

 * Let your objects implement ```ConcurrentObject```

 * Create a new instance of ```ConcurrentObjectHandler```

 * Call ```init(int workerCount, List<ConcurrentObject> objects)```

 * In this method you specify the amount of workers needed
   as well as passing a list of your objects to the handler.

 * When ready, call ```start()```

 * When the work is done, call ```stop()```
 
 * If needed, you can ask the handler if it ```isRunning()```


EXAMPLE IMPLEMENTATION:

```java
   public class TestObject implements ConcurrentObject {
       
      // Internal fields
       private float x, y;
       
       // External fields
       private float x_, y_;
       
       // Getters for external fields
       public float getX() {
           return x_;
       }
       public float getY() {
           return y_;
       }
       
       @Override
       public void read(List<ConcurrentObject> concurrentObjectList) {
           for (ConcurrentObject concurrentObject : concurrentObjectList) {
               TestObject object = (TestObject)concurrentObject;
               
               //TODO: Read values from objects
           }
           
           
       }
   
       @Override
       public void write() {
           x_ = x;
           y_ = y;
       }
       
   }
```



