import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import musicPlayer.Lrc
import musicPlayer.Player
import musicPlayer.SideBar
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.roundToInt

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
fun main() = application {
    val icon = painterResource("logo.png")
    val trayState = rememberTrayState()
    val notification = rememberNotification("通知", "消息")
    var isOpen by remember { mutableStateOf(true) }
    var isAskingToClose by remember { mutableStateOf(false) }
    Tray(
        icon = icon,
        state = trayState,
        menu = {
            Item("通知", onClick = {
                trayState.sendNotification(notification)
            })
            Item("退出程序", onClick = ::exitApplication)
        }
    )
    if (isOpen) {
        Window(
            onCloseRequest = { isAskingToClose = true },
            title = "Kotlin Compose Gallery",
            icon = icon
        ) {
            if (isAskingToClose) {
                Dialog(
                    onCloseRequest = { isAskingToClose = false },
                    title = "不保存关闭？"
                ) {
                    Button(
                        onClick = { isOpen = false }
                    ) {
                        Text("是")
                    }
                }
            }
            MenuBar {
                Menu("文件") {
                    Item("复制") {}
                    Item("黏贴") {}
                }
            }
            Root()
        }
    }
}

@Composable
fun Root() {
    // 当前激活内容页
    val currentPage = remember { mutableStateOf(ContentPageType.RECOMMEND) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.primary)
    ) {
        Row {
            SideBar(currentPage)
            Column {
                Content(modifier = Modifier.weight(1f), currentPage)
                Player()
            }
        }
    }
}

enum class ContentPageType {
    RECOMMEND, FAVORITE
}

@Composable
fun Content(modifier: Modifier, currentPage: MutableState<ContentPageType>) {
    Column(modifier = modifier) {
        Crossfade(targetState = currentPage) { currentPage ->
            when (currentPage.value) {
                ContentPageType.RECOMMEND -> ContentRecommend()
                ContentPageType.FAVORITE -> Text("喜欢内容")
            }
        }
    }
}

/**
 * 推荐页
 */
@Composable
fun ContentRecommend() {
    var boxHeight by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color(180, 180, 180))
            .padding(10.dp)
            .onSizeChanged { intSize ->
                boxHeight = intSize.height
            }
    ) {
        val stateVertical = rememberScrollState(0)
        val stateHorizontal = rememberScrollState(0)
        val map = HashMap<Int, Float>()
        var currentIndex by remember { mutableStateOf(0) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(stateVertical)
                .padding(end = 12.dp, bottom = 12.dp)
                .horizontalScroll(stateHorizontal)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                (1 until map.size).forEach { value ->
                                    delay(1000)
                                    currentIndex = value
                                    stateVertical.scrollTo((map[value]?.roundToInt() ?: 0) - boxHeight / 2)
                                }
                            }
                        }
                    ) {
                        Text("滚动")
                    }
                }
                Lrc(map, currentIndex) {
                    javaClass.getResourceAsStream("/なきむし.lrc")?.let { inputStream ->
                        return@Lrc BufferedReader(InputStreamReader(inputStream)).readLines()
                    }
                    emptyList()
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(stateVertical)
        )
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(end = 12.dp),
            adapter = rememberScrollbarAdapter(stateHorizontal)
        )
    }
}


