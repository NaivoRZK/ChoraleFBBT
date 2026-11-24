package com.example.choralefbbt.ui.parametres

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.choralefbbt.databinding.ActivityParametresBinding

class ParametresActivity : AppCompatActivity() {

    private lateinit var b: ActivityParametresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityParametresBinding.inflate(layoutInflater)
        setContentView(b.root)

        val prefs = getSharedPreferences("chorale_prefs", Context.MODE_PRIVATE)
        val cloudActive = prefs.getBoolean("cloud_enabled", false)
        val serverUrl = prefs.getString("server_url", "") ?: ""

        b.switchModeCloud.isChecked = cloudActive
        b.etServerUrl.setText(serverUrl)
        majTexteStatut(cloudActive)

        b.switchModeCloud.setOnCheckedChangeListener { _, isChecked ->
            majTexteStatut(isChecked)
        }

        b.btnEnregistrerParametres.setOnClickListener {
            prefs.edit()
                .putBoolean("cloud_enabled", b.switchModeCloud.isChecked)
                .putString("server_url", b.etServerUrl.text.toString().trim())
                .apply()

            Toast.makeText(this, "Paramètres enregistrés", Toast.LENGTH_SHORT).show()
            finish()
        }

        b.btnRetourLogin.setOnClickListener {
            finish()
        }
    }

    private fun majTexteStatut(cloudActive: Boolean) {
        b.tvStatutActuel.text = if (cloudActive) {
            "Mode actuel : Cloud (Synchronisation activée)"
        } else {
            "Mode actuel : Hors ligne (Base locale Room)"
        }
    }
}