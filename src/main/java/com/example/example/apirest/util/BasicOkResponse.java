package com.example.example.apirest.util;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BasicOkResponse {
    String codigo = "0";
    String Mensaje = "OK";


    public BasicOkResponse() {
    }


    public BasicOkResponse(String mensaje) {
        this.codigo = codigo;
        Mensaje = mensaje;
    }
}
