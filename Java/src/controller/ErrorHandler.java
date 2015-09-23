package controller;


/**
 * Works as an observer for multiple subjects, regarding error message. 
 */
public abstract class ErrorHandler {
	private static Listener<String> listener;

	public Listener getListener() {
		return listener;
	}
	
	public void addListener(Listener<String> listener) {
	    this.listener = listener;
	}

}
