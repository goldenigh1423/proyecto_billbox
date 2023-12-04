package Inventario.app.Model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import Inventario.app.ViewModel.popup;

public class QRmanager{
    public static void escaner(Activity act){
        IntentIntegrator esc=new IntentIntegrator(act);
        esc.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        esc.setPrompt("Lector");
        esc.setCameraId(0);
        esc.setOrientationLocked(false);
        esc.initiateScan();
    }
    public static void generar(Activity act,String txt, View v){
        try{
            BarcodeEncoder be=new BarcodeEncoder();
            Bitmap bm=be.encodeBitmap(txt,BarcodeFormat.QR_CODE,750,750);
            popup.popup(act,v,bm);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
