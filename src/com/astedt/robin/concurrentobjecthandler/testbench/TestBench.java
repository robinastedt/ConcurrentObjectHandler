
//Fizz buzz

package com.astedt.robin.concurrentobjecthandler.testbench;

import com.astedt.robin.concurrentobjecthandler.ConcurrentObject;
import com.astedt.robin.concurrentobjecthandler.ConcurrentObjectHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 *
 * @author robin
 */
public class TestBench implements Runnable{
    
    
    public static final int TEST_AMOUNT = 10;
    public static final int TEST_TIME_SECONDS = 60;
    public static final int MAX_THREADS = 10;
    public static final int TOTAL_OBJECTS = 5000;
    
    boolean running;
    List<TestObject1> objects;
    
    public static void main(String[] args) {
        
        
        
        double baseTestAverage;
        {
            long testTotal = 0;
            for (int testNumb = 0; testNumb < TEST_AMOUNT; testNumb++) {
                long test = baseTest();
                System.out.println("Base test, #"+ testNumb +": " + test);
                testTotal += test;
            }
            double average = (double)testTotal / TEST_AMOUNT;
            System.out.println("Base test, average: " + average);
            baseTestAverage = average;
        }
        
        for (int threads = 1; threads <= MAX_THREADS; threads++){
            long testTotal = 0;
            for (int testNumb = 0; testNumb < TEST_AMOUNT; testNumb++) {
                long test = concurrentTest(threads);
                System.out.println(threads + " threads, #" + testNumb + ": " + test + "(ratio: " + test / baseTestAverage + ")");
                testTotal += test;
            }
            double average = (double)testTotal / TEST_AMOUNT;
            System.out.println(threads + " threads, average: " + average + "(ratio: " + average / baseTestAverage + ")");
        }
    }
    
    public static long concurrentTest(int threads) {
        
        ArrayList<ConcurrentObject> objects2 = new ArrayList<>();
        for (int i = 0; i < TOTAL_OBJECTS; i++) {
            ConcurrentObject object = new TestObject2(new Random().nextDouble());
            objects2.add(object);
        }

        ConcurrentObjectHandler handler = new ConcurrentObjectHandler();
        handler.init(threads, objects2);

        handler.start();


        try {
            Thread.sleep(1000 * TEST_TIME_SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestBench.class.getName()).log(Level.SEVERE, null, ex);
        }

        handler.stop();

        TestObject2 obj2 = (TestObject2)(handler.getObjects().get(0));

        return obj2.getIterations();
    }
    
    public static long baseTest() {
        
        ArrayList<TestObject1> objects1 = new ArrayList<>();
        for (int i = 0; i < TOTAL_OBJECTS; i++) {
            TestObject1 object = new TestObject1(new Random().nextDouble());
            objects1.add(object);
        }
        
        TestBench testBench1 = new TestBench(objects1);
        Thread thread1 = new Thread(testBench1);
        
        thread1.start();
        
        try {
            Thread.sleep(1000 * TEST_TIME_SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestBench.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        testBench1.stop();
        
        TestObject1 obj1 = testBench1.getObjects().get(0);
        return obj1.getIterations();
    }
    
    public TestBench(List<TestObject1> objects) {
        this.objects = objects;
    }
    
    public void stop() {
        running = false;
    }
    
    public List<TestObject1> getObjects() {
        return objects;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            for (TestObject1 object : objects) {
                object.read(objects);
            }
            for (TestObject1 object : objects) {
                object.write();
            }
        }
    }
    
   

    
}
