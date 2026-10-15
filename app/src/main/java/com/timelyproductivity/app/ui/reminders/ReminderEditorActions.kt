package com.timelyproductivity.app.ui.reminders

interface ReminderEditorActions {
    fun openAddReminderDialog()
    fun openEditReminderDialog(minutes: Int)
    fun updateReminderInput(input: String)
    fun closeReminderDialog()

    fun saveReminder()
    fun deleteReminder(minutes: Int)
}