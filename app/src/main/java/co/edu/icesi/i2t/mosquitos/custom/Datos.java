package co.edu.icesi.i2t.mosquitos.custom;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import co.edu.icesi.i2t.mosquitos.R;

/**
 * Created by Andres Aguirre on 31/10/2017.
 */

public class Datos {

    public final static String PREFERENCIAS = "datos";
    public final static String USUARIO_ACTUAL = "id_actual";
    public final static String NOMBRE_ACTUAL = "nombre_actual";
    public final static String CEDULAS_PREF = "cedulas";
    public final static String FECHA_DIA = "cedulas";
    public final static String TOTAL_PREDIOS = "total_predios";
    public final static String TOTAL_CRIADEROS = "total_criaderos";
    public final static String TOTAL_CRIADEROS_INF = "total_criaderos_inf";
    public final static String TOTAL_SUMIDEROS = "total_sumideros";
    public final static String TOTAL_SUMIDEROS_INF = "total_sumideros_inf";

    public static final String BASE_URL = "http://190.85.249.99:8082";
    public static String SAVE_REPORT = "http://190.85.249.99:8082/Application/savereport";
    //public final static String BASE_URL = "http://190.90.52.35";

    public static int contSumidero = 0;

    public static int contSumideroTotal = 0;
    public static int contPredio = 0;
    public static int contCriaderos = 0;
    public static int contCriaderosPositivosAedes = 0;
    public static int contSumiderosPositivos = 0;

    public static HashMap datosFormulario = new HashMap();
    public static HashMap datosHogar = new HashMap();
    public static HashMap datosSumidero = new HashMap();
    public static HashMap datosConteoSumidero = new HashMap();
    public static HashMap datosLocalizacion = new HashMap();
    public static HashMap datosCriaderos = new HashMap();
    //public static HashMap temp = new HashMap();
    //public static HashMap criaderoActual = new HashMap();
    public static XmlSerializer xmlSerializer;
    public static StringWriter writer;
    public static int registro=0;//numero de regisros guardados
    public static String direccion="";

    public static String archivo = "";
    public static boolean tipoArchivo = true;// true=hogar --------- false=sumidero

    public static int criaderoActual=-1;
    public static int conteo=1;//conteo de criaderos en los predios
    //public static HashMap datosHogar = new HashMap();
    public static String rutaFile = "/data/data/co.edu.icesi.i2t.mosquitos/files/Hogares.plist";


    public static String leerArchivo(Context context){
        try
        {

            String contenido="";
            String file = "";
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    context.openFileInput("Hogares.plist")));
            contenido = fin.readLine();
            file=contenido;
            while ((contenido = fin.readLine()) != null)   {
                file+=contenido;
                System.out.println (contenido);
            }


            fin.close();
            return file;
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde memoria interna");
        }
        return "";
    }

    public static void guardarArchivo(Context context){
        try
        {
			/*File arch = new File("/data/data/com.paquete.prueba/files/Hogares.plist");
			if(arch.exists())
				arch.delete();*/

            OutputStreamWriter fout=
                    new OutputStreamWriter(
                            context.openFileOutput("Hogares.plist", context.MODE_APPEND));

            fout.write(writer.toString());


            fout.close();
            archivo="";
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }

    //Metodo que comprueba el envio e informa al usuario
    public static String comprobarEnvioDatos(Context context){
        //Todo revisar la ruta y ponerla como String Uri(?)
        File arch = new File(rutaFile);
        if(arch.exists()){
            String info = leerArchivo(context);

            if(info==""){
                //Toast.makeText(context, "No hay mas informacion por enviar!", Toast.LENGTH_LONG).show();
            }else{
                String respuesta = Datos.enviarArchivo(info);
                Log.d("archivo", info);
                Log.d("respuesta", respuesta);
                if(respuesta.startsWith("NOT")){

                    return context.getString(R.string.toast_not);
                    //Toast.makeText(context, context.getString(R.string.toast_not), Toast.LENGTH_LONG).show();
                }
                else{
                    if(respuesta.startsWith("OK")){
                        return context.getString(R.string.toast_ok);
                        //Toast.makeText(context, context.getString(R.string.toast_ok), Toast.LENGTH_LONG).show();

                    }else{
                        return context.getString(R.string.toast_other);
                        //Toast.makeText(context, context.getString(R.string.toast_other), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }else{
            return "No existe archivo";
        }
        return null;
    }

    //Metodo de envio del archivo
    public static String enviarArchivo(String info){

        // Create a new HttpClient and Post Header
        HttpURLConnection connection;
        URL url;
        info+="</dict></plist>";
        try {

			/* Se deberia usar otro Hilo para ejecutar el envio.
			 * Thread thread = new Thread(new Runnable(){
				@Override
				public void run() {
    				try {
        			//Your code goes here
    			} catch (Exception e) {
       			Log.e(TAG, e.getMessage());
    			}
				}
				});
				thread.start();
			 * */

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            //url = new URL("http://200.3.193.31/aedes/savereport");//publica siri
            //url = new URL("http://190.242.23.70/Application/savereport"); //NETGROUP
            //url = new URL("http://201.234.74.135/Application/savereport"); //NETGROUP vieja, ataque
            url = new URL(SAVE_REPORT); //NETGROUP Nueva
            //url = new URL("http://aedes.icesi.edu.co/savereport");//publica local
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // Encode according to application/x-www-form-urlencoded specification
            //String content =     "id=" + URLEncoder.encode ("username") +     "&num=" + URLEncoder.encode ("password") +     "&remember=" + URLEncoder.encode ("on") +     "&output=" + URLEncoder.encode ("xml");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream output;
            try {
                output = connection.getOutputStream();
                PrintWriter print = new PrintWriter(output);
                print.println(info);
                print.flush();
                print.close();
                //Get Response
                InputStream is ;
                Log.i("response", "code="+connection.getResponseCode());
                if(connection.getResponseCode()<=400){
                    is=connection.getInputStream();
                }else{
		              /* error from server */
                    is = connection.getErrorStream();
                }
                // is= connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                Log.v("response", ""+response.toString());

                //registro=0;
                datosHogar.clear();
                datosSumidero.clear();
                datosConteoSumidero.clear();
                datosLocalizacion.clear();
                datosCriaderos.clear();
                File arch = new File(rutaFile);
                if(arch.exists())
                    if(response.toString().startsWith("OK"))
                        arch.delete();
                return response.toString();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        // Http Method becomes POST
        // Try this should be the length of you content.
        // it is not neccessary equal to 48.
        // content.getBytes().length is not neccessarily equal to content.length() if the String contains non ASCII characters.
        //connection.setRequestProperty("Content-Length", info.getBytes());
        // Write body
        return "";
    }

    public static void empezarDocumento() throws Exception {
        xmlSerializer = Xml.newSerializer();
        writer = new StringWriter();

        xmlSerializer.setOutput(writer);
        // start DOCUMENT
        xmlSerializer.startDocument("UTF-8", true);

        xmlSerializer.docdecl(" plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\"");
        xmlSerializer.startTag("", "plist");
        xmlSerializer.attribute("","version", "1.0");

        // open tag: <dict>
        xmlSerializer.startTag("", "dict");

        //return writer.toString();

    }

    public static String writeUsingXMLSerializer(int register) throws Exception {
        //Todo revisar la ruta y ponerla como String Uri(?)
        File arch = new File(rutaFile);
        if(!arch.exists()){
            //if(registro==0){
            empezarDocumento();
        }else{
            //writer.close();
            xmlSerializer = null;
            writer = null;
            xmlSerializer = Xml.newSerializer();
            writer = new StringWriter();
            xmlSerializer.setOutput(writer);
        }
        xmlSerializer.startTag("", "key");

        xmlSerializer.text(""+register);//Contador de registros guardados <----------

        xmlSerializer.endTag("", "key");

        xmlSerializer.startTag("", "dict");

        escribir(datosFormulario, "Registro");
        escribir(datosLocalizacion, "Localizacion");
        if(tipoArchivo){// true=hogar --------- false=sumidero
            escribir(datosHogar, "Predio");
            if(datosCriaderos.size()>0){
                xmlSerializer.startTag("", "key");
                xmlSerializer.text("Criaderos");
                xmlSerializer.endTag("", "key");
                xmlSerializer.startTag("", "dict");
                //escribir(datosCriaderos, "Criaderos");//---------
                Iterator it = datosCriaderos.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String)it.next();
                    Object a = datosCriaderos.get(key);
                    Log.v("atostring",a.getClass().toString());
                    escribir((HashMap)a, key);
                }
                xmlSerializer.endTag("", "dict");
                xmlSerializer.flush();
            }
        }else{
            escribir(datosSumidero, "Sumidero");
            if(datosConteoSumidero.size()>0)
                escribir(datosConteoSumidero, "ConteoSumidero");
        }

        xmlSerializer.endTag("", "dict");
        xmlSerializer.flush();
        return writer.toString();

    }

    public static String terminarDocumento(){
        try {
            xmlSerializer.endTag("", "dict");
            xmlSerializer.endTag("", "plist");

            xmlSerializer.endDocument();

            Log.v("", writer.toString());

            System.out.println(writer.toString());

            return writer.toString();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void escribir(HashMap mapa, String tag){
        try {
            xmlSerializer.startTag("", "key");
            xmlSerializer.text(tag);
            xmlSerializer.endTag("", "key");

            xmlSerializer.startTag("", "dict");
            Log.v("criaderos", ""+datosCriaderos.size());
            Iterator it = mapa.keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                xmlSerializer.startTag("", "key");
                xmlSerializer.text(key);
                xmlSerializer.endTag("", "key");
                xmlSerializer.startTag("", "string");
                Object a = mapa.get(key);
                String val = (String)a;
                xmlSerializer.text(val);
                xmlSerializer.endTag("", "string");
                xmlSerializer.flush();
            }

            xmlSerializer.endTag("", "dict");
            xmlSerializer.flush();
        } catch (IllegalArgumentException e) {
            Log.v("", "Paila1");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.v("", "Paila2");
            e.printStackTrace();
        } catch (IOException e) {
            Log.v("", "Paila3");
            e.printStackTrace();
        }
    }
}
