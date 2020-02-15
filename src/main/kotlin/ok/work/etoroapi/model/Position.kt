package ok.work.etoroapi.model

enum class PositionType {
    BUY, SELL
}

data class Position(val id: Int, val instrumentalId: Int, val name: String, val type: PositionType, val amount: Double)
