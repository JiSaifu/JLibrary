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

