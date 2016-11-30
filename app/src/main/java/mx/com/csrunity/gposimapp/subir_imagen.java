package mx.com.csrunity.gposimapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Ximena on 17/11/2016.
 */
public class subir_imagen extends AppCompatActivity implements View.OnClickListener {

    TextView txtNserie,txtPosicion;
    ImageView ivImagen;
    Button btnElegir,btnSubir;

    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL ="http://www.webdesigns.hol.es/gposim/upload.php";
    //private String UPLOAD_URL ="http://www.gruposim.esy.es/gposim/upload.php";
    private String nserie="";
    private int n;//Para maneja la posicion y el cambio de campo a modificar
    private int pos;

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String KEY_FIELD="field";
    private String KEY_POS="position";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subir_imagen);

        txtNserie = (TextView)findViewById(R.id.txtNserie);
        txtPosicion = (TextView)findViewById(R.id.txtPosicion);

        ivImagen = (ImageView)findViewById(R.id.ivImagen);

        btnElegir = (Button)findViewById(R.id.btnElegir);
        btnSubir = (Button)findViewById(R.id.btnSubir);

        btnSubir.setOnClickListener(this);
        btnElegir.setOnClickListener(this);

        releaseBitmap();

        recibirDatos();
        pos=n+1;
        txtNserie.setText(nserie);
        txtPosicion.setText("00"+pos);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 11, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Elegir Imagen..."), PICK_IMAGE_REQUEST);
    }

    void recibirDatos(){
        Intent intent=getIntent();
        Bundle extras =intent.getExtras();
        if (extras != null) {//ver si contiene datos
            nserie=(String)extras.get("nserie");//Obtengo el nombre
            n=extras.getInt("n",0);
        }
    }

    void nuevaVentana(){
        Intent vnueva;
        nserie=txtNserie.getText().toString();
        n=pos;
        if (pos<3){//Para que abra 3 veces esta misma clase
            vnueva=new Intent(this,subir_imagen.class);
            vnueva.putExtra("nserie",nserie);
            vnueva.putExtra("n",n);
            barraProgreso(vnueva);
            //startActivity(vnueva);
        }
        else{
            vnueva=new Intent(this,MainActivity.class);
            barraProgreso(vnueva);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                ivImagen.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void releaseBitmap() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private void unbindDrawables(View view)
    {
        if (view.getBackground() != null)
        {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView))
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Subiendo imagen...","Espere porfavor...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(subir_imagen.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        //Showing toast
                        Toast.makeText(subir_imagen.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                //Getting Image Name
                String name = txtNserie.getText().toString().trim();
                //Asignar campo a modificar
                String field = "img"+pos;
                //Asignar campo a modificar
                String position = "_00"+pos;
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);
                params.put(KEY_FIELD, field);
                params.put(KEY_POS, position);
                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public Intent barraProgreso(final Intent intent){
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
            case R.id.btnElegir:
                showFileChooser();
                break;
            case R.id.btnSubir:
                uploadImage();
                nuevaVentana();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.ivImagen));
        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindDrawables(findViewById(R.id.ivImagen));
        System.gc();
    }
}
