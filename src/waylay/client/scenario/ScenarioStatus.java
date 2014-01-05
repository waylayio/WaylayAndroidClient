package waylay.client.scenario;


import java.lang.reflect.Field;

public enum ScenarioStatus {
    STOPPED("stopped"),
    RUNNING("running"),
    FINISHED("finished"),
    FAILED("failed"),
    CREATED("created"),
    INITIALIZED("initialized"),
    STOPPING("stopping"),
    INVALID("invalid");
    private String status = "invalid";

    ScenarioStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    public static ScenarioStatus getStatus(String status) {
        Field[] flds = ScenarioStatus.class.getDeclaredFields();
        for(Field field : flds){
            if(field.isEnumConstant() && field.getName().equalsIgnoreCase(status)){
                try {
                    return (ScenarioStatus) field.get(ScenarioStatus.class);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        throw new RuntimeException("Status "+status + " not defined");
    }
}
