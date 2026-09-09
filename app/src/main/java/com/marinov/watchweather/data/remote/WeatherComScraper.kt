package com.marinov.watchweather.data.remote

import com.marinov.watchweather.data.model.WeatherData
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class WeatherComScraper : WeatherScraper {

    override fun scrape(url: String, document: Document): WeatherData? {
        val current = document.findFirstBySelectors(
            "section[data-testid=current-conditions]",
            "[class*=CurrentConditions]"
        )

        val city = getCity(document)
        val temperature = getTemperature(current, document)

        if (temperature.isNullOrBlank()) return null

        val sensationLabels = listOf("Sensação térmica", "Sensação", "Feels like")
        val sensation = findLabelValue(current, sensationLabels)
            ?: findLabelValue(document.body(), sensationLabels)

        val details = document.findFirstBySelectors(
            "section[data-testid=TodaysDetailsModule]",
            "div[data-testid=TodaysDetailsModule]",
            "section:has(h2:contains(Detalhes de hoje))",
            "section:has(h2:contains(Today's Details))"
        )

        val wind = getDetailValue(details, document, listOf("Vento", "Wind"))
        val humidity = getDetailValue(details, document, listOf("Umidade", "Humidity"))
        val pressure = getDetailValue(details, document, listOf("Pressão", "Pressure"))
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

    private fun getCity(document: Document): String? {
        val city = document.findTextBySelectors(
            "h1[class*=CurrentConditions--location]",
            "h1[data-testid=LocationHeader]",
            "header h1",
            "h1[class*=location]",
            "h1"
        )

        city?.let {
            val normalized = normalizeLabel(it)
            if (!normalized.startsWith("tempo agora") && !normalized.startsWith("weather now")) {
                return it
            }
        }

        document.selectFirst("meta[property=og:title]")?.attr("content")?.takeIf { it.isNotBlank() }?.let {
            return cleanTitle(it)
        }

        document.title().takeIf { it.isNotBlank() }?.let {
            return cleanTitle(it)
        }

        return null
    }

    private fun cleanTitle(raw: String): String {
        return raw.substringBefore(" | ")
            .substringBefore(" - ")
            .trim()
            .removePrefix("Clima em ")
            .removePrefix("Tempo em ")
            .removePrefix("Weather in ")
            .trim()
    }

    private fun getTemperature(current: Element?, document: Document): String? {
        current?.let { section ->
            val mainBlock = section.findFirstBySelectors(
                "div[class*=text-5xl]",
                "div[class*=primary]",
                "div:has(> span > span:containsOwn(Agora))"
            )

            mainBlock?.select("[data-testid=TemperatureValue]")?.last()?.textOrNull()?.let { return it }

            val temperatureOutsideSummary = section.select("[data-testid=TemperatureValue]")
                .filter { element ->
                    element.parents().none { parent ->
                        parent.attr("data-testid") == "current-conditions-temperatures"
                    }
                }

            temperatureOutsideSummary.firstOrNull()?.textOrNull()?.let { return it }
        }

        return document.findTextBySelectors(
            "div[class*=CurrentConditions--primary] span[data-testid=TemperatureValue]",
            "span[data-testid=TemperatureValue]"
        )
    }

    private fun getDetailValue(details: Element?, document: Document, labels: List<String>): String? {
        val normalizedLabels = labels.map(::normalizeLabel)

        details?.select("div[data-testid=WeatherDetailsListItem]")?.forEach { item ->
            val labelText = item.selectFirst("div[data-testid=WeatherDetailsLabel]")?.textOrNull()
            if (labelText != null) {
                val normalizedLabelText = normalizeLabel(labelText)
                if (normalizedLabels.any { normalizedLabelText.contains(it) }) {
                    val valueElement = item.selectFirst("div[data-testid=wxData], [data-testid=wxData]")
                        ?: item.selectFirst("div:last-child, p:last-child, span:last-child")

                    valueElement?.textOrNull()?.let { if (it.isNotBlank()) return it }
                }
            }
        }

        findLabelValue(details, labels)?.let { return it }
        return findLabelValue(document.body(), labels)
    }
}