

package com.lightstreamer.client.requests

import com.lightstreamer.client.session.InternalConnectionDetails
import com.lightstreamer.client.session.InternalConnectionOptions

class CreateSessionRequest(targetServer: String, polling: Boolean, cause: String?, options: InternalConnectionOptions, details: InternalConnectionDetails, delay: Long, password: String?, oldSession: String?) : SessionRequest(polling, delay) {
    init {
        this.setServer(targetServer)
        this.addParameter("LS_op2", "create")
        this.addParameter("LS_polling", "true")
        if (cause != null) {
            this.addParameter("LS_cause", cause)
        }

        var requestedPollingInterval = 0L
        var requestedIdleTimeout = 0L
        if (polling) {
            requestedPollingInterval = options.pollingInterval + delay
            requestedIdleTimeout = options.idleTimeout
        }

        this.addParameter("LS_polling_millis", requestedPollingInterval)
        this.addParameter("LS_idle_millis", requestedIdleTimeout)
        this.addParameter("LS_cid", "tqGko0tg4pkpW3CAN3O4hwLri8LBSG55l")
        if (options.internalMaxBandwidth > 0.0) {
            this.addParameter("LS_requested_max_bandwidth", options.internalMaxBandwidth)
        }

        if (details.adapterSet != null) {
            this.addParameter("LS_adapter_set", details.adapterSet)
        }

        if (details.user != null) {
            this.addParameter("LS_user", details.user)
        }

        if (password != null) {
            this.addParameter("LS_password", password)
        }

        if (oldSession != null) {
            this.addParameter("LS_old_session", oldSession)
        }

        this.addParameter("LS_report_info", "true")
    }

    override fun getRequestName(): String {
        return "create_session"
    }

    override fun isSessionRequest(): Boolean {
        return true
    }
}
