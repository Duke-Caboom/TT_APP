package com.example.usersjava;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.hypelabs.hype.Error;
import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.Message;
import com.hypelabs.hype.MessageInfo;
import com.hypelabs.hype.MessageObserver;
import com.hypelabs.hype.NetworkObserver;
import com.hypelabs.hype.StateObserver;
import com.hypelabs.hype.TransportType;

import java.io.UnsupportedEncodingException;

public class ChatApplication implements StateObserver, NetworkObserver, MessageObserver{
    public static String announcement = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
    private static final String TAG = ChatApplication.class.getName();
    private boolean isConfigured = false;
    private Activity activity;
    private static ChatApplication instance = new ChatApplication();

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public static ChatApplication getInstance() {
        return instance;
    }

    private ChatApplication(){

    }

    //################################################
    //###### CONFIGURE HYPE SDK ENVIRONMENT ##########
    //################################################

    @Override
    public String onHypeRequestAccessToken(int i) {
        return "3b7786aa9e90b32a";
    }

    //################################################
    //############# STATE OBSERVER ###################
    //################################################

    public void requestHypeToStart() {
        Reloj.getInstance().start();
        Mensajes.getInstance().startApp();

        Hype.setContext(this.activity);
        Hype.addStateObserver(this);
        Hype.addNetworkObserver(this);
        Hype.addMessageObserver(this);
        Hype.setTransportType(TransportType.BLUETOOTH_LOW_ENERGY);
        Hype.setUserIdentifier(Integer.valueOf(ComponentDataBase.getInstance().getIdUser().substring(1,10)));
        Hype.setAppIdentifier("89a32a5d");

        try {
            Hype.setAnnouncement(ChatApplication.announcement.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Hype.setAnnouncement(null);
            e.printStackTrace();
        }

        MainActivity mainActivity = MainActivity.getDefaultInstance();
        mainActivity.requestPermissions();

        Hype.start();
    }

    protected void requestHypeToStop() {
        Hype.stop();
    }

    @Override
    public void onHypeStart() {
        isConfigured = true;
        Log.e(TAG, "Hype started!");
    }

    @Override
    public void onHypeStop(Error error) {
        requestHypeToStop();
        String description = "";

        if (error != null) {
            description = String.format("[%s]", error.getDescription());
        }

        Log.e(TAG, String.format("Hype stopped [%s]", description));
    }

    public void onHypeFailedStarting(Error error) {
        isConfigured = false;
        Log.e(TAG, String.format("Hype failed starting [%s]", error.getDescription()));

        // Obtain information of error
        final String failedMsg = error == null ? "" : String.format("Suggestion: %s\nDescription: %s\nReason: %s",
                error.getSuggestion(), error.getDescription(), error.getReason());

        // Prints an Error message to the application screen
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Hype failed starting");
                builder.setMessage(failedMsg);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
            }
        });
    }

    @Override
    public void onHypeReady() {
        Log.e(TAG, String.format("Hype is ready"));
        requestHypeToStart();
    }

    @Override
    public void onHypeStateChange() {
        Log.e(TAG, String.format("Hype changed state to [%d] (Idle=0, Starting=1, Running=2, Stopping=3)", Hype.getState().getValue()));
    }

    //################################################
    //########### NETWORK OBSERVER ###################
    //################################################

    @Override
    public void onHypeInstanceFound(Instance instance) {

        Log.e(TAG, String.format("Hype found instance: %s", instance.getStringIdentifier()));

        // Resolve the instance, if it is interesting
        if (!instance.isResolved()) {
            Log.e(TAG, String.format("Resolviendo instancia"));
            Hype.resolve(instance);
        }else{
            Log.e(TAG, String.format("Ya resuelto"));
            addToResolvedInstancesMap(instance);
        }
    }

    @Override
    public void onHypeInstanceLost(Instance instance, Error error) {

        Log.e(TAG, String.format("Hype lost instance: %s [%s]", instance.getStringIdentifier(), error.getDescription()));
        // Remove lost instance from resolved instances
        removeFromResolvedInstancesMap(instance);
    }

    @Override
    public void onHypeInstanceResolved(Instance instance) {
        Log.e(TAG, String.format("Hype resolved instance: %s", instance.getStringIdentifier()));
        // Add found instance to resolved instances
        addToResolvedInstancesMap(instance);
    }

    @Override
    public void onHypeInstanceFailResolving(Instance instance, Error error) {
        Log.e(TAG, String.format("Hype failed resolving instance: %s [%s]", instance.getStringIdentifier(), error.getDescription()));
    }

    public void addToResolvedInstancesMap(Instance instance) {

        Dispositivos.getInstance().replaceDispositivo(instance);

        sendTabla(0,instance);
    }

    private void sendTabla(int i, Instance instance) {
        //Enviaremos la tabla de mensajes
        StringBuilder trama = new StringBuilder();
        String tabla;

        if ((tabla = Mensajes.getInstance().getTabla()) != null) {
            // Agregamos encabezado
            trama.append("! TABLA");
            trama.append("#_");
            //Obtenemos tabla y agremaos
            trama.append(tabla);

            Log.v(getClass().getSimpleName(), "----------------> TABLA A COMPARTIR: " + trama.toString());

            try {


                if(i == 0){
                    byte[] data = trama.toString().getBytes("UTF-8");
                    Hype.send(data, instance, false);
                }else{
                    Instance[] d = Dispositivos.getInstance().getDispositivos();

                    byte[] data = trama.toString().replace("TABLA","TABLA2").getBytes("UTF-8");
                    for (int j = 0; j < d.length; j++) {
                        if (d[j].getUserIdentifier() != Long.valueOf(instance.getUserIdentifier()))
                         Hype.send(data, d[j], false);
                    }
                }

            } catch (Exception e) {
                Log.v(getClass().getSimpleName(), "----------------> ALGO FALLO AL COMPARTIR LA TABLA");
                e.printStackTrace();
            }
        }
    }

    public void removeFromResolvedInstancesMap(Instance instance) {
        Dispositivos.getInstance().deleteDispositivo(instance);
    }

    //################################################
    //########### MESSAGE OBSERVER ###################
    //################################################

    @Override
    public void onHypeMessageReceived(Message message, Instance instance) {

        Dispositivos.getInstance().replaceDispositivo(instance);

        Log.e(TAG, String.format("Hype got a message from:"));

        String cadena;

        try {

            cadena = new String(message.getData(), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        Log.e(TAG, String.format("Mensaje: " + cadena));

        if (cadena.contains("! TABLA")) {
            Log.v(getClass().getSimpleName(), "----------------> Tabla recibida");
            Mensajes.getInstance().tablaRecibida(cadena);

        } else if (cadena.contains("! MESAJE")) {
            Log.v(getClass().getSimpleName(), "----------------> Mensaje recibido");
            Mensajes.getInstance().addMensajes(cadena);
        } else {
            Log.v(getClass().getSimpleName(), "----------------> No se que reciio");
        }

        if (!cadena.contains("! TABLA2")){
            this.activity.runOnUiThread(() -> sendTabla(1,instance));
        }
    }

    @Override
    public void onHypeMessageFailedSending(MessageInfo messageInfo, Instance instance, Error error) {

        Log.e(TAG, String.format("Hype failed to send message: %d [%s]", messageInfo.getIdentifier(), error.getDescription()));
    }

    @Override
    public void onHypeMessageSent(MessageInfo messageInfo, Instance instance, float progress, boolean done) {

        Log.e(TAG, String.format("Hype is sending a message: %f", progress));
    }

    @Override
    public void onHypeMessageDelivered(MessageInfo messageInfo, Instance instance, float progress, boolean done) {

        Log.e(TAG, String.format("Hype delivered a message: %f", progress));
    }

    //################################################
    //########### END OF SDK INTEGRATION #############
    //################################################

}
