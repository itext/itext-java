package com.itextpdf.basics.font;

public class FontIdentification {

    // name ID 5
    private String ttfVersion;
    // name ID 3
    private String ttfUniqueId;
    // /UniqueID
    private Integer type1Xuid;
    // OS/2.panose
    private String panose;

    public String getTtfVersion() {
        return ttfVersion;
    }

    public String getTtfUniqueId() {
        return ttfUniqueId;
    }

    public Integer getType1Xuid() {
        return type1Xuid;
    }

    public String getPanose() {
        return panose;
    }

    protected void setTtfVersion(String ttfVersion) {
        this.ttfVersion = ttfVersion;
    }

    protected void setTtfUniqueId(String ttfUniqueId) {
        this.ttfUniqueId = ttfUniqueId;
    }

    protected void setType1Xuid(Integer type1Xuid) {
        this.type1Xuid = type1Xuid;
    }

    protected void setPanose(byte[] panose) {
        this.panose = new String(panose);
    }

    //todo change to protected!
    public void setPanose(String panose) {
        this.panose = panose;
    }
}
