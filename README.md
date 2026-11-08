# bluesky-boot-autoconfigure-devcheck

<!-- 
bluesky-boot-autoconfigure-devcheck는 Spring Boot 기반 프로젝트에서 개발 내용 확인을 위해 만드는 개발자 확인용 controller method 목록과 일반적으로 사용되는 utility static method 목록을 확인 할 수 있도록 도와주는 라이브러리입니다.

개발 확인용 controller method 목록은 `/_check` 에서 확인할 수 있고 utility static method 목록은 `/_check/util` 에서 확인할 수 있습니다.

Thymeleaf를 사용하는 경우 Thymeleaf UI 기반으로 목록이 제공되며 그 외의 경우 JSON으로 목록을 제공합니다.
-->

bluesky-boot-autoconfigure-devcheck is a library that helps you check the list of controller methods for developer checks and the list of commonly used utility static methods that Spring Boot-based projects create to check their development.

The list of controller methods for development checks can be found in `/_check` and the list of utility static methods can be found in `/_check/util`.

If you are using Thymeleaf, the list is provided based on the Thymeleaf UI, otherwise the list is provided in JSON.

**Prerequisites**

- [Java 17](https://openjdk.java.net/)
- [Spring Boot 3.4.0](https://spring.io/)

## settings

### maven dependencies

```pom.xml
<dependencies>
    <dependency>
        <groupId>io.github.luversof</groupId>
        <artifactId>bluesky-boot-autoconfigure-devcheck</artifactId>
        <version>${currentVersion}</version>
    </dependency>
</dependencies>
```

### properties

<!--
이 라이브러리는 설정 값과 로그를 보여 주므로 기본으로 꺼져 있습니다.

개발·테스트 환경에서만 다음과 같이 켭니다. 꺼져 있으면 직접 만든 `@DevCheckController` 도 등록되지 않습니다.
-->

This library shows configuration values and logs, so it is disabled by default.

Enable it only in development or test environments, as follows. While it is disabled, your own `@DevCheckController` classes are not registered either.


```properties
bluesky-boot.dev-check.enabled=true
```

<!--
지정된 범위에서 utility static method를 검색합니다.

다음과 같이 검사할 패키지를 지정합니다.
-->
Searches for utility static methods in the specified scope.

Specify the packages to be scanned as follows:


```properties
bluesky-boot.dev-check.base-packages=net.luversof
```

<!--
devCheck의 기본 주소를 변경하고 싶은 경우 다음과 같이 설정합니다.

기본 값은 `/_check` 입니다.
-->

If you want to change the default address for devCheck, set it as follows.

The default value is `/_check`.


```properties
bluesky-boot.dev-check.path-prefixes=/info/_check
```

<!--
path pattern으로 설정할 수도 있습니다.
-->

You can also set it to a path pattern.


```properties
bluesky-boot.dev-check.path-prefixes=/{somePath}/_check
```


## usage

### DevCheckDescription annotation

<!-- 
`/_check` page에서 해당 method에 대한 설명을 나타내기 위해 DevCheckDescription annotation을 사용합니다.

controller method와 utility static method에 사용할 수 있습니다.
-->
Use the DevCheckDescription annotation to represent a description of the method on the `/_check` page.

It can be used for controller methods and utility static methods.


| attribute  | description |
| ------------- | ------------- |
| value  | <!-- method 아래 추가되는 설명 --> Description added under method  |
| displayable  | <!-- 목록 노출 여부 --> Whether the list is exposed  |

### controller method

<!-- 
controller bean이 다음 조건을 만족하는 `/_check` page 목록화 대상입니다.

* `@DevCheckController` annotation을 선언 (`@RestController` 포함)
* produce 속성은 `application/json` 
* path 앞에는 `bluesky-boot.dev-check.path-prefixes` 값이 자동으로 붙음 (기본 `/_check`)

다음과 같이 controller를 생성합니다.
-->
 
The controller bean is the target of the `/_check` page listing that satisfies the following conditions.

* Declare the `@DevCheckController` annotation (it includes `@RestController`)
* The produce attribute is set to `application/json` 
* The path is prefixed with `bluesky-boot.dev-check.path-prefixes` automatically (default `/_check`).

Create a controller like this:

```java
@DevCheckController
@RequestMapping(value = "/core", produces = MediaType.APPLICATION_JSON_VALUE)
public class DevCheckCoreController {

	private ApplicationContext applicationContext;

	public DevCheckCoreController(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@DevCheckDescription("spring activeProfiles")
	@GetMapping("/activeProfiles")
	public String[] activeProfiles() {
		return applicationContext.getEnvironment().getActiveProfiles();
	}
	
	// create GetMapping method you want to check
}
```

<!-- 
아래와 같이 해당 controller의 getMapping method가 `/_check` 목록에 추가됩니다.
-->

The getMapping method of that controller is added to the `/_check` list, as shown below.

![_check](./_check.png)


### utility static method

<!--
`/_check/util` 목록에 추가할 utility class에 @DevCheckUtil 또는 @ReactiveDevCheckUtil annotation을 선언합니다.

다음과 같이 사용합니다.
-->

Declare the @DevCheckUtil or @ReactiveDevCheckUtil annotation on the utility class you want to add to the `/_check/util` list.

Use it like this:

```java
@DevCheckUtil
public class UserUtil extends RequestAttributeUtil {
	
	@Setter(onMethod_ = @DevCheckDescription(displayable = false))
	private static LoginUserService loginUserService;
	
	private static final String LOGIN_USER = "__loginUser";

	@DevCheckDescription("get loginUser")
	public static Optional<User> getLoginUser() {
		Optional<User> userOptional = getRequestAttribute(LOGIN_USER);
		if (userOptional != null) {
			return userOptional;
		}
		
		userOptional = loginUserService.getUser();
		setRequestAttribute(LOGIN_USER, userOptional);
		
		return userOptional;
	}
}

```

<!--
아래와 같이 해당 utility static method가 `/_check/util` 목록에 추가됩니다.
-->

The corresponding utility static method is added to the `/_check/util` list, as shown below.

![_check](./_checkUtil.png)

## version history

| version | prerequisites |
| ------------- | ------------- |
| 1.0.1 | Java 11, Spring Boot 2.5.4 |
| 1.0.2 | Java 11, Spring Boot 2.6.1 |
| 2.0.0 | Java 17, Spring Boot 2.7.1 |
| 3.1.0 | Java 17, Spring Boot 3.1.0 |