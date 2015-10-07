package model.network.pdu;

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
     * Returns the UNIX time in seconds that the Date represents.
     *
     * @param date The date to convert to an int.
     * @return The time in seconds.
     */
    public static int toSeconds(Date date) {
        return (int) (date.getTime() / 1000);
    }

    /**
     * Returns a Date which represents the given UNIX time in seconds.
     *
     * @param seconds The number of seconds.
     * @return The Date represented by the integer.
     */
    public static Date toDate(int seconds) {
        return new Date((((long) seconds) & 0xffff) * 1000);
    }

    /**
     * Formats the date according to the DateFormat.
     *
     * @param date The date to format.
     * @return A String representation of the Date.
     */
    public static String format(Date date) {
            return "[" + DATE_FORMAT.format(date) + "]";
    }
}