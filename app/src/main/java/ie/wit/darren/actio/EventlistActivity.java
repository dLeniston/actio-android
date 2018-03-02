package ie.wit.darren.actio;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dazza on 25/02/2018.
 */

public class EventlistActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }


        @Override
        protected void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.eventlist_screen);

            if (isServicesOK()) {
                init();
            }
        }


            public void init(){
            try {
                ListView eventList = (ListView) findViewById(R.id.event_list);
                List<String> items = new ArrayList<>();
                Context context = null;
                JSONObject obj = new JSONObject(loadJSONFromAsset());
                JSONArray array = obj.getJSONArray("events");
                this.setTitle(obj.getString("title"));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String event = object.getString("event");
                    String loc = object.getString("location");
                    String lat = object.getString("lat");
                    String lng = object.getString("lng");
                    String full = event + '\n' + loc + '\n' + lat + '\n' + lng;
                    items.add(full);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
                if (eventList != null) {
                    eventList.setAdapter(adapter);

                    eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String itemChosen = (String) parent.getItemAtPosition(position);
                            Intent intent = new Intent(EventlistActivity.this, EventMapActivity.class);
                            intent.putExtra("event", itemChosen);
                            startActivity(intent);
                        }
                    });
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
    }

            public boolean isServicesOK(){
                Log.d(TAG, "isServicesOK: checking google services version");

                int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(EventlistActivity.this);

                if(available == ConnectionResult.SUCCESS){
                    //everything is fine and the user can make map requests
                    Log.d(TAG, "isServicesOK: Google Play Services is working");
                    return true;
                }
                else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
                    //an error occured but we can resolve it
                    Log.d(TAG, "isServicesOK: an error occured but we can fix it");
                    Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(EventlistActivity.this, available, ERROR_DIALOG_REQUEST);
                    dialog.show();
                }else{
                    Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

        }
