# 充电站系统测试指南

## 🚨 测试问题解决方案

您遇到的 `Could not find a valid Docker environment` 错误是因为原始的集成测试依赖Docker Testcontainers。我已经为您创建了**无需Docker**的替代解决方案。

## 📋 测试环境选项

### 选项1：MySQL直连测试 ✅ **推荐**

**优点**：使用您真实的MySQL数据库，更接近生产环境
**前提**：确保MySQL服务正在运行

#### 配置文件已自动创建：
- `src/test/resources/application-mysql-test.yml` - 使用您的MySQL配置
- `src/test/java/com/emsp/chargingstation/integration/ChargingStationMySQLIntegrationTest.java`

#### 运行方式：

```bash
# 方式1：在IDE中直接运行
# 打开 ChargingStationMySQLIntegrationTest.java
# 右键点击 testCompleteChargingStationWorkflow 方法
# 选择 "Run test"

# 方式2：使用开发脚本（如果Maven已配置）
./scripts/dev.sh test -Dtest=ChargingStationMySQLIntegrationTest

# 方式3：使用Maven命令（如果Maven已配置）
mvn test -Dtest=ChargingStationMySQLIntegrationTest
```

### 选项2：H2内存数据库测试 ⚡

**优点**：无需任何外部数据库，最简单
**缺点**：不是真实MySQL环境

#### 运行方式：

```bash
# 运行现有的单元测试（使用H2）
# 在IDE中运行这些测试类：
# - EvseIdTest
# - EvseStatusTest  
# - LocationServiceTest
```

## 🔧 环境配置检查

### 1. 确认MySQL连接

```bash
# 测试MySQL连接
mysql -h localhost -u root -p
输入密码：you password

# 检查数据库是否存在
SHOW DATABASES;
# 如果没有chargingstation数据库，创建它：
CREATE DATABASE chargingstation CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Maven环境检查

```bash
# 检查Java版本
java -version
# 应该显示Java 17或更高版本

# 检查Maven版本（如果已安装）
mvn -version

# 如果Maven未安装，请下载安装：
# https://maven.apache.org/download.cgi
```

## 🧪 测试场景说明

### MySQL集成测试包含以下验证：

1. **完整业务流程测试**
   - ✅ 创建充电站点
   - ✅ 添加EVSE设备 
   - ✅ 添加充电连接器
   - ✅ EVSE状态转换管理
   - ✅ 查询完整信息（关联查询）

2. **地理边界查询测试**
   - ✅ 创建多个不同位置的充电站
   - ✅ 验证地理范围查询功能

3. **业务规则验证**
   - ✅ 无效状态转换拒绝
   - ✅ 重复EVSE ID检测

4. **数据一致性**
   - ✅ 事务回滚确保测试间隔离
   - ✅ 自动创建/删除表结构

## 📊 测试数据示例

测试会自动创建以下测试数据：

```json
{
  "location": {
    "name": "MySQL测试充电站",
    "address": "北京市朝阳区MySQL测试路123号",
    "latitude": 39.9042,
    "longitude": 116.4074,
    "openingTime": "06:00",
    "closingTime": "22:00"
  },
  "evse": {
    "evseId": "CN*MYS*EVSE001",
    "status": "AVAILABLE"
  },
  "connector": {
    "standard": "CCS2",
    "maxPowerKw": 120.0,
    "voltage": 400,
    "amperage": 300
  }
}
```

## 🐛 常见问题排除

### 问题1：MySQL连接失败
```
原因：MySQL服务未启动或连接配置错误
解决：
1. 启动MySQL服务
2. 验证用户名密码：root / Rtgk@2022.07~c1
3. 确认数据库chargingstation存在
```

### 问题2：表不存在错误
```
原因：测试配置使用create-drop，会自动创建表
解决：确认application-mysql-test.yml配置正确
```

### 问题3：Maven命令找不到
```
原因：Maven未安装或不在PATH中
解决：
1. 下载安装Maven 3.6+
2. 配置MAVEN_HOME和PATH环境变量
3. 或者直接在IDE中运行测试
```

## 🚀 快速验证步骤

### 1. 最快验证方式（IDE）

1. 启动应用：运行 `ChargingStationServiceApplication`
2. 访问Swagger UI：`http://localhost:8081/charging-station/swagger-ui.html`
3. 手动测试API接口

### 2. 自动化测试验证

1. 确保MySQL运行
2. 在IDE中打开 `ChargingStationMySQLIntegrationTest`
3. 右键运行 `testCompleteChargingStationWorkflow`
4. 查看测试结果

### 3. API功能验证

```bash
# 1. 启动应用后，创建充电站
curl -X POST http://localhost:8081/charging-station/api/v1/locations \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试充电站",
    "address": "北京市朝阳区测试路123号", 
    "latitude": 39.9042,
    "longitude": 116.4074,
    "is24Hours": true
  }'

# 2. 查看所有充电站
curl http://localhost:8081/charging-station/api/v1/locations
```

## 📝 测试报告

成功运行测试后，您将看到：

✅ **创建充电站点** - HTTP 201 Created  
✅ **添加EVSE设备** - HTTP 201 Created, Status=AVAILABLE  
✅ **添加充电连接器** - HTTP 201 Created, Standard=CCS2  
✅ **状态转换** - AVAILABLE→BLOCKED→AVAILABLE  
✅ **关联查询** - 完整层级数据返回  
✅ **业务规则** - 无效操作正确拒绝  

## 🎯 下一步

1. **运行MySQL集成测试** - 验证核心功能
2. **手动API测试** - 使用Swagger UI
3. **数据库查看** - 检查表结构和数据
4. **性能测试** - 使用提供的负载测试脚本

---

如果仍有问题，请确保：
- ✅ MySQL服务运行正常
- ✅ 数据库 `chargingstation` 存在  
- ✅ 用户权限正确配置
- ✅ 应用可以成功启动 