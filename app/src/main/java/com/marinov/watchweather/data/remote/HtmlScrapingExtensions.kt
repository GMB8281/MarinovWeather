package com.marinov.watchweather.data.remote

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.Normalizer

internal fun String?.orNA(): String = if (this.isNullOrBlank()) "N/A" else this.trim()

internal fun Element?.textOrNull(): String? = this?.text()?.trim()?.takeIf { it.isNotEmpty() }

internal fun normalizeLabel(value: String): String {
    val normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    return normalized.trim().lowercase().replace("\\s+".toRegex(), " ")
}

internal fun Element.findFirstBySelectors(vararg selectors: String): Element? {
    selectors.forEach { selector ->
        runCatching { selectFirst(selector) }.getOrNull()?.let { return it }
    }
    return null
}

internal fun Element.findTextBySelectors(vararg selectors: String): String? =
    findFirstBySelectors(*selectors)?.textOrNull()

internal fun findLabelValue(root: Element?, labels: List<String>): String? {
    if (root == null) return null
    val normalizedLabels = labels.map(::normalizeLabel)

    // Estrutura usada pelo Weather.com em blocos de detalhes
    root.select("[data-testid=WeatherDetailsListItem]").forEach { item ->
        val labelText = item.selectFirst("[data-testid=WeatherDetailsLabel]")?.textOrNull()
        if (labelText != null) {
            val normalizedLabelText = normalizeLabel(labelText)
            if (normalizedLabels.any { normalizedLabelText.contains(it) }) {
                val valueElement = item.selectFirst("[data-testid=wxData]")
                    ?: item.selectFirst("[data-testid=TemperatureValue]")
                    ?: item.selectFirst("div:last-child p, div:last-child span, p:last-child, span:last-child")

                valueElement?.textOrNull()?.let { if (it.isNotBlank()) return it }
            }
        }
    }

    // Busca genérica por rótulo/valor
    for (labelEl in root.select("p, span, div, h3, h4")) {
        val ownText = labelEl.ownText().trim()
        val labelText = if (ownText.isNotBlank()) ownText else labelEl.textOrNull() ?: continue

        // Evita containers grandes que contenham rótulo + valor juntos
        if (ownText.isBlank() && labelEl.children().size > 1) continue
        if (labelText.length > 80) continue

        val normalizedLabelText = normalizeLabel(labelText)
        val matches = normalizedLabels.any { label ->
            normalizedLabelText == label || normalizedLabelText.startsWith(label)
        }
        if (!matches) continue

        // Tenta pegar o irmão imediatamente seguinte
        labelEl.nextElementSibling()?.textOrNull()?.let { if (it.isNotBlank()) return it }

        // Tenta pegar o valor dentro do mesmo container
        val parent = labelEl.parent() ?: continue
        val valueEl = parent.selectFirst(
            "[data-testid=TemperatureValue], [data-testid=wxData], p:last-child, span:last-child"
        )
        if (valueEl != null && valueEl !== labelEl) {
            valueEl.textOrNull()?.let { valueText ->
                if (valueText.isNotBlank() && normalizeLabel(valueText) != normalizedLabelText) {
                    return valueText
                }
            }
        }

        // Último fallback: limpa o texto do container removendo o rótulo
        parent.textOrNull()?.let { fullText ->
            val cleaned = fullText.replace(labelText, "", ignoreCase = true).trim()
            if (cleaned.isNotBlank()) return cleaned
        }
    }

    return null
}

internal fun parseAirQuality(document: Document): String? {
    // Weather.com / novo padrão com data-testid
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

    // Climatempo / padrão antigo
    val airQualitySelectors = arrayOf(
        "div.card-health[data-id=Card_Indexes_Item_AirQuality] div.value-content p",
        "div.card-health[data-id*=AirQuality] div.value-content p",
        "[data-id=Card_Indexes_Item_AirQuality] .value-content p",
        "[data-id*=AirQuality] .value-content p",
        "[class*=air-quality] .value-content p",
        "[class*=AirQuality] .value-content p",
        "div.card-health p.value-content",
        ".card-health .value-content p"
    )

    airQualitySelectors.forEach { selector ->
        runCatching { document.selectFirst(selector)?.textOrNull() }
            .getOrNull()
            ?.let { if (it.isNotBlank()) return it }
    }

    return null
}