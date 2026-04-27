# Contributing to HiveCloud

首先，感谢您考虑为 HiveCloud 做出贡献！

## 行为准则

本项目采用《贡献者公约》行为准则。通过参与本项目，您期望遵守此准则。请在 [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) 中阅读完整内容。

## 如何贡献

### 报告 Bug

如果您发现了 Bug，请创建一个 Issue 并包含以下信息：

1. **清晰的标题**：简明扼要地描述问题
2. **复现步骤**：详细说明如何复现该问题
3. **期望行为**：描述您期望发生什么
4. **实际行为**：描述实际发生了什么
5. **环境信息**：包括 JDK 版本、Maven 版本、操作系统等
6. **日志信息**：如有错误日志，请一并提供

### 提出新功能

如果您有新功能的想法，请先创建一个 Issue 讨论：

1. **功能描述**：详细描述新功能
2. **使用场景**：说明为什么需要这个功能
3. **实现思路**：如果有实现思路，可以一并提出
4. **替代方案**：是否考虑过其他解决方案

### 提交代码

1. **Fork 项目**：点击 GitHub 上的 Fork 按钮
2. **创建分支**：从 `develop` 分支创建您的功能分支
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **进行修改**：按照代码规范进行修改
4. **提交更改**：使用有意义的提交信息
   ```bash
   git commit -m "[FEATURE] Add your feature description"
   ```
5. **推送到分支**：
   ```bash
   git push origin feature/your-feature-name
   ```
6. **创建 Pull Request**：在 GitHub 上创建 PR

### 代码规范

请遵循以下代码规范：

1. **命名规范**：
   - 类名使用大驼峰命名法（PascalCase）
   - 方法名和变量名使用小驼峰命名法（camelCase）
   - 常量名使用全大写下划线分隔（UPPER_SNAKE_CASE）

2. **注释规范**：
   - 所有公共类、方法必须有 Javadoc 注释
   - 复杂逻辑必须有注释说明
   - 使用 `@author` 标注作者
   - 使用 `@date` 标注日期

3. **日志规范**：
   - 使用 SLF4J 日志框架
   - 使用占位符打印参数
   - 合理使用日志级别（ERROR, WARN, INFO, DEBUG）

4. **异常处理**：
   - 不要吞掉异常
   - 使用统一的异常处理机制
   - 提供有意义的错误信息

### 测试要求

1. **单元测试**：核心功能必须有单元测试
2. **集成测试**：涉及多个模块的功能需要集成测试
3. **测试覆盖率**：核心模块测试覆盖率应达到 80% 以上

### Pull Request 流程

1. **代码审查**：所有 PR 都需要经过至少一位维护者审查
2. **CI 检查**：确保所有 CI 检查通过（构建、测试、代码规范）
3. **合并**：审查通过后，由维护者合并到 `develop` 分支

## 开发环境设置

### 前置要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 安装步骤

1. **克隆项目**：
   ```bash
   git clone https://github.com/zhulang-tianya/hivecloud-ms.git
   cd hivecloud-ms
   ```

2. **编译项目**：
   ```bash
   mvn clean install -DskipTests
   ```

3. **配置数据库**：
   - 创建 MySQL 数据库
   - 执行 `docs/guides/schema.sql` 初始化表结构

4. **配置 Redis**：
   - 启动 Redis 服务
   - 修改配置文件中的 Redis 连接信息

5. **启动服务**：
   ```bash
   java -jar hivecloud-framework/hivecloud-service-registry/target/*.jar
   ```

## 发布流程

### 版本号规范

遵循语义化版本号（Semantic Versioning）：`MAJOR.MINOR.PATCH`

- **MAJOR**：不兼容的 API 变更
- **MINOR**：向后兼容的功能性新增
- **PATCH**：向后兼容的问题修正

### 发布步骤

1. **更新版本号**：修改 `pom.xml` 中的版本号
2. **更新 CHANGELOG**：记录本次发布的所有变更
3. **创建 Release Branch**：
   ```bash
   git checkout -b release/v1.0.0
   ```
4. **最终测试**：进行全面的回归测试
5. **合并到主分支**：
   ```bash
   git checkout main
   git merge release/v1.0.0
   git tag -a v1.0.0 -m "Release version 1.0.0"
   ```
6. **发布到 Maven Central**：执行 Maven 发布命令
7. **创建 GitHub Release**：在 GitHub 上创建 Release

## 社区

- **GitHub Issues**：报告 Bug 和提出新功能
- **GitHub Discussions**：讨论和交流
- **邮件列表**：重要公告和讨论

## 许可证

通过贡献代码，您同意您的贡献遵循本项目的 [Apache 2.0 许可证](LICENSE)。

## 感谢

感谢所有为 HiveCloud 做出贡献的开发者！
