package com.codelog.datastorerecipe.data.repository.datastore

import com.codelog.datastorerecipe.datastore.Userpreferences
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/**
 * Serializer implementation for `Userpreferences` protocol buffer messages.
 *
 * This object provides methods to serialize and deserialize `Userpreferences` data
 * for use with Jetpack's DataStore. It ensures that all protocol buffer data
 * is properly handled, including setting a default value and managing error scenarios
 * when parsing from an input stream.
 *
 * The `defaultValue` property provides a way to define the initial default proto state,
 * while the serialization and deserialization logic is handled by overriding
 * the `readFrom` and `writeTo` methods.
 */
object PreferenceProtoSerializer : Serializer<Userpreferences> {

    /**
     * Provides the default value for the preferences model `Userpreferences`.
     *
     * This property returns an instance of `Userpreferences` that represents the default
     * state as defined by the `getDefaultInstance` method. It is typically used as the
     * initial or fallback value when no specific preference data has been set or when
     * retrieving the default state of the preferences object in the absence of user-defined data.
     *
     * The returned instance serves as a baseline for the preference data and ensures
     * consistency across the application whenever the default configuration is required.
     */
    override val defaultValue: Userpreferences
        get() = Userpreferences.getDefaultInstance()

    /**
     * Reads and deserializes a Userpreferences object from the provided InputStream.
     *
     * This method parses the InputStream to create an instance of Userpreferences using the
     * `parseFrom` method. If the InputStream cannot be parsed, an InvalidProtocolBufferException
     * is thrown to indicate the error.
     *
     * @param input The InputStream containing the serialized data to be read and parsed.
     * @return A Userpreferences instance deserialized from the provided InputStream.
     * @throws InvalidProtocolBufferException If the InputStream cannot be parsed into a Userpreferences object.
     */
    override suspend fun readFrom(input: InputStream): Userpreferences {
        try {
            return Userpreferences.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw InvalidProtocolBufferException("Cannot read proto.", e)
        }
    }

    /**
     * Writes the provided `UserPreferences` object to the specified `OutputStream`.
     *
     * @param t The `UserPreferences` instance to be written to the output stream.
     * @param output The `OutputStream` to which the `UserPreferences` instance will be serialized.
     */
    override suspend fun writeTo(
        t: Userpreferences,
        output: OutputStream
    ) {
        t.writeTo(output)
    }

}