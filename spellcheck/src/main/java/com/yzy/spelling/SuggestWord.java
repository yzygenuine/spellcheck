package com.yzy.spelling;

import com.yzy.spelling.index.Index;

/**
 * SuggestWord, used in suggestSimilar method in SpellChecker class.
 * 
 * 
 */
final class SuggestWord {
	public short day;//出现时间 

	/**
	 * The freq of the word
	 */
	public int freq;// 搜索次数，优先比较高

	/**
	 * the score of the word
	 */
	public float score;// 编辑距离，1表示两个字符串完全相同，0表示两个字符串具有最大的不相似程度

	/**
	 * the suggested word
	 */
	public String string;//词内容

	public final int compareTo(SuggestWord a) {
		// first criteria: the edit distance
		
		// second criteria (if first criteria is equal): the popularity
		if (score > a.score) {
			return -1;
		}
		if (score < a.score) {
			return 1;
		}
		if (this.getHotLevel() > a.getHotLevel())
			return -1;
		if (this.getHotLevel() < a.getHotLevel())
			return 1;
 
		return 0;
	}

	/**
	 * 获取热度
	 * 
	 * @return
	 */
	public int getHotLevel() {
		int i = Index.runDay - day;
		if (i < 0)
			return 1;
		int hotLevel = freq / (i + 1);
		if (hotLevel <= 0)
			return 0;
		return hotLevel;
	}

	@Override
	public String toString() {
		return "SuggestWord [freq=" + freq + ", score=" + score + ", string="
				+ string + "]";
	}

}
