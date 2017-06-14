package crystrom.appsolut.hereiam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import crystrom.appsolut.hereiam.Beacon.customDialog;


public class Room extends FragmentActivity implements OnMapReadyCallback {


    GoogleMap mMap;
    boolean isMapReady, roomAvailable, broadcastStarted = false;
    String roomID = "";
    TextView txt1;
    FirebaseDatabase fireDatabase;
    FloatingActionButton enterRoomFabMenuitem, createRoomFabMenuItem;
    ImageButton navLeftBtn, navRightBtn, inviteBtn, startBroadcastBtn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_container);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);


        txt1 = (TextView) findViewById(R.id.textView);
        txt1.setVisibility(View.INVISIBLE);


        fireDatabase = FirebaseDatabase.getInstance();


        startBroadcastBtn = (ImageButton) findViewById(R.id.start_broadcast);
        inviteBtn = (ImageButton) findViewById(R.id.invite);
        startBroadcastBtn.setEnabled(false);
        startBroadcastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!broadcastStarted) {
                    startBroadcastBtn.setImageResource(R.drawable.ic_002_broadcast_lighted);
                    broadcastStarted = true;
                } else {
                    broadcastStarted = false;
                    startBroadcastBtn.setImageResource(R.drawable.ic_002_broadcast);
                }
            }
        });

        enterRoomFabMenuitem = (FloatingActionButton) findViewById(R.id.fabmenu1);
        enterRoomFabMenuitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterRoomFabMenuitem.getLabelText().equals("Enter a Room")) {
                    //Enter a room make an alert dialog and get the room id


                } else {

                }
            }
        });

        createRoomFabMenuItem = (FloatingActionButton) findViewById(R.id.fabmenu2);
        createRoomFabMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Show a progress dialog
                progressDialog = new ProgressDialog(Room.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setTitle("Creating room...Please wait!");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();


                //Generate ID and check whether the generated ID is present in the database
                //Once generated update our UI and Activity state with the generated ID
                Utilities util = new Utilities(Utilities.ROOM_MODE);
                util.SetOnSuccessIdGenerationCallBack(new CustomListeners.OnGeneratedIdConfirm() {
                    @Override
                    public void OnSuccessfulIdGeneration(String id) {
                        roomID = id;
                        txt1.setVisibility(View.VISIBLE);
                        txt1.setText("Room ID: " + roomID);

                        //Close the progress Dialog
                        if (progressDialog.isShowing()) {
                            progressDialog.hide();
                        }

                    }
                });

                DatabaseReference dbRef1 = fireDatabase.getReference("Room/");


            }
        });


        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Invite thru sms or clipboard

                DialogFragment dialog = new customDialog();
                Bundle args = new Bundle();
                args.putString("ID", roomID);
                args.putString("MODE", "ROOM");
                dialog.setArguments(args);
                FragmentManager managerFrag = getFragmentManager();
                managerFrag.beginTransaction();
                dialog.show(managerFrag, "sendid");
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady = true;
        mMap = googleMap;

    }


    public static class CustomInputDialog extends DialogFragment {
        public static String obtainedID = "";
        public boolean status = false;
        EditText ed1;
        CustomListeners.getRoomID out;

        public void setOut(CustomListeners.getRoomID ot) {
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
                    if (obtainedID.equals("") || obtainedID.equals(" ")) {
                        status = false;
                        Toast.makeText(getActivity(), "Room ID cannot be empty!", Toast.LENGTH_SHORT).show();
                    } else {
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
