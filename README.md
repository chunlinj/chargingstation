# 电动汽车充电站管理系统

[![CI/CD Pipeline](https://github.com/chunlinj/charging-station-service/workflows/CI/CD%20Pipeline/badge.svg)](https://github.com/chunlinj/chargingstation/actions)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

基于领域驱动设计(DDD)和事件驱动架构(EDD)的电动汽车充电站信息管理系统，作为电动移动服务提供商(eMSP)维护充电站运营商(CPO)数据并提供统一API。

## 🏗️ 系统架构

- **领域驱动设计(DDD)** - 清晰的领域模型和业务逻辑分离
- **事件驱动架构(EDD)** - 解耦的组件通信和状态变更通知
- **微服务架构** - 基于Spring Cloud的可扩展服务
- **RESTful API** - 标准的HTTP API接口

## 🚀 核心功能

### 领域实体

- **Location (充电站点)** - 包含名称、地址、坐标、营业时间
- **EVSE (电动汽车供电设备)** - 支持OCPI标准ID格式，状态管理
- **Connector (充电连接器)** - 技术参数如标准、功率、电压

### 业务规则

- **EVSE ID格式**: `<CountryCode>*<PartyID>*<LocalEVSEID>` (如: `US*ABC*EVSE123456`)
- **状态转换**: INITIAL → AVAILABLE ↔ BLOCKED/INOPERATIVE → REMOVED(不可逆)
- **数据验证**: 完整的输入验证和格式检查

## 📋 前置要求

- Java 17 或更高版本
- Maven 3.6 或更高版本  
- Docker 和 Docker Compose (可选)
- MySQL 8.0 或更高版本 (生产环境)

## 🛠️ 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd charging-station-service
```

### 2. 运行开发环境

使用提供的开发脚本：

```bash
# 检查前置条件(linux环境下)
./scripts/dev.sh check-prereq

# 编译和测试
./scripts/dev.sh ci

# 启动应用 (使用H2内存数据库)
./scripts/dev.sh run
```

### 3. 使用Docker Compose

```bash
# 启动完整环境 (MySQL + 应用)
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

## 📚 API 文档

### 基础信息

- **基础URL**: `http://localhost:8081/charging-station`
- **API版本**: v1
- **认证**: 无需认证 (开发环境)
- **数据格式**: JSON
- **Swagger UI**: `http://localhost:8081/charging-station/swagger-ui.html`

### Location API (充电站点管理)

#### 创建充电站点
```http
POST /api/v1/locations
Content-Type: application/json

{
  "name": "Tesla Supercharger Station",
  "address": "123 Main Street, San Francisco, CA",
  "latitude": 37.7749,
  "longitude": -122.4194,
  "openingTime": "06:00",
  "closingTime": "22:00",
  "is24Hours": false,
  "description": "24/7 Tesla Supercharger station"
}
```

#### 获取充电站点详情
```http
GET /api/v1/locations/{locationId}
```

#### 获取所有充电站点 (分页)
```http
GET /api/v1/locations?page=0&size=10&sort=name
```

#### 搜索充电站点
```http
GET /api/v1/locations/search?name=Tesla&page=0&size=10
```

#### 地理边界查询
```http
GET /api/v1/locations/bounds?minLatitude=37.0&maxLatitude=38.0&minLongitude=-123.0&maxLongitude=-122.0
```

#### 时间戳增量查询
```http
GET /api/v1/locations/updated-after?lastUpdated=2024-01-01T00:00:00Z&page=0&size=10
```

#### 更新充电站点
```http
PUT /api/v1/locations/{locationId}
Content-Type: application/json

{
  "name": "Updated Station Name",
  "description": "Updated description",
  "is24Hours": true
}
```

#### 删除充电站点
```http
DELETE /api/v1/locations/{locationId}
```

### EVSE API (电动汽车供电设备管理)

#### 为充电站点添加EVSE
```http
POST /api/v1/locations/{locationId}/evses
Content-Type: application/json

{
  "evseId": "US*ABC*EVSE123456",
  "description": "Tesla Supercharger Unit 1"
}
```

#### 获取EVSE详情 (通过内部ID)
```http
GET /api/v1/evses/{evseId}
```

#### 获取EVSE详情 (通过EVSE标识符)
```http
GET /api/v1/evses/evse-id/US*ABC*EVSE123456
```

#### 获取充电站点的所有EVSE
```http
GET /api/v1/locations/{locationId}/evses
```

#### 按状态查询EVSE
```http
GET /api/v1/evses?status=AVAILABLE&page=0&size=10
```

#### 更新EVSE状态
```http
PATCH /api/v1/evses/{evseId}/status
Content-Type: application/json

{
  "status": "BLOCKED"
}
```

**可用状态**:
- `INITIAL` - 初始状态
- `AVAILABLE` - 可用
- `BLOCKED` - 被阻塞  
- `INOPERATIVE` - 不可操作
- `REMOVED` - 已移除 (终端状态)

#### 删除EVSE
```http
DELETE /api/v1/evses/{evseId}
```

### Connector API (充电连接器管理)

#### 为EVSE添加连接器
```http
POST /api/v1/evses/{evseId}/connectors
Content-Type: application/json

{
  "standard": "CCS2",
  "maxPowerKw": 150.0,
  "voltage": 400,
  "amperage": 375,
  "description": "CCS2 DC Fast Charger"
}
```

**连接器标准**:
- `CCS1` - Combined Charging System 1
- `CCS2` - Combined Charging System 2
- `CHADEMO` - CHAdeMO
- `TYPE_1` - Type 1 (SAE J1772)
- `TYPE_2` - Type 2 (IEC 62196)
- `TYPE_3` - Type 3 (Scame)
- `TESLA_SUPERCHARGER` - Tesla Supercharger
- `TESLA_DESTINATION` - Tesla Destination
- `GBT_AC` - GB/T AC
- `GBT_DC` - GB/T DC

#### 获取连接器详情
```http
GET /api/v1/connectors/{connectorId}
```

#### 获取EVSE的所有连接器
```http
GET /api/v1/evses/{evseId}/connectors
```

#### 删除连接器
```http
DELETE /api/v1/connectors/{connectorId}
```

## 🧪 测试指南

> **⚠️ 遇到测试问题？** 如果遇到Docker环境错误或其他测试问题，请查看详细的 **[测试指南](TESTING_GUIDE.md)** 获取解决方案。

### 1. 使用Swagger UI进行交互式测试

访问 `http://localhost:8081/charging-station/swagger-ui.html` 进行可视化API测试。

### 2. 自动化集成测试

我们提供了**两种**集成测试方式：

#### 选项A：MySQL直连测试 ✅ **推荐**
```bash
在IDE中运行（推荐）
打开: src/test/java/com/emsp/chargingstation/integration/ChargingStationMySQLIntegrationTest.java
右键运行: testCompleteChargingStationWorkflow

# 或使用Maven（需要Maven环境）
mvn test -Dtest=ChargingStationMySQLIntegrationTest
```

#### 选项B：单元测试（H2内存数据库，默认使用dev）
```bash
# 运行单元测试
./scripts/dev.sh test

# 或运行特定测试类
mvn test -Dtest=EvseIdTest
mvn test -Dtest=LocationServiceTest
```

### 3. 使用cURL命令行测试

#### 创建完整的充电站示例

```bash
# 1. 创建充电站点
LOCATION_RESPONSE=$(curl -s -X POST http://localhost:8081/charging-station/api/v1/locations \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试充电站",
    "address": "北京市朝阳区测试路123号",
    "latitude": 39.9042,
    "longitude": 116.4074,
    "openingTime": "06:00",
    "closingTime": "22:00",
    "description": "测试用充电站"
  }')

LOCATION_ID=$(echo $LOCATION_RESPONSE | jq -r '.id')
echo "创建的充电站ID: $LOCATION_ID"

# 2. 为充电站添加EVSE
EVSE_RESPONSE=$(curl -s -X POST http://localhost:8081/charging-station/api/v1/locations/$LOCATION_ID/evses \
  -H "Content-Type: application/json" \
  -d '{
    "evseId": "CN*TST*EVSE001",
    "description": "测试EVSE设备1号"
  }')

EVSE_ID=$(echo $EVSE_RESPONSE | jq -r '.id')
echo "创建的EVSE ID: $EVSE_ID"

# 3. 为EVSE添加连接器
CONNECTOR_RESPONSE=$(curl -s -X POST http://localhost:8081/charging-station/api/v1/evses/$EVSE_ID/connectors \
  -H "Content-Type: application/json" \
  -d '{
    "standard": "CCS2",
    "maxPowerKw": 120.0,
    "voltage": 400,
    "amperage": 300,
    "description": "CCS2直流快充接口"
  }')

echo "连接器创建响应: $CONNECTOR_RESPONSE"

# 4. 更新EVSE状态
curl -X PATCH http://localhost:8081/charging-station/api/v1/evses/$EVSE_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status": "AVAILABLE"}'

# 5. 查询充电站完整信息
curl -s http://localhost:8081/charging-station/api/v1/locations/$LOCATION_ID | jq '.'
```

### 4. 负载测试示例

使用Apache Bench进行简单负载测试：

```bash
# 测试获取充电站列表接口
ab -n 1000 -c 10 http://localhost:8081/charging-station/api/v1/locations

# 测试创建充电站接口 (需要先准备数据文件)
ab -n 100 -c 5 -p location-data.json -T application/json \
  http://localhost:8081/charging-station/api/v1/locations
```


## 🐳 Docker部署

### 构建镜像
```bash
docker build -t charging-station-service .
```

### 运行容器
```bash
docker run -d -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/chargingstation?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8" \
  -e DB_USERNAME=chargingstation \
  -e DB_PASSWORD=password \
  charging-station-service
```


### 数据库访问

#### H2控制台 (开发环境)
- URL: `http://localhost:8081/charging-station/h2-console`
- JDBC URL: `jdbc:h2:mem:chargingstation`
- 用户名: `sa`
- 密码: `password`

#### MySQL (生产环境)
```bash
# 连接MySQL
mysql -h localhost -u chargingstation -p password

# 或者使用完整连接字符串
mysql -h localhost -P 3306 -u chargingstation -p \
  --default-character-set=utf8 \
  --ssl-mode=REQUIRED \
  chargingstation
```

## 🚀 CI/CD部署

### GitHub Actions
项目包含完整的CI/CD流水线配置，支持：
- 自动化测试
- 代码质量检查
- Docker镜像构建和推送
- 自动部署到staging和production环境

### 环境变量配置

生产环境需要设置以下环境变量：
```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL="jdbc:mysql://your-mysql-host:3306/chargingstation?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8"
export DB_USERNAME=your-username
export DB_PASSWORD=your-password
# 将SPRING_CLOUD_CONFIG_ENABLED 设置为true 再配置注册中心和config
# export EUREKA_URL=http://your-eureka:8761/eureka/
# export CONFIG_SERVER_URL=http://your-config-server:8888
```

## 📄 错误码说明

| 错误码 | HTTP状态 | 说明 |
|--------|----------|------|
| RESOURCE_NOT_FOUND | 404 | 资源未找到 |
| DUPLICATE_RESOURCE | 409 | 资源已存在 |
| INVALID_STATUS_TRANSITION | 409 | 无效的状态转换 |
| VALIDATION_ERROR | 400 | 请求验证失败 |
| INVALID_ARGUMENT | 400 | 无效参数 |
| TYPE_MISMATCH | 400 | 参数类型不匹配 |
| INTERNAL_SERVER_ERROR | 500 | 内部服务器错误 |

## 🤝 贡献指南

1. Fork项目
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交变更 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启Pull Request

## 📝 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 提交Issue
- 发送邮件至 1499831507@qq.com

---

**快速链接**:
- [Swagger API文档](https://charging-station-service-hrcbddgvh5bqc3de.eastasia-01.azurewebsites.net/charging-station/swagger-ui/index.html)
- [Health Check](https://charging-station-service-hrcbddgvh5bqc3de.eastasia-01.azurewebsites.net/charging-station/actuator/health)