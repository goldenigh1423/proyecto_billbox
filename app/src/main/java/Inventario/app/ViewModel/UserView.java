package Inventario.app.ViewModel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import Inventario.app.Model.Fire;
import Inventario.app.R;

public class UserView extends AppCompatActivity {
    private ImageView img;
    private TextView usuario,nombre,sexo,telefono,fecha;
    private Button boton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);
        img=findViewById(R.id.UserView_imageView);
        usuario=findViewById(R.id.User_correo);
        nombre=findViewById(R.id.UserView_nombre);
        sexo=findViewById(R.id.UserView_sexo);
        telefono=findViewById(R.id.UserView_Telefono);
        fecha=findViewById(R.id.UserView_Fecha);
        boton=findViewById(R.id.UerView_Registrar);
        cargando();
    }

    private void cargando() {
        Fire.imagen(Fire.user.getUid()+"/fotodeperfil").thenAcceptAsync(bitmap -> {
            runOnUiThread(() -> img.setImageBitmap(bitmap));
        });
        usuario.setText(Fire.user.getEmail());
            Fire.obtenerdatousuario("telefono",telefono);
            Fire.obtenerdatousuario("birthDate",fecha);
            Fire.obtenerdatousuario("nombre",nombre);
            Fire.obtenerdatousuario("sexo",sexo);
    }
}