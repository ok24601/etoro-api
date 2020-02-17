package ok.work.etoroapi.model

import java.lang.RuntimeException

enum class TradingMode(type: String) {
    REAL("Real"),
    DEMO("Demo"),
}

fun ofString(name: String): TradingMode {
    if (name.toLowerCase().equals("demo")) {
        return TradingMode.DEMO
    } else if (name.toLowerCase().equals("real")) {
        return TradingMode.REAL
    } else {
        throw RuntimeException("Invalid trading mode $name")
    }
}
