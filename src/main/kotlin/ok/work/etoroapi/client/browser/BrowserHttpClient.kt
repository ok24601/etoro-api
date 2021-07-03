package ok.work.etoroapi.client.browser

import ok.work.etoroapi.client.UserContext
import ok.work.etoroapi.client.clientTime
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
                        "    \"x-sts-clienttime\": \"${clientTime()}\"\n" +
                        "  },\n" +
                        "  \"referrer\": \"https://www.etoro.com/watchlists\",\n" +
                        "  \"referrerPolicy\": \"strict-origin-when-cross-origin\",\n" +
                        "  \"body\": null,\n" +
                        "  \"method\": \"GET\",\n" +
                        "  \"mode\": \"cors\",\n" +
                        "  \"credentials\": \"include\"\n" +
                        "})).json());") as String

    }

    fun fetchAssetInfo(id: String, mode: String): String {
        val driver = metadataService.getDriver()
        val metadata = metadataService.getMetadata()
        val req = "return JSON.stringify(await (await fetch(\"https://www.etoro.com/sapi/trade-real/instruments/private/index?client_request_id=${userContext.requestId}\", {\n" +
                "  \"headers\": {\n" +
                "    \"accept\": \"application/json, text/plain, */*\",\n" +
                "    \"accept-language\": \"en\",\n" +
                "    \"accounttype\": \"${mode}\",\n" +
                "    \"applicationidentifier\": \"ReToro\",\n" +
                "    \"applicationversion\": \"332.0.5\",\n" +
                "    \"authorization\": \"${metadata.token}\",\n" +
                "    \"content-type\": \"application/json;charset=UTF-8\",\n" +
                "    \"sec-ch-ua\": \"\\\" Not;A Brand\\\";v=\\\"99\\\", \\\"Google Chrome\\\";v=\\\"91\\\", \\\"Chromium\\\";v=\\\"91\\\"\",\n" +
                "    \"sec-ch-ua-mobile\": \"?0\",\n" +
                "    \"sec-fetch-dest\": \"empty\",\n" +
                "    \"sec-fetch-mode\": \"cors\",\n" +
                "    \"sec-fetch-site\": \"same-origin\",\n" +
                "    \"x-csrf-token\": \"${metadata.cToken}\"\n" +
                "  },\n" +
                "  \"referrer\": \"https://www.etoro.com/watchlists\",\n" +
                "  \"referrerPolicy\": \"strict-origin-when-cross-origin\",\n" +
                "  \"body\": \"{\\\"InstrumentDataFilters\\\":[\\\"PrivateTradingDataOnly\\\"],\\\"instrumentIds\\\":[${id}],\\\"DelayIntervalMS\\\":888}\",\n" +
                "  \"method\": \"POST\",\n" +
                "  \"mode\": \"cors\",\n" +
                "  \"credentials\": \"include\"\n" +
                "})).json());"
        return driver.executeScript(req) as String
    }

    fun fetchHistory(limit: String = "100",
                     page: String = "1",
                     StartTime: String = "", mode: String): String {
        val driver = metadataService.getDriver()
        val metadata = metadataService.getMetadata()
        val req = "return JSON.stringify(await (await fetch(\"https://www.etoro.com/sapi/trade-data-real/history/private/credit/flat?ItemsPerPage=$limit&PageNumber=$page&StartTime=$StartTime&client_request_id=${userContext.requestId}\", {\n" +
                "  \"headers\": {\n" +
                "    \"accept\": \"application/json, text/plain, */*\",\n" +
                "    \"accept-language\": \"en-GB,en-US;q=0.9,en;q=0.8\",\n" +
                "    \"accounttype\": \"${mode}\",\n" +
                "    \"applicationidentifier\": \"ReToro\",\n" +
                "    \"applicationversion\": \"332.0.5\",\n" +
                "    \"authorization\": \"${metadata.token}\",\n" +
                "    \"sec-ch-ua\": \"\\\" Not;A Brand\\\";v=\\\"99\\\", \\\"Google Chrome\\\";v=\\\"91\\\", \\\"Chromium\\\";v=\\\"91\\\"\",\n" +
                "    \"sec-ch-ua-mobile\": \"?0\",\n" +
                "    \"sec-fetch-dest\": \"empty\",\n" +
                "    \"sec-fetch-mode\": \"cors\",\n" +
                "    \"sec-fetch-site\": \"same-origin\",\n" +
                "    \"x-csrf-token\": \"${metadata.cToken}\"\n" +
                "  },\n" +
                "  \"referrer\": \"https://www.etoro.com/portfolio/history\",\n" +
                "  \"referrerPolicy\": \"strict-origin-when-cross-origin\",\n" +
                "  \"body\": null,\n" +
                "  \"method\": \"GET\",\n" +
                "  \"mode\": \"cors\",\n" +
                "  \"credentials\": \"include\"\n" +
                "})).json());"
        return driver.executeScript(req) as String
    }
}
