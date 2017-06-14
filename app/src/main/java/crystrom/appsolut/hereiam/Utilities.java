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
    public final static int ROOM_MODE = 1;
    public final static int BEACON_MODE = 2;
    int mode;
    private DataSnapshot currentData;
    private DatabaseReference dbRef1;
    private FirebaseDatabase firebaseReference = FirebaseDatabase.getInstance();
    private CustomListeners.OnGeneratedIdConfirm OnGeneratedIdConfirm;

    public Utilities(int mode) {
        this.mode = mode;
        if (mode == ROOM_MODE) {
            dbRef1 = firebaseReference.getReference("Rooms");
            dbRef1.addListenerForSingleValueEvent(this);
        } else {
            dbRef1 = firebaseReference.getReference("Users");
            dbRef1.addListenerForSingleValueEvent(this);
        }
    }

    private static String giveAlphabet(int i) {
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

    public void SetOnSuccessIdGenerationCallBack(CustomListeners.OnGeneratedIdConfirm ui) {
        this.OnGeneratedIdConfirm = ui;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        currentData = dataSnapshot;
        Log.d("firebase","triggered");
        String id = generateID();
        OnGeneratedIdConfirm.OnSuccessfulIdGeneration(id);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private String generateID() {
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
        if(currentData != null){
            if(currentData.hasChild(ID)){
                Log.d("ID status",ID + " Exists!");
                generateID();
            }else{
                Log.d("ID status", ID + " Does not Exists!");
                return ID;
            }
        }else{
            Log.d("Utilities","current Data is null");
        }

        return ID;
    }

}





