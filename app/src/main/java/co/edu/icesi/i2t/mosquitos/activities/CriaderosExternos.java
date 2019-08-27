package co.edu.icesi.i2t.mosquitos.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import co.edu.icesi.i2t.mosquitos.R;
import co.edu.icesi.i2t.mosquitos.custom.Datos;
import co.edu.icesi.i2t.mosquitos.util.ListernerPosicion;

public class CriaderosExternos extends AppCompatActivity implements ListernerPosicion.LocationListener{

    private EditText textDirE;
    private EditText textDirG;

    private EditText idSum;
    private TextView lblIdSum;
    private CheckBox muestreo;

    private TextView conteoSum;
    private AlertDialog.Builder builder;
    private AlertDialog dialogoObservacion;

    private Spinner contAgua;
    private Spinner EstSum;
    private Spinner conMosq;
    private Spinner tipoCriad;

    private String sContAgua;
    private String sEstSum;
    private String sConMosq;
    private String sTipoCriad;

    private double lat;
    private double lon;
    private String address;


    LocationManager locationManager;
    ListernerPosicion locationListenerNetwork;
    ListernerPosicion locationListenerGPS;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criaderos_externos);
        inicializarComponentes();
        agregarListeners();

        //Localizacion
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListenerNetwork = new ListernerPosicion(this);
        locationListenerGPS = new ListernerPosicion(this);
        knowIfLocationAreEnabled();
        startLocation();
    }

    private void knowIfLocationAreEnabled() {
        final Context context = this;
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Por favor active los servicios de localización");
            dialog.setPositiveButton("Abrir configuración", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogo, int paramInt) {
                    dialogo.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void startLocation() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Ubicando");
        progressDialog.setMessage("Espere mientras se carga su ubicación");
        progressDialog.show();

        //tv_location.setText("Cargando las coordenadas de su ubicación...");
        Log.e(">>>>>","Cargando las coordenadas de la ubicacion....");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
        } else {
            actualizarUbicacion( locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) );
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListenerNetwork);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListenerGPS);
        }
    }


    public void guardar(View v){
        Datos.tipoArchivo=false;
        if(sConMosq!=null && sContAgua!=null && sEstSum!=null && !(textDirE.getText().toString().equals("")) && tipoCriad.getSelectedItemPosition()!=0 &&
                contAgua.getSelectedItemPosition()!=0 && EstSum.getSelectedItemPosition()!=0 && conMosq.getSelectedItemPosition()!=0){
            Datos.datosLocalizacion.put("DireccionEscrita", textDirE.getText().toString());
            Datos.datosLocalizacion.put("DireccionGenerada", textDirG.getText().toString());
            Datos.datosLocalizacion.put("Longitud", ""+lon);
            Datos.datosLocalizacion.put("Latitud", ""+lat);
            Datos.datosLocalizacion.put("Altitud", ""+0);

            Datos.datosSumidero.put("TipoSustancia", sTipoCriad);
            Datos.datosSumidero.put("ContieneAgua", sContAgua);
            Datos.datosSumidero.put("EstadoSumidero", sEstSum);
            Datos.datosSumidero.put("ContieneMosquitos", sConMosq);
            if(sTipoCriad.equals("Sumidero") && !idSum.getText().toString().isEmpty())
                Datos.datosSumidero.put("idSumidero", idSum.getText().toString());

            Datos.contSumidero++;
            Datos.contSumideroTotal++;

            if(sConMosq.equals("Positivo") && muestreo.isChecked()){
                Datos.contSumidero=0;
                Datos.contSumiderosPositivos++;
                startActivity(new Intent(getApplicationContext(), ConteoSumidero.class));
            }else{
                builder.setTitle(R.string.tit_observacion);
                builder.setMessage(R.string.observacion);
                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialogoObservacion();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Datos.writeUsingXMLSerializer(Datos.registro);
                            Datos.guardarArchivo(getApplicationContext());
                            //Datos.enviarArchivo(Datos.archivo);
                            //Datos.crearArchivo(Datos.archivo, this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Datos.registro++;
                        startActivity(new Intent(getApplicationContext(), ResumenDia.class));
                        dialog.dismiss();
                    }
                });
                builder.show();
            }

            System.out.println(Datos.datosFormulario);
            System.out.println(Datos.datosSumidero);
        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_llenar, Toast.LENGTH_LONG).show();
        }
    }

    public void dialogoObservacion(){
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.dialog_observacion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        Button guard = promptsView.findViewById(R.id.guardar);
        Button canc = promptsView.findViewById(R.id.cancelar);
        final EditText obs = promptsView.findViewById(R.id.observaciones);
        obs.setTextColor(getResources().getColor(R.color.colorPrimary));

        alertDialogBuilder.setView(promptsView);
        dialogoObservacion = alertDialogBuilder.create();

        guard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!obs.getText().toString().isEmpty()) {
                    Datos.datosConteoSumidero.put("Observacion", obs.getText().toString());
                    try {
                        Datos.writeUsingXMLSerializer(Datos.registro);
                        Datos.guardarArchivo(getApplicationContext());
                        //Datos.enviarArchivo(Datos.archivo);
                        //Datos.crearArchivo(Datos.archivo, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Datos.registro++;
                    startActivity(new Intent(getApplicationContext(), ResumenDia.class));
                    dialogoObservacion.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.toast_llenar, Toast.LENGTH_LONG).show();
                }
            }
        });
        canc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                dialogoObservacion.dismiss();
            }
        });
        dialogoObservacion.show();
    }

    public void inicializarComponentes(){
        textDirE = (EditText)findViewById(R.id.textDireccionSumE);
        textDirG = (EditText)findViewById(R.id.textDireccionSumG);
        //textSust = (EditText)findViewById(R.id.textTipoSust);
        //miUbicacion();
        //textDirG.setText(Datos.direccion);

        conteoSum = (TextView)findViewById(R.id.TextContSumidero);
        conteoSum.setText(""+ Datos.contSumidero);

        contAgua = (Spinner)findViewById(R.id.spinnerContAgua);
        EstSum = (Spinner)findViewById(R.id.spinnerEstSum);
        conMosq = (Spinner)findViewById(R.id.SpinnerContMosq);
        tipoCriad = (Spinner)findViewById(R.id.spinner_tipo_criadero);

        idSum = (EditText)findViewById(R.id.idSumidero);
        lblIdSum = (TextView)findViewById(R.id.lblIdSumidero);
        muestreo = (CheckBox)findViewById(R.id.hacerMuestreo);

        idSum.setVisibility(View.INVISIBLE);
        lblIdSum.setVisibility(View.INVISIBLE);
        muestreo.setVisibility(View.INVISIBLE);

        builder = new AlertDialog.Builder(this);
    }

    public void agregarListeners(){
        ArrayAdapter<CharSequence> adapterTC = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                if(tv.getText().equals("Seleccione"))
                    tv.setTextColor(Color.BLACK);
                else
                    tv.setTextColor(Color.WHITE);
                return tv;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                TextView tv = (TextView) super.getDropDownView(position,convertView,parent);
                tv.setTextColor(Color.BLACK);
                return tv;
            }
        };
        adapterTC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterTC.add("Seleccione");
        adapterTC.add("Sumidero");
        adapterTC.add("Llantas");
        adapterTC.add("Tanques");
        adapterTC.add("Inservibles <500ml");
        adapterTC.add("Inservibles >500ml");
        adapterTC.add("Alcantarilla");
        adapterTC.add("Pozo Septico");
        adapterTC.add("Cuneta");
        adapterTC.add("Charca");
        adapterTC.add("Criaderos naturales");
        tipoCriad.setAdapter(adapterTC);

        tipoCriad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sTipoCriad = parentView.getItemAtPosition(position).toString();
                if(!(sTipoCriad.equals("Seleccione"))){
                    tipoCriad.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    tipoCriad.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
                if(sTipoCriad.equals("Sumidero")){
                    idSum.setVisibility(View.VISIBLE);
                    lblIdSum.setVisibility(View.VISIBLE);
                }else {
                    idSum.setVisibility(View.INVISIBLE);
                    lblIdSum.setVisibility(View.INVISIBLE);
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapterCA = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                if(tv.getText().equals("Seleccione"))
                    tv.setTextColor(Color.BLACK);
                else
                    tv.setTextColor(Color.WHITE);
                return tv;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                TextView tv = (TextView) super.getDropDownView(position,convertView,parent);
                tv.setTextColor(Color.BLACK);
                return tv;
            }
        };
        adapterCA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCA.add("Seleccione");
        adapterCA.add("Si");
        adapterCA.add("No");
        contAgua.setAdapter(adapterCA);

        contAgua.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sContAgua = parentView.getItemAtPosition(position).toString();
                if(!(sContAgua.equals("Seleccione"))){
                    contAgua.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    contAgua.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapterES = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                if(tv.getText().equals("Seleccione"))
                    tv.setTextColor(Color.BLACK);
                else
                    tv.setTextColor(Color.WHITE);
                return tv;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                TextView tv = (TextView) super.getDropDownView(position,convertView,parent);
                tv.setTextColor(Color.BLACK);
                return tv;
            }
        };
        adapterES.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterES.add("Seleccione");
        adapterES.add("Intervenido");
        adapterES.add("No intervenido");
        adapterES.add("Seco");
        adapterES.add("Tapado");
        EstSum.setAdapter(adapterES);

        EstSum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sEstSum = parentView.getItemAtPosition(position).toString();
                if(!(sEstSum.equals("Seleccione"))){
                    EstSum.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    EstSum.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapterCM = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                if(tv.getText().equals("Seleccione"))
                    tv.setTextColor(Color.BLACK);
                else
                    tv.setTextColor(Color.WHITE);
                return tv;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                TextView tv = (TextView) super.getDropDownView(position,convertView,parent);
                tv.setTextColor(Color.BLACK);
                return tv;
            }
        };
        adapterCM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCM.add("Seleccione");
        adapterCM.add("Positivo");
        adapterCM.add("Negativo");
        conMosq.setAdapter(adapterCM);

        conMosq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sConMosq = parentView.getItemAtPosition(position).toString();
                if(!(sConMosq.equals("Seleccione"))){
                    conMosq.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    conMosq.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
                if(sConMosq.equals("Positivo")){
                    muestreo.setVisibility(View.VISIBLE);
                }else{
                    muestreo.setVisibility(View.INVISIBLE);
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            direccionGenerada(lat, lon);
            //Toast.makeText(getApplicationContext(), lat+" "+lon, Toast.LENGTH_LONG).show();
            //agregarMarcador(lat, lon);
        }else{
            Toast.makeText(getApplicationContext(), "Loca es nulllll", Toast.LENGTH_LONG).show();
        }

    }

    public void direccionGenerada(double lati, double longi){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lati, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses == null){
                Datos.direccion = "Dirección NO detectada";
                return;
            }
            if(addresses.size() == 0) {
                Datos.direccion = "Dirección NO detectada";
                return;
            }
            Address address = addresses.get(0);
            String direccion = address.getThoroughfare() + " " + address.getSubThoroughfare() + " "+ address.getAdminArea()+", "+address.getCountryName();
            Datos.direccion = direccion;
            textDirG.setText(Datos.direccion);
            //Toast.makeText(this,direccion,Toast.LENGTH_SHORT).show();

        }catch (IOException e){
            e.printStackTrace();
        }



    }

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "NO ub", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Toast.makeText(getApplicationContext(), "SI ub", Toast.LENGTH_LONG).show();
        actualizarUbicacion(location);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,15000,0,locListener);
    }

    @Override
    public void onLocation(ListernerPosicion listener, Location location) {
        actualizarUbicacion(location);
        if(listener.equals(locationListenerGPS)) Log.e(">>>","GPS");
        if(listener.equals(locationListenerNetwork)) Log.e(">>>","Network");
        if(location.hasAccuracy()) {
            if(location.getAccuracy()<30) {
                locationManager.removeUpdates(locationListenerGPS);
                locationManager.removeUpdates(locationListenerNetwork);
                progressDialog.dismiss();
            }
        }
    }
}
