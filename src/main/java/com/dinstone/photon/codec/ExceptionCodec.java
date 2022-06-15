/*
 * Copyright (C) 2018~2022 dinstone<dinstone@163.com>
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
package com.dinstone.photon.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.dinstone.photon.ExchangeException;
import com.dinstone.photon.utils.ByteStreamUtil;
import com.dinstone.photon.utils.ExceptionUtil;

public class ExceptionCodec {

    public static byte[] encode(ExchangeException exception) {
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ByteStreamUtil.writeInt(bao, exception.getCode());
            ByteStreamUtil.writeString(bao, exception.getMessage());
            ByteStreamUtil.writeString(bao, ExceptionUtil.getStackTrace(exception));
            return bao.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    public static ExchangeException decode(byte[] encoded) {
        try {
            if (encoded != null) {
                ByteArrayInputStream bai = new ByteArrayInputStream(encoded);
                int code = ByteStreamUtil.readInt(bai);
                String message = ByteStreamUtil.readString(bai);
                String straces = ByteStreamUtil.readString(bai);
                return new ExchangeException(code, message, straces);
            }
            return new ExchangeException(199, "unkown exception");
        } catch (Exception e) {
            return new ExchangeException(199, "unkown exception", e);
        }
    }

}
