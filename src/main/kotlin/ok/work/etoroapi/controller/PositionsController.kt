package ok.work.etoroapi.controller

import ok.work.etoroapi.client.EtoroHttpClient
import ok.work.etoroapi.client.EtoroPosition
import ok.work.etoroapi.model.Position
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/positions")
class PositionsController {

    @Autowired
    lateinit var httpClient: EtoroHttpClient


    @GetMapping
    fun getPositions(): List<EtoroPosition> {
        return httpClient.getPositions()
    }

    @PostMapping(value = ["/open"])
    fun openPosition(@RequestBody position: Position): Position {
        return httpClient.openPosition(position)
    }

}

