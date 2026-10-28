-- 令牌桶限流
-- KEYS[1] 桶的 key
-- ARGV[1] 容量
-- ARGV[2] 每秒补充速率
-- ARGV[3] 当前时间戳(毫秒)

local capacity = tonumber(ARGV[1])
local rate     = tonumber(ARGV[2])
local now      = tonumber(ARGV[3])

local bucket = redis.call('HMGET', KEYS[1], 'tokens', 'last')
local tokens = tonumber(bucket[1])
local last   = tonumber(bucket[2])

if tokens == nil then tokens = capacity end
if last == nil   then last = now end

-- 按流逝时间补充令牌
local delta = math.floor((now - last) / 1000 * rate)
if delta > 0 then
    tokens = math.min(capacity, tokens + delta)
end

local pass = 0
if tokens >= 1 then
    tokens = tokens - 1
    pass = 1
end

redis.call('HMSET', KEYS[1], 'tokens', tokens, 'last', now)
redis.call('EXPIRE', KEYS[1], 60)

return pass