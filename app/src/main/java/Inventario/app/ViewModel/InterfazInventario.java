package Inventario.app.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Inventario.app.Model.Fire;
import Inventario.app.R;
import Inventario.app.Model.QRmanager;

public class InterfazInventario extends AppCompatActivity {
    private ArrayList<String> lista=new ArrayList<String>();
    ArrayAdapter<String> adapt;
    item qr;

    Button registrarobg,registrarcli,edit,elim;
    GridView obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_inventario);


        registrarobg=findViewById(R.id.interfazinvregistrarobj);

        elim=findViewById(R.id.interfazinvelim);
        obj=findViewById(R.id.interfazinvtabla);

        estadistico();
        registrarobg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InterfazInventario.this,RegistrarObj.class);
                startActivity(intent);
            }
        });

        elim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarObjeto();
            }
        });
        obj.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> arrayAdapter = (ArrayAdapter<String>) parent.getAdapter();
                int numColumnas = obj.getNumColumns();
                int ini = position - (position % numColumnas);
                qr=new item();
                qr.setNombre(arrayAdapter.getItem(ini));
                qr.setReferencia(arrayAdapter.getItem(ini+1));
                try {
                    qr.setCatidad(Integer.parseInt(arrayAdapter.getItem(ini+2)));
                    qr.setPrecio(Integer.parseInt(arrayAdapter.getItem(ini+3)));
                } catch (NumberFormatException e) {
                }
            }
        });
    }
    private void eliminarObjeto() {
        if (qr != null) {
            DatabaseReference ref = Fire.dr.child("usuario").child(Fire.user.getUid()).child("inventario");

            // Elimina el objeto de la base de datos
            ref.orderByChild("nombre").equalTo(qr.getNombre()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Manejar errores si es necesario
                }
            });

            // Actualiza la interfaz después de eliminar
            estadistico();
        }
    }

    private void estadistico() {
        Fire.dr.child("usuario").child(Fire.user.getUid()).child("inventario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista.clear();
                lista.clear();
                lista.add("Nombre");
                lista.add("Referencia");
                lista.add("Cantidad");
                lista.add("Precio");
                if(snapshot.exists()){
                    for (DataSnapshot dat:snapshot.getChildren()){
                        item Item=new item();
                        Item.setCatidad(dat.child("catidad").getValue(int.class));
                        Item.setCosto(dat.child("costo").getValue(int.class));
                        Item.setNombre(dat.child("nombre").getValue(String.class));
                        Item.setPrecio(dat.child("precio").getValue(int.class));
                        Item.setProveedor(dat.child("proveedor").getValue(String.class));
                        Item.setReferencia(dat.child("referencia").getValue(String.class));
                        lista.add(Item.getNombre());
                        lista.add(Item.getReferencia());
                        lista.add(String.valueOf(Item.getCatidad()));
                        lista.add(String.valueOf(Item.getPrecio()));
                        }
                    adapt=new ArrayAdapter<String>(InterfazInventario.this,android.R.layout.simple_list_item_1,lista);
                    obj.setAdapter(adapt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.icon_add && qr!=null){
            View rootView = findViewById(android.R.id.content);
            QRmanager.generar(InterfazInventario.this,"_producto:"+ Fire.user.getUid()+"_nombre:"+qr.getNombre()
                            +"_referencia:"+qr.getReferencia(),rootView);
        }
        return true;
    }
}