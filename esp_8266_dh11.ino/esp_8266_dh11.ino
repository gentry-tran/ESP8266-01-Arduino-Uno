#include <climate.pb.h>

#include <pb_common.h>
#include <pb.h>
#include <pb_encode.h>
#include <pb_decode.h>

#include <DHT.h>
#include <DHT_U.h>

#include <ESP8266WiFi.h>

#define DHTPIN 2
#define DHTTYPE DHT11

DHT dht(DHTPIN, DHTTYPE);


const char* ssid     = "NETGEAR89";
const char* password = "grandcarrot657";

const String addr     = "192.168.0.23"; // mac
//const String addr     = "192.168.0.29"; // rasp
const uint16_t port  = 10101;

WiFiClient client;

// setup WIFI and sensor
void setup() {
  Serial.begin(115200);
  delay(500);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("WIFI connection failed, reconnecting...");
    delay(1000);
  }

  Serial.println("Starting DHT11 sensor...");
  dht.begin();

  Serial.print("connecting to ");
  Serial.println(addr);
}


void loop() {

  if (client.connected()) {
    float hum = dht.readHumidity();
    float tmp = dht.readTemperature(true);

    if (isnan(hum) || isnan(tmp)) {
      Serial.println("failed to read sensor data");
      return;
    }

    _TempEvent event = TempEvent_init_zero;
    event.deviceId = 1;
    event.humidity = hum;
    event.temperature = tmp;
    sendTemp(event);
    delay(5000);
    } else {
    client.connect(addr, port);
    Serial.println("Attempting to connect to server...");
    delay(5000);
  }
}

void sendTemp(_TempEvent e) {
  uint8_t buffer[32];
  pb_ostream_t stream = pb_ostream_from_buffer(buffer, sizeof(buffer));

  if (!pb_encode(&stream, TempEvent_fields, &e)) {
    Serial.println("failed to encode temp proto");
    Serial.println(PB_GET_ERROR(&stream));
    return;
  }

  Serial.println("Sending current temperature & humidity...");
  Serial.print("Bytes written: ");
  Serial.println(stream.bytes_written);
  Serial.print("Humidity: ");
  Serial.println(e.humidity);
  Serial.print("Temperature: ");
  Serial.println(e.temperature);
  client.write(buffer, stream.bytes_written);
}
