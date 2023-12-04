package Inventario.app.Model;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import Inventario.app.ViewModel.Facturacion;
import Inventario.app.ViewModel.InterfazInventario;
import Inventario.app.ViewModel.MainActivity;
import Inventario.app.ViewModel.RegistrarObj;
import Inventario.app.ViewModel.RegistrarUsuario;
import Inventario.app.ViewModel.item;
import Inventario.app.ViewModel.usuario;
import Inventario.app.ui.gallery.GalleryFragment;

public class Fire {
    public static FirebaseAuth auth;
    public static FirebaseDatabase db;
    public static DatabaseReference dr;
    public static FirebaseUser user;
    public static StorageReference store;

    public static void conectar(Activity act) {
        FirebaseApp.initializeApp(act);
        db=FirebaseDatabase.getInstance();
        dr=db.getReference();
        auth = FirebaseAuth.getInstance();
        store = FirebaseStorage.getInstance().getReference();
        user=auth.getCurrentUser();
    }

    public static void obtenerdatousuario(String ref, TextView act){
        dr.child("usuario").child(user.getUid()).child(ref).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                act.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void guardarimagen(String url, Bitmap mp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] datosImagen = baos.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(datosImagen);
        store.child(url).putStream(inputStream);
    }
    public static CompletableFuture<Bitmap> imagen(String url){
        CompletableFuture<Bitmap> future=new CompletableFuture<>();
        Target tar=new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
               future.complete(bitmap);
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                future.completeExceptionally(e);
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        store.child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).into(tar);
            }
        });
        return future;
    }
    public static void login(usuario u, MainActivity act){
        auth.signInWithEmailAndPassword(u.getUid(),u.getContraseña())
                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user=auth.getCurrentUser();
                            act.reload();
                        } else {
                            Toast.makeText(act, "No se pudo iniciar",Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
    public static void sign(usuario u, RegistrarUsuario act){
        auth.createUserWithEmailAndPassword(u.getUid(),u.getContraseña())
                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(act,"Usuario creado con exito",Toast.LENGTH_LONG).show();
                            auth.signInWithEmailAndPassword(u.getUid(),u.getContraseña()).addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(act,"Llega aca",Toast.LENGTH_LONG).show();
                                    user=auth.getCurrentUser();
                                    registrarusuario(u);
                                    act.registrado();
                                }
                            });
                        } else {
                            Toast.makeText(act, "Hubo un error, intentalo de nuevo",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public static void registrarusuario(usuario u){
        guardarimagen(user.getUid()+"/fotodeperfil",u.getFoto());
        u.setFoto(null);
        u.setUid(null);
        u.setContraseña(null);
        dr.child("usuario").child(user.getUid()).setValue(u);
    }
    public static  void agregarItemAFirebase(item item,Facturacion act) {
        dr.child("usuario").child(user.getUid()).child("facturacion").child(item.getReferencia()).setValue(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(act, "Ha sido registrado con éxito", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(act, "Ha aparecido un error, inténtalo más tarde", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static void registrarObj(Activity act, item item){
        dr.child("usuario").child(user.getUid()).child("inventario").child(item.getReferencia()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Toast.makeText(act, "Este item ya existe", Toast.LENGTH_LONG).show();
                }else{
                    dr.child("usuario").child(user.getUid()).child("inventario").child(item.getReferencia()).setValue(item);
                    Toast.makeText(act, "Ha sido registrado con exito", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(act, InterfazInventario.class);
                    act.startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(act, "Ha aparecido un error, intentalo mas tarde", Toast.LENGTH_LONG).show();
            }
        });
    }
}
