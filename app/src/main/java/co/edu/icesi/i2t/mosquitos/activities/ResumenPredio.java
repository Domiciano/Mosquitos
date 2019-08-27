package co.edu.icesi.i2t.mosquitos.activities;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import co.edu.icesi.i2t.mosquitos.R;
import co.edu.icesi.i2t.mosquitos.custom.Datos;

public class ResumenPredio extends Fragment {

    private TextView proposito;
    private TextView barrio;
    private TextView comuna;
    private TextView direccion;
    private TextView ninos;
    private TextView adultos;
    private TextView proteccion;
    private TextView eliminacion;
    private TextView condiciones;
    private TextView almacenamiento;
    private TextView acueducto;
    private TextView frecuencia;

    private Button finalizar, editar, agregar;

    public ResumenPredio(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_resumen_predio, container, false);
        inicializarComponentes(rootView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void inicializarComponentes(View v){
        proposito = (TextView)v.findViewById(R.id.tProposito);
        barrio = (TextView)v.findViewById(R.id.tBarrio);
        comuna = (TextView)v.findViewById(R.id.tComuna);
        direccion = (TextView)v.findViewById(R.id.tDireccion);
        ninos = (TextView)v.findViewById(R.id.tNinos);
        adultos = (TextView)v.findViewById(R.id.tAdultos);
        proteccion = (TextView)v.findViewById(R.id.tProteccion);
        eliminacion = (TextView)v.findViewById(R.id.tResiduos);
        condiciones = (TextView)v.findViewById(R.id.tCondiciones);
        almacenamiento = (TextView)v.findViewById(R.id.tAlmacenamiento);
        acueducto = (TextView)v.findViewById(R.id.tAcueducto);
        frecuencia = (TextView)v.findViewById(R.id.tFrecuencia);
        editar = (Button) v.findViewById(R.id.buttonEditarHogar);
        finalizar = (Button)v.findViewById(R.id.buttonSiguiente);
        agregar = (Button)v.findViewById(R.id.buttonAgregarOtroCiradero);

        editar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Datos.tipoArchivo=true;
                startActivity(new Intent(getActivity().getApplicationContext(), Predio.class));
            }
        });

        finalizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Datos.tipoArchivo=true;
                Datos.registro++;
                try {
                    Datos.writeUsingXMLSerializer(Datos.registro);
                    Datos.guardarArchivo(getActivity().getApplicationContext());
                    //Datos.enviarArchivo(Datos.archivo);
                    //Datos.crearArchivo(Datos.archivo, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //startActivity(new Intent(getActivity().getApplicationContext(), ResumenDia.class));

                Intent in = new Intent(getActivity().getApplicationContext(), ResumenDia.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }
        });

        agregar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Datos.tipoArchivo=true;
                Datos.criaderoActual=-2;
                startActivity(new Intent(getActivity().getApplicationContext(), Criaderos.class));
            }
        });

        cargarInfo();
    }

    public void cargarInfo(){
        if(Datos.datosHogar.size()>0 && Datos.datosFormulario.size()>0 /*&& Datos.datosLocalizacion.size()>0*/){

            String a = (String)Datos.datosHogar.get("PropositoPredio");
            String b = (String)Datos.datosFormulario.get("Barrio");
            String c = (String)Datos.datosFormulario.get("Comuna");
            String d = (String)Datos.datosLocalizacion.get("DireccionEscrita");
            String e = (String)Datos.datosHogar.get("NumeroNinos");
            String f = (String)Datos.datosHogar.get("NumeroAdultos");
            String g = (String)Datos.datosHogar.get("ProteccionVentanas");
            String h = (String)Datos.datosHogar.get("EliminacionResiduos");
            String i = (String)Datos.datosHogar.get("CondicionesCasa");
            String j = (String)Datos.datosHogar.get("AlmacenamientoAgua");
            String k = (String)Datos.datosHogar.get("Acueducto");
            String l = (String)Datos.datosHogar.get("Frecuencia");

            proposito.setText(a);
            barrio.setText(b);
            comuna.setText(c);
            direccion.setText(d);
            ninos.setText(e);
            adultos.setText(f);
            proteccion.setText(g);
            eliminacion.setText(h);
            condiciones.setText(i);
            almacenamiento.setText(j);
            acueducto.setText(k);
            if(k.equals("Acueducto")){
                frecuencia.setText(l);
            }else{
                frecuencia.setText("No aplica");
            }
        }
    }
}
