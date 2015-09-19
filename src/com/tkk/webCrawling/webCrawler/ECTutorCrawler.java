package com.tkk.webCrawling.webCrawler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.MultiMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.tkk.webCrawling.Crawlee;
import com.tkk.webCrawling.Crawlee_DB;
import com.tkk.webCrawling.FileManager;

public class ECTutorCrawler extends BaseCrawler {

	String URL_KEY = "WC_URL";
	String URL_INDEX_KEY = "WC_INDEX_URL";
	CrawlerKeyBinding mID = CrawlerKeyBinding.ECTutor;
	
	//TODO: make this subclass as singleton 
	private static ECTutorCrawler instance = null;
	
	protected ECTutorCrawler () {
		//exists only to defeat instantiation
		super();
	}
	
	public static ECTutorCrawler GetInstance() {
		
		if (instance == null){
			instance = new ECTutorCrawler();
		}
		
		return instance;
	}
	/*
	 * TODO: Split this function, too clumsy
	 */
	protected void ProcessUrlsAction() throws IOException, ParseException {

		System.out.println("ECTutor ProcessUrlsAction() Called");
		super.ProcessUrlsAction();

		Date runTime = new Date();

		@SuppressWarnings({ "unchecked" })
		Collection<String> idx_urls = (Collection<String>) config.get(URL_INDEX_KEY);

		// load inx board page to get on-board indices
		for (String idx_url : idx_urls) {
			System.out.println("The idx url: " + idx_url);

			Document idxDoc = Jsoup.connect(idx_url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token")
					.timeout(6000).post();

			List<String> onboard_indices = new ArrayList<String>();
			Pattern atrbt = Pattern.compile("bk_case_[0-9]{6}");
			Matcher idxMatcher = atrbt.matcher(idxDoc.body().toString());

			while (idxMatcher.find()) {
				String str = idxMatcher.group();
				str = str.substring(str.lastIndexOf('_') + 1);
				onboard_indices.add(str);
			}

			Collections.sort(onboard_indices);

			Crawlee_DB DBagent = new Crawlee_DB();

			System.out.println("[DB] DBagent size: " + DBagent.Size());

			// Do searches on remote website contents
			for (String index : onboard_indices) {
				// System.out.println("[On-board] idx : " + str);
				@SuppressWarnings({ "unchecked" })
				Collection<String> urls = (Collection<String>) config.get(URL_KEY);
				for (String url : urls) {
					String URL = url + index;

					System.out.println("[ProcessUrl] url connected: " + URL);

					Document caseDoc = Jsoup.connect(URL).data("query", "Java").userAgent("Mozilla")
							.cookie("auth", "token").timeout(6000).post();
					if (!caseDoc.text().contains("Server Error")) {
						// String title = caseDoc.title();
						// System.out.println("[Doc] Title: " + title);
						// String result = caseDoc.text();
						// System.out.println("[Doc] Result: " + result);
						Crawlee crawlee = AnalyzeContentAction(caseDoc, Integer.parseInt(index)); // crawlees got filled

						// Add qualified curled case to csv,
						// Crawlee_DB.WriteToDBFile()
						if (!DBagent.LookUpFromDB(crawlee, runTime)) {
							crawlees.add(crawlee);
						}
					}
				}
			}

		}
	}

	/*
	 * TODO: Split this function, too clumsy
	 */
	Crawlee AnalyzeContentAction(Document doc, int indx) throws IOException {

		HashMap<String, String> searchNodes = new HashMap<String, String>();
		searchNodes.put("Location", "span[class$=title]");
		searchNodes.put("LastUpdateAt", "span[class$=loginTime]");
		// searchNodes.put("Detail","div[class$=detail]:eq(1) > p:eq(2)");
		searchNodes.put("Details", "div[class$=detail] > div[class$=item]");
		// String JsoupSearchNode_CONTENT = "div[class$=detail]:eq(1)";

		Elements location = doc.select(searchNodes.get("Location"));
		Elements lastUpdate = doc.select(searchNodes.get("LastUpdateAt"));
		Elements eles = doc.select(searchNodes.get("Details"));

		System.out.println("[ECTutor] part");
		System.out.println("[Jsoup] location: " + location.text() + " and lastUpdate: " + lastUpdate.text());

		// for (int i = 0; i < eles.size(); i++){
		// Element ele = eles.get(i);
		// System.out.println("[Jsoup] ele text: " + ele.text());
		// }

		Crawlee crawlee = new Crawlee(indx);
		// location
		crawlee.Put("Location", "Location: " + location.text());
		// LastupdateAt
		crawlee.Put("LastUpdateAt", "Last Update: " + lastUpdate.text());
		// Time
		crawlee.Put("Time", eles.get(0).text());
		// Gender
		crawlee.Put("Gender", eles.get(1).text());
		// Info
		crawlee.Put("Info", eles.get(2).text());
		// Subject
		crawlee.Put("Subject", eles.get(3).text());
		// Fee
		crawlee.Put("Fee", eles.get(4).text());
		// Other
		crawlee.Put("Other", eles.get(5).text());

		System.out.println(
				"[Crawlee] crawlees size: " + crawlees.size() + " and the cralwee content: \n" + crawlee.Context());
		System.out.println("[ECTutor] part end");
		return crawlee;
	}

	 protected void FilterByCritAction() {

		for (Iterator<Crawlee> crawlee_ite = crawlees.iterator(); crawlee_ite.hasNext();) {
			Crawlee crawlee = crawlee_ite.next();
			Boolean beDeleted = true;

			if (FilterInBySubject(crawlee, config)) {
				if (!FilterByFee(crawlee, config)) {
					if (FilterOutByLocation(crawlee, config)) {
						beDeleted = false;
					}
				}
			}

			if (beDeleted) {
				System.out.println("[SearchCrit] Going to delete crawlee: " + crawlee.case_index);
				crawlee_ite.remove();
			}
		}
	}

	Boolean FilterByFee(Crawlee crawlee, MultiMap<String, String> config) {
		int price_above = -1;
		@SuppressWarnings({ "unchecked" })
		Collection<String> price_str = (Collection<String>) config.get(CRIT_PRICE_KEY);
		price_above = Integer.parseInt((String) price_str.toArray()[0]);
		if (price_above != -1) {
			if (crawlee.GetFee() > price_above)
				return false;
		}
		return true;
	}

	Boolean FilterOutByLocation(Crawlee crawlee, MultiMap<String, String> config) {

		@SuppressWarnings({ "unchecked" })
		Collection<String> location_Strs = (Collection<String>) config.get(CRIT_LOCATION_KEY);

		for (String aCrit : location_Strs) {
			Pattern crit = Pattern.compile(aCrit);
			Matcher matcher = crit.matcher(crawlee.GetValueByKey("Location"));
			if (matcher.find())
				return false;
		}
		return true;
	}

	Boolean FilterInBySubject(Crawlee crawlee, MultiMap<String, String> config) {

		@SuppressWarnings({ "unchecked" })
		Collection<String> subject_Strs = (Collection<String>) config.get(CRIT_SUBJECT_KEY);

		for (String aCrit : subject_Strs) {
			Pattern crit = Pattern.compile(aCrit);
			Matcher matcher = crit.matcher(crawlee.GetValueByKey("Subject"));
			if (matcher.find())
				return true;
		}
		return false;
	}
	
	protected void PostProcessAction () {
		//Result:
		for (Crawlee cr: crawlees){
			System.out.println("[SearchCrit] Remaining crawlee: " + cr.case_index);
		}
		try{
		ParseInResult();
		}
		catch (IOException ex){
			System.err.println(ex);
		}
	}
	
	void ParseInResult () throws IOException {

		//Parsing
		FileManager filewriter = new FileManager("result.csv");
		filewriter.AppendOnNewLine(new SimpleDateFormat().format(new Date()) + " 's update:",false); 
		for (Crawlee cr: crawlees){
			filewriter.AppendOnNewLine("The case index: " + cr.case_index);
			filewriter.AppendOnNewLine(cr.Context());
		}
		filewriter.Close();
	}

}