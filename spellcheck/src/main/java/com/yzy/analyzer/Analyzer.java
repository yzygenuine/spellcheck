package com.yzy.analyzer;

import java.util.List;

/**
 * @author yzy
 * 
 */
public abstract class Analyzer {

	/**
	 * 字母
	 */
	public final static int ALPHA = 1;
	/**
	 * 中文
	 */
	public final static int CJK = 0;
	/**
	 * 数字
	 */
	public final static int NUM = 2;

	public Analyzer() {
	}

	public boolean IsChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 是否为中文字符
	 * 
	 * @param ch
	 * @return
	 */
	public boolean isCJKChar(char ch) {
		if (ch >= '\u4E00' && ch <= '\u9FFF') {
			return true;
		}
		return false;

	}

	/**
	 * 切出的term列表
	 * 
	 * @param inputChar
	 * @return
	 */
	public abstract List<Token> process(char[] inputChar);
}
