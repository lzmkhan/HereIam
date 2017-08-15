package crystrom.appsolut.hereiam;

import com.google.firebase.database.Exclude;

import java.util.Calendar;
import java.util.HashMap;

import crystrom.appsolut.hereiam.Room.UserNodePlus;


/**
 * Created by Marcus Khan on 4/15/2017.
 */

public class RoomNode {
    public static int OBSERVE = 1;
    public static int BROADCAST = 2;
    public static int JOINED = 7;
    public static int LEFT_ROOM = 8;
    public static int CHECK_OK = 3;
    public static int CHECK_FAILED = 4;
    public static int ROOM_CLOSED = 5;
    public static int ROOM_OPEN = 6;


    @Exclude
    public String ID;

    public int state;
    public int numberOfSlots;
    public String createdTime;
    public HashMap<String, UserNodePlus> users = new HashMap<String, UserNodePlus>();


    RoomNode(String id, int numberOfSlots) {
        this.state = ROOM_OPEN;
        this.ID = id;
        this.numberOfSlots = numberOfSlots;
        this.createdTime = Calendar.getInstance().getTime().toString();
    }


    /**
     * The user shall add user with this function before sending the room node to firebase
     * <p>
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
     * used to make new usernodeplus objects since it is private to this class.
     *
     * @param userId
     * @return
     */

    @Exclude
    UserNodePlus makeNewUser(String userId) {
        return new UserNodePlus(userId, "0", "0", OBSERVE, JOINED);
    }


    /**
     * This method is used to check whether all the properties has been initialized or not.
     * It will check if the Hashmap for users contains all four of the users and the mode.
     */
    @Exclude
    public int confirm() {
        if (ID.equals("") | ID.equals(null) | !(numberOfSlots == 4) | createdTime.equals("") | createdTime.equals(null) | users.isEmpty()) {
            return CHECK_FAILED;
        } else {
            return CHECK_OK;
        }

    }


}
