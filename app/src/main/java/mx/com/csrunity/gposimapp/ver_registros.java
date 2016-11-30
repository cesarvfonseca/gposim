package mx.com.csrunity.gposimapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cesarvfonseca on 20/11/2016.
 */
public class ver_registros extends AppCompatActivity implements View.OnClickListener{

    ImageView ivImg1,ivImg2,ivImg3;
    Button btnRegresar,btnEditar;
    EditText etIni,etUnidad,etSerie;
    RadioGroup rgTipoM;
    RadioButton rbmp1M,rbmp2M;


    //IP del host
    String IP = "http://www.webdesigns.hol.es/gposim";
    //String IP = "http://www.webdesigns.hol.es/gposim";
    //Ruta de Web Services
    String UPDATE = IP + "/actualizar_registro.php";
    String GET_BY_ID = IP + "/obtener_registro_por_id.php";
    String IMAGENES = IP + "/img/";

    Bitmap bitmap,bitmap2,bitmap3;

    obtenerWebServiceB hiloConexion;

    //Guardar datos recibidos de la base de datos
    String idR,tipoR,inicialesR,unidadR,nserieR,datoRecibido;

    //Guardar datos para enviar a la base de datos
    String udTipo,udIniciales,udUnidad,udSerie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_registro);

        ivImg1 = (ImageView)findViewById(R.id.ivImg1);
        ivImg2 = (ImageView)findViewById(R.id.ivImg2);
        ivImg3 = (ImageView)findViewById(R.id.ivImg3);

        rgTipoM = (RadioGroup)findViewById(R.id.rgTipoM);
        rbmp1M=(RadioButton)findViewById(R.id.rbmp1M);
        rbmp2M=(RadioButton)findViewById(R.id.rbmp2M);

        etIni=(EditText)findViewById(R.id.et_Iniciales);
        etUnidad=(EditText)findViewById(R.id.et_Unidad);
        etSerie=(EditText)findViewById(R.id.et_Serie);

        btnRegresar=(Button)findViewById(R.id.btnRegresar);
        btnEditar=(Button)findViewById(R.id.btn_Editar);

        inhabilitar();

        Intent intent=getIntent();
        Bundle extras =intent.getExtras();
        if (extras != null) {//ver si contiene datos
            String auxid=(String)extras.get("auxid");//Obtengo el nombre
            datoRecibido=auxid;
        }

        mostrarDatos();

        btnRegresar.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        etIni.setOnClickListener(this);
    }

    void mostrarDatos(){
        hiloConexion = new obtenerWebServiceB();
        String cadenaConsulta = GET_BY_ID+"?id="+datoRecibido;
        hiloConexion.execute(cadenaConsulta, "1");   // Parámetros que recibe doInBackground
    }

    void nVentana(){
        Intent iNuevo;
        iNuevo=new Intent(this,MainActivity.class);
        startActivity(iNuevo);
    }

    void inhabilitar(){
        etIni.setFocusable(false);
        etUnidad.setFocusable(false);
        etSerie.setFocusable(false);
        rgTipoM.getChildAt(0).setEnabled(false);
        rgTipoM.getChildAt(1).setEnabled(false);
    }

    void habilitar(){
        etIni.setFocusableInTouchMode(true);
        etUnidad.setFocusableInTouchMode(true);
        etSerie.setFocusableInTouchMode(true);
        rgTipoM.getChildAt(0).setEnabled(true);
        rgTipoM.getChildAt(1).setEnabled(true);
    }

    public String separaDatos(String dato1){
        String splitted[] = dato1.split("\\|");
        idR = splitted[0].trim();
        tipoR = splitted[1].trim();
        inicialesR = splitted[2].trim();
        unidadR = splitted[3].trim();
        nserieR = splitted[4].trim();

        if (tipoR.equalsIgnoreCase("MP1")) {
            rbmp1M.setChecked(true);
        } else{
            rbmp2M.setChecked(true);
        }
        etIni.setText(inicialesR);
        etUnidad.setText(unidadR);
        etSerie.setText(nserieR);
        return null;
    }

    void enviarDatos(){
        Intent vEditar;
        vEditar=new Intent(this,editarRegistro.class);
        vEditar.putExtra("idE",idR);
        vEditar.putExtra("tipoE",tipoR);
        vEditar.putExtra("inicialesE",inicialesR);
        vEditar.putExtra("unidadE",unidadR);
        vEditar.putExtra("nserieE",nserieR);
        startActivity(vEditar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegresar:
                nVentana();
                break;
            case R.id.btn_Editar:
                enviarDatos();
                break;
            default:
                break;
        }
    }

    public class obtenerWebServiceB extends AsyncTask<String,Void,String> {
        protected String doInBackground(String... params) {
            String devuelve = "";
            String cadena = params[0];
            URL url = null;//Donde se encuentra la informacion
            if (params[1] == "1") {//OPCION 1 CONSULTA POR ID
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
                            //Recorrer JSON
                            devuelve = devuelve + respuestaJSON.getJSONObject("registro").getString("id") + " | " +
                                    respuestaJSON.getJSONObject("registro").getString("tipo") + " | " +
                                    respuestaJSON.getJSONObject("registro").getString("iniciales") + " | " +
                                    respuestaJSON.getJSONObject("registro").getString("unidad") + " | " +
                                    respuestaJSON.getJSONObject("registro").getString("nserie") + " | \n";
                            if (respuestaJSON.getJSONObject("registro").getString("img1").equals("noimagen")) {
                                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notimg);
                            } else {
                                URL urlImagen = new URL(IMAGENES + respuestaJSON.getJSONObject("registro").getString("img1"));
                                HttpURLConnection conimagen = (HttpURLConnection) urlImagen.openConnection();
                                conimagen.connect();

                                URL urlImagen2 = new URL(IMAGENES + respuestaJSON.getJSONObject("registro").getString("img2"));
                                HttpURLConnection conimagen2 = (HttpURLConnection) urlImagen2.openConnection();
                                conimagen2.connect();

                                URL urlImagen3 = new URL(IMAGENES + respuestaJSON.getJSONObject("registro").getString("img3"));
                                HttpURLConnection conimagen3 = (HttpURLConnection) urlImagen3.openConnection();
                                conimagen3.connect();

                                bitmap = BitmapFactory.decodeStream(conimagen.getInputStream());
                                bitmap2 = BitmapFactory.decodeStream(conimagen2.getInputStream());
                                bitmap3 = BitmapFactory.decodeStream(conimagen3.getInputStream());
                            }

                        } else if (resultJSON == "2") {
                            devuelve = "No existe registro";
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return devuelve;//Devuelve la respuesta
            }//Termina Consulta por ID
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            separaDatos(s);
            ivImg1.setImageBitmap(bitmap);
            ivImg2.setImageBitmap(bitmap2);
            ivImg3.setImageBitmap(bitmap3);
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
