package ok.work.etoroapi.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ok.work.etoroapi.conf.Asset
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
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

    fun getInstrumentIDs(): List<Asset> {
        val req = HttpRequest.newBuilder().uri(URI("https://api.etorostatic.com/sapi/instrumentsmetadata/V1.1/instruments?cv=1c85198476a3b802326706d0c583e99b_beb3f4faa55c3a46ed44fc6d763db563"))
                .GET()
                .build()
        val response = JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body()).get("InstrumentDisplayDatas").toString()
        val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return mapper.readValue(response)
    }

}
