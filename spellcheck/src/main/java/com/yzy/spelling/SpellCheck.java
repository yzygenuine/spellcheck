package com.yzy.spelling;

import java.util.List;

/**
 * @author yzy
 *
 */
public interface SpellCheck {

	public String[] suggestSimilar(String word, int numSug);

	public List<String> suggestSimilarList(String word, int numSug);
	
	
	public void setSd(StringDistance sd);
	
	
//	public void buildIndex();
	
}
