package ok.work.etoroapi.client.websocket

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightstreamer.client.ItemUpdate
import ok.work.etoroapi.client.EtoroPosition
import ok.work.etoroapi.transactions.Transaction
import ok.work.etoroapi.transactions.TransactionPool
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Scope("prototype")
class EtoroPositionListener : EtoroListener() {

    @Autowired
    lateinit var transactionPool: TransactionPool

    val mapper = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false).configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false).configure(DeserializationFeature.EAGER_DESERIALIZER_FETCH, true)


    override fun onItemUpdate(itemUpdate: ItemUpdate) {
        println(itemUpdate.getValue(1))
        val transactionJson =  JSONObject(JSONObject(itemUpdate.getValue(1)).getString("Content"))
        val position: EtoroPosition = mapper.readValue(transactionJson.getJSONObject("Position").toString())
        val requestToken = transactionJson.getString("RequestToken");
        transactionPool.addToPool(Transaction(requestToken, position, LocalDateTime.now()));

    }
}
