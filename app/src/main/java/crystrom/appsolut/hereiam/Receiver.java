package crystrom.appsolut.hereiam;

import android.app.Activity;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Receiver  extends FragmentActivity implements OnMapReadyCallback {

    Button startTrack;
    EditText transmitterId;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView placeHolder;
    private GoogleMap mMap;
    boolean isMapReady = false;
    String transmitterID;
    LatLng prevValue = null;
    List<LatLng> positions = new ArrayList<LatLng>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        database = FirebaseDatabase.getInstance();


        placeHolder = (TextView) findViewById(R.id.placeHolder);


        transmitterId = (EditText)findViewById(R.id.editText2);

        startTrack = (Button) findViewById(R.id.button10);
        startTrack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                transmitterID  = transmitterId.getText().toString();

                if(startTrack.getText().toString().equals("Start Tracking")){
                    if(transmitterID.equals(null) || transmitterID.equals("") || transmitterID.equals(" ")){
                        Toast.makeText(getApplicationContext(),"You need to enter transmitter's ID!", Toast.LENGTH_SHORT).show();
                    }else{
                        //handle the firebase communication. start receiving updates from the db and update the map.
                        database.goOnline();
                        myRef = database.getReference(transmitterID.toUpperCase());
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                try {
                                    String [] latLong =dataSnapshot.getValue(String.class).split(",");
                                    if( isMapReady){
                                        startTrack.setText("Stop Tracking");

                                        LatLng mark = new LatLng(Double.parseDouble(latLong[1]), Double.parseDouble(latLong[0]));
                                        if (prevValue == null){
                                            prevValue = mark;
                                            mMap.addMarker(new MarkerOptions().position(mark).title(transmitterID));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(mark));
                                            mMap.moveCamera(CameraUpdateFactory.zoomIn());
                                        }
                                        else{
                                            mMap.clear();
                                            Location locate = new Location(LocationManager.GPS_PROVIDER);
                                            float[] results = new float[2];
                                            locate.distanceBetween(prevValue.latitude,prevValue.longitude,mark.latitude,mark.longitude,results);
                                            Log.d("Distance",results[0] +"");
                                            if(results[0] > 10)//only takes positions which are atleast 10 meters away from the previous point
                                            {
                                                prevValue = mark;
                                                if(positions.size() >= 8)// since the number of points in polyline is limited to 8
                                                {//we are restricting the number of points to 8

                                                    positions.remove(0); // we are removing the first point added to the list as means of trailing the movement
                                                    positions.add(mark);

                                                }else{
                                                    positions.add(mark);
                                                }
                                                mMap.addPolyline(
                                                        new PolylineOptions().addAll(positions)
                                                            .color(Color.GREEN)
                                                );

                                                mMap.addMarker(new MarkerOptions().position(mark).title(transmitterID));
                                                mMap.moveCamera(CameraUpdateFactory.newLatLng(mark));
                                                mMap.moveCamera(CameraUpdateFactory.zoomIn());
                                            }


                                        }


                                    }
                                   // placeHolder.setText("Latitude = " + latLong[0] + " Longitude = " + latLong[1]);
                                    placeHolder.setText("Last updated at " + Calendar.getInstance().getTime());
                                }catch(IndexOutOfBoundsException e){
                                    Toast.makeText(getApplicationContext(),"Failure in receiving Coordinates. Make sure the Beacon is broadcasting!",Toast.LENGTH_SHORT).show();
                                }
                                catch(Exception e){
                                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(),"Cannot find broadcast ID",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } else{
                    startTrack.setText("Start Tracking");
                    //handle the logic to stop receiving from the firebase.
                    database.goOffline();
                }


            }
        });



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady = true;
        mMap = googleMap;



    }
}