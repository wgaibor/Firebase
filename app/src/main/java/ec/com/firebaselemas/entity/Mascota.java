package ec.com.firebaselemas.entity;

public class Mascota {

    private String idUser;
    //private String id;
    private String nombre;
    private String edad;
    private String color;
    private String precioVacuna;

    public Mascota(){} // Needed for Firebase

    public Mascota(String idUser/*, String id*/, String nombre, String edad, String color, String precioVacuna) {
        this.idUser = idUser;
        //this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.color = color;
        this.precioVacuna = precioVacuna;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPrecioVacuna() {
        return precioVacuna;
    }

    public void setPrecioVacuna(String precioVacuna) {
        this.precioVacuna = precioVacuna;
    }
}
