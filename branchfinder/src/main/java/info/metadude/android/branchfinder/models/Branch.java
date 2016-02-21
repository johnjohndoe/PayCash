
package info.metadude.android.branchfinder.models;

import java.util.ArrayList;
import java.util.List;

public class Branch {

    private String city;

    private long id;

    private String lat;

    private String lng;

    private String logoUrl;

    private List<OpeningHour> openingHours = new ArrayList<>();

    private String streetNo;

    private String streetNr;

    private String title;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public double getLatitude() {
        return Double.valueOf(lat);
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public double getLongitude() {
        return Double.valueOf(lng);
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public List<OpeningHour> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(List<OpeningHour> openingHours) {
        this.openingHours = openingHours;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public String getStreetNr() {
        return streetNr;
    }

    public void setStreetNr(String streetNr) {
        this.streetNr = streetNr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
