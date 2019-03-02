package com.a10miaomiao.bilimiao.ui.cover

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import com.a10miaomiao.bilimiao.entity.ResultInfo
import com.a10miaomiao.bilimiao.entity.ResultInfo2
import com.a10miaomiao.bilimiao.entity.SeasonEpisode
import com.a10miaomiao.bilimiao.entity.VideoInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.DebugMiao
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.math.acos

class CoverViewModel(val activity: Activity, val type: String, val id: String) : ViewModel() {

    var coverBitmap = MutableLiveData<Bitmap>()
    var title = MutableLiveData<String>()
    var loading = MutableLiveData<Boolean>()

    init {
        loading.value = true
        loadData()
    }

    private fun loadData() {
        when (type){
            "AV" -> loadAvData()
            "SS" -> loadSsData()
            "EP" -> loadEpData()
            "ROOM" -> loadRoomData()
            "CV" -> loadCvData()
            "AU" -> loadAuData()
        }
    }

    // 普通视频
    private fun loadAvData() {
        val url = BiliApiService.getVideoInfo(id)
        MiaoHttp.getJson<ResultInfo<VideoInfo>>(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ res ->
                    if (res.code == 0) {
                        val data = res.data
                        title.value = data.title
                        loadCover(data.pic)
                    } else {

                    }
                }, { err ->
                    err.printStackTrace()
                })
    }

    // 番剧
    private fun loadSsData() {

    }

    // 番剧剧集
    private fun loadEpData() {
        val url = BiliApiService.getSeasonEpisodeInfo(id)
        MiaoHttp.getJson<ResultInfo2<SeasonEpisode>>(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ res ->
                    if (res.code == 0) {
                        val data = res.result

                        val ep = data.episodes.find { it.id == id }
                        if (ep == null){
                            title.value = data.title
                            loadCover(data.cover)
                        }else{
                            title.value = ep.long_title
                            loadCover(ep.cover)
                        }
                    } else {

                    }
                }, { err ->
                    err.printStackTrace()
                })
    }

    // 直播间
    private fun loadRoomData() {

    }

    // 专栏
    private fun loadCvData() {

    }

    // 音频
    private fun loadAuData() {

    }

    private fun loadCover(pic: String) {
        var newPic = pic.replace("http://", "https://")
        Glide.with(activity)
                .load(newPic)
                .asBitmap()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                        loading.value = false
                        coverBitmap.value = resource
                    }
                })
    }

}