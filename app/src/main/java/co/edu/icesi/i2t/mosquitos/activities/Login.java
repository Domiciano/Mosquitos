package co.edu.icesi.i2t.mosquitos.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import co.edu.icesi.i2t.mosquitos.R;
import co.edu.icesi.i2t.mosquitos.custom.Datos;
import co.edu.icesi.i2t.mosquitos.custom.Sesion;

public class Login extends AppCompatActivity {

    private AutoCompleteTextView cedula;
    private SharedPreferences sharedpreferences;

    private List<String> cedulas;
    private String dataJSON;
    private CheckBox modo_pruebas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        modo_pruebas = findViewById(R.id.modo_pruebas);
        if(Datos.SAVE_REPORT.equals("http://i2thub.icesi.edu.co/test/Application/savereport")){
            modo_pruebas.setChecked(true);
        }else if(Datos.SAVE_REPORT.equals("http://190.85.249.99:8082/Application/savereport")){
            modo_pruebas.setChecked(false);
        }

        modo_pruebas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean mode) {
                if(mode){
                    Datos.SAVE_REPORT = "http://i2thub.icesi.edu.co/test/Application/savereport";
                }else{
                    Datos.SAVE_REPORT = "http://190.85.249.99:8082/Application/savereport";
                }
            }
        });

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                0);


        dataJSON = "";
        cedula = (AutoCompleteTextView)findViewById(R.id.cedula);
        sharedpreferences = getSharedPreferences(Datos.PREFERENCIAS, Context.MODE_PRIVATE);
        cedulas = new ArrayList<>();
        Log.d("antes", dataJSON);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            getCedulas();
        }else{
            String listaCedulas = sharedpreferences.getString(Datos.CEDULAS_PREF, null);
            if (listaCedulas != null && !listaCedulas.equals("")) {
                cedulas = cargarCedulas(listaCedulas);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cedulas);
                cedula.setAdapter(adapter);
            }
            Toast.makeText(getApplicationContext(), R.string.toast_no_inter, Toast.LENGTH_LONG).show();
        }
        Log.d("dsps", dataJSON);

        String nombre = sharedpreferences.getString(Datos.NOMBRE_ACTUAL, null);
        String cedul = sharedpreferences.getString(Datos.USUARIO_ACTUAL, null);
        String fecha = sharedpreferences.getString(Datos.FECHA_DIA, null);
        if (nombre != null && !nombre.equals("")) {
            ((Sesion) getApplicationContext()).setCedula(cedul);
            ((Sesion) getApplicationContext()).setNombreActual(nombre);

            if(fecha!=null) {
                if (!fecha.equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date()))) {
                    Datos.contSumideroTotal = 0;
                    Datos.contPredio = 0;
                    Datos.contCriaderos = 0;
                    Datos.contCriaderosPositivosAedes = 0;
                    Datos.contSumiderosPositivos = 0;
                }
            } else {
                Datos.contSumideroTotal = sharedpreferences.getInt(Datos.TOTAL_SUMIDEROS, 0);
                Datos.contPredio = sharedpreferences.getInt(Datos.TOTAL_PREDIOS, 0);
                Datos.contCriaderos = sharedpreferences.getInt(Datos.TOTAL_CRIADEROS, 0);
                Datos.contCriaderosPositivosAedes = sharedpreferences.getInt(Datos.TOTAL_CRIADEROS_INF, 0);
                Datos.contSumiderosPositivos = sharedpreferences.getInt(Datos.TOTAL_SUMIDEROS_INF, 0);
            }
            startActivity(new Intent(getApplicationContext(), RegistroEntomologico.class));
            finish();
        }
    }

    public void iniciarSesion(View v){
        if(!cedula.getText().toString().isEmpty() && cedulas.size()>0){

            if(cedulas.contains(cedula.getText().toString())) {
                String[] numCed = cedula.getText().toString().trim().split("-");
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Datos.USUARIO_ACTUAL, numCed[0]);
                editor.putString(Datos.NOMBRE_ACTUAL, numCed[1]);
                editor.putString(Datos.TOTAL_PREDIOS, "0");
                editor.putString(Datos.TOTAL_CRIADEROS, "0");
                editor.putString(Datos.TOTAL_CRIADEROS_INF, "0");
                editor.putString(Datos.TOTAL_SUMIDEROS, "0");
                editor.putString(Datos.TOTAL_SUMIDEROS_INF, "0");
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                editor.putString(Datos.FECHA_DIA, format.format(new Date()));
                Datos.contSumideroTotal = 0;
                Datos.contPredio = 0;
                Datos.contCriaderos = 0;
                Datos.contCriaderosPositivosAedes = 0;
                Datos.contSumiderosPositivos = 0;
                editor.commit();
                ((Sesion) getApplicationContext()).setCedula(numCed[0]);
                ((Sesion) getApplicationContext()).setNombreActual(numCed[1]);
                startActivity(new Intent(getApplicationContext(), RegistroEntomologico.class));
            }else{
                Toast.makeText(getApplicationContext(), R.string.toast_cedula, Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_conex, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link }) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link GithubQueryTask}
     */
    private void getCedulas() {
        URL githubSearchUrl;
        try {
            githubSearchUrl = new URL(Datos.BASE_URL+"/getCedulas");
            new GithubQueryTask().execute(githubSearchUrl);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    private List<String> cargarCedulas(String respuesta){
        JSONObject json;
        List<String> cedRec = new ArrayList<String>();
        try {
            json = new JSONObject(respuesta);
            JSONArray elem = json.getJSONArray("elements");
            for (int i = 0; i < elem.length(); i++) {
                JSONObject mJsonObjectProperty = elem.getJSONObject(i);
                String ced = mJsonObjectProperty.getString("cedula");
                cedRec.add(ced);
            }
            //cedulas = cedRec;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return cedRec;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public class GithubQueryTask extends AsyncTask<URL, Void, String> {

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String serverCedulas = null;
            try {
                serverCedulas = getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverCedulas;
        }

        @Override
        protected void onPostExecute(String serverCedulas) {
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (serverCedulas != null && !serverCedulas.equals("")) {
                // COMPLETED (17) Call showJsonDataView if we have valid, non-null results
                //showJsonDataView();
                //mSearchResultsTextView.setText(githubSearchResults);
                cedulas = cargarCedulas(serverCedulas);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, cedulas);
                cedula.setAdapter(adapter);
                dataJSON = serverCedulas;
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Datos.CEDULAS_PREF, serverCedulas);
                editor.commit();
                //Log.d("respuesta", githubSearchResults);
            } else {
                // COMPLETED (16) Call showErrorMessage if the result is null in onPostExecute
                //showErrorMessage();
                Toast.makeText(getApplicationContext(), "Paila", Toast.LENGTH_LONG).show();
            }
        }
    }
}
