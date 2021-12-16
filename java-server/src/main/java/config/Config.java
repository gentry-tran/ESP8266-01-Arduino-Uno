package config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import io.grpc.event.TempEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ds.EventQueue;
import service.EventService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = { "handler.event", "pojo", "service", "data"})
public class Config {

    private static final Logger logger = LogManager.getLogger(Config.class);

    private static String TOKEN;
    private static String BUCKET;
    private static String ORG;
    private static String URL;

    @Bean
    public EventQueue<TempEvent> queue() {
        return new EventQueue<>();
    }

    @Bean
    public InfluxDBClient client() {
        setupConfig();
        System.out.println(URL);
        InfluxDBClientOptions options = InfluxDBClientOptions.builder()
                .url(URL)
                .org(ORG)
                .bucket(BUCKET)
                .authenticateToken(TOKEN.toCharArray())
                .build();
        return InfluxDBClientFactory.create(options);
    }

    private void setupConfig() {
        Properties properties = new Properties();
        System.out.println(System.getProperty("user.dir"));
        String fileName = "java-server/src/main/resources/influx.config";

        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }

        TOKEN = properties.getProperty("influx.token");
        BUCKET = properties.getProperty("influx.bucket");
        ORG = properties.getProperty("influx.org");
        URL = properties.getProperty("influx.url");
    }
}
