package chanathip.gotogether;

/**
 * Created by neetc on 11/14/2016.
 */

public class GroupData {
    String GroupUID;
    String Name;
    String Description;
    String MeetingPointStatus;
    double MeetingLocationLat;
    double MeetingLocationLng;

    //group with user
    String rank;
    int Membercount;


    //method
    public void setData(String Groupid,String name,String description,String rank,String MeetingPointStatus,String Membercount){
        this.GroupUID = Groupid;
        this.Name = name;
        this.Description = description;
        this.rank = rank;
        this.MeetingPointStatus = MeetingPointStatus;
        this.Membercount = Integer.parseInt(Membercount);

    }
    public boolean isleader(){
        if(rank.equals("leader")){
            return true;
        }
        else
            return false;
    }
    public boolean isMeetingPointSet(){
        if(MeetingPointStatus.equals("active")){
            return true;
        }
        else return false;
    }
}
