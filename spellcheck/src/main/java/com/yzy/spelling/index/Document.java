package com.yzy.spelling.index;

/**
 * @author yzy
 * 
 */
public class Document {
	/**
	 * 唯一的标识
	 */
	private int id;

	/**
	 * 文档内容
	 */
	private String text;
	/**
	 * 搜索次数
	 */
	private int freq;

	/**
	 * 出现时间：自然数来表示
	 */
	private short day = 1;

	public Document() {
		super();
	}

	public Document(int id, String text, int freq) {
		super();
		this.id = id;
		this.text = text;
		this.freq = freq;
	}

	public Document(String text, int freq) {
		super();
		this.text = text;
		this.freq = freq;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getFreq() {
		return freq;
	}

	public short getDay() {
		return day;
	}

	public void setDay(short day) {
		this.day = day;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}
	

	@Override
	public String toString() {
		return "Document [day=" + day + ", freq=" + freq + ", id=" + id
				+ ", text=" + text + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + freq;
		result = prime * result + id;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Document other = (Document) obj;
		if (id == other.id)
			return true;
		return false;
	}

}
