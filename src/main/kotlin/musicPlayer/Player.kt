package musicPlayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * 播放控制器
 */
@Composable
fun Player() {
    val playProgress by remember { mutableStateOf(0.45f) }
    Column {
        LinearProgressIndicator(
            progress = playProgress,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.onError
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.secondary)
                .padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource("logo.svg"),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Text("追光者 - 岑宁儿")
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = "Icon",
                        tint = Color.Red
                    )
                    Icon(
                        imageVector = Icons.Outlined.FileDownload,
                        contentDescription = "Icon",
                        tint = MaterialTheme.colors.onSurface
                    )
                    Icon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = "Icon",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Loop,
                    contentDescription = "Icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colors.onSurface
                )
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Icon",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colors.onSurface
                )
                Icon(
                    imageVector = Icons.Filled.PlayCircle,
                    contentDescription = "Icon",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Green
                )
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Icon",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colors.onSurface
                )
                Icon(
                    imageVector = Icons.Outlined.VolumeUp,
                    contentDescription = "Icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colors.onSurface
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("04:45/05:52")
                Icon(
                    imageVector = Icons.Default.LibraryMusic,
                    contentDescription = "Icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colors.onSurface
                )
                Icon(
                    imageVector = Icons.Default.Subject,
                    contentDescription = "Icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}
