# bbAdmob
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


##Function prototype 
###init_admob_test(final String app_id, boolean isTop)

var admob = null
if(Globals.has_singleton("bbAdmob")):
	admob = Globals.get_singleton("bbAdmob")

You then call admob.init_admob_[**test**|**real**]

once that's done and an ad is successfully loaded you can request to show banner or interstitial ads.

##Function Overview

#####admob.init_admob_test("ca-app-pub","isTop"")
#####admob.init_admob_real("ca-app-pub","isTop"")
####admob.show_banner()
####admob.hide_banner()
####admob.show_interstitial()

Be sure to always test your ads with the **init_admob_test** functions, Google will band your admob account if you test with real ads. To be sure check the [admob guidelines](https://support.google.com/admob/answer/2753860?hl=en).

check the example project for usage.

###credit
This [StackOverflow](http://stackoverflow.com/questions/3934331/android-how-to-encrypt-a-string) answer for getting android device id.
