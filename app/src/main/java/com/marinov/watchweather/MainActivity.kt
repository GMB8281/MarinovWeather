package com.marinov.watchweather

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.marinov.watchweather.data.model.City
import com.marinov.watchweather.data.model.DataSource
import com.marinov.watchweather.data.model.WeatherData
import com.marinov.watchweather.data.repository.WeatherRepository
import com.marinov.watchweather.ui.CityUiBinder
import com.marinov.watchweather.ui.ViewState
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SHARED_PREFS_NAME = "WeatherAppPrefs"
        private const val KEY_LAST_CITY_URL = "last_city_url"
        private const val KEY_LAST_DATA_SOURCE = "last_data_source"
        private const val LONG_PRESS_DURATION = 5000L
    }

    private val repository = WeatherRepository()
    private val cities = CityUiBinder.buttonToCity

    // UI de Previsão
    private lateinit var tvCity: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvSensation: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvPressure: TextView
    private lateinit var tvAirQuality: TextView
    private lateinit var weatherContent: LinearLayout
    private lateinit var weatherScrollView: ScrollView

    // UI de Controle
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var selectionScreen: ScrollView
    private lateinit var countrySelectionLayout: LinearLayout
    private lateinit var citySelectionLayouts: List<LinearLayout>
    private lateinit var sharedPreferences: SharedPreferences

    // Long press
    private val longPressHandler = Handler(Looper.getMainLooper())
    private var longPressRunnable: Runnable? = null
    private var isLongPressActive = false

    private val countryToCityLayoutMap by lazy {
        mapOf<Int, LinearLayout>(
            R.id.btn_country_brazil to findViewById<LinearLayout>(R.id.cities_brazil),
            R.id.btn_country_china to findViewById<LinearLayout>(R.id.cities_china),
            R.id.btn_country_india to findViewById<LinearLayout>(R.id.cities_india),
            R.id.btn_country_russia to findViewById<LinearLayout>(R.id.cities_russia),
            R.id.btn_country_ukraine to findViewById<LinearLayout>(R.id.cities_ukraine),
            R.id.btn_country_qatar to findViewById<LinearLayout>(R.id.cities_qatar),
            R.id.btn_country_germany to findViewById<LinearLayout>(R.id.cities_germany),
            R.id.btn_country_france to findViewById<LinearLayout>(R.id.cities_france),
            R.id.btn_country_portugal to findViewById<LinearLayout>(R.id.cities_portugal),
            R.id.btn_country_italy to findViewById<LinearLayout>(R.id.cities_italy),
            R.id.btn_country_belgium to findViewById<LinearLayout>(R.id.cities_belgium),
            R.id.btn_country_netherlands to findViewById<LinearLayout>(R.id.cities_netherlands),
            R.id.btn_country_south_korea to findViewById<LinearLayout>(R.id.cities_south_korea),
            R.id.btn_country_north_korea to findViewById<LinearLayout>(R.id.cities_north_korea),
            R.id.btn_country_spain to findViewById<LinearLayout>(R.id.cities_spain),
            R.id.btn_country_cuba to findViewById<LinearLayout>(R.id.cities_cuba),
            R.id.btn_country_chile to findViewById<LinearLayout>(R.id.cities_chile),
            R.id.btn_country_mexico to findViewById<LinearLayout>(R.id.cities_mexico),
            R.id.btn_country_venezuela to findViewById<LinearLayout>(R.id.cities_venezuela),
            R.id.btn_country_bolivia to findViewById<LinearLayout>(R.id.cities_bolivia),
            R.id.btn_country_paraguay to findViewById<LinearLayout>(R.id.cities_paraguay),
            R.id.btn_country_uruguay to findViewById<LinearLayout>(R.id.cities_uruguay),
            R.id.btn_country_argentina to findViewById<LinearLayout>(R.id.cities_argentina),
            R.id.btn_country_poland to findViewById<LinearLayout>(R.id.cities_poland),
            R.id.btn_country_japan to findViewById<LinearLayout>(R.id.cities_japan),
            R.id.btn_country_united_states to findViewById<LinearLayout>(R.id.cities_united_states),
            R.id.btn_country_uae to findViewById<LinearLayout>(R.id.cities_uae),
            R.id.btn_country_iran to findViewById<LinearLayout>(R.id.cities_iran),
            R.id.btn_country_turkey to findViewById<LinearLayout>(R.id.cities_turkey),
            R.id.btn_country_israel to findViewById<LinearLayout>(R.id.cities_israel)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupSelectionListeners()
        setupBackButtonHandler()
        setupLongPressHandler()

        val lastUrl = sharedPreferences.getString(KEY_LAST_CITY_URL, null)
        val lastDataSourceName = sharedPreferences.getString(KEY_LAST_DATA_SOURCE, null)
        val lastDataSource = lastDataSourceName?.let { name ->
            runCatching { DataSource.valueOf(name) }.getOrNull()
        }

        if (lastUrl != null && lastDataSource != null) {
            fetchAndDisplayWeatherData(City(lastUrl, lastDataSource))
        } else {
            updateViewState(ViewState.SELECTION)
        }
    }

    private fun initializeViews() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)

        tvCity = findViewById(R.id.tv_city)
        tvTemperature = findViewById(R.id.tv_temperature)
        tvSensation = findViewById(R.id.tv_sensation)
        tvWind = findViewById(R.id.tv_wind)
        tvHumidity = findViewById(R.id.tv_humidity)
        tvPressure = findViewById(R.id.tv_pressure)
        tvAirQuality = findViewById(R.id.tv_air_quality)
        weatherContent = findViewById(R.id.weather_content)
        weatherScrollView = findViewById(R.id.weather_scroll)

        progressBar = findViewById(R.id.progress_bar)
        tvError = findViewById(R.id.tv_error)
        selectionScreen = findViewById(R.id.selection_screen)
        countrySelectionLayout = findViewById(R.id.country_selection)

        citySelectionLayouts = countryToCityLayoutMap.values.toList()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupLongPressHandler() {
        weatherScrollView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (weatherScrollView.isVisible) {
                        isLongPressActive = false
                        longPressRunnable = Runnable {
                            isLongPressActive = true
                            showLocationChangeDialog()
                        }
                        longPressHandler.postDelayed(longPressRunnable!!, LONG_PRESS_DURATION)
                    }
                    false
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    longPressRunnable?.let { longPressHandler.removeCallbacks(it) }
                    false
                }
                else -> false
            }
        }
    }

    private fun showLocationChangeDialog() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.dialog_change_location_message))
            .setPositiveButton(getString(R.string.dialog_confirm)) { _, _ ->
                clearSavedLocationAndRestart()
            }
            .setNegativeButton(getString(R.string.dialog_deny)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setCancelable(true)
            .create()

        dialog.show()

        dialog.window?.let { window ->
            val layoutParams = WindowManager.LayoutParams().apply {
                copyFrom(window.attributes)
                gravity = Gravity.CENTER
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
            window.attributes = layoutParams
            window.setGravity(Gravity.CENTER)
        }
    }

    @SuppressLint("ApplySharedPref", "UseKtx")
    private fun clearSavedLocationAndRestart() {
        with(sharedPreferences.edit()) {
            remove(KEY_LAST_CITY_URL)
            remove(KEY_LAST_DATA_SOURCE)
            commit()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @SuppressLint("ApplySharedPref", "UseKtx")
    private fun setupSelectionListeners() {
        countryToCityLayoutMap.forEach { (countryButtonId, cityLayout) ->
            findViewById<Button>(countryButtonId).setOnClickListener {
                countrySelectionLayout.visibility = View.GONE
                cityLayout.visibility = View.VISIBLE
            }
        }

        cities.forEach { (buttonId, city) ->
            findViewById<View>(buttonId)?.setOnClickListener {
                with(sharedPreferences.edit()) {
                    putString(KEY_LAST_CITY_URL, city.url)
                    putString(KEY_LAST_DATA_SOURCE, city.source.name)
                    commit()
                }

                fetchAndDisplayWeatherData(city)
            }
        }
    }

    private fun setupBackButtonHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val visibleCityLayout = if (selectionScreen.isVisible) {
                    citySelectionLayouts.firstOrNull { it.isVisible }
                } else {
                    null
                }

                if (visibleCityLayout != null) {
                    visibleCityLayout.visibility = View.GONE
                    countrySelectionLayout.visibility = View.VISIBLE
                } else {
                    finish()
                }
            }
        })
    }

    private fun fetchAndDisplayWeatherData(city: City) {
        lifecycleScope.launch {
            updateViewState(ViewState.LOADING)

            repository.fetchWeather(city)
                .onSuccess { data ->
                    updateUI(data)
                    updateViewState(ViewState.CONTENT)

                    weatherScrollView.post {
                        weatherScrollView.scrollTo(0, 0)
                    }
                }
                .onFailure { error ->
                    error.printStackTrace()
                    updateViewState(ViewState.ERROR)
                }
        }
    }

    private fun updateUI(data: WeatherData) {
        tvCity.text = data.city
        tvCity.isSelected = true

        tvTemperature.text = data.temperature
        tvSensation.text = getString(R.string.sensation_format, data.sensation)
        tvWind.text = getString(R.string.wind_format, data.wind)
        tvHumidity.text = getString(R.string.humidity_format, data.humidity)
        tvPressure.text = getString(R.string.pressure_format, data.pressure)
        tvAirQuality.text = getString(R.string.air_quality_format, data.airQuality)
    }

    private fun updateViewState(state: ViewState) {
        selectionScreen.visibility = if (state == ViewState.SELECTION) View.VISIBLE else View.GONE
        progressBar.visibility = if (state == ViewState.LOADING) View.VISIBLE else View.GONE
        weatherScrollView.visibility = if (state == ViewState.CONTENT) View.VISIBLE else View.GONE
        tvError.visibility = if (state == ViewState.ERROR) View.VISIBLE else View.GONE
    }
}