#资源管理
##需求
- 加快视频资源访问速度
  - 引入cache机制，防止在服务端生成两份相同的资源
  - 资源不再需要访问时，清理资源
  - 减少并发时的等待时间

为了实现视频资源的cache机制，设计了三种层次的锁作为并发控制：
- videoMaps，大锁，控制对多个session对全局资源列表的并发读写，也是一种读写锁
- sessionState， 中锁， 控制同一个session里不同request的并发
- res，小锁，控制同一个request里不同的资源的并发

##Model
每个RequestState与一个请求的资源对应
```json
{
  "RequestState": {
    "videoPath": "请求的资源的地址",
    "attachSession": "资源所绑定的session列表",
    "resExist": "资源是否存在",
    "lock": "资源访问的锁，做并发控制",
    "res": "具体的资源管理模型"
  }
}
```
```json
{
  "PlayBackRes": {
    "playFilePath": "点播文件对应的地址"
  }
}
```
```json
{
  "RealPlayRes": {
    "nvrInfo": "nvr的端口和ip信息",
    "videoPath": "直播m3u8文件地址",
    "convertProcess": "将rtsp流转为hls直播文件的ffmpeg进程",
    "stopClean": "是否停止进行转换的开关"
  }
}
```
```json
{
  "LongTimeRes": {
    "top": "是否进行回播的控制开关",
    "longTimeDir": "回播文件地址"
  }
}
```
##请求资源流程
```flow
st=>start: 开始
lock_big=>operation: 锁住videomaps
get_request_state=>operation: 根据资源名称查询资源
if_res_cache=>condition: 资源是否已经cache？
new_req=>operation: 新建RequestState
add_session=>operation: 为RequestState绑定当前session
lock_medium=>operation: 锁住当前session
att_req=>operation: 为当前session绑定新的RequestState
unlock_medium=>operation: 释放当前session
if_cache=>condition: 是否cache?
add_map=>operation: 向资源列表添加当前资源
unlock_big=>operation: 释放videomap
if_req_att_ses=>condition: 当前session是否请求过此资源
if_cache2=>condition: 是否cache?
rod1=>start:
rod2=>start:
rod3=>start:
rod4=>start:
rod5=>start:
rod6=>start:
lock_small=>operation: 锁住资源
re_new=>operation: 生成新资源
if_res_exist=>condition: 资源是否已生成
on_old=>operation: 返回cache资源
unlock_small=>operation: 释放RequestState
e=>end: 结束

st->lock_big->get_request_state->if_res_cache
if_res_cache(yes)->if_req_att_ses
if_req_att_ses(yes)->rod1->rod2->rod3->rod4->rod5->rod6->if_cache2
if_req_att_ses(no)->add_session
if_res_cache(no)->new_req->add_session->lock_medium->att_req->unlock_medium->if_cache
if_cache(no)->add_map->unlock_big
if_cache(yes)->unlock_big->lock_small->if_cache2
if_cache2(no)->re_new
if_cache2(yes)->if_res_exist
if_res_exist(no)->re_new->unlock_small
if_res_exist(yes)->on_old->unlock_small
unlock_small->e

```
##清理资源
当一个session退出或者超时时，会触发对这个session相关资源的清理，这也是资源访问中需要做并发控制的原因，防止访问到已删除的资源
###清理session
```flow
st=>start: 开始
lock_sessions=>operation: 锁注session列表
get_session=>operation: 根据sid获取需要清理的session
if_session_null=>condition: 是否获取到session
unlock_sessions=>operation: 解锁session列表
lock_medium=>operation: 锁住当前session
if_last_req=>condition: 是否遍历完request
unlock_medium=>operation: 解锁当前session
if_res_cached=>condition: 当前资源是否有请求过
remove_session=>operation: 解绑request中绑定的当前session
if_attached=>condition: 资源是否与其他session关联
lock_small=>operation: 锁住资源
remove_map=>operation: 从资源列表删除资源
on_empty=>operation: 从磁盘删除对应资源
unlock_small=>operation: 解锁资源
rod=>start:
rod1=>start:
rod2=>start:
rod3=>start:
rod4=>start:
e=>end: 结束

st->lock_sessions->if_session_null
if_session_null(yes)->lock_medium->if_last_req
if_last_req(yes)->rod->rod1->rod2->rod3->rod4->unlock_medium->unlock_sessions
if_last_req(no)->if_res_cached
if_res_cached(no)->rod
if_res_cached(yes)->remove_session->if_attached
if_attached(yes)->unlock_medium
if_attached(no)->lock_small->remove_map->on_empty->unlock_small->unlock_medium
if_session_null(no)->unlock_sessions
unlock_sessions->e
```