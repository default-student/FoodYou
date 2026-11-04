package com.maksimowiczm.foodyou.app.ui.home.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.app.ui.home.shared.FoodYouHomeCard
import com.maksimowiczm.foodyou.app.ui.home.shared.HomeState
import com.maksimowiczm.foodyou.common.compose.extension.toDp
import com.maksimowiczm.foodyou.settings.domain.entity.NutrientsOrder
import com.maksimowiczm.foodyou.settings.domain.entity.GoalCompanion
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import com.maksimowiczm.foodyou.app.generated.resources.Res as AppRes
import com.maksimowiczm.foodyou.app.generated.resources.alpaca_a_bit_hungry
import com.maksimowiczm.foodyou.app.generated.resources.alpaca_hungry
import com.maksimowiczm.foodyou.app.generated.resources.alpaca_muscular
import com.maksimowiczm.foodyou.app.generated.resources.alpaca_on_target
import com.maksimowiczm.foodyou.app.generated.resources.alpaca_slightly_over
import com.maksimowiczm.foodyou.app.generated.resources.alpaca_well_over
import com.maksimowiczm.foodyou.app.generated.resources.turtle_hungry
import com.maksimowiczm.foodyou.app.generated.resources.turtle_a_bit_hungry
import com.maksimowiczm.foodyou.app.generated.resources.turtle_on_target
import com.maksimowiczm.foodyou.app.generated.resources.turtle_slightly_over
import com.maksimowiczm.foodyou.app.generated.resources.turtle_well_over
import com.maksimowiczm.foodyou.app.generated.resources.turtle_muscular
import com.maksimowiczm.foodyou.app.generated.resources.cat_hungry
import com.maksimowiczm.foodyou.app.generated.resources.cat_a_bit_hungry
import com.maksimowiczm.foodyou.app.generated.resources.cat_on_target
import com.maksimowiczm.foodyou.app.generated.resources.cat_slightly_over
import com.maksimowiczm.foodyou.app.generated.resources.cat_well_over
import com.maksimowiczm.foodyou.app.generated.resources.cat_muscular
import com.maksimowiczm.foodyou.app.generated.resources.goose_hungry
import com.maksimowiczm.foodyou.app.generated.resources.goose_a_bit_hungry
import com.maksimowiczm.foodyou.app.generated.resources.goose_on_target
import com.maksimowiczm.foodyou.app.generated.resources.goose_slightly_over
import com.maksimowiczm.foodyou.app.generated.resources.goose_well_over
import com.maksimowiczm.foodyou.app.generated.resources.goose_muscular
import com.maksimowiczm.foodyou.app.generated.resources.duck_hungry
import com.maksimowiczm.foodyou.app.generated.resources.duck_a_bit_hungry
import com.maksimowiczm.foodyou.app.generated.resources.duck_on_target
import com.maksimowiczm.foodyou.app.generated.resources.duck_slightly_over
import com.maksimowiczm.foodyou.app.generated.resources.duck_well_over
import com.maksimowiczm.foodyou.app.generated.resources.duck_muscular
import com.maksimowiczm.foodyou.app.generated.resources.gorilla_hungry
import com.maksimowiczm.foodyou.app.generated.resources.gorilla_a_bit_hungry
import com.maksimowiczm.foodyou.app.generated.resources.gorilla_on_target
import com.maksimowiczm.foodyou.app.generated.resources.gorilla_slightly_over
import com.maksimowiczm.foodyou.app.generated.resources.gorilla_well_over
import com.maksimowiczm.foodyou.app.generated.resources.gorilla_muscular
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GoalsCard(
    homeState: HomeState,
    onClick: (epochDay: Long) -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoalsViewModel = koinViewModel(),
) {
    LaunchedEffect(homeState.selectedDate) { viewModel.setDate(homeState.selectedDate) }

    val model = viewModel.model.collectAsStateWithLifecycle().value
    val expand by viewModel.expandGoalsCard.collectAsStateWithLifecycle()
    val companion by viewModel.goalCompanion.collectAsStateWithLifecycle()

    if (model == null) {
        GoalsCardSkeleton(
            shimmer = homeState.shimmer,
            expand = expand,
            onClick = { onClick(homeState.selectedDate.toEpochDays()) },
            onLongClick = onLongClick,
            modifier = modifier,
        )
    } else {
        GoalsCard(
            expand = expand,
            energy = model.energy,
            energyGoal = model.energyGoal,
            proteins = model.proteins,
            proteinsGoal = model.proteinsGoal,
            companion = companion,
            carbohydrates = model.carbohydrates,
            carbohydratesGoal = model.carbohydratesGoal,
            fats = model.fats,
            fatsGoal = model.fatsGoal,
            onClick = { onClick(homeState.selectedDate.toEpochDays()) },
            onLongClick = onLongClick,
            modifier = modifier,
        )
    }
}

@Composable
internal fun GoalsCard(
    expand: Boolean,
    energy: Int,
    energyGoal: Int,
    proteins: Int,
    proteinsGoal: Int,
    carbohydrates: Int,
    carbohydratesGoal: Int,
    fats: Int,
    fatsGoal: Int,
    companion: GoalCompanion = GoalCompanion.Alpaca,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val proteinsPercentage = animateFloatAsState(
        targetValue = safeProgress(proteins, proteinsGoal),
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
    ).value

    val carbsPercentage = animateFloatAsState(
        targetValue = safeProgress(carbohydrates, carbohydratesGoal),
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
    ).value

    val fatsPercentage = animateFloatAsState(
        targetValue = safeProgress(fats, fatsGoal),
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
    ).value

    FoodYouHomeCard(modifier = modifier, onClick = onClick, onLongClick = onLongClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            GoalsCardContent(
                energy = energy,
                energyGoal = energyGoal,
                proteins = proteins,
                proteinsGoal = proteinsGoal,
                companion = companion,
                proteinsPercentage = proteinsPercentage,
                carbsPercentage = carbsPercentage,
                fatsPercentage = fatsPercentage,
                modifier = Modifier.fillMaxWidth(),
            )

            AnimatedVisibility(
                visible = expand,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))

                    ExpandedCardContent(
                        proteinsGrams = proteins,
                        proteinsGoalGrams = proteinsGoal,
                        carbohydratesGrams = carbohydrates,
                        carbohydratesGoalGrams = carbohydratesGoal,
                        fatsGrams = fats,
                        fatsGoalGrams = fatsGoal,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalsCardContent(
    energy: Int,
    energyGoal: Int,
    proteins: Int,
    proteinsGoal: Int,
    companion: GoalCompanion,
    proteinsPercentage: Float,
    carbsPercentage: Float,
    fatsPercentage: Float,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val nutrientsOrder = LocalNutrientsOrder.current
    val energyFormatter = LocalEnergyFormatter.current

    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val outlineColor = MaterialTheme.colorScheme.outline
    val mascotState = remember(energy, energyGoal, proteins, proteinsGoal) {
        mascotStateForDay(energy, energyGoal, proteins, proteinsGoal)
    }

    val caloriesString = buildAnnotatedString {
        withStyle(
            typography.headlineLargeEmphasized
                .merge(
                    color =
                        when {
                            energy < energyGoal -> colorScheme.onSurface
                            energy == energyGoal -> colorScheme.onSurface
                            else -> colorScheme.error
                        }
                )
                .toSpanStyle()
        ) {
            append(energyFormatter.formatEnergy(energy, withSuffix = false))
            append(" ")
        }
        withStyle(typography.bodyMedium.merge(outlineColor).toSpanStyle()) {
            val energyGoal = energyFormatter.formatEnergy(energyGoal)
            append("/ $energyGoal")
        }
    }

    val left = remember(energy, energyGoal) { energyGoal - energy }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = caloriesString, style = typography.headlineLargeEmphasized)

            when {
                left > 0 ->
                    Text(
                        text = energyFormatter.energyLeft(left),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodyMediumEmphasized,
                    )

                left == 0 ->
                    Text(
                        text = stringResource(Res.string.positive_goal_reached),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodyMediumEmphasized,
                    )

                else ->
                    Text(
                        text = energyFormatter.energyExceeded(-left),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMediumEmphasized,
                    )
            }
        }

        Row(modifier = Modifier.height(64.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            mascotState.drawableFor(companion)?.let { drawable ->
                Image(
                    painter = painterResource(drawable),
                    contentDescription = stringResource(mascotState.contentDescription),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(width = 56.dp, height = 64.dp),
                )
            }

            nutrientsOrder.forEach { field ->
                when (field) {
                    NutrientsOrder.Proteins ->
                        MacroBar(
                            progress = proteinsPercentage,
                            containerColor =
                                nutrientsPalette.proteinsOnSurfaceContainer.copy(alpha = .25f),
                            barColor = nutrientsPalette.proteinsOnSurfaceContainer,
                        )

                    NutrientsOrder.Fats ->
                        MacroBar(
                            progress = fatsPercentage,
                            containerColor =
                                nutrientsPalette.fatsOnSurfaceContainer.copy(alpha = .25f),
                            barColor = nutrientsPalette.fatsOnSurfaceContainer,
                        )

                    NutrientsOrder.Carbohydrates ->
                        MacroBar(
                            progress = carbsPercentage,
                            containerColor =
                                nutrientsPalette.carbohydratesOnSurfaceContainer.copy(alpha = .25f),
                            barColor = nutrientsPalette.carbohydratesOnSurfaceContainer,
                        )

                    NutrientsOrder.Other,
                    NutrientsOrder.Vitamins,
                    NutrientsOrder.Minerals -> Unit
                }
            }
        }
    }
}

private fun safeProgress(value: Int, goal: Int): Float =
    if (goal > 0) (value.toFloat() / goal).coerceAtLeast(0f) else 0f

private enum class GoalMascotState(
    val contentDescription: StringResource,
) {
    VERY_LOW(Res.string.alpaca_very_low),
    LOW(Res.string.alpaca_low),
    NO_GOAL(Res.string.alpaca_goal_unavailable),
    ON_TARGET(Res.string.alpaca_on_target),
    MUSCULAR(Res.string.alpaca_muscular),
    SLIGHTLY_OVER(Res.string.alpaca_slightly_over),
    WELL_OVER(Res.string.alpaca_well_over),
}

private fun GoalMascotState.drawableFor(companion: GoalCompanion): DrawableResource? =
    when (companion) {
        GoalCompanion.None -> null
        GoalCompanion.Alpaca ->
            when (this) {
                GoalMascotState.VERY_LOW -> AppRes.drawable.alpaca_hungry
                GoalMascotState.LOW -> AppRes.drawable.alpaca_a_bit_hungry
                GoalMascotState.NO_GOAL,
                GoalMascotState.ON_TARGET -> AppRes.drawable.alpaca_on_target
                GoalMascotState.MUSCULAR -> AppRes.drawable.alpaca_muscular
                GoalMascotState.SLIGHTLY_OVER -> AppRes.drawable.alpaca_slightly_over
                GoalMascotState.WELL_OVER -> AppRes.drawable.alpaca_well_over
            }
        GoalCompanion.Turtle ->
            when (this) {
                GoalMascotState.VERY_LOW -> AppRes.drawable.turtle_hungry
                GoalMascotState.LOW -> AppRes.drawable.turtle_a_bit_hungry
                GoalMascotState.NO_GOAL,
                GoalMascotState.ON_TARGET -> AppRes.drawable.turtle_on_target
                GoalMascotState.MUSCULAR -> AppRes.drawable.turtle_muscular
                GoalMascotState.SLIGHTLY_OVER -> AppRes.drawable.turtle_slightly_over
                GoalMascotState.WELL_OVER -> AppRes.drawable.turtle_well_over
            }
        GoalCompanion.Cat ->
            when (this) {
                GoalMascotState.VERY_LOW -> AppRes.drawable.cat_hungry
                GoalMascotState.LOW -> AppRes.drawable.cat_a_bit_hungry
                GoalMascotState.NO_GOAL,
                GoalMascotState.ON_TARGET -> AppRes.drawable.cat_on_target
                GoalMascotState.MUSCULAR -> AppRes.drawable.cat_muscular
                GoalMascotState.SLIGHTLY_OVER -> AppRes.drawable.cat_slightly_over
                GoalMascotState.WELL_OVER -> AppRes.drawable.cat_well_over
            }
        GoalCompanion.Goose ->
            when (this) {
                GoalMascotState.VERY_LOW -> AppRes.drawable.goose_hungry
                GoalMascotState.LOW -> AppRes.drawable.goose_a_bit_hungry
                GoalMascotState.NO_GOAL,
                GoalMascotState.ON_TARGET -> AppRes.drawable.goose_on_target
                GoalMascotState.MUSCULAR -> AppRes.drawable.goose_muscular
                GoalMascotState.SLIGHTLY_OVER -> AppRes.drawable.goose_slightly_over
                GoalMascotState.WELL_OVER -> AppRes.drawable.goose_well_over
            }
        GoalCompanion.Duck ->
            when (this) {
                GoalMascotState.VERY_LOW -> AppRes.drawable.duck_hungry
                GoalMascotState.LOW -> AppRes.drawable.duck_a_bit_hungry
                GoalMascotState.NO_GOAL,
                GoalMascotState.ON_TARGET -> AppRes.drawable.duck_on_target
                GoalMascotState.MUSCULAR -> AppRes.drawable.duck_muscular
                GoalMascotState.SLIGHTLY_OVER -> AppRes.drawable.duck_slightly_over
                GoalMascotState.WELL_OVER -> AppRes.drawable.duck_well_over
            }
        GoalCompanion.Gorilla ->
            when (this) {
                GoalMascotState.VERY_LOW -> AppRes.drawable.gorilla_hungry
                GoalMascotState.LOW -> AppRes.drawable.gorilla_a_bit_hungry
                GoalMascotState.NO_GOAL,
                GoalMascotState.ON_TARGET -> AppRes.drawable.gorilla_on_target
                GoalMascotState.MUSCULAR -> AppRes.drawable.gorilla_muscular
                GoalMascotState.SLIGHTLY_OVER -> AppRes.drawable.gorilla_slightly_over
                GoalMascotState.WELL_OVER -> AppRes.drawable.gorilla_well_over
            }
    }

private fun mascotStateForDay(
    energy: Int,
    energyGoal: Int,
    proteins: Int,
    proteinsGoal: Int,
): GoalMascotState {
    if (energyGoal <= 0) return GoalMascotState.NO_GOAL

    val energyRatio = energy.toDouble() / energyGoal
    if (energyRatio in 0.90..1.30 && proteinsGoal > 0 && proteins >= proteinsGoal) {
        return GoalMascotState.MUSCULAR
    }

    return when {
        energyRatio < 0.35 -> GoalMascotState.VERY_LOW
        energyRatio < 0.95 -> GoalMascotState.LOW
        energyRatio <= 1.05 -> GoalMascotState.ON_TARGET
        energyRatio <= 1.20 -> GoalMascotState.SLIGHTLY_OVER
        else -> GoalMascotState.WELL_OVER
    }
}

@Composable
private fun MacroBar(
    progress: Float,
    containerColor: Color,
    barColor: Color,
    modifier: Modifier = Modifier,
    overflowColor: Color = MaterialTheme.colorScheme.error,
) {
    val containerFraction = (1 - progress).coerceIn(0f, 1f)
    val overflowFraction = (progress - 1).coerceIn(0f, 1f)

    Canvas(
        modifier =
            modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp,
                    )
                )
                .fillMaxHeight()
                .width(24.dp)
    ) {
        if (overflowFraction > 0f) {
            val barHeight = 1 - overflowFraction

            drawRect(
                color = barColor,
                size = Size(width = size.width, height = size.height * barHeight - 1.dp.toPx()),
            )
            drawRect(
                color = overflowColor,
                topLeft = Offset(x = 0f, y = size.height * barHeight + 1.dp.toPx()),
                size =
                    Size(width = size.width, height = size.height * overflowFraction - 1.dp.toPx()),
            )
        } else {
            drawRect(
                color = containerColor,
                size =
                    Size(width = size.width, height = size.height * containerFraction - 1.dp.toPx()),
            )
            drawRect(
                color = barColor,
                topLeft = Offset(x = 0f, y = size.height * containerFraction + 1.dp.toPx()),
                size = Size(width = size.width, height = size.height * progress - 1.dp.toPx()),
            )
        }
    }
}

@Composable
private fun ExpandedCardContent(
    proteinsGrams: Int,
    proteinsGoalGrams: Int,
    carbohydratesGrams: Int,
    carbohydratesGoalGrams: Int,
    fatsGrams: Int,
    fatsGoalGrams: Int,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val nutrientsPalette = LocalNutrientsPalette.current
    val nutrientsOrder = LocalNutrientsOrder.current
    val gramShort = stringResource(Res.string.unit_gram_short)

    val proteinsString = buildAnnotatedString {
        val color =
            if (proteinsGrams > proteinsGoalGrams) {
                colorScheme.error
            } else {
                nutrientsPalette.proteinsOnSurfaceContainer
            }

        withStyle(typography.headlineSmall.merge(color).toSpanStyle()) {
            append(" $proteinsGrams ")
        }
        withStyle(typography.bodyMedium.merge(colorScheme.outline).toSpanStyle()) {
            append("/ $proteinsGoalGrams $gramShort")
        }
    }

    val carbohydratesString = buildAnnotatedString {
        val color =
            if (carbohydratesGrams > carbohydratesGoalGrams) {
                colorScheme.error
            } else {
                nutrientsPalette.carbohydratesOnSurfaceContainer
            }

        withStyle(typography.headlineSmall.merge(color).toSpanStyle()) {
            append(" $carbohydratesGrams ")
        }
        withStyle(typography.bodyMedium.merge(colorScheme.outline).toSpanStyle()) {
            append("/ $carbohydratesGoalGrams $gramShort")
        }
    }

    val fatsString = buildAnnotatedString {
        val color =
            if (fatsGrams > fatsGoalGrams) {
                colorScheme.error
            } else {
                nutrientsPalette.fatsOnSurfaceContainer
            }

        withStyle(typography.headlineSmall.merge(color).toSpanStyle()) { append(" $fatsGrams ") }
        withStyle(typography.bodyMedium.merge(colorScheme.outline).toSpanStyle()) {
            append("/ $fatsGoalGrams $gramShort")
        }
    }

    Column(modifier = modifier) {
        nutrientsOrder.forEach {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (it) {
                    NutrientsOrder.Proteins -> {
                        RoundedSquare(LocalNutrientsPalette.current.proteinsOnSurfaceContainer)

                        Text(
                            text = stringResource(Res.string.nutriment_proteins),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge,
                        )

                        Text(text = proteinsString, style = MaterialTheme.typography.headlineSmall)
                    }

                    NutrientsOrder.Carbohydrates -> {
                        RoundedSquare(LocalNutrientsPalette.current.carbohydratesOnSurfaceContainer)

                        Text(
                            text = stringResource(Res.string.nutriment_carbohydrates),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge,
                        )

                        Text(
                            text = carbohydratesString,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }

                    NutrientsOrder.Fats -> {
                        RoundedSquare(LocalNutrientsPalette.current.fatsOnSurfaceContainer)

                        Text(
                            text = stringResource(Res.string.nutriment_fats),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge,
                        )

                        Text(text = fatsString, style = MaterialTheme.typography.headlineSmall)
                    }

                    NutrientsOrder.Other,
                    NutrientsOrder.Vitamins,
                    NutrientsOrder.Minerals -> Unit
                }
            }
        }
    }
}

@Composable
private fun RoundedSquare(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(16.dp).clip(MaterialTheme.shapes.extraSmall)) {
        drawRect(color = color, size = size)
    }
}

@Composable
private fun GoalsCardSkeleton(
    shimmer: Shimmer,
    expand: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FoodYouHomeCard(modifier = modifier, onClick = onClick, onLongClick = onLongClick) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Spacer(
                        Modifier.shimmer(shimmer)
                            .width(60.dp)
                            .height(MaterialTheme.typography.headlineLargeEmphasized.toDp())
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )

                    Spacer(
                        Modifier.shimmer(shimmer)
                            .size(120.dp, MaterialTheme.typography.bodyMediumEmphasized.toDp())
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )
                }

                Row(
                    modifier = Modifier.height(64.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MacroBar(
                        progress = 1f,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        barColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.shimmer(shimmer),
                    )

                    MacroBar(
                        progress = 1f,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        barColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.shimmer(shimmer),
                    )

                    MacroBar(
                        progress = 1f,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        barColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.shimmer(shimmer),
                    )
                }
            }

            AnimatedVisibility(
                visible = expand,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RoundedSquare(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.shimmer(shimmer),
                        )

                        Spacer(
                            Modifier.shimmer(shimmer)
                                .width(100.dp)
                                .height(MaterialTheme.typography.labelLarge.toDp())
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )

                        Spacer(Modifier.weight(1f))

                        Spacer(
                            Modifier.shimmer(shimmer)
                                .size(80.dp, MaterialTheme.typography.headlineSmall.toDp() - 4.dp)
                                .padding(vertical = 2.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RoundedSquare(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.shimmer(shimmer),
                        )

                        Spacer(
                            Modifier.shimmer(shimmer)
                                .width(100.dp)
                                .height(MaterialTheme.typography.labelLarge.toDp())
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )

                        Spacer(Modifier.weight(1f))

                        Spacer(
                            Modifier.shimmer(shimmer)
                                .size(80.dp, MaterialTheme.typography.headlineSmall.toDp() - 4.dp)
                                .padding(vertical = 2.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RoundedSquare(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.shimmer(shimmer),
                        )

                        Spacer(
                            Modifier.shimmer(shimmer)
                                .width(100.dp)
                                .height(MaterialTheme.typography.labelLarge.toDp())
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )

                        Spacer(Modifier.weight(1f))

                        Spacer(
                            Modifier.shimmer(shimmer)
                                .size(80.dp, MaterialTheme.typography.headlineSmall.toDp() - 4.dp)
                                .padding(vertical = 2.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                    }
                }
            }
        }
    }
}
