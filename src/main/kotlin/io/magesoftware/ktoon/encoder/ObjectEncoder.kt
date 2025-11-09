package io.magesoftware.ktoon.encoder

import io.magesoftware.ktoon.ToonOptions
import io.magesoftware.ktoon.util.COLON
import io.magesoftware.ktoon.util.SPACE
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class ObjectEncoder private constructor() {

    companion object {

        fun encodeObject(value: JsonObject, writer: LineWriter, depth: Int, options: ToonOptions) {
            val fieldNames: Set<String> = value.keys

            for (fieldName in fieldNames) {
                val fieldValue = value[fieldName]
                encodeKeyValuePair(fieldName, fieldValue, writer, depth, options)
            }
        }

        fun encodeKeyValuePair(
            key: String?, value: JsonElement?, writer: LineWriter, depth: Int,
            options: ToonOptions
        ) {
            val encodedKey = PrimitiveEncoder.encodeKey(key)

            if (value is JsonPrimitive) {
                writer.push(
                    depth, (encodedKey + COLON + SPACE
                            + PrimitiveEncoder.encodePrimitive(value, options.delimiter.value))
                )
            } else if (value is JsonArray) {
                ArrayEncoder.encodeArray(key, value, writer, depth, options)
            } else if (value is JsonObject) {
                if (value.isEmpty()) {
                    writer.push(depth, encodedKey + COLON)
                } else {
                    writer.push(depth, encodedKey + COLON)
                    encodeObject(value, writer, depth + 1, options)
                }
            }
        }
    }
}
