#!/bin/bash

# Development helper script for Charging Station Service

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    if ! command_exists java; then
        print_error "Java is not installed. Please install Java 17 or later."
        exit 1
    fi
    
    if ! command_exists mvn; then
        print_error "Maven is not installed. Please install Maven 3.6 or later."
        exit 1
    fi
    
    # Check Java version
    java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$java_version" -lt 17 ]; then
        print_error "Java 17 or later is required. Current version: $java_version"
        exit 1
    fi
    
    print_status "Prerequisites check passed!"
}

# Clean and compile
clean_compile() {
    print_status "Cleaning and compiling project..."
    mvn clean compile
    print_status "Compilation completed!"
}

# Run tests
run_tests() {
    print_status "Running tests..."
    mvn test
    print_status "Tests completed!"
}

# Run integration tests
run_integration_tests() {
    print_status "Running integration tests..."
    mvn failsafe:integration-test
    print_status "Integration tests completed!"
}

# Package application
package_app() {
    print_status "Packaging application..."
    mvn clean package -DskipTests
    print_status "Application packaged successfully!"
}

# Run application locally
run_local() {
    print_status "Starting application locally..."
    mvn spring-boot:run
}

# Build Docker image
build_docker() {
    print_status "Building Docker image..."
    docker build -t charging-station-service .
    print_status "Docker image built successfully!"
}

# Start services with Docker Compose
start_services() {
    print_status "Starting services with Docker Compose..."
    docker-compose up -d
    print_status "Services started! Access the application at http://localhost:8080/charging-station"
}

# Stop services
stop_services() {
    print_status "Stopping services..."
    docker-compose down
    print_status "Services stopped!"
}

# Show logs
show_logs() {
    docker-compose logs -f charging-station-service
}

# Run full CI pipeline locally
run_ci() {
    print_status "Running full CI pipeline..."
    check_prerequisites
    clean_compile
    run_tests
    package_app
    build_docker
    print_status "CI pipeline completed successfully!"
}

# Reset environment
reset_env() {
    print_warning "Resetting environment..."
    docker-compose down -v
    docker rmi charging-station-service 2>/dev/null || true
    mvn clean
    print_status "Environment reset completed!"
}

# 测试验证
verify() {
    log_info "运行系统测试验证..."
    if [[ -f "${BASE_DIR}/scripts/test-verify.sh" ]]; then
        bash "${BASE_DIR}/scripts/test-verify.sh"
    else
        log_error "测试验证脚本不存在: ${BASE_DIR}/scripts/test-verify.sh"
        exit 1
    fi
}

# Show help
show_help() {
    echo -e "${BLUE}充电站系统开发脚本${NC}"
    echo ""
    echo -e "${YELLOW}使用方法:${NC}"
    echo "  ./scripts/dev.sh <command> [options]"
    echo ""
    echo -e "${YELLOW}可用命令:${NC}"
    echo "  check-prereq       - 检查开发环境前置条件"
    echo "  clean              - 清理编译输出"
    echo "  compile            - 编译项目"
    echo "  test               - 运行测试"
    echo "  package            - 打包应用"
    echo "  run                - 启动应用"
    echo "  docker-build       - 构建Docker镜像"
    echo "  docker-run         - 运行Docker容器"
    echo "  docker-stop        - 停止Docker容器"
    echo "  ci                 - 运行完整CI流水线"
    echo "  verify             - 运行系统测试验证"
    echo "  reset              - 重置环境"
    echo "  help               - 显示此帮助信息"
    echo ""
    echo -e "${YELLOW}示例:${NC}"
    echo "  ./scripts/dev.sh check-prereq"
    echo "  ./scripts/dev.sh compile"
    echo "  ./scripts/dev.sh test"
    echo "  ./scripts/dev.sh run"
    echo "  ./scripts/dev.sh verify"
    echo ""
}

# Main script logic
case "${1:-help}" in
    check-prereq)
        check_prerequisites
        ;;
    compile)
        check_prerequisites
        clean_compile
        ;;
    test)
        check_prerequisites
        run_tests
        ;;
    integration-test)
        check_prerequisites
        run_integration_tests
        ;;
    package)
        check_prerequisites
        package_app
        ;;
    run)
        check_prerequisites
        run_local
        ;;
    build-docker)
        build_docker
        ;;
    start)
        start_services
        ;;
    stop)
        stop_services
        ;;
    logs)
        show_logs
        ;;
    ci)
        run_ci
        ;;
    reset)
        reset_env
        ;;
    verify)
        verify
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac 