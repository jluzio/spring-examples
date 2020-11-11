
### Errors
"java.lang.IllegalAccessException: class io.netty.util.internal.PlatformDependent0$6 cannot access class jdk.internal.misc.Unsafe (in module java.base) because module java.base does not export jdk.internal.misc to unnamed module @84b8f0f"

https://stackoverflow.com/questions/57885828/netty-cannot-access-class-jdk-internal-misc-unsafe 

>--add-opens java.base/jdk.internal.misc=ALL-UNNAMED

>-Dio.netty.tryReflectionSetAccessible=true

>--illegal-access=warn