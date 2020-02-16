package ok.work.etoroapi.client.websocket

import com.lightstreamer.client.ItemUpdate
import com.lightstreamer.client.Subscription
import com.lightstreamer.client.SubscriptionListener
import ok.work.etoroapi.watchlist.Watchlist
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EtoroClientListener : SubscriptionListener {

    @Autowired
    lateinit var watchlist: Watchlist

    override fun onListenEnd(subscription: Subscription) {
        println("onListenEnd")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemUpdate(itemUpdate: ItemUpdate) {
        watchlist.updatePrice(itemUpdate.itemName.replace("instrument:", ""), itemUpdate.getValue(3), itemUpdate.getValue(4))
        println("${itemUpdate.itemName} buy:${itemUpdate.getValue(3)} sell:${itemUpdate.getValue(4)}")
    }

    override fun onSubscription() {
        println("subscribed")
    }

    override fun onEndOfSnapshot(itemName: String?, itemPos: Int) {
        println("eos $itemName")
    }

    override fun onItemLostUpdates(itemName: String?, itemPos: Int, lostUpdates: Int) {
        println("onItemLostUpdates $itemName")
    }

    override fun onSubscriptionError(code: Int, message: String?) {
        println("onSubscriptionError $message")
    }

    override fun onClearSnapshot(itemName: String?, itemPos: Int) {
        println("onClearSnapshot")
    }

    override fun onCommandSecondLevelSubscriptionError(code: Int, message: String?, key: String?) {
        println("onCommandSecondLevelSubscriptionError $code $message")
    }

    override fun onUnsubscription() {
        println("onUnsubscription")
    }

    override fun onCommandSecondLevelItemLostUpdates(lostUpdates: Int, key: String) {
        println("onCommandSecondLevelItemLostUpdates $lostUpdates")
    }

    override fun onListenStart(subscription: Subscription) {
        println("start listening")
    }

}
