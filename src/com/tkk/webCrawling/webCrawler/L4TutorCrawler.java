package com.tkk.webCrawling.webCrawler;

import java.io.IOException;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.tkk.webCrawling.Crawlee;
import com.tkk.webCrawling.FileManager;

public class L4TutorCrawler extends BaseCrawler {

	String URL_KEY = "WC_URL";
	String URL_INDEX_KEY = "WC_INDEX_URL";
	static final CrawlerKeyBinding mID = CrawlerKeyBinding.L4Tutor;
	static final String threadName = "L4Tutor-thread";

	private static L4TutorCrawler instance = null;

	protected L4TutorCrawler() {
		// exists only to defeat instantiation
		super(mID,threadName);

	}

	public static L4TutorCrawler GetInstance() {

		if (instance == null) {
			instance = new L4TutorCrawler();
		}

		return instance;
	}

	// Start to run function, shall not be called from external
	public void run() {
		try {
			ProcessUrlsAction();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/*
	 * TODO: Split this function, too clumsy
	 */
	protected void ProcessUrlsAction() {

		// super.ProcessUrlsAction();

		List<String> onboard_indices = new ArrayList<String>();

		@SuppressWarnings({ "unchecked" })
		Collection<String> idx_urls = (Collection<String>) config.get(URL_INDEX_KEY);

		// suppose should only one matching URL_KEY for those on-board indices
		@SuppressWarnings({ "unchecked" })
		String url = ((List<String>) config.get(URL_KEY)).get(0);

		// load inx board page to get on-board indices
		for (String idx_url : idx_urls) {
			System.out.println("The idx url: " + idx_url);

			try {
				Document idxDoc = Jsoup.connect(idx_url).data("query", "Java").userAgent("Mozilla")
						.cookie("auth", "token").timeout(6000).post();

				Pattern atrbt = Pattern.compile("bk_case_[0-9]{6}");
				Matcher idxMatcher = atrbt.matcher(idxDoc.body().toString());

				while (idxMatcher.find()) {
					String str = idxMatcher.group();
					str = str.substring(str.lastIndexOf('_') + 1);
					onboard_indices.add(str);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		synchronized (crawlees) {
			Collections.sort(onboard_indices);

			for (String index : onboard_indices) {
				int idx = Integer.parseInt(index);
				crawlees.add(new Crawlee(idx, url + index, this));
			}

			System.out.println("[L4Tutor crawlees] size: " + this.getCrawlees().size());
			crawlees.notify();
		}
	}

	public void AnalyzeContentAction(Crawlee crawlee) {
		Document doc = crawlee.getJdoc();
		HashMap<String, String> searchNodes = new HashMap<String, String>();
		searchNodes.put("Location", "span[class$=title]");
		searchNodes.put("LastUpdateAt", "span[class$=loginTime]");
		searchNodes.put("Details", "div[class$=detail] > div[class$=item]");

		Elements location = doc.select(searchNodes.get("Location"));
		Elements lastUpdate = doc.select(searchNodes.get("LastUpdateAt"));
		Elements eles = doc.select(searchNodes.get("Details"));

		crawlee.Put("Location", "Location: " + location.text());
		crawlee.Put("LastUpdateAt", "Last Update: " + lastUpdate.text());
		crawlee.Put("Time", eles.get(0).text());
		crawlee.Put("Gender", eles.get(1).text());
		crawlee.Put("Info", eles.get(2).text());
		crawlee.Put("Subject", eles.get(3).text());
		crawlee.Put("Fee", eles.get(4).text());
		crawlee.Put("Other", eles.get(5).text());
	}

	public void FilterByCritAction() {
		super.FilterByCritAction();
		
		for (Iterator<Crawlee> crawlee_ite = crawlees.iterator(); crawlee_ite.hasNext();) {
			Crawlee crawlee = crawlee_ite.next();
			Boolean beDeleted = true;

			if (FilterInBySubject(crawlee.GetValueByKey("Subject"))) {
				if (!FilterByFee(crawlee)) {
					if (FilterOutByLocation(crawlee.Context())) {
						beDeleted = false;
					}
				}
			}

			if (beDeleted) {
				System.out.println("[SearchCrit] Going to delete crawlee: " + crawlee.getCase_index());
				crawlee_ite.remove();
			}
		}
	}

	public void PostProcessAction() {
		super.PostProcessAction();
		// Result:
		for (Crawlee cr : crawlees) {
			System.out.println("[SearchCrit] Remaining crawlee: " + cr.getCase_index());
		}
		try {
			ParseInResult();
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	void ParseInResult() throws IOException {

		// Parsing
		FileManager filewriter = new FileManager("result.csv");
		filewriter.AppendOnNewLine(new SimpleDateFormat().format(new Date()) + " 's update:", false);
		for (Crawlee cr : crawlees) {
			filewriter.AppendOnNewLine("The case index: " + cr.getCase_index());
			filewriter.AppendOnNewLine(cr.Context());
		}
		filewriter.Close();
	}

	@Override
	public String toString() {
		return "L4TutorCrawler";
	}
}
