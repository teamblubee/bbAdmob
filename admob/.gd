
extends Button

var admob_app_id = "ca-app-pub-xxxxxxxxxxxxxxxxxxxxxxxxxxx" #get this string when you register your app in adbmob backend

func _init():
	if(Globals.has_singleton("bbAdmob")):
		admob = Globals.get_singleton("bbAdmob")
		#You can call admob.init_admob_test or admob.init_admob_real
		#If the second argument is true, the banner ad will be at the top of the screen
		#Function prototype init_admob_test(final String app_id, boolean isTop)
		admob.init_admob_test(admob_app_id, true)

func _ready():
	# Initialization here
	pass





func _on_Show_Banner_Button_pressed():
	admob.show_banner()
	
	
func _on_Hide_Banner_Button_pressed():
	admob.hide_banner()


func _on_Show_Interstitial_Button_pressed():
	admob.show_interstitial()
