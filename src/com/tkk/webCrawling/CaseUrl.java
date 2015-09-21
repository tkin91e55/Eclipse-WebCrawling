package com.tkk.webCrawling;

import java.util.Collection;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.tkk.webCrawling.utils.Stopwatch;

public class CaseUrl {

	// For experiment
	static String mainUrl = "http://www.ectutor.com/popup_case.php?id=";
	static String[] caseIndexes = { "155081", "156238", "156287", "156295", "156299", "156300", "156306", "156309",
			"156323", "156324", "156332", "157788", "158080", "158111", "158129", "158155", "158276", "158282",
			"158297", "158315", "158317", "158321", "158324", "158330", "158343", "158347", "158352", "158355",
			"158365", "158378", "158380", "158382", "158383", "158384", "158386", "158387", "158388", "158389",
			"158390", "158393", "158394", "158395", "158396", "158397", "158399", "158400", "158401", "158402",
			"158403", "158405", "158406", "158407", "158408", "158409", "158410", "158413", "158415", "158416",
			"158417", "158418" };
	// For experiment: end

	public static void main(String[] args) {

		Stopwatch timer = new Stopwatch();

		// Do searches on remote website contents
		for (String index : caseIndexes) {
			// System.out.println("[On-board] idx : " + str);

			String URL = mainUrl + index;

			System.out.println("[ProcessUrl] url connected: " + URL);

			try {
				Document caseDoc = Jsoup.connect(URL).data("query", "Java").userAgent("Mozilla").cookie("auth", "token")
						.timeout(6000).post();
				if (!caseDoc.text().contains("Server Error")) {
					String title = caseDoc.title();
					System.out.println("[Doc] Title: " + title);
					String result = caseDoc.text();
					System.out.println("[Doc] Result: " + result);

				}
			} catch (IOException e) {
				System.err.println(e);
			}

		}

		System.out.println("[Timer] elapsed time: " + timer.GetElapsedTime());
	}

	// TODO: may take reference from Java API of WWW request, jsoup API

	enum Response {
		SUCCESS, FAILURE
	}

}
