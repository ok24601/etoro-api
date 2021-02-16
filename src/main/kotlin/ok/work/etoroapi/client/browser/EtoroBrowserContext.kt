package ok.work.etoroapi.client.browser

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.lang.RuntimeException
import java.util.*
import javax.annotation.PostConstruct


data class EtoroMetadata(val cookies: String, val token: String, val lsPassword: String, val baseUrl: String, val domain: String)

@Component
class EtoroMetadataService(@Value("\${etoro.baseUrl}") val baseUrl: String, @Value("\${etoro.domain}") val domain: String) {

    private lateinit var cookies: String
    private lateinit var token: String
    private lateinit var expirationTime: Date
    private lateinit var driver: ChromeDriver
    private lateinit var opts: ChromeOptions

    @PostConstruct
    fun init() {

        val pathToDriver: String = when {
            System.getProperty("os.name").startsWith("Mac") -> {
                "drivers/mac/chromedriver"
            }
            System.getProperty("os.name").toLowerCase().contains("windows") -> {
                "drivers/windows/chromedriver.exe"
            }
            else -> {
                "drivers/ubuntu/chromedriver"
            }
        }

        opts = ChromeOptions()
        System.setProperty("webdriver.chrome.driver", pathToDriver)
        opts.addArguments("start-maximized")
        opts.addArguments("--disable-blink-features=AutomationControlled")
        login()
    }

    fun login() {
        driver = ChromeDriver(opts)

        driver.get("$baseUrl/login")
        val email = System.getenv("LOGIN")
        val password = System.getenv("PASSWORD")
        if (email == null || password == null) {
            throw RuntimeException("LOGIN and/or PASSWORD environment variables are missing")
        }
        driver.findElementById("username").sendKeys(email)
        driver.findElementById("password").sendKeys(password)
        driver.findElementByClassName("blue-btn").click()
        var seconds = 0
        while (true) {
            try {
                token = driver.executeScript("return JSON.parse(atob(window.localStorage.loginData)).stsData_app_1.accessToken;") as String
                println("Token retrieved after %d seconds".format(seconds))
                break
            } catch (e: Exception) {
                if (seconds > 5) {
                    throw RuntimeException("Failed to retrieve token")
                }
                Thread.sleep(1000)
                seconds++
            }
        }
        expirationTime = Date(driver.executeScript("return JSON.parse(atob(window.localStorage.loginData)).stsData_app_1.expirationUnixTimeMs;") as Long)
        println(token)
        println("expires at: $expirationTime")
        val cookiesSet = driver.manage().cookies
        cookies = cookiesSet.toList().joinToString("; ") { cookie -> "${cookie.name}=${cookie.value}" }
        println("cookies: $cookies")

        driver.quit()
    }

    fun getMetadata(): EtoroMetadata {
        if (Date().after(expirationTime)) {
            login()
        }
        return EtoroMetadata(
                cookies,
                token,
                """{"UserAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36","ApplicationVersion":"213.0.2","ApplicationName":"ReToro","AccountType":"Demo","ApplicationIdentifier":"ReToro"}""",
                baseUrl,
                domain
        )
    }
}
