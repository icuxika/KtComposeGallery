package musicPlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 歌词展示
 */
@Composable
fun Lrc(
    map: HashMap<Int, Float>,
    currentIndex: Int,
    load: suspend () -> List<String>
) {
    val lrcList: List<String>? by produceState<List<String>?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                load()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    lrcList?.let { list ->
        Column {
            list.forEachIndexed { index, s ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(color = Color(200, 0, 0, 20))
                        .padding(start = 10.dp)
                        .onGloballyPositioned { layoutCoordinates ->
                            map[index] = layoutCoordinates.positionInParent().y
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = if (currentIndex == index) Color.Green else Color.Red,
                                    fontSize = if (currentIndex == index) 16.sp else 12.sp,
                                    fontWeight = if (currentIndex == index) FontWeight.Bold else FontWeight.Thin
                                )
                            ) {
                                append(s)
                            }
                        }
                    )
                }
            }
        }
    }
}
