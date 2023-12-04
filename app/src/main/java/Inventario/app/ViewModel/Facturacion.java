package Inventario.app.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Inventario.app.Model.Fire;
import Inventario.app.Model.QRmanager;
import Inventario.app.R;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Facturacion extends AppCompatActivity {
    private ArrayList<String> lista=new ArrayList<String>();
    ArrayAdapter<String> adapt;
    item qr;
    EditText nombre,referencia,cantidad,precio;
    GridView obj;
    Button facturar,descargar,completar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturacion);
        nombre=findViewById(R.id.facturanombre);
        referencia=findViewById(R.id.facturaref);
        cantidad=findViewById(R.id.facturacantidad);
        precio=findViewById(R.id.facturapre);
        facturar=findViewById(R.id.botonfacturar);
        obj=findViewById(R.id.facturatabla);
        descargar = findViewById(R.id.botondescargar);
        completar= findViewById(R.id.botoncom);
        estadistico();
        facturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    item item = new item(nombre.getText().toString(), referencia.getText().toString(),
                            Integer.parseInt(cantidad.getText().toString()), Integer.parseInt(precio.getText().toString()));
                    borrar();
                    Fire.agregarItemAFirebase(item,Facturacion.this);
                }
            }
        });

        descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadDataFromFirebase();
            }
        });
        completar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completarFacturacion();
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void estadistico() {
        Fire.dr.child("usuario").child(Fire.user.getUid()).child("facturacion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista.clear();
                lista.add("Nombre");
                lista.add("Referencia");
                lista.add("Cantidad");
                lista.add("Precio");

                if(snapshot.exists()){
                    for (DataSnapshot dat : snapshot.getChildren()) {
                        item Item = dat.getValue(item.class);

                        // Acumular elementos en la lista
                        lista.add(Item.getNombre());
                        lista.add(Item.getReferencia());
                        lista.add(String.valueOf(Item.getCatidad()));
                        lista.add(String.valueOf(Item.getPrecio()));
                    }

                    // Asignar la lista al adaptador fuera del bucle for
                }
                adapt = new ArrayAdapter<>(Facturacion.this, android.R.layout.simple_list_item_1, lista);
                obj.setAdapter(adapt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Facturacion.this, "Error al obtener datos desde Firebase Realtime Database", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void borrar(){
        nombre.setText(null);
        referencia.setText(null);
        cantidad.setText(null);
        precio.setText(null);
    }
    private void DownloadDataFromFirebase() {
        // Obtener una instancia de la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataReference = database.getReference("usuario").child(Fire.user.getUid()).child("facturacion");

        // Agregar un listener para obtener los datos desde Firebase Realtime Database
        dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<item> items = new ArrayList<>();
                    for (DataSnapshot dat : dataSnapshot.getChildren()) {
                        item Item = dat.getValue(item.class);
                        items.add(Item);
                    }

                    // Llamar a saveDataToFile con la lista de items
                    saveDataToFile(items);
                } else {
                    Toast.makeText(Facturacion.this, "No hay datos para descargar", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Facturacion.this, "Error al obtener datos desde Firebase Realtime Database", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveDataToFile(ArrayList<item> items) {
        try {
            File rootPath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (rootPath == null) {
                Toast.makeText(Facturacion.this, "Error al obtener el directorio de documentos", Toast.LENGTH_LONG).show();
                return;
            }

            File facturaDirectory = new File(rootPath, "FacturaBB");
            if (!facturaDirectory.exists() && !facturaDirectory.mkdirs()) {
                Toast.makeText(Facturacion.this, "Error al crear directorio FacturaBB", Toast.LENGTH_LONG).show();
                Log.e("Facturacion", "Error al crear directorio FacturaBB");
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());

            String fileName = "factura-" + timestamp + ".pdf";
            File file = new File(facturaDirectory, fileName);
            Log.i("Facturacion", "Ruta completa del archivo: " + file.getAbsolutePath());

            // Usar iText para generar el archivo PDF
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Agregar datos en la factura
            document.add(new Paragraph(" "));
            document.add(new Paragraph("BillBox"));
            document.add(new Paragraph("NIT: 000000000"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Obtener el ID del recurso del logo desde R.drawable
            int logoResourceId = R.drawable.logo;

            // Agregar el título "Factura" en la esquina superior izquierda
            document.add(new Paragraph("Factura"));
            document.add(new Paragraph("Fecha y Hora de Facturación: " + timestamp));
            document.add(new Paragraph(" "));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Agregar marca de agua (watermark) en la esquina superior izquierda
            addWatermark(writer, "BillBox", BitmapFactory.decodeResource(getResources(), logoResourceId));

            // Crear una tabla con 4 columnas
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            // Configurar las celdas de la tabla
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            PdfPCell cell = new PdfPCell(new Paragraph("Nombre", font));
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Referencia", font));
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Cantidad", font));
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Precio", font));
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);

            // Agregar los datos de los items a la tabla
            for (item Item : items) {
                table.addCell(Item.getNombre());
                table.addCell(Item.getReferencia());
                table.addCell(String.valueOf(Item.getCatidad()));
                table.addCell(String.valueOf(Item.getPrecio()));
            }

            // Agregar la tabla al documento
            document.add(table);

            // Calcular el total
            int total = 0;
            for (item Item : items) {
                total += Item.getPrecio() * Item.getCatidad();
            }

            // Agregar el total debajo de la tabla
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.addCell(new PdfPCell(new Paragraph("Total", font)));
            totalTable.addCell(new PdfPCell(new Paragraph(String.valueOf(total), font)));

            document.add(totalTable);

            document.close();

            Toast.makeText(Facturacion.this, "Datos guardados en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            String errorMessage = "Error al guardar datos en el archivo PDF: " + e.getMessage();
            Toast.makeText(Facturacion.this, errorMessage, Toast.LENGTH_LONG).show();
            Log.e("Facturacion", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "Error inesperado: " + e.getMessage();
            Toast.makeText(Facturacion.this, errorMessage, Toast.LENGTH_LONG).show();
            Log.e("Facturacion", errorMessage);
        }
    }


    // Método para agregar una marca de agua al documento PDF
    private void addWatermark(PdfWriter writer, String text, Bitmap imageBitmap) throws DocumentException, IOException {
        PdfContentByte contentByte = writer.getDirectContentUnder();

        // Establecer la transparencia de la marca de agua
        contentByte.setGState(new PdfGState(){{setFillOpacity(0.3f);}});

        // Configurar el texto de la marca de agua
        contentByte.beginText();
        contentByte.setFontAndSize(BaseFont.createFont(), 60);
        contentByte.setColorFill(BaseColor.GRAY);
        contentByte.showTextAligned(Element.ALIGN_CENTER, text, 300, 400, 45);
        contentByte.endText();

        // Configurar la imagen de la marca de agua
        Image image = Image.getInstance(getBytesFromBitmap(imageBitmap));
        image.scaleToFit(200, 200);
        image.setAbsolutePosition(200, 400);
        contentByte.addImage(image);
    }

    // Método para convertir un Bitmap a un array de bytes
    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.icon_add){
            QRmanager.escaner(Facturacion.this);
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
                    Toast.makeText(Facturacion.this, "No se ha podido leer el qr", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(Facturacion.this, "No se ha podido leer el qr", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(Facturacion.this, "No se ha podido leer el qr", Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void completarFacturacion() {
        // Obtener la fecha y hora actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
        String timestamp = dateFormat.format(new Date());

        // Obtener referencia a la base de datos
        DatabaseReference usuarioRef = Fire.dr.child("usuario").child(Fire.user.getUid());

        // Mover datos de facturación al nuevo menú
        DatabaseReference facturacionRef = usuarioRef.child("facturacion");

        facturacionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombreItem = "";  // Variable para almacenar el nombre del ítem

                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        item Item = itemSnapshot.getValue(item.class);

                        // Guardar el nombre del primer ítem
                        if (nombreItem.isEmpty()) {
                            nombreItem = Item.getNombre();
                        }

                        // Agregar el ítem al nuevo menú
                        usuarioRef.child("facturas").child("facturacion_" + timestamp).push().setValue(Item);
                    }

                    // Limpiar datos de facturación después de completar
                    facturacionRef.removeValue();

                    Toast.makeText(Facturacion.this, "Facturación completada con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Facturacion.this, "No hay datos de facturación para completar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Facturacion.this, "Error al completar la facturación", Toast.LENGTH_SHORT).show();
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
        }else if(cantidad.getText().toString().equals("")){
            cantidad.setError("requerido");
            return false;
        }else if(precio.getText().toString().equals("")){
            precio.setError("requerido");
            return false;
        }
        return true;
    }
}