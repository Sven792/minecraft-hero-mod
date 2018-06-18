package gr8pefish.heroreactions.hero.test;

import gr8pefish.heroreactions.hero.test.data.TestData;

import java.util.Timer;

public class MainTest {

    public static void mainTest() {
        Timer timer = new Timer();
        TestData data = new TestData();
        timer.schedule(new SimulateMessagesTask(data), 0, 3000); //30 second interval, 10s delay
    }

}
