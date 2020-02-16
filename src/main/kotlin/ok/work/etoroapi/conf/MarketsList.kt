package ok.work.etoroapi.conf

import ok.work.etoroapi.client.EtoroHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

data class Asset(val InstrumentID: String, val SymbolFull: String) {
    override fun toString(): String {
        return SymbolFull
    }
}

@Component
class EtoroAssets {
    private var assetsMap: MutableMap<String, Asset> = mutableMapOf()

    @Autowired
    lateinit var etoroClient: EtoroHttpClient

    @PostConstruct
    fun fetchAssetsList() {
        etoroClient.getInstrumentIDs().forEach { asset -> assetsMap.put(asset.InstrumentID, asset) }
        println(assetsMap)
    }
}
