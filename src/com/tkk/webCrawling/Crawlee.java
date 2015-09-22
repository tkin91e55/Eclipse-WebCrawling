package com.tkk.webCrawling;

import java.lang.String;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.tkk.webCrawling.webCrawler.*;

public class Crawlee implements Callable<Document> {

	enum State {
		SUCCESS, FAILURE, QUEUE, TIME_OUT
	}

	int case_index;
	public int getCase_index() {
		return case_index;
	}
	
	String url;
	public String getUrl() {
		return url;
	}

	public BaseCrawler getCrawlerBelonged() {
		return crawlerBelonged;
	}
	BaseCrawler crawlerBelonged;
	
	public HashMap<String, String> map = new HashMap<String, String>();
	public State state;

	/*
	 * Current keys of HashMap are:
	 * Location,LastUpdateAt,Time,Gender,Info,Subject,Fee,Other
	 */

	public Crawlee(int idx, String aUrl, BaseCrawler crawler) {
		state = State.SUCCESS;
		case_index = idx;
		url = aUrl;
		crawlerBelonged = crawler;
		
	}
	
	public Crawlee(int idx){
		case_index = idx;
	}

	public void Put(String Key, String Value) {
		map.put(Key, Value);
	}

	public String Context() {
		String content = "";
		Collection<String> strings = map.values();
		for (String str : strings) {
			content = content + str + System.getProperty("line.separator");
		}
		// System.out.println("[Crawlee] content: " + content);
		return content;
	}

	public int GetFee() {
		if (map.containsKey("Fee")) {
			// System.out.println("[SearchCrit] fee: " + map.get("Fee"));
			Pattern price = Pattern.compile("\\$[0-9]{2,4}");
			Matcher matcher = price.matcher(map.get("Fee"));
			if (matcher.find()) {
				String casePriceStr = matcher.group(0).substring(1);
				int casePrice = 55699;
				casePrice = Integer.parseInt(casePriceStr);
				if (casePrice != 55699)
					return casePrice;
			}
		}
		return 689831;
	}

	public String GetValueByKey(String key) {

		if (map.containsKey(key)) {
			return map.get(key);
		}

		return "";
	}
	
	public Document call(){
		return null;
	}

}
