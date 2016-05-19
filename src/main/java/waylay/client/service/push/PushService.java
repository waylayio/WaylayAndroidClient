package waylay.client.service.push;

import waylay.client.sensor.AbstractLocalSensor;

/**
 * Created by Jasper De Vrient on 18/05/2016.
 */
public interface PushService {
    public boolean isPushing();

    public void pushOnce(AbstractLocalSensor sensor);

    public void startPushing(AbstractLocalSensor sensor, long interval);

    public void stopPushing();
}
