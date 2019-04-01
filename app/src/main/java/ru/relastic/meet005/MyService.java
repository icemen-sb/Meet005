package ru.relastic.meet005;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MyService extends Service {
    public final static String MSG_SERVICE_VALUE = "value";
    public final static String MSG_SERVICE_RNDVALUE = "rnd_value";
    public final static int MODE = Service.START_NOT_STICKY;
    private final static int INTERVAL = 3000;
    private static StateManager sm = StateManager.getInstance();
    private final Random  myRandom = new Random();
    private volatile boolean interrupted = false;
    private IBinder mBinder = new LocalBinder();
    private final List<Handler> mClients = new ArrayList<>();
    private final ProtectedService protectedService = new ProtectedService () {

        @Override
        public void addListener(Handler handler) {
            mClients.add(handler);
        }

        @Override
        public void removeListener(Handler handler) {
            mClients.remove(handler);
        }
    };

    public MyService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                while (!isInterrupted()) {
                    String message = sm.randomState(true).getValue();
                    String messageInt = ((Integer)(myRandom.nextInt(899)+100)).toString();
                    sendBroadcastClients(message);
                    sendBindClients(messageInt);
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        Log.v("LOG:", "ERROR: "+e.toString());
                    }
                }
                Log.v("LOG:", "SERVICE MyService: Stopped");
                stopSelf();
            }
        });
        t.setName("WorkThread");
        t.start();
        return MODE;
    }
    private boolean isInterrupted(){
        return interrupted;
    }
    private void sendBroadcastClients(String message) {
        Intent broadcastIntent = new Intent("Meet005");
        broadcastIntent.putExtra(MSG_SERVICE_VALUE, message);
        broadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(broadcastIntent);
    }
    private void sendBindClients(String message) {
        for (Handler client: mClients) {
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString(MSG_SERVICE_RNDVALUE,message);
            msg.setData(bundle);
            client.sendMessage(msg);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public void interrupt(){
        interrupted = true;
    }
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context,MyService.class);
        return intent;
    }

    public class LocalBinder extends Binder{
        ProtectedService getProtectedService(){
            return protectedService;
        }
    }

    public interface ProtectedService {
        public void addListener(Handler handler);
        public void removeListener(Handler handler);
    }
}
