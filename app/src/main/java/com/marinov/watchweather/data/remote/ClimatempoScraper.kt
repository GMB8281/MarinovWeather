package com.marinov.watchweather.data.remote

import com.marinov.watchweather.data.model.WeatherData
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ClimatempoScraper : WeatherScraper {

    override fun scrape(url: String, document: Document): WeatherData? {
        val mainCard = document.findFirstBySelectors(
            "article[data-type=Card_FirstElement]",
            "article.now-forecast-card.card",
            ".now-forecast-card.card",
            "div.card[data-type=Card_FirstElement]"
        )

        val city = getCity(document, mainCard)

        val temperature = mainCard?.findTextBySelectors(
            ".now-forecast-card__temperature",
            "span.now-forecast-card__temperature",
            "span.-font-55",
            "[class*=temperature]"
        ) ?: document.findTextBySelectors(
            ".now-forecast-card__temperature",
            "span.-font-55"
        )

        if (temperature.isNullOrBlank()) return null

        val sensationLabels = listOf("Sensação Térmica", "Sensação", "Feels Like")
        val sensation = getMetricValue(mainCard, sensationLabels)
            ?: findLabelValue(mainCard, sensationLabels)
            ?: findLabelValue(document.body(), sensationLabels)

        val windLabels = listOf("Vento", "Wind")
        val wind = getMetricValue(mainCard, windLabels)
            ?: findLabelValue(mainCard, windLabels)
            ?: findLabelValue(document.body(), windLabels)

        val humidityLabels = listOf("Umidade", "Humidity")
        val humidity = getMetricValue(mainCard, humidityLabels)
            ?: findLabelValue(mainCard, humidityLabels)
            ?: findLabelValue(document.body(), humidityLabels)

        val pressureLabels = listOf("Pressão", "Pressure")
        val pressure = getMetricValue(mainCard, pressureLabels)
            ?: findLabelValue(mainCard, pressureLabels)
            ?: findLabelValue(document.body(), pressureLabels)

        val airQuality = parseAirQuality(document)

        return WeatherData(
            city = city.orNA(),
            temperature = temperature.orNA(),
            sensation = sensation.orNA(),
            wind = wind.orNA(),
            humidity = humidity.orNA(),
            pressure = pressure.orNA(),
            airQuality = airQuality ?: "--"
        )
    }

    private fun getCity(document: Document, mainCard: Element?): String? {
        mainCard?.selectFirst("h1.now-forecast-card__title a")?.textOrNull()?.let { return it }

        mainCard?.selectFirst("h1.now-forecast-card__title")?.textOrNull()?.let { title ->
            return title.replace(Regex("(?i)^tempo agora em\\s*"), "").trim()
        }

        mainCard?.selectFirst("h1")?.textOrNull()?.let { title ->
            return title.replace(Regex("(?i)^tempo agora em\\s*"), "").trim()
        }

        document.title().takeIf { it.isNotBlank() }?.let { title ->
            return title.substringBefore(" | ").substringBefore(" - ").trim()
        }

        return null
    }

    private fun getMetricValue(root: Element?, labels: List<String>): String? {
        if (root == null) return null
        val normalizedLabels = labels.map(::normalizeLabel)

        val metric = root.select(".now-forecast-card__metric").firstOrNull { element ->
            val heading = element.selectFirst(".now-forecast-card__metric-heading")?.textOrNull().orEmpty()
            val normalizedHeading = normalizeLabel(heading)
            normalizedLabels.any { normalizedHeading.contains(it) }
        }

        metric?.selectFirst(".now-forecast-card__metric-value")?.textOrNull()?.let { return it }

        return findLabelValue(root, labels)
    }
}