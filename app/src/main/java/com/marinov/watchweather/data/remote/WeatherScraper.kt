package com.marinov.watchweather.data.remote

import com.marinov.watchweather.data.model.WeatherData
import org.jsoup.nodes.Document

interface WeatherScraper {
    fun scrape(url: String, document: Document): WeatherData?
}