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

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ok.okstar.stack.api.OkStackApiClient;
import ok.okstar.stack.api.auth.AuthenticationToken;
import ok.okstar.stack.api.dto.UserDTOs;
import org.junit.jupiter.api.Test;
import org.okstar.platform.common.core.defined.SystemDefines;


@Slf4j
class UserChannelTest {

    @Test
    void search() {
        AuthenticationToken token = new AuthenticationToken("okstar", "okstar");
        token.addHeader(SystemDefines.Header_X_OK_from, "ok-app://fake-app-uuid");
        var instance = OkStackApiClient.getInstance("http://localhost:8000", token);

        UserChannel channel = instance.getChannelFactory().createUserChannel();
        UserDTOs result = channel.search("SJdVr4Swzf2f");
        log.info("search=>{}", result);
    }
}