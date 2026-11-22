
package com.example.choralefbbt.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id") val userId: Long = 0,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    val pin: String
)

@Entity(
    tableName = "songbook",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["user_id"], childColumns = ["user_id"],
        onDelete = ForeignKey.RESTRICT, onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index("user_id")]
)
data class Songbook(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "songbook_id") val songbookId: Long = 0,
    @ColumnInfo(name = "event_name") val eventName: String,
    @ColumnInfo(name = "event_date") val eventDate: String,
    @ColumnInfo(name = "user_id") val userId: Long
)

@Entity(
    tableName = "song_sheet",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["user_id"], childColumns = ["user_id"],
        onDelete = ForeignKey.RESTRICT, onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index("user_id"), Index("title")]
)
data class SongSheet(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "song_id") val songId: Long = 0,
    val title: String,
    @ColumnInfo(name = "lyrics_text") val lyricsText: String? = null,
    @ColumnInfo(name = "lyrics_photo") val lyricsPhoto: String? = null,
    val audio: String? = null,
    val playback: String? = null,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "user_id") val userId: Long
)

@Entity(
    tableName = "contain",
    primaryKeys = ["songbook_id", "song_id"],
    foreignKeys = [
        ForeignKey(entity = Songbook::class, parentColumns = ["songbook_id"],
            childColumns = ["songbook_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = SongSheet::class, parentColumns = ["song_id"],
            childColumns = ["song_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index("song_id")]
)
data class Contain(
    @ColumnInfo(name = "songbook_id") val songbookId: Long,
    @ColumnInfo(name = "song_id") val songId: Long,
    @ColumnInfo(name = "song_order") val songOrder: Int
)

data class SongInBook(
    @Embedded val song: SongSheet,
    @ColumnInfo(name = "song_order") val songOrder: Int
)