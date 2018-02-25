package ie.wit.darren.actio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
/**
 * Created by Dazza on 25/02/2018.
 */

public class SelectActivity extends AppCompatActivity {

    Button activityButton;
    Button eventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_screen);

       activityButton = (Button) findViewById(R.id.activityButton);
       eventButton = (Button) findViewById(R.id.eventButton);

       activityButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view){
               Intent intent = new Intent(SelectActivity.this, TrackingActivity.class);
               startActivity(intent);
           }
       });

       eventButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view){
               Intent intent = new Intent(SelectActivity.this, EventlistActivity.class);
               startActivity(intent);
           }
       });
    }
}
