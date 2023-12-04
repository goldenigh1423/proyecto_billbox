package Inventario.app.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import Inventario.app.Model.ConectServer;
import Inventario.app.Model.Fire;
import Inventario.app.Model.QRmanager;
import Inventario.app.R;

public class MainActivityMenu extends AppCompatActivity {
    Button boton1,boton2,boton3,boton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        boton1=findViewById(R.id.Menufactura);
        boton2=findViewById(R.id.Menuinventario);
        boton3=findViewById(R.id.MenuFacturas);
        boton4=findViewById(R.id.LogOut);
        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fire.auth.signOut();
                ConectServer.getInstance().close();
                ConectServer.borrar();
                Intent intent=new Intent(MainActivityMenu.this,MainActivity.class);
                startActivity(intent);
            }
        });
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivityMenu.this, Facturacion.class);
                startActivity(intent);
            }
        });
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivityMenu.this,InterfazInventario.class);
                startActivity(intent);
            }
        });
        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityMenu.this, facturasdispo.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.icon_add){
            Intent intent=new Intent(MainActivityMenu.this,UserView.class);
            startActivity(intent);
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
}