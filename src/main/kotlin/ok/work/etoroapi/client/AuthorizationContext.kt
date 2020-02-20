package ok.work.etoroapi.client

import ok.work.etoroapi.client.credentials.CredentialsService
import ok.work.etoroapi.model.TradingMode
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import javax.annotation.PostConstruct

@Component
class AuthorizationContext {

    lateinit var exchangeToken: String
    lateinit var accessToken: String
    lateinit var requestId: String
    lateinit var demogcid: String
    lateinit var realgcid: String
    private lateinit var userdata: JSONObject

    private val client: HttpClient = HttpClient.newHttpClient()

    @Autowired
    private lateinit var credentialsService: CredentialsService

    @PostConstruct
    fun setupAuthorizationContext() {
        requestId = UUID.randomUUID().toString().toLowerCase()

        //only for dev
        val token = System.getenv("TOKEN")

        if (token != null) {
            exchangeToken = token
        } else {
            auth(System.getenv("LOGIN"), System.getenv("PASSWORD"))
            exchange()
        }
        getAccountData(TradingMode.REAL)
    }

    private fun auth(username: String, pwd: String) {
        val req = HttpRequest.newBuilder().uri(URI("https://www.etoro.com/api/sts/v2/oauth/auth?client_request_id=${requestId}"))
                .header("authority", "www.etoro.com")
                .header("accounttype", "Demo")
                .header("x-sts-appdomain", "https://www.etoro.com")
                .header("content-type", "application/json;charset=UTF-8")
                .header("accept", "application/json, text/plain, */*")
                .header("x-sts-gatewayappid", "90631448-9A01-4860-9FA5-B4EBCDE5EA1D")
                .header("applicationidentifier", "ReToro")
                .header("applicationversion", "212.0.7")
                .header("origin", "https://www.etoro.com")
                .header("sec-fetch-site", "same-origin")
                .header("sec-fetch-mode", "cors")
                .header("referer", "https://www.etoro.com/login")
                .header("cookie", "etoroHPRedirect=1; _ga=GA1.2.1096383890.1543357062; visid_incap_172517=ZNWYjpoOTt6IOfHx3HN5VxPG/VsAAAAAQUIPAAAAAACPZ88tOSyD62NooqBbZ/hN; visid_incap_773285=pfQdz3B9TZuve/PhmXgPnF3G/VsAAAAAQUIPAAAAAAC7uOLsspgsQRNnV9pm9LiA; fbm_166209726726710=base_domain=.etoro.com; liveagent_oref=; liveagent_ptid=b60ed7d3-3047-4752-86d1-14ba900fd3c4; _DCMN_id.90.13db=2a8b42dd8726053f.1543357972.4.1543872510.1543768287.; TMIS2=${credentialsService.getCredentials().tmis2}; _gat=1;")
                .POST(HttpRequest.BodyPublishers.ofString("{\"Password\":\"${pwd}\",\"UserLoginIdentifier\":\"${username}\",\"Username\":\"${username}\",\"rememberMe\":true,\"RequestedScopes\":[]}"))
                .build()
        val response = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        println(response)
        accessToken = JSONObject(response).getString("accessToken").toString()
    }

    private fun exchange() {
        val req = prepareRequest("api/sts/v2/oauth/exchange?client_request_id=${requestId}", accessToken, TradingMode.DEMO, credentialsService.getCredentials())
                .POST(HttpRequest.BodyPublishers.ofString("{\"RequestedScopes\":[]}"))
                .build()
        val response = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        exchangeToken = JSONObject(response).getString("accessToken").toString()
    }

    fun getAccountData(mode: TradingMode) {
        val req = prepareRequest("api/logininfo/v1.1/logindata?" +
                "client_request_id=${requestId}&conditionIncludeDisplayableInstruments=false&conditionIncludeMarkets=false&conditionIncludeMetadata=false&conditionIncludeMirrorValidation=false",
                exchangeToken, mode, credentialsService.getCredentials())
                .GET()
                .build()
        val response = JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body())
                .getJSONObject("AggregatedResult")
                .getJSONObject("ApiResponses")
        userdata = response.getJSONObject("CurrentUserData").getJSONObject("Content").getJSONArray("users").getJSONObject(0)
        realgcid = userdata.getInt("realCID").toString()
        demogcid = userdata.getInt("demoCID").toString()
    }
}
