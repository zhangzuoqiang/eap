package eap.comps.cache;

import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.code.ssm.Cache;
import com.google.code.ssm.providers.CacheException;

public class Main3 extends TestCase {
	public void test1() throws TimeoutException, CacheException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:AC-test-memcache.xml");
		Cache cache = (Cache) ctx.getBean("memcache1");
		
		cache.incr("abc", 1, 1);
		System.out.println(cache.getCounter("abc"));
		
		System.out.println("11111111111111");
		
		ClazzA ca = (ClazzA) ctx.getBean("clazzA");
		ca.m2("aaaa");
		ca.m2("aaaa");
		ca.m2("aaaa1");
		
	}
}
