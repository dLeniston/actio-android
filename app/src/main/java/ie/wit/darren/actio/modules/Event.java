package ie.wit.darren.actio.modules;

/**
 * Created by Dazza on 22/03/2018.
 */

public class Event {

    private String event, address, date, time, lat, lon;

    public Event() {
    }

    public Event(String event, String address, String date, String time, String lat, String lon) {
        this.event = event;
        this.address = address;
        this.date = date;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat(){
        return lat;
    }

    public void setLat(String lat){
        this.lat = lat;
    }

    public String getLon(){
        return lon;
    }

    public void setLon(String lon){
        this.lon = lon;
    }
}
