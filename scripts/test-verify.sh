#!/bin/bash

# 充电站系统测试验证脚本
# 用于快速检查环境和运行基本测试

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Java环境
check_java() {
    log_info "检查Java环境..."
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
        log_success "Java已安装: $JAVA_VERSION"
        
        # 检查是否为Java 17+
        JAVA_MAJOR=$(echo $JAVA_VERSION | cut -d'.' -f1)
        if [ "$JAVA_MAJOR" -ge 17 ]; then
            log_success "Java版本符合要求 (17+)"
            return 0
        else
            log_warning "Java版本过低，建议使用Java 17+"
            return 1
        fi
    else
        log_error "Java未安装，请安装Java 17+"
        return 1
    fi
}

# 检查Maven环境
check_maven() {
    log_info "检查Maven环境..."
    if command -v mvn &> /dev/null; then
        MAVEN_VERSION=$(mvn -version | head -1)
        log_success "Maven已安装: $MAVEN_VERSION"
        return 0
    else
        log_warning "Maven未安装，某些测试命令可能无法使用"
        log_info "可以在IDE中直接运行测试"
        return 1
    fi
}

# 检查MySQL连接
check_mysql() {
    log_info "检查MySQL连接..."
    
    # 尝试连接MySQL
    if command -v mysql &> /dev/null; then
        # 测试连接（使用配置文件中的连接信息）
        mysql -h localhost -u root -pRtgk@2022.07~c1 -e "SELECT 1;" &> /dev/null
        if [ $? -eq 0 ]; then
            log_success "MySQL连接成功"
            
            # 检查数据库是否存在
            DB_EXISTS=$(mysql -h localhost -u root -pRtgk@2022.07~c1 -e "SHOW DATABASES LIKE 'chargingstation';" --skip-column-names)
            if [ -n "$DB_EXISTS" ]; then
                log_success "数据库 'chargingstation' 已存在"
            else
                log_warning "数据库 'chargingstation' 不存在，将自动创建"
                mysql -h localhost -u root -pRtgk@2022.07~c1 -e "CREATE DATABASE chargingstation CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
                if [ $? -eq 0 ]; then
                    log_success "数据库 'chargingstation' 创建成功"
                else
                    log_error "数据库创建失败"
                    return 1
                fi
            fi
            return 0
        else
            log_error "MySQL连接失败，请检查服务是否启动和密码是否正确"
            log_info "连接信息: host=localhost, user=root, password=Rtgk@2022.07~c1"
            return 1
        fi
    else
        log_error "MySQL客户端未安装"
        return 1
    fi
}

# 检查应用是否运行
check_app() {
    log_info "检查应用是否运行..."
    
    # 检查端口8081是否被监听
    if command -v curl &> /dev/null; then
        curl -s http://localhost:8081/charging-station/actuator/health &> /dev/null
        if [ $? -eq 0 ]; then
            log_success "应用正在运行 (端口8081)"
            return 0
        else
            log_warning "应用未运行，请先启动应用"
            log_info "启动命令: ./scripts/dev.sh run 或在IDE中运行ChargingStationServiceApplication"
            return 1
        fi
    else
        log_warning "curl未安装，无法检查应用状态"
        return 1
    fi
}

# 运行单元测试
run_unit_tests() {
    log_info "运行单元测试..."
    
    if command -v mvn &> /dev/null; then
        log_info "使用Maven运行单元测试..."
        mvn test -Dtest="*Test" -DfailIfNoTests=false
        if [ $? -eq 0 ]; then
            log_success "单元测试通过"
            return 0
        else
            log_error "单元测试失败"
            return 1
        fi
    else
        log_warning "Maven未安装，请在IDE中运行以下测试类："
        echo "  - EvseIdTest"
        echo "  - EvseStatusTest"
        echo "  - LocationServiceTest"
        return 1
    fi
}

# 运行MySQL集成测试
run_mysql_integration_test() {
    log_info "运行MySQL集成测试..."
    
    if command -v mvn &> /dev/null; then
        log_info "使用Maven运行MySQL集成测试..."
        mvn test -Dtest="ChargingStationMySQLIntegrationTest#testCompleteChargingStationWorkflow"
        if [ $? -eq 0 ]; then
            log_success "MySQL集成测试通过"
            return 0
        else
            log_error "MySQL集成测试失败"
            return 1
        fi
    else
        log_warning "Maven未安装，请在IDE中运行："
        echo "  打开: src/test/java/com/emsp/chargingstation/integration/ChargingStationMySQLIntegrationTest.java"
        echo "  右键运行: testCompleteChargingStationWorkflow"
        return 1
    fi
}

# 基本API测试
test_basic_api() {
    log_info "运行基本API测试..."
    
    if ! command -v curl &> /dev/null; then
        log_error "curl未安装，无法进行API测试"
        return 1
    fi
    
    # 测试健康检查
    log_info "测试健康检查端点..."
    HEALTH_RESPONSE=$(curl -s http://localhost:8081/charging-station/actuator/health)
    if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
        log_success "健康检查通过"
    else
        log_error "健康检查失败: $HEALTH_RESPONSE"
        return 1
    fi
    
    # 测试Swagger UI
    log_info "测试Swagger UI..."
    curl -s http://localhost:8081/charging-station/swagger-ui.html &> /dev/null
    if [ $? -eq 0 ]; then
        log_success "Swagger UI可访问"
        log_info "访问地址: http://localhost:8081/charging-station/swagger-ui.html"
    else
        log_warning "Swagger UI不可访问"
    fi
    
    return 0
}

# 主函数
main() {
    echo "========================================"
    echo "      充电站系统测试验证脚本"
    echo "========================================"
    
    # 环境检查
    echo -e "\n${BLUE}=== 环境检查 ===${NC}"
    check_java
    JAVA_OK=$?
    
    check_maven
    MAVEN_OK=$?
    
    check_mysql
    MYSQL_OK=$?
    
    check_app
    APP_OK=$?
    
    # 测试执行
    echo -e "\n${BLUE}=== 测试执行 ===${NC}"
    
    if [ $APP_OK -eq 0 ]; then
        test_basic_api
        API_OK=$?
    else
        log_warning "跳过API测试（应用未运行）"
        API_OK=1
    fi
    
    if [ $MAVEN_OK -eq 0 ]; then
        run_unit_tests
        UNIT_OK=$?
        
        if [ $MYSQL_OK -eq 0 ]; then
            run_mysql_integration_test
            INTEGRATION_OK=$?
        else
            log_warning "跳过MySQL集成测试（MySQL连接失败）"
            INTEGRATION_OK=1
        fi
    else
        log_warning "跳过自动化测试（Maven未安装）"
        UNIT_OK=1
        INTEGRATION_OK=1
    fi
    
    # 总结报告
    echo -e "\n${BLUE}=== 测试总结 ===${NC}"
    
    [ $JAVA_OK -eq 0 ] && echo -e "${GREEN}✅ Java环境${NC}" || echo -e "${RED}❌ Java环境${NC}"
    [ $MAVEN_OK -eq 0 ] && echo -e "${GREEN}✅ Maven环境${NC}" || echo -e "${YELLOW}⚠️  Maven环境${NC}"
    [ $MYSQL_OK -eq 0 ] && echo -e "${GREEN}✅ MySQL连接${NC}" || echo -e "${RED}❌ MySQL连接${NC}"
    [ $APP_OK -eq 0 ] && echo -e "${GREEN}✅ 应用运行${NC}" || echo -e "${YELLOW}⚠️  应用运行${NC}"
    [ $API_OK -eq 0 ] && echo -e "${GREEN}✅ API测试${NC}" || echo -e "${YELLOW}⚠️  API测试${NC}"
    [ $UNIT_OK -eq 0 ] && echo -e "${GREEN}✅ 单元测试${NC}" || echo -e "${YELLOW}⚠️  单元测试${NC}"
    [ $INTEGRATION_OK -eq 0 ] && echo -e "${GREEN}✅ 集成测试${NC}" || echo -e "${YELLOW}⚠️  集成测试${NC}"
    
    echo -e "\n${BLUE}=== 建议操作 ===${NC}"
    
    if [ $JAVA_OK -ne 0 ]; then
        echo "1. 安装Java 17或更高版本"
    fi
    
    if [ $MYSQL_OK -ne 0 ]; then
        echo "2. 启动MySQL服务并检查连接配置"
    fi
    
    if [ $APP_OK -ne 0 ]; then
        echo "3. 启动应用: ./scripts/dev.sh run"
    fi
    
    if [ $MAVEN_OK -ne 0 ]; then
        echo "4. 安装Maven或在IDE中运行测试"
    fi
    
    echo -e "\n详细测试指南请查看: ${BLUE}TESTING_GUIDE.md${NC}"
    echo "Swagger UI地址: http://localhost:8081/charging-station/swagger-ui.html"
}

# 执行主函数
main "$@" 
 