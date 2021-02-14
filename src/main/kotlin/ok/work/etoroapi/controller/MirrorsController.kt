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
    fun getMirrorPositions(@RequestParam(required = false) mirror_id: String?, @RequestHeader(defaultValue = "Demo") mode: String): List<EtoroPosition> {
        return httpClient.getMirrorPositions(mode, mirror_id)
    }

    @PutMapping("/watch")
    fun watchMirroredAssets(@RequestHeader(defaultValue = "Demo") mode: String): Int {
        return httpClient.watchMirroredAssets(mode)
    }
}

