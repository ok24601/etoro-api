package ok.work.etoroapi.controller

import ok.work.etoroapi.client.EtoroHttpClient
import ok.work.etoroapi.client.EtoroPosition
import ok.work.etoroapi.model.Mirror
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/mirrors")
class MirrorsController {

    @Autowired
    lateinit var httpClient: EtoroHttpClient

    @GetMapping
    fun getMirrors(@RequestHeader(defaultValue = "Demo") mode: String): List<Mirror> {
        return httpClient.getMirrors(mode)
    }

    @GetMapping("/positions")
    fun getMirrorPositions(@RequestParam mirror_id: String, @RequestHeader(defaultValue = "Demo") mode: String): List<EtoroPosition> {
        return httpClient.getMirrorPositions(mirror_id, mode)
    }

    @GetMapping("/instruments")
    fun getMirrorInstruments(@RequestHeader(defaultValue = "Demo") mode: String): ArrayList<String> {
        return httpClient.getMirroredInstrumentIds(mode)
    }

    @GetMapping("/watch")
    fun watchMirroredAssets(@RequestHeader(defaultValue = "Demo") mode: String): Int {
        return httpClient.watchMirroredAssets(mode)
    }
}

