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
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import sim.toolkit.EnvelopeHandler;
import sim.toolkit.ProactiveHandler;
import sim.toolkit.ToolkitConstants;
import sim.toolkit.ToolkitInterface;
import sim.toolkit.ToolkitRegistry;

/**
 *
 * @author Christopher Burke <christopher.burke@simulity.com>
 */
public class FortuneApplet extends Applet implements ToolkitConstants, ToolkitInterface, ISO7816 {

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
    private short msgLength;
    private byte[] msgBuffer = new byte[14];

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
        apdu.setIncomingAndReceive();
        process(apdu.getBuffer());
    }

    public void process(byte[] apduBuffer) throws ISOException {
        if (!selectingApplet()) {
            if(apduBuffer[OFFSET_CLA] != CLA) {
                throw new ISOException(SW_CLA_NOT_SUPPORTED);
            } else {
                if(apduBuffer[OFFSET_INS] != INS_INCOMING) {
                    throw new ISOException(SW_INS_NOT_SUPPORTED);
                } else {
                    if((apduBuffer[OFFSET_P1] != P1) && apduBuffer[OFFSET_P2] != P2) {
                        throw new ISOException(SW_INCORRECT_P1P2);
                    } else {
                        // we've validated the header data, let's check the payload
                        // the apduBuffer should look something like this....
                        // 0x0A, 0x02, 0x00, 0x00, 0x0E,
                        // ^CLA, ^INS, ^^P1, ^^P2, ^^LE
                        // 
                        // BEGIN TLVS....
                        // TAG_MESSAGE_DATA
                        // LEN_MESSAGE_DATA
                        // MESSAGE_DATA
                        // 
                        // If our message is: Hello, World!
                        //
                        // And our Tag is 0xF0
                        //
                        // Then our incoming payload should be:
                        // 
                        // F0 0C 48 65 6C 6C 6F 2C 20 57 6F 72 6C 64 21
                        //
                        // Copy the message into some sort of buffer...
                        //
                        // to do this you'll need to process this data...
                        //
                        // apduBuffer = { 0A, 0x02, 0x00, 0x00, 0x0E, F0, 0C, 
                        // 48, 65, 6C, 6C, 6F, 2C, 20, 57, 6F, 72, 6C, 64, 21 }
                        //
                        //
                        // 1. Get the length of the payload data... 
                        // Clue: OFFSET_LC
                        //
                        // 2. Validate that the tag for the payload data is 
                        //      correct, in this example above the tag is 0xF0
                        // 
                        // 3. If the payload tag is correct, retrieve the
                        //      length of the meaningful data... i.e. go one
                        //      past the tag byte to get the length byte
                        //
                        // 4. Use the length byte to invoke Util.arrayCopy
                        //      on the data to retrieve the actual message. 
                        //
                        // 5. You will then have 'Hello, World!' inside a separate
                        //      buffer.
                        // 
                        // 6. You will then be prepared to display this message
                        //      on the handset to the end user. This comes
                        //      next week. ^_^
                        /*      Trial implementation of the above   */
                        
                        short payloadLength = (short) apduBuffer[OFFSET_LC];
                        if (apduBuffer[OFFSET_CDATA]!=(byte) 0xF0){
                            throw new ISOException(SW_CONDITIONS_NOT_SATISFIED);
                        } else {
                            msgLength = (short) OFFSET_CDATA+2;
                            Util.arrayCopy(apduBuffer, msgLength, msgBuffer, (short) 0, payloadLength);
                            /*  To test whether the above code is working
                             * Refer to GitHub
                             */
                            System.out.println("**Debug**\tlength of msgBuffer = "+msgBuffer.length);
                            System.out.println("**Debug**\tpayloadLength = "+payloadLength);
                        }
                        
                        
                        ProactiveHandler theHandler = ProactiveHandler.getTheHandler();
                        theHandler.initDisplayText((byte) 0x00, DCS_8_BIT_DATA, new byte[] { (char) 'H', (char) 'i' }, (short) 0, (short) 2);
                        byte send = theHandler.send();
                        if(send != RES_CMD_PERF) {
                            // some error
                        }
                    }
                }
            }
            
        }
    }

    public void processToolkit(byte event) {
        switch (event) {
            case EVENT_MENU_SELECTION:
                // TODO: Convert data from 8-bit ASCII to GSM-7 bit
                // TODO: Create a sendSms method which can transmit an 
                // SMS
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
    
//    /**
//     * Converts an 8Bit message to GSM7 bit ASCII encoding
//     *
//     * http://en.wikipedia.org/wiki/GSM_03.38
//     *
//     * @param byaSrc 8bit byte array of data to convert
//     * @return 7bit GSM7 encoded 
//     */
//    public static Byte[] conv8bitToGsm7(byte[] byaSrc) {
//
//        List<Byte> dstByaList = new ArrayList<Byte>();
//        byte buf = (byte) 0x00;
//
//        for (int i = 0; i < byaSrc.length; i++) {
//            byte b = byaSrc[i];
//            byte c = (byte) (i & 7);
//            if (c == 0) {
//                buf = b;
//            } else {
//                dstByaList.add((byte) ((b << (8 - c) | buf)));
//                buf = (byte) (b >> c);
//            }
//        }
//
//        if ((byaSrc.length % 8) != 0) {
//            dstByaList.add(buf);
//        }
//        return dstByaList.toArray(new Byte[dstByaList.size()]);
//    }
}
