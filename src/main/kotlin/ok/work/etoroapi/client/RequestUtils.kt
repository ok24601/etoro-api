package ok.work.etoroapi.client

import ok.work.etoroapi.client.browser.EtoroMetadata
import ok.work.etoroapi.model.TradingMode
import okhttp3.Request
import java.net.URI
import java.net.http.HttpRequest

fun prepareRequest(path: String, auth: String, mode: TradingMode, credentials: EtoroMetadata): HttpRequest.Builder {
    return HttpRequest.newBuilder().uri(URI("${credentials.baseUrl}/${path}"))
            .header("authority", credentials.domain)
            .header("accounttype", mode.name)
            .header("x-sts-appdomain", credentials.baseUrl)
            .header("content-type", "application/json;charset=UTF-8")
            .header("accept", "application/json, text/plain, */*")
            .header("x-sts-gatewayappid", "90631448-9A01-4860-9FA5-B4EBCDE5EA1D")
            .header("applicationidentifier", "ReToro")
            .header("applicationversion", "212.0.7")
            .header("origin", credentials.baseUrl)
            .header("sec-fetch-site", "same-origin")
            .header("sec-fetch-mode", "cors")
            .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
            .header("authorization", auth)
            .header("referer", "${credentials.baseUrl}/login")
            .header("cookie", credentials.cookies)
}

fun prepareOkRequest(path: String, auth: String, mode: TradingMode, credentials: EtoroMetadata): Request.Builder {
    return Request.Builder().url("${credentials.baseUrl}/${path}")
            .header("authority", credentials.domain)
            .header("accounttype", mode.name)
            .header("x-sts-appdomain", credentials.baseUrl)
            .header("content-type", "application/json;charset=UTF-8")
            .header("accept", "application/json, text/plain, */*")
            .header("x-sts-gatewayappid", "90631448-9A01-4860-9FA5-B4EBCDE5EA1D")
            .header("applicationidentifier", "ReToro")
            .header("applicationversion", "212.0.7")
            .header("origin", credentials.baseUrl)
            .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
            .header("sec-fetch-site", "same-origin")
            .header("sec-fetch-mode", "cors")
            .header("authorization", auth)
            .header("referer", "${credentials.baseUrl}/login")
            .header("cookie", credentials.cookies)
}
