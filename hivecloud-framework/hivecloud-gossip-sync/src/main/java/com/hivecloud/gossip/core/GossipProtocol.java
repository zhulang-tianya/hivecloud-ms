package com.hivecloud.gossip.core;

import com.hivecloud.gossip.model.GossipMessage;

public interface GossipProtocol {

    void sendMessage(GossipMessage message);

    void receiveMessage(GossipMessage message);

    void selectPeersAndSpread(GossipMessage message);
}
