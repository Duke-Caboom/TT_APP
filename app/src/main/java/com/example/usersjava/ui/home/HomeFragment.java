package com.example.usersjava.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.usersjava.ComponentDataBase;
import com.example.usersjava.Dispositivos;
import com.example.usersjava.Mensajes;
import com.example.usersjava.R;
import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.Message;

import java.io.UnsupportedEncodingException;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;

    private RadioButton radioPublica, radioPrivada;
    private EditText inputMensaje;
    private Button buttonEnviar;
    private TextView textMSG1;
    private TextView textMSG2;
    private TextView textMSG4;
    private TextView textMSG5;
    private TextView textMSG6;

    private double latitud, longitud;
    private LocationManager ubicacion;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //Inicializamos los elemntos creados
        initElements(root);
        //Definimos los eventos para cada elemento que se ocupe
        setButtonListeners();
        return root;
    }

    //################################################
    //###### CONFIGURANDO ELEMENTOS DEL MAIN #########
    //################################################

    private void initElements(View root) {
        radioPrivada = root.findViewById(R.id.radioPrivada);
        radioPublica = root.findViewById(R.id.radioPublica);
        inputMensaje = root.findViewById(R.id.editAyuda);
        buttonEnviar = (Button) root.findViewById(R.id.buttonAgregar);
        textMSG1 = root.findViewById(R.id.Text);
        textMSG2 = root.findViewById(R.id.msg2);
        textMSG4 = root.findViewById(R.id.msg4);
        textMSG5 = root.findViewById(R.id.msg5);
        textMSG6 = root.findViewById(R.id.msg6);

        localizacion();
        registrarLocalizacion();

    }

    private void setButtonListeners() {
        //Click en el boton enviar
        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
            }
        });

        //Click en los mensajes rapidos
        textMSG1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMensaje.append(textMSG1.getText().toString().replace("■ ", "") + " ");
            }
        });
        textMSG2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMensaje.append(textMSG2.getText().toString().replace("■ ", "") + " ");
            }
        });
        textMSG4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMensaje.append(textMSG4.getText().toString().replace("■ ", "") + " ");
            }
        });
        textMSG5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMensaje.append(textMSG5.getText().toString().replace("■ ", "") + " ");
            }
        });
        textMSG6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMensaje.append(textMSG6.getText().toString().replace("■ ", "") + " ");
            }
        });
    }

    //################################################
    //###### METODO PARA EL ENNVIO DE MENSAJES #######
    //################################################

    private void enviarMensaje() {
        String text = inputMensaje.getText().toString();
        text = text.replace("\n", "|!");
        Log.v(TAG, "##### Texto: " + text);
        //Si el mensaje es nulo o esta vacio no se manda
        if (text == null || text.length() == 0)
            return;

        boolean error;

        //Evaluamos si el radio button de envio provado esta activo
        if (radioPrivada.isChecked()) {
            Log.v(TAG, "##### Mensaje privado seleccionado");
            error = sendMessagePrivado(text);
        } else {
            Log.v(TAG, "##### Mensaje PUBLICO seleccionado");
            error = sendMessagePublico(text);
        }

        //Evaluamos si el metodo de enviar arrojo algun error
        if (!error) {
            Log.v(TAG, "##### Se presento un error al enviar mensaje");
            return;
        }

        //Limpiamos la entrada del mensaje
        inputMensaje.setText("");
        Log.v(TAG, "##### Envio correcto");
        Toast.makeText(getActivity(), "Envio exitoso!", Toast.LENGTH_LONG).show();
    }

    protected boolean sendMessagePrivado(String text) {
        Message message;
        StringBuilder trama = new StringBuilder();

        if (ComponentDataBase.getInstance().getContactos().equalsIgnoreCase("")) {
            Log.e(TAG, "##### ----------------> No tiene contactos agregados");
            Toast.makeText(getActivity(), "No tienes contactos de confianza agregados", Toast.LENGTH_LONG).show();
            return false;
        }

        //Construccion de la trama publica

        //idUSer
        trama.append("! MESAJE");
        trama.append("!_");
        Log.v(TAG, "##### ----------------> 0 TRAMA: " + trama.toString());

        //idUSer
        trama.append(ComponentDataBase.getInstance().getIdUser());
        trama.append("!_");
        Log.v(TAG, "##### ----------------> 1 TRAMA: " + trama.toString());

        //idMensaje
        int temp = ComponentDataBase.getInstance().getLastIdMensaje() + 1;
        ComponentDataBase.getInstance().setLastIdMensaje(temp);
        trama.append(temp);
        trama.append("!_");
        Log.v(TAG, "##### ----------------> 2 TRAMA: " + trama.toString());

        //Indicador publico
        trama.append("0");
        trama.append("!_");
        Log.v(TAG, "##### ----------------> 3 TRAMA: " + trama.toString());

        //Agregado destinatarios
        trama.append(ComponentDataBase.getInstance().getContactos());
        trama.append("!_");
        Log.v(TAG, "##### ----------------> 4 TRAMA: " + trama.toString());
        //texto
        trama.append(text);
        Log.v(TAG, "##### ----------------> 5 TRAMA: " + trama.toString());

        //Convierte trama a Bytes
        byte[] data;
        try {
            data = trama.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.v(TAG, "#####  Error al convertir la trama: " + trama.toString());
            e.printStackTrace();
            return false;
        }

        Instance[] d = Dispositivos.getInstance().getDispositivos();

        for (int i = 0; i < d.length; i++) {
            message = Hype.send(data, d[i], false);
        }
        Mensajes.getInstance().addMensajesEnviados(trama.toString());
        //Log.v(TAG, "##### El mensaje que salio fue: " + new String(message.getData(), "UTF-8"));
        return true;
    }

    private boolean sendMessagePublico(String text) {
        Message message;

        StringBuilder trama = new StringBuilder();

        //Construccion de la trama publica
        //idUSer
        trama.append("! MESAJE");
        trama.append("!_");
        Log.v(TAG, "##### ----------------> 0 TRAMA: " + trama.toString());

        //idUSer
        trama.append(ComponentDataBase.getInstance().getIdUser());
        trama.append("!_");
        Log.v(TAG, "##### ----------------> 1 TRAMA: " + trama.toString());

        //idMensaje
        int temp = ComponentDataBase.getInstance().getLastIdMensaje() + 1;
        ComponentDataBase.getInstance().setLastIdMensaje(temp);
        trama.append(temp);
        trama.append("!_");
        Log.v(TAG, "##### ----------------> 2 TRAMA: " + trama.toString());

        //Indicador publico
        trama.append("1");
        trama.append("!_");
        Log.v(TAG, "##### ----------------> 3 TRAMA: " + trama.toString());

        //Mensaje
        trama.append(text);
        trama.append("|!Localizacion Apox: " + String.valueOf(latitud) + "," + String.valueOf(longitud));
        Log.v(TAG, "##### ----------------> 4 TRAMA: " + trama.toString());

        //Convierte trama a Bytes
        byte[] data;
        try {
            data = trama.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.v(TAG, "#####  Error al convertir la trama: " + trama.toString());
            e.printStackTrace();
            return false;
        }

        Instance[] d = Dispositivos.getInstance().getDispositivos();

        for (Instance ins : d){
            Log.v(TAG, "#####  Dispositivos: " + ins.getUserIdentifier());
        }



        for (int i = 0; i < d.length; i++) {
            Log.v(TAG, "##### Enviando a: "+ d[i].getUserIdentifier() );
            message = Hype.send(data, d[i], false);
        }

        Mensajes.getInstance().addMensajesEnviados(trama.toString());
        //Log.v(TAG, "##### El mensaje que salio fue: " + new String(message.getData(), "UTF-8"));
        return true;
    }

    private void localizacion() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1000);
        }

        ubicacion = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = ubicacion.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location != null) {
            Log.d("Latitud", String.valueOf(location.getLatitude()));
            Log.d("Longitud", String.valueOf(location.getLongitude()));
        }

    }

    private void registrarLocalizacion() {
        ubicacion = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ubicacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, new miLocalizacionListener());
    }


    private class miLocalizacionListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}