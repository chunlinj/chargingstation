package com.emsp.chargingstation.integration;

import com.emsp.chargingstation.domain.enums.ConnectorStandard;
import com.emsp.chargingstation.domain.enums.EvseStatus;
import com.emsp.chargingstation.dto.request.CreateConnectorRequest;
import com.emsp.chargingstation.dto.request.CreateEvseRequest;
import com.emsp.chargingstation.dto.request.CreateLocationRequest;
import com.emsp.chargingstation.dto.request.UpdateEvseStatusRequest;
import com.emsp.chargingstation.dto.response.ConnectorResponse;
import com.emsp.chargingstation.dto.response.EvseResponse;
import com.emsp.chargingstation.dto.response.LocationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("mysql-test") // 使用专门的MySQL测试配置
// @Transactional // 每个测试后回滚数据库变更
@DisplayName("充电站系统MySQL集成测试")
class ChargingStationMySQLIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateLocationRequest locationRequest;
    private CreateEvseRequest evseRequest;
    private CreateConnectorRequest connectorRequest;

    @BeforeEach
    void setUp() {
        locationRequest = new CreateLocationRequest(
                "MySQL测试充电站",
                "北京市朝阳区MySQL测试路123号",
                BigDecimal.valueOf(39.9042),
                BigDecimal.valueOf(116.4074)
        );
        locationRequest.setOpeningTime(LocalTime.of(6, 0));
        locationRequest.setClosingTime(LocalTime.of(22, 0));
        locationRequest.setDescription("MySQL集成测试用充电站");

        evseRequest = new CreateEvseRequest("CN*MYS*EVSE001");
        evseRequest.setDescription("MySQL测试EVSE设备1号");

        connectorRequest = new CreateConnectorRequest(
                ConnectorStandard.CCS2,
                BigDecimal.valueOf(120.0),
                400,
                300
        );
        connectorRequest.setDescription("MySQL测试CCS2直流快充接口");
    }

    @Test
    @DisplayName("完整业务流程测试：创建充电站 -> 添加EVSE -> 添加连接器 -> 状态管理")
    void testCompleteChargingStationWorkflow() throws Exception {
        // 1. 创建充电站点
        MvcResult locationResult = mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("MySQL测试充电站"))
                .andExpect(jsonPath("$.address").value("北京市朝阳区MySQL测试路123号"))
                .andExpect(jsonPath("$.latitude").value(39.9042))
                .andExpect(jsonPath("$.longitude").value(116.4074))
                .andReturn();

        LocationResponse location = objectMapper.readValue(
                locationResult.getResponse().getContentAsString(),
                LocationResponse.class
        );
        assertNotNull(location.getId());
        String locationId = location.getId().toString();

        // 2. 为充电站添加EVSE
        MvcResult evseResult = mockMvc.perform(post("/api/v1/locations/{locationId}/evses", locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.evseId").value("CN*MYS*EVSE001"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andReturn();

        EvseResponse evse = objectMapper.readValue(
                evseResult.getResponse().getContentAsString(),
                EvseResponse.class
        );
        assertNotNull(evse.getId());
        String evseId = evse.getId().toString();

        // 3. 为EVSE添加连接器
        MvcResult connectorResult = mockMvc.perform(post("/api/v1/evses/{evseId}/connectors", evseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(connectorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.standard").value("CCS2"))
                .andExpect(jsonPath("$.maxPowerKw").value(120.0))
                .andExpect(jsonPath("$.voltage").value(400))
                .andExpect(jsonPath("$.amperage").value(300))
                .andReturn();

        ConnectorResponse connector = objectMapper.readValue(
                connectorResult.getResponse().getContentAsString(),
                ConnectorResponse.class
        );
        assertNotNull(connector.getId());

        // 4. 测试EVSE状态转换：AVAILABLE -> BLOCKED
        UpdateEvseStatusRequest statusRequest = new UpdateEvseStatusRequest(EvseStatus.BLOCKED);
        mockMvc.perform(patch("/api/v1/evses/{evseId}/status", evseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));

        // 5. 测试EVSE状态转换：BLOCKED -> AVAILABLE
        statusRequest = new UpdateEvseStatusRequest(EvseStatus.AVAILABLE);
        mockMvc.perform(patch("/api/v1/evses/{evseId}/status", evseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));

        // 6. 查询完整的充电站信息（包含EVSE和连接器）
        MvcResult fullLocationResult = mockMvc.perform(get("/api/v1/locations/{locationId}", locationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("MySQL测试充电站"))
                .andReturn();
        
        // 调试：打印完整响应内容
        String fullResponse = fullLocationResult.getResponse().getContentAsString();
        System.out.println("完整Location响应: " + fullResponse);
        // 验证evses字段存在
        mockMvc.perform(get("/api/v1/locations/{locationId}", locationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evses").exists())
                .andExpect(jsonPath("$.evses").isArray())
                .andExpect(jsonPath("$.evses[0].evseId").value("CN*MYS*EVSE001"))
                .andExpect(jsonPath("$.evses[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.evses[0].connectors").exists())
                .andExpect(jsonPath("$.evses[0].connectors").isArray())
                .andExpect(jsonPath("$.evses[0].connectors[0].standard").value("CCS2"));

        // 7. 通过EVSE标识符查询EVSE
        mockMvc.perform(get("/api/v1/evses/evse-id/{evseId}", "CN*MYS*EVSE001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evseId").value("CN*MYS*EVSE001"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    @DisplayName("测试地理边界查询功能")
    void testGeographicBoundsQuery() throws Exception {
        // 创建多个不同位置的充电站
        CreateLocationRequest location1 = new CreateLocationRequest(
                "MySQL充电站1", "地址1", BigDecimal.valueOf(39.9042), BigDecimal.valueOf(116.4074)
        );
        CreateLocationRequest location2 = new CreateLocationRequest(
                "MySQL充电站2", "地址2", BigDecimal.valueOf(39.9142), BigDecimal.valueOf(116.4174)
        );
        CreateLocationRequest location3 = new CreateLocationRequest(
                "MySQL充电站3", "地址3", BigDecimal.valueOf(40.0042), BigDecimal.valueOf(116.5074)
        );

        // 创建充电站
        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location3)))
                .andExpect(status().isCreated());

        // 地理边界查询 - 应该返回前两个充电站
        MvcResult result = mockMvc.perform(get("/api/v1/locations/bounds")
                        .param("minLatitude", "39.9")
                        .param("maxLatitude", "39.92")
                        .param("minLongitude", "116.4")
                        .param("maxLongitude", "116.42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andReturn();

        System.out.println("地理边界查询结果: " + result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("测试无效状态转换")
    void testInvalidStatusTransition() throws Exception {
        // 先创建充电站和EVSE
        MvcResult locationResult = mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        LocationResponse location = objectMapper.readValue(
                locationResult.getResponse().getContentAsString(),
                LocationResponse.class
        );

        // 创建EVSE时使用不同的ID避免冲突
        CreateEvseRequest testEvseRequest = new CreateEvseRequest("CN*MYS*EVSE002");
        testEvseRequest.setDescription("状态转换测试EVSE");

        MvcResult evseResult = mockMvc.perform(post("/api/v1/locations/{locationId}/evses", location.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEvseRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        EvseResponse evse = objectMapper.readValue(
                evseResult.getResponse().getContentAsString(),
                EvseResponse.class
        );

        // 先设置为REMOVED状态
        UpdateEvseStatusRequest removeRequest = new UpdateEvseStatusRequest(EvseStatus.REMOVED);
        mockMvc.perform(patch("/api/v1/evses/{evseId}/status", evse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeRequest)))
                .andExpect(status().isOk());

        // 尝试从REMOVED状态转换到其他状态 - 应该失败
        UpdateEvseStatusRequest invalidRequest = new UpdateEvseStatusRequest(EvseStatus.AVAILABLE);
        mockMvc.perform(patch("/api/v1/evses/{evseId}/status", evse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("INVALID_STATUS_TRANSITION"));
    }

    @Test
    @DisplayName("测试重复EVSE ID")
    void testDuplicateEvseId() throws Exception {
        // 创建充电站
        MvcResult locationResult = mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        LocationResponse location = objectMapper.readValue(
                locationResult.getResponse().getContentAsString(),
                LocationResponse.class
        );

        // 创建第一个EVSE
        CreateEvseRequest firstEvseRequest = new CreateEvseRequest("CN*MYS*EVSE003");
        mockMvc.perform(post("/api/v1/locations/{locationId}/evses", location.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstEvseRequest)))
                .andExpect(status().isCreated());

        // 尝试创建相同EVSE ID的EVSE - 应该失败
        mockMvc.perform(post("/api/v1/locations/{locationId}/evses", location.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstEvseRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_RESOURCE"));
    }
} 