package controller;

import java.util.ArrayList;
						   //Storage	
public abstract class ErrorManager {  //abstract
	private static ArrayList<String>errorList = new ArrayList<String>();
	
	//constructor initating arrayList
	
	//Sync this list
	public void  reportError(String error) {
		errorList.add(error);
		//this.notify
	}
	
	public ArrayList<String> getErrorList(){
		return errorList;
	}
	
}
