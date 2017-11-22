package rdm.niazicab;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CustomeNotification extends AppCompatActivity {

    Button bt_notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custome_notification);

        bt_notify = (Button) findViewById(R.id.bt_notify);

        btHandler();


    }

    public void btHandler(){

        bt_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Notification();
            }
        });
    }

    public void Notification() {
        // Set Notification Title
        String strtitle = "Sample Title";
        // Set Notification Text
        String strtext = "Second Sample";

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(this, MainActivity.class);
        // Send data to NotificationView Class
        intent.putExtra("title", strtitle);
        intent.putExtra("text", strtext);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_ONE_SHOT);

        //Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.drawable.time_icon)
                // Set Ticker Message
                .setTicker("Title Ticker")
                // Set Title
                .setContentTitle("content title")
                // Set Text
                .setContentText("Content Text")
                // Add an Action Button below Notification
                .addAction(R.drawable.date_icon, "Action Button", pIntent)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Dismiss Notification
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }

}
