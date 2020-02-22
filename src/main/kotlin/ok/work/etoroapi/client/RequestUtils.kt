package ok.work.etoroapi.client

import ok.work.etoroapi.client.cookies.EtoroMetadata
import ok.work.etoroapi.model.TradingMode
import java.net.URI
import java.net.http.HttpRequest

fun prepareRequest(path: String, auth: String, mode: TradingMode, credentials: EtoroMetadata): HttpRequest.Builder {
    return HttpRequest.newBuilder().uri(URI("https://www.etoro.com/${path}"))
            .header("authority", "www.etoro.com")
            .header("accounttype", mode.name)
            .header("x-sts-appdomain", "https://www.etoro.com")
            .header("content-type", "application/json;charset=UTF-8")
            .header("accept", "application/json, text/plain, */*")
            .header("x-sts-gatewayappid", "90631448-9A01-4860-9FA5-B4EBCDE5EA1D")
            .header("applicationidentifier", "ReToro")
            .header("applicationversion", "212.0.7")
            .header("origin", "https://www.etoro.com")
            .header("sec-fetch-site", "same-origin")
            .header("sec-fetch-mode", "cors")
            .header("authorization", auth)
            .header("referer", "https://www.etoro.com/login")
            .header("cookie", credentials.cookies)
}
