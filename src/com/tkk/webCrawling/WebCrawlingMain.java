package com.tkk.webCrawling;

import com.tkk.webCrawling.webCrawler.*;
import java.util.concurrent.*;

public class WebCrawlingMain {

	public static void main(String[] args) {
		
		//TODO: DB flushing
		Crawlee_DB DB = Crawlee_DB.GetInstance();
		
		//TODO: declared all the websites
		ECTutorCrawler ecTutorWorker = ECTutorCrawler.GetInstance();
		
		//TODO: WAIT, until constructors finish and have websites get their board indexes
		//ConcurrencyMachine.getInstance().publicMethodMassCurl()
		ecTutorWorker.StartRun(); //this StartRun() does call the thread to start to run
		
		//TODO: WAIT, until crawled and crawlee mature, write to same DB file
		//need to lock the log file
		
		//TODO: WAIT, until writing DB file, write result file (this is postprocessing)
		System.out.println("Program main runned to LAST line!");

	}
}
