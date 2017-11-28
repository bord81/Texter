package labut.md311.texter.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class ConnectivityChecker {

    //returns false if there's no connection or true if it's present
    public static boolean checkConnectivity(WeakReference<AppCompatActivity> activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = false;
        if (activeNetwork == null) {
            activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork == null)
                return false;
        } else {
            isConnected = activeNetwork.isConnectedOrConnecting();
        }
        return isConnected;
    }
}
