package gr8pefish.heroreactions.hero.test;

import com.google.gson.JsonElement;
import gr8pefish.heroreactions.hero.network.json.JsonMessageHelper;
import gr8pefish.heroreactions.hero.network.message.HeroMessages;
import gr8pefish.heroreactions.hero.test.data.TestData;

import java.util.Map;
import java.util.TimerTask;

/**
 * Simulated message. Sends some {@link TestData}.
 */
public class SimulateMessagesTask extends TimerTask {

    private TestData data;

    public SimulateMessagesTask(TestData data) {
        this.data = data;
    }

    @Override
    public void run() {
        //update view count
        data.incrementViewerCount();
        //"Send" the message directly (simulated sending which bypasses some networking code to make this test code much simpler)
        for (Map.Entry<HeroMessages, JsonElement> entry: data.getMessages().entrySet()){
            JsonMessageHelper.setMessageData(entry.getValue(), entry.getKey());
        }
    }

}
