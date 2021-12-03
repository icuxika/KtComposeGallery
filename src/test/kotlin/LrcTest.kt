import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class LrcTest {
    @Test
    fun readLrcFile() {
        javaClass.getResource("test.lrc")?.let { url ->
            File(url.toURI()).let { file ->
                assertTrue(file.exists(), "文件读取失败")
                file.readLines().forEach { line -> println(line) }
            }
        }
    }
}