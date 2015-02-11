#Connect SDK Sampler App for Android

Connect SDK is an open source framework that connects your mobile apps with multiple TV platforms. Because most TV platforms support a variety of protocols, Connect SDK integrates and abstracts the discovery and connectivity between all supported protocols.

For more information, visit our [website](http://www.connectsdk.com/).

* [General information about Connect SDK](http://www.connectsdk.com/discover/)
* [Platform documentation & FAQs](http://www.connectsdk.com/docs/android/)
* [API documentation](http://www.connectsdk.com/apis/android/)

##Dependencies
- [Android v7 appcompat library](http://developer.android.com/tools/support-library/features.html#v7-appcompat)
- Android SDK v21 (the Sampler app targets v21, but works on v9 and greater)
- [Connect-SDK-Android-Core](https://github.com/ConnectSDK/Connect-SDK-Android-Core)
- [Connect-SDK-Android-Google-Cast](https://github.com/ConnectSDK/Connect-SDK-Android-Google-Cast) for full version of Connect-SDK

##Setup with Android Studio
1. Download the Sampler project
    ```
    git clone https://github.com/ConnectSDK/Connect-SDK-Android-API-Sampler.git
    ```
2. Import the Sampler project into Android Studio

##Setup with Eclipse
1. Setup Connect-SDK-Android [full](https://github.com/ConnectSDK/Connect-SDK-Android) or [lite](https://github.com/ConnectSDK/Connect-SDK-Android-Lite) version
2. Import the Android v7 appcompat libary into your Eclipse workspace
3. Import the Google google-play-services library into your Eclipse workspace (for full version of Connect-SDK)
4. [Clone project](https://github.com/ConnectSDK/Connect-SDK-Android-API-Sampler) or [Download & unzip](https://github.com/ConnectSDK/Connect-SDK-Android-API-Sampler/archive/master.zip) the Connect SDK Sampler App ZIP file
5. Import the Sampler project into Eclipse
6. Right-click the `Connect-SDK-Android-API-Sampler` project and select `Properties`, in the `Library` pane of the `Android` tab add following libraries
   - android-support-v7-appcompat
   - Connect-SDK-Android-Core
   - Connect-SDK-Android-Google-Cast (for full version)
8. See [https://github.com/ConnectSDK/Connect-SDK-Android](https://github.com/ConnectSDK/Connect-SDK-Android) for the remainder of the setup

##Contact
* Twitter: [@ConnectSDK](https://www.twitter.com/ConnectSDK)
* Ask a question with the "tv" tag on [Stack Overflow](http://stackoverflow.com/tags/tv)
* General Inquiries: info@connectsdk.com
* Developer Support: support@connectsdk.com
* Partnerships: partners@connectsdk.com

##License

Connect SDK Sample App by LG Electronics

To the extent possible under law, the person who associated CC0 with
this sample app has waived all copyright and related or neighboring rights
to the sample app.

You should have received a copy of the CC0 legalcode along with this
work. If not, see http://creativecommons.org/publicdomain/zero/1.0/.