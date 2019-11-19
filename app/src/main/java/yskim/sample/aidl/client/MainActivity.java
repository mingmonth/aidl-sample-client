package yskim.sample.aidl.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import yskim.sample.aidl.server.IRemoteService;
import yskim.sample.aidl.server.IRemoteCallback;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER_PACKAGE = "yskim.sample.aidl.server";
    private static final String SERVER_ACTION = "yskim.sample.action.aidl.server";

    private final static String TAG = "MainActivity";

    private IRemoteCallback.Stub callback = new IRemoteCallback.Stub() {
        @Override
        public void onUpdate(int value) throws RemoteException {
            Log.d(TAG, "value:" + value);
        }
    };

    private IRemoteService mRemoteService;

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(SERVER_ACTION);
        serviceIntent.setPackage(SERVER_PACKAGE);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText("ABC");

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int sum = mRemoteService.sum(2, 5);
                    mTextView.setText(Integer.toString(sum));
                    Log.e("TEST", "abc:" + sum);
                } catch(RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mRemoteService.unregisterCallback(callback);
        } catch(RemoteException e) {
            Log.e(TAG, "exception", e);
        }
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteService = IRemoteService.Stub.asInterface(service);
            try {
                mRemoteService.registerCallback(callback);
            } catch(RemoteException e) {
                Log.e(TAG, "exception", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteService = null;
        }
    };
}
