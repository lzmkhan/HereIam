package crystrom.appsolut.hereiam;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BeaconService extends Service {
    public BeaconService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
