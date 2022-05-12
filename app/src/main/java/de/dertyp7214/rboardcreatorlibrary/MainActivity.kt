@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package de.dertyp7214.rboardcreatorlibrary

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import de.dertyp7214.rboardcreatorlibrary.data.Theme
import de.dertyp7214.rboardcreatorlibrary.ui.components.ThemeRecyclerView
import de.dertyp7214.rboardcreatorlibrary.ui.theme.RboardCreatorLibraryTheme
import de.dertyp7214.rboardcreatorlibrary.ui.theme.Typography
import de.dertyp7214.rboardcreatorlibrary.viewmodel.MainViewModel

private enum class Actions(val actionName: String) {
    OPEN("open in manager"), SHARE("share"), DOWNLOAD("download"), DELETE("delete")
}

class MainActivity : ComponentActivity() {
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    private var defaultThemes
        get() = preferences.getStringSet("urls", setOf()) ?: setOf()
        set(value) {
            preferences.edit {
                putStringSet("urls", value)
            }
        }

    private val viewModel by viewModels<MainViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.updateThemes(defaultThemes)

        setContent {
            RboardCreatorLibraryTheme {
                val state by viewModel.state.collectAsState()
                var openDialog by remember { mutableStateOf(false) }
                var newUrl by remember { mutableStateOf("") }
                val customTabsIntent = CustomTabsIntent.Builder().setDefaultColorSchemeParams(
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(MaterialTheme.colorScheme.background.toArgb())
                        .setSecondaryToolbarColor(MaterialTheme.colorScheme.background.toArgb())
                        .setNavigationBarColor(MaterialTheme.colorScheme.background.toArgb())
                        .build()
                ).setShowTitle(true).setInstantAppsEnabled(false).build()
                Scaffold(floatingActionButton = {
                    FloatingActionButton(onClick = {
                        openDialog = true
                        newUrl = ""
                    }) {
                        Icon(Icons.Filled.Add, "")
                    }
                }) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        var isError by remember { mutableStateOf(false) }
                        if (openDialog) AlertDialog(onDismissRequest = {
                            openDialog = false
                        }, confirmButton = {
                            Button(onClick = {
                                if (Theme.validateTheme(newUrl)) {
                                    viewModel.addTheme(newUrl)
                                    defaultThemes = viewModel.getThemes()
                                    openDialog = false
                                } else isError = true
                            }) {
                                Text(text = stringResource(android.R.string.ok))
                            }
                        }, title = { Text(text = stringResource(R.string.add_theme)) }, text = {
                            TextField(value = newUrl, singleLine = true, onValueChange = {
                                newUrl = it
                                isError = false
                            }, isError = isError, label = {
                                Text(text = stringResource(R.string.url))
                            })
                        })
                        var showMenu by remember {
                            mutableStateOf(false)
                        }
                        var selectedTheme by remember {
                            mutableStateOf<Theme?>(null)
                        }
                        ThemeRecyclerView(themes = state.themes.map { Theme.parseTheme(it) }) {
                            selectedTheme = it
                            showMenu = true
                        }
                        if (showMenu) AlertDialog(onDismissRequest = { showMenu = false },
                            confirmButton = {},
                            text = {
                                Column {
                                    Actions.values().forEach { action ->
                                        Surface(modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                showMenu = false
                                                selectedTheme?.let { theme ->
                                                    when (action) {
                                                        Actions.OPEN -> {
                                                            // TODO: implement open in manager functionality
                                                        }
                                                        Actions.DELETE -> {
                                                            viewModel.removeTheme(theme.url)
                                                            defaultThemes = viewModel.getThemes()
                                                        }
                                                        Actions.DOWNLOAD -> {
                                                            customTabsIntent.launchUrl(
                                                                this@MainActivity,
                                                                Uri.parse(theme.download)
                                                            )
                                                        }
                                                        Actions.SHARE -> {
                                                            startActivity(
                                                                Intent.createChooser(
                                                                    Intent(Intent.ACTION_SEND).apply {
                                                                        putExtra(
                                                                            Intent.EXTRA_TEXT,
                                                                            theme.url
                                                                        )
                                                                        type = "text/plain"
                                                                    }, null
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }) {
                                            Text(
                                                text = action.actionName.cap(),
                                                style = Typography.bodyLarge,
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            })
                    }
                }
            }
        }
    }
}