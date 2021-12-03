package http

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.*
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.time.Duration

inline fun <reified T : Any> api(
    url: String,
    data: Any? = null,
    fileList: List<File>? = null,
    noinline listener: ((workDone: Long, max: Long) -> Unit)? = null
): ApiExecutor<T> {
    val type = object : TypeToken<T>() {}.type
    return ApiExecutor(ApiHelper.request(url, data, fileList, listener), type)
}

object ApiHelper {

    private val gson = Gson()

    fun request(
        url: String,
        data: Any?,
        fileList: List<File>?,
        listener: ((workDone: Long, max: Long) -> Unit)?
    ): Request {
        val requestBuilder = Request
            .Builder()
            .url(url)
        if (fileList.isNullOrEmpty()) {
            requestBuilder.post(gson.toJson(data).toRequestBody("application/json; charset=utf-8".toMediaType()))
        } else {
            val multipartBody = MultipartBody
                .Builder()
                .addFormDataPart("data", gson.toJson(data))
            fileList.forEach { file ->
                multipartBody.addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("application/octet-stream".toMediaType())
                )
            }
            requestBuilder.post(ObservableRequestBody(multipartBody.build(), listener))
        }
        return requestBuilder.build()
    }
}

class ObservableRequestBody(
    private val delegate: RequestBody,
    private val listener: ((workDone: Long, max: Long) -> Unit)? = null
) : RequestBody() {

    lateinit var observableSink: ObservableSink

    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    override fun writeTo(sink: BufferedSink) {
        observableSink = ObservableSink(sink)
        val bufferedSink = observableSink.buffer()
        delegate.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    override fun contentLength(): Long {
        return try {
            delegate.contentLength()
        } catch (e: Exception) {
            -1
        }
    }

    inner class ObservableSink(delegate: Sink) : ForwardingSink(delegate) {
        private var workDone: Long = 0
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            workDone += byteCount
            listener?.invoke(workDone, contentLength())
        }
    }
}

class ApiExecutor<T> constructor(private val request: Request, private val type: Type) {

    private val onSuccessList = ArrayList<(T) -> Unit>()
    private val onFailureList = ArrayList<(String) -> Unit>()

    fun success(block: (data: T) -> Unit): ApiExecutor<T> {
        onSuccessList += block
        return this
    }

    fun failure(block: (msg: String) -> Unit): ApiExecutor<T> {
        onFailureList += block
        return this
    }

    private fun doSuccess(data: T) = onSuccessList.forEach { it.invoke(data) }

    private fun doFailure(msg: String) = onFailureList.forEach { it.invoke(msg) }

    fun execute() {
        val dataType = type as? ParameterizedType
            ?: throw RuntimeException("Type [${type.typeName}] is not a ParameterizedType")
        val actualTypeArguments = dataType.actualTypeArguments
        if (actualTypeArguments.isEmpty()) {
            throw RuntimeException("Type [${dataType.typeName}]'s actualTypeArguments is Empty")
        }
        okHttpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                resolveResponse(response, dataType)
            } else {
                doFailure("[code]: ${response.code}, [message]: ${response.message}")
            }
        }
    }

    private fun resolveResponse(response: Response, type: ParameterizedType) {
        response.body?.string()?.let { bodyString ->
            var apiData: T
            try {
                apiData = gson.fromJson(bodyString, type)
                if (apiData is ApiData<*>) {
                    apiData.code?.let { code ->
                        // 1、可以对业务状态码范围做一个校验
                        // 2、业务数据可为空
                        doSuccess(apiData)
                    } ?: kotlin.run {
                        doFailure("返回的结果不包含状态码属性[code]")
                    }
                } else {
                    doFailure("数据类型不是[ApiData]格式")
                }
            } catch (e: Exception) {
                // JSON解析异常的话，进行一次模糊解析，只解析[code]和[msg]属性
                try {
                    apiData = gson.fromJson(bodyString, object : TypeToken<ApiData<Any>>() {}.type)
                    doSuccess(apiData)
                } catch (ee: Exception) {
                    ee.message?.let { doFailure(it) } ?: doFailure("返回的结果进行反序列化时发生了错误")
                }
            }
        }
    }

    companion object {
        private val okHttpClient = OkHttpClient()
            .newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .readTimeout(Duration.ofSeconds(30))
            .writeTimeout(Duration.ofSeconds(60))
            .build()

        private val gson = Gson()
    }
}