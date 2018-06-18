package gr8pefish.heroreactions.hero.test;

import com.google.gson.JsonElement;
import gr8pefish.heroreactions.hero.network.json.JsonMessageHelper;
import gr8pefish.heroreactions.hero.network.message.HeroMessages;
import gr8pefish.heroreactions.hero.test.data.TestData;

import java.util.Map;
import java.util.TimerTask;

public class SimulateMessagesTask extends TimerTask {

    private TestData data;

    public SimulateMessagesTask(TestData data) {
        this.data = data;
    }

    @Override
    public void run() {
        //update view count
        data.incrementViewerCount();
        //Store the data of each type of message directly (simulated "sending", bypasses some networking code to make this test code much easier to write)
        for (Map.Entry<HeroMessages, JsonElement> entry: data.getMessages().entrySet()){
            JsonMessageHelper.setMessageData(entry.getValue(), entry.getKey());
        }
    }

}
