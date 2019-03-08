package cn.itcast.demo;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

import cn.itcast.entity.User;

// 测试二级缓存的存在 
public class HibernateDemo09 {
	@Test
	public void testSecondCache() throws Exception {
		SessionFactory factory = new Configuration().configure().buildSessionFactory();
		Session session = factory.openSession();
		Transaction transaction = session.beginTransaction();
		User user = session.get(User.class, 10);
		System.out.println(user);
		transaction.commit();
		session.close();
		
		Session session2 = factory.openSession();
		Transaction transaction2 = session2.beginTransaction();
		User user2 = session2.get(User.class, 10);
		System.out.println(user2 == user);
		transaction2.commit();
		session2.close();
	}
	
	// createQuery() 查询的结果会保存在二级缓存中
	@Test
	public void testSecondCache2() {
		SessionFactory factory = new Configuration().configure().buildSessionFactory();
		Session session = factory.openSession();
		Transaction transaction = session.beginTransaction();
		
		List<User> list = session.createQuery("from User", User.class).list();
		System.out.println(list);
		transaction.commit();
		
		session.close();
		//factory.close();
		
		// 获取另外的 session ，在这个session 的基础上
		// 再次查询 id 为10 的user，因为刚才已经查过全表了，而且数据已经保存在二级缓存中了
		// 所以这里不会发送 sql 语句
		Session session2 = factory.openSession();
		Transaction transaction2 = session2.beginTransaction();
		User user = session2.get(User.class, 10);
		System.out.println(user);
		
		transaction2.commit();
		session2.close();
		factory.close();
	}
	
	// createQuery() 查询的结果会保存在二级缓存中, 但是本身并不从二级缓存中取数据
	@Test
	public void testSecondCache3() {
		SessionFactory factory = new Configuration().configure().buildSessionFactory();
		Session session = factory.openSession();
		Transaction transaction = session.beginTransaction();
		
		List<User> list = session.createQuery("from User", User.class).list();
		System.out.println(list);
		transaction.commit();
		
		session.close();
		//factory.close();
		
		// 前面我们已经试验过了，createQuery() 方法查询出来的数据会保存在二级缓存中
		// 但是我们再次查询同一条语句，却不会走缓存
		// 所以下面的语句会再执行一次查询
		Session session2 = factory.openSession();
		Transaction transaction2 = session2.beginTransaction();
		List<User> list2 = session2.createQuery("from User", User.class).list();
		System.out.println(list2);
		
		transaction2.commit();
		session2.close();
		factory.close();
	}
	
	// 开启查询缓存
	// 首先，我们得先开启hibernate 的二级缓存
	// <property name="hibernate.cache.use_second_level_cache">true</property>
	// 然后，我们还要提供二级缓存提供商
	// <property name="hibernate.cache.region.factory_class">org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory</property>  
	// 再然后，还要设置对哪些类开启二级缓存 
	// <class-cache usage="read-write" class="cn.itcast.entity.User"/>
	
	// 再然后，我们需要在配置文件中手动开启查询缓存： 
	// <property name="hibernate.cache.use_query_cache">true</property>
	// 再然后，我们还要在方法中明确指定开启查询缓存，才可以直接实现查询缓存
	// ===> 看下面的演示吧
	@Test
	public void testSecondCache4() {
		SessionFactory factory = new Configuration().configure().buildSessionFactory();
		Session session = factory.openSession();
		Transaction transaction = session.beginTransaction();
		
		// 必须明确指定 setCacheable(true) ，才会开启查询缓存，把查询数据保存在查询缓存中
		List<User> list = session.createQuery("from User", User.class).setCacheable(true).list();
		System.out.println(list);
		transaction.commit();
		session.close();
		
		
		Session session2 = factory.openSession();
		Transaction transaction2 = session2.beginTransaction();
		// 要使用查询缓存数据，也必须明确指定 setCacheable(true)
		List<User> list2 = session2.createQuery("from User", User.class).setCacheable(true).list();
		System.out.println(list2);
		transaction2.commit();
		session2.close();
		factory.close();
	}
}
