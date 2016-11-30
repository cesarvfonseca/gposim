package mx.com.csrunity.gposimapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cvalenciano on 18/11/2016.
 */

public class consultarDatos extends AppCompatActivity implements View.OnClickListener{

    TextView txtDatos;
    EditText etBuscar;
    Button btnBuscar,btnRegresar,btnEliminar;


    //IP del host
    String IP = "http://www.webdesigns.hol.es/gposim";
    //String IP = "http://www.gruposim.esy.es/gposim";
    //Ruta de Web Services
    String GET = IP + "/obtener_registros.php";
    String DELETE = IP + "/borrar_registro.php";

    obtenerWebService hiloConexion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consulta_registro);


        txtDatos = (TextView)findViewById(R.id.txtDatos);

        etBuscar=(EditText)findViewById(R.id.etBuscar);

        btnBuscar = (Button)findViewById(R.id.btnBuscar);
        btnRegresar = (Button)findViewById(R.id.btnRegresar);
        btnEliminar = (Button)findViewById(R.id.btnEliminar);

        mostrarDatos();

        btnBuscar.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);
        btnEliminar.setOnClickListener(this);
    }

    void mostrarDatos(){
        hiloConexion = new obtenerWebService();
        hiloConexion.execute(GET, "1");   // Parámetros que recibe doInBackground
    }

    void mensajeConfirmar(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("ALERTA!");
        dialogo1.setMessage("Realmente desea eliminar el registro "+etBuscar.getText().toString()+"?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar();
            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.cancel();
            }
        });
        dialogo1.show();
    }

    public void aceptar() {
        hiloConexion = new obtenerWebService();
        hiloConexion.execute(DELETE, "2", etBuscar.getText().toString());   // Parámetros que recibe doInBackground
        etBuscar.setText("");
        mostrarDatos();
    }


    public Intent pausa(final Intent intent){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 4500);
        return intent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBuscar:
                Intent iNuevo;
                iNuevo=new Intent(this,ver_registros.class);
                String auxID=etBuscar.getText().toString();
                iNuevo.putExtra("auxid",auxID);
                pausa(iNuevo);
                etBuscar.setText("");
                break;
            case R.id.btnRegresar:
                Intent iMenu;
                iMenu=new Intent(this,MainActivity.class);
                startActivity(iMenu);
                break;
            case R.id.btnEliminar:
                mensajeConfirmar();
                break;
            default:
                break;
        }
    }

    public class obtenerWebService extends AsyncTask<String,Void,String> {
        protected String doInBackground(String... params) {
            String devuelve = "";
            String cadena = params[0];
            URL url = null;//Donde se encuentra la informacion

            if (params[1] == "1") {//OPCION 1 CONSULTA DE TODA LA TABLA

                try {
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                            " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                    //connection.setHeader("content-type", "application/json");

                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader
                        // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                        // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                        // StringBuilder.

                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);        // Paso toda la entrada al StringBuilder
                        }

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                        JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                        //Accedemos al vector de resultados
                        String resultJSON = respuestaJSON.getString("estado");

                        //Vamos obteniendo todos los campos que nos interesen.
                        if (resultJSON == "1") {//Hay informacion en la Tabla
                            JSONArray alumnosJSON = respuestaJSON.getJSONArray("registro");
                            //Recorrer JSON
                            for (int i = 0; i < alumnosJSON.length(); i++) {
                                devuelve = devuelve + alumnosJSON.getJSONObject(i).getString("id") + " | " +
                                        alumnosJSON.getJSONObject(i).getString("tipo") + " | " +
                                        alumnosJSON.getJSONObject(i).getString("iniciales") + " | " +
                                        alumnosJSON.getJSONObject(i).getString("unidad") + " | " +
                                        alumnosJSON.getJSONObject(i).getString("nserie") + " | \n";
                            }
                        } else if (resultJSON == "2") {
                            devuelve = "No hay información en la tabla";
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return devuelve;
            }else if (params[1] == "2") {//OPCION 4 BORRAR

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
                            devuelve = "Registro borrado correctamente";
                        } else if (resultJSON == "2") {
                            devuelve = "No hay alumnos";
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
            txtDatos.setText(s);
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


