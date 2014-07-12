package waylay.client.sensor;


public class ActivityResult {
    public final int confidence;
    public final int type;
    public final String name;

    public ActivityResult(int confidence, int type, String name) {
        this.confidence = confidence;
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return "ActivityResult{" +
                "confidence=" + confidence +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
