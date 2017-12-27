package jlib.ex.web.spring.controller;

import jlib.ex.web.spring.bean.User;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Controller
/*
 * 在POJO类定义处标注@Controller，在通过<context:component-scan/>扫描相应的类包，
 * 即可是一个POJO成为一个能处理HTTP请求的控制器
 */
@RequestMapping("/user")
@SessionAttributes({
        "session_user1",
        "session_user2"})
public class UserController {

    @RequestMapping("/register")
    public String register() {
        return "user/register";
    }

    @RequestMapping(method=RequestMethod.POST)
    public ModelAndView createUser(User user) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("user/createSuccess");
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping("/register/{target}")
    // 带占位符的URL，可以利用@PathVariable绑定到控制器方法的入参中（推荐显示指定绑定的参数名）
    public ModelAndView viewParams(@PathVariable("target") String target,
                                   @CookieValue("JSESSIONID") String sessionId,
                                   @RequestHeader("Accept-Encoding") String acceptEncoding) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("sessionId", sessionId);
        paramMap.put("acceptEncoding", acceptEncoding);
        paramMap.put("target", target);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("user/paramViewer");
        mav.addObject("paramMap", paramMap);
        return mav;
    }

    /**
     * \@RequestBody的使用实践类
     * @param requestBody 将请求报文体转换为字符串绑定到requestBody中
     * @return 逻辑视图名
     */
    @RequestMapping("/handle01")
    public String handle01(@RequestBody String requestBody) {
        /*
         * 由于HttpMessageConverter对应的泛型类型为String，
         * 所以StringHttpMessageConverter将被SpringMVC选中
         */

        System.out.println(requestBody);
        return "/user/blank";
    }

    /**
     * \@ResponseBody的使用实践类
     * @return 响应流
     * @throws IOException 输入输出异常
     */
    @ResponseBody
    @RequestMapping("/handle02")
    public byte[] handle02() throws IOException {
        // 读取一张图片，并将图片数据输出到响应流中，客户端将显示这张图片
        Resource res = new ClassPathResource("/img/logo.png");
        /*
         * 由于方法返回类型为byte[]，
         * SpringMVC给句类型匹配查找规则将使用ByteArrayHttpMessageConverter对返回值进行处理
         */
        return FileCopyUtils.copyToByteArray(res.getInputStream());
    }

    /**
     * 使用HttpEntity&lt;T&gt;实现请求内容转换
     * @param httpEntity 请求报文
     * @return 逻辑视图名
     */
    @RequestMapping("/handle03")
    public String handle03(HttpEntity<String> httpEntity) {
        /*
         * HttpEntity<T>不但可以访问请求及响应报文体的数据，还可以访问请求和响应报文头的数据。
         * SpringMVC根据HttpEntity的泛型类型查找对应的HttpMessageConverter
         */
        Long content= httpEntity.getHeaders().getContentLength();
        System.out.println(content);
        System.out.println(httpEntity.getBody());

        return "/user/blank";
    }

    /**
     * 使用ResponseEntity&lt;T&gt;实现响应报文转换
     * @return 响应流
     * @throws IOException 输入输出异常
     */
    @RequestMapping("/handle04")
    public ResponseEntity<byte[]> handle04() throws IOException {
        Resource res = new ClassPathResource("/img/logo.png");
        byte[] fileData = FileCopyUtils.copyToByteArray(res.getInputStream());

        return new ResponseEntity<byte[]>(fileData, HttpStatus.OK);
    }

    /**
     * 测试HttpMessageConverter对XML和json的支持
     * @param requestEntity HttpEntity入参
     * @return ResponseEntity响应对象
     */
    @RequestMapping("/handle05")
    public ResponseEntity<User> handle05(HttpEntity<User> requestEntity) {
        User user = requestEntity.getBody();
        user.setUserId("001");
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    /**
     * 使用@ModelAttribute的实例
     * @param user User入参
     * @see #getUser()
     * @return 逻辑视图名
     */
    @RequestMapping("/handle06")
    public String handle06(@ModelAttribute("user") User user) {
        /*
         * user会与getUser返回的user进行整合，realName将会被下面的设定覆盖，
         * 而userName会采用getUser中的赋值
         */
        user.setRealName("abc");
        System.out.println("userName:" + user.getUserName());
        System.out.println("realName:" + user.getRealName());
        return "/user/blank";
    }

    /**
     * 事前设定User对象到视图上下文中
     * <p>
     *     在方法定义中使用@ModelAttribute。
     *     SpringMVC在调用目标处理方法前，会先逐个调用在方法级上标注了@ModelAttribute的方法，
     *     并将这些方法的返回值添加到模型中。
     * </p>
     *
     * @return 设定好的User对象
     */
    @ModelAttribute("user")
    public User getUser() {
        System.out.println("xxx");
        User user = new User();
        user.setUserName("ABC");
        user.setRealName("XYZ");
        return user;
    }

    /**
     * 使用ModelMap的实例
     * @param modelMap ModelMap入参
     *                 #getUser()方法的隐含模型对象将传递给modelMap
     *                 如果没有User作为参数，将不会合并隐含对象和User的属性值
     * @param user User入参
     * @return 逻辑视图名
     */
    @RequestMapping("/handle07")
    public String handle07(ModelMap modelMap, @ModelAttribute("user") User user) {

        System.out.println("ModelMap:");
        System.out.println("\t" + ((User)modelMap.get("user")).getUserName());
        System.out.println("\t" + ((User)modelMap.get("user")).getRealName());
        System.out.println("ModelMap:");
        System.out.println("\t" + user.getUserName());
        System.out.println("\t" + user.getRealName());
        return "/user/blank";
    }

    /**
     * \@SessionAttributes的使用实例1
     * 设置sessionAttribute值
     * @param user1 session对象1
     * @param user2 session对象2
     * @return 逻辑视图名
     */
    @RequestMapping("/handle081")
    public String handle081(@ModelAttribute("session_user1") User user1,
                           @ModelAttribute("session_user2") User user2) {
        System.out.println("in handle081");

        /*
         * session_user1/2已经在类级别处标注为sessionAttributes，
         * SpringMVC在处理sessionScope属性是，会现在隐含模型中查找是否有对应属性
         * 初次设定session属性是，许添件该属性的@ModelAttribute方法
         * 如getSessionUser1(), getSessionUser2()
         */
        user1.setUserName("John");
        user2.setUserName("Tom");
        return "forward:/user/handle082.html";
    }

    /**
     * \@SessionAttributes的使用实例2
     * 读取session中的值，读取之后调用SessionStatus#setComplete()方法，
     * 清空该控制器类的所有属性
     * @param modelMap ModelMap入参
     * @param sessionStatus SessionStatus入参
     * @return 逻辑视图名
     */
    @RequestMapping("/handle082")
    public String handle082(ModelMap modelMap, SessionStatus sessionStatus) {
        System.out.println("in handle82");
        User user = (User)modelMap.get("session_user1");
        if(user != null) {
            System.out.println(user.getUserName());
            // 清除本处理器对应的对话属性（SessionScope中的全部属性）
            sessionStatus.setComplete();
        }
        return "forward:/user/handle083.html";
    }

    /**
     * \@SessionAttributes的使用实例3
     * 测试被调用setComplete()方法之后的session属性设置内容
     * @modelMap modelMap 入参
     * @return 逻辑视图名
     */
    @RequestMapping("/handle083")
    public String handle083(ModelMap modelMap, SessionStatus sessionStatus) {
        System.out.println("in handle083");
        User user = (User)modelMap.get("session_user2");
        if(user != null) {
            System.out.println(user.getUserName());
        }
        return "/user/blank";
    }

    /**
     * 设置session scope隐含对象（防止handle08_1()抛出异常）
     * @return User隐含对象
     */
    @ModelAttribute("session_user1")
    public User getSessionUser1() {
        return new User();
    }

    /**
     * 设置session scope隐含对象（防止handle08_1()抛出异常）
     * @return User隐含对象
     */
    @ModelAttribute("session_user2")
    public User getSessionUser2() {
        return new User();
    }

}
