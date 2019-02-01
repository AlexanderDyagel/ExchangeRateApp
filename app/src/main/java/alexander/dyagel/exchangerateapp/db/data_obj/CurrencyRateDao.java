package alexander.dyagel.exchangerateapp.db.data_obj;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CurrencyRateDao {

    @Query("SELECT * FROM CurrencyRate")
    List<CurrencyRate> getAll();

    @Insert
    void insert(CurrencyRate obj);

    @Query("SELECT * FROM CurrencyRate ORDER BY id DESC LIMIT 3")
    List<CurrencyRate> getSaveInstance();

    @Query("SELECT date FROM CurrencyRate ORDER BY id DESC LIMIT 1")
    String getOneData();

}
