package ok.work.etoroapi

import ok.work.etoroapi.client.EtoroHttpClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EtoroApiApplication


fun main(args: Array<String>) {
    val context = runApplication<EtoroApiApplication>(*args)

}
