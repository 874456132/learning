package com.learning.bliss.redis;

import com.alibaba.fastjson.JSONObject;
import com.learning.bliss.utils.RedisUtil;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author xuexc
 * @Date 2021/12/8 14:37
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//@ActiveProfiles("standalone")
public class RedisCommandTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Redis keys 命令
     */
    @Test
    public void keysRedis() {
        stringRedisTemplate.opsForValue().set("zhang", "111");
        Assertions.assertEquals("111", stringRedisTemplate.opsForValue().get("zhang"));

        //TYPE key 返回 key 所储存的值的类型。
        System.out.println(stringRedisTemplate.type("zhang").toString());

        //DEL key 该命令用于在 key 存在时删除 key。
        Assertions.assertTrue(stringRedisTemplate.delete("zhang"));

        //GETDEL key 该命令用于获取key的值并在 key 存在时删除 key。 目前报错，因为没有对应的 "GETDEL" 命令
        /*stringRedisTemplate.opsForValue().set("wang", "111");
        System.out.println(stringRedisTemplate.opsForValue().getAndDelete("wang"));*/

        //DUMP key 序列化给定 key ，并返回被序列化的值。
        stringRedisTemplate.opsForValue().set("zhangsan", "111");
        System.out.println(stringRedisTemplate.dump("zhangsan"));

        //检查给定 key 是否存在，命令 EXISTS key
        Set set = new HashSet(1);
        set.add("zhangsan");
        Assertions.assertEquals(1, stringRedisTemplate.countExistingKeys(set).intValue());

        //EXPIRE key seconds 为给定 key 设置过期时间
        Assertions.assertTrue(stringRedisTemplate.expire("zhangsan", 10, TimeUnit.SECONDS));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));
        System.out.println(stringRedisTemplate.getExpire("zhangsan", TimeUnit.SECONDS));

        Assertions.assertTrue(stringRedisTemplate.expire("zhangsan", Duration.ofSeconds(100)));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        //EXPIREAT key timestamp EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置过期时间。 不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
        Assertions.assertTrue(stringRedisTemplate.expireAt("zhangsan", LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        Assertions.assertTrue(stringRedisTemplate.expireAt("zhangsan", Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        Set<String> keys = stringRedisTemplate.keys("*");
        System.out.println(Arrays.toString(keys.toArray()));
        //keys.stream().forEach(s -> System.out.println(s));

        //GETEX key 该命令用于获取key的值并在 key 存在时删除 key。 目前报错，因为没有对应的 "GETEX" 命令
        /*System.out.println(stringRedisTemplate.opsForValue().getAndExpire("zhangsan", 10, TimeUnit.SECONDS));
        stringRedisTemplate.opsForValue().getAndExpire("zhangsan", Duration.ofSeconds(10));*/

        //PERSIST key 移除 key 的过期时间，key 将持久保持。
        Assertions.assertTrue(stringRedisTemplate.persist("zhangsan"));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));

        //RANDOMKEY 从当前数据库中随机返回一个 key 。
        System.out.println(stringRedisTemplate.randomKey());

        //RENAME key newkey 修改 key 的名称
        stringRedisTemplate.rename("zhangsan", "lisi");
        Set set1 = new HashSet(1);
        set1.add("lisi");
        Assertions.assertEquals(1, stringRedisTemplate.countExistingKeys(set1).intValue());
        //RENAMENX key newkey 仅当 newkey 不存在时，将 key 改名为 newkey 。
        stringRedisTemplate.opsForValue().set("zhangsan", "111");
        Assertions.assertTrue(stringRedisTemplate.renameIfAbsent("lisi", "zhangsan"));

        //PERSIST key 移除 key 的过期时间，key 将持久保持。
        Assertions.assertTrue(stringRedisTemplate.persist("zhangsan"));
        System.out.println(stringRedisTemplate.getExpire("zhangsan"));
    }

    /**
     * Strings 数据类型相关操作
     */
    @Test
    public void stringsRedis() {

        //SET key value 设置指定 key 的值
        stringRedisTemplate.opsForValue().set("刘邦", "汉高祖");
        //MSET key value [key value ...] 同时设置一个或多个 key-value 对。
        Map<String, String> map = new HashMap<>();
        map.put("刘盈", "汉惠帝");
        map.put("刘恒", "汉文帝");
        stringRedisTemplate.opsForValue().multiSet(map);
        stringRedisTemplate.opsForValue().setIfAbsent("吕雉", "吕后");
        Map<String, String> map1 = new HashMap<>();
        //map1.put("刘盈", "汉惠帝");
        map1.put("刘肥", "西汉齐悼惠王");
        stringRedisTemplate.delete("刘肥");
        System.out.println(stringRedisTemplate.opsForValue().multiSetIfAbsent(map1));
        //SETBIT key offset value 对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)。
        stringRedisTemplate.opsForValue().set("bitTest", "a");
        // 'a' 的ASCII码是 97。转换为二进制是：01100001
        // 'b' 的ASCII码是 98  转换为二进制是：01100010
        // 'c' 的ASCII码是 99  转换为二进制是：01100011
        //因为二进制只有0和1，在setbit中true为1，false为0，因此我要变为'b'的话第六位设置为1，第七位设置为0
        stringRedisTemplate.opsForValue().setBit("bitTest", 6, true);
        stringRedisTemplate.opsForValue().setBit("bitTest", 7, false);
        System.out.println(stringRedisTemplate.opsForValue().get("bitTest")); //b

        //GET key 获取指定 key 的值。
        System.out.println(stringRedisTemplate.opsForValue().get("刘邦"));
        //MGET key1 [key2..] 获取所有(一个或多个)给定 key 的值。
        Set s = new HashSet();
        s.add("刘盈");
        s.add("刘恒");
        List list = new ArrayList();
        list.add("刘盈");
        list.add("刘恒");
        System.out.println(stringRedisTemplate.opsForValue().multiGet(list));
        //GETBIT key offset 对 key 所储存的字符串值，获取指定偏移量上的位(bit)。
        System.out.println(stringRedisTemplate.opsForValue().getBit("刘盈", 2));

        //GETSET key value 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
        System.out.println(stringRedisTemplate.opsForValue().getAndSet("刘邦", "泗水亭长"));
        System.out.println(stringRedisTemplate.opsForValue().get("刘邦"));

        //GETRANGE key start end 返回 key 中字符串值的子字符
        System.out.println(stringRedisTemplate.opsForValue().get("刘邦", 0, 2));//一个汉字占用3位
        System.out.println(stringRedisTemplate.opsForValue().get("刘邦", 3, 5));//一个汉字占用3位
        System.out.println(stringRedisTemplate.opsForValue().get("刘邦").length());

        //STRLEN key 返回 key 所储存的字符串值的长度。
        System.out.println(stringRedisTemplate.opsForValue().size("刘邦"));//4个汉字12位
        System.out.println(stringRedisTemplate.opsForValue().size("刘肥"));//6个汉字18位

        //APPEND key value 如果 key 已经存在并且是一个字符串， APPEND 命令将 value 追加到 key 原来的值的末尾。
        stringRedisTemplate.opsForValue().append("bitTest", "c");
        System.out.println(stringRedisTemplate.opsForValue().get("bitTest"));

        //INCRBY key increment 将 key 所储存的值加上给定的增量值（increment） 。
        stringRedisTemplate.opsForValue().set("inc", "1");
        //INCR key 将 key 中储存的数字值增一。
        System.out.println(stringRedisTemplate.opsForValue().increment("inc"));
        ////INCRBY key increment 将 key 所储存的值加上给定的增量值（increment） 。
        System.out.println(stringRedisTemplate.opsForValue().increment("inc", 10));
        //INCRBYFLOAT key increment 将 key 所储存的值加上给定的浮点增量值（increment） 。
        System.out.println(stringRedisTemplate.opsForValue().increment("inc", 0.1));
        System.out.println(stringRedisTemplate.opsForValue().increment("inc", -0.1));

        //DECR key 将 key 中储存的数字值减一。
        System.out.println(stringRedisTemplate.opsForValue().decrement("inc"));
        //DECRBY key decrement key 所储存的值减去给定的减量值（decrement） 。
        System.out.println(stringRedisTemplate.opsForValue().decrement("inc", 1));
    }

    /**
     * hashes 数据类型相关操作
     */
    @Test
    public void hashesRedis() {

        System.out.println("------------set------");
        //HSET key field value 将哈希表 key 中的字段 field 的值设为 value 。
        redisTemplate.opsForHash().put("大秦", "始皇", "嬴政");
        //HSETNX key field value 只有在字段 field 不存在时，设置哈希表字段的值。
        //Assertions.assertTrue(redisTemplate.opsForHash().putIfAbsent("大秦", "赵姬", "嫪毐"), "大秦：赵姬=嫪毐已存在");
        System.out.println(redisTemplate.opsForHash().putIfAbsent("大秦", "赵姬", "嫪毐"));

        System.out.println(redisTemplate.opsForHash().get("大秦", "始皇"));
        //HMSET key field1 value1 [field2 value2 ] 同时将多个 field-value (域-值)对设置到哈希表 key 中。
        Map<String, String> map = new HashMap<>();
        map.put("孝文王", "嬴柱");
        map.put("庄襄王", "嬴异人又名子楚");
        map.put("二世", "胡亥");
        map.put("三世", "子婴");
        redisTemplate.opsForHash().putAll("大秦", map);

        System.out.println("------------get------");
        //HGET key field 获取存储在哈希表中指定字段的值/td>
        System.out.println(redisTemplate.opsForHash().get("大秦", "始皇"));
        //HMGET key field1 [field2] 获取所有给定字段的值
        Set set = new HashSet<>();
        set.add("始皇");
        set.add("二世");
        System.out.println(redisTemplate.opsForHash().multiGet("大秦", set));

        System.out.println("------------keys------");
        //HKEYS key 获取所有哈希表中的字段
        System.out.println(redisTemplate.opsForHash().keys("大秦"));

        //HVALS key 获取哈希表中所有值
        System.out.println("------------values------");
        System.out.println(redisTemplate.opsForHash().values("大秦"));

        //HGETALL key 获取在哈希表中指定 key 的所有字段和值
        System.out.println("------------entries------");
        System.out.println(redisTemplate.opsForHash().entries("大秦"));

        System.out.println("------------delete------");
        redisTemplate.opsForHash().put("大秦", "相国", "吕不韦");
        Assertions.assertTrue(redisTemplate.opsForHash().hasKey("大秦", "相国"));
        Assertions.assertEquals(redisTemplate.opsForHash().delete("大秦", "相国"), 1);
        System.out.println(redisTemplate.opsForHash().hasKey("大秦", "相国"));

        //HEXISTS key field 查看哈希表 key 中，指定的字段是否存在。
        Assertions.assertTrue(redisTemplate.opsForHash().hasKey("大秦", "始皇"));

        System.out.println("------------random------");
        System.out.println(redisTemplate.opsForHash().randomKey("大秦"));
        System.out.println(redisTemplate.opsForHash().randomKeys("大秦", 3));
        System.out.println(redisTemplate.opsForHash().randomEntry("大秦"));
        System.out.println(redisTemplate.opsForHash().randomEntries("大秦", 3));

        System.out.println("------------size------");
        System.out.println(redisTemplate.opsForHash().size("大秦"));
        System.out.println(redisTemplate.opsForHash().size("大汉"));
        System.out.println(redisTemplate.opsForHash().lengthOfValue("大秦", "庄襄王"));

        System.out.println("------------increment------");
        //HINCRBY key field increment 为哈希表 key 中的指定字段的整数值加上增量 increment 。
        redisTemplate.opsForHash().put("incHash", "id", 0);
        System.out.println(redisTemplate.opsForHash().increment("incHash", "id", 1));
        System.out.println(redisTemplate.opsForHash().increment("incHash", "id", 0.1));

        System.out.println("------------scan------");
        //HSCAN key cursor [MATCH pattern] [COUNT count] 迭代哈希表中的键值对。
        Cursor<Map.Entry<Object, Object>> c = redisTemplate.opsForHash().scan("大秦", ScanOptions.NONE);
        while (c.hasNext()) {
            Map.Entry<Object, Object> entry = c.next();
            System.out.println(entry);
        }
        Cursor<Map.Entry<Object, Object>> c1 = redisTemplate.opsForHash().scan("大秦", ScanOptions.scanOptions().match("*世").build());
        while (c1.hasNext()) {
            Map.Entry<Object, Object> entry = c1.next();
            System.out.println(entry);
        }

    }

    /**
     * Lists 数据类型相关操作
     */
    @Test
    public void listsRedis() {
        //LPUSH key value1 [value2] 将一个或多个值插入到列表头部
        redisTemplate.opsForList().leftPush("唐", "唐高祖：李渊：隋末民变、晋阳起兵、李渊攻取长安之战");
        List<String> list = new ArrayList<>();
        list.add("唐太宗：李世民：玄武门之变、贞观之治");
        list.add("唐高宗：李世民：晋王李治、永徽之治");
        redisTemplate.opsForList().leftPushAll("唐", list);
        int size = redisTemplate.opsForList().size("唐").intValue();
        System.out.println(size);
        /*while (size > 0) {
            System.out.println(redisTemplate.opsForList().rightPop("唐"));
            size --;
        }*/

        redisTemplate.opsForList().remove("唐", 0, "唐太宗：李世民：玄武门之变、贞观之治");
        System.out.println(size = redisTemplate.opsForList().size("唐").intValue());
        while (size >= 0) {
            System.out.println(redisTemplate.opsForList().index("唐", size));
            size--;
        }
        System.out.println(size = redisTemplate.opsForList().size("唐").intValue());
    }

    /**
     * Sets 数据类型相关操作
     */
    @Test
    public void setsRedis() {
        redisTemplate.delete("唐");

        //SADD key member1 [member2] 向集合添加一个或多个成员
        System.out.println("------------SADD----------");
        System.out.println(redisTemplate.opsForSet().add("唐", "武周代唐：武则天：科举制度得到进一步完善；她开创殿试和武举，上承贞观，下启开元"));
        System.out.println(redisTemplate.opsForSet().add("唐", "唐中宗：李显：神龙政变、唐隆政变", "唐睿宗：李旦：太平公主与李隆基发生权力之争，让位于李隆基"));
        System.out.println(redisTemplate.opsForSet().add("唐", "唐玄宗：李隆基：开元盛世，安史之乱、奉天之难"));
        System.out.println(redisTemplate.opsForSet().add("唐", "唐玄宗：李隆基：开元盛世，安史之乱、奉天之难"));

        System.out.println(redisTemplate.type("唐"));

        //SMEMBERS key 返回集合中的所有成员
        System.out.println("------------SMEMBERS----------");
        System.out.println(redisTemplate.opsForSet().members("唐"));

        //SCARD key 获取集合的成员数
        System.out.println("------------SCARD----------");
        System.out.println(redisTemplate.opsForSet().size("唐"));

        //SRANDMEMBER key [count] 返回集合中一个或多个随机数
        System.out.println("------------SISMEMBER----------");
        System.out.println(redisTemplate.opsForSet().randomMember("唐"));
        System.out.println(redisTemplate.opsForSet().randomMembers("唐", 2));
        System.out.println(redisTemplate.opsForSet().distinctRandomMembers("唐", 2));

        //SISMEMBER key member 判断 member 元素是否是集合 key 的成员
        System.out.println("------------SISMEMBER----------");
        System.out.println(redisTemplate.opsForSet().isMember("唐", "武周代唐：武则天：科举制度得到进一步完善；她开创殿试和武举，上承贞观，下启开元"));
        System.out.println(redisTemplate.opsForSet().isMember("唐", "唐中宗：李显：神龙政变、唐隆政变", "唐睿宗：李旦：太平公主与李隆基发生权力之争，让位于李隆基"));

        //SMOVE source destination member 将 member 元素从 source 集合移动到 destination 集合
        System.out.println("------------SMOVE----------");
        System.out.println(redisTemplate.opsForSet().move("唐", "武周代唐：武则天：科举制度得到进一步完善；她开创殿试和武举，上承贞观，下启开元", "武周"));

        //SREM key member1 [member2] 移除集合中一个或多个成员
        System.out.println("------------SREM----------");
        System.out.println(redisTemplate.opsForSet().add("武周", "太平", "狄仁杰"));
        System.out.println(redisTemplate.opsForSet().remove("武周", "太平", "狄仁杰"));

        //SPOP key 移除并返回集合中的一个随机元素
        System.out.println("------------SPOP----------");
        System.out.println(redisTemplate.opsForSet().pop("唐"));
        System.out.println(redisTemplate.opsForSet().pop("唐", 2));

        //SSCAN key cursor [MATCH pattern] [COUNT count] 迭代集合中的元素
        System.out.println("------------SSCAN----------");
        System.out.println(redisTemplate.opsForSet().add("唐", "唐肃宗李亨", "唐代宗李豫"));
        Cursor<String> cursor = redisTemplate.opsForSet().scan("唐", ScanOptions.scanOptions().match("*唐*").build());
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }

        //SDIFF key1 [key2] 返回给定所有集合的差集
        System.out.println("------------SDIFF----------");
        System.out.println(redisTemplate.opsForSet().add("后唐", "唐代宗李豫"));
        System.out.println(redisTemplate.opsForSet().difference("唐", "后唐"));

        //SDIFFSTORE destination key1 [key2] 返回给定所有集合的差集并存储在 destination 中
        System.out.println("------------SDIFFSTORE----------");
        System.out.println(redisTemplate.opsForSet().add("a", "111"));
        System.out.println(redisTemplate.opsForSet().add("b", "222"));
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        System.out.println(redisTemplate.opsForSet().differenceAndStore(set, "a"));
        System.out.println(redisTemplate.opsForSet().members("a"));

        //SINTER key1 [key2] 返回给定所有集合的交集
        System.out.println("------------SINTER----------");
        System.out.println(redisTemplate.opsForSet().add("c", "111", "222", "333"));
        System.out.println(redisTemplate.opsForSet().intersect("a", "c"));

        //SINTERSTORE destination key1 [key2] 返回给定所有集合的交集并存储在 destination 中
        System.out.println("------------SINTERSTORE----------");
        Set<String> set1 = new HashSet<>();
        set1.add("a");
        set1.add("c");
        System.out.println(redisTemplate.opsForSet().intersectAndStore(set1, "d"));
        System.out.println(redisTemplate.opsForSet().members("d"));

        //SUNION key1 [key2] 返回所有给定集合的并集
        System.out.println("------------SUNION----------");
        System.out.println(redisTemplate.opsForSet().add("A", "111"));
        System.out.println(redisTemplate.opsForSet().add("B", "222"));
        System.out.println(redisTemplate.opsForSet().union("A", "B"));

        //SUNIONSTORE destination key1 [key2] 所有给定集合的并集存储在 destination 集合中
        System.out.println("------------SUNIONSTORE----------");
        System.out.println(redisTemplate.opsForSet().unionAndStore("A", "B", "C"));
        System.out.println(redisTemplate.opsForSet().members("C"));

    }

    /**
     * Zset(Sorted Sets) 数据类型相关操作
     */
    @Test
    public void zsetsRedis() {
        redisTemplate.delete("a");
        //ZADD key score1 member1 [score2 member2] 向有序集合添加一个或多个成员，或者更新已存在成员的分数
        System.out.println("------------ZADD----------");
        System.out.println(redisTemplate.opsForZSet().add("a", "张三", 80));
        ZSetOperations.TypedTuple<String> tuple = new DefaultTypedTuple<>("李四", 96.6);
        ZSetOperations.TypedTuple<String> tuple1 = new DefaultTypedTuple<>("王五", 56.0);
        ZSetOperations.TypedTuple<String> tuple2 = new DefaultTypedTuple<>("赵六", 78.0);
        Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
        set.add(tuple);
        set.add(tuple1);
        set.add(tuple2);
        System.out.println(redisTemplate.opsForZSet().add("a", set));

        //ZCARD key 获取有序集合的成员数
        System.out.println("------------ZCARD----------");
        System.out.println(redisTemplate.opsForZSet().size("a"));//底层调用的还是zCard
        System.out.println(redisTemplate.opsForZSet().zCard("a"));

        //ZCOUNT key min max 计算在有序集合中指定区间分数的成员数
        System.out.println("------------ZCOUNT----------");
        System.out.println("及格人数：" + redisTemplate.opsForZSet().count("a", 60, 100));
        System.out.println("优秀人数：" + redisTemplate.opsForZSet().count("a", 80, 100));

        //ZLEXCOUNT key min max 在有序集合中计算指定字典区间内成员数量
        System.out.println("------------ZLEXCOUNT----------");
        ZSetOperations.TypedTuple<String> tupleA = new DefaultTypedTuple<>("A", 90.6);
        ZSetOperations.TypedTuple<String> tupleB = new DefaultTypedTuple<>("B", 45.0);
        ZSetOperations.TypedTuple<String> tupleC = new DefaultTypedTuple<>("C", 78.0);
        Set<ZSetOperations.TypedTuple<String>> setA = new HashSet<>();
        setA.add(tupleA);
        setA.add(tupleB);
        setA.add(tupleC);
        System.out.println(redisTemplate.opsForZSet().add("zsetlexCount", setA));
        RedisZSetCommands.Range range = new RedisZSetCommands.Range();
        range.gte("A");
        range.lte("B");
        System.out.println(redisTemplate.opsForZSet().lexCount("zsetlexCount", range));

        //ZINCRBY key increment member 有序集合中对指定成员的分数加上增量 increment
        System.out.println("------------ZINCRBY----------");
        System.out.println(redisTemplate.opsForZSet().incrementScore("a", "王五", 4));
        System.out.println("及格人数：" + redisTemplate.opsForZSet().count("a", 60, 100));

        //ZRANGE key start stop [WITHSCORES] 通过索引区间返回有序集合成指定区间内的成员
        System.out.println("------------ZRANGE----------");
        System.out.println(redisTemplate.opsForZSet().range("a", 0, 2));
        //ZREVRANGE key start stop [WITHSCORES] 返回有序集中指定区间内的成员，通过索引，分数从高到底
        System.out.println("------------ZREVRANGE----------");
        System.out.println(redisTemplate.opsForZSet().reverseRange("a", 0, 2));

        //ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT] 通过分数返回有序集合指定区间内的成员
        System.out.println("------------ZRANGEBYSCORE----------");
        System.out.println(redisTemplate.opsForZSet().rangeByScore("a", 80, 100));
        System.out.println(redisTemplate.opsForZSet().rangeByScore("a", 80, 100, 0, 1));

        //ZSCORE key member 返回有序集中，成员的分数值
        System.out.println("------------ZSCORE----------");
        System.out.println(redisTemplate.opsForZSet().score("a", "张三"));

        //ZRANK key member 返回有序集合中指定成员的索引
        System.out.println("------------ZRANK----------");
        System.out.println(redisTemplate.opsForZSet().rank("a", "张三"));
        //ZREVRANK key member 返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序
        System.out.println("------------ZREVRANK----------");
        System.out.println(redisTemplate.opsForZSet().reverseRank("a", "张三"));

        //ZREM key member [member ...] 移除有序集合中的一个或多个成员
        System.out.println("------------ZREM----------");
        System.out.println(redisTemplate.opsForZSet().remove("a", "张三"));
        //ZREMRANGEBYRANK key start stop 移除有序集合中给定的排名区间的所有成员
        System.out.println("------------ZREMRANGEBYRANK----------");
        System.out.println(redisTemplate.opsForZSet().removeRange("zsetlexCount", 0, 1));
        //ZREMRANGEBYSCORE key min max 移除有序集合中给定的分数区间的所有成员
        System.out.println("------------ZREMRANGEBYSCORE----------");
        System.out.println(redisTemplate.opsForZSet().removeRangeByScore("zsetlexCount", 60, 80));
        //SRANDMEMBER
        System.out.println("------------SRANDMEMBER----------");
        System.out.println(redisTemplate.opsForZSet().randomMember("a"));

        System.out.println("----------- 遍历Zset ----------");
        int size = redisTemplate.opsForZSet().zCard("a").intValue();
        System.out.println("Zset a size： " + size);
        while (size > 0) {
            System.out.println(redisTemplate.opsForZSet().range("a", --size, size));//只读取不删除，但只返回value
        }
        //推荐使用rangeByScoreWithScores遍历Zset
        size = redisTemplate.opsForZSet().zCard("a").intValue();
        System.out.println("Zset a size： " + size);
        while (size > 0) {
            System.out.println(redisTemplate.opsForZSet().rangeByScoreWithScores("a", 0, 100, --size, 1));//从Zset中移出
        }

        size = redisTemplate.opsForZSet().zCard("a").intValue();
        System.out.println("Zset a size： " + size);
        while (size > 0) {
            System.out.println(redisTemplate.opsForZSet().popMax("a"));//从Zset中移出
            size--;
        }
        size = redisTemplate.opsForZSet().zCard("a").intValue();
        System.out.println("Zset a size： " + size);
    }

    /**
     * Streams 数据类型相关操作
     */
    @Test
    public void streamsRedis() {
        /**
         * STREAM独立消费类型特点：
         *      消息可回溯，读取不会删除
         *      一个消息可以被多个消费者读取
         *      可以阻塞读取
         *      有消息漏读的风险：
         *          当我们指定起始ID为$时，代表读取最新的消息，如果我们处理一条消息的过程中，
         *          又有超过1条以上的消息到达队列，则下次获取时也只能获取到最新的一条，会出现漏读消息的问题
         */
        System.out.println("-----------STREAM独立消费类型-------------");
        //清数据
        redisTemplate.delete(redisTemplate.keys("*"));

        //XADD key ID field string [field string ...]  添加key主题的消息到末尾
        Map<String, String> map = new HashMap<>();
        map.put("琴", "琴瑟");
        map.put("棋", "围棋");
        map.put("书", "书法");
        map.put("画", "绘画");
        //XADD key ID field string [field string ...]将指定的流条目追加到指定key的流中
        MapRecord<String, String, String> mapRecord = StreamRecords.mapBacked(map).withStreamKey("my-queue");
        RecordId recordId = redisTemplate.opsForStream().add(mapRecord);
        System.out.println(recordId.toString());
        System.out.println(redisTemplate.opsForStream().add("my-queue", map).toString());

        //XREAD [COUNT count] [BLOCK milliseconds] STREAMS key [key ...] ID [ID ...] 从一个或多个stream中读取数据，仅返回ID大于调用者报告的最后接收ID的条目。
        //BLOCK项用于指定阻塞时长。STREAMS项必须在最后，用于指定stream和ID。
        List<MapRecord<String, Object, Object>> recordList = redisTemplate.opsForStream().read( Consumer.from("myGroup", "myConsumer"), //独立消费无消费组
                StreamReadOptions.empty().count(10).block(Duration.of(5000, ChronoUnit.SECONDS)), StreamOffset.fromStart("my-queue"));
        recordList.stream().forEach(record -> {
            System.out.println("redisTemplate.opsForStream().read：-------StreamReadOptions.empty().count(1).block(Duration.of(5000, ChronoUnit.SECONDS))------");
            System.out.println("id=" + record.getId());
            System.out.println("stream=" + record.getStream());
            System.out.println("value=" + JSONObject.toJSONString(record.getValue()));
        });

        List<MapRecord<String, Object, Object>> recordList1 = redisTemplate.opsForStream().read( Consumer.from("myGroup", "myConsumer"), //独立消费无消费组
                StreamReadOptions.empty(), StreamOffset.fromStart("my-queue"));
        recordList1.stream().forEach(record -> {
            System.out.println("redisTemplate.opsForStream().read：-------StreamReadOptions.empty()------");
            System.out.println("id=" + record.getId());
            System.out.println("stream=" + record.getStream());
            System.out.println("value=" + JSONObject.toJSONString(record.getValue()));
        });

        //XDEL key ID [ID ...]删除stream中的entry并返回删除的数量。
        System.out.println("redisTemplate.opsForStream().delete：" + redisTemplate.opsForStream().delete("my-queue", recordId));

        // XLEN key返回stream中的entry数量。如果key不存在，则返回0。对于长度为0的stream，Redis不会删除，因为可能存在关联的消费者组。
        System.out.println("redisTemplate.opsForStream().size：" + redisTemplate.opsForStream().size("my-queue"));

        //XRANGE key start end [COUNT count]：该命令用于返回stream中指定ID范围的数据，可以使用-和+表示最小和最大ID。ID也可以指定为不完全ID，即只指定Unix时间戳，就可以获取指定时间范围内的数据。
        //读取队列的前10条记录，从ID=0的记录开始，一直到ID最大值
        System.out.println("redisTemplate.opsForStream().range：" + redisTemplate.opsForStream().range("my-queue", Range.closed("0", "+"), RedisZSetCommands.Limit.limit().count(2)));
        /**
         * STREAM消费组类型特点：
         *         消息可回溯
         *         可以多消费者争抢消息，加快消费速度
         *         可以阻塞读取
         *         没有消息漏读的风险
         *         有消息确认机制，保证消息至少被消费一次
         */
        System.out.println("-----------STREAM消费组类型-------------");
        //清数据
        redisTemplate.delete(redisTemplate.keys("*"));
        //创建消费者组 XGROUP CREATE key groupName ID [MKSTREAM]
        System.out.println("redisTemplate.opsForStream().createGroup：" + redisTemplate.opsForStream().createGroup("myStream", "myGroup"));
        System.out.println("redisTemplate.opsForStream().createGroup：" + redisTemplate.opsForStream().createGroup("myStream", "myGroup1"));
        // 在消费组 myGroup 中创建一个名为 myConsumer 的消费者
        // 消费者将从名为 myStream 的 Stream 中消费消息
        Map<String, String> map1 = new HashMap<>();
        map1.put("琴", "琴瑟");
        map1.put("棋", "围棋");
        map1.put("书", "书法");
        map1.put("画", "绘画");
        //XADD key ID field string [field string ...]将指定的流条目追加到指定key的流中
        MapRecord<String, String, String> mapRecord1 = StreamRecords.mapBacked(map1).withStreamKey("myStream");
        System.out.println("------redisTemplate.opsForStream().add------" + redisTemplate.opsForStream().add(mapRecord1).toString());
        System.out.println("------redisTemplate.opsForStream().add------" + redisTemplate.opsForStream().add("myStream", map1).toString());

        StreamInfo.XInfoStream xInfoStream = redisTemplate.opsForStream().info("myStream");
        System.out.println("------redisTemplate.opsForStream().info---StreamInfo.XInfoStream.getFirstEntry() ---" + xInfoStream.getFirstEntry());
        long streamSize = xInfoStream.streamLength();
        System.out.println("------redisTemplate.opsForStream().info---StreamInfo.XInfoStream.streamLength() ---" + streamSize);

        System.out.println("--------redisTemplate.opsForStream().read：XREADGROUP -------");
        while (streamSize > 0) {
            List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().read(
                    Consumer.from("myGroup", "myConsumer"),
                    StreamReadOptions.empty(),
                    StreamOffset.create("myStream", ReadOffset.lastConsumed()/**读取 ID 大于使用者组使用的最后一个元素的所有新到达元素**/));
            for(MapRecord<String, Object, Object> message : messages) {
                System.out.println("id=" + message.getId());
                System.out.println("stream=" + message.getStream());
                System.out.println("value=" + JSONObject.toJSONString(message.getValue()));
            }

            System.out.println("--------redisTemplate.opsForStream().read：XREADGROUP 2-------");
            List<MapRecord<String, Object, Object>> messages1 = redisTemplate.opsForStream().read(
                    Consumer.from("myGroup", "myConsumer1"),
                    StreamReadOptions.empty().count(10).noack(),
                    StreamOffset.create("myStream", ReadOffset.lastConsumed()));
            for(MapRecord<String, Object, Object> message1 : messages1) {
                System.out.println("id=" + message1.getId());
                System.out.println("stream=" + message1.getStream());
                System.out.println("value=" + JSONObject.toJSONString(message1.getValue()));
            }
            streamSize--;
        }

        //消费组信息
        StreamInfo.XInfoConsumers xInfoConsumers = redisTemplate.opsForStream().consumers("myStream", "myGroup");
        System.out.println("------------redisTemplate.opsForStream().consumers------------");
        Iterator<StreamInfo.XInfoConsumer> iterator = xInfoConsumers.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toString());
        }

        //显示待处理消息的相关信息
        System.out.println("-------------redisTemplate.opsForStream().pending------------xpending显示待处理消息的相关信息--");
        PendingMessages pendingMessages = redisTemplate.opsForStream().pending("myStream", "myGroup", Range.closed("0", "+"), 10);
        Iterator<PendingMessage> itr = pendingMessages.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next().toString());
        }
        PendingMessages pendingMessages1 = redisTemplate.opsForStream().pending("myStream", "myGroup1", Range.closed("0", "+"), 10);
        Iterator<PendingMessage> itr1 = pendingMessages1.iterator();
        while (itr1.hasNext()) {
            System.out.println(itr1.next().toString());
        }
        System.out.println("--------redisTemplate.opsForStream().acknowledge------XACK----" +
                redisTemplate.opsForStream().acknowledge("myStream", "myGroup", pendingMessages.get(0).getId()));
        PendingMessages pendingMessages2 = redisTemplate.opsForStream().pending("myStream", "myGroup", Range.closed("0", "+"), 10);
        Iterator<PendingMessage> itr2 = pendingMessages2.iterator();
        RecordId recordIdXclaim = null;
        while (itr2.hasNext()) {
            recordIdXclaim = itr2.next().getId();
            System.out.println(recordIdXclaim);
        }
        StreamInfo.XInfoConsumers xInfoConsumers1 = redisTemplate.opsForStream().consumers("myStream", "myGroup");
        System.out.println("------------redisTemplate.opsForStream().consumers------------");
        Iterator<StreamInfo.XInfoConsumer> iteratorConsumers = xInfoConsumers1.iterator();
        while (iteratorConsumers.hasNext()) {
            System.out.println(iteratorConsumers.next().toString());
        }

        //XCLAIM 这个命令用于改变pending消息的所有权，新的owner是命令参数中的consumer。
        //最终调用的是XCLAIM
        System.out.println("----------------XCLAIM---------------");
        RecordId finalRecordIdXclaim = recordIdXclaim;
        List<ByteRecord> retVal = this.redisTemplate.execute((RedisCallback<List<ByteRecord>>) connection -> { // XCLAIM 指令的实现方法
            return connection.streamCommands().xClaim("myStream".getBytes(), "myGroup", "myConsumer1", Duration.ofSeconds(10), finalRecordIdXclaim);
        });
        for (ByteRecord byteRecord : retVal) {
            System.out.println("id=" + byteRecord.getId());
            System.out.println("stream=" + byteRecord.getStream());
            System.out.println("value=" + JSONObject.toJSONString(byteRecord.getValue()));
        }

        System.out.println("------------redisTemplate.opsForStream().consumers------------");
        StreamInfo.XInfoConsumers xInfoConsumers2 = redisTemplate.opsForStream().consumers("myStream", "myGroup");
        Iterator<StreamInfo.XInfoConsumer> iteratorConsumers2 = xInfoConsumers2.iterator();
        while (iteratorConsumers2.hasNext()) {
            System.out.println(iteratorConsumers2.next().toString());
        }

        //删除消费者组中的指定消费者：XGROUP DELCONSUMER mystream consumer-group-name myconsumer123
        Consumer consumer = Consumer.from("myGroup", "myConsumer");
        Assertions.assertTrue(redisTemplate.opsForStream().deleteConsumer("myStream", consumer));

        //删除指定的消费者组：XGROUP DESTROY mystream some-consumer-group
        Assertions.assertTrue(redisTemplate.opsForStream().destroyGroup("myStream", "myGroup"));
    }

    /**
     * HyperLogLog 数据类型相关操作
     */
    @Test
    public void hyperLogLogRedis() {

        /**
         * 1：HyperLogLog是一种算法，并非redis独有
         * 2：目的是做基数统计，故不是集合，不会保存元数据，只记录数量而不是数值。
         * 3：耗空间极小，支持输入非常体积的数据量
         * 4：核心是基数估算算法，主要表现为计算时内存的使用和数据合并的处理。最终数值存在一定误差
         * 5：redis中每个hyperloglog key占用了12K的内存用于标记基数
         * 6：pfadd命令并不会一次性分配12k内存，而是随着基数的增加而逐渐增加内存分配；而pfmerge操作则会将sourcekey合并后存储在12k大小的key中，
         *   这由hyperloglog合并操作的原理（两个hyperloglog合并时需要单独比较每个桶的值）可以很容易理解。
         * 7：误差说明：基数估计的结果是一个带有 0.81% 标准错误（standard error）的近似值。是可接受的范围
         * 8：Redis 对 HyperLogLog 的存储进行了优化，在计数比较小时，它的存储空间采用稀疏矩阵存储，空间占用很小，仅仅在计数慢慢变大，
         *   稀疏矩阵占用空间渐渐超过了阈值时才会一次性转变成稠密矩阵，才会占用 12k 的空间
         * 9：HyperLogLog算法一开始就是为了大数据量的统计而发明的，所以很适合那种数据量很大，然后又没要求不能有一点误差的计算，
         *   HyperLogLog 提供不精确的去重计数方案，虽然不精确但是也不是非常不精确，标准误差是 0.81%，不过这对于页面用户访问量是没影响的，
         *   因为这种统计可能是访问量非常巨大，但是又没必要做到绝对准确，访问量对准确率要求没那么高，但是性能存储方面要求就比较高了，
         *   而HyperLogLog正好符合这种要求，不会占用太多存储空间，同时性能不错
         */
        // Pfadd 命令 添加指定元素到 HyperLogLog 中。
        for (int i = 0; i < 1000; i++) {
            redisTemplate.opsForHyperLogLog().add("pv1", String.valueOf(i));
        }

        // Pfcount 命令 返回给定 HyperLogLog 的基数估算值。
        System.out.println("Redis HyperLogLog command for Pfcount ：" + redisTemplate.opsForHyperLogLog().size("pv1"));
    }

    /**
     * geo 数据类型相关操作
     */
    @Test
    public void geoRedis() {
        Point point = new Point(116.41338, 39.91092);
        redisTemplate.opsForGeo().add("geo", point, "北京");
        Point point1 = new Point(108.94647, 34.34727);
        redisTemplate.opsForGeo().add("geo", point1, "西安");


        Distance d = redisTemplate.opsForGeo().distance("geo", "北京", "西安", RedisGeoCommands.DistanceUnit.KILOMETERS);
        System.out.println("Redis Geo command for GEODIST ： " + d);
        System.out.println("Redis Geo command for GEODIST ： " + d.getValue());
    }

    /**
     * bitmaps 数据类型相关操作
     */
    @Test
    public void bitmapsRedis() {

        /**
         * SETBIT ：把bit array的第bit位设置为value的值，value只能是0或者1。
         * GETBIT ：返回bit array的第bit位的二进制值。
         * BITCOUNT ：返回bit array中1的个数。
         * BITOP ：OP操作有AND(与)、OR(或)、XOR(异或)、NOT(非)，该命令表示将array1和array2进行位操作，并把结构存入result中。
         */
        //set 记录8900001客户浏览了哪些网站
        RedisUtil.Bitmaps.BitMapKey bitMapKey = RedisUtil.Bitmaps.BitMapKey.computeUserGroup(8900001);
        System.out.println(bitMapKey);

        String redisKey = bitMapKey.generateKeyWithPrefix("Browse");
        System.out.println("BitMap key ：" + redisKey);
        RedisUtil.Bitmaps.setBit(redisKey, "www.baidu.com", true);
        RedisUtil.Bitmaps.setBit(redisKey, "www.google.com", true);

        System.out.println("BITCOUNT ：返回bit array中1的个数 ：" + RedisUtil.Bitmaps.bitCount(redisKey));

        System.out.println("GETBIT ：返回bit array的第bit位的二进制值" + RedisUtil.Bitmaps.getBit(redisKey, "www.google.com"));
        System.out.println("BITCOUNT ：返回bit array中1的个数 ：" + RedisUtil.Bitmaps.bitCount(redisKey));

    }
}

