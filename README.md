# ESP8266-01-Arduino-Uno

This project seemed quite simple, but the implementation was more difficult than I anticipated due to running into some errors along the way which prevented me from flashing the firmware to the ESP8266-01. After searching for days on the web to understand exactly what my issues were, I wanted to document my process along the way so that someone else won't have to experience this relentless YouTube/Google tutorial search.

From the forums, the problems look like they have persisted for more than 6-7 years with many many write ups but very few succeeding to address the issues with this simple little device. I will document my process below. 

In this project, I am using the Arduino Uno R3 and the Generic ESP8266-01 (bought a two pack at Microcenter for $5.99). This is a rough guide on following this blog post, but not the same (ESP8266 is a weaker chip): https://medium.com/grpc/efficient-iot-with-the-esp8266-protocol-buffers-grafana-go-and-kubernetes-a2ae214dbd29

![image](https://user-images.githubusercontent.com/6667252/143783770-d4c70b0c-b3b6-4818-845e-61952bc62f3c.png)

![image](https://user-images.githubusercontent.com/6667252/143783826-5e992eb3-1b99-450d-ab8c-1c2ddc86d67d.png)


Architecture of project:

1. DHT11 Sensor + Arduino Uno + ESP8266 (Data Source)
	-> Writes to Socket using protobufs
2. Java application reads from Socket as byte stream
	-> Sensor readings are read and put onto an EventQueue (Blocking)
3. An EventConsumer reads from the queue and posts it into an InfluxDB
	-> Using Grafana to view the data
4. Application is deployed on Docker on a Raspberry Pi



The code is in this repository under: java-server


Main Problems (from the Arduino forums):

1. Flashing to wifi module through Arduino goes awry
```
esptool.py v2.8
Serial port COM4
Connecting........_____....._____....._____....._____....._____....._____....._____
Traceback (most recent call last):
  File "C:\Users\N\AppData\Local\Arduino15\packages\esp8266\hardware\esp8266\2.6.3/tools/upload.py", line 65, in <module>
    esptool.main(cmdline)
  File "C:/Users/N/AppData/Local/Arduino15/packages/esp8266/hardware/esp8266/2.6.3/tools/esptool\esptool.py", line 2890, in main
    esp.connect(args.before)
  File "C:/Users/N/AppData/Local/Arduino15/packages/esp8266/hardware/esp8266/2.6.3/tools/esptool\esptool.py", line 483, in connect
    raise FatalError('Failed to connect to %s: %s' % (self.CHIP_NAME, last_error))
esptool.FatalError: Failed to connect to ESP8266: Timed out waiting for packet header
esptool.FatalError: Failed to connect to ESP8266: Timed out waiting for packet header
```  
2. WiFiClient.h does not exist
3. Getting the WiFi module into boot loader mode
  - Ground reset (move purple over)
4. Getting the WiFi module to reset after flashing
  - Remove both yellow and orange (Ground from Arduino and GPiO0)
5. Client.write() hits the gRPC Java Server, with response (I think the write() implementation may be the issue):
```
Nov 30, 2021 2:07:31 PM io.grpc.netty.shaded.io.grpc.netty.NettyServerTransport notifyTerminated
INFO: Transport failed
io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2Exception: HTTP/2 client preface string missing or corrupt. Hex dump for received bytes: 080c10641d0000144225b81ea0422df7769f42
	at io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2Exception.connectionError(Http2Exception.java:108)
	at io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2ConnectionHandler$PrefaceDecoder.readClientPrefaceString(Http2ConnectionHandler.java:306)
	at io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2ConnectionHandler$PrefaceDecoder.decode(Http2ConnectionHandler.java:239)
	at io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2ConnectionHandler.decode(Http2ConnectionHandler.java:438)
	at io.grpc.netty.shaded.io.netty.handler.codec.ByteToMessageDecoder.decodeRemovalReentryProtection(ByteToMessageDecoder.java:508)
	at io.grpc.netty.shaded.io.netty.handler.codec.ByteToMessageDecoder.callDecode(ByteToMessageDecoder.java:447)
	at io.grpc.netty.shaded.io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:276)
	at io.grpc.netty.shaded.io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
	at io.grpc.netty.shaded.io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
	at io.grpc.netty.shaded.io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)
	at io.grpc.netty.shaded.io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)
	at io.grpc.netty.shaded.io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
	at io.grpc.netty.shaded.io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
	at io.grpc.netty.shaded.io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)
	at io.grpc.netty.shaded.io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:166)
	at io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:719)
	at io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:655)
	at io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:581)
	at io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:493)
	at io.grpc.netty.shaded.io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989)
	at io.grpc.netty.shaded.io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
	at io.grpc.netty.shaded.io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.base/java.lang.Thread.run(Thread.java:831)
```

Attempts to solve current issue (Solved below):
1. Created a Java client and successfully invoked the method (/)
2. Looking into the [WifiClient.cpp](https://github.com/esp8266/Arduino/blob/master/libraries/ESP8266WiFi/src/WiFiClient.cpp)
3. Looking into the [Http2Handler](https://github.com/netty/netty/blob/4.1/codec-http2/src/main/java/io/netty/handler/codec/http2/Http2ConnectionHandler.java#L285)
```
... blah blah blah ...
        private boolean readClientPrefaceString(ByteBuf in) throws Http2Exception {
            if (clientPrefaceString == null) {
                return true;
            }

            int prefaceRemaining = clientPrefaceString.readableBytes();
            int bytesRead = min(in.readableBytes(), prefaceRemaining);

            // If the input so far doesn't match the preface, break the connection.
            if (bytesRead == 0 || !ByteBufUtil.equals(in, in.readerIndex(),
                                                      clientPrefaceString, clientPrefaceString.readerIndex(),
                                                      bytesRead)) {
                int maxSearch = 1024; // picked because 512 is too little, and 2048 too much
                int http1Index =
                    ByteBufUtil.indexOf(HTTP_1_X_BUF, in.slice(in.readerIndex(), min(in.readableBytes(), maxSearch)));
                if (http1Index != -1) {
                    String chunk = in.toString(in.readerIndex(), http1Index - in.readerIndex(), CharsetUtil.US_ASCII);
                    throw connectionError(PROTOCOL_ERROR, "Unexpected HTTP/1.x request: %s", chunk);
                }
                String receivedBytes = hexDump(in, in.readerIndex(),
                                               min(in.readableBytes(), clientPrefaceString.readableBytes()));
                throw connectionError(PROTOCOL_ERROR, "HTTP/2 client preface string missing or corrupt. " +
                                                      "Hex dump for received bytes: %s", receivedBytes);
            }
            in.skipBytes(bytesRead);
            clientPrefaceString.skipBytes(bytesRead);

            if (!clientPrefaceString.isReadable()) {
                // Entire preface has been read.
                clientPrefaceString.release();
                clientPrefaceString = null;
                return true;
            }
            return false;
        }
```
4. Currently remote debugging the Java helloworld server with Eclipse:
hello-world-server.sh script
``` 
... blah blah blah ...

46 # Add default JVM options here. You can also use JAVA_OPTS and HELLO_WORLD_SERVER_OPTS to pass JVM options to this script.
47 DEFAULT_JVM_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000"
```

```
localhost@pro examples % ./build/install/examples/bin/hello-world-server    
Listening for transport dt_socket at address: 8000
Nov 30, 2021 2:05:49 PM io.grpc.examples.helloworld.HelloWorldServer start
INFO: Server started, listening on 50051
````

5. Looking into the [Nanopb](https://github.com/nanopb/nanopb) implementation

6. Creating my own implementation of gRPC to handle the decoding...

Useful notes for configuration problems:

When using Maven it is a good idea to use requireUpperBoundDeps from Maven Enforcer plugin to detect cases where Maven is resolving dependencies incorrectly (or rather, as defined, but in an unhelpful way). [ejona86's comment](https://github.com/grpc/grpc-java/issues/3049)

pom.xml
```
<configuration>
	<protocArtifact>com.google.protobuf:protoc:${protoc.version}:exe:osx-x86_64</protocArtifact>
	<pluginId>grpc-java</pluginId>
	<pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:osx-x86_64</pluginArtifact>				
</configuration>
```
gRPC Configuration:

https://github.com/protocolbuffers/protobuf/issues/6286
https://github.com/grpc/grpc-java/issues/7690

7. I had thought of debugging the application by remotely hooking up to the application. You can do this by adding to the 

8. In the meantime, I wanted to ensure the data was hitting my server, so I created a sockets server and it prints out just fine. TempEvent was define in the .proto file and it works, though not secure. I will get this working on HTTP/2.

```
    public static void main(String[] args) throws IOException, InterruptedException {

        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("Server ready to accept connections on port " + server.getLocalPort());
            TemperatureClient tempClient = new TemperatureClient();
            while(true) {

                Socket client = server.accept();
                System.out.println("Client connected using remote port " + client.getPort());

                final Thread t = new Thread(() -> {
                    try {
                        TempEvent p = TempEvent.parseFrom(client.getInputStream());
                        float i = p.getTempCel();
                        System.out.println("TEMP " + i);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                });
                t.start();
            }
        }
    }
```

9. I decided to create a gRPC Java server from scratch instead of trying to debug Google's helloworld example. Every time I made a change, I would break something. Building it from scratch gave me a much clearer idea of what I needed to do to fix it and helped me learn the gRPC core concepts. My project will be on my [github](https://github.com/yidongnan/grpc-spring-boot-starter). HTTP/2 issue is still preventing this. After getting stuck, I wrote the socket above to ensure my sanity... time to debug Netty.


HTTP/2 client preface string missing or corrupt. - this issue comes from the .ino file. The client is using straight TCP connection (seen below in the connect implementation). Asked this [question|https://stackoverflow.com/questions/70311138/http-2-client-preface-string-missing-or-corrupt-for-c-client-grpc-using-nanopb] on Stackoverflow for assistance.


```
WiFiClient client
client.connect(addr, port)
```

WiFiClient.cpp file
```
int WiFiClient::connect(IPAddress ip, uint16_t port)
{
    if (_client) {
        stop();
        _client->unref();
        _client = nullptr;
    }

    tcp_pcb* pcb = tcp_new();
    if (!pcb)
        return 0;

    if (_localPort > 0) {
        pcb->local_port = _localPort++;
    }

    _client = new ClientContext(pcb, nullptr, nullptr);
    _client->ref();
    _client->setTimeout(_timeout);
    int res = _client->connect(ip, port);
    if (res == 0) {
        _client->unref();
        _client = nullptr;
        return 0;
    }

    setSync(defaultSync);
    setNoDelay(defaultNoDelay);

    return 1;
}
```

And, since there is no gRPC C client, I will just continue writing to sockets for the remainder of the project. There was a [project|https://github.com/d21d3q/nanogrpc] started to add this, but is now abandoned.


Useful forums:
  
  https://forum.arduino.cc/t/esp-8266-timed-out-waiting-for-packet-header/597634/27
  https://forum.arduino.cc/t/failed-to-connect-to-esp8266-timed-out-waiting-for-packet-header/626922/4
  https://github.com/espressif/esptool/issues/441
  https://jpa.kapsi.fi/nanopb/docs/concepts.html#streams
  
