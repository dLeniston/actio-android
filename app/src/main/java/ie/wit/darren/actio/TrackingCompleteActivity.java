package ie.wit.darren.actio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dazza on 22/03/2018.
 */

public class TrackingCompleteActivity extends AppCompatActivity{

    String totalTime;
    String totalDistance;
    String activityType;
    String date;
    String points;
    ProgressBar uploadProgress;
    TextView time;
    TextView timeText;
    TextView distanceDone;
    TextView distanceText;
    Button continueButton;
    FloatingActionButton uploadButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_complete_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tracking_complete_toolbar);

        ImageView icon = (ImageView) findViewById(R.id.tracking_complete_icon);

       time = (TextView) findViewById(R.id.time);
       distanceDone = (TextView) findViewById(R.id.distance_done);
       continueButton = (Button) findViewById(R.id.continue_button);
       uploadButton = (FloatingActionButton) findViewById(R.id.submit_activity_button);
       uploadProgress = (ProgressBar) findViewById(R.id.upload_progress);
       uploadProgress.setVisibility(View.INVISIBLE);

       timeText = (TextView) findViewById(R.id.time_text);
       distanceText = (TextView) findViewById(R.id.distance_text);

       Intent intent = getIntent();
       totalTime = intent.getStringExtra("time");
       totalDistance = intent.getStringExtra("distance");
       activityType = intent.getStringExtra("type");
       points = intent.getStringExtra("points");
       date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

       time.setText(totalTime);
       distanceDone.setText(totalDistance);
       setIcon(icon, activityType);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Activity Complete!");

        continueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    time.setVisibility(View.INVISIBLE);
                    distanceDone.setVisibility(View.INVISIBLE);
                    continueButton.setVisibility(View.INVISIBLE);
                    uploadButton.setVisibility(View.INVISIBLE);
                    distanceText.setVisibility(View.INVISIBLE);
                    timeText.setVisibility(View.INVISIBLE);
                    uploadProgress.setVisibility(View.VISIBLE);
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    String URL = "https://actio-project.herokuapp.com/api/activities";
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("type", activityType);
                    jsonBody.put("time", totalTime);
                    jsonBody.put("date", date);
                    jsonBody.put("distance", totalDistance);
                    jsonBody.put("polylinePoints", points);
                    jsonBody.put("id", SelectActivity.user);
                    jsonBody.put("username", SelectActivity.username);
                    final String requestBody = jsonBody.toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("VOLLEY", response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", error.toString());
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                return null;
                            }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString = "";
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                            }
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };

                    requestQueue.add(stringRequest);
                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                        @Override
                        public void onRequestFinished(Request<String> request) {
                            if (uploadProgress != null)
                                uploadProgress.setVisibility(View.INVISIBLE);

                            Intent intent = new Intent(TrackingCompleteActivity.this, SelectActivity.class);
                            startActivityForResult(intent, 0);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
