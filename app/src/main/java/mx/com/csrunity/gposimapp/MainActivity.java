package mx.com.csrunity.gposimapp;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnNuevo,btnConsulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNuevo = (Button)findViewById(R.id.btnNuevo);
        btnConsulta = (Button)findViewById(R.id.btnConsulta);

        validarConexion();

        btnNuevo.setOnClickListener(this);
        btnConsulta.setOnClickListener(this);

    }

    //COMPROBAR CONEXION A INTERNET
    public static boolean compruebaConexion(Context context) {
        boolean connected = false;
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = conn.getAllNetworkInfo();
        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    void validarConexion(){
        if (!compruebaConexion(this)) {
            Toast.makeText(getBaseContext(),"No hay conexión a internet", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    //ACABA COMPROBAR CONEXION A INTERNET



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnNuevo:
                Intent intent=new Intent(MainActivity.this,nuevo_registro.class);
                startActivity(intent);
                break;
            case R.id.btnConsulta:
                Intent iConsulta;
                iConsulta=new Intent(this,consultarDatos.class);
                startActivity(iConsulta);
                break;
            default:
                break;
        }
    }
}
