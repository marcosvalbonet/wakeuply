package com.valbonet.wakeuplyapp.usecases

import com.valbonet.wakeuplyapp.data.DataRepository
import com.valbonet.wakeuplyapp.model.item.Tiktoker
import com.valbonet.wakeuplyapp.utils.UtilsVideo
import io.reactivex.Observable

class GetTiktokerUseCase {
    val dataRepository = DataRepository()

    operator fun invoke(userID: String?, secUid: String?, offset: Int, sizeList: Int): Observable<Tiktoker> {
        var params : HashMap<String, String> = HashMap()

        //cursor=0&sourceType=8&secUid=MS4wLjABAAAAKNEV4Vcuz8kAyhqmBlX3AP5fF_aR5LclGua97AbbonUnOkO1cntxK2at5ckXrOzk&type=1&appId=1233&region=es&priority_region=es&language=es&aid=1988&app_name=tiktok_web&device_platform=web&referer=&root_referer=&user_agent=Mozilla%252F5.0%2B%28iPhone%253B%2BCPU%2BiPhone%2BOS%2B12_2%2Blike%2BMac%2BOS%2BX%29%2BAppleWebKit%252F605.1.15%2B%28KHTML%2C%2Blike%2BGecko%29%2BVersion%252F13.0%2BMobile%252F15E148%2BSafari%252F604.1&cookie_enabled=true&screen_width=&screen_height=&browser_language=&browser_platform=&browser_name=&browser_version=&browser_online=&ac=4g&timezone_name=&appType=m&isAndroid=false&isMobile=false&isIOS=false&OS=windows
        params.put("cursor", "0")
        params.put("sourceType", "8")

        params.put("secUid", secUid!!)
        //params.put("id", "251447176915243008")
        params.put("id", userID!!)
        params.put("type", "1")
        params.put("count", Integer.toString(offset + sizeList))
        params.put("sourceType", "8")
        params.put("appId", "1233")
        params.put("region", "es")
        params.put("priority_region", "es")
        params.put("language", "es")

        /*
        *   "count": realCount,
            "id": userID,
            "cursor": cursor,
            "type": 1,
            "secUid": secUID,
            "sourceType": 8,
            "appId": 1233,
            "region": region,
            "priority_region": region,
            "language": language,*/

        /*
        * 	"aid": 1988,
            "app_name": "tiktok_web",
            "device_platform": "web",
            "referer": "",
            "root_referer": "",
            "user_agent": self.__format_new_params__(self.userAgent),
            "cookie_enabled": "true",
            "screen_width": self.width,
            "screen_height": self.height,
            "browser_language": self.browser_language,
            "browser_platform": self.browser_platform,
            "browser_name": self.browser_name,
            "browser_version": self.browser_version,
            "browser_online": "true",
            "ac": "4g",
            "timezone_name": self.timezone_name,
            "appId": 1233,
            "appType": "m",
            "isAndroid": False,
            "isMobile": False,
            "isIOS": False,
            "OS": "windows",
			*/

        params.put("aid", "1988")
        params.put("app_name", "tiktok_web")
        params.put("device_platform", "web")
        params.put("referer", "")
        params.put("root_referer", "")
        params.put("user_agent", "Mozilla%252F5.0%2B%28iPhone%253B%2BCPU%2BiPhone%2BOS%2B12_2%2Blike%2BMac%2BOS%2BX%29%2BAppleWebKit%252F605.1.15%2B%28KHTML%2C%2Blike%2BGecko%29%2BVersion%252F13.0%2BMobile%252F15E148%2BSafari%252F604.1")
        params.put("cookie_enabled", "true")
        params.put("screen_width", "")
        params.put("screen_height", "")
        params.put("browser_language", "")
        params.put("browser_platform", "")
        params.put("browser_name", "")
        params.put("browser_version", "")
        params.put("browser_online", "true")
        params.put("ac", "4g")
        //"timezone_name": self.timezone_name,
        //"appId": 1233,
        params.put("appType", "m")
        params.put("isAndroid", "False")
        params.put("isMobile", "False")
        params.put("isIOS", "False")
        params.put("OS", "windows")

        //https://t.tiktok.com/api/post/item_list/?count=10&id=6601281089016627201&cursor=0&sourceType=8&secUid=MS4wLjABAAAAKNEV4Vcuz8kAyhqmBlX3AP5fF_aR5LclGua97AbbonUnOkO1cntxK2at5ckXrOzk&type=1&appId=1233&region=es&priority_region=es&language=es&aid=1988&app_name=tiktok_web&device_platform=web&referer=&root_referer=&user_agent=Mozilla%252F5.0%2B%28iPhone%253B%2BCPU%2BiPhone%2BOS%2B12_2%2Blike%2BMac%2BOS%2BX%29%2BAppleWebKit%252F605.1.15%2B%28KHTML%2C%2Blike%2BGecko%29%2BVersion%252F13.0%2BMobile%252F15E148%2BSafari%252F604.1&cookie_enabled=true&screen_width=&screen_height=&browser_language=&browser_platform=&browser_name=&browser_version=&browser_online=&ac=4g&timezone_name=&appType=m&isAndroid=false&isMobile=false&isIOS=false&OS=windows

        return dataRepository.getTiktoker(params)
    }
}