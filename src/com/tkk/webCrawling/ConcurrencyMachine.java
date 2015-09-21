package com.tkk.webCrawling;

public class ConcurrencyMachine {
	
	//TODO: it should be a singleton
	
	static final int maxWorkerNumber = 5;
	
	//TODO: there is a queue of workers, input source object
	//TODO: this object should subscribe to worker's status
	/*
	 * Create a new worker
	 */
	void CreateWorker () {
		//if 
	}
	
	/*
	 * keep a dummy thread to keep track of other workers' state
	 */
	void Loop () {
		//if input source object queue is not zero, there should be thread not sleep
		//this thread priority may be low and sleep for a short moment
	}
}
