package Inventario.app.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Inventario.app.Model.Fire;
import Inventario.app.R;
import Inventario.app.Model.QRmanager;

public class RegistrarObj extends AppCompatActivity {

    EditText nombre,referencia,proveedor,cantidad,costo,precio;
    Button registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_obj);

        nombre=findViewById(R.id.registrarobjnombre);
        referencia=findViewById(R.id.registrarobjref);
        proveedor=findViewById(R.id.registrarobjproov);
        cantidad=findViewById(R.id.registrarobjcantidad);
        costo=findViewById(R.id.registrarobjcosto);
        precio=findViewById(R.id.registrarobjprecio);
        registrar=findViewById(R.id.registrarobjboton);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validar()) {
                    item item=new item(Integer.parseInt(cantidad.getText().toString()),Integer.parseInt(costo.getText().toString()),nombre.getText().toString(),Integer.parseInt(precio.getText().toString()),proveedor.getText().toString(),referencia.getText().toString());
                    borrar();
                    Fire.registrarObj(RegistrarObj.this,item);
                }
            }
        });
    }

    private boolean validar() {
        if(nombre.getText().toString().equals("")){
            nombre.setError("requerido");
            return false;
        }else if(referencia.getText().toString().equals("")){
            referencia.setError("requerido");
            return false;
        }else if(proveedor.getText().toString().equals("")){
            proveedor.setError("requerido");
            return false;
        }else if(cantidad.getText().toString().equals("")){
            cantidad.setError("requerido");
            return false;
        }else if(costo.getText().toString().equals("")){
            costo.setError("requerido");
            return false;
        }else if(precio.getText().toString().equals("")){
            precio.setError("requerido");
            return false;
        }
        return true;
    }
    private void borrar(){
        nombre.setText(null);
        referencia.setText(null);
        proveedor.setText(null);
        cantidad.setText(null);
        costo.setText(null);
        precio.setText(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.icon_add){
            QRmanager.escaner(RegistrarObj.this);
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        IntentResult res = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (res != null) {
            if (res.getContents() != null) {
                Pattern pattern = Pattern.compile("_producto:(\\w+)_nombre:(\\w+)_referencia:(\\w+)");
                Matcher matcher = pattern.matcher(res.getContents());
                if (matcher.find()) {
                    referencia.setText(matcher.group(1));
                    nombre.setText(matcher.group(2));
                    referencia.setText(matcher.group(3));
                } else {
                    Toast.makeText(RegistrarObj.this, "No se ha podido leer el qr", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(RegistrarObj.this, "No se ha podido leer el qr", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(RegistrarObj.this, "No se ha podido leer el qr", Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}