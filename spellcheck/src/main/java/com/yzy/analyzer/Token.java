package com.yzy.analyzer;

public class Token {
	/**
	 * 值
	 */
	public String value = "";
	/**
	 * 类型
	 */
	public int type = 0;
	/**
	 * 起始位置
	 */
	public int startPos = 0;
	/**
	 * 结束位置
	 */
	public int endPos = 0;
 
	

	@Override
	public String toString() {
		return "Token [endPos=" + endPos + ", startPos=" + startPos + ", type="
				+ type + ", value=" + value + "]";
	}
	
	

}
