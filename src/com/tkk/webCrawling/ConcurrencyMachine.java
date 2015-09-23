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
import com.tkk.webCrawling.webCrawler.*;

public class ConcurrencyMachine {

	//TODO: this value is got by testing, different machines should redo.
	static final int maxWorkerNumber = 15;
	private static ConcurrencyMachine instance = null;
	
	public static ConcurrencyMachine GetInstance() {

		if (instance == null) {
			instance = new ConcurrencyMachine();
		}

		return instance;
	}

	// TODO: there is a queue of workers, input source object
	// TODO: this object should subscribe to worker's status
	// TODO: runtime should be recorded just about to mass crawl, refer to ECTutor

	// For experiment
	static String mainUrl = "http://www.ectutor.com/popup_case.php?id=";
	static String[] caseIndexes = { "155081" };
	// For experiment: end
	
	ExecutorService executorService;
	
	public ConcurrencyMachine () {
		executorService = Executors.newFixedThreadPool(maxWorkerNumber);
	}

	public void Main(String[] args) throws IOException {

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
			handles = executorService.invokeAll(requests);
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
	void CreateWorkers() {
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

	public Document call() {
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
