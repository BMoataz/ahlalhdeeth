package com.example.master.ahlalhdeeth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Master on 10/11/2014.
 */
public class ConnectionDetector {

    public Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    public boolean IsConnectedToInternet(){

        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if(info != null) {
                for (int i = 0; i<info.length; i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
