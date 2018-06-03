package ar.com.viewdevs.notif;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button btnIniciar;
    private Button btnFinService;
    public static Context contexto;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contexto = this;

        btnIniciar = (Button) findViewById(R.id.btnIniciar);btnIniciar.setOnClickListener(this);
        btnFinService = (Button) findViewById(R.id.btnFinService);btnFinService.setOnClickListener(this);
    }
    public void startService(View view)
    {
        System.out.println("INICIANDO SERVICE CONSULTA");

        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, ServicioConsulta.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        int interval = 8000;
        AlarmManager manager = (AlarmManager) MainActivity.contexto.getSystemService(Context.ALARM_SERVICE);
       //manager.set
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        /*AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60*1000, pintent);
        **/


        //startService(new Intent(getBaseContext(), ServicioConsulta.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), ServicioConsulta.class));
    }

    @Override
    public void onClick(View presionado)
    {
        if(presionado.getId() == btnIniciar.getId() )
        {
            startService(presionado);
        }
        if(presionado.getId() == btnFinService.getId() )
        {
            stopService(presionado);
        }
    }
}
