package com.hivecloud.gossip.core;

import com.hivecloud.gossip.model.GossipNode;

import java.util.List;

public interface NodeDiscovery {

    void registerNode(GossipNode node);

    void removeNode(String nodeId);

    List<GossipNode> getActiveNodes();

    List<GossipNode> selectRandomPeers(int count);
}
