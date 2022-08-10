package com.yzy.analyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yzy
 * 
 */
public class CJKAnalyzer extends Analyzer {

	public CJKAnalyzer() {
		super();
	}

	/**
	 * 一元切词
	 * 
	 * @return
	 */
	public List<Token> _process(char[] inputChar) {
		List<Token> list = new ArrayList<Token>();
		Token word;
		int pos = 0;
		while (true) {
			word = new Token();
			pos = nextWord(pos, word, inputChar);
			if (pos == -1)
				break;
			list.add(word);
		}
		return list;
	}

	/**
	 * 一元切词
	 * 
	 * @return
	 */
	public List<Token> _processCN(char[] inputChar) {
		List<Token> list = new ArrayList<Token>();
		Token word;
		int pos = 0;
		while (true) {
			word = new Token();
			pos = nextWord(pos, word, inputChar);
			if (pos == -1)
				break;
			if (word.type == Analyzer.CJK)
				list.add(word);
		}
		return list;
	}

	/**
	 * 取下一个单词
	 * 
	 * @return
	 */
	private int nextWord(int pos, Token token, char[] inputChar) {
		char nowCh;
		if (pos < inputChar.length)
			nowCh = inputChar[pos];
		else {
			return -1;
		}
		while (!isCJKChar(nowCh) && !Character.isLetterOrDigit(nowCh)) {// 忽略其它字符
			pos++;
			if (pos < inputChar.length)
				nowCh = inputChar[pos];
			else {
				return -1;
			}
		}
		
		if (isCJKChar(nowCh)) {// 中文
			token.type = CJK;
			token.startPos = pos;
			token.endPos = pos + 1;
			token.value += nowCh;
			pos++;
			return pos;
		}
		if (Character.isLetter(nowCh)) {// 字母
			token.type = ALPHA;
			token.startPos = pos;
			while (Character.isLetter(nowCh)) {// 选判断下一个字符是否为字母
				token.value += nowCh;
				pos++;
				if (pos < inputChar.length)
					nowCh = inputChar[pos];
				else {
					break;
				}
			}
			token.endPos = pos;
			return pos;
		}
		if (Character.isDigit(nowCh)) {// 数字
			token.type = NUM;
			token.startPos = pos;
			while (Character.isDigit(nowCh)) {// 选判断下一个字符是否为数字
				token.value += nowCh;
				pos++;
				if (pos < inputChar.length)
					nowCh = inputChar[pos];
				else {
					break;
				}
			}
			token.endPos = pos;
			return pos;
		}
		return -1;
	}

	/**
	 * 开始分析字符串 循环二元切词，如ABC->AB,BC,CA
	 * 
	 * @return
	 */
	@Override
	public List<Token> process(char[] inputChar) {
		List<Token> list = _process(inputChar);
		Token t = new Token();
		// 暂时只认为全部为中文
		for (int i = 0, j = 0; i < list.size(); i++) {
			if (i == 0) {
				t.value = list.get(0).value;
			}
			if (i + 1 == list.size()) {
				list.get(i).value = list.get(i).value + t.value;
			} else {
				j = i + 1;
				list.get(i).value = list.get(i).value + list.get(j).value;
			}

		}
		return list;
	}

	/**
	 * 过滤掉不是中文的
	 * @param inputChar
	 * @return
	 */
	public List<Token> processCN(char[] inputChar) {
		List<Token> list = _processCN(inputChar);
		Token t = new Token();
		// 暂时只认为全部为中文
		for (int i = 0, j = 0; i < list.size(); i++) {
			if (i == 0) {
				t.value = list.get(0).value;
			}
			if (i + 1 == list.size()) {
				list.get(i).value = list.get(i).value + t.value;
			} else {
				j = i + 1;
				list.get(i).value = list.get(i).value + list.get(j).value;
			}

		}
		return list;
	}

}
