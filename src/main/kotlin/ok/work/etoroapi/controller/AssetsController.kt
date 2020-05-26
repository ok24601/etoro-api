package ok.work.etoroapi.controller

import ok.work.etoroapi.client.EtoroHttpClient
import ok.work.etoroapi.model.TradingMode
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/assets")
class AssetsController {

    @Autowired
    lateinit var httpClient: EtoroHttpClient

    @GetMapping
    fun getAssetInfo(@RequestParam("id") id: String): Map<String, Any> {
        return httpClient.getAssetInfo(id, TradingMode.REAL).toMap()
    }
}
