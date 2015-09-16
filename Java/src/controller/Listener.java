package controller;

/**
 * @author c12ton
 * @version 2015.09.06
 */
public class Listener {  //abstract class??
	
	public Listener() {
		
		ErrorListener errListener = new ErrorListener();
		errListener.Startwatch();
		
	}
	
	
	/**
	 * 
	 */
	public class ErrorListener extends ErrorManager {
		
		//implements runnable 
		 //startWatch();
		
		private void Startwatch() {
			//while(true) {
				for(String msg:getErrorList()) {
					System.out.println("msg:" +msg);
			//	}
			}
			
			//gui.printError(message)
		}
		
		
		//thead in sleep till notify singla is sent
	}
	 
}
