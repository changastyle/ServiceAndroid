package ar.com.viewdevs.notif;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONWS
{
    public static URL dameUrlParaGet(String urlWS, List<ParametroJSON> listaParametros)
    {
        URL url = null;
        String acumuladorParametrosSTR = "";
        try {
            if (listaParametros != null)
            {
                if (listaParametros.size() > 0)
                {
                    int contador = 0;
                    for (ParametroJSON parametro : listaParametros)
                    {
                        if (contador == 0)
                        {
                            acumuladorParametrosSTR += "?" + parametro.getNombreParametro() + "=" + parametro.getValor();
                        }
                        else
                        {
                            acumuladorParametrosSTR += "&" + parametro.getNombreParametro() + "=" + parametro.getValor();
                        }
                        contador++;

                    }
                }

                // B - CREO LA URL PARA GET:
//                urlWS = urlWS.replaceAll("\\+", "%20");
                acumuladorParametrosSTR = acumuladorParametrosSTR.replaceAll(" ", "%20");
                url = new URL(urlWS + acumuladorParametrosSTR);
                System.out.println("WS -> GET(" + url + ") CON " + listaParametros.size()  + " PARAMETROS" );

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return url;
    }
    public static StringBuilder dameUrlParaPOST(String urlWS, List<ParametroJSON> listaParametros)
    {
        StringBuilder infoParaMandar = new StringBuilder();
        if (listaParametros != null) {
            if (listaParametros.size() > 0) {
                int contador = 0;
                for (ParametroJSON parametro : listaParametros)
                {
                    if (contador == 0)
                    {
                        infoParaMandar.append(parametro.getNombreParametro() + "=" + parametro.getValor());
                    }
                    else
                    {
                        infoParaMandar.append("&" + parametro.getNombreParametro() + "=" + parametro.getValor());
                    }
                    contador++;
                }
            }
        }
        System.out.println("WS -> POST(" + urlWS + ") CON " + listaParametros.size()  + " PARAMETROS " + infoParaMandar );

        return infoParaMandar;
    }
    public static ResultadoRest sendData2(String urlWS, List<ParametroJSON> listaParametros, boolean isGet)
    {
        ResultadoRest resultado = null;
        URL url = null;
        String data = "";
        int largo;
        String metodo;
        byte[] bytesAenviar = null;

        try
        {
            // 1 - PONGO LOS PARAMETROS EN LA URL:
            if(isGet)
            {
                //GET:
                metodo = "GET";
                url = dameUrlParaGet(urlWS,listaParametros);
                largo = 0;
            }
            else
            {
                //POST:
                metodo = "POST";
                url = new URL(urlWS);
                bytesAenviar = dameUrlParaPOST(urlWS,listaParametros).toString().getBytes("UTF-8");
                largo = bytesAenviar.length;
            }


            // 2 - CREO LA CONEXION HTTP:
            if(url != null)
            {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                conn.setRequestMethod(metodo);

                    //conn.setRequestProperty("Cookie", "JSESSIONID=" + URLEncoder.encode(MasterController.tokenGlobal, "UTF-8"));

                /*Usuario usuarioLogeado = MasterController.usuarioLogeadoGlobal;
                if(usuarioLogeado != null)
                {
                    String usuarioJson = new Gson().toJson(usuarioLogeado);
                    conn.setRequestProperty("Cookie", MasterController.nombreVariableSessionParaMantenerLogeadoAlUsuario + "=" + URLEncoder.encode(usuarioJson, "UTF-8"));
                }*/
                conn.setRequestProperty("Content-Length",String.valueOf(largo));

                if(! isGet)
                {
                    if(bytesAenviar != null)
                    {
                        conn.getOutputStream().write(bytesAenviar);
                    }
                }


                // 3 - LEO EL ARCHIVO QUE ME DEVUELVE:

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                String linea;
                while ((linea = in.readLine()) != null)
                {
                    data += linea;
                }

                if (data != null && !data.isEmpty())
                {
                    System.out.println("AUTH:" +  conn.getHeaderField("JSESSIONID"));


                    // VERIFICO CON QUE EMPIEZA:
                    if(data.startsWith("[") && data.endsWith("]"))
                    {
                        // ES ARRAY:
                        JsonArray jsonArrayRecibido = new JsonParser().parse(data).getAsJsonArray();
                        resultado = new ResultadoRest(jsonArrayRecibido, null, null, conn.getResponseCode());
                    }
                    else if(data.startsWith("{") && data.endsWith("}"))
                    {
                        // ES JSON OBJECT:
                        JsonObject jsonObjectRecibido = new JsonParser().parse(data).getAsJsonObject();
                        resultado = new ResultadoRest(null, jsonObjectRecibido, null, conn.getResponseCode());
                    }
                    else
                    {
                        //ES PRIMITIVA:
                        Object primitiva = null;
                        String strData = "" + data;
                        System.out.println("DATA: " + data + " - " + strData);
                        if(strData.startsWith("true") || strData.startsWith("false"))
                        {
                            primitiva = new Boolean(strData);
                        }
                        else
                        {
                            primitiva = new String(strData);
                        }

                        resultado = new ResultadoRest(null, null, primitiva,  conn.getResponseCode());
                    }
                }

                //COOKIES:
                /*
                String COOKIES_HEADER = "Set-Cookie";
                java.net.CookieManager msCookieManager = new java.net.CookieManager();
                Map<String, List<String>> headerFields = conn.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                String token = "";
                if (cookiesHeader != null)
                {
                    for (String cookie : cookiesHeader)
                    {
                        System.out.println("COOKIE: " + cookie);
                        token = cookie;
                        //
                        msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                }

                //String token = conn.get(MasterController.nombreVariableSessionParaMantenerLogeadoAlUsuario);
                System.out.println("TOKEN:(" +  url + "):" +  token);*/
            }
        }
        catch (Exception e)
        {
            System.out.println("ERROR NETWORKING: sendData(\"" + urlWS + "\") ->" + e.toString());
            e.printStackTrace();
        }


        return resultado;
    }

    public static String  sendMultipartFile(String urlToSend , Bitmap bitmapAEnviar)
    {
        String response = null;
        String attachmentName = "foto";
        String attachmentFileName = "bitmap.bmp";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        // 1 - Setup the request:


        try
        {
            HttpURLConnection httpUrlConnection = null;
            URL url = null;
            url = new URL(urlToSend);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            //2 - Start content wrapper:
            DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);

            // 3 - Convert Bitmap to ByteBuffer:
            byte[] pixels = new byte[bitmapAEnviar.getWidth() * bitmapAEnviar.getHeight()];
            for (int i = 0; i < bitmapAEnviar.getWidth(); ++i) {
                for (int j = 0; j < bitmapAEnviar.getHeight(); ++j) {
                    //we're interested only in the MSB of the first byte,
                    //since the other 3 bytes are identical for B&W images
                    pixels[i + j] = (byte) ((bitmapAEnviar.getPixel(i, j) & 0x80) >> 7);
                }
            }
            request.write(pixels);

            // 4 - End content wrapper:
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

            // 5 - Flush output buffer:
            request.flush();
            request.close();

            // 6 - Get response:
            InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null)
            {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            response = stringBuilder.toString();

            // 7 - Close response stream:
            responseStream.close();

            // 8 - Close the connection:
            httpUrlConnection.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }
/*
    public static String multipost(String urlString, MultipartEntity reqEntity) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            /*
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
            conn.addRequestProperty(reqEntity.getContentType(), reqEntity.getContentType().getValue());

            OutputStream os = conn.getOutputStream();
            reqEntity.writeTo(conn.getOutputStream());
            os.close();
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return readStream(conn.getInputStream());
            }

        } catch (Exception e) {
            System.out.println("multipart post error " + e + "(" + urlString + ")");
        }
        return null;
    }*/

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }


    public static ResultadoRest sendPorPost(String url, List<ParametroJSON> listadoParametros)
    {
        ResultadoRest resultado = null;

        ResultadoRest resultadoRest = sendData2(url,listadoParametros,false);

        if(resultadoRest != null )
        {
            resultado = resultadoRest;
        }

        return resultado;
    }

    public static ResultadoRest sendPorGet(String url, List<ParametroJSON> listadoParametros)
    {
        ResultadoRest resultado = null;

        ResultadoRest resultadoRest = sendData2(url,listadoParametros,true);

        if(resultadoRest != null )
        {
            resultado = resultadoRest;
        }

        return resultado;
    }
}