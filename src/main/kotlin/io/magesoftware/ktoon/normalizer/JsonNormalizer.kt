package io.magesoftware.ktoon.normalizer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor


@OptIn(ExperimentalSerializationApi::class)
object JsonNormalizer {

    val JSON: Json = Json {
        encodeDefaults = true
        useAlternativeNames = false
        ignoreUnknownKeys = true
        prettyPrint = false
        serializersModule = SerializersModule {
            contextual(LocalDateTime::class, TemporalSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            contextual(LocalDate::class, TemporalSerializer(DateTimeFormatter.ISO_LOCAL_DATE))
            contextual(LocalTime::class, TemporalSerializer(DateTimeFormatter.ISO_LOCAL_TIME))
            contextual(ZonedDateTime::class, TemporalSerializer(DateTimeFormatter.ISO_ZONED_DATE_TIME))
            contextual(OffsetDateTime::class, TemporalSerializer(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
            contextual(Instant::class, TemporalSerializer(DateTimeFormatter.ISO_INSTANT))
            //TODO contextual(Date::class, TemporalSerializer(DateTimeFormatter.ISO_INSTANT))

            contextual(BigInteger::class, BigNumberAsStringSerializer(BigInteger::toString))
            contextual(BigDecimal::class, BigNumberAsStringSerializer(BigDecimal::toPlainString))
        }
    }

    fun parse(json: String?): JsonElement {
        if (json.isNullOrBlank()) {
            throw IllegalArgumentException("Invalid JSON: input is null or blank.")
        }
        return try {
            JSON.parseToJsonElement(json)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JSON structure", e)
        }
    }

    inline fun <reified T : Any> parse(value: T?): JsonElement {
        if (value == null) {
            return JsonNull
        }
        return try {
            JSON.encodeToJsonElement(value)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Invalid object structure or missing serializer for type ${T::class.simpleName}",
                e
            )
        }
    }
}

open class TemporalSerializer<T : TemporalAccessor>(
    private val formatter: DateTimeFormatter,
) : KSerializer<T> {
    override val descriptor = PrimitiveSerialDescriptor("TemporalAccessor", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(formatter.format(value))
    }

    override fun deserialize(decoder: Decoder): T {
        throw UnsupportedOperationException("Deserialization not implemented for this normalizer")
    }
}

private class BigNumberAsStringSerializer<T : Any>(
    private val stringify: (T) -> String
) : KSerializer<T> {
    override val descriptor = PrimitiveSerialDescriptor("BigNumber", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(stringify(value))
    }

    override fun deserialize(decoder: Decoder): T {
        throw UnsupportedOperationException("Deserialization not implemented for this normalizer")
    }
}