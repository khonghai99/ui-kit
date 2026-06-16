package com.apero.composa.ui.screen

internal data class BottomNavigationDemoContent(
    val title: String,
    val description: String,
    val details: List<String>,
)

internal val DemoContent = listOf(
    BottomNavigationDemoContent(
        title = "Home dashboard",
        description = "A neutral landing surface to test the centered indicator against short labels and a familiar icon.",
        details = listOf("Recent activity", "Pinned shortcuts", "Daily summary"),
    ),
    BottomNavigationDemoContent(
        title = "Create workspace",
        description = "A focused state for new actions where the pill should glide cleanly without changing icon or text color.",
        details = listOf("Start a project", "Import assets", "Draft quick ideas"),
    ),
    BottomNavigationDemoContent(
        title = "Design library",
        description = "A content-heavy placeholder to confirm the nav bar still feels balanced when the screen body changes tone and density.",
        details = listOf("Saved systems", "Reusable templates", "Component snapshots"),
    ),
    BottomNavigationDemoContent(
        title = "Profile hub",
        description = "A personal state for testing the far-right tab alignment and the pill indicator's final resting position.",
        details = listOf("Account settings", "Recent edits", "Usage history"),
    ),
)
