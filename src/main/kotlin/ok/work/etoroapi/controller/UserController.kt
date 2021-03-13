package ok.work.etoroapi.controller

import ok.work.etoroapi.client.EtoroHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    private lateinit var client: EtoroHttpClient

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserData(@RequestHeader(defaultValue = "Demo") mode: String): String {
        return client.getLoginData(mode).toString()
    }

}