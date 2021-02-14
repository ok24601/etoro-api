package ok.work.etoroapi.controller

import ok.work.etoroapi.client.EtoroHttpClient
import ok.work.etoroapi.client.EtoroPosition
import ok.work.etoroapi.client.EtoroPositionForUpdate
import ok.work.etoroapi.model.Mirror
import ok.work.etoroapi.model.Position
import ok.work.etoroapi.model.ofString
import ok.work.etoroapi.transactions.Transaction
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/mirrors")
class MirrorsController {

    @Autowired
    lateinit var httpClient: EtoroHttpClient

    @GetMapping
    fun getMirrors(): List<Mirror> {
        return httpClient.getMirrors()
    }

    @GetMapping("/positions")
    fun getMirrorPositions(@RequestParam(required = false) mirror_id: String?): List<EtoroPosition> {
        return httpClient.getMirrorPositions(mirror_id)
    }

    @GetMapping("/instruments")
    fun getMirrorInstrumentIds(): List<String> {
        return httpClient.getMirroredInstrumentIds()
    }

    @PutMapping("/watch")
    fun watchMirroredAssets(): Int {
        return httpClient.watchMirroredAssets()
    }
}

