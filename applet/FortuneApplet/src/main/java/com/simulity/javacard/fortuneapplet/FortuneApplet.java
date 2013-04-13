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

import javacard.framework.*;
import sim.access.SIMSystem;
import sim.access.SIMView;
import sim.toolkit.*;

/**
 *
 * @author Christopher Burke <christopher.burke@simulity.com>
 */
public class FortuneApplet extends Applet implements ToolkitConstants, ToolkitInterface, ISO7816 {

    /**
     * Storage Buffers
     */
    private byte[] sms_buffer;
    private byte[] swap_buffer;
    /**
     * SMS Transmission Constants
     */
    private static final byte FDI_LENGTH = (byte) 0x0f;
    private static final byte FDI_SIZE_OFFSET = (byte) 0x0e;
    private static final byte SMSF_SMS_SUBMIT_NOVP = (byte) 0x11;
    private static final byte SMSF_SMS_MR = (byte) 0x00;
    private static final byte TP_PID = (byte) 0x00;
    /**
     * Applet Constants
     */
    private byte CLA = (byte) 0x0A; // The Applet CLASS byte
    private byte INS_INCOMING = (byte) 0x01; // Instruction byte for Incoming 
    private byte INS_OUTGOING = (byte) 0x02; // Instruction byte for Outgoing
    private byte P1 = (byte) 0x00; // P1 Constant
    private byte P2 = (byte) 0x00; // P2 Constant
    /**
     * 'Fortune' buffer
     */
    private byte[] MENU_ENTRY = new byte[]{
        (byte) 'F', (byte) 'o', (byte) 'r', (byte) 't',
        (byte) 'u', (byte) 'n', (byte) 'e'
    };
    /**
     * The MSISDN that SMS are transmitted to for reporting.
     */
    private final byte[] SMS_TRANSMIT_MSISDN = new byte[]{
        //+447860033047  ( Coded as contents of EF_ADN (GSM 11.11) )
        (byte) 0x08, // 0x08 == Length of Bytes that Follow 
        (byte) 0x0C, // 0x0C == len(447860033047)
        (byte) 0x91, // 0x91 == International Numbering Plan Identifier
        (byte) 0x44, // MSISDN Follows (nibbles swapped)
        (byte) 0x87,
        (byte) 0x06,
        (byte) 0x30,
        (byte) 0x03,
        (byte) 0x74
    };
    /**
     * The Content of the SMS Transmission
     */
    private byte[] SMS_TRANSMIT_CONTENT = new byte[15];
    /**
     * The ToolKit Registry, used for provisioning events on the handset.
     */
    private ToolkitRegistry toolkitRegistry = ToolkitRegistry.getEntry();

    /**
     * Applet Constructor
     *
     * @param bArray
     * @see install
     * @param bOffset
     * @see install
     * @param parametersLength
     * @see install
     */
    public FortuneApplet(byte[] bArray, short bOffset, short parametersLength) {
        toolkitRegistry = ToolkitRegistry.getEntry();

        /**
         * Add support for Formatted SMS-PP Messages
         */
        toolkitRegistry.setEvent(EVENT_FORMATTED_SMS_PP_ENV);

        /**
         * Construct the swap and sms buffers to transient byte arrays
         */
        this.swap_buffer = JCSystem.makeTransientByteArray((short) 250, JCSystem.CLEAR_ON_RESET);
        this.sms_buffer = JCSystem.makeTransientByteArray((short) 160, JCSystem.CLEAR_ON_RESET);
        this.SMS_TRANSMIT_CONTENT = JCSystem.makeTransientByteArray((short) 15, JCSystem.CLEAR_ON_RESET);

        SMS_TRANSMIT_CONTENT[0] = (byte) 0x06; // Length
        // FASW == Fortune Applet Software Hut -- Identifies this applet on the server
        // for message routing 

        SMS_TRANSMIT_CONTENT[1] = (byte) 'F';
        SMS_TRANSMIT_CONTENT[2] = (byte) 'A';
        SMS_TRANSMIT_CONTENT[3] = (byte) 'S';
        SMS_TRANSMIT_CONTENT[4] = (byte) 'H';
        SMS_TRANSMIT_CONTENT[5] = (byte) ' ';
        SMS_TRANSMIT_CONTENT[6] = (byte) '0'; // 0 == Send a Fortune message to this handset

        /**
         * Add the menu item for the applet
         */
        toolkitRegistry.initMenuEntry(
                MENU_ENTRY,
                (short) 0, // offset in array 
                (short) 7, // length of 'Fortune'
                PRO_CMD_SELECT_ITEM, false, (byte) 0, (short) 0);
    }

    /**
     * The Install method, called by the JCVM
     *
     * @param bArray
     * @param bOffset: offset in bArray
     * @param bLength: length of bArray
     * @throws ISOException
     */
    public static void install(byte[] bArray, short bOffset, byte bLength) throws ISOException {

        short parametersLength, workOffset = bOffset;

        // Skip the instance AID
        workOffset += (short) (bArray[workOffset] + 1);

        // Skip the application privileges
        workOffset += (short) (bArray[workOffset] + 1);

        // Get the application parameters length
        parametersLength = (short) (bArray[workOffset] & 0xff);

        // Create the instance and register it with the given instance AID
        new FortuneApplet(bArray, (short) (workOffset + 1), parametersLength) /*
                 * This method is used by the applet to register this applet
                 * instance with the Java Card runtime environment and assign
                 * the specified AID bytes as its instance AID bytes.
                 */.register(bArray, (short) (bOffset + 1), bArray[bOffset]);
    }

    /**
     * The process method, called by the JCVM. The processBuffer method is only
     * needed in here for the simulated testing environment, and has no effect
     * when the applet is installed onto a card and used in the production
     * system.
     *
     * @param apdu The incoming APDU object
     * @throws ISOException should there be any errors
     */
    public void process(APDU apdu) throws ISOException {
        apdu.setIncomingAndReceive();
        processBuffer(apdu.getBuffer());
    }

    /**
     * Process an APDU buffer in the Applet
     *
     * Example APDU for Simulator:
     *
     * apdu
     * 0A01000020466F74696E7565204170706C657420666F7220536F6674776172652048757421
     *
     * @param apduBuffer
     * @throws ISOException
     */
    private void processBuffer(byte[] apduBuffer) throws ISOException {
        if (!selectingApplet()) {
            if (apduBuffer[OFFSET_CLA] != CLA) {
                throw new ISOException(SW_CLA_NOT_SUPPORTED);
            } else {
                if (apduBuffer[OFFSET_INS] != INS_INCOMING) {
                    throw new ISOException(SW_INS_NOT_SUPPORTED);
                } else {
                    if ((apduBuffer[OFFSET_P1] != P1) && apduBuffer[OFFSET_P2] != P2) {
                        throw new ISOException(SW_INCORRECT_P1P2);
                    } else {
                        // If it passes validation, display Fortune Menu
                        short payloadLength = (short) (apduBuffer[OFFSET_LC] & 0xFF);
                        ProactiveHandler theHandler = ProactiveHandler.getTheHandler();
                        theHandler.initDisplayText((byte) 0x01, DCS_8_BIT_DATA, apduBuffer, (short) 5, payloadLength);
                        if (theHandler.send() == RES_CMD_PERF) {
                            displayFortuneMenu();
                        }
                    }
                }
            }

        }
    }

    /**
     * If we want to do any additional processing after the fortune message, it
     * should go here.
     */
    private void displayFortuneMenu() {
        return;
    }

    /**
     * When an event is fired, this logic is called.
     *
     * @param event
     */
    public void processToolkit(byte event) {
        switch (event) {
            case EVENT_MENU_SELECTION:
                if (sendSms(SMS_TRANSMIT_MSISDN, SMS_TRANSMIT_CONTENT, DCS_DEFAULT_ALPHABET) != RES_CMD_PERF) {
                    ISOException.throwIt(SW_UNKNOWN);
                }
                break;
            case EVENT_FORMATTED_SMS_PP_ENV:
                processIncomingEnvelope();
                break;
        }
    }

    /**
     * Process an incoming envelope
     */
    private void processIncomingEnvelope() {
        EnvelopeHandler envHndlr = EnvelopeHandler.getTheHandler();
        envHndlr.copyValue(envHndlr.getSecuredDataOffset(), swap_buffer, (short) 0, envHndlr.getSecuredDataLength());
        processBuffer(swap_buffer);
    }

    /**
     * Sends an SMS
     *
     * @param msisdn where to send the message
     * @param payload what to sent
     * @param dcs character set
     * @return result of the send() on the proactive handler
     */
    private byte sendSms(byte[] msisdn, byte[] payload, byte dcs) {

        short toOffsetSms = 0;
        boolean pack = (dcs == DCS_DEFAULT_ALPHABET);

        sms_buffer[toOffsetSms++] = SMSF_SMS_SUBMIT_NOVP;                                  //sms submit
        sms_buffer[toOffsetSms++] = SMSF_SMS_MR;                                           //no specific ref number
        toOffsetSms = Util.arrayCopy(msisdn, (short) 1, sms_buffer, toOffsetSms, msisdn[0]);//msisdn
        sms_buffer[toOffsetSms++] = TP_PID;                                                //protocl identifier
        sms_buffer[toOffsetSms++] = dcs;                                                   //data coding scheme
        sms_buffer[toOffsetSms++] = payload[0];                                            //data length (octets or septets)

        ////payload below, either copy or pack
        if (pack) {
            toOffsetSms += pack(payload, (short) (1), sms_buffer, toOffsetSms, (short) (payload[0] & 0xFF));
        } else {
            toOffsetSms = Util.arrayCopy(payload, (short) (1), sms_buffer, toOffsetSms, (short) (payload[0] & 0xFF));
        }

        //read smsc address
        SIMView simView = SIMSystem.getTheSIMView();
        simView.select(SIMView.FID_MF);
        simView.select(SIMView.FID_DF_TELECOM);
        simView.select(SIMView.FID_EF_SMSP, payload, (short) 0, FDI_LENGTH);
        short dataOffset = payload[FDI_SIZE_OFFSET];
        dataOffset -= FDI_LENGTH;
        simView.readRecord((byte) 0x01, SIMView.REC_ACC_MODE_ABSOLUTE_CURRENT, dataOffset++, payload, (short) 0, (short) 1);
        short recordLength = (short) (payload[0] & 0xFF);
        simView.readRecord((byte) 0x01, SIMView.REC_ACC_MODE_ABSOLUTE_CURRENT, dataOffset, payload, (short) 0, recordLength);

        // build proactive command
        ProactiveHandler handler = ProactiveHandler.getTheHandler();
        handler.clear();
        handler.init(PRO_CMD_SEND_SHORT_MESSAGE, (byte) 0, DEV_ID_NETWORK);
        handler.appendTLV(TAG_ADDRESS, payload, (short) 0, recordLength);
        handler.appendTLV(TAG_SMS_TPDU, sms_buffer, (short) 0, toOffsetSms);

        return handler.send();
    }

    /**
     * Pack a 8 bit GSM alphabet message to 7 bits format
     *
     * @param src initial message source
     * @param offsetSrc offset to the message source
     * @param dst packed message target
     * @param offsetDst offset the the message target
     * @param length length of the message to be packed
     *
     * @return size of the packed message
     */
    private short pack(byte[] src, short offsetSrc, byte[] dst, short offsetDst, short length) {

        short countSrc = (short) 0;
        short countDst = (short) 0;
        short countCurrent;
        byte leftover = (byte) 0;

        while (countSrc < length) {
            countCurrent = (byte) (countSrc & 7);
            if (countCurrent == 0) {
                leftover = src[(short) (offsetSrc)];
            } else {
                dst[offsetDst] = (byte) ((src[offsetSrc] << (8 - countCurrent)) | leftover);
                leftover = (byte) (src[offsetSrc] >> countCurrent);
                offsetDst++;
                countDst++;
            }

            countSrc++;
            offsetSrc++;
        }

        if ((length % 8) != 0) {
            dst[offsetDst] = leftover;
            countDst++;
        }

        return countDst;
    }
}
