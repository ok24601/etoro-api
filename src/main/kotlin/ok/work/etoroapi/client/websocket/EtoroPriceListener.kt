package ok.work.etoroapi.client.websocket

import com.lightstreamer.client.ItemUpdate
import com.lightstreamer.client.Subscription
import ok.work.etoroapi.watchlist.Watchlist
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EtoroPriceListener : EtoroListener() {

    @Autowired
    lateinit var watchlist: Watchlist

    override fun onListenEnd(subscription: Subscription) {
        println("onListenEnd")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemUpdate(itemUpdate: ItemUpdate) {
        watchlist.updatePrice(itemUpdate.itemName.replace("instrument:", ""), itemUpdate.getValue(2), itemUpdate.getValue(3))
        val log = StringBuilder()
        for (i in 1..subscriptionFields.size) {
            log.append("${itemUpdate.getValue(i)} | ")
        }
        println(log.toString())
    }


}
