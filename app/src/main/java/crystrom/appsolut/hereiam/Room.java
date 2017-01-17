package crystrom.appsolut.hereiam;

        import android.app.Activity;
        import android.app.Fragment;
        import android.app.FragmentManager;
        import android.app.FragmentTransaction;

        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.Timer;
        import java.util.TimerTask;

public class Room extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_container);

        Fragment fragmentEnter = new RoomEnterFragment();

        FragmentManager fmanager = getFragmentManager();
        FragmentTransaction ftransact = fmanager.beginTransaction();
        ftransact.replace(R.id.room_fragment_frame, fragmentEnter);
        ftransact.commit();

    }





    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }

    public static class RoomEnterFragment extends Fragment    {



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final View view1 = inflater.inflate(R.layout.room_enter, container, false);
            Button enterBtn = (Button) view1.findViewById(R.id.button14);
            enterBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Boolean isPresent = false;
                    // Validate the room ID, if it is present, then call the next fragment
                    //Room validation by connecting firebase should be done here.
                    //just a placeholder for validity
                    EditText edit1 = (EditText) view1.findViewById(R.id.editText4);
                    String roomID  = edit1.getText().toString();

                    if (roomID.equals("ABCD1234")){
                        isPresent = true;
                    }else{
                        isPresent = false;
                    }

                    if( isPresent == true) {
                        Fragment roomFrag = new RoomFragment();
                        FragmentManager fmanager1 = getFragmentManager();
                        FragmentTransaction ftransact1 = fmanager1.beginTransaction();

                        ftransact1.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left,R.animator.enter_from_left, R.animator.exit_to_right);
                        ftransact1.replace(R.id.room_fragment_frame, roomFrag);
                        ftransact1.addToBackStack(null);
                        ftransact1.commit();
                    }
                    else{
                        Toast.makeText(getActivity(),"Sorry Room ID incorrect or not available!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            Button createBtn = (Button) view1.findViewById(R.id.button15);
            createBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CreateFragment create = new CreateFragment();
                    FragmentManager fmanager2 = getFragmentManager();
                    FragmentTransaction ftransact2 = fmanager2.beginTransaction();
                    ftransact2.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left, R.animator.enter_from_left, R.animator.exit_to_right);
                    ftransact2.replace(R.id.room_fragment_frame,create);
                    ftransact2.addToBackStack(null);
                    ftransact2.commit();
                }
            });
            return view1;
        }



    }

    public static class RoomFragment extends Fragment{

        String state;
        Button sendBtn, exitRoomBtn, endRoomBtn;
        Timer timer1;
        long delay =1000;//should be updated from settings.
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view2 = inflater.inflate(R.layout.activity_geo_code,container,false);
            state = "STOP";

            sendBtn = (Button) view2.findViewById(R.id.button11);
            sendBtn.setText("Send Location");
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state.equals("STOP") && sendBtn.getText().equals("Send Location")) {
                        sendBtn.setText("Stop broadcast");
                        state = "SEND";
                        timer1 = new Timer();
                        timer1.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendLocation();
                            }
                        }, 10, delay);
                    } else {
                        sendBtn.setText("Send Location");
                        state = "STOP";
                        timer1.cancel();
                        timer1.purge();
                    }

                }
            });


            exitRoomBtn = (Button) view2.findViewById(R.id.button12);
            exitRoomBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    state = "STOP";
                    try {
                        if(timer1 != null){
                        timer1.cancel();
                        timer1.purge();
                        }
                        getFragmentManager().popBackStack();


                    } catch (Exception e) {
                        Log.d("Exception", e.toString());
                    }

                }
            });

            endRoomBtn = (Button) view2.findViewById(R.id.button13);
            endRoomBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**delete the room  if it is created by the same user else should be disabled.**/
                    //code to delete the room info from firebase should come here.




                    getFragmentManager().popBackStack();
                }
            });
            return view2;
        }

        private boolean sendLocation() {
            boolean status = false;
            //send location to firebase if sent successfully return true, else return false
            Log.d("room", "Timer on");

            return status;
        }
    }

    public static class CreateFragment extends Fragment{

        TextView editText1;
        Button exitRoomBtn,endRoomBtn,startBCBtn;
        Timer timer1;
        int delay =1000;//should be taken from settings

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.room_create, container, false);

            editText1 = (TextView)v.findViewById(R.id.textView3);
            String id = new Utilities().generateID();
            editText1.setText(id);

            exitRoomBtn = (Button)v.findViewById(R.id.button17);
            endRoomBtn = (Button)v.findViewById(R.id.button16);
            startBCBtn = (Button)v.findViewById(R.id.button18);



            exitRoomBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    getFragmentManager().popBackStack();
                    Log.d("Create room","exit button pressed");
                }
            });

            endRoomBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //delete the room in firebase. and exit the fragment.
                    Log.d("Create room","end button pressed");





                    getFragmentManager().popBackStack();
                }
            });
            startBCBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //start sending location to firebase.
                    if(startBCBtn.getText().toString().equals("Start Broadcast")){
                        startBCBtn.setText("Stop Broadcast");
                        timer1 = new Timer();
                        timer1.schedule(new TimerTask(){
                            @Override
                        public void run(){
                                //get location from gps, and commit to firebase
                                Log.d("RoomCreate","Getting data from GPS");
                            }
                        },100,delay);

                    }else{
                        startBCBtn.setText("Start Broadcast");
                        if(timer1 != null){
                            timer1.cancel();
                            timer1.purge();
                        }
                    }
                }
            });




            return v;
        }
    }
}
