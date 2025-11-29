package grupo12.practico.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpClientUtil Tests")
class HttpClientUtilTest {

    @Test
    @DisplayName("encodeParam - Should encode simple string")
    void encodeParam_ShouldEncodeSimpleString() {
        String result = HttpClientUtil.encodeParam("hello");
        assertEquals("hello", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode string with spaces")
    void encodeParam_ShouldEncodeStringWithSpaces() {
        String result = HttpClientUtil.encodeParam("hello world");
        assertEquals("hello+world", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode special characters")
    void encodeParam_ShouldEncodeSpecialCharacters() {
        String result = HttpClientUtil.encodeParam("test@example.com");
        assertEquals("test%40example.com", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode ampersand")
    void encodeParam_ShouldEncodeAmpersand() {
        String result = HttpClientUtil.encodeParam("key=value&foo=bar");
        assertEquals("key%3Dvalue%26foo%3Dbar", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode question mark")
    void encodeParam_ShouldEncodeQuestionMark() {
        String result = HttpClientUtil.encodeParam("what?");
        assertEquals("what%3F", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode plus sign")
    void encodeParam_ShouldEncodePlusSign() {
        String result = HttpClientUtil.encodeParam("1+1");
        assertEquals("1%2B1", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode unicode characters")
    void encodeParam_ShouldEncodeUnicodeCharacters() {
        String result = HttpClientUtil.encodeParam("José García");
        assertTrue(result.contains("%C3%A9")); // é encoded
        assertTrue(result.contains("%C3%AD")); // í encoded
    }

    @Test
    @DisplayName("encodeParam - Should handle empty string")
    void encodeParam_ShouldHandleEmptyString() {
        String result = HttpClientUtil.encodeParam("");
        assertEquals("", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode percent sign")
    void encodeParam_ShouldEncodePercentSign() {
        String result = HttpClientUtil.encodeParam("100%");
        assertEquals("100%25", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode slash")
    void encodeParam_ShouldEncodeSlash() {
        String result = HttpClientUtil.encodeParam("path/to/resource");
        assertEquals("path%2Fto%2Fresource", result);
    }

    @Test
    @DisplayName("encodeParam - Should preserve alphanumeric characters")
    void encodeParam_ShouldPreserveAlphanumericCharacters() {
        String result = HttpClientUtil.encodeParam("abc123XYZ");
        assertEquals("abc123XYZ", result);
    }

    @Test
    @DisplayName("encodeParam - Should preserve hyphen and underscore")
    void encodeParam_ShouldPreserveHyphenAndUnderscore() {
        String result = HttpClientUtil.encodeParam("test-value_123");
        assertEquals("test-value_123", result);
    }

    @Test
    @DisplayName("encodeParam - Should preserve dot")
    void encodeParam_ShouldPreserveDot() {
        String result = HttpClientUtil.encodeParam("file.txt");
        assertEquals("file.txt", result);
    }

    @Test
    @DisplayName("encodeParam - Should preserve asterisk")
    void encodeParam_ShouldPreserveAsterisk() {
        String result = HttpClientUtil.encodeParam("test*");
        assertEquals("test*", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode hash symbol")
    void encodeParam_ShouldEncodeHashSymbol() {
        String result = HttpClientUtil.encodeParam("section#anchor");
        assertEquals("section%23anchor", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode newline characters")
    void encodeParam_ShouldEncodeNewlineCharacters() {
        String result = HttpClientUtil.encodeParam("line1\nline2");
        assertEquals("line1%0Aline2", result);
    }

    @Test
    @DisplayName("encodeParam - Should encode tab characters")
    void encodeParam_ShouldEncodeTabCharacters() {
        String result = HttpClientUtil.encodeParam("col1\tcol2");
        assertEquals("col1%09col2", result);
    }

    @Test
    @DisplayName("get - Should throw exception for invalid URL")
    void get_ShouldThrowExceptionForInvalidURL() {
        assertThrows(Exception.class, () -> {
            HttpClientUtil.get("not-a-valid-url");
        });
    }

    @Test
    @DisplayName("get - Should throw exception for non-existent host")
    void get_ShouldThrowExceptionForNonExistentHost() {
        assertThrows(Exception.class, () -> {
            HttpClientUtil.get("http://non-existent-host-that-does-not-exist.invalid");
        });
    }

    @Test
    @DisplayName("get - Should throw exception for null URL")
    void get_ShouldThrowExceptionForNullURL() {
        assertThrows(Exception.class, () -> {
            HttpClientUtil.get(null);
        });
    }
}
