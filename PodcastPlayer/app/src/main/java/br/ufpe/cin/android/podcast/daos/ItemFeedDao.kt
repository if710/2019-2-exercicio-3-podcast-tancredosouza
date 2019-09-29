package br.ufpe.cin.android.podcast.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.ufpe.cin.android.podcast.ItemFeed

@Dao
interface ItemFeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemFeeds(vararg itemFeeds:ItemFeed)

    @Query("SELECT * FROM itemFeeds")
    fun getAllItemFeeds() : List<ItemFeed>

    @Query("SELECT * FROM itemFeeds WHERE title LIKE :q")
    fun getItemFeed(q : String) : ItemFeed
}