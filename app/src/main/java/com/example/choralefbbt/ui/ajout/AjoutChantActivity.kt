package com.example.choralefbbt.ui.ajout

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.choralefbbt.data.AppDatabase
import com.example.choralefbbt.data.ChoraleRepository
import com.example.choralefbbt.databinding.ActivityAjoutChantBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.io.File

class AjoutChantActivity : AppCompatActivity() {

    private lateinit var b: ActivityAjoutChantBinding
    private val repo by lazy { ChoraleRepository(AppDatabase.get(this)) }
    private val userId by lazy { intent.getLongExtra("user_id", 0L) }

    private var photoPath: String? = null
    private var audioUri: String? = null
    private var playbackUri: String? = null

    private val prendrePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
            bmp?.let { sauverPhoto(it) }
        }

    private val choisirPhoto =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                contentResolver.openInputStream(it)?.use { flux ->
                    BitmapFactory.decodeStream(flux)?.let { bmp -> sauverPhoto(bmp) }
                }
            }
        }

    private val choisirAudio =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                garderAcces(it)
                audioUri = it.toString()
                b.btnAudio.text = "✓ Audio ajouté"
            }
        }

    private val choisirPlayback =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                garderAcces(it)
                playbackUri = it.toString()
                b.btnPlayback.text = "✓ Playback ajouté"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAjoutChantBinding.inflate(layoutInflater)
        setContentView(b.root)

        majNumero()
        b.chipGroup.setOnCheckedStateChangeListener { _, _ -> majNumero() }
        b.etAutreEvenement.addTextChangedListener { majNumero() }

        b.btnPhoto.setOnClickListener { prendrePhoto.launch(null) }
        b.btnGalerie.setOnClickListener { choisirPhoto.launch("image/*") }
        b.btnAudio.setOnClickListener { choisirAudio.launch(arrayOf("audio/*")) }
        b.btnPlayback.setOnClickListener { choisirPlayback.launch(arrayOf("audio/*")) }
        b.btnAnnuler.setOnClickListener { finish() }
        b.btnEnregistrer.setOnClickListener { enregistrer() }
    }

    private fun evenementChoisi(): String? {
        val saisi = b.etAutreEvenement.text.toString().trim()
        if (saisi.isNotEmpty()) return saisi
        val id = b.chipGroup.checkedChipId.takeIf { it != View.NO_ID } ?: return null
        return findViewById<Chip>(id).text.toString()
    }

    private fun majNumero() {
        val evenement = evenementChoisi() ?: return
        lifecycleScope.launch {
            b.etNumero.setText("%02d".format(repo.prochainNumero(evenement)))
        }
    }

    private fun sauverPhoto(bmp: Bitmap) {
        val fichier = File(filesDir, "photo_${System.currentTimeMillis()}.jpg")
        fichier.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        photoPath = fichier.absolutePath
        b.imgApercu.setImageBitmap(bmp)
        b.imgApercu.visibility = View.VISIBLE
        b.tvAucunePhoto.visibility = View.GONE
    }

    private fun garderAcces(uri: Uri) {
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    private fun enregistrer() {
        val numero = b.etNumero.text.toString().trim().toIntOrNull()
        val titre = b.etTitre.text.toString().trim()
        val evenement = evenementChoisi()

        when {
            numero == null -> b.etNumero.error = "Numéro requis"
            titre.isEmpty() -> b.etTitre.error = "Titre requis"
            evenement == null ->
                Toast.makeText(this, "Choisis ou saisis un événement", Toast.LENGTH_SHORT).show()
            else -> lifecycleScope.launch {
                repo.ajouterChant(userId, numero, titre, evenement, photoPath, audioUri, playbackUri)
                Toast.makeText(this@AjoutChantActivity, "Chant enregistré", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}