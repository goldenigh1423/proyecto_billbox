package Inventario.app.ViewModel;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import Inventario.app.R;

public class VerFacturaActivity extends AppCompatActivity {

    private ImageView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_factura);

        pdfView = findViewById(R.id.pdfView);

        // Obtener el nombre de la factura del Intent
        String facturaNombre = getIntent().getStringExtra("facturaNombre");

        // Mostrar el contenido del archivo PDF
        mostrarPDF(facturaNombre);
    }

    private void mostrarPDF(String facturaNombre) {
        try {
            // Obtén la ruta del directorio de documentos de tu aplicación
            File directorioDocumentos = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

            if (directorioDocumentos != null) {
                // Construye la ruta completa del archivo
                File archivoFactura = new File(directorioDocumentos, "FacturaBB/" + facturaNombre);

                // Abre el archivo de la factura con PdfRenderer
                ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(archivoFactura, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);

                // Muestra la primera página del PDF
                PdfRenderer.Page pagina = pdfRenderer.openPage(0);
                Bitmap bitmap = Bitmap.createBitmap(pagina.getWidth(), pagina.getHeight(), Bitmap.Config.ARGB_8888);
                pagina.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                pdfView.setImageBitmap(bitmap);

                // Cierra el PdfRenderer y el archivo
                pagina.close();
                pdfRenderer.close();
                parcelFileDescriptor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Manejar el error al cargar el PDF
        }
    }
}