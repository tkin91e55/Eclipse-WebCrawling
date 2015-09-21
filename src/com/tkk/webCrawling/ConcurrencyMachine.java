package com.tkk.webCrawling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tkk.webCrawling.utils.Stopwatch;

public class ConcurrencyMachine {

	// TODO: it should be a singleton

	static final int maxWorkerNumber = 5;

	// TODO: there is a queue of workers, input source object
	// TODO: this object should subscribe to worker's status

	// For experiment
	static String mainUrl = "http://www.ectutor.com/popup_case.php?id=";
	/*
	 * static String[] caseIndexes = { "155081", "156238", "156287", "156295",
	 * "156299", "156300", "156306", "156309", "156323", "156324", "156332",
	 * "157788", "158080", "158111", "158129", "158155", "158276", "158282",
	 * "158297", "158315", "158317", "158321", "158324", "158330", "158343",
	 * "158347", "158352", "158355", "158365", "158378", "158380", "158382",
	 * "158383", "158384", "158386", "158387", "158388", "158389", "158390",
	 * "158393", "158394", "158395", "158396", "158397", "158399", "158400",
	 * "158401", "158402", "158403", "158405", "158406", "158407", "158408",
	 * "158409", "158410", "158413", "158415", "158416", "158417", "158418" };
	 */
	static String[] caseIndexes = { "155081" };
	// For experiment: end

	public static void main(String[] args) throws IOException {

		ExecutorService executorService = Executors.newFixedThreadPool(16);
		List<Future<Document>> handles = new ArrayList<Future<Document>>();
		List<Callable<Document>> requests = new ArrayList<Callable<Document>>();

		/*
		 * Document d =
		 * Jsoup.connect("http://www.betexplorer.com/results/").timeout(0).get()
		 * ; Elements elements = d.select("a"); Iterator<Element> it =
		 * elements.iterator(); Element e; while(it.hasNext()) { e = it.next();
		 * //System.out.println(e.attr("href")); if
		 * (e.attr("href").startsWith("/soccer")) { requests.add(new
		 * Request("http://www.betexplorer.com"+e.attr("href"))); } }
		 */

		for (int i = 0; i < 128; i++) {
			for (String index : caseIndexes) {
				String URL = mainUrl + index;
				requests.add(new Request(URL));
			}
		}

		/*for (Callable<Document> request : requests) {
			handles.add(executorService.submit(request));
		}*/

		Stopwatch timer = new Stopwatch();

		
		try{
		//By this method, all Future runned and then this parent jump to next line
		List<Future<Document>> workingQuests = executorService.invokeAll(requests);
		}catch (Exception e){
			e.printStackTrace();
		}

		System.out.println("[Timer] elapsed time: " + timer.GetElapsedTime());

		executorService.shutdownNow();

		// Do searches on remote website contents
		/*
		 * for (String index : caseIndexes) { // System.out.println(
		 * "[On-board] idx : " + str);
		 * 
		 * String URL = mainUrl + index;
		 * 
		 * //System.out.println("[ProcessUrl] url connected: " + URL);
		 * 
		 * try { Document caseDoc = Jsoup.connect(URL).data("query",
		 * "Java").userAgent("Mozilla").cookie("auth", "token")
		 * .timeout(6000).post(); if (!caseDoc.text().contains("Server Error"))
		 * { String title = caseDoc.title(); System.out.println("[Doc] Title: "
		 * + title); String result = caseDoc.text(); System.out.println(
		 * "[Doc] Result: " + result); } } catch (IOException e) {
		 * System.err.println(e); }
		 * 
		 * }
		 */

	}

	/*
	 * Create a new worker
	 */
	void CreateWorker() {
		// if
	}

	/*
	 * keep a dummy thread to keep track of other workers' state
	 */
	void Loop() {
		// if input source object queue is not zero, there should be thread not
		// sleep
		// this thread priority may be low and sleep for a short moment
	}
}

class Request implements Callable<Document> {

	public final String url;
	Document doc = null;

	public Request(String url) {
		this.url = url;
	}

	public Document call() throws Exception {
		try {
			// doc = Jsoup.connect(url).timeout(600).get();
			doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token").timeout(10000)
					.get();
		} catch (IOException ex) {
			ex.printStackTrace();
			// System.out.println("\n\n\t\t"+url+"\n\n");
		}
		return doc;
	}

	@Override
	public String toString() {
		//return "Request{" + "url=" + url + '}';
		return doc.text();
	}
}
