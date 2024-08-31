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

package ok.okstar.stack.api;

import ok.okstar.stack.api.auth.AuthenticationToken;
import ok.okstar.stack.api.channel.ChannelFactory;


public class OkStackApiClient {
    private static OkStackApiClient client;
    private final ChannelFactory channelFactory;

    private OkStackApiClient(String url, AuthenticationToken authenticationToken) {
        channelFactory = new ChannelFactory(url, authenticationToken);
    }

    public static OkStackApiClient getInstance(String url, AuthenticationToken authenticationToken) {
        if (client == null) {
            client = new OkStackApiClient(url, authenticationToken);
        }
        return client;
    }

    public ChannelFactory getChannelFactory() {
        return channelFactory;
    }
}
