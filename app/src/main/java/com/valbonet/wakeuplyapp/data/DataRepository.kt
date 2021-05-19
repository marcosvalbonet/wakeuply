package com.valbonet.wakeuplyapp.data

import com.valbonet.wakeuplyapp.framework.ApiAccess
import com.valbonet.wakeuplyapp.framework.TikTokApiAccess
import com.valbonet.wakeuplyapp.framework.TikTokWebAccess
import com.valbonet.wakeuplyapp.model.item.Tiktoker
import com.valbonet.wakeuplyapp.model.search.Muser
import com.valbonet.wakeuplyapp.presentation.searcher.Lead
import com.valbonet.wakeuplyapp.utils.UrlUtils
import io.reactivex.Observable
import retrofit2.Call
import java.util.HashMap

class DataRepository {

    fun getTiktoker(params: HashMap<String, String>): Observable<Tiktoker> {
        return TikTokApiAccess.serviceApi.getItemList(params)
    }

    fun getVideoData(nickname: String?, videoID: String?, cookie: String?): Observable<String> {
        return TikTokWebAccess.serviceApi.getVideoData(nickname, videoID, cookie)
    }

    fun getMusersList(offset: String?, sizeList: String?): Observable<List<Muser>> {
        return ApiAccess.serviceApi.getMusersList("getMoreTikTokMusersWithBlobList", offset, sizeList)
    }

    fun findMusersByNickname(nickname: String?): Observable<List<Muser>> {
        return ApiAccess.serviceApi.findMusersByNickname("getMuserByNicknameList", nickname)
    }

    fun addNewTiktokMuser(muser: Muser?): Call<Boolean> {
        return ApiAccess.serviceApi.addNewTikTokMuser(
                "addNewTikTokMuser",
                muser?.nickname,
                muser?.name,
                muser?.userId,
                muser?.secUid,
                muser?.avatarMedium)
    }

    fun updateUserId(nickname: String?, userId: String?): Call<Boolean> {
        return ApiAccess.serviceApi.updateUserId("updateUserIdTikTokMuser", nickname, userId)
    }

    fun updateSecUid(nickname: String?, secUid: String?): Call<Boolean> {
        return ApiAccess.serviceApi.updateSecUid("updateSecUidTikTokMuser", nickname, secUid)
    }

    fun updateTiktokerImg(nickname: String?, imgSrc: String?): Call<Boolean> {
        return ApiAccess.serviceApi.updateTiktokerImage("updateTikTokMuserImg", nickname, imgSrc)
    }

}