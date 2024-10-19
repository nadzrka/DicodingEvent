package com.nadzirakarimantika.dicodingevent.ui.setting

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.google.android.material.switchmaterial.SwitchMaterial
import com.nadzirakarimantika.dicodingevent.R
import com.nadzirakarimantika.dicodingevent.databinding.ActivitySettingBinding
import com.nadzirakarimantika.dicodingevent.ui.home.EventWorker
import java.util.concurrent.TimeUnit

class SettingActivity : AppCompatActivity() {
    private lateinit var workManager: WorkManager
    private lateinit var binding: ActivitySettingBinding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        workManager = WorkManager.getInstance(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
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

        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))

        val switchTheme = findViewById<SwitchMaterial>(R.id.switch_theme)

        val pref = SettingPreferences.getInstance(application.dataStore)
        val settingsViewModel = ViewModelProvider(this, SettingViewModelFactory(pref))[SettingsViewModel::class.java]

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            AppCompatDelegate.setDefaultNightMode(if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
            switchTheme.isChecked = isDarkModeActive
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveThemeSetting(isChecked)
        }

        settingsViewModel.loadNotificationSetting()

        settingsViewModel.isPeriodicTaskEnabled.observe(this) { isEnabled ->
            binding.checkBox.isChecked = isEnabled
        }

        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveNotificationSetting(isChecked)
            if (isChecked) {
                schedulePeriodicEventNotification()
            } else {
                cancelPeriodicTask()
            }
        }
    }

    private fun schedulePeriodicEventNotification() {
        val data = Data.Builder().build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(EventWorker::class.java, 1, TimeUnit.DAYS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "EventNotification",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }

    private fun cancelPeriodicTask() {
        workManager.cancelUniqueWork("EventNotification")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
