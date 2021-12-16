package service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import factory.SensorDTOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sensor.SensorType;
import wrapper.Event;

@Component
public class EventService implements Service {

    private InfluxDBClient client;

    private SensorDTOFactory factory;

    @Autowired
    public EventService(InfluxDBClient client, SensorDTOFactory factory) {
        this.client = client;
        this.factory = factory;
    }

    @Override
    public void process(Event event) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(WritePrecision.S, factory.getSensorDTO(SensorType.DHT11, event));
    }
}
