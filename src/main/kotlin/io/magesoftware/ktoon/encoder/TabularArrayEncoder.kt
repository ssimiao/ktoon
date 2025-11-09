package io.magesoftware.ktoon.encoder

import io.magesoftware.ktoon.ToonOptions
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class TabularArrayEncoder private constructor() {

    companion object {

        fun detectTabularHeader(rows: JsonArray): MutableList<String> {
            if (rows.isEmpty()) {
                return ArrayList()
            }

            val firstRow: JsonElement = rows.first()
            if (firstRow !is JsonObject) {
                return ArrayList()
            }

            val firstKeys: MutableList<String> = ArrayList(firstRow.keys)

            if (firstKeys.isEmpty()) {
                return ArrayList()
            }

            if (isTabularArray(rows, firstKeys)) {
                return firstKeys
            }

            return ArrayList()
        }

        private fun isTabularArray(rows: JsonArray, header: MutableList<String>): Boolean {
            for (row in rows) {
                if (row !is JsonObject) {
                    return false
                }

                val keys: MutableList<String?> = ArrayList(row.keys)

                if (keys.size != header.size) {
                    return false
                }

                for (key in header) {
                    if (!row.containsKey(key)) {
                        return false
                    }
                    if (row[key] !is JsonPrimitive) {
                        return false
                    }
                }
            }

            return true
        }

        fun encodeArrayOfObjectsAsTabular(
            prefix: String?, rows: JsonArray, header: MutableList<String>,
            writer: LineWriter, depth: Int, options: ToonOptions
        ) {
            val headerStr: String = PrimitiveEncoder.formatHeader(
                rows.size, prefix, header, options.delimiter.value,
                options.lengthMarker
            )
            writer.push(depth, headerStr)

            writeTabularRows(rows, header, writer, depth + 1, options)
        }

        fun writeTabularRows(
            rows: JsonArray, header: MutableList<String>, writer: LineWriter, depth: Int,
            options: ToonOptions
        ) {
            for (row in rows) {
                val obj = row as JsonObject
                val values: MutableList<JsonElement?> = ArrayList()
                for (key in header) {
                    values.add(obj[key])
                }
                val joinedValue: String = PrimitiveEncoder.joinEncodedValues(values, options.delimiter.value)
                writer.push(depth, joinedValue)
            }
        }
    }
}