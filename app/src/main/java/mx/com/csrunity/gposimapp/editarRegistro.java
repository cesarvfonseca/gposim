package mx.com.csrunity.gposimapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cesarvfonseca on 21/11/2016.
 */
public class editarRegistro extends AppCompatActivity implements View.OnClickListener {

    EditText etIncialesE, etUnidadE, etNSerie;
    Button btnModificar;
    RadioGroup rgTipoE;
    RadioButton rbmp1E, rbmp2E;

    String IP = "http://www.webdesigns.hol.es/gposim";
    //String IP = "http://www.gruposim.esy.es/gposim";
    String UPDATE = IP + "/actualizar_registro.php";

    private int n=0;//Para maneja la posicion y el cambio de campo a modificar
    String opcion,nserie;

    obtenerWebServiceE hiloConexion;

    String vid, vtipo, viniciales, vunidad, vserie;//Valores para recibir los datos
    String stipo, siniciales, sunidad, sserie;//Valores para enviar los datos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_reg);

        rgTipoE = (RadioGroup) findViewById(R.id.rgTipoE);
        rbmp1E = (RadioButton) findViewById(R.id.rbmp1E);
        rbmp2E = (RadioButton) findViewById(R.id.rbmp2E);

        etIncialesE = (EditText) findViewById(R.id.etInicialesE);
        etUnidadE = (EditText) findViewById(R.id.etUnidadE);
        etNSerie = (EditText) findViewById(R.id.etNserieE);

        btnModificar = (Button) findViewById(R.id.btnModificar);

        btnModificar.setOnClickListener(this);

        recibirDatos();
        asignarDatos();
    }

    void recibirDatos() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {//ver si contiene datos
            vid = (String) extras.get("idE");//Obtengo el id
            vtipo = (String) extras.get("tipoE");//Obtengo el tipo
            viniciales = (String) extras.get("inicialesE");//Obtengo el iniciales
            vunidad = (String) extras.get("unidadE");//Obtengo el unidad
            vserie = (String) extras.get("nserieE");//Obtengo el serie
        }
    }

    void asignarDatos() {
        if (vtipo.equalsIgnoreCase("MP1")) {
            rbmp1E.setChecked(true);
        } else {
            rbmp2E.setChecked(true);
        }
        etIncialesE.setText(viniciales);
        etUnidadE.setText(vunidad);
        etNSerie.setText(vserie);
    }

    void obtenerDatos() {
        if (rgTipoE.getCheckedRadioButtonId()==R.id.rbmp1E)
            stipo="MP1";
        else
            stipo="MP2";

        siniciales=etIncialesE.getText().toString();
        sunidad=etUnidadE.getText().toString();
        sserie=etNSerie.getText().toString();
    }

    void nuevaVentana(){
        Intent vnueva;
        vnueva=new Intent(this,subir_imagen.class);
        nserie=sserie;
        vnueva.putExtra("pos",n);
        vnueva.putExtra("nserie",nserie);
        startActivity(vnueva);
    }

    void vMenu(){
        Intent vnueva;
        vnueva=new Intent(this,MainActivity.class);
        startActivity(vnueva);
    }

    void mensajeConfirmar(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("AVISO!");
        dialogo1.setMessage("Desea modificar la imagenes?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar();
            }
        });
        dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                cancelar();
            }
        });
        dialogo1.show();
    }

    public void aceptar() {
        nuevaVentana();
    }

    public void cancelar(){
        vMenu();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnModificar:
                obtenerDatos();
                hiloConexion = new obtenerWebServiceE();
                hiloConexion.execute(UPDATE, "1",vid,stipo,siniciales,sunidad,sserie);   // Parámetros que recibe doInBackground
                mensajeConfirmar();
                Toast.makeText(this,"Datos modificados!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public class obtenerWebServiceE extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String devuelve = "";
            String cadena = params[0];
            URL url = null;//Donde se encuentra la informacion

            if (params[1] == "1") {//OPCION 5 ACTUALIZAR

                try {
                    HttpURLConnection urlConn;

                    DataOutputStream printout;
                    DataInputStream input;
                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");
                    urlConn.connect();
                    //CREAR OBJETO JSON
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id", params[2]);
                    jsonParam.put("tipo", params[3]);
                    jsonParam.put("iniciales", params[4]);
                    jsonParam.put("unidad", params[5]);
                    jsonParam.put("nserie", params[6]);
                    //Envio de parametros POST
                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();

                    int respuesta = urlConn.getResponseCode();

                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }

                        JSONObject respuestaJSON = new JSONObject((result.toString()));

                        String resultJSON = respuestaJSON.getString("estado");

                        if (resultJSON == "1") {
                            devuelve = "Registro modificado correctamente";
                        } else if (resultJSON == "2") {
                            devuelve = "Modificación fallida";
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return devuelve;

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

    }
}
