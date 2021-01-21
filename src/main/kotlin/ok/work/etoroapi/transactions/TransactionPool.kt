package ok.work.etoroapi.transactions

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ok.work.etoroapi.client.EtoroPosition
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime


data class EtoroTransactionResponse(val Id: String, val Durable: Boolean?, val Type: String?, val Content: String?, val Aggregated: Boolean?)

data class Transaction(val RequestToken: String, val Position: EtoroPosition?, val ErrorMessageCode: Int?, val NotificationParams: Map<String, String>?, var date: LocalDateTime?)

class TransactionError(val code: Int, val details: Map<String, String>?) : RuntimeException("Code: $code, details: $details")

@Component
class TransactionPool {

    private var transactionsPool: MutableMap<String, Transaction> = mutableMapOf()

    fun addToPool(transaction: Transaction) {
        transactionsPool[transaction.RequestToken] = transaction
    }

    fun getFromPool(id: String): Transaction? {
        //todo poling
        val maxTimeout = 4000
        for (i in 200 until maxTimeout step 50) {
            Thread.sleep(200)
            val transaction = transactionsPool[id]
            if (transaction != null) {
                if (transaction.ErrorMessageCode != 0) {
                    throw TransactionError(transaction.ErrorMessageCode ?: 0, transaction.NotificationParams)
                }
                break
            }
        }
        return transactionsPool[id]
    }

    fun getFromPoolBlocking(id: String): Transaction? {
        val job = GlobalScope.launch {
            repeat(20) {
                val transaction = transactionsPool[id]
                if (transaction != null) {
                    if (transaction.ErrorMessageCode != 0) {
                        throw TransactionError(transaction.ErrorMessageCode ?: 0, transaction.NotificationParams)
                    } else {
                        return@launch
                    }
                }
                delay(50)
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


