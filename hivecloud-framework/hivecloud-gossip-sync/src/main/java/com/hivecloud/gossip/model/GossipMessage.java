package com.hivecloud.gossip.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class GossipMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String messageId;

    private String sourceNodeId;

    private String messageType;

    private Map<String, Object> payload;

    private Long timestamp;

    private Integer version;
}
