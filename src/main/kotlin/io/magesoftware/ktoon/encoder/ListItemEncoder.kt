package io.magesoftware.ktoon.encoder

import io.magesoftware.ktoon.ToonOptions
import io.magesoftware.ktoon.util.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive


class ListItemEncoder private constructor() {

    companion object {

        fun encodeObjectAsListItem(obj: JsonObject, writer: LineWriter, depth: Int, options: ToonOptions) {
            val keys: MutableList<String> = ArrayList(obj.keys)

            if (keys.isEmpty()) {
                writer.push(depth, LIST_ITEM_MARKER)
                return
            }

            val firstKey: String = keys.first()
            val firstValue = obj[firstKey]
            encodeFirstKeyValue(firstKey, firstValue, writer, depth, options)

            for (i in 1..<keys.size) {
                val key = keys[i]
                ObjectEncoder.encodeKeyValuePair(key, obj[key], writer, depth + 1, options)
            }
        }

        private fun encodeFirstKeyValue(
            key: String?, value: JsonElement?, writer: LineWriter, depth: Int,
            options: ToonOptions
        ) {
            val encodedKey = PrimitiveEncoder.encodeKey(key)

            when (value) {
                is JsonPrimitive -> {
                    encodeFirstValueAsPrimitive(encodedKey, value, writer, depth, options)
                }

                is JsonArray -> {
                    encodeFirstValueAsArray(key, encodedKey, value, writer, depth, options)
                }

                is JsonObject -> {
                    encodeFirstValueAsObject(encodedKey, value, writer, depth, options)
                }

                else -> {}
            }
        }

        private fun encodeFirstValueAsPrimitive(
            encodedKey: String?, value: JsonPrimitive, writer: LineWriter, depth: Int,
            options: ToonOptions
        ) {
            writer.push(
                depth, (LIST_ITEM_PREFIX + encodedKey + COLON + SPACE
                        + PrimitiveEncoder.encodePrimitive(value, options.delimiter.value))
            )
        }

        private fun encodeFirstValueAsArray(
            key: String?, encodedKey: String?, arrayValue: JsonArray, writer: LineWriter,
            depth: Int, options: ToonOptions
        ) {
            if (ArrayEncoder.isArrayOfPrimitives(arrayValue)) {
                encodeFirstArrayAsPrimitives(key, arrayValue, writer, depth, options)
            } else if (ArrayEncoder.isArrayOfObjects(arrayValue)) {
                encodeFirstArrayAsObjects(key, encodedKey, arrayValue, writer, depth, options)
            } else {
                encodeFirstArrayAsComplex(encodedKey, arrayValue, writer, depth, options)
            }
        }

        private fun encodeFirstArrayAsPrimitives(
            key: String?, arrayValue: JsonArray?, writer: LineWriter, depth: Int,
            options: ToonOptions
        ) {
            val formatted: String = ArrayEncoder.formatInlineArray(
                arrayValue!!, options.delimiter.value, key,
                options.lengthMarker
            )
            writer.push(depth, LIST_ITEM_PREFIX + formatted)
        }

        private fun encodeFirstArrayAsObjects(
            key: String?, encodedKey: String?, arrayValue: JsonArray,
            writer: LineWriter, depth: Int, options: ToonOptions
        ) {
            val header = TabularArrayEncoder.detectTabularHeader(arrayValue)
            if (!header.isEmpty()) {
                val headerStr = PrimitiveEncoder.formatHeader(
                    arrayValue.size, key, header,
                    options.delimiter.value, options.lengthMarker
                )
                writer.push(depth, LIST_ITEM_PREFIX + headerStr)
                // Write just the rows, header was already written above
                TabularArrayEncoder.writeTabularRows(arrayValue, header, writer, depth + 1, options)
            } else {
                writer.push(
                    depth,
                    LIST_ITEM_PREFIX + encodedKey + OPEN_BRACKET + arrayValue.size + CLOSE_BRACKET + COLON
                )
                for (item in arrayValue) {
                    if (item is JsonObject) {
                        encodeObjectAsListItem(item, writer, depth + 1, options)
                    }
                }
            }
        }

        private fun encodeFirstArrayAsComplex(
            encodedKey: String?, arrayValue: JsonArray, writer: LineWriter, depth: Int,
            options: ToonOptions
        ) {
            writer.push(depth, LIST_ITEM_PREFIX + encodedKey + OPEN_BRACKET + arrayValue.size + CLOSE_BRACKET + COLON)

            for (item in arrayValue) {
                when (item) {
                    is JsonPrimitive -> {
                        writer.push(
                            depth + 1, LIST_ITEM_PREFIX
                                    + PrimitiveEncoder.encodePrimitive(item, options.delimiter.value)
                        )
                    }

                    is JsonArray if ArrayEncoder.isArrayOfPrimitives(item) -> {
                        val inline: String = ArrayEncoder.formatInlineArray(
                            item, options.delimiter.value, null,
                            options.lengthMarker
                        )
                        writer.push(depth + 1, LIST_ITEM_PREFIX + inline)
                    }

                    is JsonObject -> {
                        encodeObjectAsListItem(item, writer, depth + 1, options)
                    }

                    else -> {}
                }
            }
        }

        private fun encodeFirstValueAsObject(
            encodedKey: String?, nestedObj: JsonObject, writer: LineWriter, depth: Int,
            options: ToonOptions?
        ) {
            writer.push(depth, LIST_ITEM_PREFIX + encodedKey + COLON)
            if (!nestedObj.isEmpty()) {
                ObjectEncoder.encodeObject(nestedObj, writer, depth + 2, options!!)
            }
        }
    }
}