package jp.obanet.gpsreminder;

import java.io.Serializable;

public class CheckedPlace implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long id;
    private double lat;
    private double lng;
    private int distance;
    private float zoom;
    private String name;
    private String memo;
    private int notified;
    private float bearing;
    private float tilt;

    public CheckedPlace(Long id, String name, double lat, double lng, int distance, float zoom, String memo, float bearing, float tilt, int notified){
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.zoom = zoom;
        this.memo = memo;
        this.bearing = bearing;
        this.tilt = tilt;
        this.notified = notified;
    }

    public CheckedPlace(String name, double lat, double lng, int distance, float zoom, String memo, float bearing, float tilt){
        this(null, name, lat, lng, distance, zoom, memo, bearing, tilt, 0);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public String getMemo() {
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String toString(){
        return this.name;
    }

    public int getNotified() {
        return notified;
    }

    public void setNotified(int notified) {
        this.notified = notified;
    }

    public boolean isNotified(){
        return this.notified == 1;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getTilt() {
        return tilt;
    }

    public void setTilt(float tilt) {
        this.tilt = tilt;
    }
}
