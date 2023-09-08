package com.example.ideawhirl.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun IdeaWhirlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PoppinsTypography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun NoteTheme(
    palette: NotePalette,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> palette.darkScheme
        else -> palette.lightScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PoppinsTypography,
        shapes = Shapes,
        content = content
    )
}

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

enum class NotePalette(
    private val light: Color,
    private val lightVariant: Color,
    private val lightBackground: Color,
    private val lightOnEmphasis: Color,
    private val dark: Color,
    private val darkVariant: Color,
    private val darkBackground: Color,
    private val darkOnEmphasis: Color,
    val id: Int) {
    PALETTE_0(
        Color(0), Color(0), Color(0), Color(0), Color(0), Color(0), Color(0), Color(0),
        0),
    PALETTE_1(
        note_light_pink,
        note_light_pink_variant,
        note_light_pink_background,
        note_light_pink_on_emphasis,
        note_dark_pink,
        note_dark_pink_variant,
        note_dark_pink_background,
        note_dark_pink_on_emphasis,
        1),
    PALETTE_2(
        note_light_blue,
        note_light_blue_variant,
        note_light_blue_background,
        note_light_blue_on_emphasis,
        note_dark_blue,
        note_dark_blue_variant,
        note_dark_blue_background,
        note_dark_blue_on_emphasis,
        2),
    PALETTE_3(
        note_light_green,
        note_light_green_variant,
        note_light_green_background,
        note_light_green_on_emphasis,
        note_dark_green,
        note_dark_green_variant,
        note_dark_green_background,
        note_dark_green_on_emphasis,
        3),
    PALETTE_4(
        note_light_orange,
        note_light_orange_variant,
        note_light_orange_background,
        note_light_orange_on_emphasis,
        note_dark_orange,
        note_dark_orange_variant,
        note_dark_orange_background,
        note_dark_orange_on_emphasis,
        4),
    PALETTE_5(
        note_light_purple,
        note_light_purple_variant,
        note_light_purple_background,
        note_light_purple_on_emphasis,
        note_dark_purple,
        note_dark_purple_variant,
        note_dark_purple_background,
        note_dark_purple_on_emphasis,
        5);

    val main: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.primary
            } else if (isSystemInDarkTheme()) {
                dark
            } else {
                light
            }
        }
    val variant: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.primaryContainer
            } else if (isSystemInDarkTheme()) {
                darkVariant
            } else {
                lightVariant
            }
        }
    val background: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.background
            } else if (isSystemInDarkTheme()) {
                darkBackground
            } else {
                lightBackground
            }
        }
    val onVariant: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else if (isSystemInDarkTheme()) {
                note_dark_on_custom
            } else {
                note_light_on_custom
            }
        }
    val onBackground: Color
        @Composable
        get() {
            return MaterialTheme.colorScheme.onBackground
        }

    val onEmphasis: Color
        @Composable
        get() {
            return if (id == 0) {
                MaterialTheme.colorScheme.onBackground
            } else if (isSystemInDarkTheme()) {
                darkOnEmphasis
            } else {
                lightOnEmphasis
            }
        }

    val lightScheme: ColorScheme
        get() {
            return if (id == 0) {
                LightColorScheme
            } else {
                LightColorScheme.copy(
                    primary = light,
                    onPrimary = note_light_on_custom,
                    secondary = lightVariant,
                    onSecondary = note_light_on_custom,
                    background = lightBackground,
                    inversePrimary = lightOnEmphasis,
                )
            }
        }

    val darkScheme: ColorScheme
        get() {
            return if (id == 0) {
                DarkColorScheme
            } else {
                DarkColorScheme.copy(
                    primary = dark,
                    onPrimary = note_dark_on_custom,
                    secondary = darkVariant,
                    onSecondary = note_dark_on_custom,
                    background = darkBackground,
                    inversePrimary = darkOnEmphasis,
                )
            }
        }

    companion object {
        fun random() = values().filter { it.id != 0 }.toList().random()
        fun fromId(id: Int) = values().first { it.id == id }
    }
}