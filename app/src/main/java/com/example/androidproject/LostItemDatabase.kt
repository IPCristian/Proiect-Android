package com.example.androidproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LostItem::class], version = 1, exportSchema = false)
@TypeConverters(ImageConverter::class)
abstract class LostItemDatabase : RoomDatabase() {
    abstract fun itemDao(): LostItemDAO

    companion object {
        @Volatile
        private var INSTANCE: LostItemDatabase? = null

        private fun buildDatabase(context: Context): LostItemDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                LostItemDatabase::class.java,
                "item_database"
            )
                .fallbackToDestructiveMigration().allowMainThreadQueries().build()
        }

        fun getDatabase(context: Context): LostItemDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
        }
    }
//        private class UserDatabaseCallback(
//            private val scope: CoroutineScope) : RoomDatabase.Callback() {
//
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                INSTANCE?.let { database ->
//                    scope.launch {
//                        populateDatabase(database.userDao())
//                    }
//                }
//            }
//
//            suspend fun populateDatabase(userDao: UserDAO) {
//                // Add sample words.
//                var user = User(1,"Test Account","abc@gmail.com","1234")
//                userDao.insert(user)
//            }
//        }
//
//    }
}
