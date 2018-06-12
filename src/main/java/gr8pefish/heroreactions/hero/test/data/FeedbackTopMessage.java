package gr8pefish.heroreactions.hero.test.data;

//note: already in the "data" object
public class FeedbackTopMessage {

    public FeedbackOptions[] options;

    public FeedbackTopMessage(FeedbackOptions[] feedbackOptions) {
        this.options = feedbackOptions;
    }

    //Inner class to hold the options
    public static class FeedbackOptions {

        public String id;
        public int activity;

        public FeedbackOptions(String id, int activity) {
            this.id = id;
            this.activity = activity;
        }

    }

}