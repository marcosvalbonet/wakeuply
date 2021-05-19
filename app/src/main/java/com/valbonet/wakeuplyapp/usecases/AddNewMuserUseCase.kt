package com.valbonet.wakeuplyapp.usecases

import com.valbonet.wakeuplyapp.data.DataRepository
import com.valbonet.wakeuplyapp.model.search.Muser
import retrofit2.Call

class AddNewMuserUseCase {

    val dataRepository = DataRepository()

    operator fun invoke(muser: Muser?): Call<Boolean> {
        return dataRepository.addNewTiktokMuser(muser)
    }
}