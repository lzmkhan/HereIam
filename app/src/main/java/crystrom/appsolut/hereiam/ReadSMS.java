package crystrom.appsolut.hereiam;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadSMS extends BroadcastReceiver {


    public ReadSMS() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        final Bundle bundle = intent.getExtras();


        try {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {
                        String ID ="NORMAL";
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;

                        String message = currentMessage.getMessageBody();

                        //"Please enter this ID(\"+idToBeSent+\") in Receiver Activity";
                        //String pattern  = "Please\\senter\\sthis\\sID\\([A-Z]{4}[0-9]{4}\\)\\sin\\sReceiver\\sActivity";
                        String pattern = "Please\\senter\\sthis\\sID\\([A-Z]{4}[0-9]{4}\\)\\sin\\s[Roomecivr]{4,8}\\sActivity";
                        Pattern pattern1 = Pattern.compile(pattern);
                        Matcher match = pattern1.matcher(message);
                        if(match.find()) {
                            int start = message.indexOf("(");
                            int end = message.indexOf(")");
                            ID = message.substring(start+1, end);


                            Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + ID);


                            // Show Alert
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context,
                                    "senderNum: " + senderNum + ", message: " + ID, duration);
                            toast.show();

                            Intent startIntent;
                            if(message.contains("Room")){
                                startIntent = new Intent(context, Room.class);
                            }else{
                                startIntent = new Intent(context, Receiver.class);
                            }


                            startIntent.putExtra("ID",ID);
                            startIntent.setAction(ID);// because putExtra is not received if setAction is not unique. it will take the first intent with the same action
                            PendingIntent piOpen = PendingIntent.getActivity(context, 0, startIntent, 0);


                                // Constructs the Builder object.
                            Notification.Builder builder =
                                    new Notification.Builder(context)
                                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                            .setContentTitle("HereIam")
                                            .setContentText("Broadcast ID(" + ID + ") received from " + senderNum)
                                            .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                                            .setStyle(new Notification.BigTextStyle()
                                                    .bigText("You have received a Broadcast ID(" + ID + ") from " + senderNum + ". You can tap this notification to go directly to app's Receiver/Room page"))
                                            .setContentIntent(piOpen);


                            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(1, builder.build());

                        }
                    } // end for loop
                } // bundle is null
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}
