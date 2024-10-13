package ok.okstar.stack.api.dto;

import lombok.Data;

@Data
public class InstanceDTO {
    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 应用UUID
     */
    private String appUuid;

    /**
     * 应用业务
     */
    private String appBiz;

    /**
     * 实例名称=租户名称+应用名称+订单名词
     */
    private String name;

    /**
     * 实例描述
     */
    private String description;

}
