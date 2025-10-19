import ai.snapmatch.api.v1.models.IRequestDto
import ai.snapmatch.api.v1.models.IResponseDto
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import kotlin.jvm.java
import kotlin.run

val apiV1Mapper = JsonMapper.builder().run {
//    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
//    setSerializationInclusion(JsonInclude.Include.NON_NULL)
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
