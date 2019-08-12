package com.kotlinify.updatechecker

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.*
import android.os.Build
import android.os.LocaleList
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.core.text.HtmlCompat
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

class GoogleChecker(activity: Activity, packageName: String? = null, haveNoButton: Boolean? = false, lang: String? = "en", showPopup:Boolean = true) {

    private val PLAY_STORE_ROOT_WEB = "https://play.google.com/store/apps/details?id="
    private var isThereNewVersion: Boolean = false
    private var marketVersion: String? = null
    private val TAG = "UpdateChecker"
    private var appPackageName: String? = null
    private var context: Context? = activity
    private var html: String? = null


    fun isThereANewVersion():Boolean = isThereNewVersion
    private fun isOnline(): Boolean {
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            run {
                val network: Network? = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(network)
                capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            }
        } else {
            val netInfo: NetworkInfo? = cm.activeNetworkInfo
            netInfo != null && netInfo.isConnectedOrConnecting
        }
    }

    init {
        appPackageName = packageName ?: activity.baseContext.packageName
        val url = "$PLAY_STORE_ROOT_WEB$packageName&hl=$lang"
        control(activity, url, haveNoButton, lang)
    }

    private fun control(activity: Activity, url: String, haveNoButton: Boolean?, lang: String? = null, showPopup:Boolean = true) {
        lang?.let {
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocales(LocaleList(locale))
                val overrideConfiguration = activity.resources.configuration
                overrideConfiguration.setLocales(LocaleList(locale))
            } else {
                config.locale = locale
                activity.resources.updateConfiguration(config,
                        activity.resources.displayMetrics)
            }
        }
        val lastIsBigger = arrayOf(false)
        if (isOnline()) {
            Thread(Runnable {
                try {
                    val sayfa = Jsoup.connect(url).get()
                    val version = sayfa.select("div.JHTxhe.IQ1z0d > div > div:nth-child(4) > span > div > span").first()
                    var title = sayfa.select("c-wiz:nth-child(3) > div.W4P4ne > div.wSaTQd > h2").first().text()
                    title = "<h3>$title</h3>"
                    html = title + ("\n" + sayfa.select("c-wiz:nth-child(3) > div.W4P4ne > div.PHBdkd > div.DWPxHb > span").first().html() + "<br>")
                    marketVersion = version.text()
                    Log.e("version", marketVersion!!)

                } catch (e: IOException) {
                    e.printStackTrace()

                }

                var pInfo: PackageInfo? = null
                var version: String? = null
                var newversion: Int? = 1
                var newMarketVersion: Int? = 0
                try {
                    pInfo = activity.packageManager.getPackageInfo(activity
                            .packageName, 0)
                    version = pInfo!!.versionName.toString()
                    if (version.split(".".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size == marketVersion!!.split(".".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray().size) {
                        newversion = Integer.valueOf(version.replace("[^\\d]".toRegex(), "") ?: "0")
                        newMarketVersion = Integer.valueOf(marketVersion!!.replace("[^\\d]".toRegex(), ""))
                    } else {
                        var sameOldVersion = StringBuilder()
                        val mv = marketVersion!!.split(".".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                        for (v in mv) {
                            sameOldVersion.append("$v.")
                        }
                        sameOldVersion = sameOldVersion.delete(mv.size * 2 - 1, mv.size * 2)
                        lastIsBigger[0] = true
                        newversion = Integer.valueOf(version.replace("[^\\d]".toRegex(), ""))
                        newMarketVersion = Integer.valueOf(sameOldVersion.toString().replace("[^\\d]".toRegex(), ""))
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(TAG, e.message)
                }
                if (newversion!! < newMarketVersion!! || lastIsBigger[0] && newversion === newMarketVersion) {
                    isThereNewVersion = true
                }
                if(showPopup){
                    var title: Spanned? = null
                    val translateTitle = activity.getString(R.string.update_available_title)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        title = Html.fromHtml("<u>" + translateTitle.toUpperCase() + "</u>", 1)
                    }
                    val finalTitle = title
                    activity.runOnUiThread {
                        if (isThereNewVersion) {
                            val successDialog = AwesomeSuccessDialog(activity)
                                    .setTitle(finalTitle)
                                    .setMessage(HtmlCompat.fromHtml(html!!, 0))
                                    .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                                    .setDialogIconAndColor(R.drawable.ic_update_black_24dp, R.color.white)
                                    .setCancelable(haveNoButton!!)
                                    .setPositiveButtonText(activity.getString(R.string.update))
                                    .setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                                    .setPositiveButtonTextColor(R.color.white)
                                    .setPositiveButtonClick {
                                        try {
                                            val i = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName!!))
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
                                            activity.startActivity(i)
                                        } catch (anfe: ActivityNotFoundException) {

                                            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName!!))
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
                                            activity.startActivity(i)
                                        }
                                        activity.finish()
                                    }
                            if (haveNoButton == true) {
                                successDialog.setNegativeButtonText(activity.getString(R.string.cancel))
                                        .setNegativeButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                                        .setNegativeButtonTextColor(R.color.white)
                                        .setNegativeButtonClick {
                                            //click
                                        }
                            }
                            successDialog.show()
                        }
                    }
                }
            }).start()
        } else {
            Log.e("UpdateChecker", "No internet connection")
        }

    }

}
