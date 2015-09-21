package com.tkk.webCrawling;

import java.util.Collection;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.tkk.webCrawling.utils.Stopwatch;

public class CaseUrl {



	// TODO: may take reference from Java API of WWW request, jsoup API

	enum State {
		SUCCESS, FAILURE, QUEUE, TIME_OUT
	}
	
	State rep;

}
