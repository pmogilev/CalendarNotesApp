package tk.calendar.app;

import java.util.Calendar;
import java.util.Date;
/**
 * Created by Pasha on 5/3/14.
 */
public class Utils {

    public static String convertDateToKey(Date date){

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if(date != null)
            return String.valueOf(year + "-" + month + "-" + day);
        else
            throw new IllegalArgumentException("Can not convert this date to a Note key");
    }

    public static String convertDateToKey(int year, int month, int dayOfMonth) {
        return String.valueOf(year + "-" + month + "-" + dayOfMonth);
    }
}
