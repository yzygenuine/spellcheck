package com.yzy.analyzer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

/**
 * @author yzygenuine
 * 
 * 输入: 中国  
 * 输出：对应全拼与拼音的缩写
 * zhongguo
 * zg
 *
 */
public class SimpePinyinAnalyzer extends Analyzer {
	private static HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
	static {
		outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	}

	/**
	 * 得到拼音串的缩写,取每个拼音的第一个字母
	 * 
	 * @param py
	 * @return
	 */
	private String getPinyinShort(String py) {
		StringBuffer sb = new StringBuffer();
		String[] pyArray = py.split(" ");
		int i = 0;
		while (i < pyArray.length) {
			sb.append(pyArray[i].substring(0, 1));
			i++;
		}
		return sb.toString();
	}

	/**
	 */
	@Override
	public List<Token> process(char[] inputChar) {
		List<Token> list = new ArrayList<Token>();
		String text = new String(inputChar);
		try {
			String py = PinyinHelper.toHanyuPinyinString(text, outputFormat, " ");

			String pyShort = getPinyinShort(py);
//		py = py.replaceAll("\\s", "");
			Token pyToken = new Token();
			pyToken.value = py.replaceAll("\\s", "");
			pyToken.type = Analyzer.ALPHA;
			list.add(pyToken);
			Token pyShortToken = new Token();
			pyShortToken.value = pyShort;
			pyShortToken.type = Analyzer.ALPHA;
			list.add(pyShortToken);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static void main(String[] args) {
		SimpePinyinAnalyzer s = new SimpePinyinAnalyzer();
		List<Token> py = s.process("中国".toCharArray());
		for (Token token : py) {
			System.out.println(token.value);

		}
	}

}
