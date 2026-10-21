package modelo;

import java.math.BigDecimal;

public class Entrada {

    private int idEntrada;
    private int idCliente;
    private int idFuncion;
    private int idVenta;
    private String butaca;
    private java.sql.Timestamp fechaVenta;
    private BigDecimal precioPagado;
    private String estado;
    private String tituloPelicula;
    private String fechaFuncion;
    private String sala;
    private String nombreSede;
    private String nombreCliente;

    public Entrada() {
    }

    public Entrada(int idFuncion, String butaca) {
        this.idFuncion = idFuncion;
        this.butaca = butaca;
    }

    public int getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(int idEntrada) {
        this.idEntrada = idEntrada;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getButaca() {
        return butaca;
    }

    public void setButaca(String butaca) {
        this.butaca = butaca;
    }

    public java.sql.Timestamp getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(java.sql.Timestamp fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getPrecioPagado() {
        return precioPagado;
    }

    public void setPrecioPagado(BigDecimal precioPagado) {
        this.precioPagado = precioPagado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTituloPelicula() {
        return tituloPelicula;
    }

    public void setTituloPelicula(String tituloPelicula) {
        this.tituloPelicula = tituloPelicula;
    }

    public String getFechaFuncion() {
        return fechaFuncion;
    }

    public void setFechaFuncion(String fechaFuncion) {
        this.fechaFuncion = fechaFuncion;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public String getNombreSede() {
        return nombreSede;
    }

    public void setNombreSede(String nombreSede) {
        this.nombreSede = nombreSede;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}
