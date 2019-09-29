package br.ufpe.cin.android.podcast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.ufpe.cin.android.podcast.ItemFeed
import br.ufpe.cin.android.podcast.daos.ItemFeedDao

@Database(entities= arrayOf(ItemFeed::class), version=1)
abstract class ItemFeedsDatabase : RoomDatabase() {
    abstract fun itemFeedsDao() : ItemFeedDao
    companion object {
        private var INSTANCE : ItemFeedsDatabase? = null

        fun getDatabase(ctx : Context) : ItemFeedsDatabase {
            if (INSTANCE == null) {
                synchronized(ItemFeedsDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        ItemFeedsDatabase::class.java,
                        "itemFeeds.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}