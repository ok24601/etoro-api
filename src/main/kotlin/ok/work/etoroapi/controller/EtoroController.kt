package ok.work.etoroapi.controller

import ok.work.etoroapi.model.Position
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EtoroController {

    @RequestMapping("/positions")
    fun getPositions(): List<Position> {
        return ArrayList();
    }


}

