package ok.work.etoroapi.controller

import ok.work.etoroapi.client.EtoroHttpClient
import ok.work.etoroapi.client.EtoroPosition
import ok.work.etoroapi.model.Position
import ok.work.etoroapi.model.ofString
import ok.work.etoroapi.transactions.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/positions")
class PositionsController {

    @Autowired
    lateinit var httpClient: EtoroHttpClient


    @GetMapping
    fun getPositions(@RequestHeader(defaultValue = "Demo") mode: String): List<EtoroPosition> {
        return httpClient.getPositions(ofString(mode))
    }

    @PostMapping(value = ["/open"])
    fun openPosition(@RequestBody position: Position, @RequestHeader(defaultValue = "Demo") mode: String): Transaction {
        return httpClient.openPosition(position, ofString(mode))
    }

    @DeleteMapping(value = ["/close"])
    fun closePosition(id: String, @RequestHeader(defaultValue = "Demo") mode: String) {
        httpClient.deletePosition(id, ofString(mode))
    }

}

