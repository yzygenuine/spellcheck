package com.yzy.spelling;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yzy.spelling.index.Document;
import com.yzy.spelling.index.Index;

/**
 *@author yzy  
 */
public class CnSpellcheck  implements SpellCheck{
	/**
	 * 日志记录引用
	 */
	private Logger logger = Logger.getLogger(CnSpellcheck.class);
	/**
	 * 默认最小的分数
	 */
	private float minScore = 0.5f;

	/**
	 * 索引类
	 */
	private Index index = Index.getInstance();

	/**
	 * 编辑距离的类
	 */
	private StringDistance sd;

	/**
	 * 建议的词组
	 * 
	 * @param word
	 *            某个词
	 * @param numSug
	 *            建议的个数
	 * @return
	 */
	public String[] suggestSimilar(String word, int numSug) {
		SuggestWordQueue sugQueue = _suggestSimilar(word, numSug);
		String[] list = new String[sugQueue.size()];
		for (int i = sugQueue.size() - 1; i >= 0; i--) {
			list[i] = sugQueue.pop().string;
		}
		return list;
	}

	/**
	 * 建议的词组
	 * 
	 * @param word
	 *            某个词
	 * @param numSug
	 *            建议的个数
	 * @return
	 */
	public List<String> suggestSimilarList(String word, int numSug) {
		SuggestWordQueue sugQueue = _suggestSimilar(word, numSug);
		List<String> list = new ArrayList<String>(numSug);
		for (int i = sugQueue.size() - 1; i >= 0; i--) {
			list.add(sugQueue.pop().string);
		}
		return list;
	}

	/**
	 * 建议的词组
	 * 
	 * @param word
	 *            某个词
	 * @param numSug
	 *            建议的个数
	 * @return
	 */
	public SuggestWordQueue _suggestSimilar(String word, int numSug) {
		if (sd == null) {
			sd = new NGramDistance(1);// 默认
		}
		float min = this.minScore;
		SuggestWordQueue sugQueue = new SuggestWordQueue(numSug);
		List<Document> docList = index.getDocListByText(word, 0);
		if (docList == null || docList.size() <= 0) {
			logger.debug("docList 为空，转用只取中文");
			// docList = index.getDocListByText(word, 3);
			// if (docList == null || docList.size() <= 0)
			return sugQueue;
		}
		logger.debug(word + "  找到相关的文档：" + docList.size());
		SuggestWord sugWord = new SuggestWord();
		for (Document doc : docList) {
			sugWord.string = doc.getText();
			// if (sugWord.string.equals(word)) {
			// continue;
			// }
			sugWord.score = sd.getDistance(word, sugWord.string);
			if (sugWord.score < min) {
				continue;
			}
			sugWord.freq = doc.getFreq();
			sugWord.day = doc.getDay();
			sugQueue.insertWithOverflow(sugWord);
			if (sugQueue.size() == numSug) {
				min = sugQueue.top().score;
			}
			sugWord = new SuggestWord();
		}
		return sugQueue;
	}

	public float getMinScore() {
		return minScore;
	}

	public void setMinScore(float minScore) {
		this.minScore = minScore;
	}

	public StringDistance getSd() {
		return sd;
	}

	public void setSd(StringDistance sd) {
		this.sd = sd;
	}

}
