package s4jk.jvm.serialization.objects

import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import s4jk.jvm.serialization.JSUtils
import s4jk.jvm.serialization.JsonStringManager

/**
 * Creates a [JsonObject] with an optional name and a custom build action.
 *
 * This function allows for the creation of a [JsonObject] by providing an optional name and a lambda function
 * ([buildAction]) that configures the [JsonObject.Constructor]. The [buildAction] lambda is applied to a new
 * instance of [JsonObject.Constructor] to set up its internal map. The resulting map is then used to create
 * a [JsonObject], which is returned with the specified name.
 *
 * This approach provides a concise and flexible way to construct JSON objects programmatically.
 *
 * @param name An optional name to associate with the [JsonObject]. Defaults to a generated name if null.
 * @param buildAction A lambda function that configures the [JsonObject.Constructor]. The lambda is applied
 * to a new [JsonObject.Constructor] instance and should populate its map.
 * @return An [IJO] instance representing the created [JsonObject].
 */
fun jsonObjectOf(
    name: String = JSUtils.generateName(null),
    buildAction: JsonObject.Constructor.() -> JsonObject.Constructor
): IJO {
    val constructor = JsonObject.Constructor().apply { buildAction() }
    return JsonObject.from(name, constructor.map)
}

/**
 * Converts a map into a [JsonObject].
 *
 * This function creates a [JsonObject] from a given map. The map's keys are used as the keys in the resulting
 * [JsonObject], and the map's values are used as the corresponding values.
 *
 * @param name An optional name to associate with the [JsonObject]. Can be null.
 * @return An [IJO] instance representing the created [JsonObject].
 */
fun Map<*,*>.toJsonObject(name: String? = null): IJO {
    return JsonObject.from(name, this)
}

/**
 * Converts a JSON string into a [JsonObject].
 *
 * This function creates a [JsonObject] from a JSON-formatted string. The string is parsed, and the resulting
 * [JsonObject] contains the data represented by the JSON string.
 *
 * @param name An optional name to associate with the [JsonObject]. Can be null.
 * @return An [IJO] instance representing the parsed JSON data.
 */
fun String.toJsonObject(name: String? = null): IJO {
    return JsonObject.from(name, this)
}

/**
 * Concrete implementation of [IJO], extending [AbstractJsonObject].
 * This class provides methods for creating and manipulating JSON objects from various data sources
 * such as maps and JSON strings.
 */
class JsonObject private constructor(
    @Nullable objectName: String?,
    @NotNull map: MutableMap<String, Any?>
): AbstractJsonObject(JSUtils.generateName(objectName), map) {

    class Constructor {
        internal val map: MutableMap<String, Any?> = mutableMapOf()

        infix fun String.to(value: Any?): Constructor {
            this@Constructor.map[this] = value
            return this@Constructor
        }
    }

    companion object Static {

        /**
         * Creates a new [JsonObject] with an optional name and an empty map.
         *
         * @param name An optional name to associate with the [JsonObject]. Defaults to null.
         * @return A new [JsonObject] instance with the specified name and an empty map.
         */
        @JvmStatic
        fun create(@Nullable name: String?): IJO {
            return JsonObject(name, mutableMapOf())
        }

        @JvmStatic
        fun create(): IJO {
            return create(null)
        }

        /**
         * Creates a [JsonObject] from a given map.
         *
         * @param name An optional name to associate with the [JsonObject]. Can be null.
         * @param source The source map to convert into a [JsonObject]. Map keys are converted to strings.
         * @return A new [JsonObject] instance with the entries from the source map.
         */
        @JvmStatic
        fun from(@Nullable name: String?, @NotNull source: Map<*, *>): IJO {
            val json = create(name)
            source.forEach { (key, value) ->
                json.set(key.toString(), value)
            }
            return json
        }

        @JvmStatic
        fun from(@NotNull source: Map<*, *>): IJO {
            return from(null, source)
        }

        /**
         * Creates a [JsonObject] from another [IJO] instance.
         *
         * @param name An optional name to associate with the new [JsonObject]. Can be null.
         * @param source The source [IJO] instance to convert.
         * @return A new [JsonObject] instance containing the entries from the source [IJO].
         */
        @JvmStatic
        fun from(@Nullable name: String?, @NotNull source: IJO): IJO {
            val json = create(name)
            source.entries.forEach { (key, value) ->
                json.set(key, value)
            }
            return json
        }

        @JvmStatic
        fun from(@NotNull source: IJO): IJO {
            return from(null, source)
        }

        /**
         * Creates a [JsonObject] from a JSON string.
         *
         * @param name An optional name to associate with the new [JsonObject]. Can be null.
         * @param source The JSON string to parse into a [JsonObject].
         * @return A new [JsonObject] instance representing the parsed JSON data.
         */
        @JvmStatic
        fun from(@Nullable name: String?, @NotNull source: String): IJO {
            return JsonStringManager.stringToJsonObject(name, source)
        }

        @JvmStatic
        fun from(@NotNull source: String): IJO {
            return from(null, source)
        }
    }
}