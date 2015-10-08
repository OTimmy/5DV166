package network.pdu;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for converting integers to Dates and vice versa.
 * Also contains a method for converting Dates to pretty Strings.
 * <br/>
 * Note that this code will not work for dates after 2038 (the real Y2K bug).
 */
public class DateUtils {

    /**
     * The format for printing dates. Feel free to change.
     */
    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("HH:mm:ss");

    /**
     * Formats the date according to the DateFormat.
     *
     * @param date The date to format.
     * @return A String representation of the Date.
     */
    public static String format(Date date) {
            return "[" + DATE_FORMAT.format(date) + "]";
    }
    
    public static Date getDateByBytes(byte[] bytes) {
    	long seconds = (bytes[0] & 0xff) << 24 | (bytes[1] & 0xff) << 16;
    	seconds |= (bytes[2] &0xff) << 8;
    	seconds |= (bytes[3] & 0xff);
    	return new Date(seconds * 1000);
    }
 
}