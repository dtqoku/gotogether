package chanathip.gotogether;

/**
 * Created by neetc on 10/25/2016.
 */

public class UserData {
    int UserID;
    String Firstname;
    String Lastname;
    String Username;
    String password;
    String Email;
    String Emailwithoutadd;
    String Phone;
    String displayname;
    double LocationLat;
    double LocationLng;

    public void setData(String userID,String Firstname,String Lastname,String Username,String Email,String Phone){
        this.UserID = Integer.parseInt(userID);
        this.Firstname = Firstname;
        this.Lastname = Lastname;
        this.Username = Username;
        this.Email = Email;
        this.Phone = Phone;
    }
}
