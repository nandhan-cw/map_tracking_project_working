package com.example.map_tracking;

public class LocationInfo {
    String latlngupdates, startpoint, detinationpoint;

    public LocationInfo() {
    }

    public LocationInfo (String latlngupdates, String startpoint, String detinationpoint) {
            this.latlngupdates = latlngupdates;
            this.startpoint = startpoint;
            this.detinationpoint = detinationpoint;
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

    public void setDetinationpoint(String detinationpoint) {
        this.detinationpoint = detinationpoint;
    }
}
