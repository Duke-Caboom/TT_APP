package com.example.usersjava;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TipoMensaje {
    private Long contador;
    private String idUser;
    private String idMensaje;
    private boolean publico;
    private String[] destinatarios;
    private String mensajes;
    private String hora;
    private String fecha;

    public TipoMensaje(Long contador, String idUser, String idMensaje, int publico, String destinatarios, String mensajes, String hora, String fecha) {
        this.contador = contador;
        this.idUser = idUser;
        this.idMensaje = idMensaje;
        this.publico = publico ==1 ? true : false;
        if (publico == 0){
            this.destinatarios =  destinatarios.split(",");
        }
        this.mensajes = mensajes;
        this.hora = hora;
        this.fecha = fecha;
    }

    public TipoMensaje() {
    }


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
                this.idUser = parts[0];
                this.idMensaje= parts[1];
                this.publico= parts[2].equalsIgnoreCase("1") ? true : false;
                this.destinatarios =  parts[3].split(",");
                this.mensajes = parts[4];
            }else if (parts.length ==4){
                Log.i(getClass().getSimpleName(), "Se trata de un mensaje Publico morrito");
                this.idUser = parts[0];
                this.idMensaje= parts[1];
                this.publico= parts[2].equalsIgnoreCase("1") ? true : false;
                this.mensajes = parts[3];
            }else{
                Log.i(getClass().getSimpleName(), "No se que mando este men: " + parts.length);
            }

        }catch (Exception e){
            Log.i(getClass().getSimpleName(), "Error al tratar de parsear el mensaje");
        }
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.fecha = dateFormat.format(date);

        dateFormat = new SimpleDateFormat("HH:mm:ss");
        this.hora = dateFormat.format(date);
    }

    public String getHora() {
        return hora;
    }

    public String getFecha() {
        return fecha;
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

    public String getIdUser() {
        return idUser;
    }

    public boolean isPublico() {
        return publico;
    }

    public String getDestinatarios() {
        String string = "";

        for (String destinatario : destinatarios){
            string = string + destinatario + ",";
        }
        string = string + ",,";
        string.replace(",,,","");

        Log.e(getClass().getSimpleName(), "----------------> destinatario:" + string);
        return string;
    }
}
