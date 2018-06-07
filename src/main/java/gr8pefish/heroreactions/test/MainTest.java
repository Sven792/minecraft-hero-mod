package gr8pefish.heroreactions.test;

import gr8pefish.heroreactions.test.data.TestData;

import java.util.Timer;

public class MainTest {

    public static void mainTest() {
        Timer timer = new Timer();
        TestData data = new TestData();
        timer.schedule(new SimulateMessageTask(data), 0, 15000); //30 second interval, 10s delay
    }

}
