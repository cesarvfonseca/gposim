package mx.com.csrunity.gposimapp;

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
 * Created by Ximena on 17/11/2016.
 */
public class nuevo_registro extends AppCompatActivity implements View.OnClickListener {

    RadioGroup radioGroup;
    RadioButton rbmp1,rbmp2;
    EditText etIniciales,etUnidad,etNserie;
    Button btnGuardar;

    //IP del host
    String IP = "http://www.webdesigns.hol.es/gposim";
    //String IP = "http://www.gruposim.esy.es/gposim";
    String INSERT = IP + "/insertar_registro.php";

    private int n=0;//Para maneja la posicion y el cambio de campo a modificar

    String opcion,nserie;

    obtenerWebService hiloConexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_reg);

        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        rbmp1=(RadioButton)findViewById(R.id.rbmp1);
        rbmp2=(RadioButton)findViewById(R.id.rbmp2);

        etIniciales=(EditText)findViewById(R.id.etIniciales);
        etUnidad=(EditText)findViewById(R.id.etUnidad);
        etNserie=(EditText)findViewById(R.id.etNserie);

        btnGuardar = (Button)findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(this);
    }

    void nuevaVentana(){
        Intent vnueva;
        vnueva=new Intent(this,subir_imagen.class);
        nserie=etNserie.getText().toString();
        vnueva.putExtra("pos",n);
        vnueva.putExtra("nserie",nserie);
        startActivity(vnueva);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGuardar:
                //Obtener tipo de mant
                if (radioGroup.getCheckedRadioButtonId()==R.id.rbmp1)
                    opcion="MP1";
                else
                    opcion="MP2";
                hiloConexion = new obtenerWebService();
                hiloConexion.execute(INSERT,"1",opcion,etIniciales.getText().toString(),etUnidad.getText().toString(),etNserie.getText().toString());   // Parámetros que recibe doInBackground
                Toast.makeText(this, "Registro Guardado!", Toast.LENGTH_SHORT).show();
                nuevaVentana();
                break;
            default:
                break;
        }
    }

    public class obtenerWebService extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            String devuelve = "";
            String cadena = params[0];
            URL url = null;//Donde se encuentra la informacion
            if (params[1] == "1") {//OPCION 1 NUEVO
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
                    jsonParam.put("tipo", params[2]);
                    jsonParam.put("iniciales", params[3]);
                    jsonParam.put("unidad", params[4]);
                    jsonParam.put("nserie", params[5]);
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
                            devuelve = "Registro insertado correctamente";
                        } else if (resultJSON == "2") {
                            devuelve = "Insercion fallida";
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


