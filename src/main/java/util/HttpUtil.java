package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {

    public static final String UTF_8 = "UTF-8";
    public static final String ISO_8859_1 = "ISO-8859-1";

    public static final String STR_PLAIN = "text/plain";
    public static final String STR_BINARY = "application/binary";
    public static final String STR_JSON = "application/json";
    public static final String STR_CBOR = "application/cbor";

    public static final String TMP_FMT = "%s; charset=%s";

    public static final String TYPE_PLAIN = String.format(TMP_FMT, STR_PLAIN, UTF_8);
    public static final String TYPE_BINARY = String.format(TMP_FMT, STR_BINARY, ISO_8859_1);
    public static final String TYPE_JSON = String.format(TMP_FMT, STR_JSON, UTF_8);
    public static final String TYPE_CBOR = String.format(TMP_FMT, STR_CBOR, UTF_8);

    public static class ContentTypes {
        /**
         * {@code "text"}
         */
        public static final int PLAIN = 0;

        /**
         * {@code "binary"}
         */
        public static final int BINARY = 1;

        /**
         * {@code "json"}
         */
        public static final int JSON = 2;

        /**
         * {@code "cbor"}
         */
        public static final int CBOR = 3;
    }

    public static class EncryptTypes {

        public static final String NONE = "";

        public static final String AES = "a";

        public static final String BASE64 = "b";

        public static final String AES_RSA = "c";

        public static final String RSA = "r";

        public static final String HEX = "h";

    }

    public static class ZipTypes {

        /**
         * {@code "none"}
         */
        public static final int NONE = 0;
        /**
         * {@code "gzip"}
         */
        public static final int GZIP = 1;

        /**
         * {@code "deflate"}
         */
        public static final int DEFLATE = 2;

        /**
         * {@code "snappy"}
         */
        public static final int SNAPPY = 3;

        /**
         * {@code "lz4"}
         */
        public static final int LZ4 = 4;
    }

    public static class HeaderValues {

        /**
         * {@code "none"}
         */
        public static final String NONE = "";

        /**
         * {@code "gzip"}
         */
        public static final String GZIP = "gzip";

        /**
         * {@code "deflate"}
         */
        public static final String DEFLATE = "deflate";
        /**
         * {@code "snappy"}
         */
        public static final String SNAPPY = "snappy";

        /**
         * {@code "lz4"}
         */
        public static final String LZ4 = "lz4";
    }

    public static class HeaderNames {
        /** RFC 1945 (HTTP/1.0) Section 10.5, RFC 2616 (HTTP/1.1) Section 14.17 */
        public static final String CONTENT_TYPE = "Content-Type";

        /**
         * {@code "Original-Encoding"}
         */
        public static final String ORIGINAL_ENCODING = "Original-Encoding";
        /**
         * {@code "Content-Encrypt"}
         */
        public static final String ORIGINAL_ENCRYPT = "Original-Encrypt";
        /**
         * {@code "Encrypt-Key"}
         */
        public static final String ENCRYPT_KEY = "Encrypt-Key";
        /**
         * {@code "Encrypt-Data"}
         */
        public static final String ENCRYPT_DATA = "Encrypt-Data";
        /**
         * {@code "Original-Length"}
         */
        public static final String ORIGINAL_LENGTH = "Original-Length";
    }

    static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    public static final int MIN_COMPRESS_SIZE = 256;

    public static class HttpContentBean {
        public byte[] content;
        public String encryptAesKey;
        public int compressType;
        public int contenSrcLen;

        public HttpContentBean(byte[] c, String aesKey, int compress, int srcLen) {
            content = c;
            encryptAesKey = aesKey;
            compressType = compress;
            contenSrcLen = srcLen;
        }

        public void init(byte[] c, String aesKey, int compress, int srcLen) {
            content = c;
            encryptAesKey = aesKey;
            compressType = compress;
            contenSrcLen = srcLen;
        }
    }
}
