package com.example.choralefbbt.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserer(user: User): Long

    @Query("SELECT * FROM users WHERE pin = :pin LIMIT 1")
    suspend fun parPin(pin: String): User?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun compter(): Int
}

@Dao
interface SongbookDao {
    @Insert
    suspend fun inserer(songbook: Songbook): Long

    @Query("SELECT * FROM songbook WHERE event_name = :nom LIMIT 1")
    suspend fun parNom(nom: String): Songbook?
}

@Dao
interface SongDao {
    @Insert
    suspend fun inserer(song: SongSheet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun lier(contain: Contain)

    @Query("SELECT COUNT(*) FROM contain WHERE songbook_id = :songbookId")
    suspend fun compterDansRecueil(songbookId: Long): Int

    @Query("SELECT * FROM song_sheet WHERE is_deleted = 0 ORDER BY title")
    fun tous(): Flow<List<SongSheet>>
}