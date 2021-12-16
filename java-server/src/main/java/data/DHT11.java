package data;

import com.influxdb.annotations.Measurement;
import io.grpc.event.TempEvent;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Measurement(name="DH11")
public class DH11Event {

    private int deviceId;
    private float humidity;
    private float tempertaure;

    public DH11Event(TempEvent event) {
        this.deviceId = event.getDeviceId();
        this.humidity = event.getHumidity();
        this.tempertaure = event.getTemperature();
    }
}
