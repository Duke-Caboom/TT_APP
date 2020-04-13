package Library;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import androidx.appcompat.app.AppCompatActivity;

public class Networks extends AppCompatActivity {

    private Activity _activity;

    public Networks(Activity activity) {
        _activity = activity;
    }

    public boolean verificaNetworks() {
        boolean valor = false;
        ConnectivityManager cm = (ConnectivityManager) _activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (nc != null) {
                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    valor = true;
                } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    valor = true;
                }
            } else {
                valor = false;
            }
        } else {
            valor = false;
        }
        return valor;
    }
}
