package cn.itcast.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import cn.itcast.entity.User;

public class HibernateDemo01 {
	public static void main(String[] args) {
		// 加载hibernate核心配置文件，创建 factory 创建
		SessionFactory factory = new Configuration().configure().buildSessionFactory();
		// 从factory 中获取 session 对象，这个 session 对象就像是连接池里面的connection 对象
		Session session = factory.openSession();
		// 开启事务
		Transaction transaction = session.beginTransaction();
		
		User user = new User();
		user.setUsername("Rose");
		user.setPassword("123456");
		
		// 保存user 对象
		session.save(user);
		
		transaction.commit();
		session.close();
		factory.close();
	}
}
