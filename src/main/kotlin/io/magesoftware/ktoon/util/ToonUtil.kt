package io.magesoftware.ktoon.util

private val NUMERIC_REGEX = Regex("^-?\\d+(?:\\.\\d+)?(?:e[+-]?\\d+)?$", RegexOption.IGNORE_CASE)
private val OCTAL_REGEX = Regex("^0\\d+$")
private val UNQUOTED_KEY_REGEX = Regex("^[A-Z_][\\w.]*$", RegexOption.IGNORE_CASE)
private val STRUCTURAL_REGEX = Regex("[\\[\\]{}]")
private val CONTROL_REGEX = Regex("[\\n\\r\\t]")

fun isSafeUnquoted(value: String?, delimiter: String?): Boolean {
    if (value.isNullOrEmpty()) {
        return false
    }

    return when {
        value.isPaddedWithWhitespace() -> false
        value.looksLikeKeyword() -> false
        value.looksLikeNumber() -> false
        value.contains(COLON) -> false
        value.contains(DOUBLE_QUOTE) || value.contains(BACKSLASH) -> false
        value.containsStructuralCharacters() -> false
        value.containsControlCharacters() -> false
        value.containsDelimiter(delimiter) -> false
        value.startsWith(LIST_ITEM_MARKER) -> false
        else -> true
    }
}

fun isValidUnquotedKey(key: String?): Boolean {
    return key != null && UNQUOTED_KEY_REGEX.matches(key)
}

private fun String.isPaddedWithWhitespace(): Boolean {
    return this != this.trim()
}

private fun String.looksLikeKeyword(): Boolean {
    val keywords = setOf(TRUE_LITERAL, FALSE_LITERAL, NULL_LITERAL)
    return this in keywords
}

private fun String.looksLikeNumber(): Boolean {
    return NUMERIC_REGEX.matches(this) || OCTAL_REGEX.matches(this)
}

private fun String.containsStructuralCharacters(): Boolean {
    return STRUCTURAL_REGEX.containsMatchIn(this)
}

private fun String.containsControlCharacters(): Boolean {
    return CONTROL_REGEX.containsMatchIn(this)
}

private fun String.containsDelimiter(delimiter: String?): Boolean {
    return delimiter?.let { this.contains(it) } ?: false
}

fun escape(value: String?): String? {
    return value
        ?.replace("\\", "\\\\") // Must escape backslash first
        ?.replace("\"", "\\\"")
        ?.replace("\n", "\\n")
        ?.replace("\r", "\\r")
        ?.replace("\t", "\\t")
}