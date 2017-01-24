package crystrom.appsolut.hereiam;

        import crystrom.appsolut.hereiam.Beacon.customDialog;
        import crystrom.appsolut.hereiam.Utilities;
        import android.app.Dialog;
        import android.app.DialogFragment;
        import android.app.FragmentManager;

        import android.content.DialogInterface;
        import android.content.Intent;

        import android.os.Bundle;
        import android.support.v4.app.FragmentActivity;
        import android.app.AlertDialog;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.EditText;

        import android.widget.TextView;
        import android.widget.Toast;

        import com.github.clans.fab.*;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.HashMap;
        import java.util.Map;


public class Room extends FragmentActivity implements OnMapReadyCallback,Utilities.getRoomID {


    GoogleMap mMap;
    boolean isMapReady,roomAvailable = false;
    String roomID ="";
    TextView txt1;
    FirebaseDatabase fireDatabase;
    DatabaseReference dbRef, dbRef1,user1Ref,user2Ref,user3Ref,user4Ref,user5Ref,user6Ref,user7Ref,user8Ref;
    FloatingActionButton fab1,fab2, fab5, fab6;
    String SLOT="USER1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_container);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        txt1 = (TextView)findViewById(R.id.textView);
        txt1.setVisibility(View.INVISIBLE);

        fireDatabase = FirebaseDatabase.getInstance();



        //FloatingActionMenu fam = (FloatingActionMenu) findViewById(R.id.fabmenu);
        fab1 = (FloatingActionButton) findViewById(R.id.fabmenu1);
        fab2 = (FloatingActionButton) findViewById(R.id.fabmenu2);
        fab5 = (FloatingActionButton) findViewById(R.id.fabmenu5);
        fab6 = (FloatingActionButton) findViewById(R.id.fabmenu6);
        fab5.setVisibility(View.INVISIBLE);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fab1.getLabelText().equals("Enter a Room")) {
                    //Enter a room make an alert dialog and get the room id
                    CustomInputDialog dialog1 = new CustomInputDialog();
                    dialog1.setOut(Room.this);

                    dialog1.show(getFragmentManager().beginTransaction(), "getRoomID");
                    fireDatabase.goOnline();



                }else{
                    fab1.setLabelText("Enter a Room");
                    txt1.setText("");
                    txt1.setVisibility(View.INVISIBLE);
                    DatabaseReference dbRef12 = fireDatabase.getReference("Room:" + roomID + "/"+SLOT);
                    dbRef12.setValue("NONE");
                    dbRef12 = fireDatabase.getReference("Room:" + roomID + "/FREESLOTS");
                    dbRef12.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String freeSlot = dataSnapshot.getValue().toString();
                            freeSlot =freeSlot + "," + SLOT.replace("USER","");
                            DatabaseReference dbRef13 = fireDatabase.getReference("Room:" + roomID + "/FREESLOTS");
                            dbRef13.setValue(freeSlot);
                            SLOT = "USER1";
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    roomAvailable = false;
                    fireDatabase.goOffline();
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fab2.getLabelText().equals("Create a Room")) {
                    //Create a room auto generate room ID.


                    roomID = Utilities.generateID();
                    fireDatabase.goOnline();
                    createRoom(roomID);
                    fab2.setLabelText("Close this Room");
                    txt1.setText(roomID);
                    txt1.setVisibility(View.VISIBLE);
                }else{
                    fab2.setLabelText("Create a Room");
                    txt1.setText("");
                    txt1.setVisibility(View.INVISIBLE);
                    DatabaseReference closeRoomRef = fireDatabase.getReference("Room:" + roomID);
                    roomAvailable = false;
                    closeRoomRef.removeValue();
                    fireDatabase.goOffline();

                }
            }
        });




        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start broadcasting, get the room id, write to firebase record whose parent has room id
            if(roomAvailable == true){
                //start broadcasting if room available
            }


            }
        });

        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Invite thru sms or clipboard

                DialogFragment dialog = new customDialog();
                Bundle args = new Bundle();
                args.putString("ID",roomID);
                args.putString("MODE","ROOM");
                dialog.setArguments(args);
                FragmentManager managerFrag = getFragmentManager();
                managerFrag.beginTransaction();
                dialog.show(managerFrag,"sendid");
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady = true;
        mMap = googleMap;

    }


    public void createRoom(String ID){
        dbRef = fireDatabase.getReference("Room:" + ID);
        Map<String,String> childs = new HashMap<String,String>();
        childs.put("FREESLOTS","2,3,4,5,6,7,8");
        childs.put("USER1","RCVR");
        childs.put("USER2","NONE");
        childs.put("USER3","NONE");
        childs.put("USER4","NONE");
        childs.put("USER5","NONE");
        childs.put("USER6","NONE");
        childs.put("USER7","NONE");
        childs.put("USER8","NONE");
        dbRef.setValue(childs);


        roomAvailable = true;
        fab5.setVisibility(View.VISIBLE);

            watchRoom(ID);

    }

    public void getRoom(String ID){
        final String roomID = ID;
        DatabaseReference dbRef1 = fireDatabase.getReference("Room:" + roomID + "/FREESLOTS");
        dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("FREESLOTS",dataSnapshot.getValue().toString());
                String[] contents = dataSnapshot.getValue().toString().split(",");
                if(contents.length != 0) {
                    SLOT = "USER"+contents[0];
                    DatabaseReference dbRef11 = fireDatabase.getReference("Room:" + roomID + "/USER" + contents[0]);
                    dbRef11.setValue("RCVR");
                    dbRef11 = fireDatabase.getReference("Room:" + roomID + "/FREESLOTS");
                    String Freeslots ="";
                    for(int i = 1; i < contents.length;i++){
                        if(i == contents.length-1){
                            Freeslots = Freeslots + contents[i];
                        }else{
                            Freeslots = Freeslots + contents[i] + ",";
                        }
                    }
                    dbRef11.setValue(Freeslots);

                    roomAvailable = true;
                }else{
                    Toast.makeText(getApplicationContext(),"Room is full. No slots available!", Toast.LENGTH_SHORT).show();
                    roomAvailable = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fab5.setVisibility(View.VISIBLE);
    }

    public void watchRoom(String ID) {

        dbRef1 = fireDatabase.getReference("Room:" + ID +"/FREESLOTS");
        dbRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String[] contents = dataSnapshot.getValue().toString().split(",");
                if(roomAvailable != false) {
                    Toast.makeText(getApplicationContext(), "Slots: " + dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        user1Ref = fireDatabase.getReference("Room:" + ID +"/USER1");
        user1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(roomAvailable != false) {
                    String content = dataSnapshot.getValue().toString();
                    if (!content.equals("NONE") && (content.contains("BC:") || content.contains("RCVR"))) {
                        Toast.makeText(getApplicationContext(), "USER1 has joined", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"USER1 has left", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user2Ref = fireDatabase.getReference("Room:" + ID +"/USER2");
        user2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(roomAvailable != false) {
                        String content = dataSnapshot.getValue().toString();
                        if(!content.equals("NONE") && (content.contains("BC:") || content.contains("RCVR:"))) {
                            Toast.makeText(getApplicationContext(), "USER2 has joined", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"USER2 has left",Toast.LENGTH_SHORT).show();
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user3Ref = fireDatabase.getReference("Room:" + ID +"/USER3");
        user3Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (roomAvailable != false) {
                    String content = dataSnapshot.getValue().toString();
                    if (!content.equals("NONE") ) {
                        if( content.contains("BC:")){
                            String broadCastId = content.replace("BC:","");
                            Toast.makeText(getApplicationContext(), "USER3 has joined as Beacon", Toast.LENGTH_SHORT).show();
                        }else if(content.contains("RCVR:")){
                            String broadCastId = content.replace("RCVR:","");
                            Toast.makeText(getApplicationContext(), "USER3 has joined as Observer", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),"USER3 has left",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user4Ref = fireDatabase.getReference("Room:" + ID +"/USER4");
        user4Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (roomAvailable != false) {
                    String content = dataSnapshot.getValue().toString();
                    if (!content.equals("NONE") && (content.contains("BC:") || content.contains("RCVR:"))) {
                        Toast.makeText(getApplicationContext(), "USER4 has joined", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"USER4 has left", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user5Ref = fireDatabase.getReference("Room:" + ID +"/USER5");
        user5Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (roomAvailable != false) {
                    String content = dataSnapshot.getValue().toString();
                    if (!content.equals("NONE") && (content.contains("BC:") || content.contains("RCVR:"))) {
                        Toast.makeText(getApplicationContext(), "USER5 has joined", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "USER5 has left", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user6Ref = fireDatabase.getReference("Room:" + ID +"/USER6");
        user6Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (roomAvailable != false) {
                    String content = dataSnapshot.getValue().toString();
                    if (!content.equals("NONE") && (content.contains("BC:") || content.contains("RCVR:"))) {
                        Toast.makeText(getApplicationContext(), "USER6 has joined", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"USER6 has left", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user7Ref = fireDatabase.getReference("Room:" + ID +"/USER7");
        user7Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (roomAvailable != false) {
                    String content = dataSnapshot.getValue().toString();
                    if (!content.equals("NONE") && (content.contains("BC:") || content.contains("RCVR:"))) {
                        Toast.makeText(getApplicationContext(), "USER7 has joined", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"USER7 has left", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user8Ref = fireDatabase.getReference("Room:" + ID +"/USER8");
        user8Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (roomAvailable != false) {
                    String content = dataSnapshot.getValue().toString();
                    if (!content.equals("NONE") && (content.contains("BC:") || content.contains("RCVR:"))) {
                        Toast.makeText(getApplicationContext(), "USER8 has joined", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"USER8 has left", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRoomIDObtained(String id) {
        roomID = id.toUpperCase();
        fab1.setLabelText("Exit Room");
        getRoom(roomID);
        txt1.setText(roomID);
        txt1.setVisibility(View.VISIBLE);
    }

    public static class CustomInputDialog extends DialogFragment{
        public static  String obtainedID ="";
        EditText ed1;
        public boolean status = false;
        Utilities.getRoomID out;

        public void setOut(Utilities.getRoomID ot){
            this.out = ot;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View v = inflater.inflate(R.layout.dialog2, null);
            ed1 = (EditText) v.findViewById(R.id.editText);
            builder.setTitle("Enter Room ID");
            builder.setView(v);
            builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   obtainedID = ed1.getText().toString();
                    if(obtainedID.equals("") || obtainedID.equals(" ")){
                        status = false;
                        Toast.makeText(getActivity(),"Room ID cannot be empty!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        status = true;
                        out.onRoomIDObtained(obtainedID);
                    }

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    status = false;
                    dismiss();
                }
            });



            return builder.create();
        }
    }




}
