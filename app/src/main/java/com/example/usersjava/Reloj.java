package com.example.usersjava;

public class Reloj implements Runnable {

    private static final Reloj instance = new Reloj();
    private Long contador;
    private boolean isAlive;

    public static Reloj getInstance() {
        return instance;
    }

    private Reloj() {
        contador = 0L;
        isAlive = false;
    }

    public Long getContador(){
        return contador;
    }


    @Override
    public void run() {

        while (true){
            try {

                Thread.sleep(60000);
                contador ++;

                if (contador == 10800){
                    contador = 0L;
                }
                //Log.v(getClass().getSimpleName(), "Contador: "+contador);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void start() {
        if (!isAlive){
            Thread t = new Thread(this);
            t.start();
        }
    }
}
