package musicPlayer

import ContentPageType
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * 侧边栏
 */
@Composable
fun SideBar(currentPage: MutableState<ContentPageType>) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(Color.White)
    ) {
        Text("在线音乐")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(
                arrayListOf(
                    OnlineItem(Icons.Outlined.Recommend, "推荐"),
                    OnlineItem(Icons.Outlined.LibraryMusic, "音乐馆"),
                    OnlineItem(Icons.Outlined.VideoLibrary, "视频"),
                    OnlineItem(Icons.Outlined.Radio, "电台")
                )
            ) { _, item ->
                SideBarItem(item.icon, item.text) {
                    currentPage.value = ContentPageType.RECOMMEND
                }
            }
        }
        Text("我的音乐")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(
                arrayListOf(
                    MyItem(Icons.Outlined.Favorite, "我喜欢"),
                    MyItem(Icons.Outlined.Favorite, "我喜欢"),
                    MyItem(Icons.Outlined.Favorite, "我喜欢"),
                    MyItem(Icons.Outlined.Favorite, "我喜欢")
                )
            ) { _, item ->
                SideBarItem(item.icon, item.text) {
                    currentPage.value = ContentPageType.FAVORITE
                }
            }
        }
    }
}

data class OnlineItem(val icon: ImageVector, val text: String)
data class MyItem(val icon: ImageVector, val text: String)

/**
 * 侧边栏条目
 */
@Composable
fun SideBarItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Blue
        )
        Text(text = text)
    }
}
