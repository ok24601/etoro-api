package ok.work.etoroapi.client.cookies

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


data class EtoroMetadata(val cookies: String, val token: String, val lsPassword: String, val baseUrl: String, val domain: String)

@Component
class EtoroMetadataService(@Value("\${etoro.baseUrl}") val baseUrl: String, @Value("\${etoro.domain}") val domain: String) {

    private lateinit var cookies: String
    private lateinit var token: String

    @PostConstruct
    fun init() {

        var pathToDriver: String

        if (System.getProperty("os.name").startsWith("Mac")) {
            pathToDriver = "drivers/mac/chromedriver"
        } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            pathToDriver = "drivers/windows/chromedriver.exe"
        } else {
            pathToDriver = "drivers/ubuntu/chromedriver"
        }

        val opts = ChromeOptions()
        System.setProperty("webdriver.chrome.driver", pathToDriver)
        opts.addArguments("start-maximized")
        opts.addArguments("--disable-blink-features=AutomationControlled")
        val driver = ChromeDriver(opts)

        driver.get("$baseUrl/login")

        driver.findElementById("username").sendKeys(System.getenv("LOGIN"))
        driver.findElementById("password").sendKeys(System.getenv("PASSWORD"))
        driver.findElementByXPath("/html/body/ui-layout/div/div/div[1]/login/login-sts/div/div/div/form/div/div[5]/button").click()
        Thread.sleep(2000)
        token = driver.executeScript("return JSON.parse(atob(window.localStorage.loginData)).stsData_app_1.accessToken;") as String

        val cookiesSet = driver.manage().cookies
        cookies = cookiesSet.toList().map { cookie -> "${cookie.name}=${cookie.value}" }.joinToString("; ")
        println("cookies: $cookies")


        driver.quit()
    }

    fun getMetadata(): EtoroMetadata {
        return EtoroMetadata(
                cookies,
                token,
                """{"UserAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36","ApplicationVersion":"213.0.2","ApplicationName":"ReToro","AccountType":"Demo","ApplicationIdentifier":"ReToro"}""",
                baseUrl,
                domain
        )
    }
}
