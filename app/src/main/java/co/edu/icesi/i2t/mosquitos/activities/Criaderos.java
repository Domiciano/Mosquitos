package co.edu.icesi.i2t.mosquitos.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import co.edu.icesi.i2t.mosquitos.R;
import co.edu.icesi.i2t.mosquitos.custom.Datos;

public class Criaderos extends AppCompatActivity {

    private float vol;

    private EditText volumen;
    private EditText pupasCulex;
    private EditText pupasAedes;

    //SeekBar seekCulex;
    //SeekBar seekAedes;

    //TextView textPupasCulex;
    //TextView textPupasAedes;

    private Button butVolvRes;
    private Button botonFinalizarRegistro;
    private Button botonOtroCriadero;

    private Spinner tipoCriadero;
    private Spinner larvasCulex;
    private Spinner larvasAedes;

    private String sLarvasCulex;
    private String sLarvasAedes;
    private String sTipoCriadero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criaderos);

        inicializarComponentes();
        agregarListeners();
        verificarResumen();
    }

    public void finalizar(View v){
        if(verificarInfo()) {
            Datos.contCriaderos++;
            if (sLarvasAedes.equals("Positivo") || Integer.parseInt(pupasAedes.getText().toString()) > 0) {
                Datos.contCriaderosPositivosAedes++;
            }
            Datos.conteo = Datos.datosCriaderos.size();
            // TODO iniciar tab activity de resumen
            startActivity(new Intent(getApplicationContext(), TabHostController.class));
        }

    }

    public void otroCriadero(View v){
        if(verificarInfo()){
            Datos.contCriaderos++;
            if(sLarvasAedes.equals("Positivo") || Integer.parseInt(pupasAedes.getText().toString())>0){
                Datos.contCriaderosPositivosAedes++;
            }
            Datos.conteo++;
            startActivity(new Intent(getApplicationContext(), Criaderos.class));
        }

    }

    public void resumen(View v){
        if(Datos.criaderoActual==-2){
            Datos.criaderoActual = Datos.datosCriaderos.size();
            if(verificarInfo()){
                startActivity(new Intent(getApplicationContext(), TabHostController.class));
            }
        }else {
            if (!(pupasCulex.getText().toString().equals("")) && !(pupasAedes.getText().toString().equals("")) && !(volumen.getText().toString().equals(""))) {
                Datos.datosCriaderos.remove("Conteo " + Datos.criaderoActual);
                HashMap temp = new HashMap();
                temp.put("LarvasCulex", sLarvasCulex);
                temp.put("PupasCulex", pupasCulex.getText().toString());
                temp.put("LarvasAedes", sLarvasAedes);
                temp.put("PupasAedes", pupasAedes.getText().toString());
                temp.put("TipoCriadero", sTipoCriadero);
                temp.put("Volumen", volumen.getText().toString());

                Datos.datosCriaderos.put("Conteo " + Datos.criaderoActual, temp);
                startActivity(new Intent(getApplicationContext(), TabHostController.class));
            } else {
                Toast.makeText(getApplicationContext(), R.string.toast_llenar, Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean verificarInfo(){
        boolean validar = false;
        if(!(pupasCulex.getText().toString().equals("")) && !(pupasAedes.getText().toString().equals("")) && !(volumen.getText().toString().equals("")) &&
                tipoCriadero.getSelectedItemPosition()!=0 && larvasCulex.getSelectedItemPosition()!=0 && larvasAedes.getSelectedItemPosition()!=0){
            HashMap temp = new HashMap();
            validar = true;
            temp.put("LarvasCulex", sLarvasCulex);
            temp.put("PupasCulex", pupasCulex.getText().toString());
            temp.put("LarvasAedes", sLarvasAedes);
            temp.put("PupasAedes", pupasAedes.getText().toString());
            temp.put("TipoCriadero", sTipoCriadero);
            temp.put("Volumen", volumen.getText().toString());
			/*int val = Integer.parseInt(textPupasAedes.getText().toString());
			temp.put("RangoPupasAedes", rango(val));
			int val2 = Integer.parseInt(textPupasCulex.getText().toString());
			temp.put("RangoPupasCulex", rango(val2));*/

            Datos.datosCriaderos.put("Conteo "+Datos.conteo, temp);
        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_llenar, Toast.LENGTH_LONG).show();
        }
        Log.v("antes",""+Datos.datosCriaderos.size());
        //Datos.temp.clear();
        return validar;
    }

    public void inicializarComponentes(){
        volumen = (EditText)findViewById(R.id.textVolumen);
        pupasCulex = (EditText)findViewById(R.id.textPupasCulexH);
        pupasAedes = (EditText)findViewById(R.id.textPupasAedesH);

        //textPupasCulex = (TextView)findViewById(R.id.TextViewPupasCulexH);
        //textPupasAedes = (TextView)findViewById(R.id.TextViewPupasAedesH);

        larvasCulex = (Spinner)findViewById(R.id.spinnerPupasCulexH);//spinnerPupasCulexH=larvasculex
        larvasAedes = (Spinner)findViewById(R.id.spinnerLarvasAedesH);
        tipoCriadero = (Spinner)findViewById(R.id.spinnerLarvasCulexH);//spinnerLarvasCulexH=tipocriadero

        //seekCulex = (SeekBar)findViewById(R.id.seekBarPupasCulexH);
        //seekAedes = (SeekBar)findViewById(R.id.seekBarPupasAedesH);

        vol=0;
        botonFinalizarRegistro = (Button)findViewById(R.id.buttonFinalizarHogar);
        botonOtroCriadero = (Button)findViewById(R.id.ButtonOtroCriadero);
        butVolvRes = (Button)findViewById(R.id.ButtonResumenCriad);

        butVolvRes.setVisibility(View.INVISIBLE);
        botonFinalizarRegistro.setVisibility(View.VISIBLE);
        botonOtroCriadero.setVisibility(View.VISIBLE);

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
                    if(Datos.criaderoActual>0){
                        HashMap mapa = (HashMap)Datos.datosCriaderos.get("Conteo "+Datos.criaderoActual);
                        String pCulex = (String)mapa.get("PupasCulex");
                        pupasCulex.setText(pCulex);
                    }
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
                }else {
                    larvasAedes.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
                if(sLarvasAedes.equals("Negativo")){
                    pupasAedes.setEnabled(false);
                    pupasAedes.setText("0");
                }else{
                    pupasAedes.setEnabled(true);
                    pupasAedes.setText("");
                    if(Datos.criaderoActual>0){
                        HashMap mapa = (HashMap)Datos.datosCriaderos.get("Conteo "+Datos.criaderoActual);
                        String pAedes = (String)(String)mapa.get("PupasAedes");
                        pupasAedes.setText(pAedes);
                    }
                }


            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

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
        adapterTC.add("Tanques Altos");
        adapterTC.add("Tanques Bajos");
        adapterTC.add("Llanta");
        adapterTC.add("Tina");
        adapterTC.add("Florero");
        adapterTC.add("Mata en Agua");
        adapterTC.add("Tarros, Latas");
        adapterTC.add("Criaderos Naturales");
        adapterTC.add("Diversos <= 500ml");
        adapterTC.add("Diversos > 500ml");
        tipoCriadero.setAdapter(adapterTC);

        tipoCriadero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                sTipoCriadero = parentView.getItemAtPosition(position).toString();
                if(!(sTipoCriadero.equals("Seleccione"))){
                    tipoCriadero.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    tipoCriadero.setBackgroundColor(getResources().getColor(R.color.blanco));
                }
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        Datos.conteo=Datos.datosCriaderos.size();
    }

    public void  verificarResumen(){
        if(Datos.criaderoActual==-2){
            butVolvRes.setVisibility(View.VISIBLE);
            botonFinalizarRegistro.setVisibility(View.INVISIBLE);
            botonOtroCriadero.setVisibility(View.INVISIBLE);
        }else {
            if (Datos.criaderoActual != -1) {
                butVolvRes.setVisibility(View.VISIBLE);
                botonFinalizarRegistro.setVisibility(View.INVISIBLE);
                botonOtroCriadero.setVisibility(View.INVISIBLE);
                HashMap mapa = (HashMap) Datos.datosCriaderos.get("Conteo " + Datos.criaderoActual);

                String lara = (String) mapa.get("LarvasAedes");
                if (lara.equals("Positivo"))
                    larvasAedes.setSelection(1);
                else
                    larvasAedes.setSelection(2);
                String larc = (String) mapa.get("LarvasCulex");
                if (larc.equals("Positivo"))
                    larvasCulex.setSelection(1);
                else
                    larvasCulex.setSelection(2);
                //seekCulex.setProgress(Integer.parseInt(pCulex));
                //seekAedes.setProgress(Integer.parseInt(pAedes));
                String volu = (String) mapa.get("Volumen");
                vol = Float.parseFloat(volu);
                volumen.setText("" + vol);
                String type = (String) mapa.get("TipoCriadero");
                tipoCriadero.setSelection(posicionTipo(type));
                pupasAedes.setText((String)mapa.get("PupasAedes"));
                pupasCulex.setText((String)mapa.get("PupasCulex"));
            }
        }
    }

    public int posicionTipo(String tipo){
        if(tipo.equals("Tanques Altos"))
            return 1;
        else
        if(tipo.equals("Tanques Bajos"))
            return 2;
        else
        if(tipo.equals("Llanta"))
            return 3;
        else
        if(tipo.equals("Tina"))
            return 4;
        else
        if(tipo.equals("Florero"))
            return 5;
        else
        if(tipo.equals("Mata en Agua"))
            return 6;
        else
        if(tipo.equals("Tarros, Latas"))
            return 7;
        else
        if(tipo.equals("Criaderos Naturales"))
            return 8;
        else
        if(tipo.equals("Diversos <= 500ml"))
            return 9;
        else
        if(tipo.equals("Diversos > 500ml"))
            return 10;
        return 0;
    }

    public String rango(int entero){
        String retorno = "";
        if (entero<31) {
            retorno = "Z";
        }else{
            if(entero>31 && entero<51){
                retorno="A";
            }else{
                if(entero>50 && entero<71){
                    retorno="B";
                }else{
                    if(entero>70 && entero<101){
                        retorno="C";
                    }else{
                        if (entero>100){
                            retorno="D";
                        }
                    }
                }
            }
        }
        return retorno;
    }
}
