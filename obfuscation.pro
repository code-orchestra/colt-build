-injars /Users/eliseyev/git/colt-build/artifacts/unobfuscated/colt-core.jar
-injars /Users/eliseyev/git/colt-build/artifacts/unobfuscated/colt-as.jar
-injars /Users/eliseyev/git/colt-build/artifacts/unobfuscated/colt-js.jar
-outjars /Users/eliseyev/git/colt-build/artifacts

-libraryjars /Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home/jre/lib/

-target 1.8
-dontshrink
-dontoptimize
-keeppackagenames
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod
-keepparameternames
-microedition

-keep class !codeOrchestra.** { *; }

-keep,allowshrinking public final @interface  *

# Keep - Applications. Keep all application classes, along with their 'main'
# methods.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Also keep - Database drivers. Keep all implementations of java.sql.Driver.
-keep class * extends java.sql.Driver

# Also keep - Swing UI L&F. Keep all extensions of javax.swing.plaf.ComponentUI,
# along with the special 'createUI' method.
-keep class * extends javax.swing.plaf.ComponentUI {
    public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}
