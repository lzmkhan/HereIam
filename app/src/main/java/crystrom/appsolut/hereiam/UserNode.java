package crystrom.appsolut.hereiam;

import com.google.firebase.database.Exclude;

import java.util.Calendar;

/**
 * Created by Marcus Khan on 4/15/2017.
 */

public class UserNode {
    @Exclude
    public String userId;

    public String latitude;
    public String longitude;
    public String lastUpdated;

    UserNode() {
    }

    UserNode(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdated = Calendar.getInstance().getTime().toString();
    }

    void updateLatLong(String latitude, String longitude, String lastUpdated) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdated = lastUpdated;
    }
}
