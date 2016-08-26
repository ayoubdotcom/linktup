package victor.forum;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by hp on 2016-07-11.
 */
public class MyLocation implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        if(location != null)
        {
            Log.e("Longitute :", "" + location.getLongitude());
            Log.e("Latitute :", "" + location.getLatitude());
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
