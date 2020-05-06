package ok.work.etoroapi.model

enum class PositionType {
    BUY, SELL
}

data class Position(val instrumentId: String?, val name: String?, val type: PositionType, val amount: Int, val leverage: Int, var takeProfitRate: Double, var stopLossRate: Double, val tsl: Boolean = false)
