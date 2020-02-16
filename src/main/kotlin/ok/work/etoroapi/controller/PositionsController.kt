package ok.work.etoroapi.controller

import ok.work.etoroapi.client.EtoroHttpClient
import ok.work.etoroapi.model.Position
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/positions")
class PositionsController {

    @Autowired
    lateinit var httpClient: EtoroHttpClient


    fun getPositions(): List<Position> {
        return ArrayList()
    }

    @PostMapping(value = ["/open"])
    fun openPosition(@RequestBody position: Position): Position {
        return httpClient.openPosition(position)
    }

}

