package com.chatmodule.xmpp.readMsg;

import android.util.Log;

import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class ReadReceiptManager implements PacketListener {


    private static Map<XMPPConnection, ReadReceiptManager> instances = Collections.synchronizedMap(new WeakHashMap<XMPPConnection, ReadReceiptManager>());

    private Set<ReceiptReceivedListener> receiptReceivedListeners = Collections.synchronizedSet(new HashSet<ReceiptReceivedListener>());

    private ReadReceiptManager(XMPPConnection connection) {
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        sdm.addFeature(ReadReceipt.NAMESPACE);
        instances.put(connection, this);

        connection.addAsyncPacketListener(this, new PacketExtensionFilter(ReadReceipt.NAMESPACE));
    }

    public static synchronized ReadReceiptManager getInstanceFor(XMPPConnection connection) {
        ReadReceiptManager receiptManager = instances.get(connection);

        if (receiptManager == null) {
            receiptManager = new ReadReceiptManager(connection);
        }

        return receiptManager;
    }

    public void addReadReceivedListener(ReceiptReceivedListener listener) {
        receiptReceivedListeners.add(listener);
    }

    public void removeRemoveReceivedListener(ReceiptReceivedListener listener) {
        receiptReceivedListeners.remove(listener);
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        Log.e("processPacket", "processPacket  "+packet);

        ReadReceipt dr = (ReadReceipt) packet.getExtension(ReadReceipt.ELEMENT, ReadReceipt.NAMESPACE);

        if (dr != null) {
            for (ReceiptReceivedListener l : receiptReceivedListeners) {
                l.onReceiptReceived(packet.getFrom(), packet.getTo(), dr.getId(), packet);
            }
        }
    }
}