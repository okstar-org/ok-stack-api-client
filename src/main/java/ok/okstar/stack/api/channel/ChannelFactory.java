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

package ok.okstar.stack.api.channel;

import ok.okstar.stack.api.auth.AuthenticationToken;
import ok.okstar.stack.api.transport.RestClient;
import ok.okstar.stack.api.transport.SupportedMediaType;

public class ChannelFactory {
    RestClient restClient;

    public ChannelFactory(String base, AuthenticationToken authenticationToken) {
            restClient = new RestClient.RestClientBuilder(base)
                .authenticationToken(authenticationToken)
                .connectionTimeout(60 * 1000)
                .mediaType(SupportedMediaType.JSON)
                .build();
    }

    public UserChannel createUserChannel() {
        return new UserChannelImpl(restClient);
    }
}
