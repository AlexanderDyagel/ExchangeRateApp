package alexander.dyagel.exchangerateapp.utils;

import android.os.AsyncTask;
import android.util.Log;

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

import alexander.dyagel.exchangerateapp.CurrencyAdapter;
import alexander.dyagel.exchangerateapp.data.Bank;
import alexander.dyagel.exchangerateapp.data.Currency;
import alexander.dyagel.exchangerateapp.db.AppDatabase;
import alexander.dyagel.exchangerateapp.db.data_obj.CurrencyRate;

import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.BANK_ID_BELAPB;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.CITY_CODE_BELAPB;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.formatUrlBelapb;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.formatUrlBelarusbank;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.formatUrlBelveb;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.getResponseFromUrl;
import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.getUrl;

public class TaskManager {
    private static final String TAG = "msg";

    private String currentDate;
    private List<CurrencyRate> currencyRateList;
    private CurrencyAdapter adapter;
    private String bankId;
    private AppDatabase db;

    public TaskManager(String bankId, String date, List<CurrencyRate> list, CurrencyAdapter adapter, AppDatabase db) {
        currentDate = date;
        this.bankId = bankId;
        currencyRateList = list;
        this.adapter = adapter;
        this.db = db;
    }

    // Загрузка тасков банков
    public void load() {
        try {
            switch (bankId) {
                case Bank.BELVEB: {
                    new BankTask().execute(getUrl(formatUrlBelveb()));
                    break;
                }
                case Bank.BELARUSBANK: {
                    new BankTask().execute(getUrl(formatUrlBelarusbank()));
                    break;
                }
                case Bank.BELAPB: {
                    new BankTask().execute(getUrl(formatUrlBelapb()));
                    break;
                }
            }
        } catch (MalformedURLException e) {
        }
    }

    // Таск на ответ сервера банка
    class BankTask extends AsyncTask<URL, Void, String> {

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
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            switch (bankId) {
                case Bank.BELVEB: {
                    parseDataFromBelveb(result);
                    break;
                }
                case Bank.BELARUSBANK: {
                    parseDataFromBelarusbank(result);
                    break;
                }
                case Bank.BELAPB: {
                    parseDataFromBelapb(result);
                    break;
                }
            }

            adapter.notifyDataSetChanged();

            //progressBarBelveb.setVisibility(View.INVISIBLE);

        }
    }

    private void parseDataFromBelveb(String resultHTML) {
        Document document = Jsoup.parse(resultHTML);

        Elements elements = document.getElementsByTag("table");
        Element table = elements.get(7);
        Elements elementsTr = table.getElementsByTag("tr");

        Elements elem = null; // может быть краш

        CurrencyRate currencyRate = new CurrencyRate(bankId, currentDate);
        int flag = 0;
        for (int i = 1; i < elementsTr.size(); i++) {
            String title = elementsTr.get(i).getElementsByTag("td").get(0).text();
            if (title.equals("100 РОССИЙСКИХ РУБЛЕЙ")) {
                elem = elementsTr.get(i).getElementsByTag("td");

                currencyRate.setRateBuyRur(elem.get(2).text());
                //rusBuy.setText(currencyRate.getRateBuyRur());

                currencyRate.setRateSellRur(elem.get(3).text());
                //rusSell.setText(currencyRate.getRateSellRur());

                flag++;
                continue;
            }
            if (title.equals("1 ДОЛЛАР США")) {
                elem = elementsTr.get(i).getElementsByTag("td");

                currencyRate.setRateBuyUsd(elem.get(2).text());
                //usdBuy.setText(currencyRate.getRateBuyUsd());

                currencyRate.setRateSellUsd(elem.get(3).text());
                //usdSell.setText(currencyRate.getRateSellUsd());

                flag++;
                continue;
            }
            if (title.equals("1 ЕВРО")) {
                elem = elementsTr.get(i).getElementsByTag("td");

                currencyRate.setRateBuyEuro(elem.get(2).text());
                //euroBuy.setText(currencyRate.getRateBuyEuro());

                currencyRate.setRateSellEuro(elem.get(3).text());
                //euroSell.setText(currencyRate.getRateSellEuro());

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
                    //rusBuy.setText(currencyRate.getRateBuyRur());

                    currencyRate.setRateSellRur(elem.get(4).text());
                    //rusSell.setText(currencyRate.getRateSellRur());
                    Log.d("msg", "russian");
                    break;
                }
                case "1 ДОЛЛАР США": {
                    currencyRate.setRateBuyUsd(elem.get(3).text());
                    //usdBuy.setText(currencyRate.getRateBuyUsd());

                    currencyRate.setRateSellUsd(elem.get(4).text());
                    //usdSell.setText(currencyRate.getRateSellUsd());
                    Log.d("msg", "dollar");
                    break;
                }
                case "1 ЕВРО": {
                    currencyRate.setRateBuyEuro(elem.get(3).text());
                    //euroBuy.setText(currencyRate.getRateBuyEuro());

                    currencyRate.setRateSellEuro(elem.get(4).text());
                    //euroSell.setText(currencyRate.getRateSellEuro());
                    Log.d("msg", "euro");
                    break;
                }
                default: {
                }
            }
        }
        currencyRateList.add(currencyRate);
        compareDataFromDb(currencyRate);
    }

    private void parseDataFromBelarusbank(String resultJSON) {
        CurrencyRate currencyRate = new CurrencyRate(bankId, currentDate);
        try {
            JSONArray jsonArray = new JSONArray(resultJSON);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

            currencyRate.setRateBuyRur(jsonObject.getString("RUB_in"));
            currencyRate.setRateSellRur(jsonObject.getString("RUB_out"));
            currencyRate.setRateBuyEuro(jsonObject.getString("EUR_in"));
            currencyRate.setRateSellEuro(jsonObject.getString("EUR_out"));
            currencyRate.setRateBuyUsd(jsonObject.getString("USD_in"));
            currencyRate.setRateSellUsd(jsonObject.getString("USD_out"));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            currencyRateList.add(currencyRate);
            compareDataFromDb(currencyRate);
        }
    }

    private void parseDataFromBelapb(String resultXML) {
        ArrayList<Currency> currencies = new ArrayList<>();
        CurrencyRate currencyRate = new CurrencyRate(bankId, currentDate);

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
                            currencyRate.setRateSellEuro(currency.getRateSell());
                            break;
                        }
                        case "USD": {
                            currencyRate.setRateBuyUsd(currency.getRateBuy());
                            currencyRate.setRateSellUsd(currency.getRateSell());
                            break;
                        }
                        case "RUB": {
                            currencyRate.setRateBuyRur(currency.getRateBuy());
                            currencyRate.setRateSellRur(currency.getRateSell());
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
            currencyRateList.add(currencyRate);
            compareDataFromDb(currencyRate);
        }
    }

    private void compareDataFromDb(CurrencyRate currencyRate){
        int temp = db.currencyRateDao().getAll().size();
        Log.d(TAG, "compareDataFromDb: size() = " + temp);
        if(temp == 0 || temp < 3)
            db.currencyRateDao().insert(currencyRate);
        else if (!db.currencyRateDao().getOneData().equals(currentDate)) {
            db.currencyRateDao().insert(currencyRate);
        }
    }
}

