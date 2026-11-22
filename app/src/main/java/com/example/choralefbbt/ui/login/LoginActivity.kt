package com.example.choralefbbt.ui.login

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.GridLayout
import android.widget.Space
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.choralefbbt.R
import com.example.choralefbbt.data.AppDatabase
import com.example.choralefbbt.data.ChoraleRepository
import com.example.choralefbbt.databinding.ActivityLoginBinding
import com.example.choralefbbt.ui.ajout.AjoutChantActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding
    private val repo by lazy { ChoraleRepository(AppDatabase.get(this)) }
    private var pin = ""
    private val points by lazy { listOf(b.dot1, b.dot2, b.dot3, b.dot4) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.tabs.addTab(b.tabs.newTab().setText("Admin"))
        b.tabs.addTab(b.tabs.newTab().setText("Membre"))
        b.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { pin = ""; majPoints() }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        construireClavier()
        b.btnConnexion.setOnClickListener { valider() }
    }

    private fun construireClavier() {
        val touches = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "⌫")
        touches.forEach { t ->
            if (t.isEmpty()) {
                b.clavier.addView(Space(this), params())
                return@forEach
            }
            val fond = if (t == "⌫") R.color.vert_clair else android.R.color.white
            val btn = MaterialButton(this).apply {
                text = t
                textSize = 22f
                setTextColor(getColor(R.color.texte))
                cornerRadius = dp(32)
                insetTop = 0
                insetBottom = 0
                backgroundTintList = ColorStateList.valueOf(getColor(fond))
                setOnClickListener { toucher(t) }
            }
            b.clavier.addView(btn, params())
        }
    }

    private fun params() = GridLayout.LayoutParams(
        GridLayout.spec(GridLayout.UNDEFINED),
        GridLayout.spec(GridLayout.UNDEFINED, 1f)
    ).apply {
        width = 0
        height = dp(64)
        setMargins(dp(8), dp(6), dp(8), dp(6))
    }

    private fun toucher(t: String) {
        if (t == "⌫") pin = pin.dropLast(1)
        else if (pin.length < 4) pin += t
        majPoints()
    }

    private fun majPoints() {
        points.forEachIndexed { i, v ->
            v.setBackgroundResource(if (i < pin.length) R.drawable.bg_dot_on else R.drawable.bg_dot_off)
        }
    }

    private fun valider() {
        val admin = b.tabs.selectedTabPosition == 0
        if (!admin) {
            // Membre : pas de PIN. À remplacer par l'écran liste quand il existera.
            Toast.makeText(this, "Liste des chants à venir", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val user = repo.connecterAdmin(pin)
            if (user != null) {
                startActivity(
                    Intent(this@LoginActivity, AjoutChantActivity::class.java)
                        .putExtra("user_id", user.userId)
                )
            } else {
                Toast.makeText(this@LoginActivity, "Code incorrect", Toast.LENGTH_SHORT).show()
                pin = ""
                majPoints()
            }
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}