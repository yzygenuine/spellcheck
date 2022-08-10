package com.yzy.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFilter {
	private static  String day="(\\d{4}[年]?)(\\-|\\.|\\/)?(\\d{1,2}[月]?)(\\-|\\.|\\/)?(\\d{1,2}[日]?)";
//	private static String SEP = "[\\|\\-\\/\\/\\s]?";
	/*private static final String REGEX_DATE = "^((\\d{2}(([02468][048])|([13579][26]))[年]?"
		+ SEP
		+ "((((1[02])|(0?[13578]))[月]?"
		+ SEP
		+ "(([1-2][0-9])|(3[01])|(0?[1-9])))"
		+ "|(((0?[469])|(11))[月]?"
		+ SEP
		+ "(([1-2][0-9])|(30)|(0?[1-9])))"
		+ "|(0?2[月]?"
		+ SEP
		+ "(([1-2][0-9])|(0?[1-9])))))"
		+ "|(\\d{2}(([02468][12"
		+ "35679])|([13579][01345789]))[年]?"
		+ SEP
		+ "((((1[02])|(0?[13578]))[月]?"
		+ SEP
		+ "(([1-2][0-9])|(3[01])|(0?[1-9])))"
		+ "|(((0?[469])|(11))[月]?"
		+ SEP
		+ "(([1-2][0-9])|(30)|(0?[1-9])))"
		+ "|(0?2[月]?"
		+ SEP
		+ "((1[0-9])|(2[0-8])|(0?[1-9]))))))"
		+ "[日]?";*/
	/**过滤字符串
	 * @param inputStr
	 * @return
	 */
	public static String transform(String inputStr) {
		Pattern p = Pattern.compile(day);
		String s = inputStr;
		System.out.println(s + " " + p.matcher(s).matches());
		Matcher ma=p.matcher(s);
		
		if(ma.find()){
			System.out.println(ma.group());
		}
		return inputStr.replaceAll(day, "");
	}

	public static void main(String[] args) {
//		 "20060201我独往独来", 
		String ss[] = { "2006-11-01我独往独来","2005年12月23日6" ,"20030411" ,"2004-12-31","1111"};
		for (String s : ss) {
			System.out.println(s + " -> " + transform(s));
		}
	}

}
