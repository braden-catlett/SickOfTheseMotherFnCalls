package com.idksoftware.sickofthesemotherfncalls;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    final int PERMISSION_READ_STATE = 9879;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        CallReceiver.contacts = CallReceiver.getContactList(this.getApplicationContext());

        ArrayList<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.READ_CONTACTS);
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissions.add(android.Manifest.permission.ACCESS_NOTIFICATION_POLICY);
            }
        }
        if(!permissions.isEmpty()) {
            String[] permissionsValue = new String[permissions.size()];
            permissions.toArray(permissionsValue);
            ActivityCompat.requestPermissions(this, permissionsValue, PERMISSION_READ_STATE);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Grant \"Sick Of These MotherFn Calls\" the permission to alter the Do Not Disturb settings so I may silence phone calls", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Thank you, I'll get to work", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Ok but I won't be able to work correctly", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
