# HiveCloud 项目文档目录

> **创建日期**: 2026-04-27  
> **最后更新**: 2026-04-27

---

## 📁 目录结构

```
docs/
├── architecture/          # 架构设计文档
│   └── ARCHITECTURE-ASSESSMENT.md
├── design/               # 详细设计文档
├── api/                  # API 接口文档
├── optimization/         # 优化任务文档
│   ├── T1-OPTIMIZATION-TASK.md
│   ├── T1-OPTIMIZATION-SUMMARY.md
│   ├── T1-OPT-EXEC-SUMMARY.md
│   └── T1-OPT-RECHECK-SUMMARY.md
├── progress/             # 进度跟踪文档
│   ├── T1.1-JAVADOC-TRACKING.md
│   └── T1.4-UNIT-TEST-PLAN.md
└── reports/              # 评估报告
    └── RECHECK-REPORT.md
```

---

## 📋 文档分类说明

### architecture/ - 架构设计文档

存放系统架构相关的文档：
- 架构评估报告
- 架构设计决策
- 技术选型文档
- 部署架构图
- 服务调用流程图

**当前文档**:
- [ARCHITECTURE-ASSESSMENT.md](./architecture/ARCHITECTURE-ASSESSMENT.md) - 架构评估报告
- [DEVELOPMENT_PLAN.md](./architecture/DEVELOPMENT_PLAN.md) - 架构设计开发计划
- [IMPLEMENTATION_REPORT.md](./architecture/IMPLEMENTATION_REPORT.md) - 项目实现分析报告
- [hivecloud-architecture.html](./architecture/hivecloud-architecture.html) - 整体架构图（可视化）
- [hivecloud-deployment.html](./architecture/hivecloud-deployment.html) - 部署架构图（可视化）
- [hivecloud-service-flow.html](./architecture/hivecloud-service-flow.html) - 服务调用与数据流图（可视化）

---

### design/ - 详细设计文档

存放详细设计相关的文档：
- 模块设计文档
- 数据库设计文档
- 接口设计文档

**当前文档**:
- （待补充）

---

### api/ - API 接口文档

存放 API 接口相关的文档：
- RESTful API 文档
- Swagger/OpenAPI 规范
- 接口调用示例

**当前文档**:
- （待补充）

---

### optimization/ - 优化任务文档

存放代码优化相关的文档：
- 优化任务计划
- 优化执行总结
- 优化效果评估

**当前文档**:
- [T1-OPTIMIZATION-TASK.md](./optimization/T1-OPTIMIZATION-TASK.md) - T1 优化任务计划
- [T1-OPTIMIZATION-SUMMARY.md](./optimization/T1-OPTIMIZATION-SUMMARY.md) - T1 优化任务总结
- [T1-OPT-EXEC-SUMMARY.md](./optimization/T1-OPT-EXEC-SUMMARY.md) - T1 优化执行摘要
- [T1-OPT-RECHECK-SUMMARY.md](./optimization/T1-OPT-RECHECK-SUMMARY.md) - T1 优化复核总结

---

### progress/ - 进度跟踪文档

存放项目进度跟踪文档：
- 任务进度跟踪
- 里程碑报告
- 迭代计划

**当前文档**:
- [T1.1-JAVADOC-TRACKING.md](./progress/T1.1-JAVADOC-TRACKING.md) - T1.1 Javadoc 注释规范化进度
- [T1.4-UNIT-TEST-PLAN.md](./progress/T1.4-UNIT-TEST-PLAN.md) - T1.4 单元测试建设计划

---

### reports/ - 评估报告

存放各类评估报告：
- 代码质量评估
- 性能测试报告
- 安全审计报告

**当前文档**:
- [RECHECK-REPORT.md](./reports/RECHECK-REPORT.md) - 架构评估复核报告

---

## 📝 文档管理规范

### 命名规范

1. **架构文档**: `ARCH-{主题}.md`
   - 示例：`ARCH-MICROSERVICE-DESIGN.md`

2. **设计文档**: `DESIGN-{模块名}.md`
   - 示例：`DESIGN-USER-MODULE.md`

3. **API 文档**: `API-{服务名}.md`
   - 示例：`API-SYSTEM-SERVICE.md`

4. **优化文档**: `T{n}-OPT-{内容}.md`
   - 示例：`T2-OPT-ARCHITECTURE.md`

5. **进度文档**: `T{n}.{m}-{内容}-TRACKING.md`
   - 示例：`T1.1-JAVADOC-TRACKING.md`

6. **报告文档**: `RPT-{内容}.md`
   - 示例：`RPT-CODE-QUALITY.md`

### 版本控制

- 所有文档都应纳入 Git 版本控制
- 重大变更应在文档头部更新"最后更新"日期
- 重要版本变更应添加版本号

### 文档模板

#### 架构文档模板

```markdown
# {文档标题}

> **文档编号**: {编号}  
> **版本**: {版本号}  
> **创建日期**: {日期}  
> **最后更新**: {日期}  
> **负责人**: {姓名}

---

## 一、概述

{简要描述}

## 二、{主要内容}

{详细内容}

## 三、总结

{总结内容}
```

---

## 🔗 相关链接

- [项目 README](../README.md)
- [技能规范](../SKILLS.md)

---

**文档维护**: HiveCloud Team  
**联系方式**: hivecloud-team@example.com
