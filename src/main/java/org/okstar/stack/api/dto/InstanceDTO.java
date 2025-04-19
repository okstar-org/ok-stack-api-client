/*
 * * Copyright (c) 2022 船山信息 chuanshaninfo.com
 * OkStack is licensed under Mulan PubL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PubL v2. You may obtain a copy of Mulan PubL v2 at:
 *          http://license.coscl.org.cn/MulanPubL-2.0
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PubL v2 for more details.
 * /
 */

package org.okstar.stack.api.dto;

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
