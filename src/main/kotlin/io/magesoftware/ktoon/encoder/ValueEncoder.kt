package io.magesoftware.ktoon.encoder

import io.magesoftware.ktoon.ToonOptions
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object ValueEncoder {

    fun encodeValue(value: JsonElement?, options: ToonOptions): String {
        return value?.let { element ->
            when (element) {
                is JsonPrimitive -> {
                    PrimitiveEncoder.encodePrimitive(element, options.delimiter.value)
                }

                is JsonArray -> {
                    buildString {
                        val writer = LineWriter(options.indent)
                        ArrayEncoder.encodeArray(null, element, writer, 0, options)
                        append(writer.toString())
                    }
                }

                is JsonObject -> {
                    buildString {
                        val writer = LineWriter(options.indent)
                        ObjectEncoder.encodeObject(element, writer, 0, options)
                        append(writer.toString())
                    }
                }
            }
        } ?: ""
    }
}