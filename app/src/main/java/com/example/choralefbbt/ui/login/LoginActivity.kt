package com.example.choralefbbt.ui.login

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.choralefbbt.R
import com.example.choralefbbt.data.AppDatabase
import com.example.choralefbbt.data.ChoraleRepository
import com.example.choralefbbt.databinding.ActivityLoginBinding
import com.example.choralefbbt.ui.ajout.AjoutChantActivity
import com.example.choralefbbt.ui.parametres.ParametresActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding
    private val repo by lazy { ChoraleRepository(AppDatabase.get(this)) }
    private var pin = ""
    private val points by lazy { listOf(b.pinDot1, b.pinDot2, b.pinDot3, b.pinDot4) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        initialiserActionsHaut()

        b.layoutAdminFields.visibility = if (b.tabRole.selectedTabPosition == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }

        b.tabRole.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val estAdmin = (tab?.position ?: 0) == 0
                b.layoutAdminFields.visibility = if (estAdmin) View.VISIBLE else View.GONE
                reinitialiserPin()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        configurerSaisiePin()
        b.btnLogin.setOnClickListener { valider() }
    }

    override fun onResume() {
        super.onResume()
        majIndicateurCloud()
    }

    private fun initialiserActionsHaut() {
        majIndicateurCloud()

        b.btnCloudStatus.setOnClickListener {
            val prefs = getSharedPreferences("chorale_prefs", MODE_PRIVATE)
            val nouveauModeCloud = !prefs.getBoolean("cloud_enabled", false)
            prefs.edit().putBoolean("cloud_enabled", nouveauModeCloud).apply()
            majIndicateurCloud()

            val message = if (nouveauModeCloud) {
                "Mode Cloud activé"
            } else {
                "Mode Hors ligne (Room local)"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        b.btnSettings.setOnClickListener {
            startActivity(Intent(this, ParametresActivity::class.java))
        }
    }

    private fun majIndicateurCloud() {
        val prefs = getSharedPreferences("chorale_prefs", MODE_PRIVATE)
        val cloudActive = prefs.getBoolean("cloud_enabled", false)

        if (cloudActive) {
            b.btnCloudStatus.setImageResource(R.drawable.ic_cloud_done)
            b.btnCloudStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E6F4EA"))
            b.btnCloudStatus.imageTintList = ColorStateList.valueOf(Color.parseColor("#1E7A46"))
        } else {
            b.btnCloudStatus.setImageResource(R.drawable.ic_cloud_off)
            b.btnCloudStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
            b.btnCloudStatus.imageTintList = ColorStateList.valueOf(Color.parseColor("#1E7A46"))
        }
    }

    private fun configurerSaisiePin() {
        b.etPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pin = s?.toString().orEmpty()
                majPoints()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun reinitialiserPin() {
        pin = ""
        if (b.etPin.text?.isNotEmpty() == true) {
            b.etPin.text?.clear()
        }
        majPoints()
    }

    private fun majPoints() {
        points.forEachIndexed { i, v ->
            val estRempli = i < pin.length
            v.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(if (estRempli) "#1E7A46" else "#DCE3DF")
            )
        }
    }

    private fun valider() {
        val admin = b.tabRole.selectedTabPosition == 0
        if (!admin) {
            Toast.makeText(this, "Liste des chants à venir", Toast.LENGTH_SHORT).show()
            return
        }

        val nom = b.etLastName.text?.toString()?.trim().orEmpty()
        val prenom = b.etFirstName.text?.toString()?.trim().orEmpty()
        pin = b.etPin.text?.toString()?.trim().orEmpty()

        if (nom.isEmpty() || prenom.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir le nom et le prénom", Toast.LENGTH_SHORT).show()
            return
        }

        if (pin.length < 4) {
            Toast.makeText(this, "Veuillez saisir le code PIN à 4 chiffres", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val user = repo.connecterAdmin(pin)
            if (user != null &&
                user.lastName.equals(nom, ignoreCase = true) &&
                user.firstName.equals(prenom, ignoreCase = true)
            ) {
                Toast.makeText(
                    this@LoginActivity,
                    "Bienvenue ${user.firstName} ${user.lastName}",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(
                    Intent(this@LoginActivity, AjoutChantActivity::class.java)
                        .putExtra("user_id", user.userId)
                )
            } else {
                Toast.makeText(this@LoginActivity, "Nom, prénom ou code incorrect", Toast.LENGTH_SHORT).show()
                reinitialiserPin()
            }
        }
    }
}