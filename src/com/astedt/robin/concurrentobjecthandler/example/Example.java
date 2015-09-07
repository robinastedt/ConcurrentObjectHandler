
//Fizz buzz

package com.astedt.robin.concurrentobjecthandler.example;

import com.astedt.robin.concurrentobjecthandler.ConcurrentObject;
import com.astedt.robin.concurrentobjecthandler.ConcurrentObjectHandler;
import java.util.ArrayList;



/**
 *
 * @author robin
 */
public class Example {
    
    public static float singleton;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       ArrayList<ConcurrentObject> objects = new ArrayList<>();
       for (int i = 0; i < 10000; i++) {
           ExampleObject object = new ExampleObject();
           objects.add(object);
       }
       
       ConcurrentObjectHandler objHandler = new ConcurrentObjectHandler();
       objHandler.init(10, objects);
       objHandler.start();

       /*
       while (objHandler.isRunning()) {
           ExampleObject object = new ExampleObject();
           objHandler.addNewObject(object);
       }
       */
    }
    
   

    
}
