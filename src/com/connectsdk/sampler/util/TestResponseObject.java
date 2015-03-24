//
//  Connect SDK Sample App by LG Electronics
//
//  To the extent possible under law, the person who associated CC0 with
//  this sample app has waived all copyright and related or neighboring rights
//  to the sample app.
//
//  You should have received a copy of the CC0 legalcode along with this
//  work. If not, see http://creativecommons.org/publicdomain/zero/1.0/.
//

package com.connectsdk.sampler.util;

public class TestResponseObject {

    public boolean isSuccess;
    public int httpResponseCode;
    public String responseMessage;


    public static final String Default = "default";
    public static final int SuccessCode = 200;

    public static final String Display_image = "ImageLaunched";
    public static final String Play_Video = "VideoLaunched";
    public static final String Play_Audio = "AudioLaunched";

    public static final String Closed_Media = "MediaClosed";
    public static final String Stopped_Media = "MediaStop";
    public static final String Stopped_image = "ImageStopped";
    public static final String Paused_Media = "MediaPaused";
    public static final String Played_Media = "MediaPlayed";
    public static final String Rewind_Media = "MediaRewind";
    public static final String FastForward_Media = "MediaFastForward";

    public static final String Launched_Netflix = "NetflixLaunched";
    public static final String Launched_Browser = "BrowserLaunched";
    public static final String Launched_Youtube = "YoutubeLaunched";
    public static final String Launched_WebAPP = "WebAPPLaunched";
    public static final String Joined_WebAPP = "WebAPPJoined";
    public static final String Pinned_WebAPP = "WebAPPPinned";
    public static final String UnPinned_WebAPP = "WebAPPUnPinned";
    public static final String Sent_Message = "SentMessage";
    public static final String Sent_JSON = "SentJSON";
    public static final String Leave_WebAPP = "LeaveWebAPP";
    public static final String Close_WebAPP = "ClosedWebAPP";

    public static final String Power_OFF = "PowerOFF";

    public static final String Show_Toast = "ShowedToast";

    public static final String Mute_Toggle = "MuteToggle";
    public static final String Muted_Media = "MuteMedia";
    public static final String UnMuted_Media = "UnMuteMedia";

    public static final String VolumeUp = "VolumeIncreased";
    public static final String VolumeDown = "VolumeDecreased";
    public static final String InputPickerVisible = "InputPickerShowing";

    public static final String HomeClicked = "HomeClicked";
    public static final String LeftClicked = "LeftClicked";
    public static final String RightClicked = "RightClicked";
    public static final String UpClicked = "UpClicked";
    public static final String DownClicked = "DownClicked";
    public static final String Clicked = "Clicked";

    public static final String Played_Playlist = "PlayedPlaylist";
    public static final String Jump = "jumpedTrack";
    public static final String Previous = "PreviousClicked";
    public static final String Next = "NextClicked";


    public TestResponseObject() {
        super();
        this.isSuccess = false;
        this.httpResponseCode = 0;
        this.responseMessage = "default";

    }

    public TestResponseObject(boolean isSuccess, int httpResponseCode, String responseMessage) {
        super();
        this.isSuccess = isSuccess;
        this.httpResponseCode = httpResponseCode;
        this.responseMessage = responseMessage;
    }


}
