package ok.work.etoroapi.client.credentials

import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


data class Credentials(val tmis2: String, val lsPassword: String)

@Component
class CredentialsService {

    private lateinit var tmis2: String

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

        driver.get("https://www.etoro.com/")
        Thread.sleep(3000)
        val cookies = driver.manage().cookies
        tmis2 = cookies.filter { c -> c.name.equals("TMIS2") }.first().value

        driver.close()

    }

    fun getCredentials(): Credentials {
        return Credentials(tmis2,
                """{"UserAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36","ApplicationVersion":"213.0.2","ApplicationName":"ReToro","AccountType":"Demo","ApplicationIdentifier":"ReToro"}""")
    }
}
