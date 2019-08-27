package co.edu.icesi.i2t.mosquitos.custom;

import android.app.Application;

/**
 * Created by Andres Aguirre on 31/10/2017.
 */

public class Sesion extends Application {

    private String cedula;

    private String nombreActual;

    public String getNombreActual() {
        return nombreActual;
    }

    public void setNombreActual(String nombreActual) {
        this.nombreActual = nombreActual;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
}
