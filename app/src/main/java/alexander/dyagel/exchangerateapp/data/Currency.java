package alexander.dyagel.exchangerateapp.data;

public class Currency {
    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String name) {
        this.charCode = name;
    }

    public String getRateBuy() {
        return rateBuy;
    }

    public void setRateBuy(String rateBuy) {
        this.rateBuy = rateBuy;
    }

    public String getRateSell() {
        return rateSell;
    }

    public void setRateSell(String rateSell) {
        this.rateSell = rateSell;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    private String charCode;
    private String rateBuy;
    private String rateSell;
    private String cityId;

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    private String bankId;
}
