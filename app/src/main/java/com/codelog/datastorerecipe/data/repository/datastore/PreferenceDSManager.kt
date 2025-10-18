package com.codelog.datastorerecipe.data.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Manages key-value storage using Jetpack DataStore with the Preferences API.
 *
 * PreferenceDSManager is responsible for persisting and retrieving key-value pairs using the Preferences
 * DataStore implementation. It provides a convenient way to save scalar values and retrieve them in a
 * structured and efficient manner.
 *
 * The DataStore is backed by a file named "settings" stored in the app's internal storage, and it supports
 * multiple data types such as strings, integers, and booleans.
 *
 * @param context The application context used to initialize the underlying DataStore instance.
 */
class PreferenceDSManager ( private val context: Context) {

    /**
     * Extension property to provide a `DataStore` instance for storing key-value pairs using Jetpack DataStore with Preferences API.
     *
     * This property is scoped to a `Context` and is initialized through the `preferencesDataStore` delegate,
     * which automatically handles the creation and management of a DataStore instance. The underlying data
     * is stored in a file named "settings" within the app's internal storage.
     *
     * The `dataStore` property can be used to persistently store and retrieve data of arbitrary types
     * using predefined `Preferences.Key` objects. Data retrieval and saving are facilitated using flows
     * and suspend functions respectively. Common operations include saving and fetching scalar values
     * like strings, integers, booleans, or other supported types.
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name ="settings")

    /**
     * Saves a preference key-value pair to the Jetpack DataStore using the Preferences API.
     *
     * This function persists the provided value associated with the given key in the DataStore.
     * The operation is asynchronous and can be called within a coroutine or another suspend function.
     *
     * @param key The key associated with the preference. It is an instance of `Preferences.Key<T>`
     *            and determines where the value is stored in the DataStore.
     * @param value The value to be saved, associated with the provided key. The type of the value
     *              must match the type of the key (`T`).
     */
    suspend fun <T> savePreference(key: Preferences.Key<T>, value:T) {
        context.dataStore.edit { prefs->
            prefs[key] = value
        }
    }

    /**
     * Retrieves the preference value associated with the given key from the Jetpack DataStore.
     *
     * This function returns a Flow that emits the preference value whenever it changes in the DataStore.
     * If the key does not exist, the Flow emits `null`.
     *
     * @param key The key associated with the preference. It is an instance of `Preferences.Key<T>`,
     *            which identifies the specific preference value to retrieve.
     * @return A Flow that emits the value of the preference associated with the key, or `null` if the key does not exist.
     */
    fun <T> getPreference(key: Preferences.Key<T>): Flow<T?> {
        return  context.dataStore.data.map { prefs-> prefs[key] }

    }

    /**
     * Retrieves the value of the preference associated with the given key. If the key does not exist
     * in the DataStore, returns the specified default value.
     *
     * @param key The key associated with the preference. It is an instance of `Preferences.Key<T>`,
     *            which identifies the specific preference value to retrieve.
     * @param default The default value to return if the key does not exist or the value is null.
     * @return The preference value associated with the given key, or the default value if the key
     *         does not exist or the value is null.
     */
    suspend fun <T> getPreferenceValue(key: Preferences.Key<T>, default: T) : T {
        return getPreference(key).first() ?:default
    }

}


/**
 * Object that defines keys for accessing stored preferences in a DataStore.
 *
 * These keys are used to uniquely identify preference data stored as part of
 * the application’s persistent storage. They facilitate interaction with
 * `DataStore<Preferences>` instances, providing a type-safe way of reading
 * and writing preferences for boolean, string, and integer data types.
 *
 * The preferences identified by these keys can be used for various purposes,
 * such as saving user-specific settings or application-wide configuration values.
 */
object Keys {
    val KEY_BOOLEAN = booleanPreferencesKey("key_boolean")
    val KEY_STRING = stringPreferencesKey("key_string")
    val KEY_INT = intPreferencesKey("key_int")
}