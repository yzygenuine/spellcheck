package com.yzy;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yzy.dao.WordsDao;
import com.yzy.dao.WordsDbDao;
import com.yzy.dao.WordsFileDao;
import com.yzy.spelling.CnSpellcheck;
import com.yzy.spelling.NGramDistance;
import com.yzy.spelling.PinyinSpellCheck;
import com.yzy.spelling.SpellCheck;
import com.yzy.spelling.index.Document;
import com.yzy.spelling.index.Index;

/**
 * @author yzy
 * 
 */
@SpringBootApplication
@RestController
public class SpellcheckApp {

	private WordsDao dao;
	private final Logger logger = LoggerFactory.getLogger(SpellcheckApp.class);
	private final SpellCheck pinyinSpellcheck = new PinyinSpellCheck();
	private final SpellCheck cnSpellcheck=new CnSpellcheck();

	public SpellcheckApp() {
		dao=new WordsFileDao();
		reload();
		pinyinSpellcheck.setSd(new NGramDistance(2));
		cnSpellcheck.setSd(new NGramDistance(1));
	}
	

	public static void main(String[] args) {
		SpringApplication.run(SpellcheckApp.class, args);
	}

	@GetMapping("/words")
	public String status(@RequestParam(value = "oper", defaultValue = "reload") String oper) {
		JSONObject resultJSON = new JSONObject();
		if ("reload".equals(oper)) {// 重新加载
			reload();
			resultJSON.put("status", "加载完成");
		} 
		return resultJSON.toString();
	}

	@GetMapping("/spellcheck")
	/**
	 * input 输入query串 sugNum 搜索建议个数
	 */
	public String spellcheck(@RequestParam(value = "input", defaultValue = "") String input,@RequestParam(value = "type", defaultValue = "pinyin") String type,
			@RequestParam(value = "sugNum", defaultValue = "5") int sugNum) {
		logger.debug("input:"+input+",type:"+type+",sugNum:"+sugNum);
		SpellCheck spell=pinyinSpellcheck;
		if("cn".equals(type)) {
			System.out.println("中文拼写检查");
			spell=cnSpellcheck;
		}
		return spellcheckHandle(input, sugNum,spell);
	}

	public void setDao(WordsDbDao dao) {
		this.dao = dao;
	}
	

	// 拼写检查处理
	private String spellcheckHandle(String input, int sugNum,SpellCheck spell) {
		
		long start = new Date().getTime();
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		if (input == null || input.isEmpty()) {
			json.put("sugArray", array);
			json.put("status", "输入为空");
			return json.toString();
		}
		List<String> sugword = spell.suggestSimilarList(input, sugNum);
		long end = new Date().getTime();
		long total = end - start;
		for (String sug : sugword) {
			array.put(sug);
		}
		json.put("sugArray", array);
		json.put("totalTime", total);
		json.put("status", "ok");
		return json.toString();
	}

	/**
	 * 重新加载所有词库
	 */
	public void reload() {
		logger.info("重新加载所有词库");
		List<Document> list = dao.readAllData();
		logger.info("全部词汇量：" + list.size());
		Index index = Index.getInstance();
		index.buildIndex(list);
//		index.buildPinYinIndex(list);
		list.clear();
		list = null;
		System.gc();
	}
	
}
