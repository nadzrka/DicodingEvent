package com.nadzirakarimantika.dicodingevent.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nadzirakarimantika.dicodingevent.R
import com.nadzirakarimantika.dicodingevent.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    this@SettingActivity,
                    R.drawable.arrow_back_24dp_e8eaed_fill0_wght400_grad0_opsz24
                )
            )
            title = getString(R.string.setting_title)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}