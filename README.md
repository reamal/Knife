## Knife
编译时生成findViewById的小Demo

* 运行编译demo，会自动生成如下文件。
app->build->intermediates->classes->debug->com->bravo->knife->MainActivity$$ViewBinder.class

* 此练习编写此项目时，首先需要配置一下gradle，具体顺序如下：
  1. project 配置 
      ```
        buildscript {
            repositories {
                jcenter()
                //add this code
                mavenCentral()
            }
            dependencies {
                classpath 'com.android.tools.build:gradle:2.3.3'
                //add this code
                classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
            }
        }
        
        allprojects {
            repositories {
                jcenter()
                //add this code
                mavenCentral()
            }
        }
      ```
  2. 创建一个android moudle,示例demo中是 inject
  3. 创建两个java moudle，用于放注解类和生成代码的类。示例中为inject-annotion 和inecjt-compiler
  4. app gradle 配置 
    ```
    //first
    apply plugin: 'com.neenbedankt.android-apt'
    
    //second
    compile project(':inject')
    apt project(':inject-compiler')
    
    ```
  5. inject gradle 配置
  ```
   compile project(':inject-annotion')
  ```
  6. inject-compiler gradle 配置
  ```
      compile 'com.google.auto:auto-common:0.8'
      compile 'com.google.auto.service:auto-service:1.0-rc3'
      compile 'com.squareup:javapoet:1.8.0'
  ```
  
  然后 sync now  同步一下，让gradle去下载相关的配置文件。
  
 * 主要的生成代码写在inject-compiler项目中的BindViewProcessor文件中。编译时注解的奥妙都在这个里面了。androidStudio在编译项目时，会运行继承了AbstractProcessor的BindViewProcessor类的process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) 方法，不过需要注意的是要在类上面加注解@AutoService(Processor.class)，我开始就是没有加注解，找了好久的问题。
 
