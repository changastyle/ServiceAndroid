package ar.com.viewdevs.notif;

/**
 * Created by ngrossi on 11/5/2018.
 */

public interface Retorno
{
    public void onProgress();
    public void onResult(String result);
    public void onCancel();
}
