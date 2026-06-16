package com.apero.composa.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apero.composa.ui.components.BottomNavigationBar
import com.apero.composa.ui.components.BottomNavigationBarDefaults
import com.apero.composa.ui.components.BottomNavigationBarItem
import com.apero.composa.ui.components.demoBottomNavigationItems

@Composable
fun BottomNavigationDemoScreen(onBack: () -> Unit) {
    val items = remember { demoBottomNavigationItems() }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val selectedContent = DemoContent[selectedIndex]

    Scaffold(
        containerColor = BottomNavigationBarDefaults.ScreenBackgroundColor,
        bottomBar = {
            BottomNavigationBar(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
        ) {
            TextButton(onClick = onBack) {
                Text("Back to Gallery", color = BottomNavigationBarDefaults.ContentColor)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Sliding Pill Bottom Bar",
                style = MaterialTheme.typography.headlineMedium,
                color = BottomNavigationBarDefaults.ContentColor,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tap each tab to validate the centered 300ms indicator motion, ripple-free touch behavior, and consistent charcoal icon and label styling.",
                style = MaterialTheme.typography.bodyMedium,
                color = BottomNavigationBarDefaults.ContentColor.copy(alpha = 0.78f),
            )

            Spacer(Modifier.height(28.dp))

            BottomNavigationPreviewCard(
                item = items[selectedIndex],
                content = selectedContent,
            )

            Spacer(Modifier.height(18.dp))

            selectedContent.details.forEach { detail ->
                PreviewDetailRow(detail = detail)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun BottomNavigationPreviewCard(
    item: BottomNavigationBarItem,
    content: BottomNavigationDemoContent,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        color = BottomNavigationBarDefaults.IndicatorBackgroundColor,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                item.icon(
                    Modifier.size(34.dp),
                    BottomNavigationBarDefaults.ContentColor,
                )
            }

            Text(
                text = item.label,
                style = MaterialTheme.typography.labelLarge,
                color = BottomNavigationBarDefaults.ContentColor.copy(alpha = 0.72f),
            )
            Text(
                text = content.title,
                style = MaterialTheme.typography.headlineSmall,
                color = BottomNavigationBarDefaults.ContentColor,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = content.description,
                style = MaterialTheme.typography.bodyLarge,
                color = BottomNavigationBarDefaults.ContentColor.copy(alpha = 0.82f),
            )
        }
    }
}

@Composable
private fun PreviewDetailRow(detail: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = BottomNavigationBarDefaults.IndicatorBorderColor,
                        shape = CircleShape,
                    ),
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyLarge,
                color = BottomNavigationBarDefaults.ContentColor,
                textAlign = TextAlign.Start,
            )
        }
    }
}
