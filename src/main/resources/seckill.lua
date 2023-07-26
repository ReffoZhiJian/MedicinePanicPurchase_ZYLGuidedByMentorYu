-- 1.参数列表
-- 1.1.限量药品id
local medicineId = ARGV[1]
-- 1.2.购药者id
local purchaserId = ARGV[2]
-- 1.3.订单id
local orderId = ARGV[3]

-- 2.数据key
-- 2.1.库存key
local stockKey = 'panicpurchase:stock:' .. medicineId --//@ZYL：lua脚本中用”..“来拼接字符串   *j~
-- 2.2.订单key
local orderKey = 'panicpurchase:order:' .. medicineId

-- 3.脚本业务
-- 3.1.判断库存是否充足 get stockKey
if(tonumber(redis.call('get', stockKey)) <= 0) then
    -- 3.2.库存不足，返回1
    return 1
end
-- 3.2.判断购药者是否下单 SISMEMBER orderKey purchaserId
if(redis.call('sismember', orderKey, purchaserId) == 1) then
    -- 3.3.存在，说明是重复下单，返回2
    return 2
end
-- 3.4.扣库存 incrby stockKey -1
redis.call('incrby', stockKey, -1)
-- 3.5.下单（保存购药者）sadd orderKey purchaserId
redis.call('sadd', orderKey, purchaserId)
-- 3.6.发送消息到队列中， XADD stream.orders * k1 v1 k2 v2 ...
redis.call('xadd', 'stream.orders', '*', 'purchaserId', purchaserId, 'medicineId', medicineId, 'id', orderId)
return 0
