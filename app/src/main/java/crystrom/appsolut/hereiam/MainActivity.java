package crystrom.appsolut.hereiam;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    Button roomBtn;
    Button beaconBtn;
    Button receiverBtn;
    Button settingsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roomBtn = (Button) findViewById(R.id.button);
        roomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start geocode activity
                Intent roomIntent = new Intent();
                roomIntent.setClass(getApplicationContext(), Room.class);
                startActivity(roomIntent);

            }
        });

        beaconBtn = (Button) findViewById(R.id.button2);
        beaconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start beacon Activity
                Intent becIntent = new Intent();
                becIntent.setClass(getApplicationContext(), Beacon.class);
                startActivity(becIntent);
            }
        });

        receiverBtn = (Button) findViewById(R.id.button3);
        receiverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Receiver Activity
                Intent recIntent = new Intent();
                recIntent.putExtra("ID","NORMAL");
                recIntent.setClass(getApplicationContext(), Receiver.class);
                startActivity(recIntent);
            }
        });

        settingsBtn = (Button) findViewById(R.id.button4);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Settings Activity
                Intent setIntent = new Intent();
                setIntent.setClass(getApplicationContext(), Settings.class);
                startActivity(setIntent);
            }
        });






    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }
}
