package com.codelog.datastorerecipe.data.repository.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.codelog.datastorerecipe.datastore.Userpreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first

/**
 * A manager class for handling user preferences using Proto DataStore.
 *
 * This class provides functionality to save and retrieve strongly-typed user preferences
 * such as boolean, string, integer fields, and enums. The user preferences are stored
 * in a Proto DataStore file named "user_prefs.pb", utilizing a protobuf-based serialization mechanism.
 *
 * It is designed to efficiently manage user-specific customization settings persistently while providing
 * an interface for easy and straightforward operations.
 *
 * @constructor Initializes the manager with the given application context.
 * @param context The application context used for accessing the DataStore.
 */

class PreferenceProtoDSManager  ( private val context: Context) {

    /**
     * Extension property providing a `DataStore` instance configured to work with protobuf-based user preferences.
     * This property is scoped to a `Context` and utilizes the `PreferenceProtoSerializer` for serialization.
     *
     * The `userPreferencesDataStore` is used to manage persistent storage and retrieval of user-specific
     * preferences, including various fields such as boolean, string, integer, and enum types. The preferences
     * are stored in a file named "user_prefs.pb".
     *
     * - To read data, use the associated `data` property on the `DataStore` instance, which provides a flow of
     *   the `Userpreferences` object.
     * - To update data, use the `updateData` function on the `DataStore` instance with the current builder
     *   instance of `Userpreferences`.
     *
     * Example purposes include storing user customization settings, such as theme preferences or contrast levels.
     *
     * Errors during data reading are handled by emitting the default instance of `Userpreferences`.
     */
    private val Context.userPreferencesDataStore: DataStore<Userpreferences> by dataStore(
        fileName = "user_prefs.pb",
        serializer = PreferenceProtoSerializer
    )

    /**
     * A Flow that emits the user preferences data stored in the `userPreferencesDataStore`.
     *
     * This flow listens to changes in the underlying Proto DataStore and emits updated
     * `Userpreferences` objects whenever the data changes. In case of an exception during
     * data retrieval, it emits the default `Userpreferences` instance and logs the exception.
     *
     * The default instance of `Userpreferences` is provided by the `getDefaultInstance()` method.
     * Exceptions are caught and logged using `Log.e` to ensure seamless operations and handling
     * of fallback scenarios.
     */
    val userPreferencesFlow: Flow<Userpreferences> =context.userPreferencesDataStore.data
        .catch {  exception ->

            emit(Userpreferences.getDefaultInstance())
            Log.e ("Error"," PreferenceProtoDSManager::userPreferencesFlow message "+exception.message)
        }


    /**
     * Saves the given boolean preference value to the UserPreferences DataStore.
     *
     * @param boolPref The boolean value to be stored in the preferences.
     */
    suspend fun saveBoolPref(boolPref: Boolean) {
        context.userPreferencesDataStore.updateData {
            it.toBuilder().setBoolPref(boolPref).build()
        }
    }

    /**
     * Retrieves the boolean preference value from the UserPreferences DataStore.
     *
     * @return The boolean value stored in the preferences.
     */
    suspend fun getBoolPref(): Boolean {
        return context.userPreferencesDataStore.data.first().boolPref
    }

    /**
     * Saves the given string field value to the UserPreferences DataStore.
     *
     * @param strField The string value to be stored in the preferences.
     */
    suspend fun saveStrField(strField:String) {
        context.userPreferencesDataStore.updateData {
            it.toBuilder().setStrField(strField).build()
        }
    }

    /**
     * Retrieves the string field value from the UserPreferences DataStore.
     *
     * @return The string value stored in the preferences.
     */
    suspend fun getStrField() : String {
        return context.userPreferencesDataStore.data.first().strField
    }

    /**
     * Saves the given integer field value to the UserPreferences DataStore.
     *
     * @param numField The integer value to be stored in the preferences.
     */
    suspend fun saveNumField(numField:Int) {
        context.userPreferencesDataStore.updateData {
            it.toBuilder().setNumField(numField).build()
        }
    }

    /**
     * Retrieves the integer value of the numField from the UserPreferences DataStore.
     *
     * @return The integer value stored in the numField preference.
     */
    suspend fun getNumField() : Int {
        return context.userPreferencesDataStore.data.first().numField
    }

    /**
     * Saves the specified contrast level value to the UserPreferences DataStore.
     *
     * @param contrastLevel The contrast level value to be stored in the preferences.
     */
    suspend fun saveContrastLevel(contrastLevel: Userpreferences.ContrastLevel) {
        context.userPreferencesDataStore.updateData {
            it.toBuilder().setContrastLevel(contrastLevel).build()

        }
    }

    /**
     * Retrieves the contrast level preference value from the UserPreferences DataStore.
     * This function suspends while it fetches the data.
     *
     * @return The contrast level value stored in the UserPreferences DataStore.
     */
    suspend fun getContrastLevel() : Userpreferences.ContrastLevel {
        return context.userPreferencesDataStore.data.first().contrastLevel
    }



}