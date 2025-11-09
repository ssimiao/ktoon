package io.magesoftware.ktoon

import io.magesoftware.ktoon.encoder.ValueEncoder
import kotlinx.serialization.json.JsonElement
import io.magesoftware.ktoon.normalizer.JsonNormalizer

class Ktoon {

    inline fun <reified T : Any> encodeSerializable(json: T?, options: ToonOptions = ToonOptions.DEFAULT): String {
        val parsed: JsonElement = JsonNormalizer.parse(json)
        return ValueEncoder.encodeValue(parsed, options)
    }

    fun encodeJson(json: String?, options: ToonOptions = ToonOptions.DEFAULT): String {
        val parsed: JsonElement = JsonNormalizer.parse(json)
        return ValueEncoder.encodeValue(parsed, options)
    }
}