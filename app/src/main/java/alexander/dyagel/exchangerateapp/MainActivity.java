package alexander.dyagel.exchangerateapp;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import alexander.dyagel.exchangerateapp.data.Bank;
import alexander.dyagel.exchangerateapp.data.Currency;
import alexander.dyagel.exchangerateapp.db.data_obj.CurrencyRate;
import alexander.dyagel.exchangerateapp.db.AppDatabase;
import alexander.dyagel.exchangerateapp.utils.NetworkUtils;

import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.BANK_ID_BELAPB;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.CITY_CODE_BELAPB;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.formatUrlBelapb;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.formatUrlBelarusbank;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.getCurrentDate;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.getResponseFromUrl;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.getUrl;

public class MainActivity extends AppCompatActivity {
    private TextView rusSell;
    private TextView rusBuy;
    private TextView usdSell;
    private TextView usdBuy;
    private TextView euroSell;
    private TextView euroBuy;
    private TextView rusOut;
    private TextView rusIn;
    private TextView usdOut;
    private TextView usdIn;
    private TextView euroOut;
    private TextView euroIn;
    private TextView rusRateSell;
    private TextView rusRateBuy;
    private TextView usdRateSell;
    private TextView usdRateBuy;
    private TextView euroRateSell;
    private TextView euroRateBuy;

    private TextView dateBelarusbank;
    private TextView dateBelapb;
    private TextView dateBelveb;

    private ProgressBar progressBarBelarusbank;
    private ProgressBar progressBarBelapb;
    private ProgressBar progressBarBelveb;

    private FloatingActionButton fab;

    private String currentDate;

    private AppDatabase db;
    private List<CurrencyRate> listCurrencyRates;
    private static final String TAG = "msg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();


        currentDate = getCurrentDate("dd.MM.yyyy");
        listCurrencyRates = new ArrayList<>();

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "mydatabase")
                .allowMainThreadQueries()
                .build();


        for (CurrencyRate o : db.currencyRateDao().getAll()) {
            Log.d(TAG, " Это из базы id " + o.getId());

        }

        if (db.currencyRateDao().getSaveInstance() != null){
            setViewResultFromDb(db.currencyRateDao().getSaveInstance());
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new BelvebTask().execute(getUrl(NetworkUtils.formatUrlBelveb()));
                    new BelarusbankTask().execute(getUrl(NetworkUtils.formatUrlBelarusbank()));
                    new BelapbTask().execute(getUrl(NetworkUtils.formatUrlBelapb()));
                    fab.hide();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void initUI() {
        rusSell = findViewById(R.id.tv_rus_sell);
        rusBuy = findViewById(R.id.tv_rus_buy);
        usdSell = findViewById(R.id.tv_usd_sell);
        usdBuy = findViewById(R.id.tv_usd_buy);
        euroSell = findViewById(R.id.tv_euro_sell);
        euroBuy = findViewById(R.id.tv_euro_buy);
        rusIn = findViewById(R.id.tv_rus_in);
        rusOut = findViewById(R.id.tv_rus_out);
        usdIn = findViewById(R.id.tv_usd_in);
        usdOut = findViewById(R.id.tv_usd_out);
        euroIn = findViewById(R.id.tv_euro_in);
        euroOut = findViewById(R.id.tv_euro_out);
        rusRateBuy = findViewById(R.id.tv_rus_rate_buy);
        rusRateSell = findViewById(R.id.tv_rus_rate_sell);
        usdRateBuy = findViewById(R.id.tv_usd_rate_buy);
        usdRateSell = findViewById(R.id.tv_usd_rate_sell);
        euroRateBuy = findViewById(R.id.tv_euro_rate_buy);
        euroRateSell = findViewById(R.id.tv_euro_rate_sell);

        dateBelapb = findViewById(R.id.tv_belabp_date);
        dateBelarusbank = findViewById(R.id.tv_belarusbank_date);
        dateBelveb = findViewById(R.id.tv_belveb_date);

        fab = findViewById(R.id.fab);

        progressBarBelapb = findViewById(R.id.pb_belapb);
        progressBarBelarusbank = findViewById(R.id.pb_belarusbank);
        progressBarBelveb = findViewById(R.id.pb_belveb);

    }

    class BelvebTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String result = null;
            try {
                result = getResponseFromUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            progressBarBelveb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            Document document = Jsoup.parse(result);

            Elements elements = document.getElementsByTag("table");
            Element table = elements.get(7);
            Elements elementsTr = table.getElementsByTag("tr");

            Elements elem = null; // может быть краш

            CurrencyRate currencyRate = new CurrencyRate(Bank.BELVEB, getCurrentDate("dd.MM.yyyy"));
            int flag = 0;
            for (int i = 1; i < elementsTr.size(); i++) {
                String title = elementsTr.get(i).getElementsByTag("td").get(0).text();
                if (title.equals("100 РОССИЙСКИХ РУБЛЕЙ")) {
                    elem = elementsTr.get(i).getElementsByTag("td");

                    currencyRate.setRateBuyRur(elem.get(2).text());
                    rusBuy.setText(currencyRate.getRateBuyRur());

                    currencyRate.setRateSellRur(elem.get(3).text());
                    rusSell.setText(currencyRate.getRateSellRur());

                    flag++;
                    continue;
                }
                if (title.equals("1 ДОЛЛАР США")) {
                    elem = elementsTr.get(i).getElementsByTag("td");

                    currencyRate.setRateBuyUsd(elem.get(2).text());
                    usdBuy.setText(currencyRate.getRateBuyUsd());

                    currencyRate.setRateSellUsd(elem.get(3).text());
                    usdSell.setText(currencyRate.getRateSellUsd());

                    flag++;
                    continue;
                }
                if (title.equals("1 ЕВРО")) {
                    elem = elementsTr.get(i).getElementsByTag("td");

                    currencyRate.setRateBuyEuro(elem.get(2).text());
                    euroBuy.setText(currencyRate.getRateBuyEuro());

                    currencyRate.setRateSellEuro(elem.get(3).text());
                    euroSell.setText(currencyRate.getRateSellEuro());

                    flag++;
                    continue;
                }
            }
            if (flag != 3) {
                elem = elementsTr.get(1).getElementsByTag("td");
                Log.d("msg", "Flag = " + flag);
                switch (elem.get(1).text()) {
                    case "100 РОССИЙСКИХ РУБЛЕЙ": {
                        currencyRate.setRateBuyRur(elem.get(3).text());
                        rusBuy.setText(currencyRate.getRateBuyRur());

                        currencyRate.setRateSellRur(elem.get(4).text());
                        rusSell.setText(currencyRate.getRateSellRur());
                        Log.d("msg", "russian");
                        break;
                    }
                    case "1 ДОЛЛАР США": {
                        currencyRate.setRateBuyUsd(elem.get(3).text());
                        usdBuy.setText(currencyRate.getRateBuyUsd());

                        currencyRate.setRateSellUsd(elem.get(4).text());
                        usdSell.setText(currencyRate.getRateSellUsd());
                        Log.d("msg", "dollar");
                        break;
                    }
                    case "1 ЕВРО": {
                        currencyRate.setRateBuyEuro(elem.get(3).text());
                        euroBuy.setText(currencyRate.getRateBuyEuro());

                        currencyRate.setRateSellEuro(elem.get(4).text());
                        euroSell.setText(currencyRate.getRateSellEuro());
                        Log.d("msg", "euro");
                        break;
                    }
                    default: {
                    }
                }
            }
            listCurrencyRates.add(currencyRate);
            dateBelveb.setText(currentDate);
            progressBarBelveb.setVisibility(View.INVISIBLE);
            db.currencyRateDao().insert(currencyRate);

        }
    }

    class BelarusbankTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String resultJSON = null;
            try {
                resultJSON = getResponseFromUrl(getUrl(formatUrlBelarusbank()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJSON;
        }

        @Override
        protected void onPreExecute() {
            progressBarBelarusbank.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String resultJSON) {
            CurrencyRate currencyRate = new CurrencyRate(Bank.BELARUSBANK, getCurrentDate("dd.MM.yyyy"));
            try {
                JSONArray jsonArray = new JSONArray(resultJSON);
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                currencyRate.setRateBuyRur(jsonObject.getString("RUB_in"));
                rusIn.setText(currencyRate.getRateBuyRur());
                currencyRate.setRateSellRur(jsonObject.getString("RUB_out"));
                rusOut.setText(currencyRate.getRateSellRur());
                currencyRate.setRateBuyEuro(jsonObject.getString("EUR_in"));
                euroIn.setText(currencyRate.getRateBuyEuro());
                currencyRate.setRateSellEuro(jsonObject.getString("EUR_out"));
                euroOut.setText(currencyRate.getRateSellEuro());
                currencyRate.setRateBuyUsd(jsonObject.getString("USD_in"));
                usdIn.setText(currencyRate.getRateBuyUsd());
                currencyRate.setRateSellUsd(jsonObject.getString("USD_out"));
                usdOut.setText(currencyRate.getRateSellUsd());


            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                listCurrencyRates.add(currencyRate);
                dateBelarusbank.setText(currentDate);
                progressBarBelarusbank.setVisibility(View.INVISIBLE);
                Log.d(TAG, "belarusbank " + listCurrencyRates.size());
                db.currencyRateDao().insert(currencyRate);
            }
        }
    }

    class BelapbTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String resultXML = null;
            try {
                resultXML = getResponseFromUrl(getUrl(formatUrlBelapb()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultXML;
        }

        @Override
        protected void onPreExecute() {
            progressBarBelapb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String resultXML) {
            ArrayList<Currency> currencies = new ArrayList<>();
            CurrencyRate currencyRate = new CurrencyRate(Bank.BELAPB, getCurrentDate("dd.MM.yyyy"));

            Currency currentCurrency = null;
            boolean inEntry = false;
            String textValue = "";

            XmlPullParserFactory factory = null;
            try {
                factory = XmlPullParserFactory.newInstance();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            try {
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(resultXML));
                xpp.next();

                Log.d("msg", xpp.getName());
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    String tagName = xpp.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if ("Currency".equalsIgnoreCase(tagName)) {
                                inEntry = true;
                                currentCurrency = new Currency();
                            }
                            break;
                        case XmlPullParser.TEXT:
                            textValue = xpp.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if (inEntry) {
                                if ("Currency".equalsIgnoreCase(tagName)) {
                                    currencies.add(currentCurrency);
                                    inEntry = false;
                                } else if ("CharCode".equalsIgnoreCase(tagName)) {
                                    currentCurrency.setCharCode(textValue);
                                } else if ("RateBuy".equalsIgnoreCase(tagName)) {
                                    currentCurrency.setRateBuy(textValue);
                                } else if ("RateSell".equalsIgnoreCase(tagName)) {
                                    currentCurrency.setRateSell(textValue);
                                } else if ("CityId".equalsIgnoreCase(tagName)) {
                                    currentCurrency.setCityId(textValue);
                                } else if ("BankId".equalsIgnoreCase(tagName)) {
                                    currentCurrency.setBankId(textValue);
                                }
                            }
                            break;
                        default:
                    }
                    eventType = xpp.next();
                }

                for (Currency currency : currencies) {

                    if (currency.getCityId().equals(CITY_CODE_BELAPB) && currency.getBankId().equals(BANK_ID_BELAPB)) {

                        switch (currency.getCharCode()) {
                            case "EUR": {
                                currencyRate.setRateBuyEuro(currency.getRateBuy());
                                euroRateBuy.setText(currencyRate.getRateBuyEuro());

                                currencyRate.setRateSellEuro(currency.getRateSell());
                                euroRateSell.setText(currencyRate.getRateSellEuro());
                                break;
                            }
                            case "USD": {
                                currencyRate.setRateBuyUsd(currency.getRateBuy());
                                usdRateBuy.setText(currencyRate.getRateBuyUsd());

                                currencyRate.setRateSellUsd(currency.getRateSell());
                                usdRateSell.setText(currencyRate.getRateSellUsd());
                                break;
                            }
                            case "RUB": {
                                currencyRate.setRateBuyRur(currency.getRateBuy());
                                rusRateBuy.setText(currencyRate.getRateBuyRur());

                                currencyRate.setRateSellRur(currency.getRateSell());
                                rusRateSell.setText(currencyRate.getRateSellRur());
                                break;
                            }
                            default: {
                            }
                        }
                    }
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                listCurrencyRates.add(currencyRate);
                dateBelapb.setText(currentDate);
                progressBarBelapb.setVisibility(View.INVISIBLE);
                Log.d(TAG, "belapb " + listCurrencyRates.size());
                db.currencyRateDao().insert(currencyRate);
            }

        }
    }

    private void setViewResultFromDb(List<CurrencyRate> obj){
        for (CurrencyRate curr : obj){
            switch (curr.getBankId()){
                case Bank.BELVEB : {
                    rusBuy.setText(curr.getRateBuyRur());
                    rusSell.setText(curr.getRateSellRur());
                    usdBuy.setText(curr.getRateBuyUsd());
                    usdSell.setText(curr.getRateSellUsd());
                    euroBuy.setText(curr.getRateBuyEuro());
                    euroSell.setText(curr.getRateSellEuro());
                    dateBelveb.setText(curr.getDate());
                    break;
                }
                case Bank.BELARUSBANK : {
                    rusIn.setText(curr.getRateBuyRur());
                    rusOut.setText(curr.getRateSellRur());
                    usdIn.setText(curr.getRateBuyUsd());
                    usdOut.setText(curr.getRateSellUsd());
                    euroIn.setText(curr.getRateBuyEuro());
                    euroOut.setText(curr.getRateSellEuro());
                    dateBelarusbank.setText(curr.getDate());
                    break;
                }
                case Bank.BELAPB : {
                    rusRateBuy.setText(curr.getRateBuyRur());
                    rusRateSell.setText(curr.getRateSellRur());
                    usdRateBuy.setText(curr.getRateBuyUsd());
                    usdRateSell.setText(curr.getRateSellUsd());
                    euroRateBuy.setText(curr.getRateBuyEuro());
                    euroRateSell.setText(curr.getRateSellEuro());
                    dateBelapb.setText(curr.getDate());
                    break;
                }
            }
            Log.d(TAG, "setViewResultFromDb: " + curr.getId());
        }
    }
}
