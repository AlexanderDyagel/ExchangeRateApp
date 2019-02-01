package alexander.dyagel.exchangerateapp;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import alexander.dyagel.exchangerateapp.data.Bank;
import alexander.dyagel.exchangerateapp.db.AppDatabase;
import alexander.dyagel.exchangerateapp.db.DatabaseApp;
import alexander.dyagel.exchangerateapp.db.data_obj.CurrencyRate;
import alexander.dyagel.exchangerateapp.utils.NetworkUtils;
import alexander.dyagel.exchangerateapp.utils.TaskManager;

import static alexander.dyagel.exchangerateapp.utils.NetworkUtils.*;

public class MainListActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private static final String TAG = "msg";
    private RecyclerView recyclerView;
    private List<CurrencyRate> listCurrencyRate;
    private String currentDate;
    private CurrencyAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        bindElementsUI();
        currentDate = getCurrentDate("dd.MM.yyyy");

        listCurrencyRate = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        db = DatabaseApp.getInstance(this);

        if (db.currencyRateDao().getAll().size() != 0) {
            listCurrencyRate = db.currencyRateDao().getSaveInstance();
            adapter = new CurrencyAdapter(this, listCurrencyRate);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Жмите на кнопку", Toast.LENGTH_SHORT).show();
        }

        adapter = new CurrencyAdapter(getApplicationContext(), listCurrencyRate);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listCurrencyRate.size() != 0) listCurrencyRate.clear();


                showMainActivity(Bank.BELVEB);
                showMainActivity(Bank.BELARUSBANK);
                showMainActivity(Bank.BELAPB);

                fab.hide();

                Log.d(TAG, "list from click " + db.currencyRateDao().getAll().size());
            }
        });
        Log.d(TAG, "list " + listCurrencyRate.size());
    }

    private void bindElementsUI() {
        recyclerView = findViewById(R.id.rv_list);
        fab = findViewById(R.id.fab);
    }

    private void showMainActivity(String bankId) {
        new TaskManager(bankId, currentDate, listCurrencyRate, adapter, db).load();
    }


}
