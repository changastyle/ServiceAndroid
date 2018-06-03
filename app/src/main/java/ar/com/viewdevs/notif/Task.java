package ar.com.viewdevs.notif;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;


public class Task extends AsyncTask<String, Void, String>
{

    public Retorno retorno;
    private String url;
    private String token;
    private List<ParametroJSON> listadoParametros;
    private ResultadoRest resultado;
    private boolean isGetRequest;

    public Task(String url, boolean isGet , Retorno retorno)
    {
        this.url = url;
        this.isGetRequest = isGet;
        this.listadoParametros = new ArrayList<ParametroJSON>();
        this.retorno = retorno;
    }
    public Task(String url, boolean isGet, List<ParametroJSON> listadoParametros, Retorno retorno)
    {
        this.url = url;
        this.isGetRequest = isGet;
        this.listadoParametros = listadoParametros;
        this.retorno = retorno;
    }


    @Override
    protected String doInBackground(String... baseUrls) {

        publishProgress(null);

        if(url != null)
        {
            if (isGetRequest)
            {
                System.out.println("SOY GET");
                resultado = JSONWS.sendPorGet(url, listadoParametros);
            }
            else
            {
                System.out.println("SOY POST");
                resultado = JSONWS.sendPorPost(url, listadoParametros);
            }
        }

        return null;

    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result)
    {
        ResultadoRest resultadoRest = dameResultado();

        if(resultadoRest != null)
        {
            if(resultadoRest.esArray())
            {
                result = resultadoRest.getJsonArray().toString();
            }
            else if(resultadoRest.esObject())
            {
                result = resultadoRest.getAsJsonObject().toString();
            }
            else
            {
                result = resultadoRest.getAsPrimitiva().toString();
            }

            retorno.onResult(result);
        }
    }

    @Override
    protected void onProgressUpdate(Void...voids )
    {
        retorno.onProgress();
    }




    // GYS:
    public ResultadoRest dameResultado()
    {
    return resultado;
    }
    public boolean isOK()
    {
        boolean ok = false;

        if(resultado != null)
        {
            if (this.dameResultado().getAsReturnCode() == 200)
            {
                ok = true;
            }
        }

        return ok;
    }
    public int getCodigoError()
    {
        int codigoError = -1;

        if(resultado != null)
        {
            codigoError = dameResultado().getAsReturnCode();
        }

        return codigoError ;
    }

    public Task(Retorno retorno)
    {
        this.retorno = retorno;
    }
}