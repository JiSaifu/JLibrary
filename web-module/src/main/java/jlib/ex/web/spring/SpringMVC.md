# SpringMVC

**《Spring 3.x 企业应用开发实战》系统学习**

### 15.2 注解驱动的控制器
#### 15.2.1 使用@RequestMapping映射请求（P.514）
* 可以通过请求URL（value），请求方法（method），请求参数（params），
报文头（header）设定应设条件
* 它们之间是与的关系
* params和header支持简单表达式
    * param1:表示请求必须包含名为param1的参数
    * !param1:表示请求不包含名为parame1的参数
    * param1!=value1:表示请求包含名为param1的参数，但不能等于value1
    * {"param1=value1", "param2"}:表示请求包含param1和param2两个参数，
    且param1的值为value1

#### 15.2.2 请求处理方法签名概述（P.518）
* 几种典型的处理方法的签名
1. 请求参数按名称匹配的方式绑定到方法入参中，方法返回的字符串代表逻辑视图名
```java
@RequestMapping(value="/handle1")
public String handle1(@ReuqestParam("userName") String userName,
                      @RequestParam("password") String password,
                      @RequestParam("realName") String realName) {
    return "success"
}
```
2. 将Cookie值及报文头属性绑定到入参中，方法返回ModelAndView
```java
@RequestMapping(value="/handle2")
public String handle1(@CookieValue("JSESSIONID) String sessionId,
                      @RequestHeader("Accept-Language") String acceptLanguage) {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("success"); // view
    mav.addObject("user", new User()); // model
    return mav;
}
```
3.请求参数按名称匹配方式绑定到user的属性中，方法返回的字符串代表逻辑视图名
```java
@RequestMapping(value="/handle3")
public String handle3(User user) {
    return "success"
}
```
4.直接将HTTP请求对象传递给处理方法，方法返回的字符串代表逻辑视图名
```java
@RequestMapping(value="/handle4")
public String handle3(HttpServletRequest request) {
    return "success"
}
```
#### 15.2.3 处理方法签名详细说明（P.519）
* 签名种类

    1. RequestParam, CookieValue, RequestHeader

    |签名种类|参数|作用&备注|
    |---|:---|---|
    |@RequestParam|value<br>required<br>defaultValue|绑定请求参数值|
    |@CookieValue|value<br>required<br>defaultValue|绑定Cookie值|
    |@RequestHeader|value<br>required<br>defaultValue|绑定请求报文头的值|

    2. POJO, ServletAPI

    |签名种类|作用&备注|
    |---|---|
    |POJO|表单对象绑定请求参数值<br>（支持级联属性名，<br>如：dept.userId, dept.address.tel）|
|**Servlet API**<br>HttpServletRequest<br>HttpServletResponse<br>HttpSession|可以和SpringAPI类的入参并用，且没有顺序要求|
|IO对象|可以使用<br>InputStream / Reader<br>OutputStream / Writer|
|其他类型参数|-|

#### 15.2.4 使用HttpMessageConverter<T>（P.523）
Spring 3.0新添加的一个重要接口，负责将请求信息转换成一个对象（类型为T），将对象（类型为T）输出为响应信息

**使用HttpMessageConverter<T>**
* 使用@RequestBody/@ResponseBody对处理方法进行标注
    * 使用例：
    ```java
    /**
     * @RequestBody的使用实践类
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
     * @ResponseBody的使用实践类
     * @return 响应流
     * @throws IOException
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
    ```
* 使用HttpEntity<T>/ResponseEntity<T>作为处理方法的入参或返回值
    * 使用例
    ```java
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
    ```
    * 结论
        * 当控制器处理方法使用@RequestBody / @ResponseBody或HttpEntity<T>/ResponseEntity<T>时，
        SpringMVC才使用注册的HttpMessageConverter对请求/响应进行处理
        * 当控制器处理方法使用到@RequestBody / @ResponseBody或HttpEntity<T>/ResponseEntity<T>时，
        Spring首先根据请求头或响应头的Accept属性选择匹配的HttpMessageConverter，进而根据参数类型或泛型
        类型的过滤得到匹配的HttpMessageConverter，如果找不到可用的HttpMessageConverter将报错。
        * HttpEntity<T>/ResponseEntity<T>的功用和@RequestBody/@ResponseBody<T>相似。

#### 15.2.5 处理模型数据(P.532)
**对于MVC框架来说模型数据是最重要的，因为控制（C）是为了产生模型数据（M），而视图（V）则是为了渲染模型数据。**
* ModelAndView
    * 既包含视图信息，也包含模型数据信息。
    * 可以简单地将模型数据看成一个Map<String, Object>

    * 可以使用如下方法添加模型数据
        * ModelAndView addObject(String attributeName, Object attributeValue)
        * ModelAndView addAllObjects(Map<String, ?> modeMap)
    * 可以通过如下方法设置视图
        * void setView(View view) // 指定一个具体的视图对象
        * void setViewName(String viewName) // 指定一个逻辑视图名

* @ModelAttribute

    **如果希望将方法入参对象添加到模型中，仅需在相应入参前使用@ModelAttribute注解**
    * 使用例：
    ```java
    /**
     * 使用@ModelAttribute的实例
     * @param user User入参
     * @see #getUser()
     * @return 逻辑视图名
     */
    @RequestMapping("/handle06")
    public String handle06(@ModelAttribute("user") User user) {
        /*
         * user会与getUser返回的user进行整合，realName将会被覆盖，
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
        User user = new User();
        user.setUserName("ABC");
        user.setRealName("XYZ");
        return user;
    }
    ```

    **注意：处理方法的入参只能使用一个SpringMVC注解<br>
    例如同时使用@ModelAttribute和RequestParam的话会抛出异常**

* Map和Model

    **入参为Map或Model时，SpringMVC会将隐含模型的引用传递给这些入参。
    在方法体内，开发者可以通过这个入参对象访问到模型中的所有数据，
    也可以向模型中添加新的属性数据。**

* @SessionAttributes

### 15.3 处理方法的数据绑定
#### 15.3.1 数据绑定流程剖析
* 流程

|顺序|组件|功能|
|---|---|---|
|1|Servlet<br><入参对象>Object|传递入参对象|
|2|ConversionService|1.数据类型转换<br>2.数据格式化|
|3|Validator|数据合法性校验|
|4|BindingResult|保存数据绑定结果|
**数据绑定核心组件：DataBinder**

#### 15.3.2 数据转换
* ConversionService(org.springframework.core.convert\<interface>)
    * 配置
    在Spring上下文中定义自定义转换器
    ```xml
        <bean id="conversionService"
            class="org.springframework.context.support.ConversionServiceFactoryBean"/>
            <property name="converters">
                <list>
                    <bean class="jlib.ex.web.spring.converter.Converter1" />
                    <bean class="jlib.ex.web.spring.converter.Converter2" />
                </list>
            </property>
        </bean>
    ```
* Spring支持哪些转换器

    实现以下任意一个转换器接口都可以注册到ConversionServiceFactoryBean中：
    * Converter<S, T>
    * GenericConverter
    * ConverterFactory

