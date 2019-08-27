package co.edu.icesi.i2t.mosquitos.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import co.edu.icesi.i2t.mosquitos.R;
import co.edu.icesi.i2t.mosquitos.custom.Datos;

public class ConteoSumidero extends AppCompatActivity {

    private EditText pupasCulex;
    private EditText pupasAedes;

    //TextView textPupasCulex;
    //TextView textPupasAedes;

    private Spinner larvasCulex;
    private Spinner larvasAedes;

    private String sLarvasCulex;
    private String sLarvasAedes;

    private AlertDialog.Builder builder;
    private AlertDialog dialogoObservacion;
    private String observacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteo_sumidero);
        inicializarComponentes();
        agregarListeners();
    }

    public void guardarConteo(View v){
        Datos.tipoArchivo=false;
        if(!(pupasCulex.getText().toString().equals("")) && !(pupasAedes.getText().toString().equals("")) &&
                larvasAedes.getSelectedItemPosition()!=0 && larvasCulex.getSelectedItemPosition()!=0){
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
                    continuar();
                    dialog.dismiss();
                    if(sLarvasAedes.equals("Positivo")){
                        Datos.contSumiderosPositivos++;
                    }
                }
            });
            builder.show();
        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_llenar, Toast.LENGTH_LONG).show();
        }
    }

    public void dialogoObservacion(){
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.dialog_observacion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        Button guard = (Button)promptsView.findViewById(R.id.guardar);
        Button canc = (Button) promptsView.findViewById(R.id.cancelar);
        final EditText obs = (EditText) promptsView.findViewById(R.id.observaciones);
        obs.setTextColor(getResources().getColor(R.color.colorPrimary));

        alertDialogBuilder.setView(promptsView);
        dialogoObservacion = alertDialogBuilder.create();

        guard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!obs.getText().toString().isEmpty()) {

                    Datos.datosConteoSumidero.put("Observacion", obs.getText().toString());
                    continuar();
                    dialogoObservacion.dismiss();
                    if(sLarvasAedes.equals("Positivo")){
                        Datos.contSumiderosPositivos++;
                    }
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

    public void continuar(){
        Datos.datosConteoSumidero.put("LarvasCulex", sLarvasCulex);
        Datos.datosConteoSumidero.put("PupasCulex", pupasCulex.getText().toString());
        Datos.datosConteoSumidero.put("LarvasAedes", sLarvasAedes);
        Datos.datosConteoSumidero.put("PupasAedes", pupasAedes.getText().toString());
			/*int val = Integer.parseInt(textPupasAedes.getText().toString());
			Datos.datosConteoSumidero.put("RangoPupasAedes", rango(val));
			int val2 = Integer.parseInt(textPupasCulex.getText().toString());
			Datos.datosConteoSumidero.put("RangoPupasCulex", rango(val2));*/

        try {
            Datos.writeUsingXMLSerializer(Datos.registro);
            Datos.guardarArchivo(getApplicationContext());
            //Datos.crearArchivo(Datos.archivo, this);
            //Datos.enviarArchivo(Datos.archivo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Datos.registro++;
        startActivity(new Intent(getApplicationContext(), ResumenDia.class));
    }

    public void inicializarComponentes(){
        larvasCulex = (Spinner)findViewById(R.id.spinnerLarvasCulex);
        larvasAedes = (Spinner)findViewById(R.id.spinnerLarvasAedes);
        pupasCulex = (EditText) findViewById(R.id.textPupasCulexC);
        pupasAedes = (EditText) findViewById(R.id.textPupasAedesC);
        builder = new AlertDialog.Builder(this);
    }

    public void agregarListeners(){
        ArrayAdapter<CharSequence> adapterLC = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
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
        adapterLC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterLC.add("Seleccione");
        adapterLC.add("Positivo");
        adapterLC.add("Negativo");
        larvasCulex.setAdapter(adapterLC);

        larvasCulex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sLarvasCulex = parentView.getItemAtPosition(position).toString();
                if(!(sLarvasCulex.equals("Seleccione"))){
                    larvasCulex.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    larvasCulex.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
                if(sLarvasCulex.equals("Negativo")){
                    pupasCulex.setEnabled(false);
                    pupasCulex.setText("0");
                }else{
                    pupasCulex.setEnabled(true);
                    pupasCulex.setText("");
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        ArrayAdapter<CharSequence> adapterLA = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item ){
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
        adapterLA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterLA.add("Seleccione");
        adapterLA.add("Positivo");
        adapterLA.add("Negativo");
        larvasAedes.setAdapter(adapterLA);

        larvasAedes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sLarvasAedes = parentView.getItemAtPosition(position).toString();
                if(!(sLarvasAedes.equals("Seleccione"))){
                    larvasAedes.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    larvasAedes.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
                if(sLarvasAedes.equals("Negativo")){
                    pupasAedes.setEnabled(false);
                    pupasAedes.setText("0");
                }else{
                    pupasAedes.setEnabled(true);
                    pupasAedes.setText("");
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }
}
