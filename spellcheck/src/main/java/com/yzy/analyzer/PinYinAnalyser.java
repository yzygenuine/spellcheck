package com.yzy.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yzy.spelling.util.DictOper;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 把拼音分割，例如：fencisuanfa分成：fen ci suan fa 以空格为间隔
 * 
 */
public class PinYinAnalyser extends Analyzer {
	private final HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
	
	
	

	/** 拼音数据结构 */
	private Map<String, List<String>> pyData;
	{
		outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		pyData = DictOper.getPYData();
	}
	
	public static void main(String[] args) {
		PinYinAnalyser s = new PinYinAnalyser();
		List<Token> py = s.process("zhongguo".toCharArray());
		for (Token token : py) {
			System.out.println(token.value);

		}
	}

	/**
	 * 拼音转中文的处理，查找拼音词典，查出对应拼音<br/>
	 * 注意要先调用setInputChar(char[]),把字符转为纯字母
	 */
	private List<Token> _process(char[] inputChar) {
		List<Token> list = new ArrayList<Token>();
		try {
		String processResult = _processStringByMap(inputChar);
		list = str2token(processResult);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据Map查找是否存在对应的拼音<br/>
	 * 贪婪算法，最大匹配拼音序列
	 * 
	 * @param inputChar
	 * @return 以空格间隔的拼音字符串，eg: zhong guo ren
	 * @throws BadHanyuPinyinOutputFormatCombination 
	 */
	private String _processStringByMap(char[] inputChar) throws BadHanyuPinyinOutputFormatCombination {
		String temp = new String(inputChar);
		temp = PinyinHelper.toHanyuPinyinString(temp, outputFormat, "");
		String[] strArray = temp.split(" ");
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < strArray.length; i++) {
			String curStr = strArray[i];
			int curStrLen = curStr.length();
			int beginIndex = 0, nextWordIndex = 0;
			while (beginIndex < curStrLen) {
				String firstLetter = curStr.substring(beginIndex,
						beginIndex + 1);
				List<String> list = pyData.get(firstLetter);
				if (list == null) {
					beginIndex += 1;
					nextWordIndex = beginIndex;
					continue;
				}
				for (int subLen = 1; subLen <= 6; subLen++) {
					if (beginIndex + subLen > curStrLen) {
						break;
					}
					String piece = curStr.substring(beginIndex, beginIndex
							+ subLen);
					if (list.contains(piece)) {
						nextWordIndex = subLen + beginIndex;
					}
				}
				// 若不存在任何匹配，begin和next都向后移一位
				if (nextWordIndex == beginIndex) {
					beginIndex += 1;
					nextWordIndex = beginIndex;
					continue;
				}
				String subStr = curStr.substring(beginIndex, nextWordIndex);
				result.append(subStr + " ");
				beginIndex = nextWordIndex;
			}
		}
		if (result.length() == 0) {
			result.append(temp);
		}
		return result.toString();
	}

	/**
	 * 返回拼音全称和拼音缩写的List<token>，其中拼音间不存在间隔
	 * 
	 * @param inputChar
	 * @return List<token>: pinyin and py
	 */
	@Override
	public List<Token> process(char[] inputChar) {
		List<Token> list = _process(inputChar);
		Token pinyinAll = new Token();
//		Token pinyinShort = new Token();
		Token pinyinFilterG = new Token();// 去掉ang,ing,ong中的g
		StringBuffer pinyin = new StringBuffer();
		StringBuffer pinyinNG = new StringBuffer();
//		StringBuffer py = new StringBuffer();
		for (Token tt : list) {
			pinyin.append(tt.value);
			int indexG = tt.value.indexOf("g");
			if (indexG > 0) {
				pinyinNG.append(tt.value.substring(0, tt.value.length() - 1));
			} else {
				pinyinNG.append(tt.value);
			}
//			py.append(tt.value.charAt(0));
		}
		pinyinAll.value = pinyin.toString();
		pinyinAll.type = Analyzer.ALPHA;
//		pinyinShort.value = py.toString();
//		pinyinShort.type = Analyzer.ALPHA;

		List<Token> result = new ArrayList<Token>();
		if (!pinyinNG.toString().equals(pinyin.toString())) {
			pinyinFilterG.value = pinyinNG.toString();
			pinyinFilterG.type = Analyzer.ALPHA;
			result.add(pinyinFilterG);
		}
		result.add(pinyinAll);
//		result.add(pinyinShort);

		return result;
	}

	private List<Token> str2token(String string) {
		List<Token> list = new ArrayList<Token>();
		String[] array = string.split(" ");
		int i = 0;
		while (i < array.length) {
			Token token = new Token();
			token.type = Analyzer.ALPHA;
			token.value = array[i];
			list.add(token);
			i++;
		}
		return list;
	}

}
