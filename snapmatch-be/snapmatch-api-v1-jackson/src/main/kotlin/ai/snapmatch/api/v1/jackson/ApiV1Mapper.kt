package ai.snapmatch.api.v1.jackson

import ai.snapmatch.api.v1.models.IRequestDto
import ai.snapmatch.api.v1.models.IResponseDto
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.datetime.Instant
import kotlin.jvm.java
import kotlin.run

// Custom serializer for kotlinx.datetime.Instant
private class KotlinxInstantSerializer : JsonSerializer<Instant>() {
    override fun serialize(value: Instant, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toString())
    }
}

// Custom deserializer for kotlinx.datetime.Instant
private class KotlinxInstantDeserializer : JsonDeserializer<Instant>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant {
        return Instant.parse(p.text)
    }
}

val kotlinxDatetimeModule = SimpleModule().apply {
    addSerializer(Instant::class.java, KotlinxInstantSerializer())
    addDeserializer(Instant::class.java, KotlinxInstantDeserializer())
}

val apiV1Mapper = JsonMapper.builder().run {
//    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
//  setSerializationInclusion(JsonInclude.Include.NON_NULL)
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    addModule(KotlinModule.Builder().build())
    addModule(JavaTimeModule())
    addModule(kotlinxDatetimeModule)
    build()
}

@Suppress("unused")
fun apiV1RequestSerialize(request: IRequestDto): String = apiV1Mapper.writeValueAsString(request)

@Suppress("UNCHECKED_CAST", "unused")
fun <T : IRequestDto> apiV1RequestDeserialize(json: String): T =
    apiV1Mapper.readValue(json, IRequestDto::class.java) as T

@Suppress("unused")
fun apiV1ResponseSerialize(response: IResponseDto): String = apiV1Mapper.writeValueAsString(response)

@Suppress("UNCHECKED_CAST", "unused")
fun <T : IResponseDto> apiV1ResponseDeserialize(json: String): T =
    apiV1Mapper.readValue(json, IResponseDto::class.java) as T
