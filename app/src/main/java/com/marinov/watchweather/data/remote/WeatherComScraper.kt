package com.marinov.watchweather.data.remote

import com.marinov.watchweather.data.model.WeatherData
import org.jsoup.nodes.Document

class WeatherComScraper : WeatherScraper {

    override fun scrape(url: String, document: Document): WeatherData? {
        val city = getCity(document)
        val temperature = getTemperature(document)

        if (temperature.isNullOrBlank() || temperature == "--") return null

        val sensation = getSensation(document)

        val wind = getDetailValue(document, "Wind")
        val humidity = getDetailValue(document, "PercentageValue")
        val pressure = getDetailValue(document, "PressureValue")
        val rawAirQuality = getDetailValue(document, "AirQualityValue")

        // Separa o formato "60 - Moderada" e retorna apenas "Moderada"
        val airQuality = rawAirQuality?.let {
            if (it.contains("-")) it.substringAfter("-").trim() else it
        } ?: parseAirQualityFallback(document)

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

    private fun getCity(document: Document): String {
        val h1 = document.select("h1").firstOrNull {
            it.className().contains("text-2xl") ||
                    it.className().contains("font-extrabold") ||
                    it.text().contains("Clima", ignoreCase = true) ||
                    it.text().contains("Weather", ignoreCase = true)
        } ?: document.selectFirst("h1")

        val exactSiblingMatch = h1?.nextElementSibling()?.selectFirst("p")?.text()?.trim()
        if (!exactSiblingMatch.isNullOrBlank() && isNotFooterLink(exactSiblingMatch)) {
            return exactSiblingMatch
        }

        val h1ParentMatch = h1?.parent()?.selectFirst("p")?.text()?.trim()
        if (!h1ParentMatch.isNullOrBlank() && isNotFooterLink(h1ParentMatch)) {
            return h1ParentMatch
        }

        h1?.text()?.let {
            val cleaned = it.replace(Regex("(?i)\\s*(Clima em|Weather in|Tiempo en|Weather|Clima)\\s*"), "").trim()
            if (cleaned.isNotBlank() && isNotFooterLink(cleaned)) return cleaned
        }

        return document.title().substringBefore(" | ").substringBefore(" - ").trim()
    }

    private fun isNotFooterLink(text: String): Boolean {
        val lower = text.lowercase()
        return !lower.contains("termos") &&
                !lower.contains("privacidade") &&
                !lower.contains("política") &&
                !lower.contains("policy")
    }

    private fun getTemperature(document: Document): String? {
        // Estratégia principal: o span com "font-extrabold" contém o valor final
        // (ignora os frames anteriores da animação de troca de temperatura).
        val extraboldValue = document.selectFirst("span[class*=font-extrabold] span[data-testid=TemperatureValue]")
        if (extraboldValue != null) {
            return extraboldValue.text().trim()
        }

        // Fallback: pega todos os valores de temperatura da página, ignorando os que
        // estão dentro de blocos de sensação/máx/mín, previsão horária ou 10 dias.
        val allTemps = document.select("span[data-testid=TemperatureValue]")
        val mainTemps = allTemps.filterNot { temp ->
            temp.parents().any { parent ->
                val testId = parent.attr("data-testid")
                testId.contains("temperatures", ignoreCase = true) ||
                        testId.contains("hourly", ignoreCase = true) ||
                        testId.contains("forecast", ignoreCase = true)
            }
        }
        return mainTemps.lastOrNull()?.text()?.trim() ?: allTemps.firstOrNull()?.text()?.trim()
    }

    private fun getSensation(document: Document): String? {
        val sensationContainer = document.selectFirst("div[data-testid=current-conditions-temperatures]") ?: return null

        // Dentro desse bloco os TemperatureValue aparecem sempre na ordem
        // Sensação, Máxima, Mínima — então o PRIMEIRO é sempre a sensação térmica.
        return sensationContainer.selectFirst("span[data-testid=TemperatureValue]")?.text()?.trim()
    }

    /**
     * Busca o valor de um detalhe (Vento, Umidade, Pressão, Qualidade do ar...) pelo
     * data-testid ESPECÍFICO daquele campo (ex.: "Wind", "PercentageValue",
     * "PressureValue", "AirQualityValue"), escopando a busca para dentro da section
     * "Clima hoje" (data-testid="current-details").
     *
     * IMPORTANTE: isto só funciona se o "document" recebido já for o HTML
     * renderizado (pós-JavaScript). Veja RenderedHtmlFetcher.
     */
    private fun getDetailValue(document: Document, valueTestId: String): String? {
        val detailsSection = document.selectFirst("section[data-testid=current-details]")
            ?: document.selectFirst("[aria-label*=Clima hoje]")
            ?: document.selectFirst("[aria-label*=Today]")
            ?: document.selectFirst("[aria-label*=current-details]")

        val scope = detailsSection?.clone() ?: document.clone()
        scope.select("svg, title, path").remove()

        return scope.selectFirst("[data-testid=$valueTestId]")?.text()?.cleanValue()
    }

    private fun String.cleanValue(): String =
        this.replace("\u00A0", " ").replace(Regex("\\s+"), " ").trim()

    private fun parseAirQualityFallback(document: Document): String? {
        // Fallback legado para o widget antigo de AQI em formato de "donut chart",
        // caso a página volte a usar esse layout separado em vez da linha de detalhe.
        document.selectFirst("div[data-testid=AirQualityIndex]")?.let { aqi ->
            val value = aqi.selectFirst("[data-testid=DonutChartValue]")?.textOrNull()
            val allH2 = aqi.select("h2").mapNotNull { it.textOrNull() }
            val category = allH2.firstOrNull { text ->
                val normalized = normalizeLabel(text)
                !normalized.contains("indice de qualidade do ar") &&
                        !normalized.contains("qualidade do ar") &&
                        !normalized.contains("air quality")
            } ?: allH2.getOrNull(1)

            return when {
                value != null && category != null -> "$category ($value)"
                value != null -> value
                category != null -> category
                else -> aqi.textOrNull()
            }
        }
        return null
    }
}
