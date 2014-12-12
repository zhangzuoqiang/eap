package eap.comps.cache;

import javax.persistence.SharedCacheMode;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.util.Pool;

public class Main2 extends TestCase {
	public void test1() {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:AC-test-redis.xml");
		
//		Pool<ShardedJedis> jedisPool = (Pool<ShardedJedis>) ctx.getBean("redisPool1");
//		
//		ShardedJedis sj = jedisPool.getResource();
//		for (int i = 0; i < 10; i++) {
//			sj.incr("test=" + i);
//		}
//		jedisPool.returnResource(sj);
		
//		Pool<Jedis> jedisPool = (Pool<Jedis>) ctx.getBean("redisPool2");
//		Jedis j = jedisPool.getResource();
//		for (int i = 0; i < 10; i++) {
//			j.incr("test1=" + i);
//		}
//		jedisPool.returnResource(j);
		
		Jedis j = (Jedis) ctx.getBean("redis3");
		for (int i = 0; i < 10; i++) {
			j.incr("test2=" + i);
		}
		
	}
}
