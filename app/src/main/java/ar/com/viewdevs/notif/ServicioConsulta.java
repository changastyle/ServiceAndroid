package ar.com.viewdevs.notif;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class ServicioConsulta extends Service {

    int mStartMode;
    IBinder mBinder;
    boolean mAllowRebind;

    private long ultimoExecute = 0;
    public static int contadorNotificaciones = 1;
    public static final int NOTIFICATION_ID = 543;
    public static boolean isServiceRunning = false;


    @Override
    public void onCreate()
    {
       // startServiceWithNotification();
        //Toast.makeText(this, "SERVICIO CREADO", Toast.LENGTH_LONG).show();
    }


    private void startServiceWithNotification() {
        if (isServiceRunning) return;
        isServiceRunning = true;


        Toast.makeText(this, "CREANDO NOTI", Toast.LENGTH_LONG).show();

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction("XXX");  // A string containing the action name
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText("NOTI..")
                .setSmallIcon(R.drawable.ic_notification)
                //.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, -128, false))
                .setContentIntent(contentPendingIntent)
                .setOngoing(true)
//                .setDeleteIntent(contentPendingIntent)  // if needed
                .build();
        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        startForeground(NOTIFICATION_ID, notification);
    }

    public void generarNotificacion(int id, String header, String subheader, int icono, Context contexto)
    {
        // 1 - NOTIFICATION BUILDER:
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        // 2 - CONFIGURANDO COSAS OBLIGATORIAS DE UNA NOTIFICACION:
        mBuilder.setSmallIcon(icono);
        mBuilder.setContentTitle(String.valueOf(header));
        mBuilder.setContentText(String.valueOf(subheader));


        // 3 - GENERO LA ACCION QUE QUIERO QUE HAGA , CUANDO CLICKEO LA NOTIFICACION:
        /*Intent resultIntent = new Intent(contexto, AccionesDespuesNotificacion.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        stackBuilder.addParentStack(AccionesDespuesNotificacion.class);*/

        // Adds the Intent that starts the Activity to the top of the stack
        /*stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);*/

        // 4 - LE PASO LA NOTIFICACION AL SISTEMA:
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());
    }


    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();


        Date d = new Date();

        long tiempoTranscurrido = 0;
        if(ultimoExecute != 0)
        {
            tiempoTranscurrido = d.getTime() - ultimoExecute;
        }
        ultimoExecute= d.getTime();

        System.out.println("CONSULTANDO MIS VENTAS " + tiempoTranscurrido);

        // 1 - URL
        String url = "http://192.168.5.131:8080/pocasPulgasWS/findTodosLosPedidos";

        // 2 - PARAMETROS:
        List<ParametroJSON> parametros = new ArrayList<ParametroJSON>();

        // 3 - TASK:
        Task task = new Task(url, true, parametros, new Retorno()
        {
            @Override
            public void onProgress()
            {
            }

            @Override
            public void onResult(String result)
            {
                System.out.println("RES FIND PEDIDOS : " + result);

                String header = "Tenes un nuevo pedido!!";
                Date d = new Date();
                String subHeader = "Realizado a las " + d.getHours() +" :" + d.getMinutes() +"hs";
                int icono = R.drawable.ic_notification;
                generarNotificacion(contadorNotificaciones,header,subHeader,icono ,  MainActivity.contexto);
                contadorNotificaciones++;

                /*
                Intent msgIntent = new Intent(MainActivity.contexto, BackgroundService.class);
                msgIntent.putExtra("iteraciones", 10);
                startService(msgIntent);


                /*
                //RECIBER:
                IntentFilter filter = new IntentFilter();
                filter.addAction(BackgroundService.ACTION_PROGRESO);
                filter.addAction(BackgroundService.ACTION_FIN);
                ProgressReceiver rcv = new ProgressReceiver();
                registerReceiver(rcv, filter);*/
            }

            @Override
            public void onCancel()
            {
            }
        });
        task.execute();

        /*
        while(true)
        {
            try
            {




                Thread.sleep(5000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }
        */
        return mStartMode;



    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service STOP", Toast.LENGTH_LONG).show();
    }




    void stopMyService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }






}