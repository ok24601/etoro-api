package ok.work.etoroapi.controller

import ok.work.etoroapi.client.EtoroHttpClient
import ok.work.etoroapi.watchlist.EtoroFullAsset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/instruments")
class InstrumentsController {

    @Autowired
    lateinit var httpClient: EtoroHttpClient


    @GetMapping
    fun getInstruments(): List<EtoroFullAsset> {
        return httpClient.getInstruments()
    }

    @GetMapping("/{id}")
    fun getInstrumentById(@PathVariable("id") id: String): EtoroFullAsset? {
        return httpClient.getInstruments().find { i -> i.InstrumentID === id }
    }

}

