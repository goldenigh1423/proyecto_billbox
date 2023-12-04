package Inventario.app.ViewModel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import Inventario.app.Model.ConectServer;
import Inventario.app.Model.Fire;
import Inventario.app.R;

public class MainActivity extends AppCompatActivity{
    Button iniciar,registrar;
    EditText nombre,contraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registrar=findViewById(R.id.Registrar);
        iniciar=findViewById(R.id.Iniciar);
        nombre=findViewById(R.id.txt_nombre);
        contraseña=findViewById(R.id.txt_contraseña);

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(validar()) {
                    usuario u=new usuario(nombre.getText().toString(),contraseña.getText().toString());
                    borrar();
                    Fire.login(u,MainActivity.this);
                }
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario.crear(nombre.getText().toString(),contraseña.getText().toString());
                borrar();
                regis();
            }
        });
    }

    private void borrar() {
        nombre.setText("");
        contraseña.setText("");
    }

    private boolean validar() {
        if(nombre.getText().toString().equals("")) {
            nombre.setError("requerido");
            return false;
        }else if(!validarEmail()){
            nombre.setError("Correo incorrecto: example@dominio.com");
            return false;
        }else if(contraseña.getText().toString().equals("")){
            contraseña.setError("requerido");
            return false;
        }
        return true;
    }

    private boolean validarEmail() {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(nombre.getText().toString());
        return matcher.matches();
    }

    public void reload() {
        Toast.makeText(MainActivity.this,"Iniciando",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(MainActivity.this,MainActivityMenu.class);
        ConectServer webSocketClient = ConectServer.getInstance();
        if (!webSocketClient.isOpen()) {
            ConectServer.borrar();
            webSocketClient.connect();
        }
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Fire.conectar(this);
        FirebaseUser currentUser = Fire.auth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    public void regis(){
        Intent intent=new Intent(MainActivity.this, RegistrarUsuario.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
}