>先赞后看，养成习惯 🌹 欢迎微信关注[Java编程之道],每天进步一点点，沉淀技术分享知识。

# Spring Boot中Web应用的统一异常处理

> 大家在日常开发中常常遇到系统爆出各种不友好的异常，导致整个系统交互体验极差甚至是系统崩溃，今天我们就聊一下SpringBoot中如果开发一个统一的异常处理中心。

这部分内容比较基础，只是很多同学很少去设计或者参与到需要全局异常处理的开发中来...以下介绍几种常用的处理方式。


## 友好的异常处理方式

> SpringBoot默认的已经提供了一套处理异常的机制。一旦程序中出现了异常SpringBoot会像/error的url发送请求。在springBoot中提供了一个叫BasicExceptionController来处理/error请求，然后跳转到默认显示异常的页面来展示异常信息。


![](https://user-gold-cdn.xitu.io/2020/7/5/1731e2e387ac1848?w=862&h=349&f=png&s=162205)

也就是你们常常看到的这个错误界面，在一个完善的系统中出现这种界面你是会被祭天的！

![](https://user-gold-cdn.xitu.io/2020/7/5/1731e2deb5e2612e?w=150&h=145&f=png&s=6777)

### 定义错误页面

如果我们需要将所有的异常`统一`跳转到自定义的错误页面，需要在src/main/resources/templates目录下创建error.html页面,注意：`名称必须叫error`。

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>错误提示页面</title>
</head>
<body>
	出错了，请与管理员联系。。。
	<span th:text="${exception}"></span>
</body>
</html>

```
该方式可以较好的把各种错误统一跳转到自己的页面上，这个页面你就可以自由发挥写的漂漂亮亮的避免被祭天了。

### 使用@ExceptionHandle注解处理异常

```java

@Controller
public class DemoController {
	
	@RequestMapping("/show")
	public String showInfo(){
		String str = null;
		str.length();
		return "index";
	}
	
	@RequestMapping("/show2")
	public String showInfo2(){
		int a = 10/0;
		return "index";
	}
	
	/**
	 * java.lang.ArithmeticException
	 * 该方法需要返回一个ModelAndView：目的是可以让我们封装异常信息以及视图的指定
	 * 参数Exception e:会将产生异常对象注入到方法中
	 */
	@ExceptionHandler(value={java.lang.ArithmeticException.class})
	public ModelAndView arithmeticExceptionHandler(Exception e){
		ModelAndView mv = new ModelAndView();
		mv.addObject("error", e.toString());
		mv.setViewName("error1");
		return mv;
	}
	
	/**
	 * java.lang.NullPointerException
	 * 该方法需要返回一个ModelAndView：目的是可以让我们封装异常信息以及视图的指定
	 * 参数Exception e:会将产生异常对象注入到方法中
	 */
	@ExceptionHandler(value={java.lang.NullPointerException.class})
	public ModelAndView nullPointerExceptionHandler(Exception e){
		ModelAndView mv = new ModelAndView();
		mv.addObject("error", e.toString());
		mv.setViewName("error2");
		return mv;
	}	
}

```
以下两个页面分别是error1和error2。
```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>错误提示页面-ArithmeticException</title>
</head>
<body>
	出错了，请与管理员联系。。。
	<span th:text="${error}"></span>
</body>
</html>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>错误提示页面-NullPointerException</title>
</head>
<body>
	出错了，请与管理员联系。。。
	<span th:text="${error}"></span>
</body>
</html>

```
浏览器访问show1便会出现以下图片。


![](https://user-gold-cdn.xitu.io/2020/7/5/1731e2da03256140?w=554&h=64&f=png&s=4214)

这种方式可以实现针对不同的异常跳转到不同的页面如404或者403、401等...针对不同报错实现个性化的异常处理才是正确的做法。


![](https://user-gold-cdn.xitu.io/2020/7/5/1731e2d781a7fad2?w=690&h=615&f=png&s=870685)

### @ControllerAdvice+@ExceptionHandler
> 这种方式只是把ExceptionHandler处理类给单独提了出来减少了Controller的代码冗余，提高了对异常统一管理的能力。

```java
/**
 * 全局异常处理类
 *
 *
 */
@ControllerAdvice
public class GlobalException {
	/**
	 * java.lang.ArithmeticException
	 * 该方法需要返回一个ModelAndView：目的是可以让我们封装异常信息以及视图的指定
	 * 参数Exception e:会将产生异常对象注入到方法中
	 */
	@ExceptionHandler(value={java.lang.ArithmeticException.class})
	public ModelAndView arithmeticExceptionHandler(Exception e){
		ModelAndView mv = new ModelAndView();
		mv.addObject("error", e.toString());
		mv.setViewName("error1");
		return mv;
	}
	/**
	 * java.lang.NullPointerException
	 * 该方法需要返回一个ModelAndView：目的是可以让我们封装异常信息以及视图的指定
	 * 参数Exception e:会将产生异常对象注入到方法中
	 */
	@ExceptionHandler(value={java.lang.NullPointerException.class})
	public ModelAndView nullPointerExceptionHandler(Exception e){
		ModelAndView mv = new ModelAndView();
		mv.addObject("error", e.toString());
		mv.setViewName("error2");
		return mv;
	}	
}

```
### 配置SimpleMappingExceptionResolver处理异常解析器
> 这种方式的自由度更大

```java
/**
 * 通过SimpleMappingExceptionResolver做全局异常处理
 *
 *
 */
@Configuration
public class GlobalException {
	
	/**
	 * 该方法必须要有返回值。返回值类型必须是：SimpleMappingExceptionResolver
	 */
	@Bean
	public SimpleMappingExceptionResolver getSimpleMappingExceptionResolver(){
		SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
		
		Properties mappings = new Properties();	
		/**
		 * 参数一：异常的类型，注意必须是异常类型的全名
		 * 参数二：视图名称
		 */
		mappings.put("java.lang.ArithmeticException", "error1");
		mappings.put("java.lang.NullPointerException","error2");
		//设置异常与视图映射信息的
		resolver.setExceptionMappings(mappings);
		return resolver;
	}
	
}

```

```java
/**
 * 通过实现HandlerExceptionResolver接口做全局异常处理
 *
 *
 */
@Configuration
public class GlobalException implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		ModelAndView mv = new ModelAndView();
		//判断不同异常类型，做不同视图跳转
		if(ex instanceof ArithmeticException){
			mv.setViewName("error1");
		}
		
		if(ex instanceof NullPointerException){
			mv.setViewName("error2");
		}
		mv.addObject("error", ex.toString());
		
		return mv;
	}
}
```
### 最后再说

> 现在基本上已经是前后端分离了，对于前后端分离的情况下我们做统一的异常处理则是实现一个同一个的Json返回。

- @RestControllerAdvice = ControllerAdvice + @ResponseBody


```java
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Error defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        Error<String> stringError = new Error<String>();
        stringError.setCode(500);
        stringError.setMessage(e.getMessage());
        stringError.setUrl(req.getPathInfo());
        return stringError;
    }

    @ExceptionHandler(value = MyException.class)
    public Error myException(HttpServletRequest req, Exception e) throws Exception {
        Error<String> stringError = new Error<String>();
        stringError.setCode(500);
        stringError.setMessage(e.getMessage());
        stringError.setUrl(req.getPathInfo());
        return stringError;
    }
}
```

至此，已完成在Spring Boot中创建统一的异常处理，实际实现还是依靠Spring MVC的注解，更多更深入的使用可参考Spring MVC的文档。



![](https://user-gold-cdn.xitu.io/2020/7/5/1731e2d3b9686aac?w=300&h=300&f=png&s=14153)

---

更多精彩好文尽在：Java编程之道 🎁<br>
欢迎各位好友前去关注！🌹


![](https://user-gold-cdn.xitu.io/2020/7/5/1731e2ca9187b5f4?w=243&h=277&f=png&s=29211)