package com.tkk.webCrawling;

import com.tkk.webCrawling.webCrawler.*;

public class WebCrawlingMain {

	public static void main(String[] args) {
		
		ECTutorCrawler ecTutorWorker = ECTutorCrawler.GetInstance();
		ecTutorWorker.StartRun();
		System.out.println("Program main runned to LAST line!");

	}
}
