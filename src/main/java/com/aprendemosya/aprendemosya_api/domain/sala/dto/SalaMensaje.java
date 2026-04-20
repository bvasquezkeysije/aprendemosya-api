package com.aprendemosya.aprendemosya_api.domain.sala.dto;

public class SalaMensaje {

    private String usuario;
    private String mensaje;
    private String salaCodigo;

    public SalaMensaje() {
    }

    public SalaMensaje(String usuario, String mensaje, String salaCodigo) {
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.salaCodigo = salaCodigo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getSalaCodigo() {
        return salaCodigo;
    }

    public void setSalaCodigo(String salaCodigo) {
        this.salaCodigo = salaCodigo;
    }
}
