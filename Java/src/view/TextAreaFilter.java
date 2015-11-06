package view;


import java.awt.Toolkit;
import java.nio.charset.StandardCharsets;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class TextAreaFilter extends DocumentFilter{

    private int maxChars;
    public TextAreaFilter(int maxChars) {
        this.maxChars = maxChars;
    }

    public void insertString(FilterBypass fb, int offs, String str,
                            AttributeSet attr) throws BadLocationException {
    	System.out.println("Aasdadwssd");
        if ((fb.getDocument().getLength() + str.length()) <= maxChars) {
        	System.out.println("Doc length: "+fb.getDocument().getLength());
        	System.out.println("Str:" +str.length());
            super.insertString(fb, offs, str, attr);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void replace (DocumentFilter.FilterBypass fb, int offset, int length,
    		String str, AttributeSet attrs) throws BadLocationException {


    	String docString = fb.getDocument().getText(0,
    											  fb.getDocument().getLength());

    	byte[] docBytes = docString.getBytes(StandardCharsets.UTF_8);

    	int strByteLength = str.getBytes(StandardCharsets.UTF_8).length;
    	int docByteLength = docBytes.length;


        if ((strByteLength + docByteLength) <= maxChars) {
//        	System.out.println("Str text: "+str);
//        	System.out.println("Str byte: "+strByteLength);
//        	System.out.println("doc str" +fb.getDocument().getText(0, fb.getDocument().getLength()));
//        	System.out.println("doc length:"+ docByteLength);

        	super.replace(fb, offset, length, str, attrs);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
