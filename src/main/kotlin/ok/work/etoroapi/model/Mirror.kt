package ok.work.etoroapi.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Mirror(
    val MirrorID: Int,
    val InitialInvestment: Double,
    val DepositSummary: Double,
    val WithdrawalSummary: Double,
    val AvailableAmount: Double,
    val ClosedPositionsNetProfit: Double,
    val StopLossAmount: Double,
    val StopLossPercentage: Double,
    val CopyExistingPositions: Boolean,
    val IsPaused: Boolean,
    val PendingForClosure: Boolean,
    val StartedCopyDate: String,
    val User: User
)