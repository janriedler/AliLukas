package events;

public class Event implements Comparable<Event> {

    public Long id;
    public String ver_name;
    public String ort;
    public String datum;
    public String beschreibung;
    public String art;
    public String wetter = "unbekannt";
    public String ranking = "0";

    Event(Long id, String ver_name, String ort, String datum, String beschreibung, String art) {
        super();
        this.id = id;
        this.ver_name = ver_name;
        this.art = art;
        this.ort = ort;
        this.datum = datum;
        this.beschreibung = beschreibung;
    }

    public Event(String ver_name, String ort, String datum, String beschreibung, String art) {
        super();
        this.ver_name = ver_name;
        this.art = art;
        this.ort = ort;
        this.datum = datum;
        this.beschreibung = beschreibung;
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

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getWetter() {
        return wetter;
    }

    public void setWetter(String wetter) {
        this.wetter = wetter;
    }

    public String getRanking() {
        return ranking;
    }

    void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public int getRankingInt() {
        return Integer.parseInt(getRanking());
    }

    @Override
    public int compareTo(Event o) {
        return Integer.compare(Integer.parseInt(this.getRanking()), Integer.parseInt(o.getRanking()));
    }
}