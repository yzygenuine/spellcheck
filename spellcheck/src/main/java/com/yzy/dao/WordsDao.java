package com.yzy.dao;

import java.util.List;

import com.yzy.spelling.index.Document;

/**
 * @author yzy
 *
 */
public interface WordsDao {
	
	/**
	 * 读取文档
	 * @return
	 */
	public List<Document> readAllData();

}
