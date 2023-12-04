package Inventario.app.ViewModel;// Importa las clases necesarias
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

import Inventario.app.R;
import Inventario.app.ViewModel.VerFacturaActivity;

public class facturasdispo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturasdispo);

        // Obtén la referencia del ListView desde tu diseño XML
        ListView listView = findViewById(R.id.listViewFacturas);

        // Obtiene la lista de archivos PDF en la carpeta FacturaBB
        ArrayList<String> listaFacturas = obtenerListaFacturas();

        // Crea un adaptador para mostrar la lista de facturas en el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaFacturas);

        // Establece el adaptador en el ListView
        listView.setAdapter(adapter);

        // Configura un listener para manejar la selección de elementos en el ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtiene el nombre de la factura seleccionada
                String facturaNombre = listaFacturas.get(position);

                // Inicia la actividad para ver la factura
                Intent intent = new Intent(facturasdispo.this, VerFacturaActivity.class);
                intent.putExtra("facturaNombre", facturaNombre);
                startActivity(intent);
            }
        });
    }

    // Método para obtener la lista de archivos PDF en la carpeta FacturaBB
    private ArrayList<String> obtenerListaFacturas() {
        ArrayList<String> listaFacturas = new ArrayList<>();
        File directorioFacturas = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "FacturaBB");

        if (directorioFacturas.exists()) {
            File[] archivos = directorioFacturas.listFiles();

            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isFile() && archivo.getName().endsWith(".pdf")) {
                        listaFacturas.add(archivo.getName());
                    }
                }
            }
        }

        return listaFacturas;
    }
}