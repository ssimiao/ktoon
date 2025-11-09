package io.magesoftware.ktoon.encoder

import io.magesoftware.ktoon.util.DOUBLE_QUOTE
import io.magesoftware.ktoon.util.escape
import io.magesoftware.ktoon.util.isSafeUnquoted
import io.magesoftware.ktoon.util.isValidUnquotedKey
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive


class PrimitiveEncoder private constructor() {

    companion object {
        fun encodePrimitive(value: JsonPrimitive, delimiter: String?): String {
            if (value.isString) return encodeStringLiteral(value.content, delimiter)

            return value.content
        }

        fun encodeStringLiteral(value: String, delimiter: String?): String {
            if (isSafeUnquoted(value, delimiter)) {
                return value
            }

            return DOUBLE_QUOTE.toString() + escape(value) + DOUBLE_QUOTE
        }

        fun encodeKey(key: String?): String? {
            if (isValidUnquotedKey(key)) {
                return key
            }

            return DOUBLE_QUOTE.toString() + escape(key) + DOUBLE_QUOTE
        }


        fun joinEncodedValues(values: MutableList<JsonElement?>, delimiter: String?): String {
            return values.stream()
                .map { v: JsonElement? ->
                    encodePrimitive(
                        (v as JsonPrimitive?)!!,
                        delimiter
                    )
                }
                .reduce { a: String?, b: String? -> a + delimiter + b }
                .orElse("")
        }

        fun formatHeader(
            length: Int,
            key: String?,
            fields: MutableList<String>?,
            delimiter: String,
            lengthMarker: Boolean
        ): String {
            return HeaderFormatter.format(length, key, fields, delimiter, lengthMarker)
        }
    }
}