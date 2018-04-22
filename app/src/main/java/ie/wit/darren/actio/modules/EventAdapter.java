package ie.wit.darren.actio.modules;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ie.wit.darren.actio.EventMapActivity;
import ie.wit.darren.actio.R;

/**
 * Created by Dazza on 22/03/2018.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder>{
    View.OnClickListener onClickListener;
    private static List<Event> eventList;
    public Context context;

    public static class  MyViewHolder extends RecyclerView.ViewHolder{
        public TextView event, address, date, time;
        public Context context;

        public MyViewHolder(View view, Context context) {
            super(view);
            event = (TextView) view.findViewById(R.id.event);
            address = (TextView) view.findViewById(R.id.address);
            date = (TextView) view.findViewById(R.id.event_date);
            time = (TextView) view.findViewById(R.id.event_time);

            this.context = context;
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        Event clickedDataItem = eventList.get(pos);
                        Intent intent = new Intent(v.getContext(), EventMapActivity.class);
                        intent.putExtra("event", clickedDataItem.getEvent());
                        intent.putExtra("address", clickedDataItem.getAddress());
                        intent.putExtra("lat", clickedDataItem.getLat());
                        intent.putExtra("lon", clickedDataItem.getLon());
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }

    public EventAdapter(View.OnClickListener onClickListener, List<Event> eventList) {
        this.onClickListener = onClickListener;
        this.eventList = eventList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_row, parent, false);

        return new MyViewHolder(itemView, this.context);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.event.setText(event.getEvent());
        holder.address.setText(event.getAddress());
        holder.date.setText(event.getDate());
        holder.time.setText(event.getTime());
    }

    @Override
    public int getItemCount() {
        return  eventList == null ? 0 : eventList.size();
    }
}
