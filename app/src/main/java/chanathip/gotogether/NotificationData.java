package chanathip.gotogether;

/**
 * Created by neetc on 11/9/2016.
 */

public class NotificationData {
    String Type;
    String CurrentuserUid;
    String CurrentuserDisplayname;

    //title
    String titlename;

    //friend request
    String RequestUserUid;
    String RequestUserFirstname;
    String RequestUserLastname;
    String RequestUserdisplayname;

    //unread message
    String SenderUid;
    String SenderLastmessage;
    String SenderDisplayname;
    int Unreadcount;
}
