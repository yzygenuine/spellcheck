package com.yzy.test;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.yzy.dao.WordsDbDao;

/**
 * 定时执行读取文件，更新数据库的操作
 * 
 * @author yzy
 */
public class DbMain {
	private static Logger logger = Logger.getLogger(DbMain.class);

	public static void main(String[] args) {
		DbMain db = new DbMain();
		db.registerUpdateDb();
	}

	void registerUpdateDb() {
		logger.info("更新数据库开始");
		logger.info(new Date());
		FileSystemXmlApplicationContext dbContext = new FileSystemXmlApplicationContext(
				"classpath:DBContext.xml");
		WordsDbDao db = (WordsDbDao) dbContext.getBean("DbManager");
		db.run();
		logger.info("更新结束");
	}
}
