package co.edu.icesi.i2t.mosquitos.custom;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Andres Aguirre on 31/10/2017.
 */

public class EnvioAsyncTask extends AsyncTask<Context, Void, String> {

    // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(Context... params) {
        // todo revisar parámetro y retorno. Cambiar el retorno de comprobarEnvioDatos.
        Context contexto = params[0];
        String envio = Datos.comprobarEnvioDatos(contexto);
        /*String githubSearchResults = null;
        try {

            //githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return githubSearchResults;
        */
        return envio;
    }

    @Override
    protected void onPostExecute(String envio) {
        // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
        //mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (envio != null && !envio.equals("")) {

            /* COMPLETED (17) Call showJsonDataView if we have valid, non-null results
            showJsonDataView();
            mSearchResultsTextView.setText(githubSearchResults);
            */
        } else {
            // COMPLETED (16) Call showErrorMessage if the result is null in onPostExecute
            //showErrorMessage();
        }
    }
}