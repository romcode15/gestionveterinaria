package com.gestionvet.veterinariaapi.dto;

public class ChatResponseDTO {

    private String  respuesta;
    private boolean exitoso;

    public ChatResponseDTO() {}

    public ChatResponseDTO(String respuesta, boolean exitoso) {
        this.respuesta = respuesta;
        this.exitoso   = exitoso;
    }

    public String  getRespuesta()         { return respuesta; }
    public void    setRespuesta(String r) { this.respuesta = r; }

    public boolean isExitoso()            { return exitoso; }
    public void    setExitoso(boolean e)  { this.exitoso = e; }
}
