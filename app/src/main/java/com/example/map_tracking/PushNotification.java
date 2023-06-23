package com.example.map_tracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PushNotification extends AppCompatActivity {
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;
    private static final String TAG = "MapsActivity2";
    private static final String CHANNEL_ID = "notification_channel";
    private static final String CHANNEL_NAME = "Notification Channel";
    private static final String CHANNEL_DESCRIPTION = "This is a notification channel";
    private int notificationId = 0;
    Button myButton;
    private NotificationCompat.Builder notificationBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_push_notification);
        myButton =findViewById(R.id.myButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "push: clicked push");
                Toast.makeText(PushNotification.this, "clicked", Toast.LENGTH_SHORT).show();
                notificationManagerCompat.notify(notificationId++, notification);
            }
        });
        createNotificationChannel();

        notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.redcar1)
                .setContentTitle("Notification Title")
                .setContentText("Notification Text")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notification = notificationBuilder.build();
    }

    public void push(View view) {

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    }
