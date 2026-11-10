package com.israrxy.gestureessentials.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.israrxy.gestureessentials.ui.components.SectionCard
import com.israrxy.gestureessentials.ui.components.StatTile
import com.israrxy.gestureessentials.ui.components.StatusCard
import com.israrxy.gestureessentials.ui.theme.GestureEssentialsTheme

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            HomeHeader()
            StatusCard(
                enabled = state.serviceEnabled,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    value = state.ruleCount.toString(),
                    label = "Rules",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = state.actionsRunToday.toString(),
                    label = "Actions run",
                    modifier = Modifier.weight(1f),
                )
            }
            RulesPreview(count = state.ruleCount, onOpen = { onAction(HomeAction.OpenRules) })
            BuildInfo(versionName = state.versionName)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HomeHeader() {
    Column {
        Text(
            text = "Gesture\nEssentials",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "One gesture. The right action. Every time.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun RulesPreview(count: Int, onOpen: () -> Unit) {
    SectionCard(title = "Rules") {
        if (count == 0) {
            Text(
                text = "No rules yet. Rule building arrives in the next build.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = "Shake, volume keys, letter drawing and more — all local, no network.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onOpen) {
            Text(text = "What's coming")
        }
    }
}

@Composable
private fun BuildInfo(versionName: String) {
    Text(
        text = "v$versionName • debug build from GitHub Actions",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    GestureEssentialsTheme(dynamicColor = false) {
        HomeScreen(state = HomeUiState(versionName = "0.1.0"), onAction = {})
    }
}
