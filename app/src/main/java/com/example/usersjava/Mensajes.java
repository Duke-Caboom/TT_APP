package com.example.usersjava;

import android.util.Log;

import java.util.HashMap;

public class Mensajes {
    private static final Mensajes instance = new Mensajes();

    private HashMap<Integer, TipoMensaje> mensajes;
    private HashMap<Integer, TipoMensaje> mensajesEnviado;
    private Integer index;
    private Integer indexEnviado;
    private String tabla;

    public static Mensajes getInstance() {
        return instance;
    }

    private Mensajes() {
        mensajes = new HashMap<>();
        mensajesEnviado = new HashMap<>();
        index = 0;
        indexEnviado = 0;
        tabla = new String();
    }

    public void addMensajes(String message) {
        TipoMensaje tipoMensaje = new TipoMensaje(message);

        if (tipoMensaje.getIdUser().equalsIgnoreCase(ComponentDataBase.getInstance().getIdUser())
                || existsMessage(tipoMensaje.getIdUser(), tipoMensaje.getIdMensaje())) {
            Log.e(getClass().getSimpleName(), "----------------> El mensaje ya fue entregado anteriormente");
            return;
        }
        this.mensajes.put(index, tipoMensaje);
        Log.e(getClass().getSimpleName(), "----------------> Indice del mensaje agregado es: " + index);
        index = index + 1;

        ComponentDataBase.getInstance().addMensaje(tipoMensaje);

        if (tipoMensaje.isPublico()){
            Log.e(getClass().getSimpleName(), "----------------> Notificando mensaje Puiblico ");
            MainActivity.getDefaultInstance().notificacion();
        }else if(tipoMensaje.getDestinatarios().contains(ComponentDataBase.getInstance().getIdUser())){
            Log.e(getClass().getSimpleName(), "----------------> Notificando mensaje privado ");
            MainActivity.getDefaultInstance().notificacion();
        }else{
            Log.e(getClass().getSimpleName(), "----------------> Es un mensaje privado pero no soy el receptor ");
        }
    }

    public void addMensajesEnviados(String message) {
        TipoMensaje tipoMensaje = new TipoMensaje(message);

        this.mensajesEnviado.put(indexEnviado, tipoMensaje);
        indexEnviado = indexEnviado + 1;

        this.mensajes.put(index, tipoMensaje);
        index = index + 1;
        ComponentDataBase.getInstance().addMensaje(tipoMensaje);
    }

    public String getTabla() {
        StringBuilder data = new StringBuilder();
        data.append("");

        int sizeMensajes = mensajes.size();

        if (mensajes.size() != 0) {

            for (int i = sizeMensajes; i != 0; i--) {
                if (mensajes.get(i - 1).getContador() + 10 >= Reloj.getInstance().getContador()) {
                    data.append(mensajes.get(i - 1).getKeyValue());
                } else {
                    return data.toString();
                }
                data.append("#_");
            }
        }

        if (data != null && data.toString() != "") {
            data.append("#");
            Log.e(getClass().getSimpleName(), "----------------> Regresando Tabla:" + data.toString().replace("#_#", ""));
            return data.toString().replace("#_#", "");
        }

        Log.e(getClass().getSimpleName(), "----------------> Tabla vacia");
        return null;
    }

    public boolean tablaRecibida(String tabla) {

        String[] mensajesTabla = tabla.split("#_");

        if (mensajesTabla.length != 0) {
            for (int i = 1; i < mensajesTabla.length; i++) {
                addMensajes(mensajesTabla[i]);
            }
        } else {
            Log.e(getClass().getSimpleName(), "Mamo, quien sabe que mandaron estos mens: " + tabla);
        }

        return true;
    }

    public boolean existsMessage(String idUser, String idMensaje) {

        if (this.mensajes.size() != 0) {

            Log.e(getClass().getSimpleName(), "Revisando si existe ya el mensaje en la HASMAP");
            for (TipoMensaje temp : this.mensajes.values()) {
                if (temp.getIdMensaje().equalsIgnoreCase(idMensaje)) {
                    Log.e(getClass().getSimpleName(), "Mismo ID");
                    if (temp.getIdUser().equalsIgnoreCase(idUser)) {
                        Log.e(getClass().getSimpleName(), "Mismo Usuario");
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public String[] getAdapterMensajes() {
        String mensajeA = "";
        HashMap<Integer, String> data = new HashMap();
        int j = 0;
        int sizeMensajes = this.mensajes.size();
        if (this.mensajes.size() != 0) {
            int id_mensaje = 0;
            for (int i = sizeMensajes; i != 0; i--) {
                id_mensaje = i - 1;
                if (this.mensajes.get(id_mensaje).isPublico() &&
                        !this.mensajes.get(id_mensaje).getIdUser().equalsIgnoreCase(ComponentDataBase.getInstance().getIdUser())) {

                    mensajeA = this.mensajes.get(id_mensaje).getIdUser() +"-"+ this.mensajes.get(id_mensaje).getIdMensaje()+"\n" +
                            "De: " + this.mensajes.get(id_mensaje).getIdUser() + "\n" +
                            "Fecha: " + this.mensajes.get(id_mensaje).getFecha() + " Hora: " + this.mensajes.get(id_mensaje).getHora() + "\n" +
                            this.mensajes.get(id_mensaje).getMensajes().replace("|!", "\n");
                    data.put(j, mensajeA);
                    j++;
                }
            }
            String[] dataS = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                dataS[i] = data.get(i);
            }
            return dataS;
        } else {
            Log.e(getClass().getSimpleName(), "----------------> No hay menajes publicos para mostrar");
            return null;
        }
    }

    public String[] getAdapterMensajesPrivado() {
        String mensajeA = "";
        HashMap<Integer, String> data = new HashMap();
        int j = 0;
        int sizeMensajes = mensajes.size();

        if (mensajes.size() != 0) {
            int id_mensaje = 0;
            for (int i = sizeMensajes; i != 0; i--) {
                id_mensaje = i - 1;

                if (this.mensajes.get(id_mensaje).isPublico() == false &&
                        this.mensajes.get(id_mensaje).getDestinatarios().contains(ComponentDataBase.getInstance().getIdUser()) &&
                        !this.mensajes.get(id_mensaje).getIdUser().equalsIgnoreCase(ComponentDataBase.getInstance().getIdUser())) {
                    String[] msg = this.mensajes.get(id_mensaje).getMensajes().split("Estoy en: ");
                    mensajeA = this.mensajes.get(id_mensaje).getIdUser() +"-"+ this.mensajes.get(id_mensaje).getIdMensaje()+"\n" +
                            "\tDe: " + this.mensajes.get(id_mensaje).getIdUser() + "\n" +
                            "\tRecibido: " + this.mensajes.get(id_mensaje).getFecha() + "-" + this.mensajes.get(id_mensaje).getHora() + "\n" +
                            "\t"+msg[0];
                    data.put(j, mensajeA);
                    j++;
                }
            }
            String[] dataS = new String[data.size()];

            for (int i = 0; i < data.size(); i++) {
                dataS[i] = data.get(i);
            }
            return dataS;
        } else {
            Log.e(getClass().getSimpleName(), "----------------> No hay menajes Privados para mostrar");
            return null;
        }
    }

    public synchronized void startApp() {
        HashMap<Integer, TipoMensaje> temp = ComponentDataBase.getInstance().getLatestMessages();
        if (temp != null) {

            this.mensajes = temp;
            index = temp.size();
            Log.e(getClass().getSimpleName(), "----------------> Tabla Cargada");
        } else {
            Log.e(getClass().getSimpleName(), "----------------> Sin Mensajes");
        }
    }

    public TipoMensaje getMensaje(String user, String idMensaje){

        for (HashMap.Entry<Integer, TipoMensaje> entry : this.mensajes.entrySet()) {
            if (entry.getValue().getIdMensaje().equalsIgnoreCase(idMensaje)
                    && entry.getValue().getIdUser().equalsIgnoreCase(user)){
                return entry.getValue();
            }
        }
        return null;
    }
}
