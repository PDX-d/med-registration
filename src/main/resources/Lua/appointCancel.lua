-- 排班id
local scheduleId = ARGV[1]
-- 用户 id
local userId = ARGV[2]

--排班key
local scheduleKey = "appoint:order:" .. scheduleId
-- 用户key
local userKey = "appoint:user:" .. scheduleId

-- 增加库存
redis.call("INCR", scheduleKey)
-- 删除用户
redis.call("SREM", userKey, userId)
return 0