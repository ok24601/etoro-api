package ok.work.etoroapi.client.websocket.listeners

import com.lightstreamer.client.ItemUpdate
import com.lightstreamer.client.Subscription
import ok.work.etoroapi.client.websocket.subscriptionFields
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
        val id = itemUpdate.itemName.replace("instrument:", "")

        if (watchlist.getById(id) !== null ) {
            watchlist.updatePrice(id, itemUpdate.getValue(2), itemUpdate.getValue(3))
            watchlist.updateMarketStatus(id, itemUpdate.getValue(4)!!.toBoolean())
            watchlist.updateDiscounted(id, itemUpdate.getValue(16)!!.toDouble(), itemUpdate.getValue(17)!!.toDouble())

            val log = StringBuilder()
            for (i in 1..subscriptionFields.size) {
                log.append("${itemUpdate.getValue(i)} | ")
            }
            println(log.toString())
        }
    }


}
