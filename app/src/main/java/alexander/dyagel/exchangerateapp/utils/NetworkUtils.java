package alexander.dyagel.exchangerateapp.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class NetworkUtils {

    public static final String CITY_CODE_BELVEB = "96000";
    public static final String CITY_CODE_BELAPB = "287568";
    public static final String CITY_CODE_BELARUSBANK = "Глубокое";
    public static final String BANK_ID_BELAPB = "12036";

    // Получение текущей даты
    public static String getCurrentDate(String formatString){
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(new Date());
    }

    // Формирование URL
    public static URL getUrl(String uri) throws MalformedURLException {
        URL url = new URL(uri);
        return url;
    }
    // БелВЭБ
    public static String formatUrlBelveb(){
        String url = String.format("https://www.belveb.by/individual/uslugi-i-obsluzhivanie/valyutno-obmennye-operatsii/kursy-valyut/?date=%s&type=bveb&office=%s",
                getCurrentDate("dd.MM.yyyy"), CITY_CODE_BELVEB);
        return url;
    }
    // Белагропромбанк
    public static String formatUrlBelapb(){
        String url = String.format("https://belapb.by/CashExRatesDaily.php?ondate=%s",
                getCurrentDate("MM/dd/yyyy"));
        return url;
    }
    // Беларусьбанк
    public static String formatUrlBelarusbank(){
        String url = "https://belarusbank.by/api/kursExchange?city=" + CITY_CODE_BELARUSBANK;
        return url;
    }
    // Запрос данных от сервера
    public static String getResponseFromUrl(URL url) throws IOException{
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = connection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            String response = scanner.hasNext() ? scanner.next() : "Нет данных от сервера!";

            return response;
        }catch (UnknownHostException e){
            return null;
        }finally {
            connection.disconnect();
        }
    }
}
