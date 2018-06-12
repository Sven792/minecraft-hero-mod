package gr8pefish.heroreactions.hero.test;

import gr8pefish.heroreactions.hero.network.json.JsonMessageHelper;
import gr8pefish.heroreactions.hero.test.data.TestData;

import java.util.TimerTask;

public class SimulateMessageTask extends TimerTask {

    private TestData data;

    public SimulateMessageTask(TestData data) {
        this.data = data;
    }

    @Override
    public void run() {
        //Just bypass "sending" the message and call the method to store the data directly
        JsonMessageHelper.setMessageData(data.getMessage(), data.getMessageType());
    }

}
