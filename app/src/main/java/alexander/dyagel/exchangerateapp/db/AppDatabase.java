package alexander.dyagel.exchangerateapp.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import alexander.dyagel.exchangerateapp.db.data_obj.CurrencyRate;
import alexander.dyagel.exchangerateapp.db.data_obj.CurrencyRateDao;

@Database(entities = {CurrencyRate.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CurrencyRateDao currencyRateDao();
}
