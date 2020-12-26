package ok.work.etoroapi.client

import ok.work.etoroapi.client.cookies.EtoroMetadataService
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
    private lateinit var metadataService: EtoroMetadataService

    @PostConstruct
    fun setupAuthorizationContext() {
        requestId = UUID.randomUUID().toString().toLowerCase()

        //only for dev
        val token = System.getenv("TOKEN")

        if (token != null) {
            exchangeToken = token
        } else {
            val tokenServer = System.getenv("TOKEN_SERVER")
            if (tokenServer != null) {
                auth_by_token_server(tokenServer, System.getenv("LOGIN"), System.getenv("PASSWORD"))
            } else {
                auth(System.getenv("LOGIN"), System.getenv("PASSWORD"))
                exchange()
            }
        }
        getAccountData(TradingMode.REAL)
    }
    private fun auth_by_token_server(tokenServer: String, username: String, pwd: String) {
        val req = HttpRequest.newBuilder().uri(URI("http://${tokenServer}/auth"))
                .header("content-type", "application/json;charset=UTF-8")
                .header("accept", "application/json, text/plain, */*")
                .POST(HttpRequest.BodyPublishers.ofString("{\"password\":\"${pwd}\",\"username\":\"${username}\"}"))
                .build()
        println(req)
        val response = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        println("==========================")
        println(response)
        println("==========================")
        exchangeToken = JSONObject(response).getString("token").toString()
    }
    private fun auth(username: String, pwd: String) {
        val req = HttpRequest.newBuilder().uri(URI("${metadataService.baseUrl}/api/sts/v2/oauth/auth?client_request_id=${requestId}"))
                .header("authority", metadataService.domain)
                .header("accounttype", "Demo")
                .header("x-sts-appdomain", metadataService.baseUrl)
                .header("content-type", "application/json;charset=UTF-8")
                .header("accept", "application/json, text/plain, */*")
                .header("x-sts-gatewayappid", "90631448-9A01-4860-9FA5-B4EBCDE5EA1D")
//                .header("x-sts-clienttime", "2020-12-24T09:19:14")
                .header("x-device-id", "699914f6-e983-4c26-bb86-41eaaf14de09")
                .header("applicationidentifier", "ReToro")
                .header("applicationversion", "288.0.1")
                .header("sec-fetch-site", "same-origin")
                .header("sec-fetch-mode", "cors")
                .header("referer", "${metadataService.baseUrl}/login")
                .header("cookie", metadataService.getMetadata().cookies)
               // .header("cookie", "etoroHPRedirect=1; _ga=GA1.2.1096383890.1543357062; visid_incap_172517=ZNWYjpoOTt6IOfHx3HN5VxPG/VsAAAAAQUIPAAAAAACPZ88tOSyD62NooqBbZ/hN; visid_incap_773285=pfQdz3B9TZuve/PhmXgPnF3G/VsAAAAAQUIPAAAAAAC7uOLsspgsQRNnV9pm9LiA; fbm_166209726726710=base_domain=.etoro.com; liveagent_oref=; liveagent_ptid=b60ed7d3-3047-4752-86d1-14ba900fd3c4; _DCMN_id.90.13db=2a8b42dd8726053f.1543357972.4.1543872510.1543768287.; TMIS2=${credentialsService.getCredentials().tmis2}; _gat=1;")
                .POST(HttpRequest.BodyPublishers.ofString("{\"Password\":\"${pwd}\",\"UserLoginIdentifier\":\"${username}\",\"Username\":\"${username}\",\"rememberMe\":true,\"RequestedScopes\":[]}"))
                .build()
        println(req)
        val response = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        println("==========================")
        println(response)
        println("==========================")
        accessToken = JSONObject(response).getString("accessToken").toString()
    }

    private fun exchange() {
        val req = prepareRequest("api/sts/v2/oauth/exchange?client_request_id=${requestId}", accessToken, TradingMode.DEMO, metadataService.getMetadata())
                .POST(HttpRequest.BodyPublishers.ofString("{\"RequestedScopes\":[]}"))
                .build()
        val response = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        exchangeToken = JSONObject(response).getString("accessToken").toString()
    }

    fun getAccountData(mode: TradingMode) {
        val req = prepareRequest("api/logininfo/v1.1/logindata?" +
                "client_request_id=${requestId}&conditionIncludeDisplayableInstruments=false&conditionIncludeMarkets=false&conditionIncludeMetadata=false&conditionIncludeMirrorValidation=false",
                exchangeToken, mode, metadataService.getMetadata())
                .GET()
                .build()
        val response = JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body())
                .getJSONObject("AggregatedResult")
                .getJSONObject("ApiResponses")
        println(response)
        userdata = response.getJSONObject("CurrentUserData").getJSONObject("Content").getJSONArray("users").getJSONObject(0)
        realgcid = userdata.getInt("realCID").toString()
        demogcid = userdata.getInt("demoCID").toString()
    }
}
