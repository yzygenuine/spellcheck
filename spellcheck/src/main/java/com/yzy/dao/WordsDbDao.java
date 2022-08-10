package com.yzy.dao;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.yzy.spelling.index.Document;
import com.yzy.spelling.util.DateUtil;

 

/**
 * 搜索关键词写入数据库
 * 
 * @author yzy
 */
@Service("wordsFileDao")
public class WordsDbDao implements WordsDao {
	static final Logger logger = LoggerFactory.getLogger(WordsDbDao.class);
	private JdbcTemplate jdbc;
	private int minLimitNum;
	
	

	public int getRunDay() {
		String runDaySql = "select runday from searchwordday";
		int tRunDay = 0;
		try {
			tRunDay = jdbc.queryForInt(runDaySql);
		} catch ( Exception e) {
			logger.warn("",e);
			// 若空数据，则返回默认的runday，不抛出异常
		}
		return tRunDay;
	}

	private int insert2Db(String content, int searchTimes, int runDay) {
		String insertSql = "insert into searchword(content, searchtimes,"
				+ " appearcount, firstday, allsearchtimes, occurdaynum) "
				+ "values(?, ?, ?, ?, ?, ?)";
		return jdbc.update(insertSql, new Object[] { content, searchTimes, 1,
				new Date(), searchTimes, runDay });
	}

	@SuppressWarnings("unchecked")
	private Map queryContent(String content) {
		String querySql = "select id,content,appearcount,allsearchtimes,occurdaynum"
				+ " from searchword where content = ?";
		try {
			Map map = jdbc.queryForMap(querySql, new Object[] { content });
			return map;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 返回所有数据
	 * 
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public List<Document> readAllData() {
		String queryModifySql = "select id,content,allsearchtimes,occurdaynum"
				+ " from searchword";
		try {
			List<Document> result = jdbc.query(queryModifySql, new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					Document doc = new Document();
					doc.setId(rs.getInt("id"));
					doc.setText(rs.getString("content"));
					doc.setFreq(rs.getInt("allsearchtimes"));
					doc.setDay(rs.getShort("occurdaynum"));
					return doc;
				}

			});
			return result;
		} catch (EmptyResultDataAccessException e) {
			// 若空数据，则返回null，不抛出异常
		}
		return Collections.emptyList();
	}

	private Map<String, Integer> readData() {
		logger.info("read data");
		// 读取昨天的表，放置在数据库中
		final Map<String, Integer> dataMap = new HashMap<String, Integer>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date frontDate = DateUtil.getDateAfterNDays(-1, new Date());
		String tableName = "log_" + formatter.format(frontDate);
		logger.info("tableName is " + tableName);
		String sql = "SELECT keyword,count FROM `" + tableName
				+ "` WHERE count>=?";
		jdbc.query(sql, new Object[] { minLimitNum }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				dataMap.put(rs.getString("keyword"), rs.getInt("count"));
				return null;
			}
		});
		return dataMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Integer> readData(URL updateFileNameURL) {
		logger.info("read data");
		File file = null;
		try {
			file = new File(updateFileNameURL.toURI());
		} catch (URISyntaxException e1) {
			logger.debug("", e1);
		}
		if (!file.exists()) {
			logger.info("the file is no exists");
			return readData();
		}
		Map<String, Integer> dataMap = new HashMap<String, Integer>();
		try {
			List<String> lines = FileUtils.readLines(file);
			for (String line : lines.toArray(new String[0])) {
				String[] lineData = line.split("\\s+");
				// lineData[1] 为搜索次数， lineData[2]为关键字
				int freq = Integer.valueOf(lineData[1]);
				if (freq < minLimitNum) {
					continue;
				}
				dataMap.put(lineData[2], freq);
			}
		} catch (IOException e) {
			// e.printStackTrace();
			return readData();
		}
		return dataMap;
	}

	@SuppressWarnings("unchecked")	
	public List<Document> readUpdateData() {
		String queryModifySql = "select id,content,allsearchtimes,occurdaynum"
				+ " from searchword where isUpdate = ?";
		try {
			List<Document> result = jdbc.query(queryModifySql,
					new Object[] { 1 }, new RowMapper() {
						@Override
						public Object mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							Document doc = new Document();
							doc.setId(rs.getInt("id"));
							doc.setText(rs.getString("content"));
							doc.setFreq(rs.getInt("allsearchtimes"));
							doc.setDay(rs.getShort("occurdaynum"));
							return doc;
						}

					});
			return result;
		} catch (EmptyResultDataAccessException e) {
			// 若空数据，则返回null，不抛出异常
		}
		return Collections.emptyList();
	}

	/**
	 * 读数据库记录 数据记录到另一个数据 表
	 */
	// 先更新runDay的值。
	public void run() {
		// 读取数据文件
		Map<String, Integer> map = readData();
		logger.info("搜索词次数大于" + minLimitNum + "的词的数 量为：" + map.size());
		// 保存到数据库
		boolean isSuc = save2Db(map);
		if (isSuc) {
			logger.info("修改runday的值");
			updateRunDay();
		}
	}

	/**
	 * 读文件 数据记录到数据 库
	 */
	// 先更新runDay的值。
	public void run(URL updateFileNameURL) {
		updateRunDay();
		// 读取数据文件
		Map<String, Integer> map = readData(updateFileNameURL);

		// 保存到数据库
		save2Db(map);
	}

	@SuppressWarnings("unchecked")
	private boolean save2Db(Map<String, Integer> map) {
		logger.info("save data to database");
		int runDay = getRunDay();
		for (Entry<String, Integer> entry : map.entrySet()) {
			String content = entry.getKey();
			int searchTimes = entry.getValue();
			Map contentMap = queryContent(content);

			// 若数据库不存在对应的内容，插入新数据
			if (contentMap == null || contentMap.isEmpty()) {
				int ok = insert2Db(content, searchTimes, runDay);
				if (ok > 0) {
					logger.debug("insert ok : " + content);
				} else {
					logger.debug("insert fail : " + content);
				}
			} else { // 存在数据，则更新
				int ok = updateInDb(content, searchTimes, contentMap);
				if (ok > 0) {
					logger.debug("update ok : " + content);
				} else {
					logger.debug("update fail : " + content);
				}
			}
		}
		logger.info("save to db finish");
		return true;
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbc = new JdbcTemplate(dataSource);
	}

	public void setMinLimitNum(int minLimitNum) {
		this.minLimitNum = minLimitNum;
	}

 

	/**
	 * 从数据库读出今天更新的数据,若没有数据更新，则返回null
	 * 
	 * @param now
	 * @return list, 其中属性有:id,content,allsearchtimes,occurdaynum
	 */
	public boolean setSucUdate() {
		String updateSql = "update searchword set isUpdate=0 where isUpdate = 1";
		int i = jdbc.update(updateSql);
		if (i > 0)
			return true;
		else
			return false;

	}

	@SuppressWarnings("unchecked")
	private int updateInDb(String content, int searchTimes, Map contentMap) {
		String updateSql = "update searchword set searchtimes = ?, appearcount = ?,"
				+ " allsearchtimes = ?,isUpdate=? where id = ?";
		long appearcount = (Long) contentMap.get("appearcount");
		appearcount++;

		long allSearchTimes = (Long) contentMap.get("allsearchtimes");
		allSearchTimes += searchTimes;

		int id = (Integer) contentMap.get("id");

		return jdbc.update(updateSql, new Object[] { searchTimes, appearcount,
				allSearchTimes, 1, id });
	}

	// public void setDataFilePath(String dataFilePath) {
	// this.dataFilePath = dataFilePath;
	// }

	public boolean updateRunDay() {
		String updateRunDaySql = "update searchwordday set  runday=runday+1";
		try {
			int i = jdbc.update(updateRunDaySql);
			if (i > 0)
				return true;

		} catch (EmptyResultDataAccessException e) {
			// 若空数据，则返回默认的runday，不抛出异常
		}
		return false;
	}
}
