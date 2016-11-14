package chanathip.gotogether;

import java.util.Calendar;

/**
 * Created by neetc on 11/11/2016.
 */

public class UserMessage implements Comparable<UserMessage> {
    String sender;
    String message;
    String time;
    Calendar calendar;

    String Type;

    String readstatus;


    @Override
    public int compareTo(UserMessage o) {

        return calendar.compareTo(o.calendar);
    }

    public boolean isRead(){
        if(readstatus.equals("read")){
            return true;
        }
        else return false;
    }
}
