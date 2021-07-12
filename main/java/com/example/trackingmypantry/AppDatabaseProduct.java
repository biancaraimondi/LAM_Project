package com.example.trackingmypantry;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Product.class}, version = 1)
public abstract class AppDatabaseProduct extends RoomDatabase {

    public abstract ProductDao productDao();
    private static volatile AppDatabaseProduct INSTANCE;

    private static final int nTHREADS = 4;

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(nTHREADS);
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                ProductDao dao = INSTANCE.productDao();
                dao.deleteAll();
            });


        }
    };

    static AppDatabaseProduct getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabaseProduct.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabaseProduct.class, "product_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }
}
