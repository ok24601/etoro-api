package ok.work.etoroapi.client

import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.http.HttpClient
import java.net.http.HttpResponse

@Component
class EtoroHttpClient {

    @Autowired
    lateinit var authorizationContext: AuthorizationContext

    private val client = HttpClient.newHttpClient()

    fun getPortfolio() {
        val req = prepareRequest("api/logininfo/v1.1/logindata?client_request_id=${authorizationContext.requestId}&conditionIncludeDisplayableInstruments=false&conditionIncludeMarkets=false&conditionIncludeMetadata=false&conditionIncludeMirrorValidation=false",
                authorizationContext.exchangeToken)
                .GET()
                .build()
        val response = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        println(JSONObject(response).getString("ClientPortfolio"))
    }

    fun getPositions() {
        val req = prepareRequest("api/logininfo/v1.1/logindata?client_request_id=${authorizationContext.requestId}&conditionIncludeDisplayableInstruments=false&conditionIncludeMarkets=false&conditionIncludeMetadata=false&conditionIncludeMirrorValidation=false",
                authorizationContext.exchangeToken)
                .GET()
                .build()
        val response = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        println(JSONObject(response).getJSONArray("Positions"))
    }

}
