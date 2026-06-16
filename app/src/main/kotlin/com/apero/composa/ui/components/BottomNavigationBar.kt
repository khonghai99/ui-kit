package com.apero.composa.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavigationBarItem(
    val label: String,
    val icon: @Composable (Modifier, Color) -> Unit,
)

object BottomNavigationBarDefaults {
    val NavbarBackgroundColor: Color = Color(0xFFFFFFFF)
    val ScreenBackgroundColor: Color = Color(0xFFF5F5F3)
    val ContentColor: Color = Color(0xFF2D3142)
    val IndicatorBackgroundColor: Color = Color(0xFFE2EBF5)
    val IndicatorBorderColor: Color = Color(0xFFB8D2EB)
    val IndicatorWidth: Dp = 100.dp
    val IndicatorHeight: Dp = 54.dp
    val IndicatorShape = RoundedCornerShape(27.dp)
}

private val PillSlideEasing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
private const val IndicatorMotionDurationMs = 300

@Composable
fun BottomNavigationBar(
    items: List<BottomNavigationBarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    indicatorWidth: Dp = BottomNavigationBarDefaults.IndicatorWidth,
    indicatorHeight: Dp = BottomNavigationBarDefaults.IndicatorHeight,
    barHeight: Dp = 84.dp,
) {
    if (items.isEmpty()) return

    val clampedIndex = selectedIndex.coerceIn(0, items.lastIndex)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(BottomNavigationBarDefaults.NavbarBackgroundColor)
            .navigationBarsPadding(),
    ) {
        val tabWidth = maxWidth / items.size
        val targetOffset = (tabWidth * clampedIndex.toFloat()) + ((tabWidth - indicatorWidth) / 2f)
        val indicatorTransition = updateTransition(
            targetState = targetOffset,
            label = "bottomNavIndicatorTransition",
        )
        val animatedOffset = indicatorTransition.animateDp(
            transitionSpec = {
                tween(durationMillis = IndicatorMotionDurationMs, easing = PillSlideEasing)
            },
            label = "bottomNavIndicatorOffset",
        ) { offset -> offset }
        val animatedAlpha = indicatorTransition.animateFloat(
            transitionSpec = {
                keyframes {
                    durationMillis = IndicatorMotionDurationMs
                    0f at 100 using PillSlideEasing
                    1f at IndicatorMotionDurationMs
                }
            },
            label = "bottomNavIndicatorAlpha",
        )
        { 1f }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = animatedOffset.value)
                    .size(width = indicatorWidth, height = indicatorHeight)
                    .alpha(animatedAlpha.value)
                    .background(
                        color = BottomNavigationBarDefaults.IndicatorBackgroundColor,
                        shape = BottomNavigationBarDefaults.IndicatorShape,
                    )
                    .border(
                        width = 1.5.dp,
                        color = BottomNavigationBarDefaults.IndicatorBorderColor,
                        shape = BottomNavigationBarDefaults.IndicatorShape,
                    ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, item ->
                    BottomNavigationBarItemCell(
                        item = item,
                        index = index,
                        selected = index == clampedIndex,
                        onItemSelected = onItemSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavigationBarItemCell(
    item: BottomNavigationBarItem,
    index: Int,
    selected: Boolean,
    onItemSelected: (Int) -> Unit,
) {
    val interactionSource = remember(index) { MutableInteractionSource() }
    val selectedScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = tween(durationMillis = 220, easing = PillSlideEasing),
        label = "bottomNavItemScale",
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onItemSelected(index) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier.graphicsLayer {
                scaleX = selectedScale
                scaleY = selectedScale
            },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item.icon(
                Modifier.size(24.dp),
                BottomNavigationBarDefaults.ContentColor,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = item.label,
                color = BottomNavigationBarDefaults.ContentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
