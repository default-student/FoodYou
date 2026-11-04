package com.maksimowiczm.foodyou.app.ui.home.goals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.settings.domain.entity.GoalCompanion
import com.maksimowiczm.foodyou.app.generated.resources.Res as AppRes
import com.maksimowiczm.foodyou.app.generated.resources.alpaca_on_target
import com.maksimowiczm.foodyou.app.generated.resources.turtle_on_target
import com.maksimowiczm.foodyou.app.generated.resources.cat_on_target
import com.maksimowiczm.foodyou.app.generated.resources.goose_on_target
import com.maksimowiczm.foodyou.app.generated.resources.duck_on_target
import com.maksimowiczm.foodyou.app.generated.resources.gorilla_on_target
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GoalsCardSettings(
    onBack: () -> Unit,
    onGoalsSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: GoalsViewModel = koinViewModel()
    val expand by viewModel.expandGoalsCard.collectAsStateWithLifecycle()
    val companion by viewModel.goalCompanion.collectAsStateWithLifecycle()

    GoalsCardSettings(
        onBack = onBack,
        expand = expand,
        companion = companion,
        onCompanionChange = viewModel::setGoalCompanion,
        onShowDetailsChange = viewModel::setExpandGoalsCard,
        onGoalsSettings = onGoalsSettings,
        modifier = modifier,
    )
}

@Composable
private fun GoalsCardSettings(
    onBack: () -> Unit,
    onGoalsSettings: () -> Unit,
    onShowDetailsChange: (Boolean) -> Unit,
    expand: Boolean,
    companion: GoalCompanion,
    onCompanionChange: (GoalCompanion) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_daily_goals)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            stickyHeader {
                GoalsCard(
                    expand = expand,
                    energy = 1600,
                    energyGoal = 2000,
                    proteins = 50,
                    proteinsGoal = 75,
                    carbohydrates = 200,
                    carbohydratesGoal = 300,
                    fats = 70,
                    fatsGoal = 90,
                    companion = companion,
                    onClick = {},
                    onLongClick = {},
                    modifier = Modifier.padding(16.dp),
                )
            }

            item { HorizontalDivider() }

            item {
                GoalCompanionPicker(
                    selected = companion,
                    onSelect = onCompanionChange,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                )
            }

            item { HorizontalDivider() }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.action_show_details)) },
                    modifier = Modifier.clickable { onShowDetailsChange(!expand) },
                    supportingContent = {
                        Text(stringResource(Res.string.description_show_macronutrients_goals))
                    },
                    trailingContent = {
                        Switch(checked = expand, onCheckedChange = onShowDetailsChange)
                    },
                )
            }

            item { HorizontalDivider() }

            item {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.headline_daily_goals_settings))
                    },
                    modifier = Modifier.clickable { onGoalsSettings() },
                )
            }
        }
    }
}

@Composable
private fun GoalCompanionPicker(
    selected: GoalCompanion,
    onSelect: (GoalCompanion) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(Res.string.headline_goal_companion), style = MaterialTheme.typography.titleMedium)
        Text(
            stringResource(Res.string.description_goal_companion),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
        GoalCompanion.entries.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { companion ->
                    GoalCompanionOption(
                        companion = companion,
                        selected = companion == selected,
                        onClick = { onSelect(companion) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GoalCompanionOption(
    companion: GoalCompanion,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.selectable(
            selected = selected,
            onClick = onClick,
            role = Role.RadioButton,
        ),
        shape = MaterialTheme.shapes.medium,
        color =
            if (selected) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceContainer,
        contentColor =
            if (selected) MaterialTheme.colorScheme.onSecondaryContainer
            else MaterialTheme.colorScheme.onSurface,
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(104.dp).padding(8.dp)) {
            val preview = companion.previewDrawable()
            if (preview != null) {
                Image(
                    painter = painterResource(preview),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.TopCenter).size(58.dp),
                )
            } else {
                Text(
                    text = "∅",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
            RadioButton(
                selected = selected,
                onClick = null,
                modifier = Modifier.align(Alignment.TopEnd).size(24.dp),
            )
            Text(
                text = stringResource(companion.label()),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

private fun GoalCompanion.previewDrawable(): DrawableResource? =
    when (this) {
        GoalCompanion.Alpaca -> AppRes.drawable.alpaca_on_target
        GoalCompanion.Turtle -> AppRes.drawable.turtle_on_target
        GoalCompanion.Cat -> AppRes.drawable.cat_on_target
        GoalCompanion.Goose -> AppRes.drawable.goose_on_target
        GoalCompanion.Duck -> AppRes.drawable.duck_on_target
        GoalCompanion.Gorilla -> AppRes.drawable.gorilla_on_target
        GoalCompanion.None -> null
    }

private fun GoalCompanion.label() =
    when (this) {
        GoalCompanion.Alpaca -> Res.string.goal_companion_alpaca
        GoalCompanion.Turtle -> Res.string.goal_companion_turtle
        GoalCompanion.Cat -> Res.string.goal_companion_cat
        GoalCompanion.Goose -> Res.string.goal_companion_goose
        GoalCompanion.Duck -> Res.string.goal_companion_duck
        GoalCompanion.Gorilla -> Res.string.goal_companion_gorilla
        GoalCompanion.None -> Res.string.goal_companion_none
    }
