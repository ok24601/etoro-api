package ok.work.etoroapi.client.websocket

import com.lightstreamer.client.ItemUpdate
import com.lightstreamer.client.Subscription
import com.lightstreamer.client.SubscriptionListener

class EtoroClientListener : SubscriptionListener {
    override fun onListenEnd(subscription: Subscription) {
        println("onListenEnd")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemUpdate(itemUpdate: ItemUpdate) {
        println("${itemUpdate.itemName} buy:${itemUpdate.getValue(3)} sell:${itemUpdate.getValue(4)}")
    }

    override fun onSubscription() {
        println("subscribed")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEndOfSnapshot(itemName: String?, itemPos: Int) {
        println("eos ${itemName}")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemLostUpdates(itemName: String?, itemPos: Int, lostUpdates: Int) {
        println("onItemLostUpdates ${itemName}")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSubscriptionError(code: Int, message: String?) {
        println("onSubscriptionError ${message}")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClearSnapshot(itemName: String?, itemPos: Int) {
        println("onClearSnapshot")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCommandSecondLevelSubscriptionError(code: Int, message: String?, key: String?) {
        println("onCommandSecondLevelSubscriptionError ${code} ${message}")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUnsubscription() {
        println("onUnsubscription")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCommandSecondLevelItemLostUpdates(lostUpdates: Int, key: String) {
        println("onCommandSecondLevelItemLostUpdates ${lostUpdates}")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListenStart(subscription: Subscription) {
        println("start listening")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    override fun onRealMaxFrequency(frequency: String?) {
//        println("max freq")
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

}
