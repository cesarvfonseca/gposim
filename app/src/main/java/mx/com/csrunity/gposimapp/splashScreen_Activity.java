package mx.com.csrunity.gposimapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.widget.ProgressBar;

/**
 * Created by cesarvfonseca on 07/03/2016.
 */
public class splashScreen_Activity extends ActionBarActivity {
    public static final int segundo=5;
    public static final int miliSegundos=segundo*1000;
    public static final int delay=2;
    private ProgressBar barra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        barra=(ProgressBar)findViewById(R.id.progressBar);
        barra.setMax(maximoProgreso());
        startanimation();
    }

    public void startanimation(){
        new CountDownTimer(miliSegundos,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                barra.setProgress(establecer_progreso(millisUntilFinished));

            }

            @Override
            public void onFinish() {
                Intent iniLogin = new Intent(splashScreen_Activity.this,MainActivity.class);
                startActivity(iniLogin);
                finish();
            }
        }.start();
    }

    public int establecer_progreso(long miliseconds){
        return (int)((miliSegundos-miliseconds)/1000);
    }

    public int maximoProgreso(){
        return segundo-delay;
    }
}


