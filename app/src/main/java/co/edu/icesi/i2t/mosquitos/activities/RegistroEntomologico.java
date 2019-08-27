package co.edu.icesi.i2t.mosquitos.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.edu.icesi.i2t.mosquitos.R;
import co.edu.icesi.i2t.mosquitos.custom.Datos;
import co.edu.icesi.i2t.mosquitos.custom.Sesion;

public class RegistroEntomologico extends AppCompatActivity {

    private AlertDialog.Builder builder;
    private String userAct;
    private String nomAct;

    private AutoCompleteTextView textMuni;
    private EditText textBarr;
    private TextView nombreUsuario;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_entomologico);

        Log.e(">>>",""+Datos.SAVE_REPORT);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            EnvioDatos envio = new EnvioDatos(getApplicationContext());
            envio.execute();
        }
        builder = new AlertDialog.Builder(this);
        sharedPreferences = getSharedPreferences(Datos.PREFERENCIAS, Context.MODE_PRIVATE);

        textMuni = (AutoCompleteTextView)findViewById(R.id.textMunicipio);
        textBarr = (EditText)findViewById(R.id.textBarrio);
        nombreUsuario = (TextView) findViewById(R.id.nombreUsuario);

        userAct = ((Sesion)getApplicationContext()).getCedula();
        nomAct = ((Sesion)getApplicationContext()).getNombreActual();

        if (userAct != null && !userAct.isEmpty() && nomAct != null && !nomAct.isEmpty()) {
            nombreUsuario.setText(getText(R.string.txt_bienvenida)+"  "+nomAct);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.municipios));
        textMuni.setAdapter(adapter);

        cargarInfo();
    }

    public void predio(View v){
        if(!textMuni.getText().toString().isEmpty() && !textBarr.getText().toString().isEmpty()) {
            Datos.datosFormulario.clear();
            Datos.datosFormulario.put("Cedula", userAct);
            Datos.datosFormulario.put("Comuna", textMuni.getText().toString());
            Datos.datosFormulario.put("Barrio", textBarr.getText().toString());
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy 'T' HH:mm");
            String fecha = formato.format(new Date());
            Datos.datosFormulario.put("Fecha", fecha);
            startActivity(new Intent(getApplicationContext(), Predio.class));
        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_llenar, Toast.LENGTH_LONG).show();
        }
    }

    public void externo(View v){
        if(!textMuni.getText().toString().isEmpty() && !textBarr.getText().toString().isEmpty()) {
            Datos.datosFormulario.clear();
            Datos.datosFormulario.put("Cedula", userAct);
            Datos.datosFormulario.put("Comuna", textMuni.getText().toString());
            Datos.datosFormulario.put("Barrio", textBarr.getText().toString());
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy 'T' HH:mm");
            String fecha = formato.format(new Date());
            Datos.datosFormulario.put("Fecha", fecha);
            startActivity(new Intent(getApplicationContext(), CriaderosExternos.class));
        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_llenar, Toast.LENGTH_LONG).show();
        }
    }

    public void cargarInfo(){
        if(Datos.datosFormulario.size()>0){
            String a = (String)Datos.datosFormulario.get("Cedula");
            String b = (String)Datos.datosFormulario.get("Comuna");
            String c = (String)Datos.datosFormulario.get("Barrio");

            textMuni.setText(b);
            textBarr.setText(c);
        }
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
            if(!retorno.startsWith("No ex"))
                Toast.makeText(getApplicationContext(), retorno, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.cerrarSesion) {
            builder.setTitle(R.string.cerrarSesion);
            builder.setMessage(R.string.msjCerrar);
            builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Datos.USUARIO_ACTUAL, "");
                    editor.putString(Datos.NOMBRE_ACTUAL, "");
                    editor.commit();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
