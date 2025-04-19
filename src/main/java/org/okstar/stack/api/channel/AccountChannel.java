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

import org.okstar.stack.api.dto.AccountBindDTO;
import org.okstar.stack.api.dto.BindResultDTO;

/**
 * 帐号通道
 */
public interface AccountChannel {

    /**
     * 绑定帐号。
     * <p/>
     * 将`应用`绑定到`帐号`，实现消息推送。
     *
     * @param bind 绑定信息
     * @return Account 帐号
     */
    BindResultDTO bind(AccountBindDTO bind);

}
