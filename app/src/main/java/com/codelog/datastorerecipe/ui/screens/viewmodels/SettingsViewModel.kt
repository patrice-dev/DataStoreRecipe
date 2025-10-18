package com.codelog.datastorerecipe.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codelog.datastorerecipe.data.repository.datastore.Keys.KEY_BOOLEAN
import com.codelog.datastorerecipe.data.repository.datastore.Keys.KEY_INT
import com.codelog.datastorerecipe.data.repository.datastore.Keys.KEY_STRING
import com.codelog.datastorerecipe.data.repository.datastore.PreferenceDSManager
import com.codelog.datastorerecipe.data.repository.datastore.PreferenceProtoDSManager
import com.codelog.datastorerecipe.datastore.Userpreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(private val preferenceDSManager: PreferenceDSManager,
                                            private val preferenceProtoDSManager: PreferenceProtoDSManager
) : ViewModel() {

    /**
     * Represents the state of user settings managed using protobuf-based DataStore.
     *
     * @property boolPref A boolean preference that is stored in the user settings.
     * @property strFiled A string field representing a user preference or setting.
     * @property numField An integer field representing a user preference or numerical setting.
     * @property contrastLevel The contrast level setting of the user, represented as an enum value
     *                         with a default of `Userpreferences.ContrastLevel.UNRECOGNIZED`.
     */
    data class UserSettingsProtoState (
        val boolPref : Boolean = false,
        val strFiled : String = "",
        val numField : Int = 0,
        val contrastLevel : Userpreferences.ContrastLevel = Userpreferences.ContrastLevel.UNRECOGNIZED
    )

    /**
     * Represents the state of user settings managed through preferences-based DataStore.
     *
     * @property boolPref A boolean setting, defaulting to false.
     * @property strFiled A string field for custom user input or configuration, defaulting to an empty string.
     * @property numField An integer field used for numerical preferences, defaulting to 0.
     */
    data class UserSettingsPreferenceState (
        val boolPref : Boolean = false,
        val strFiled : String = "",
        val numField : Int = 0
    )

    /**
     * A private `MutableStateFlow` that holds the current state of user settings managed
     * using protobuf-based DataStore. This acts as a reactive data holder for the
     * `UserSettingsProtoState`, which contains various user preferences such as boolean,
     * string, numeric fields, and contrast level.
     *
     * This state is mutable and is primarily used internally within the `SettingsViewModel`
     * to update and observe changes to the user's settings. For external access, use the
     * publicly exposed `userSettingsProtoState` as an immutable `StateFlow`.
     */
    private val _userSettingsProtoState = MutableStateFlow(UserSettingsProtoState())
    val userSettingsProtoState: StateFlow<UserSettingsProtoState> = _userSettingsProtoState.asStateFlow()
    private var initialSettingsProtoState = _userSettingsProtoState.value

    /**
     * Represents a private state holder for user settings managed via preferences-based DataStore.
     * It holds a mutable state flow of type `UserSettingsPreferenceState` to observe changes
     * in user preference settings in a reactive manner within the `SettingsViewModel`.
     *
     * This variable is initialized with a default `UserSettingsPreferenceState` object and can be
     * updated dynamically to reflect the current preference settings of the user.
     */
    private val _userSettingsPreferenceState = MutableStateFlow(UserSettingsPreferenceState())
    val userSettingsPreferenceState: StateFlow<UserSettingsPreferenceState> = _userSettingsPreferenceState.asStateFlow()
    private var initialSettingsPreferenceState = _userSettingsPreferenceState.value


    /**
     * A `MutableStateFlow` representing whether the current user settings proto state differs
     * from the initial settings proto state. This property tracks changes in user settings that
     * are managed using a protobuf-based `DataStore`.
     *
     * The state flow value is initialized by comparing the current value of `_userSettingsProtoState`
     * with the defined `initialSettingsPreferenceState` to determine if they are not equal.
     *
     * Observing this state allows other components to react to changes in the user settings proto
     * configuration.
     */
    private val _canSettingsProto = MutableStateFlow(_userSettingsProtoState.value != initialSettingsPreferenceState)
    val canSettingsProto: StateFlow<Boolean> =  _canSettingsProto.asStateFlow()


    /**
     * A private [MutableStateFlow] representing a boolean value that determines whether
     * the user's current settings in the preferences-based DataStore differ from the
     * defined initial settings state.
     *
     * This state is dynamically updated based on a comparison between the current
     * `UserSettingsPreferenceState` value and the predefined `initialSettingsPreferenceState`.
     * It serves as an indicator for detecting changes or modifications made in the user's
     * preference settings.
     */
    private val _canSettingsPreference = MutableStateFlow(_userSettingsPreferenceState.value != initialSettingsPreferenceState)
    val canSettingsPreference: StateFlow<Boolean> = _canSettingsPreference.asStateFlow()
    init {
        initializeUserSettings()

    }

    /**
     * Initializes user settings by retrieving and updating the state of user settings
     * managed through both Proto DataStore and Preferences DataStore.
     *
     * This method is executed inside a coroutine within the `viewModelScope`. It fetches
     * the user's boolean, string, numerical, and contrast level preferences from the
     * `PreferenceProtoDSManager` and `PreferenceDSManager`, and updates the respective
     * state objects `_userSettingsProtoState` and `_userSettingsPreferenceState`.
     *
     * State updates:
     * - `_userSettingsProtoState` is updated with data retrieved from `PreferenceProtoDSManager`.
     * - `_userSettingsPreferenceState` is updated with data retrieved from `PreferenceDSManager`.
     *
     * The function ensures that the UI or other components observing the
     * `userSettingsProtoState` and `userSettingsPreferenceState` are provided with the
     * latest user preference data.
     */
    private fun initializeUserSettings() {
        viewModelScope.launch {

            _userSettingsProtoState.value = _userSettingsProtoState.value.copy(
                boolPref = preferenceProtoDSManager.getBoolPref(),
                strFiled = preferenceProtoDSManager.getStrField(),
                numField = preferenceProtoDSManager.getNumField(),
                contrastLevel =  preferenceProtoDSManager.getContrastLevel()
            )


            _userSettingsPreferenceState.value = _userSettingsPreferenceState.value.copy(
                boolPref = preferenceDSManager.getPreferenceValue(KEY_BOOLEAN,false),
                strFiled = preferenceDSManager.getPreferenceValue(KEY_STRING,""),
                numField = preferenceDSManager.getPreferenceValue(KEY_INT,0),
            )

            initialSettingsProtoState = _userSettingsProtoState.value
            initialSettingsPreferenceState = _userSettingsPreferenceState.value

            updateSaveProtoPreference()
            updateSavePreference()
        }



    }

    /**
     * Evaluates and updates the state of `_canSettingsProto` to indicate whether
     * the current protobuf-based user settings differ from their initial state.
     *
     * This method compares `_userSettingsProtoState` with `initialSettingsProtoState`.
     * If any changes are detected, `_canSettingsProto` is updated to `true`, signifying
     * that the settings have been modified. Otherwise, `_canSettingsProto` is set to `false`.
     *
     * This function is typically invoked after modifications to `UserSettingsProtoState`
     * (e.g., through preference updates) to ensure the UI or other components can respond
     * appropriately to changes.
     */
    fun updateSaveProtoPreference() {
        _canSettingsProto.value = _userSettingsProtoState.value != initialSettingsProtoState
    }

    /**
     * Evaluates and updates the state of `_canSettingsPreference` to determine if the
     * current preferences-based user settings differ from their initially saved state.
     *
     * This method compares `_userSettingsPreferenceState` with `initialSettingsPreferenceState`.
     * If discrepancies are found, `_canSettingsPreference` is updated to `true`, indicating
     * that the settings have been modified. Otherwise, it is set to `false`.
     *
     * It is typically invoked after modifications to `_userSettingsPreferenceState`
     * (via preference updates) or during initialization to ensure the UI or other
     * components can respond appropriately to changes in user settings.
     */
    fun updateSavePreference() {
        _canSettingsPreference.value = _userSettingsPreferenceState.value != initialSettingsPreferenceState
    }




    /**
     * Handles updates to the boolean preference managed within the protobuf-based settings state.
     *
     * This method updates the `_userSettingsProtoState` with the new boolean preference value
     * and triggers the `updateSaveProtoPreference` method to re-evaluate whether settings have changed
     * in relation to their initial state.
     *
     * @param boolPref The new boolean preference value to be updated in the settings.
     */
    fun onBoolPrefProtoChanged(boolPref: Boolean ) {
        _userSettingsProtoState.value = _userSettingsProtoState.value.copy(boolPref=boolPref)
        updateSaveProtoPreference()
    }

    /**
     * Updates the boolean preference within the preferences-based settings state and triggers
     * a re-evaluation of whether settings have changed relative to their*/
    fun onBoolPrefChanged(boolPref: Boolean ) {
        _userSettingsPreferenceState.value = _userSettingsPreferenceState.value.copy(boolPref=boolPref)
        updateSavePreference()
    }

    /**
     * Updates the string field in the protobuf-based user settings state and triggers the
     * evaluation of any changes relative to the initial state.
     *
     * @param strFiled The new string value to be updated in the user settings.
     */
    fun onStrFiledProtoChanged(strFiled: String ) {
        _userSettingsProtoState.value = _userSettingsProtoState.value.copy(strFiled=strFiled)
        updateSaveProtoPreference()
    }

    /**
     * Updates the string field in the preferences-based user settings state and triggers the
     * evaluation of any changes relative to the initial state.
     *
     * @param strFiled The new string value to be updated in the user settings.
     */
    fun onStrFiledChanged(strFiled: String ) {
        _userSettingsPreferenceState.value = _userSettingsPreferenceState.value.copy(strFiled=strFiled)
        updateSavePreference()
    }

    /**
     * Updates the numerical field in the protobuf-based user settings state and triggers
     * the evaluation of changes relative to the initial state.
     *
     * @param numField The new integer value to update in the user settings.
     */
    fun onNumFieldProtoChanged(numField: Int ) {
        _userSettingsProtoState.value = _userSettingsProtoState.value.copy(numField=numField)
        updateSaveProtoPreference()
    }

    /**
     * Updates the numerical field in the preferences-based user settings state and triggers
     * a re-evaluation of whether settings have changed relative to their initial state.
     *
     * @param numField The new integer value to be updated in the user settings.
     */
    fun onNumFieldChanged(numField: Int ) {
        _userSettingsPreferenceState.value = _userSettingsPreferenceState.value.copy(numField=numField)
        updateSavePreference()
    }

    /**
     * Updates the contrast level in the protobuf-based user settings state and triggers a
     * re-evaluation of whether settings have changed relative to their initial state.
     *
     * @param contrastLevel The new contrast level value to be updated in the user settings.
     */
    fun onContrastLevelProtoChanged(contrastLevel: Userpreferences.ContrastLevel) {
        _userSettingsProtoState.value = _userSettingsProtoState.value.copy(contrastLevel=contrastLevel)
        updateSaveProtoPreference()
    }

    /**
     * Persists the current user preference state to the Preferences DataStore.
     *
     * This method checks for changes in the user's boolean, string, and numerical preferences
     * by comparing the current state (`_userSettingsPreferenceState`) with the initial saved state
     * (`initialSettingsPreferenceState`). If any changes are detected, the corresponding
     * preferences are updated in the `preferenceDSManager`.
     *
     * The preferences are saved with specific keys:
     * - Boolean preference is saved with `KEY_BOOLEAN`.
     * - String field is saved with `KEY_STRING`.
     * - Numerical field is saved with `KEY_INT`.
     *
     * After saving the preferences, the method updates the initial state to match the current state
     * and triggers the `updateSavePreference` method to assess whether any changes still exist
     * relative to the updated initial state.
     *
     * This operation is performed asynchronously within the `viewModelScope`.
     */
    fun onSavePreference() {
        viewModelScope.launch {

            if (initialSettingsPreferenceState.boolPref != _userSettingsPreferenceState.value.boolPref)
                preferenceDSManager.savePreference( KEY_BOOLEAN, _userSettingsPreferenceState.value.boolPref )

            if(initialSettingsPreferenceState.strFiled != _userSettingsPreferenceState.value.strFiled)
                preferenceDSManager.savePreference( KEY_STRING, _userSettingsPreferenceState.value.strFiled )

            if(initialSettingsPreferenceState.numField != _userSettingsPreferenceState.value.numField)
                preferenceDSManager.savePreference( KEY_INT, _userSettingsPreferenceState.value.numField )


            initialSettingsPreferenceState = _userSettingsPreferenceState.value
            _userSettingsPreferenceState.value = initialSettingsPreferenceState

            updateSavePreference()
        }
    }
fun onSaveProtoPreference() {
    viewModelScope.launch {
        if (initialSettingsProtoState.boolPref != _userSettingsProtoState.value.boolPref)
            preferenceProtoDSManager.saveBoolPref(_userSettingsProtoState.value.boolPref)

        if(initialSettingsProtoState.strFiled != _userSettingsProtoState.value.strFiled)
            preferenceProtoDSManager.saveStrField(_userSettingsProtoState.value.strFiled)

        if(initialSettingsProtoState.numField != _userSettingsProtoState.value.numField)
            preferenceProtoDSManager.saveNumField(_userSettingsProtoState.value.numField)

        if(initialSettingsProtoState.contrastLevel != _userSettingsProtoState.value.contrastLevel)
            preferenceProtoDSManager.saveContrastLevel(_userSettingsProtoState.value.contrastLevel)

        initialSettingsProtoState = _userSettingsProtoState.value
        _userSettingsProtoState.value = initialSettingsProtoState
        updateSaveProtoPreference()
    }
}

}