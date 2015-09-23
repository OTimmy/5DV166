package controller;


public abstract class ErrorHandler {
	private static Listener<String> listener;

	public void  reportError(String error) {
		listener.update(error);
	}

	public void addListener(Listener listener) {
	    this.listener = listener;
	}

}
