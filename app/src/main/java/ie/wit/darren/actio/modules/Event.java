package ie.wit.darren.actio.modules;

/**
 * Created by Dazza on 22/03/2018.
 */

public class Event {

    private String event, address, lat, lon;

    public Event() {
    }

    public Event(String event, String address, String lat, String lon) {
        this.event = event;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
       //this.year = year;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    /*public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }*/

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
