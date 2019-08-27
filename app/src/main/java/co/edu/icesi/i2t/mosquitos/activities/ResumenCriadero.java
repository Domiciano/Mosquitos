package co.edu.icesi.i2t.mosquitos.activities;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import co.edu.icesi.i2t.mosquitos.R;
import co.edu.icesi.i2t.mosquitos.custom.Datos;

public class ResumenCriadero extends Fragment {

    private TextView tipo;
    private TextView volumen;
    private TextView larCulex;
    private TextView pupCulex;
    private TextView larAedes;
    private TextView pupAedes;

    private Button editar, finalizar;

    private String id;

    private HashMap mapa;

    public ResumenCriadero(){}

    public void setId(String id){
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_resumen_criadero, container, false);
        inicializarComponentes(rootView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void inicializarComponentes(View v){
        tipo = (TextView)v.findViewById(R.id.TextTipo1);
        volumen = (TextView)v.findViewById(R.id.TextVolumen1);
        larCulex = (TextView)v.findViewById(R.id.TextLCulex1);
        pupCulex = (TextView)v.findViewById(R.id.TextPCulex1);
        larAedes = (TextView)v.findViewById(R.id.TextLAedes1);
        pupAedes = (TextView)v.findViewById(R.id.TextPAedes1);
        editar = (Button) v.findViewById(R.id.buttonEditarCriad);
        finalizar = (Button)v.findViewById(R.id.ButtonFinCriad);

        editar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String[] info = id.split(" ");
                Datos.criaderoActual = Integer.parseInt(info[1]);
                Datos.tipoArchivo=true;
                startActivity(new Intent(getActivity().getApplicationContext(), Criaderos.class));
            }
        });

        finalizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String[] info = id.split(" ");
                Datos.criaderoActual = Integer.parseInt(info[1]);
                Datos.tipoArchivo=true;
                try {
                    Datos.writeUsingXMLSerializer(Datos.registro);
                    Datos.guardarArchivo(getActivity().getApplicationContext());
                    //Datos.enviarArchivo(Datos.archivo);
                    //Datos.crearArchivo(Datos.archivo, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Datos.registro++;
                startActivity(new Intent(getActivity().getApplicationContext(), ResumenDia.class));
            }
        });

        cargarInfo();
    }

    public void cargarInfo(){
        //Bundle recibir = this.getIntent().getExtras();
        //id = recibir.getString("idCriadero");
        mapa = (HashMap) Datos.datosCriaderos.get(id);
        if(mapa.size()>0) {
            String a = (String) mapa.get("TipoCriadero");
            String b = (String) mapa.get("Volumen");
            String c = (String) mapa.get("LarvasCulex");
            String d = (String) mapa.get("PupasCulex");
            String e = (String) mapa.get("LarvasAedes");
            String f = (String) mapa.get("PupasAedes");

            tipo.setText(a);
            volumen.setText(b);
            larCulex.setText(c);
            pupCulex.setText(d);
            larAedes.setText(e);
            pupAedes.setText(f);
        }
    }
}
