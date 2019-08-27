package co.edu.icesi.i2t.mosquitos.util;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by Domiciano on 13/02/2018.
 */
public class ListernerPosicion implements LocationListener {

    public double lat;
    public double lng;

    public ListernerPosicion(LocationListener listener) {
        this.listener = listener;

    }

    boolean firstTime = true;

    @Override
    public void onLocationChanged(Location location) {
        java.text.NumberFormat nf = new DecimalFormat("#0.000");
        //root.tv_location.setText("Coordenada: \nLat: " + nf.format(location.getLatitude()) + ", Lng: " + nf.format(location.getLongitude()) + "\n"
        //        + "Exacto a " + location.getAccuracy() + " metros");
        lat = location.getLatitude();
        lng = location.getLongitude();
        listener.onLocation(this, location);
        firstTime = false;
        Log.e(">>>","Coordenada: \nLat: " + nf.format(location.getLatitude()) + ", Lng: " + nf.format(location.getLongitude()) + "\n" + "Exacto a " + location.getAccuracy() + " metros");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.OUT_OF_SERVICE) ;

        if (status == LocationProvider.AVAILABLE) ;

        if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) ;

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    LocationListener listener;


    public interface LocationListener {
        void onLocation(ListernerPosicion listernerPosicion, Location location);
    }
}
