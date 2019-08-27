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

public class Predio extends AppCompatActivity implements ListernerPosicion.LocationListener{

    private EditText textDirG;
    private EditText textDirE;
    private EditText textAdu;
    private EditText textNin;

    private TextView textFrecuencia;

    private Spinner propPredio;
    private Spinner protVentanas;
    private Spinner elimResiduos;
    private Spinner condGeneral;
    private Spinner AlmAgua;
    private Spinner acueducto;
    private Spinner frecuencia;

    private String sPropPredio;
    private String sProtVentanas;
    private String sElimResiduos;
    private String sCondGeneral;
    private String sAlmAgua;
    private String sAcueducto;
    private String sFrecuencia;

    private Button VolverResumen;
    private Button botonFinRegis;
    private Button botonAgreCriaderos;

    private double lat;
    private double lon;
    private String address;

    private AlertDialog.Builder builder;
    private AlertDialog dialogoObservacion;
    private boolean tieneObservacion;


    LocationManager locationManager;
    ListernerPosicion locationListenerNetwork;
    ListernerPosicion locationListenerGPS;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predio);
        //Todo faltala ubicación

        inicializarComponentes();
        agregarListeners();
        verificarResumen();



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


    public void finalizar(View v){
        if(verificarInfo()) {
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
                    Datos.registro++;
                    startActivity(new Intent(getApplicationContext(), TabHostController.class));
                    dialog.dismiss();
                }
            });
            builder.show();

            Datos.contPredio++;
        }
    }

    public void criadero(View v){
        if(verificarInfo()){
            Datos.contPredio++;
            startActivity(new Intent(getApplicationContext(), Criaderos.class));
        }
    }

    public void resumenCriaderos(View v){
        if(verificarInfo()) {
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
                    startActivity(new Intent(getApplicationContext(), TabHostController.class));
                    dialog.dismiss();
                }
            });
            builder.show();
        }

    }

    public boolean verificarInfo(){
        boolean retorno = false;
        if(!(textDirE.getText().toString().equals("")) && /*!(textDirG.getText().toString().equals("")) &&*/
                !(textAdu.getText().toString().equals("")) && !(textNin.getText().toString().equals("")) &&
                propPredio.getSelectedItemPosition()!=0 && protVentanas.getSelectedItemPosition()!=0 &&
                elimResiduos.getSelectedItemPosition()!=0 && condGeneral.getSelectedItemPosition()!=0 &&
                AlmAgua.getSelectedItemPosition()!=0 && acueducto.getSelectedItemPosition()!=0){
            if((acueducto.getSelectedItemPosition()==1 && frecuencia.getSelectedItemPosition()!=0) || acueducto.getSelectedItemPosition()>=2) {
                retorno = true;
                Datos.datosLocalizacion.put("DireccionEscrita", textDirE.getText().toString());
                Datos.datosLocalizacion.put("DireccionGenerada", textDirG.getText().toString());
                Datos.datosLocalizacion.put("Longitud", ""+lon);
                Datos.datosLocalizacion.put("Latitud", ""+lat);
                Datos.datosLocalizacion.put("Altitud", ""+0);

                Datos.datosHogar.put("NumeroAdultos", textAdu.getText().toString());
                Datos.datosHogar.put("NumeroNinos", textNin.getText().toString());
                Datos.datosHogar.put("PropositoPredio", sPropPredio);
                Datos.datosHogar.put("ProteccionVentanas", sProtVentanas);
				/*if(sElimResiduos.equals("Sistema Pœblico")){
					Datos.datosHogar.put("EliminacionResiduos", "Sistema Publico");
				}else{*/
                Datos.datosHogar.put("EliminacionResiduos", sElimResiduos);
                //}
                Datos.datosHogar.put("CondicionesCasa", sCondGeneral);
                Datos.datosHogar.put("AlmacenamientoAgua", sAlmAgua);
                Datos.datosHogar.put("Acueducto", sAcueducto);
                Datos.datosHogar.put("Frecuencia", sFrecuencia);

            }else{
                Toast.makeText(getApplicationContext(), R.string.toast_llenar, Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_llenar, Toast.LENGTH_LONG).show();
        }
        return retorno;
    }

    public void verificarResumen(){
        if(Datos.datosHogar.size()>0 /*&& Datos.datosLocalizacion.size()>0*/){

            VolverResumen.setVisibility(View.VISIBLE);
            botonFinRegis.setVisibility(View.INVISIBLE);
            botonAgreCriaderos.setVisibility(View.INVISIBLE);

            String dire = (String)Datos.datosLocalizacion.get("DireccionEscrita");
            textDirE.setText(dire);
            String dir = (String)Datos.datosLocalizacion.get("DireccionGenerada");
            textDirG.setText(dir);
            String na = (String)Datos.datosHogar.get("NumeroAdultos");
            textAdu.setText(na);
            String nn = (String)Datos.datosHogar.get("NumeroNinos");
            textNin.setText(nn);
            String pp = (String)Datos.datosHogar.get("PropositoPredio");
            if(pp.equals("Hogar")){
                propPredio.setSelection(1);
            }else{
                if(pp.equals("Lote")){
                    propPredio.setSelection(2);
                }else{
                    if(pp.equals("Negocio")){
                        propPredio.setSelection(3);
                    }
                }
            }
            String pv = (String)Datos.datosHogar.get("ProteccionVentanas");//no
            if(pv.equals("Angeo")){
                protVentanas.setSelection(1);
            }else{
                if(pv.equals("Cortina")){
                    protVentanas.setSelection(2);
                }else{
                    if(pv.equals("Ninguna")){
                        protVentanas.setSelection(3);
                    }
                }
            }
            String er = (String)Datos.datosHogar.get("EliminacionResiduos");
            if(er.equals("Sistema Publico"))
                elimResiduos.setSelection(1);
            else
            if(er.equals("Cerca a la Vivienda"))
                elimResiduos.setSelection(2);
            String cc = (String)Datos.datosHogar.get("CondicionesCasa");//no
            if(cc.equals("Buena")){
                condGeneral.setSelection(1);
            }else{
                if(cc.equals("Regular")){
                    condGeneral.setSelection(2);
                }else{
                    if(cc.equals("Mala")){
                        condGeneral.setSelection(3);
                    }
                }
            }
            String aa = (String)Datos.datosHogar.get("AlmacenamientoAgua");
            if(aa.equals("Si"))
                AlmAgua.setSelection(1);
            else
                AlmAgua.setSelection(2);
            String a = (String)Datos.datosHogar.get("Acueducto");
            if(a.equals("Acueducto")){/////////////////////////////////////////////////////////////////////////////////////////
                acueducto.setSelection(1);
            }else{
                if(a.equals("Carrotanque")){
                    acueducto.setSelection(2);
                }else{
                    if(a.equals("Motobomba"))
                        acueducto.setSelection(3);
                }
            }

            String f = (String)Datos.datosHogar.get("Frecuencia");
            if(f.equals("Continua")){
                frecuencia.setSelection(1);
            }else{
                if(f.equals("1 vez por semana")){
                    frecuencia.setSelection(2);
                }else{
                    if(f.equals("2 veces por semana")){
                        frecuencia.setSelection(3);
                    }
                }
            }

            String obs = (String)Datos.datosHogar.get("Observacion");
            if(obs!=null){
                if(!obs.isEmpty())
                    tieneObservacion=true;
            }
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
        if(tieneObservacion)
            obs.setText((String)Datos.datosHogar.get("Observacion"));

        guard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!obs.getText().toString().isEmpty()) {
                    Datos.datosHogar.put("Observacion", obs.getText().toString());
                    try {
                        //Datos.writeUsingXMLSerializer(Datos.registro);
                        //Datos.guardarArchivo(getApplicationContext());
                        //Datos.enviarArchivo(Datos.archivo);
                        //Datos.crearArchivo(Datos.archivo, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Datos.registro++;
                    startActivity(new Intent(getApplicationContext(), TabHostController.class));
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
        lat = 0;
        lon = 0;
        tieneObservacion = false;
        address = "";
        builder = new AlertDialog.Builder(this);
        //miUbicacion();
        textDirG = (EditText)findViewById(R.id.textDireccionG);
        textDirE = (EditText)findViewById(R.id.textDireccionE);
        textAdu = (EditText)findViewById(R.id.textAdultos);
        textNin = (EditText)findViewById(R.id.textNinos);


        propPredio = (Spinner)findViewById(R.id.spinnerPropPred);
        protVentanas = (Spinner)findViewById(R.id.SpinnerProtVen);
        elimResiduos = (Spinner)findViewById(R.id.SpinnerElimRes);
        condGeneral = (Spinner)findViewById(R.id.SpinnerCondGen);
        AlmAgua = (Spinner)findViewById(R.id.SpinnerAlmAgua);
        acueducto = (Spinner)findViewById(R.id.SpinnerAcueducto);
        frecuencia = (Spinner)findViewById(R.id.SpinnerFrecuencia);
        textFrecuencia =(TextView)findViewById(R.id.Textfrec);

        botonFinRegis = (Button) findViewById(R.id.buttonFinRegistro);
        botonAgreCriaderos = (Button)findViewById(R.id.buttonAgreCriaderos);
        VolverResumen = (Button)findViewById(R.id.ButtonVolverResumenHogar);

        VolverResumen.setVisibility(View.INVISIBLE);
        botonFinRegis.setVisibility(View.VISIBLE);
        botonAgreCriaderos.setVisibility(View.VISIBLE);

    }

    public void agregarListeners(){
        ArrayAdapter<CharSequence> adapterPP = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
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
        adapterPP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPP.add("Seleccione");
        adapterPP.add("Hogar");
        adapterPP.add("Lote");
        adapterPP.add("Negocio");
        adapterPP.add("Restaurante");
        propPredio.setAdapter(adapterPP);

        //mAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,flowers){


        propPredio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sPropPredio = parentView.getItemAtPosition(position).toString();
                if(!(sPropPredio.equals("Seleccione"))){
                    propPredio.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    propPredio.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapterPV = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
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
        adapterPV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPV.add("Seleccione");
        adapterPV.add("Angeo");
        adapterPV.add("Cortina");
        adapterPV.add("Ninguna");
        protVentanas.setAdapter(adapterPV);

        protVentanas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sProtVentanas = parentView.getItemAtPosition(position).toString();
                if(!(sProtVentanas.equals("Seleccione"))){
                    protVentanas.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    protVentanas.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapterER = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
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
        adapterER.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterER.add("Seleccione");
        adapterER.add("Sistema Publico");
        adapterER.add("Cerca a la Vivienda");
        elimResiduos.setAdapter(adapterER);
        //elimResiduos.setSelection(adapterER.NO_SELECTION);
        elimResiduos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sElimResiduos = parentView.getItemAtPosition(position).toString();
                if(!(sElimResiduos.equals("Seleccione"))){
                    elimResiduos.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    elimResiduos.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapterCC = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
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
        adapterCC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCC.add("Seleccione");
        adapterCC.add("Buena");
        adapterCC.add("Regular");
        adapterCC.add("Mala");
        condGeneral.setAdapter(adapterCC);

        condGeneral.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sCondGeneral = parentView.getItemAtPosition(position).toString();
                if(!(sCondGeneral.equals("Seleccione"))){
                    condGeneral.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    condGeneral.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapterAA = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
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
        adapterAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterAA.add("Seleccione");
        adapterAA.add("Si");
        adapterAA.add("No");
        AlmAgua.setAdapter(adapterAA);

        AlmAgua.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sAlmAgua = parentView.getItemAtPosition(position).toString();
                if(!(sAlmAgua.equals("Seleccione"))){
                    AlmAgua.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    AlmAgua.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapterA = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
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
        adapterA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterA.add("Seleccione");
        adapterA.add("Acueducto");
        adapterA.add("Carrotanque");
        adapterA.add("Motobomba");
        acueducto.setAdapter(adapterA);

        acueducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sAcueducto = parentView.getItemAtPosition(position).toString();
                if(!(sAcueducto.equals("Seleccione"))){
                    acueducto.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    acueducto.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
                if(sAcueducto.equals("Acueducto")){
                    textFrecuencia.setVisibility(View.VISIBLE);
                    frecuencia.setVisibility(View.VISIBLE);
                }else{
                    textFrecuencia.setVisibility(View.INVISIBLE);
                    frecuencia.setVisibility(View.INVISIBLE);
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapterF = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
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
        adapterF.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterF.add("Seleccione");
        adapterF.add("Continua");
        adapterF.add("1 vez por semana");
        adapterF.add("2 veces por semana");
        frecuencia.setAdapter(adapterF);

        frecuencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sFrecuencia = parentView.getItemAtPosition(position).toString();
                if(!(sFrecuencia.equals("Seleccione"))){
                    frecuencia.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    frecuencia.setBackgroundColor(getResources().getColor(R.color.blanco));
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
