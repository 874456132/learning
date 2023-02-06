package com.learning.bliss.utils;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis封装操作工具类
 *
 * @Author xuexc
 * @Date 2023/1/9 22:36
 * @Version 1.0
 */
@Component
@Slf4j
public class RedisUtil {

    private static StringRedisTemplate stringRedisTemplate;
    private static RedisTemplate redisTemplate;

    /**
     * 注入 static 标识的 bean，无法直接使用 @Autowired或者@Resource，以下两种方式任选一种
     * 本质上都是在bean注入到spring容器之后，再将需要注入的bean延迟注入到类中
     */
    //方式一，将需要注入的bean通过@Autowired标注的set方法的形参传入，从而赋值给全局变量
    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate)  {
        this.redisTemplate = redisTemplate;
    }
    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate)  {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    //方式二，创建initialize()方法，并通过@PostConstruct标识（在bean的创建中延迟执行），方法内部通过上下文对象ApplicationContext获取bean，从而赋值给全局变量
    /*@PostConstruct
    public void initialize() {
        this.stringRedisTemplate = SpringUtils.getBean("stringRedisTemplate", StringRedisTemplate.class);
        this.redisTemplate =  SpringUtils.getBean("redisTemplate", RedisTemplate.class);
    }*/

    /**
     * redis String数据类型
     */
    public static class Strings {

        /**
         * 读取缓存
         *
         * @param key Redis键名
         * @return 是否存在
         */
        public static String get(final String key) {
            return stringRedisTemplate.opsForValue().get(key);
        }

        /**
         * 写入缓存
         *
         * @param key   redis键
         * @param value redis值
         * @return 是否成功
         */
        public static boolean set(final String key, String value) {
            boolean result = false;
            try {
                //将key插入Bloom过滤器，以解决缓存击穿问题

                stringRedisTemplate.opsForValue().set(key, value);
                result = true;
            } catch (Exception e) {
                log.error("redis set value is error, key: " + key + "value: " + value, e);
            }
            return result;
        }

        /**
         * 写入缓存设置时效时间
         *
         * @param key   redis键
         * @param value redis值
         * @return 是否成功
         */
        public static boolean set(final String key, String value, Long expireTime) {
            boolean result = false;
            try {
                stringRedisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
                result = true;
            } catch (Exception e) {
                log.error("redis set value is error, key: " + key + "value: " + value + "expireTime: " + expireTime, e);
            }
            return result;
        }

        /**
         * 批量删除对应的键值对
         *
         * @param keys Redis键名数组
         */
        public static void removeByKeys(final String... keys) {
            for (String key : keys) {
                remove(key);
            }
        }

        /**
         * 批量删除Redis key
         *
         * @param pattern 键名包含字符串（如：myKey*）
         */
        public static void removePattern(final String pattern) {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && keys.size() > 0) {
                try {
                    redisTemplate.delete(keys);
                } catch (Exception e) {
                    throw new RuntimeException("redis delete keys is error, keys: " + Arrays.toString(keys.toArray()), e);
                }
            }
        }

        /**
         * 删除key,也删除对应的value
         *
         * @param key Redis键名
         */
        private static void remove(final String key) {
            if (exists(key)) {
                try {
                    stringRedisTemplate.delete(key);
                } catch (Exception e) {
                    throw new RuntimeException("redis delete key is error, key: " + key, e);
                }
            }
        }

        /**
         * 判断缓存中是否有对应的value
         *
         * @param key Redis键名
         * @return 是否存在
         */
        public static Boolean exists(final String key) {
            boolean result = Boolean.FALSE;
            try {
                result = redisTemplate.hasKey(key);
            } catch (Exception e) {
                log.error("redis hasKey key is error, key: " + key, e);
            }
            return result;
        }
    }
    /**
     * redis Stream数据类型，主要用来实现消息队列
     */
    public static class Streams {


        /**
         * create by: zz
         * description: 创建消费组
         * create time: 2022/5/11 16:45
         * @param:
         * @return java.lang.String
         */
        public static String createGroup(String key, String group){
            return redisTemplate.opsForStream().createGroup(key, group);
        }

        /**
         * create by: zz
         * description: 获取消费者信息
         * create time: 2022/5/11 16:48
         * @param: key
         * @param: group
         * @return org.springframework.data.redis.connection.stream.StreamInfo.XInfoConsumers
         */
        public static StreamInfo.XInfoConsumers queryConsumers(String key, String group){
            return redisTemplate.opsForStream().consumers(key, group);
        }

        /**
         * create by: zz
         * description: 添加Map消息
         * create time: 2022/5/11 16:28
         * @param: key
         * @param: value
         * @return
         */
        public static String addMap(String key, Map<String, String> value){
            return redisTemplate.opsForStream().add(key, value).getValue();
        }

        /**
         * create by: zz
         * description: 添加Record消息
         * create time: 2022/5/11 16:30
         * @param: record
         * @return
         */
        public static String addRecord(Record<String, Object> record){
            return redisTemplate.opsForStream().add(record).getValue();
        }

        /**
         * create by: zz
         * description: 读取消息
         * create time: 2022/5/11 16:52
         * @param: key
         * @return java.util.List<org.springframework.data.redis.connection.stream.MapRecord<java.lang.String,java.lang.Object,java.lang.Object>>
         */
        public static List<MapRecord<String, Object, Object>> read(String key){
            return redisTemplate.opsForStream().read(StreamOffset.fromStart(key));
        }

        /**
         * create by: zz
         * description: 确认消费
         * create time: 2022/5/19 11:21
         * @param: key
         * @param: group
         * @param: recordIds
         * @return java.lang.Long
         */
        public static Long ack(String key, String group, String... recordIds){
            return redisTemplate.opsForStream().acknowledge(key, group, recordIds);
        }

        /**
         * create by: zz
         * description: 确认消费
         * create time: 2022/5/25 15:07
         * @param: group
         * @param: record
         * @return java.lang.Long
         */
        public static Long ack(String group, Record<String, Object> record){
            return redisTemplate.opsForStream().acknowledge(group, record);
        }

        /**
         * create by: zz
         * description: 删除消息。当一个节点的所有消息都被删除，那么该节点会自动销毁
         * create time: 2022/7/18 15:33
         * @param: key
         * @param: recordIds
         * @return java.lang.Long
         */
        public static Long del(String key, String... recordIds){
            return redisTemplate.opsForStream().delete(key, recordIds);
        }

    }

    /**
     * 地理空间索引
     */
    public static class Geo {
        /**
         * 添加节点及位置信息
         * @param geoKey 位置集合
         * @param pointName 位置点标识
         * @param longitude 经度
         * @param latitude 纬度
         */
        public static void geoAdd(String geoKey, String pointName, double longitude, double latitude){
            Point point = new Point(longitude, latitude);
            redisTemplate.opsForGeo().add(geoKey, point, pointName);
        }

        /**
         *
         * @param longitude
         * @param latitude
         * @param radius
         * @param geoKey
         * @param metricUnit 距离单位，例如 Metrics.KILOMETERS
         * @param metricUnit
         * @return
         */
        public static List<GeoResult<RedisGeoCommands.GeoLocation<String>>> findRadius(String geoKey
                , double longitude, double latitude, double radius, Metrics metricUnit, int limit){
            // 设置检索范围
            Point point = new Point(longitude, latitude);
            Circle circle = new Circle(point, new Distance(radius, metricUnit));
            // 定义返回结果参数，如果不指定默认只返回content即保存的member信息
            RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                    .newGeoRadiusArgs().includeDistance().includeCoordinates()
                    .sortAscending()
                    .limit(limit);
            GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(geoKey, circle, args);
            List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
            return list;
        }

        /**
         * 计算指定key下两个成员点之间的距离
         * @param geoKey
         * @param member1
         * @param member2
         * @param unit 单位
         * @return
         */
        public static Distance calDistance(String geoKey, String member1, String member2
                , RedisGeoCommands.DistanceUnit unit){
            Distance distance = redisTemplate.opsForGeo()
                    .distance(geoKey, member1, member2, unit);
            return distance;
        }

        /**
         * 根据成员点名称查询位置信息
         * @param geoKey geo key
         * @param members 名称数组
         * @return
         */
        public static List<Point> geoPosition(String geoKey, String[] members){
            List<Point> points = redisTemplate.opsForGeo().position(geoKey, members);
            return points;
        }
    }

    /**
     * 位图，redis Bitmaps数据类型，其实是String 数据类型的一种存储方式
     * 特点：非常节省空间，运算效率高等
     */
    public static class Bitmaps {

        /**
         * 将指定param的值设置为1，{@param param}会经过hash计算进行存储。
         *
         * @param key   bitmap结构的key
         * @param param 要设置偏移的key，该key会经过hash运算。
         * @param value true：即该位设置为1，否则设置为0
         * @return 返回设置该value之前的值。
         */
        public static Boolean setBit(String key, String param, boolean value) {
            return stringRedisTemplate.opsForValue().setBit(key, hash(param), value);
        }

        /**
         * 返回bit array的第bit位的二进制值，{@param param}会经过hash计算进行存储。
         *
         * @param key   bitmap结构的key
         * @param param 要移除偏移的key，该key会经过hash运算。
         * @return 若偏移位上的值为1，那么返回true。
         */
        public static Boolean getBit(String key, String param) {
            return stringRedisTemplate.opsForValue().getBit(key, hash(param));
        }

        /**
         * 将指定offset偏移量的值设置为1；
         *
         * @param key    bitmap结构的key
         * @param offset 指定的偏移量。
         * @param value  true：即该位设置为1，否则设置为0
         * @return 返回设置该value之前的值。
         */
        public static Boolean setBit(String key, Long offset, boolean value) {
            return stringRedisTemplate.opsForValue().setBit(key, offset, value);
        }

        /**
         * 返回bit array的第bit位的二进制值
         *
         * @param key    bitmap结构的key
         * @param offset 指定的偏移量。
         * @return 若偏移位上的值为1，那么返回true。
         */
        public static Boolean getBit(String key, long offset) {
            return stringRedisTemplate.opsForValue().getBit(key, offset);
        }

        /**
         * 统计对应的bitmap上value为1的数量
         *
         * @param key bitmap的key
         * @return value等于1的数量
         */
        public static Long bitCount(String key) {
            return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
        }

        /**
         * 统计指定范围中value为1的数量
         *
         * @param key   bitMap中的key
         * @param start 该参数的单位是byte（1byte=8bit），{@code setBit(key,7,true);}进行存储时，单位是bit。那么只需要统计[0,1]便可以统计到上述set的值。
         * @param end   该参数的单位是byte。
         * @return 在指定范围[start*8,end*8]内所有value=1的数量
         */
        public static Long bitCount(String key, int start, int end) {
            return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes(), start, end));
        }


        /**
         * 对一个或多个保存二进制的字符串key进行元操作，并将结果保存到saveKey上。
         * <p>
         * bitop and saveKey key [key...]，对一个或多个key逻辑并，结果保存到saveKey。
         * bitop or saveKey key [key...]，对一个或多个key逻辑或，结果保存到saveKey。
         * bitop xor saveKey key [key...]，对一个或多个key逻辑异或，结果保存到saveKey。
         * bitop xor saveKey key，对一个或多个key逻辑非，结果保存到saveKey。
         * <p>
         *
         * @param op      元操作类型；
         * @param saveKey 元操作后将结果保存到saveKey所在的结构中。
         * @param desKey  需要进行元操作的类型。
         * @return 1：返回元操作值。
         */
        public static Long bitOp(RedisStringCommands.BitOperation op, String saveKey, String... desKey) {
            byte[][] bytes = new byte[desKey.length][];
            for (int i = 0; i < desKey.length; i++) {
                bytes[i] = desKey[i].getBytes();
            }
            return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(op, saveKey.getBytes(), bytes));
        }

        /**
         * 对一个或多个保存二进制的字符串key进行元操作，并将结果保存到saveKey上，并返回统计之后的结果。
         *
         * @param op      元操作类型；
         * @param saveKey 元操作后将结果保存到saveKey所在的结构中。
         * @param desKey  需要进行元操作的类型。
         * @return 返回saveKey结构上value=1的所有数量值。
         */
        public static Long bitOpResult(RedisStringCommands.BitOperation op, String saveKey, String... desKey) {
            bitOp(op, saveKey, desKey);
            return bitCount(saveKey);
        }

        /**
         * guava依赖获取一致性 hash值。
         */
        private static long hash(String key) {
            Charset charset = Charset.forName("UTF-8");
            return Math.abs(Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(charset)).asInt());
        }

        /**
         * 热key的解决方案
         * 将key 分割为桶和片，类似于HashMap的结构
         */
        @Data
        public static class BitMapKey {
            /**
             * 组
             */
            private final int groupIndex;
            /**
             * 组中分片
             */
            private final int shardIndex;
            /**
             *
             */
            private final int bitIndex;

            public BitMapKey(int groupIndex, int shardIndex, int bitIndex) {
                this.groupIndex = groupIndex;
                this.shardIndex = shardIndex;
                this.bitIndex = bitIndex;
            }

            public int getBitIndex() {
                return bitIndex;
            }

            /**
             * prefix:groupIndex:shardIndex
             * @param prefix
             * @return
             */
            public String generateKeyWithPrefix(String prefix) {
                return String.join(":", prefix, groupIndex + "", shardIndex + "");
            }

            // 单个bitmap占用1M内存
            // 如果useId < 100亿， 则会分到7000个分组里
            private static final int ONE_BITMAP_SIZE = 1 << 20;
            // 同一个分组里的的useId划分到20个bitmap里
            // 避免出现范围用户太多导致查询时出现热key
            private static final int SHARD_COUNT = 20;

            // 计算用户的 raw， shard， 和对应的offset
            public static BitMapKey computeUserGroup(long userId) {
                //获取组
                long groupIndex = userId / ONE_BITMAP_SIZE;
                //获取分片位置
                int shardIndex = Math.abs((int) (hash(userId+"") % SHARD_COUNT));
                //获取（组-分片）下的offset位置
                int bitIndex = (int) (userId - groupIndex * ONE_BITMAP_SIZE);
                //获取到对象
                return new BitMapKey((int) groupIndex, shardIndex, bitIndex);
            }
        }

    }

}
