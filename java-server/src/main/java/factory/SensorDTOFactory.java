package factory;

import data.DHT11;
import org.springframework.stereotype.Component;
import sensor.Sensor;
import sensor.SensorType;
import wrapper.Event;

@Component
public class SensorDTOFactory {

    public Sensor getSensorDTO(SensorType type, Event event) {
        switch(type) {
            case DHT11:
                return new DHT11(event);
            default:
                return null;
        }
    }
}
