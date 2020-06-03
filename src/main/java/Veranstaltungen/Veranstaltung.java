package Veranstaltungen;

/**
 * logisch eigentlich
 */

public class Veranstaltung {

    private Long id;
    private String ver_name;
    private String ort;
    private String datum;
    private String beschreibung;
    private String art;
    private String wetter = "unbekannt";
    private String ranking = "0";

    public Veranstaltung() {
        super();
    }

    public Veranstaltung(Long id, String ver_name, String ort, String datum, String beschreibung, String art) {
        super();
        this.id = id;
        this.ver_name = ver_name;
        this.art = art;
        this.ort = ort;
        this.datum = datum;
        this.beschreibung = beschreibung;
    }

    public Veranstaltung(String ver_name, String ort, String datum, String beschreibung, String art) {

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

    public void setVer_name(String ver_name) {
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

    public void setWetter(String wetter) {
        this.wetter = wetter;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }



    //wie bis jetzt nicht benötigt
    @Override
    public String toString() {

        return String.format("Student [id=%s, name=%s, passportNumber=%s]", id, ver_name, ort, datum, beschreibung, art);

    }

}