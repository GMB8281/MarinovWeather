package com.marinov.watchweather.data.repository

import com.marinov.watchweather.data.model.City
import com.marinov.watchweather.data.model.DataSource
import com.marinov.watchweather.data.model.WeatherData
import com.marinov.watchweather.data.remote.ClimatempoScraper
import com.marinov.watchweather.data.remote.WeatherComScraper
import com.marinov.watchweather.data.remote.WeatherScraper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class WeatherRepository(
    private val climatempoScraper: WeatherScraper = ClimatempoScraper(),
    private val weatherComScraper: WeatherScraper = WeatherComScraper()
) {

    suspend fun fetchWeather(city: City): Result<WeatherData> = withContext(Dispatchers.IO) {
        try {
            val document = Jsoup.connect(city.url)
                .timeout(20_000)
                .userAgent(
                    "Mozilla/5.0 (Linux; Android 10; Smartwatch) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                )
                .header(
                    "Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8"
                )
                .header("Accept-Language", "pt-BR,pt;q=0.9,en;q=0.8")
                .referrer("https://www.google.com/")
                .followRedirects(true)
                .get()

            val scraper = when (city.source) {
                DataSource.CLIMATEMPO -> climatempoScraper
                DataSource.WEATHER_COM -> weatherComScraper
            }

            val data = scraper.scrape(city.url, document)
                ?: throw IllegalStateException("Não foi possível interpretar os dados da página.")

            Result.success(data)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}