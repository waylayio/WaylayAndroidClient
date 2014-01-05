package waylay.client.scenario;

import java.text.ParseException;

public class Condition {

    private double threshold;
    private int operator;
    private String stopstate;

    public static final int GREATER = 0;
    public static final int SMALLER = 1;

    private Condition() {
    }

    public Condition(double threshold, int operator, String stopState) {
        this.threshold = threshold;
        this.operator = operator;
        stopstate = stopState;
    }

    public Condition(Number threshold, int operator, String stopState) {
        this.threshold = threshold.doubleValue();
        this.operator = operator;
        stopstate = stopState;
    }

    public Condition(Number threshold, Number operator, String stopState) {
        this(threshold.doubleValue(), operator.intValue(), stopState);
    }

    public boolean isTrue(double value) {
        switch (operator) {
            case GREATER:
                return value > threshold;
            case SMALLER:
                return value < threshold;
            default :
                throw new RuntimeException("unknown operator");
        }
    }

    /**
     * Returns true if value1 is closer to the goal than value2
     *
     * @param value1
     * @param value2
     * @return
     */
    public boolean isCloser(double value1, double value2) {
        switch (operator) {
            case GREATER:
                return value1 > value2;
            case SMALLER:
                return value1 < value2;
            default :
                throw new RuntimeException("unknown operator");
        }
    }


    public double getThreshold() {
        return threshold;
    }

    private void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getOperator() {
        return operator;
    }

    private void setOperator(int operator) {
        this.operator = operator;
    }

    public String getStopState() {
        return stopstate;
    }

    public void setStopState(String stopState) {
        stopstate = stopState;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder().append(getStopState());
        switch (operator) {
            case GREATER:
                builder.append(" > ");
                break;
            case SMALLER:
                builder.append(" < ");
                break;
        }
        return builder.append(getThreshold()).toString();
    }

    //double value, condition integer, stopState string
    public static Condition parseString(String arg) throws ParseException {
        String [] input = arg.split(",");
        if(input.length != 3){
            throw new ParseException("Input string not possible to parse for condition - " +arg, 0);
        }
        try{
            return new Condition(Double.parseDouble(input[0]), Integer.parseInt(input[1]), input[2]);
        } catch (Exception e){
            throw new ParseException("Input string not possible to parse for condition - " +arg, 0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Condition condition = (Condition) o;

        if (operator != condition.operator) return false;
        if (Double.compare(condition.threshold, threshold) != 0) return false;
        if (stopstate != null ? !stopstate.equals(condition.stopstate) : condition.stopstate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = threshold != +0.0d ? Double.doubleToLongBits(threshold) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + operator;
        result = 31 * result + (stopstate != null ? stopstate.hashCode() : 0);
        return result;
    }

    public String oneLineString() {
        return getThreshold()+","+getOperator()+ ","+getStopState();
    }
}