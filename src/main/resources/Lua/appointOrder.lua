-- 排班id
local scheduleId = ARGV[1]
-- 用户 id
local userId = ARGV[2]
--排班key
local scheduleKey = "appoint:order:" .. scheduleId

-- 用户key
local userKey = "appoint:user:" .. scheduleId
local count = tonumber(redis.call("get", scheduleKey))
if( count<=0) then
    -- 号原不足
    return 1
end
-- 判断用户是否已经购买过
 if(redis.call("sismember", userKey, userId) == 1) then
     -- 用户已经购买过
     return 2
 end

-- 扣减库存
redis.call("incrby", scheduleKey, -1)
-- 添加用户
redis.call("sadd", userKey, userId)
return 0