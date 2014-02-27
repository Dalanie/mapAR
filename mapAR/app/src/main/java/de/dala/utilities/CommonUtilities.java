/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dala.utilities;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {


    /**
     * Expression for validating the url or ip-adress (ip v4/v6, dez/hex
     * possible)
     */
    public static final String UrlAndIPRegEx = "^((([hH][tT][tT][pP][sS]?|[fF][tT][pP])\\:\\/\\/)?([\\w\\.\\-]+(\\:[\\w\\.\\&%\\$\\-]+)*@)?"
            + "((([^\\s\\(\\)\\<\\>\\\\\\\"\\.\\[\\]\\,@;:]+)(\\.[^\\s\\(\\)\\<\\>\\\\\\\"\\.\\[\\]\\,@;:]+)*"
            + "(\\.[a-zA-Z]{2,4}))|((([01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d{1,2}|2[0-4]\\d|25[0-5])))"
            + "(\\b\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)\\b)?((\\/[^\\/]"
            + "[\\w\\.\\,\\?\\'\\\\\\/\\+&%\\$#\\=~_\\-@]*)*[^\\.\\,\\?\\\"\\'\\(\\)\\[\\]!;<>{}\\s\\x7F-\\xFF])?)$";

    /**
     * Base URL of the Server
     */
    public static final String SERVER_URL = "http://dalanie.de:9079";

    /**
     * Validates if the server-adress is correct (ipv4 || ipv6 || url)
     *
     * @param serverUrl
     * @return
     */
    public static boolean isValideUrl(String serverUrl) {
        return serverUrl.matches(UrlAndIPRegEx);
    }


    public static void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(String.format("Please set the %1$s constant and recompile the app.",
                    name));
        }
    }
}
