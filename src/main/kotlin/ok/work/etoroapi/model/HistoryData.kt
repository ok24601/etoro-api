package ok.work.etoroapi.model

enum class HistoryInterval {
    OneMinute,
    OneDay
}

data class EtoroHistoryPosition(var InstrumentID: String, var IsBuy: Boolean, var Leverage: Int, var Amount: Int, var CloseDateTime: String, var OpenDateTime: String,
                                var NetProfit: Double, var OpenRate: Double, var Units: Double, var TotalFees: Double, var CloseRate: Double, var InitialUnits: Double)

data class InstrumentHistoryData(var High : Double, var Low : Double, var FromDate : String, var Close : Double, var InstrumentID: String, var Open : Double)