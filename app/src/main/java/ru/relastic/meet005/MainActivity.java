package ru.relastic.meet005;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static final String SERVICE_STARTED = "service started";

    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    private TextView mTextView11,mTextView12,mTextView21,mTextView22,mTextView31,mTextView32;
    private Button mButton1,mButton2;

    private final IncomingHandler mHandler = new IncomingHandler();
    private MyService.ProtectedService mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            mService = ((MyService.LocalBinder) service).getProtectedService();
            mService.addListener(mHandler);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v("LOG:", "SERVICE MyService: Stopped");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!(savedInstanceState != null && savedInstanceState.getBoolean(SERVICE_STARTED))) {
            startService(MyService.newIntent(this));
            Log.v("LOG:", "SERVICE MyService: Started");
        }
        initViews();
        initListeners();
        init();
    }
    private void initViews() {
        mTextView11 = findViewById(R.id.textView1_1);
        mTextView12 = findViewById(R.id.textView1_2);
        mTextView21 = findViewById(R.id.textView2_1);
        mTextView22 = findViewById(R.id.textView2_2);
        mTextView31 = findViewById(R.id.textView3_1);
        mTextView32 = findViewById(R.id.textView3_2);

        mButton1 = findViewById(R.id.button1);
        mButton2 = findViewById(R.id.button2);
    }

    private void initListeners() {
        mButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });
        mButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //<...>
            }
        });
    }

    private void init( ) {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hasStatusChangeByBroadcast(intent.getStringExtra(MyService.MSG_SERVICE_VALUE));
            }
        };
        mIntentFilter = new IntentFilter("Meet005");
    }

    private void hasStatusChangeByBroadcast(String newStatus){
        mTextView11.setText(newStatus);
        mTextView21.setText(newStatus);
        mTextView31.setText(newStatus);
        mButton1.setText(newStatus);
    }
    private void hasStatusChangeByBind(String newValue) {
        mTextView12.setText(newValue);
        mTextView22.setText(newValue);
        mTextView32.setText(newValue);
        mButton2.setText(newValue);
        ((ConstraintLayout.LayoutParams)mButton2.getLayoutParams()).circleAngle += (float)3;
    }

    protected void onPause() {
        unregisterReceiver(mReceiver);

        mService.removeListener(mHandler);
        unbindService(mServiceConnection);

        super.onPause();
    }
    @Override
    protected void onResume() {
        registerReceiver(mReceiver, mIntentFilter, null, null);
        bindService(MyService.newIntent(MainActivity.this), mServiceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(SERVICE_STARTED,true);
        savedInstanceState.putString("mTextView11",mTextView11.getText().toString());
        savedInstanceState.putString("mTextView12",mTextView12.getText().toString());
        savedInstanceState.putString("mTextView21",mTextView21.getText().toString());
        savedInstanceState.putString("mTextView22",mTextView22.getText().toString());
        savedInstanceState.putString("mTextView31",mTextView31.getText().toString());
        savedInstanceState.putString("mTextView32",mTextView32.getText().toString());
        savedInstanceState.putString("mButton1",mButton1.getText().toString());
        savedInstanceState.putString("mButton2",mButton2.getText().toString());
        savedInstanceState.putFloat("mButton2_angle",
                ((ConstraintLayout.LayoutParams)mButton2.getLayoutParams()).circleAngle);
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mTextView11.setText(savedInstanceState.getString("mTextView11"));
        mTextView12.setText(savedInstanceState.getString("mTextView12"));
        mTextView21.setText(savedInstanceState.getString("mTextView21"));
        mTextView22.setText(savedInstanceState.getString("mTextView22"));
        mTextView31.setText(savedInstanceState.getString("mTextView31"));
        mTextView32.setText(savedInstanceState.getString("mTextView32"));
        mButton1.setText(savedInstanceState.getString("mButton1"));
        mButton2.setText(savedInstanceState.getString("mButton2"));
        ((ConstraintLayout.LayoutParams)mButton2.getLayoutParams()).circleAngle =
                savedInstanceState.getFloat("mButton2_angle");
        super.onRestoreInstanceState(savedInstanceState);
    }


    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String message = msg.getData().getString(MyService.MSG_SERVICE_RNDVALUE);
            hasStatusChangeByBind(message);
        }
    }
}
