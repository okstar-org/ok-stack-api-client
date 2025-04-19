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

package org.okstar.stack.api.channel;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.okstar.stack.api.dto.AccountBindDTO;
import org.okstar.stack.api.dto.BindResultDTO;
import org.okstar.stack.api.transport.RestClient;

import java.util.HashMap;

/**
 * 帐号通道实现
 */
@Slf4j
class AccountChannelImpl extends AbsChannel implements AccountChannel {

    public AccountChannelImpl(RestClient client) {
        super(client);
    }

    /**
     * 绑定
     * @param bind 绑定信息
     * @return Account
     */
    @Override
    public BindResultDTO bind(AccountBindDTO bind) {
        try {
            String path = "account/bind";
            HashMap<String, String> m = new HashMap<>();
            try (Response s = restClient.post(path, bind, m)) {
                return s.readEntity(BindResultDTO.class);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Throwable rootCause = e.getCause() == null ? e : e.getCause();
            throw new WebApplicationException(rootCause.getMessage());
        }
    }
}
