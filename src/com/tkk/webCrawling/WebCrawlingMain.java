package com.tkk.webCrawling;

import com.tkk.webCrawling.webCrawler.*;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class WebCrawlingMain {

	public static void main(String[] args) {

		//DB flushing
		Crawlee_DB.GetInstance();

		// declared all the websites
		BaseCrawler ecTutorWorker = ECTutorCrawler.GetInstance();

		// WAIT, until constructors finish and have websites get their
		// board indexes
		List<Crawlee> eCrawlees = ecTutorWorker.getCrawlees();

		synchronized (eCrawlees) {
			try {
				ecTutorWorker.StartRun();
				eCrawlees.wait();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("[ECTutor crawlees] size 2: " + ecTutorWorker.getCrawlees().size());
		ConcurrencyMachine.GetInstance().RegisterQueue(eCrawlees);
		ConcurrencyMachine.GetInstance().InvokeQueue();

		//WAIT, until crawled and crawlee mature, write to same DB file
		// need to lock the log file
		Date runTime = new Date();

		// @Problem: this is interesting, crawlees is protected inside Crawler, but I can
		//modifiy it outside here
		for (Iterator<Crawlee> crwl_iter = eCrawlees.iterator(); crwl_iter.hasNext();) {
			Crawlee crwl = crwl_iter.next();
			try {
				if (Crawlee_DB.GetInstance().LookUpFromDB(crwl, runTime)) {
					crwl_iter.remove();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("[ECTutor crawlees] size 3: " + ecTutorWorker.getCrawlees().size());

		ecTutorWorker.FilterByCritAction();

		System.out.println("[ECTutor crawlees] size 4: " + ecTutorWorker.getCrawlees().size());

		// WAIT, until writing DB file, write result file (this is
		// postprocessing)
		ecTutorWorker.PostProcessAction();

		System.out.println("Program main runned to LAST line!");

	}

}
