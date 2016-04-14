#会话机制
由于我们会在session超时时清理会话对应的资源，但由于浏览器兼容问题，ajax请求可能没有带上cookie，所以需要自己实现一套会话机制。

成果即接口中的Login, SessionClean, Touch, 并且让前端将获得的session id应用于每次视频请求中。
#Model
```json
{
  "SessionState": {
    "session": "会话",
    "lastTouchTime": "上次访问时间",
    "requestStates": "session中的所有请求",
    "lock": "对session中资源访问进行控制的锁"
  }
}

```
##获取session
用session id作为会话凭证，创建或使用session
###流程图
```flow
st=>start: 开始
lock_map=>operation: 请求session集合的锁
if_sid_null=>condition: 请求中sid为空？
sid_from_req=>operation: 从当前request对象获取sid
get_session=>operation: 根据sid从session集合检索session
if_session_null=>condition:  没有与sid对应的session?
new_session=>operation:  创建新的session，存入集合
old_session=>operation: 使用已有session
get_session2=>operation: 根据sid从session集合检索session
if_session_null2=>condition:  存在与sid对应的session?
false_sid=>operation: sid已失效
e=>end: 结束
rod=>start:
st->lock_map->if_sid_null
if_sid_null(yes)->sid_from_req->get_session->if_session_null
if_session_null(yes)->new_session->e
if_session_null(no)->old_session->e
if_sid_null(no)->get_session2->rod->if_session_null2
if_session_null2(no)->sid_from_req
if_session_null2(yes)->old_session->e
```

##Keep Alive
由于tomcat容器中本身对session有超时机制，为了防止这种错误的超时，需要实现一套keep alive机制
###流程图
```flow
   st=>start: 开始
   get_session=>operation: 获取sid对应的session
   if_session_null=>condition: 没有找到session
   session_null=>operation: 通知前端sid无效
   if_freq=>condition: keep alive间隔小于阈值
   touch_succ=>operation: 通知前端touch成功
   mod_time=>operation: 修改最后访问时间
   e=>end: 结束

   st->get_session->if_session_null
   if_session_null(yes)->session_null->e
   if_session_null(no)->if_freq
   if_freq(yes)->touch_succ->e
   if_freq(no)->mod_time->touch_succ->e
```

