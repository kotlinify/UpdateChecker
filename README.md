# UpdateChecker
Update Checker For Google Play

![](/IntroPic/updatechecker.png)

[![](https://jitpack.io/v/kotlinify/UpdateChecker.svg)](https://jitpack.io/#kotlinify/UpdateChecker)

This repo forked [kobeumut/UpdateChecker](https://github.com/kobeumut/UpdateChecker) and convert to Kotlin and finally release stable version 1.

# Features

* Add new super looking alert dialog
* Fix bugs and improve performance performance fixes
* Auto show Whats New list on Google Play Market by language

**Used Libraries:**

* Jsoup for scrap.
* AwesomeDialog for AlertDialog

**Screenshots:**

![](/IntroPic/updateChecker.gif)

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


Add the dependency

	dependencies {
	        implementation 'com.github.kotlinify:UpdateChecker:1.0.0'
	}




Or Maven


	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

 Add the dependency

	<dependency>
	    <groupId>com.github.kobeumut</groupId>
	    <artifactId>UpdateChecker</artifactId>
	    <version>1.0.0</version>
	</dependency>


# Usage


```kotlin
 GoogleChecker(activity)
```

Additional parameters is lang for language, package name for search in market, haveNoButton for soft update and showPopup

If you listen callback;

```
GoogleChecker(activity){
 //return boolean
}
```

**Other Usages**

If you want to option a cancel you can send second parameters to true.

And if you send a difference package name
```kotlin
         GoogleChecker(activity = this@MainActivity, packageName = "com.teknasyon.photofont", lang = "en", showPopup = false){
            Toast.makeText(this@MainActivity, "Is There a New Version: $it", Toast.LENGTH_SHORT).show()
        }
```



# Licence
Copyright 2017 Umut ADALI
Copyright 2019 Kotlinify

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.