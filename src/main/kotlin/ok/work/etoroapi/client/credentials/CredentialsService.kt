package ok.work.etoroapi.client.credentials

import org.springframework.stereotype.Component

data class Credentials(val tsim2: String, val lsPassword: String)

@Component
class CredentialsService {

    fun getCredentials(): Credentials {
        return Credentials("9a74f2a902374865a44dcd9d44dac7511925a5e52fb7a36e93a52e65592814c96e58c9cfa0445c77a0ec769470f5fccd92c73d2cab1cb675113f3a76b5e69dd4fdb70ba01ff4de55c72b359a5986ceb04dd73b66c9571b3b24c077f77c322f3a106df990ff2c8492691a971924df35f8fe9b6f63afcf1283097e2177581fdb9d",
                """{"UserAgent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36","ApplicationVersion":"213.0.2","ApplicationName":"ReToro","AccountType":"Demo","ApplicationIdentifier":"ReToro"}""")
    }
}
