import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// ===== BottomBar chính =====
@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem(Icons.Default.Home, "Home", "home", currentRoute) {
            if (currentRoute != "home") navController.navigate("home") {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.startDestinationId) { saveState = true }
            }
        }

        BottomBarItem(Icons.Default.Notifications, "Notifications", "notification", currentRoute) {
            if (currentRoute != "notification") navController.navigate("notification") {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.startDestinationId) { saveState = true }
            }
        }

        BottomBarItem(Icons.Default.Person, "Account", "account", currentRoute) {
            if (currentRoute != "account") navController.navigate("account") {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.startDestinationId) { saveState = true }
            }
        }
    }
}

@Composable
fun BottomBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    routeName: String,
    currentRoute: String?,
    onClick: () -> Unit
) {
    val color = if (currentRoute == routeName) Color(0xFF6200EE) else Color.LightGray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
        // Nếu muốn hiển thị label dưới icon:
        // Text(text = label, fontSize = 10.sp, color = color)
    }
}

