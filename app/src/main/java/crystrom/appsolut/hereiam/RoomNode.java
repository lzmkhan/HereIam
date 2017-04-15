package crystrom.appsolut.hereiam;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

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
    private Map<String, String> users = new HashMap<String, String>();


    RoomNode(String id, String numberOfSlots, String createdTime) {
        this.ID = id;
        this.numberOfSlots = numberOfSlots;
        this.createdTime = createdTime;
    }


    /**
     * The user shall add user with this function before sending the room node to firebase
     * id is the generated id
     * and mode is the user's behaviour inside the database.
     * mode shall either be OBSERVE or BROADCAST
     * While in OBSERVE mode, the user can only observe and their location will not be
     * shared. While in Broadcast, they can Observer and Broadcast their location and other
     * broadcasters's location.
     *
     * @param id
     * @param mode
     */
    @Exclude
    public void addUpdateUser(String id, String mode) {
        users.put(id, mode);
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
}
