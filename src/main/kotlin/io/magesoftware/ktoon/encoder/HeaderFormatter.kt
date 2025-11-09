package io.magesoftware.ktoon.encoder

import io.magesoftware.ktoon.util.CLOSE_BRACE
import io.magesoftware.ktoon.util.CLOSE_BRACKET
import io.magesoftware.ktoon.util.COLON
import io.magesoftware.ktoon.util.COMMA
import io.magesoftware.ktoon.util.OPEN_BRACE
import io.magesoftware.ktoon.util.OPEN_BRACKET

object HeaderFormatter {

    data class HeaderConfig(
        val length: Int,
        val key: String? = null,
        val fields: List<String?>? = null,
        val delimiter: String = COMMA,
        val lengthMarker: Boolean = false
    )

    fun format(length: Int, key: String?, fields: MutableList<String>?, delimiter: String, lengthMarker: Boolean
    ): String {
        val config = HeaderConfig(length, key, fields, delimiter, lengthMarker)
        return format(config)
    }

    fun format(config: HeaderConfig): String {
        return buildString {
            val (length, key, fields, delimiter, lengthMarker) = config

            appendKeyIfPresent(key)
            appendArrayLength(length, delimiter, lengthMarker)
            appendFieldsIfPresent(fields, delimiter)
            append(COLON)
        }
    }

    private fun StringBuilder.appendKeyIfPresent(key: String?) {
        key?.let { append(PrimitiveEncoder.encodeKey(it)) }
    }

    private fun StringBuilder.appendArrayLength(
        length: Int,
        delimiter: String,
        lengthMarker: Boolean
    ) {
        append(OPEN_BRACKET)

        if (lengthMarker) {
            append("#")
        }

        append(length)
        appendDelimiterIfNotDefault(delimiter)
        append(CLOSE_BRACKET)
    }

    private fun StringBuilder.appendDelimiterIfNotDefault(delimiter: String) {
        if (delimiter != COMMA) {
            append(delimiter)
        }
    }

    private fun StringBuilder.appendFieldsIfPresent(
        fields: List<String?>?,
        delimiter: String
    ) {
        if (fields.isNullOrEmpty()) {
            return
        }

        append(OPEN_BRACE)
        val quotedFields = formatFields(fields, delimiter)
        append(quotedFields)
        append(CLOSE_BRACE)
    }

    private fun formatFields(fields: List<String?>, delimiter: String?): String? {
        return fields.stream()
            .map { key: String? -> PrimitiveEncoder.encodeKey(key) }
            .reduce { a: String?, b: String? -> a + delimiter + b }
            .orElse("")
    }
}