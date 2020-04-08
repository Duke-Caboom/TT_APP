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

        if (tipoMensaje.getIdUser().intValue() != ComponentDataBase.getInstance().getIdUser().intValue()
                && existsMessage(tipoMensaje.getIdUser(), tipoMensaje.getIdMensaje())){
            Log.e(getClass().getSimpleName(), "----------------> El mensaje ya fue entregado anteriormente: "+ index);
            return;
        }
        this.mensajes.put(index, tipoMensaje);
        Log.e(getClass().getSimpleName(), "----------------> Indice del mensaje agregado es: "+ index);
        index = index + 1;
    }

    public void addMensajesEnviados(String message) {
        TipoMensaje tipoMensaje = new TipoMensaje(message);

        this.mensajesEnviado.put(indexEnviado, tipoMensaje);
        Log.e(getClass().getSimpleName(), "----------------> Mensaje privado agregado al registro"+ index);
        indexEnviado = indexEnviado + 1;

        this.mensajes.put(index, tipoMensaje);
        Log.e(getClass().getSimpleName(), "----------------> Mensaje agregado a la tabla: "+ index);
        index = index + 1;

        ComponentDataBase.getInstance().addMensaje(tipoMensaje);
    }

    public String getTabla(){
        StringBuilder data = new StringBuilder();
        data.append("");

        int sizeMensajes = mensajes.size();

        if (mensajes.size()!=0){

            for (int i = sizeMensajes; i != 0; i--){
                Log.e(getClass().getSimpleName(), "----------------> Contador(): "+ mensajes.get(i-1).getContador()+10
                        +" Reloj: " +  (Reloj.getInstance().getContador()));
                if (mensajes.get(i-1).getContador()+10 >= Reloj.getInstance().getContador()){
                    Log.e(getClass().getSimpleName(), "----------------> Agregando mensaje a la tabla");
                    data.append(mensajes.get(i-1).getKeyValue());
                    Log.e(getClass().getSimpleName(), "----------------> Se agrego mensaje a la tabla");
                }else{
                    Log.e(getClass().getSimpleName(), "----------------> Regresando mensaje");
                    return data.toString();
                }
                data.append("#_");
            }
        }

        if (data != null && data.toString() != ""){
            data.append("#");
            Log.e(getClass().getSimpleName(), "----------------> Regresando Tabla:" + data.toString().replace("#_#",""));
            return data.toString().replace("#_#","");
        }

        Log.e(getClass().getSimpleName(), "----------------> Tabla vacia");
        return null;
    }

    public boolean tablaRecibida(String tabla){

        String[] mensajesTabla = tabla.split("#_");

        if (mensajesTabla.length != 0){
            for (int i= 1; i < mensajesTabla.length; i++){
                addMensajes(mensajesTabla[i]);
            }
        }else{
            Log.e(getClass().getSimpleName(), "Mamo, quien sabe que mandaron estos mens: " + tabla);
        }

        return true;
    }

    public boolean existsMessage(Integer idUser, String idMensaje){

        if (this.mensajes.size()!= 0){

            Log.e(getClass().getSimpleName(),"Revisando si existe ya el mensaje en la HASMAP");
            for (TipoMensaje temp : this.mensajes.values()) {

                Log.e(getClass().getSimpleName(),"User=" + temp.getIdUser()+
                        ", IdMensa: "+ temp.getIdMensaje());

                if (temp.getIdMensaje().equalsIgnoreCase(idMensaje)){
                    Log.e(getClass().getSimpleName(),"Mismo ID");
                    if (temp.getIdUser().intValue() == idUser.intValue()) {
                        Log.e(getClass().getSimpleName(),"Mismo Usuario");
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public String[] getAdapterMensajes(){
        String mensajeA = "";
        HashMap<Integer, String> data= new HashMap();
        int j=0;
        int sizeMensajes = mensajes.size();

        if (mensajes.size()!=0) {
            int id_mensaje= 0;
            for (int i = sizeMensajes; i != 0; i--) {
                id_mensaje = i -1;
                if (this.mensajes.get(id_mensaje).isPublico()){
                    mensajeA="De: " + this.mensajes.get(id_mensaje).getIdUser()+ "\n"+
                            "Fecha: "+this.mensajes.get(id_mensaje).getFecha()+ " Hora: " + this.mensajes.get(id_mensaje).getHora()+"\n"+
                            this.mensajes.get(id_mensaje).getMensajes();
                    data.put(j, mensajeA);
                    j++;
                }
            }
            String[] dataS= new String[data.size()];
            for (int i=0; i < data.size();i++){
                dataS[i] = data.get(i);
            }
            return dataS;
        }else{
            Log.e(getClass().getSimpleName(), "----------------> No hay menajes para mostrar eseeeee");
            return null;
        }
    }

    public String[] getAdapterMensajesPrivado(){
        String mensajeA = "";
        HashMap<Integer, String> data= new HashMap();
        int j=0;
        int sizeMensajes = mensajes.size();

        if (mensajes.size()!=0) {
            int id_mensaje= 0;
            for (int i = sizeMensajes; i != 0; i--) {
                id_mensaje = i -1;

                if (this.mensajes.get(id_mensaje).isPublico() == false){
                    mensajeA="De: " + this.mensajes.get(id_mensaje).getIdUser()+ "\n"+
                            "Fecha: "+this.mensajes.get(id_mensaje).getFecha()+ " Hora: " + this.mensajes.get(id_mensaje).getHora()+"\n"+
                            this.mensajes.get(id_mensaje).getMensajes();
                    data.put(j, mensajeA);
                    j++;
                }
            }

            Log.e(getClass().getSimpleName(), "----------------> data.size(): "+ data.size());
            String[] dataS= new String[data.size()];

            for (int i=0; i < data.size();i++){
                dataS[i] = data.get(i);
            }
            return dataS;
        }else{
            Log.e(getClass().getSimpleName(), "----------------> No hay menajes para mostrar eseeeee");
            return null;
        }
    }

    public void startApp(){
        Log.e(getClass().getSimpleName(), "----------------> Cargando tabla desde BD");
        HashMap<Integer, TipoMensaje> temp = ComponentDataBase.getInstance().getLatestMessages();
        if(temp != null){
            this.mensajes = temp;
            index = this.mensajes.size();
            Log.e(getClass().getSimpleName(), "----------------> Tabla Cargada");
        }else{
            Log.e(getClass().getSimpleName(), "----------------> Sin Mensajes");
        }
    }
}
