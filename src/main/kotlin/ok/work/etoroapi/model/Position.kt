package ok.work.etoroapi.model

enum class PositionType {
    BUY, SELL
}

data class Position(val instrumentId: String?, val name: String?, val type: PositionType, val amount: Double, val leverage: Int, var takeProfit: Double, var stopLoss: Double, var takeProfitRate: Double, var stopLossRate: Double, var takeProfitAmountRate: Double, var stopLossAmountRate: Double,val tsl: Boolean = false)