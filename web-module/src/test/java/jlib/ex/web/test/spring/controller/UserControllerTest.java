package jlib.ex.web.test.spring.controller;

import com.thoughtworks.xstream.io.xml.StaxDriver;
import jlib.ex.web.spring.bean.User;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserControllerTest {
    /**
     * 测试jlib.ex.web.spring.controller.UserController#handle01(String)
     * @see jlib.ex.web.spring.controller.UserController#handle01(String)
     */
    @Test
    public void testHandle01() {
        /*
         * RestTemplate是Spring 3.0新增的模板类，在客户端程序中可以使用该类调用Web服务端的服务
         * 它支持REST风格的URL。
         * RestTemplate默认已经注册了一些HttpMessageConverter，
         * 因此可以对响应数据进行相应的转换处理
         */
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("userName", "Tom");
        form.add("password", "1234");
        form.add("age", "10");
        restTemplate.postForLocation(
                "http://localhost:8081/jweb/user/handle01.html",
                form
        );
    }

    /**
     * 测试jlib.ex.web.spring.controller.UserController#handle02()
     * @see jlib.ex.web.spring.controller.UserController#handle02()
     */
    @Test
    public void testHandle02() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        byte[] response = restTemplate.postForObject(
                "http://localhost:8081/jweb/user/handle02.html",
                null,
                byte[].class
        );
        FileCopyUtils.copy(response,
                new FileSystemResource("C:\\Users\\saifu.ji\\test.png").getFile());
    }

    /**
     * 测试jlib.ex.web.spring.controller.UserController#handle03(HttpEntity<String>)
     * @see jlib.ex.web.spring.controller.UserController#handle03(HttpEntity<String>)
     */
    @Test
    public void testHandle03() {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("userName", "Tom");
        form.add("password", "1234");
        form.add("age", "10");
        restTemplate.postForLocation(
                "http://localhost:8081/jweb/user/handle03.html",
                form
        );
    }

    /**
     * 测试jlib.ex.web.spring.controller.UserController#handle04()
     * @see jlib.ex.web.spring.controller.UserController#handle04()
     */
    @Test
    public void testHandle04() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        byte[] response = restTemplate.postForObject(
                "http://localhost:8081/jweb/user/handle04.html",
                null,
                byte[].class
        );
        FileCopyUtils.copy(response,
                new FileSystemResource("C:\\Users\\saifu.ji\\test.png").getFile());
    }

    @Test
    public void testHandle05() throws IOException {
        RestTemplate restTemplate = buildRestTemplate();
        HttpHeaders entityHeaders = new HttpHeaders();
        entityHeaders.setContentType(MediaType.valueOf("application/json;UTF-8"));
        entityHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        User user = new User();
        user.setUserName("User1");
        user.setPassword("1234");
        user.setRealName("DAMAOMAO");

        HttpEntity<User> requestEntity = new HttpEntity<User>(user, entityHeaders);
        ResponseEntity<User> responseEntity = restTemplate.exchange(
                "http://localhost:8081/jweb/user/handle05.html",
                HttpMethod.POST, requestEntity, User.class
        );

        User responseUser = responseEntity.getBody();

        assertNotNull(responseUser);
        assertEquals("User1", responseUser.getUserName());
        assertEquals("1234", responseUser.getPassword());
        assertEquals("DAMAOMAO", responseUser.getRealName());
    }

    private RestTemplate buildRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        XStreamMarshaller xmlMarshaller = new XStreamMarshaller();
        xmlMarshaller.setStreamDriver(new StaxDriver());
        xmlMarshaller.setAnnotatedClasses(User.class);

        MarshallingHttpMessageConverter xmlConverter =
                new MarshallingHttpMessageConverter();
        xmlConverter.setMarshaller(xmlMarshaller);
        xmlConverter.setUnmarshaller(xmlMarshaller);
        restTemplate.getMessageConverters().add(xmlConverter);

        MappingJacksonHttpMessageConverter jsonConverter =
                new MappingJacksonHttpMessageConverter();
        restTemplate.getMessageConverters().add(jsonConverter);
        return restTemplate;
    }

    @Test
    public void testHandle06() {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("password", "1234");
        restTemplate.postForLocation(
                "http://localhost:8081/jweb/user/handle06.html",
                form
        );
    }

    @Test
    public void testHandle07() {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("userName", "ModelTest");
        form.add("password", "2345");
        restTemplate.postForLocation(
                "http://localhost:8081/jweb/user/handle07.html",
                form
        );
    }

    @Test
    public void testHandle08() {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("userName", "SessionAttributeTest");
        form.add("password", "2345");
        restTemplate.postForLocation(
                "http://localhost:8081/jweb/user/handle081.html",
                form
        );
    }
}
