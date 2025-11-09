package io.magesoftware.ktoon

enum class Delimiter(val value: String) {
    COMMA(","),
    TAB("\t"),
    PIPE("|");

    override fun toString(): String {
        return value
    }
}