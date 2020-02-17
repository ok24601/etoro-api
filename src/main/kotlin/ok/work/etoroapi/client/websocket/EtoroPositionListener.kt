package ok.work.etoroapi.client.websocket

import com.lightstreamer.client.ItemUpdate
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class EtoroPositionListener : EtoroListener() {

    override fun onItemUpdate(itemUpdate: ItemUpdate) {
        println("${itemUpdate.getValue(1)} ")
    }
}
