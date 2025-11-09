package io.magesoftware

import io.magesoftware.ktoon.Ktoon
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Test

@kotlinx.serialization.Serializable
data class SimplePojo(val id: Int, val name: String)

@kotlinx.serialization.Serializable
data class NestedPojo(val code: String, val item: SimplePojo, val items: List<SimplePojo>)

class KtoonTest {

    private val ktoon = Ktoon()

    private val json = Json

    @Test
    fun `test encode simple pojo`() {
        val pojo = SimplePojo(1, "oi")
        val ktoon = ktoon.encodeJson(json.encodeToString(pojo))
        println(ktoon)
    }

    @Test
    fun `test encode nested pojo`() {
        val pojo = NestedPojo("1", SimplePojo(1, "code"), listOf(SimplePojo(2, "code2")))
        val ktoon = ktoon.encodeJson(json.encodeToString(pojo))
        println(ktoon)
    }

    @Test
    fun `test encode nested pojo object`() {
        val pojo = NestedPojo("1", SimplePojo(1, "code"), listOf(SimplePojo(2, "code2")))
        val ktoon = ktoon.encodeSerializable(pojo)
        println(ktoon)
    }
}