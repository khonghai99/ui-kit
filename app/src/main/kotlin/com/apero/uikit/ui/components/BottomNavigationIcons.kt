package com.apero.uikit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

fun demoBottomNavigationItems(): List<BottomNavigationBarItem> = listOf(
    BottomNavigationBarItem(label = "Home", icon = { modifier, tint -> HomeNavigationIcon(modifier, tint) }),
    BottomNavigationBarItem(label = "Create", icon = { modifier, tint -> CreateNavigationIcon(modifier, tint) }),
    BottomNavigationBarItem(label = "Designs", icon = { modifier, tint -> DesignsNavigationIcon(modifier, tint) }),
    BottomNavigationBarItem(label = "Profile", icon = { modifier, tint -> ProfileNavigationIcon(modifier, tint) }),
)

@Composable
private fun NavigationIconCanvas(
    modifier: Modifier,
    tint: Color,
    content: DrawScope.(stroke: Stroke, tint: Color) -> Unit,
) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(
            width = size.minDimension * 0.085f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        content(stroke, tint)
    }
}

@Composable
private fun HomeNavigationIcon(modifier: Modifier, tint: Color) {
    NavigationIconCanvas(modifier = modifier, tint = tint) { stroke, color ->
        val roof = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.48f)
            lineTo(size.width * 0.5f, size.height * 0.2f)
            lineTo(size.width * 0.8f, size.height * 0.48f)
        }
        drawPath(path = roof, color = color, style = stroke)
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.28f, size.height * 0.48f),
            size = Size(size.width * 0.44f, size.height * 0.3f),
            cornerRadius = CornerRadius(size.width * 0.06f, size.width * 0.06f),
            style = stroke,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.58f),
            end = Offset(size.width * 0.5f, size.height * 0.78f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun CreateNavigationIcon(modifier: Modifier, tint: Color) {
    NavigationIconCanvas(modifier = modifier, tint = tint) { stroke, color ->
        drawCircle(
            color = color,
            radius = size.minDimension * 0.32f,
            center = center,
            style = stroke,
        )
        drawLine(
            color = color,
            start = Offset(center.x, size.height * 0.32f),
            end = Offset(center.x, size.height * 0.68f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.32f, center.y),
            end = Offset(size.width * 0.68f, center.y),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun DesignsNavigationIcon(modifier: Modifier, tint: Color) {
    NavigationIconCanvas(modifier = modifier, tint = tint) { stroke, color ->
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.18f, size.height * 0.24f),
            size = Size(size.width * 0.36f, size.height * 0.26f),
            cornerRadius = CornerRadius(size.width * 0.05f, size.width * 0.05f),
            style = stroke,
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.46f, size.height * 0.24f),
            size = Size(size.width * 0.36f, size.height * 0.26f),
            cornerRadius = CornerRadius(size.width * 0.05f, size.width * 0.05f),
            style = stroke,
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.3f, size.height * 0.54f),
            size = Size(size.width * 0.4f, size.height * 0.22f),
            cornerRadius = CornerRadius(size.width * 0.05f, size.width * 0.05f),
            style = stroke,
        )
    }
}

@Composable
private fun ProfileNavigationIcon(modifier: Modifier, tint: Color) {
    NavigationIconCanvas(modifier = modifier, tint = tint) { stroke, color ->
        drawCircle(
            color = color,
            radius = size.minDimension * 0.16f,
            center = Offset(center.x, size.height * 0.34f),
            style = stroke,
        )
        drawArc(
            color = color,
            startAngle = 205f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(size.width * 0.22f, size.height * 0.44f),
            size = Size(size.width * 0.56f, size.height * 0.36f),
            style = stroke,
        )
    }
}
