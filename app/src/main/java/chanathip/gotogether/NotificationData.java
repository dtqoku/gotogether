package chanathip.gotogether;

/**
 * Created by neetc on 11/9/2016.
 */

public class NotificationData {
    NotificationData(){
        UserData userData = new UserData();
        GroupData groupData = new GroupData();
    }

    String Type;
    String CurrentuserUid;
    String CurrentuserDisplayname;

    //title
    String titlename;

    //friend request
    String RequestUserUid;
    String requestEmail;
    String RequestUserdisplayname;

    //unread message
    String SenderUid;
    String SenderLastmessage;
    String SenderDisplayname;
    int Unreadcount;

    //group request
    String RequestGroupUid;
    String RequestGroupname;
    String RequestGroupdetail;

    //group
    GroupData groupData;
}
