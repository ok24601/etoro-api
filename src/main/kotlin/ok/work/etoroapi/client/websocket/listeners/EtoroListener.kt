package ok.work.etoroapi.client.websocket.listeners

import com.lightstreamer.client.ItemUpdate
import com.lightstreamer.client.Subscription
import com.lightstreamer.client.SubscriptionListener

open class EtoroListener : SubscriptionListener {
    
    override fun onListenEnd(subscription: Subscription) {
        println("onListenEnd")
    }

    override fun onItemUpdate(itemUpdate: ItemUpdate) {
        println("updated")
    }

    override fun onSubscription() {
        println("subscribed")
    }

    override fun onEndOfSnapshot(itemName: String?, itemPos: Int) {
        println("eos $itemName $itemPos")
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
