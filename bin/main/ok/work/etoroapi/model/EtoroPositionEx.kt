package ok.work.etoroapi.model

enum class EtoroPositionTypeEx {
    BUY, SELL
}

public data class EtoroPositionEx(val instrumentId: String?, val name: String?, val type: EtoroPositionTypeEx, val amount: Double, val leverage: Int, var takeProfit: Double, var stopLoss: Double, var takeProfitRate: Double, var stopLossRate: Double, var takeProfitAmountRate: Double, var stopLossAmountRate: Double, val tsl: Boolean = false)
