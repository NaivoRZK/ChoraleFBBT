package com.example.choralefbbt.data

import androidx.room.withTransaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChoraleRepository(private val db: AppDatabase) {

    suspend fun connecterAdmin(pin: String): User? {
        // Premier lancement : crée un admin provisoire avec le code 1234
        if (db.userDao().compter() == 0) {
            db.userDao().inserer(User(lastName = "Admin", firstName = "Chorale", pin = "1234"))
        }
        return db.userDao().parPin(pin)
    }

    suspend fun prochainNumero(evenement: String): Int {
        val recueil = db.songbookDao().parNom(evenement) ?: return 1
        return db.songDao().compterDansRecueil(recueil.songbookId) + 1
    }

    suspend fun ajouterChant(
        userId: Long, numero: Int, titre: String, evenement: String,
        photo: String?, audio: String?, playback: String?
    ) = db.withTransaction {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(Date())
        val recueilId = db.songbookDao().parNom(evenement)?.songbookId
            ?: db.songbookDao().inserer(Songbook(eventName = evenement, eventDate = date, userId = userId))
        val songId = db.songDao().inserer(
            SongSheet(title = titre, lyricsPhoto = photo, audio = audio, playback = playback, userId = userId)
        )
        db.songDao().lier(Contain(songbookId = recueilId, songId = songId, songOrder = numero))
    }
}