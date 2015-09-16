package controller;

import java.util.ArrayList;

public abstract class ErrorManager {
	private static ArrayList<String>errorList = new ArrayList<String>();


	//Sync this list
	public void  reportError(String error) {
		errorList.add(error);
		//this.notify
	}

	public ArrayList<String> getErrorList(){
		return errorList;
	}

}
