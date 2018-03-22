package ie.wit.darren.actio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

/**
 * Created by Dazza on 25/02/2018.
 */

public class TrackingActivity extends Fragment {
    Spinner trackingMenuSpinner;
    FloatingActionButton trackingButton;
    ImageView icon;
    public String selection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tracking_screen, container, false);
        icon = (ImageView) v.findViewById(R.id.icon);
        trackingMenuSpinner = (Spinner) v.findViewById(R.id.trackingMenuSpinner);
        trackingButton = (FloatingActionButton) v.findViewById(R.id.trackingButton);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.tracking_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        trackingMenuSpinner.setAdapter(adapter);

        trackingMenuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //spinner.
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if(arg2==0)
                {
                    //Running by Abraham from the Noun Project
                    icon.setImageResource(R.drawable.running_icon);
                    selection = trackingMenuSpinner.getSelectedItem().toString();
                }
                else if(arg2==1){
                    //Cycling by Atif Arshad from the Noun Project
                    icon.setImageResource(R.drawable.cycling_icon);
                    selection = trackingMenuSpinner.getSelectedItem().toString();
                }
                else if(arg3 == 2){
                    //Walking by Samy Menai from the Noun Project
                    icon.setImageResource(R.drawable.walking_icon);
                    selection = trackingMenuSpinner.getSelectedItem().toString();
                }else{
                    //Hiker by Luis Prado from the Noun Project
                    icon.setImageResource(R.drawable.hiking_icon);
                    selection = trackingMenuSpinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

            public String getItem(Spinner spinner){
               return spinner.getSelectedItem().toString();


            }
        });

        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TrackerActivity.class);
                intent.putExtra("activity", selection);
                startActivity(intent);
            }
        });
        return v;
    }
}
