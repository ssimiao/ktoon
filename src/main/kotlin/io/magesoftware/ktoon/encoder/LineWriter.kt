package io.magesoftware.ktoon.encoder

import kotlin.Int
import kotlin.text.repeat

class LineWriter(indentSize: Int) {
    private val lines: MutableList<String?> = ArrayList()
    private val indentationString: String = " ".repeat(indentSize)

    fun push(depth: Int, content: String?) {
        val indent = indentationString.repeat(depth)
        lines.add(indent + content)
    }

    override fun toString(): String {
        return lines.joinToString(separator = "\n")
    }
}