package data;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import io.grpc.event.TempEvent;
import sensor.Sensor;
import sensor.SensorType;
import wrapper.Event;

import java.time.Instant;

@Measurement(name = "DHT11")
public class DHT11 implements Sensor {
    private final SensorType type = SensorType.DHT11;

    // Location mapping & Sensor mapping
    private static final int MASTER_BEDROOM = 0;
    private static final int BEDROOM_1 = 1;
    private static final int KEYES_DHT11_SENSOR = 1;

    @Column(tag = true)
    private int deviceId;

    @Column
    private String location;

    @Column
    private float humidity;

    @Column
    private float temperature;

    @Column(timestamp = true)
    private Instant timestamp;

    public DHT11(Event event) {
        TempEvent e = (TempEvent) event.getEvent();
        this.deviceId = KEYES_DHT11_SENSOR;
        this.timestamp = Instant.now();

        try {
            this.temperature = e.getTemperature();
            this.humidity = e.getHumidity();
            this.location = checkDeviceLocation(e.getDeviceId());
        } catch(NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    private String checkDeviceLocation(int deviceId) {
        switch (deviceId) {
            case MASTER_BEDROOM:
                return "MASTER";
            case BEDROOM_1:
                return "BEDROOM_1";
            default:
                return "UNKNOWN";
        }
    }

    public SensorType getType() {
        return this.type;
    }
}
