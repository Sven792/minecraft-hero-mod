package data;

//note: already in the "data" object
public class FeedbackTopMessage {

    public FeedbackOptions[] feedbackOptions;

    public FeedbackTopMessage(FeedbackOptions[] feedbackOptions) {
        this.feedbackOptions = feedbackOptions;
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