package Inventario.app.ViewModel;

import android.graphics.Bitmap;

public class usuario {
    private static usuario user;
    private Bitmap foto;
    private String nombre;
    private String contraseña;
    private String uid;
    private String sexo;
    private String telefono;
    private String BirthDate;

    public static void foto(Bitmap bm){
        user.setFoto(bm);
    }
    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public usuario(String nombre, String contraseña, String uid, String sexo, String telefono, String birthDate,Bitmap foto) {
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.uid = uid;
        this.sexo = sexo;
        this.telefono = telefono;
        this.BirthDate = birthDate;
        this.foto=foto;
    }

    public usuario(String Uid, String contraseña) {
        this.uid = Uid;
        this.contraseña = contraseña;
    }
    public static void crear(String Uid,String contraseña){
        user=new usuario(Uid,contraseña);
    }
    public static synchronized usuario getInstance(){
        return user;
    }
    public static void borrar(){
        user=null;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
