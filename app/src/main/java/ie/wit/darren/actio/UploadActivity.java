package ie.wit.darren.actio;

import android.animation.ObjectAnimator;
import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
//import android.view.View;
import android.view.animation.DecelerateInterpolator;
//import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Dazza on 23/03/2018.
 */

public class UploadActivity extends AppCompatActivity {

    private ProgressBar uploadProgress;
    private TextView uploading;
    private TextView progress;
    private Handler handler = new Handler();
    private int status = 0;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_screen);
        uploading = (TextView) findViewById(R.id.uploading);
        uploadProgress = (ProgressBar) findViewById(R.id.progress_bar);
        progress = (TextView) findViewById(R.id.progress);
        ObjectAnimator animation = ObjectAnimator.ofInt (uploadProgress, "progress", 0, 500); // see this max value coming back here, we animate towards that value
        animation.setDuration (5000); //in milliseconds
        uploadProgress.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.red),
                android.graphics.PorterDuff.Mode.SRC_IN);
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();

        new Thread(new Runnable() {
            public void run() {
                while (status < 100) {
                    status += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            uploadProgress.setProgress(status);
                            progress.setText(status+"%");
                            count = uploadString(uploading, count);

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //ADD CODE HERE TO TAKE USER BACK TO UPLOAD SCREEN
                    }
                }
                if(status == 100){
                    /*new Thread(new Runnable(){
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Upload Completed!", Toast.LENGTH_LONG).show();
                        }
                    });*/
                    Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
                    startActivity(intent);
                }
            }
        }).start();
    }

    public int uploadString(TextView textView, int x){
        String upload = "Uploading";

        if(x == 0){
           textView.setText(upload + ".");
           x++;
        }else if(x == 1){
            textView.setText(upload + "..");
            x++;
        }else if(x == 2){
            textView.setText(upload + "...");
            x++;
        }else if(x == 3){
            textView.setText(upload + "....");
            x = 0;
        }
        return x;
    }
}
