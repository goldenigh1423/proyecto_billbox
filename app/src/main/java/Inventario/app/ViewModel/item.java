package Inventario.app.ViewModel;

public class item {
    String nombre;
    String referencia;
    String proveedor;
    int catidad;
    int precio;
    int costo;

    public item() {
    }
    public item(String nombre, String referencia, int catidad, int precio) {
        this.nombre = nombre;
        this.referencia = referencia;
        this.catidad = catidad;
        this.precio = precio;
    }
    public item(int catidad, int costo, String nombre, int precio, String proveedor, String referencia) {
        this.nombre = nombre;
        this.referencia = referencia;
        this.proveedor = proveedor;
        this.catidad = catidad;
        this.precio = precio;
        this.costo = costo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public int getCatidad() {
        return catidad;
    }

    public void setCatidad(int catidad) {
        this.catidad = catidad;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }
}
