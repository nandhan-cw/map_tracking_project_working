package com.example.map_tracking;

public class LocationInfo {
    String latlngupdates, startpoint, detinationpoint,bearing;

    public LocationInfo() {
    }


    public LocationInfo (String latlngupdates, String startpoint, String detinationpoint, String bearing) {
            this.latlngupdates = latlngupdates;
            this.startpoint = startpoint;
            this.detinationpoint = detinationpoint;
            this.bearing = bearing;
        }

    public String getLatlngupdates() {
        return latlngupdates;
    }

    public void setLatlngupdates(String latlngupdates) {
        this.latlngupdates = latlngupdates;
    }

    public String getStartpoint() {
        return startpoint;
    }

    public void setStartpoint(String startpoint) {
        this.startpoint = startpoint;
    }

    public String getDetinationpoint() {
        return detinationpoint;
    }
    public String getBearing() {
        return bearing;
    }

    public void setBearing(String bearing) {
        this.bearing = bearing;
    }

    public void setDetinationpoint(String detinationpoint) {
        this.detinationpoint = detinationpoint;
    }
}
