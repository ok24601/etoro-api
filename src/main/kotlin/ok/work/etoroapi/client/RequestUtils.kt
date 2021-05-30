package ok.work.etoroapi.client

import ok.work.etoroapi.client.browser.EtoroMetadata
import ok.work.etoroapi.model.TradingMode
import okhttp3.Request
import java.net.URI
import java.net.http.HttpRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.round

fun prepareRequest(path: String, auth: String, mode: TradingMode, credentials: EtoroMetadata): HttpRequest.Builder {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val formatted = now.format(formatter)
    val dt = formatted.split(".")[0]
    println("prepareRequest Current Date and Time: $dt")

    return HttpRequest.newBuilder().uri(URI("${credentials.baseUrl}/${path}"))
            .header("authority", credentials.domain)
            .header("accounttype", mode.name)
            .header("x-sts-appdomain", credentials.baseUrl)
            .header("content-type", "application/json;charset=UTF-8")
            .header("accept", "application/json, text/plain, */*")
            .header("x-sts-gatewayappid", "90631448-9A01-4860-9FA5-B4EBCDE5EA1D")
            .header("applicationidentifier", "ReToro")
            .header("applicationversion", "326.0.3")
            .header("origin", credentials.baseUrl)
            .header("sec-fetch-site", "same-origin")
            .header("sec-fetch-mode", "cors")
            .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
            .header("authorization", auth)
            .header("referer", "${credentials.baseUrl}/login")
            .header("cookie", credentials.cookies)
            .header("sec-fetch-dest", "empty")
            .header("x-csrf-token", credentials.cToken)
            .header("x-sts-clienttime", dt)
}

fun prepareOkRequest(path: String, auth: String, mode: TradingMode, credentials: EtoroMetadata): Request.Builder {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val formatted = now.format(formatter)
    val dt = formatted.split(".")[0]
    println("prepareRequest Current Date and Time: $dt")

    return Request.Builder().url("${credentials.baseUrl}/${path}")
            .header("authority", credentials.domain)
            .header("accounttype", mode.name)
            .header("x-sts-appdomain", credentials.baseUrl)
            .header("content-type", "application/json;charset=UTF-8")
            .header("accept", "application/json, text/plain, */*")
            .header("x-sts-gatewayappid", "90631448-9A01-4860-9FA5-B4EBCDE5EA1D")
            .header("applicationidentifier", "ReToro")
            .header("applicationversion", "326.0.3")
            .header("origin", credentials.baseUrl)
            .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
            .header("sec-fetch-site", "same-origin")
            .header("sec-fetch-mode", "cors")
            .header("authorization", auth)
            .header("referer", "${credentials.baseUrl}/login")
            .header("cookie", credentials.cookies)
            .header("sec-fetch-dest", "empty")
            .header("x-csrf-token", "$credentials.cToken")
            .header("x-sts-clienttime", "$dt")
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}