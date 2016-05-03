package tysheng.gank.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import tysheng.gank.R;

/**
 * Created by shengtianyang on 16/5/2.
 */
public class TimeUtil {

    public static int getWeekIcon() {
        //get today
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int today = c.get(Calendar.DAY_OF_WEEK);
        //get icon
        switch(today){
            case 1:
                return R.drawable.sunday;
            case 2:
                return R.drawable.monday;
            case 3:
                return R.drawable.tuesday;
            case 4:
                return R.drawable.wednesday;
            case 5:
                return R.drawable.thursday;
            case 6:
                return R.drawable.friday;
            default:
                return R.drawable.saturday;
        }
    }
    public static long getDataTime(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date date = format.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
