package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ControllerTestHelpers {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static void runTest(MockMvc mvc, MockHttpServletRequestBuilder requestBuilder, ResultMatcher... matchers) throws Exception {
        var actions = mvc.perform(requestBuilder);
        for (var matcher : matchers) {
            actions = actions.andExpect(matcher);
        }
    }

    public static MockHttpServletRequestBuilder postJson(String path, Object content) throws Exception {
        return post(path)
                .content(mapper.writeValueAsString(content))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    public static MockHttpServletRequestBuilder patchJson(String path, Object content) throws Exception {
        return patch(path)
                .content(mapper.writeValueAsString(content))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    public static MockHttpServletRequestBuilder getJson(String path, Object content) throws Exception {
        return get(path)
                .content(mapper.writeValueAsString(content))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    public static MockHttpServletRequestBuilder deleteJson(String path) {
        return delete(path)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    public static <T> ResultMatcher j(String path, T value) {
        return jsonPath(path, is(value));
    }

    public static <T> ResultMatcher no(String path) {
        return jsonPath(path).doesNotExist();
    }

    public static <T> Matcher<T> is(T value) {
        return new BaseMatcher<>() {
            @Override
            public boolean matches(Object o) {
                return value.equals(o);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}
