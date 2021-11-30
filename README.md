# ESP8266-01-Arduino-Uno

This project seemed quite simple, but the implementation was more difficult than I anticipated due to running into some errors along the way which prevented me from flashing the firmware to the ESP8266-01. After searching for days on the web to understand exactly what my issues were, I wanted to document my process along the way so that someone else won't have to experience this relentless YouTube/Google tutorial search.

From the forums, the problems look like they have persisted for more than 6-7 years with many many write ups but very few succeeding to address the issues with this simple little device. Now that I have figured it out, I will document it all below. 

In this project, I am using the Arduino Uno R3 and the Generic ESP8266-01 (bought a two pack at Microcenter for $5.99). This is a rough guide on following this blog post: https://medium.com/grpc/efficient-iot-with-the-esp8266-protocol-buffers-grafana-go-and-kubernetes-a2ae214dbd29

![image](https://user-images.githubusercontent.com/6667252/143783770-d4c70b0c-b3b6-4818-845e-61952bc62f3c.png)

![image](https://user-images.githubusercontent.com/6667252/143783826-5e992eb3-1b99-450d-ab8c-1c2ddc86d67d.png)

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
5. Client.write() hits the gRPC Java Server, with response:
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
  
  
Useful forums:
  
  https://forum.arduino.cc/t/esp-8266-timed-out-waiting-for-packet-header/597634/27
  https://forum.arduino.cc/t/failed-to-connect-to-esp8266-timed-out-waiting-for-packet-header/626922/4
  https://github.com/espressif/esptool/issues/441
  
