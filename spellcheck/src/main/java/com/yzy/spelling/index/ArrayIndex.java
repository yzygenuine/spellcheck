package com.yzy.spelling.index;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.yzy.analyzer.Analyzer;
import com.yzy.analyzer.CJKAnalyzer;
import com.yzy.analyzer.Token;

/**
 * @author yzy
 * 
 */
public class ArrayIndex {
	/**
	 * 存放索引
	 */
	private static Map<String, List<Document>> index;

	/**
	 * 分析器
	 */
	private Analyzer analyzer;

	/**
	 * 索引类的引用，采用单例模式
	 */
	private static ArrayIndex instance;

	private ArrayIndex() {
//		index = new TreeMap<String, ArrayList<Document>>();
		index=new TreeMap<String, List<Document>>();
	}

	public static ArrayIndex getInstance() {
		if (instance == null) {
			instance = new ArrayIndex();
		}
		return instance;
	}

	public void buildIndex(Document[] a) {
		if (a == null || a.length <= 0) {
			throw new RuntimeException("索引内容不能为空");
		}
		buildIndex(Arrays.asList(a));
	}

	/**
	 * 建立索引
	 * 
	 * @param arrayIndex
	 */
	public void buildIndex(Set<Document> arrayIndex) {
		if (arrayIndex == null || arrayIndex.size() <= 0) {
			throw new RuntimeException("索引内容不能为空");
		}
		if (analyzer == null) {// 默认
			analyzer = new CJKAnalyzer();
		}
		Map<String, List<Document>> _index=new TreeMap<String, List<Document>>();
		Iterator<Document> it = arrayIndex.iterator();
		Document doc = null;
		int i = 0;
		while (it.hasNext()) {
			doc = it.next();
			doc.setId(i);
			String text = doc.getText();
			if(i%1000==0){
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  "+text);
			}
			List<Token> tokens = analyzer.process(text.toCharArray());
			for (Token token : tokens) {
				List<Document> docList = _index.get(token.value);
				if (docList == null) {
					docList = new CopyOnWriteArrayList<Document>();
				}
				docList.add(doc);
				_index.put(token.value, docList);
			}
			i++;
		}
		arrayIndex = null;
		index = _index;
		System.out.println("index end#########################################################");
	}

	/**
	 * 建立索引
	 * 
	 * @param arrayIndex
	 */
	public void buildIndex(List<Document> arrayIndex) {
		if (arrayIndex == null || arrayIndex.size() <= 0) {
			throw new RuntimeException("索引内容不能为空");
		}
		if (analyzer == null) {// 默认
			analyzer = new CJKAnalyzer();
		}
		Map<String, List<Document>> _index = new TreeMap<String, List<Document>>();
		for (int i = 0; i < arrayIndex.size(); i++) {
			Document doc = arrayIndex.get(i);
			doc.setId(i);
			String text = doc.getText();
			// analyzer.setInputChar(text.toCharArray());
			List<Token> tokens = analyzer.process(text.toCharArray());
			for (Token token : tokens) {
				List<Document> docList = _index.get(token.value);
				if (docList == null) {
					docList = new CopyOnWriteArrayList<Document>();
				}
				docList.add(doc);
				_index.put(token.value, docList);
			}

		}
		arrayIndex = null;
		index = _index;
	}

	/**
	 * 获取词对应的所有文档id
	 * 
	 * @param key
	 * @return
	 */
	// public ArrayList<Document> getDocListByTerm(String term) {
	// Map<String, ArrayList<Document>> _index = index;
	// return _index.get(term);
	// }

	/**
	 * 默认为并集操作
	 * 
	 * @param text
	 * @return
	 */
	public Set<Document> getDocSetByText(String text) {
		List<Token> tokens = analyzer.process(text.toCharArray());
		Set<Document> docSet = new HashSet<Document>();
		if (tokens == null || tokens.size() <= 0)
			return docSet;
		Map<String, List<Document>> _index = index;
		for (int i = 0; i < tokens.size(); i++) {
			List<Document> _docList = _index.get(tokens.get(i).value);
			if (_docList != null)
				docSet.addAll(_docList);
		}
//		Iterator<Document> it = docSet.iterator();
//		while (it.hasNext()) {
//			Document doc=it.next();
//			System.out.println(doc.getText());
//		}

		return docSet;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = index.keySet().iterator();
		sb.append("{");
		while (it.hasNext()) {
			String key = it.next();
			sb.append("[");
			sb.append("key:" + key + "@ ");
			List<Document> docList = index.get(key);
			for (Document doc : docList) {
				sb.append(doc.getText() + " ");
			}
			sb.append("]\n");
		}
		sb.append("}\n");
		return sb.toString();
	}

}
