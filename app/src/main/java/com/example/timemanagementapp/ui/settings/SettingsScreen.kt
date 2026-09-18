package com.example.timemanagementapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timemanagementapp.R
import com.example.timemanagementapp.ui.AppViewModelProvider
import com.example.timemanagementapp.ui.TimelyScaffold
import com.example.timemanagementapp.ui.components.settings.ReminderEditorDialog
import com.example.timemanagementapp.ui.components.settings.ReminderTimes
import com.example.timemanagementapp.ui.navigation.NavigationDest
import com.example.timemanagementapp.ui.theme.TimeManagementAppTheme

object SettingsDestination : NavigationDest {
    override val route = "settings"
    override val titleRes = R.string.settings
}

@Composable
fun SettingsScreen(
    navigateToCalendar: () -> Unit,
    navigateToAnalytics: () -> Unit,
    navigateToHome: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val settingsUiState by settingsViewModel.settingsUiState.collectAsState()

    val reminderEditorUiState by settingsViewModel.reminderEditorUiState.collectAsState()

    TimelyScaffold(
        topBarTitle = stringResource(R.string.settings),
        onHomeClick = navigateToHome,
        onCalendarClick = navigateToCalendar,
        onAnalyticsClick = navigateToAnalytics
    ){ innerPadding ->
        SettingsBody(
            settingsUiState = settingsUiState,
            reminderEditorUiState = reminderEditorUiState,
            onTaskCompletionNotificationCheckedChange = settingsViewModel::setTaskCompletionNotificationsEnabled,
            onCountdownRemindersCheckedChange = settingsViewModel::setCountdownRemindersEnabled,

            onAddReminder = settingsViewModel::openAddReminderDialog,
            openEditReminder = settingsViewModel::openEditReminderDialog,
            onDeleteReminder = settingsViewModel::deleteReminder,
            onReminderInputChanged = settingsViewModel::updateReminderInput,
            onSaveReminder = settingsViewModel::saveReminder,
            onDismissReminderDialog = settingsViewModel::closeReminderDialog,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SettingsBody(
    settingsUiState: SettingsUiState,
    reminderEditorUiState: ReminderEditorUiState,
    onTaskCompletionNotificationCheckedChange: (Boolean) -> Unit,
    onCountdownRemindersCheckedChange: (Boolean) -> Unit,

    onAddReminder: () -> Unit,
    openEditReminder: (Int) -> Unit,
    onDeleteReminder: (Int) -> Unit,
    onReminderInputChanged: (String) -> Unit,
    onSaveReminder: () -> Unit,
    onDismissReminderDialog: () -> Unit,

    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        Text(
            text = "Notification Settings",
            style = MaterialTheme.typography.headlineMedium,
        )
        SettingsSwitch(
            text = stringResource(R.string.task_completion_notifications),
            checked = settingsUiState.taskCompletionNotificationsEnabled,
            onCheckedChange = onTaskCompletionNotificationCheckedChange
        )
        SettingsSwitch(
            text = stringResource(R.string.countdown_reminders_on_off),
            checked = settingsUiState.countdownRemindersEnabled,
            onCheckedChange = onCountdownRemindersCheckedChange
        )
        if(settingsUiState.countdownRemindersEnabled){
            ReminderTimes(
                selectedMinutes = settingsUiState.countdownRemindersMinutes,
                onEditReminder = openEditReminder,
                onAddReminder = onAddReminder,
                onDeleteReminder = onDeleteReminder
            )
        }
    }

    if(reminderEditorUiState.isVisible){
        ReminderEditorDialog(
            editorUiState = reminderEditorUiState,
            onInputChanged = onReminderInputChanged,
            onSave = onSaveReminder,
            onDismiss = onDismissReminderDialog
        )
    }
}

@Composable
fun SettingsSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsBodyPreview(){
    TimeManagementAppTheme {
        SettingsBody(
            settingsUiState = SettingsUiState(
                taskCompletionNotificationsEnabled = true,
                countdownRemindersEnabled = true
            ),
            onTaskCompletionNotificationCheckedChange = {},
            onCountdownRemindersCheckedChange = {},
            onAddReminder = {},
            onSaveReminder = {},
            onDeleteReminder = {},
            onReminderInputChanged = {},
            onDismissReminderDialog = {},
            reminderEditorUiState = ReminderEditorUiState(),
            openEditReminder = {}
        )
    }
}