package com.hivecloud.gossip.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GossipNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nodeId;

    private String ip;

    private Integer port;

    private String status;

    private Long lastSyncTime;

    private Integer version;
}
