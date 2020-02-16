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

    @PostConstruct
    fun init() {
        client = LightstreamerClient("https://push-demo-lightstreamer.cloud.etoro.com", "PROXY_PUSH")
        client.connectionDetails.setPassword("""{"UserAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36","ApplicationVersion":"213.0.2","ApplicationName":"ReToro","AccountType":"Demo","ApplicationIdentifier":"ReToro"}""")
        client.connectionDetails.user = authorizationContext.exchangeToken
        //  client.connectionDetails.setPassword("%7B%22UserAgent%22%3A%22Mozilla%2F5.0+%28Macintosh%3B+Intel+Mac+OS+X+10_14_0%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Chrome%2F79.0.3945.130+Safari%2F537.36%22%2C%22ApplicationVersion%22%3A%22213.0.2%22%2C%22ApplicationName%22%3A%22ReToro%22%2C%22AccountType%22%3A%22Demo%22%2C%22ApplicationIdentifier%22%3A%22ReToro%22%7D")
        client.connectionOptions.connectTimeout = "10000"
        client.connect()

        val sub = Subscription("MERGE", (100000..100001).map { i -> "instrument:${i}" }.toList().toTypedArray(), arrayOf("IsInstrumentActive", "InstrumentID", "Ask", "Bid",
                "ConversionRateBid", "ConversionRateAsk", "AllowBuy", "AllowSell"))

        sub.requestedSnapshot = "no"
        sub.addListener(EtoroClientListener())
        client.subscribe(sub)
        println("connected")
    }


}
