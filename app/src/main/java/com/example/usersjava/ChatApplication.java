//
// MIT License
//
// Copyright (C) 2018 HypeLabs Inc.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package com.example.usersjava;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.util.Log;

import com.hypelabs.hype.Error;
import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.Message;
import com.hypelabs.hype.MessageInfo;
import com.hypelabs.hype.MessageObserver;
import com.hypelabs.hype.NetworkObserver;
import com.hypelabs.hype.StateObserver;

import java.io.UnsupportedEncodingException;

public class ChatApplication extends BaseApplication implements StateObserver, NetworkObserver, MessageObserver, BaseApplication.LifecycleDelegate {

    public static String announcement = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
    private static final String TAG = ChatApplication.class.getName();

    private boolean isConfigured = false;
    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onApplicationStart(Application app) {
    }

    public void configChatApp(){
        if (!isConfigured){
            Reloj.getInstance().start();
            Mensajes.getInstance().startApp();
            configureHype();
        }
    }

    @Override
    public void onApplicationStop(Application app) {
    }

    //################################################
    //###### CONFIGURE HYPE SDK ENVIRONMENT ##########
    //################################################

    private void configureHype() {
        if (isConfigured)
            return;

        Hype.setContext(getApplicationContext());
        Hype.addStateObserver(this);
        Hype.addNetworkObserver(this);
        Hype.addMessageObserver(this);
        Hype.setTransportType(31);
        //Hype.setUserIdentifier(ComponentDataBase.getInstance().getIdUser());
        Hype.setAppIdentifier("c28a6ca4");

        try {
            Hype.setAnnouncement(ChatApplication.announcement.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Hype.setAnnouncement(null);
            e.printStackTrace();
        }

        Hype.start();
        isConfigured = true;
    }

    @Override
    public String onHypeRequestAccessToken(int i) {
        return "44eae8c7d9eaa712";
    }


    //################################################
    //############# STATE OBSERVER ###################
    //################################################

    public void requestHypeToStart() {
        Hype.start();
    }

    protected void requestHypeToStop() {
        Hype.stop();
    }

    @Override
    public void onHypeStart() {
        Log.e(TAG, "Hype started!");
    }

    @Override
    public void onHypeStop(Error error) {

        String description = "";

        if (error != null) {
            description = String.format("[%s]", error.getDescription());
        }

        Log.e(TAG, String.format("Hype stopped [%s]", description));
    }

    public void onHypeFailedStarting(Error error) {

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

    boolean shouldResolveInstance(Instance instance) {
        // This method can be used to decide whether an instance is interesting.
        return true;
    }

    @Override
    public void onHypeInstanceFound(Instance instance) {

        Log.e(TAG, String.format("Hype found instance: %s", instance.getStringIdentifier()));

        // Resolve the instance, if it is interesting
        if (shouldResolveInstance(instance)) {
            Hype.resolve(instance);
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

        Dispositivos.getInstance().addDispositivo(instance);

        //Enviaremos la tabla de mensajes
        Message message;
        StringBuilder trama = new StringBuilder();
        String tabla;

        if ((tabla = Mensajes.getInstance().getTabla())!= null){
            // Agregamos encabezado
            trama.append("! TABLA");
            trama.append("#_");
            //Obtenemos tabla y agremaos
            trama.append(tabla);

            Log.v(getClass().getSimpleName(), "----------------> TABLA A COMPARTIR: "+trama.toString());

            try {
                byte[] data = trama.toString().getBytes("UTF-8");
                message = Hype.send(data, instance, false);
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

        Log.e(TAG, String.format("Hype got a message from: %s", instance.getStringIdentifier()));

        String cadena = new String();

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
        }else{
            Log.v(getClass().getSimpleName(), "----------------> No se que reciio");
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

    @Override
    public void onCreate() {
        super.onCreate();
        // See BaseApplication.java
        setLifecyleDelegate(this);
    }


}
