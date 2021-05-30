package ok.work.etoroapi.client.browser

import ok.work.etoroapi.client.UserContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BrowserHttpClient {

    @Autowired
    lateinit var metadataService: EtoroMetadataService

    @Autowired
    lateinit var userContext: UserContext

    fun fetchAccountData(mode: String): String {
        val driver = metadataService.getDriver()
        val metadata = metadataService.getMetadata()
        return driver.executeScript(
                "return JSON.stringify(await (await fetch(\"https://www.etoro.com/api/logindata/v1.1/logindata?client_request_id=${userContext.requestId}&conditionIncludeDisplayableInstruments=false&conditionIncludeMarkets=false&conditionIncludeMetadata=false&conditionIncludeMirrorValidation=false\", {\n" +
                        "  \"headers\": {\n" +
                        "    \"accept\": \"application/json, text/plain, */*\",\n" +
                        "    \"accept-language\": \"en,de;q=0.9,uk;q=0.8,ru;q=0.7,en-US;q=0.6,de-DE;q=0.5,ru-UA;q=0.4\",\n" +
                        "    \"accounttype\": \"${mode}\",\n" +
                        "    \"applicationidentifier\": \"ReToro\",\n" +
                        "    \"applicationversion\": \"326.0.3\",\n" +
                        "    \"authorization\": \"${metadata.token}\",\n" +
                        "    \"sec-ch-ua\": \"\\\" Not A;Brand\\\";v=\\\"99\\\", \\\"Chromium\\\";v=\\\"90\\\", \\\"Google Chrome\\\";v=\\\"90\\\"\",\n" +
                        "    \"sec-ch-ua-mobile\": \"?0\",\n" +
                        "    \"sec-fetch-dest\": \"empty\",\n" +
                        "    \"sec-fetch-mode\": \"cors\",\n" +
                        "    \"sec-fetch-site\": \"same-origin\",\n" +
                        "    \"x-csrf-token\": \"${metadata.cToken}\",\n" +
                        "    \"x-sts-autologin\": \"true\",\n" +
                        "    \"x-sts-clienttime\": \"2021-05-30T21:59:03\"\n" +
                        "  },\n" +
                        "  \"referrer\": \"https://www.etoro.com/watchlists\",\n" +
                        "  \"referrerPolicy\": \"strict-origin-when-cross-origin\",\n" +
                        "  \"body\": null,\n" +
                        "  \"method\": \"GET\",\n" +
                        "  \"mode\": \"cors\",\n" +
                        "  \"credentials\": \"include\"\n" +
                        "})).json());") as String

    }

}