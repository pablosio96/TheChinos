
public class psi14_Player {

      private String nombre;
      private int id;
      private int ganadas;
      private int perdidas;

    public psi14_Player(String n_nombre, int n_id, int n_ganadas, int n_perdidas) {
        this.nombre = n_nombre;
        this.id = n_id;
        this.ganadas = n_ganadas;
        this.perdidas = n_perdidas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String n_nombre) {
        this.nombre = n_nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int n_id) {
        this.id = n_id;
    }

    public int getGanadas() {
        return ganadas;
    }

    public void setGanadas(int n_ganadas) {
        this.ganadas += n_ganadas;
    }

    public int getPerdidas() {
        return perdidas;
    }

    public void setPerdidas(int n_perdidas) {
        this.perdidas += n_perdidas;
    }

}
