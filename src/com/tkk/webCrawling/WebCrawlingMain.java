package com.tkk.webCrawling;

import com.tkk.webCrawling.webCrawler.*;
import java.util.concurrent.*;

public class WebCrawlingMain {

	public static void main(String[] args) {
		
		ECTutorCrawler ecTutorWorker = ECTutorCrawler.GetInstance();
		ecTutorWorker.StartRun();
		//refer to book, use static intance callback method
		
		System.out.println("Program main runned to LAST line!");

	}
}
