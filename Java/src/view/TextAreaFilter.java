package view;


import java.awt.Toolkit;

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

        if ((fb.getDocument().getLength() + str.length()) <= maxChars) {
            super.insertString(fb, offs, str, attr);     
        } else {
            Toolkit.getDefaultToolkit().beep();    
        }
    }
    
    public void replace (DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet attrs) throws BadLocationException {
        if ((fb.getDocument().getLength() + str.length()) <= maxChars) {
            super.replace(fb, offset, length, str, attrs); 
        } else {
            Toolkit.getDefaultToolkit().beep();            
        }
    }
}
