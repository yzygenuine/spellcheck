package com.yzy.spelling.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.yzy.dao.WordsFileDao;
import com.yzy.spelling.index.Document;

/**
 * 字典操作类，读取词库字典和拼音字典
 * 
 * @author yzy
 */
public class DictOper {

	private static final String DICT_NAME = "dict";
//	private static final String ENCODING = "gbk";
	/** 拼音词典文件路径 */

	private static String pinyinDictPath = "pyDic.txt";

	private static String splitor = "\\s+"; // 默认分隔符

	// 搜索次数 关键词
	// 100 ABC
	// 90 BCD
	// 80 CBD

	/**
	 * 建立拼音的数据结构 HashMap(String,List<String>)<br/>
	 * 每个首字母对应其可能的拼音组合,如:a:a,ai,an,ang,ao
	 * 
	 */
	public static Map<String, List<String>> getPYData() {
		String[] pyDict = DictOper.readPYDict();
		Map<String, List<String>> pyData = new HashMap<String, List<String>>();
		for (String str : pyDict) {
			String key = str.substring(0, 1);
			List<String> list = pyData.get(key);
			if (list == null) {
				list = new ArrayList<String>();
			}
			list.add(str);
			pyData.put(key, list);
		}
		return pyData;
	}

	/** 指定拼音字典路径的的拼音Map */
	public static Map<String, List<String>> getPYData(String dictPath) {
		pinyinDictPath = dictPath;
		return getPYData();
	}

	/**
	 * 读取dict目录 下所有词典文件
	 * 
	 * @return
	 */
	public static Set<Document> readDocFromDictDir() {
		Set<Document> allDocSet = new HashSet<Document>();
		URL in = WordsFileDao.class.getClassLoader().getResource(DICT_NAME);
		try {
		File dictRoot = new File(in.toURI());
//		File dictRoot = new File(DICT_NAME);
		if (dictRoot.isDirectory()) {
			File dictFiles[] = dictRoot.listFiles();
			for (File dictFile : dictFiles) {
				if (!dictFile.isFile() || !dictFile.canRead()) {
					continue;
				}
				Set<Document> docSet = readDocFromFile(dictFile);
				if (docSet != null) {
					allDocSet.addAll(docSet);
				}
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return allDocSet;
	}

	/**
	 * 以空格分隔字符，使用默认分隔符"\\s+"
	 */
	public static Set<Document> readDocFromFile(File f) {
		splitor = "\\s+";
		return readDocFromFile(f, splitor);
	}

	/**
	 * 读取词典文件 中的所有单词，每一行为一个词或者还有相应的词频（搜索次数）
	 * 
	 * @param f
	 *            词典文件
	 * @param splitor
	 *            分隔符，默认为"\\s+"
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set<Document> readDocFromFile(File f, String splitor) {
		Set<Document> docSet = new HashSet<Document>();
		try {
			List<String> list = FileUtils.readLines(f);
			// int i=0;

			for (String line : list) {
				if (line == null || line.isEmpty())
					continue;
				String ss[] = line.split(splitor);
				if (ss.length <= 0)
					continue;
				// 有待改善
				if (ss.length == 1) {
					docSet.add(new Document(ss[0], 0));
				} else if (ss.length == 3) {
					docSet.add(new Document(ss[2], Integer.valueOf(ss[1])));
				}
				// i++;
			}
			list = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return docSet;
	}

	/**
	 * 读取拼音字典
	 * 
	 */
//	public static String[] readPYDict() {
//		String[] pinyinDict = null;
//		try {
//			ClassLoader classLoader=DictOper.class.getClassLoader();
//			InputStream in = classLoader.getResourceAsStream(pinyinDictPath);
//			BufferedReader reader =
//				new BufferedReader(new InputStreamReader(in, "utf8"));
//			List<String> lines=new ArrayList<String>();
//			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
//				lines.add(line);
//			}
//			pinyinDict = lines.toArray(new String[0]);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return pinyinDict;
//	}

	
	public static String[]  readPYDict() {
		String[] pinyinDict = null;
		try {	
			URL in = WordsFileDao.class.getClassLoader().getResource(pinyinDictPath);
			File f = new File(in.toURI());
			List<String> lines = FileUtils.readLines(f);
			pinyinDict = lines.toArray(new String[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pinyinDict;
	}
	/**
	 * 读取dictPath路径下的拼音字典
	 * 
	 */
	public static String[] readPYDict(String dictPath) {
		pinyinDictPath = dictPath;
		return readPYDict();
	}

	public static void setSplitor(String splitor) {
		DictOper.splitor = splitor;
	}

}
