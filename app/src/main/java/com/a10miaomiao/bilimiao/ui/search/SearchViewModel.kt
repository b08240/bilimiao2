package com.a10miaomiao.bilimiao.ui.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.ui.widget.flow.FlowAdapter
import com.a10miaomiao.bilimiao.utils.DebugMiao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

class SearchViewModel : ViewModel() {

    var canGoBack = false
    val showSearchBox = MutableLiveData<Boolean>()
    val keyword = MutableLiveData<String>()
    val historyList = arrayListOf<String>()
    val suggestList = arrayListOf<String>()

    init {
        keyword.value = ""
        showSearchBox.value = true
    }

    /**
     * 加载搜索历史
     */
    fun loadHistoryData(adapter: FlowAdapter<*>) {

    }

    /**
     * 加载搜索提示
     */
    fun loadSuggestData(adapter: FlowAdapter<*>) {
        val text = keyword.value!!
        if (text.isEmpty()) {
            suggestList.clear()
            adapter.notifyDataSetChanged()
            return
        }
        MiaoHttp.getString(BiliApiService.getKeyWord(text))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    val jsonParser = JSONTokener(data)
                    try {
                        val jsonArray = (jsonParser.nextValue() as JSONObject).getJSONObject("result").getJSONArray("tag")
                        if (text != keyword.value!!)
                            return@subscribe
                        suggestList.clear()
                        (0 until jsonArray.length()).mapTo(suggestList) { jsonArray.getJSONObject(it).getString("value") }
                        adapter.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }
                }, { err ->
                    err.printStackTrace()
                })
    }

    /**
     * 开始搜索
     */
    fun startSearch(text: String) {
        showSearchBox.value = false
        canGoBack = true
        SearchFragment.keyword.value = text
        if (keyword.value != text)
            keyword.value = text
    }

}