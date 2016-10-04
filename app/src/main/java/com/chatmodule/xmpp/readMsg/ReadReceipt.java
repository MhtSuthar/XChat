package com.chatmodule.xmpp.readMsg;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

import java.util.List;
import java.util.Map;

public class ReadReceipt implements PacketExtension {

    public static final String NAMESPACE = "urn:xmpp:read";
    public static final String ELEMENT = "read";

    private String id; /// original ID of the delivered message

    public ReadReceipt(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String toXML() {
        return "<read xmlns='" + NAMESPACE + "' id='" + id + "'/>";
    }

    public static class Provider extends EmbeddedExtensionProvider {
        @Override
        protected PacketExtension createReturnExtension(String currentElement, String currentNamespace, Map attributeMap, List content) {
            return new ReadReceipt((String) attributeMap.get("id"));
        }
    }
}