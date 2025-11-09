package io.magesoftware.ktoon.encoder

import io.magesoftware.ktoon.ToonOptions
import io.magesoftware.ktoon.util.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class ArrayEncoder private constructor() {

    companion object {
        fun encodeArray(key: String?, value: JsonArray, writer: LineWriter, depth: Int, options: ToonOptions) {
            if (value.isEmpty()) {
                val header = PrimitiveEncoder.formatHeader(
                    0, key, null, options.delimiter.value,
                    options.lengthMarker
                )
                writer.push(depth, header)
                return
            }

            if (isArrayOfPrimitives(value)) {
                encodeInlinePrimitiveArray(key, value, writer, depth, options)
                return
            }

            if (isArrayOfArrays(value)) {
                val allPrimitiveArrays = value.stream()
                    .filter { it: JsonElement? -> it is JsonArray }
                    .map { it: JsonElement? -> it as JsonArray }
                    .allMatch { array: JsonArray? -> isArrayOfPrimitives(array!!) }

                if (allPrimitiveArrays) {
                    encodeArrayOfArraysAsListItems(key, value, writer, depth, options)
                    return
                }
            }

            if (isArrayOfObjects(value)) {
                val header = TabularArrayEncoder.detectTabularHeader(value)
                if (!header.isEmpty()) {
                    TabularArrayEncoder.encodeArrayOfObjectsAsTabular(key, value, header, writer, depth, options)
                } else {
                    encodeMixedArrayAsListItems(key, value, writer, depth, options)
                }
                return
            }

            encodeMixedArrayAsListItems(key, value, writer, depth, options)
        }

        fun isArrayOfPrimitives(array: JsonArray): Boolean {
            for (item in array) {
                if (item !is JsonPrimitive) {
                    return false
                }
            }
            return true
        }

        fun isArrayOfArrays(array: JsonArray): Boolean {
            for (item in array) {
                if (item !is JsonArray) {
                    return false
                }
            }
            return true
        }

        fun isArrayOfObjects(array: JsonArray): Boolean {
            for (item in array) {
                if (item !is JsonObject) {
                    return false
                }
            }
            return true
        }


        private fun encodeInlinePrimitiveArray(
            prefix: String?, values: JsonArray, writer: LineWriter, depth: Int,
            options: ToonOptions
        ) {
            val formatted: String = formatInlineArray(values, options.delimiter.value, prefix, options.lengthMarker)
            writer.push(depth, formatted)
        }

        fun formatInlineArray(values: JsonArray, delimiter: String, prefix: String?, lengthMarker: Boolean): String {
            val valueList: MutableList<JsonElement?> = ArrayList(values)

            val header = PrimitiveEncoder.formatHeader(values.size, prefix, null, delimiter, lengthMarker)
            val joinedValue = PrimitiveEncoder.joinEncodedValues(valueList, delimiter)

            if (values.isEmpty()) {
                return header
            }
            return header + SPACE + joinedValue
        }

        private fun encodeArrayOfArraysAsListItems(
            prefix: String?, values: JsonArray, writer: LineWriter, depth: Int,
            options: ToonOptions
        ) {
            val header = PrimitiveEncoder.formatHeader(
                values.size, prefix, null, options.delimiter.value,
                options.lengthMarker
            )
            writer.push(depth, header)

            for (arr in values) {
                if (arr is JsonArray && isArrayOfPrimitives(arr)) {
                    val inline: String = formatInlineArray(
                        arr, options.delimiter.value, null,
                        options.lengthMarker
                    )
                    writer.push(depth + 1, LIST_ITEM_PREFIX + inline)
                }
            }
        }

        private fun encodeMixedArrayAsListItems(
            prefix: String?, items: JsonArray, writer: LineWriter, depth: Int,
            options: ToonOptions
        ) {
            val header = PrimitiveEncoder.formatHeader(
                items.size, prefix, null, options.delimiter.value,
                options.lengthMarker
            )
            writer.push(depth, header)

            for (item in items) {
                if (item is JsonPrimitive) {
                    writer.push(
                        depth + 1,
                        LIST_ITEM_PREFIX + PrimitiveEncoder.encodePrimitive(item, options.delimiter.value)
                    )
                } else if (item is JsonArray) {
                    if (isArrayOfPrimitives(item)) {
                        val inline: String = formatInlineArray(
                            item, options.delimiter.value, null,
                            options.lengthMarker
                        )
                        writer.push(depth + 1, LIST_ITEM_PREFIX + inline)
                    }
                } else if (item is JsonObject) {
                    ListItemEncoder.encodeObjectAsListItem(
                        item, writer,
                        depth + 1, options
                    )
                }
            }
        }
    }
}