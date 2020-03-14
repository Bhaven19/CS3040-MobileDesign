package com.example.memori.components.places;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GooglePlace implements Serializable {

    private static final long serialVersionUID = -4041502421563593320L;
    private String name;
    private String vicinity;
    private String formatted_address;
    private String formatted_phone_number;

    private List<String> types;

    public static class Geometry implements Serializable {

        private static final long serialVersionUID = 2946649576104623502L;
        public static class Location implements Serializable {

            private static final long serialVersionUID = -1861462299276634548L;
            private double lat;
            private double lng;

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
        }

        private Location location;


        public void setLocation(Location location) {
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }
    }

    private Geometry geometry;
    private String icon;
    private String id;
    private String reference;
    private float rating;
    private String url;
    private ArrayList<Review> reviews;

    public static class Review implements Serializable {

        public static class Aspect implements Serializable {
            private int rating;
            private String type;

            public void setRating(int r) {
                rating = r;
            }

            public int getRating() {
                return rating;
            }

            public void setType(String t) {
                type = t;
            }

            public String getType() {
                return type;
            }
        }

        private ArrayList<Aspect> aspects;
        private String author_name;
        private int rating;
        private String text;
                /*
                 * For example, the JSON data looks like this...
                 *
                "author_name" : "Little Al",
                "author_url" : "https://plus.google.com/103209428135026695692",
                "language" : "en",
                "rating" : 5,
                "text" : "fabulous experience ..really enjoyed",
                "time" : 1422356740
                *
                */

        public int getRating() { return rating; }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public void setAspects(ArrayList<Aspect> aspects) {
            this.aspects = aspects;
        }

        public List<Aspect> getAspects() {
            return aspects;
        }
    }

    private String website;

    public GooglePlace() {
        types = new ArrayList<String>();
    }

    @Override
    public String toString() {
        String typeList = "";
        for (String type : types) {
            typeList = typeList + type + " ";
        }
        return name + "\n" + vicinity + "\n" + typeList + "\n" +
                this.getGeometry().getLocation().getLat() + ", " +
                this.getGeometry().getLocation().getLng();
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public void addType(String type) {
        types.add(type);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }


}
