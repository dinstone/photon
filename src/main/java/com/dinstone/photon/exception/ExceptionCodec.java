/*
 * Copyright (C) 2018~2020 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dinstone.photon.exception;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.dinstone.photon.util.ByteStreamUtil;

public class ExceptionCodec {

    public static byte[] encode(ExchangeException exception) {
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ByteStreamUtil.writeString(bao, "" + exception.getCode());
            ByteStreamUtil.writeString(bao, exception.getMessage());

            return bao.toByteArray();
        } catch (IOException e) {
            // igonre
        }
        return null;
    }

    public static ExchangeException decode(byte[] encoded) {
        try {
            if (encoded != null) {
                ByteArrayInputStream bai = new ByteArrayInputStream(encoded);
                int code = Integer.parseInt(ByteStreamUtil.readString(bai));
                String message = ByteStreamUtil.readString(bai);
                return new ExchangeException(code, message);
            }
            return new ExchangeException(199, "unkown exception");
        } catch (Exception e) {
            return new ExchangeException(199, "unkown exception", e);
        }
    }
}
