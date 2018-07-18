package gr8pefish.heroreactions.hero.test;

import gr8pefish.heroreactions.hero.test.data.TestData;

import java.util.Timer;

/**
 * Testing code that simulates a Hero message to the client
 */
public class MainTest {

    public static void mainTest() {
        Timer timer = new Timer();
        TestData data = new TestData();
        //Comment out/on to disable/enable testing
//        timer.schedule(new SimulateMessagesTask(data), 0, 3000); //30 second interval, 0s delay
    }

}
