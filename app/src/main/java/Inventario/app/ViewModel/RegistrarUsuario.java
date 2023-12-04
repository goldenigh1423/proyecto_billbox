package Inventario.app.ViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Inventario.app.Model.Fire;
import Inventario.app.R;

public class RegistrarUsuario extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 0;
    private static final int REQUEST_CROP_PICTURE = 1;
    private ImageView fotodeperfil;
    private TextView correo, contraseña, username, sex, tel, fecha, confirmar;
    private Button registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        fotodeperfil = findViewById(R.id.RegistrarUsuario_imageView);
        correo = findViewById(R.id.RegistrarUuario_correo);
        contraseña = findViewById(R.id.RegistrarUsuario_txt_contraseña);
        username = findViewById(R.id.RegistrarUsuario_nombre);
        sex = findViewById(R.id.RegistrarUsuario_sexo);
        tel = findViewById(R.id.RegistrarUsuario_Telefono);
        fecha = findViewById(R.id.RegistrarUsuario_Fecha);
        confirmar = findViewById(R.id.RegistrarUsuario_Confirmar);
        registrar = findViewById(R.id.RegistrarUsuario_Registrar);

        Fire.imagen("plataforma/user.png").thenAcceptAsync(bitmap -> {
            runOnUiThread(()->fotodeperfil.setImageBitmap(bitmap));
        });

        if(usuario.getInstance().getFoto()!=null){
            fotodeperfil.setImageBitmap(usuario.getInstance().getFoto());
        }
        correo.setText(usuario.getInstance().getUid());
        contraseña.setText(usuario.getInstance().getContraseña());
        fotodeperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validar()){
                    if(usuario.getInstance().getFoto()==null){
                        fotodeperfil.setDrawingCacheEnabled(true);
                        usuario.foto(fotodeperfil.getDrawingCache());
                        fotodeperfil.setDrawingCacheEnabled(false);
                    }
                    usuario user=new usuario(username.getText().toString(),
                            contraseña.getText().toString(),
                            correo.getText().toString(),
                            sex.getText().toString(),
                            tel.getText().toString(),
                            fecha.getText().toString(),
                            usuario.getInstance().getFoto());
                    Fire.sign(user,RegistrarUsuario.this);
                }
            }
        });
    }

    public void registrado(){
        usuario.borrar();
        Intent intent=new Intent(RegistrarUsuario.this,MainActivityMenu.class);
        startActivity(intent);
    }

    private boolean validar() {
        if(correo.getText().toString().equals("")) {
            correo.setError("requerido");
            return false;
        }else if(!validarEmail()){
            correo.setError("Correo incorrecto: example@dominio.com");
            return false;
        }else if(contraseña.getText().toString().equals("")){
            contraseña.setError("requerido");
            return false;
        }else if(username.getText().toString().equals("")){
            username.setError("Requerido");
            return false;
        }else if(confirmar.getText().toString().equals("")){
            confirmar.setError("requerido");
            return false;
        }else if(!confirmar.getText().toString().equals(contraseña.getText().toString())){
            confirmar.setError("La contraseña es diferente");
            return false;
        }
        return true;
    }

    private boolean validarEmail() {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(correo.getText().toString());
        return matcher.matches();
    }

    private void iniciarActividadRecorte(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "recorte_temporal.jpg"));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(80);
        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .withAspectRatio(1,1)
                .start(this, REQUEST_CROP_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        iniciarActividadRecorte(selectedImageUri);
                    }
                    break;
                case REQUEST_CROP_PICTURE:
                    if (data != null) {
                        Uri imagen = UCrop.getOutput(data);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagen);
                            usuario.foto(bitmap);
                            fotodeperfil.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }
}