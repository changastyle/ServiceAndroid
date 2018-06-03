package ar.com.viewdevs.notif;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ResultadoRest
{
    private JsonArray jsonArray;
    private JsonObject jsonObject;
    private Object primitiva;
    private int returnCode = -1;


    public ResultadoRest(JsonArray jsonArray, JsonObject jsonObject , Object primitiva, int returnCode) {
        this.jsonArray = jsonArray;
        this.jsonObject = jsonObject;
        this.primitiva = primitiva;
        this.returnCode = returnCode;
    }

    //GYS:
    public JsonArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JsonArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JsonObject getAsJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int getAsReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public Object getAsPrimitiva() {
        return primitiva;
    }

    public void setPrimitiva(Object primitiva) {
        this.primitiva = primitiva;
    }

    @Override
    public String toString()
    {
        return "ResultadoRest\n{\n" + " jsonArray = " + jsonArray + "\n jsonObject = " + jsonObject + "\n returnCode = " + returnCode + "\n}\n";
    }

    //DINAMIC:
    public boolean isValid()
    {
        boolean valid = false;


        if(returnCode == 200)
        {
            valid = true;
        }


        return valid;
    }
    public boolean is401()
    {
        boolean si = false;

        if(returnCode == 401)
        {
            si = true;
        }

        return si;
    }


    public boolean esArray()
    {
        boolean respuesta = false;

        if(jsonArray != null)
        {
            respuesta = true;
        }
        return respuesta;
    }
    public boolean esObject()
    {
        boolean respuesta = false;

        if(jsonObject != null)
        {
            respuesta = true;
        }
        return respuesta;
    }



    
}
