package co.edu.icesi.i2t.mosquitos.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import co.edu.icesi.i2t.mosquitos.R;
import co.edu.icesi.i2t.mosquitos.custom.Datos;

public class ResumenDia extends AppCompatActivity {

    private TextView totalPredios;
    private TextView totalCriaderos;
    private TextView totalCriaderosInfectados;
    private TextView totalSumideros;
    private TextView totalSumiderosInfectados;
    private SharedPreferences sharedpreferences;

    int con;
    String retorno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_dia);
        inicializarComponentes();
        cargarInfo();
    }

    public void enviar(View v){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            EnvioDatos envio = new EnvioDatos(getApplicationContext());
            envio.execute();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Datos.USUARIO_ACTUAL, "");
            editor.putString(Datos.NOMBRE_ACTUAL, "");
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), RegistroEntomologico.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_no_inter, Toast.LENGTH_LONG).show();
        }
    }

    public void continuar(View v){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            EnvioDatos envio = new EnvioDatos(getApplicationContext());
            envio.execute();
        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_no_inter, Toast.LENGTH_LONG).show();
        }
        cont();
        Intent intent = new Intent(getApplicationContext(), RegistroEntomologico.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(new Intent(intent));
    }

    @Override
    public void onBackPressed() {

    }

    public void inicializarComponentes() {
        totalPredios = (TextView)findViewById(R.id.TotalPredios);
        totalCriaderos = (TextView)findViewById(R.id.TotalCriaderos);
        totalCriaderosInfectados = (TextView)findViewById(R.id.TotalCriaderosInfectados);
        totalSumideros = (TextView)findViewById(R.id.TotalSumideros);
        totalSumiderosInfectados = (TextView)findViewById(R.id.TotalSumiderosInfectados);
        con = 0;
        sharedpreferences = getSharedPreferences(Datos.PREFERENCIAS, Context.MODE_PRIVATE);
        retorno = "";
        cargarInfo();
    }

    public void cont(){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(Datos.TOTAL_PREDIOS, Datos.contPredio);
        editor.putInt(Datos.TOTAL_CRIADEROS, Datos.contCriaderos);
        editor.putInt(Datos.TOTAL_CRIADEROS_INF, Datos.contCriaderosPositivosAedes);
        editor.putInt(Datos.TOTAL_SUMIDEROS, Datos.contSumideroTotal);
        editor.putInt(Datos.TOTAL_SUMIDEROS_INF, Datos.contSumiderosPositivos);
        Datos.datosHogar.clear();
        Datos.datosSumidero.clear();
        Datos. datosConteoSumidero.clear();
        Datos.datosLocalizacion.clear();
        Datos.datosCriaderos.clear();
        Datos.criaderoActual=-1;
    }

    public void cargarInfo(){
        totalPredios.setText(""+ Datos.contPredio);
        totalCriaderos.setText(""+Datos.contCriaderos);
        totalCriaderosInfectados.setText(""+Datos.contCriaderosPositivosAedes);
        totalSumideros.setText(""+Datos.contSumideroTotal);
        totalSumiderosInfectados.setText(""+Datos.contSumiderosPositivos);
    }

    public class EnvioDatos extends AsyncTask<Void, Void, String> {
        private Context contexto;

        public EnvioDatos(Context contexto){
            this.contexto = contexto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String retorno = Datos.comprobarEnvioDatos(contexto);
            return retorno;
        }

        @Override
        protected void onPostExecute(String retorno) {

            Toast.makeText(getApplicationContext(), retorno, Toast.LENGTH_LONG).show();
        }
    }
}
