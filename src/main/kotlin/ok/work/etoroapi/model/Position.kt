package ok.work.etoroapi.model

enum class PositionType {
    BUY, SELL
}

data class Position(val id: String, val name: String, val type: PositionType, val amount: Int, val leverage: Int)
