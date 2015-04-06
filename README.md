
# bbAdmob

[![Godot Google Play Services Admob Video](http://img.youtube.com/vi/ssdkUM_XIsQ/0.jpg)](http://www.youtube.com/watch?v=ssdkUM_XIsQ)


This is a [Google Play Services module](https://developer.android.com/google/play-services/index.html) for the [Godot game engine](https://github.com/okamstudio/godot), written by [blubee](http://blubee.me)

This module should be straigh forward to use.

Setup your app in the new Google Play Services back end, you'll need your "ca-app-pub" number.

Download this project put the "GodotGooglePlayServices" inside your godot-src/modules foler.
Then recompile your android export templates.

[How To Compiling Android Export Templates](https://github.com/okamstudio/godot/wiki/compiling_android#compiling-export-templates)

In the Godot editor go to the export tab
Click on Android
Make sure that **Access Network State** and **internet** permissions are checked

In your script create a variable, instantiate that variable after checking that it's available to to engine.

this is where you'll also need your "ca-app-pub".

Store that as a string and pass it into the init_admob_ function


###Function prototype
####init_admob_test("banner pub id", "interstitial pub id", "isTop")
####init_admob_interstitial_test("interstitial pub id", "isTop")
####init_admob_banner_test("banner pub id", "isTop")

Replace _test with _real after you've successfully tested the ads. Do not test with live ads because clicking your own ads **even by accident** can cause you to lose your admob account.

**Also note**
You should only call one of the init_admob_ functions.

var admob = null
if(Globals.has_singleton("bbAdmob")):
        admob = Globals.get_singleton("bbAdmob")

You then call admob.init_admob_[**test**|**real**]

once that's done and an ad is successfully loaded you can request to show banner or interstitial ads. 


With the new Admob you can have separate publisher IDs for your banners and your interstitials. 
Calling the main functions init_admob with your banner publisher id as the first argument and your interstitial publisher id as the second argument isTop is boolean true or false. This will load both ad and once they are ready you can call show/ hide banner or show interstitial.

Remember that you should only call the init function **once**.

##Main Functions Overview

#####admob.init_admob_test("banner pub id", "interstitial pub id", "isTop")
#####admob.init_admob_real("banner pub id", "interstitial pub id", "isTop")
####admob.show_banner()
####admob.hide_banner()
####admob.show_interstitial()


If you for any reason would just like to show either interstitial ads or banner ads then call one of these sets of functions below.



###Additional Functions
#####admob.init_admob_interstitial_test("interstitial pub id", "isTop")
#####init_admob_interstitial_real("interstitial pub id", "isTop")
#####init_admob_banner_test("banner pub id", "isTop")
#####init_admob_banner_real("banner pub id", "isTop")


Be sure to always test your ads with the **init_admob_test** functions, Google will band your admob account if you test with real ads. To be sure check the [admob guidelines](https://support.google.com/admob/answer/2753860?hl=en)

I've also added added proper lifecycle handling code, that means if your showing a banner ad before your app goes into the background, the banner ad will handle that properly.


check the example project for usage.

###credit
This [StackOverflow](http://stackoverflow.com/questions/3934331/android-how-to-encrypt-a-string) answer for getting android device id.
