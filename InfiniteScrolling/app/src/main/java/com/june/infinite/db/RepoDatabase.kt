package com.june.infinite.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.june.infinite.model.Repo

@Database(
    entities = [Repo::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class RepoDatabase : RoomDatabase() {

    abstract fun reposDao() : RepoDao
    abstract fun remoteKeysDao() : RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: RepoDatabase? = null

        // RepoDatabase 객체가 아직 존재하지 않는 경우, 객체를 빌드하는 함수
        fun getInstance(context: Context): RepoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, RepoDatabase::class.java, "Github.db")
                .build()
    }
}