package ie.wit.darren.actio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Dazza on 22/03/2018.
 */

public class TrackingCompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_complete_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tracking_complete_toolbar);

        ImageView icon = (ImageView) findViewById(R.id.tracking_complete_icon);

       TextView time = (TextView) findViewById(R.id.time);
       TextView distanceDone = (TextView) findViewById(R.id.distance_done);
       TextView activityType = (TextView) findViewById(R.id.activity_type);

       Intent intent = getIntent();
       String totalTime = intent.getStringExtra("time");
       String totalDistance = intent.getStringExtra("distance");
       String type = intent.getStringExtra("type");

       time.setText(totalTime);
       distanceDone.setText(totalDistance);
       setIcon(icon, type);



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Activity Complete!");

    }

    public void setIcon(ImageView image, String string){
        switch(string){
            case "running":
                image.setImageResource(R.drawable.running_icon);
                break;
            case "cycling":
                image.setImageResource(R.drawable.cycling_icon);
                break;
            case "walking":
                image.setImageResource(R.drawable.walking_icon);
                break;
            case "hiking":
                image.setImageResource(R.drawable.hiking_icon);
                break;
        }
    }
}
