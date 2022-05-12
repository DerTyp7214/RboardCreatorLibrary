package de.dertyp7214.rboardcreatorlibrary.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(MainState())

    val state: StateFlow<MainState>
        get() = _state.asStateFlow()

    fun updateThemes(themes: Set<String>) {
        _state.value = state.value.copy(themes = themes)
    }

    fun addTheme(theme: String) {
        _state.value = state.value.copy(themes = state.value.themes.let {
            val list = ArrayList(it)
            list.add(theme)
            list.toSet()
        })
    }

    fun removeTheme(theme: String) {
        _state.value = state.value.copy(themes = state.value.themes.let {
            val list = ArrayList(it)
            list.remove(theme)
            list.toSet()
        })
    }

    fun getThemes(): Set<String> {
        return _state.value.themes
    }
}

data class MainState(
    val themes: Set<String> = setOf()
)