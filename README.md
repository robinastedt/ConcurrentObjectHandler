# ConcurrentObjectHandler
Concurrent Object Handler for Java

AUTHOR: Robin Åstedt

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



