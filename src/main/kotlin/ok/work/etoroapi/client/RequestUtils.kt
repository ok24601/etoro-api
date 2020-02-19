package ok.work.etoroapi.client

import ok.work.etoroapi.client.credentials.Credentials
import ok.work.etoroapi.model.TradingMode
import java.net.URI
import java.net.http.HttpRequest

fun prepareRequest(path: String, auth: String, mode: TradingMode, credentials: Credentials): HttpRequest.Builder {
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
            .header("cookie", "etoroHPRedirect=1; _ga=GA1.2.1096383890.1543357062; visid_incap_172517=ZNWYjpoOTt6IOfHx3HN5VxPG/VsAAAAAQUIPAAAAAACPZ88tOSyD62NooqBbZ/hN; visid_incap_773285=pfQdz3B9TZuve/PhmXgPnF3G/VsAAAAAQUIPAAAAAAC7uOLsspgsQRNnV9pm9LiA; fbm_166209726726710=base_domain=.etoro.com; liveagent_oref=; liveagent_ptid=b60ed7d3-3047-4752-86d1-14ba900fd3c4; _DCMN_id.90.13db=2a8b42dd8726053f.1543357972.4.1543872510.1543768287.; TMIS2=${credentials.tsim2}; _gat=1;")
}
