package com.valbonet.wakeuplyapp

import com.valbonet.wakeuplyapp.data.DataRepository
import com.valbonet.wakeuplyapp.model.item.Tiktoker
import com.valbonet.wakeuplyapp.model.search.Muser
import com.valbonet.wakeuplyapp.usecases.GetMusersUseCase
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.Callable


class CallApiTest{

    private var tiktoker: Tiktoker? = null

    companion object {
        init {
            // things that may need to be setup before companion class member variables are instantiated
        }

        // variables you initialize for the class just once:
//        val someClassVar = initializer()
//
//        // variables you initialize for the class later in the @BeforeClass method:
//        lateinit var someClassLateVar: SomeResource

//        @BeforeClass @JvmStatic fun setup() {
//            // things to execute once and keep around for the class
//        }
//
//        @AfterClass @JvmStatic fun teardown() {
//            // clean up after this class, leave nothing dirty behind
//        }
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        }
    }


    @Before
    fun setUp(){
        //RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline())
    }


    @Test
    fun callGetRateApi_isCorrect(){

//        val dataRepository = DataRepository()
//        //dataRepository.getTiktoker()
//        val getMusersUseCase = GetMusersUseCase()
//
//        getMusersUseCase.invoke("0","100")
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ s ->  review(s)  },
//                {  },
//                { assertNotNull()})

//       Thread.sleep(10000)
    }

//    fun review(musers :List<Muser>){
//        musers.foreach
//    }

}
