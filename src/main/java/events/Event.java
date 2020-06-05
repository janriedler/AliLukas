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

    public String getVer_name() {
        return ver_name;
    }

    public String getOrt() {
        return ort;
    }

    public String getDatum() {
        return datum;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public String getArt() {
        return art;
    }

    public Long getId() {
        return id;
    }

    void setVer_name(String ver_name) {
        this.ver_name = ver_name;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWetter() {
        return wetter;
    }

    void setWetter(String wetter) {
        this.wetter = wetter;
    }

    String getRanking() {
        return ranking;
    }

    int getRankingInt() {
        return Integer.parseInt(getRanking());
    }

    void setRanking(String ranking) {
        this.ranking = ranking;
    }

    //wie bis jetzt nicht benötigt
    @Override
    public String toString() {
        return String.format("Student [id=%s, name=%s, passportNumber=%s]", id, ver_name, ort, datum, beschreibung, art);
    }

    @Override
    public int compareTo(Event o) {
        return Integer.compare(Integer.parseInt(this.getRanking()), Integer.parseInt(o.getRanking()));
    }
}