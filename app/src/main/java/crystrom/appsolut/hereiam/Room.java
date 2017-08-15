package crystrom.appsolut.hereiam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

import crystrom.appsolut.hereiam.Beacon.customDialog;


public class Room extends FragmentActivity implements OnMapReadyCallback, CustomListeners.getRoomID, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    GoogleMap mMap;
    int delay = 5000;//to be taken from settings
    boolean isMapReady, roomAvailable, broadcastStarted = false;
    String mRoomID = "", mUserID = "";
    TextView txt1;
    FirebaseDatabase fireDatabase;
    FloatingActionButton enterRoomFabMenuitem, createRoomFabMenuItem;
    ImageButton navLeftBtn, navRightBtn, inviteBtn, startBroadcastBtn;
    ProgressDialog progressDialog;
    DatabaseReference dbRefMain;
    LocationManager manager;
    GoogleApiClient googleApi;
    UserNodePlus userNode;


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


                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        // Highlight the broadcast item to indicate that the user is broadcasting their location.
                        if (googleApi.isConnected() == false) {
                            googleApi.connect();
                        }
                        startBroadcastBtn.setImageResource(R.drawable.ic_002_broadcast_lighted);
                        broadcastStarted = true;

                        int state = checkPermission("android.permission.ACCESS_FINE_LOCATION", Binder.getCallingPid(), Binder.getCallingUid());
                        if (state == PackageManager.PERMISSION_GRANTED) {//check for permission, if permission is granted check for location

                        } else {//else ask permission.
                            ActivityCompat.requestPermissions(Room.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, Beacon.REQUEST_ACCESS_LOCATION);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Seems like your GPS is not enabled. Please enable to broadcast.", Toast.LENGTH_SHORT).show();
                        //go to settings menu to enable the gps
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }

                    // TODO: broadcast logic should be implemented here
                    // Update the CommMode to Broadcast(2)
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
                if (enterRoomFabMenuitem.getLabelText().equals("Join Room")) {
                    //Enter a room make an alert dialog and get the room id

                    CustomInputDialog customInputDialog = new CustomInputDialog();
                    customInputDialog.setOut(Room.this);
                    customInputDialog.show(getFragmentManager(), "RoomID");

                    //enter the room logic is performed at OnRoomIDObtained implemented method of CustomListeners interface.

                } else {
                    //Exit a room

                    // Remove the listeners from the dbRefMain
                    dbRefMain = null;


                    // Clear the map with existing geo points
                    mMap.clear();

                    //Set the floating action bar to default
                    enterRoomFabMenuitem.setLabelText("Join Room");

                    //set the Room id text view to invisible.
                    txt1.setVisibility(View.INVISIBLE);

                    //Make the create room fab menu visible
                    createRoomFabMenuItem.setVisibility(View.VISIBLE);

                    //Reference the accessmode for the current user and make it LEFT_ROOM so that this user no longer
                    //receive or update data to or from the firebase.
                    dbRefMain = fireDatabase.getReference("Rooms/" + mRoomID + "/users/" + mUserID + "/accessMode");
                    dbRefMain.setValue(RoomNode.LEFT_ROOM);



                }
            }
        });

        createRoomFabMenuItem = (FloatingActionButton) findViewById(R.id.fabmenu2);
        createRoomFabMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (createRoomFabMenuItem.getLabelText().equalsIgnoreCase("Create a Room")) {

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
                            mRoomID = id;
                            txt1.setVisibility(View.VISIBLE);
                            txt1.setText("Room ID: " + mRoomID);

                            dbRefMain = fireDatabase.getReference("Rooms/" + mRoomID);


                            //Generate a User ID to enter the room.
                            Utilities util = new Utilities(Utilities.BEACON_MODE);

                            util.SetOnSuccessIdGenerationCallBack(new CustomListeners.OnGeneratedIdConfirm() {
                                @Override
                                public void OnSuccessfulIdGeneration(String id) {
                                    mUserID = id;

                                    //Create a room node with 4 available slots.
                                    RoomNode roomNode = new RoomNode(mRoomID, 4);

                                    //add the room Creater as user.
                                    roomNode.addUpdateUser(roomNode.makeNewUser(mUserID));


                                    //Check whether all the necessary info has been added before commiting to database
                                    //this is to reduce the error and maintain a same heirarchy of nodes in the firebase.
                                    if (roomNode.confirm() == RoomNode.CHECK_OK) {
                                        Log.d("Room", "roomNode creation check OK");
                                        dbRefMain.setValue(roomNode);
                                    }

                                    //Close the progress Dialog
                                    if (progressDialog.isShowing()) {
                                        progressDialog.hide();
                                    }


                                    //Change the fab menu item text to End room
                                    createRoomFabMenuItem.setLabelText("End Room");
                                }
                            });

                        }
                    });
                } else {

                    //remove the current room node from the database.
                    //Before removing check whether there are users present.
                    //If they are present update the state as Closed.
                    //Before updating any room info, they should check whether the room is open or closed.
                    dbRefMain = fireDatabase.getReference("Rooms/" + mRoomID + "/state");
                    dbRefMain.setValue(RoomNode.ROOM_CLOSED);


                }

            }
        });


        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Invite thru sms or clipboard

                DialogFragment dialog = new customDialog();
                Bundle args = new Bundle();
                args.putString("ID", mRoomID);
                args.putString("MODE", "ROOM");
                dialog.setArguments(args);
                FragmentManager managerFrag = getFragmentManager();
                managerFrag.beginTransaction();
                dialog.show(managerFrag, "sendid");
            }
        });


    }

    @Override
    public void onRoomIDObtained(String id) {
        // Once obtaining the Room Id from the Dialog, we need to check whether this room id is present
        // in the database or not.
        // Then if it is present, we will take the data from it and update our UI
        // else we need to show a toast or snack bar that the provided room does not exists.

        final ProgressDialog progressDialog1 = new ProgressDialog(Room.this);
        progressDialog1.setIndeterminate(true);
        progressDialog1.setTitle("Joining room...Please wait!");
        progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog1.show();


        final String rId = id;
        Log.d("ROOM", "OnRoomIDObtained Entered");

        try {
            dbRefMain = fireDatabase.getReference("Rooms/");

            dbRefMain.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Check whether the room actually exists
                    if (dataSnapshot.hasChild(rId)) {
                        // Check if the state of room is ROOM_OPENED
                        if (dataSnapshot.child(rId).child("state").getValue(Integer.class) == RoomNode.ROOM_OPEN) {

                            // Check if room has empty space by comparing it with the number of slots child node
                            if (((HashMap<Integer, UserNodePlus>) dataSnapshot.child(rId).child("users").getValue()).size() < dataSnapshot.child(rId).child("numberOfSlots").getValue(Integer.class)) {

                                Log.d("can join room?", " yes");
                                // Room Joined successfully
                                mRoomID = rId;

                                // Generate id for this user
                                Utilities util = new Utilities(Utilities.BEACON_MODE);
                                util.SetOnSuccessIdGenerationCallBack(new CustomListeners.OnGeneratedIdConfirm() {
                                    @Override
                                    public void OnSuccessfulIdGeneration(String id) {
                                        mUserID = id;
                                        // Add details about this user.
                                        DatabaseReference dbRefTemp = fireDatabase.getReference("Rooms/" + mRoomID + "/users/" + mUserID);
                                        userNode = new UserNodePlus(mUserID, "0", "0", RoomNode.OBSERVE, RoomNode.JOINED);
                                        dbRefTemp.setValue(userNode);


                                        txt1.setVisibility(View.VISIBLE);
                                        txt1.setText("Room ID: " + mRoomID);
                                        roomAvailable = true;

                                        // Change the label to Exit room so  that the else part can execute
                                        enterRoomFabMenuitem.setLabelText("Exit Room");
                                        createRoomFabMenuItem.setVisibility(View.GONE);
                                        progressDialog1.hide();

                                        // Setting up user listeners to update the google map with user's current locations.
                                        setupUserListeners();
                                    }
                                });
                            } else {
                                Log.d("can join room?", " no");
                                Toast.makeText(Room.this, "Room is full you cannot join!", Toast.LENGTH_SHORT).show();

                            }

                        } else if (dataSnapshot.child(rId).child("state").getValue(Integer.class) == RoomNode.ROOM_CLOSED) {

                            // Room is closed ie. state is 5(ROOM_CLOsED)
                            Toast.makeText(Room.this, "Sorry, the Room you are trying to has been closed!", Toast.LENGTH_LONG).show();

                        } else {
                            // Room does not exist
                            Toast.makeText(Room.this, "Sorry, the Room you are trying to Join does not exist!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Room does not exist
                        Toast.makeText(Room.this, "Sorry, the Room you are trying to Join does not exist!", Toast.LENGTH_LONG).show();
                    }
                    progressDialog1.hide();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Operation cancelled due to network failure or user
                    Toast.makeText(Room.this, "Something went wrong, please try again!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.d("Room", "OnRoomIDObtained catch block");

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady = true;
        mMap = googleMap;

    }

    /**
     * This function is used to watch the particular room for changes and update the map
     * with user's location.
     */


    public void setupUserListeners() {

        if (roomAvailable == true) {

            dbRefMain = fireDatabase.getReference("Rooms/" + mRoomID);
            dbRefMain.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // First if room gets close we need to get this listener to stop listening
                    if (!(dataSnapshot.child("state").getValue(Integer.class) == RoomNode.ROOM_CLOSED)) {

                        // Check if any of the child is changing. if it changes, then you can update the map.

                        // Todo : Handle the map updation logic here.

                        mMap.clear();

                        for (DataSnapshot snapshot : dataSnapshot.child("users").getChildren()) {


                            // Check whether the user has joined or left
                            if (snapshot.child("accessMode").getValue(Integer.class) == RoomNode.JOINED) {

                                // Check whether the user is observer or broadcasted.
                                if (snapshot.child("commMode").getValue(Integer.class) == RoomNode.BROADCAST) {

                                    // Gets the current latitude and longitude positions of the user
                                    LatLng mark = new LatLng(Double.parseDouble(snapshot.child("latitude").getValue(String.class)), Double.parseDouble(snapshot.child("longitude").getValue(String.class)));

                                    // Adds the marker in the Map
                                    mMap.addMarker(new MarkerOptions().position(mark).title(snapshot.getKey()));


                                }
                            } else {
                                if (snapshot.child("accessMode").getValue(Integer.class) == RoomNode.LEFT_ROOM) {
                                    Toast.makeText(Room.this, "The user " + snapshot.getKey() + " has left the room", Toast.LENGTH_LONG).show();
                                }
                            }
                        }


                    } else {
                        // Notify the user that room has been closed. Hide the room id textview.
                        Toast.makeText(Room.this, "Sorry Room has been closed. You can no longer see the updates.", Toast.LENGTH_SHORT).show();
                        mMap.clear();
                        txt1.setText("");
                        txt1.setVisibility(View.GONE);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(delay); // Update location every  5 second
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApi, mLocationRequest, this);
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApi);
            if (mLastLocation != null) {
                String lat = String.valueOf(mLastLocation.getLatitude());
                String lon = String.valueOf(mLastLocation.getLongitude());
                Log.d("Location", "Latitude = " + lat + "Longitude" + lon);
                userNode.updateLatLong(lat, lon, Calendar.getInstance().getTime().toString());
                writeToFirebase(userNode);

            }
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d("Error", "Error at onConnected(): " + e.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("Location", "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude());
            userNode.updateLatLong(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), Calendar.getInstance().getTime().toString());
            writeToFirebase(userNode);
        } else {
            Toast.makeText(getApplicationContext(), "GPS has been disabled!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
    }


    void writeToFirebase(UserNode user) {

        // Add details about this user.
        DatabaseReference dbRefTemp = fireDatabase.getReference("Rooms/" + mRoomID + "/users/" + mUserID);
        dbRefTemp.setValue(user);


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
                        out.onRoomIDObtained(obtainedID.toUpperCase());
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

    public static class UserNodePlus extends UserNode {
        int commMode;
        int accessMode;

        UserNodePlus(String id, String latitude, String longitude, int mode, int aMode) {
            super.userId = id;
            super.latitude = latitude;
            super.longitude = longitude;
            super.lastUpdated = Calendar.getInstance().getTime().toString();
            this.commMode = mode;
            this.accessMode = aMode;
        }
    }
}
