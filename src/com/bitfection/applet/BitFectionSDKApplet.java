package com.bitfection.applet;

import java.net.URL;

import com.bitfection.game.GameChallenge;
import com.bitfection.util.Util;

@SuppressWarnings("serial")
public class BitFectionSDKApplet extends BitFectionApplet {
	
	@Override
	public void init() {
		super.init();
		
		// without this, the BitFection applet would download the images from a website
		// now for whom would that be good
                // will not work with Netbeans though - resources must be under src afaik
		
		for(String challengeId : new String[] { "vascular", "liver", "lungs", "tharm", "brain" }) {
			GameChallenge challenge = getGameChallenge(challengeId);
			URL defaultGameImageURL = getClass().getResource("/"+challenge.getDefaultGameImageRes());
			if(defaultGameImageURL instanceof URL) {
				challenge.setDefaultGameImage(Util.loadImageFromRes(defaultGameImageURL));
			}
			URL defaultMaskImageURL = getClass().getResource("/"+challenge.getDefaultMaskImageRes());
			if(defaultMaskImageURL instanceof URL) {
				challenge.setDefaultMaskImage(Util.loadTranscluentImageFromRes(defaultMaskImageURL));
			}
		}
	};
}
