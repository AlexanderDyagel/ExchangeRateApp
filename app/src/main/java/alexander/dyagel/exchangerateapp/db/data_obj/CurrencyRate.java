package alexander.dyagel.exchangerateapp.db.data_obj;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import alexander.dyagel.exchangerateapp.data.Bank;

@Entity
public class CurrencyRate {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String rateBuyRur;
    private String rateSellRur;
    private String rateBuyUsd;
    private String rateSellUsd;
    private String rateBuyEuro;
    private String rateSellEuro;
    private String bankId;
    private String date;

    public CurrencyRate(String bankId, String date){
        this.bankId = bankId;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRateBuyRur() {
        return rateBuyRur;
    }

    public void setRateBuyRur(String rateBuyRur) {
        this.rateBuyRur = rateBuyRur;
    }

    public String getRateSellRur() {
        return rateSellRur;
    }

    public void setRateSellRur(String rateSellRur) {
        this.rateSellRur = rateSellRur;
    }

    public String getRateBuyUsd() {
        return rateBuyUsd;
    }

    public void setRateBuyUsd(String rateBuyUsd) {
        this.rateBuyUsd = rateBuyUsd;
    }

    public String getRateSellUsd() {
        return rateSellUsd;
    }

    public void setRateSellUsd(String rateSellUsd) {
        this.rateSellUsd = rateSellUsd;
    }

    public String getRateBuyEuro() {
        return rateBuyEuro;
    }

    public void setRateBuyEuro(String rateBuyEuro) {
        this.rateBuyEuro = rateBuyEuro;
    }

    public String getRateSellEuro() {
        return rateSellEuro;
    }

    public void setRateSellEuro(String rateSellEuro) {
        this.rateSellEuro = rateSellEuro;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
