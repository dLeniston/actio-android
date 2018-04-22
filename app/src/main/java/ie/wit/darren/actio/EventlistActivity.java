package ie.wit.darren.actio;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ie.wit.darren.actio.modules.Event;
import ie.wit.darren.actio.modules.EventAdapter;


/**
 * Created by Dazza on 25/02/2018.
 */

public class EventlistActivity extends Fragment implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private List<Event> eventList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EventAdapter mAdapter;
    String reqResponse = "[{}]";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.eventlist_screen, null);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        mAdapter = new EventAdapter(this, eventList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getEvents();
        return v;
    }

    public void onClick(View v) {
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.getActivity());

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this.getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void getEvents(){
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("https")
                            .encodedAuthority("actio-project.herokuapp.com/api/user")
                            .appendEncodedPath(SelectActivity.user)
                            .appendEncodedPath("events")
                            .appendEncodedPath("joined");
                    String url = builder.build().toString();
                    RequestQueue queue = Volley.newRequestQueue(getContext());

                    JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    Log.v("EVENT DATA", response.toString());
                                    reqResponse = response.toString();
                                    Event event;
                                    try {
                                        JSONArray res =  new JSONArray(reqResponse);
                                        Log.v("LOADED ARRAY", res.toString());
                                        for (int i = 0; i < res.length(); i++) {
                                            JSONObject object = res.getJSONObject(i);
                                            String name = object.getString("eventName");
                                            String loc = object.getString("eventLocation");
                                            String date = object.getString("date");
                                            String time = object.getString("time");
                                            JSONObject origin = object.getJSONObject("origin");
                                            String lat = origin.getString("lat");
                                            String lng = origin.getString("lng");

                                            event = new Event(name, loc, date, time, lat, lng);
                                            eventList.add(event);
                                        }
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            }
                    );
                    queue.add(jsonObjectRequest);
                }catch(Exception e){
                    Log.e("ERROR", "Error with JSON object", e);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}

