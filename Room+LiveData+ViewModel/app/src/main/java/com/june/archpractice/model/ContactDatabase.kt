package com.june.archpractice.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.june.archpractice.model.dao.ContactDAO
import com.june.archpractice.model.entity.Contact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Contact::class), version = 1, exportSchema = false)
abstract class ContactDatabase: RoomDatabase() {
    abstract fun contactDao(): ContactDAO

    private class ContactDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var contactDao = database.contactDao()

                    contactDao.deleteAll()
                }
            }
        }
    }

    // 데이터베이스 인스턴스를 싱글톤으로 사용하기 위해 companion object
    companion object {
        private var INSTANCE: ContactDatabase? = null

        // 여러 스레드가 접근하지 못하도록 synchronized로 설정
        fun getDatabase(context: Context, scope: CoroutineScope): ContactDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact"
                ).addCallback(ContactDatabaseCallback(scope)) // build 전 콜백 추가
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

