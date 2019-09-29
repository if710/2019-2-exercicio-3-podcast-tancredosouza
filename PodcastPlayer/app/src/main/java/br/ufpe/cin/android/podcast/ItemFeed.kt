package br.ufpe.cin.android.podcast

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itemFeeds")
data class ItemFeed(
    @PrimaryKey
    val title: String,
    val link: String,
    val pubDate: String,
    val description: String,
    val downloadLink: String,
    val imageLink: String) {

    override fun toString(): String {
        return title
    }
}
