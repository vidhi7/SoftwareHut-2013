/*
 * 
 * Simulity Labs Ltd.
 * 
 * Copyright (c) Simulity Labs Ltd. All rights reserved.
 *
 * This source code is the property of Simulity Labs Ltd. Redistribution and
 * use in source (source code) or binary (object code) forms with or without 
 * modification, for commercial, educational or research purposes is not
 * permitted without the prior written consent of Simulity Labs Limited 
 *
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE, UNLESS PRIOR WRITTEN CONSENT STATES OTHERWISE.
 * 
 *
 */
package com.simulity.javacard.fortuneapplet;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import sim.toolkit.EnvelopeHandler;
import sim.toolkit.ToolkitConstants;
import sim.toolkit.ToolkitInterface;
import sim.toolkit.ToolkitRegistry;

/**
 *
 * @author Christopher Burke <christopher.burke@simulity.com>
 */
public class FortuneApplet extends Applet implements ToolkitConstants, ToolkitInterface {

    private ToolkitRegistry toolkitRegistry = ToolkitRegistry.getEntry();
    private byte[] swap;
    private byte CLA = (byte) 0x0A;
    private byte INS_INCOMING = (byte) 0x01;
    private byte INS_OUTGOING = (byte) 0x02;
    private byte P1 = (byte) 0x00;
    private byte P2 = (byte) 0x00;
    private byte[] MENU_ENTRY = new byte[]{
        (char) 'F', (char) 'o', (char) 'r', (char) 't', 
        (char) 'u', (char) 'n', (char) 'e'
    };

    public FortuneApplet(byte[] bArray, short bOffset, short parametersLength) {
        // Get the reference of the applet ToolkitRegistry object
        toolkitRegistry = ToolkitRegistry.getEntry();

        toolkitRegistry.setEvent(EVENT_FORMATTED_SMS_PP_ENV);

        this.swap = JCSystem.makeTransientByteArray((short) 250, JCSystem.CLEAR_ON_RESET);

        toolkitRegistry.initMenuEntry(
                MENU_ENTRY, 
                (short) 0, // offset in array 
                (short) 7, // length of 'Fortune'
                PRO_CMD_SELECT_ITEM, false, (byte) 0, (short) 0);

    }

    public static void install(byte[] bArray, short bOffset, byte bLength) throws ISOException {

        short parametersLength, workOffset = bOffset;

        // Skip the instance AID
        workOffset += (short) (bArray[workOffset] + 1);

        // Skip the application privileges
        workOffset += (short) (bArray[workOffset] + 1);

        // Get the application parameters length
        parametersLength = (short) (bArray[workOffset] & 0xff);

        // Create the instance and register it with the given instance AID
        new FortuneApplet(bArray, (short) (workOffset + 1), parametersLength)
                /*
                 * This method is used by the applet to register this applet 
                 * instance with the Java Card runtime environment and assign 
                 * the specified AID bytes as its instance AID bytes.
                 */
                .register(bArray, (short) (bOffset + 1), bArray[bOffset]);

    }

    public void process(APDU apdu) throws ISOException {
        byte[] buffer = apdu.getBuffer();
        short setIncomingAndReceive = apdu.setIncomingAndReceive();
        process(buffer);
    }

    public void process(byte[] apduBuffer) {
        if (!selectingApplet()) {
            // we've recieved in incoming request from the SMSC/GW
            // to process a fortune message.
        }
    }

    public void processToolkit(byte event) {
        switch (event) {
            case EVENT_MENU_SELECTION:
                // We now need to transmit and SMS to the server to request
                // a fortune message
                break;
            case EVENT_FORMATTED_SMS_PP_ENV:
                EnvelopeHandler envelopeHandler = EnvelopeHandler.getTheHandler();

                short securedDataLength = envelopeHandler.getSecuredDataLength();
                short securedDataOffset = envelopeHandler.getSecuredDataOffset();

                envelopeHandler.copyValue(securedDataOffset, swap, (short) 0, securedDataLength);

                process(swap);

                break;
        }
    }
}
