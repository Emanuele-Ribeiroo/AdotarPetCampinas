package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.repository.HistoryRepository

class DonationViewModel : ViewModel() {
    fun registrarDoacao(valor: String) =
        HistoryRepository.add(HistoryType.DOACAO, "Doação via PIX", valor)
}
