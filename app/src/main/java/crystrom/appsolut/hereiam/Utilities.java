package crystrom.appsolut.hereiam;

import android.util.Log;

import java.util.Random;

/**
 * Created by marcus on 11/26/2016.
 */

public class Utilities {

    private static boolean checkAvailability(String ID){
        /**check in firebase if the generated ID exists. if it exists return false, else return true**/
        boolean result = false;
        //start a connection and send ID
        //the python script at server will check whether the generated id is already present
        //if it is present it will set result to true




        return !result;
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

/*    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");

    myRef.setValue("Hello, World!");*/



    public interface getRoomID{
        void onRoomIDObtained(String id);
    }
    }





