package ok.work.etoroapi.client.cookies

import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


data class EtoroMetadata(val cookies: String, val lsPassword: String, val baseUrl: String, val domain: String)

@Component
class EtoroMetadataService(@Value("\${etoro.baseUrl}") val baseUrl: String, @Value("\${etoro.domain}") val domain: String) {

    private lateinit var cookies: String

    @PostConstruct
    fun init() {

        var pathToDriver: String

        if (System.getProperty("os.name").startsWith("Mac")) {
            pathToDriver = "drivers/mac/phantomjs"
        } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            pathToDriver = "drivers/windows/phantomjs.exe"
        } else {
            pathToDriver = "drivers/ubuntu/phantomjs"
        }

        val caps = DesiredCapabilities()
        caps.isJavascriptEnabled = true // not really needed: JS enabled by default
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, pathToDriver)


        val driver = PhantomJSDriver(caps)

        driver.get(baseUrl)
        Thread.sleep(5000)

        val cookiesSet = driver.manage().cookies
        cookies = cookiesSet.toList().map { cookie -> "${cookie.name}=${cookie.value}" }.joinToString("; ")
        println("cookies: $cookies")

        driver.quit()

    }

    fun getMetadata(): EtoroMetadata {
        return EtoroMetadata(
                cookies,
                """{"UserAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36","ApplicationVersion":"213.0.2","ApplicationName":"ReToro","AccountType":"Demo","ApplicationIdentifier":"ReToro"}""",
                baseUrl,
                domain
        )
    }
}
