package chanathip.gotogether;

import java.util.ArrayList;

/**
 * Created by neetc on 10/25/2016.
 */

public class UserData {
    String UserUid;
    String Firstname;
    String Lastname;
    String password;
    String Email;
    String Emailwithoutadd;
    String Phone;
    String displayname;
    double LocationLat;
    double LocationLng;

    String rank;
    String GroupUid;
    String GroupName;

    String Status;

    String FriendStatus;

    int unreadMassage;

    public void setData(String UserUid,String Firstname,String Lastname,String displayname,String Email,String Phone){
        this.UserUid = UserUid;
        this.Firstname = Firstname;
        this.Lastname = Lastname;
        this.displayname = displayname;
        this.Email = Email;
        this.Phone = Phone;
    }
}
