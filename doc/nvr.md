#NVR sdk二次开发

根据示例demo，使用大华提供的NVR库，在linux平台上进行二次开发。

大华提供：
- libavnetsdk.so，libdhconfigsdk.so，libdhnvr.so，libdhnetsdk.so，libInfra.so，libNetFramework.so，libStream.so，libStreamSvr.so
- avglobal.h，dhconfigsdk.h，dhnetsdk.h

开发结果：
> libnvr.so

NVR调用层架构：

```seq
前端->Tomcat容器: 请求视频
Tomcat容器->视频调度层: 视频请求参数
视频调度层->native代码: 视频请求参数
Note right of native代码: 处理参数
native代码->NVR SDK: 调用NVR SDK
NVR SDK->NVR: 获取NVR数据
NVR->NVR SDK: 下载视频数据
NVR SDK->native代码: 处理返回数据
native代码->视频调度层: 回调java层方法，\n返回视频地址
Note left of 视频调度层: 视频文件处理
视频调度层->Tomcat容器: 返回视频url
Tomcat容器->前端: 返回视频url
```

- 功能
  - 用户在回播时拉取NVR中已有视频片段
  - 用于在点播时拉取NVR中已有视频片段
  - 用于在直播时获取NVR的当前时间，与wifi模块收集到的数据进行时间校准
  - native层日志系统，用于调试native层代码

- NVR调用流程
```flow
st=>start: 开始
op1=>operation: 初始化SDK（CLIENT_Init）
op2=>operation: 注册用户到设备（CLIENT_LoginEx）
op3=>operation: 初始化SDK（CLIENT_Init）
op4=>operation: 你想要的操作
op5=>operation: 注销用户（CLIENT_Logout）
op6=>operation: 释放SDK资源（CLIENT_Cleanup）
e=>end: 结束

st->op1->op2->op3->op4->op5->op6->e
```

- 视频拉取流程
使用CLIENT_DownloadByTime接口获取NVR视频

* 函数原型：
```c
CLIENT_API LLONG CALL_METHOD CLIENT_DownloadByTime(LLONG lLoginID, int nChannelId, int nRecordFileType, LPNET_TIME tmStart, LPNET_TIME tmEnd, char *sSavedFileName, fTimeDownLoadPosCallBack cbTimeDownLoadPos, LDWORD dwUserData);
```

流程图：
```flow
st=>start: 开始
op1=>operation: 初始化下载参数
op2=>operation: 调用CLIENT_DownloadByTime接口执行异步下载
cond=>condition: 计时是否结束？
cond2=>condition: 是否已下载完成？
op5=>operation: 通知上层下载完成，返回视频地址
op4=>operation: 释放jni线程及相关资源
e=>end: 结束


st->op1->op2->cond
cond(yes)->op4->e
cond(no)->cond2->op4->e
cond2(yes)->op5->e
cond2(no)->cond->e
```
其中，异步下载完成时会设置一个标志位，让计时器获取判断结果。

获取NVR时间则调用CLIENT_QueryDeviceTime接口，即可。

视频转码则通过上层调用FFmpeg执行，将在转码模块详细说明。

- 日志系统
  - 可变参数实现字符串格式化
  - 枚举变量实现日志等级设置
  - 代码执行时间信息

关键代码
```c++
//日志函数，输出运行状态信息，便于调试
void fLog(int type, const char *format, ...) {
    if (LOG_CTRL)return;

    safeMkdir(LOG_FILE_PATH);
    string curLogName = string(LOG_FILE_PATH) + "/" + curDay() + ".log";
    FILE *logFile = fopen(curLogName.c_str(), "a+");
    if (logFile == NULL) {
        return;
    }
    time_t now;
    struct tm *tn;
    time(&now);
    tn = localtime(&now);
    printf("%04d-%02d-%02d %02d:%02d:%02d",
           tn->tm_year + 1900, tn->tm_mon + 1, tn->tm_mday, tn->tm_hour, tn->tm_min,
           tn->tm_sec);
    printf(" [%s]: ", typeStr(type).c_str());

    fprintf(logFile, "%04d-%02d-%02d %02d:%02d:%02d",
            tn->tm_year + 1900, tn->tm_mon + 1, tn->tm_mday, tn->tm_hour, tn->tm_min,
            tn->tm_sec);
    fprintf(logFile, " [%s]: ", typeStr(type).c_str());
    va_list ap;
    va_start(ap, format);
    vfprintf(logFile, format, ap);
    printf(format, ap);
    printf("\n");
    va_end(ap);
    fprintf(logFile, "\n");
    fclose(logFile);
}
```

