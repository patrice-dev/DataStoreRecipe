package com.codelog.datastorerecipe.ui.screens.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.codelog.datastorerecipe.ui.screens.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, viewModel: SettingsViewModel = hiltViewModel()) {




    val userSettingsProtoState by viewModel.userSettingsProtoState.collectAsState()

    DisposableEffect(Unit){

        onDispose {
           // viewModel.setInitValues()
        }
    }

   LazyColumn(modifier = Modifier
       .fillMaxSize()
       .padding(16.dp)) {


       item {
           PreferenceDSItem(viewModel)

       }

       item {
           ProtoDSItem(viewModel)
       }


   }

}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}