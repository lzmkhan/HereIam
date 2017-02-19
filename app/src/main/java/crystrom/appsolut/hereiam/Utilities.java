package crystrom.appsolut.hereiam;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

/**
 * Created by marcus on 11/26/2016.
 */

public class Utilities implements ValueEventListener{
    static boolean availability;
    static FirebaseDatabase fireDb = FirebaseDatabase.getInstance();
    static DatabaseReference dbRef1;
    private static boolean checkAvailability(String ID) {
        /**check in firebase if the generated ID exists. if it exists return false, else return true**/
        final String id = "QKAR6584";
        //availability = true;
        //start a connection and send ID
        //the python script at server will check whether the generated id is already present
        //if it is present it will set result to true
        //Note to myself
        //The below code isnt working. I could not simulate a datachange event for this singlevalueevent listener to trigger
        //need to work on this till then by default the availability is set to true. once you figure this shit out
        //change it back to false.
            dbRef1 = fireDb.getReference("hereiam-5b3de");
            dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        Log.d("ID",id);
                        if (dataSnapshot.hasChild(id) || dataSnapshot.hasChild("Room:" + id)) {
                            Log.d("key", "Key exists!");
                            availability = false;
                        } else {
                            availability = true;
                        }
                    } catch (Exception e) {
                        Log.d("Error", "error getting key" + e.toString());
                    }
                    
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        Log.d("Availability","" + availability);
        return availability;

    }


    public static String generateID() {
        /**generates ID **/
        String ID = "";
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            if (i < 4) {
                ID = ID + giveAlphabet(random.nextInt(26));
            } else {
                ID = ID + random.nextInt(9);
            }
        }


        if (checkAvailability(ID)) {
            return ID;
        } else {
            return generateID();
        }
    }

    public static String giveAlphabet(int i) {
        /**returns an alphabet from A-Z**/
        String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        String output = "";
        try {
            if (i >= 0 && i < 26) {
                output = alphabet[i];
            } else {
                throw new Exception("Value not between 0-26 cannot generate Alphabet");
            }
        } catch (Exception e) {
            Log.d("Exception at Beacon", e.toString());
        }
        return output;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

/*    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");

    myRef.setValue("Hello, World!");*/


    public interface getRoomID {
        void onRoomIDObtained(String id);
    }


}





