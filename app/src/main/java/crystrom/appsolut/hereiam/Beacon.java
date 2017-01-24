package crystrom.appsolut.hereiam;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Beacon extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    TextView idText;
    String ID ="";
    Button sendIdBtn;
    Button stopBCBtn;
    int delay=5000;//to be taken from settings
    ProgressBar pr;
    private int REQUEST_ACCESS_LOCATION = 1;
    GoogleApiClient googleApi;
    LocationManager  manager;
    Utilities util = new Utilities();
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        database = FirebaseDatabase.getInstance();


        pr= (ProgressBar) findViewById(R.id.progressBar);
        pr.setVisibility(View.INVISIBLE);
        idText = (TextView)findViewById(R.id.idTextView);
        ID = util.generateID();
        idText.setText(ID);

        manager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

        sendIdBtn = (Button)findViewById(R.id.button8);
        sendIdBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //display a dialogue get alias/id then send the generated id to that particular alias/id
                //start updating the geocodes in the firebase db for that current id
                DialogFragment dialog = new customDialog();
                Bundle args = new Bundle();
                args.putString("ID",ID);
                args.putString("MODE","BEACON");
                dialog.setArguments(args);
                FragmentManager managerFrag = getFragmentManager();
                managerFrag.beginTransaction();
                dialog.show(managerFrag,"sendid");


            }
        });

        stopBCBtn = (Button)findViewById(R.id.button9);
        stopBCBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if (stopBCBtn.getText().toString().equals("Start Broadcasting")){


                    if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                        pr.setVisibility(View.VISIBLE);
                        stopBCBtn.setText("Stop Broadcasting");

                        int state = checkPermission("android.permission.ACCESS_FINE_LOCATION", Binder.getCallingPid(), Binder.getCallingUid());
                        if (state == PackageManager.PERMISSION_GRANTED) {//check for permission, if permission is granted check for location
                            googleApi.connect();
                        } else {//else ask permission.
                            ActivityCompat.requestPermissions(Beacon.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Seems like your GPS is not enabled. Please enable to broadcast.",Toast.LENGTH_SHORT).show();
                        //go to settings menu to enable the gps
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }else {
                    stopBCBtn.setText("Start Broadcasting");
                    pr.setVisibility(View.INVISIBLE);

                    googleApi.disconnect();
                    myRef.removeValue();
                }
            }
        });

        googleApi = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
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
                Log.d("Location","Latitude = " + lat + "Longitude"+ lon);
                writeToFirebase(lon,lat);

            }
        }
        catch(SecurityException e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            Log.d("Error","Error at onConnected(): " + e.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("Location", "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude());
            writeToFirebase(String.valueOf(location.getLongitude()),String.valueOf(location.getLatitude()));
        }else{
            Toast.makeText(getApplicationContext(),"GPS has been disabled!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    Toast.makeText(getApplicationContext(),"Connection Failed", Toast.LENGTH_SHORT).show();
    }

    public static class customDialog extends DialogFragment{
        String mode,message;
        String idToBeSent;
        RadioButton rb1, rb2;



        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        }



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View v = inflater.inflate(R.layout.dialog1, null);

            mode = getArguments().getString("MODE");
            idToBeSent = getArguments().getString("ID");
            if(mode.equals("BEACON")) {
                builder.setTitle("Send ID");
                message = "Please enter this ID("+idToBeSent+") in Receiver Activity";
            }else if(mode.equals("ROOM")){
                builder.setTitle("Send Room ID");
                message ="Please enter this room ID("+idToBeSent+") in Room Activity";
            }



            builder.setView(v);
            rb1 = (RadioButton)v.findViewById(R.id.radioButton);
            rb2 = (RadioButton)v.findViewById(R.id.radioButton2);
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //send location to firebase db


                   if(rb2.isChecked()){
                       Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + ""));
                       intent.putExtra("sms_body",message );
                       startActivity(intent);
                   }else{
                       ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                       ClipData clip = ClipData.newPlainText("ID",message);
                       clipboardManager.setPrimaryClip(clip);


                   }
                }
            });

            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            return builder.create();
        }


    }

    void writeToFirebase(String longitude, String latitude){

        myRef = database.getReference(ID);
        myRef.setValue(longitude + "," + latitude);

    }

}

