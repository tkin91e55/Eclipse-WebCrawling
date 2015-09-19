
public abstract class BaseCrawler {
	
	public enum CrawlingStates {
		STATE_PROCESS_URL,
		STATE_SEARCH_CRIT_FILTER
	}
	
	BaseCrawler.CrawlingStates mState;
	
	void ProcessUrls () {
		
	}
	
	void FilterByCrit () {
		
	}

}
