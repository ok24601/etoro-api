package ok.work.etoroapi.model

import java.lang.RuntimeException

enum class TradingMode(type: String) {
    REAL("Real"),
    DEMO("Demo"),
}

fun ofString(name: String): TradingMode {
    return when {
        name.toLowerCase().equals("demo") -> TradingMode.DEMO
        name.toLowerCase().equals("real") -> TradingMode.REAL
        else -> throw RuntimeException("Invalid trading mode $name")
    }
}
