package com.example.usersjava;

import android.util.Log;

public class TipoMensaje {
    private Long contador;
    private Integer idUser;
    private String idMensaje;
    private boolean publico;
    private String[] destinatarios;
    private String mensajes;


    public String getIdMensaje() {
        return idMensaje;
    }

    public TipoMensaje (String dataMensaje){
        this.contador = Reloj.getInstance().getContador();

        try {
            dataMensaje = dataMensaje.replace("! MESAJE!_","");

            String[] parts = dataMensaje.split("!_");

            for (int i = 0 ; i < parts.length; i++){
                Log.v(getClass().getSimpleName(), "----------------> Parte "+i + ": "+ parts[i]);
            }

            if (parts.length == 5){
                Log.i(getClass().getSimpleName(), "Se trata de un mensaje privado morrito");
                this.idUser = Integer.valueOf(parts[0]);
                this.idMensaje= parts[1];
                this.publico= parts[2].equalsIgnoreCase("1") ? true : false;
                this.destinatarios =  parts[3].split(",");
                this.mensajes = parts[4];
            }else if (parts.length ==4){
                Log.i(getClass().getSimpleName(), "Se trata de un mensaje Publico morrito");
                this.idUser = Integer.valueOf(parts[0]);
                this.idMensaje= parts[1];
                this.publico= parts[2].equalsIgnoreCase("1") ? true : false;
                this.mensajes = parts[3];
            }else{
                Log.i(getClass().getSimpleName(), "No se que mando este men: " + parts.length);
            }

        }catch (Exception e){
            Log.i(getClass().getSimpleName(), "Error al tratar de parsear el mensaje");
        }
    }

    public String getKeyValue(){
        StringBuilder data = new StringBuilder();

        data.append(this.idUser);
        data.append("!_");
        data.append(this.idMensaje);
        data.append("!_");
        data.append(this.publico == true ? "1" : "0");

        if (!this.publico){
            data.append("!_");
            if (!this.isPublico()){
                for (int i=0; i < this.destinatarios.length; i++){
                    data.append(this.destinatarios[i]);
                    if (i+1 != this.destinatarios.length) {
                        data.append(",");
                    }
                }
            }
        }

        data.append("!_");
        data.append(this.mensajes);
        return data.toString();
    }

    public Long getContador(){
        return this.contador;
    }

    public String getMensajes() {
        return mensajes;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public boolean isPublico() {
        return publico;
    }
}
