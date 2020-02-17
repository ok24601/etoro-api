package ok.work.etoroapi.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ok.work.etoroapi.model.Position
import ok.work.etoroapi.model.PositionType
import ok.work.etoroapi.model.TradingMode
import ok.work.etoroapi.watchlist.EtoroAsset
import ok.work.etoroapi.watchlist.Watchlist
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

data class ViewContext(val ClientViewRate: Double)

data class EtoroPosition(val PositionID: String?, val InstrumentID: String, val IsBuy: Boolean, val Leverage: Int,
                         val StopLossRate: Double, val TakeProfitRate: Int, val IsTslEnabled: Boolean,
                         val View_MaxPositionUnits: Int, val View_Units: Double, val View_openByUnits: Boolean?,
                         val IsDiscounted: Boolean, val Amount: Int, val ViewRateContext: ViewContext?, val OpenDateTime: String?)

@Component
class EtoroHttpClient {

    @Autowired
    lateinit var authorizationContext: AuthorizationContext

    @Autowired
    lateinit var watchlist: Watchlist

    private val client = HttpClient.newHttpClient()

    fun getPositions(mode: TradingMode): List<EtoroPosition> {
        val req = prepareRequest("api/logininfo/v1.1/logindata?" +
                "client_request_id=${authorizationContext.requestId}&conditionIncludeDisplayableInstruments=false&conditionIncludeMarkets=false&conditionIncludeMetadata=false&conditionIncludeMirrorValidation=false",
                authorizationContext.exchangeToken, mode)
                .GET()
                .build()
        val response = JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body())
                .getJSONObject("AggregatedResult")
                .getJSONObject("ApiResponses")
                .getJSONObject("PrivatePortfolio")
                .getJSONObject("Content")
                .getJSONObject("ClientPortfolio")
                .getJSONArray("Positions")
                .toString()
        val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
        return mapper.readValue(response)
    }

    fun getInstrumentIDs(): List<EtoroAsset> {
        val req = HttpRequest.newBuilder().uri(URI("https://api.etorostatic.com/sapi/instrumentsmetadata/V1.1/instruments?cv=1c85198476a3b802326706d0c583e99b_beb3f4faa55c3a46ed44fc6d763db563"))
                .GET()
                .build()
        val response = JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body()).get("InstrumentDisplayDatas").toString()
        val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return mapper.readValue(response)
    }

    fun openPosition(position: Position, mode: TradingMode): Position {
        val type = position.type.equals(PositionType.BUY)
        val price = watchlist.getPrice(position.id, position.type)
        val positionRequestBody = EtoroPosition(null ,position.id, type, position.leverage, 0.01, 0, false, 50,
                0.01, false, false, position.amount, ViewContext(price), null)
        val req = prepareRequest("sapi/trade-${mode.name.toLowerCase()}/positions?client_request_id=${authorizationContext.requestId}", authorizationContext.exchangeToken, mode)
                .POST(HttpRequest.BodyPublishers.ofString(JSONObject(positionRequestBody).toString()))
                .build()
        val code = client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode()
        if (code != 200) {
            throw RuntimeException("Failed open position $position")
        }
        return position
    }

    fun closePosition(id: String, mode: TradingMode) {
        val req = prepareRequest("sapi/trade-${mode.name.toLowerCase()}/positions/$id?PositionID=$id&client_request_id=${authorizationContext.requestId}",
                authorizationContext.exchangeToken, mode)
                .DELETE()
                .build()
        val code = client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode()
        if (code != 200) {
            throw RuntimeException("Failed close positionID $id")
        }
    }

}
