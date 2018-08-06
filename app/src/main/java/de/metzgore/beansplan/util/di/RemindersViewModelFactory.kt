package de.metzgore.beansplan.util.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import de.metzgore.beansplan.reminders.RemindersRepository
import de.metzgore.beansplan.reminders.RemindersViewModel

class RemindersViewModelFactory(repo: RemindersRepository) : ViewModelProvider.Factory {

    private val remindersRepo = repo

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RemindersViewModel(remindersRepo) as T
    }
}
