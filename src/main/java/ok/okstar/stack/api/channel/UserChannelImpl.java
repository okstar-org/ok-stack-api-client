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

import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import ok.okstar.stack.api.dto.UserDTOs;
import ok.okstar.stack.api.transport.RestClient;

import java.util.HashMap;

@Slf4j
class UserChannelImpl extends AbsChannel implements UserChannel {

    public UserChannelImpl(RestClient client) {
        super(client);
    }

    /**
     * Gets the apps.
     *
     * @return the apps
     */
    @Override
    public UserDTOs search(String query) {
        String path = "user/search";
        try {
            HashMap<String, String> m = new HashMap<>();
            m.put("q", query);
            UserDTOs s = restClient.get(path, UserDTOs.class, m);
            log.info("json=>{}", s);
            return s;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Throwable rootCause = e.getCause() == null ? e : e.getCause();
            throw new WebApplicationException(rootCause.getMessage());
        }
    }


}
