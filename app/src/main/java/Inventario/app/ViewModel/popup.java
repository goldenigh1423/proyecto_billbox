package Inventario.app.ViewModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import Inventario.app.R;

public class popup {
    public static void popup(final Context context, View view,Bitmap bm) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

        final ImageView imgPopup = popupView.findViewById(R.id.imgPopup);
        Button btnDownload = popupView.findViewById(R.id.btnDownload);

        imgPopup.setImageBitmap(bm);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        popupWindow.showAtLocation(view, 0, 50, 50);
    }
}

