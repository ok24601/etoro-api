package ok.work.etoroapi.transactions

import ok.work.etoroapi.client.EtoroPosition
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*


data class EtoroTransactionResponse(val Id: String, val Durable: Boolean?, val Type: String?, val Content: String?, val Aggregated: Boolean?)

class Transaction(val RequestToken: String, val Position: EtoroPosition?, var date: LocalDateTime?)

@Component
class TransactionPool {

    private var transactionsPool: MutableMap<String, Transaction> = mutableMapOf()

    fun addToPool(transaction: Transaction) {
        transactionsPool[transaction.RequestToken] = transaction
    }

    fun getFromPool(id: String): Transaction? {
        //todo poling
        val maxTimeout = 10000
        for (i in 200 until maxTimeout step 50) {
            Thread.sleep(200)
            if (transactionsPool[id] != null) {
                break
            }
        }
        return transactionsPool[id]

    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    fun removeOldTransactions() {
        val toRemove = transactionsPool.filter { p ->
            p.value.date?.isBefore(LocalDateTime.now().minusHours(1)) ?: false
        }
        toRemove.forEach { p -> transactionsPool.remove(p.key) }
    }
}


