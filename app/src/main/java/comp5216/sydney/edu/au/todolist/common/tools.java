package comp5216.sydney.edu.au.todolist.common;

import java.util.Calendar;

/**
 * Created by Barry on 2015/8/25.
 */
public class tools {

    private static Calendar can= Calendar.getInstance();

    public static String localCreatedTime(){
        can= Calendar.getInstance();
        return can.get(Calendar.YEAR)+"-"+can.get(Calendar.MONTH)+"-"+can.get(Calendar.DAY_OF_MONTH)+" "+can.get(Calendar.HOUR_OF_DAY)+":"+can.get(Calendar.MINUTE);
    }
}
