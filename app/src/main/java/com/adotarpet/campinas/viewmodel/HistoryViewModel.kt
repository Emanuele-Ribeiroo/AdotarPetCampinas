package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.repository.HistoryRepository
import kotlinx.coroutines.flow.StateFlow

class HistoryViewModel : ViewModel() {
    val entries: StateFlow<List<HistoryEntry>> = HistoryRepository.entries
}
