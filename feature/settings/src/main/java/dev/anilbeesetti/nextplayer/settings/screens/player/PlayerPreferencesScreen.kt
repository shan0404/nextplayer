package dev.anilbeesetti.nextplayer.settings.screens.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.anilbeesetti.nextplayer.core.model.DoubleTapGesture
import dev.anilbeesetti.nextplayer.core.model.FastSeek
import dev.anilbeesetti.nextplayer.core.model.Resume
import dev.anilbeesetti.nextplayer.core.ui.R
import dev.anilbeesetti.nextplayer.core.ui.components.ClickablePreferenceItem
import dev.anilbeesetti.nextplayer.core.ui.components.NextTopAppBar
import dev.anilbeesetti.nextplayer.core.ui.components.PreferenceSwitch
import dev.anilbeesetti.nextplayer.core.ui.components.PreferenceSwitchWithDivider
import dev.anilbeesetti.nextplayer.core.ui.components.RadioTextButton
import dev.anilbeesetti.nextplayer.core.ui.designsystem.NextIcons
import dev.anilbeesetti.nextplayer.settings.composables.OptionsDialog
import dev.anilbeesetti.nextplayer.settings.composables.PreferenceSubtitle
import dev.anilbeesetti.nextplayer.settings.extensions.name
import java.lang.Exception
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerPreferencesScreen(
    onNavigateUp: () -> Unit,
    viewModel: PlayerPreferencesViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val languages = remember { listOf(Pair("None", "")) + getLanguages() }

    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            NextTopAppBar(
                title = stringResource(id = R.string.player_name),
                scrollBehavior = scrollBehaviour,
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = NextIcons.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.interface_name))
                }
                seekGestureSetting(
                    isChecked = preferences.useSeekControls,
                    onClick = viewModel::toggleSeekControls
                )
                swipeGestureSetting(
                    isChecked = preferences.useSwipeControls,
                    onClick = viewModel::toggleSwipeControls
                )
                doubleTapGestureSetting(
                    isChecked = (preferences.doubleTapGesture != DoubleTapGesture.NONE),
                    onChecked = viewModel::toggleDoubleTapGesture,
                    onClick = { viewModel.showDialog(PlayerPreferenceDialog.DoubleTapDialog) }
                )
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.playback))
                }
                resumeSetting(
                    onClick = { viewModel.showDialog(PlayerPreferenceDialog.ResumeDialog) }
                )
                rememberBrightnessSetting(
                    isChecked = preferences.rememberPlayerBrightness,
                    onClick = viewModel::toggleRememberBrightnessLevel
                )
                rememberSelectionsSetting(
                    isChecked = preferences.rememberSelections,
                    onClick = viewModel::toggleRememberSelections
                )
                fastSeekSetting(
                    isChecked = (preferences.fastSeek != FastSeek.DISABLE),
                    onChecked = viewModel::toggleFastSeek,
                    onClick = { viewModel.showDialog(PlayerPreferenceDialog.FastSeekDialog) }
                )
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.audio))
                }
                preferredAudioLanguageSetting(
                    onClick = { viewModel.showDialog(PlayerPreferenceDialog.AudioLanguageDialog) }
                )
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.subtitle))
                }
                preferredSubtitleLanguageSetting(
                    onClick = { viewModel.showDialog(PlayerPreferenceDialog.SubtitleLanguageDialog) }
                )
            }

            when (uiState.showDialog) {
                PlayerPreferenceDialog.ResumeDialog -> {
                    OptionsDialog(
                        text = stringResource(id = R.string.resume),
                        onDismissClick = viewModel::hideDialog
                    ) {
                        items(Resume.values()) {
                            RadioTextButton(
                                text = it.name(),
                                selected = (it == preferences.resume),
                                onClick = {
                                    viewModel.updatePlaybackResume(it)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }

                PlayerPreferenceDialog.DoubleTapDialog -> {
                    OptionsDialog(
                        text = stringResource(id = R.string.double_tap),
                        onDismissClick = viewModel::hideDialog
                    ) {
                        items(DoubleTapGesture.values()) {
                            RadioTextButton(
                                text = it.name(),
                                selected = (it == preferences.doubleTapGesture),
                                onClick = {
                                    viewModel.updateDoubleTapGesture(it)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }

                PlayerPreferenceDialog.FastSeekDialog -> {
                    OptionsDialog(
                        text = stringResource(id = R.string.fast_seek),
                        onDismissClick = viewModel::hideDialog
                    ) {
                        items(FastSeek.values()) {
                            RadioTextButton(
                                text = it.name(),
                                selected = (it == preferences.fastSeek),
                                onClick = {
                                    viewModel.updateFastSeek(it)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }

                PlayerPreferenceDialog.AudioLanguageDialog -> {
                    OptionsDialog(
                        text = stringResource(id = R.string.preferred_audio_lang),
                        onDismissClick = viewModel::hideDialog
                    ) {
                        items(languages) {
                            RadioTextButton(
                                text = it.first,
                                selected = it.second == preferences.preferredAudioLanguage,
                                onClick = {
                                    viewModel.updateAudioLanguage(it.second)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }

                PlayerPreferenceDialog.SubtitleLanguageDialog -> {
                    OptionsDialog(
                        text = stringResource(id = R.string.preferred_subtitle_lang),
                        onDismissClick = viewModel::hideDialog
                    ) {
                        items(languages) {
                            RadioTextButton(
                                text = it.first,
                                selected = it.second == preferences.preferredSubtitleLanguage,
                                onClick = {
                                    viewModel.updateSubtitleLanguage(it.second)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }

                PlayerPreferenceDialog.None -> { /* Do nothing */ }
            }
        }
    }
}

fun LazyListScope.seekGestureSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    item {
        PreferenceSwitch(
            title = stringResource(id = R.string.seek_gesture),
            description = stringResource(id = R.string.seek_gesture_description),
            icon = NextIcons.SwipeHorizontal,
            isChecked = isChecked,
            onClick = onClick
        )
    }
}

fun LazyListScope.swipeGestureSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    item {
        PreferenceSwitch(
            title = stringResource(id = R.string.swipe_gesture),
            description = stringResource(id = R.string.swipe_gesture_description),
            icon = NextIcons.SwipeVertical,
            isChecked = isChecked,
            onClick = onClick
        )
    }
}

fun LazyListScope.doubleTapGestureSetting(
    isChecked: Boolean,
    onChecked: () -> Unit,
    onClick: () -> Unit
) {
    item {
        PreferenceSwitchWithDivider(
            title = stringResource(id = R.string.double_tap),
            description = stringResource(id = R.string.double_tap_description),
            isChecked = isChecked,
            onChecked = onChecked,
            icon = NextIcons.DoubleTap,
            onClick = onClick
        )
    }
}

fun LazyListScope.resumeSetting(
    onClick: () -> Unit
) {
    item {
        ClickablePreferenceItem(
            title = stringResource(id = R.string.resume),
            description = stringResource(id = R.string.resume_description),
            icon = NextIcons.Resume,
            onClick = onClick
        )
    }
}

fun LazyListScope.rememberBrightnessSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    item {
        PreferenceSwitch(
            title = stringResource(id = R.string.remember_brightness_level),
            description = stringResource(
                id = R.string.remember_brightness_level_description
            ),
            icon = NextIcons.Brightness,
            isChecked = isChecked,
            onClick = onClick
        )
    }
}

fun LazyListScope.rememberSelectionsSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    item {
        PreferenceSwitch(
            title = stringResource(id = R.string.remember_selections),
            description = stringResource(id = R.string.remember_selections_description),
            icon = NextIcons.Selection,
            isChecked = isChecked,
            onClick = onClick
        )
    }
}

fun LazyListScope.fastSeekSetting(
    isChecked: Boolean,
    onChecked: () -> Unit,
    onClick: () -> Unit
) {
    item {
        PreferenceSwitchWithDivider(
            title = stringResource(id = R.string.fast_seek),
            description = stringResource(id = R.string.fast_seek_description),
            isChecked = isChecked,
            onChecked = onChecked,
            icon = NextIcons.Fast,
            onClick = onClick
        )
    }
}

fun LazyListScope.preferredAudioLanguageSetting(
    onClick: () -> Unit
) {
    item {
        ClickablePreferenceItem(
            title = stringResource(id = R.string.preferred_audio_lang),
            description = stringResource(id = R.string.preferred_audio_lang_description),
            icon = NextIcons.AudioTrack,
            onClick = onClick
        )
    }
}

fun LazyListScope.preferredSubtitleLanguageSetting(
    onClick: () -> Unit
) {
    item {
        ClickablePreferenceItem(
            title = stringResource(id = R.string.preferred_subtitle_lang),
            description = stringResource(
                id = R.string.preferred_subtitle_lang_description
            ),
            icon = NextIcons.Subtitle,
            onClick = onClick
        )
    }
}

fun getLanguages(): List<Pair<String, String>> {
    return try {
        Locale.getAvailableLocales().map {
            val key = it.isO3Language
            val language = it.displayLanguage
            Pair(language, key)
        }.distinctBy { it.second }.sortedBy { it.first }
    } catch (e: Exception) {
        e.printStackTrace()
        listOf()
    }
}
