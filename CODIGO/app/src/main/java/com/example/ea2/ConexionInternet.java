package com.example.ea2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class ConexionInternet extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Hay internet o no
        if (!checkearInternet(context)) {
            Toast.makeText(context, "Internet desconectada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Internet conectada", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkearInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            Toast.makeText(context, "No hay conexion de internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
