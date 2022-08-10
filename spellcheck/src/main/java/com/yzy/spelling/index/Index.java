package com.yzy.spelling.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yzy.analyzer.Analyzer;
import com.yzy.analyzer.CJKAnalyzer;
import com.yzy.analyzer.PinYinAnalyser;
import com.yzy.analyzer.Token;

/**
 * @author yzy
 * 
 */
public class Index {
	/**
	 * 存放索引
	 */
	private static ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>> indexKey2IdListMap = new ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>>();

	/**
	 * 索引管理类的实例引用
	 */
	private static Index instance;

	/**
	 * 存储文档
	 */
	private static ConcurrentHashMap<Integer, Document> instoreId2DocMap = new ConcurrentHashMap<Integer, Document>();

	/**
	 * 程序
	 */
	public static int runDay = 0;

	public static Index getInstance() {
		if (instance == null) {
			instance = new Index();
		}
		return instance;
	}

	/**
	 * 二元循环切词分析器
	 */
	private final Analyzer cjkAnalyzer = new CJKAnalyzer();

	/**
	 * 日志记录
	 */
	private final Logger logger = LoggerFactory.getLogger(Index.class);

	/**
	 * 拼音分析器
	 */
	private final Analyzer pinyinAnalyzer = new PinYinAnalyser();

	/**
	 * 简单拼音分析器
	 */
	@Deprecated
//	private final Analyzer simplePinyin = new SimpePinyinAnalyzer();

	private Index() {

	}

	/**
	 * 二元循环切词
	 * 
	 * @param list
	 */
	public void buildCJKIndex(List<Document> list) {
		if (list == null || list.size() <= 0) {
			throw new RuntimeException("索引内容不能为空");
		}

		for (int i = 0; i < list.size(); i++) {
			updateIndexByDoc(list.get(i), cjkAnalyzer);
		}
		list = null;

	}

	/**
	 * 用拼音与二元循环切词建索引
	 * 
	 * @param list
	 */
	public void buildIndex(List<Document> list) {
		if (list == null || list.size() <= 0) {
			logger.error("索引内容为空");
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			updateIndexByDoc(list.get(i), new Analyzer[] { cjkAnalyzer,
					pinyinAnalyzer });
		}
		list = null;
	}

	/**
	 * 用拼音与二元循环切词建索引
	 * 
	 * @param docSet
	 */
	public void buildIndex(Set<Document> docSet) {
		if (docSet == null || docSet.size() <= 0) {
			throw new RuntimeException("索引内容不能为空");
		}
		int id = 0;
		Iterator<Document> it = docSet.iterator();
		while (it.hasNext()) {
			Document doc = it.next();
			doc.setId(id);// 测试，正式要去掉
			updateIndexByDoc(doc,
					new Analyzer[] { cjkAnalyzer, pinyinAnalyzer });
			id++;
		}
		docSet = null;
	}

	/**
	 * 为拼音建索引,全词缩写索引
	 * 
	 * @param arrayIndex
	 */
	public void buildPinYinIndex(List<Document> arrayIndex) {
		if (arrayIndex == null || arrayIndex.size() <= 0) {
			throw new RuntimeException("索引内容不能为空");
		}
		for (int i = 0; i < arrayIndex.size(); i++) {
			if(i%3000==0){
				logger.info("已加载"+i+"条记录");
			}
			updateIndexByDoc(arrayIndex.get(i), pinyinAnalyzer);
		}
		logger.info("共加载"+arrayIndex.size()+"条记录");
		arrayIndex = null;
	}

	/**
	 * 获取相应的文档集合
	 * 
	 * @param idSet
	 *            文档id集合
	 * @return
	 */
	public List<Document> getDocListByText(Set<Integer> idSet) {
		List<Document> docList = new ArrayList<Document>();
		Iterator<Integer> it = idSet.iterator();
		while (it.hasNext()) {
			Document doc = instoreId2DocMap.get(it.next());
			if (doc != null)
				docList.add(doc);
		}
		return docList;
	}

	/**
	 * 默认为并集操作
	 * 
	 * @param text
	 * @return
	 */
	public List<Document> getDocListByText(String text, Analyzer analyzer) {
		List<Token> tokens = analyzer.process(text.toCharArray());
		if (tokens == null || tokens.size() <= 0) {
			logger.debug("token is null OR size is 0");
			return Collections.emptyList();
		}
		final HashSet<Integer> idSet = new HashSet<Integer>();
		for (int i = 0; i < tokens.size(); i++) {
			List<Integer> _idList = indexKey2IdListMap.get(tokens.get(i).value);
			if (_idList != null) {
				idSet.addAll(_idList);
			}
		}
		List<Document> docList = getDocListByText(idSet);
		return docList;
	}

	public List<Document> getDocListByText(String text, int flag) {
		if (flag == 0) {// 中文
			return getDocListByText(text, cjkAnalyzer);
		} else if (flag == 1) {
			return getDocListByText(text, pinyinAnalyzer);
		} else if (flag == 3) {
			return getDocListByText_CN(text, (CJKAnalyzer) cjkAnalyzer);
		}
		return Collections.emptyList();
	}

	/**
	 * 只分析其中的中文字符，其它过滤掉
	 * 
	 * @param text
	 * @param analyzer
	 * @return
	 */
	private List<Document> getDocListByText_CN(String text, CJKAnalyzer analyzer) {
		List<Token> tokens = analyzer.processCN(text.toCharArray());
		if (tokens == null || tokens.size() <= 0) {
			logger.debug("token is null OR size is 0");
			return Collections.emptyList();
		}
		HashSet<Integer> idSet = new HashSet<Integer>();
		for (int i = 0; i < tokens.size(); i++) {
			List<Integer> _idList = indexKey2IdListMap.get(tokens.get(i).value);
			if (_idList != null) {
				// logger.debug("_idList is not null");
				idSet.addAll(_idList);
			}
		}
		List<Document> docList = getDocListByText(idSet);
		return docList;
	}

	/**
	 * 通过key 获取相应的id列表
	 * 
	 * @param key
	 * @return
	 */
	public List<Integer> getIdList(String key) {
		List<Integer> _idList = indexKey2IdListMap.get(key);
		return _idList;
	}


	/**
	 * @param doc
	 *            文档
	 * @param analyzers
	 *            分析器数组
	 */
	public boolean updateIndexByDoc(Document doc, Analyzer analyzers[]) {
		if (doc == null || analyzers == null || analyzers.length <= 0) {
			logger.error("文档为空或者分析器为空");
			return false;
		}
		// 新词
		if (!instoreId2DocMap.containsKey(doc.getId())) {
			if (instoreId2DocMap.putIfAbsent(doc.getId(), doc) == null) {// 写入的时候为空，说明写入成功
			// logger.debug("写入成功");
			} else {// 有另一个写线程先写入
				logger.debug("另有线程捷足先登");
			}
			String text = doc.getText();
			List<Token> tokens = new ArrayList<Token>();
			for (Analyzer a : analyzers) {
				List<Token> e = a.process(text.toCharArray());
				tokens.addAll(e);
			}
			for (Token token : tokens) {
				CopyOnWriteArrayList<Integer> docList = null;
				do {// 增加索引文档ID
					docList = indexKey2IdListMap.get(token.value);
					if (docList == null) {
						docList = new CopyOnWriteArrayList<Integer>();
						if (indexKey2IdListMap
								.putIfAbsent(token.value, docList) != null) {// 有线程先增加
							docList = indexKey2IdListMap.get(token.value);
						}
					}
				} while (docList == null);
				if (docList.contains(doc.getId())) {// 已存在
					continue;
				}
				if (docList.addIfAbsent(doc.getId())) {// 增加成功
//					logger.debug("写入成功");
				} else {// 有另一个写线程先写入
					logger.debug("另有线程捷足先登");
				}
			}
		}
		// 旧词,只修改instoreId2DocMap 的数据
		else {
			boolean updateSuc = false;
			do {
				Document oldValue = instoreId2DocMap.get(doc.getId());
				// doc.setFreq(oldValue.getFreq() + doc.getFreq());
				// // 替换掉旧的数据，成功返回true 否则false
				updateSuc = instoreId2DocMap
						.replace(doc.getId(), oldValue, doc);
			} while (!updateSuc);
//			logger.debug("更新成功");
		}
		// 更新成功
		return true;
	}

	/**
	 * @param doc
	 *            文档
	 * @param analyzer
	 *            分析器
	 */
	public boolean updateIndexByDoc(Document doc, Analyzer analyzer) {
		if (doc == null || analyzer == null) {
			logger.error("文档为空或者分析器为空");
			return false;
		}
		// 新词
		if (!instoreId2DocMap.containsKey(doc.getId())) {
			if (instoreId2DocMap.putIfAbsent(doc.getId(), doc) == null) {// 写入的时候为空，说明写入成功
//				logger.debug("写入成功");
			} else {// 有另一个写线程先写入
				logger.debug("另有线程捷足先登");
			}
			String text = doc.getText();
			List<Token> tokens = analyzer.process(text.toCharArray());
			for (Token token : tokens) {
				CopyOnWriteArrayList<Integer> docList = null;
				do {// 增加索引文档ID
					docList = indexKey2IdListMap.get(token.value);
					if (docList == null) {
						docList = new CopyOnWriteArrayList<Integer>();
						if (indexKey2IdListMap
								.putIfAbsent(token.value, docList) != null) {// 有线程先增加
							docList = indexKey2IdListMap.get(token.value);
						}
					}
				} while (docList == null);
				if (docList.contains(doc.getId())) {// 已存在
					continue;
				}
				if (docList.addIfAbsent(doc.getId())) {// 增加成功
//					logger.debug("写入成功");
				} else {// 有另一个写线程先写入
					logger.debug("另有线程捷足先登");
				}
			}
		}
		// 旧词,只修改instoreId2DocMap 的数据，主要更新搜索数量
		else {
			boolean updateSuc = false;
			do {
				Document oldValue = instoreId2DocMap.get(doc.getId());
				doc.setFreq(oldValue.getFreq() + doc.getFreq());
				// // 替换掉旧的数据，成功返回true 否则false
				updateSuc = instoreId2DocMap
						.replace(doc.getId(), oldValue, doc);
			} while (!updateSuc);
//			logger.debug("更新成功");
		}

		// 更新成功
		return true;
	}

}
