package ok.work.etoroapi.client.websocket

import com.lightstreamer.client.LightstreamerClient
import com.lightstreamer.client.Subscription
import ok.work.etoroapi.client.AuthorizationContext
import ok.work.etoroapi.client.cookies.EtoroMetadataService
import ok.work.etoroapi.client.websocket.listeners.EtoroPositionListener
import ok.work.etoroapi.client.websocket.listeners.EtoroPriceListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

val subscriptionFields = arrayOf( "InstrumentID", "Ask", "Bid", "IsMarketOpen",
       "ConversionRateBid", "ConversionRateAsk",
        "AllowBuy", "AllowSell",
        "LastExecution",
        "OfficialClosingPrice", "PriceRateID", "UnitMarginAsk", "UnitMarginBid",
        "MaxPositionUnits", "IsInstrumentActive", "AskDiscounted", "BidDiscounted","UnitMarginAskDiscounted", "UnitMarginBidDiscounted")

@Component
class EtoroLightStreamerClient {

    lateinit var client: LightstreamerClient

    lateinit var realClient: LightstreamerClient

    @Autowired
    lateinit var priceListener: EtoroPriceListener

    @Autowired
    lateinit var positionListener: EtoroPositionListener

    @Autowired
    lateinit var authorizationContext: AuthorizationContext

    @Autowired
    private lateinit var credentialsService: EtoroMetadataService

    @PostConstruct
    fun init() {
        client = LightstreamerClient("https://push-demo-lightstreamer.cloud.etoro.com", "PROXY_PUSH")
        client.connectionDetails.user = authorizationContext.exchangeToken
        client.connectionDetails.setPassword(credentialsService.getMetadata().lsPassword)
        client.connectionOptions.connectTimeout = "10000"
        client.connect()

        realClient = LightstreamerClient("https://push-lightstreamer.cloud.etoro.com", "PROXY_PUSH")
        realClient.connectionDetails.user = authorizationContext.exchangeToken
        realClient.connectionDetails.setPassword(credentialsService.getMetadata().lsPassword)
        realClient.connectionOptions.connectTimeout = "10000"
        realClient.connect()

        val positionsSubDemo = Subscription("DISTINCT", arrayOf("@democid:${authorizationContext.demogcid}/"), arrayOf("message"))
        positionsSubDemo.addListener(positionListener)
        positionsSubDemo.requestedSnapshot = "no"
        client.subscribe(positionsSubDemo)

        val positionsSubReal = Subscription("DISTINCT", arrayOf("@realcid:${authorizationContext.realgcid}/"), arrayOf("message"))
        positionsSubReal.addListener(positionListener)
        positionsSubReal.requestedSnapshot = "no"
        realClient.subscribe(positionsSubReal)

        println("connected")
    }

    fun subscribeById(id: String) {
        val sub = Subscription("MERGE", arrayOf("instrument:${id}"), subscriptionFields)
        sub.requestedSnapshot = "yes"
        sub.addListener(priceListener)
        client.subscribe(sub)
        println(subscriptionFields.joinToString(" | "))
    }

    fun subscribeByIds(idList: List<String>) {
        val idArray = idList.map { id -> "instrument:$id" }.toTypedArray()
        val sub = Subscription("MERGE", idArray, subscriptionFields)
        sub.requestedSnapshot = "yes"
        sub.addListener(priceListener)
        client.subscribe(sub)
        println(subscriptionFields.joinToString(" | "))
    }

}
