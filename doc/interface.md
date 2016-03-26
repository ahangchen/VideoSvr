#对外接口

##资源管理

### 登录:

- 描述： 初次请求资源之前，建立session，请求sid
- 示例：

####url
```url
    http://222.201.145.237:8888/VideoSvr/Login
```
- 参数解释：无

####响应

```json
    {
    sid: "59807274C7563E5F68BEE01BE128AD4C"
    }
```
- 返回参数：当前请求对应的sessionID

### 保活

- 描述： session有30分钟超时机制，为防止播放时的超时清理，需要在超时前延长该session超时时限
- 示例：

####url
```url
    http://222.201.145.237:8888/VideoSvr/Touch?sid=59807274C7563E5F68BEE01BE128AD4C
```

- 参数解释：

sid：Login请求到的sessionId

####响应：
1.延迟超时成功
```
    Delay session clean for session: 59807274C7563E5F68BEE01BE128AD4C
```
2.延迟超时失败
```
    Touch Session: 59807274C7563E5F68BEE01BE128AD4C failed
```


### 资源清理

- 描述： 退出系统时，通知服务器解除视频资源占用
- 示例：

####url
```url
    http://222.201.145.237:8888/VideoSvr/SessionClean?sid=59807274C7563E5F68BEE01BE128AD4C
```
- 参数解释

sid：Login请求到的sessionId

####响应：

1.清理成功
```
    Session 59807274C7563E5F68BEE01BE128AD4Ccleaned
```
2.给定的sid格式非法（sid字符必须都是字母或数字）
```
    illegal sid: 59807274C7563E5F68BEE01BE128AD4.
```
3.给定的sid没有对应的session，清理失败
```
    no such session:59807274C7563E5F68BEE01BE128AD4
```

##视频请求

### 点播

- 描述： 请求过去时间中的一个视频小片段
- 示例

####url
```url
    http://222.201.145.237:8888/VideoSvr/Playback?start=2016-3-22-0-0-0&end=2016-3-22-0-0-3&channel=3&ip=125.216.231.164&port=37777&sid=6C0A9AF34FD40BD63D12EE5F761AF910
```
- 参数解释

* start：视频段的起始时间，格式为年-月-日-时-分-秒，不需要补零
* end： 视频段的结束时间，格式为年-月-日-时-分-秒，不需要补零
* channel： 视频在NVR的哪个通道上，标识了视频是来源于哪一个摄像头，范围为0-8
* ip：NVR的ip，服务器根据ip和端口找到NVR并调取视频
* port：NVR的端口，服务器根据ip和端口找到NVR并调取视频
* sid：Login请求到的sessionId

####响应

1.参数无效，打印出参数
```
    param illegal start=2016-3-23-0-0-0
```
2.参数合法，但是请求不到对应的视频（通常是由于NVR没有启动或者摄像头没有录制到对应的有效视频导致）
```
    generate required file failed
```
3.生成成功
```
   {
    sid: "6C0A9AF34FD40BD63D12EE5F761AF910",
    rpp: "125-216-231-164-37777-3-2016-3-22-0-0-0-2016-3-22-0-0-3.mp4"
   }
```
- 参数解释
* sid：这个请求对应的session ID，通常与请求时给定的sid一致，但由于某些原因给定的sid无效时，会返回新的有效的sid，具体可以查看session管理逻辑。
* rpp：点播视频文件地址，交给前端播放

### 直播

- 描述： 请求某个摄像头对应的当前视频
- 示例

####url
```url
    http://222.201.145.237:8888/VideoSvr/RealPlay?ip=125.216.231.164&port=35556&channel=1&nip=125.216.231.164&nport=37777&sid=6C0A9AF34FD40BD63D12EE5F761AF910
```
- 参数解释

* ip：摄像头对应的ip
* port： 摄像头对应的rtsp端口
* channel： 摄像头rtsp通道
* nip：NVR的ip，服务器根据ip和端口找到NVR，获取当前时间，用于时间校准
* port：NVR的端口，服务器根据ip和端口找到NVR，获取当前时间，用于时间校准
* sid：Login请求到的sessionId

####响应

1.参数无效，打印出参数
```
    param illegal start=2016-3-23-0-0-0
```
2.参数合法，但是请求不到对应的视频（通常是由于摄像头或摄像头网络连接故障）
```
    generate m3u8 time out
```
3.生成成功
```
   {
       sid: "C00A022EE8F6743C8A80660E26D3B439",
       rpp: "realplay/125-216-231-164-35556-1/t.m3u8",
       svrt: "2016-03-26 14:50:28.000"
   }
```
- 参数解释
* sid：这个请求对应的session ID，通常与请求时给定的sid一致，但由于某些原因给定的sid无效时，会返回新的有效的sid，具体可以查看session管理逻辑。
* rpp：直播视频文件地址，交给前端播放，m3u8格式
* svrt：此时NVR的时间，用于前端时间校准

### 回播

- 描述： 请求某个摄像头对应的当前视频
- 示例

####url
```url
    http://222.201.145.237:8888/VideoSvr/LongTime?start=2016-3-9-9-0-0&channel=1&ip=125.216.231.164&port=37777&sid=6C0A9AF34FD40BD63D12EE5F761AF910
```
- 参数解释

* ip：摄像头对应的ip
* port： 摄像头对应的rtsp端口
* channel： 摄像头rtsp通道
* nip：NVR的ip，服务器根据ip和端口找到NVR，获取当前时间，用于时间校准
* port：NVR的端口，服务器根据ip和端口找到NVR，获取当前时间，用于时间校准
* sid：Login请求到的sessionId

####响应

1.参数无效，打印出参数
```
    param illegal start=2016-3-23-0-0-0
```
2.参数合法，但是请求不到对应的视频（通常是由于摄像头或摄像头网络连接故障）
```
    generate required file failed
```
3.生成视频时间太长，生成失败
```
    wait for longtime over 50 seconds
```
3.生成成功
```
   {
        sid: "210A9E70246BABE79214FF409842B1AA",
        rpp: "playback/125-216-231-164-37777-1-2016-3-26-9-0-0-l/play.m3u8"
   }
```
- 参数解释
* sid：这个请求对应的session ID，通常与请求时给定的sid一致，但由于某些原因给定的sid无效时，会返回新的有效的sid，具体可以查看session管理逻辑。
* rpp：回播视频文件地址，交给前端播放，m3u8格式
