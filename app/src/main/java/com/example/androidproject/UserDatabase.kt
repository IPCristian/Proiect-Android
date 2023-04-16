package com.example.androidproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        private fun buildDatabase(context: Context): UserDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                UserDatabase::class.java,
                "user_database"
            )
                .fallbackToDestructiveMigration().allowMainThreadQueries().build()
        }

        fun getDatabase(context: Context): UserDatabase {
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
