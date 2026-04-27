package com.hivecloud.plugin.ai.service;

/**
 * AI 智能客服接口
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
public interface AiService {

    /**
     * 智能问答
     *
     * @param question 问题
     * @return 回答
     */
    String answer(String question);

    /**
     * 风险评估
     *
     * @param userId 用户 ID
     * @return 风险分数
     */
    Integer assessRisk(Long userId);
}
