package org.toorcamp.CryptoChallenge;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import sim.toolkit.EnvelopeHandler;
import sim.toolkit.ProactiveHandler;
import sim.toolkit.ProactiveResponseHandler;
import sim.toolkit.ToolkitConstants;
import sim.toolkit.ToolkitException;
import sim.toolkit.ToolkitInterface;
import sim.toolkit.ToolkitRegistry;

public class CryptoChallenge extends Applet implements ToolkitConstants, ToolkitInterface {
	private byte hintsGiven;
	private byte mainMenuItem;
	
	private static byte[] menuItemText = new byte[] { 'C', 'r', 'e', 'd', 'i', 't', 's' }; 
	private static byte[] needHints = new byte[] {
		'N', 'e', 'e', 'd', ' ', 's', 'o', 'm', 'e', ' ', 'h', 'i', 'n', 't', 's', '?'};
	private static byte[] yes = new byte[] { 'Y', 'e', 's' };
	private static byte[] no = new byte[] { 'N', 'o' };
	private static byte[] hints = new byte[] { 'H', 'i', 'n', 't', 's' };
	private static byte[] hint1 = new byte[] { 'X', 'T', 'P', 'L', 'K', 'T', 'G', 'Q', 'L' };
	private static byte[] hint2 = new byte[] { 'D', 'P', 'Q', 'T', 'D', 'U', 'U', 'U', 'B', 'J', 'I', 'L' };
	private static byte[] hint3 = new byte[] { 'X', 'P', 'G', 'L', 'K', 'T', 'V', 'T', 'C', 'D', 'J', 'K' };
	private static byte[] hint4 = new byte[] { 'G', 'X', 'A', 'D', 'H', 'O', 'V', 'K', 'G', 'U', 'P', 'D', 'F', 'P', 'A' };
	private static byte[] hint5 = new byte[] { 'X', 'P', 'G', 'L', 'K', 'W', 'N', 'T', 'Y', 'G' };
	
	private static byte[] hintNames = new byte[] { '1', '2', '3', '4', '5' };
	
	private static byte[] credits = new byte [] {
		'T', 'o', 'o', 'r', 'c', 'a', 'm', 'p', ' ', 'G', 'S', 'M', ' ', 'n', 'e', 't', 'w', 'o', 'r', 'k', ' ', 
		'b', 'y', ' ', 'S', 'h', 'a', 'd', 'y', 'T', 'e', 'l', '\n', '\n',
		'c', 'h', 'o', 'n', 'o', 'm', 'e', 'x', '\n',
		'c', 'o', 'd', 'e', 'b', 'u', 't', 'l', 'e', 'r', '\n',
		'e', 'x', 't', 'r', 'a', 'p', 'i', 'c', 'k', 'l', 'e', 's', '\n',
		's', 'u', 'p', 'e', 'r', 's', 'a', 't', '\n'
	};
	
	private CryptoChallenge() {
		hintsGiven = 0;
		
		ToolkitRegistry reg = ToolkitRegistry.getEntry();
		mainMenuItem = reg.initMenuEntry(menuItemText, (short)0, (short)menuItemText.length,
				PRO_CMD_SELECT_ITEM, false, (byte)0, (short)0);
	}
	
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		CryptoChallenge applet = new CryptoChallenge();
		applet.register();
	}
	
	public void processToolkit(byte event) throws ToolkitException {
		EnvelopeHandler envHdlr = EnvelopeHandler.getTheHandler();
		if (event == EVENT_MENU_SELECTION) {
			byte selectedItemId = envHdlr.getItemIdentifier();

			if (selectedItemId == mainMenuItem) {
				ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
				if (hintsGiven == 0) {
					proHdlr.initDisplayText((byte)0, DCS_8_BIT_DATA, credits, (short)0, 
							(short)(credits.length));
					proHdlr.send();
					
					hintsGiven = (byte)0x80;
					return;
				}
				
				proHdlr.init(PRO_CMD_SELECT_ITEM, (byte)0x00, (byte)ToolkitConstants.DEV_ID_ME);
				proHdlr.appendTLV((byte)TAG_ALPHA_IDENTIFIER, needHints, (short)0x0000, (short)needHints.length);
				proHdlr.appendTLV((byte)TAG_ITEM, (byte)1, yes, (short)0x0000, (short)yes.length);
				proHdlr.appendTLV((byte)TAG_ITEM, (byte)2, no, (short)0x0000, (short)no.length);
				proHdlr.send();
				
				ProactiveResponseHandler rspHdlr = ProactiveResponseHandler.getTheHandler();
				byte selItemId = rspHdlr.getItemIdentifier();
				if (selItemId == 2) { // No
					proHdlr.initDisplayText((byte)0, DCS_8_BIT_DATA, credits, (short)0, 
							(short)(credits.length));
					proHdlr.send();
				} else {
					proHdlr.init(PRO_CMD_SELECT_ITEM, (byte)0x00, (byte)ToolkitConstants.DEV_ID_ME);
					proHdlr.appendTLV((byte)TAG_ALPHA_IDENTIFIER, hints, (short)0x0000, (short)hints.length);
					for (byte i = 0; i < 5; i++) {
						proHdlr.appendTLV((byte)TAG_ITEM, i, hintNames, (short)i, (short)1);
					}
					proHdlr.send();
					selItemId = rspHdlr.getItemIdentifier();
					hintsGiven |= (1 << selItemId);
					
					byte[] hint = null;
					if (selItemId == 0) {
						hint = hint1;
					} else if (selItemId == 1) {
						hint = hint2;
					} else if (selItemId == 2) {
						hint = hint3;
					} else if (selItemId == 3) {
						hint = hint4;
					} else if (selItemId == 4) {
						hint = hint5;
					}
					proHdlr.initDisplayText((byte)0, DCS_8_BIT_DATA, hint, (short)0, 
							(short)hint.length);
					proHdlr.send();
				}
			}
		}
	}

	public void process(APDU apdu) throws ISOException {
		// ignore the applet select command dispached to the process
		if (selectingApplet())
			return;

		byte[] buffer = apdu.getBuffer();
		if (buffer[ISO7816.OFFSET_CLA] != (byte)0x80)
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		
		if (buffer[ISO7816.OFFSET_INS] == 0x61) {
			buffer[0] = hintsGiven;
			apdu.setOutgoingAndSend((short)0, (short)1);
			return;
		}
		
		ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
	}

}
