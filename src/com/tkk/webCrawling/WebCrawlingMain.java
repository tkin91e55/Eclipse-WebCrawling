package com.tkk.webCrawling;

import com.tkk.webCrawling.webCrawler.ECTutorCrawler;

public class WebCrawlingMain {

	public static void main(String[] args) {
		
		ECTutorCrawler ecTutorWorker = ECTutorCrawler.GetInstance();
		ecTutorWorker.Start();
		System.out.println("Program main runned to LAST line!");

	}
}
