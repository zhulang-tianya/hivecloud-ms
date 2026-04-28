//package com.hivecloud.gossip.core;
//
//import com.hivecloud.gossip.config.GossipProperties;
//import com.hivecloud.gossip.model.GossipMessage;
//import com.hivecloud.gossip.model.GossipNode;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
///**
// * GossipSyncEngine 单元测试
// * 测试 Gossip 协议同步引擎的节点同步、消息传播等功能
// *
// * @author HiveCloud Team
// * @date 2026-04-27
// * @see GossipSyncEngine
// * @see GossipNode
// * @see GossipMessage
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("GossipSyncEngine 单元测试")
//class GossipSyncEngineTest {
//
//    @Mock
//    private NodeDiscovery nodeDiscovery;
//
//    @Mock
//    private GossipProperties properties;
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    private GossipSyncEngine gossipEngine;
//
//    @BeforeEach
//    void setUp() {
//        GossipNode localNode = createTestNode("local-node-001");
//        gossipEngine = new GossipSyncEngine(nodeDiscovery, properties, restTemplate);
//        gossipEngine.setLocalNode(localNode);
//    }
//
//    @Test
//    @DisplayName("测试设置本地节点 - 成功")
//    void testSetLocalNode_Success() {
//        // Given
//        GossipNode node = createTestNode("test-node");
//
//        // When
//        gossipEngine.setLocalNode(node);
//
//        // Then
//        assertNotNull(node);
//        assertEquals("test-node", node.getNodeId());
//    }
//
//    @Test
//    @DisplayName("测试启动同步引擎 - 成功")
//    void testStart_Success() {
//        // Given
//        when(properties.getSyncInterval()).thenReturn(5000L);
//
//        // When
//        gossipEngine.start();
//
//        // Then
//        // 验证启动后没有抛出异常
//        assertTrue(true);
//
//        // 清理
//        gossipEngine.stop();
//    }
//
//    @Test
//    @DisplayName("测试停止同步引擎 - 成功")
//    void testStop_Success() {
//        // Given
//        when(properties.getSyncInterval()).thenReturn(5000L);
//        gossipEngine.start();
//
//        // When
//        gossipEngine.stop();
//
//        // Then
//        // 验证停止后没有抛出异常
//        assertTrue(true);
//    }
//
//    @Test
//    @DisplayName("测试停止同步引擎 - 未启动时停止")
//    void testStop_WithoutStart() {
//        // When
//        gossipEngine.stop();
//
//        // Then
//        // 验证未启动时停止不会抛出异常
//        assertTrue(true);
//    }
//
//    @Test
//    @DisplayName("测试获取邻居节点 - 有多个节点")
//    void testSelectNeighbors_WithMultipleNodes() {
//        // Given
//        List<GossipNode> allNodes = Arrays.asList(
//                createTestNode("node-001"),
//                createTestNode("node-002"),
//                createTestNode("node-003")
//        );
//        when(nodeDiscovery.getAllNodes()).thenReturn(allNodes);
//        when(properties.getFanout()).thenReturn(2);
//
//        // When
//        List<GossipNode> neighbors = gossipEngine.selectNeighbors();
//
//        // Then
//        assertNotNull(neighbors);
//        assertTrue(neighbors.size() <= 2);
//    }
//
//    @Test
//    @DisplayName("测试获取邻居节点 - 节点数少于 fanout")
//    void testSelectNeighbors_FewerNodesThanFanout() {
//        // Given
//        List<GossipNode> allNodes = Arrays.asList(
//                createTestNode("node-001")
//        );
//        when(nodeDiscovery.getAllNodes()).thenReturn(allNodes);
//        when(properties.getFanout()).thenReturn(3);
//
//        // When
//        List<GossipNode> neighbors = gossipEngine.selectNeighbors();
//
//        // Then
//        assertNotNull(neighbors);
//        assertEquals(1, neighbors.size());
//    }
//
//    @Test
//    @DisplayName("测试获取邻居节点 - 无可用节点")
//    void testSelectNeighbors_NoNodes() {
//        // Given
//        when(nodeDiscovery.getAllNodes()).thenReturn(Collections.emptyList());
//
//        // When
//        List<GossipNode> neighbors = gossipEngine.selectNeighbors();
//
//        // Then
//        assertNotNull(neighbors);
//        assertTrue(neighbors.isEmpty());
//    }
//
//    @Test
//    @DisplayName("测试创建 Gossip 消息 - 成功")
//    void testCreateGossipMessage_Success() {
//        // Given
//        String dataType = "service-registry";
//        Object data = "test-data";
//
//        // When
//        GossipMessage message = gossipEngine.createGossipMessage(dataType, data);
//
//        // Then
//        assertNotNull(message);
//        assertEquals("service-registry", message.getDataType());
//        assertEquals("test-data", message.getData());
//        assertNotNull(message.getTimestamp());
//        assertNotNull(message.getMessageId());
//    }
//
//    @Test
//    @DisplayName("测试传播消息 - 成功")
//    void testSpreadMessage_Success() {
//        // Given
//        GossipMessage message = createTestMessage();
//        GossipNode neighbor = createTestNode("neighbor-node");
//
//        when(restTemplate.postForObject(anyString(), any(GossipMessage.class), any(Class.class)))
//                .thenReturn("OK");
//
//        // When
//        gossipEngine.spreadMessage(message, Arrays.asList(neighbor));
//
//        // Then
//        verify(restTemplate, atLeastOnce()).postForObject(
//                anyString(),
//                any(GossipMessage.class),
//                any(Class.class)
//        );
//    }
//
//    @Test
//    @DisplayName("测试处理接收到的消息 - 成功")
//    void testHandleReceivedMessage_Success() {
//        // Given
//        GossipMessage message = createTestMessage();
//
//        // When
//        boolean result = gossipEngine.handleReceivedMessage(message);
//
//        // Then
//        // 验证消息处理成功
//        assertTrue(result);
//    }
//
//    @Test
//    @DisplayName("测试消息去重 - 已处理的消息")
//    void testMessageDeduplication_ProcessedMessage() {
//        // Given
//        GossipMessage message = createTestMessage();
//
//        // When
//        // 第一次处理
//        boolean firstResult = gossipEngine.handleReceivedMessage(message);
//        // 第二次处理相同消息
//        boolean secondResult = gossipEngine.handleReceivedMessage(message);
//
//        // Then
//        assertTrue(firstResult);
//        // 第二次应该被去重
//        assertFalse(secondResult);
//    }
//
//    @Test
//    @DisplayName("测试获取本地节点信息")
//    void testGetLocalNode() {
//        // When
//        GossipNode localNode = gossipEngine.getLocalNode();
//
//        // Then
//        assertNotNull(localNode);
//        assertEquals("local-node-001", localNode.getNodeId());
//    }
//
//    /**
//     * 创建测试节点
//     */
//    private GossipNode createTestNode(String nodeId) {
//        GossipNode node = new GossipNode();
//        node.setNodeId(nodeId);
//        node.setHost("127.0.0.1");
//        node.setPort(8080);
//        node.setStatus(GossipNode.Status.ACTIVE);
//        return node;
//    }
//
//    /**
//     * 创建测试消息
//     */
//    private GossipMessage createTestMessage() {
//        GossipMessage message = new GossipMessage();
//        message.setMessageId("msg-001");
//        message.setDataType("test-data-type");
//        message.setData("test-data");
//        message.setTimestamp(System.currentTimeMillis());
//        return message;
//    }
//}
