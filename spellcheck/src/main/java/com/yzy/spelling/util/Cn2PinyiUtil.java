package com.yzy.spelling.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import com.yzy.spelling.index.Document;

/**
 * 把词典的中文转化为拼音并保存
 */
public class Cn2PinyiUtil {
	private String cnDictPath = "./name.txt";
	private String pyDictPath = "./pinyi.txt";
	
	/** 把字典的汉字转换成拼音 */
	public void cn2pinyi(){
		try{
		File cnFile = new File(cnDictPath);
		File pyFile = new File(pyDictPath);
		if(!cnFile.exists()){
			System.out.println("cnfile is not exist");
			return;
		}
		if(!pyFile.exists()){
			pyFile.createNewFile();
		}
		
		HanyuPinyinOutputFormat outputFormat = getPinyinOutputFormat();
		
		FileWriter fileWriter = new FileWriter(pyFile);
		int lineCount = 0;
		
		Set<Document> cnDocSet = DictOper.readDocFromFile(cnFile);
		Iterator<Document> it = cnDocSet.iterator();
		while(it.hasNext()){
			Document doc = (Document)it.next();
			String pinyinStr = PinyinHelper.toHanyuPinyinString(doc.getText(), outputFormat, " ");
			int pinyinFeq = doc.getFreq();
			fileWriter.write(pinyinStr + ":" + pinyinFeq + "\n");
			lineCount++;
			if(lineCount % 1000 == 0){
				fileWriter.flush();
			}
		}
		fileWriter.close();
		System.out.println(cnDictPath + " 成功转换成拼音字典：" + pyDictPath);
		}catch(Exception e){
			System.out.println(cnDictPath + " 转换成拼音字典：" + pyDictPath + "异常");
			e.printStackTrace();
		}
	}
	
	private HanyuPinyinOutputFormat getPinyinOutputFormat(){
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		return outputFormat;
	}

	public void setCnDictPath(String cnDictPath) {
		this.cnDictPath = cnDictPath;
	}

	public void setPyDictPath(String pyDictPath) {
		this.pyDictPath = pyDictPath;
	}
	
	public static void main(String[] args) throws IOException {
		Cn2PinyiUtil u = new Cn2PinyiUtil();
		u.cn2pinyi();
	}
	/*	
	private void test(){
		File f = new File(cnDictPath);
		if(f.exists()){
			System.out.println("ok");
		}
	}*/
}
