package modelo;

import java.math.BigDecimal;

public class Funcion {

    private int idFuncion;
    private int idPelicula;
    private int idSede;
    private int sala;
    private java.sql.Timestamp fechaHora;
    private BigDecimal precio;
    private int butacasTotales;
    private int butacasDisponibles;
    private String tituloPelicula;
    private String generoPelicula;
    private String nombreSede;

    public Funcion() {
    }

    public int getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }

    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public int getIdSede() {
        return idSede;
    }

    public void setIdSede(int idSede) {
        this.idSede = idSede;
    }

    public int getSala() {
        return sala;
    }

    public void setSala(int sala) {
        this.sala = sala;
    }

    public java.sql.Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(java.sql.Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getButacasTotales() {
        return butacasTotales;
    }

    public void setButacasTotales(int butacasTotales) {
        this.butacasTotales = butacasTotales;
    }

    public int getButacasDisponibles() {
        return butacasDisponibles;
    }

    public void setButacasDisponibles(int butacasDisponibles) {
        this.butacasDisponibles = butacasDisponibles;
    }

    public String getTituloPelicula() {
        return tituloPelicula;
    }

    public void setTituloPelicula(String tituloPelicula) {
        this.tituloPelicula = tituloPelicula;
    }

    public String getGeneroPelicula() {
        return generoPelicula;
    }

    public void setGeneroPelicula(String generoPelicula) {
        this.generoPelicula = generoPelicula;
    }

    public String getNombreSede() {
        return nombreSede;
    }

    public void setNombreSede(String nombreSede) {
        this.nombreSede = nombreSede;
    }
}
