package br.ufpe.cin.android.podcast.daos

import androidx.room.*
import br.ufpe.cin.android.podcast.ItemFeed

@Dao
interface ItemFeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemFeeds(vararg itemFeeds: ItemFeed)

    @Query("SELECT * FROM itemFeeds")
    fun getAllItemFeeds() : List<ItemFeed>

    @Query("SELECT * FROM itemFeeds WHERE title LIKE :q")
    fun getItemFeed(q : String) : ItemFeed

    @Update()
    fun updateItemFeed(vararg itemFeeds: ItemFeed)
}