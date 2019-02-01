package alexander.dyagel.exchangerateapp.db;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DatabaseApp {
    private static AppDatabase db;

    private DatabaseApp(){
    }
    public static synchronized AppDatabase getInstance(Context context){
        if (db == null){
            db = Room.databaseBuilder(context, AppDatabase.class, "dbCurrencyRate.db").allowMainThreadQueries().build();
        }
        return db;
    }
}
