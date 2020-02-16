package ok.work.etoroapi.watchlist

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ok.work.etoroapi.client.EtoroHttpClient
import ok.work.etoroapi.client.websocket.EtoroLightStreamerClient
import ok.work.etoroapi.model.PositionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.lang.RuntimeException
import javax.annotation.PostConstruct

data class EtoroAsset(val InstrumentID: String, val SymbolFull: String, val InstrumentDisplayName: String) {
    override fun toString(): String {
        return SymbolFull
    }
}

data class Asset(val id: String, val name: String, val fullName: String, var buy: Double?, var sell: Double?)


@Component
class Watchlist {
    private val assetsMapIDs: MutableMap<String, EtoroAsset> = mutableMapOf()
    private val assetsMapNames: MutableMap<String, EtoroAsset> = mutableMapOf()

    private val watchlist: MutableMap<String, Asset> = mutableMapOf()

    @Autowired
    lateinit var etoroClient: EtoroHttpClient

    @Autowired
    lateinit var lightStreamerClient: EtoroLightStreamerClient

    val SAVED_LIST_PATH = "watchlist.json"


    @PostConstruct
    fun init() {
        etoroClient.getInstrumentIDs().forEach { asset ->
            assetsMapIDs.put(asset.InstrumentID, asset)
            assetsMapNames.put(asset.SymbolFull.toLowerCase(), asset)
        }
        val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
                .configure(MapperFeature.USE_STD_BEAN_NAMING, true)
        val json = File(SAVED_LIST_PATH)
        if (json.exists()) {
            mapper.readValue<MutableMap<String, Asset>>(json).forEach { p -> addAssetToWatchlistById(p.key) }
        }

        println(assetsMapIDs)
    }

    fun addAssetToWatchlistById(id: String): MutableMap<String, Asset> {
        if (watchlist[id] != null) {
            throw RuntimeException("Already in watchlist $watchlist")
        }
        val asset = assetsMapIDs[id]
        if (asset != null) {
            lightStreamerClient.subscribeById(asset.InstrumentID)
            watchlist[id] = Asset(asset.InstrumentID, asset.SymbolFull, asset.InstrumentDisplayName, null, null)
            saveToFile()
            return watchlist
        } else {
            throw RuntimeException("Asset with InstrumentID $id was not found.")
        }
    }

    fun addAssetToWatchlistByName(name: String): MutableMap<String, Asset> {
        val asset = assetsMapNames[name.toLowerCase()]
        if (asset != null) {
            lightStreamerClient.subscribeById(asset.InstrumentID)
            watchlist[asset.InstrumentID] = Asset(asset.InstrumentID, asset.SymbolFull, asset.InstrumentDisplayName, null, null)
            saveToFile()
            return watchlist
        } else {
            throw RuntimeException("Asset with name $name was not found.")
        }
    }

    fun watchlist(): List<Asset> {
        return watchlist.map { p -> p.value }.toList()
    }

    private fun saveToFile() {
        val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
                .configure(MapperFeature.USE_STD_BEAN_NAMING, true)
        var file = File(SAVED_LIST_PATH)
        if (!file.exists()) {
            file.createNewFile()
        }
        mapper.writeValue(File(SAVED_LIST_PATH), watchlist)
    }

    fun updatePrice(id: String, buy: String?, sell: String?) {
        watchlist[id]?.buy = buy?.toDouble()
        watchlist[id]?.sell = sell?.toDouble()
    }

    fun getPrice(id: String, type: PositionType): Double {
        val asset = watchlist[id]
        if (asset != null) {
            if (type.equals(PositionType.BUY) && asset.buy != null) {
                return asset.buy!!
            } else if (type.equals(PositionType.SELL) && asset.sell != null) {
                return asset.sell!!
            } else {
                throw RuntimeException("None $type price available for id $id")
            }
        }
        throw RuntimeException("Asset with id $id was not found.")
    }
}
