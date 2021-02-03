package ok.work.etoroapi.controller

import ok.work.etoroapi.client.EtoroHttpClient
import ok.work.etoroapi.model.TradingMode
import ok.work.etoroapi.model.ofString
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/assets")
class AssetsController {

    @Autowired
    lateinit var httpClient: EtoroHttpClient

    @GetMapping
    fun getAssetInfo(@RequestParam("id") id: String): Map<String, Any> {
        return httpClient.getAssetInfo(id, TradingMode.REAL).toMap()
    }

    @GetMapping(value = ["/cash"])
    fun getCash(@RequestHeader(defaultValue = "Demo") mode: String): Double {
        return httpClient.getCash(ofString(mode))
    }
}
