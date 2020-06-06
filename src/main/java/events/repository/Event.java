package events.repository;

public class Event implements Comparable<Event> {

    private Long id;
    private String ver_name;
    private String place;
    private String datum;
    private String description;
    private String eventType;
    private String weather = "unbekannt";
    private String rank = "0";

    public Event(Long id, String ver_name, String place, String datum, String description, String eventType, String weather, String rank) {
        this.id = id;
        this.ver_name = ver_name;
        this.place = place;
        this.datum = datum;
        this.description = description;
        this.eventType = eventType;
        this.weather = weather;
        this.rank = rank;
    }

    public Event(String ver_name, String place, String datum, String description, String eventType) {
        this.ver_name = ver_name;
        this.place = place;
        this.datum = datum;
        this.description = description;
        this.eventType = eventType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVer_name() {
        return ver_name;
    }

    public void setVer_name(String ver_name) {
        this.ver_name = ver_name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getRankingInt() {
        return Integer.parseInt(getRank());
    }

    @Override
    public int compareTo(Event o) {
        return Integer.compare(Integer.parseInt(this.getRank()), Integer.parseInt(o.getRank()));
    }
}