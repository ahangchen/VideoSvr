#视频调度
##点播
```flow
st=>start: 开始
nvr_dav=>operation: 调用NVR，获取dav文件
ff_conv=>operation: 调用FFmpeg，将dav转为mp4文件
nvr_tick=>condition: java层监听是否超时
no_time=>operation: 通知前端生成视频超时
connect=>start:
cond_tick_end=>condition: NVR调用层是否执行完毕
cond_mp4_exist=>condition: 检查视频是否存在
mp4_exist=>operation: 拼装url返回前端
mp4_miss=>operation: 通知前端视频生成失败
e=>end: 结束


st->nvr_dav->ff_conv->nvr_tick
nvr_tick(no)->cond_tick_end
cond_tick_end(no)->nvr_tick
cond_tick_end(yes)->cond_mp4_exist
cond_mp4_exist(yes)->mp4_exist->e
cond_mp4_exist(no)->mp4_miss->e
nvr_tick(yes)->no_time->connect->e
```
##直播
```flow
st=>start: 开始
ff_rtsp=>operation: 启动FFmpeg进程，拉取rtsp流
hls_save=>operation: 等待直播视频生成
cond_m3u8_to=>condition: 生成m3u8是否超时
m3u8_to=>operation: 通知前端直播失败
cond_m3u8_exist=>condition: m3u8文件是否存在
m3u8_exist=>operation: 拼接url返回前端
e=>end: 结束

st->ff_rtsp->hls_save->cond_m3u8_to
cond_m3u8_to(yes)->m3u8_to->e
cond_m3u8_to(no)->cond_m3u8_exist
cond_m3u8_exist(yes)->m3u8_exist->e
cond_m3u8_exist(no)->m3u8_to->e
```

启动tomcat时会启动冗余视频文件的清理线程，防止过期视频占用不必要空间
```flow
st=>start: 开始
clean_pb=>operation: 清理点播文件夹
cond_clean_toggle=>condition: 是否停止直播清理
release=>operation: 清理线程，释放资源
cond_last_dir=>condition: 是否检查完所有目录
read_m3u8=>operation: 读取m3u8，获取正在播放视频序号
clean_overdue=>operation: 删除序号远小于当前播放序号的视频
connect1=>start:
connect2=>start:
e=>end: 结束

st->clean_pb->connect1->cond_clean_toggle
cond_clean_toggle(yes)->release->e
cond_clean_toggle(no)->cond_last_dir
cond_last_dir(no)->read_m3u8->clean_overdue(right)->cond_last_dir
cond_last_dir(yes)->connect1->cond_clean_toggle
```
##回播
```flow
st=>start: 开始
c_stop=>condition: 是否仍在回播
time_cac=>operation: 计算新的dav时间段(8s/段)
down_dav=>operation: (计时)NVR下载指定dav片段
if_dav=>condition: dav下载成功？
gen_fail=>operation: 通知前端生成回播视频失败
ff_conv=>operation: (计时)FFmpeg将dav转为hls片段
gen_m3u8=>operation: 整理hls格式，合并到总m3u8中
if_m3u8=>condition: m3u8生成成功？
gen_succ=>operation: 拼接url，通知前端视频地址
e=>end: 结束

st->c_stop
c_stop(no)->e
c_stop(yes)->time_cac->down_dav->if_dav
if_dav(no)->gen_fail
if_dav(yes)->ff_conv->gen_m3u8->if_m3u8
if_m3u8(no)->gen_fail(right)->e
if_m3u8(yes)->gen_succ->c_stop
```