package com.tkk.webCrawling;

import com.tkk.webCrawling.webCrawler.*;

import java.util.List;
import java.util.concurrent.*;

public class WebCrawlingMain {

	public static void main(String[] args) {

		// TODO: DB flushing
		Crawlee_DB DB = Crawlee_DB.GetInstance();
		ConcurrencyMachine workers = ConcurrencyMachine.GetInstance();

		// TODO: declared all the websites
		BaseCrawler ecTutorWorker = ECTutorCrawler.GetInstance();

		// TODO: WAIT, until constructors finish and have websites get their
		// board indexes
		// ConcurrencyMachine.getInstance().publicMethodMassCurl()
		// ecTutorWorker.StartRun(); //this StartRun() does call the thread to
		// start to run

		List<Crawlee> eCrawlees = ecTutorWorker.getCrawlees();

		synchronized (eCrawlees) {
			try {
				ecTutorWorker.StartRun();
				eCrawlees.wait();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//System.out.println("[ECTutor crawlees] size: " + ecTutorWorker.getCrawlees().size());
		ConcurrencyMachine.GetInstance().RegisterQueue(eCrawlees);
		ConcurrencyMachine.GetInstance().InvokeQueue();

		// TODO: WAIT, until crawled and crawlee mature, write to same DB file
		// need to lock the log file

		// TODO: WAIT, until writing DB file, write result file (this is
		// postprocessing)
		
		System.out.println("Program main runned to LAST line!");

	}

	class WaitLooper implements Runnable {

		public void run() {

		}
	}
}
