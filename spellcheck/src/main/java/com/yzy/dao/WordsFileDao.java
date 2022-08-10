package com.yzy.dao;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.yzy.spelling.index.Document;
@Service("wordsFileDao")
public class WordsFileDao implements WordsDao{

	public static String wordsFile = "./name.txt";

	public static void main(String[] args) {
		WordsFileDao dao = new WordsFileDao();

		List<Document> list = dao.readAllData();
		System.out.println(list.size());
	}

	public List<Document> readAllData() {
		
		
		List<Document> list = new ArrayList<Document>();
		URL in = WordsFileDao.class.getClassLoader().getResource(wordsFile);
		try {
			File f = new File(in.toURI());

			System.out.println(f.getAbsolutePath());
			if (!f.exists()) {
				return list;
			}
			List<String> lines = FileUtils.readLines(f);
			for (int i = 0; i < lines.size(); i++) {
				Document doc = new Document();
				doc.setId(i);
				doc.setText(lines.get(i));
				doc.setFreq(1);
				doc.setDay((short) 1);
				list.add(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
