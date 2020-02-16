package ok.work.etoroapi.client.websocket

import com.lightstreamer.client.LightstreamerClient
import com.lightstreamer.client.Subscription
import ok.work.etoroapi.client.AuthorizationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Component
class EtoroLightStreamerClient {

    lateinit var client: LightstreamerClient

    @Autowired
    lateinit var authorizationContext: AuthorizationContext

    @Autowired
    lateinit var listener: EtoroClientListener

    @PostConstruct
    fun init() {
        client = LightstreamerClient("https://push-lightstreamer.cloud.etoro.com", "PROXY_PUSH")
        client.connectionDetails.setPassword("""{"UserAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36","ApplicationVersion":"213.0.2","ApplicationName":"ReToro","AccountType":"Real","ApplicationIdentifier":"ReToro"}""")
        client.connectionDetails.user = authorizationContext.exchangeToken
        client.connectionOptions.connectTimeout = "10000"
        client.connect()

        println("connected")
    }

    fun subscribeById(id: String) {
        val sub = Subscription("MERGE", arrayOf("instrument:${id}"), arrayOf("IsInstrumentActive", "InstrumentID", "Ask", "Bid",
                "ConversionRateBid", "ConversionRateAsk", "AllowBuy", "AllowSell"))
        sub.requestedSnapshot = "yes"
        sub.addListener(listener)
        client.subscribe(sub)
    }

}
