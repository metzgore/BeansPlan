package de.metzgore.beansplan.reminders

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import de.metzgore.beansplan.R
import de.metzgore.beansplan.baseschedule.BaseFragment
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.Show
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.databinding.FragmentRemindersBinding
import de.metzgore.beansplan.shared.OnReminderButtonClickListener
import de.metzgore.beansplan.util.di.RemindersViewModelFactory
import javax.inject.Inject

class RemindersFragment : BaseFragment(), OnReminderButtonClickListener {
    private lateinit var remindersAdapter: RemindersAdapter
    private lateinit var remindersViewModel: RemindersViewModel

    @Inject
    lateinit var repo: RemindersRepository

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        remindersAdapter = RemindersAdapter(this)

        remindersViewModel = ViewModelProviders.of(this, RemindersViewModelFactory(repo)).get(RemindersViewModel::class.java)

        subscribeUi(remindersViewModel)
    }

    private fun subscribeUi(viewModel: RemindersViewModel) {
        viewModel.reminders.observe(this, Observer<List<ShowWithReminder>> {
            remindersAdapter.setShowsWithReminders(it!!)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentRemindersBinding>(inflater, R.layout.fragment_reminders, container, false)

        binding.apply {
            viewModel = viewModel
            setLifecycleOwner(this@RemindersFragment)
        }

        binding.fragmentRemindersList.apply {
            adapter = remindersAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null)
            (activity as FragmentActivity).title = getString(R.string.drawer_item_reminders)
    }

    override fun onUpsertReminder(show: Show, reminder: Reminder) {
        remindersViewModel.updateReminder(show, reminder)
    }

    override fun deleteReminder(show: Show, reminder: Reminder) {
        remindersViewModel.deleteReminder(show, reminder)
    }

    companion object {

        @JvmStatic
        fun newInstance() = RemindersFragment()
    }
}
