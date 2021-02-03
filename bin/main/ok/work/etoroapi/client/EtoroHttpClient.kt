package ok.work.etoroapi.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ok.work.etoroapi.client.browser.EtoroMetadataService
import ok.work.etoroapi.model.EtoroHistoryPosition
import ok.work.etoroapi.model.EtoroPositionEx
import ok.work.etoroapi.model.EtoroPositionTypeEx
import ok.work.etoroapi.model.TradingMode
import ok.work.etoroapi.transactions.Transaction
import ok.work.etoroapi.transactions.TransactionPool
import ok.work.etoroapi.watchlist.EtoroAsset
import ok.work.etoroapi.watchlist.Watchlist
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class ViewContext(val ClientViewRate: Double)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EtoroPosition(val PositionID: String?, val InstrumentID: String, val IsBuy: Boolean, val Leverage: Int,
                        val StopLossRate: Double, val TakeProfitRate: Double, val IsTslEnabled: Boolean,
                        val View_MaxPositionUnits: Int, val View_Units: Double, val View_openByUnits: Boolean?,
                        val Amount: Double, val ViewRateContext: ViewContext?, val OpenDateTime: String?, val IsDiscounted: Boolean?, val OpenRate: Double?, val CloseRate: Double?, val NetProfit: Double?, val CloseDateTime: String?)

data class EtoroPositionForOpen(val PositionID: String?, val InstrumentID: String, val IsBuy: Boolean, val Leverage: Int,
                         val StopLossRate: Double, val TakeProfitRate: Double, val IsTslEnabled: Boolean,
                         val View_MaxPositionUnits: Int, val View_Units: Double, val View_openByUnits: Boolean?,
                         val Amount: Double, val ViewRateContext: ViewContext?, val OpenDateTime: String?, val IsDiscounted: Boolean?)

data class EtoroOrder(var OrderID: String?, var InstrumentID: String, var IsBuy: Boolean, var Leverage: Int,
                      var IsDiscounted: Boolean?, var IsTslEnabled: Boolean, var Rate: Double, var Amount: Int, var StopLossRate: Double,
                      var TakeProfitRate: Double, var View_MaxPositionUnits: Int, var View_Units: Double, var View_openByUnits: Boolean?)


@JsonIgnoreProperties(ignoreUnknown = true)
data class EtoroPositionForUpdate(val PositionID: String, val StopLossRate: Double?, val TakeProfitRate: Double?, val IsTslEnabled: Boolean?)

data class AssetInfoRequest(val instrumentIds: Array<String>)

data class AssetInfo(val InstrumentID: Int, val AllowDiscountedRates: Boolean)

data class AssetInfoResponse(val Instruments: Array<AssetInfo>)

@Component
class EtoroHttpClient {

    @Autowired
    private lateinit var userContext: UserContext

    @Autowired
    private lateinit var watchlist: Watchlist

    @Autowired
    private lateinit var transactionPool: TransactionPool

    @Autowired
    private lateinit var metadataService: EtoroMetadataService

    private val client = HttpClient.newHttpClient()

    var okHttpClient = OkHttpClient()


    fun getUserHistory(mode: TradingMode, periodInDays: Long): List<EtoroHistoryPosition> {
        val startDate = LocalDateTime.now().minusDays(periodInDays).format(DateTimeFormatter.ISO_DATE)


        val req = prepareRequest("sapi/trade-data-${mode.name.toLowerCase()}/history/private/credit/flat?ItemsPerPage=10000000&PageNumber=1&StartTime=${startDate}T00:00:00.000Z&client_request_id=${userContext.requestId}",
                userContext.exchangeToken, mode, metadataService.getMetadata()).GET().build()

        val response = JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body()).getJSONArray("HistoryPositions").toString()

        val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)

        return mapper.readValue(response)
    }

    constructor(watchlist: Watchlist, metadataService: EtoroMetadataService, userContext: UserContext, transactionPool: TransactionPool) {
        this.watchlist = watchlist
        this.metadataService = metadataService;
        this.userContext = userContext;
        this.transactionPool = transactionPool;
        this.watchlist.etoroClient = this
        this.watchlist.init()
    }

    fun getPositions(mode: TradingMode): List<EtoroPosition> {
        val req = prepareRequest("api/logininfo/v1.1/logindata?" +
                "client_request_id=${userContext.requestId}&conditionIncludeDisplayableInstruments=false&conditionIncludeMarkets=false&conditionIncludeMetadata=false&conditionIncludeMirrorValidation=false",
                userContext.exchangeToken, mode, metadataService.getMetadata())
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

        val positions : List<EtoroPosition> = mapper.readValue(response)
        return positions.map {
            val instrumentId = it.InstrumentID
            val assetInfo = getAssetInfo(instrumentId, mode)
            if(watchlist.getById(instrumentId) != null) {
                if (it.IsBuy) {
                    val price = watchlist.getPrice(instrumentId, EtoroPositionTypeEx.SELL, assetInfo.getBoolean("AllowDiscountedRates"))
                    it.copy(NetProfit = (price - it.OpenRate!!) * it.Leverage * it.Amount / it.OpenRate)
                } else {
                    val price = watchlist.getPrice(instrumentId, EtoroPositionTypeEx.BUY, assetInfo.getBoolean("AllowDiscountedRates"))
                    it.copy(NetProfit = (it.OpenRate!! - price) * it.Leverage * it.Amount / it.OpenRate)
                }
            } else {
                it
            }
        }
    }

    fun getHistoryPositions(limit: String = "100", page: String = "1", StartTime: String = "", mode: TradingMode): List<EtoroPosition> {
        val req = prepareRequest("sapi/trade-data-${mode.name.toLowerCase()}/history/private/credit/flat?ItemsPerPage=$limit&PageNumber=$page&StartTime=$StartTime",
                userContext.exchangeToken, mode, metadataService.getMetadata())
                .GET()
                .build()

        val response = JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body())
                .getJSONArray("HistoryPositions")
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
    fun openOrder(order: EtoroOrder, mode: TradingMode) {
        var orderType = EtoroPositionTypeEx.SELL;

        if (order.IsBuy) {
            orderType = EtoroPositionTypeEx.BUY;
        }
        val instrumentId = order.InstrumentID
        val assetInfo = getAssetInfo(instrumentId, mode)
        val price = watchlist.getPrice(instrumentId, orderType, assetInfo.getBoolean("AllowDiscountedRates"))
        val leverages = assetInfo.getJSONArray("Leverages")
        val minOrderAmount = assetInfo.getInt("MinPositionAmount")
        val minOrderAmountAbsolute = assetInfo.getInt("MinPositionAmountAbsolute")

        if (!leverages.contains(order.Leverage)) {
            throw RuntimeException("x${order.Leverage} is not permitted. You can use $leverages")
        }
        if ((order.Leverage > 1 && minOrderAmount > order.Leverage * order.Amount) || order.Amount < minOrderAmountAbsolute) {
            throw RuntimeException(order.InstrumentID + ": You cannot open less than minimum position amount $$minOrderAmount, and minimum absolute amount $$minOrderAmountAbsolute with actual position amount $${order.Amount}")
        }

        if (order.StopLossRate == 0.0) {
            order.StopLossRate = if (!order.IsBuy) price + price * 0.45 / order.Leverage else price - price * 0.45 / order.Leverage
        }

        if (order.TakeProfitRate == 0.0) {
            order.TakeProfitRate = if (!order.IsBuy) Math.max(0.1, price - price * 9.5 / order.Leverage) else price + price * 9.5 / order.Leverage
        }

        val orderRequestBody = EtoroOrder(null, instrumentId, order.IsBuy, order.Leverage, assetInfo.getBoolean("AllowDiscountedRates"), order.IsTslEnabled, order.Rate, order.Amount, order.StopLossRate, order.TakeProfitRate,
                assetInfo.getInt("MaxPositionUnits"), 0.01, false)
        val req = prepareRequest("sapi/trade-${mode.name.toLowerCase()}/orders?client_request_id=${userContext.requestId}", userContext.exchangeToken, mode, metadataService.getMetadata())
                .POST(HttpRequest.BodyPublishers.ofString(JSONObject(orderRequestBody).toString()))
                .build()
        val res = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        val transactionId = JSONObject(res).getString("Token")
    }
    fun openPosition(position: EtoroPositionEx, mode: TradingMode): Transaction {
        val type = position.type.equals(EtoroPositionTypeEx.BUY)
        val instrumentId = position.instrumentId ?: watchlist.getInstrumentIdByName(position.name ?: "")
        val assetInfo = getAssetInfo(instrumentId, mode)
        val price = watchlist.getPrice(instrumentId, position.type, assetInfo.getBoolean("AllowDiscountedRates"))
        val leverages = assetInfo.getJSONArray("Leverages")
        val minPositionAmount = assetInfo.getInt("MinPositionAmount")
        val minPositionAmountAbsolute = assetInfo.getInt("MinPositionAmountAbsolute")

/*        if (watchlist.isMarketOpen(instrumentId)) {*/
            if (!leverages.contains(position.leverage)) {
                throw RuntimeException("x${position.leverage} is not permitted. You can use $leverages")
            }
            if (minPositionAmount > position.leverage * position.amount || position.amount < minPositionAmountAbsolute) {
                throw RuntimeException("You cannot open less than minimum position amount $$minPositionAmount, and minimum absolute amount $$minPositionAmountAbsolute")
            }


        if (position.type == EtoroPositionTypeEx.SELL) {
            when {
                position.stopLossAmountRate > 0.0 -> position.stopLossRate = price + (price * position.stopLossAmountRate / 100) / position.leverage
                position.stopLossRate > 0.0 -> position.stopLossRate = price + (price * position.stopLossRate / 100)
                position.stopLoss > 0.0 -> position.stopLossRate = position.stopLoss
                else -> {
                    val maxSL = assetInfo.getInt("MaxStopLossPercentage")
                    position.stopLossRate = price + (price * maxSL / 100)
                }
            }

            when {
                position.takeProfitAmountRate > 0.0 -> position.takeProfitRate = price - (price * position.takeProfitAmountRate / 100) / position.leverage
                position.takeProfitRate > 0.0 -> position.takeProfitRate = price - (price * position.takeProfitRate / 100)
                position.takeProfit > 0.0 -> position.takeProfitRate = position.takeProfit
                else -> {
                    position.takeProfitRate = price - (price * 50 / 100)
                }
            }
        } else if (position.type == EtoroPositionTypeEx.BUY){
            when {
                position.stopLossAmountRate > 0.0 ->  position.stopLossRate = price - (price * position.stopLossAmountRate / 100) / position.leverage
                position.stopLossRate > 0.0 -> position.stopLossRate = price - (price * position.stopLossRate / 100)
                position.stopLoss > 0.0 -> position.stopLossRate = position.stopLoss
                else -> {
                    val maxSL = assetInfo.getInt("MaxStopLossPercentage")
                    position.stopLossRate = price - (price * maxSL / 100)
                }
            }
            when {
                position.takeProfitAmountRate > 0.0 -> position.takeProfitRate = price + (price * position.takeProfitAmountRate / 100) / position.leverage
                position.takeProfitRate > 0.0 -> position.takeProfitRate = price + (price * position.takeProfitRate / 100)
                position.takeProfit > 0.0 -> position.takeProfitRate = position.takeProfit
                else -> {
                    position.takeProfitRate = price + (price * 50 / 100)
                }
            }

        }
        position.takeProfitRate = position.takeProfitRate.round(assetInfo.getInt("Precision"))
        position.stopLossRate = position.stopLossRate.round(assetInfo.getInt("Precision"))

        val positionRequestBody = EtoroPositionForOpen(null, instrumentId, type, position.leverage, position.stopLossRate, position.takeProfitRate, position.tsl, assetInfo.getInt("MaxPositionUnits"),
                    0.01, false,  position.amount, ViewContext(price), null, assetInfo.getBoolean("AllowDiscountedRates"))

            val req = prepareRequest("sapi/trade-${mode.name.toLowerCase()}/positions?client_request_id=${userContext.requestId}", userContext.exchangeToken, mode, metadataService.getMetadata())
                    .POST(HttpRequest.BodyPublishers.ofString(JSONObject(positionRequestBody).toString()))
                    .build()
            val body = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
            // println(JSONObject(positionRequestBody).toString())
            // println(body)
            val transactionId = JSONObject(body).getString("Token")
            return transactionPool.getFromPool(transactionId) ?: Transaction(transactionId, null, null, null, null)
/*        }*/
/*        throw RuntimeException("Market ${position.instrumentId} is closed.")*/

    }
    fun updatePosition(positionRequestBody: EtoroPositionForUpdate, mode: TradingMode): Transaction {
//        val req = prepareOkRequest("sapi/trade-${mode.name.toLowerCase()}/positions/$id?PositionID=$id&client_request_id=${authorizationContext.requestId}",  authorizationContext.exchangeToken, mode, metadataService.getMetadata())
//        req.put( RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), "{}"))
        val id = positionRequestBody.PositionID
        val requestBody =  JSONObject(positionRequestBody)

        val req = prepareRequest("sapi/trade-${mode.name.toLowerCase()}/positions/$id?client_request_id=${userContext.requestId}", userContext.exchangeToken, mode, metadataService.getMetadata())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build()

        val body = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        val transactionId = JSONObject(body).getString("Token")
        return transactionPool.getFromPool(transactionId) ?: Transaction(transactionId, null, null, null, null)
    }
    fun deletePosition(id: String, mode: TradingMode) {
        val req = prepareOkRequest("sapi/trade-${mode.name.toLowerCase()}/positions/$id?PositionID=$id&client_request_id=${userContext.requestId}", userContext.exchangeToken, mode, metadataService.getMetadata())
        req.delete("{}".toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()))

        val code = okHttpClient.newCall(req.build()).execute().code

        if (code != 200) {
            throw RuntimeException("Failed close positionID $id")
        }
    }

    fun getAssetInfo(id: String, mode: TradingMode): JSONObject {
        val body = AssetInfoRequest(arrayOf(id))
        val req = prepareRequest("sapi/trade-real/instruments/private/index?client_request_id=${userContext.requestId}",
                userContext.exchangeToken, mode, metadataService.getMetadata())
                .POST(HttpRequest.BodyPublishers.ofString(JSONObject(body).toString()))
                .build()

        return JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body()).getJSONArray("Instruments").getJSONObject(0)
    }

    fun getCash(mode: TradingMode): Double {
        val req = prepareRequest("api/logininfo/v1.1/logindata?" +
                "client_request_id=${userContext.requestId}&conditionIncludeDisplayableInstruments=false&conditionIncludeMarkets=false&conditionIncludeMetadata=false&conditionIncludeMirrorValidation=false",
                userContext.exchangeToken, mode, metadataService.getMetadata())
                .GET()
                .build()

        return JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body())
                .getJSONObject("AggregatedResult")
                .getJSONObject("ApiResponses")
                .getJSONObject("PrivatePortfolio")
                .getJSONObject("Content")
                .getJSONObject("ClientPortfolio")
                .getDouble("Credit")
    }
    fun getOrders(mode: TradingMode): List<EtoroOrder> {
        val req = prepareRequest("api/logininfo/v1.1/logindata?" +
                "client_request_id=${userContext.requestId}&conditionIncludeDisplayableInstruments=false&conditionIncludeMarkets=false&conditionIncludeMetadata=false&conditionIncludeMirrorValidation=false",
                userContext.exchangeToken, mode, metadataService.getMetadata())
                .GET()
                .build()

        val response = JSONObject(client.send(req, HttpResponse.BodyHandlers.ofString()).body())
                .getJSONObject("AggregatedResult")
                .getJSONObject("ApiResponses")
                .getJSONObject("PrivatePortfolio")
                .getJSONObject("Content")
                .getJSONObject("ClientPortfolio")
                .getJSONArray("Orders")
                .toString()

        val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
        return mapper.readValue(response)
    }
    fun updateStopLossRate(instrumentId: String, positionId: String, stopLossRate: Double, isTslEnabled: Boolean, mode: TradingMode) {
        val stopLossString = DecimalFormat("#.##").format(stopLossRate).replace(",", ".");
        val sendObject = JSONObject()
        sendObject.put("PositionID", positionId)
        sendObject.put("StopLossRate", stopLossString)
        sendObject.put("IsTslEnabled", isTslEnabled)

        val req = prepareRequest("sapi/trade-${mode.name.toLowerCase()}/positions/${positionId}?client_request_id=${userContext.requestId}", userContext.exchangeToken, mode, metadataService.getMetadata())
                .PUT(HttpRequest.BodyPublishers.ofString(sendObject.toString()))
                .build()
        val res = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        val transactionId = JSONObject(res).getString("Token")

    }
    fun updateTakeProfit(instrumentId: String, positionId: String, takeProfitRate: Double, mode: TradingMode) {
        val takeProfitString = DecimalFormat("#.##").format(takeProfitRate).replace(",", ".");
        val sendObject = JSONObject()
        sendObject.put("PositionID", positionId)
        sendObject.put("TakeProfitRate", takeProfitString)

        val req = prepareRequest("sapi/trade-${mode.name.toLowerCase()}/positions/${positionId}?client_request_id=${userContext.requestId}", userContext.exchangeToken, mode, metadataService.getMetadata())
                .PUT(HttpRequest.BodyPublishers.ofString(sendObject.toString()))
                .build()
        val res = client.send(req, HttpResponse.BodyHandlers.ofString()).body()
        val transactionId = JSONObject(res).getString("Token")
    }
    fun deleteOrder(id: String, mode: TradingMode) {
        val req = prepareOkRequest("sapi/trade-${mode.name.toLowerCase()}/orders/$id?PositionID=$id&client_request_id=${userContext.requestId}",
                userContext.exchangeToken, mode, metadataService.getMetadata())
        req.delete(RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), "{}"))

        val code = okHttpClient.newCall(req.build()).execute().code

        if (code != 200) {
            throw RuntimeException("Failed close order $id")
        }
    }
}
