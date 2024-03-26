package com.sessac.myaitrip

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

@GlideModule
class GlideModule: AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
//        super.applyOptions(context, builder) // Default = Empty

        with(builder) {
            // 메모리 캐시
            val calculator = MemorySizeCalculator.Builder(context).setMemoryCacheScreens(2F).build()    // 최대 2개의 화면의 이미지를 캐싱한다.
            setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))

            // 비트맵 풀
            val bitmapPoolSizeBytes = 30 * 1024 * 1024
            setBitmapPool(LruBitmapPool(bitmapPoolSizeBytes.toLong()))

            // 디스크 캐시
            val diskCacheSizeBytes = 100 * 1024 * 1024
            builder.setDiskCache( InternalCacheDiskCacheFactory(context, "MyAiTripCaches", diskCacheSizeBytes.toLong()))

            // 리퀘스트 옵션 기본 값
            setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565).disallowHardwareConfig())

            // 로그 레벨
//            setLogLevel(Log.DEBUG)
            setLogLevel(Log.ERROR) // 릴리즈 단계
        }
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        // 커스터마이즈된 OkHttp 클라이언트로 Glide 설정
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}