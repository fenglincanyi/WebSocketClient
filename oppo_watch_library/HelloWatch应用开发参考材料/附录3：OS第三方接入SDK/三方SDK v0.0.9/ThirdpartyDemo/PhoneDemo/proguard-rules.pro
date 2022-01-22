# 混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames

# 保留注解不混淆
-keepattributes *Annotation*,InnerClasses
# 避免混淆泛型
-keepattributes Signature

# 保留代码行号，方便异常信息的追踪
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile