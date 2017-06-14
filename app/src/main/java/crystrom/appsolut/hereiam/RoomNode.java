package crystrom.appsolut.hereiam;

import com.google.firebase.database.Exclude;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Marcus Khan on 4/15/2017.
 */

public class RoomNode {
    public static int OBSERVE = 1;
    public static int BROADCAST = 2;
    public static int CHECK_OK = 3;
    public static int CHECK_FAILED = 4;
    private String ID;
    private String numberOfSlots;
    private String createdTime;
    private HashMap<String, UserNodePlus> users = new HashMap<String, UserNodePlus>();


    RoomNode(String id, String numberOfSlots) {
        this.ID = id;
        this.numberOfSlots = numberOfSlots;
        this.createdTime = Calendar.getInstance().getTime().toString();
    }


    /**
     * The user shall add user with this function before sending the room node to firebase
     *
     * mode shall either be OBSERVE or BROADCAST
     * While in OBSERVE mode, the user can only observe and their location will not be
     * shared. While in Broadcast, they can Observe and Broadcast their location and other
     * broadcasters's location.
     *
     * @param user is the UserNodePlus object which is the child of UserNode
     */
    @Exclude
    public void addUpdateUser(UserNodePlus user) {
        users.put(user.userId, user);
    }


    @Exclude
    public void removeUser(UserNodePlus user) {
        users.remove(user.userId);
    }

    @Exclude
    public UserNodePlus getUser(String userId) {
        return users.get(userId);
    }


    /**
     * This method is used to check whether all the properties has been initialized or not.
     * It will check if the Hashmap for users contains all four of the users and the mode.
     */
    @Exclude
    public int confirm() {
        if (ID.equals("") | ID.equals(null) | !numberOfSlots.equals("4") | createdTime.equals("") | createdTime.equals(null) | users.isEmpty()) {
            return CHECK_FAILED;
        } else {
            return CHECK_OK;
        }

    }


    private class UserNodePlus extends UserNode {
        int mode;

        UserNodePlus(String id, String latitude, String longitude, int mode) {
            super.userId = id;
            super.latitude = latitude;
            super.longitude = longitude;
            super.lastUpdated = Calendar.getInstance().getTime().toString();
            this.mode = mode;
        }
    }
}
